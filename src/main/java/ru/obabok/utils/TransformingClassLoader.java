package ru.obabok.utils;

import ru.obabok.Main;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TransformingClassLoader extends ClassLoader{
    private Object transformer;

    private final List<JarFile> allJars = new ArrayList<>();
    public TransformingClassLoader(List<Path> allPaths, ClassLoader classLoader) throws IOException {
        super(classLoader);
        for (Path path : allPaths) {
            allJars.add(new JarFile(path.toFile()));
        }
    }

    public void initTransformer(){
        this.transformer = createTransformer();
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

//        if (name.startsWith("ru.obabok.")) {
//            return getParent().loadClass(name);
//        }

        if (name.contains("lwjgl")) {
            return super.findClass(name);
        }

        String classPath = name.replace('.', '/') + ".class";
        byte[] rawBytes = null;

        for (JarFile jar : allJars) {
            JarEntry entry = jar.getJarEntry(classPath);
            if (entry != null) {
                try (InputStream is = jar.getInputStream(entry)) {
                    rawBytes = is.readAllBytes();
                    break;
                } catch (IOException ignored) {}
            }
        }

        if (rawBytes != null) {
            byte[] finalBytes = rawBytes;

            if (name.startsWith("net.minecraft.") && transformer != null) {
                try {
                    Method transformMethod = transformer.getClass().getMethod("transformClassBytes", String.class, String.class, byte[].class);
                    transformMethod.setAccessible(true);
                    finalBytes = (byte[]) transformMethod.invoke(transformer, name, name, rawBytes);
                    if (rawBytes.length != finalBytes.length) {
                        Main.LOGGER.debug("[Mixin] Class" + name + " was changed");
                    }
                } catch (Exception e) {
                    Main.LOGGER.error("[Mixin]: " + name, e.fillInStackTrace());
                }
            }

            return defineClass(name, finalBytes, 0, finalBytes.length);
        }

        return super.findClass(name);
    }

    private Object createTransformer() {
        try {
            Class<?> transformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
            java.lang.reflect.Constructor<?> constructor = transformerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать Mixin Transformer", e);
        }
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 1. Проверяем, не загружен ли уже
            Class<?> c = findLoadedClass(name);
            if (c != null) return c;

            if (name.startsWith("ru.obabok.event.") || name.equals("ru.obabok.utils.Loader")) {
                c = findClass(name);
                if (resolve) resolveClass(c);
                return c; // Сразу возвращаем, не даем super.loadClass сработать
            }

            // 2. Специфичные системные исключения (ВСЕГДА РОДИТЕЛЬ)
            if (name.startsWith("java.") || name.startsWith("javax.") ||
                    name.startsWith("org.spongepowered.") || name.startsWith("org.objectweb.asm.") ||
                    name.contains("lwjgl")) {
                return getParent().loadClass(name);
            }

            // 3. ТВОИ КЛАССЫ И МАЙНКРАФТ (ВСЕГДА САМИ)
            if (name.startsWith("ru.obabok.") || name.startsWith("net.minecraft.") || name.startsWith("com.mojang.")) {
                // Исключаем сам лоадер, чтобы не было StackOverflow
                if (!name.equals("ru.obabok.utils.TransformingClassLoader")) {
                    c = findClass(name);
                    if (resolve) resolveClass(c);
                    return c; // ВОЗВРАЩАЕМ СРАЗУ, не идем в super!
                }
            }

            // 4. Всё остальное (библиотеки) — родителю
            return super.loadClass(name, resolve);
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        for (JarFile jarFile : allJars) {
            JarEntry entry = jarFile.getJarEntry(name);
            if (entry != null) {
                try {
                    return jarFile.getInputStream(entry);
                } catch (IOException ignored) {}
            }
        }
        // 2. Если не нашли — отдаем родителю
        return super.getResourceAsStream(name);
    }

}

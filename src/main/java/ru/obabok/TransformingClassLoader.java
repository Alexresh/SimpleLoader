package ru.obabok;

import org.spongepowered.asm.mixin.transformer.IMixinTransformer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TransformingClassLoader extends ClassLoader{
    //private final Path gameJar;
    private final IMixinTransformer transformer;
    //private final JarFile jar;
    private final List<JarFile> allJars = new ArrayList<>();
    public TransformingClassLoader(List<Path> allPaths, ClassLoader classLoader) throws IOException {
        super(classLoader);
        for (Path path : allPaths) {
            allJars.add(new JarFile(path.toFile()));
        }
        this.transformer = createTransformer();
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        if (name.startsWith("ru.obabok.")) {
            return getParent().loadClass(name);
        }

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

            if (name.startsWith("net.minecraft.")) {
                try {
                    finalBytes = transformer.transformClassBytes(name, name, rawBytes);
                    if (rawBytes.length != finalBytes.length) {
                        Main.LOGGER.debug("[Mixin] Class" + name + " was changed");
                    }
                } catch (Exception e) {
                    Main.LOGGER.error("[Mixin]: " + name);
                }
            }

            return defineClass(name, finalBytes, 0, finalBytes.length);
        }

        return super.findClass(name);
    }

//    @Override
//    protected Class<?> findClass(String name) throws ClassNotFoundException {
//        // Превращаем имя класса (net.minecraft.Main) в путь (net/minecraft/Main.class)
//        if (name.startsWith("ru.obabok.")) {
//            return getParent().loadClass(name);
//        }
////        if (name.startsWith("com.mojang.")) {
////            // Просто просим родителя, НО только если мы еще не загрузили этот класс сами
////            return getParent().loadClass(name);
////        }
//        if (name.startsWith("net.minecraft.")) {
//            String classPath = name.replace('.', '/') + ".class";
//            JarEntry entry = jar.getJarEntry(classPath);
//            System.out.println("Test: " + classPath);
//            if (entry != null) {
//                try (InputStream is = jar.getInputStream(entry)) {
//                    // 1. Читаем "сырые" байты из JAR
//                    byte[] rawBytes = is.readAllBytes();
//
//                    System.out.println("Попытка трансформировать: " + name);
//                    byte[] transformedBytes = transformer.transformClassBytes(name, name, rawBytes);
//                    if (rawBytes.length != transformedBytes.length) {
//                        System.out.println("!!! КЛАСС " + name + " БЫЛ ИЗМЕНЕН МИКСИНОМ !!!");
//                    }
//                    // 3. Регистрируем класс в JVM
//                    return defineClass(name, transformedBytes, 0, transformedBytes.length);
//                } catch (Exception e) {
//                    throw new ClassNotFoundException("Ошибка трансформации класса " + name, e);
//                }
//            }
//        }
//
//        // Если класса нет в JAR игры (например, это библиотеки), ищем в родителе
//        return super.findClass(name);
//    }
    private IMixinTransformer createTransformer() {
        try {
            // В версии 0.8.5 трансформер создается через внутренний билд
            Class<?> transformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
            java.lang.reflect.Constructor<?> constructor = transformerClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (IMixinTransformer) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать Mixin Transformer", e);
        }
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                // МЫ ГРУЗИМ ТОЛЬКО ЭТО:
                if (name.startsWith("net.minecraft.") || name.startsWith("com.mojang.")) {
                    // Но исключаем LWJGL внутри com.mojang, если он там есть
                    if (!name.contains("lwjgl")) {
                        try {
                            c = findClass(name);
                        } catch (ClassNotFoundException ignored) {}
                    }
                }
            }

            // ВСЁ ОСТАЛЬНОЕ (JOpt, LWJGL, Netty, Authlib, лоадер) — отдаем Prism (родителю)
            if (c == null) {
                // ВАЖНО: используем системный лоадер Prism, а не Platform
                // Для этого в Main при создании лоадера передавай Main.class.getClassLoader()
                c = super.loadClass(name, resolve);
            }

            if (resolve) resolveClass(c);
            return c;
        }
    }

//    @Override
//    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
//        synchronized (getClassLoadingLock(name)) {
//            Class<?> c = findLoadedClass(name);
//            if (c == null) {
//                // Если это Майнкрафт или его внутренние библиотеки Моджанг — грузим сами
//                if (name.startsWith("net.minecraft.") || name.startsWith("com.mojang.")) {
//                    try {
//                        c = findClass(name);
//                    } catch (ClassNotFoundException ignored) {}
//                }
//            }
//
//            // ВСЁ ОСТАЛЬНОЕ (LWJGL, системные библиотеки, лоадер) — отдаем Prism (родителю)
//            if (c == null) {
//                c = super.loadClass(name, resolve);
//            }
//
//            if (resolve) resolveClass(c);
//            return c;
//        }
//    }

}

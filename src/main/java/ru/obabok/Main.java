package ru.obabok;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Main {
    public static JarFile gameJarFile;
    public static boolean TRACE = false;
    public static boolean DEBUG = false;
    public static boolean MIXIN_EXPORT = true;
    public static void main(String[] args) {
        System.out.println("Запуск SimpleLoader... " + args.length);
        try {
            Path mcPath = Paths.get("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");
            gameJarFile = new JarFile(mcPath.toFile());

            //System.setProperty("org.lwjgl.librarypath", System.getProperty("java.library.path"));
            //System.setProperty("mixin.verify", "false");
            if(MIXIN_EXPORT)
                System.setProperty("mixin.debug.export", "true");
            //System.setProperty("mixin.debug", "true");
            MixinBootstrap.init();
            MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
            Mixins.addConfiguration("simpleloader.mixins.json");
            System.out.println("Mixin система готова!");
            //Path mcJar = Paths.get("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");


            String cp = System.getProperty("java.class.path");
            String[] paths = cp.split(File.pathSeparator);
            List<Path> jarPaths = Arrays.stream(paths)
                    .map(Paths::get)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .collect(Collectors.toList());


            File modsDir = new File("mods");
            if (!modsDir.exists()) modsDir.mkdirs();

            List<Path> allPaths = new ArrayList<>();
            allPaths.addAll(jarPaths);

            File[] modFiles = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (modFiles != null) {
                for (File mod : modFiles) {
                    allPaths.add(mod.toPath());
                    System.out.println("Найден мод: " + mod.getName());
                }
            }


            TransformingClassLoader loader = new TransformingClassLoader(allPaths, Main.class.getClassLoader());

            Class<?> mcMain = Class.forName("net.minecraft.client.main.Main", true, loader);

            Method mainMethod = mcMain.getMethod("main", String[].class);
            Thread.currentThread().setContextClassLoader(loader);

            for (File modFile : modFiles) {
                try (JarFile jar = new JarFile(modFile)) {
                    JarEntry entry = jar.getJarEntry("mod.json");
                    System.out.println("mod.json найден");
                    if (entry != null) {
                        InputStreamReader reader = new InputStreamReader(jar.getInputStream(entry));
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        String mainClass = json.get("mainClass").getAsString();
                        System.out.println("mainClass= "+mainClass);
                        Class<?> clazz = Class.forName(mainClass, true, loader);
                        System.out.println("class loaded");
                        System.out.println("Методы в классе " + mainClass + ":");
                        for (Method m : clazz.getMethods()) {
                            System.out.println("- " + m.getName() + " (static: " + java.lang.reflect.Modifier.isStatic(m.getModifiers()) + ")");
                        }

                        Method initMethod = clazz.getDeclaredMethod("main");
                        initMethod.setAccessible(true);
                        initMethod.invoke(null);

                        System.out.println("Мод " + modFile.getName() + " инициализирован.");
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка загрузки мода " + modFile.getName());
                    e.printStackTrace();
                }
            }


            mainMethod.invoke(null, (Object) args);
        }catch (Exception exception){
            exception.printStackTrace();
        }

    }
}

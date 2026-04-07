package ru.obabok;


import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class Main {
    public static JarFile gameJarFile;
    public static boolean TRACE = false;
    public static boolean DEBUG = false;
    public static void main(String[] args) {
        System.out.println("Запуск SimpleLoader... " + args.length);
        try {
            Path mcPath = Paths.get("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");
            gameJarFile = new JarFile(mcPath.toFile());

            //System.setProperty("org.lwjgl.librarypath", System.getProperty("java.library.path"));
            //System.setProperty("mixin.verify", "false");
            //System.setProperty("mixin.debug.export", "true");
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

            TransformingClassLoader loader = new TransformingClassLoader(jarPaths, Main.class.getClassLoader());

            Class<?> mcMain = Class.forName("net.minecraft.client.main.Main", true, loader);

            Method mainMethod = mcMain.getMethod("main", String[].class);
            Thread.currentThread().setContextClassLoader(loader);

            mainMethod.invoke(null, (Object) args);
        }catch (Exception exception){
            exception.printStackTrace();
        }

    }
}

package ru.obabok.utils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

import static ru.obabok.Main.LOGGER;

public class Bootstrap {
    public static JarFile gameJarFile;

    public static void init(File minecraftJar, String[] args){
        try {
            List<Path> allJars = new ArrayList<>();

            addLibPaths(minecraftJar, allJars);
            File[] modFiles = addMods(allJars);

            TransformingClassLoader loader = new TransformingClassLoader(allJars, Bootstrap.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);

            Class<?> internalLoaderClass = Class.forName("ru.obabok.utils.Loader", true, loader);
            internalLoaderClass.getMethod("init", String[].class, TransformingClassLoader.class, File[].class)
                    .invoke(null, (Object) args, loader, modFiles);

        } catch (Exception e) {
            LOGGER.error("[Bootstrap] error: " + e.getMessage(), e.fillInStackTrace());
        }

    }

    private static void addLibPaths(File minecraftJar, List<Path> paths){
        LOGGER.debug("[Bootstrap] addLibPaths");
        try {
            Path mcPath = Paths.get(minecraftJar.toURI());
            gameJarFile = new JarFile(mcPath.toFile());
            String cp = System.getProperty("java.class.path");
            paths.addAll(Arrays.stream(cp.split(File.pathSeparator))
                    .map(Paths::get)
                    .filter(p -> p.toString().endsWith(".jar")).toList());

        }catch (Exception e){
            LOGGER.error("[Bootstrap] addLibPaths: " + e.getMessage(), e.fillInStackTrace());
        }

    }

    private static File[] addMods(List<Path> paths){
        LOGGER.debug("[Bootstrap] addMods");

        File modsDir = new File("mods");
        if (!modsDir.exists()) modsDir.mkdirs();
        File[] modFiles = modsDir.listFiles((_, name) -> name.endsWith(".jar"));

        if (modFiles != null) {
            for (File mod : modFiles) {
                paths.add(mod.toPath());
            }
        }
        return modFiles;
    }
}

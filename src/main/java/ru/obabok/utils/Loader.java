package ru.obabok.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import ru.obabok.Main;
import ru.obabok.TransformingClassLoader;

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

import static ru.obabok.Main.LOGGER;
import static ru.obabok.Main.MIXIN_EXPORT;

public class Loader {
    public static JarFile gameJarFile;


    public static void initialize(File minecraftJar, String[] args){

        LOGGER.info("Load SimpleLoader by Obabok");
        List<Path> allJars = new ArrayList<>();

        addLibPaths(minecraftJar, allJars);
        File[] modFiles = addMods(allJars);

        TransformingClassLoader loader = createLoader(allJars);

        initMixins(loader);

        loader.initTransformer();

        registerMixins(modFiles);


        invokeMinecraft(loader, args);
        invokeMods(modFiles, loader);

    }

    private static void addLibPaths(File minecraftJar, List<Path> paths){
        LOGGER.debug("[Loader] addLibPaths");
        try {
            Path mcPath = Paths.get(minecraftJar.toURI());
            gameJarFile = new JarFile(mcPath.toFile());
            String cp = System.getProperty("java.class.path");
            paths.addAll(Arrays.stream(cp.split(File.pathSeparator))
                    .map(Paths::get)
                    .filter(p -> p.toString().endsWith(".jar")).toList());

        }catch (Exception e){
            LOGGER.error("[Loader] addLibPaths: " + e.getMessage(), e.fillInStackTrace());
        }

    }

    private static File[] addMods(List<Path> paths){
        LOGGER.debug("[Loader] addMods");

        File modsDir = new File("mods");
        if (!modsDir.exists()) modsDir.mkdirs();
        File[] modFiles = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (modFiles != null) {
            for (File mod : modFiles) {
                paths.add(mod.toPath());
            }
        }
        return modFiles;
    }

    private static TransformingClassLoader createLoader(List<Path> allJars){
        LOGGER.debug("[Loader] createLoader");
        TransformingClassLoader loader = null;
        try {
            loader = new TransformingClassLoader(allJars, Main.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(loader);
            return loader;
        }catch (Exception e){
            LOGGER.error("[Loader] createLoader: " + e.getMessage(), e.fillInStackTrace());
        }
        return loader;
    }

    private static void initMixins(TransformingClassLoader loader){
        LOGGER.debug("[Loader] initMixins");
        System.setProperty("mixin.service", "ru.obabok.mixin.MyMixinService");
        if (MIXIN_EXPORT) System.setProperty("mixin.debug.export", "true");
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }
    private static void registerMixins(File[] modFiles){
        LOGGER.debug("[Loader] registerMixins");
        Mixins.addConfiguration("simpleloader.mixins.json");
        LOGGER.debug("[Loader] added loader mixins");

        LOGGER.debug("[Loader] mixin mods");
        if (modFiles != null) {
            for (File modFile : modFiles) {
                try (JarFile jar = new JarFile(modFile)) {
                    JarEntry entry = jar.getJarEntry("mod.json");
                    if (entry != null) {
                        JsonObject json = JsonParser.parseReader(new InputStreamReader(jar.getInputStream(entry))).getAsJsonObject();
                        if (json.has("mixins")) {
                            String mixinConfig = json.get("mixins").getAsString();
                            Mixins.addConfiguration(mixinConfig);
                            LOGGER.debug("[Loader] Registered mixins for mod " + modFile.getName() + ": " + mixinConfig);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("[Loader] registerMixins: " + modFile.getName() + " " + e.getMessage(), e.fillInStackTrace());
                }
            }
        }
        LOGGER.debug("[Loader] Mixin system fully loaded!");
    }

    private static void invokeMods(File[] modFiles, TransformingClassLoader loader){
        if (modFiles != null) {
            for (File modFile : modFiles) {
                try (JarFile jar = new JarFile(modFile)) {
                    JarEntry entry = jar.getJarEntry("mod.json");
                    if (entry == null) continue;

                    JsonObject json = JsonParser.parseReader(new InputStreamReader(jar.getInputStream(entry))).getAsJsonObject();
                    String mainClass = json.get("mainClass").getAsString().trim();

                    Class<?> clazz = Class.forName(mainClass, true, loader);
                    Method initMethod = clazz.getDeclaredMethod("main");
                    initMethod.setAccessible(true);
                    initMethod.invoke(null);

                    LOGGER.debug("[Loader] Mod " + modFile.getName() + " initialized");
                } catch (Exception e) {
                    LOGGER.error("[Loader] invokeMods: " + modFile.getName() + " " + e.getMessage(), e.fillInStackTrace());
                }
            }
        }
    }
    private static void invokeMinecraft(TransformingClassLoader loader, String[] args){
        try {
            Class<?> mcMain = Class.forName("net.minecraft.client.main.Main", true, loader);
            Method mainMethod = mcMain.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
            LOGGER.debug("[Loader] Minecraft started");
        }catch (Exception e){
            LOGGER.error("[Loader] invokeMinecraft: " + e.getMessage(), e.fillInStackTrace());
        }
    }

}

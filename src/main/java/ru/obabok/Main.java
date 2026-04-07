package ru.obabok;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import ru.obabok.mixin.SimpleLogger;

import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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
    public static boolean MIXIN_EXPORT = false;

    public static final SimpleLogger LOGGER = new SimpleLogger();

    public static void main(String[] args) {

        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        LOGGER.info("Запуск SimpleLoader");
        try {
            //linux
            //Path mcPath = Paths.get("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");
            //windows
            Path mcPath = Paths.get("C:/Users/Alexresh/AppData/Roaming/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");
            gameJarFile = new JarFile(mcPath.toFile());

            //System.setProperty("org.lwjgl.librarypath", System.getProperty("java.library.path"));
            //System.setProperty("mixin.verify", "false");
            if(MIXIN_EXPORT)
                System.setProperty("mixin.debug.export", "true");
            //System.setProperty("mixin.debug", "true");
            MixinBootstrap.init();
            MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
            Mixins.addConfiguration("simpleloader.mixins.json");
            LOGGER.debug("Mixin loaded!");
            //Path mcJar = Paths.get("/home/alex/.local/share/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");


            String cp = System.getProperty("java.class.path");
            String[] paths = cp.split(File.pathSeparator);
            List<Path> jarPaths = Arrays.stream(paths)
                    .map(Paths::get)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .toList();


            File modsDir = new File("mods");
            if (!modsDir.exists()) modsDir.mkdirs();

            List<Path> allPaths = new ArrayList<>(jarPaths);

            File[] modFiles = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (modFiles != null) {
                for (File mod : modFiles) {
                    allPaths.add(mod.toPath());

                    try (JarFile jar = new JarFile(mod)) {
                        JarEntry entry = jar.getJarEntry("mod.json");
                        if (entry != null) {
                            JsonObject json = JsonParser.parseReader(new InputStreamReader(jar.getInputStream(entry))).getAsJsonObject();

                            // Если в json есть поле "mixins"
                            if (json.has("mixins")) {
                                String mixinConfig = json.get("mixins").getAsString();
                                Mixins.addConfiguration(mixinConfig);
                                System.out.println("Зарегистрированы миксины мода " + mod.getName() + ": " + mixinConfig);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Ошибка пред-загрузки мода: " + mod.getName());
                    }


                }
            }


            TransformingClassLoader loader = new TransformingClassLoader(allPaths, Main.class.getClassLoader());

            Class<?> mcMain = Class.forName("net.minecraft.client.main.Main", true, loader);

            Method mainMethod = mcMain.getMethod("main", String[].class);
            Thread.currentThread().setContextClassLoader(loader);
            if(modFiles != null){
                for (File modFile : modFiles) {
                    try (JarFile jar = new JarFile(modFile)) {
                        JarEntry entry = jar.getJarEntry("mod.json");
                        if (entry != null) {
                            InputStreamReader reader = new InputStreamReader(jar.getInputStream(entry));
                            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                            String mainClass = json.get("mainClass").getAsString();
                            LOGGER.debug("mainClass = " + mainClass);

                            Class<?> clazz = Class.forName(mainClass, true, loader);

                            if(DEBUG){
                                LOGGER.debug("[Loader] Methods in class " + mainClass + ":");
                                for (Method m : clazz.getMethods()) {
                                    LOGGER.debug("- " + m.getName() + " (static: " + java.lang.reflect.Modifier.isStatic(m.getModifiers()) + ")");
                                }
                            }


                            Method initMethod = clazz.getDeclaredMethod("main");
                            initMethod.setAccessible(true);
                            initMethod.invoke(null);

                            LOGGER.debug("[Loader] Mod " + modFile.getName() + " initialized");
                        }else {
                            LOGGER.error("[Loader] Mod: " + jar.getName() + " has no mod.json! [not loaded]");
                        }
                    } catch (Exception e) {
                        LOGGER.error("[Loader] Error while load mod: " + modFile.getName(), e.fillInStackTrace());
                    }
                }
            }

            mainMethod.invoke(null, (Object) args);
        }catch (Exception exception){
            LOGGER.error("[Loader] Error while load: ", exception.fillInStackTrace());
        }

    }

    public static void print(String text){
        LOGGER.info(text);
    }
}

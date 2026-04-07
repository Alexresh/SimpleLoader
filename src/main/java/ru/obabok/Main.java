package ru.obabok;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import ru.obabok.mixin.SimpleLogger;
import ru.obabok.utils.Installer;

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

        if(args.length == 0){
            Installer.run();
            return;
        }


        LOGGER.info("Load SimpleLoader by Obabok");

        try {
            // 1. Подготовка путей

            LOGGER.debug("paths");

            Path mcPath = Paths.get("C:/Users/Alexresh/AppData/Roaming/PrismLauncher/libraries/com/mojang/minecraft/26.1.1/minecraft-26.1.1-client.jar");
            gameJarFile = new JarFile(mcPath.toFile());

            String cp = System.getProperty("java.class.path");
            List<Path> allPaths = new ArrayList<>(
                    Arrays.stream(cp.split(File.pathSeparator))
                            .map(Paths::get)
                            .filter(p -> p.toString().endsWith(".jar"))
                            .toList()
            );
            LOGGER.debug("mods");
            // 2. Сканирование папки модов
            File modsDir = new File("mods");
            if (!modsDir.exists()) modsDir.mkdirs();
            File[] modFiles = modsDir.listFiles((dir, name) -> name.endsWith(".jar"));

            if (modFiles != null) {
                for (File mod : modFiles) {
                    allPaths.add(mod.toPath());
                }
            }
            LOGGER.debug("loader");
            // 3. Создание лоадера (ДО регистрации миксинов модов!)
            TransformingClassLoader loader = new TransformingClassLoader(allPaths, Main.class.getClassLoader());
            // КРИТИЧНО: Устанавливаем лоадер как контекстный, чтобы Mixin видел ресурсы внутри JAR модов
            Thread.currentThread().setContextClassLoader(loader);

            // 4. Инициализация Mixin
            LOGGER.debug("mixin");
            System.setProperty("mixin.service", "ru.obabok.mixin.MyMixinService");
            if (MIXIN_EXPORT) System.setProperty("mixin.debug.export", "true");
            MixinBootstrap.init();
            MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);

            loader.initTransformer();

            // Регистрируем миксины самого лоадера
            LOGGER.debug("mixin my");
            Mixins.addConfiguration("simpleloader.mixins.json");


            LOGGER.debug("mixin mods");
            // 5. Регистрация миксинов из модов
            if (modFiles != null) {
                for (File modFile : modFiles) {
                    try (JarFile jar = new JarFile(modFile)) {
                        JarEntry entry = jar.getJarEntry("mod.json");
                        if (entry != null) {
                            JsonObject json = JsonParser.parseReader(new InputStreamReader(jar.getInputStream(entry))).getAsJsonObject();
                            if (json.has("mixins")) {
                                String mixinConfig = json.get("mixins").getAsString();
                                Mixins.addConfiguration(mixinConfig);
                                LOGGER.debug("[Mixin] Registered mixins for mod " + modFile.getName() + ": " + mixinConfig);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("[Mixin] Error while load mixins: " + modFile.getName(), e);
                    }
                }
            }

            LOGGER.debug("Mixin system fully loaded!");

            // 6. Инициализация логики модов (вызов main/init)
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
                        LOGGER.error("[Loader] Error while loading mod: " + modFile.getName(), e);
                    }
                }
            }

            // 7. Запуск Майнкрафта
            Class<?> mcMain = Class.forName("net.minecraft.client.main.Main", true, loader);
            Method mainMethod = mcMain.getMethod("main", String[].class);

            LOGGER.info("Starting Minecraft...");
            mainMethod.invoke(null, (Object) args);

        } catch (Throwable t) {
            LOGGER.error("[Loader] Critical error during startup: ", t);
        }

    }

    public static void print(String text){
        LOGGER.info(text);
    }
}

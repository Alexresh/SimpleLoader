package ru.obabok;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import ru.obabok.utils.FileUtils;
import ru.obabok.utils.Loader;
import ru.obabok.utils.logger.SimpleLogger;
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

public class Main {
    public static boolean TRACE = false;
    public static boolean DEBUG = true;
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

        //LoggerAppender.registerAppender("Proxy", null, null);

        File mcJarFile = FileUtils.getMinecraftJar();

        Loader.initialize(mcJarFile, args);

    }

    public static void print(String text){
        LOGGER.info(text);
    }
}

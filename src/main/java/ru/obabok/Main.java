package ru.obabok;

import ru.obabok.utils.Bootstrap;
import ru.obabok.utils.FileUtils;
import ru.obabok.utils.logger.SimpleLogger;
import ru.obabok.utils.Installer;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

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
        Bootstrap.init(mcJarFile, args);
        //Loader.initialize(mcJarFile, args);

    }

    public static void print(String text){
        LOGGER.info(text);
    }
}

package ru.obabok.utils;

import java.io.File;

import static ru.obabok.Launcher.LOGGER;

public class FileUtils {
    private static FileUtils instance;
    private File minecraftJarFile;


    public static File getMinecraftJar(){

        if(getInstance().minecraftJarFile != null) return instance.minecraftJarFile;

        try {
            instance.minecraftJarFile = new File(Class.forName("net.minecraft.client.main.Main")
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            LOGGER.debug("Minecraft jar found: " + instance.minecraftJarFile);
        } catch (Exception e) {
            LOGGER.error("Minecraft jar not found", e);
        }
        return instance.minecraftJarFile;
    }

    public static FileUtils getInstance(){
        if(instance == null){
            instance = new FileUtils();
        }
        return instance;
    }

}

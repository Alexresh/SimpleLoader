package ru.obabok.utils;

import ru.obabok.Main;

import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.nio.file.*;

public class Installer {


    public static void run(){
        String instanceName = JOptionPane.showInputDialog(null,
                "Enter a name for the new instance:",
                "Creating",
                JOptionPane.QUESTION_MESSAGE);

        // Если нажали "Отмена" или закрыли крестиком
        if (instanceName == null) {
            System.exit(0);
        }

        // Если ввели пустоту, ставим имя по умолчанию
        if (instanceName.trim().isEmpty()) {
            instanceName = "SimpleLoader-Instance";
        }

        // Убираем символы, которые запрещены в названиях папок Windows
        instanceName = instanceName.replaceAll("[\\\\/:*?\"<>|]", "_");

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select the PrismLauncher folder (which contains the instances folder)");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        File defaultPath = new File(System.getenv("APPDATA"), "PrismLauncher");
        if (defaultPath.exists()) chooser.setCurrentDirectory(defaultPath);
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            System.exit(0);
        }
        File prismDir = chooser.getSelectedFile();

        try {

            Path selfPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            Path libPath = prismDir.toPath().resolve("libraries/ru/obabok/simpleloader/1/simpleloader-1.jar");

            Files.createDirectories(libPath.getParent());
            Files.copy(selfPath, libPath, StandardCopyOption.REPLACE_EXISTING);


            // --- СОЗДАЕМ ИНСТАНС ---
            Path instPath = prismDir.toPath().resolve("instances").resolve(instanceName);
            Files.createDirectories(instPath.resolve("patches"));

            // Извлекаем файлы из ресурсов JAR в папку инстанса
            copyFromResources("/setup_files/instance.cfg", instPath.resolve("instance.cfg"));
            Files.write(instPath.resolve("instance.cfg"), ("\nname=" + instanceName).getBytes(), StandardOpenOption.APPEND);

            copyFromResources("/setup_files/mmc-pack.json", instPath.resolve("mmc-pack.json"));
            copyFromResources("/setup_files/patch.json", instPath.resolve("patches/org.multimc.custom.simpleloader.json"));

            JOptionPane.showMessageDialog(null,
                    "Completed!\ninstance '" + instanceName + "' created.\nRestart prism Launcher.",
                    "Finally", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Install error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void copyFromResources(String resourcePath, Path targetPath) throws Exception {
        try (InputStream in = Main.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new RuntimeException("File not found: " + resourcePath);
            }
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

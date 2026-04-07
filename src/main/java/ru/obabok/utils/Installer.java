package ru.obabok.utils;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Installer {

    public static void run(){
        String appData = System.getenv("APPDATA");
        Path mcDir = Paths.get(appData, ".minecraft");
        Path versionDir = mcDir.resolve("versions/26.1.1-SimpleLoader");

        try {
            // 1. Создаем папки
            Files.createDirectories(versionDir);

            // 2. Копируем сам лоадер и либы в папку версии
            // Предполагаем, что инсталлер запущен из папки, где есть 'libs'
            Files.copy(Paths.get("SimpleLoader.jar"), versionDir.resolve("SimpleLoader.jar"), StandardCopyOption.REPLACE_EXISTING);

            Path libsDest = versionDir.resolve("libs");
            Files.createDirectories(libsDest);

            File libsSrc = new File("libs");
            if (libsSrc.exists()) {
                for (File lib : libsSrc.listFiles()) {
                    Files.copy(lib.toPath(), libsDest.resolve(lib.getName()), StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // 3. Генерируем JSON
            String jsonContent = createJson(versionDir);
            Files.writeString(versionDir.resolve("26.1.1-SimpleLoader.json"), jsonContent);

            JOptionPane.showMessageDialog(null, "Установка завершена! Выберите версию '26.1.1-SimpleLoader' в лаунчере.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ошибка при установке: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String createJson(Path versionDir) {
        StringBuilder libsJson = new StringBuilder();
        File libsFolder = versionDir.resolve("libs").toFile();

        // Добавляем сам лоадер
        // ОБРАТИ ВНИМАНИЕ: size должен быть числом 0 без кавычек!
        libsJson.append("{\n" +
                "      \"name\": \"ru.obabok:simpleloader:1.0\",\n" +
                "      \"downloads\": {\n" +
                "        \"artifact\": {\n" +
                "          \"path\": \"SimpleLoader.jar\",\n" +
                "          \"url\": \"\",\n" +
                "          \"sha1\": \"\",\n" +
                "          \"size\": 0\n" +
                "        }\n" +
                "      }\n" +
                "    },");

        // Добавляем либы из папки
        if (libsFolder.exists()) {
            for (File lib : libsFolder.listFiles()) {
                if (!lib.getName().endsWith(".jar")) continue;
                libsJson.append(String.format(
                        "{\n" +
                                "      \"name\": \"lib:%s:1.0\",\n" +
                                "      \"downloads\": {\n" +
                                "        \"artifact\": {\n" +
                                "          \"path\": \"libs/%s\",\n" +
                                "          \"url\": \"\",\n" +
                                "          \"sha1\": \"\",\n" +
                                "          \"size\": 0\n" + // Здесь тоже число 0
                                "        }\n" +
                                "      }\n" +
                                "    },",
                        lib.getName().replace(".jar", ""), lib.getName()
                ));
            }
        }

        if (libsJson.length() > 0) libsJson.setLength(libsJson.length() - 1);

        return "{\n" +
                "  \"id\": \"26.1.1-SimpleLoader\",\n" +
                "  \"inheritsFrom\": \"26.1.1\",\n" +
                "  \"type\": \"release\",\n" +
                "  \"mainClass\": \"ru.obabok.Main\",\n" +
                "  \"arguments\": {},\n" +
                "  \"libraries\": [\n" + libsJson.toString() + "\n  ]\n" +
                "}";
    }
}

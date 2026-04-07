package ru.obabok;

import ru.obabok.utils.Installer;

public class InstallerEntrypoint {
    public static void main(String[] args) {
        if (args.length == 0) {
            Installer.run(); // Запускаем твой код установки
        } else {
            Main.main(args);
        }
    }
}

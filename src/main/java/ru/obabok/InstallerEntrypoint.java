package ru.obabok;

import ru.obabok.utils.Installer;

public class InstallerEntrypoint {
    public static void main(String[] args) {
        if (args.length == 0) {
            Installer.run();
        } else {
            Main.main(args);
        }
    }
}

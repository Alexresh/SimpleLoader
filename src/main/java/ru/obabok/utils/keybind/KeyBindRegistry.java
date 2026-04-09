package ru.obabok.utils.keybind;

import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;

public class KeyBindRegistry {
    private static final List<KeyMapping> mappings = new ArrayList<>();

    public static void register(KeyBinding keyBinding){
        mappings.add(keyBinding.getMapping());
    }

    public static List<KeyMapping> getMappings(){
        return mappings;
    }
}

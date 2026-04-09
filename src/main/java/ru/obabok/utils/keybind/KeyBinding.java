package ru.obabok.utils.keybind;

import net.minecraft.client.KeyMapping;

public class KeyBinding {
    private final KeyMapping mapping;

    public KeyBinding(String translationKey, int keyCode, KeyMapping.Category category) {
        this.mapping = new KeyMapping(translationKey, keyCode, category);
    }

    public boolean wasPressed() {
        return mapping.consumeClick();
    }
    public boolean isDown(){
        return mapping.isDown();
    }

    public KeyMapping getMapping() { return mapping; }
}

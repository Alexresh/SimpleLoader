package ru.obabok.mixins;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.utils.keybind.KeyBindRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(Options.class)
public class OptionsMixin {
    @Final
    @Shadow @Mutable public KeyMapping[] keyMappings;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void addCustomKeys(Minecraft minecraft, File file, CallbackInfo ci) {
        List<KeyMapping> allKeys = new ArrayList<>(Arrays.asList(this.keyMappings));
        allKeys.addAll(KeyBindRegistry.getMappings());
        this.keyMappings = allKeys.toArray(new KeyMapping[0]);
    }
}

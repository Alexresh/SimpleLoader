package ru.obabok.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Shadow
    public abstract void sendSystemMessage(Component message);

    @Inject(method = "attack", at = @At("HEAD"))
    private void addItem(Entity entity, CallbackInfo ci){
        sendSystemMessage(Component.literal("attack " + entity.toString()));
    }
}

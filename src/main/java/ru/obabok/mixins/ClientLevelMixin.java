package ru.obabok.mixins;

import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.event.EventManager;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(BooleanSupplier haveTime, CallbackInfo ci){
        EventManager.CLIENT_WORLD_TICK.invoker().onTick((ClientLevel) (Object)this);
    }
}

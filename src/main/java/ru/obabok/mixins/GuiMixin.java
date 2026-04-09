package ru.obabok.mixins;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.obabok.event.EventManager;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onExtractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // Опционально: создаем новый слой, чтобы наш текст не смешивался с предыдущими
        //graphics.nextStratum();

        // Вызываем наш инвокер
        EventManager.HUD_RENDER.invoker().onRender(graphics, deltaTracker);
    }
}

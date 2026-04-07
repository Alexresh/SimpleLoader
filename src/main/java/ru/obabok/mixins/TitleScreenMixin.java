package ru.obabok.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Redirect(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"))
    private void changeTitleText(GuiGraphicsExtractor instance, Font font, String str, int x, int y, int color){
        instance.text(font, "Minecraft " + SharedConstants.getCurrentVersion().name() + " powered by Obabok", x, y, color);
    }
}

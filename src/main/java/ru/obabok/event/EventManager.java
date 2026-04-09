package ru.obabok.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import ru.obabok.Main;

public class EventManager {
    @FunctionalInterface
    public interface HudRenderCallback {
        void onRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }

    public static final Event<HudRenderCallback> HUD_RENDER = new Event<>(
            HudRenderCallback.class,
            (listeners) -> (graphics ,deltaTracker) -> {
                for (int i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].onRender(graphics, deltaTracker);
                    } catch (Throwable e) {
                        Main.LOGGER.error("[HUD] ", e);
                    }
                }
            }
    );
}

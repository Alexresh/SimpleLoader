package ru.obabok.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.server.level.ServerLevel;
import ru.obabok.Launcher;

public class EventManager {
    @FunctionalInterface
    public interface HudRenderCallback {
        void onRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }
    @FunctionalInterface
    public interface WorldTickCallback {
        void onTick(ServerLevel serverLevel);
    }


    public static final Event<HudRenderCallback> HUD_RENDER = new Event<>(
            HudRenderCallback.class,
            (listeners) -> (graphics ,deltaTracker) -> {
                for (int i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].onRender(graphics, deltaTracker);
                    } catch (Throwable e) {
                        Launcher.LOGGER.error("[HUD] ", e);
                    }
                }
            }
    );

    public static final Event<WorldTickCallback> WORLD_TICK = new Event<>(
            WorldTickCallback.class,
            (listeners) -> (serverLevel -> {
                for (int i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].onTick(serverLevel);
                    } catch (Throwable e) {
                        Launcher.LOGGER.error("[HUD] ", e);
                    }
                }
            })
    );
}

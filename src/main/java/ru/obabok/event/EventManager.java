package ru.obabok.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import ru.obabok.Launcher;

public class EventManager {
    @FunctionalInterface
    public interface HudRenderCallback {
        void onRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
    }
    @FunctionalInterface
    public interface ServerWorldTickCallback {
        void onTick(ServerLevel serverLevel);
    }
    @FunctionalInterface
    public interface ClientWorldTickCallback {
        void onTick(ClientLevel clientLevel);
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

    public static final Event<ServerWorldTickCallback> SERVER_WORLD_TICK = new Event<>(
            ServerWorldTickCallback.class,
            (listeners) -> (serverLevel -> {
                for (int i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].onTick(serverLevel);
                    } catch (Throwable e) {
                        Launcher.LOGGER.error("[SERVER_WORLD_TICK] ", e);
                    }
                }
            })
    );

    public static final Event<ClientWorldTickCallback> CLIENT_WORLD_TICK = new Event<>(
            ClientWorldTickCallback.class,
            (listeners) -> (clientLevel -> {
                for (int i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].onTick(clientLevel);
                    } catch (Throwable e) {
                        Launcher.LOGGER.error("[CLIENT_WORLD_TICK] ", e);
                    }
                }
            })
    );
}

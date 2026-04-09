package ru.obabok.event;

import ru.obabok.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {
    @FunctionalInterface
    public interface HudRenderCallback {
        void onRender(float partialTicks);
    }

    public static final Event<HudRenderCallback> HUD_RENDER = new Event<>(
            HudRenderCallback.class,
            (listeners) -> (partialTicks) -> {
                for (HudRenderCallback listener : listeners) {
                    listener.onRender(partialTicks);
                }
            }
    );
}

package ru.obabok.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {
    private static final Map<Class<?>, List<Consumer<Object>>> LISTENERS = new HashMap<>();

    // Универсальный метод регистрации
    public static <T> void register(Class<T> eventClass, Consumer<T> listener) {
        LISTENERS.computeIfAbsent(eventClass, k -> new ArrayList<>())
                .add((Consumer<Object>) listener);
    }

    // Универсальный метод вызова события
    public static void post(Object event) {
        List<Consumer<Object>> eventListeners = LISTENERS.get(event.getClass());
        if (eventListeners != null) {
            for (Consumer<Object> listener : eventListeners) {
                listener.accept(event);
            }
        }
    }
}

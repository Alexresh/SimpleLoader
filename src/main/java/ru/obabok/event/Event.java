package ru.obabok.event;

import java.util.Arrays;
import java.util.function.Function;

public class Event<T>{
    private final Function<T[], T> invokerFactory;
    private T[] handlers;
    private T invoker;

    @SuppressWarnings("unchecked")
    public Event(Class<T> type, Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.handlers = (T[]) java.lang.reflect.Array.newInstance(type, 0);
        updateInvoker();
    }

    public void register(T listener) {
        handlers = Arrays.copyOf(handlers, handlers.length + 1);
        handlers[handlers.length - 1] = listener;
        updateInvoker();
    }

    private void updateInvoker() {
        this.invoker = invokerFactory.apply(handlers);
    }

    public T invoker() {
        return invoker;
    }

}

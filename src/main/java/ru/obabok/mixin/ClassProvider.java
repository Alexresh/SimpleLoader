package ru.obabok.mixin;

import org.spongepowered.asm.service.IClassProvider;
import ru.obabok.Main;

import java.net.URL;

public class ClassProvider implements IClassProvider {
    private static IClassProvider instance;
    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Main.class.getClassLoader());
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, Main.class.getClassLoader());
    }

    public static IClassProvider getInstance(){
        if(instance == null){
            instance = new ClassProvider();
        }
        return instance;
    }
}


package ru.obabok.mixin;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;
import ru.obabok.Main;

public class SimpleLogger implements ILogger {
    @Override
    public String getId() {
        return "simplelogger";
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public void catching(Level level, Throwable throwable) {
        print(level, "catching", throwable);
    }

    @Override
    public void catching(Throwable throwable) {
        print(Level.ERROR, "catching", throwable);
    }

    @Override
    public void debug(String s, Object... objects) {
        print(Level.DEBUG, s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        print(Level.DEBUG, s, throwable);
    }

    @Override
    public void error(String s, Object... objects) {
        print(Level.ERROR, s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        print(Level.ERROR, s, throwable);
    }

    @Override
    public void fatal(String s, Object... objects) {
        print(Level.FATAL, s, objects);
    }

    @Override
    public void fatal(String s, Throwable throwable) {
        print(Level.FATAL, s, throwable);
    }

    @Override
    public void info(String s, Object... objects) {
        print(Level.INFO, s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        print(Level.INFO, s, throwable);
    }

    @Override
    public void log(Level level, String s, Object... objects) {
        print(level, s, objects);
    }

    @Override
    public void log(Level level, String s, Throwable throwable) {
        print(level, s, throwable);
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        return null;
    }

    @Override
    public void trace(String s, Object... objects) {
        print(Level.TRACE, s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        print(Level.TRACE, s, throwable);
    }

    @Override
    public void warn(String s, Object... objects) {
        print(Level.WARN, s, objects);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        print(Level.WARN, s, throwable);
    }


    private void print(Level level, String s, Object... objects){
        if(level == Level.DEBUG && !Main.DEBUG) return;
        if(level == Level.TRACE && !Main.TRACE) return;
        String message = s;
        for (Object obj : objects) {
            message = message.replaceFirst("\\{}", String.valueOf(obj));
        }
        System.out.println("[" + level + "] " + message);
    }
}

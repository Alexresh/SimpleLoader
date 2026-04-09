//package ru.obabok.utils.logger;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.core.Filter;
//import org.apache.logging.log4j.core.Layout;
//import org.apache.logging.log4j.core.LogEvent;
//import org.apache.logging.log4j.core.LoggerContext;
//import org.apache.logging.log4j.core.appender.AbstractAppender;
//import org.apache.logging.log4j.core.config.Configuration;
//import org.spongepowered.asm.logging.Level;
//import ru.obabok.Main;
//
//import java.io.FileDescriptor;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//import java.io.Serializable;
//
//public class LoggerAppender extends AbstractAppender {
//    private static final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//    private static final Configuration config = ctx.getConfiguration();
//
//    protected LoggerAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
//        super(name, filter, layout, true, null);
//    }
//
//    @Override
//    public void append(LogEvent event) {
//        String message = event.getMessage().getFormattedMessage();
//        Main.LOGGER.info(message);
//    }
//
//    public static void registerAppender(String appenderName, Filter filter, Layout<? extends Serializable> layout){
//        //return new LoggerAppender(name, filter, layout);
//        LoggerAppender appender = new LoggerAppender(appenderName, filter, layout);
//        appender.start();
//        config.getRootLogger().getAppenders().forEach((name, MinecraftAppender) -> {
//            config.getRootLogger().removeAppender(name);
//        });
//        config.addAppender(appender);
//        config.getRootLogger().addAppender(appender, null, null);
//        ctx.updateLoggers();
//
//        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)) {
//            @Override
//            public void println(String x) {
//                Main.LOGGER.print(Level.WARN, x);
//            }
//
//            @Override
//            public void print(String s) {
//                if (s.endsWith("\n")) {
//                    Main.LOGGER.print(Level.WARN, s.substring(0, s.length() - 1));
//                } else {
//                    Main.LOGGER.print(Level.WARN, s);
//                }
//            }
//        });
//
//
//    }
//}

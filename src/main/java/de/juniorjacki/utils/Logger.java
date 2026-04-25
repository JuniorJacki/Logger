package de.juniorjacki.utils;

import de.juniorjacki.utils.logger.Level;
import de.juniorjacki.utils.logger.LogConfiguration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public abstract class Logger {

    private static final DateTimeFormatter LOG_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneId.systemDefault());

    private static String now() {
        return LOG_FORMAT.format(Instant.now());
    }

    private static final ThreadLocal<StringWriter> TL_WRITER =
            ThreadLocal.withInitial(StringWriter::new);

    public static String exceptionToString(Throwable t) {
        StringWriter sw = TL_WRITER.get();
        sw.getBuffer().setLength(0);
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static String stringify(Object msg) {
        if (msg == null) return "null";
        try {
            if (msg instanceof Throwable t) return exceptionToString(t);
            if (msg instanceof Object[] arr) return Arrays.toString(arr);
            return String.valueOf(msg);
        } catch (Throwable t) {
            return "[toString() failed: " + t + "]";
        }
    }

    private static final StackWalker WALKER =
            StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    private static String getCaller() {
        return WALKER.walk(stream ->
                stream
                        .filter(f -> !f.getClassName().equals(Logger.class.getName()))
                        .findFirst()
                        .map(f -> f.getClassName() + "." + f.getMethodName())
                        .orElse("unknown")
        );
    }

    private static void log(String source, Level level, String content) {
        if (LogConfiguration.activeConsumer != null) {
            LogConfiguration.activeConsumer.log(source, level, content);
        } else {
            System.out.println(now() + " [" + level.name() + "] " + source + ": " + content);
        }
    }

    public static void log(Level level, Object msg) {
        log(getCaller(), level, stringify(msg));
    }

    public static void info(Object msg)  { log(Level.INFO, msg); }
    public static void debug(Object msg) { log(Level.DEBUG, msg); }
    public static void warn(Object msg)  { log(Level.WARNING, msg); }
    public static void error(Object msg) { log(Level.ERROR, msg); }

    public static void error(String message, Exception e) {
        log(Level.ERROR, message + "\n" + exceptionToString(e));
    }
}

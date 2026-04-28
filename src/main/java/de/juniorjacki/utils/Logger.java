package de.juniorjacki.utils;

import de.juniorjacki.utils.logger.Level;
import de.juniorjacki.utils.logger.Configuration;
import de.juniorjacki.utils.logger.processing.QueueProcessor;
import de.juniorjacki.utils.logger.Log;

import java.time.Instant;

public final class Logger {




    private static void logAsync(Log log) {
        QueueProcessor.enqueue(log);
    }

    private static void logBlocking(Log log) {
        log.process();
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

    /**
     * Logs a message at the specified log level using either asynchronous (queued)
     * or synchronous (blocking) processing.
     * Asynchronous logging is the default and ensures that the calling thread is
     * never blocked by log formatting, color processing, file I/O or console output.
     * Blocking logging is intended for shutdown sequences, crash logs or situations
     * where guaranteed immediate log persistence is required.
     *
     * @param level         the log level of the message (INFO, DEBUG, WARNING, ERROR)
     * @param includeSource whether the caller's class and method name should be included
     *                      in the log output
     * @param blocking      if {@code true}, the log is processed synchronously in the
     *                      current thread; if {@code false}, the log is queued and
     *                      processed asynchronously
     * @param msgs          one or more message objects to log; each object is converted
     *                      using the logger's stringify and indentation rules
     */
    public static void log(Level level, boolean includeSource, boolean blocking, Object... msgs) {
        if (!Configuration.isEnabled(level)) return;
        Log task = new Log(Instant.now(),getCaller(), level, msgs, includeSource);
        if (blocking) logBlocking(task);
        else logAsync(task);
    }

    /**
     * Logs an INFO-level message asynchronously.
     * @param msgs message objects to log
     */
    public static void info(Object... msgs) { log(Level.INFO, true, false, msgs); }
    /**
     * Logs an INFO-level message synchronously.
     * @param msgs message objects to log
     */
    public static void infoBlocking(Object... msgs) { log(Level.INFO, true, true, msgs); }
    /**
     * Logs an INFO-level message asynchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void infoWithoutSource(Object... msgs) { log(Level.INFO, false, false, msgs); }
    /**
     * Logs an INFO-level message synchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void infoWithoutSourceBlocking(Object... msgs) { log(Level.INFO, false, true, msgs); }

    /**
     * Logs a DEBUG-level message asynchronously.
     *
     * @param msgs message objects to log
     */
    public static void debug(Object... msgs) { log(Level.DEBUG, true, false, msgs); }

    /**
     * Logs a DEBUG-level message synchronously.
     *
     * @param msgs message objects to log
     */
    public static void debugBlocking(Object... msgs) { log(Level.DEBUG, true, true, msgs); }

    /**
     * Logs a DEBUG-level message asynchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void debugWithoutSource(Object... msgs) { log(Level.DEBUG, false, false, msgs); }

    /**
     * Logs a DEBUG-level message synchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void debugWithoutSourceBlocking(Object... msgs) { log(Level.DEBUG, false, true, msgs); }


    /**
     * Logs a WARNING-level message asynchronously.
     *
     * @param msgs message objects to log
     */
    public static void warn(Object... msgs) { log(Level.WARNING, true, false, msgs); }

    /**
     * Logs a WARNING-level message synchronously.
     *
     * @param msgs message objects to log
     */
    public static void warnBlocking(Object... msgs) { log(Level.WARNING, true, true, msgs); }

    /**
     * Logs a WARNING-level message asynchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void warnWithoutSource(Object... msgs) { log(Level.WARNING, false, false, msgs); }

    /**
     * Logs a WARNING-level message synchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void warnWithoutSourceBlocking(Object... msgs) { log(Level.WARNING, false, true, msgs); }

    /**
     * Logs an ERROR-level message asynchronously.
     *
     * @param msgs message objects to log
     */
    public static void error(Object... msgs) { log(Level.ERROR, true, false, msgs); }

    /**
     * Logs an ERROR-level message synchronously.
     *
     * @param msgs message objects to log
     */
    public static void errorBlocking(Object... msgs) { log(Level.ERROR, true, true, msgs); }

    /**
     * Logs an ERROR-level message asynchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void errorWithoutSource(Object... msgs) { log(Level.ERROR, false, false, msgs); }

    /**
     * Logs an ERROR-level message synchronously without including the caller source.
     *
     * @param msgs message objects to log
     */
    public static void errorWithoutSourceBlocking(Object... msgs) { log(Level.ERROR, false, true, msgs); }


}

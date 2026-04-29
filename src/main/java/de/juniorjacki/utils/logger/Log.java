package de.juniorjacki.utils.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Log {
    public final Instant timestamp;
    public final String source;
    public final Level level;
    public final Object[] messages;
    public final boolean includeSource;

    public Log(Instant timestamp, String source, Level level, Object[] messages, boolean includeSource) {
        this.timestamp = timestamp;
        this.source = source;
        this.level = level;
        this.messages = messages;
        this.includeSource = includeSource;
    }

    public void process() {
        String content = stringifyAll(messages);

        boolean externalLogged = false;
        if (Configuration.activeConsumer != null) {
            externalLogged = Configuration.activeConsumer.log(timestamp.getEpochSecond(),source, level, content);
        }
        if (!externalLogged || FileWriter.isEnabled()) {
            String logMsg = formatTime(timestamp) + " [" + level.name() + "] " + (includeSource ? source + ": " : "") + content;
            FileWriter.write(logMsg);
            if (!externalLogged) System.out.println(color(level,logMsg));
        }
    }

    // ANSI Farben
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String SecondRED = "\u001B[91m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";

    private static final DateTimeFormatter LOG_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneId.systemDefault());


    private static String formatTime(Instant instant) {
        return LOG_FORMAT.format(instant);
    }

    // StringBuilder Reuse
    private static final ThreadLocal<StringBuilder> TL_SB =
            ThreadLocal.withInitial(() -> new StringBuilder(512));

    // Exception Writer Reuse
    private static final ThreadLocal<StringWriter> TL_WRITER =
            ThreadLocal.withInitial(StringWriter::new);



    public static String exceptionToString(Throwable t) {
        StringWriter sw = TL_WRITER.get();
        sw.getBuffer().setLength(0);
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private static String indent(String s) {
        return s.replace("\n", "\n    ");
    }

    private static String color(Level level, String msg) {
        return switch (level) {
            case ERROR -> RED + msg + RESET;
            case DEBUG_ERROR -> SecondRED + msg + RESET;
            case WARNING -> YELLOW + msg + RESET;
            case DEBUG -> CYAN + msg + RESET;
            default -> msg;
        };
    }

    private static String stringify(Object msg) {
        if (msg == null) return "null";
        try {
            if (msg instanceof Throwable t) return exceptionToString(t);
            if (msg instanceof Object[] arr) return Arrays.toString(arr);
            return indent(String.valueOf(msg));
        } catch (Throwable t) {
            return "[toString() failed: " + t + "]";
        }
    }

    private static String stringifyAll(Object... msgs) {
        if (msgs == null) return "null";
        StringBuilder sb = TL_SB.get();
        sb.setLength(0);
        for (Object m : msgs) {
            sb.append(stringify(m)).append(" ");
        }
        return sb.toString().trim();
    }
}

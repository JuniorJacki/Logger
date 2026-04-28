package de.juniorjacki.utils.logger;

public final class Configuration {

    public static volatile LogConsumer activeConsumer;

    private static volatile Level minLevel = Level.DEBUG;

    public static void setMinLevel(Level level) {
        minLevel = level;
    }

    public static boolean isEnabled(Level level) {
        return level.ordinal() >= minLevel.ordinal();
    }

    public interface LogConsumer {
        boolean log(long timestamp,String logSource, Level level, String content);
    }
}

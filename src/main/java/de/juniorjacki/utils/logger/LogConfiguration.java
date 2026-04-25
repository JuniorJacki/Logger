package de.juniorjacki.utils.logger;

import de.juniorjacki.utils.Logger;

public class LogConfiguration {

    public static volatile LogConsumer activeConsumer;

    public interface LogConsumer {
        void log(String logSource, Level level, String content);
    }
}

package de.juniorjacki.utils.logger;

public enum Level {
    DEBUG_ERROR(130),
    DEBUG(100),
    WARNING(70),
    ERROR(50),
    INFO(10)
    ;

    public final int neverChangingIdentifier;

    Level(int identifier) {
        this.neverChangingIdentifier = identifier;
    }
}

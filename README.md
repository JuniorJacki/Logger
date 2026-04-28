# ⚡ Java Logger  
A modern, color‑enabled and extensible Java logger focused on clarity, performance, and seamless integration with external log systems.

---

## 🚀 Features

- 🧵 **Thread‑safe asynchronous logging**  
  Log calls never block your application — messages are queued and processed in a background worker.

- ⚡ **Non‑blocking write pipeline**  
  Ideal for high‑performance servers, tools, and microservices.

- 🎨 **ANSI color support**  
  Clean, readable console output with colored log levels.

- 📁 **`latest.log` session file**  
  All logs of the current runtime are written into a single session file.

- 📅 **Automatic daily rotation**  
  On shutdown, `latest.log` is appended to `YYYY‑MM‑DD.log` automatically.

- 🔌 **External log uplinks via `Configuration.activeConsumer`**  
  Forward logs to WebSockets, databases, dashboards, or fully override local logging.

- 🎚️ **Configurable log levels via `Configuration.minimumLevel`**  
  Control which log levels are active (useful for dev/production switching).

- 📦 **Zero dependencies**  
  Pure Java — no external libraries required.

---

## 📦 Maven‑Integration

```xml
<dependency>
    <groupId>de.juniorjacki.utils.logger</groupId>
    <artifactId>logger</artifactId>
    <version>0.1.1</version>
</dependency>
```

## 🛠️ Usage
### Enable the logger
```java
import de.juniorjacki.utils.logger.FileWriter;
import de.juniorjacki.utils.logger.Logger;

public class Main {
    public static void main(String[] args) throws Exception {
        FileWriter.enable("logs"); // Enables Saving of Logs into the "logs" folder

        Logger.info("Server started");
        Logger.warn("Latency increased to",50,"ms");
        Logger.error("Failed to load resource",new Exception("Loading Error"));
    }
}
```

### Output
```code
2026-04-28 20:31:12.123 [INFO] Main.main: Server started
2026-04-28 20:31:12.124 [WARN] Main.main: Latency increased to 50 ms
2026-04-28 20:31:12.125 [ERROR] Main.main: Failed to load resource
java.lang.Exception: Loading Error
    at Main.main(Main.java:8)
```

### 📅 Automatic Rotation
When the program exits:
- latest.log is automatically appended to the daily file (YYYY‑MM‑DD.log)
- No manual action required

## 🔌 External Log Integration (Log Uplink)
The logger supports forwarding all logs to external systems:
### Example: Send logs to a WebSocket uplink
```java
Configuration.activeConsumer = (timestamp,source, level, message) -> {
    MyWebSocketClient.send(
        "[" + level + "] " + source + ": " + message
    );
    return false; // keep local output active
};
```
### Example: Fully override local logging
```java
Configuration.activeConsumer = (timestamp,source, level, message) -> {
    ExternalLogUplink.push(level, source, message,timestamp);
    return true; // suppress local output
};
```
### Example: Write logs into a database
```java
Configuration.activeConsumer = (timestamp,source, level, message) -> {
    Database.insertLog(level.name(), source, message,timestamp);
    return false; // keep local output active
};
```
Behaviour
|Return value|Meaning                    |
|------------|---------------------------|
|true        |Local output is suppressed |
|false       |Local output remains active|

## 🎚️ Log Level Configuration
The logger allows you to control which log levels are processed (Useful for Dev/Production Switching) using:
```java
Configuration.minimumLevel
```

Log levels are ordered exactly as defined in the enum:
```java
public enum Level {
    DEBUG,
    WARNING,
    ERROR,
    INFO
}
```

The logger activates all levels whose ordinal is greater than or equal to the selected one.
### ✔ Default behavior
```java
Configuration.minimumLevel = Level.DEBUG;
```
This enables all log levels.

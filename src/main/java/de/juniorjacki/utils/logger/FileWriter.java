package de.juniorjacki.utils.logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

public final class FileWriter {

    private static volatile boolean enabled = false;
    private static volatile Path logDir;
    private static volatile BufferedWriter writer;
    private static volatile LocalDate currentDay;

    public static synchronized void enable(String relativeToHomePath) throws IOException {
        enable(Path.of(System.getProperty("user.dir")).resolve(relativeToHomePath).toAbsolutePath());
    }

    public static synchronized void enable(Path directory) throws IOException {
        enabled = true;
        logDir = directory;
        Files.createDirectories(logDir);

        Path latest = logDir.resolve("latest.log");
        Files.writeString(latest, "", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        rotateIfNeeded();
    }


    public static synchronized void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static synchronized void write(String line) {
        if (!enabled) return;

        try {
            rotateIfNeeded();
            writer.write(line);
            writer.newLine();
            writer.flush();
            Path latest = logDir.resolve("latest.log");
            Files.writeString(latest, line + System.lineSeparator(),
                    StandardOpenOption.APPEND);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void rotateIfNeeded() throws IOException {
        LocalDate today = LocalDate.now();
        if (writer != null && today.equals(currentDay)) return;

        currentDay = today;

        if (writer != null) writer.close();

        Path dailyFile = logDir.resolve(today + ".log");
        writer = Files.newBufferedWriter(dailyFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Path latest = logDir.resolve("latest.log");
        try {
            Files.deleteIfExists(latest);
        } catch (IOException ignored) {}

        Files.copy(dailyFile, latest, StandardCopyOption.REPLACE_EXISTING);
    }
}

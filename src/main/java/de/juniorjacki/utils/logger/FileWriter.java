package de.juniorjacki.utils.logger;

import de.juniorjacki.utils.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

public final class FileWriter {

    private static volatile boolean enabled = false;
    private static volatile Path logDir;
    private static volatile Path latestFile;
    private static volatile BufferedWriter latestWriter;
    private static volatile LocalDate startDay;

    public static synchronized void enable(String relativeToHomePath) throws IOException {
        enable(Path.of(System.getProperty("user.dir")).resolve(relativeToHomePath).toAbsolutePath());
    }

    public static synchronized void enable(Path directory) throws IOException {
        if (enabled) return;

        enabled = true;
        logDir = directory;
        Files.createDirectories(logDir);

        startDay = LocalDate.now();
        latestFile = logDir.resolve("latest.log");
        latestWriter = Files.newBufferedWriter(
                latestFile,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        );
    }

    public static synchronized void disable() {
        if (!enabled) return;
        enabled = false;

        try {
            if (latestWriter != null) {
                latestWriter.flush();
                latestWriter.close();
            }
            Path dailyFile = logDir.resolve(startDay + ".log");
            if (Files.exists(latestFile)) {
                try (InputStream in = Files.newInputStream(latestFile);
                     OutputStream out = Files.newOutputStream(
                             dailyFile,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.APPEND
                     )) {
                    in.transferTo(out);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            latestWriter = null;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static synchronized void write(String line) {
        if (!enabled) return;
        try {
            if (latestWriter == null) {
                latestWriter = Files.newBufferedWriter(
                        latestFile,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND,
                        StandardOpenOption.WRITE
                );
            }
            latestWriter.write(line);
            latestWriter.newLine();
            latestWriter.flush();
        } catch (IOException e) {
            Logger.error("Error while writing log to file: " + e.getMessage());
        }
    }
}

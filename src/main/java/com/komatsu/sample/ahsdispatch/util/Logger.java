package com.komatsu.sample.ahsdispatch.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public final class Logger {

    private static final Path LOG_DIR = Paths.get("logs");
    private static final Path LOG_FILE = LOG_DIR.resolve("dispatch.log");

    private static final long MAX_BYTES_BEFORE_ROTATE = 2L * 1024 * 1024;

    private Logger() {}

    public static void info(String event, String message) {
        log("INFO", event, message);
    }

    public static void warn(String event, String message) {
        log("WARN", event, message);
    }

    public static void error(String event, String message) {
        log("ERROR", event, message);
    }

    private static void log(String level, String event, String message) {
        String ts = Instant.now().toString();
        String line = String.format("%s level=%s event=%s %s", ts, level, event, message);

        System.out.println(line);

        try {
            ensureLogDir();
            rotateIfNeeded();
            appendLine(line);
        } catch (IOException e) {
            System.out.println(ts + " level=ERROR event=LOGGER_FAILURE reason=" + e.getMessage());
        }
    }

    private static void ensureLogDir() throws IOException {
        if (!Files.exists(LOG_DIR)) {
            Files.createDirectories(LOG_DIR);
        }
    }

    private static void rotateIfNeeded() throws IOException {
        if (Files.exists(LOG_FILE)) {
            long size = Files.size(LOG_FILE);
            if (size >= MAX_BYTES_BEFORE_ROTATE) {
                String suffix = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")
                        .withZone(java.time.ZoneId.of("UTC"))
                        .format(Instant.now());
                Path rotated = LOG_DIR.resolve("dispatch-" + suffix + ".log");
                Files.move(LOG_FILE, rotated, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void appendLine(String line) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                LOG_FILE,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        )) {
            writer.write(line);
            writer.newLine();
        }
    }
}



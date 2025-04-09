package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.exception.InvalidInputException;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService {

    private static final String LOG_FILE_PATH = "log/app.log";
    private static final Path SECURE_TEMP_DIR = Paths.get("D:/JavaProjects/InternetShop/log");

    private final Map<String, String> logFileStatus = new ConcurrentHashMap<>();
    private final Map<String, Path> logFilePaths = new ConcurrentHashMap<>();

    static {
        try {
            if (!Files.exists(SECURE_TEMP_DIR)) {
                Files.createDirectories(SECURE_TEMP_DIR);
                log.info("Created secure temporary directory: {}", SECURE_TEMP_DIR);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create secure temp directory", e);
        }
    }

    public String createLogFileAsync(String date) {
        String id = UUID.randomUUID().toString();
        logFileStatus.put(id, "IN_PROGRESS");
        CompletableFuture.runAsync(() -> generateLogFile(id, date));
        return id;
    }

    private void generateLogFile(String id, String date) {
        try {
            LocalDate logDate = parseDate(date);
            Path logFilePath = Paths.get(LOG_FILE_PATH);
            validateLogFileExists(logFilePath);
            String formattedDate = logDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            Path tempFile = createTempFile(logDate);
            filterAndWriteLogsToTempFile(logFilePath, formattedDate, tempFile);

            logFilePaths.put(id, tempFile);
            logFileStatus.put(id, "COMPLETED");
            log.info("Log file with ID {} successfully created", id);
        } catch (Exception e) {
            logFileStatus.put(id, "FAILED");
            log.error("Error creating log file with ID {}: {}", id, e.getMessage());
        }
    }

    public String getLogFileStatus(String id) {
        return logFileStatus.getOrDefault(id, "NOT_FOUND");
    }

    public Resource getLogFileById(String id) {
        Path filePath = logFilePaths.get(id);
        if (filePath == null || !"COMPLETED".equals(logFileStatus.get(id))) {
            return null;
        }
        try {
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new IllegalStateException("Error accessing log file: " + e.getMessage());
        }
    }

    public Resource downloadLogs(String date) {
        LocalDate logDate = parseDate(date);
        Path logFilePath = Paths.get(LOG_FILE_PATH);
        validateLogFileExists(logFilePath);
        String formattedDate = logDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Path tempFile = createTempFile(logDate);
        filterAndWriteLogsToTempFile(logFilePath, formattedDate, tempFile);

        Resource resource = createResourceFromTempFile(tempFile, date);
        log.info("Log file with date {} downloaded successfully", date);
        return resource;
    }

    private LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Required dd-MM-yyyy");
        }
    }

    private void validateLogFileExists(Path path) {
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File doesn't exist: " + LOG_FILE_PATH);
        }
    }

    private Path createTempFile(LocalDate logDate) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                File tempFile = Files.createTempFile(SECURE_TEMP_DIR, "log-" + logDate + "-", ".log").toFile();
                if (!tempFile.setReadable(true, true)) {
                    throw new IllegalStateException("Failed to set readable permission on temp file: " + tempFile);
                }
                if (!tempFile.setWritable(true, true)) {
                    throw new IllegalStateException("Failed to set writable permission on temp file: " + tempFile);
                }
                if (tempFile.canExecute() && !tempFile.setExecutable(false, false)) {
                    log.warn("Failed to remove executable permission on temp file: {}", tempFile);
                }
                log.info("Created secure temp file on Windows: {}", tempFile.getAbsolutePath());
                return tempFile.toPath();
            } else {
                FileAttribute<Set<PosixFilePermission>> attr =
                        PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
                Path tempFile = Files.createTempFile(SECURE_TEMP_DIR, "log-" + logDate + "-", ".log", attr);
                log.info("Created secure temp file on Unix/Linux: {}", tempFile.toAbsolutePath());
                return tempFile;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error creating temp file: " + e.getMessage());
        }
    }

    private void filterAndWriteLogsToTempFile(Path logFilePath, String formattedDate, Path tempFile) {
        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            Files.write(tempFile, reader.lines()
                    .filter(line -> line.contains(formattedDate))
                    .toList());
            log.info("Filtered logs for date {} written to temp file {}", formattedDate, tempFile);
        } catch (IOException e) {
            throw new IllegalStateException("Error processing log file: " + e.getMessage());
        }
    }

    private Resource createResourceFromTempFile(Path tempFile, String date) {
        try {
            if (Files.size(tempFile) == 0) {
                throw new ResourceNotFoundException("There are no logs for specified date: " + date);
            }
            Resource resource = new UrlResource(tempFile.toUri());
            tempFile.toFile().deleteOnExit();
            log.info("Created downloadable resource from temp file: {}", tempFile);
            return resource;
        } catch (IOException e) {
            throw new IllegalStateException("Error creating resource from temp file: " + e.getMessage());
        }
    }

}

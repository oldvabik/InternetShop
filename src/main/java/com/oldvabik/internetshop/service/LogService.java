package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.exception.InvalidInputException;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import java.io.BufferedReader;
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
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService {

    private static final String LOG_FILE_PATH = "log/app.log";

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
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));

            if (Files.getFileStore(tempDir).supportsFileAttributeView("posix")) {
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
                return Files.createTempFile("log-" + logDate, ".log", attr);
            } else {
                return Files.createTempFile("log-" + logDate, ".log");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error creating temp file: " + e.getMessage());
        }
    }

    private void filterAndWriteLogsToTempFile(Path logFilePath, String formattedDate,
                                              Path tempFile) {
        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            Files.write(tempFile, reader.lines()
                    .filter(line -> line.contains(formattedDate))
                    .toList());
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
            return resource;
        } catch (IOException e) {
            throw new IllegalStateException("Error creating resource from temp file: "
                    + e.getMessage());
        }
    }
}

package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import com.oldvabik.internetshop.model.LogObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LogService {

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<Long, LogObject> tasks = new ConcurrentHashMap<>();
    private static final String LOG_FILE_PATH = "log/app.log";
    private final LogService self;

    public LogService(@Lazy LogService self) {
        this.self = self;
    }

    @Async("executor")
    public void createLogs(Long taskId, String date) {
        try {
            Thread.sleep(5000);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate logDate = LocalDate.parse(date, formatter);

            Path path = Paths.get(LOG_FILE_PATH);
            List<String> logLines = Files.readAllLines(path);
            String formattedDate = logDate.format(formatter);
            List<String> currentLogs = logLines.stream()
                    .filter(line -> line.startsWith(formattedDate))
                    .toList();

            if (currentLogs.isEmpty()) {
                LogObject logObject = tasks.get(taskId);
                if (logObject != null) {
                    logObject.setStatus("FAILED");
                    logObject.setErrorMessage("Нет логов за дату: " + date);
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Нет логов за дату: " + date);
            }

            Path logFile;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                logFile = Files.createTempFile("logs-" + formattedDate, ".log");
            } else {
                FileAttribute<Set<PosixFilePermission>> attr =
                        PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
                logFile = Files.createTempFile("logs-" + formattedDate, ".log", attr);
            }

            Files.write(logFile, currentLogs);
            logFile.toFile().deleteOnExit();

            LogObject task = tasks.get(taskId);
            if (task != null) {
                task.setStatus("COMPLETED");
                task.setFilePath(logFile.toString());
            }
        } catch (IOException e) {
            LogObject task = tasks.get(taskId);
            if (task != null) {
                task.setStatus("FAILED");
                task.setErrorMessage(e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Long createLogAsync(String date) {
        Long id = idCounter.getAndIncrement();
        LogObject logObject = new LogObject(id, "IN_PROGRESS");
        tasks.put(id, logObject);
        self.createLogs(id, date);
        return id;
    }

    public LogObject getStatus(Long taskId) {
        return tasks.get(taskId);
    }

    public ResponseEntity<Resource> downloadCreatedLogs(Long taskId) throws IOException {
        LogObject logObject = getStatus(taskId);
        if (logObject == null) {
            throw new ResourceNotFoundException("Not found log file");
        }
        if (!"COMPLETED".equals(logObject.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The logs are not ready yet");
        }

        Path path = Paths.get(logObject.getFilePath());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}

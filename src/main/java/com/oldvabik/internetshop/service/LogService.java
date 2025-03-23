package com.oldvabik.internetshop.service;

import com.oldvabik.internetshop.exception.InvalidInputException;
import com.oldvabik.internetshop.exception.LoggingException;
import com.oldvabik.internetshop.exception.ResourceNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final String LOGS_DIR = "log";
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter LOG_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Resource getLogFileForDate(String date) {
        LocalDate parsedDate = parseDate(date);
        String formattedDate = parsedDate.format(LOG_DATE_FORMATTER);

        Path logFilePath = Paths.get(LOGS_DIR, "app.log");
        if (!Files.exists(logFilePath)) {
            throw new ResourceNotFoundException("Log file does not exist.");
        }

        List<String> filteredLines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(logFilePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(formattedDate)) {
                    filteredLines.add(line);
                }
            }
        } catch (IOException ex) {
            throw new LoggingException("Error reading log file");
        }

        if (filteredLines.isEmpty()) {
            throw new ResourceNotFoundException("No logs found for this date.");
        }

        String fileContent = String.join(System.lineSeparator(), filteredLines);
        return new ByteArrayResource(fileContent.getBytes(StandardCharsets.UTF_8));
    }


    public String getDownloadFileName(String date) {
        LocalDate parsedDate = parseDate(date);
        return String.format("app-%s.log", parsedDate.format(LOG_DATE_FORMATTER));
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new InvalidInputException("Incorrect date format. Use dd.MM.yyyy.");
        }
    }

}

//package com.oldvabik.internetshop.service;
//
//import com.oldvabik.internetshop.exception.InvalidInputException;
//import com.oldvabik.internetshop.exception.ResourceNotFoundException;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.core.io.Resource;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Stream;
//
//@ExtendWith(MockitoExtension.class)
//class LogServiceTest {
//
//    private final LogService logService = new LogService();
//    private final Path logDir = Paths.get("log");
//    private final Path logFilePath = logDir.resolve("app.log");
//
//    @BeforeEach
//    void setUp() throws IOException {
//        if (!Files.exists(logDir)) {
//            Files.createDirectory(logDir);
//        }
//    }
//
//    @AfterEach
//    void tearDown() throws IOException {
//        if (Files.exists(logFilePath)) {
//            Files.delete(logFilePath);
//        }
//        if (Files.exists(logDir)) {
//            try (Stream<Path> stream = Files.list(logDir)) {
//                if (stream.findAny().isEmpty()) {
//                    Files.delete(logDir);
//                }
//            }
//        }
//    }
//
//    @Test
//    void downloadLogs_success() throws Exception {
//        String date = "15-03-2025";
//        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//
//        String matchingLine = "INFO - Log entry on " + formattedDate;
//        String nonMatchingLine = "INFO - Some other log entry";
//        List<String> lines = Arrays.asList(matchingLine, nonMatchingLine);
//        Files.write(logFilePath, lines);
//
//        Resource resource = logService.downloadLogs(date);
//
//        Assertions.assertTrue(resource.exists());
//        List<String> resourceLines = Files.readAllLines(Paths.get(resource.getURI()));
//        Assertions.assertEquals(1, resourceLines.size());
//        Assertions.assertEquals(matchingLine, resourceLines.get(0));
//    }
//
//    @Test
//    void downloadLogs_invalidDateFormat() {
//        String invalidDate = "2025-03-15";
//        Assertions.assertThrows(InvalidInputException.class, () -> logService.downloadLogs(invalidDate));
//    }
//
//    @Test
//    void downloadLogs_logFileNotFound() {
//        if (Files.exists(logFilePath)) {
//            try {
//                Files.delete(logFilePath);
//            } catch (IOException e) {
//                // ignore
//            }
//        }
//        Assertions.assertThrows(ResourceNotFoundException.class, () -> logService.downloadLogs("15-03-2025"));
//    }
//
//    @Test
//    void downloadLogs_noLogsForSpecifiedDate() throws Exception {
//        List<String> lines = Arrays.asList("INFO - Log entry on 14-03-2025", "INFO - Another log entry");
//        Files.write(logFilePath, lines);
//
//        Assertions.assertThrows(ResourceNotFoundException.class, () -> logService.downloadLogs("16-03-2025"));
//    }
//
//}

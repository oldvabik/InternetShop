package com.oldvabik.warehousemanagement.controller;

import com.oldvabik.warehousemanagement.service.VisitCounterService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visits")
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(visitCounterService.getStats());
    }

}
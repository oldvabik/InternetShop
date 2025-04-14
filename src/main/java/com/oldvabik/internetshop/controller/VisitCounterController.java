package com.oldvabik.internetshop.controller;

import com.oldvabik.internetshop.service.VisitCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }
    
    @GetMapping("/increment")
    public ResponseEntity<String> incrementVisit() {
        visitCounterService.increment();
        return ResponseEntity.ok("Посещение увеличено");
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount() {
        Long count = visitCounterService.getCounter();
        return ResponseEntity.ok(count);
    }
}
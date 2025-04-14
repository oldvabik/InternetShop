package com.oldvabik.internetshop.controller;

import com.oldvabik.internetshop.service.VisitCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visits")
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/increment")
    public ResponseEntity<String> incrementVisit(@RequestParam String url) {
        visitCounterService.increment(url);
        String res = "Посещение увеличено";
        return ResponseEntity.ok(res);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@RequestParam String url) {
        int count = visitCounterService.getCount(url);
        return ResponseEntity.ok(count);
    }
}
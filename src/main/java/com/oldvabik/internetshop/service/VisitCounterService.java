package com.oldvabik.internetshop.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterService {

    private final ConcurrentHashMap<String, AtomicInteger> visitCounts = new ConcurrentHashMap<>();

    public void increment(String url) {
        visitCounts.computeIfAbsent(url, key -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int getCount(String url) {
        return visitCounts.getOrDefault(url, new AtomicInteger(0)).get();
    }

}

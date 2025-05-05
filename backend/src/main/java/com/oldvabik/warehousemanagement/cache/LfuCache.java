package com.oldvabik.warehousemanagement.cache;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class LfuCache<T> {

    private final int capacity;
    private final Map<Long, Entry<T>> cache = new HashMap<>();

    protected static class Entry<T> {
        T value;
        int frequency;

        Entry(T value) {
            this.value = value;
            this.frequency = 1;
        }
    }

    protected LfuCache(int capacity) {
        this.capacity = capacity;
    }

    public T get(Long id) {
        Entry<T> entry = cache.get(id);
        if (entry == null) {
            return null;
        }
        entry.frequency++;
        return entry.value;
    }

    public void put(Long id, T value) {
        if (cache.containsKey(id)) {
            Entry<T> entry = cache.get(id);
            entry.value = value;
            entry.frequency++;
        } else {
            if (cache.size() >= capacity) {
                evictLeastFrequentlyUsed();
            }
            cache.put(id, new Entry<>(value));
        }
    }

    private void evictLeastFrequentlyUsed() {
        Long lfuKey = null;
        int minFrequency = Integer.MAX_VALUE;

        for (Map.Entry<Long, Entry<T>> entry : cache.entrySet()) {
            if (entry.getValue().frequency < minFrequency) {
                minFrequency = entry.getValue().frequency;
                lfuKey = entry.getKey();
            }
        }

        if (lfuKey != null) {
            cache.remove(lfuKey);
        }
    }

    public void remove(Long id) {
        cache.remove(id);
    }

    public void clear() {
        cache.clear();
    }
}
package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CacheService {
//    private final CacheManager cacheManager;
//
//    public void clearAllCache() {
//        cacheManager.getCacheNames().forEach(name -> {
//            Objects.requireNonNull(cacheManager.getCache(name)).clear();
//        });
//    }
//
//    public void clearProjectCache() {
//        Objects.requireNonNull(cacheManager.getCache("PROJECT_CACHE")).clear();
//    }
}

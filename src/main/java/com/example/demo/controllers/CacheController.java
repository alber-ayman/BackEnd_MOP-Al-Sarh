package com.example.demo.controllers;

import com.example.demo.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearAllCache() {
//        cacheService.clearAllCache();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear-projects")
    public ResponseEntity<Void> clearProjectCache() {
//        cacheService.clearProjectCache();
        return ResponseEntity.noContent().build();
    }
}

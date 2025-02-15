package com.liondance.liondance_backend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Profile("!production")
@RestController
@RequestMapping("/reset-db")
public class DataLoaderController {
    private final DataLoaderService dataLoaderService;

    public DataLoaderController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @GetMapping()
    public ResponseEntity<Void> resetDb() {
        log.debug("Resetting DB for e2e...");
        try {
            dataLoaderService.run();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

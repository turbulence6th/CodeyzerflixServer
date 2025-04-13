package com.codeyzerflix.admin.service;

import com.codeyzerflix.common.service.GridFsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GridFsCleanupScheduler {

    private final GridFsService gridFsService;

    // Her 30 dakikada bir çalışacak
    @Scheduled(fixedRate = 1800000) // 30 dakika = 1800000 ms
    public void cleanupOrphanedFiles() {
        try {
            gridFsService.deleteUnusedGridFsFiles();
            System.out.println("Orphaned files cleaned up successfully.");
        } catch (Exception e) {
            System.err.println("Error cleaning up orphaned files: " + e.getMessage());
        }
    }
}
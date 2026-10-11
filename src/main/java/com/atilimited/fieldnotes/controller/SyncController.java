package com.atilimited.fieldnotes.controller;

import com.atilimited.fieldnotes.dto.SyncPullResponse;
import com.atilimited.fieldnotes.dto.SyncPushRequest;
import com.atilimited.fieldnotes.service.SyncService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

@PostMapping("/push")
    public Map<String, Instant> push(@Valid @RequestBody SyncPushRequest req) {
        Instant serverTime = syncService.push(req);
        return Map.of("serverTime", serverTime);
    }

@GetMapping("/pull")
    public SyncPullResponse pull(@RequestParam(required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since) {
        return syncService.pull(since);
    }
}

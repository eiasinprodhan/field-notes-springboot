package com.atilimited.fieldnotes.controller;

import com.atilimited.fieldnotes.dto.SiteRequest;
import com.atilimited.fieldnotes.dto.SiteResponse;
import com.atilimited.fieldnotes.service.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public List<SiteResponse> list() {
        return siteService.listAll();
    }

    @PostMapping
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody SiteRequest req) {
        return ResponseEntity.status(201).body(siteService.create(req));
    }

    @GetMapping("/{uuid}")
    public SiteResponse get(@PathVariable String uuid) {
        return siteService.get(uuid);
    }

    @PutMapping("/{uuid}")
    public SiteResponse update(@PathVariable String uuid, @Valid @RequestBody SiteRequest req) {
        return siteService.update(uuid, req);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        siteService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}

package com.atilimited.fieldnotes.controller;

import com.atilimited.fieldnotes.dto.FieldNoteRequest;
import com.atilimited.fieldnotes.dto.FieldNoteResponse;
import com.atilimited.fieldnotes.model.NoteStatus;
import com.atilimited.fieldnotes.service.FieldNoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class FieldNoteController {

    private final FieldNoteService fieldNoteService;

    public FieldNoteController(FieldNoteService fieldNoteService) {
        this.fieldNoteService = fieldNoteService;
    }

@GetMapping
    public List<FieldNoteResponse> list(@RequestParam(required = false) String search,
                                        @RequestParam(required = false) NoteStatus status,
                                        @RequestParam(required = false) String siteUuid) {
        return fieldNoteService.search(search, status, siteUuid);
    }

    @PostMapping
    public ResponseEntity<FieldNoteResponse> create(@Valid @RequestBody FieldNoteRequest req) {
        return ResponseEntity.status(201).body(fieldNoteService.create(req));
    }

    @GetMapping("/{uuid}")
    public FieldNoteResponse get(@PathVariable String uuid) {
        return fieldNoteService.get(uuid);
    }

    @PutMapping("/{uuid}")
    public FieldNoteResponse update(@PathVariable String uuid, @Valid @RequestBody FieldNoteRequest req) {
        return fieldNoteService.update(uuid, req);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        fieldNoteService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}

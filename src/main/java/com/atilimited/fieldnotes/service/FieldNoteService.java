package com.atilimited.fieldnotes.service;

import com.atilimited.fieldnotes.dto.FieldNoteRequest;
import com.atilimited.fieldnotes.dto.FieldNoteResponse;
import com.atilimited.fieldnotes.exception.ResourceNotFoundException;
import com.atilimited.fieldnotes.model.FieldNote;
import com.atilimited.fieldnotes.model.NoteStatus;
import com.atilimited.fieldnotes.model.Site;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.FieldNoteRepository;
import com.atilimited.fieldnotes.repository.SiteRepository;
import com.atilimited.fieldnotes.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FieldNoteService {

    private final FieldNoteRepository fieldNoteRepository;
    private final SiteRepository siteRepository;
    private final CurrentUser currentUser;

    public FieldNoteService(FieldNoteRepository fieldNoteRepository,
                            SiteRepository siteRepository,
                            CurrentUser currentUser) {
        this.fieldNoteRepository = fieldNoteRepository;
        this.siteRepository = siteRepository;
        this.currentUser = currentUser;
    }

public List<FieldNoteResponse> search(String search, NoteStatus status, String siteUuid) {
        User user = currentUser.get();
        String q = (search == null || search.isBlank()) ? null : search.trim();
        return fieldNoteRepository.search(user, q, status, siteUuid).stream()
                .map(FieldNoteResponse::from)
                .collect(Collectors.toList());
    }

    public FieldNoteResponse create(FieldNoteRequest req) {
        User user = currentUser.get();
        Site site = siteRepository.findByUserAndUuid(user, req.getSiteUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Site", req.getSiteUuid()));
        FieldNote n = FieldNote.builder()
                .site(site)
                .title(req.getTitle())
                .description(req.getDescription())
                .location(req.getLocation())
                .dateTime(req.getDateTime())
                .status(req.getStatus())
                .photoUrl(req.getPhotoUrl())
                .build();
        return FieldNoteResponse.from(fieldNoteRepository.save(n));
    }

    public FieldNoteResponse get(String uuid) {
        FieldNote n = fieldNoteRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("FieldNote", uuid));
        return FieldNoteResponse.from(n);
    }

    public FieldNoteResponse update(String uuid, FieldNoteRequest req) {
        FieldNote n = fieldNoteRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("FieldNote", uuid));
        Site site = siteRepository.findByUserAndUuid(currentUser.get(), req.getSiteUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Site", req.getSiteUuid()));
        n.setSite(site);
        n.setTitle(req.getTitle());
        n.setDescription(req.getDescription());
        n.setLocation(req.getLocation());
        n.setDateTime(req.getDateTime());
        n.setStatus(req.getStatus());
        n.setPhotoUrl(req.getPhotoUrl());
        return FieldNoteResponse.from(fieldNoteRepository.save(n));
    }

    public void delete(String uuid) {
        FieldNote n = fieldNoteRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("FieldNote", uuid));
        n.setDeleted(true);
        fieldNoteRepository.save(n);
    }
}

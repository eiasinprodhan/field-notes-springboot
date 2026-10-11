package com.atilimited.fieldnotes.dto;

import com.atilimited.fieldnotes.model.FieldNote;
import com.atilimited.fieldnotes.model.NoteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldNoteResponse {
    private String uuid;
    private Instant updatedAt;
    private boolean deleted;
    private String siteUuid;
    private String customerUuid;
    private String siteName;
    private String customerName;
    private String title;
    private String description;
    private String location;
    private Instant dateTime;
    private NoteStatus status;
    private String photoUrl;

    public static FieldNoteResponse from(FieldNote n) {
        return new FieldNoteResponse(
                n.getUuid(),
                n.getUpdatedAt(),
                n.isDeleted(),
                n.getSite().getUuid(),
                n.getSite().getCustomer().getUuid(),
                n.getSite().getSiteName(),
                n.getSite().getCustomer().getName(),
                n.getTitle(),
                n.getDescription(),
                n.getLocation(),
                n.getDateTime(),
                n.getStatus(),
                n.getPhotoUrl());
    }
}

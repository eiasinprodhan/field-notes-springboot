package com.atilimited.fieldnotes.dto;

import com.atilimited.fieldnotes.model.NoteStatus;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Data
public class FieldNoteRequest {
    @NotBlank
    private String siteUuid; 

    @NotBlank @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @Size(max = 200)
    private String location;

    @NotNull
    private Instant dateTime;

    @NotNull
    private NoteStatus status;

    @Size(max = 500)
    private String photoUrl;
}

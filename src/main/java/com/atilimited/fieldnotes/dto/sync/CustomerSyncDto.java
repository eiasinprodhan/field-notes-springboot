package com.atilimited.fieldnotes.dto.sync;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Data
public class CustomerSyncDto {
    @NotBlank
    private String uuid;

    @NotNull
    private Instant updatedAt;

    private boolean deleted;

    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String contactInfo;
}

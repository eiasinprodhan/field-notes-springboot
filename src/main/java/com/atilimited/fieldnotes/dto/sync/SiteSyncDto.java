package com.atilimited.fieldnotes.dto.sync;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Data
public class SiteSyncDto {
    @NotBlank
    private String uuid;

    @NotNull
    private Instant updatedAt;

    private boolean deleted;

    @NotBlank
    private String customerUuid; 

    @Size(max = 200)
    private String siteName;

    @Size(max = 500)
    private String address;
}

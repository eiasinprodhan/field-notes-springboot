package com.atilimited.fieldnotes.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class SiteRequest {
    @NotBlank
    private String customerUuid;

    @NotBlank @Size(max = 200)
    private String siteName;

    @Size(max = 500)
    private String address;
}

package com.atilimited.fieldnotes.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CustomerRequest {
    @NotBlank @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String contactInfo;
}

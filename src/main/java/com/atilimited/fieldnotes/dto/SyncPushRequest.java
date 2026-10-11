package com.atilimited.fieldnotes.dto;

import com.atilimited.fieldnotes.dto.sync.CustomerSyncDto;
import com.atilimited.fieldnotes.dto.sync.FieldNoteSyncDto;
import com.atilimited.fieldnotes.dto.sync.SiteSyncDto;
import lombok.Data;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public class SyncPushRequest {
    @Valid
    private List<CustomerSyncDto> customers = new ArrayList<>();
    @Valid
    private List<SiteSyncDto> sites = new ArrayList<>();
    @Valid
    private List<FieldNoteSyncDto> notes = new ArrayList<>();
}

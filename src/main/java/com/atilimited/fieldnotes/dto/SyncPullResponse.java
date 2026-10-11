package com.atilimited.fieldnotes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class SyncPullResponse {
    private Instant serverTime;
    private List<CustomerResponse> customers;
    private List<SiteResponse> sites;
    private List<FieldNoteResponse> notes;
}

package com.atilimited.fieldnotes.dto;

import com.atilimited.fieldnotes.model.Site;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteResponse {
    private String uuid;
    private Instant updatedAt;
    private boolean deleted;
    private String customerUuid;
    private String siteName;
    private String address;

    public static SiteResponse from(Site s) {
        return new SiteResponse(
                s.getUuid(),
                s.getUpdatedAt(),
                s.isDeleted(),
                s.getCustomer().getUuid(),
                s.getSiteName(),
                s.getAddress());
    }
}

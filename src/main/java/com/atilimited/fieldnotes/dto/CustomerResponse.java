package com.atilimited.fieldnotes.dto;

import com.atilimited.fieldnotes.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private String uuid;
    private Instant updatedAt;
    private boolean deleted;
    private String name;
    private String contactInfo;

    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getUuid(),
                c.getUpdatedAt(),
                c.isDeleted(),
                c.getName(),
                c.getContactInfo());
    }
}

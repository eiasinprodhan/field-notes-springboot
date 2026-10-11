package com.atilimited.fieldnotes.service;

import com.atilimited.fieldnotes.dto.SyncPullResponse;
import com.atilimited.fieldnotes.dto.SyncPushRequest;
import com.atilimited.fieldnotes.dto.CustomerResponse;
import com.atilimited.fieldnotes.dto.SiteResponse;
import com.atilimited.fieldnotes.dto.FieldNoteResponse;
import com.atilimited.fieldnotes.dto.sync.CustomerSyncDto;
import com.atilimited.fieldnotes.dto.sync.SiteSyncDto;
import com.atilimited.fieldnotes.dto.sync.FieldNoteSyncDto;
import com.atilimited.fieldnotes.model.Customer;
import com.atilimited.fieldnotes.model.FieldNote;
import com.atilimited.fieldnotes.model.Site;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.CustomerRepository;
import com.atilimited.fieldnotes.repository.FieldNoteRepository;
import com.atilimited.fieldnotes.repository.SiteRepository;
import com.atilimited.fieldnotes.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class SyncService {

    private final CustomerRepository customerRepository;
    private final SiteRepository siteRepository;
    private final FieldNoteRepository fieldNoteRepository;
    private final CurrentUser currentUser;

    public SyncService(CustomerRepository customerRepository,
                       SiteRepository siteRepository,
                       FieldNoteRepository fieldNoteRepository,
                       CurrentUser currentUser) {
        this.customerRepository = customerRepository;
        this.siteRepository = siteRepository;
        this.fieldNoteRepository = fieldNoteRepository;
        this.currentUser = currentUser;
    }

    public Instant push(SyncPushRequest req) {
        User user = currentUser.get();

Map<String, Customer> customerByUuid = new HashMap<>();
        for (CustomerSyncDto d : req.getCustomers()) {
            Customer c = customerRepository.findByUserAndUuid(user, d.getUuid()).orElse(null);
            boolean isNew = (c == null);
            if (isNew) {
                if (d.isDeleted()) continue; 
                c = new Customer();
                c.setUser(user);
                c.setUuid(d.getUuid());
            }
            if (applyCustomer(c, d) || isNew) {
                c = customerRepository.save(c);
            }
            customerByUuid.put(d.getUuid(), c);
        }

Map<String, Site> siteByUuid = new HashMap<>();
        for (SiteSyncDto d : req.getSites()) {
            Site s = siteRepository.findByUserAndUuid(user, d.getUuid()).orElse(null);
            boolean isNew = (s == null);
            if (isNew) {
                if (d.isDeleted()) continue;
                s = new Site();
                s.setUuid(d.getUuid());
            }
            Customer customer = customerByUuid.get(d.getCustomerUuid());
            if (customer == null) {
                customer = customerRepository.findByUserAndUuid(user, d.getCustomerUuid()).orElse(null);
            }
            if (customer == null) continue; 
            s.setCustomer(customer);
            if (applySite(s, d) || isNew) {
                s = siteRepository.save(s);
            }
            siteByUuid.put(d.getUuid(), s);
        }

for (FieldNoteSyncDto d : req.getNotes()) {
            FieldNote n = fieldNoteRepository.findByUserAndUuid(user, d.getUuid()).orElse(null);
            boolean isNew = (n == null);
            if (isNew) {
                if (d.isDeleted()) continue;
                n = new FieldNote();
                n.setUuid(d.getUuid());
            }
            Site site = siteByUuid.get(d.getSiteUuid());
            if (site == null) {
                site = siteRepository.findByUserAndUuid(user, d.getSiteUuid()).orElse(null);
            }
            if (site == null) continue; 
            n.setSite(site);
            if (applyNote(n, d) || isNew) {
                fieldNoteRepository.save(n);
            }
        }

        return Instant.now();
    }

    public SyncPullResponse pull(Instant since) {
        User user = currentUser.get();
        Instant from = (since != null) ? since : Instant.EPOCH;

        List<CustomerResponse> customers = customerRepository.findAllByUserSince(user, from).stream()
                .map(CustomerResponse::from).collect(Collectors.toList());
        List<SiteResponse> sites = siteRepository.findAllByUserSince(user, from).stream()
                .map(SiteResponse::from).collect(Collectors.toList());
        List<FieldNoteResponse> notes = fieldNoteRepository.findAllByUserSince(user, from).stream()
                .map(FieldNoteResponse::from).collect(Collectors.toList());

        return new SyncPullResponse(Instant.now(), customers, sites, notes);
    }

private boolean applyCustomer(Customer c, CustomerSyncDto d) {
        if (d.isDeleted()) {
            if (!c.isDeleted()) { c.setDeleted(true); c.setUpdatedAt(d.getUpdatedAt()); return true; }
            return false;
        }
        if (c.getUpdatedAt() == null || d.getUpdatedAt().isAfter(c.getUpdatedAt())) {
            c.setName(d.getName());
            c.setContactInfo(d.getContactInfo());
            c.setDeleted(false);
            c.setUpdatedAt(d.getUpdatedAt());
            return true;
        }
        return false;
    }

    private boolean applySite(Site s, SiteSyncDto d) {
        if (d.isDeleted()) {
            if (!s.isDeleted()) { s.setDeleted(true); s.setUpdatedAt(d.getUpdatedAt()); return true; }
            return false;
        }
        if (s.getUpdatedAt() == null || d.getUpdatedAt().isAfter(s.getUpdatedAt())) {
            s.setSiteName(d.getSiteName());
            s.setAddress(d.getAddress());
            s.setDeleted(false);
            s.setUpdatedAt(d.getUpdatedAt());
            return true;
        }
        return false;
    }

    private boolean applyNote(FieldNote n, FieldNoteSyncDto d) {
        if (d.isDeleted()) {
            if (!n.isDeleted()) { n.setDeleted(true); n.setUpdatedAt(d.getUpdatedAt()); return true; }
            return false;
        }
        if (n.getUpdatedAt() == null || d.getUpdatedAt().isAfter(n.getUpdatedAt())) {
            n.setTitle(d.getTitle());
            n.setDescription(d.getDescription());
            n.setLocation(d.getLocation());
            n.setDateTime(d.getDateTime());
            n.setStatus(d.getStatus());
            n.setPhotoUrl(d.getPhotoUrl());
            n.setDeleted(false);
            n.setUpdatedAt(d.getUpdatedAt());
            return true;
        }
        return false;
    }
}

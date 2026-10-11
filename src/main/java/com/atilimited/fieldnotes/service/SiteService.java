package com.atilimited.fieldnotes.service;

import com.atilimited.fieldnotes.dto.SiteRequest;
import com.atilimited.fieldnotes.dto.SiteResponse;
import com.atilimited.fieldnotes.exception.ResourceNotFoundException;
import com.atilimited.fieldnotes.model.Customer;
import com.atilimited.fieldnotes.model.Site;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.CustomerRepository;
import com.atilimited.fieldnotes.repository.SiteRepository;
import com.atilimited.fieldnotes.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SiteService {

    private final SiteRepository siteRepository;
    private final CustomerRepository customerRepository;
    private final CurrentUser currentUser;

    public SiteService(SiteRepository siteRepository,
                       CustomerRepository customerRepository,
                       CurrentUser currentUser) {
        this.siteRepository = siteRepository;
        this.customerRepository = customerRepository;
        this.currentUser = currentUser;
    }

    public List<SiteResponse> listAll() {
        return siteRepository.findAllByUser(currentUser.get()).stream()
                .map(SiteResponse::from)
                .collect(Collectors.toList());
    }

    public List<SiteResponse> listByCustomer(String customerUuid) {

return siteRepository.findAllByUser(currentUser.get()).stream()
                .filter(s -> s.getCustomer().getUuid().equals(customerUuid))
                .map(SiteResponse::from)
                .collect(Collectors.toList());
    }

    public SiteResponse create(SiteRequest req) {
        User user = currentUser.get();
        Customer customer = customerRepository.findByUserAndUuid(user, req.getCustomerUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", req.getCustomerUuid()));
        Site s = Site.builder()
                .customer(customer)
                .siteName(req.getSiteName())
                .address(req.getAddress())
                .build();
        return SiteResponse.from(siteRepository.save(s));
    }

    public SiteResponse get(String uuid) {
        Site s = siteRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Site", uuid));
        return SiteResponse.from(s);
    }

    public SiteResponse update(String uuid, SiteRequest req) {
        Site s = siteRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Site", uuid));
        Customer customer = customerRepository.findByUserAndUuid(currentUser.get(), req.getCustomerUuid())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", req.getCustomerUuid()));
        s.setCustomer(customer);
        s.setSiteName(req.getSiteName());
        s.setAddress(req.getAddress());
        return SiteResponse.from(siteRepository.save(s));
    }

    public void delete(String uuid) {
        Site s = siteRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Site", uuid));
        s.setDeleted(true);
        siteRepository.save(s);
    }
}

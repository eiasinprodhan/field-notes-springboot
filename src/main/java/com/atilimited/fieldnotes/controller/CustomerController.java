package com.atilimited.fieldnotes.controller;

import com.atilimited.fieldnotes.dto.CustomerRequest;
import com.atilimited.fieldnotes.dto.CustomerResponse;
import com.atilimited.fieldnotes.dto.SiteResponse;
import com.atilimited.fieldnotes.service.CustomerService;
import com.atilimited.fieldnotes.service.SiteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final SiteService siteService;

    public CustomerController(CustomerService customerService, SiteService siteService) {
        this.customerService = customerService;
        this.siteService = siteService;
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return customerService.list();
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest req) {
        return ResponseEntity.status(201).body(customerService.create(req));
    }

    @GetMapping("/{uuid}")
    public CustomerResponse get(@PathVariable String uuid) {
        return customerService.get(uuid);
    }

    @PutMapping("/{uuid}")
    public CustomerResponse update(@PathVariable String uuid, @Valid @RequestBody CustomerRequest req) {
        return customerService.update(uuid, req);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        customerService.delete(uuid);
        return ResponseEntity.noContent().build();
    }

@GetMapping("/{uuid}/sites")
    public List<SiteResponse> sites(@PathVariable String uuid) {
        return siteService.listByCustomer(uuid);
    }
}

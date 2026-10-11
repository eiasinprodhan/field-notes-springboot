package com.atilimited.fieldnotes.service;

import com.atilimited.fieldnotes.dto.CustomerRequest;
import com.atilimited.fieldnotes.dto.CustomerResponse;
import com.atilimited.fieldnotes.exception.ResourceNotFoundException;
import com.atilimited.fieldnotes.model.Customer;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.CustomerRepository;
import com.atilimited.fieldnotes.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CurrentUser currentUser;

    public CustomerService(CustomerRepository customerRepository, CurrentUser currentUser) {
        this.customerRepository = customerRepository;
        this.currentUser = currentUser;
    }

    public List<CustomerResponse> list() {
        User user = currentUser.get();
        return customerRepository.findAllByUser(user).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public CustomerResponse create(CustomerRequest req) {
        User user = currentUser.get();
        Customer c = Customer.builder()
                .user(user)
                .name(req.getName())
                .contactInfo(req.getContactInfo())
                .build();
        return CustomerResponse.from(customerRepository.save(c));
    }

    public CustomerResponse get(String uuid) {
        Customer c = customerRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", uuid));
        return CustomerResponse.from(c);
    }

    public CustomerResponse update(String uuid, CustomerRequest req) {
        Customer c = customerRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", uuid));
        c.setName(req.getName());
        c.setContactInfo(req.getContactInfo());
        return CustomerResponse.from(customerRepository.save(c));
    }

    public void delete(String uuid) {
        Customer c = customerRepository.findByUserAndUuid(currentUser.get(), uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", uuid));
        c.setDeleted(true); 
        customerRepository.save(c);
    }
}

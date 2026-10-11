package com.atilimited.fieldnotes.config;

import com.atilimited.fieldnotes.model.Customer;
import com.atilimited.fieldnotes.model.Site;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.CustomerRepository;
import com.atilimited.fieldnotes.repository.SiteRepository;
import com.atilimited.fieldnotes.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedDemoUser(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            SiteRepository siteRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            final String demoEmail = "worker@fieldnotes.com";
            if (!userRepository.existsByEmail(demoEmail)) {
                User demoUser = User.builder()
                        .email(demoEmail)
                        .name("Alex Morgan")
                        .passwordHash(passwordEncoder.encode("Password@123"))
                        .build();
                demoUser = userRepository.save(demoUser);

                Customer demoCustomer = Customer.builder()
                        .uuid(UUID.randomUUID().toString())
                        .user(demoUser)
                        .name("Apex Industrial Power")
                        .contactInfo("+1 (555) 234-5678 | dispatch@apexindustrial.com")
                        .build();
                demoCustomer = customerRepository.save(demoCustomer);

                Site demoSite = Site.builder()
                        .uuid(UUID.randomUUID().toString())
                        .customer(demoCustomer)
                        .siteName("West Valley Transformer Station #4")
                        .address("4900 Powerline Rd, Sector 12")
                        .build();
                siteRepository.save(demoSite);
            }
        };
    }
}

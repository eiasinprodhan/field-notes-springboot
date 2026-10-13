package com.atilimited.fieldnotes.service;

import com.atilimited.fieldnotes.dto.AuthResponse;
import com.atilimited.fieldnotes.dto.LoginRequest;
import com.atilimited.fieldnotes.dto.RegisterRequest;
import com.atilimited.fieldnotes.exception.InvalidCredentialsException;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.UserRepository;
import com.atilimited.fieldnotes.security.CurrentUser;
import com.atilimited.fieldnotes.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CurrentUser currentUser;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.currentUser = currentUser;
    }

    public AuthResponse register(RegisterRequest req) {
        String cleanEmail = req.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .email(cleanEmail)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .name(req.getName().trim())
                .build();
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String cleanEmail = req.getEmail().toLowerCase().trim();
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return toResponse(user);
    }

    public AuthResponse updateProfile(String newName, String newEmail, String currentPassword, String newPassword) {
        User user = currentUser.get();
        if (newName != null && !newName.trim().isEmpty()) {
            user.setName(newName.trim());
        }

        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String cleanEmail = newEmail.toLowerCase().trim();
            if (!cleanEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(cleanEmail)) {
                    throw new IllegalArgumentException("Email already in use by another account");
                }
                user.setEmail(cleanEmail);
            }
        }

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            if (newPassword.trim().length() < 6) {
                throw new IllegalArgumentException("New password must be at least 6 characters");
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
        }

        user = userRepository.save(user);
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), user.getEmail(), user.getName());
    }
}

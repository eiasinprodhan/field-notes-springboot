package com.atilimited.fieldnotes.service;

import com.atilimited.fieldnotes.dto.AuthResponse;
import com.atilimited.fieldnotes.dto.LoginRequest;
import com.atilimited.fieldnotes.dto.RegisterRequest;
import com.atilimited.fieldnotes.exception.InvalidCredentialsException;
import com.atilimited.fieldnotes.model.User;
import com.atilimited.fieldnotes.repository.UserRepository;
import com.atilimited.fieldnotes.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .email(req.getEmail().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build();
        user = userRepository.save(user);
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail().toLowerCase().trim(), req.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(InvalidCredentialsException::new);
        return toResponse(user);
    }

    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtUtil.generateToken(user.getEmail()), user.getEmail(), user.getName());
    }
}

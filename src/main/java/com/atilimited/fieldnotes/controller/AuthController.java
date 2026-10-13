package com.atilimited.fieldnotes.controller;

import com.atilimited.fieldnotes.dto.AuthResponse;
import com.atilimited.fieldnotes.dto.LoginRequest;
import com.atilimited.fieldnotes.dto.RegisterRequest;
import com.atilimited.fieldnotes.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(201).body(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody java.util.Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        return ResponseEntity.ok(authService.updateProfile(name, email, currentPassword, newPassword));
    }
}

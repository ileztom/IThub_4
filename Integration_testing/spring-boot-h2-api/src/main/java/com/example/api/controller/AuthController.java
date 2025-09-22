package com.example.api.controller;

import com.example.api.dto.AuthResponse;
import com.example.api.dto.LoginRequest;
import com.example.api.dto.RefreshRequest;
import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import com.example.api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;

    // store refresh tokens in-memory (demo)
    private static ConcurrentHashMap<String, Long> refreshStore = new ConcurrentHashMap<>();

    public AuthController(UserRepository userRepository, AuthService authService){
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req){
        Optional<User> u = userRepository.findByEmail(req.getEmail());
        if(u.isEmpty() || !u.get().getPassword().equals(req.getPassword())){
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        User user = u.get();
        String access = authService.generateAccessToken(user);
        String refresh = authService.generateRefreshToken(user);
        refreshStore.put(refresh, user.getId());
        return ResponseEntity.ok(new AuthResponse(access, refresh));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            String newToken = authService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(Map.of("accessToken", newToken));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(@RequestParam String refreshToken){
//        refreshStore.remove(refreshToken);
//        return ResponseEntity.ok("Logged out");
//    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshRequest request) {
        try {
            authService.logout(request.getRefreshToken());
            return ResponseEntity.ok(Map.of("status", "logged out"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

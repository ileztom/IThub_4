package com.example.userservice.user;

import com.example.common.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        User user = new User(request.username(), encoder.encode(request.password()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(u -> encoder.matches(request.password(), u.getPassword()))
                .map(u -> {
                    String token = JwtUtil.generateToken(u.getUsername());
                    return ResponseEntity.ok(Map.of("token", token, "userId", u.getId()));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }
}

record AuthRequest(String username, String password) {}

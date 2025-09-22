package com.example.api.service;

import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private Long jwtExpirationMs;

    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String generateAccessToken(User user){
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(exp)
                .claim("email", user.getEmail())
                .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                .compact();
    }

    // Обновление токена
    public String refreshToken(String refreshToken) {
        String email = refreshToken;
        if (email == null) {
            throw new RuntimeException("Invalid refresh token");
        }
        return generateToken(email);
    }

    // Logout
    public void logout(String token) {
        HashMap<Object, Object> refreshStorage = null;
        refreshStorage.values().removeIf(email -> token.equals(email));
    }

    private String generateToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 min
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // Naive refresh token generator (for demo)
    public String generateRefreshToken(User user){
        return UUID.randomUUID().toString();
    }
}
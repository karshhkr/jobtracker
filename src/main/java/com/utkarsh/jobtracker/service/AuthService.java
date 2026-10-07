package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.AuthRequest;
import com.utkarsh.jobtracker.dto.AuthResponse;
import com.utkarsh.jobtracker.entity.User;
import com.utkarsh.jobtracker.repository.UserRepository;
import com.utkarsh.jobtracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse signup(AuthRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userRepo.save(user);

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token);
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponse(token);
    }
}
package com.utkarsh.jobtracker.service;

import com.utkarsh.jobtracker.dto.AuthRequest;
import com.utkarsh.jobtracker.dto.AuthResponse;
import com.utkarsh.jobtracker.entity.Subscription;
import com.utkarsh.jobtracker.entity.User;
import com.utkarsh.jobtracker.repository.SubscriptionRepository;
import com.utkarsh.jobtracker.repository.UserRepository;
import com.utkarsh.jobtracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(AuthRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        userRepository.save(user);

        Subscription sub = new Subscription();
        sub.setUser(user);
        subscriptionRepository.save(sub);

        return new AuthResponse(jwtUtil.generateToken(user.getId()), user.getName(), email);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest req) {
        String email = req.email().trim().toLowerCase();
        // Same error chahe email galat ho ya password: attacker ko user enumeration ka hint nahi milta
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        return new AuthResponse(jwtUtil.generateToken(user.getId()), user.getName(), user.getEmail());
    }
}
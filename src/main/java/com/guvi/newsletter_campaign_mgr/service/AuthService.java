package com.guvi.newsletter_campaign_mgr.service;

import com.guvi.newsletter_campaign_mgr.dto.AuthResponse;
import com.guvi.newsletter_campaign_mgr.dto.LoginRequest;
import com.guvi.newsletter_campaign_mgr.dto.RegisterRequest;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.model.User;
import com.guvi.newsletter_campaign_mgr.repo.UserRepository;
import com.guvi.newsletter_campaign_mgr.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check for duplicate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Encode password and save user
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        userRepository.save(user);

        // Generate and return token immediately after registration
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername());
    }

    public AuthResponse login(LoginRequest request) {
        // Delegates to DaoAuthenticationProvider which checks username + BCrypt password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // If authentication passed, generate token
        String token = jwtUtil.generateToken(request.getUsername());
        return new AuthResponse(token, request.getUsername());
    }
}

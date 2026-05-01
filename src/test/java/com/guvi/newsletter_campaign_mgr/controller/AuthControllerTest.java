package com.guvi.newsletter_campaign_mgr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.newsletter_campaign_mgr.dto.AuthResponse;
import com.guvi.newsletter_campaign_mgr.dto.LoginRequest;
import com.guvi.newsletter_campaign_mgr.dto.RegisterRequest;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.security.JwtUtil;
import com.guvi.newsletter_campaign_mgr.security.UserDetailsServiceImpl;
import com.guvi.newsletter_campaign_mgr.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    private ObjectMapper objectMapper;  // ← no @Autowired

    @MockitoBean
    private AuthService authService;
    @MockitoBean private JwtUtil jwtUtil;
    @MockitoBean private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void register_WhenValidRequest_Returns200WithTokenAndUsername() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(authService.register(any())).thenReturn(new AuthResponse("jwt-token", "newuser"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    void login_WhenValidCredentials_Returns200WithTokenAndUsername() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(authService.login(any())).thenReturn(new AuthResponse("jwt-token", "testuser"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_WhenDuplicateUsername_Returns409() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existing");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(authService.register(any()))
                .thenThrow(new DuplicateResourceException("Username already taken"));

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_WhenMissingFields_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest(); // empty - validation fails

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenMissingFields_Returns400() throws Exception {
        LoginRequest request = new LoginRequest(); // empty - validation fails

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
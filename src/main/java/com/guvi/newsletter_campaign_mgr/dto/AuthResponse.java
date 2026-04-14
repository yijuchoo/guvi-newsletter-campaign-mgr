package com.guvi.newsletter_campaign_mgr.dto;

public class AuthResponse {

    private String token;
    private String username;

    // Constructor
    public AuthResponse() {}

    public AuthResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    // Getters & Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package com.guvi.newsletter_campaign_mgr.dto;

import java.time.LocalDateTime;

public class SubscriberResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime subscribedAt;

    public SubscriberResponse() {
    }

    public SubscriberResponse(Long id, String name, String email, LocalDateTime subscribedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subscribedAt = subscribedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSubscribedAt(LocalDateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }
}

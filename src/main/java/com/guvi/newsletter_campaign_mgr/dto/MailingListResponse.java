package com.guvi.newsletter_campaign_mgr.dto;

import java.time.LocalDateTime;

public class MailingListResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    // Includes subscriberCount instead of full subscriber list
    private int subscriberCount;

    public MailingListResponse() {
    }

    public MailingListResponse(Long id, String name, LocalDateTime createdAt, int subscriberCount) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.subscriberCount = subscriberCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

}

package com.guvi.newsletter_campaign_mgr.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class ScheduleCampaignRequest {
    @NotNull(message = "Scheduled time cannot be null")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledAt;

    public ScheduleCampaignRequest() {
    }

    public ScheduleCampaignRequest(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}

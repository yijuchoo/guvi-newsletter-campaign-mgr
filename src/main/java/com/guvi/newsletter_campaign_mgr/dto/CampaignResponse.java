package com.guvi.newsletter_campaign_mgr.dto;

import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;

import java.time.LocalDateTime;

public class CampaignResponse {
    private Long id;
    private String name;
    private String subject;
    private String content;
    private CampaignStatus status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Flattened mailing list info — no MailingList entity exposed
    private Long mailingListId;
    private String mailingListName;

    public CampaignResponse() {
    }

    public CampaignResponse(Long id, String name, String subject, String content,
                            CampaignStatus status, LocalDateTime scheduledAt,
                            LocalDateTime sentAt, LocalDateTime createdAt,
                            LocalDateTime updatedAt, Long mailingListId,
                            String mailingListName) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.mailingListId = mailingListId;
        this.mailingListName = mailingListName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getMailingListId() {
        return mailingListId;
    }

    public String getMailingListName() {
        return mailingListName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setMailingListId(Long mailingListId) {
        this.mailingListId = mailingListId;
    }

    public void setMailingListName(String mailingListName) {
        this.mailingListName = mailingListName;
    }
}

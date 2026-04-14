package com.guvi.newsletter_campaign_mgr.model;

import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String subject;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;   // null if not yet scheduled

    @Column(name = "sent_at")
    private LocalDateTime sentAt;        // populated when status flips to SENT

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mailing_list_id", nullable = false)
    private MailingList mailingList;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Campaign() {
    }

    public Campaign(Long id, String name, String subject, String content, CampaignStatus status,
                    LocalDateTime scheduledAt, User user, MailingList mailingList) {
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.user = user;
        this.mailingList = mailingList;
    }

    // Used when creating a new campaign in CampaignService
    // status is hardcoded to DRAFT inside the constructor itself — the caller doesn't need to think about it.
    // A brand-new campaign is always a draft. scheduledAt and sentAt start as null and only get set later when the
    // user explicitly schedules or the scheduler fires.
    public Campaign(String name, String subject, String content,
                    User user, MailingList mailingList) {
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.user = user;
        this.mailingList = mailingList;
        this.status = CampaignStatus.DRAFT;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public void setStatus(CampaignStatus status) {
        this.status = status;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }
}

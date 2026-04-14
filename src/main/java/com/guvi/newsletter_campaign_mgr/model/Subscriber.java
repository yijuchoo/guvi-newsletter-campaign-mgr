package com.guvi.newsletter_campaign_mgr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscribers",
        uniqueConstraints = {
                // Prevents duplicate emails within the same mailing list
                @UniqueConstraint(columnNames = {"email", "mailing_list_id"})
        }
)
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @Column(name = "subscribed_at", nullable = false, updatable = false)
    private LocalDateTime subscribedAt;

    // Subscriber belongs to ONE MailingList. The relationship is stored in a mailing_list_id column.
    // The MailingList data will only be loaded when needed, and it cannot be null.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mailing_list_id", nullable = false)
    private MailingList mailingList;

    // Whenever a new Subscriber is saved, automatically record the current time as subscribedAt before inserting
    // into the database.
    @PrePersist
    protected void onCreate() {
        this.subscribedAt = LocalDateTime.now();
    }

    // Constructor
    public Subscriber() {
    }

    public Subscriber(Long id, String name, String email, MailingList mailingList) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mailingList = mailingList;
    }

    // Used when adding a subscriber to a list in SubscriberService
    // subscribedAt is excluded because @PrePersist sets it automatically on save.
    public Subscriber(String name, String email, MailingList mailingList) {
        this.name = name;
        this.email = email;
        this.mailingList = mailingList;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(LocalDateTime subscribedAt) {
        this.subscribedAt = subscribedAt;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }
}

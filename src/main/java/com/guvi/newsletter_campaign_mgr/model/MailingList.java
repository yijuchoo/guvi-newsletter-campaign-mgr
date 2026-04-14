package com.guvi.newsletter_campaign_mgr.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mailing_lists")
public class MailingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // LAZY prevents unnecessary data loading
    // Loads id, name, createdAt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // cascade.all -> PERSIST (save), MERGE (update), REMOVE (delete),
    // REFRESH, DETACH
    // Whatever I do to MailingList, do the same to its subscribers
    @OneToMany(mappedBy = "mailingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscriber> subscribers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor
    public MailingList() {
    }

    public MailingList(Long id, String name, User user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }
    // Used when a logged-in user creates a new list in MailingListService
    // createdAt is also not passed because @PrePersist sets it automatically on save.
    public MailingList(String name, User user) {
        this.name = name;
        this.user = user;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }
}

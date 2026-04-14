package com.guvi.newsletter_campaign_mgr.service;

import com.guvi.newsletter_campaign_mgr.dto.SubscriberRequest;
import com.guvi.newsletter_campaign_mgr.dto.SubscriberResponse;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.Subscriber;
import com.guvi.newsletter_campaign_mgr.model.User;
import com.guvi.newsletter_campaign_mgr.repo.SubscriberRepository;
import com.guvi.newsletter_campaign_mgr.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final MailingListService mailingListService;
    private final UserRepository userRepository;

    public SubscriberService(SubscriberRepository subscriberRepository,
                             MailingListService mailingListService,
                             UserRepository userRepository) {
        this.subscriberRepository = subscriberRepository;
        this.mailingListService = mailingListService;
        this.userRepository = userRepository;
    }

    // ── Helper: get authenticated user from SecurityContext ───────────────
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    // ── Helper: map entity to response DTO ───────────────────────────────
    private SubscriberResponse toResponse(Subscriber subscriber) {
        return new SubscriberResponse(
                subscriber.getId(),
                subscriber.getName(),
                subscriber.getEmail(),
                subscriber.getSubscribedAt()
        );
    }

    // ── Add subscriber to a mailing list ─────────────────────────────────
    public SubscriberResponse addSubscriber(Long mailingListId, SubscriberRequest request) {
        User currentUser = getCurrentUser();

        // Verify mailing list belongs to current user
        MailingList mailingList = mailingListService
                .getMailingListEntityById(mailingListId, currentUser.getId());

        // Check for duplicate email in this list
        if (subscriberRepository.existsByEmailAndMailingListId(
                request.getEmail(), mailingListId)) {
            throw new DuplicateResourceException("Email already subscribed to this list");
        }

        Subscriber subscriber = new Subscriber(
                request.getName(),
                request.getEmail(),
                mailingList
        );
        return toResponse(subscriberRepository.save(subscriber));
    }

    // ── Get all subscribers in a mailing list ─────────────────────────────
    public List<SubscriberResponse> getSubscribers(Long mailingListId) {
        User currentUser = getCurrentUser();

        // Verify ownership before returning subscribers
        mailingListService.getMailingListEntityById(mailingListId, currentUser.getId());

        return subscriberRepository.findByMailingListId(mailingListId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Remove subscriber from a mailing list ─────────────────────────────
    public void removeSubscriber(Long mailingListId, Long subscriberId) {
        User currentUser = getCurrentUser();

        // Verify ownership of the mailing list first
        mailingListService.getMailingListEntityById(mailingListId, currentUser.getId());

        Subscriber subscriber = subscriberRepository
                .findByIdAndMailingListId(subscriberId, mailingListId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscriber not found in this list"));

        subscriberRepository.delete(subscriber);
    }
}

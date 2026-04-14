package com.guvi.newsletter_campaign_mgr.service;

import com.guvi.newsletter_campaign_mgr.dto.MailingListRequest;
import com.guvi.newsletter_campaign_mgr.dto.MailingListResponse;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.User;
import com.guvi.newsletter_campaign_mgr.repo.MailingListRepository;
import com.guvi.newsletter_campaign_mgr.repo.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MailingListService {

    private final MailingListRepository mailingListRepository;
    private final UserRepository userRepository;

    public MailingListService(MailingListRepository mailingListRepository,
                              UserRepository userRepository) {
        this.mailingListRepository = mailingListRepository;
        this.userRepository = userRepository;
    }

    // ── Helper: get authenticated user from SecurityContext
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    // ── Helper: map entity to response DTO
    private MailingListResponse toResponse(MailingList mailingList) {
        return new MailingListResponse(
                mailingList.getId(),
                mailingList.getName(),
                mailingList.getCreatedAt(),
                mailingList.getSubscribers().size()
        );
    }

    // ── Create mailing list
    public MailingListResponse createMailingList(MailingListRequest request) {
        User currentUser = getCurrentUser();

        if (mailingListRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new DuplicateResourceException("Mailing list with this name already exists");
        }

        MailingList mailingList = new MailingList(request.getName(), currentUser);
        return toResponse(mailingListRepository.save(mailingList));
    }

    // ── Get all mailing lists for current user
    public List<MailingListResponse> getAllMailingLists() {
        User currentUser = getCurrentUser();
        return mailingListRepository.findByUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Get single mailing list by id
    public MailingListResponse getMailingListById(Long id) {
        User currentUser = getCurrentUser();
        MailingList mailingList = mailingListRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));
        return toResponse(mailingList);
    }

    // ── Rename mailing list
    public MailingListResponse updateMailingList(Long id, MailingListRequest request) {
        User currentUser = getCurrentUser();
        MailingList mailingList = mailingListRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));

        if (mailingListRepository.existsByNameAndUserId(request.getName(), currentUser.getId())) {
            throw new DuplicateResourceException("Mailing list with this name already exists");
        }

        mailingList.setName(request.getName());
        return toResponse(mailingListRepository.save(mailingList));
    }

    // ── Delete mailing list
    public void deleteMailingList(Long id) {
        User currentUser = getCurrentUser();
        MailingList mailingList = mailingListRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));
        mailingListRepository.delete(mailingList);
    }

    // ── Used by SubscriberService and CampaignService
    public MailingList getMailingListEntityById(Long id, Long userId) {
        return mailingListRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mailing list not found"));
    }
}

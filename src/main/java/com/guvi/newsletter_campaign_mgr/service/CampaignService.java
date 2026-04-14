package com.guvi.newsletter_campaign_mgr.service;

import com.guvi.newsletter_campaign_mgr.dto.CampaignResponse;
import com.guvi.newsletter_campaign_mgr.dto.CreateCampaignRequest;
import com.guvi.newsletter_campaign_mgr.dto.ScheduleCampaignRequest;
import com.guvi.newsletter_campaign_mgr.dto.UpdateCampaignRequest;
import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.exception.BadRequestException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.model.Campaign;
import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.User;
import com.guvi.newsletter_campaign_mgr.repo.CampaignRepository;
import com.guvi.newsletter_campaign_mgr.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final MailingListService mailingListService;
    private final UserRepository userRepository;

    public CampaignService(CampaignRepository campaignRepository,
                           MailingListService mailingListService,
                           UserRepository userRepository) {
        this.campaignRepository = campaignRepository;
        this.mailingListService = mailingListService;
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
    private CampaignResponse toResponse(Campaign campaign) {
        return new CampaignResponse(
                campaign.getId(),
                campaign.getName(),
                campaign.getSubject(),
                campaign.getContent(),
                campaign.getStatus(),
                campaign.getScheduledAt(),
                campaign.getSentAt(),
                campaign.getCreatedAt(),
                campaign.getUpdatedAt(),
                campaign.getMailingList().getId(),
                campaign.getMailingList().getName()
        );
    }

    // ── Create campaign
    public CampaignResponse createCampaign(CreateCampaignRequest request) {
        User currentUser = getCurrentUser();

        MailingList mailingList = mailingListService
                .getMailingListEntityById(request.getMailingListId(), currentUser.getId());

        Campaign campaign = new Campaign(
                request.getName(),
                request.getSubject(),
                request.getContent(),
                currentUser,
                mailingList
        );
        return toResponse(campaignRepository.save(campaign));
    }

    // ── Get all campaigns with pagination, optional status filter ─────────
    public Page<CampaignResponse> getCampaigns(int page, int size, CampaignStatus status) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (status != null) {
            return campaignRepository
                    .findByUserIdAndStatus(currentUser.getId(), status, pageable)
                    .map(this::toResponse);
        }
        return campaignRepository
                .findByUserId(currentUser.getId(), pageable)
                .map(this::toResponse);
    }

    // ── Get single campaign by id
    public CampaignResponse getCampaignById(Long id) {
        User currentUser = getCurrentUser();
        Campaign campaign = campaignRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));
        return toResponse(campaign);
    }

    // ── Update campaign (DRAFT only)
    public CampaignResponse updateCampaign(Long id, UpdateCampaignRequest request) {
        User currentUser = getCurrentUser();
        Campaign campaign = campaignRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        // Only DRAFT campaigns can be edited
        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT campaigns can be edited");
        }

        // Apply only non-null fields
        if (request.getName() != null) campaign.setName(request.getName());
        if (request.getSubject() != null) campaign.setSubject(request.getSubject());
        if (request.getContent() != null) campaign.setContent(request.getContent());

        if (request.getMailingListId() != null) {
            MailingList mailingList = mailingListService
                    .getMailingListEntityById(request.getMailingListId(), currentUser.getId());
            campaign.setMailingList(mailingList);
        }

        return toResponse(campaignRepository.save(campaign));
    }

    // ── Schedule campaign
    public CampaignResponse scheduleCampaign(Long id, ScheduleCampaignRequest request) {
        User currentUser = getCurrentUser();
        Campaign campaign = campaignRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        // Only DRAFT campaigns can be scheduled
        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT campaigns can be scheduled");
        }

        campaign.setScheduledAt(request.getScheduledAt());
        campaign.setStatus(CampaignStatus.SCHEDULED);
        return toResponse(campaignRepository.save(campaign));
    }

    // ── Delete campaign (DRAFT only)
    public void deleteCampaign(Long id) {
        User currentUser = getCurrentUser();
        Campaign campaign = campaignRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT campaigns can be deleted");
        }

        campaignRepository.delete(campaign);
    }

    // ── Reschedule a SCHEDULED campaign
    public CampaignResponse rescheduleCampaign(Long id, ScheduleCampaignRequest request) {
        User currentUser = getCurrentUser();
        Campaign campaign = campaignRepository
                .findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

        // Only SCHEDULED campaigns can be rescheduled
        if (campaign.getStatus() != CampaignStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED campaigns can be rescheduled");
        }

        campaign.setScheduledAt(request.getScheduledAt());
        return toResponse(campaignRepository.save(campaign));
    }
}

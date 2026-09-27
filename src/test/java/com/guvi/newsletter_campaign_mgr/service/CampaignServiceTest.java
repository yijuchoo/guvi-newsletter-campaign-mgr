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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock private CampaignRepository campaignRepository;
    @Mock private MailingListService mailingListService;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private CampaignService campaignService;

    private User user;
    private MailingList mailingList;
    private Campaign draftCampaign;
    private Campaign scheduledCampaign;
    private Campaign sentCampaign;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        mailingList = new MailingList();
        mailingList.setId(1L);
        mailingList.setName("My List");
        mailingList.setUser(user);

        draftCampaign = new Campaign();
        draftCampaign.setId(1L);
        draftCampaign.setName("Draft Campaign");
        draftCampaign.setSubject("Subject");
        draftCampaign.setContent("Content");
        draftCampaign.setStatus(CampaignStatus.DRAFT);
        draftCampaign.setUser(user);
        draftCampaign.setMailingList(mailingList);

        scheduledCampaign = new Campaign();
        scheduledCampaign.setId(2L);
        scheduledCampaign.setName("Scheduled Campaign");
        scheduledCampaign.setSubject("Subject");
        scheduledCampaign.setContent("Content");
        scheduledCampaign.setStatus(CampaignStatus.SCHEDULED);
        scheduledCampaign.setScheduledAt(LocalDateTime.now().plusHours(1));
        scheduledCampaign.setUser(user);
        scheduledCampaign.setMailingList(mailingList);

        sentCampaign = new Campaign();
        sentCampaign.setId(3L);
        sentCampaign.setName("Sent Campaign");
        sentCampaign.setStatus(CampaignStatus.SENT);
        sentCampaign.setUser(user);
        sentCampaign.setMailingList(mailingList);

        // Mock SecurityContextHolder
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void createCampaign_WhenValid_ReturnsDraftCampaign() {
        CreateCampaignRequest request = new CreateCampaignRequest();
        request.setName("New Campaign");
        request.setSubject("Subject");
        request.setContent("Content");
        request.setMailingListId(1L);

        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(campaignRepository.save(any())).thenReturn(draftCampaign);

        CampaignResponse response = campaignService.createCampaign(request);

        assertThat(response.getStatus()).isEqualTo(CampaignStatus.DRAFT);
        verify(campaignRepository).save(any(Campaign.class));
    }

    @Test
    void getCampaigns_WithNoFilter_ReturnsAllCampaigns() {
        Page<Campaign> page = new PageImpl<>(List.of(draftCampaign));
        when(campaignRepository.findByUserId(eq(1L), any())).thenReturn(page);

        Page<CampaignResponse> result = campaignService.getCampaigns(0, 10, null);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getCampaigns_WithStatusFilter_ReturnsFilteredCampaigns() {
        Page<Campaign> page = new PageImpl<>(List.of(draftCampaign));
        when(campaignRepository.findByUserIdAndStatus(eq(1L), eq(CampaignStatus.DRAFT), any()))
                .thenReturn(page);

        Page<CampaignResponse> result = campaignService.getCampaigns(0, 10, CampaignStatus.DRAFT);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    void getCampaignById_WhenExists_ReturnsResponse() {
        when(campaignRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(draftCampaign));

        CampaignResponse response = campaignService.getCampaignById(1L);

        assertThat(response.getName()).isEqualTo("Draft Campaign");
    }

    @Test
    void getCampaignById_WhenNotFound_ThrowsResourceNotFoundException() {
        when(campaignRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> campaignService.getCampaignById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCampaign_WhenDraft_UpdatesSuccessfully() {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setName("Updated Name");

        when(campaignRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(draftCampaign));
        when(campaignRepository.save(any())).thenReturn(draftCampaign);

        CampaignResponse response = campaignService.updateCampaign(1L, request);

        assertThat(response).isNotNull();
        verify(campaignRepository).save(any(Campaign.class));
    }

    @Test
    void updateCampaign_WhenNotDraft_ThrowsBadRequestException() {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setName("Updated");

        when(campaignRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(scheduledCampaign));

        assertThatThrownBy(() -> campaignService.updateCampaign(2L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only DRAFT campaigns can be edited");
    }

    @Test
    void scheduleCampaign_WhenDraft_SetsStatusToScheduled() {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusHours(2));

        when(campaignRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(draftCampaign));
        when(campaignRepository.save(any())).thenReturn(draftCampaign);

        CampaignResponse response = campaignService.scheduleCampaign(1L, request);

        assertThat(response).isNotNull();
        verify(campaignRepository).save(argThat(c ->
                c.getStatus() == CampaignStatus.SCHEDULED
        ));
    }

    @Test
    void scheduleCampaign_WhenAlreadyScheduled_ThrowsBadRequestException() {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusHours(1));

        when(campaignRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(scheduledCampaign));

        assertThatThrownBy(() -> campaignService.scheduleCampaign(2L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only DRAFT campaigns can be scheduled");
    }

    @Test
    void scheduleCampaign_WhenSent_ThrowsBadRequestException() {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusHours(1));

        when(campaignRepository.findByIdAndUserId(3L, 1L)).thenReturn(Optional.of(sentCampaign));

        assertThatThrownBy(() -> campaignService.scheduleCampaign(3L, request))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void deleteCampaign_WhenDraft_DeletesSuccessfully() {
        when(campaignRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(draftCampaign));

        campaignService.deleteCampaign(1L);

        verify(campaignRepository).delete(draftCampaign);
    }

    @Test
    void deleteCampaign_WhenNotDraft_ThrowsBadRequestException() {
        when(campaignRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(scheduledCampaign));

        assertThatThrownBy(() -> campaignService.deleteCampaign(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only DRAFT campaigns can be deleted");
    }

    @Test
    void deleteCampaign_WhenNotFound_ThrowsResourceNotFoundException() {
        when(campaignRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> campaignService.deleteCampaign(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void rescheduleCampaign_WhenScheduled_UpdatesScheduledAt() {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusDays(1));

        when(campaignRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(scheduledCampaign));
        when(campaignRepository.save(any())).thenReturn(scheduledCampaign);

        CampaignResponse response = campaignService.rescheduleCampaign(2L, request);

        assertThat(response).isNotNull();
        verify(campaignRepository).save(any(Campaign.class));
    }

    @Test
    void rescheduleCampaign_WhenNotScheduled_ThrowsBadRequestException() {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusDays(1));

        when(campaignRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(draftCampaign));

        assertThatThrownBy(() -> campaignService.rescheduleCampaign(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Only SCHEDULED campaigns can be rescheduled");
    }
}
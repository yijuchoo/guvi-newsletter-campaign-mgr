package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.model.Campaign;
import com.guvi.newsletter_campaign_mgr.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignRepositoryTest {

    @Mock
    private CampaignRepository campaignRepository;

    private User user;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("Test Campaign");
        campaign.setStatus(CampaignStatus.DRAFT);
        campaign.setUser(user);
    }

    @Test
    void findByUserId_WhenCampaignsExist_ReturnsPage() {
        Page<Campaign> page = new PageImpl<>(List.of(campaign));
        when(campaignRepository.findByUserId(eq(1L), any())).thenReturn(page);

        Page<Campaign> result = campaignRepository.findByUserId(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Campaign");
    }

    @Test
    void findByUserId_WhenNoCampaigns_ReturnsEmptyPage() {
        when(campaignRepository.findByUserId(eq(1L), any())).thenReturn(Page.empty());

        Page<Campaign> result = campaignRepository.findByUserId(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByUserIdAndStatus_WhenMatch_ReturnsFilteredPage() {
        Page<Campaign> page = new PageImpl<>(List.of(campaign));
        when(campaignRepository.findByUserIdAndStatus(eq(1L), eq(CampaignStatus.DRAFT), any()))
                .thenReturn(page);

        Page<Campaign> result = campaignRepository.findByUserIdAndStatus(
                1L, CampaignStatus.DRAFT, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }

    @Test
    void findByUserIdAndStatus_WhenNoMatch_ReturnsEmptyPage() {
        when(campaignRepository.findByUserIdAndStatus(eq(1L), eq(CampaignStatus.SENT), any()))
                .thenReturn(Page.empty());

        Page<Campaign> result = campaignRepository.findByUserIdAndStatus(
                1L, CampaignStatus.SENT, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findByIdAndUserId_WhenExists_ReturnsCampaign() {
        when(campaignRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(campaign));

        Optional<Campaign> result = campaignRepository.findByIdAndUserId(1L, 1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Campaign");
    }

    @Test
    void findByIdAndUserId_WhenWrongUser_ReturnsEmpty() {
        when(campaignRepository.findByIdAndUserId(1L, 99L)).thenReturn(Optional.empty());

        assertThat(campaignRepository.findByIdAndUserId(1L, 99L)).isEmpty();
    }

    @Test
    void findByIdAndUserId_WhenIdNotFound_ReturnsEmpty() {
        when(campaignRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThat(campaignRepository.findByIdAndUserId(99L, 1L)).isEmpty();
    }

    @Test
    void findByStatusAndScheduledAtBefore_WhenDueCampaigns_ReturnsList() {
        campaign.setStatus(CampaignStatus.SCHEDULED);
        campaign.setScheduledAt(LocalDateTime.now().minusMinutes(5));

        when(campaignRepository.findByStatusAndScheduledAtBefore(
                eq(CampaignStatus.SCHEDULED), any()))
                .thenReturn(List.of(campaign));

        List<Campaign> result = campaignRepository.findByStatusAndScheduledAtBefore(
                CampaignStatus.SCHEDULED, LocalDateTime.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(CampaignStatus.SCHEDULED);
    }

    @Test
    void findByStatusAndScheduledAtBefore_WhenNoneDue_ReturnsEmpty() {
        when(campaignRepository.findByStatusAndScheduledAtBefore(any(), any()))
                .thenReturn(List.of());

        List<Campaign> result = campaignRepository.findByStatusAndScheduledAtBefore(
                CampaignStatus.SCHEDULED, LocalDateTime.now());

        assertThat(result).isEmpty();
    }
}
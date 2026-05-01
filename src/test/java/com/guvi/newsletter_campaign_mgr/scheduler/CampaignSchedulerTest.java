package com.guvi.newsletter_campaign_mgr.scheduler;

import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.model.Campaign;
import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.Subscriber;
import com.guvi.newsletter_campaign_mgr.repo.CampaignRepository;
import com.guvi.newsletter_campaign_mgr.repo.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignSchedulerTest {

    @Mock private CampaignRepository campaignRepository;
    @Mock private SubscriberRepository subscriberRepository;

    @InjectMocks
    private CampaignScheduler campaignScheduler;

    private MailingList mailingList;
    private Campaign campaign;
    private Subscriber subscriber1;
    private Subscriber subscriber2;

    @BeforeEach
    void setUp() {
        mailingList = new MailingList();
        mailingList.setId(1L);
        mailingList.setName("Test List");

        campaign = new Campaign();
        campaign.setId(1L);
        campaign.setName("Test Campaign");
        campaign.setSubject("Test Subject");
        campaign.setContent("Test Content");
        campaign.setStatus(CampaignStatus.SCHEDULED);
        campaign.setScheduledAt(LocalDateTime.now().minusMinutes(5));
        campaign.setMailingList(mailingList);

        subscriber1 = new Subscriber();
        subscriber1.setId(1L);
        subscriber1.setName("Alice");
        subscriber1.setEmail("alice@example.com");
        subscriber1.setMailingList(mailingList);

        subscriber2 = new Subscriber();
        subscriber2.setId(2L);
        subscriber2.setName("Bob");
        subscriber2.setEmail("bob@example.com");
        subscriber2.setMailingList(mailingList);
    }

    @Test
    void processDueCampaigns_WhenDueCampaignsExist_UpdatesStatusToSent() {
        when(campaignRepository.findByStatusAndScheduledAtBefore(
                eq(CampaignStatus.SCHEDULED), any()))
                .thenReturn(List.of(campaign));
        when(subscriberRepository.findByMailingListId(1L))
                .thenReturn(List.of(subscriber1, subscriber2));

        campaignScheduler.processDueCampaigns();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.SENT);
        assertThat(campaign.getSentAt()).isNotNull();
        verify(campaignRepository).save(campaign);
    }

    @Test
    void processDueCampaigns_WhenNoCampaignsDue_DoesNotSaveAnything() {
        when(campaignRepository.findByStatusAndScheduledAtBefore(any(), any()))
                .thenReturn(List.of());

        campaignScheduler.processDueCampaigns();

        verify(campaignRepository, never()).save(any());
        verify(subscriberRepository, never()).findByMailingListId(any());
    }

    @Test
    void processDueCampaigns_WhenNoSubscribers_StillMarksCampaignAsSent() {
        when(campaignRepository.findByStatusAndScheduledAtBefore(
                eq(CampaignStatus.SCHEDULED), any()))
                .thenReturn(List.of(campaign));
        when(subscriberRepository.findByMailingListId(1L)).thenReturn(List.of());

        campaignScheduler.processDueCampaigns();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.SENT);
        verify(campaignRepository).save(campaign);
    }

    @Test
    void processDueCampaigns_WhenMultipleCampaignsDue_ProcessesAll() {
        Campaign campaign2 = new Campaign();
        campaign2.setId(2L);
        campaign2.setName("Second Campaign");
        campaign2.setSubject("Subject 2");
        campaign2.setContent("Content 2");
        campaign2.setStatus(CampaignStatus.SCHEDULED);
        campaign2.setScheduledAt(LocalDateTime.now().minusMinutes(10));
        campaign2.setMailingList(mailingList);

        when(campaignRepository.findByStatusAndScheduledAtBefore(
                eq(CampaignStatus.SCHEDULED), any()))
                .thenReturn(List.of(campaign, campaign2));
        when(subscriberRepository.findByMailingListId(1L))
                .thenReturn(List.of(subscriber1));

        campaignScheduler.processDueCampaigns();

        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.SENT);
        assertThat(campaign2.getStatus()).isEqualTo(CampaignStatus.SENT);
        verify(campaignRepository, times(2)).save(any(Campaign.class));
    }
}
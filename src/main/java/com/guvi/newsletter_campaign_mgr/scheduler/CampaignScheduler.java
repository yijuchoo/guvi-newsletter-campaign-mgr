package com.guvi.newsletter_campaign_mgr.scheduler;

import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.model.Campaign;
import com.guvi.newsletter_campaign_mgr.model.Subscriber;
import com.guvi.newsletter_campaign_mgr.repo.CampaignRepository;
import com.guvi.newsletter_campaign_mgr.repo.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CampaignScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CampaignScheduler.class);

    private final CampaignRepository campaignRepository;
    private final SubscriberRepository subscriberRepository;

    public CampaignScheduler(CampaignRepository campaignRepository,
                             SubscriberRepository subscriberRepository) {
        this.campaignRepository = campaignRepository;
        this.subscriberRepository = subscriberRepository;
    }

    // Runs every minute — checks for SCHEDULED campaigns that are due
    @Transactional // JPA session stays open for the entire method
    @Scheduled(fixedRate = 60000)
    public void processDueCampaigns() {
        LocalDateTime now = LocalDateTime.now();

        // Fetch all SCHEDULED campaigns whose scheduledAt is now or in the past
        List<Campaign> dueCampaigns = campaignRepository
                .findByStatusAndScheduledAtBefore(CampaignStatus.SCHEDULED, now);

        if (dueCampaigns.isEmpty()) {
            logger.debug("No due campaigns found at {}", now);
            return;
        }

        for (Campaign campaign : dueCampaigns) {
            processCampaign(campaign);
        }
    }

    private void processCampaign(Campaign campaign) {
        logger.info("========================================");
        logger.info("Processing campaign: [{}] - '{}'", campaign.getId(), campaign.getName());
        logger.info("Subject   : {}", campaign.getSubject());
        logger.info("Mailing List: {} (id: {})",
                campaign.getMailingList().getName(),
                campaign.getMailingList().getId());
        logger.info("Scheduled at: {}", campaign.getScheduledAt());
        logger.info("========================================");

        // Fetch all subscribers for this campaign's mailing list
        List<Subscriber> subscribers = subscriberRepository
                .findByMailingListId(campaign.getMailingList().getId());

        if (subscribers.isEmpty()) {
            logger.warn("Campaign [{}] - '{}' has no subscribers. Marking as SENT anyway.",
                    campaign.getId(), campaign.getName());
        }

        // Simulate sending email to each subscriber
        for (Subscriber subscriber : subscribers) {
            simulateSendEmail(campaign, subscriber);
        }

        // Update campaign status to SENT
        campaign.setStatus(CampaignStatus.SENT);
        campaign.setSentAt(LocalDateTime.now());
        campaignRepository.save(campaign);

        logger.info("Campaign [{}] - '{}' marked as SENT. Total sent: {}",
                campaign.getId(), campaign.getName(), subscribers.size());
        logger.info("========================================");
    }

    private void simulateSendEmail(Campaign campaign, Subscriber subscriber) {
        logger.info("  >> Sending email to: {} <{}>", subscriber.getName(), subscriber.getEmail());
        logger.info("     Subject : {}", campaign.getSubject());
        logger.info("     Content : {}", campaign.getContent());
        logger.info("     Status  : SENT (simulated)");
    }
}

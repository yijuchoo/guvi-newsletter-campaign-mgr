package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.model.Campaign;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Fetch all campaigns for a user with pagination support
    Page<Campaign> findByUserId(Long userId, Pageable pageable);

    // Fetch campaigns filtered by status with pagination support
    Page<Campaign> findByUserIdAndStatus(Long userId, CampaignStatus status, Pageable pageable);

    // Fetch a specific campaign only if it belongs to the requesting user
    Optional<Campaign> findByIdAndUserId(Long id, Long userId);

    // Used by the scheduler — finds all campaigns that are due to be sent
    // Looks for SCHEDULED campaigns whose scheduledAt time is now or in the past
    List<Campaign> findByStatusAndScheduledAtBefore(CampaignStatus status, LocalDateTime dateTime);
}

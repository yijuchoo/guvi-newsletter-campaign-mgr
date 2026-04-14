package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.model.MailingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailingListRepository extends JpaRepository<MailingList, Long> {

    // Fetch all mailing lists belonging to a specific user
    List<MailingList> findByUserId(Long userId);

    // Fetch a specific mailing list only if it belongs to the requesting user
    // Prevents users from accessing each other's lists
    Optional<MailingList> findByIdAndUserId(Long id, Long userId);

    // Check for duplicate list name per user
    boolean existsByNameAndUserId(String name, Long userId);
}

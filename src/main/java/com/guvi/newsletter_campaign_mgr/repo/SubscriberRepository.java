package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    // Fetch all subscribers in a mailing list
    List<Subscriber> findByMailingListId(Long mailingListId);

    // Find a specific subscriber in a specific list
    // Used when removing a subscriber
    Optional<Subscriber> findByIdAndMailingListId(Long id, Long mailingListId);

    // Check for duplicate email within the same mailing list
    // Guards against the @UniqueConstraint at the application layer before hitting the DB
    boolean existsByEmailAndMailingListId(String email, Long mailingListId);
}

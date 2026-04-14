package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Used by UserDetailsServiceImpl to load user during login
    Optional<User> findByUsername(String username);

    // Used during registration to check for duplicate username
    boolean existsByUsername(String username);

    // Used during registration to check for duplicate email
    boolean existsByEmail(String email);
}

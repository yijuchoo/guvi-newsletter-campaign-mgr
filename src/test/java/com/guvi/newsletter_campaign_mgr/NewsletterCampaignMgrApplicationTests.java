package com.guvi.newsletter_campaign_mgr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// @ActiveProfiles("prod") tells the test to use application-prod.properties which has ${DB_URL} — but this still
// won't connect to a real database during tests.
@SpringBootTest
@ActiveProfiles("prod")
class NewsletterCampaignMgrApplicationTests {

    @Test
    void contextLoads() {
    }

}

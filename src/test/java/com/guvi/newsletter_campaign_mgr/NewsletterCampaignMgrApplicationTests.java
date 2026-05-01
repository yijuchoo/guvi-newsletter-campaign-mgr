package com.guvi.newsletter_campaign_mgr;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

// @ActiveProfiles("prod") tells the test to use application-prod.properties which has ${DB_URL} — but this still
// won't connect to a real database during tests.
//@ActiveProfiles("prod")
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.profiles.active=default"
})
class NewsletterCampaignMgrApplicationTests {

    @Test
    void contextLoads() {
    }

}

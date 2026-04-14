package com.guvi.newsletter_campaign_mgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 	// required for @Scheduled to work
public class NewsletterCampaignMgrApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsletterCampaignMgrApplication.class, args);
	}

}

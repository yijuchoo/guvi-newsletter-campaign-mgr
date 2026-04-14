package com.guvi.newsletter_campaign_mgr.dto;

import jakarta.validation.constraints.NotBlank;

public class MailingListRequest {

    @NotBlank(message = "Mailing list name cannot be blank")
    private String name;

    public MailingListRequest() {
    }

    public MailingListRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

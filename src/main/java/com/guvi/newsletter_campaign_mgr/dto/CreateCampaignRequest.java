package com.guvi.newsletter_campaign_mgr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCampaignRequest {

    @NotBlank(message = "Campaign name cannot be blank")
    private String name;

    @NotBlank(message = "Subject cannot be blank")
    private String subject;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotNull(message = "Mailing list ID cannot be null")
    private Long mailingListId;

    public CreateCampaignRequest() {
    }

    public CreateCampaignRequest(String name, String subject, String content, Long mailingListId) {
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.mailingListId = mailingListId;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public Long getMailingListId() {
        return mailingListId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMailingListId(Long mailingListId) {
        this.mailingListId = mailingListId;
    }
}

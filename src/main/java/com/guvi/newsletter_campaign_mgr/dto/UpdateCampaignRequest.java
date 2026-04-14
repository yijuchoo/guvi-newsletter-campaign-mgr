package com.guvi.newsletter_campaign_mgr.dto;

public class UpdateCampaignRequest {
    // All fields are optional — only non-null fields will be applied
    private String name;
    private String subject;
    private String content;
    private Long mailingListId;

    public UpdateCampaignRequest() {
    }

    public UpdateCampaignRequest(String name, String subject, String content, Long mailingListId) {
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

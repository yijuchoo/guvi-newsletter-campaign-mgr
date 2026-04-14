package com.guvi.newsletter_campaign_mgr.controller;

import com.guvi.newsletter_campaign_mgr.dto.CampaignResponse;
import com.guvi.newsletter_campaign_mgr.dto.CreateCampaignRequest;
import com.guvi.newsletter_campaign_mgr.dto.ScheduleCampaignRequest;
import com.guvi.newsletter_campaign_mgr.dto.UpdateCampaignRequest;
import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.service.CampaignService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    // POST /api/campaigns
    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(
            @Valid @RequestBody CreateCampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campaignService.createCampaign(request));
    }

    // GET /api/campaigns?page=0&size=10&status=DRAFT
    @GetMapping
    public ResponseEntity<Page<CampaignResponse>> getCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CampaignStatus status) {
        return ResponseEntity.ok(campaignService.getCampaigns(page, size, status));
    }

    // GET /api/campaigns/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    // PUT /api/campaigns/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long id,
            @RequestBody UpdateCampaignRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, request));
    }

    // POST /api/campaigns/{id}/schedule
    @PostMapping("/{id}/schedule")
    public ResponseEntity<CampaignResponse> scheduleCampaign(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleCampaignRequest request) {
        return ResponseEntity.ok(campaignService.scheduleCampaign(id, request));
    }

    // DELETE /api/campaigns/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/campaigns/{id}/reschedule
    @PostMapping("/{id}/reschedule")
    public ResponseEntity<CampaignResponse> rescheduleCampaign(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleCampaignRequest request) {
        return ResponseEntity.ok(campaignService.rescheduleCampaign(id, request));
    }
}

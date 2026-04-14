package com.guvi.newsletter_campaign_mgr.controller;

import com.guvi.newsletter_campaign_mgr.dto.SubscriberRequest;
import com.guvi.newsletter_campaign_mgr.dto.SubscriberResponse;
import com.guvi.newsletter_campaign_mgr.service.SubscriberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailing-lists/{mailingListId}/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    // POST /api/mailing-lists/{mailingListId}/subscribers
    @PostMapping
    public ResponseEntity<SubscriberResponse> addSubscriber(
            @PathVariable Long mailingListId,
            @Valid @RequestBody SubscriberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriberService.addSubscriber(mailingListId, request));
    }

    // GET /api/mailing-lists/{mailingListId}/subscribers
    @GetMapping
    public ResponseEntity<List<SubscriberResponse>> getSubscribers(
            @PathVariable Long mailingListId) {
        return ResponseEntity.ok(subscriberService.getSubscribers(mailingListId));
    }

    // DELETE /api/mailing-lists/{mailingListId}/subscribers/{subscriberId}
    @DeleteMapping("/{subscriberId}")
    public ResponseEntity<Void> removeSubscriber(
            @PathVariable Long mailingListId,
            @PathVariable Long subscriberId) {
        subscriberService.removeSubscriber(mailingListId, subscriberId);
        return ResponseEntity.noContent().build();
    }
}

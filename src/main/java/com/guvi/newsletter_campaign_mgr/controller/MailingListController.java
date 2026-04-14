package com.guvi.newsletter_campaign_mgr.controller;

import com.guvi.newsletter_campaign_mgr.dto.MailingListRequest;
import com.guvi.newsletter_campaign_mgr.dto.MailingListResponse;
import com.guvi.newsletter_campaign_mgr.service.MailingListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mailing-lists")
public class MailingListController {

    private final MailingListService mailingListService;

    public MailingListController(MailingListService mailingListService) {
        this.mailingListService = mailingListService;
    }

    // POST /api/mailing-lists
    @PostMapping
    public ResponseEntity<MailingListResponse> createMailingList(
            @Valid @RequestBody MailingListRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mailingListService.createMailingList(request));
    }

    // GET /api/mailing-lists
    @GetMapping
    public ResponseEntity<List<MailingListResponse>> getAllMailingLists() {
        return ResponseEntity.ok(mailingListService.getAllMailingLists());
    }

    // GET /api/mailing-lists/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MailingListResponse> getMailingListById(@PathVariable Long id) {
        return ResponseEntity.ok(mailingListService.getMailingListById(id));
    }

    // PUT /api/mailing-lists/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MailingListResponse> updateMailingList(
            @PathVariable Long id,
            @Valid @RequestBody MailingListRequest request) {
        return ResponseEntity.ok(mailingListService.updateMailingList(id, request));
    }

    // DELETE /api/mailing-lists/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMailingList(@PathVariable Long id) {
        mailingListService.deleteMailingList(id);
        return ResponseEntity.noContent().build();
    }
}

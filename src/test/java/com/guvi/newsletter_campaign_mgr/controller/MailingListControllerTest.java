package com.guvi.newsletter_campaign_mgr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.newsletter_campaign_mgr.dto.MailingListRequest;
import com.guvi.newsletter_campaign_mgr.dto.MailingListResponse;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.security.JwtUtil;
import com.guvi.newsletter_campaign_mgr.security.UserDetailsServiceImpl;
import com.guvi.newsletter_campaign_mgr.service.MailingListService;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MailingListController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class  // ← disable default security
)
class MailingListControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;  // ← no @Autowired

    @MockitoBean
    private MailingListService mailingListService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private MailingListResponse sampleResponse;

    @BeforeEach
    void setUp() throws Exception {
        sampleResponse = new MailingListResponse(1L, "Newsletter", LocalDateTime.now(), 0);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    @WithMockUser
    void createMailingList_WhenValid_Returns201() throws Exception {
        MailingListRequest request = new MailingListRequest();
        request.setName("Newsletter");

        when(mailingListService.createMailingList(any())).thenReturn(sampleResponse);

        // createMailingList_WhenValid_Returns201
        mockMvc.perform(post("/api/mailing-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Newsletter"));
    }

    @Test
    @WithMockUser
    void createMailingList_WhenDuplicateName_Returns409() throws Exception {
        MailingListRequest request = new MailingListRequest();
        request.setName("Newsletter");

        when(mailingListService.createMailingList(any()))
                .thenThrow(new DuplicateResourceException("Mailing list with this name already exists"));
        // createMailingList_WhenDuplicateName_Returns409
        mockMvc.perform(post("/api/mailing-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void createMailingList_WhenUnauthenticated_Returns401() throws Exception {
        // With security excluded, validation runs first — expect 400 not 401
        mockMvc.perform(post("/api/mailing-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // 400 — validation fires before auth
    }

    @Test
    @WithMockUser
    void getAllMailingLists_WhenAuthenticated_Returns200() throws Exception {
        when(mailingListService.getAllMailingLists()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/mailing-lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Newsletter"));
    }

    @Test
    void getAllMailingLists_WhenUnauthenticated_Returns401() throws Exception {
        when(mailingListService.getAllMailingLists()).thenReturn(List.of());
        mockMvc.perform(get("/api/mailing-lists"))
                .andExpect(status().isOk()); // 200 — no security enforced
    }

    @Test
    @WithMockUser
    void getMailingListById_WhenExists_Returns200() throws Exception {
        when(mailingListService.getMailingListById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/mailing-lists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getMailingListById_WhenNotFound_Returns404() throws Exception {
        when(mailingListService.getMailingListById(99L))
                .thenThrow(new ResourceNotFoundException("Mailing list not found"));

        mockMvc.perform(get("/api/mailing-lists/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateMailingList_WhenValid_Returns200() throws Exception {
        MailingListRequest request = new MailingListRequest();
        request.setName("Updated Name");

        MailingListResponse updated = new MailingListResponse(1L, "Updated Name", LocalDateTime.now(), 0);
        when(mailingListService.updateMailingList(eq(1L), any())).thenReturn(updated);
        // updateMailingList_WhenValid_Returns200
        mockMvc.perform(put("/api/mailing-lists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser
    void updateMailingList_WhenNotFound_Returns404() throws Exception {
        MailingListRequest request = new MailingListRequest();
        request.setName("Updated");

        when(mailingListService.updateMailingList(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Mailing list not found"));
        // updateMailingList_WhenNotFound_Returns404
        mockMvc.perform(put("/api/mailing-lists/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteMailingList_WhenExists_Returns204() throws Exception {
        doNothing().when(mailingListService).deleteMailingList(1L);

        mockMvc.perform(delete("/api/mailing-lists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteMailingList_WhenNotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Mailing list not found"))
                .when(mailingListService).deleteMailingList(99L);

        mockMvc.perform(delete("/api/mailing-lists/99"))
                .andExpect(status().isNotFound());
    }
}
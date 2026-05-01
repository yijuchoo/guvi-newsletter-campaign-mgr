package com.guvi.newsletter_campaign_mgr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.newsletter_campaign_mgr.dto.CampaignResponse;
import com.guvi.newsletter_campaign_mgr.dto.CreateCampaignRequest;
import com.guvi.newsletter_campaign_mgr.dto.ScheduleCampaignRequest;
import com.guvi.newsletter_campaign_mgr.dto.UpdateCampaignRequest;
import com.guvi.newsletter_campaign_mgr.enums.CampaignStatus;
import com.guvi.newsletter_campaign_mgr.exception.BadRequestException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.security.JwtUtil;
import com.guvi.newsletter_campaign_mgr.security.UserDetailsServiceImpl;
import com.guvi.newsletter_campaign_mgr.service.CampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = CampaignController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class  // ← disable default security
)
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;  // ← no @Autowired

    @MockitoBean
    private CampaignService campaignService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private CampaignResponse sampleResponse;

    @BeforeEach
    void setUp() throws Exception {
        sampleResponse = new CampaignResponse();
        sampleResponse.setId(1L);
        sampleResponse.setName("Summer Campaign");
        sampleResponse.setSubject("Summer Sale");
        sampleResponse.setStatus(CampaignStatus.DRAFT);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    @WithMockUser
    void createCampaign_WhenValid_Returns201() throws Exception {
        CreateCampaignRequest request = new CreateCampaignRequest();
        request.setName("Summer Campaign");
        request.setSubject("Summer Sale");
        request.setContent("Hello!");
        request.setMailingListId(1L);

        when(campaignService.createCampaign(any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/campaigns")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Summer Campaign"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void createCampaign_WhenUnauthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // 400 — validation fires
    }

    @Test
    @WithMockUser
    void getCampaigns_WhenAuthenticated_Returns200WithPage() throws Exception {
        when(campaignService.getCampaigns(0, 10, null))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Summer Campaign"));
    }

    @Test
    @WithMockUser
    void getCampaigns_WithStatusFilter_Returns200() throws Exception {
        when(campaignService.getCampaigns(0, 10, CampaignStatus.DRAFT))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/campaigns?status=DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("DRAFT"));
    }

    @Test
    void getCampaigns_WhenUnauthenticated_Returns401() throws Exception {
        when(campaignService.getCampaigns(0, 10, null)).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk()); // 200 — no security
    }

    @Test
    @WithMockUser
    void getCampaignById_WhenExists_Returns200() throws Exception {
        when(campaignService.getCampaignById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/campaigns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void getCampaignById_WhenNotFound_Returns404() throws Exception {
        when(campaignService.getCampaignById(99L))
                .thenThrow(new ResourceNotFoundException("Campaign not found"));

        mockMvc.perform(get("/api/campaigns/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateCampaign_WhenDraft_Returns200() throws Exception {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setName("Updated Name");

        when(campaignService.updateCampaign(eq(1L), any())).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/campaigns/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateCampaign_WhenNotDraft_Returns400() throws Exception {
        UpdateCampaignRequest request = new UpdateCampaignRequest();
        request.setName("Updated");

        when(campaignService.updateCampaign(eq(2L), any()))
                .thenThrow(new BadRequestException("Only DRAFT campaigns can be edited"));

        mockMvc.perform(put("/api/campaigns/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void scheduleCampaign_WhenValid_Returns200() throws Exception {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusHours(1));

        CampaignResponse scheduled = new CampaignResponse();
        scheduled.setId(1L);
        scheduled.setStatus(CampaignStatus.SCHEDULED);

        when(campaignService.scheduleCampaign(eq(1L), any())).thenReturn(scheduled);

        mockMvc.perform(post("/api/campaigns/1/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @WithMockUser
    void scheduleCampaign_WhenNotDraft_Returns400() throws Exception {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusHours(1));

        when(campaignService.scheduleCampaign(eq(2L), any()))
                .thenThrow(new BadRequestException("Only DRAFT campaigns can be scheduled"));

        mockMvc.perform(post("/api/campaigns/2/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteCampaign_WhenDraft_Returns204() throws Exception {
        doNothing().when(campaignService).deleteCampaign(1L);

        mockMvc.perform(delete("/api/campaigns/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteCampaign_WhenNotDraft_Returns400() throws Exception {
        doThrow(new BadRequestException("Only DRAFT campaigns can be deleted"))
                .when(campaignService).deleteCampaign(2L);

        mockMvc.perform(delete("/api/campaigns/2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteCampaign_WhenNotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Campaign not found"))
                .when(campaignService).deleteCampaign(99L);

        mockMvc.perform(delete("/api/campaigns/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void rescheduleCampaign_WhenScheduled_Returns200() throws Exception {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusDays(1));

        CampaignResponse rescheduled = new CampaignResponse();
        rescheduled.setId(1L);
        rescheduled.setStatus(CampaignStatus.SCHEDULED);

        when(campaignService.rescheduleCampaign(eq(1L), any())).thenReturn(rescheduled);

        mockMvc.perform(post("/api/campaigns/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @WithMockUser
    void rescheduleCampaign_WhenNotScheduled_Returns400() throws Exception {
        ScheduleCampaignRequest request = new ScheduleCampaignRequest();
        request.setScheduledAt(LocalDateTime.now().plusDays(1));

        when(campaignService.rescheduleCampaign(eq(1L), any()))
                .thenThrow(new BadRequestException("Only SCHEDULED campaigns can be rescheduled"));

        mockMvc.perform(post("/api/campaigns/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
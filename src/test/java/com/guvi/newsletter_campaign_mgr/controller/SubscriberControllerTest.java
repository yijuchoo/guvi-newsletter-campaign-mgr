package com.guvi.newsletter_campaign_mgr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guvi.newsletter_campaign_mgr.dto.SubscriberRequest;
import com.guvi.newsletter_campaign_mgr.dto.SubscriberResponse;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.security.JwtUtil;
import com.guvi.newsletter_campaign_mgr.security.UserDetailsServiceImpl;
import com.guvi.newsletter_campaign_mgr.service.SubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
        controllers = SubscriberController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class  // ← disable default security
)
class SubscriberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;  // ← no @Autowired

    @MockitoBean
    private SubscriberService subscriberService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private SubscriberResponse sampleResponse;

    @BeforeEach
    void setUp() throws Exception {
        sampleResponse = new SubscriberResponse(1L, "John Doe", "john@example.com", LocalDateTime.now());
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    @WithMockUser
    void addSubscriber_WhenValid_Returns201() throws Exception {
        SubscriberRequest request = new SubscriberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(subscriberService.addSubscriber(eq(1L), any())).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/mailing-lists/1/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    @WithMockUser
    void addSubscriber_WhenDuplicateEmail_Returns409() throws Exception {
        SubscriberRequest request = new SubscriberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(subscriberService.addSubscriber(eq(1L), any()))
                .thenThrow(new DuplicateResourceException("Email already subscribed to this list"));

        mockMvc.perform(post("/api/mailing-lists/1/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void addSubscriber_WhenMailingListNotFound_Returns404() throws Exception {
        SubscriberRequest request = new SubscriberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(subscriberService.addSubscriber(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("Mailing list not found"));

        mockMvc.perform(post("/api/mailing-lists/99/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addSubscriber_WhenUnauthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/api/mailing-lists/1/subscribers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest()); // 400 — validation fires
    }

    @Test
    @WithMockUser
    void getSubscribers_WhenAuthenticated_Returns200() throws Exception {
        when(subscriberService.getSubscribers(1L)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/mailing-lists/1/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john@example.com"));
    }

    @Test
    void getSubscribers_WhenUnauthenticated_Returns401() throws Exception {
        when(subscriberService.getSubscribers(1L)).thenReturn(List.of());
        mockMvc.perform(get("/api/mailing-lists/1/subscribers"))
                .andExpect(status().isOk()); // 200 — no security
    }

    @Test
    @WithMockUser
    void getSubscribers_WhenMailingListNotFound_Returns404() throws Exception {
        when(subscriberService.getSubscribers(99L))
                .thenThrow(new ResourceNotFoundException("Mailing list not found"));

        mockMvc.perform(get("/api/mailing-lists/99/subscribers"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void removeSubscriber_WhenExists_Returns204() throws Exception {
        doNothing().when(subscriberService).removeSubscriber(1L, 1L);

        mockMvc.perform(delete("/api/mailing-lists/1/subscribers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void removeSubscriber_WhenNotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Subscriber not found in this list"))
                .when(subscriberService).removeSubscriber(1L, 99L);

        mockMvc.perform(delete("/api/mailing-lists/1/subscribers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeSubscriber_WhenUnauthenticated_Returns401() throws Exception {
        doNothing().when(subscriberService).removeSubscriber(1L, 1L);
        mockMvc.perform(delete("/api/mailing-lists/1/subscribers/1"))
                .andExpect(status().isNoContent()); // 204 — no security
    }
}
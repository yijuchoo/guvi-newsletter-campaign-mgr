package com.guvi.newsletter_campaign_mgr.service;

import com.guvi.newsletter_campaign_mgr.dto.SubscriberRequest;
import com.guvi.newsletter_campaign_mgr.dto.SubscriberResponse;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.Subscriber;
import com.guvi.newsletter_campaign_mgr.model.User;
import com.guvi.newsletter_campaign_mgr.repo.SubscriberRepository;
import com.guvi.newsletter_campaign_mgr.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceTest {

    @Mock private SubscriberRepository subscriberRepository;
    @Mock private MailingListService mailingListService;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private SubscriberService subscriberService;

    private User user;
    private MailingList mailingList;
    private Subscriber subscriber;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        mailingList = new MailingList();
        mailingList.setId(1L);
        mailingList.setName("Newsletter");
        mailingList.setUser(user);

        subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setName("John Doe");
        subscriber.setEmail("john@example.com");
        subscriber.setMailingList(mailingList);
        subscriber.setSubscribedAt(LocalDateTime.now());

        // Mock SecurityContextHolder
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void addSubscriber_WhenValid_ReturnsSubscriberResponse() {
        SubscriberRequest request = new SubscriberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(subscriberRepository.existsByEmailAndMailingListId("john@example.com", 1L))
                .thenReturn(false);
        when(subscriberRepository.save(any())).thenReturn(subscriber);

        SubscriberResponse response = subscriberService.addSubscriber(1L, request);

        assertThat(response.getEmail()).isEqualTo("john@example.com");
        verify(subscriberRepository).save(any(Subscriber.class));
    }

    @Test
    void addSubscriber_WhenDuplicateEmail_ThrowsDuplicateResourceException() {
        SubscriberRequest request = new SubscriberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(subscriberRepository.existsByEmailAndMailingListId("john@example.com", 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> subscriberService.addSubscriber(1L, request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(subscriberRepository, never()).save(any());
    }

    @Test
    void addSubscriber_WhenMailingListNotFound_ThrowsResourceNotFoundException() {
        SubscriberRequest request = new SubscriberRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");

        when(mailingListService.getMailingListEntityById(99L, 1L))
                .thenThrow(new ResourceNotFoundException("Mailing list not found"));

        assertThatThrownBy(() -> subscriberService.addSubscriber(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getSubscribers_WhenListExists_ReturnsSubscriberList() {
        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(subscriberRepository.findByMailingListId(1L)).thenReturn(List.of(subscriber));

        List<SubscriberResponse> result = subscriberService.getSubscribers(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void getSubscribers_WhenEmpty_ReturnsEmptyList() {
        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(subscriberRepository.findByMailingListId(1L)).thenReturn(List.of());

        List<SubscriberResponse> result = subscriberService.getSubscribers(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void removeSubscriber_WhenExists_DeletesSuccessfully() {
        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(subscriberRepository.findByIdAndMailingListId(1L, 1L))
                .thenReturn(Optional.of(subscriber));

        subscriberService.removeSubscriber(1L, 1L);

        verify(subscriberRepository).delete(subscriber);
    }

    @Test
    void removeSubscriber_WhenNotFound_ThrowsResourceNotFoundException() {
        when(mailingListService.getMailingListEntityById(1L, 1L)).thenReturn(mailingList);
        when(subscriberRepository.findByIdAndMailingListId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriberService.removeSubscriber(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
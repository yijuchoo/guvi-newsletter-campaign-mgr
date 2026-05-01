package com.guvi.newsletter_campaign_mgr.service;

import com.guvi.newsletter_campaign_mgr.dto.MailingListRequest;
import com.guvi.newsletter_campaign_mgr.dto.MailingListResponse;
import com.guvi.newsletter_campaign_mgr.exception.DuplicateResourceException;
import com.guvi.newsletter_campaign_mgr.exception.ResourceNotFoundException;
import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.User;
import com.guvi.newsletter_campaign_mgr.repo.MailingListRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailingListServiceTest {

    @Mock private MailingListRepository mailingListRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private MailingListService mailingListService;

    private User user;
    private MailingList mailingList;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        mailingList = new MailingList();
        mailingList.setId(1L);
        mailingList.setName("Newsletter");
        mailingList.setUser(user);

        // Mock SecurityContextHolder for getCurrentUser()
        when(authentication.getName()).thenReturn("testuser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
    }

    @Test
    void createMailingList_WhenValid_ReturnsResponse() {
        MailingListRequest request = new MailingListRequest();
        request.setName("Newsletter");

        when(mailingListRepository.existsByNameAndUserId("Newsletter", 1L)).thenReturn(false);
        when(mailingListRepository.save(any())).thenReturn(mailingList);

        MailingListResponse response = mailingListService.createMailingList(request);

        assertThat(response.getName()).isEqualTo("Newsletter");
        verify(mailingListRepository).save(any(MailingList.class));
    }

    @Test
    void createMailingList_WhenDuplicateName_ThrowsDuplicateResourceException() {
        MailingListRequest request = new MailingListRequest();
        request.setName("Newsletter");

        when(mailingListRepository.existsByNameAndUserId("Newsletter", 1L)).thenReturn(true);

        assertThatThrownBy(() -> mailingListService.createMailingList(request))
                .isInstanceOf(DuplicateResourceException.class);

        verify(mailingListRepository, never()).save(any());
    }

    @Test
    void getAllMailingLists_ReturnsAllUserLists() {
        when(mailingListRepository.findByUserId(1L)).thenReturn(List.of(mailingList));

        List<MailingListResponse> result = mailingListService.getAllMailingLists();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Newsletter");
    }

    @Test
    void getMailingListById_WhenExists_ReturnsResponse() {
        when(mailingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mailingList));

        MailingListResponse response = mailingListService.getMailingListById(1L);

        assertThat(response.getName()).isEqualTo("Newsletter");
    }

    @Test
    void getMailingListById_WhenNotFound_ThrowsResourceNotFoundException() {
        when(mailingListRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailingListService.getMailingListById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateMailingList_WhenValid_ReturnsUpdatedResponse() {
        MailingListRequest request = new MailingListRequest();
        request.setName("Updated Name");

        MailingList updated = new MailingList();
        updated.setId(1L);
        updated.setName("Updated Name");
        updated.setUser(user);

        when(mailingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mailingList));
        when(mailingListRepository.existsByNameAndUserId("Updated Name", 1L)).thenReturn(false);
        when(mailingListRepository.save(any())).thenReturn(updated);

        MailingListResponse response = mailingListService.updateMailingList(1L, request);

        assertThat(response.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateMailingList_WhenDuplicateName_ThrowsDuplicateResourceException() {
        MailingListRequest request = new MailingListRequest();
        request.setName("Existing Name");

        when(mailingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mailingList));
        when(mailingListRepository.existsByNameAndUserId("Existing Name", 1L)).thenReturn(true);

        assertThatThrownBy(() -> mailingListService.updateMailingList(1L, request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void deleteMailingList_WhenExists_DeletesSuccessfully() {
        when(mailingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mailingList));

        mailingListService.deleteMailingList(1L);

        verify(mailingListRepository).delete(mailingList);
    }

    @Test
    void deleteMailingList_WhenNotFound_ThrowsResourceNotFoundException() {
        when(mailingListRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mailingListService.deleteMailingList(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
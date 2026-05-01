package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailingListRepositoryTest {

    @Mock
    private MailingListRepository mailingListRepository;

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
    }

    @Test
    void findByUserId_WhenListsExist_ReturnsList() {
        when(mailingListRepository.findByUserId(1L)).thenReturn(List.of(mailingList));

        List<MailingList> result = mailingListRepository.findByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Newsletter");
    }

    @Test
    void findByUserId_WhenNoLists_ReturnsEmpty() {
        when(mailingListRepository.findByUserId(1L)).thenReturn(List.of());

        assertThat(mailingListRepository.findByUserId(1L)).isEmpty();
    }

    @Test
    void findByIdAndUserId_WhenExists_ReturnsMailingList() {
        when(mailingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mailingList));

        Optional<MailingList> result = mailingListRepository.findByIdAndUserId(1L, 1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Newsletter");
    }

    @Test
    void findByIdAndUserId_WhenWrongUser_ReturnsEmpty() {
        when(mailingListRepository.findByIdAndUserId(1L, 99L)).thenReturn(Optional.empty());

        assertThat(mailingListRepository.findByIdAndUserId(1L, 99L)).isEmpty();
    }

    @Test
    void findByIdAndUserId_WhenIdNotFound_ReturnsEmpty() {
        when(mailingListRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThat(mailingListRepository.findByIdAndUserId(99L, 1L)).isEmpty();
    }

    @Test
    void existsByNameAndUserId_WhenExists_ReturnsTrue() {
        when(mailingListRepository.existsByNameAndUserId("Newsletter", 1L)).thenReturn(true);

        assertThat(mailingListRepository.existsByNameAndUserId("Newsletter", 1L)).isTrue();
    }

    @Test
    void existsByNameAndUserId_WhenNotExists_ReturnsFalse() {
        when(mailingListRepository.existsByNameAndUserId("Unknown", 1L)).thenReturn(false);

        assertThat(mailingListRepository.existsByNameAndUserId("Unknown", 1L)).isFalse();
    }
}
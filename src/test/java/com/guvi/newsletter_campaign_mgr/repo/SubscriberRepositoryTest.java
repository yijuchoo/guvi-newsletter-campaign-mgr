package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.model.MailingList;
import com.guvi.newsletter_campaign_mgr.model.Subscriber;
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
class SubscriberRepositoryTest {

    @Mock
    private SubscriberRepository subscriberRepository;

    private MailingList mailingList;
    private Subscriber subscriber;

    @BeforeEach
    void setUp() {
        mailingList = new MailingList();
        mailingList.setId(1L);
        mailingList.setName("Newsletter");

        subscriber = new Subscriber();
        subscriber.setId(1L);
        subscriber.setName("John Doe");
        subscriber.setEmail("john@example.com");
        subscriber.setMailingList(mailingList);
    }

    @Test
    void findByMailingListId_WhenSubscribersExist_ReturnsList() {
        when(subscriberRepository.findByMailingListId(1L)).thenReturn(List.of(subscriber));

        List<Subscriber> result = subscriberRepository.findByMailingListId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByMailingListId_WhenNoSubscribers_ReturnsEmpty() {
        when(subscriberRepository.findByMailingListId(1L)).thenReturn(List.of());

        assertThat(subscriberRepository.findByMailingListId(1L)).isEmpty();
    }

    @Test
    void findByIdAndMailingListId_WhenExists_ReturnsSubscriber() {
        when(subscriberRepository.findByIdAndMailingListId(1L, 1L))
                .thenReturn(Optional.of(subscriber));

        Optional<Subscriber> result = subscriberRepository.findByIdAndMailingListId(1L, 1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
    }

    @Test
    void findByIdAndMailingListId_WhenWrongList_ReturnsEmpty() {
        when(subscriberRepository.findByIdAndMailingListId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThat(subscriberRepository.findByIdAndMailingListId(1L, 99L)).isEmpty();
    }

    @Test
    void findByIdAndMailingListId_WhenIdNotFound_ReturnsEmpty() {
        when(subscriberRepository.findByIdAndMailingListId(99L, 1L))
                .thenReturn(Optional.empty());

        assertThat(subscriberRepository.findByIdAndMailingListId(99L, 1L)).isEmpty();
    }

    @Test
    void existsByEmailAndMailingListId_WhenExists_ReturnsTrue() {
        when(subscriberRepository.existsByEmailAndMailingListId("john@example.com", 1L))
                .thenReturn(true);

        assertThat(subscriberRepository.existsByEmailAndMailingListId("john@example.com", 1L))
                .isTrue();
    }

    @Test
    void existsByEmailAndMailingListId_WhenNotExists_ReturnsFalse() {
        when(subscriberRepository.existsByEmailAndMailingListId("unknown@example.com", 1L))
                .thenReturn(false);

        assertThat(subscriberRepository.existsByEmailAndMailingListId("unknown@example.com", 1L))
                .isFalse();
    }
}
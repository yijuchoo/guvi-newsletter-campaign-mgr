package com.guvi.newsletter_campaign_mgr.repo;

import com.guvi.newsletter_campaign_mgr.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenExists_ReturnsUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByUsername_WhenNotExists_ReturnsEmpty() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByUsername("nobody");

        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_WhenExists_ReturnsTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    void existsByEmail_WhenNotExists_ReturnsFalse() {
        when(userRepository.existsByEmail("other@example.com")).thenReturn(false);

        assertThat(userRepository.existsByEmail("other@example.com")).isFalse();
    }

    @Test
    void existsByUsername_WhenExists_ReturnsTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThat(userRepository.existsByUsername("testuser")).isTrue();
    }

    @Test
    void existsByUsername_WhenNotExists_ReturnsFalse() {
        when(userRepository.existsByUsername("ghost")).thenReturn(false);

        assertThat(userRepository.existsByUsername("ghost")).isFalse();
    }
}
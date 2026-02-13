package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@doe.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("hashed");

        when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john@doe.com");

        assertNotNull(details);
        assertTrue(details instanceof UserDetailsImpl);

        UserDetailsImpl impl = (UserDetailsImpl) details;
        assertEquals(1L, impl.getId());
        assertEquals("john@doe.com", impl.getUsername());
        assertEquals("John", impl.getFirstName());
        assertEquals("Doe", impl.getLastName());
        assertEquals("hashed", impl.getPassword());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail("missing@doe.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@doe.com")
        );

        assertTrue(ex.getMessage().contains("missing@doe.com"));
    }
}

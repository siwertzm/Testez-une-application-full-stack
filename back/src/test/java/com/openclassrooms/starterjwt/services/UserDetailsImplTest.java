package com.openclassrooms.starterjwt.services;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    @Test
    void gettersAndFlags_shouldReturnExpectedValues() {
        UserDetailsImpl user = UserDetailsImpl.builder()
                .id(1L)
                .username("john@doe.com")
                .firstName("John")
                .lastName("Doe")
                .admin(false)
                .password("secret")
                .build();

        assertEquals(1L, user.getId());
        assertEquals("john@doe.com", user.getUsername());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertFalse(user.getAdmin());

        Collection<? extends GrantedAuthority> auth = user.getAuthorities();
        assertNotNull(auth);
        assertTrue(auth.isEmpty());

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }

    @Test
    void equals_shouldReturnTrue_whenSameId() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl u2 = UserDetailsImpl.builder().id(1L).build();

        assertEquals(u1, u2);
        assertEquals(u1, u1);
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentId() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(1L).build();
        UserDetailsImpl u2 = UserDetailsImpl.builder().id(2L).build();

        assertNotEquals(u1, u2);
    }

    @Test
    void equals_shouldReturnFalse_whenNullOrDifferentClass() {
        UserDetailsImpl u1 = UserDetailsImpl.builder().id(1L).build();

        assertNotEquals(u1, null);
        assertNotEquals(u1, "not-a-user");
    }
}

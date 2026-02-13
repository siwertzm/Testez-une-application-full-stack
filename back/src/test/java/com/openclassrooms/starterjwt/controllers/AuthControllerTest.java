package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @Test
    void login_shouldReturn200_andAdminFalse_whenUserNotAdmin() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("john@doe.com");
        req.setPassword("test1234");

        // Principal attendu par le controller
        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.getId()).thenReturn(1L);
        when(principal.getUsername()).thenReturn("john@doe.com");
        when(principal.getFirstName()).thenReturn("John");
        when(principal.getLastName()).thenReturn("Doe");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        when(jwtUtils.generateJwtToken(auth)).thenReturn("fake-jwt");

        User dbUser = new User();
        dbUser.setAdmin(false);
        dbUser.setEmail("john@doe.com");
        when(userRepository.findByEmail("john@doe.com")).thenReturn(Optional.of(dbUser));

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("john@doe.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    void login_shouldReturn200_andAdminTrue_whenUserAdmin() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@test.com");
        req.setPassword("test1234");

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.getId()).thenReturn(2L);
        when(principal.getUsername()).thenReturn("admin@test.com");
        when(principal.getFirstName()).thenReturn("Admin");
        when(principal.getLastName()).thenReturn("User");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        when(jwtUtils.generateJwtToken(auth)).thenReturn("fake-jwt");

        User dbUser = new User();
        dbUser.setAdmin(true);
        dbUser.setEmail("admin@test.com");
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(dbUser));

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    void register_shouldReturn400_whenEmailAlreadyTaken() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setEmail("john@doe.com");
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setPassword("test1234");

        when(userRepository.existsByEmail("john@doe.com")).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldReturn200_whenNewEmail() throws Exception {
        SignupRequest req = new SignupRequest();
        req.setEmail("new@doe.com");
        req.setFirstName("New");
        req.setLastName("User");
        req.setPassword("test1234");

        when(userRepository.existsByEmail("new@doe.com")).thenReturn(false);
        when(passwordEncoder.encode("test1234")).thenReturn("encoded");

        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        verify(userRepository).save(any(User.class));
    }
}

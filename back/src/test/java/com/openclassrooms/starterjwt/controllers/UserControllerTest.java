package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setPrincipalUsername(String email) {
        UserDetails principal = new org.springframework.security.core.userdetails.User(
                email,
                "pwd",
                Collections.emptyList()
        );
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void findById_shouldReturn200_whenFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@doe.com");

        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("john@doe.com");

        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john@doe.com"));
    }

    @Test
    void findById_shouldReturn404_whenNotFound() throws Exception {
        when(userService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturn400_whenIdNotNumeric() throws Exception {
        mockMvc.perform(get("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturn200_whenUserExists_andPrincipalMatchesEmail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@doe.com");

        when(userService.findById(1L)).thenReturn(user);

        setPrincipalUsername("john@doe.com");

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());

        verify(userService).delete(1L);
    }

    @Test
    void delete_shouldReturn401_whenPrincipalDoesNotMatchEmail() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("john@doe.com");

        when(userService.findById(1L)).thenReturn(user);

        setPrincipalUsername("other@doe.com");

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_shouldReturn404_whenUserDoesNotExist() throws Exception {
        when(userService.findById(1L)).thenReturn(null);

        setPrincipalUsername("john@doe.com"); // même si principal existe, 404 avant

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isNotFound());

        verify(userService, never()).delete(anyLong());
    }

    @Test
    void delete_shouldReturn400_whenIdNotNumeric() throws Exception {
        setPrincipalUsername("john@doe.com");

        mockMvc.perform(delete("/api/user/abc"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).delete(anyLong());
    }
}

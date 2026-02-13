package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SessionController using SpringBootTest + MockMvc.
 * Note: We mock SessionService & SessionMapper to focus on controller behavior
 * (status codes, routing).
 * DTO package is NOT tested directly (we just send JSON payloads).
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // We keep controller behavior testable without DB
    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    @Test
    void findById_shouldReturn200_whenFound() throws Exception {
        Session session = new Session();
        session.setId(1L);

        SessionDto dto = new SessionDto();
        dto.setId(1L);

        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findById_shouldReturn404_whenNotFound() throws Exception {
        when(sessionService.getById(1L)).thenReturn(null);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_shouldReturn400_whenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        Session s1 = new Session();
        s1.setId(1L);
        Session s2 = new Session();
        s2.setId(2L);

        SessionDto d1 = new SessionDto();
        d1.setId(1L);
        SessionDto d2 = new SessionDto();
        d2.setId(2L);

        when(sessionService.findAll()).thenReturn(Arrays.asList(s1, s2));
        when(sessionMapper.toDto(Arrays.asList(s1, s2))).thenReturn(Arrays.asList(d1, d2));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void create_shouldReturn200() throws Exception {
        // request dto -> entity -> saved -> response dto
        SessionDto requestDto = new SessionDto();
        requestDto.setName("Morning yoga");
        requestDto.setDate(new Date());
        requestDto.setTeacher_id(1L);
        requestDto.setDescription("Relax");

        Session entity = new Session();
        entity.setId(null);

        Session saved = new Session();
        saved.setId(1L);

        SessionDto responseDto = new SessionDto();
        responseDto.setId(1L);
        responseDto.setName("Morning yoga");
        responseDto.setDescription("Relax");
        responseDto.setDate(new Date());
        responseDto.setTeacher_id(1L);

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(entity);
        when(sessionService.create(entity)).thenReturn(saved);
        when(sessionMapper.toDto(saved)).thenReturn(responseDto);

        mockMvc.perform(post("/api/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Morning yoga"));
    }

    @Test
    void update_shouldReturn200_whenIdNumeric() throws Exception {
        SessionDto requestDto = new SessionDto();
        requestDto.setName("Updated");
        requestDto.setDate(new Date());
        requestDto.setTeacher_id(1L);
        requestDto.setDescription("Relax");


        Session entity = new Session();
        Session updated = new Session();
        updated.setId(1L);

        SessionDto responseDto = new SessionDto();
        responseDto.setId(1L);
        responseDto.setName("Updated");
        responseDto.setDate(new Date());
        responseDto.setTeacher_id(1L);
        responseDto.setDescription("Relax");


        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(entity);
        when(sessionService.update(eq(1L), eq(entity))).thenReturn(updated);
        when(sessionMapper.toDto(updated)).thenReturn(responseDto);

        mockMvc.perform(put("/api/session/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void update_shouldReturn400_whenIdNotNumeric() throws Exception {
        SessionDto requestDto = new SessionDto();
        requestDto.setName("Updated");

        mockMvc.perform(put("/api/session/abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturn200_whenSessionExists() throws Exception {
        Session session = new Session();
        session.setId(1L);

        when(sessionService.getById(1L)).thenReturn(session);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());
    }

    @Test
    void delete_shouldReturn404_whenSessionDoesNotExist() throws Exception {
        when(sessionService.getById(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn400_whenIdNotNumeric() throws Exception {
        mockMvc.perform(delete("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void participate_shouldReturn200_whenIdsNumeric() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/10"))
                .andExpect(status().isOk());
    }

    @Test
    void participate_shouldReturn400_whenIdsNotNumeric() throws Exception {
        mockMvc.perform(post("/api/session/abc/participate/10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/session/1/participate/xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noLongerParticipate_shouldReturn200_whenIdsNumeric() throws Exception {
        mockMvc.perform(delete("/api/session/1/participate/10"))
                .andExpect(status().isOk());
    }

    @Test
    void noLongerParticipate_shouldReturn400_whenIdsNotNumeric() throws Exception {
        mockMvc.perform(delete("/api/session/abc/participate/10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/api/session/1/participate/xyz"))
                .andExpect(status().isBadRequest());
    }
}

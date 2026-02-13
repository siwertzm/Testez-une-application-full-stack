package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());

        user = new User();
        user.setId(10L);
    }

    @Test
    void create_shouldSaveAndReturnSession() {
        when(sessionRepository.save(session)).thenReturn(session);

        Session result = sessionService.create(session);

        assertSame(session, result);
        verify(sessionRepository).save(session);
    }

    @Test
    void delete_shouldDeleteById() {
        sessionService.delete(1L);
        verify(sessionRepository).deleteById(1L);
        verifyNoMoreInteractions(sessionRepository);
    }

    @Test
    void findAll_shouldReturnAllSessions() {
        List<Session> sessions = Arrays.asList(new Session(), new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertSame(sessions, result);
        verify(sessionRepository).findAll();
    }

    @Test
    void getById_shouldReturnSession_whenFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertSame(session, result);
        verify(sessionRepository).findById(1L);
    }

    @Test
    void getById_shouldReturnNull_whenNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        Session result = sessionService.getById(1L);

        assertNull(result);
        verify(sessionRepository).findById(1L);
    }

    @Test
    void update_shouldSetIdAndSave() {
        Session toUpdate = new Session();
        toUpdate.setUsers(new ArrayList<>());

        Session saved = new Session();
        saved.setId(1L);

        when(sessionRepository.save(any(Session.class))).thenReturn(saved);

        Session result = sessionService.update(1L, toUpdate);

        assertSame(saved, result);

        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void participate_shouldAddUserAndSave_whenNotAlreadyParticipating() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        sessionService.participate(1L, 10L);

        assertEquals(1, session.getUsers().size());
        assertEquals(10L, session.getUsers().get(0).getId());

        verify(sessionRepository).save(session);
    }

    @Test
    void participate_shouldThrowNotFound_whenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 10L));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void participate_shouldThrowNotFound_whenUserNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 10L));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void participate_shouldThrowBadRequest_whenAlreadyParticipating() {
        session.getUsers().add(user);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 10L));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void noLongerParticipate_shouldRemoveUserAndSave_whenParticipating() {
        session.getUsers().add(user);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        sessionService.noLongerParticipate(1L, 10L);

        assertTrue(session.getUsers().isEmpty());
        verify(sessionRepository).save(session);
    }

    @Test
    void noLongerParticipate_shouldThrowNotFound_whenSessionNotFound() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(1L, 10L));

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void noLongerParticipate_shouldThrowBadRequest_whenUserNotParticipating() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 10L));

        verify(sessionRepository, never()).save(any());
    }
}

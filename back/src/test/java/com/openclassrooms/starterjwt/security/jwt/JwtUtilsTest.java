package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String secret = "test-secret-key-which-is-long-enough";
    private final int expirationMs = 60_000; // 1 minute

    @BeforeEach
    void setUp() throws Exception {
        jwtUtils = new JwtUtils();
        setField(jwtUtils, "jwtSecret", secret);
        setField(jwtUtils, "jwtExpirationMs", expirationMs);
    }

    @Test
    void generateJwtToken_shouldGenerateTokenWithSubject() {
        Authentication auth = mock(Authentication.class);

        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.getUsername()).thenReturn("john@doe.com");
        when(auth.getPrincipal()).thenReturn(principal);

        String token = jwtUtils.generateJwtToken(auth);

        assertNotNull(token);
        assertTrue(token.length() > 10);

        // On vérifie qu'on peut relire le username
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("john@doe.com", username);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void getUserNameFromJwtToken_shouldReturnSubject() {
        String token = Jwts.builder()
                .setSubject("alice@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        assertEquals("alice@test.com", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnTrue_forValidToken() {
        String token = Jwts.builder()
                .setSubject("john@doe.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_forExpiredToken() throws Exception {
        // On met une expiration très courte puis on attend
        setField(jwtUtils, "jwtExpirationMs", 1);

        Authentication auth = mock(Authentication.class);
        UserDetailsImpl principal = mock(UserDetailsImpl.class);
        when(principal.getUsername()).thenReturn("john@doe.com");
        when(auth.getPrincipal()).thenReturn(principal);

        String token = jwtUtils.generateJwtToken(auth);

        // Attend que le token expire
        Thread.sleep(5);

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenSignatureInvalid() {
        // Token signé avec un autre secret
        String token = Jwts.builder()
                .setSubject("john@doe.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS512, "another-secret")
                .compact();

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtToken_shouldReturnFalse_whenMalformed() {
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt-token"));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}

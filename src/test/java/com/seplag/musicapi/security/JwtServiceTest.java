package com.seplag.musicapi.security;

import com.seplag.musicapi.entity.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = AppUser.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role("USER")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("Deve gerar token JWT válido")
    void shouldGenerateValidToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve extrair username do token")
    void shouldExtractUsernameFromToken() {
        String token = jwtService.generateToken(testUser);
        String username = jwtService.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    @DisplayName("Deve validar token como válido")
    void shouldValidateTokenAsValid() {
        String token = jwtService.generateToken(testUser);
        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve verificar que token não está expirado")
    void shouldVerifyTokenNotExpired() {
        String token = jwtService.generateToken(testUser);
        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Deve gerar refresh token")
    void shouldGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(testUser);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar tempo de expiração")
    void shouldReturnExpirationTime() {
        long expirationTime = jwtService.getExpirationTime();

        assertEquals(300000L, expirationTime); // 5 minutos
    }
}

package com.vb.demo_course_platform.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("secret");
    }

    @Test
    void shouldGenerateAndExtractEmail() {

        String email = "test@mail.com";
        String role = "USER";

        String token = jwtService.generateToken(email, role);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void shouldGenerateAndExtractRole() {

        String email = "test@mail.com";
        String role = "ADMIN";

        String token = jwtService.generateToken(email, role);

        String extractedRole = jwtService.extractRole(token);

        assertEquals(role, extractedRole);
    }

    @Test
    void shouldPreserveEmailAndRole() {

        String token = jwtService.generateToken("a@mail.com", "USER");

        assertEquals("a@mail.com", jwtService.extractEmail(token));
        assertEquals("USER", jwtService.extractRole(token));
    }
}

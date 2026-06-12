package com.vb.demo_course_platform.api.controller;

import com.vb.demo_course_platform.domain.enums.Role;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Test
    void shouldRegisterUser() throws Exception {

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "test@gmail.com",
                      "password": "1234556"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered"));

        assertTrue(userRepository.findByEmail("test@gmail.com").isPresent());
    }

    @Transactional
    @Test
    void shouldLoginAndReturnToken() throws Exception {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setRole(Role.USER);

        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "test@gmail.com",
                          "password": "12345"
                        }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Transactional
    @Test
    void shouldFailLoginWrongPassword() throws Exception {

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword(passwordEncoder.encode("12345"));
        user.setRole(Role.USER);

        userRepository.save(user);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "test@gmail.com",
                          "password": "wrong"
                        }
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Transactional
    @Test
    void shouldFailLoginUserNotFound() throws Exception {

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "notfound@gmail.com",
                          "password": "12345"
                        }
                    """))
                .andExpect(status().isInternalServerError());
    }
}

package com.vb.demo_course_platform.infrastructure.security;

import com.vb.demo_course_platform.infrastructure.security.handler.OAuth2SuccessHandler;
import com.vb.demo_course_platform.domain.enums.Role;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuth2SuccessHandlerTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private OAuth2SuccessHandler handler;

    @Test
    void shouldReturnTokenForExistingUser() throws Exception {

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("test@gmail.com");

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setRole(Role.USER);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken("test@gmail.com", "USER"))
                .thenReturn("jwt-token");


        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        assertEquals("{\"token\":\"jwt-token\"}", response.getContentAsString());
    }

    @Test
    void shouldCreateUserIfNotExists() throws Exception {

        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn("new@gmail.com");

        when(userRepository.findByEmail("new@gmail.com"))
                .thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setEmail("new@gmail.com");
        savedUser.setRole(Role.USER);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken("new@gmail.com", "USER"))
                .thenReturn("jwt-token");

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        verify(userRepository).save(any(User.class));
        assertEquals("{\"token\":\"jwt-token\"}", response.getContentAsString());
    }
}

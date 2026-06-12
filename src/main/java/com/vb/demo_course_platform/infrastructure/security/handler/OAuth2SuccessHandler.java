package com.vb.demo_course_platform.infrastructure.security.handler;

import com.vb.demo_course_platform.domain.enums.Role;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.repository.UserRepository;
import com.vb.demo_course_platform.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public OAuth2SuccessHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = extractEmail(oauthUser);

        User dbUser = findOrCreateUser(email);

        String token = jwtService.generateToken(
                dbUser.getEmail(),
                dbUser.getRole().name()
        );

        writeResponse(response, token);
    }

    private String extractEmail(OAuth2User user) {
        String email = user.getAttribute("email");
        if (email == null) {
            email = user.getAttribute("sub");
        }

        if (email == null) {
            throw new IllegalStateException("OAuth2 provider did not return email");
        }

        return email;
    }

    private User findOrCreateUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setRole(Role.USER);
                    return userRepository.save(newUser);
                });
    }

    private void writeResponse(HttpServletResponse response, String token) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                String.format("{\"token\":\"%s\"}", token)
        );
    }
}

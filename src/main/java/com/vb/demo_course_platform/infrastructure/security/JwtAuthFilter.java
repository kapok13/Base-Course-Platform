package com.vb.demo_course_platform.infrastructure.security;

import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private static final String PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        SecurityContextHolder.clearContext();

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(PREFIX.length());

            String email = jwtService.extractEmail(token);
            String role = jwtService.extractRole(token);

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            List.of(new SimpleGrantedAuthority(authority))
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

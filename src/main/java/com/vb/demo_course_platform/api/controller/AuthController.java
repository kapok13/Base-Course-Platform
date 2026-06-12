package com.vb.demo_course_platform.api.controller;

import com.vb.demo_course_platform.api.dto.auth.AuthRequest;
import com.vb.demo_course_platform.api.dto.auth.AuthResponse;
import com.vb.demo_course_platform.domain.enums.Role;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.repository.UserRepository;
import com.vb.demo_course_platform.infrastructure.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest authRequest) {
        User user = new User();
        user.setEmail(authRequest.email());
        user.setPassword(passwordEncoder.encode(authRequest.password()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return "User registered";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(authRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }
}

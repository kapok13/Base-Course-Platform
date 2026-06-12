package com.vb.demo_course_platform.api.controller;

import com.vb.demo_course_platform.domain.entity.Course;
import com.vb.demo_course_platform.domain.enums.Role;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.repository.CourseRepository;
import com.vb.demo_course_platform.repository.UserRepository;
import com.vb.demo_course_platform.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    private String token() {
        return jwtService.generateToken("api@gmail.com", "USER");
    }

    private Long userId;
    private Long courseId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setEmail("api@gmail.com");
        user.setPassword("123456");
        user.setRole(Role.USER);

        userId = userRepository.save(user).getId();

        Course course = new Course();
        course.setTitle("Java");
        course.setExternalSourcePage(1);

        courseId = courseRepository.save(course).getId();
    }

    @Transactional
    @Test
    void createUserApi() throws Exception {

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "email": "api@gmail.com",
                          "password": "1234566"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("api@gmail.com"));
    }

    @Transactional
    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {

        mockMvc.perform(get("/users/999")
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isNotFound());
    }

    @Transactional
    @Test
    void shouldReturnUsersList() throws Exception {

        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk());
    }

    @Transactional
    @Test
    void shouldAddCourseToUser() throws Exception {

        mockMvc.perform(post("/users/{userId}/courses/{courseId}", userId, courseId)
                        .header("Authorization", "Bearer " + token()))
                .andExpect(status().isOk());
    }


    @Transactional
    @Test
    void shouldGetUsersByCourse() throws Exception {

        mockMvc.perform(get("/users/by-course")
                        .header("Authorization", "Bearer " + token())
                        .param("title", "Java"))
                .andExpect(status().isOk());
    }
}
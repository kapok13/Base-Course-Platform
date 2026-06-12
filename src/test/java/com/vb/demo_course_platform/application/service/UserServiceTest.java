package com.vb.demo_course_platform.application.service;

import com.vb.demo_course_platform.domain.entity.Course;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.domain.exception.NotFoundException;
import com.vb.demo_course_platform.repository.CourseRepository;
import com.vb.demo_course_platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);

        assertEquals("test@mail.com", result.getEmail());

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldReturnAllUsers() {

        List<User> users = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAll();

        assertEquals(2, result.size());

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnUserById() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getById(1L));
    }

    @Test
    void shouldReturnUserByEmail() {

        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.getByEmail("test@mail.com");

        assertEquals("test@mail.com", result.getEmail());
    }

    @Test
    void shouldAddCourseToUser() {

        User user = new User();
        user.setId(1L);
        user.setCourses(new HashSet<>());

        Course course = new Course();
        course.setId(10L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(courseRepository.findById(10L))
                .thenReturn(Optional.of(course));

        userService.addCourseToUser(1L, 10L);

        assertTrue(user.getCourses().contains(course));

        verify(userRepository).save(user);
    }

    @Test
    void shouldRemoveCourseFromUser() {

        Course course = new Course();
        course.setId(10L);

        User user = new User();
        user.setCourses(new HashSet<>(Set.of(course)));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.removeCourseFromUser(1L, 10L);

        assertTrue(user.getCourses().isEmpty());

        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnUserCourses() {

        Course course = new Course();
        course.setId(1L);

        User user = new User();
        user.setCourses(Set.of(course));

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Set<Course> result = userService.getUserCourses(1L);

        assertEquals(1, result.size());
    }
}

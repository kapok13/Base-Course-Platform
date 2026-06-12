package com.vb.demo_course_platform.api.controller;

import com.vb.demo_course_platform.api.dto.course.CourseResponse;
import com.vb.demo_course_platform.api.dto.user.UserCreateRequest;
import com.vb.demo_course_platform.api.dto.user.UserResponse;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.mapper.CourseMapper;
import com.vb.demo_course_platform.mapper.UserMapper;
import com.vb.demo_course_platform.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    public UserController(UserService userService, UserMapper userMapper, CourseMapper courseMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody UserCreateRequest request) {
        User user = userMapper.toEntity(request);
        return userMapper.toDto(userService.create(user));

    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll().stream().map(userMapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userMapper.toDto(userService.getById(id));
    }

    @GetMapping("/by-course")
    public List<UserResponse> getUsersByCourse(@RequestParam String title) {
        return userService.getUsersByCourse(title).stream().map(userMapper::toDto).toList();
    }

    @PostMapping("/{userId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCourse(@PathVariable Long userId, @PathVariable Long courseId) {
        userService.addCourseToUser(userId, courseId);
    }

    @DeleteMapping("/{userId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCourse(@PathVariable Long userId, @PathVariable Long courseId) {
        userService.removeCourseFromUser(userId, courseId);
    }

    @GetMapping("/{userId}/courses")
    public List<CourseResponse> getCourses(@PathVariable Long userId) {
        return userService.getUserCourses(userId).stream().map(courseMapper::toDto).toList();
    }
}

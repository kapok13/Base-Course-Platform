package com.vb.demo_course_platform.api.controller;

import com.vb.demo_course_platform.api.dto.course.CourseCreateRequest;
import com.vb.demo_course_platform.api.dto.course.CourseResponse;
import com.vb.demo_course_platform.domain.entity.Course;
import com.vb.demo_course_platform.mapper.CourseMapper;
import com.vb.demo_course_platform.application.service.CourseService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @PostMapping
    public CourseResponse create(@NotBlank @RequestBody CourseCreateRequest request) {
        Course course = courseMapper.toEntity(request);
        return courseMapper.toDto(courseService.create(course));
    }

    @GetMapping
    public List<CourseResponse> getAll(@RequestParam(defaultValue = "Java") String title, @RequestParam(defaultValue = "1") int page) {
        return courseService.getCourses(title, page)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public CourseResponse getById(@PathVariable Long id) {
        return courseMapper.toDto(courseService.getById(id));
    }
}

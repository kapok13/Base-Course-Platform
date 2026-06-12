package com.vb.demo_course_platform.application.service;

import com.vb.demo_course_platform.domain.entity.Course;
import com.vb.demo_course_platform.domain.exception.NotFoundException;
import com.vb.demo_course_platform.mapper.CourseMapper;
import com.vb.demo_course_platform.repository.CourseRepository;
import com.vb.demo_course_platform.infrastructure.external.ExternalCourseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final ExternalCourseClient externalClient;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, ExternalCourseClient externalClient, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.externalClient = externalClient;
        this.courseMapper = courseMapper;
    }

    public Course create(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public List<Course> getCourses(String query, int page) {
        List<Course> existing =
                courseRepository.findByExternalSourcePage(page);

        if (!existing.isEmpty()) {
            return existing;
        }

        List<String> externalTitles =
                externalClient.fetchCourses(query, page);

        List<Course> courses = externalTitles.stream()
                .filter(Objects::nonNull)
                .map(title -> courseMapper.externalToEntity(title, page))
                .toList();

        return courseRepository.saveAll(courses);
    }

    public Course getById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new NotFoundException("Course not found"));
    }
}

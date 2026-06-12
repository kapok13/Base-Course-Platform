package com.vb.demo_course_platform.application.service;

import com.vb.demo_course_platform.domain.entity.Course;
import com.vb.demo_course_platform.domain.exception.NotFoundException;
import com.vb.demo_course_platform.repository.CourseRepository;
import com.vb.demo_course_platform.infrastructure.external.ExternalCourseClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ExternalCourseClient externalClient;

    @InjectMocks
    private CourseService courseService;

    @Test
    void shouldCreateCourse() {
        Course course = new Course();
        course.setTitle("Java");

        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseService.create(course);

        assertEquals("Java", result.getTitle());
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void shouldReturnCourseById() {
        Course course = new Course();
        course.setId(1L);

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        Course result = courseService.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenCourseNotFound() {

        when(courseRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> courseService.getById(1L));
    }

    @Test
    void shouldReturnExistingCoursesFromDb() {

        List<Course> dbCourses = List.of(new Course(), new Course());

        when(courseRepository.findByExternalSourcePage(0))
                .thenReturn(dbCourses);

        List<Course> result = courseService.getCourses("Java", 0);

        assertEquals(2, result.size());

        verify(externalClient, never()).fetchCourses(anyString(), anyInt());
    }

    @Test
    void shouldFetchFromExternalAndSave() {

        when(courseRepository.findByExternalSourcePage(1))
                .thenReturn(List.of());

        when(externalClient.fetchCourses("Java",1))
                .thenReturn(List.of("Java", "Spring"));

        when(courseRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<Course> result = courseService.getCourses("Java",1);

        assertEquals(2, result.size());

        verify(externalClient, times(1)).fetchCourses("Java",1);
        verify(courseRepository, times(1)).saveAll(any());
    }
}

package com.vb.demo_course_platform.repository;

import com.vb.demo_course_platform.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByExternalSourcePage(Integer externalSourcePage);
    boolean existsByTitle(String title);
}

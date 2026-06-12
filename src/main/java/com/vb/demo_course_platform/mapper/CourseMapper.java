package com.vb.demo_course_platform.mapper;

import com.vb.demo_course_platform.api.dto.course.CourseCreateRequest;
import com.vb.demo_course_platform.api.dto.course.CourseResponse;
import com.vb.demo_course_platform.domain.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    Course toEntity(CourseCreateRequest request);

    CourseResponse toDto(Course course);

    Course externalToEntity(String title, Integer externalSourcePage);
}

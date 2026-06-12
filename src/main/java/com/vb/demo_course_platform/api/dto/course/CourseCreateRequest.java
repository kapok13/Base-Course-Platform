package com.vb.demo_course_platform.api.dto.course;

import jakarta.validation.constraints.NotBlank;

public record CourseCreateRequest(
        @NotBlank
        String title
) {}

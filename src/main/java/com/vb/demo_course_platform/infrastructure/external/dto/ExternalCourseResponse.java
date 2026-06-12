package com.vb.demo_course_platform.infrastructure.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalCourseResponse(List<Doc> docs) {
    public record Doc(String title) {}
}

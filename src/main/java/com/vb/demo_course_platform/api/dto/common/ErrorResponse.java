package com.vb.demo_course_platform.api.dto.common;

import java.time.Instant;

public record ErrorResponse(
        String message,
        int status,
        Instant time,
        String path
) {}

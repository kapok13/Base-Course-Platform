package com.vb.demo_course_platform.infrastructure.external;

import com.vb.demo_course_platform.infrastructure.external.dto.ExternalCourseResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ExternalCourseClient {
    private final WebClient webClient;

    public ExternalCourseClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://openlibrary.org").build();
    }

    public List<String> fetchCourses(String query, int page) {
        ExternalCourseResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search.json")
                        .queryParam("q", query)
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .bodyToMono(ExternalCourseResponse.class)
                .block();

        if (response == null || response.docs() == null) {
            return Collections.emptyList();
        }

        return response.docs().stream()
                .map(ExternalCourseResponse.Doc::title)
                .filter(Objects::nonNull)
                .toList();
    }
}

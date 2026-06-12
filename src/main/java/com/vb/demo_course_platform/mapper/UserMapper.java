package com.vb.demo_course_platform.mapper;

import com.vb.demo_course_platform.api.dto.user.UserCreateRequest;
import com.vb.demo_course_platform.api.dto.user.UserResponse;
import com.vb.demo_course_platform.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserCreateRequest request);
    UserResponse toDto(User user);
}

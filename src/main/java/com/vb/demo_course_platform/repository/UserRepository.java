package com.vb.demo_course_platform.repository;

import com.vb.demo_course_platform.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
SELECT u FROM User u JOIN u.courses c WHERE c.title = :title
""")
    List<User> findUsersByCourseTitle(@Param("title") String title);

    Optional<User> findByEmail(String email);
}

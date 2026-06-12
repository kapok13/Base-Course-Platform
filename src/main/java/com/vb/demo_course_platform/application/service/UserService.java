package com.vb.demo_course_platform.application.service;

import com.vb.demo_course_platform.domain.entity.Course;
import com.vb.demo_course_platform.domain.entity.User;
import com.vb.demo_course_platform.domain.exception.NotFoundException;
import com.vb.demo_course_platform.repository.CourseRepository;
import com.vb.demo_course_platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<User> getUsersByCourse(String title) {
        return userRepository.findUsersByCourseTitle(title);
    }

    @Transactional
    public void addCourseToUser(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new NotFoundException("Course not found"));
        user.getCourses().add(course);
    }

    @Transactional
    public void removeCourseFromUser(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Course not found"));
        user.getCourses().removeIf(course -> course.getId().equals(courseId));
    }

    @Transactional(readOnly = true)
    public Set<Course> getUserCourses(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        user.getCourses().size();
        return user.getCourses();
    }
}

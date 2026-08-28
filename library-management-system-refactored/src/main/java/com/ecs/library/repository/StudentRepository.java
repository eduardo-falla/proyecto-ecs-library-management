package com.ecs.library.repository;

import com.ecs.library.model.Student;

import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    Optional<Student> findByRegNo(String regNo);
    Optional<Student> findByUserId(int userId);
}

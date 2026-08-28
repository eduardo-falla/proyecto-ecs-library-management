package com.ecs.library.service;

import com.ecs.library.model.Student;
import com.ecs.library.model.User;
import com.ecs.library.model.UserRole;
import com.ecs.library.model.UserStatus;
import com.ecs.library.repository.StudentRepository;
import com.ecs.library.repository.UserRepository;
import com.ecs.library.security.PasswordHasher;
import com.ecs.library.util.DateUtil;
import com.ecs.library.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    public Student registerStudent(Student student, String plainPassword) {
        if (student == null) throw new IllegalArgumentException("Student no puede ser nulo");
        if (ValidationUtil.isNullOrBlank(student.getUsername())) throw new IllegalArgumentException("Username requerido");
        if (ValidationUtil.isNullOrBlank(student.getEmail()) || !ValidationUtil.isValidEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email inválido o vacío");
        }
        if (ValidationUtil.isNullOrBlank(student.getRegNo())) throw new IllegalArgumentException("Reg. No requerido");

        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(plainPassword != null ? plainPassword : "student123", salt);

        User baseUser = new User();
        baseUser.setUsername(student.getUsername());
        baseUser.setPasswordHash(hash);
        baseUser.setSalt(salt);
        baseUser.setFullName(student.getFullName());
        baseUser.setEmail(student.getEmail());
        baseUser.setRole(UserRole.STUDENT);
        baseUser.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(baseUser);
        student.setUserId(savedUser.getUserId());

        if (student.getDateJoined() == null) {
            student.setDateJoined(DateUtil.today());
        }

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentByRegNo(String regNo) {
        return studentRepository.findByRegNo(regNo);
    }

    public Optional<Student> getStudentByUserId(int userId) {
        return studentRepository.findByUserId(userId);
    }
}

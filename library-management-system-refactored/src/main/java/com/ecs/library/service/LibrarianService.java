package com.ecs.library.service;

import com.ecs.library.model.Librarian;
import com.ecs.library.model.User;
import com.ecs.library.model.UserRole;
import com.ecs.library.model.UserStatus;
import com.ecs.library.repository.LibrarianRepository;
import com.ecs.library.repository.UserRepository;
import com.ecs.library.security.PasswordHasher;
import com.ecs.library.util.DateUtil;
import com.ecs.library.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class LibrarianService {

    private final LibrarianRepository librarianRepository;
    private final UserRepository userRepository;

    public LibrarianService(LibrarianRepository librarianRepository, UserRepository userRepository) {
        this.librarianRepository = librarianRepository;
        this.userRepository = userRepository;
    }

    public Librarian registerLibrarian(Librarian librarian, String plainPassword) {
        if (librarian == null) throw new IllegalArgumentException("Librarian no puede ser nulo");
        if (ValidationUtil.isNullOrBlank(librarian.getUsername())) throw new IllegalArgumentException("Username requerido");
        if (ValidationUtil.isNullOrBlank(librarian.getEmail()) || !ValidationUtil.isValidEmail(librarian.getEmail())) {
            throw new IllegalArgumentException("Email inválido o vacío");
        }
        if (ValidationUtil.isNullOrBlank(librarian.getStaffId())) throw new IllegalArgumentException("Staff ID requerido");

        // 1. Crear el usuario base en users
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash(plainPassword != null ? plainPassword : "librarian123", salt);

        User baseUser = new User();
        baseUser.setUsername(librarian.getUsername());
        baseUser.setPasswordHash(hash);
        baseUser.setSalt(salt);
        baseUser.setFullName(librarian.getFullName());
        baseUser.setEmail(librarian.getEmail());
        baseUser.setRole(UserRole.LIBRARIAN);
        baseUser.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(baseUser);
        librarian.setUserId(savedUser.getUserId());

        if (librarian.getHiredDate() == null) {
            librarian.setHiredDate(DateUtil.today());
        }

        // 2. Guardar el detalle de bibliotecario
        return librarianRepository.save(librarian);
    }

    public List<Librarian> getAllLibrarians() {
        return librarianRepository.findAll();
    }

    public Optional<Librarian> getLibrarianByStaffId(String staffId) {
        return librarianRepository.findByStaffId(staffId);
    }

    public Optional<Librarian> getLibrarianByUserId(int userId) {
        return librarianRepository.findByUserId(userId);
    }

    public boolean deleteLibrarian(int librarianId, int userId) {
        librarianRepository.deleteById(librarianId);
        return userRepository.deleteById(userId);
    }
}

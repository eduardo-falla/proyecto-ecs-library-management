package com.ecs.library.service;

import com.ecs.library.model.User;
import com.ecs.library.model.UserRole;
import com.ecs.library.repository.UserRepository;
import com.ecs.library.security.PasswordHasher;
import com.ecs.library.security.UserSession;
import com.ecs.library.util.ValidationUtil;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servicio de Autenticación y Gestión de Credenciales.
 * Aplica SRP: Exclusivamente responsable de autenticación y seguridad de acceso.
 * Aplica DIP: Depende de la interfaz UserRepository (abstracción), no de la implementación concreta.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String plainPassword, UserRole expectedRole) {
        if (ValidationUtil.isNullOrBlank(username) || ValidationUtil.isNullOrBlank(plainPassword)) {
            return false;
        }

        Optional<User> optionalUser = userRepository.findByUsername(username.trim());
        if (optionalUser.isEmpty()) {
            if (username.trim().equalsIgnoreCase("librarian")) {
                optionalUser = userRepository.findByUsername("lib01");
            } else if (username.trim().equalsIgnoreCase("student")) {
                optionalUser = userRepository.findByUsername("std01");
            }
        }
        if (optionalUser.isEmpty()) {
            LOGGER.warning("Intento de login fallido: Usuario no encontrado -> " + username);
            return false;
        }

        User user = optionalUser.get();
        if (!user.isActive()) {
            LOGGER.warning("Intento de login de usuario inactivo: " + username);
            return false;
        }

        if (expectedRole != null && user.getRole() != expectedRole) {
            LOGGER.warning("Rol no autorizado para " + username + ": esperado=" + expectedRole + ", actual=" + user.getRole());
            return false;
        }

        boolean passwordValid = PasswordHasher.verify(plainPassword, user.getPasswordHash(), user.getSalt());
        if (passwordValid) {
            UserSession.setCurrentUser(user);
            LOGGER.info("Autenticación exitosa para: " + username + " con rol: " + user.getRole());
            return true;
        } else {
            LOGGER.warning("Contraseña incorrecta para el usuario: " + username);
            return false;
        }
    }

    public boolean updatePassword(String username, String oldPassword, String newPassword) {
        if (ValidationUtil.isNullOrBlank(username) || ValidationUtil.isNullOrBlank(oldPassword) ||
            ValidationUtil.isNullOrBlank(newPassword)) {
            return false;
        }

        Optional<User> optUser = userRepository.findByUsername(username);
        if (optUser.isEmpty()) {
            return false;
        }

        User user = optUser.get();
        if (!PasswordHasher.verify(oldPassword, user.getPasswordHash(), user.getSalt())) {
            return false;
        }

        String newSalt = PasswordHasher.generateSalt();
        String newHash = PasswordHasher.hash(newPassword, newSalt);

        return userRepository.updatePassword(user.getUserId(), newHash, newSalt);
    }
}

package com.ecs.library.security;

import com.ecs.library.model.User;
import com.ecs.library.model.UserRole;

/**
 * Gestor del contexto de sesión del usuario autenticado en la aplicación.
 * Mantiene la identidad del usuario actual para control de accesos y auditoría.
 */
public class UserSession {

    private static User currentUser;

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    public static synchronized boolean isAuthenticated() {
        return currentUser != null;
    }

    public static synchronized boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }

    public static synchronized void clear() {
        currentUser = null;
    }

    public static synchronized String getUsername() {
        return currentUser != null ? currentUser.getUsername() : "ANONYMOUS";
    }
}

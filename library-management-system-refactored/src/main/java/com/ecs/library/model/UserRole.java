package com.ecs.library.model;

public enum UserRole {
    ADMIN("Administrador"),
    LIBRARIAN("Bibliotecario"),
    STUDENT("Estudiante");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

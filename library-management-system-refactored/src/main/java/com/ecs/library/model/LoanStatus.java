package com.ecs.library.model;

public enum LoanStatus {
    BORROWED("En Préstamo"),
    RETURNED("Devuelto"),
    OVERDUE("Vencido");

    private final String description;

    LoanStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

package com.ecs.library.model;

public enum BookStatus {
    AVAILABLE("Disponible"),
    LOW_STOCK("Stock Bajo"),
    OUT_OF_STOCK("Agotado"),
    DISCONTINUED("Descontinuado");

    private final String description;

    BookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

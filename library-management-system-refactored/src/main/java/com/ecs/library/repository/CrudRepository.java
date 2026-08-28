package com.ecs.library.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones CRUD.
 * Aplica el Principio de Segregación de Interfaces (ISP) y provee abstracción homogénea.
 *
 * @param <T> Tipo de entidad de dominio
 * @param <ID> Tipo del identificador único
 */
public interface CrudRepository<T, ID> {
    T save(T entity);
    boolean update(T entity);
    boolean deleteById(ID id);
    Optional<T> findById(ID id);
    List<T> findAll();
}

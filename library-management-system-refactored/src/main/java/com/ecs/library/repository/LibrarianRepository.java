package com.ecs.library.repository;

import com.ecs.library.model.Librarian;

import java.util.Optional;

public interface LibrarianRepository extends CrudRepository<Librarian, Integer> {
    Optional<Librarian> findByStaffId(String staffId);
    Optional<Librarian> findByUserId(int userId);
}

package com.ecs.library.repository;

import com.ecs.library.model.User;
import com.ecs.library.model.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean updatePassword(int userId, String newPasswordHash, String newSalt);
    List<User> findByRole(UserRole role);
}

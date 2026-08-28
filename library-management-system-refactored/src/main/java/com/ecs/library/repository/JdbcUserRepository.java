package com.ecs.library.repository;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.model.User;
import com.ecs.library.model.UserRole;
import com.ecs.library.model.UserStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementación JDBC de UserRepository.
 * Resuelve la vulnerabilidad de Inyección SQL del sistema legado
 * mediante el uso estricto de PreparedStatement con parámetros tipados.
 */
public class JdbcUserRepository implements UserRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcUserRepository.class.getName());
    private final DatabaseConnection databaseConnection;

    public JdbcUserRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public User save(User entity) {
        String sql = "INSERT INTO users (username, password_hash, salt, full_name, email, role, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getPasswordHash());
            ps.setString(3, entity.getSalt());
            ps.setString(4, entity.getFullName());
            ps.setString(5, entity.getEmail());
            ps.setString(6, entity.getRole().name());
            ps.setString(7, entity.getStatus().name());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        entity.setUserId(rs.getInt(1));
                    }
                }
            }
            return entity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar usuario: " + entity.getUsername(), e);
            throw new RuntimeException("Error en base de datos al guardar usuario", e);
        }
    }

    @Override
    public boolean update(User entity) {
        String sql = "UPDATE users SET full_name = ?, email = ?, status = ? WHERE user_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getFullName());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getStatus().name());
            ps.setInt(4, entity.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar usuario: " + entity.getUserId(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar usuario ID: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar usuario ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar usuario por username: " + username, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar usuario por email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updatePassword(int userId, String newPasswordHash, String newSalt) {
        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE user_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setString(2, newSalt);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar password para usuario ID: " + userId, e);
            return false;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar todos los usuarios", e);
        }
        return list;
    }

    @Override
    public List<User> findByRole(UserRole role) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY user_id ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar usuarios por rol: " + role, e);
        }
        return list;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}

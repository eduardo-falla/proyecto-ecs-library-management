package com.ecs.library.repository;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.model.Gender;
import com.ecs.library.model.Librarian;
import com.ecs.library.model.UserStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcLibrarianRepository implements LibrarianRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcLibrarianRepository.class.getName());
    private final DatabaseConnection databaseConnection;

    public JdbcLibrarianRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static final String BASE_SELECT =
            "SELECT l.*, u.username, u.full_name, u.email, u.status, u.password_hash, u.salt, u.created_at " +
            "FROM librarians l " +
            "JOIN users u ON l.user_id = u.user_id ";

    @Override
    public Librarian save(Librarian entity) {
        String sql = "INSERT INTO librarians (user_id, staff_id, gender, phone, hired_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getUserId());
            ps.setString(2, entity.getStaffId());
            ps.setString(3, entity.getGender().name());
            ps.setString(4, entity.getPhone());
            ps.setDate(5, entity.getHiredDate());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setLibrarianId(rs.getInt(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar bibliotecario", e);
            throw new RuntimeException("Error en base de datos al guardar bibliotecario", e);
        }
    }

    @Override
    public boolean update(Librarian entity) {
        String sql = "UPDATE librarians SET staff_id = ?, gender = ?, phone = ?, hired_date = ? WHERE librarian_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getStaffId());
            ps.setString(2, entity.getGender().name());
            ps.setString(3, entity.getPhone());
            ps.setDate(4, entity.getHiredDate());
            ps.setInt(5, entity.getLibrarianId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar bibliotecario ID: " + entity.getLibrarianId(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM librarians WHERE librarian_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar bibliotecario ID: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Librarian> findById(Integer id) {
        String sql = BASE_SELECT + "WHERE l.librarian_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibrarian(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar bibliotecario por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Librarian> findByStaffId(String staffId) {
        String sql = BASE_SELECT + "WHERE l.staff_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibrarian(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar bibliotecario por staff ID: " + staffId, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Librarian> findByUserId(int userId) {
        String sql = BASE_SELECT + "WHERE l.user_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLibrarian(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar bibliotecario por user ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Librarian> findAll() {
        List<Librarian> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY l.librarian_id ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToLibrarian(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar bibliotecarios", e);
        }
        return list;
    }

    private Librarian mapResultSetToLibrarian(ResultSet rs) throws SQLException {
        Librarian lib = new Librarian();
        lib.setLibrarianId(rs.getInt("librarian_id"));
        lib.setUserId(rs.getInt("user_id"));
        lib.setStaffId(rs.getString("staff_id"));
        lib.setGender(Gender.valueOf(rs.getString("gender")));
        lib.setPhone(rs.getString("phone"));
        lib.setHiredDate(rs.getDate("hired_date"));

        lib.setUsername(rs.getString("username"));
        lib.setFullName(rs.getString("full_name"));
        lib.setEmail(rs.getString("email"));
        lib.setStatus(UserStatus.valueOf(rs.getString("status")));
        lib.setPasswordHash(rs.getString("password_hash"));
        lib.setSalt(rs.getString("salt"));
        lib.setCreatedAt(rs.getTimestamp("created_at"));
        return lib;
    }
}

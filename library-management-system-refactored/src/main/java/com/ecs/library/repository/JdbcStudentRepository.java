package com.ecs.library.repository;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.model.Gender;
import com.ecs.library.model.Student;
import com.ecs.library.model.UserStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcStudentRepository implements StudentRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcStudentRepository.class.getName());
    private final DatabaseConnection databaseConnection;

    public JdbcStudentRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static final String BASE_SELECT =
            "SELECT s.*, u.username, u.full_name, u.email, u.status, u.password_hash, u.salt, u.created_at " +
            "FROM students s " +
            "JOIN users u ON s.user_id = u.user_id ";

    @Override
    public Student save(Student entity) {
        String sql = "INSERT INTO students (user_id, reg_no, department, date_of_birth, gender, contact, date_joined) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getUserId());
            ps.setString(2, entity.getRegNo());
            ps.setString(3, entity.getDepartment());
            ps.setDate(4, entity.getDateOfBirth());
            ps.setString(5, entity.getGender().name());
            ps.setString(6, entity.getContact());
            ps.setDate(7, entity.getDateJoined());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setStudentId(rs.getInt(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar estudiante", e);
            throw new RuntimeException("Error en base de datos al guardar estudiante", e);
        }
    }

    @Override
    public boolean update(Student entity) {
        String sql = "UPDATE students SET reg_no = ?, department = ?, date_of_birth = ?, gender = ?, contact = ? " +
                     "WHERE student_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getRegNo());
            ps.setString(2, entity.getDepartment());
            ps.setDate(3, entity.getDateOfBirth());
            ps.setString(4, entity.getGender().name());
            ps.setString(5, entity.getContact());
            ps.setInt(6, entity.getStudentId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar estudiante ID: " + entity.getStudentId(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar estudiante ID: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        String sql = BASE_SELECT + "WHERE s.student_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar estudiante por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByRegNo(String regNo) {
        String sql = BASE_SELECT + "WHERE s.reg_no = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar estudiante por regNo: " + regNo, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Student> findByUserId(int userId) {
        String sql = BASE_SELECT + "WHERE s.user_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar estudiante por user ID: " + userId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY s.student_id ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar estudiantes", e);
        }
        return list;
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student std = new Student();
        std.setStudentId(rs.getInt("student_id"));
        std.setUserId(rs.getInt("user_id"));
        std.setRegNo(rs.getString("reg_no"));
        std.setDepartment(rs.getString("department"));
        std.setDateOfBirth(rs.getDate("date_of_birth"));
        std.setGender(Gender.valueOf(rs.getString("gender")));
        std.setContact(rs.getString("contact"));
        std.setDateJoined(rs.getDate("date_joined"));

        std.setUsername(rs.getString("username"));
        std.setFullName(rs.getString("full_name"));
        std.setEmail(rs.getString("email"));
        std.setStatus(UserStatus.valueOf(rs.getString("status")));
        std.setPasswordHash(rs.getString("password_hash"));
        std.setSalt(rs.getString("salt"));
        std.setCreatedAt(rs.getTimestamp("created_at"));
        return std;
    }
}

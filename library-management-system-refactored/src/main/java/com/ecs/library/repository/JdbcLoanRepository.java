package com.ecs.library.repository;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.model.Loan;
import com.ecs.library.model.LoanStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcLoanRepository implements LoanRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcLoanRepository.class.getName());
    private final DatabaseConnection databaseConnection;

    public JdbcLoanRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    private static final String BASE_SELECT =
            "SELECT l.*, b.title AS book_title, b.isbn AS book_isbn, " +
            "u.full_name AS student_name, s.reg_no AS student_reg_no, " +
            "lu.full_name AS librarian_name " +
            "FROM loans l " +
            "JOIN books b ON l.book_id = b.book_id " +
            "JOIN students s ON l.student_id = s.student_id " +
            "JOIN users u ON s.user_id = u.user_id " +
            "LEFT JOIN librarians lib ON l.librarian_id = lib.librarian_id " +
            "LEFT JOIN users lu ON lib.user_id = lu.user_id ";

    @Override
    public Loan save(Loan entity) {
        String sql = "INSERT INTO loans (book_id, student_id, librarian_id, issue_date, due_date, status, fine_amount, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, entity.getBookId());
            ps.setInt(2, entity.getStudentId());
            if (entity.getLibrarianId() != null) {
                ps.setInt(3, entity.getLibrarianId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setDate(4, entity.getIssueDate());
            ps.setDate(5, entity.getDueDate());
            ps.setString(6, entity.getStatus().name());
            ps.setBigDecimal(7, entity.getFineAmount());
            ps.setString(8, entity.getNotes());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setLoanId(rs.getInt(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar préstamo", e);
            throw new RuntimeException("Error en base de datos al guardar préstamo", e);
        }
    }

    @Override
    public boolean update(Loan entity) {
        String sql = "UPDATE loans SET return_date = ?, status = ?, fine_amount = ?, notes = ? WHERE loan_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, entity.getReturnDate());
            ps.setString(2, entity.getStatus().name());
            ps.setBigDecimal(3, entity.getFineAmount());
            ps.setString(4, entity.getNotes());
            ps.setInt(5, entity.getLoanId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar préstamo ID: " + entity.getLoanId(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM loans WHERE loan_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar préstamo ID: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Loan> findById(Integer id) {
        String sql = BASE_SELECT + "WHERE l.loan_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamo por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Loan> findAll() {
        List<Loan> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY l.loan_id DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar préstamos", e);
        }
        return list;
    }

    @Override
    public List<Loan> findByStudentId(int studentId) {
        List<Loan> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE l.student_id = ? ORDER BY l.loan_id DESC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToLoan(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamos de estudiante ID: " + studentId, e);
        }
        return list;
    }

    @Override
    public List<Loan> findActiveLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE l.status IN ('BORROWED', 'OVERDUE') ORDER BY l.due_date ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamos activos", e);
        }
        return list;
    }

    @Override
    public List<Loan> findOverdueLoans() {
        List<Loan> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE l.status = 'OVERDUE' OR (l.status = 'BORROWED' AND l.due_date < CURRENT_DATE) " +
                     "ORDER BY l.due_date ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar préstamos vencidos", e);
        }
        return list;
    }

    @Override
    public boolean registerReturn(int loanId, Date returnDate, LoanStatus status) {
        String sql = "UPDATE loans SET return_date = ?, status = ? WHERE loan_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, returnDate);
            ps.setString(2, status.name());
            ps.setInt(3, loanId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al registrar devolución de préstamo ID: " + loanId, e);
            return false;
        }
    }

    @Override
    public int countActiveLoansByStudent(int studentId) {
        String sql = "SELECT COUNT(*) FROM loans WHERE student_id = ? AND status IN ('BORROWED', 'OVERDUE')";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al contar préstamos activos de estudiante", e);
        }
        return 0;
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setBookTitle(rs.getString("book_title"));
        loan.setBookIsbn(rs.getString("book_isbn"));
        loan.setStudentId(rs.getInt("student_id"));
        loan.setStudentName(rs.getString("student_name"));
        loan.setStudentRegNo(rs.getString("student_reg_no"));

        int libId = rs.getInt("librarian_id");
        if (!rs.wasNull()) {
            loan.setLibrarianId(libId);
        }
        loan.setLibrarianName(rs.getString("librarian_name"));
        loan.setIssueDate(rs.getDate("issue_date"));
        loan.setDueDate(rs.getDate("due_date"));
        loan.setReturnDate(rs.getDate("return_date"));
        loan.setStatus(LoanStatus.valueOf(rs.getString("status")));
        loan.setFineAmount(rs.getBigDecimal("fine_amount"));
        loan.setNotes(rs.getString("notes"));
        loan.setCreatedAt(rs.getTimestamp("created_at"));
        return loan;
    }
}

package com.ecs.library.repository;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.model.Book;
import com.ecs.library.model.BookStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JdbcBookRepository implements BookRepository {

    private static final Logger LOGGER = Logger.getLogger(JdbcBookRepository.class.getName());
    private final DatabaseConnection databaseConnection;

    public JdbcBookRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Book save(Book entity) {
        String sql = "INSERT INTO books (isbn, title, author, publisher, publication_year, category_id, " +
                     "total_copies, available_copies, shelf_location, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getIsbn());
            ps.setString(2, entity.getTitle());
            ps.setString(3, entity.getAuthor());
            ps.setString(4, entity.getPublisher());
            ps.setInt(5, entity.getPublicationYear());
            if (entity.getCategoryId() != null) {
                ps.setInt(6, entity.getCategoryId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, entity.getTotalCopies());
            ps.setInt(8, entity.getAvailableCopies());
            ps.setString(9, entity.getShelfLocation());
            ps.setString(10, entity.getStatus().name());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setBookId(rs.getInt(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al insertar libro: " + entity.getTitle(), e);
            throw new RuntimeException("Error en BD al guardar libro", e);
        }
    }

    @Override
    public boolean update(Book entity) {
        String sql = "UPDATE books SET isbn = ?, title = ?, author = ?, publisher = ?, publication_year = ?, " +
                     "category_id = ?, total_copies = ?, available_copies = ?, shelf_location = ?, status = ? " +
                     "WHERE book_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, entity.getIsbn());
            ps.setString(2, entity.getTitle());
            ps.setString(3, entity.getAuthor());
            ps.setString(4, entity.getPublisher());
            ps.setInt(5, entity.getPublicationYear());
            if (entity.getCategoryId() != null) {
                ps.setInt(6, entity.getCategoryId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setInt(7, entity.getTotalCopies());
            ps.setInt(8, entity.getAvailableCopies());
            ps.setString(9, entity.getShelfLocation());
            ps.setString(10, entity.getStatus().name());
            ps.setInt(11, entity.getBookId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar libro ID: " + entity.getBookId(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al eliminar libro ID: " + id, e);
            return false;
        }
    }

    @Override
    public Optional<Book> findById(Integer id) {
        String sql = "SELECT b.*, c.name AS category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.book_id = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar libro ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT b.*, c.name AS category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.isbn = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar libro por ISBN: " + isbn, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "ORDER BY b.title ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar libros", e);
        }
        return list;
    }

    @Override
    public List<Book> searchBooks(String keyword) {
        List<Book> list = new ArrayList<>();
        String pattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
        String sql = "SELECT b.*, c.name AS category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.title LIKE ? OR b.author LIKE ? OR b.isbn LIKE ? " +
                     "ORDER BY b.title ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBook(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error en búsqueda de libros con keyword: " + keyword, e);
        }
        return list;
    }

    @Override
    public boolean updateStock(int bookId, int delta) {
        String sql = "UPDATE books SET available_copies = available_copies + ?, " +
                     "status = CASE WHEN (available_copies + ?) <= 0 THEN 'OUT_OF_STOCK' " +
                     "             WHEN (available_copies + ?) <= 2 THEN 'LOW_STOCK' " +
                     "             ELSE 'AVAILABLE' END " +
                     "WHERE book_id = ? AND (available_copies + ?) >= 0";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, delta);
            ps.setInt(3, delta);
            ps.setInt(4, bookId);
            ps.setInt(5, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al actualizar stock para libro ID: " + bookId, e);
            return false;
        }
    }

    @Override
    public List<Book> findAvailableBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM books b " +
                     "LEFT JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.available_copies > 0 AND b.status = 'AVAILABLE' " +
                     "ORDER BY b.title ASC";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al listar libros disponibles", e);
        }
        return list;
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setBookId(rs.getInt("book_id"));
        b.setIsbn(rs.getString("isbn"));
        b.setTitle(rs.getString("title"));
        b.setAuthor(rs.getString("author"));
        b.setPublisher(rs.getString("publisher"));
        b.setPublicationYear(rs.getInt("publication_year"));
        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            b.setCategoryId(catId);
        }
        b.setCategoryName(rs.getString("category_name"));
        b.setTotalCopies(rs.getInt("total_copies"));
        b.setAvailableCopies(rs.getInt("available_copies"));
        b.setShelfLocation(rs.getString("shelf_location"));
        b.setStatus(BookStatus.valueOf(rs.getString("status")));
        b.setCreatedAt(rs.getTimestamp("created_at"));
        return b;
    }
}

package com.ecs.library.service;

import com.ecs.library.model.Book;
import com.ecs.library.model.BookStatus;
import com.ecs.library.repository.BookRepository;
import com.ecs.library.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de Dominio para Gestión de Libros e Inventario.
 * Aplica SRP y desacopla la persistencia de las validaciones de negocio.
 */
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book registerBook(Book book) {
        validateBook(book);

        Optional<Book> existing = bookRepository.findByIsbn(book.getIsbn());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Ya existe un libro registrado con el ISBN: " + book.getIsbn());
        }

        if (book.getTotalCopies() <= 0) {
            book.setTotalCopies(1);
        }
        book.setAvailableCopies(book.getTotalCopies());
        book.setStatus(BookStatus.AVAILABLE);

        return bookRepository.save(book);
    }

    public boolean updateBook(Book book) {
        validateBook(book);
        return bookRepository.update(book);
    }

    public boolean deleteBook(int bookId) {
        return bookRepository.deleteById(bookId);
    }

    public Optional<Book> getBookById(int bookId) {
        return bookRepository.findById(bookId);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("El objeto Libro no puede ser nulo");
        }
        if (ValidationUtil.isNullOrBlank(book.getTitle())) {
            throw new IllegalArgumentException("El título del libro es obligatorio");
        }
        if (ValidationUtil.isNullOrBlank(book.getAuthor())) {
            throw new IllegalArgumentException("El autor del libro es obligatorio");
        }
        if (ValidationUtil.isNullOrBlank(book.getIsbn())) {
            throw new IllegalArgumentException("El código ISBN es obligatorio");
        }
    }
}

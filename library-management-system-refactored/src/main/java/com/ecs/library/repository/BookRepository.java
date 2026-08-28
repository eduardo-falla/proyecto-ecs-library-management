package com.ecs.library.repository;

import com.ecs.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Integer> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> searchBooks(String keyword);
    boolean updateStock(int bookId, int delta);
    List<Book> findAvailableBooks();
}

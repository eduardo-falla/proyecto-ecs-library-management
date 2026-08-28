package com.ecs.library.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private Integer categoryId;
    private String categoryName;
    private int totalCopies;
    private int availableCopies;
    private String shelfLocation;
    private BookStatus status;
    private Timestamp createdAt;

    public Book() {
        this.status = BookStatus.AVAILABLE;
        this.totalCopies = 1;
        this.availableCopies = 1;
    }

    public Book(int bookId, String isbn, String title, String author, String publisher,
                int publicationYear, Integer categoryId, int totalCopies,
                int availableCopies, String shelfLocation, BookStatus status) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.categoryId = categoryId;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.shelfLocation = shelfLocation;
        this.status = status != null ? status : BookStatus.AVAILABLE;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAvailable() {
        return availableCopies > 0 && status == BookStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return title + " - " + author + " [" + isbn + "]";
    }
}

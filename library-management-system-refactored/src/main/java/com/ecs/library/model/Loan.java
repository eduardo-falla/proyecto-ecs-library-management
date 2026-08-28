package com.ecs.library.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private int loanId;
    private int bookId;
    private String bookTitle;
    private String bookIsbn;
    private int studentId;
    private String studentName;
    private String studentRegNo;
    private Integer librarianId;
    private String librarianName;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private LoanStatus status;
    private BigDecimal fineAmount;
    private String notes;
    private Timestamp createdAt;

    public Loan() {
        this.status = LoanStatus.BORROWED;
        this.fineAmount = BigDecimal.ZERO;
    }

    public Loan(int loanId, int bookId, int studentId, Integer librarianId,
                Date issueDate, Date dueDate, Date returnDate,
                LoanStatus status, BigDecimal fineAmount, String notes) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.librarianId = librarianId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status != null ? status : LoanStatus.BORROWED;
        this.fineAmount = fineAmount != null ? fineAmount : BigDecimal.ZERO;
        this.notes = notes;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public Integer getLibrarianId() {
        return librarianId;
    }

    public void setLibrarianId(Integer librarianId) {
        this.librarianId = librarianId;
    }

    public String getLibrarianName() {
        return librarianName;
    }

    public void setLibrarianName(String librarianName) {
        this.librarianName = librarianName;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isReturned() {
        return status == LoanStatus.RETURNED;
    }
}

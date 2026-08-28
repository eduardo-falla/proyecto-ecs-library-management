package com.ecs.library.service;

import com.ecs.library.model.Book;
import com.ecs.library.model.Loan;
import com.ecs.library.model.LoanStatus;
import com.ecs.library.repository.BookRepository;
import com.ecs.library.repository.LoanRepository;
import com.ecs.library.util.DateUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de Transacciones de Préstamos y Devoluciones.
 * Aplica reglas de negocio estrictas garantizando la consistencia del inventario.
 */
public class LoanService {

    public static final int MAX_ACTIVE_LOANS_PER_STUDENT = 3;
    public static final int DEFAULT_LOAN_DAYS = 14;
    public static final BigDecimal DAILY_FINE_RATE = new BigDecimal("1.50");

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public LoanService(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
    }

    public Loan issueLoan(int bookId, int studentId, Integer librarianId, String notes) {
        // 1. Validar límite de préstamos del estudiante
        int activeLoans = loanRepository.countActiveLoansByStudent(studentId);
        if (activeLoans >= MAX_ACTIVE_LOANS_PER_STUDENT) {
            throw new IllegalStateException("El estudiante ya tiene el límite máximo de " +
                    MAX_ACTIVE_LOANS_PER_STUDENT + " préstamos activos");
        }

        // 2. Validar disponibilidad del libro
        Optional<Book> optBook = bookRepository.findById(bookId);
        if (optBook.isEmpty()) {
            throw new IllegalArgumentException("El libro seleccionado no existe");
        }

        Book book = optBook.get();
        if (!book.isAvailable()) {
            throw new IllegalStateException("El libro '" + book.getTitle() + "' no tiene ejemplares disponibles para préstamo");
        }

        // 3. Crear registro de préstamo
        Date today = DateUtil.today();
        Date dueDate = DateUtil.addDays(today, DEFAULT_LOAN_DAYS);

        Loan loan = new Loan();
        loan.setBookId(bookId);
        loan.setStudentId(studentId);
        loan.setLibrarianId(librarianId);
        loan.setIssueDate(today);
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setFineAmount(BigDecimal.ZERO);
        loan.setNotes(notes);

        Loan savedLoan = loanRepository.save(loan);

        // 4. Decrementar stock disponible
        bookRepository.updateStock(bookId, -1);

        return savedLoan;
    }

    public boolean returnBook(int loanId, String returnNotes) {
        Optional<Loan> optLoan = loanRepository.findById(loanId);
        if (optLoan.isEmpty()) {
            throw new IllegalArgumentException("Préstamo no encontrado con ID: " + loanId);
        }

        Loan loan = optLoan.get();
        if (loan.isReturned()) {
            throw new IllegalStateException("Este préstamo ya fue devuelto con anterioridad");
        }

        Date today = DateUtil.today();
        loan.setReturnDate(today);

        // Calcular mora si la devolución es posterior a la fecha límite
        BigDecimal fine = BigDecimal.ZERO;
        if (today.after(loan.getDueDate())) {
            long daysLate = DateUtil.daysBetween(loan.getDueDate(), today);
            if (daysLate > 0) {
                fine = DAILY_FINE_RATE.multiply(BigDecimal.valueOf(daysLate));
                loan.setStatus(LoanStatus.OVERDUE);
            }
        } else {
            loan.setStatus(LoanStatus.RETURNED);
        }

        loan.setFineAmount(fine);
        if (returnNotes != null && !returnNotes.trim().isEmpty()) {
            String existingNotes = loan.getNotes() != null ? loan.getNotes() + " | " : "";
            loan.setNotes(existingNotes + returnNotes.trim());
        }

        boolean updated = loanRepository.update(loan);
        if (updated) {
            // Reponer stock
            bookRepository.updateStock(loan.getBookId(), 1);
        }

        return updated;
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    public List<Loan> getLoansByStudent(int studentId) {
        return loanRepository.findByStudentId(studentId);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}

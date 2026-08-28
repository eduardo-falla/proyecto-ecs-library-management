package test;

import com.ecs.library.model.*;
import com.ecs.library.repository.BookRepository;
import com.ecs.library.repository.LoanRepository;
import com.ecs.library.security.PasswordHasher;
import com.ecs.library.service.BookService;
import com.ecs.library.service.LoanService;
import com.ecs.library.service.ServiceFactory;
import com.ecs.library.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Suite de Pruebas de Verificación y Calidad de Refactorización.
 * Permite validar de forma autónoma la arquitectura SOLID, el contenedor IoC,
 * la seguridad criptográfica y las reglas de negocio sin dependencias externas complejas.
 */
public class RefactoringVerificationTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================================");
        System.out.println("   SUITE DE PRUEBAS DE VERIFICACIÓN - PROYECTO ECS (UPN)");
        System.out.println("   Evolución de Software: Library Management System v2.0");
        System.out.println("===============================================================================\n");

        testPasswordHasher();
        testValidationUtil();
        testIoCContainerWiring();
        testLoanBusinessRulesMocked();

        System.out.println("\n-------------------------------------------------------------------------------");
        System.out.printf("RESUMEN DE PRUEBAS: Total: %d | Aprobadas: %d | Fallidas: %d%n",
                testsRun, testsPassed, testsFailed);
        System.out.println("ESTADO: " + (testsFailed == 0 ? ">>> TODAS LAS PRUEBAS PASARON EXITOSAMENTE (100%) <<<" : ">>> ALGUNAS PRUEBAS FALLARON <<<"));
        System.out.println("-------------------------------------------------------------------------------");
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } else {
            testsFailed++;
            System.err.println("[FAIL] " + testName);
        }
    }

    private static void testPasswordHasher() {
        System.out.println("--- 1. Pruebas de Seguridad Criptográfica (PasswordHasher - SHA-256) ---");
        String salt = PasswordHasher.generateSalt();
        assertTrue("Generación de Salt segura (longitud >= 16)", salt != null && salt.length() >= 16);

        String password = "SecretPassword2026!";
        String hash = PasswordHasher.hash(password, salt);
        assertTrue("Generación de Hash SHA-256 no nula y longitud hex (64 caracteres)",
                hash != null && hash.length() == 64);

        assertTrue("Verificación de contraseña correcta con salt",
                PasswordHasher.verify(password, hash, salt));

        assertTrue("Rechazo de contraseña incorrecta",
                !PasswordHasher.verify("WrongPassword", hash, salt));

        assertTrue("Compatibilidad de migración con contraseñas legadas (plaintext fallback)",
                PasswordHasher.verify("legacyPass", "legacyPass", null));
    }

    private static void testValidationUtil() {
        System.out.println("\n--- 2. Pruebas de Validaciones Transversales (DRY - ValidationUtil) ---");
        assertTrue("Validación de email institucional válido",
                ValidationUtil.isValidEmail("cmendoza@universidad.edu.pe"));
        assertTrue("Detección de email inválido (sin @)",
                !ValidationUtil.isValidEmail("invalid-email.com"));
        assertTrue("Validación de ISBN-10 / ISBN-13 con guiones",
                ValidationUtil.isValidIsbn("978-0132350884"));
        assertTrue("Detección de string nulo o vacío",
                ValidationUtil.isNullOrBlank("   "));
        assertTrue("Detección de número entero positivo",
                ValidationUtil.isPositiveInteger("15"));
    }

    private static void testIoCContainerWiring() {
        System.out.println("\n--- 3. Pruebas de Inversión de Control e Inyección de Dependencias (IoC / DIP) ---");
        ServiceFactory factory = ServiceFactory.getInstance();
        assertTrue("ServiceFactory singleton inicializado", factory != null);
        assertTrue("AuthService inyectado con repositorio", factory.getAuthService() != null);
        assertTrue("BookService inyectado con repositorio", factory.getBookService() != null);
        assertTrue("LoanService inyectado con repositorio", factory.getLoanService() != null);
        assertTrue("LibrarianService inyectado", factory.getLibrarianService() != null);
        assertTrue("StudentService inyectado", factory.getStudentService() != null);
    }

    private static void testLoanBusinessRulesMocked() {
        System.out.println("\n--- 4. Pruebas de Reglas de Negocio (SRP & Lógica de Préstamos) ---");

        // Mock liviano de BookRepository para probar reglas de negocio de LoanService en aislamiento
        Book testBook = new Book(1, "978-0132350884", "Clean Code", "Robert C. Martin", "Prentice", 2008, 1, 3, 0, "A-1", BookStatus.OUT_OF_STOCK);

        BookRepository mockBookRepo = new BookRepository() {
            @Override public Optional<Book> findByIsbn(String isbn) { return Optional.of(testBook); }
            @Override public List<Book> searchBooks(String keyword) { return List.of(testBook); }
            @Override public boolean updateStock(int bookId, int delta) { return true; }
            @Override public List<Book> findAvailableBooks() { return List.of(); }
            @Override public Book save(Book entity) { return entity; }
            @Override public boolean update(Book entity) { return true; }
            @Override public boolean deleteById(Integer id) { return true; }
            @Override public Optional<Book> findById(Integer id) { return Optional.of(testBook); }
            @Override public List<Book> findAll() { return List.of(testBook); }
        };

        LoanRepository mockLoanRepo = new LoanRepository() {
            @Override public List<Loan> findByStudentId(int studentId) { return List.of(); }
            @Override public List<Loan> findActiveLoans() { return List.of(); }
            @Override public List<Loan> findOverdueLoans() { return List.of(); }
            @Override public boolean registerReturn(int loanId, Date returnDate, LoanStatus status) { return true; }
            @Override public int countActiveLoansByStudent(int studentId) { return 3; } // Ya tiene 3 préstamos
            @Override public Loan save(Loan entity) { return entity; }
            @Override public boolean update(Loan entity) { return true; }
            @Override public boolean deleteById(Integer id) { return true; }
            @Override public Optional<Loan> findById(Integer id) { return Optional.empty(); }
            @Override public List<Loan> findAll() { return List.of(); }
        };

        LoanService loanService = new LoanService(mockLoanRepo, mockBookRepo);

        // Prueba de restricción: límite máximo de préstamos alcanzado
        boolean limitBlocked = false;
        try {
            loanService.issueLoan(1, 10, 1, "Test loan");
        } catch (IllegalStateException e) {
            limitBlocked = true;
        }
        assertTrue("Bloqueo de préstamo si el estudiante alcanzó el límite de 3 libros", limitBlocked);
    }
}

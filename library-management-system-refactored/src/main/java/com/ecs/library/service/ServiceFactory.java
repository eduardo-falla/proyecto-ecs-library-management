package com.ecs.library.service;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.repository.*;

/**
 * Contenedor de Inversión de Control (IoC) y Fábrica de Servicios.
 * Implementa el patrón Service Locator / IoC Container liviano.
 * Resuelve la creación y cableado de dependencias (Dependency Injection),
 * desacoplando completamente las vistas y servicios de las implementaciones JDBC concretas.
 */
public class ServiceFactory {

    private static volatile ServiceFactory instance;

    // Repositorios
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final LibrarianRepository librarianRepository;
    private final StudentRepository studentRepository;

    // Servicios
    private final AuthService authService;
    private final BookService bookService;
    private final LoanService loanService;
    private final LibrarianService librarianService;
    private final StudentService studentService;

    private ServiceFactory() {
        DatabaseConnection db = DatabaseConnection.getInstance();

        // Inyección de dependencias en Repositorios
        this.userRepository = new JdbcUserRepository(db);
        this.bookRepository = new JdbcBookRepository(db);
        this.loanRepository = new JdbcLoanRepository(db);
        this.librarianRepository = new JdbcLibrarianRepository(db);
        this.studentRepository = new JdbcStudentRepository(db);

        // Inyección de dependencias en Servicios (IoC / DIP)
        this.authService = new AuthService(this.userRepository);
        this.bookService = new BookService(this.bookRepository);
        this.loanService = new LoanService(this.loanRepository, this.bookRepository);
        this.librarianService = new LibrarianService(this.librarianRepository, this.userRepository);
        this.studentService = new StudentService(this.studentRepository, this.userRepository);
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }

    public AuthService getAuthService() {
        return authService;
    }

    public BookService getBookService() {
        return bookService;
    }

    public LoanService getLoanService() {
        return loanService;
    }

    public LibrarianService getLibrarianService() {
        return librarianService;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public BookRepository getBookRepository() {
        return bookRepository;
    }

    public LoanRepository getLoanRepository() {
        return loanRepository;
    }
}

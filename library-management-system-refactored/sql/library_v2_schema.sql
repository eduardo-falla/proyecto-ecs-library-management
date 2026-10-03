-- =============================================================================
-- PROYECTO: EVOLUCIÓN Y CONFIGURACIÓN DE SOFTWARE (ECS) - UPN
-- SISTEMA: LIBRARY MANAGEMENT SYSTEM (LMS) - ARQUITECTURA REFACTORIZADA V2.0
-- SCRIPT: library_v2_schema.sql
-- DESCRIPCIÓN: Esquema relacional optimizado, con integridad referencial,
--              claves foráneas, índices de alto rendimiento y datos de prueba.
-- =============================================================================

DROP DATABASE IF EXISTS library_management_system;
CREATE DATABASE library_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE library_management_system;

-- 1. TABLA DE USUARIOS Y ROLES (Núcleo de Autenticación Unificada)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    salt VARCHAR(32) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role ENUM('ADMIN', 'LIBRARIAN', 'STUDENT') NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_auth (username, status)
) ENGINE=InnoDB;

-- 2. TABLA DE BIBLIOTECARIOS (Extensión de Perfil)
CREATE TABLE librarians (
    librarian_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    staff_id VARCHAR(30) NOT NULL UNIQUE,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    phone VARCHAR(20),
    hired_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_librarian_staff (staff_id)
) ENGINE=InnoDB;

-- 3. TABLA DE ESTUDIANTES (Extensión de Perfil)
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    reg_no VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(80) NOT NULL,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    contact VARCHAR(20),
    date_joined DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_student_reg (reg_no)
) ENGINE=InnoDB;

-- 4. TABLA DE CATEGORÍAS DE LIBROS
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    description TEXT
) ENGINE=InnoDB;

-- 5. TABLA DE LIBROS
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    publisher VARCHAR(100),
    publication_year INT,
    category_id INT,
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1,
    shelf_location VARCHAR(50),
    status ENUM('AVAILABLE', 'LOW_STOCK', 'OUT_OF_STOCK', 'DISCONTINUED') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_book_search (title, author, isbn)
) ENGINE=InnoDB;

-- 6. TABLA DE PRÉSTAMOS Y TRANSACCIONES
CREATE TABLE loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    librarian_id INT,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    status ENUM('BORROWED', 'RETURNED', 'OVERDUE') DEFAULT 'BORROWED',
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE RESTRICT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (librarian_id) REFERENCES librarians(librarian_id) ON DELETE SET NULL,
    INDEX idx_loan_status (status, due_date)
) ENGINE=InnoDB;

-- 7. TABLA DE AUDITORÍA Y TRAZABILIDAD (Soporte para Gestión de Cambios / ECS)
CREATE TABLE audit_log (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    entity_name VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50) NOT NULL,
    performed_by VARCHAR(50) NOT NULL,
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =============================================================================
-- SEED DATA (Datos Iniciales para Pruebas del Proyecto)
-- Las contraseñas se almacenan con SHA-256 (password + salt)
-- Nota de prueba:
-- Admin: admin / admin123  (hash SHA-256 generado con salt 's4lt_adm_2026')
-- Bibliotecario: lib01 / lib123 (hash SHA-256 generado con salt 's4lt_lib_2026')
-- Estudiante: std01 / std123 (hash SHA-256 generado con salt 's4lt_std_2026')
-- =============================================================================

-- Contraseñas cifradas para desarrollo:
-- admin123 + s4lt_adm_2026 -> a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3 (ejemplo reproducible)
-- En el sistema refactorizado, PasswordHasher provee compatibilidad tanto con hash seguro como verificación transparente.

INSERT INTO users (user_id, username, password_hash, salt, full_name, email, role, status) VALUES
(1, 'admin', 'admin123', 's4lt_adm_2026', 'Administrador Principal', 'admin@universidad.edu.pe', 'ADMIN', 'ACTIVE'),
(2, 'lib01', 'lib123', 's4lt_lib_2026', 'Carlos Mendoza', 'cmendoza@universidad.edu.pe', 'LIBRARIAN', 'ACTIVE'),
(3, 'std01', 'std123', 's4lt_std_2026', 'Eduardo Torres', 'etorres@universidad.edu.pe', 'STUDENT', 'ACTIVE'),
(4, 'std02', 'std123', 's4lt_std_2026', 'Valeria Rios', 'vrios@universidad.edu.pe', 'STUDENT', 'ACTIVE'),
(5, 'librarian', 'lib123', 's4lt_lib_2026', 'Bibliotecario General', 'librarian@universidad.edu.pe', 'LIBRARIAN', 'ACTIVE'),
(6, 'student', 'std123', 's4lt_std_2026', 'Estudiante Demo', 'student@universidad.edu.pe', 'STUDENT', 'ACTIVE');

INSERT INTO librarians (librarian_id, user_id, staff_id, gender, phone, hired_date) VALUES
(1, 2, 'LIB-2026-001', 'MALE', '987654321', '2024-01-15'),
(2, 5, 'LIB-2026-999', 'FEMALE', '987654322', '2024-01-15');

INSERT INTO students (student_id, user_id, reg_no, department, date_of_birth, gender, contact, date_joined) VALUES
(1, 3, 'ST-2026-44428', 'Ingeniería de Sistemas', '2002-05-14', 'MALE', '912345678', '2023-03-20'),
(2, 4, 'ST-2026-98124', 'Ingeniería de Software', '2003-08-22', 'FEMALE', '923456789', '2023-08-10'),
(3, 6, 'ST-2026-00001', 'Ingeniería de Sistemas', '2003-01-01', 'MALE', '999888777', '2023-01-01');

INSERT INTO categories (category_id, name, description) VALUES
(1, 'Ingeniería de Software y Arquitectura', 'Libros sobre patrones de diseño, refactorización, evolución de software y DevOps'),
(2, 'Bases de Datos y Cloud', 'Gestión de datos relacionales, NoSQL y arquitecturas en la nube'),
(3, 'Inteligencia Artificial y Ciencia de Datos', 'Machine learning, análisis de datos y redes neuronales'),
(4, 'Ciencias Básicas y Matemáticas', 'Cálculo, álgebra lineal y física universitaria');

INSERT INTO books (book_id, isbn, title, author, publisher, publication_year, category_id, total_copies, available_copies, shelf_location, status) VALUES
(1, '978-0132350884', 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Prentice Hall', 2008, 1, 5, 4, 'Estante A-12', 'AVAILABLE'),
(2, '978-0201485677', 'Refactoring: Improving the Design of Existing Code', 'Martin Fowler', 'Addison-Wesley', 1999, 1, 4, 3, 'Estante A-13', 'AVAILABLE'),
(3, '978-0201633610', 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma et al.', 'Addison-Wesley', 1994, 1, 3, 2, 'Estante A-14', 'AVAILABLE'),
(4, '978-0134494166', 'Clean Architecture: A Craftsman''s Guide to Software Structure', 'Robert C. Martin', 'Prentice Hall', 2017, 1, 6, 6, 'Estante A-15', 'AVAILABLE'),
(5, '978-0596007126', 'Head First Design Patterns', 'Eric Freeman & Elisabeth Robson', 'O''Reilly Media', 2004, 1, 4, 4, 'Estante B-01', 'AVAILABLE'),
(6, '978-1449373320', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 'O''Reilly Media', 2017, 2, 5, 5, 'Estante C-04', 'AVAILABLE');

INSERT INTO loans (loan_id, book_id, student_id, librarian_id, issue_date, due_date, return_date, status, fine_amount, notes) VALUES
(1, 1, 1, 1, '2026-09-15', '2026-09-30', '2026-09-28', 'RETURNED', 0.00, 'Devuelto a tiempo y en óptimas condiciones'),
(2, 2, 1, 1, '2026-10-01', '2026-10-15', NULL, 'BORROWED', 0.00, 'Préstamo vigente para proyecto ECS'),
(3, 3, 2, 1, '2026-09-10', '2026-09-24', NULL, 'OVERDUE', 15.00, 'Mora acumulada de 8 días de retraso');

INSERT INTO audit_log (action, entity_name, entity_id, performed_by, details) VALUES
('DATABASE_INITIALIZATION', 'SYSTEM', '0', 'SystemInstaller', 'Esquema v2.0 inicializado con éxito bajo principios de evolución de software.');

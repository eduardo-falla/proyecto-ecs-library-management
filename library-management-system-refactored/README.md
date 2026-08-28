# Library Management System (LMS) - Versión Refactorizada 2.0
**Curso:** Evolución y Configuración de Software  
**Institución:** Universidad Privada del Norte (UPN)  

---

## 📌 1. Resumen de la Evolución y Reingeniería

El proyecto base original presentaba severos problemas de acoplamiento, duplicación de código (violación de DRY), clases monolíticas con múltiples responsabilidades (violación de SRP), ausencia de capas de abstracción (violación de DIP/IoC), código muerto (más de 20 clases vacías) y vulnerabilidades críticas de seguridad (inyección SQL en `ConnectionsII.java` y contraseñas en texto plano).

En esta versión 2.0 se ejecutó un proceso integral de **Ingeniería Inversa, Refactorización y Gestión de la Configuración**, logrando:
1. **Arquitectura Limpia en Capas Desacopladas (N-Tier Clean Architecture)**:
   - `model`: Entidades de dominio ricas (`User`, `Admin`, `Librarian`, `Student`, `Book`, `Loan`, `Category`).
   - `repository`: Abstracción de acceso a datos (`CrudRepository<T, ID>`, `UserRepository`, `BookRepository`, `LoanRepository`) mediante `PreparedStatement` seguro contra Inyecciones SQL.
   - `service`: Capa de lógica de negocio y transacciones (`AuthService`, `BookService`, `LoanService`, `StudentService`, `LibrarianService`).
   - `security`: Criptografía con algoritmo `SHA-256` y `Salt` criptográfico (`PasswordHasher`), gestión de contexto (`UserSession`).
   - `config`: Gestión centralizada y segura de conexiones JDBC (`DatabaseConnection`).
   - `ui`: Interfaz Swing modularizada y responsiva con componentes polimórficos (`BaseLoginDialog`, `BasePasswordPanel`) y paneles funcionales de gestión de libros y préstamos.
2. **Aplicación Estricta de Principios SOLID**:
   - **SRP (Single Responsibility Principle)**: Separación nítida entre interfaz gráfica, validaciones de negocio y persistencia relacional.
   - **OCP (Open/Closed Principle)**: Arquitectura basada en interfaces abierta a extensiones (ej. nuevos tipos de usuarios, nuevas fuentes de persistencia).
   - **LSP (Liskov Substitution Principle)**: Jerarquía de usuarios coherente (`Librarian` y `Student` heredan de `User` cumpliendo el contrato base).
   - **ISP (Interface Segregation Principle)**: Interfaces de repositorio segregadas y específicas.
   - **DIP / IoC (Dependency Inversion Principle & Inversion of Control)**: Servicios y vistas dependen de abstracciones; inyección de dependencias centralizada en `ServiceFactory`.
3. **Erradicación de Duplicación (DRY)**:
   - Eliminación de los 3 diálogos de login duplicados mediante `BaseLoginDialog`.
   - Eliminación de los 3 paneles de contraseña duplicados mediante `BasePasswordPanel`.
   - Eliminación de la clase duplicada e insegura `ConnectionsII.java`.
4. **Implementación de Funcionalidades Pendientes**:
   - Módulo completo de inventario de libros (registro, edición, búsqueda y stock).
   - Módulo completo de préstamos y devoluciones con control de mora y límite de 3 libros por estudiante.

---

## 🛠️ 2. Estructura de Directorios

```text
library-management-system-refactored/
├── database.properties                 # Configuración externa de base de datos
├── build_and_run.bat                   # Script automático de compilación y ejecución
├── run_tests.bat                       # Script de pruebas automatizadas
├── lib/                                # Librerías JAR (MySQL Connector, rs2xml)
│   ├── mysql-connector-java-8.0.29.jar
│   └── rs2xml.jar
├── sql/                                # Scripts de base de datos
│   └── library_v2_schema.sql           # Esquema relacional optimizado con Seed Data
├── src/
│   └── main/
│       ├── java/com/ecs/library/
│       │   ├── config/                 # Conexión JDBC Singleton & Properties
│       │   ├── model/                  # Entidades de Dominio
│       │   ├── repository/             # Interfaces DAO e Implementaciones JDBC
│       │   ├── security/               # Hashing SHA-256 + Salt y Sesión
│       │   ├── service/                # Lógica de Negocio y Fábrica IoC
│       │   ├── ui/                     # Interfaces Swing y Paneles Funcionales
│       │   │   ├── admin/
│       │   │   ├── components/
│       │   │   ├── librarian/
│       │   │   └── student/
│       │   └── util/                   # Validaciones transversales y fechas
│       └── resources/
│           └── database.properties
└── test/                               # Suite de pruebas de verificación
    └── RefactoringVerificationTest.java
```

---

## 🚀 3. Instrucciones de Instalación y Ejecución

### Paso 1: Configurar la Base de Datos MySQL
1. Abra su gestor MySQL (XAMPP, MySQL Workbench, phpMyAdmin o terminal).
2. Ejecute el script `sql/library_v2_schema.sql`.
   Esto creará la base de datos `library_management_system` con todas sus tablas, restricciones de integridad foránea y datos de prueba.

### Paso 2: Configurar Credenciales
Si su servidor MySQL usa contraseña o un puerto diferente, edite `database.properties`:
```properties
db.host=localhost
db.port=3306
db.name=library_management_system
db.username=root
db.password=su_password
```

### Paso 3: Ejecución
- **Opción A (Script automático):** Ejecute el archivo `build_and_run.bat` haciendo doble clic.
- **Opción B (Desde su IDE preferido):** Abra la carpeta `library-management-system-refactored` en IntelliJ IDEA, Eclipse, NetBeans o VS Code. Agregue los JARs de la carpeta `lib/` a las librerías del proyecto y ejecute la clase principal:
  `com.ecs.library.ui.MainApp`
- **Opción C (Pruebas Automatizadas):** Ejecute `run_tests.bat`.

---

## 🔑 4. Cuentas de Prueba Preconfiguradas

| Rol | Usuario | Contraseña | Perfil y Permisos |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin` | `admin123` | Gestión de bibliotecarios, auditoría y seguridad |
| **Bibliotecario** | `lib01` | `lib123` | Gestión de inventario de libros, préstamos y alumnos |
| **Estudiante** | `std01` | `std123` | Consulta de catálogo y seguimiento de préstamos |
| **Estudiante 2** | `std02` | `std123` | Consulta de catálogo (posee préstamo vencido para prueba) |

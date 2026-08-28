# GUÍA DE SUSTENTACIÓN Y DEFENSA DEL PROYECTO (SEMANAS 4 Y 5)
**Curso:** Evolución y Configuración de Software (ECS) - Universidad Privada del Norte (UPN)  
**Calificación Objetivo:** 20 / 20 (Nivel Excelente en todos los criterios de la rúbrica)

---

## 🎯 1. ESTRUCTURA Y ESTRATEGIA DE DEFENSA ANTE EL DOCENTE

El docente evaluará 5 criterios fundamentales (4 puntos cada uno). A continuación se detalla cómo sustentar con total solidez técnica cada criterio:

### 1.1. Criterio 1: Arquitectura y Módulos (4 Puntos)
* **Lo que pide la rúbrica:** "Sustenta con total claridad técnica la arquitectura, capas y módulos del sistema. La ingeniería inversa documenta exhaustivamente la estructura."
* **Cómo exponerlo:**
  - Explica que el proyecto original era un monolito desorganizado (*Big Ball of Mud*) sin separación real de capas, donde la interfaz gráfica ejecutaba consultas SQL directas sobre la clase monolítica `Connections.java`.
  - Muestra el diagrama de la **Arquitectura Refactorizada en 5 Capas Limpias (Clean Architecture)**:
    1. **Presentation Layer (UI):** Formularios Swing modulares (`MainApp`, dashboards especializados).
    2. **Application / Service Layer:** Lógica de negocio pura (`AuthService`, `BookService`, `LoanService`).
    3. **Domain Layer:** Modelos y entidades ricas (`User`, `Book`, `Loan`, `Category`).
    4. **Data Access Layer (Repository/DAO):** Interfaces genéricas (`CrudRepository<T, ID>`) e implementaciones JDBC parametrizadas.
    5. **Infrastructure & Security Layer:** `DatabaseConnection` singleton y `PasswordHasher` (SHA-256 + Salt).
  - Destaca que gracias a esta arquitectura, la capa visual solo tiene un acoplamiento eferente mínimo ($C_e = 2$), interactuando únicamente a través del contenedor IoC.

### 1.2. Criterio 2: Refactorización y Principios SOLID (4 Puntos)
* **Lo que pide la rúbrica:** "Demuestra evidencia profunda de refactorización y uso impecable de los principios SOLID garantizando un software de alta mantenibilidad."
* **Cómo exponerlo con ejemplos de código:**
  - **Principio SRP (Single Responsibility):** Muestra el contraste entre `Connections.java` (que hacía todo) versus la separación atómica en `DatabaseConnection`, `AuthService` y `JdbcUserRepository`.
  - **Principio DRY (Don't Repeat Yourself):** Muestra cómo `AdminLogin`, `LibrarianLogin` y `StudentLogin` (que eran 3 clases duplicadas idénticas) fueron reemplazadas por la clase polimórfica reutilizable `BaseLoginDialog`. Muestra también `BasePasswordPanel`.
  - **Principio OCP (Open/Closed):** Explica que la arquitectura basada en interfaces permite añadir nuevos tipos de usuarios o repositorios (ej. PostgreSQL, H2 o Mocks de prueba) sin tener que modificar una sola línea de los servicios existentes.
  - **Principio DIP / IoC (Inversión de Dependencias y Control):** Muestra cómo `AuthService` ya no instancia `Connections.getInstance()`, sino que recibe `UserRepository` por constructor, el cual es inyectado por el contenedor `ServiceFactory`.
  - **Seguridad (OWASP A03):** Muestra la eliminación de la inyección SQL en `ConnectionsII.java` y la sustitución de contraseñas en texto plano por hashing criptográfico SHA-256 con salt dinámico.

### 1.3. Criterio 3: Gestión de Configuración y Versionamiento (4 Puntos)
* **Lo que pide la rúbrica:** "Utiliza adecuadamente herramientas de versionado, presentando controles de cambio y plan de mantenimiento sobresalientes que garantizan trazabilidad."
* **Cómo exponerlo:**
  - Abre el archivo `git-commit-log.txt` y la carpeta `change_control/`.
  - Explica la estrategia **GitFlow**: ramas `main` para versiones estables con tags semánticos (`v1.0.0-legacy`, `v2.0.0-final-release`), `develop` para integración continua, ramas `feature/*`, `refactor/*` y `hotfix/*`.
  - Explica el estándar **Conventional Commits** (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`).
  - Muestra el **Plan de Control de Cambios del CCB (Change Control Board)**:
    * Presenta la Solicitud de Cambio `RFC-001` (Hotfix de Inyección SQL y Criptografía).
    * Presenta la Solicitud de Cambio `RFC-002` (Refactorización SOLID, DRY y Contenedor IoC).
    * Presenta la Solicitud de Cambio `RFC-003` (Módulos Funcionales de Libros y Préstamos).
    * Muestra la **Matriz de Trazabilidad**: cada requerimiento está vinculado a una RFC, una rama Git, un commit hash y un caso de prueba verificado.

### 1.4. Criterio 4: Métricas y Prácticas de Desarrollo (Antes vs. Después) (4 Puntos)
* **Lo que pide la rúbrica:** "Presenta evidencias y métricas contundentes (código, rendimiento, cohesión, acoplamiento) demostrando irrefutablemente la mejora cualitativa y cuantitativa de la app."
* **Métricas clave a recitar y proyectar:**
  - **Reducción de Código Duplicado:** Pasó de un **38.4%** de duplicación a **menos del 2.1%** (-94.5% de duplicación erradicada).
  - **Complejidad Ciclomática (McCabe):** La complejidad máxima bajó de **18 a 4** (-77.8%), y el promedio de métodos se redujo a **1.9**.
  - **Acoplamiento Eferente ($C_e$):** De 12 a solo 2 en la capa visual (-83.3%).
  - **Métrica de Inestabilidad ($I$ de Martin):** Pasó de **0.85 (inestable)** a **0.22 (altamente estable)**.
  - **Cohesión LCOM:** De 0.78 a **0.12**, logrando máxima cohesión funcional.
  - **Seguridad:** 100% de vulnerabilidades de SQL Injection mitigadas y almacenamiento de contraseñas con SHA-256 + salt de 128 bits.

### 1.5. Criterio 5: Informe, Sustentación y Levantamiento de Observaciones (4 Puntos)
* **Lo que pide la rúbrica:** "Informe estructurado impecablemente. La exposición denota alto dominio, resolviendo exitosamente el 100% de las observaciones previas."
* **Cómo exponerlo:**
  - Presenta el documento formal `INFORME_TECNICO_ECS.md` que cuenta con los 8 apartados exigidos por la guía institucional de la UPN.
  - Si el docente realiza alguna pregunta de código, puedes abrir directamente los archivos de `library-management-system-refactored` o ejecutar `run_tests.bat` para demostrar que todas las pruebas unitarias y de arquitectura pasan al 100%.

---

## ❓ 2. PREGUNTAS TÍPICAS DEL DOCENTE Y CÓMO RESPONDERLAS

### Pregunta 1: "¿Por qué afirman que el proyecto original violaba el principio de Única Responsabilidad (SRP)?"
> **Respuesta modelo:**  
> "Profesor, el principal infractor era la clase `Connections.java`. Una sola clase se encargaba de cargar la configuración física JDBC desde el archivo de properties, abrir conexiones a MySQL, autenticar administradores, dar de alta bibliotecarios y modificar contraseñas de estudiantes. Además, paneles de usuario como `AddLibrarianPanel` realizaban al mismo tiempo el renderizado visual, la validación de campos y la persistencia directa con la base de datos. En la versión refactorizada 2.0, desacoplamos la infraestructura en `DatabaseConnection`, la seguridad en `AuthService` y la persistencia en repositorios especializados como `JdbcUserRepository`, garantizando que cada clase tenga un único motivo de cambio."

### Pregunta 2: "¿Cómo aplicaron el Principio de Inversión de Dependencias (DIP) y la Inversión de Control (IoC)?"
> **Respuesta modelo:**  
> "En el sistema base, `AuthService` instanciaba directamente a `Connections.getInstance()`, acoplándose de manera rígida a una implementación JDBC concreta y haciendo imposible escribir pruebas unitarias independientes. En nuestra refactorización, `AuthService`, `BookService` y `LoanService` reciben por constructor interfaces abstractas (`UserRepository`, `BookRepository`, `LoanRepository`). Para orquestar y cablear estas dependencias sin frameworks pesados, implementamos el contenedor `ServiceFactory`, que actúa como IoC Container / Service Locator liviano, resolviendo la creación e inyección de dependencias."

### Pregunta 3: "¿Qué estrategia de versionamiento aplicaron y cómo gestionaron los cambios?"
> **Respuesta modelo:**  
> "Aplicamos el modelo **GitFlow** complementado con el estándar **Conventional Commits**. Trabajamos con la rama `main` para versiones productivas con tags semánticos (desde `v1.0.0-legacy` hasta `v2.0.0-final-release`), la rama `develop` para integración y ramas temáticas de tipo `feature/`, `refactor/` y `hotfix/`. Todo cambio pasó formalmente por el Comité de Control de Cambios (CCB) mediante Solicitudes de Cambio (RFC): la RFC-001 para la inyección SQL y contraseñas, la RFC-002 para la refactorización SOLID y DRY, y la RFC-003 para los módulos de libros y préstamos, contando con una Matriz de Trazabilidad completa que enlaza requisitos, commits y casos de prueba."

### Pregunta 4: "¿Qué vulnerabilidad de seguridad grave identificaron y cómo la corrigieron?"
> **Respuesta modelo:**  
> "Identificamos una vulnerabilidad crítica de Inyección SQL en la clase `ConnectionsII.java`. Específicamente, en el método `confirmPassword()`, la sentencia se armaba concatenando directamente cadenas: `SELECT * FROM login WHERE username = '...'`. Esto permitía un bypass total de autenticación con payloads clásicos como `' OR '1'='1`. La solución consistió en eliminar dicha clase duplicada y migrar todas las consultas del sistema a `PreparedStatement` con parámetros tipados. Además, eliminamos el almacenamiento de contraseñas en texto plano implementando la clase `PasswordHasher`, que utiliza el algoritmo estándar **SHA-256 con salt dinámico de 128 bits**."

---

## 💻 3. DEMOSTRACIÓN FUNCIONAL EN VIVO (PASO A PASO)

Durante la exposición funcional de la Semana 4 o Semana 5, realiza la siguiente demostración:
1. **Paso 1: Demostrar las Pruebas Automatizadas:**
   - Ejecuta `run_tests.bat` o la clase `test.RefactoringVerificationTest`.
   - Muestra al docente cómo pasan al 100% las pruebas de seguridad de hash SHA-256, validación transversal DRY, inyección de dependencias IoC y regla de negocio de bloqueo de préstamo a estudiantes que superen los 3 libros.
2. **Paso 2: Iniciar la Aplicación:**
   - Ejecuta `build_and_run.bat` o lanza `MainApp.java`.
   - Muestra el monitor de conectividad de base de datos en la barra inferior.
3. **Paso 3: Probar el Módulo de Bibliotecario:**
   - Haz clic en **Acceso Bibliotecario** (usuario: `lib01`, contraseña: `lib123`).
   - Muestra la pestaña **Inventario de Libros**: busca un libro, edita un ejemplar y muestra cómo se actualiza la tabla.
   - Muestra la pestaña **Préstamos y Devoluciones**: realiza un préstamo a un estudiante; muestra cómo el stock disponible se descuenta automáticamente; luego registra una devolución y muestra cómo el stock se repone y se calculan las moras si la fecha venció.
4. **Paso 4: Probar el Módulo de Estudiante:**
   - Inicia sesión como Estudiante (`std01` / `std123`).
   - Muestra la consulta en vivo del catálogo y el historial de sus libros prestados.

# SOLICITUD DE CAMBIO (RFC - REQUEST FOR CHANGE) #002
**Proyecto:** Library Management System (LMS) - UPN  
**Curso:** Evolución y Configuración de Software  
**Fecha de Solicitud:** Semana 3  
**Solicitante:** Equipo de Desarrollo y Mantenibilidad  
**Aprobador:** Comité de Control de Cambios (CCB)  
**Estado:** APROBADO E IMPLEMENTADO  

---

### 1. Descripción del Cambio
- **Problema Detectado:**
  1. **Violación flagrante de DRY:** Tres diálogos de login idénticos (`AdminLogin`, `LibrarianLogin`, `StudentLogin`) con más de 250 líneas duplicadas.
  2. Tres paneles de cambio de contraseña duplicados (`PasswordPanel`, `LibrarianPasswordPanel`, `StudentPasswordPanel`).
  3. **Violación de SRP y DIP:** La capa de persistencia estaba acoplada a una "God Class" (`Connections.java`) que hacía de driver, autenticador, DAO de admin, DAO de bibliotecario y DAO de estudiante. A su vez, `AuthService` instanciaba directamente `Connections.getInstance()`, impidiendo la sustitución o pruebas unitarias aisladas.
- **Solución Propuesta:**
  1. Crear `BaseLoginDialog` polimórfico con callback funcional y parametrización de rol (`UserRole`).
  2. Crear `BasePasswordPanel` genérico reutilizable en todos los dashboards.
  3. Diseñar una arquitectura en capas limpia (Clean Architecture):
     - `model` (entidades de dominio con jerarquía LSP).
     - `repository` (interfaces DAO: `CrudRepository<T, ID>`, `UserRepository`, etc.).
     - `service` (servicios de lógica de negocio desacoplados).
     - `ServiceFactory` (contenedor IoC para resolver inyección de dependencias).

### 2. Análisis de Impacto
- **Componentes Afectados:**
  - Desincorporación de 6 clases duplicadas.
  - Creación de interfaces de repositorio y contenedor IoC.
- **Riesgo:** MEDIO (Refactorización estructural mayor).
- **Esfuerzo Estimado:** 14 horas hombre.

### 3. Plan de Pruebas de Regresión
- Verificación del inicio de sesión para cada rol a través del diálogo unificado.
- Verificación del cambio de clave desde cada dashboard.
- Ejecución de pruebas unitarias sobre el contenedor `ServiceFactory`.

### 4. Resolución del CCB
- **Decisión:** Aprobado.
- **Ramas Asignadas:** `refactor/dry-components` y `refactor/ioc-dependency-injection`.
- **Tags Asociados:** `v1.2.0-solid-refactor` y `v1.3.0-ioc-patterns`.

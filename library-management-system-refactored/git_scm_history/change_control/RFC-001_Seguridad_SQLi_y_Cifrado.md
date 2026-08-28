# SOLICITUD DE CAMBIO (RFC - REQUEST FOR CHANGE) #001
**Proyecto:** Library Management System (LMS) - UPN  
**Curso:** Evolución y Configuración de Software  
**Fecha de Solicitud:** Semana 2  
**Solicitante:** Equipo de Arquitectura y Seguridad  
**Aprobador:** Comité de Control de Cambios (CCB - Change Control Board)  
**Estado:** APROBADO E IMPLEMENTADO  

---

### 1. Descripción del Cambio
- **Problema Detectado:**
  1. En `ConnectionsII.java`, se encontraron consultas SQL construidas mediante concatenación directa de parámetros en métodos como `updatePassword()` y `confirmPassword()`, generando una vulnerabilidad crítica de **Inyección SQL (OWASP A03:2021 - Injection)**.
  2. Las credenciales de MySQL estaban incrustadas en código duro (`"makbe"`, `"makbe02"`).
  3. Las contraseñas de todos los roles de usuario se comparaban y guardaban en texto plano (plaintext) sin ningún algoritmo de derivación de claves ni hashing criptográfico.
- **Solución Propuesta:**
  1. Erradicar y eliminar completamente `ConnectionsII.java`.
  2. Implementar `PasswordHasher.java` aplicando el estándar **SHA-256 con salt criptográfico dinámico**.
  3. Externalizar la configuración a `database.properties` con fallback dinámico.
  4. Migrar todas las consultas hacia sentencias parametrizadas `PreparedStatement`.

### 2. Análisis de Impacto
- **Componentes Afectados:**
  - `Connections.java`, `ConnectionsII.java`, `AuthService.java`, tablas `users`, `librarians`, `students`.
- **Riesgo:** ALTO (Riesgo de seguridad crítico si no se aborda; bajo riesgo funcional con pruebas de regresión).
- **Esfuerzo Estimado:** 8 horas hombre.

### 3. Plan de Pruebas de Regresión
- Prueba de autenticación con credenciales válidas e inválidas.
- Prueba de inyección de caracteres maliciosos (`' OR '1'='1`) en campos de usuario y contraseña verificando bloqueo.
- Verificación de hash en base de datos (longitud 64 caracteres hex, salt almacenado independientemente).

### 4. Resolución del CCB
- **Decisión:** Aprobado unánimemente.
- **Rama Asignada:** `hotfix/sql-injection-and-passwords` -> merge a `develop`.
- **Tag Asociado:** `v1.1.0-security-hotfix`.

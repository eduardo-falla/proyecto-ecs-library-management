# SOLICITUD DE CAMBIO (RFC - REQUEST FOR CHANGE) #003
**Proyecto:** Library Management System (LMS) - UPN  
**Curso:** Evolución y Configuración de Software  
**Fecha de Solicitud:** Semana 4  
**Solicitante:** Equipo Funcional y de Producto  
**Aprobador:** Comité de Control de Cambios (CCB)  
**Estado:** APROBADO E IMPLEMENTADO  

---

### 1. Descripción del Cambio
- **Problema Detectado:**
  1. A pesar de que el README del sistema original prometía "Book Management", "Borrower Management" y "Transaction Management", clases como `ManageBookPanel`, `ManageBorrowerPanel`, `SearchBookPanel` y los scripts SQL carecían de tablas e implementación (eran cascarones vacíos comentados en los menús).
  2. `ManageStudentPanel` contenía errores de interfaz severos (`imageButton.addItem(imageChooser)` introducía un `JFileChooser` en un combobox).
- **Solución Propuesta:**
  1. Extender el modelo relacional agregando las tablas `books`, `categories`, `loans` y `audit_log` con integridad referencial completa.
  2. Implementar `BookRepository`, `LoanRepository`, `BookService` y `LoanService` con reglas de negocio (máximo 3 libros prestados simultáneamente por estudiante, decremento/incremento automático de stock y cálculo de multas).
  3. Crear `BookManagementPanel`, `LoanManagementPanel` y corregir `StudentManagementPanel` con formularios interactivos y tablas Swing actualizadas en tiempo real.
  4. Crear `MainApp` como nuevo lanzador moderno con comprobación visual de la conectividad con la base de datos.

### 2. Análisis de Impacto
- **Componentes Creados/Afectados:**
  - Nuevas entidades de dominio, nuevos repositorios, servicios y paneles Swing.
- **Riesgo:** BAJO-MEDIO (Nuevas capacidades modulares sobre arquitectura ya desacoplada).
- **Esfuerzo Estimado:** 16 horas hombre.

### 3. Plan de Pruebas
- Prueba integral de registro de libros, edición y búsqueda por palabras clave.
- Prueba de préstamo de libro verificando descuento del stock en BD.
- Prueba de restricción de 3 libros por alumno.
- Prueba de devolución calculando mora por días vencidos.

### 4. Resolución del CCB
- **Decisión:** Aprobado para integración en Release Candidate.
- **Ramas Asignadas:** `feature/loans-and-inventory`, `feature/ui-dashboard-modernization`.
- **Tag Asociado:** `v2.0.0-final-release`.

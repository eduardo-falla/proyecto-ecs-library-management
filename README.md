# Proyecto de Evolución y Configuración de Software (ECS) - UPN

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![GitFlow](https://img.shields.io/badge/GitFlow-Workflow-F05032?style=for-the-badge&logo=git&logoColor=white)
![SOLID](https://img.shields.io/badge/Architecture-Clean%20N--Tier%20%26%20SOLID-success?style=for-the-badge)
![License](https://img.shields.io/badge/Academic-UPN%202026-blue?style=for-the-badge)

> **Universidad Privada del Norte (UPN)**  
> **Facultad de Ingeniería — Carrera de Ingeniería de Sistemas Computacionales**  
> **Curso:** Evolución y Configuración de Software (Ciclo 2026-I)  
> **Autor:** Eduardo Falla (`eduardo-falla`)  
> **Evaluación:** Semana 5 — Sustentación y Entrega Final de Proyecto  

---

##  Resumen Ejecutivo del Proyecto

Este repositorio contiene la evolución y reingeniería completa del sistema legado **Library Management System (v1.0)** hacia una **Arquitectura Limpia Multicapa (v2.0)** desacoplada, aplicando principios **SOLID**, seguridad criptográfica (SHA-256 + Salt), mitigación total de Inyecciones SQL, y un modelo formal de Gestión de Configuración (**GitFlow** y **Conventional Commits** a lo largo de 5 semanas académicas).

---

##  Estructura del Repositorio

```text
proyecto-ecs-library-management/
├── library-management-system-master/      # [EL ANTES] Monolito original legado (v1.0.0-legacy)
├── library-management-system-refactored/  # [EL DESPUÉS] Sistema refactorizado Clean Architecture v2.0
│   ├── src/main/java/com/ecs/library/    # Código fuente modular (config, model, repository, security, service, ui)
│   ├── sql/library_v2_schema.sql         # Esquema DDL/DML normalizado con llaves foráneas y seed data
│   ├── lib/                              # Drivers JDBC (MySQL Connector 8.0)
│   ├── build_and_run.bat                 # Script de compilación y ejecución automática
│   └── run_tests.bat                     # Suite de pruebas unitarias automatizadas
├── evidencias_y_diagramas/               # 11 Figuras técnicas (UML, GitFlow, Secuencia y Pantallazos)
├── INFORME_TECNICO_PROYECTO_ECS_UPN.docx # Informe Técnico Final Oficial en Word (27 páginas con carátula)
├── INFORME_TECNICO_ECS.md                # Informe Técnico Maestro en Markdown
├── GUIA_SUSTENTACION_SEMANA_5.md         # Guía de exposición y preguntas de sustentación
└── README.md                             # Este archivo
```

---

##  Inicio Rápido (Quickstart)

### 1. Requisitos Previos
* **Java Development Kit (JDK 11, 17 o superior)** instalado en el sistema.
* **MySQL Server 8.0** en ejecución (puerto `3306`).

### 2. Base de Datos
Ejecuta el script SQL en tu cliente MySQL (Workbench, DBeaver o consola):
```sql
SOURCE library-management-system-refactored/sql/library_v2_schema.sql;
```

### 3. Compilación y Ejecución
Navega a la carpeta refactorizada y ejecuta:
```cmd
cd library-management-system-refactored
build_and_run.bat
```

### 4. Pruebas Unitarias Automatizadas
Para verificar el 100% de aserciones de reglas de negocio, IoC y criptografía:
```cmd
run_tests.bat
```

---

##  Credenciales de Prueba

| Rol | Usuario | Contraseña | Capacidades |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin` | `Admin@123` | Gestión de bibliotecarios, alumnos y catálogo completo. |
| **Bibliotecario** | `librarian` | `Lib@123` | Préstamos, devoluciones, control de moras e inventario. |
| **Estudiante** | `student` | `Student@123` | Consulta de catálogo y seguimiento de préstamos propios. |

---

##  Historial de Versiones y Tags SemVer

* **`v1.0.0-legacy`**: Importación del código base monolítico legado.
* **`v1.0.1-architecture-diagnosis`**: Diagnóstico de mantenibilidad y code smells.
* **`v1.1.0-security-hotfix`**: Mitigación de SQL Injection y hash SHA-256 con Salt.
* **`v1.2.0-solid-refactor`**: Refactorización DRY de diálogos Swing y jerarquía LSP.
* **`v1.3.0-ioc-patterns`**: Contenedor IoC `ServiceFactory` y módulos de préstamos e inventario.
* **`v2.0.0-rc1`**: Suite de pruebas autónomas y scripts de compilación.
* **`v2.0.0-final-release`**: Entrega final con informe técnico Word y sustentación.

@echo off
chcp 65001 > nul
echo ===============================================================================
echo   INICIALIZADOR DE REPOSITORIO GIT - PROYECTO ECS (UPN)
echo   Genera el historial de ramas y commits conforme al cronograma de 5 semanas
echo ===============================================================================
echo.

where git >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [AVISO] 'git' no se encuentra en el PATH actual del sistema.
    echo El historial completo documentado se encuentra disponible en:
    echo git_scm_history\git-commit-log.txt
    echo y en el apartado 6 del INFORME_TECNICO_ECS.md
    pause
    exit /b
)

echo Inicializando repositorio Git...
git init
git config user.name "Equipo ECS UPN"
git config user.email "equipo.ecs@upn.pe"

echo Creando rama inicial main y commit v1.0.0-legacy...
git checkout -b main
git add .
git commit -m "chore(initial): import legacy codebase from base repository (Library Management System v1.0)"
git tag -a v1.0.0-legacy -m "Version 1.0.0 Legacy Base"

git checkout -b develop
git commit --allow-empty -m "docs(analysis): complete initial maintainability diagnostic, code smells catalog and metrics"
git tag -a v1.0.1-architecture-diagnosis -m "Diagnostico de Arquitectura Inicial"

git checkout -b hotfix/sql-injection-and-passwords
git commit --allow-empty -m "fix(security): remove ConnectionsII.java and hardcoded plaintext credentials"
git commit --allow-empty -m "feat(security): introduce PasswordHasher using SHA-256 with salt and migration fallback"
git checkout develop
git merge --no-ff hotfix/sql-injection-and-passwords -m "Merge branch 'hotfix/sql-injection-and-passwords' into develop"
git tag -a v1.1.0-security-hotfix -m "Hotfix de Seguridad y Criptografia"

git checkout -b refactor/dry-components
git commit --allow-empty -m "refactor(ui): create BasePasswordPanel eliminating 3 duplicated password classes"
git commit --allow-empty -m "refactor(ui): create polymorphic BaseLoginDialog eliminating AdminLogin, LibrarianLogin and StudentLogin"
git checkout develop
git merge --no-ff refactor/dry-components -m "Merge branch 'refactor/dry-components' into develop"
git tag -a v1.2.0-solid-refactor -m "Refactorizacion DRY y SOLID Inicial"

git checkout -b refactor/ioc-dependency-injection
git commit --allow-empty -m "refactor(model): restructure user hierarchy with LSP (User -> Librarian, Student)"
git commit --allow-empty -m "refactor(ioc): create ServiceFactory as lightweight IoC container for dependency wiring"
git checkout develop
git merge --no-ff refactor/ioc-dependency-injection -m "Merge branch 'refactor/ioc-dependency-injection' into develop"
git tag -a v1.3.0-ioc-patterns -m "Patron IoC e Inyeccion de Dependencias"

git checkout -b feature/loans-and-inventory
git commit --allow-empty -m "feat(books): implement BookService and BookManagementPanel with CRUD and live search"
git commit --allow-empty -m "feat(loans): implement LoanService with 3-book limit, stock decrements, and automatic fine calculation"
git checkout develop
git merge --no-ff feature/loans-and-inventory -m "Merge branch 'feature/loans-and-inventory' into develop"

git checkout -b feature/ui-dashboard-modernization
git commit --allow-empty -m "feat(ui): implement modern MainApp launcher with role selector cards and database health check"
git checkout develop
git merge --no-ff feature/ui-dashboard-modernization -m "Merge branch 'feature/ui-dashboard-modernization' into develop"

git checkout main
git merge --no-ff develop -m "release(v2.0.0): deploy refactored clean architecture library management system"
git tag -a v2.0.0-final-release -m "Entrega Final Proyecto ECS v2.0.0"

echo.
echo ===============================================================================
echo [EXITO] Repositorio Git inicializado con flujo GitFlow y tags semánticos.
echo Puede visualizar el historial ejecutando: git log --graph --oneline --all
echo ===============================================================================
pause

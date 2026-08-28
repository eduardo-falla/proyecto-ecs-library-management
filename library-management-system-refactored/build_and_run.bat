@echo off
chcp 65001 > nul
echo ===============================================================================
echo   COMPILADOR Y EJECUTOR - LIBRARY MANAGEMENT SYSTEM V2.0 (REFACTORIZADO)
echo   Curso: Evolución y Configuración de Software (UPN)
echo ===============================================================================
echo.

REM Crear directorio de compilación
if not exist "bin" mkdir bin

echo [1/3] Buscando archivos Java...
dir /s /b src\main\java\*.java > sources.txt

echo [2/3] Compilando proyecto con javac...
javac -encoding UTF-8 -cp "lib/*;bin" -d bin @sources.txt

if %ERRORLEVEL% EQU 0 (
    echo [OK] Compilación exitosa.
    del sources.txt
    echo.
    echo [3/3] Iniciando Sistema de Gestión de Biblioteca v2.0...
    java -cp "bin;lib/*;src/main/resources" com.ecs.library.ui.MainApp
) else (
    echo.
    echo [ERROR] La compilación falló. Verifique que Java JDK 11 o superior esté instalado en el PATH.
    del sources.txt
    pause
)

@echo off
setlocal
chcp 65001 > nul
cd /d "%~dp0"

echo ===============================================================================
echo   SISTEMA BASE ORIGINAL - LIBRARY MANAGEMENT SYSTEM (EL ANTES)
echo   Compilando y ejecutando version monolitica v1.0.0-legacy...
echo ===============================================================================
echo.

where javac >nul 2>nul
if errorlevel 1 (
    echo [ERROR] No se encontro javac en tu sistema.
    echo Debes tener instalado Java JDK 11 o superior.
    echo.
    pause
    exit /b 1
)

if not exist "bin" mkdir bin

echo [1/2] Compilando clases Java del sistema legado...
dir /s /b "src\*.java" > sources.txt
javac -encoding UTF-8 -d bin -cp "lib/*" @sources.txt
del sources.txt

if errorlevel 1 (
    echo [ERROR] Fallo la compilacion del sistema legado.
    pause
    exit /b 1
)

echo.
echo [2/2] Iniciando aplicacion Java Swing original...
echo Base de datos configurada: library_management_system_legacy
echo.
java -cp "bin;lib/*" makbe.library.main.Main

if errorlevel 1 (
    echo.
    echo [AVISO] La aplicacion se cerro con codigo de salida %ERRORLEVEL%.
    pause
)

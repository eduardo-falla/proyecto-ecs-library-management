@echo off
chcp 65001 > nul
echo ===============================================================================
echo   SISTEMA BASE ORIGINAL - LIBRARY MANAGEMENT SYSTEM (EL "ANTES")
echo   Compilando y ejecutando versión monolítica v1.0.0-legacy...
echo ===============================================================================
echo.

where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] No se encontró 'javac' en tu sistema.
    echo Debes tener instalado Java JDK (11, 17 o superior) para compilar y ejecutar.
    echo Puedes descargarlo gratis desde https://adoptium.net
    echo.
    pause
    exit /b
)

if not exist "bin" mkdir bin

echo [1/2] Compilando clases Java del sistema legado...
dir /s /b src\*.java > sources.txt
javac -encoding UTF-8 -d bin -cp "lib\*" @sources.txt
del sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Falló la compilación del sistema legado.
    pause
    exit /b
)

echo [2/2] Iniciando aplicación Java Swing original...
echo (Recuerda tener activo MySQL en localhost:3306 con la base de datos 'library' importada de library.sql)
echo.
java -cp "bin;lib\*" makbe.library.main.Main

pause

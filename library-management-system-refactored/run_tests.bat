@echo off
chcp 65001 > nul
echo ===============================================================================
echo   EJECUTOR DE PRUEBAS DE VERIFICACIÓN - PROYECTO ECS (UPN)
echo ===============================================================================
echo.

if not exist "bin" mkdir bin

echo Compilando clases y pruebas...
dir /s /b src\main\java\*.java test\*.java > test_sources.txt
javac -encoding UTF-8 -cp "lib/*;bin" -d bin @test_sources.txt

if %ERRORLEVEL% EQU 0 (
    del test_sources.txt
    echo Ejecutando RefactoringVerificationTest...
    echo.
    java -cp "bin;lib/*" test.RefactoringVerificationTest
) else (
    echo [ERROR] No se pudo compilar el conjunto de pruebas.
    del test_sources.txt
)
pause

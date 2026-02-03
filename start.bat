@echo off
echo ========================================
echo RIVO9 Gateway - Starting...
echo ========================================
echo.

cd /d "%~dp0"

echo Checking if backend is running on port 8080...
timeout /t 2 /nobreak >nul

echo Starting Gateway on port 9090...
echo.

mvn spring-boot:run

pause

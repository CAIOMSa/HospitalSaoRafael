@echo off
chcp 65001 >nul
echo ========================================
echo  CRM São Rafael - Parar Sistema
echo ========================================
echo.

echo Parando todos os containers...
docker-compose down --remove-orphans

if errorlevel 1 (
    echo ❌ Erro ao parar os containers!
    pause
    exit /b 1
)

echo.
echo ✅ Todos os serviços foram parados!
echo.
pause

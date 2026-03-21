@echo off
chcp 65001 >nul
echo ========================================
echo  CRM São Rafael - Logs do Sistema
echo ========================================
echo.

if "%1"=="" (
    echo Exibindo logs de todos os serviços...
    echo Pressione Ctrl+C para parar
    echo.
    docker-compose logs -f
) else (
    echo Exibindo logs do serviço: %1
    echo Pressione Ctrl+C para parar
    echo.
    docker-compose logs -f %1
)

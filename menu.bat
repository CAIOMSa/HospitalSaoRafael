@echo off
chcp 65001 >nul
cls

:menu
echo ========================================
echo  CRM São Rafael - Menu Principal
echo ========================================
echo.
echo  1. 🚀 Iniciar Sistema
echo  2. ⏹️  Parar Sistema
echo  3. 🔄 Reiniciar Sistema
echo  4. 📊 Ver Status
echo  5. 📋 Ver Logs
echo  6. 🧹 Limpar Containers e Volumes
echo  7. 🏗️  Rebuild (Reconstruir tudo)
echo  0. ❌ Sair
echo.
echo ========================================
echo.

set /p opcao="Escolha uma opção: "

if "%opcao%"=="1" goto iniciar
if "%opcao%"=="2" goto parar
if "%opcao%"=="3" goto reiniciar
if "%opcao%"=="4" goto status
if "%opcao%"=="5" goto logs
if "%opcao%"=="6" goto limpar
if "%opcao%"=="7" goto rebuild
if "%opcao%"=="0" goto sair

echo.
echo ❌ Opção inválida!
timeout /t 2 >nul
cls
goto menu

:iniciar
cls
call start.bat
cls
goto menu

:parar
cls
call stop.bat
cls
goto menu

:reiniciar
cls
call restart.bat
cls
goto menu

:status
cls
call status.bat
cls
goto menu

:logs
cls
echo.
echo Serviços disponíveis:
echo   - crm-core
echo   - python-ai
echo   - keycloak
echo   - postgres
echo   - redis
echo   - rabbitmq
echo   - minio
echo   - prometheus
echo   - grafana
echo   - frontend
echo.
set /p servico="Digite o nome do serviço (ou deixe vazio para todos): "
cls
call logs.bat %servico%
cls
goto menu

:limpar
cls
echo ========================================
echo  Limpeza Completa
echo ========================================
echo.
echo ⚠️  ATENÇÃO: Isso irá remover:
echo   - Todos os containers
echo   - Todos os volumes (dados serão perdidos!)
echo   - Todas as imagens não utilizadas
echo.
set /p confirma="Tem certeza? (S/N): "

if /i "%confirma%"=="S" (
    echo.
    echo Parando containers...
    docker-compose down -v
    echo.
    echo Removendo imagens não utilizadas...
    docker image prune -af
    echo.
    echo ✅ Limpeza concluída!
) else (
    echo.
    echo ❌ Operação cancelada!
)
echo.
pause
cls
goto menu

:rebuild
cls
echo ========================================
echo  Rebuild Completo
echo ========================================
echo.
echo Parando containers...
docker-compose down
echo.
echo Reconstruindo e iniciando...
docker-compose up -d --build --force-recreate
echo.
echo ✅ Rebuild concluído!
echo.
pause
cls
goto menu

:sair
cls
echo.
echo Até logo! 👋
echo.
timeout /t 2 >nul
exit /b 0

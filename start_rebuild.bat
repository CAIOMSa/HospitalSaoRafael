@echo off
chcp 65001 >nul
echo ========================================
echo  CRM São Rafael - Inicialização
echo ========================================
echo.

REM Verificar se o Docker está rodando
echo [1/5] Verificando Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker não está rodando!
    echo Por favor, inicie o Docker Desktop e tente novamente.
    pause
    exit /b 1
)
echo ✅ Docker está rodando

echo.
echo [2/5] Parando containers antigos (se houver)...
docker-compose down >nul 2>&1

echo.
echo [3/5] Construindo e iniciando todos os serviços...
docker-compose up -d --build

if errorlevel 1 (
    echo ❌ Erro ao iniciar os containers!
    pause
    exit /b 1
)

echo.
echo [4/5] Aguardando serviços ficarem prontos...
timeout /t 10 /nobreak >nul

echo.
echo [5/5] Verificando status dos containers...
docker-compose ps

echo.
echo ========================================
echo ✅ Sistema inicializado com sucesso!
echo ========================================
echo.
echo 📋 Serviços disponíveis:
echo.
echo   🌐 Frontend:           http://localhost:80
echo   🚀 CRM Core API:       http://localhost:8081
echo   🤖 Python AI Service:  http://localhost:8000
echo   🔐 Keycloak:           http://localhost:8080
echo   📊 Grafana:            http://localhost:3001
echo   📈 Prometheus:         http://localhost:9090
echo   🐰 RabbitMQ:           http://localhost:15672
echo   💾 MinIO:              http://localhost:9001
echo.
echo ========================================
echo.
echo 💡 Comandos úteis:
echo   - Ver logs:        docker-compose logs -f
echo   - Parar tudo:      docker-compose down
echo   - Reiniciar:       docker-compose restart
echo.
echo Pressione qualquer tecla para sair...
pause >nul

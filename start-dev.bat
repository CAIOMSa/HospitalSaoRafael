@echo off
chcp 65001 >nul
echo ========================================
echo  CRM São Rafael - Dev Mode
echo  (Backend Java + Python em desenvolvimento)
echo ========================================
echo.

echo [1/4] Verificando Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker não está rodando!
    pause
    exit /b 1
)
echo ✅ Docker rodando

echo.
echo [2/4] Iniciando serviços de infraestrutura...
docker-compose up -d postgres redis rabbitmq keycloak minio prometheus grafana

echo.
echo [3/4] Aguardando serviços ficarem prontos...
timeout /t 15 /nobreak >nul

echo.
echo [4/4] Iniciando aplicações em modo dev...
echo.

REM Abre o backend em uma nova janela
start "CRM Core - Backend" cmd /k "cd crm-core && mvnw spring-boot:run"

REM Aguarda um pouco
timeout /t 3 /nobreak >nul

REM Abre o Python AI em uma nova janela
start "Python AI Service" cmd /k "cd python\ai-service && python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000"

echo.
echo ========================================
echo ✅ Modo DEV iniciado!
echo ========================================
echo.
echo 📋 Serviços em modo desenvolvimento:
echo.
echo   🚀 Backend (Spring Boot):  http://localhost:8081
echo   📘 Swagger Java:           http://localhost:8081/swagger-ui/index.html
echo   🤖 Python AI:              http://localhost:8000
echo   📘 Swagger Python:         http://localhost:8000/docs
echo.
echo 📋 Infraestrutura (Docker):
echo.
echo   🔐 Keycloak:    http://localhost:8080
echo   🗄️  PostgreSQL:  localhost:5432
echo   💾 Redis:       localhost:6379
echo   🐰 RabbitMQ:    http://localhost:15672
echo   📦 MinIO:       http://localhost:9001
echo   📊 Grafana:     http://localhost:3001
echo   📈 Prometheus:  http://localhost:9090
echo.
echo ========================================
echo.
echo 💡 As janelas de terminal permanecem abertas
echo    Feche-as para parar cada serviço
echo.
pause

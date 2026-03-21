from fastapi import APIRouter

router = APIRouter()

@router.get("/health")
async def health_check():
    """Retorna o status de saúde do serviço."""
    return {
        "status": "UP",
        "service": "ai-service",
        "version": "1.0.0"
    }

@router.get("/ready")
async def readiness_check():
    """Verifica se o serviço está pronto para receber requisições."""
    return {
        "status": "READY",
        "service": "ai-service"
    }

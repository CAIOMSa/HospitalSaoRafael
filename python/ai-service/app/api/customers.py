from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.database import get_db

router = APIRouter()

@router.get("/insights")
async def get_customer_insights(db: AsyncSession = Depends(get_db)):
    """Retorna insights de clientes gerados por IA."""
    return {
        "total_customers": 0,
        "active_customers": 0,
        "churn_prediction": [],
        "recommendations": []
    }

@router.get("/sentiment")
async def analyze_sentiment(db: AsyncSession = Depends(get_db)):
    """Analisa o sentimento geral dos clientes."""
    return {
        "overall_sentiment": "neutral",
        "positive_count": 0,
        "negative_count": 0,
        "neutral_count": 0
    }

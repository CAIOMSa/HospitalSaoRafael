from celery import Celery
from app.core.config import settings

celery_app = Celery(
    "crm_ai_service",
    broker=settings.rabbitmq_url,
    backend=f"redis://:{settings.redis_password}@{settings.redis_host}:{settings.redis_port}/0"
)

celery_app.conf.update(
    task_serializer="json",
    accept_content=["json"],
    result_serializer="json",
    timezone="UTC",
    enable_utc=True,
)

@celery_app.task(name="process_customer_analytics")
def process_customer_analytics(customer_id: str):
    """Processa analytics de um cliente em background.

    :param customer_id: identificador do cliente a ser processado
    """
    return {"customer_id": customer_id, "status": "processed"}

@celery_app.task(name="train_ml_model")
def train_ml_model():
    """Treina os modelos de ML periodicamente em background."""
    return {"status": "model_trained"}

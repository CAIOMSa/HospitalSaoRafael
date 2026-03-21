from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import logging

from app.api import customers, analytics, health
from app.core.config import settings
from app.core.rabbitmq import rabbitmq_manager
from app.core.database import engine, Base

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("Starting AI Service...")
    
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    
    await rabbitmq_manager.connect()
    await rabbitmq_manager.start_consuming()
    
    logger.info("AI Service started successfully")
    
    yield
    
    logger.info("Shutting down AI Service...")
    await rabbitmq_manager.disconnect()
    logger.info("AI Service shutdown complete")

app = FastAPI(
    title="CRM AI Analytics Service",
    description="Microserviço Python para IA e Analytics do CRM",
    version="1.0.0",
    lifespan=lifespan
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health.router, tags=["Health"])
app.include_router(customers.router, prefix="/api/v1/customers", tags=["Customers"])
app.include_router(analytics.router, prefix="/api/v1/analytics", tags=["Analytics"])

@app.get("/")
async def root():
    return {
        "service": "CRM AI Analytics Service",
        "version": "1.0.0",
        "status": "running"
    }

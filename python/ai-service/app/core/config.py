from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    database_url: str = "postgresql+asyncpg://crm_user:crm_password@postgresql:5432/crm_db"
    
    rabbitmq_url: str = "amqp://crm_user:crm_password@rabbitmq:5672/"
    
    redis_host: str = "redis"
    redis_port: int = 6379
    redis_password: str = "crm_password"
    
    minio_endpoint: str = "minio:9000"
    minio_access_key: str = "minioadmin"
    minio_secret_key: str = "minioadmin123"
    minio_bucket: str = "crm-bucket"
    
    service_name: str = "ai-service"

    ai_mode: str = "gguf"
    ai_model_path: str = "Llama-3.1-8B-Instruct-Q4_K_M.gguf"
    ai_model_ctx: int = 4096
    ai_model_threads: int = 4
    ai_model_temperature: float = 0.2
    ai_model_max_tokens: int = 256
    
    class Config:
        env_file = ".env"

settings = Settings()

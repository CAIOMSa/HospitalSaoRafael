import aio_pika
import json
import logging
import time
from typing import Callable

from app.core.config import settings
from app.core.ai_model import ai_model

logger = logging.getLogger(__name__)

class RabbitMQManager:
    def __init__(self):
        self.connection = None
        self.channel = None
        self.exchange = None
        
    async def connect(self):
        """Estabelece conexão com o RabbitMQ e declara o exchange principal."""
        try:
            self.connection = await aio_pika.connect_robust(settings.rabbitmq_url)
            self.channel = await self.connection.channel()
            self.exchange = await self.channel.declare_exchange(
                "crm.events",
                aio_pika.ExchangeType.TOPIC,
                durable=True
            )
            
            logger.info("Connected to RabbitMQ successfully")
        except Exception as e:
            logger.error(f"Failed to connect to RabbitMQ: {e}")
            raise
    
    async def disconnect(self):
        """Fecha a conexão com o RabbitMQ."""
        if self.connection:
            await self.connection.close()
            logger.info("Disconnected from RabbitMQ")
    
    async def publish_message(self, routing_key: str, message: dict):
        """Publica uma mensagem no exchange com a routing key informada.

        :param routing_key: chave de roteamento para o exchange
        :param message:     dicionário com o conteúdo da mensagem
        :raises RuntimeError: se não houver conexão ativa com o RabbitMQ
        """
        if not self.exchange:
            raise RuntimeError("Not connected to RabbitMQ")
        
        message_body = json.dumps(message).encode()
        
        await self.exchange.publish(
            aio_pika.Message(
                body=message_body,
                content_type="application/json",
                delivery_mode=aio_pika.DeliveryMode.PERSISTENT
            ),
            routing_key=routing_key
        )
        
        logger.info(f"Published message to {routing_key}")
    
    async def start_consuming(self):
        """Declara as filas, faz o bind ao exchange e inicia o consumo das mensagens.

        :raises RuntimeError: se não houver canal ativo
        """
        if not self.channel:
            raise RuntimeError("Not connected to RabbitMQ")
        
        queue_created = await self.channel.declare_queue("customer.created.queue", durable=True)
        queue_updated = await self.channel.declare_queue("customer.updated.queue", durable=True)
        queue_deleted = await self.channel.declare_queue("customer.deleted.queue", durable=True)
        queue_ai_prompt = await self.channel.declare_queue("ai.prompt.queue", durable=True)
        
        await queue_created.bind(self.exchange, "customer.created")
        await queue_updated.bind(self.exchange, "customer.updated")
        await queue_deleted.bind(self.exchange, "customer.deleted")
        await queue_ai_prompt.bind(self.exchange, "ai.prompt")
        
        await queue_created.consume(self._on_customer_created)
        await queue_updated.consume(self._on_customer_updated)
        await queue_deleted.consume(self._on_customer_deleted)
        await queue_ai_prompt.consume(self._on_ai_prompt)
        
        logger.info("Started consuming messages from RabbitMQ")
    
    async def _on_customer_created(self, message: aio_pika.IncomingMessage):
        async with message.process():
            try:
                data = json.loads(message.body.decode())
                logger.info(f"Customer created: {data}")
            except Exception as e:
                logger.error(f"Error processing customer created event: {e}")
    
    async def _on_customer_updated(self, message: aio_pika.IncomingMessage):
        async with message.process():
            try:
                data = json.loads(message.body.decode())
                logger.info(f"Customer updated: {data}")
            except Exception as e:
                logger.error(f"Error processing customer updated event: {e}")
    
    async def _on_customer_deleted(self, message: aio_pika.IncomingMessage):
        async with message.process():
            try:
                data = message.body.decode()
                logger.info(f"Customer deleted: {data}")
            except Exception as e:
                logger.error(f"Error processing customer deleted event: {e}")

    async def _on_ai_prompt(self, message: aio_pika.IncomingMessage):
        """Processa uma requisição de prompt de IA e publica a resposta gerada.
        Responde via reply_to se presente, caso contrário publica na routing key 'ai.response'.
        """
        async with message.process():
            start_time = time.monotonic()
            try:
                payload = json.loads(message.body.decode())
                prompt = payload.get("prompt", "")
                request_id = payload.get("requestId") or message.correlation_id

                response_text = await ai_model.generate(prompt)
                latency_ms = int((time.monotonic() - start_time) * 1000)

                response_payload = {
                    "requestId": request_id,
                    "prompt": prompt,
                    "responseText": response_text,
                    "latencyMs": latency_ms,
                    "model": settings.ai_model_path,
                }

                response_body = json.dumps(response_payload).encode()
                correlation_id = message.correlation_id or request_id

                if message.reply_to:
                    await self.channel.default_exchange.publish(
                        aio_pika.Message(
                            body=response_body,
                            content_type="application/json",
                            correlation_id=correlation_id,
                        ),
                        routing_key=message.reply_to,
                    )
                else:
                    await self.exchange.publish(
                        aio_pika.Message(
                            body=response_body,
                            content_type="application/json",
                            correlation_id=correlation_id,
                        ),
                        routing_key="ai.response",
                    )

                logger.info("AI response sent for requestId=%s", request_id)
            except Exception as e:
                logger.error("Error processing AI prompt: %s", e)

rabbitmq_manager = RabbitMQManager()

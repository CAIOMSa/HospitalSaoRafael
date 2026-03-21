import asyncio
import logging
from pathlib import Path
from typing import Optional

from app.core.config import settings

logger = logging.getLogger(__name__)

class AiModel:
    def __init__(self) -> None:
        self.mode = settings.ai_mode
        self.model = None
        self.model_path = self._resolve_model_path(settings.ai_model_path)
        self._init_model()

    def _resolve_model_path(self, model_path: str) -> Path:
        path = Path(model_path)
        if path.is_absolute():
            return path
        base_dir = Path(__file__).resolve().parents[2]
        return base_dir / path

    def _init_model(self) -> None:
        if self.mode != "gguf":
            logger.info("AI model mode set to %s", self.mode)
            return
        try:
            from llama_cpp import Llama

            self.model = Llama(
                model_path=str(self.model_path),
                n_ctx=settings.ai_model_ctx,
                n_threads=settings.ai_model_threads,
            )
            logger.info("Loaded GGUF model from %s", self.model_path)
        except Exception as exc:
            logger.warning("Failed to load GGUF model: %s", exc)
            self.mode = "hello"
            self.model = None

    async def generate(self, prompt: str) -> str:
        """Gera uma resposta assíncrona para o prompt fornecido usando o modelo GGUF.
        Retorna 'ola' se o modelo não estiver carregado.

        :param prompt: texto de entrada para o modelo
        :return: texto gerado pelo modelo
        """
        if self.mode != "gguf" or self.model is None:
            return "ola"

        return await asyncio.to_thread(self._generate_sync, prompt)

    def _generate_sync(self, prompt: str) -> str:
        """Executa a inferência síncrona do modelo llama.cpp.

        :param prompt: texto de entrada
        :return: conteúdo gerado pelo modelo
        """
        result = self.model.create_chat_completion(
            messages=[
                {"role": "system", "content": "You are a helpful assistant."},
                {"role": "user", "content": prompt},
            ],
            temperature=settings.ai_model_temperature,
            max_tokens=settings.ai_model_max_tokens,
        )
        return result["choices"][0]["message"]["content"].strip()

ai_model: Optional[AiModel] = AiModel()

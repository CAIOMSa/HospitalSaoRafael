FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN apt-get update \
	&& apt-get install -y --no-install-recommends curl build-essential cmake git gcc g++ gfortran ninja-build \
	&& rm -rf /var/lib/apt/lists/* \
	&& pip install --upgrade pip setuptools wheel \
	&& CMAKE_ARGS="-DLLAMA_OPENBLAS=ON" pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000", "--reload"]

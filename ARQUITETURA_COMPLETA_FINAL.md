# 🏗️ ARQUITETURA COMPLETA - CRM SÃO RAFAEL

## 📋 Resumo Executivo

```
INFRAESTRUTURA:
├─ Servidor físico próprio (on-premise)
├─ Arquitetura Serverless/Lambda-like (on-demand)
├─ Volume WhatsApp: 200 msgs/mês (40 templates + 160 conversacionais)
├─ Hardware: i7, 16GB RAM, 500GB SSD (R$ 8-12k, one-time)
├─ Operacional: R$ 701/mês (25% mais barato que cloud VPS)
└─ Break-even: ~2 anos

COMPONENTES:
🟢 ESSENCIAL (24/7):     Spring Boot | PostgreSQL | Redis | RabbitMQ | Keycloak | Monitoring
🟡 FACULTATIVO (Ligável): LLM (Llama) | NLP2SQL | WhatsApp Bot | Templates
⚡ SERVERLESS:           Executam sob demanda, pagam apenas pelo tempo real
```

---

## 🎨 DIAGRAMA 1: Arquitetura Completa (Mermaid)

```mermaid
graph TB
    USER["👤 Usuário Web"]
    WHATSAPP_USER["📱 Cliente WhatsApp"]
    META_API["Meta WhatsApp Business API"]
    
    TRAEFIK["🛡️ Traefik<br/>Reverse Proxy"]
    REACT["⚛️ React SPA"]
    KEYCLOAK["🔐 Keycloak Auth"]
    
    SPRING["☕ Spring Boot API<br/>Java 17 - Hexagonal"]
    POSTGRES["🐘 PostgreSQL 15"]
    REDIS["⚡ Redis 7"]
    RABBITMQ["🐰 RabbitMQ"]
    
    PROMETHEUS["📈 Prometheus"]
    GRAFANA["📊 Grafana"]
    LOKI["📝 Loki Logs"]
    
    USER -->|HTTPS| TRAEFIK
    WHATSAPP_USER -->|Messages| META_API
    META_API -->|Webhooks| TRAEFIK
    
    TRAEFIK -->|/| REACT
    TRAEFIK -->|/api| SPRING
    TRAEFIK -->|/auth| KEYCLOAK
    
    REACT -->|OAuth2| KEYCLOAK
    REACT -->|REST| SPRING
    
    SPRING -->|JPA| POSTGRES
    SPRING -->|Cache| REDIS
    SPRING -->|Events| RABBITMQ
    
    SPRING -->|Metrics| PROMETHEUS
    SPRING -->|Logs| LOKI
    PROMETHEUS -->|Display| GRAFANA
    LOKI -->|Display| GRAFANA
    
    classDef essential fill:#90EE90,stroke:#006400,stroke-width:3px,color:#000000
    class TRAEFIK,REACT,KEYCLOAK,SPRING,POSTGRES,REDIS,RABBITMQ,PROMETHEUS,GRAFANA,LOKI essential
```

---

## 🟢 ESSENCIAL (Componentes Sempre Ligados 24/7)

```mermaid
graph TB
    USER["👤 Usuário Web"]
    WHATSAPP["📱 Meta WhatsApp API"]
    
    TRAEFIK["🛡️ Traefik<br/>Reverse Proxy"]
    REACT["⚛️ React Frontend"]
    KEYCLOAK["🔐 Keycloak Auth"]
    
    SPRING["☕ Spring Boot API<br/>Java 17 - Hexagonal"]
    POSTGRES["🐘 PostgreSQL 15"]
    REDIS["⚡ Redis 7"]
    RABBITMQ["🐰 RabbitMQ"]
    
    PROMETHEUS["📈 Prometheus"]
    GRAFANA["📊 Grafana"]
    LOKI["📝 Loki Logs"]
    
    USER -->|HTTPS| TRAEFIK
    WHATSAPP -->|Webhooks| TRAEFIK
    
    TRAEFIK -->|/| REACT
    TRAEFIK -->|/api| SPRING
    TRAEFIK -->|/auth| KEYCLOAK
    
    REACT -->|OAuth2| KEYCLOAK
    REACT -->|REST| SPRING
    
    SPRING -->|JPA| POSTGRES
    SPRING -->|Cache| REDIS
    SPRING -->|Events| RABBITMQ
    
    SPRING -->|Metrics| PROMETHEUS
    SPRING -->|Logs| LOKI
    PROMETHEUS -->|Display| GRAFANA
    LOKI -->|Display| GRAFANA
    
    classDef essential fill:#90EE90,stroke:#006400,stroke-width:3px,color:#000000
    class TRAEFIK,REACT,KEYCLOAK,SPRING,POSTGRES,REDIS,RABBITMQ,PROMETHEUS,GRAFANA,LOKI essential
```

---

## 🟡 FACULTATIVO (Pode Ser Desligado)

```mermaid
graph TB
    SPRING["☕ Spring Boot<br/>Core System"]
    RMQ_QUEUES["🐰 RabbitMQ Queues<br/>ai-tasks, webhook,<br/>whatsapp, reports"]
    
    AI_SERVICE["Python AI Service<br/>FastAPI + Celery"]
    LLM["🧠 Llama-3.1-8B-Instruct<br/>GPU-Accelerated<br/>8B Parameters"]
    
    NLP2SQL["🔍 NLP2SQL Module<br/>Query Generation<br/>SQL Validation"]
    
    FAQ["❓ FAQ Engine<br/>Knowledge Base<br/>Intent Recognition"]
    
    CHAT_MANAGER["💬 Chat Manager<br/>Bot Orchestration<br/>Human Handoff<br/>Sentiment Analysis"]
    
    WEBHOOK["🔗 Webhook Handler<br/>Meta WhatsApp Integration<br/>Message receive/send"]
    
    TEMPLATE["📋 Template Service<br/>Appointment Reminders<br/>Marketing Campaigns"]
    
    POSTGRES["🐘 PostgreSQL<br/>Customer Data"]
    MINIO["🗄️ MinIO/S3<br/>Reports & Media"]
    
    SPRING --> RMQ_QUEUES
    RMQ_QUEUES --> AI_SERVICE
    
    AI_SERVICE --> LLM
    AI_SERVICE --> NLP2SQL
    AI_SERVICE --> FAQ
    
    WEBHOOK --> RMQ_QUEUES
    WEBHOOK --> CHAT_MANAGER
    
    CHAT_MANAGER --> LLM
    CHAT_MANAGER --> FAQ
    CHAT_MANAGER --> SPRING
    
    TEMPLATE --> WEBHOOK
    
    NLP2SQL --> POSTGRES
    AI_SERVICE --> MINIO
    
    classDef facultative fill:#FFD700,stroke:#FF8C00,stroke-width:3px,color:#000000
    classDef core fill:#90EE90,stroke:#006400,stroke-width:2px,color:#000000
    
    class AI_SERVICE,LLM,NLP2SQL,FAQ,CHAT_MANAGER,WEBHOOK,TEMPLATE facultative
    class SPRING,RMQ_QUEUES,POSTGRES,MINIO core
```



---

## 📊 FLUXOS PRINCIPAIS

### Fluxo 1: Cadastro de Cliente (Essencial)

```mermaid
sequenceDiagram
    participant U as 👤 Usuário
    participant R as ⚛️ React
    participant T as 🔀 Traefik
    participant K as 🔐 Keycloak
    participant S as ☕ Spring Boot
    participant P as 🐘 PostgreSQL
    participant C as ⚡ Redis Cache
    participant L as 📝 Loki

    U->>R: Acessa /customers/new
    R->>K: Valida JWT Token
    K-->>R: Token OK
    R->>T: POST /api/v1/customers
    T->>S: Forward Request
    S->>S: BaseController.create()
    S->>S: CustomerService.createCustomer()
    S->>P: INSERT customer
    P-->>S: Customer saved (ID: 123)
    S->>C: Cache customer:123
    S->>L: Log success
    S-->>T: 201 Created + Customer Data
    T-->>R: Response
    R-->>U: Exibe confirmação
```

---

### Fluxo 2: Lembrete de Consulta via WhatsApp (Scheduler)

```mermaid
sequenceDiagram
    participant S as ☕ Spring Boot
    participant RMQ as 🐰 RabbitMQ
    participant TS as 📋 Template Service
    participant META as 📱 Meta API
    participant WU as 👤 Cliente WhatsApp
    participant P as 🐘 PostgreSQL

    Note over S: Scheduler executa 08:00 AM
    S->>P: SELECT appointments WHERE date = tomorrow
    P-->>S: [Consultas do próximo dia]
    
    loop Para cada consulta
        S->>RMQ: Publish whatsapp-queue
        RMQ->>TS: Consume message
        TS->>P: Get customer phone
        P-->>TS: +55 11 99999-9999
        TS->>META: Send Template Message
        Note over META: Template: "Olá {nome}, lembrete<br/>de consulta amanhã às {hora}"
        META->>WU: WhatsApp Notification
        META-->>TS: Message Status (sent/delivered/read)
        TS->>P: Update message_log
    end
```

**Volume:** 40 templates/mês = ~1.3/dia  
**Tempo:** ~10-30s por vez  
**Custo:** Mínimo (apenas scheduler)

---

### Fluxo 3: Chat Conversacional com LLM (On-Demand)

```mermaid
sequenceDiagram
    participant WU as 👤 Cliente WhatsApp
    participant META as 📱 Meta API
    participant WH as 🔗 Webhook Handler
    participant RMQ as 🐰 RabbitMQ
    participant CS as 💬 Conversation Service
    participant FAQ as ❓ FAQ Engine
    participant LLM as 🧠 Llama-3.1-8B
    participant SB as ☕ Spring Boot
    participant P as 🐘 PostgreSQL

    WU->>META: "Olá, gostaria de agendar consulta"
    META->>WH: Webhook: message.received
    WH->>RMQ: Publish whatsapp-queue
    RMQ->>CS: Consume message
    
    CS->>P: Get conversation history
    P-->>CS: Previous messages (if any)
    
    CS->>FAQ: Check if FAQ question
    FAQ-->>CS: Not FAQ, needs LLM
    
    CS->>LLM: Generate response
    Note over LLM: Context: Customer wants appointment<br/>Action: Collect info (name, date, time)
    LLM-->>CS: "Claro! Para agendar, preciso de<br/>seu nome completo."
    
    CS->>META: Send message
    META->>WU: WhatsApp response
    
    WU->>META: "João Silva"
    META->>WH: Webhook: message.received
    WH->>RMQ: Publish whatsapp-queue
    RMQ->>CS: Consume message
    
    CS->>LLM: Continue conversation
    LLM-->>CS: "Ótimo, João! Qual data prefere?"
    CS->>META: Send message
    META->>WU: WhatsApp response
    
    alt Cliente satisfeito
        LLM-->>CS: "Appointment info collected"
        CS->>SB: POST /api/v1/appointments
        SB->>P: INSERT appointment
        P-->>SB: Saved
        CS->>META: "Consulta agendada com sucesso!"
        META->>WU: Confirmation
    else Cliente confuso/insatisfeito
        CS->>CS: Detect frustration
        CS->>SB: Create support ticket
        CS->>META: "Vou transferir para um atendente..."
        META->>WU: Human handoff message
    end
```

**Volume:** 160 msgs/mês = 5.3/dia  
**Tempo:** 5-10s por mensagem  
**Custo GPU:** 160 msgs × 7.5s ≈ 20 min/mês

---

### Fluxo 4: NLP2SQL para Relatórios (On-Demand)

```mermaid
sequenceDiagram
    participant U as 👤 Gerente
    participant R as ⚛️ React
    participant S as ☕ Spring Boot
    participant RMQ as 🐰 RabbitMQ
    participant NLP as 🔍 NLP2SQL
    participant LLM as 🧠 Llama-3.1-8B
    participant P as 🐘 PostgreSQL
    participant MIN as 🗄️ MinIO

    U->>R: "Quantos clientes novos este mês?"
    R->>S: POST /api/v1/reports/nlp-query
    S->>RMQ: Publish reports-queue
    RMQ->>NLP: Consume query
    
    NLP->>LLM: Parse natural language
    Note over LLM: Input: "Quantos clientes novos este mês?"<br/>Schema: customers table
    
    LLM-->>NLP: Generated SQL
    Note over NLP: SELECT COUNT(*) FROM customers<br/>WHERE DATE_TRUNC('month', created_at)<br/>= DATE_TRUNC('month', CURRENT_DATE)
    
    NLP->>NLP: Validate SQL (safety check)
    NLP->>P: Execute query
    P-->>NLP: Result: {total: 42}
    
    NLP->>LLM: Format response
    LLM-->>NLP: "Houve 42 novos clientes<br/>neste mês (fevereiro/2026)."
    
    NLP->>MIN: Store report (PDF/Excel)
    NLP->>RMQ: Publish result
    RMQ->>S: Consume result
    S-->>R: WebSocket notification
    R-->>U: Exibe relatório + download link
```

**Frequência:** Sob demanda  
**Tempo:** 5-30s (primeira execução)  
**Custo GPU:** ~22 min/mês

---

## ⚙️ ATIVAÇÃO/DESATIVAÇÃO DO MÓDULO FACULTATIVO

```mermaid
graph LR
    ENV["⚙️ Environment Variable<br/>AI_MODULE_ENABLED=true/false"]
    UI["🖥️ Admin Web UI<br/>Toggle Switch"]
    FLAG["🚩 Feature Flag<br/>Redis Cache"]
    
    ESSENTIAL["🟢 Essencial<br/>(Always ON)"]
    FACULTATIVE["🟡 Facultativo<br/>(ON/OFF)"]
    
    ENV --> FLAG
    UI --> FLAG
    
    FLAG -->|enabled=true| ESSENTIAL
    FLAG -->|enabled=true| FACULTATIVE
    FLAG -->|enabled=false| ESSENTIAL
    
    ESSENTIAL -.->|Works without| FACULTATIVE
    

    style FACULTATIVE fill:#FFD700,stroke:#FF8C00,stroke-width:3px,color:#000000
    style ESSENTIAL fill:#90EE90,stroke:#006400,stroke-width:3px,color:#000000
```

**Configuração em tempo real via:**
- Variável ambiente (.env)
- Admin Interface (toggle switch)
- Feature Flag (Redis, sem restart)

---

## 📡 OBSERVABILIDADE COMPLETA

```mermaid
graph TB
    subgraph SOURCES["Fontes de Dados"]
        SB["☕ Spring Boot"]
        AI["🐍 AI Service"]
        WH["🔗 Webhook"]
    end
    
    subgraph COLLECTION["Coleta"]
        PROM["📈 Prometheus<br/>Métricas"]
        PROMTAIL["📤 Promtail<br/>Log Shipper"]
        JAEGER["🔍 Jaeger<br/>Tracing"]
    end
    
    subgraph STORAGE["Armazenamento"]
        LOKI["📝 Loki<br/>Logs"]
    end
    
    subgraph VISUALIZATION["Visualização"]
        GRAF["🎨 Grafana<br/>Dashboards"]
    end
    
    SB --> PROM
    AI --> PROM
    WH --> PROM
    
    SB --> PROMTAIL
    AI --> PROMTAIL
    WH --> PROMTAIL
    
    PROMTAIL --> LOKI
    
    SB --> JAEGER
    AI --> JAEGER
    
    PROM --> GRAF
    LOKI --> GRAF
    JAEGER --> GRAF
    
    classDef source fill:#87CEEB,stroke:#4682B4,color:#000000
    classDef monitor fill:#90EE90,stroke:#006400,color:#000000
    
    class SOURCES source
    class COLLECTION,STORAGE,VISUALIZATION monitor
```

### Dashboards Disponíveis:
- Request Rate (req/s)
- Error Rate (%)
- Latency (p50/p99)
- LLM Inference Time
- GPU Memory Usage
- Token Count
- Database Load
- Cache Hit Rate
- Active Users
- WhatsApp msgs/day
- Relatórios gerados/day

---

## 🖥️ PRÉ-REQUISITOS DO SERVIDOR FÍSICO

### Hardware Mínimo Viável (Recomendado)

```
CPU:        Intel Core i7 / AMD Ryzen 5 (4-6 cores)
RAM:        16 GB DDR4 / DDR5
Storage:    
├─ SSD 500GB para OS + Docker
├─ HDD 2TB para PostgreSQL backups
└─ HDD 1TB para MinIO (Object Storage)

Rede:       Gigabit Ethernet (1Gbps)
Fonte:      650W (fonte com redundância)
Cooling:    Ventilação adequada (rack/armário)
UPS:        Para evitar downtime

GPU (Opcional):
├─ Opção A: RTX 3060 local (R$ 2.500) → +R$ 80-120/mês energia
├─ Opção B: GPU Cloud Share (Vast.ai) ⭐ RECOMENDADO
│  └─ 42 min/mês × R$ 0,40/h = R$ 0,30/mês
├─ Opção C: CPU only (mais lento, mas funciona)
└─ MELHOR CUSTO: Opção B

CUSTO HARDWARE: R$ 8.000 - R$ 15.000 (one-time)
```

### Software Requerido

```
Ubuntu 22.04 LTS
Docker 24.0+
Docker Compose 2.0+
Java 17 JDK
Python 3.11
Git, curl, wget, htop, iotop
```

---

## 💰 ANÁLISE DE CUSTOS DETALHADA

### Cenário A: Cloud VPS (Traditional)

| Item | Custo Unit | Frequência | Mensal |
|------|-----------|-----------|--------|
| VPS (2vCPU, 4GB) | R$ 800 | Mensal | R$ 800 |
| GPU Cloud (150h) | R$ 0,50/h | ~150h | R$ 75 |
| Backup S3 (200GB) | R$ 0,10/GB | 200GB | R$ 20 |
| Domínio | R$ 40/ano | - | R$ 3,33 |
| SMTP (5k emails) | R$ 50 | Mensal | R$ 50 |
| Meta WhatsApp API | R$ 4 | Mensal | R$ 4 |
| **TOTAL** | | | **R$ 952,33/mês** |

**Anual:** R$ 11.428/ano  
**Break-even:** Imediato  
**SLA:** 99.9%

---

### Cenário B: On-Premise Mínimo ⭐ RECOMENDADO

#### SETUP INICIAL (One-time)
- Servidor (i7, 16GB, 500GB): **R$ 8.000**
- Rack/Cabeamento/Setup: **R$ 2.000**
- **Total:** **R$ 10.000**

#### AMORTIZAÇÃO (60 meses = 5 anos)
**R$ 10.000 / 60 = R$ 166,67/mês**

#### OPERACIONAL MENSAL

| Item | Custo |
|------|-------|
| Energia (servidor 24/7) | R$ 150 |
| Internet Fibra (300Mbps) | R$ 200 |
| S3 Backup (semanal) | R$ 30 |
| Manutenção preventiva | R$ 50 |
| Domínio | R$ 50 |
| SMTP (5k emails) | R$ 50 |
| GPU Cloud Share (42min/mês) | R$ 0,30 |
| Meta WhatsApp API | R$ 4 |
| **OPERACIONAL TOTAL** | **R$ 534,30** |

#### CUSTO MENSAL TOTAL
- Amortização: **R$ 167**
- Operacional: **R$ 534,30**
- **TOTAL: R$ 701,30/mês**

#### CUSTO ANUAL
- **Ano 1:** R$ 8.416 (inclui inicial)
- **Anos 2-5:** R$ 8.416/ano
- **Média 5 anos:** R$ 8.435/ano
- **Break-even:** ~2 anos
- **Economia vs Cloud:** R$ 251/mês = R$ 3.012/ano (26%)

---

### Cenário C: On-Premise Enterprise (Não recomendado para 200 msgs)

```
Setup:        R$ 48.000
Operacional:  R$ 1.500/mês
Ano 1 Total:  R$ 26.400
Total 5 anos: R$ 117.000

⚠️ ACIMA do cloud
✅ Justificado apenas se: SLA crítico + crescimento 10x
```

---

## 📊 TABELA COMPARATIVA (5 ANOS)

| Cenário | Ano 1 | Anos 2-5 | Total 5 Anos | Break-even |
|---------|-------|----------|--------------|-----------|
| **Cloud VPS** | R$ 11.428 | R$ 11.428/ano | **R$ 56.568** | Imediato |
| **On-Premise Mínimo** ⭐ | R$ 8.416 | R$ 8.416/ano | **R$ 41.680** | ~2 anos |
| **Economia** | -R$ 3.012 | R$ 3.012/ano | **+R$ 14.888 (26%)** | 18-24 meses |

---

## 🎯 RECOMENDAÇÃO FINAL: CENÁRIO B

```
✅ SETUP RECOMENDADO

INVESTIMENTO INICIAL: R$ 10.000
OPERACIONAL: R$ 701,30/mês
ANUAL: R$ 8.416

VANTAGENS:
• 26% economia no 1º ano
• 38% economia nos anos seguintes
• Escalável (adicione servidores)
• Custódia total dos dados
• Sem vendor lock-in
• Ambiente controlado

DESAFIOS:
• Requer manutenção manual
• Sem redundância automática
• Uptime depende do usuário
• Precisa knowledge Linux

ESCALABILIDADE:
├─ Ano 1: Setup mínimo (R$ 701/mês)
├─ Ano 2: +Backup server (R$ 1.100/mês)
├─ Ano 3: +Colocation (R$ 1.400-1.800/mês)
└─ Ano 5: Multi-region (R$ 2.500+/mês)
```

---

## 📌 RESUMO FINAL

```

ECONOMIA EM LONGO PRAZO (5 ANOS):
└─ R$ 14.888 mais barato que cloud VPS (26% economia)

SERVIDOR RECOMENDADO:
├─ i7 / 16GB RAM / 500GB SSD
├─ Investimento: R$ 10.000
├─ Operacional: R$ 701,30/mês
└─ Break-even: 2 anos

```

---

**Documento Consolidado e Completo**  
**Data:** 09 de Fevereiro de 2026  
**Versão:** 2.0 - Final com Merrmaids + Custos Otimizados + Meta API

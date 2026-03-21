# CRM São Rafael - Frontend

Sistema de gerenciamento de clientes com autenticação Keycloak, health check de serviços e controle de infraestrutura.

## 🚀 Tecnologias

- React 18
- React Router DOM
- Keycloak JS (autenticação)
- Axios (requisições HTTP)
- Recharts (gráficos)

## 📋 Funcionalidades

### 1. **Sistema de Login**
- Autenticação via Keycloak
- Suporte a RBAC (Role-Based Access Control)
- Refresh automático de tokens

### 2. **Dashboard**
- Visão geral do sistema
- Estatísticas de clientes
- Ações rápidas
- Status dos serviços

### 3. **Health Check**
- Monitoramento em tempo real de todos os serviços
- Verificação automática a cada 30 segundos
- Tempo de resposta de cada serviço
- Status geral do sistema (healthy/degraded/critical)

Serviços monitorados:
- CRM Core (Spring Boot)
- Python AI Service
- Keycloak
- PostgreSQL
- Redis
- RabbitMQ
- MinIO
- Prometheus
- Grafana

### 4. **Controle de Serviços**
- Visualização de status de cada serviço
- Iniciar/Parar/Reiniciar serviços individuais
- Ações em massa (iniciar/parar todos)
- Informações detalhadas (porta, ID, descrição)

### 5. **Gerenciamento de Clientes**
- Listagem de clientes
- Criar novo cliente
- Editar cliente existente
- Excluir cliente
- Filtros e busca

## 🛠️ Instalação

### Pré-requisitos
- Node.js 16+
- npm ou yarn

### Passos

1. **Instalar dependências:**
```bash
cd front-end
npm install
```

2. **Configurar variáveis de ambiente:**
Copie o arquivo `.env.example` para `.env` e ajuste conforme necessário:
```bash
cp .env.example .env
```

3. **Iniciar em modo desenvolvimento:**
```bash
npm start
```

O aplicativo estará disponível em `http://localhost:3000`

4. **Build para produção:**
```bash
npm run build
```

## 🐳 Docker

### Construir imagem:
```bash
docker build -t crm-frontend .
```

### Executar container:
```bash
docker run -p 80:80 crm-frontend
```

## 📁 Estrutura do Projeto

```
front-end/
├── public/
│   ├── index.html
│   ├── manifest.json
│   └── robots.txt
├── src/
│   ├── components/
│   │   ├── Navbar.js
│   │   ├── StatsCard.js
│   │   └── ServiceCard.js
│   ├── context/
│   │   └── AuthContext.js
│   ├── pages/
│   │   ├── Login.js
│   │   ├── Dashboard.js
│   │   ├── HealthCheck.js
│   │   ├── ServiceControl.js
│   │   └── Customers.js
│   ├── services/
│   │   └── api.js
│   ├── App.js
│   ├── App.css
│   ├── index.js
│   └── index.css
├── .env
├── .env.example
├── package.json
├── Dockerfile
└── README.md
```

## 🔐 Autenticação

O sistema utiliza Keycloak para autenticação. Configure o Keycloak com:

1. **Realm:** crm-realm
2. **Client ID:** crm-frontend
3. **Client Type:** Public
4. **Valid Redirect URIs:** http://localhost:3000/*
5. **Web Origins:** http://localhost:3000

## 🌐 Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `REACT_APP_API_URL` | URL da API backend | http://localhost:8081/api |
| `REACT_APP_KEYCLOAK_URL` | URL do Keycloak | http://localhost:8080 |
| `REACT_APP_KEYCLOAK_REALM` | Realm do Keycloak | crm-realm |
| `REACT_APP_KEYCLOAK_CLIENT_ID` | Client ID | crm-frontend |

## 🎨 Telas

### Login
- Integração com Keycloak
- Redirecionamento automático após autenticação

### Dashboard
- Cards de estatísticas
- Ações rápidas
- Status dos serviços

### Health Check
- Grid de cards de serviços
- Indicador de status (online/offline)
- Tempo de resposta
- Atualização manual e automática

### Controle de Serviços
- Lista de todos os serviços
- Botões de ação (start/stop/restart)
- Resumo de serviços ativos/inativos

### Clientes
- Tabela de clientes
- Modal de criação/edição
- Formulário completo com endereço

## 📱 PWA (Progressive Web App)

O aplicativo está configurado como PWA, permitindo:
- Instalação no dispositivo
- Funcionamento offline
- Ícones e splash screens personalizados

## 🧪 Testes

```bash
npm test
```

## 📦 Deploy

### Usando Docker Compose
O frontend já está incluído no `docker-compose.yml` principal:

```yaml
frontend:
  build: ./front-end
  ports:
    - "80:80"
  environment:
    - REACT_APP_API_URL=http://localhost:8081/api
```

Execute:
```bash
docker-compose up -d frontend
```

## 📝 Licença

Este projeto é parte do CRM São Rafael.

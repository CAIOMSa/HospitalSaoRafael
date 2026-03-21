#!/bin/bash

# Configuração do Keycloak via Admin REST API

KEYCLOAK_URL="http://localhost:8080"
ADMIN_USER="admin"
ADMIN_PASS="admin123"
REALM="crm-realm"

# 1. Login e obter token de acesso
echo "Obtendo token de admin..."
TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASS" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

echo "Token obtido: ${TOKEN:0:50}..."

# 2. Verificar se o client crm-frontend existe
echo "Verificando client crm-frontend..."
CLIENT_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/clients" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | grep -o '"id":"[^"]*","clientId":"crm-frontend"' | cut -d'"' -f4)

if [ -z "$CLIENT_ID" ]; then
  echo "Client não existe. Criando crm-frontend..."
  
  curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/clients" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "clientId": "crm-frontend",
      "enabled": true,
      "publicClient": true,
      "protocol": "openid-connect",
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": true,
      "redirectUris": [
        "http://localhost:3000/*",
        "http://127.0.0.1:3000/*"
      ],
      "webOrigins": [
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "+"
      ],
      "attributes": {
        "pkce.code.challenge.method": "S256"
      }
    }'
    
  echo "Client crm-frontend criado com sucesso!"
else
  echo "Client existe. Atualizando..."
  
  curl -s -X PUT "$KEYCLOAK_URL/admin/realms/$REALM/clients/$CLIENT_ID" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "id": "'"$CLIENT_ID"'",
      "clientId": "crm-frontend",
      "enabled": true,
      "publicClient": true,
      "protocol": "openid-connect",
      "standardFlowEnabled": true,
      "implicitFlowEnabled": false,
      "directAccessGrantsEnabled": true,
      "redirectUris": [
        "http://localhost:3000/*",
        "http://127.0.0.1:3000/*"
      ],
      "webOrigins": [
        "http://localhost:3000",
        "http://127.0.0.1:3000",
        "+"
      ],
      "attributes": {
        "pkce.code.challenge.method": "S256"
      }
    }'
    
  echo "Client atualizado!"
fi

# 3. Criar usuário de teste se não existir
echo "Verificando usuário admin..."
USER_EXISTS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM/users?username=admin" \
  -H "Authorization: Bearer $TOKEN" | grep -o '"username":"admin"')

if [ -z "$USER_EXISTS" ]; then
  echo "Criando usuário admin..."
  curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM/users" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "username": "admin",
      "enabled": true,
      "email": "admin@crm.com",
      "firstName": "Admin",
      "lastName": "User",
      "credentials": [{
        "type": "password",
        "value": "admin123",
        "temporary": false
      }]
    }'
  echo "Usuário admin criado!"
fi

echo "Configuração concluída!"

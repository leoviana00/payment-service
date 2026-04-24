# Payment Service

![CI](https://github.com/leoviana00/payment-service/actions/workflows/ci.yml/badge.svg)

Microserviço de pagamentos desenvolvido em Spring Boot como parte do ecossistema **DevOps Governance Platform**.

Este projeto representa uma aplicação de negócio real utilizada como alvo de build, testes, deploy automatizado e validações operacionais em pipelines CI/CD.

---

## 🎯 Objetivo

Simular um serviço corporativo responsável pelo processamento básico de pagamentos, permitindo integração com pipelines Jenkins e práticas modernas de DevOps.

---

## 🚀 Funcionalidades Atuais

- Criar pagamento
- Consultar pagamento por ID
- Listar pagamentos
- Confirmar pagamento
- Health Check com Spring Boot Actuator

---

## 🧩 Fluxo do Serviço

```text
PENDING → CONFIRMED
```

Todo pagamento nasce com status:

```text
PENDING
```

Após confirmação:

```text
CONFIRMED
```

## 🛠️ Stack Tecnológica

```text
Java 17
Spring Boot
Spring Web
Spring Data JPA
H2 Database
Spring Boot Actuator
Maven
```

## 📁 Estrutura do Projeto

```console
src/main/java/io/viana/payment_service/

├── controller
├── service
├── repository
├── entity
├── enums
├── dto (roadmap)
└── handler (roadmap)
```

## 📡 Endpoints

Criar pagamento

```bash
POST /api/payments
```

Listar pagamentos

```bash
GET /api/payments
```

Buscar por ID

```bash
GET /api/payments/{id}
```

Confirmar pagamento

```bash
PUT /api/payments/{id}/confirm
```

## 🧪 Exemplo de Requisição

Criar pagamento

```bash
curl -X POST "http://localhost:8082/api/payments" \
-H "Content-Type: application/json" \
-d "{\"customerName\":\"Leonardo\",\"amount\":150.00,\"currency\":\"BRL\"}"
```

## ❤️ Health Check

```bash
GET /actuator/health
```

Resposta esperada:

```json
{
  "status": "UP"
}
```

## ▶️ Executando Localmente

Rodar aplicação

```bash
./mvnw spring-boot:run
```

Porta padrão

```text
8082
```

## 🐳 Integração DevOps

Este serviço foi projetado para ser utilizado em pipelines automatizadas com:

```console
Jenkins
Docker
Health Checks
Evidence Generation
Governance Approval Flow
```

## 🔗 Ecossistema Relacionado

- [devops-governance-platform](https://github.com/leoviana00/devops-governance-platform)
- [change-governance-service](https://github.com/leoviana00/change-governance-service)
# 💸 Transactions API

Uma API RESTful para gerenciamento de transações financeiras desenvolvida com Spring Boot.

## 📝 Descrição

A Transactions API permite que usuários registrem transações financeiras (créditos e débitos), visualizem todas as suas transações, obtenham detalhes de uma transação específica e visualizem um resumo de suas finanças.

A aplicação utiliza cookies de sessão para identificar os usuários, garantindo que cada usuário tenha acesso apenas às suas próprias transações.

## ✨ Funcionalidades e Requisitos

- Criação de transações (crédito/débito)
- Listagem de todas as transações do usuário
- Detalhes de uma transação específica
- Resumo financeiro (saldo total)
- Gerenciamento de sessão via cookies

### RF

- [x] O usuário deve poder criar uma nova transação;
- [x] O usuário deve poder obter um resumo da sua conta;
- [x] O usuário deve poder listar todas transações que já ocorreram;
- [x] O usuário deve poder visualizar uma transação única;

### RN

- [x] A transação pode ser do tipo crédito que somará ao valor total, ou débito subtrairá;
- [x] Deve ser possível identificarmos o usuário entre as requisições;
- [x] O usuário só pode visualizar transações o qual ele criou;

## 🔗 Endpoints da API

### Criar Transação 💰
- **POST** `/transactions`
- **Body**: 
```json
{
  "title": "Salário",
  "amount": 1000.00,
  "type": "credit"
}
```
- **Resposta**: 201 Created
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
  "title": "Salário",
  "amount": 1000.00,
  "type": "credit",
  "createdAt": "2025-05-21T10:30:45"
}
```

### Listar Todas as Transações 📋
- **GET** `/transactions`
- **Resposta**: 200 OK
```json
[
  {
    "id": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
    "title": "Salário",
    "amount": 1000.00,
    "type": "credit",
    "createdAt": "2025-05-21T10:30:45"
  },
  {
    "id": "b2c3d4e5-f6a7-8901-bcde-2345678901bc",
    "title": "Aluguel",
    "amount": 500.00,
    "type": "debit",
    "createdAt": "2025-05-21T11:15:30"
  }
]
```

### Obter Transação por ID 🔍
- **GET** `/transactions/{id}`
- **Resposta**: 200 OK
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-1234567890ab",
  "title": "Salário",
  "amount": 1000.00,
  "type": "credit",
  "createdAt": "2025-05-21T10:30:45"
}
```

### Obter Resumo Financeiro 📊
- **GET** `/transactions/summary`
- **Resposta**: 200 OK
```json
{
  "total": 500.00
}
```

## 🔐 Gerenciamento de Sessão

A API utiliza cookies de sessão para identificar os usuários. Um UUID é gerado na primeira transação e armazenado como cookie no navegador do usuário. Todas as transações subsequentes são associadas a este UUID de sessão.

O serviço `SessionService` gerencia a criação e obtenção desses IDs de sessão, garantindo que cada usuário acesse apenas suas próprias transações.

## ⚙️ Configuração e Execução

### Pré-requisitos ✅
- Java 21
- Maven

### Etapas para execução 🚦

1. Clone o repositório:
```
git clone https://github.com/seu-usuario/transactions-api.git
cd transactions-api
```

2. Compile o projeto:
```
mvn clean package
```

3. Execute a aplicação:
```
java -jar target/transactions-0.0.1-SNAPSHOT.jar
```

Ou usando o Maven:
```
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 🧪 Testes

O projeto inclui testes unitários para os serviços e controladores. Para executar os testes:

```
mvn test
```
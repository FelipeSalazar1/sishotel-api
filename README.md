# SisHotel API — Sistema de Reserva de Hotel

API REST para gestão de reservas de hotel, cobrindo o ciclo completo **reserva → check-in → check-out**, desenvolvida em Java 20 + Spring Boot 3.3.5.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 20 |
| Framework | Spring Boot 3.3.5 |
| Persistência | Spring Data JPA + H2 (in-memory) |
| Migrações | Flyway |
| Segurança | Spring Security + JWT (jjwt 0.12.7) |
| Validação | Jakarta Bean Validation |
| Documentação | SpringDoc OpenAPI 2.6.0 (Swagger UI) |
| Build | Maven 3.x |

---

## Como executar localmente

**Pré-requisitos:** Java 20 e Maven 3.8+

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd sishotel-api

# 2. Compile e suba
./mvnw spring-boot:run
```

A aplicação sobe em **http://localhost:8080**

---

## Banco de dados

O banco H2 in-memory é criado automaticamente ao subir a aplicação.

- **Console H2:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:sishotel`
  - Usuário: `sa` / Senha: (vazio)

### Migrações Flyway

| Arquivo | Descrição |
|---|---|
| `V1__init.sql` | Criação das tabelas guests, rooms, reservations e índices |
| `V2__seed.sql` | Dados iniciais: 2 hóspedes, 3 quartos e 1 reserva de exemplo |

---

## Autenticação

Todos os GETs são públicos. Operações de escrita (POST, PUT, PATCH, DELETE) requerem token JWT.

```bash
# Obter token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# Resposta
{"token":"eyJ..."}

# Usar nas requisições autenticadas
-H "Authorization: Bearer eyJ..."
```

---

## Endpoints

### Autenticação
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| POST | /auth/login | Obter token JWT | Não |

### Hóspedes /api/v1/guests
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | /api/v1/guests | Listar todos | Não |
| GET | /api/v1/guests/{id} | Buscar por ID | Não |
| POST | /api/v1/guests | Cadastrar | Sim |
| PUT | /api/v1/guests/{id} | Atualizar | Sim |
| DELETE | /api/v1/guests/{id} | Remover | Sim |

### Quartos /api/v1/rooms
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | /api/v1/rooms | Listar todos | Não |
| GET | /api/v1/rooms/{id} | Buscar por ID | Não |
| GET | /api/v1/rooms/available?checkin=YYYY-MM-DD&checkout=YYYY-MM-DD | Disponíveis no período | Não |
| POST | /api/v1/rooms | Cadastrar | Sim |
| PUT | /api/v1/rooms/{id} | Atualizar | Sim |
| DELETE | /api/v1/rooms/{id} | Desativar (exclusão lógica) | Sim |

### Reservas /api/v1/reservations
| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| GET | /api/v1/reservations | Listar todas | Não |
| GET | /api/v1/reservations/{id} | Buscar por ID | Não |
| GET | /api/v1/reservations/guest/{guestId} | Reservas de um hóspede | Não |
| POST | /api/v1/reservations | Criar reserva | Sim |
| PATCH | /api/v1/reservations/{id}/checkin | Realizar check-in | Sim |
| PATCH | /api/v1/reservations/{id}/checkout | Realizar check-out | Sim |
| PATCH | /api/v1/reservations/{id}/cancel | Cancelar reserva | Sim |

---

## Swagger UI

Acesse: http://localhost:8080/swagger-ui.html

---

## Exemplos cURL

### Criar hóspede
```bash
curl -X POST http://localhost:8080/api/v1/guests \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"fullName":"João Costa","document":"11122233300","email":"joao@example.com","phone":"+55-11-91234-5678"}'
```

### Criar quarto
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"number":401,"type":"SUITE","capacity":4,"pricePerNight":650.00}'
```

### Criar reserva
```bash
curl -X POST http://localhost:8080/api/v1/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN>" \
  -d '{"guestId":"11111111-1111-1111-1111-111111111111","roomId":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa","checkinExpected":"2026-06-10","checkoutExpected":"2026-06-13","numGuests":2}'
```

### Check-in / Check-out / Cancelar
```bash
curl -X PATCH http://localhost:8080/api/v1/reservations/{id}/checkin  -H "Authorization: Bearer <TOKEN>"
curl -X PATCH http://localhost:8080/api/v1/reservations/{id}/checkout -H "Authorization: Bearer <TOKEN>"
curl -X PATCH http://localhost:8080/api/v1/reservations/{id}/cancel   -H "Authorization: Bearer <TOKEN>"
```

---

## Regras de Negócio

| # | Regra | HTTP |
|---|---|---|
| 1 | checkoutExpected > checkinExpected | 400 InvalidDateRangeException |
| 2 | Quarto sem sobreposição de datas (exceto CANCELED) | 409 RoomUnavailableException |
| 3 | numGuests <= capacidade do quarto | 400 CapacityExceededException |
| 4 | FSM: CREATED->CHECKED_IN->CHECKED_OUT / CREATED->CANCELED | 409 InvalidReservationStateException |
| 5 | Check-in somente a partir de checkinExpected | 409 |
| 6 | valorFinal = max(1, dias_efetivos) × pricePerNight | — |
| 7 | Quartos com reservas ativas não podem ser desativados | 409 RoomHasReservationsException |

### FSM de Status da Reserva
CREATED -> CHECKED_IN -> CHECKED_OUT
CREATED -> CANCELED

---

## Payload de erro padrão
```json
{
  "timestamp": "2026-05-05T14:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Quarto 101 indisponível no período solicitado",
  "path": "/api/v1/reservations"
}
```

---

## Decisões de Arquitetura (ADRs)

**ADR-1: Arquitetura em 3 Camadas (MVC)**
Controller recebe HTTP e delega ao Service. Service contém toda a lógica de domínio. Repository acessa dados via Spring Data JPA.

**ADR-2: H2 + Flyway**
Optou-se por `ddl-auto=none` com migrações Flyway versionadas, garantindo rastreabilidade e portabilidade para MySQL/PostgreSQL sem alteração de código.

**ADR-3: Exclusão lógica de Quartos**
Quartos com reservas ativas não são excluídos fisicamente. DELETE /rooms/{id} marca como INATIVO (retorna 409 se houver conflito), preservando histórico.

# Contrato de API — Gestão de equipe (`/api/v1/users`, `/api/v1/invitations`)

Documentação de contrato das rotas do `ms-administrative-core` para as telas do app mobile: card "Funcionários" na home do gestor, lista de funcionários, e convites pendentes.

## Autenticação e headers comuns

| Header | Obrigatório | Descrição |
|---|---|---|
| `Authorization` | Sim (exceto resgate) | `Bearer <access_token>` — JWT emitido no login (`POST /api/v1/auth/login`). `sub` = `userId`. Claim `role` vira `ROLE_*`. |
| `X-Unit-Id` | Não | A unidade **não** vem de header: gestores usam o `unitId` do usuário autenticado no token/DB. |

Rotas de gestão (`GET/POST` de usuários e convites, exceto resgate) exigem papel `MANAGER`. Token ausente/inválido: `401`. Ator autenticado sem `MANAGER`: `403`.

`POST /api/v1/invitations/redeem` é público neste serviço (`permitAll`): o funcionário ainda não tem conta.

Este microserviço **não** valida header `apiKey`. Se a chamada passar pelo gateway, o edge pode exigir `x-api-key` à parte.

Não há envelope de paginação (`totalElements`/`totalPages`) em `GET /api/v1/users`: a resposta é um array simples (`List<UserOutput>`).

---

## 1. `GET /api/v1/users` — listar/contar funcionários (card + `EmployeesScreen`)

Endpoint único cobre lista e filtros. Rota exige `MANAGER`.

### Request

```
GET /api/v1/users?role=EMPLOYEE&status=ACTIVE&managerId=<managerId>&page=0&size=50 HTTP/1.1
Authorization: Bearer <token>
```

| Query param | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `role` | `MANAGER`\|`EMPLOYEE` | Não | Filtra por papel. Para a tela de equipe, mandar `role=EMPLOYEE`. |
| `status` | `ACTIVE`\|`INACTIVE`\|`SUSPENDED` | Não | Filtra por status. Card/tela de "Equipe ativa" devem mandar `status=ACTIVE`. |
| `managerId` | UUID | Não | Filtra pelos funcionários daquele gestor. App manda o `userId` do gestor logado. |
| `unitId` | UUID | Não | Filtra pela unidade. Combinável com `role`/`status`/`managerId`. |
| `email` | string | Não | Se presente, **ignora os demais filtros** e devolve só o usuário daquele e-mail (lista com 1 item). E-mail inexistente: `404`. Não usar nesta tela. |
| `page` / `size` | int | Não (default `0`/`20`) | Paginação 0-based, aplicada só quando **não** há `email`. |

```bash
curl -X GET "https://<host>/api/v1/users?role=EMPLOYEE&status=ACTIVE&managerId=3fa85f64-5717-4562-b3fc-2c963f66afa6" \
  -H "Authorization: Bearer eyJhbGciOi..."
```

### Response — `200 OK`

```json
[
  {
    "userId": "9c858901-8a57-4791-81fe-4c455b099bc9",
    "name": "João Silva",
    "email": "joao@empresa.com",
    "role": "EMPLOYEE",
    "status": "ACTIVE",
    "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "createdAt": "2026-07-01T09:00:00",
    "updatedAt": "2026-08-01T10:00:00",
    "managerId": "aa11bb22-0000-0000-0000-000000000009",
    "imageUrl": "https://cdn.example.com/avatars/joao.png"
  }
]
```

`email` serializa como string (value object com `@JsonValue`). `managerId` só vem preenchido quando o usuário é `EMPLOYEE`; gestor tem `managerId: null`. `imageUrl` é URL absoluta do avatar ou `null` quando não há foto — usar na lista de funcionários; o upload em si fica fora deste serviço (app envia só a URL via `PATCH /users/{id}/image`).

O mesmo shape de `UserOutput` vale para `GET /api/v1/users/{id}` (detalhe de um usuário; exige `SELF_OR_MANAGER`, não só gestor).

`role` na API é `MANAGER`/`EMPLOYEE`; o rótulo de UI (ex. "Operador") é mapeamento do app, não vem do backend.

**Definição de "ativo" e "pendente" para o card e a lista:**

- **Ativo** = usuário com `status == ACTIVE`. O gestor logado **não** entra na contagem: o card e a tela filtram `role=EMPLOYEE`, e o gestor é `role=MANAGER`.
- **Pendente** = convite ainda não resgatado e **não expirado**. Não existe usuário `INACTIVE` esperando ativação: o cadastro do funcionário só é criado no `redeem`, já como `ACTIVE`. `isPending` na UI **não** vem de `GET /users`: a tela deve compor a lista de funcionários (`GET /users?role=EMPLOYEE`) com `GET /invitations/pending` e marcar os convites como linhas "Pendente".

### Contagem para o card "Funcionários" e o header "{n} pessoas"

Duas opções — escolher uma e manter as duas telas consistentes:

- `GET /api/v1/users?role=EMPLOYEE&status=ACTIVE&managerId={managerId}` e usar o tamanho do array JSON (se a lista puder passar de uma página, subir `size`; não há `totalElements`).
- `GET /api/v1/users/count-by-manager` e pegar `count` da linha cujo `managerId` é o do gestor logado. Esta rota **já conta só `EMPLOYEE` `ACTIVE`**.

Para contagem exata sem depender de `page`/`size`, prefira `count-by-manager`.

### 1.1 `PATCH /api/v1/users/{id}/image` — avatar na equipe (opcional)

Atualiza a URL da foto de perfil de um funcionário (ou do próprio gestor). Exige `SELF_OR_MANAGER` (gestor pode alterar avatar de funcionário da equipe).

```json
{ "imageUrl": "https://cdn.example.com/avatar.png" }
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `imageUrl` | string | Não | Nova URL. Body `{}` ou campo omitido limpa o avatar (`imageUrl` passa a `null`). |

Resposta: `204 No Content`. Usuário inexistente: `400` (`User not found`).

---

## 2. `GET /api/v1/users/count-by-manager`

Devolve, para cada gestor que tem pelo menos um funcionário ativo, quantos `EMPLOYEE` com `status = ACTIVE` e `managerId` preenchido aquele gestor tem. Gestores sem funcionários ativos **não** aparecem na lista.

Rota exige `MANAGER`.

### Request

```
GET /api/v1/users/count-by-manager HTTP/1.1
Authorization: Bearer <token>
```

### Response — `200 OK`

```json
[
  { "managerId": "aa11bb22-0000-0000-0000-000000000009", "count": 43 }
]
```

App deve filtrar a linha do `managerId` do usuário logado. Se o gestor não estiver no array, a contagem é `0`.

---

## 3. Convites

### 3.1 `GET /api/v1/invitations/pending` — convites pendentes do gestor (`InviteCard`)

Rota exige `MANAGER`. Lista só convites `PENDING` **com `expiresAt` no futuro**, ordenados por `expiresAt` crescente. Convites vencidos que ainda estão `PENDING` **não** entram na resposta (não existe status `EXPIRED`; vencimento é checagem de tempo).

### Request

```
GET /api/v1/invitations/pending?managerId=<managerId> HTTP/1.1
Authorization: Bearer <token>
```

| Query param | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `managerId` | UUID | Sim | App manda o `userId` do gestor logado. Ausente: `400`. |

```bash
curl -X GET "https://<host>/api/v1/invitations/pending?managerId=aa11bb22-0000-0000-0000-000000000009" \
  -H "Authorization: Bearer eyJhbGciOi..."
```

### Response — `200 OK`

Array (pode ser vazio). Cada item tem **apenas** código, nome do convidado e validade:

```json
[
  {
    "code": "120443",
    "inviteeName": "Operadora Carol the Best",
    "expiresAt": "2026-10-03T09:00:00"
  }
]
```

- `inviteeName` é o nome informado na geração do convite (não o e-mail; e-mail só existe depois do `redeem`).
- `expiresAt` é timestamp absoluto (ISO-8601, `LocalDateTime` sem timezone). O app calcula "expira em N horas" com `expiresAt - now()`.
- Não devolve `id`, `managerId`, `unitId`, `status`, `createdAt` nem `usedByUserId`.
- **Pendente visível** = `status == PENDING` e `expiresAt > now()` — o backend já aplica esse filtro.
- **Resgatado** = `status == USED`; some desta lista.
- **Expirado** = `PENDING` com `expiresAt` no passado; some desta lista (o registro continua no banco até um `redeem` falhar com `422` se alguém tentar o código).

### 3.2 `POST /api/v1/invitations` — criar convite

Rota exige `MANAGER`. O gestor informa o nome da pessoa convidada; o backend gera o código.

```json
{
  "managerId": "aa11bb22-0000-0000-0000-000000000009",
  "inviteeName": "Carol"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `managerId` | UUID | Sim | Gestor dono do convite. Precisa existir e ser `MANAGER`. |
| `inviteeName` | string | Sim | Nome para exibir no `InviteCard`. Não pode ser em branco. |

### Response — `201 Created`

```json
{
  "id": "cc33dd44-0000-0000-0000-000000000001",
  "code": "120443",
  "managerId": "aa11bb22-0000-0000-0000-000000000009",
  "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "inviteeName": "Carol",
  "expiresAt": "2026-10-03T09:00:00"
}
```

- Código: 6 dígitos numéricos, único entre os `PENDING` (`GenerateInvitationCodeImpl`).
- `unitId` copiado da unidade do gestor.
- TTL: **168 horas (7 dias)** a partir da criação (`EXPIRATION_HOURS = 168` — constante no código, não configurável via request).
- Botão "Novo" do app deve chamar esta rota com `managerId` = usuário logado e `inviteeName` digitado na UI.

| Status | Quando |
|---|---|
| `400` | Body inválido, `inviteeName` em branco, ou `managerId` não é um gestor. |
| `404` | `managerId` não existe. |

### 3.3 `POST /api/v1/invitations/redeem` — resgate (lado do funcionário)

Público neste serviço (sem JWT). Cria o funcionário já `ACTIVE`, vinculado ao `managerId` e `unitId` do convite, e marca o convite como `USED`.

```json
{
  "code": "120443",
  "name": "Maria Souza",
  "email": "maria@empresa.com",
  "rawPassword": "Senha123"
}
```

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `code` | string | Sim | Exatamente 6 dígitos (`\\d{6}`). |
| `name` | string | Sim | Nome da conta criada (pode diferir do `inviteeName` do convite). |
| `email` | string | Sim | E-mail válido, único. |
| `rawPassword` | string | Sim | Senha em claro; o backend faz o hash. |

### Response — `201 Created`

Header `Location: /api/v1/users/{userId}`.

```json
{
  "userId": "9c858901-8a57-4791-81fe-4c455b099bc9",
  "name": "Maria Souza",
  "email": "maria@empresa.com",
  "role": "EMPLOYEE",
  "managerId": "aa11bb22-0000-0000-0000-000000000009"
}
```

Não devolve `unitId` nem `status` neste payload (`status` do usuário criado é `ACTIVE`).

| Status | Quando |
|---|---|
| `400` | Validação do body (ex. código sem 6 dígitos). |
| `404` | Código inexistente ou já `USED` (`Invitation code not found or already used`). |
| `409` | E-mail já em uso. |
| `422` | Código ainda `PENDING`, mas `expiresAt` no passado. |

---

## Enums usados

- **`Role`**: `MANAGER`, `EMPLOYEE`.
- **`Status`** (do usuário): `ACTIVE`, `INACTIVE`, `SUSPENDED`.
- **`InvitationStatus`**: `PENDING`, `USED`. Não há `EXPIRED`.

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Contagem de funcionários ativos para o card da home | `GET /users/count-by-manager` (só ativos) ou `GET /users?role=EMPLOYEE&status=ACTIVE&managerId=` |
| Lista de funcionários (`EmployeesScreen`) | `GET /users?role=EMPLOYEE&managerId=` (adicionar `status=ACTIVE` se a tela for só ativos) |
| Avatar na lista / perfil do funcionário | Ler `imageUrl` em `GET /users` ou `GET /users/{id}`; gravar com `PATCH /users/{id}/image` |
| Convites pendentes (`InviteCard`) | `GET /invitations/pending?managerId=` |
| Criar convite | `POST /invitations` com `managerId` + `inviteeName` |
| Cadastro do funcionário via código | `POST /invitations/redeem` |
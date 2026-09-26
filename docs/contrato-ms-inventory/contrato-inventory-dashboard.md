# Contrato de API — `/api/v1/dashboard`

Documentação de contrato das rotas de dashboard do `ms-inventory`, para uso do app mobile.

## Autenticação e headers comuns

Todas as rotas exigem usuário autenticado (`isAuthenticated()`).

| Header | Obrigatório | Descrição |
|---|---|---|
| `Authorization` | Sim | `Bearer <access_token>` — JWT emitido pelo ms-administrative-core. O `sub` do token é o `userId` (UUID) e o claim `role` (`ROLE_MANAGER`/outro) define se o ator é `MANAGER` ou `EMPLOYEE`. O claim `name` é usado como nome do ator. |
| `X-Unit-Id` | Sim, em todas as 3 rotas | UUID da unidade (loja/filial) cujo painel será consultado. |

Se `X-Unit-Id` estiver ausente: `400 Bad Request` (ProblemDetail).
Se o token estiver ausente/inválido: `401 Unauthorized`.

### Formato de erro (RFC 7807 `ProblemDetail`)

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Required request header 'X-Unit-Id' for method parameter type UUID is not present",
  "instance": "/api/v1/dashboard/home"
}
```

---

## 1. `GET /api/v1/dashboard/home`

**O que essa rota entrega:** visão geral da unidade — quantidade de itens ativos em estoque (e sua variação percentual), ocupação da capacidade de armazenamento, contadores de pendências (aprovação, manutenção, avaliação), descartes na janela recente, e uma lista paginada dos itens mais recentes.

**Quando o cliente deve usar:** tela inicial/home do app, quando quer mostrar um resumo executivo da unidade sem detalhar pendências específicas de quem está logado. É a rota mais "pesada" em dados agregados (números para cards/gráficos) e inclui a lista de itens recentes já pronta para exibir, com paginação — não precisa de outra chamada para listar itens recentes.

### Request

```
GET /api/v1/dashboard/home?page=0&size=5 HTTP/1.1
Authorization: Bearer <token>
X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6
```

| Query param | Tipo | Obrigatório | Default | Descrição |
|---|---|---|---|---|
| `page` | int | Não | `0` | Página da lista de itens recentes (0-based). |
| `size` | int | Não | `5` | Tamanho da página. Só afeta `recentItems`; os contadores são sempre da unidade inteira. |

```bash
curl -X GET "https://<host>/api/v1/dashboard/home?page=0&size=5" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
```

### Response — `200 OK`

```json
{
  "activeItems": 128,
  "activeItemsChangePercent": 4.35,
  "stockCapacity": 200,
  "occupancyPercent": 64.0,
  "pendingApproval": 3,
  "inMaintenance": 2,
  "awaitingEvaluation": 1,
  "disposalsInWindow": 5,
  "windowDays": 30,
  "recentItems": {
    "content": [
      {
        "id": "9c858901-8a57-4791-81fe-4c455b099bc9",
        "barcode": "7891234567890",
        "displayCode": "ITM-0042",
        "name": "Notebook Dell Latitude",
        "status": "IN_STOCK",
        "condition": "USED",
        "hasDamages": false,
        "damages": [],
        "notes": null,
        "photoUrl": "https://storage.example.com/photos/item-9c85.jpg",
        "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        "model": {
          "id": "1b2c3d4e-0000-0000-0000-000000000001",
          "unitId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
          "name": "Latitude 5420",
          "manufacturer": "Dell",
          "warrantyMonths": 12,
          "expectedLifespanMonths": 48,
          "materials": [
            {"id": "m1", "name": "Plastic"}
          ],
          "hazardous": false,
          "estimatedWeightKg": 1.8,
          "notes": null,
          "category": {"id": "c1", "name": "Informática"},
          "approvalStatus": "APPROVED",
          "rejectionReason": null,
          "createdBy": "user-uuid",
          "reviewedBy": "user-uuid",
          "reviewedAt": "2026-08-01T10:00:00",
          "createdAt": "2026-07-01T09:00:00",
          "updatedAt": "2026-08-01T10:00:00"
        },
        "serialNumber": "SN123456",
        "acquiredAt": "2026-07-01",
        "manufacturingYear": 2024,
        "usageIntensity": 3,
        "predictedFailureDate": "2029-07-01",
        "predictionUpdatedAt": "2026-08-01T10:00:00",
        "missingFields": [],
        "lastEventAt": "2026-09-20T15:00:00",
        "createdBy": "user-uuid",
        "createdByName": "João Silva",
        "createdAt": "2026-07-01T09:00:00",
        "updatedAt": "2026-09-20T15:00:00"
      }
    ],
    "page": 0,
    "size": 5,
    "totalElements": 128,
    "totalPages": 26
  }
}
```

| Campo | Tipo | Observação |
|---|---|---|
| `activeItems` | long | Itens ativos (não descartados/removidos) na unidade. |
| `activeItemsChangePercent` | double\|null | Variação % vs. período anterior. `null` = sem estoque na janela anterior para comparar. |
| `stockCapacity` | int\|null | `null` = unidade sem capacidade configurada. |
| `occupancyPercent` | double\|null | `null` pela mesma razão de `stockCapacity`. |
| `pendingApproval` / `inMaintenance` / `awaitingEvaluation` / `disposalsInWindow` | long | Contadores da unidade inteira (não paginados). |
| `windowDays` | int | Tamanho da janela usada para `disposalsInWindow` e a variação (padrão do backend). |
| `recentItems` | objeto paginado | Ver `ItemResponse` completo acima; use `page`/`size`/`totalElements`/`totalPages` para paginar. |

---

## 2. `GET /api/v1/dashboard/work-center`

**O que essa rota entrega:** a "Central de Trabalho" — a lista do que a pessoa logada precisa resolver agora: rascunhos incompletos, itens reprovados (com motivo da reprovação), itens danificados sem destino definido, além dos contadores de manutenção e avaliação pendente.

**Quando o cliente deve usar:** telas de ação/pendências, tipo "o que eu preciso fazer hoje". Diferente da `/home`, aqui cada item pendente já vem com `missingFields` (o que falta preencher no rascunho) e `rejectionReason` (por que foi reprovado), prontos para guiar o usuário a corrigir. Não tem paginação — é a lista de pendências, esperada como pequena.

### Request

```
GET /api/v1/dashboard/work-center HTTP/1.1
Authorization: Bearer <token>
X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6
```

Sem query params.

```bash
curl -X GET "https://<host>/api/v1/dashboard/work-center" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
```

### Response — `200 OK`

```json
{
  "drafts": [
    {
      "id": "aa11bb22-0000-0000-0000-000000000001",
      "displayCode": "ITM-0099",
      "name": "Monitor LG 24pol",
      "modelName": "24ML600",
      "missingFields": ["serialNumber", "photoUrl"],
      "rejectionReason": null,
      "updatedAt": "2026-09-22T11:30:00"
    }
  ],
  "rejected": [
    {
      "id": "aa11bb22-0000-0000-0000-000000000002",
      "displayCode": "ITM-0088",
      "name": "Teclado Logitech",
      "modelName": "K120",
      "missingFields": [],
      "rejectionReason": "Foto ilegível, reenviar",
      "updatedAt": "2026-09-21T09:00:00"
    }
  ],
  "damagedWithoutDestination": [
    {
      "id": "aa11bb22-0000-0000-0000-000000000003",
      "displayCode": "ITM-0077",
      "name": "Mouse Dell",
      "modelName": "MS116",
      "missingFields": [],
      "rejectionReason": null,
      "updatedAt": "2026-09-19T14:00:00"
    }
  ],
  "inMaintenance": 2,
  "awaitingEvaluation": 1
}
```

| Campo | Tipo | Observação |
|---|---|---|
| `drafts` | lista | Rascunhos incompletos (`status = DRAFT`), com `missingFields` preenchido. |
| `rejected` | lista | Itens reprovados pelo gestor, com `rejectionReason`. |
| `damagedWithoutDestination` | lista | Itens danificados que ainda não tiveram um destino (manutenção/descarte) definido. |
| `inMaintenance` / `awaitingEvaluation` | long | Contadores, mesmos números da `/home`. |
| `PendingItemResponse.missingFields` | lista de string | Nomes dos campos obrigatórios que faltam (só relevante em `drafts`). |
| `PendingItemResponse.rejectionReason` | string\|null | Só preenchido em `rejected`. |

**Nota:** a resposta muda conforme quem está logado (role `MANAGER` vs `EMPLOYEE`), já que o use case usa o `Actor` do token — não é um filtro exposto via query param.

---

## 3. `GET /api/v1/dashboard/indicators`

**O que essa rota entrega:** indicadores de descarte (sustentabilidade/reciclagem) da unidade: peso total descartado, taxa de reciclagem, variações contra o período anterior, série mensal de peso descartado, e a divisão do peso por tipo de material.

**Quando o cliente deve usar:** telas de relatórios/gráficos de sustentabilidade (ex.: gráfico de barras mensal, gráfico de pizza por material). Único endpoint com filtro de período — útil se o app quiser deixar o usuário escolher um intervalo customizado; sem parâmetros, já vem com os últimos 12 meses prontos.

### Request

```
GET /api/v1/dashboard/indicators?from=2026-01-01&to=2026-09-26 HTTP/1.1
Authorization: Bearer <token>
X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6
```

| Query param | Tipo | Obrigatório | Default | Descrição |
|---|---|---|---|---|
| `from` | date (`YYYY-MM-DD`) | Não | — | Início do período. Se omitido junto de `to`, assume os últimos 12 meses. |
| `to` | date (`YYYY-MM-DD`) | Não | — | Fim do período. |

```bash
curl -X GET "https://<host>/api/v1/dashboard/indicators" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"

# com periodo customizado
curl -X GET "https://<host>/api/v1/dashboard/indicators?from=2026-01-01&to=2026-09-26" \
  -H "Authorization: Bearer eyJhbGciOi..." \
  -H "X-Unit-Id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
```

### Response — `200 OK`

```json
{
  "from": "2025-09-26",
  "to": "2026-09-26",
  "totalWeightKg": 342.75,
  "recyclingRatePercent": 78.4,
  "recyclingRateChangePoints": 5.2,
  "totalWeightChangePercent": -12.3,
  "monthlyWeightKg": [
    {"month": "2026-01", "weightKg": 25.5},
    {"month": "2026-02", "weightKg": 40.0}
  ],
  "weightByMaterial": [
    {"material": "PLASTIC", "weightKg": 120.5, "percent": 35.16},
    {"material": "METAL", "weightKg": 80.0, "percent": 23.34}
  ]
}
```

| Campo | Tipo | Observação |
|---|---|---|
| `from` / `to` | date | Período efetivamente usado (preenchido mesmo se a request não passou `from`/`to`). |
| `totalWeightKg` | double | Peso total descartado no período. |
| `recyclingRatePercent` | double | % do peso descartado que foi para reciclagem. |
| `recyclingRateChangePoints` | double\|null | Variação em pontos percentuais vs. período anterior. `null` = sem descarte no período anterior para comparar. |
| `totalWeightChangePercent` | double\|null | Variação % de peso vs. período anterior. `null` pela mesma razão. |
| `monthlyWeightKg` | lista `{month, weightKg}` | Série mensal, `month` no formato `YYYY-MM`. |
| `weightByMaterial` | lista `{material, weightKg, percent}` | Peso do item é dividido igualmente entre os materiais do modelo. `material` é um dos valores do enum abaixo. |

**Valores possíveis de `material` (`MaterialCode`):** `PLASTIC`, `METAL`, `GLASS`, `PAPER`, `BATTERY`, `CIRCUIT_BOARD`, `CABLE`, `SCREEN`, `OTHER`.

---

## Enums usados nas respostas

- **`ItemStatus`**: `DRAFT`, `PENDING_APPROVAL`, `REJECTED`, `IN_STOCK`, `IN_MAINTENANCE`, `AWAITING_EVALUATION`, `DISPOSED`, `REMOVED`.
- **`ItemCondition`**: `NEW`, `USED`, `SEMI_DAMAGED`, `DAMAGED`.
- **`DamageType`**: `BROKEN_SCREEN`, `MISSING_PART`, `DOES_NOT_POWER_ON`, `OXIDATION`, `OTHER`.
- **`ApprovalStatus`** (dentro de `model`): consultar `ApprovalStatus.java` — usado só como metadado do modelo do item, não é o foco das rotas de dashboard.

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Números gerais da unidade + últimos itens cadastrados (com paginação) | `/dashboard/home` |
| O que o usuário logado precisa resolver agora (rascunhos, reprovados, danificados) | `/dashboard/work-center` |
| Gráficos de descarte/reciclagem, com opção de período customizado | `/dashboard/indicators` |
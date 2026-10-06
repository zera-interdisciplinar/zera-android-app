# Contrato de API — adm-core (`/recycling-places`, `/recyclings`, `/telephone/recyclings`)

Rotas do `ms-administrative-core` para o mapa **perto de mim** (proxy fino da **Google Places API New**) e para a **ficha da parceira** (CNPJ, e-mail, `placeId`, telefone).

Nome, endereço, coordenada, horário e descrição do pin **não** são persistidos — só o `place_id` pode ser gravado na ficha (`PATCH /api/v1/recyclings/{id}/place-id`). Pin já vinculado: a lista do mapa também traz `recyclingBusinessId` e `email`. Telefone **não** entra no JSON do pin.

**Este microserviço não** registra o lote descartado nem gera PDF. Isso é [contrato-descarte.md](contrato-descarte.md) (inventory + IA). A tabela `disposal_report` existe no Postgres deste serviço, mas **não há rota HTTP**. Como o app pluga: [contexto.md](contexto.md).

| Recurso | O que é | Persistido? |
|---|---|---|
| `/api/v1/recycling-places` | Ponto **descoberto no Google** perto de `(lat, lng)` | Nome/endereço/horário: **não** (cache 6 h). Se vinculado, vêm `recyclingBusinessId` e `email` |
| `/api/v1/recyclings` | Empresa recicladora **parceira** (CNPJ, e-mail, `placeId` opcional) | Sim — `recycling_business` |

Endereço da recicladora: a tabela `address` tem `recycling_business_id`, mas **não há controller de endereço**.

---

## Autenticação e headers comuns

| Header | Obrigatório | Descrição |
|---|---|---|
| `Authorization` | Sim | `Bearer <access_token>` — JWT de `POST /api/v1/auth/login` ou refresh. |
| `Content-Type` | Sim (corpo JSON) | `application/json` |

Token ausente/inválido: `401`. Este microserviço **não** valida `apiKey`. Gateway pode exigir `x-api-key`. Unidade **não** vem de header nestas rotas. `GET /recyclings` é **global**.

| Autorização | Rotas |
|---|---|
| Autenticado (qualquer role, inclusive token de serviço) | `GET /recyclings`, `GET /recyclings/{id}`, `GET /recyclings/cnpj/{cnpj}`, `GET /recycling-places`, `GET /telephone/recyclings` |
| `MANAGER` | `POST /recyclings`, `PATCH /recyclings/{id}/name`, `PATCH /recyclings/{id}/email`, `PATCH /recyclings/{id}/place-id`, `POST /telephone/recyclings`, `PATCH /telephone/{id}/number`, `DELETE /telephone/{id}` |

Sem papel de gestor nas escritas: `403` (`"Acesso negado"`). Respostas de erro: **RFC 7807** (`detail`). Listas são **array JSON**, sem envelope. Telefone da recicladora é **um** objeto, não lista.

---

## 1. `GET /api/v1/recycling-places` — recicladoras próximas

Proxy read-only no Google. Busca **só por texto** (`ferro velho`, `cooperativa de reciclagem`, `ecoponto`, `descarte eletronico`), deduplica por `placeId`, distância Haversine até `(lat, lng)`, lista **ordenada por `distanceMeters` crescente**. Não há `searchNearby` com tipo `recycling_center` (Places New rejeita com 400).

Depois do Google, um `SELECT` cruza `placeId` com `recycling_business.place_id`. Pin vinculado preenche `recyclingBusinessId` e `email`; senão ambos vêm `null`.

**Cache:** nome, endereço, coordenada, `isOpen`, `description` e `openingHours` ficam em memória (TTL padrão **6 horas**). Pelos Termos do Google, o app **pode** guardar o `placeId` indefinidamente; snapshot eterno de nome/endereço/coordenada, não. Horários: `languageCode=pt-BR`.

```
GET /api/v1/recycling-places?lat=-23.5505&lng=-46.6333&radiusMeters=5000 HTTP/1.1
Authorization: Bearer <token>
```

| Query param | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `lat` | number (double) | Sim | Latitude. **[-90, 90]**. |
| `lng` | number (double) | Sim | Longitude. **[-180, 180]**. |
| `radiusMeters` | int | Não | Omitido → **5000**. Acima do teto → limitado a **20000**, sem erro. |

```bash
curl -X GET "https://<host>/api/v1/recycling-places?lat=-23.5505&lng=-46.6333" \
  -H "Authorization: Bearer eyJhbGciOi..."
```

### Response — `200 OK`

Array (pode ser vazio).

| Campo | Tipo | Descrição |
|---|---|---|
| `placeId` | string | Id Google Places (New). Usar no `PATCH /recyclings/{id}/place-id`. |
| `name` | string | `displayName`. Pode ser vazio. |
| `address` | string | `formattedAddress`. |
| `lat` / `lng` | number | Coordenada do lugar. |
| `distanceMeters` | number (long) | Distância até a query, arredondada. |
| `isOpen` | boolean ou `null` | `currentOpeningHours.openNow`. `null` se o Google não mandou. |
| `description` | string ou `null` | `editorialSummary`. |
| `openingHours` | array | `{ "days": "segunda-feira", "hours": "08:00 – 18:00" }` (split no `": "`). Sem dado: `[]`. Sem `": "` : `hours` `""`. |
| `recyclingBusinessId` | UUID ou `null` | Ficha interna neste `placeId`. |
| `email` | string ou `null` | Só quando `recyclingBusinessId` não é nulo. |

**Não** vêm: telefone, rating, CNPJ, `unitId`.

```json
[
  {
    "placeId": "places/ChIJxxxxxxxx",
    "name": "Cooperativa Recicla SP",
    "address": "Rua X, 123 - Centro, São Paulo - SP",
    "lat": -23.551,
    "lng": -46.634,
    "distanceMeters": 1240,
    "isOpen": true,
    "description": "Cooperativa de reciclagem de materiais.",
    "openingHours": [
      { "days": "segunda-feira", "hours": "08:00 – 18:00" }
    ],
    "recyclingBusinessId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "contato@reciclasp.com"
  }
]
```

- **`200` + `[]`**: busca ok, **zero** resultados no raio. UI: “nenhuma recicladora encontrada por aqui”.
- **`200` + itens**: ordem do backend; o app não precisa reordenar.
- Quantidade: até **20** lugares por chamada interna ao Google, merge + dedup. Não é “todos os pontos do raio”.

| Status | Quando |
|---|---|
| `400` | `lat`/`lng` ausentes ou fora do intervalo (`"Invalid coordinate: lat=200.0, lng=0.0"`). |
| `401` | Sem JWT ou inválido. |
| `503` | Integração **desligada** (`GOOGLE_PLACES_ENABLED=false`) — `"Busca de recicladoras proximas esta desativada nesta instancia"`. |
| `503` | Google indisponível após retentativas — `"Nao foi possivel buscar recicladoras proximas no momento"`. |

**`503` não é lista vazia.** Mostrar erro/retry. Ambiente sem chave no cluster: o pod sobe, a rota tende a **`503`**.

### Configuração no servidor (não é parâmetro da API)

| Variável / propriedade | Padrão | Efeito |
|---|---|---|
| `GOOGLE_PLACES_ENABLED` → `zera.places.enabled` | `false` | `false` → adaptador que sempre lança `503`. |
| `GOOGLE_PLACES_API_KEY` → `zera.places.api-key` | vazio | Obrigatória quando enabled. Chamadas saem do **servidor**. |
| `zera.places.default-radius-meters` | `5000` | Raio se a query omitir `radiusMeters`. |
| `zera.places.max-radius-meters` | `20000` | `min(requested, max)`. |
| `zera.places.cache-ttl` | `PT6H` | Cache por `(lat,lng,radius)` arredondado (~110 m). |
| `zera.places.max-retries` / `retry-backoff` | `3` / `200ms` | Antes do `503` por falha HTTP no Google. |

---

## Shape compartilhado — `RecyclingBusiness`

Usado em `POST` (201) e nos `GET` de ficha. `cnpj` e `email` serializam como **string**.

```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "name": "Cooperativa Recicla SP",
  "cnpj": "11222333000181",
  "email": "contato@reciclasp.com",
  "placeId": "places/ChIJxxxxxxxx",
  "createdAt": "2026-07-01T09:00:00",
  "updatedAt": "2026-08-01T10:00:00"
}
```

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | UUID | É o `recyclingBusinessId` no pin e no telefone. |
| `name` | string | VARCHAR(100). |
| `cnpj` | string | **14 dígitos**. Entrada aceita máscara; resposta só dígitos. |
| `email` | string | `contact_email`, trim + minúsculas. |
| `placeId` | string ou `null` | `null` até o PATCH de vínculo. |
| `createdAt` / `updatedAt` | datetime | `LocalDateTime` sem timezone. |

**Não** vêm: telefone, endereço, `unitId`.

---

## 2. `GET /api/v1/recyclings` — listar parceiras

Sem query, sem filtro por unidade, sem paginação.

```
GET /api/v1/recyclings HTTP/1.1
Authorization: Bearer <token>
```

`200`: array de `RecyclingBusiness` (pode ser vazio). `401` sem JWT.

---

## 3. `GET /api/v1/recyclings/{id}` — detalhe

`200`: um `RecyclingBusiness`. `400` se `{id}` não é UUID. `404`: `"Recycling not found with id: <uuid>"`.

---

## 4. `GET /api/v1/recyclings/cnpj/{cnpj}` — detalhe por CNPJ

Path passa pelo validador (com ou sem máscara). `400`: `"Invalid CNPJ: <valor>"`. `404`: `"Recycling not found with value: <cnpj da URL>"`.

---

## 5. `POST /api/v1/recyclings` — cadastrar (MANAGER)

Não cria telefone nem endereço no mesmo request. **Não** aceita `placeId` no body — vínculo é o PATCH da seção 8.

```json
{
  "name": "Cooperativa Recicla SP",
  "cnpj": "11.222.333/0001-81",
  "email": "contato@reciclasp.com"
}
```

| Campo | Obrigatório | Descrição |
|---|---|---|
| `name` | Na prática sim | Sem `@NotBlank` no DTO. |
| `cnpj` | Sim | Inválido → `400`. |
| `email` | Sim | Inválido → `400` (`"Invalid email: ..."`). |

`201`: `RecyclingBusiness` com `id` gerado. `403` sem `MANAGER`. **Não use `409` como contrato estável** para CNPJ duplicado: o use case não mapeia `CnpjAlreadyInUseException`; o unique do banco pode estourar no persist. Não há `DELETE /recyclings/{id}`.

---

## 6. `PATCH /api/v1/recyclings/{id}/name` — (MANAGER)

Query param `name`, **não** body. `204` sem corpo. `404` se id inexistente.

---

## 7. `PATCH /api/v1/recyclings/{id}/email` — (MANAGER)

Query param `email`. `204`. `400` e-mail inválido. Sem unicidade de e-mail entre fichas.

---

## 8. `PATCH /api/v1/recyclings/{id}/place-id` — vincular pin (MANAGER)

Query param `placeId`. Um `placeId` só em **uma** parceira (`ux_recycling_business_place_id`). Religar o mesmo ponto à mesma ficha: `204`. Ligar a outra ficha: `409` (`"Recycling place already linked to another recycling business: <placeId>"`). Não há rota para desvincular. A API **não** valida se o id existe no Google (máx. 200 caracteres na coluna).

---

## 9. Telefone (`/api/v1/telephone/recyclings`)

Um telefone por recicladora. Segundo cadastro no mesmo id: `409`. Número: 10 ou 11 dígitos; máscara aceita; resposta **só dígitos**.

### 9.1 `GET /api/v1/telephone/recyclings?recyclingBusinessId=`

Autenticado. **Um** `TelephoneOutput`, não array.

```json
{
  "telephoneId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "number": "11987654321",
  "userId": null,
  "organizationId": null,
  "unitId": null,
  "recyclingBusinessId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "createdAt": "2026-07-01T09:00:00",
  "updatedAt": "2026-07-01T09:00:00"
}
```

`400` query ausente ou não-UUID. `404` sem telefone para essa ficha.

### 9.2 `POST /api/v1/telephone/recyclings` — (MANAGER)

```json
{
  "recyclingBusinessId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "number": "(11) 98765-4321"
}
```

`201` + `Location: /api/v1/telephone/{telephoneId}`. Corpo típico: `telephoneId` + `number`. `409`: `"Telephone already registered for recycling business: <uuid>"`.

### 9.3 `PATCH /api/v1/telephone/{id}/number` e `DELETE /api/v1/telephone/{id}`

`{id}` é o **`telephoneId`**. `PATCH` usa **body** `{ "number": "..." }` (os PATCH de recicladora usam query). `204` nos dois. Contrato mais amplo de telefone (usuário/org/unidade): [contrato-team-management.md](../contrato-adm-core-membros/contrato-team-management.md).

---

## Alerta `RECYCLABLE_TO_LANDFILL`

Quem grava o descarte (inventory) pode notificar o gestor:

```
POST /api/v1/notifications/alerts
Authorization: Bearer <token de serviço com notifications:write>
```

`kind`: `RECYCLABLE_TO_LANDFILL`. O usuário lista os próprios alertas em `GET /api/v1/notifications/alerts`. **Não** substitui o PDF.

---

## Schema vs HTTP

| Objeto no banco | API hoje |
|---|---|
| `recycling_business` | Create, list, get, patch name/email/`place-id`. Sem delete nem unlink. |
| `telephone.recycling_business_id` | GET/POST em `/telephone/recyclings`; PATCH/DELETE pelo `telephoneId`. |
| `address.recycling_business_id` | Sem rotas. |
| `disposal_report` | Sem rotas. Não usar como contrato do app. |

---

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Pontos no mapa (Google + vínculo se existir) | `GET /api/v1/recycling-places?lat=&lng=` |
| Saber se a busca falhou vs. zero resultados | `503` = falha/desligado; `200 []` = busca ok, vazio |
| Lista de parceiras (CNPJ) | `GET /recyclings` |
| Ficha (inclui `placeId` se vinculado) | `GET /recyclings/{id}` ou `GET /recyclings/cnpj/{cnpj}` |
| Cadastrar / nome / e-mail | `POST /recyclings`, `PATCH .../name`, `PATCH .../email` (`MANAGER`) |
| Ligar ficha ao pin | `PATCH /recyclings/{id}/place-id?placeId=` (`MANAGER`) |
| Telefone | `GET /telephone/recyclings?recyclingBusinessId=` |
| Incluir telefone | `POST /telephone/recyclings` (`MANAGER`) |
| Reciclável no aterro | `POST /notifications/alerts` (`kind=RECYCLABLE_TO_LANDFILL`) |
| Registrar o **item/lote** | **Não neste serviço** — [contrato-descarte.md](contrato-descarte.md) |
| PDF | **Não neste serviço** — `POST /api/v1/reports` |

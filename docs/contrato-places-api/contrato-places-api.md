# Contrato de API — Recicladoras próximas (`/api/v1/recycling-places`)

Documentação de contrato da rota do `ms-administrative-core` que descobre **pontos de reciclagem perto de uma coordenada**, via proxy fino para a **Google Places API (New)**. Não há cadastro local, CNPJ nem vínculo com organização/unidade — só o que o Google devolve, limpo e ordenado por distância.

**Não confundir com** `GET /api/v1/recyclings`: esse recurso é o **cadastro interno** de empresas recicladoras (CNPJ, e-mail, telefone, endereço persistido). Mapa “perto de mim” usa **`/recycling-places`**; CRUD de parceiro cadastrado usa **`/recyclings`**.

---

## Autenticação e headers comuns

| Header | Obrigatório | Descrição |
|---|---|---|
| `Authorization` | Sim | `Bearer <access_token>` — JWT emitido no login (`POST /api/v1/auth/login`) ou refresh. Qualquer token **válido** basta: `MANAGER`, `EMPLOYEE` ou token de serviço (desde que autentique neste serviço). |

Token ausente ou inválido: `401`.

Não há autorização por papel nesta rota: gestor e funcionário têm o mesmo acesso.

Este microserviço **não** valida header `apiKey`. Se a chamada passar pelo gateway, o edge pode exigir `x-api-key` à parte.

Não há paginação nem envelope (`totalElements`): a resposta é um **array JSON** (`List<RecyclingPlaceOutput>`).

---

## 1. `GET /api/v1/recycling-places` — buscar recicladoras próximas

Proxy read-only. O backend combina busca por tipo `recycling_center` com buscas textuais (`ferro velho`, `cooperativa de reciclagem`, `ecoponto`, `descarte eletronico`), deduplica por `placeId`, calcula a distância em relação a `(lat, lng)` e devolve a lista **ordenada por `distanceMeters` crescente**.

**Persistência:** nada disso grava no PostgreSQL. Nome, endereço e coordenada dos pontos ficam só em **cache em memória** no processo (TTL padrão **6 horas**), para reduzir custo de chamadas repetidas. Pelos Termos de Uso do Google, o app **pode** guardar o `placeId` por tempo indefinido; nome/endereço/coordenada **não** devem ser tratados como dados permanentes no cliente além do permitido pelo Google (em geral, não armazenar nome/endereço por mais de 30 dias).

### Request

```
GET /api/v1/recycling-places?lat=-23.5505&lng=-46.6333&radiusMeters=5000 HTTP/1.1
Authorization: Bearer <token>
```

| Query param | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `lat` | number (double) | Sim | Latitude do ponto de referência (ex.: GPS do usuário ou centro do mapa). Intervalo válido: **[-90, 90]**. |
| `lng` | number (double) | Sim | Longitude. Intervalo válido: **[-180, 180]**. |
| `radiusMeters` | int | Não | Raio de busca em metros. Omitido → usa o padrão do servidor (**5000**). Valores acima do teto são **limitados** ao máximo (**20000**), sem erro — evita requisições absurdas (ex.: slider de 500 km). |

```bash
curl -X GET "https://<host>/api/v1/recycling-places?lat=-23.5505&lng=-46.6333" \
  -H "Authorization: Bearer eyJhbGciOi..."
```

### Response — `200 OK`

Array (pode ser vazio). Cada item:

| Campo | Tipo | Descrição |
|---|---|---|
| `placeId` | string | Identificador do lugar na Google Places API (New). Estável para referência no app. |
| `name` | string | Nome exibido (`displayName` no Google). Pode ser string vazia se o Google não enviar nome. |
| `address` | string | Endereço formatado (`formattedAddress`). |
| `lat` | number (double) | Latitude do lugar (mesmo sentido da query `lat`). |
| `lng` | number (double) | Longitude do lugar. |
| `distanceMeters` | number (long) | Distância em metros entre `(lat, lng)` da query e a coordenada do lugar (Haversine, arredondada). |

**Não** vêm na resposta: telefone, horário, rating, CNPJ ou `unitId`.

```json
[
  {
    "placeId": "places/ChIJxxxxxxxx",
    "name": "Cooperativa Recicla SP",
    "address": "Rua X, 123 - Centro, São Paulo - SP",
    "lat": -23.551,
    "lng": -46.634,
    "distanceMeters": 1240
  }
]
```

- **`200` + `[]`**: integração ativa, Google respondeu, **não havia resultados** no raio (ou todas as buscas voltaram vazias). UI pode mostrar “nenhuma recicladora encontrada por aqui”.
- **`200` + itens**: lista já vem ordenada do mais perto ao mais longe; o app pode reutilizar a ordem sem reordenar.

Quantidade de resultados: o adaptador pede até **20** lugares por chamada interna ao Google (tipo + cada termo de texto), depois faz merge e deduplica. O tamanho final do array **não** é fixo e **não** há garantia de “todos os pontos do raio” — é o subconjunto que o Google devolve nessas buscas.

### Erros

Respostas de erro seguem **RFC 7807** (`ProblemDetail`), com campo `detail` (mensagem legível).

| Status | Quando |
|---|---|
| `400` | `lat` ou `lng` ausentes na query (binding Spring). |
| `400` | Coordenada fora do intervalo válido — corpo típico: `"Invalid coordinate: lat=200.0, lng=0.0"`. |
| `401` | Sem JWT ou token inválido/expirado. |
| `503` | Integração **desligada** na instância (`GOOGLE_PLACES_ENABLED=false`, padrão local) — `detail` exemplo: `"Busca de recicladoras proximas esta desativada nesta instancia"`. |
| `503` | Integração ligada, mas Google **indisponível** após retentativas — `detail` exemplo: `"Nao foi possivel buscar recicladoras proximas no momento"`. |

**Importante para a UI:** `503` **não** significa “zero recicladoras”. Significa que a busca **não foi feita** (feature off ou falha upstream). Mostrar estado de erro/retry; **não** usar a mesma tela de lista vazia do `200 []`.

Ambiente sem chave Google no cluster (Secret opcional no k8s): o pod sobe, mas a rota tende a responder **`503`**, não `200` com lista vazia.

---

## Configuração no servidor (referência para devops / debug)

Variáveis mapeadas em `application.properties` (não são parâmetros da API):

| Variável / propriedade | Padrão | Efeito |
|---|---|---|
| `GOOGLE_PLACES_ENABLED` → `zera.places.enabled` | `false` | `true` registra o client Google; `false` usa adaptador que sempre lança `503`. |
| `GOOGLE_PLACES_API_KEY` → `zera.places.api-key` | vazio | Obrigatória quando enabled=true (chamadas saem do **servidor**, não do app). |
| `zera.places.default-radius-meters` | `5000` | Raio quando `radiusMeters` omitido na query. |
| `zera.places.max-radius-meters` | `20000` | Teto aplicado com `min(requested, max)`. |
| `zera.places.cache-ttl` | `PT6H` (6 h) | TTL do cache em memória por `(lat,lng,radius)` arredondado (~110 m). |
| `zera.places.max-retries` / `retry-backoff` | `3` / `200ms` | Retentativas antes do `503` por falha HTTP no Google. |

Detalhes de Secret no cluster: ver `k8s/README.md` (seção `google-places`).

---

## Fluxo típico no app (mapa / “perto de mim”)

1. Obter coordenada (GPS ou centro do mapa).
2. `GET /api/v1/recycling-places?lat=&lng=` (opcional `radiusMeters` alinhado ao zoom do mapa, lembrando o teto de 20 km).
3. Tratar resposta:
   - `200` + dados → pins/lista usando `name`, `address`, `distanceMeters` (ex.: “1,2 km”).
   - `200` + `[]` → empty state “nenhum resultado neste raio”.
   - `503` → erro de serviço / feature desligada; oferecer retry.
4. Para favoritos ou histórico, preferir persistir **`placeId`**, não snapshot eterno de nome/endereço.

---

## Qual rota escolher

| Preciso de... | Rota |
|---|---|
| Pontos no mapa descobertos pelo Google (sem CNPJ) | `GET /api/v1/recycling-places?lat=&lng=` |
| Cadastro interno de recicladora parceira (CNPJ, CRUD) | `/api/v1/recyclings` |
| Saber se a busca falhou vs. não há pontos no raio | `503` = falha/desligado; `200 []` = busca ok, zero resultados |
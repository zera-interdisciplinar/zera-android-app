# Contrato API Mobile — Scrapy

Dois endpoints:

- `POST /v1/boot` — boot do app, antes do login. Retorna só envs (entries marcadas `boot_only=true` no admin). Sem body, sem segmentação.
- `POST /v1/flags` — depois do login. Retorna flags/conteúdo (entries com `boot_only=false`), geral ou individual por chave via `attrs`.

Mesma autenticação e mesmo formato de resposta/caching nos dois; a diferença é o body (só `/v1/flags` aceita `attrs`) e o filtro de quais entries voltam (`boot_only`).

## Base URL

```
https://<host>/v1
```

## Autenticação

Header obrigatório em toda chamada, nos dois endpoints:

```
Authorization: Bearer <api_key>
```

- API key é do tipo cliente: só leitura, presa a um `scope` + `env` fixos (definidos no momento em que a key foi criada no admin).
- Key pode ser extraída do app (é pública por natureza) — por isso é somente leitura e tem rate limit por IP (120 req/min, por endpoint).
- Key inválida ou revogada → `401 { "error": "invalid api key" }`.

## `POST /v1/boot`

Chamado no startup do app, antes de qualquer login.

```
POST /v1/boot
Authorization: Bearer <api_key>
If-None-Match: "<etag_anterior>"   // opcional, ver caching abaixo
```

Sem body (o handler não lê nada do request além dos headers).

Resposta `200 OK`, mapa `key -> value` só das entries marcadas `boot_only=true` no admin:

```json
{
  "api_base_url": "https://api.zera.com",
  "min_supported_version": "3.0.0"
}
```

Não há segmentação aqui — é sempre o valor base, não existe usuário logado ainda pra aplicar regra.

## `POST /v1/flags`

Chamado depois do login, quando já dá pra segmentar por usuário.

```
POST /v1/flags
Content-Type: application/json
Authorization: Bearer <api_key>
If-None-Match: "<etag_anterior>"   // opcional, ver caching abaixo
```

Body:

```json
{
  "attrs": {
    "user_id": "abc123",
    "plan": "pro",
    "country": "BR",
    "app_version": "3.2.0"
  }
}
```

- `attrs`: mapa livre chave→valor (string, número, bool). Usado pelo servidor pra bater com as regras de segmentação de cada flag/conteúdo — inclusive pra resolver valor individual por usuário (regra com `attr: "user_id"`, por exemplo). Sem `attrs` correspondente, cai no valor geral.
- Nunca mande dado sensível em `attrs` — não é criptografado além do TLS.
- Não existe schema fixo; manda só o que os rules do scope realmente usam.

Resposta `200 OK`, mapa `key -> value` só das entries com `boot_only=false`, já resolvido pro `attrs` enviado:

```json
{
  "new_checkout_flow": true,
  "max_retries": 3,
  "banner_config": { "title": "Promo", "color": "#FF0000" }
}
```

- Resolução de regra é 100% server-side: mobile nunca vê as regras, só o resultado final.

## Comum aos dois endpoints

- Cada valor pode ser bool, número, string ou objeto — depende de como foi cadastrado no admin.
- Entries marcadas como `secret` nunca aparecem em nenhum dos dois (servidor já filtra).

Header de resposta:

```
ETag: "<hash>"
```

### Caching / polling

Guarda o `ETag` recebido (por endpoint — `/v1/boot` e `/v1/flags` têm ETags independentes). Na próxima chamada manda:

```
If-None-Match: "<etag_guardado>"
```

Se nada mudou, servidor responde `304 Not Modified` (corpo vazio). Se mudou, vem `200` com novo corpo + novo ETag.

### Erros

| Status | Quando |
|---|---|
| 400 | body malformado (`/v1/flags`: `attrs` não é JSON válido) |
| 401 | key ausente, inválida ou revogada |
| 429 | rate limit estourado (120 req/min por IP) |
| 500 | erro interno |

## Exemplo (Swift / URLSession)

```swift
var req = URLRequest(url: URL(string: "https://host/v1/flags")!)
req.httpMethod = "POST"
req.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
req.setValue("application/json", forHTTPHeaderField: "Content-Type")
if let etag = cachedETag {
    req.setValue(etag, forHTTPHeaderField: "If-None-Match")
}
req.httpBody = try! JSONSerialization.data(withJSONObject: ["attrs": attrs])
```

## Exemplo (Kotlin / OkHttp)

```kotlin
val body = JSONObject(mapOf("attrs" to attrs)).toString()
    .toRequestBody("application/json".toMediaType())
val req = Request.Builder()
    .url("https://host/v1/flags")
    .post(body)
    .addHeader("Authorization", "Bearer $apiKey")
    .apply { cachedETag?.let { addHeader("If-None-Match", it) } }
    .build()
```

`/v1/boot` é igual, só sem `Content-Type`/body e trocando a URL/endpoint.
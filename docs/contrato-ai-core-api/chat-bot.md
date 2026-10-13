# Contrato — chat (`ms-artificial-intelligence-core`)

Fonte: Confluence 8.3 e 8.5. Rotas no `ChatService` / `AiClient`.

| Método | Path | Uso |
|---|---|---|
| GET | `/api/v1/multi-agent/conversations` | Inbox (`ListChatConversations`) |
| GET | `/api/v1/multi-agent/conversations/{thread_id}` | Hidratar o chat (`GetChatConversation`) |
| POST | `/api/v1/multi-agent/process-message` | Um turno (`ProcessChatMessage`) |

Auth: `Authorization: Bearer`. Sem `user_id` no body/query. `redirect_to.screen` = nome de `Route.kt`; o app filtra por papel.

# Spec — Chat com o assistente (Zé Bot)

## Metadados

- **Feature:** Inbox de conversas (`Route.ChatConversations`) e tela de chat (`Route.ChatBot(threadId)`)
- **Perfil(is) envolvido(s):** Gestor e Operário
- **Status:** integrado ao AI Core (lista, hidratação, envio, `redirect_to`)
- **Última atualização:** 2026-10-13

## Contexto

O FAB dos scaffolds (`ManagerScaffold` / `EmployeeScaffold`) e o item "Fale com o Zé" abrem o **inbox** `Route.ChatConversations` (`SlideVertical`). Dali, uma linha abre `Route.ChatBot(threadId)` e o FAB `+` gera um UUID v4 e abre um fio novo (sem POST até a primeira mensagem).

Telas compartilhadas: `ChatConversationsScreen` / `ChatConversationsViewModel` e `ChatBotScreen` / `ChatBotViewModel`.

Back: Confluence [8.3](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/64946177/8.3+Contrato+de+API) e [8.5](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/64978945/8.5+FAQ+Qdrant+e+redirect_to). Usuário só do JWT `sub`. O MS **não** filtra cargo; o app esconde o CTA de `redirect_to` quando a rota não é pertinente ao papel.

## User story

Como Gestor ou Operário, quero ver minhas conversas, continuar um fio ou começar outro, e seguir atalhos do assistente só para telas que eu posso abrir.

## Inbox

`GET /api/v1/multi-agent/conversations?page=1&page_size=20`. Envelope `items` + `page` + `page_size` + `total`. `preview` = 40 chars da primeira mensagem. Recarrega em `ON_RESUME`. Horário na linha só se `created_at`/`updated_at` vier no JSON.

Eventos: `load`, `onConversationClick`, `onNewConversationClick`, `onBackClick`.

## Chat (`ChatBotState`)

| Campo | Papel |
|---|---|
| `messages` | conversa, antiga → recente |
| `input` | campo |
| `isLoadingMessage` | turno em andamento |
| `suggestions` | chips; só com `messages` vazia |

Eventos: `onInputChange`, `onSend`, `onSuggestionClick`, `onBackClick`. Init hidrata `GET /conversations/{thread_id}` (descarta `system`). `[]` → welcome + sugestões.

## Turno

1. Ignorar texto em branco, `isLoadingMessage` ou `content` > 8000.
2. Append user, limpar input, ligar loading.
3. `POST /api/v1/multi-agent/process-message` com `thread_id`, `unit_id`, `content` (sem `user_id`).
4. `200`: append assistant `content`. `blocked: true` ainda é 200. `redirect_to` vira CTA só se `ChatRedirect.resolve` devolver rota para o papel.
5. Erro: append mensagem genérica do assistente; a do usuário permanece.

## `redirect_to`

`{ "screen": "Recycling", "message": "Ver pontos próximos" }`. `screen` = nome PascalCase de `Route.kt` sem parâmetro obrigatório. Papel filtrado no app. Histórico GET não traz CTA.

## Critérios de aceite

- [x] Inbox `ChatConversations` + FAB `+` + lista
- [x] FAB/sidebar abrem o inbox
- [x] `ChatBot(threadId)` hidrata e envia no mesmo fio
- [x] `ChatService` no `AiClient`
- [x] `redirect_to` filtrado por papel
- [x] Sugestões só com conversa vazia
- [x] Erro de turno com mensagem genérica

## Fora de escopo

Paginação infinita, persistir thread no SQLite, streaming, PDF via chat, sugestões dinâmicas, CTA a partir do GET de mensagens.

## Estado de carregamento

Atualização de nomes no `ChatBotState`, seguindo a convenção em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de flags de carregamento":

| Campo | Papel |
|---|---|
| `isSendingMessage` | turno em andamento (antes `isLoadingMessage`; é a flag citada em "Turno" e na tabela acima) |
| `isLoading` | hidratação inicial do histórico (antes `isLoadingHistory`); nasce `true` |

## Skeleton

- **Conversa:** durante a hidratação inicial do histórico (`isLoading`), a área de mensagens mostra 4 bolhas de skeleton, alternando assistente (esquerda) e usuário (direita). A barra de digitação continua real. As sugestões só aparecem com `!isLoading && messages.isEmpty()`, para não piscarem antes de o histórico chegar.
- **Lista de conversas:** durante a primeira carga, mostra 7 linhas de skeleton (avatar e uma linha de texto). O retorno à tela recarrega em silêncio, sem reativar `isLoading`.

Convenção em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de skeleton".

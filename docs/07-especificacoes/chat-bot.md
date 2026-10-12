# Spec — Chat com o assistente (Zé Bot)

## Metadados

- **Feature:** Tela de chat com o assistente virtual (`Route.ChatBot`)
- **Perfil(is) envolvido(s):** Gestor e Operário (funcionário)
- **Status:** em progresso (UI pronta; envio e resposta ainda não integrados ao back)
- **Última atualização:** 2026-10-12

## Contexto

Tela de conversa com o assistente de IA, aberta pelo FAB de assistente virtual dos scaffolds (`ManagerScaffold` e `EmployeeScaffold`, animação `SlideVertical`) e pelo item "Fale com o Zé" do menu lateral do Gestor. É uma tela compartilhada entre os dois perfis (`view/screens/shared/ChatBotScreen.kt`, `viewmodel/shared/ChatBot.kt`).

O back é o microsserviço de IA (`POST /api/v1/multi-agent/process-message`), documentado em [../contrato-places-api/contrato-descarte.md](../contrato-places-api/contrato-descarte.md). O cliente não escolhe o agente; cada turno é uma requisição que devolve a resposta completa (sem streaming).

Divisão de responsabilidades (ver [../01-arquitetura.md](../01-arquitetura.md)): `ChatBotScreen` só renderiza `ChatBotState` e repassa eventos; o `ChatBotViewModel` é o único que altera a lista de mensagens, chama o back e controla o loading.

## User story

Como Gestor ou Operário, quero conversar com o assistente e tocar em perguntas sugeridas, para tirar dúvidas sobre o Zera sem sair do app.

## Estado e eventos

`ChatBotState`:

| Campo | Papel |
|---|---|
| `messages: List<Message>` | conversa, da mais antiga para a mais recente |
| `input: String` | texto do `MessageTextBox` |
| `isLoadingMessage: Boolean` | assistente preparando a resposta; mostra o `LoadingMessageContainer` e bloqueia o campo |
| `suggestions: List<String>` | textos dos `SuggestionChip`s |
| `showSuggestions` (derivado) | `messages.isEmpty()`; não é armazenado |

Eventos do `ChatBotViewModel`: `onInputChange`, `onSend`, `onSuggestionClick`, `onBackClick`.

## Como inserir mensagens (fluxo de um turno)

`onSend` e `onSuggestionClick` seguem o **mesmo fluxo**; `onSuggestionClick(texto)` apenas usa o texto da sugestão no lugar de `state.input`.

1. Ignorar se o texto estiver em branco ou se `isLoadingMessage` já for `true`.
2. Acrescentar ao fim de `messages` `Message(MessageRole.User, texto, LocalDateTime.now())`, limpar `input` e ligar `isLoadingMessage`. O `ChatScroll` rola sozinho até o fim.
3. Chamar `POST /api/v1/multi-agent/process-message` com `user_id`, `thread_id`, `unit_id` e `content = texto` (formato e origem de cada campo em [../04-modelo-de-dados.md](../04-modelo-de-dados.md), seção "Modelos de chat").
4. Ao receber `200`, acrescentar `Message(MessageRole.Assistant, resposta.content, LocalDateTime.now())` e desligar `isLoadingMessage`. Se `blocked = true`, o `content` da resposta é a recusa e é exibido como qualquer outra mensagem do assistente.
5. Em erro (`403`, `422`, rede, timeout), desligar `isLoadingMessage` e mostrar uma mensagem do assistente com texto de falha genérico — a mensagem do usuário permanece na lista. Não repetir o turno automaticamente.

Regras:

- `thread_id` é um UUID v4 gerado pelo app quando o `ChatBotViewModel` é criado e reutilizado em todos os turnos; mesmo `user_id` + `thread_id` continua o histórico no back.
- Um turno por vez: enquanto `isLoadingMessage = true`, o `MessageTextBox` fica desabilitado (`enabled = false`).
- `sentAt` é sempre definido pelo app; o back não devolve horário por mensagem.
- O loading não é uma `Message`: nunca entra na lista.
- `content` do usuário tem de ter 1–8000 caracteres; acima disso o back responde `422`.

## Cenários (Given/When/Then)

### Cenário: Abrir o chat sem mensagens

- **Given** `messages` está vazia
- **When** a tela é composta
- **Then** o `ChatScroll` mostra a mensagem de boas-vindas do assistente e o bloco "Sugestões" aparece acima do campo de texto

### Cenário: Enviar uma pergunta digitada

- **Given** o usuário digitou um texto não vazio e `isLoadingMessage` é `false`
- **When** toca em enviar
- **Then** a mensagem entra à direita, o campo é limpo, o `LoadingMessageContainer` aparece e o campo fica desabilitado até a resposta chegar

### Cenário: Receber a resposta

- **Given** há um turno em andamento
- **When** o back responde `200`
- **Then** o loading some, a resposta entra à esquerda com o avatar do assistente e o campo volta a ficar habilitado

### Cenário: Tocar em uma sugestão

- **Given** a conversa ainda não tem mensagens
- **When** o usuário toca em um `SuggestionChip`
- **Then** o texto da sugestão é enviado como mensagem do usuário (mesmo fluxo de "Enviar uma pergunta digitada") e o bloco "Sugestões" desaparece, pois `messages` deixou de estar vazia

### Cenário: Sugestões depois da primeira mensagem

- **Given** `messages` tem ao menos uma mensagem
- **When** a tela é composta
- **Then** o bloco "Sugestões" não é exibido

### Cenário: Falha ao obter a resposta

- **Given** há um turno em andamento
- **When** a chamada falha (rede, `403`, `422`)
- **Then** o loading some, o campo é reabilitado e uma mensagem de falha do assistente é adicionada

## Critérios de aceite

- [x] `ChatBotScreen` + `ChatBotViewModel` + rota `Route.ChatBot` no `ZeraNavHost`
- [x] Acesso pelo FAB dos scaffolds e pelo menu lateral do Gestor
- [x] Sugestões exibidas só enquanto `messages` está vazia (`showSuggestions`)
- [x] `ChatScroll` rola para o fim a cada mensagem nova e mostra o loading por último
- [ ] `onSend` / `onSuggestionClick` (hoje `TODO` no `ChatBotViewModel`)
- [ ] Serviço Retrofit do chat no `AiClient` (hoje só `ReportsService`) e DTOs de request/response
- [ ] `thread_id` gerado pelo app e enviado em todos os turnos
- [ ] Tratamento de erro com mensagem do assistente

## Edge cases considerados

- Texto só com espaços: botão de enviar desabilitado pelo `MessageTextBox`; o `ViewModel` ainda valida.
- Toque duplo em sugestão ou em enviar durante o loading: ignorado (um turno por vez).
- `blocked = true`: HTTP 200; exibir `content` normalmente e não tratar como erro.
- Papel `system` no histórico: não modelado no app; descartar.
- URL de IA não configurada no boot: `AiClient` não inicializa o serviço e o acesso falha com a mensagem "URL da API de IA não configurada no boot." — tratar como falha do turno.

## Fora de escopo desta spec

- Histórico de conversas (`GET /conversations`): reabrir threads antigas ou listar o inbox.
- Link de navegação dentro da mensagem (`MessageContainer.navigateTo`): o contrato não devolve rota por mensagem.
- Geração do PDF de descarte (`POST /api/v1/reports`): fluxo separado, não usa o chat.
- Sugestões dinâmicas vindas do back (a lista é fixa no `ChatBotState`).
- Streaming de resposta.
- Mock de 15 mensagens usado no `ChatBotViewModel` para testar o scroll (provisório).

## Referências

- Componentes: `../03-catalogo-componentes.md` (seção "Chat")
- Modelo de dados: `../04-modelo-de-dados.md` (seção "Modelos de chat")
- Contratos de API: `../contrato-places-api/contrato-descarte.md` (`process-message`, histórico, enums), `../contrato-ai-core-api/chat-bot.md`
- Spec relacionada: [manager-home-dashboard.md](manager-home-dashboard.md) (FAB de acesso ao chat)

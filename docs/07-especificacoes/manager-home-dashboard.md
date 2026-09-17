# Spec — Dashboard do Gestor (Visão Geral)

## Metadados

- **Feature:** Tela inicial do Gestor
- **Perfil(is) envolvido(s):** Gestor
- **Status:** em progresso (layout pronto, dados mockados no `ViewModel`)
- **Última atualização:** 2026-09-17

## Contexto

Primeira tela que o Gestor vê após o login (`Route.ManagerHome`). Dá uma visão rápida da operação: ocupação do estoque, atalhos numéricos (itens/funcionários), alertas importantes e os últimos itens cadastrados. Hoje todos os dados vêm hardcoded em `ManagerHomeViewModel`; `loadDashboard()` é um `TODO`.

## User story

Como Gestor, quero ver um resumo do estoque, dos alertas pendentes e dos últimos itens cadastrados ao abrir o app, para acompanhar a operação sem precisar navegar por várias telas.

## Cenários (Given/When/Then)

### Cenário: Exibição do resumo (dado real) — planejado

- **Given** o Gestor está autenticado e abre `Route.ManagerHome`
- **When** `loadDashboard()` é chamado e a API responde com sucesso
- **Then** `state` é atualizado com ocupação de estoque, total de itens/funcionários, alertas e últimos itens reais (substituindo os mocks atuais)

### Cenário: Lista de alertas vazia

- **Given** não há nenhum alerta pendente
- **When** a tela é exibida
- **Then** `NotificationList` mostra a mensagem padrão "Nenhuma notificação" (já implementado via `emptyContent`)

### Cenário: Lista de últimos itens vazia

- **Given** não há nenhum item cadastrado recentemente
- **When** a tela é exibida
- **Then** `ProductList` mostra a mensagem padrão "Nenhum produto encontrado" (já implementado via `emptyContent`)

### Cenário: Falha ao carregar o dashboard — planejado

- **Given** a chamada de `loadDashboard()` falha (rede ou servidor)
- **When** a tela é exibida
- **Then** `state.errorMessage` é preenchido — **hoje `ManagerHomeScreen` não lê `errorMessage` nem `isLoading` do `state`, então nenhum feedback visual existe ainda para esse caso**

## Critérios de aceite

- [ ] `loadDashboard()` implementado, substituindo os dados mockados por uma chamada real de API
- [ ] Estado de carregamento (`isLoading`) refletido na UI (ex.: skeleton ou indicador) — campo já existe no `State`, falta o uso na `Screen`
- [ ] Estado de erro (`errorMessage`) refletido na UI — campo já existe no `State`, falta o uso na `Screen`
- [x] Estados vazios de alertas e de últimos itens já cobertos
- [ ] Atalhos "Itens" e "Funcionários" navegam para suas respectivas telas (hoje `onClick` é `TODO`)
- [ ] "Ver Todos" (últimos itens) navega para a listagem completa (hoje `onClick` é `TODO`)
- [ ] FAB de assistente virtual (`onFabClick`) tem alguma ação (hoje `TODO`)

## Edge cases considerados

- Estado de erro do `State` existe mas não é renderizado em nenhum lugar da `ManagerHomeScreen` hoje — sinalizado para não ser esquecido ao integrar com a API real.
- Toque nos atalhos/FAB antes de terem destino implementado — hoje não faz nada (`{ /* TODO: ... */ }`), não deveria travar nem dar crash.

## Fora de escopo desta spec

- Telas de destino dos atalhos (Itens, Funcionários, listagem completa de produtos, chatbot) — cada uma terá spec própria quando implementada.
- Cálculo/regra de negócio por trás da ocupação de estoque e dos alertas — pertence a [../05-regras-de-negocio/](../05-regras-de-negocio/) quando o domínio de inventário for especificado.

## Referências

- Catálogo de componentes usados: [../03-catalogo-componentes.md](../03-catalogo-componentes.md), seções "Cards" e "Listas"
- Arquitetura / separação View-lógica: [../01-arquitetura.md](../01-arquitetura.md), seção "Separação entre View e lógica (regra estrita)"

# Spec — Dashboard do Gestor (Visão Geral)

## Metadados

- **Feature:** Tela inicial do Gestor
- **Perfil(is) envolvido(s):** Gestor
- **Status:** implementado (funcionários ainda sem fonte de dados)
- **Última atualização:** 2026-09-26

## Contexto

Primeira tela que o Gestor vê após o login (`Route.ManagerHome`). Dá uma visão rápida da operação: ocupação do estoque, atalhos numéricos (itens/funcionários), alertas e os últimos itens cadastrados. Ocupação, itens, alertas e últimos itens vêm de `GET /api/v1/dashboard/home` via `GetManagerHome`. Nome e papel vêm de `GetSelfUser`. O atalho de funcionários **não** faz parte desse contrato e permanece vazio.

## User story

Como Gestor, quero ver um resumo do estoque, dos alertas pendentes e dos últimos itens cadastrados ao abrir o app, para acompanhar a operação sem precisar navegar por várias telas.

## Cenários (Given/When/Then)

### Cenário: Exibição do resumo (dado real)

- **Given** o Gestor está autenticado e abre `Route.ManagerHome`
- **When** `GetManagerHome.execute()` responde com sucesso
- **Then** `state` atualiza ocupação (`occupancyPercent` / 100), total de itens ativos, variação percentual, alertas derivados dos contadores e últimos itens de `recentItems.content`

### Cenário: Lista de alertas vazia

- **Given** `pendingApproval`, `inMaintenance` e `awaitingEvaluation` são todos zero
- **When** a tela é exibida
- **Then** `NotificationList` mostra a mensagem padrão "Nenhuma notificação"

### Cenário: Lista de últimos itens vazia

- **Given** `recentItems.content` vem vazio
- **When** a tela é exibida
- **Then** `ProductList` mostra a mensagem padrão "Nenhum produto encontrado"

### Cenário: Falha ao carregar o dashboard

- **Given** a chamada de `GetManagerHome` falha (rede ou servidor)
- **When** a tela é exibida
- **Then** `state.errorMessage` é renderizado como `CaptionText` de erro acima do card de ocupação

### Cenário: Atalhos de navegação

- **Given** a home carregou
- **When** o Gestor toca em "Itens", "Ver Todos" ou "Funcionários"
- **Then** navega para `Route.Itens` (itens / ver todos) ou `Route.Employees` (funcionários)

## Critérios de aceite

- [x] `loadDashboard()` chama a API real (`GetManagerHome`), sem mock de ocupação/itens/alertas/últimos itens
- [x] Estado de erro (`errorMessage`) refletido na UI
- [x] Estados vazios de alertas e de últimos itens
- [x] Atalhos "Itens" e "Funcionários" navegam para suas respectivas telas
- [x] "Ver Todos" (últimos itens) navega para `Route.Itens`
- [ ] Total de funcionários preenchido (não existe no Inventory Dashboard — ver TODO no `ManagerHomeViewModel`)
- [ ] Estado de carregamento (`isLoading`) na UI (campo removido do `State`; a tela não mostra skeleton)
- [ ] FAB de assistente virtual (`onFabClick`) tem alguma ação (hoje `TODO`)

## Edge cases considerados

- Falha ao buscar o usuário logado: a home ainda tenta preencher o dashboard; nome/papel podem ficar vazios.
- `occupancyPercent` nulo: ocupação cai para `0f`.
- `activeItemsChangePercent` nulo: o subtítulo do card de itens some.

## Fora de escopo desta spec

- Telas de destino dos atalhos (listagem completa de produtos, gestão de funcionários, chatbot) — cada uma tem (ou terá) spec própria.
- Cálculo dos agregados no backend (capacidade de estoque, janela de descartes).
- `GET /api/v1/dashboard/work-center`.

## Referências

- Regras de negócio: [../05-regras-de-negocio/inventory-dashboard.md](../05-regras-de-negocio/inventory-dashboard.md)
- Catálogo de componentes usados: [../03-catalogo-componentes.md](../03-catalogo-componentes.md), seções "Cards" e "Listas"
- Contratos: [../06-contratos-api.md](../06-contratos-api.md), [../contrato-ms-inventory/contrato-inventory-dashboard.md](../contrato-ms-inventory/contrato-inventory-dashboard.md)
- Arquitetura / separação View-lógica: [../01-arquitetura.md](../01-arquitetura.md), seção "Separação entre View e lógica (regra estrita)"

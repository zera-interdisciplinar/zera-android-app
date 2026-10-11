# Regras de Negócio — Dashboard de Inventário

## Metadados

- **Domínio:** Inventário (home do gestor, home do operário e indicadores)
- **Perfil(is) envolvido(s):** Gestor, Operário
- **Última atualização:** 2026-10-11

## Visão geral

Regras de apresentação dos agregados devolvidos pelo `ms-inventory` (`GET /api/v1/dashboard/home` e `GET /api/v1/dashboard/indicators`). O backend já calcula os totais; o app só converte unidade, formata rótulos e decide o que entra em cada card/gráfico.

## Regras

### Ocupação do estoque

`occupancyPercent` chega em 0–100. O card de ocupação espera fração `0f`–`1f`, então o app divide por 100 e limita a esse intervalo. `null` vira `0f` (card vazio, sem crash).

### Variação de itens ativos

`activeItemsChangePercent` vira o subtítulo do atalho "Itens" (`↑`/`↓` + valor em %). Valor `null` some o subtítulo. Sinal positivo ou zero usa cor de sucesso; negativo, cor de erro.

### Pendências da home do operário

Fonte: `GET /api/v1/dashboard/work-center` (`GetEmployeePendencies`). O backend já filtra pelo `Actor` do token. O app não envia `createdBy` nem `status`.

A lista da seção Pendências **não** lista cada item: um card por bucket com tamanho maior que zero, nesta ordem:

| Bucket | `id` do card | Texto (1 / N) | Cor |
|---|---|---|---|
| `drafts` | `incomplete-registrations` | cadastro incompleto / cadastros incompletos | Red |
| `rejected` | `rejected-registrations` | cadastro recusado / cadastros recusados | Red |
| `damagedWithoutDestination` | `damaged-without-destination` | item danificado sem destino / itens danificados sem destino | Yellow |

Bucket vazio não gera linha. Todos vazios (ou erro da chamada) caem no empty state do `NotificationList` ("Nenhuma notificação"). Clique em qualquer card abre `Route.WorkingCentral`. `inMaintenance` e `awaitingEvaluation` **não** entram nos atalhos da home.

### Pendências da Central de Trabalho

Mesma fonte (`GetEmployeePendencies`). Aqui cada elemento dos três arrays vira um `NotificationItem` (`id` = `item.id`), nesta ordem:

| Bucket | Label | Texto | Cor |
|---|---|---|---|
| `drafts` | Completar cadastro | `name · faltam <missingFields>` (só `name` se a lista vier vazia) | Yellow |
| `rejected` | Recusado pelo gestor | `name · rejectionReason` (só `name` se o motivo vier vazio) | Red |
| `damagedWithoutDestination` | Enviar para manutenção | `name · aguardando destino` | Blue |

`pendingCount` do resumo = tamanho dessa lista. `maintenanceCount` = `inMaintenance` (contador; não há IDs para montar a seção "Em manutenção"). Falha da chamada zera o estado.

### Alertas da home

A lista de alertas **não** vem pronta da API. O app monta um item por contador maior que zero:

| Campo da API | Texto (singular / plural) |
|---|---|
| `pendingApproval` | item aguardando aprovação / itens aguardando aprovação |
| `inMaintenance` | item em manutenção / itens em manutenção |
| `awaitingEvaluation` | item aguardando avaliação / itens aguardando avaliação |

Contador `0` não gera linha. A lista vazia cai no empty state do `NotificationList` ("Nenhuma notificação").

### Últimos itens

Vêm de `recentItems.content` (`id` + `name`) em `GET /api/v1/dashboard/home`. A home do Gestor (`GetManagerHome`) e a home do Operário (`GetEmployeeHome`) usam o mesmo campo; não há mock nessa lista. Página padrão: `page=0`, `size=5` (defaults do `InventoryService`). Sem tag de status. Lista vazia usa o empty state do `ProductList`. No Operário, "Ver Todos" abre `Route.EmployeeItems` (catálogo `GET /api/v1/items`); no Gestor, abre `Route.Itens`.

### Funcionários na home

O contrato Inventory Dashboard **não** traz total de funcionários. O atalho permanece vazio até existir outra fonte.

### Taxa de reciclagem

`recyclingRatePercent` (0–100) alimenta o texto do card e o `CircularGraph` (de novo, dividido por 100). O rótulo "Meta" na UI é fixo: a API não envia meta. `recyclingRateChangePoints` vira o delta (`↑`/`↓` + "no período"); `null` omite o delta.

### Filtro de período dos indicadores

| Chip | Query `from` / `to` |
|---|---|
| Todos | omitidos (janela padrão do backend) |
| Este mês | primeiro dia do mês corrente até hoje (`yyyy-MM-dd`) |
| Categoria | iguais a "Todos" (a API não filtra por categoria nesse endpoint) |

### Evolução mensal

Cada `monthlyWeightKg` vira uma coluna: rótulo = mês curto em pt-BR (ex.: `2026-09` → `Set`), valor = `weightKg`. A ordem da API é preservada. O gráfico rola na horizontal e, por padrão, começa no fim da série (mês mais recente).

### Resíduos por categoria

Cada `weightByMaterial.percent` (0–100) vira `BarGraphItem.percentage` (0–1). Códigos de material viram rótulo em português:

| Código | Rótulo |
|---|---|
| `PLASTIC` | Plásticos |
| `METAL` | Metal |
| `GLASS` | Vidro |
| `PAPER` | Papel |
| `BATTERY` | Baterias |
| `CIRCUIT_BOARD` | Placas |
| `CABLE` | Cabos |
| `SCREEN` | Telas |
| `OTHER` | Outros |
| qualquer outro | o próprio código, sem tradução |

Lista vazia mostra o empty state do `HorizontalBarGraph` ("Nenhum resíduo no período"), não um card em branco.

### Unidade consultada

Todas as rotas do dashboard usam o `unitId` do usuário logado (`GET users/{userId}`), gravado em `SharedPreferences` e enviado no header `X-Unit-Id`. Sem unidade, o Kong/backend rejeita a chamada.

## Parâmetros configuráveis

Nenhum parâmetro de negócio é configurável no app. Percentuais, janela padrão de indicadores e capacidade de estoque vêm do backend. Campos do contrato que o app não usa (`stockCapacity`, `disposalsInWindow`, `windowDays`, detalhes extras do item, `inMaintenance`/`awaitingEvaluation` do work-center nesta tela) são ignorados (`ignoreUnknownKeys`).

## Relacionamento com perfis

O Gestor vê `Route.ManagerHome` e `Route.Indexes`. O Operário vê ocupação, últimos itens e pendências em `Route.EmployeeHome`. O interceptor envia o token do usuário autenticado; a autorização de quem pode consultar o dashboard é do backend.

## Referências

- Specs relacionadas: [../07-especificacoes/manager-home-dashboard.md](../07-especificacoes/manager-home-dashboard.md), [../07-especificacoes/indexes.md](../07-especificacoes/indexes.md), [../07-especificacoes/employee-home.md](../07-especificacoes/employee-home.md), [../07-especificacoes/central-de-trabalho.md](../07-especificacoes/central-de-trabalho.md)
- Contratos de API: [../06-contratos-api.md](../06-contratos-api.md), [../contrato-dashboard/contrato-inventory-dashboard.md](../contrato-dashboard/contrato-inventory-dashboard.md)
- Modelo de dados: [../04-modelo-de-dados.md](../04-modelo-de-dados.md)

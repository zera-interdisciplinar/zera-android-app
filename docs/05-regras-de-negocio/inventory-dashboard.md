# Regras de Negócio — Dashboard de Inventário

## Metadados

- **Domínio:** Inventário (home do gestor e indicadores)
- **Perfil(is) envolvido(s):** Gestor
- **Última atualização:** 2026-09-26

## Visão geral

Regras de apresentação dos agregados devolvidos pelo `ms-inventory` (`GET /api/v1/dashboard/home` e `GET /api/v1/dashboard/indicators`). O backend já calcula os totais; o app só converte unidade, formata rótulos e decide o que entra em cada card/gráfico.

## Regras

### Ocupação do estoque

`occupancyPercent` chega em 0–100. O card de ocupação espera fração `0f`–`1f`, então o app divide por 100 e limita a esse intervalo. `null` vira `0f` (card vazio, sem crash).

### Variação de itens ativos

`activeItemsChangePercent` vira o subtítulo do atalho "Itens" (`↑`/`↓` + valor em %). Valor `null` some o subtítulo. Sinal positivo ou zero usa cor de sucesso; negativo, cor de erro.

### Alertas da home

A lista de alertas **não** vem pronta da API. O app monta um item por contador maior que zero:

| Campo da API | Texto (singular / plural) |
|---|---|
| `pendingApproval` | item aguardando aprovação / itens aguardando aprovação |
| `inMaintenance` | item em manutenção / itens em manutenção |
| `awaitingEvaluation` | item aguardando avaliação / itens aguardando avaliação |

Contador `0` não gera linha. A lista vazia cai no empty state do `NotificationList` ("Nenhuma notificação").

### Últimos itens

Vêm de `recentItems.content` (`id` + `name`). Não há mock nessa lista. Página padrão da home: `page=0`, `size=5` (defaults do `InventoryService`). Lista vazia usa o empty state do `ProductList`.

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

Nenhum parâmetro de negócio é configurável no app. Percentuais, janela padrão de indicadores e capacidade de estoque vêm do backend. Campos do contrato que o app não usa (`stockCapacity`, `disposalsInWindow`, `windowDays`, detalhes extras do item, `GET .../work-center`) são ignorados (`ignoreUnknownKeys`).

## Relacionamento com perfis

Só o Gestor vê essas telas (`Route.ManagerHome`, `Route.Indexes`). O interceptor ainda envia o token do usuário autenticado; a autorização de quem pode consultar o dashboard é do backend.

## Referências

- Specs relacionadas: [../07-especificacoes/manager-home-dashboard.md](../07-especificacoes/manager-home-dashboard.md), [../07-especificacoes/indexes.md](../07-especificacoes/indexes.md)
- Contratos de API: [../06-contratos-api.md](../06-contratos-api.md), [../contrato-ms-inventory/contrato-inventory-dashboard.md](../contrato-ms-inventory/contrato-inventory-dashboard.md)
- Modelo de dados: [../04-modelo-de-dados.md](../04-modelo-de-dados.md)

# Spec — Indicadores do Gestor

## Metadados

- **Feature:** Tela de indicadores do Gestor
- **Perfil(is) envolvido(s):** Gestor
- **Status:** implementado
- **Última atualização:** 2026-09-26

## Contexto

Tela `Route.Indexes` ("Indicadores" na barra inferior). Mostra taxa de reciclagem, evolução mensal de peso descartado e distribuição de resíduos por material, a partir de `GET /api/v1/dashboard/indicators`.

## User story

Como Gestor, quero ver indicadores de reciclagem e descarte da minha unidade, para acompanhar o desempenho sem montar relatório à mão.

## Cenários (Given/When/Then)

### Cenário: Carga inicial com sucesso

- **Given** o Gestor está autenticado e abre `Route.Indexes`
- **When** `GetIndicators.execute()` responde com sucesso (filtro padrão "Todos", sem `from`/`to`)
- **Then** o `state` preenche taxa de reciclagem, gráfico mensal e resíduos por categoria

### Cenário: Filtro "Este mês"

- **Given** a tela já carregou
- **When** o Gestor seleciona o chip "Este mês"
- **Then** a chamada envia `from` = primeiro dia do mês corrente e `to` = hoje, e os gráficos atualizam

### Cenário: Filtro "Categoria"

- **Given** a tela já carregou
- **When** o Gestor seleciona o chip "Categoria"
- **Then** a chamada é igual à de "Todos" (sem `from`/`to`): o endpoint não filtra por categoria

### Cenário: Sem resíduos no período

- **Given** `weightByMaterial` vem vazio
- **When** a seção "Resíduos por categoria" é exibida
- **Then** o `HorizontalBarGraph` mostra "Nenhum resíduo no período" (não um card branco vazio)

### Cenário: Série mensal com mais de 6 meses

- **Given** `monthlyWeightKg` tem mais colunas do que cabem na largura
- **When** o gráfico de evolução mensal é exibido
- **Then** a faixa de barras rola na horizontal e começa no fim da série (mês mais recente visível)

### Cenário: Falha ao carregar

- **Given** a chamada de indicadores falha (rede, 401, 400 sem `X-Unit-Id`, etc.)
- **When** a tela é exibida
- **Then** `state.errorMessage` aparece acima dos chips

## Critérios de aceite

- [x] Dados vêm de `GetIndicators` / `InventoryService.getIndicators`, não de mock
- [x] Taxa de reciclagem, evolução mensal e resíduos por categoria mapeados no `IndexesViewModel`
- [x] Empty state de resíduos por categoria
- [x] Scroll horizontal da evolução mensal, padrão no mês mais recente
- [x] Erro de rede/API visível na tela
- [x] Chip "Este mês" altera o período da query
- [ ] Chip "Categoria" com filtro real de categoria (hoje equivale a "Todos")
- [ ] Rótulo "Meta" do `CircularGraph` ligado a uma meta vinda da API (hoje é texto fixo; a API não envia meta)

## Edge cases considerados

- `recyclingRateChangePoints` nulo: o delta some, a taxa continua.
- Material com código desconhecido: o gráfico mostra o código cru.
- Sem `unitId` na sessão: o interceptor não manda `X-Unit-Id` e a API responde 400.

## Fora de escopo desta spec

- `GET /api/v1/dashboard/work-center` (home do operário e Central de Trabalho; não entra nos indicadores).
- Indicadores do perfil Funcionário.
- Skeleton/`isLoading` na tela.

## Referências

- Regras de negócio: [../05-regras-de-negocio/inventory-dashboard.md](../05-regras-de-negocio/inventory-dashboard.md)
- Contratos de API: [../06-contratos-api.md](../06-contratos-api.md), [../contrato-dashboard/contrato-inventory-dashboard.md](../contrato-dashboard/contrato-inventory-dashboard.md)
- Catálogo: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (gráficos)

## Estado de carregamento

O `IndexesState` tem `isLoading`, `true` desde a criação do `ViewModel` e baixada ao fim de cada carga (sucesso ou erro). Regra particular desta tela: a flag volta a `true` a cada troca de filtro, e o skeleton dos gráficos deve reaparecer. Enquanto `isLoading` for `true`, `onFilterChange` ignora a troca, evitando cargas concorrentes.

- Pendente na tela: desabilitar os chips de filtro enquanto `isLoading` for verdadeiro (hoje o toque é ignorado em silêncio; há um `TODO` no `ViewModel`) e desenhar o skeleton.
- Convenção geral em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de flags de carregamento".

## Skeleton dos gráficos

Enquanto `isLoading` (carga inicial ou troca de filtro), os blocos abaixo dos chips de período mostram o skeleton do card de taxa de reciclagem e dos dois gráficos. Os chips ficam reais. Atualiza o item "Pendente na tela" acima: o skeleton está feito; falta só desabilitar os chips durante o carregamento, o que depende de o `ZeraChipsGroup` ganhar um parâmetro `enabled` (hoje ele não tem; o `TODO` continua no `onFilterChange` do `ViewModel`). Convenção em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de skeleton".

### Atualização: chips desabilitados durante o carregamento

Os chips de período agora ficam desabilitados enquanto `isLoading` (o `ZeraChipsGroup` ganhou o parâmetro `enabled`), resolvendo o pendente acima. A guarda no `onFilterChange` do `ViewModel` continua, como proteção contra cargas concorrentes, e o `TODO` foi trocado por um comentário.

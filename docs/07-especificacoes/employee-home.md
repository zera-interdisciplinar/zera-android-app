# Spec — Home do Operário (Visão Geral)

## Metadados

- **Feature:** Tela inicial do Operário
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (atalhos reais; "Adicionar Modelo" abre `Route.Models`)
- **Última atualização:** 2026-10-11

## Contexto

Primeira tela do Operário após o login (`Route.EmployeeHome`). Reaproveita os componentes da home do Gestor ([manager-home-dashboard.md](manager-home-dashboard.md)) dentro do `EmployeeScaffold`, com textos próprios do fluxo de Operário. Nome e cargo vêm de `GetSelfUser`; ocupação e últimos itens vêm de `GetEmployeeHome` (`GET /api/v1/dashboard/home`, `recentItems.content`); pendências de `GetEmployeePendencies` (`GET /api/v1/dashboard/work-center`). Atalhos usam `inMaintenance` e `disposalsInWindow` de `GET /api/v1/dashboard/home`. O toque em Descartes abre o histórico. O toque em Manutenção abre os itens com o filtro "Em manutenção".

## User story

Como Operário, quero ver a ocupação do estoque, meus atalhos de manutenção/descarte, minhas pendências de cadastro e os últimos itens, para retomar o trabalho rapidamente.

## Cenários (Given/When/Then)

### Cenário: Exibição da home

- **Given** o Operário está autenticado e abre `Route.EmployeeHome`
- **When** a tela é composta
- **Then** mostra nome e cargo (de `GetSelfUser`), `StockOccupationCard`, botões "Escanear" e "Adicionar Modelo", atalhos "Manutenção" e "Descartes", a lista de pendências (work-center) e os últimos itens

### Cenário: Falha ao carregar o usuário

- **Given** `GetSelfUser.execute()` falha
- **When** a tela é exibida
- **Then** nome e cargo ficam vazios e o restante da tela continua visível (sem mensagem de erro)

### Cenário: Toque em um último item

- **Given** a lista "Últimos itens" tem ao menos um item
- **When** o Operário toca no item
- **Then** navega para `Route.ItemDetails(itemId)`

## Critérios de aceite

- [x] Layout com os mesmos componentes da home do Gestor (`StockOccupationCard`, `ShortcutCard`, `NotificationList`, `ProductList`)
- [x] Pendências mapeadas de `drafts` / `rejected` / `damagedWithoutDestination` (contador > 0 vira um `NotificationItem`; drafts e rejected em Red, danificados em Yellow)
- [x] Dados reais de ocupação, pendências e últimos itens (`recentItems.content`)
- [x] Dados reais de manutenção e descartes (`inMaintenance` e `disposalsInWindow`)
- [x] Clique em pendência abre `Route.WorkingCentral`
- [x] Clique em um último item abre `Route.ItemDetails(itemId)`
- [x] "Ver Todos" abre `Route.EmployeeItems`
- [x] Navegação de "Escanear" e "Descartes" (`Route.Scan` e `Route.ScheduledDisposals`)
- [x] "Manutenção" abre `Route.EmployeeItems(status = "Em manutenção")`
- [x] Navegação de "Adicionar Modelo" (`Route.Models`)

## Edge cases considerados

- Lista de pendências vazia (todos os buckets vazios ou falha do work-center): `NotificationList` mostra "Nenhuma notificação".
- Um item no bucket: label no singular (`1 cadastro incompleto`). Dois ou mais: plural (`3 cadastros incompletos`).
- Lista de últimos itens vazia (`recentItems.content` vazio ou falha de `GetEmployeeHome`): `ProductList` mostra "Nenhum produto encontrado".

## Fora de escopo desta spec

- Sidebar do Operário (o botão de menu da `UpperNavBar` ainda não faz nada).
- Navegação da `EmployeeBottomNavBar` (ver "Inconsistências conhecidas" em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md)).

## Referências

- Componentes: `../03-catalogo-componentes.md` (`EmployeeScaffold`, `Notification`)
- Spec relacionada: [manager-home-dashboard.md](manager-home-dashboard.md), [central-de-trabalho.md](central-de-trabalho.md), [employee-descarte.md](employee-descarte.md)
- Contrato: [../contrato-dashboard/contrato-inventory-dashboard.md](../contrato-dashboard/contrato-inventory-dashboard.md) (`GET /dashboard/home` e `GET /dashboard/work-center`)
- Regras: [../05-regras-de-negocio/inventory-dashboard.md](../05-regras-de-negocio/inventory-dashboard.md)

## Estado de carregamento

O `EmployeeHomeState` tem uma flag por carga, todas `true` desde a criação do `ViewModel` e baixadas quando a respectiva chamada termina (sucesso ou erro):

- `isLoadingUser`: nome e cargo do usuário.
- `isLoadingHome`: ocupação do estoque, contadores dos atalhos e últimos itens.
- `isLoadingPendencies`: pendências do work-center.

`isLoading` é calculada e fica `true` enquanto qualquer uma delas estiver ativa. A tela ainda não desenha skeleton. Convenção geral em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de flags de carregamento".

## Skeleton

Atualização do estado de carregamento acima: `isLoading` calculado agora considera só `isLoadingUser` e `isLoadingHome`. As pendências são opcionais: a seção "Pendências" só entra quando `isLoadingPendencies` fica `false`, sem skeleton. Enquanto `isLoading`, a tela mostra o skeleton de nome/cargo, card de ocupação, botões "Escanear" e "Adicionar Modelo" e os atalhos "Manutenção" e "Descartes". Convenção em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de skeleton".

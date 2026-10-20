# Spec — Central de Trabalho (Operário)

## Metadados

- **Feature:** Central de Trabalho (`WorkingCentralScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (lista real de pendências; seção de manutenção e resolução ainda pendentes)
- **Última atualização:** 2026-10-11

## Contexto

`Route.WorkingCentral` é aberta pelo atalho "Central" da `EmployeeBottomNavBar`, pelo item "Central de Trabalho" da sidebar do Operário e pelos cards da seção Pendências da home. Reúne o resumo do turno e as pendências **item a item**. Fonte: `GetEmployeePendencies` (`GET /api/v1/dashboard/work-center`) — o mesmo payload da home; a home só mostra o resumo por bucket.

## User story

Como Operário, quero ver o que está pendente no meu turno, para saber o que resolver primeiro.

## Cenários (Given/When/Then)

### Cenário: Exibir o resumo do turno

- **Given** o Operário abre a Central de Trabalho
- **When** a tela é composta
- **Then** o `SummaryCard` "Resumo do turno" mostra `"<pendências> pendências · <manutenção> em manutenção"` (`pendingCount` = tamanho da lista; `maintenanceCount` = `inMaintenance` da API)

### Cenário: Listar pendências prioritárias

- **Given** o work-center tem rascunhos, recusados e/ou danificados sem destino
- **When** a tela é composta
- **Then** `NotificationList` exibe um card por item, nesta ordem: `drafts` ("Completar cadastro", Yellow), `rejected` ("Recusado pelo gestor", Red), `damagedWithoutDestination` ("Enviar para manutenção", Blue)

### Cenário: Tocar em uma pendência

- **Given** a lista de pendências está visível
- **When** o Operário toca em uma pendência
- **Then** `onNotificationClick` é chamado; ainda sem navegação para a tela de resolução

### Cenário: Falha ao carregar

- **Given** `GetEmployeePendencies.execute()` falha
- **When** a tela é exibida
- **Then** o resumo fica `"0 pendências · 0 em manutenção"` e a lista mostra "Nenhuma notificação"

## Critérios de aceite

- [x] Resumo do turno calculado a partir do estado (`pendingCount` = tamanho da lista; `maintenanceCount` = `inMaintenance`)
- [x] Lista de pendências reaproveitando `NotificationList`/`NotificationItem`
- [x] Dados reais de pendências (work-center, um card por item)
- [ ] Tocar em uma pendência abrir a tela de resolução correspondente (`TODO` em `onNotificationClick`; telas de resolução ainda não existem)
- [ ] Seção "Em manutenção" (`TODO` na tela; a API só manda o contador)

## Edge cases considerados

- Lista vazia: o resumo mostra "0 pendências" e `NotificationList` mostra "Nenhuma notificação".
- Rascunho com `missingFields` vazio: o texto do card é só o `name`. O app **não** recalcula o que falta (foto não é obrigatória no back-end, ZERA-236).
- Rascunho com `missingFields`: os nomes da API viram rótulos (`photoUrl` → foto, se o back-end ainda mandar). Upload de foto continua fora desta tela.
- Recusado sem `rejectionReason`: o texto do card é só o `name`.

## Fora de escopo desta spec

- Telas de resolução de cada tipo de pendência.
- Fluxo de manutenção do Operário (lista de itens em manutenção).
- `awaitingEvaluation` (o contrato manda o número, a tela não usa).

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`EmployeeScaffold`, `SummaryCard`, `NotificationList`)
- Spec relacionada: [employee-home.md](employee-home.md)
- Contrato: [../contrato-dashboard/contrato-inventory-dashboard.md](../contrato-dashboard/contrato-inventory-dashboard.md)
- Regras: [../05-regras-de-negocio/inventory-dashboard.md](../05-regras-de-negocio/inventory-dashboard.md)

## Estado de carregamento

O `WorkingCentralState` tem `isLoading`, `true` desde a criação do `ViewModel` e baixada ao fim da carga (sucesso ou erro; em erro a tela cai no estado vazio, como antes). A tela ainda não desenha skeleton. Convenção geral em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de flags de carregamento".

## Skeleton

Enquanto `isLoading`, a tela mostra o skeleton do resumo do turno, do título "Pendências prioritárias" e de 3 cartões de pendência (`NotificationListSkeleton`). Convenção em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de skeleton".

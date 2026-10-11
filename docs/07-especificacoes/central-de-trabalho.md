# Spec — Central de Trabalho (Operário)

## Metadados

- **Feature:** Central de Trabalho (`WorkingCentralScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (UI pronta com dados mockados; ação das pendências e seção de manutenção pendentes)
- **Última atualização:** 2026-10-09

## Contexto

`Route.WorkingCentral` é aberta pelo atalho "Central" da `EmployeeBottomNavBar` e pelo item "Central de Trabalho" da sidebar do Operário. Reúne o resumo do turno e as pendências prioritárias. Os dados vêm fixos do `WorkingCentralViewModel` até o back expor as pendências e a manutenção do Operário.

## User story

Como Operário, quero ver o que está pendente no meu turno, para saber o que resolver primeiro.

## Cenários (Given/When/Then)

### Cenário: Exibir o resumo do turno

- **Given** o Operário abre a Central de Trabalho
- **When** a tela é composta
- **Then** o `SummaryCard` "Resumo do turno" mostra `"<pendências> pendências · <manutenção> em manutenção"` (`WorkingCentralState.shiftSummary`)

### Cenário: Listar pendências prioritárias

- **Given** `priorityNotifications` tem itens
- **When** a tela é composta
- **Then** `NotificationList` exibe cada pendência com rótulo e cor por tipo: "Completar cadastro" (Yellow), "Recusado pelo gestor" (Red) e "Enviar para manutenção" (Blue); a lista tem altura máxima de 400 dp

### Cenário: Tocar em uma pendência

- **Given** a lista de pendências está visível
- **When** o Operário toca em uma pendência
- **Then** `onNotificationClick` é chamado; ainda sem navegação

## Critérios de aceite

- [x] Resumo do turno calculado a partir do estado (`pendingCount` = tamanho da lista)
- [x] Lista de pendências reaproveitando `NotificationList`/`NotificationItem`
- [ ] Dados reais de pendências e de itens em manutenção
- [ ] Tocar em uma pendência abrir a tela de resolução correspondente (`TODO` em `onNotificationClick`; telas de resolução ainda não existem)
- [ ] Seção "Em manutenção" (`TODO` na tela)

## Edge cases considerados

- Lista de pendências vazia: o resumo mostra "0 pendências" e `NotificationList` mostra "Nenhuma notificação".

## Fora de escopo desta spec

- Telas de resolução de cada tipo de pendência.
- Fluxo de manutenção do Operário.

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`EmployeeScaffold`, `SummaryCard`, `NotificationList`)
- Spec relacionada: [employee-home.md](employee-home.md)

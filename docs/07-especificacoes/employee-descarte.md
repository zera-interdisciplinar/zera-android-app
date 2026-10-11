# Spec — Descarte do Operário

## Metadados

- **Feature:** Fluxo de descarte do Operário (mapa, registro e histórico)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** implementado
- **Última atualização:** 2026-10-11

## Contexto

O mapa, a seleção de itens, o registro (`POST /disposals`), o PDF e o histórico já existem no fluxo do gestor. O Operário usa as mesmas rotas e telas, com `EmployeeScaffold` no mapa e no histórico. Cancelar descarte continua só do gestor (`PATCH /disposals/{id}/cancel` → `403` para operário).

## User story

Como Operário, quero registrar um descarte no mapa e ver o histórico da unidade, para destinar itens sem depender do gestor na operação.

## Cenários (Given/When/Then)

### Cenário: Abrir o mapa pela barra inferior

- **Given** o Operário está autenticado
- **When** toca em "Reciclagem" na `EmployeeBottomNavBar`
- **Then** navega para `Route.Recycling` (mapa) com `EmployeeScaffold`

### Cenário: Abrir o histórico pela sidebar

- **Given** a sidebar do Operário está aberta
- **When** toca em "Histórico de descartes"
- **Then** navega para `Route.ScheduledDisposals`

### Cenário: Atalho da home

- **Given** o Operário está em `Route.EmployeeHome`
- **When** toca no card "Descartes"
- **Then** navega para `Route.ScheduledDisposals`

### Cenário: Confirmar descarte e voltar à home

- **Given** o Operário confirmou o lote em `RecyclingResume`
- **When** a tela de sucesso é fechada ou "voltar para o início"
- **Then** `pushAndPopAll` vai para `Route.EmployeeHome`

### Cenário: Detalhe sem cancelar

- **Given** o Operário abre `Route.SchedulingDetails`
- **When** a tela é composta
- **Then** mostra data, local, itens, contato e relatório; não mostra "Cancelar descarte"

## Critérios de aceite

- [x] Bottom bar "Reciclagem" abre `Route.Recycling` e marca a aba
- [x] Sidebar "Histórico de descartes" abre `Route.ScheduledDisposals`
- [x] Card "Descartes" da home abre o histórico
- [x] Mapa e histórico usam `EmployeeScaffold` quando o papel é `EMPLOYEE`
- [x] Sucesso volta para a home do Operário
- [x] Operário não vê cancelar descarte
- [x] Relatório e contato da recicladora permanecem

## Edge cases considerados

- Papel ainda não carregado: casca do gestor até `GetSelfUser` responder (sem tela extra).
- Falha de `GetSelfUser` no sucesso: cai em `Route.ManagerHome` (mesmo fallback do login).

## Fora de escopo desta spec

- Contador real do card "Descartes" (continua mock).
- Manutenção.
- Cancelar descarte pelo Operário.
- Telas ou rotas novas de descarte.

## Referências

- Contrato: [../contrato-places-api/contrato-descarte.md](../contrato-places-api/contrato-descarte.md), [../contrato-places-api/contexto.md](../contrato-places-api/contexto.md)
- Relatório: [../contrato-ai-core-api/reports.md](../contrato-ai-core-api/reports.md)
- Spec relacionada: [employee-home.md](employee-home.md), [descartes-agendados.md](descartes-agendados.md)

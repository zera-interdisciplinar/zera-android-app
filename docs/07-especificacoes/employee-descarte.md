# Spec — Descarte do Operário

## Metadados

- **Feature:** Fluxo de descarte do Operário (mapa, registro e histórico)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** implementado
- **Última atualização:** 2026-10-31

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

### Cenário: Bloquear navegação enquanto confirma o descarte

- **Given** o Operário está em `Route.RecyclingResume` com pelo menos um item selecionado
- **When** toca em "Confirmar descarte"
- **Then** o botão entra em carregamento; `ZeraNavigator.navigationStatus(false)` bloqueia navegação por comando, botão voltar da interface e voltar nativo/gestual; os botões "Voltar" e "Visualizar itens selecionados" ficam indisponíveis

### Cenário: Restaurar navegação após confirmar

- **Given** a confirmação está em carregamento
- **When** `CreateDisposal` termina com sucesso, erro ou cancelamento
- **Then** o estado de carregamento é encerrado e `ZeraNavigator.navigationStatus(true)` restaura todas as navegações; em sucesso, navega para `Route.SchedulingSuccess`; em erro, permanece na tela de resumo exibindo a mensagem

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
- [x] Durante a confirmação do descarte, nenhuma navegação é permitida, inclusive com o voltar nativo/gestual
- [x] O estado de carregamento desbloqueia a navegação em sucesso, erro e cancelamento
- [x] O ViewModel restaura a navegação ao ser encerrado, evitando bloqueio permanente

## Edge cases considerados

- Papel ainda não carregado: casca do gestor até `GetSelfUser` responder (sem tela extra).
- Falha de `GetSelfUser` no sucesso: cai em `Route.ManagerHome` (mesmo fallback do login).
- Erro ou cancelamento da confirmação: a navegação é reabilitada e o usuário permanece em `Route.RecyclingResume`, podendo tentar novamente ou voltar.
- O ViewModel pode ser destruído enquanto aguarda a API: `onCleared()` garante que o status global não fique bloqueado.

## Fora de escopo desta spec

- Contador do card "Descartes" (`disposalsInWindow` na home).
- Manutenção.
- Cancelar descarte pelo Operário.
- Telas ou rotas novas de descarte.

## Referências

- Contrato: [../contrato-places-api/contrato-descarte.md](../contrato-places-api/contrato-descarte.md), [../contrato-places-api/contexto.md](../contrato-places-api/contexto.md)
- Relatório: [../contrato-ai-core-api/reports.md](../contrato-ai-core-api/reports.md)
- Spec relacionada: [employee-home.md](employee-home.md), [descartes-agendados.md](descartes-agendados.md)

## Skeleton

- **Seleção de itens:** enquanto `isLoading` (carga inicial ou busca), só a região da lista mostra o skeleton (`OptionListSkeleton`). O cabeçalho da recicladora (vem da rota), a busca e o botão "Continuar" ficam reais.
- **Resumo do descarte:** não usa skeleton. Todo o conteúdo vem pronto pela rota; o `isLoading` do state só acompanha a busca do contato da recicladora, que a tela não exibe.

Convenção em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenção de skeleton".

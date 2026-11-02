# Spec — Dispensa de alertas

## Metadados

- **Feature:** dispensar alertas na Central de Alertas (`AlertCentralScreen`), na home do Gestor (`ManagerHomeScreen`) e pelo toque no push
- **Perfil(is) envolvido(s):** Gestor e Funcionário (quem recebe alertas da API)
- **Status:** em progresso
- **Última atualização:** 2026-11-01

## Contexto

Os alertas se acumulam no app e continuam na lista depois de o usuário interagir com eles. O `ms-administrative-core` agora permite fechá-los:

```
PATCH /api/v1/notifications/alerts/{alertId}
Authorization: Bearer <JWT do usuário>
```

Sem corpo. Fecha o alerta do `sub` do token (`CLOSED`). Já fechado devolve `200`; id inexistente ou de outro usuário devolve `404`. Não existe status de "lido": interagir e dispensar são a mesma ação.

Fechar não resolve o problema que gerou o alerta. Se a regra continuar valendo, um job futuro cria um alerta novo, com outro id. Isso é esperado e não pede código no app.

## User story

Como usuário que recebe alertas, quero que um alerta saia da lista quando eu interagir com ele ou o dispensar, para que a lista mostre só o que ainda precisa da minha atenção.

## Cenários (Given/When/Then)

### Cenário: Dispensar deslizando

- **Given** a Central de Alertas ou a home do Gestor mostra um alerta
- **When** o usuário desliza o card para qualquer lado
- **Then** o card sai da lista com animação, os seguintes sobem e o alerta é fechado na API

### Cenário: Dispensar pelo X

- **Given** a Central de Alertas ou a home do Gestor mostra um alerta
- **When** o usuário toca no X do card
- **Then** acontece o mesmo que ao deslizar

### Cenário: Resolver agora

- **Given** um alerta com "Resolver agora"
- **When** o usuário toca na ação
- **Then** o alerta sai da lista, é fechado na API e o app navega para a tela da ação

### Cenário: Tocar no push

- **Given** um push que traz `alertId` no `data`
- **When** o usuário toca na notificação com a sessão ativa
- **Then** o alerta é fechado na API e não aparece nas listas; o app navega se houver rota para o `deepLink` ou o `kind`, e só abre se não houver

### Cenário: Tocar no push sem sessão

- **Given** um push com `alertId` e o usuário deslogado
- **When** o usuário toca na notificação e depois faz login
- **Then** o alerta é fechado depois do login

### Cenário: Falha ao deslizar ou tocar no X

- **Given** o usuário dispensou um alerta
- **When** o `PATCH` falha por um motivo que não seja `404`
- **Then** o card volta à posição original e o popup de erro aparece; sem conexão, vale a tela cheia de conexão, como nas outras chamadas

### Cenário: Falha em "Resolver agora" ou no push

- **Given** o usuário interagiu com o alerta
- **When** o `PATCH` falha
- **Then** a navegação segue sem popup; o alerta volta para a lista, pois continua aberto

### Cenário: Alerta dispensado em outra tela

- **Given** a home do Gestor mostra 3 alertas e há mais carregados
- **When** o usuário dispensa um deles, na home ou na Central
- **Then** o alerta sai da home e o próximo sobe para o lugar dele

## Desenho da implementação

### Modelo

- `NotificationsService.closeAlert(alertId)` — `@PATCH("notifications/alerts/{alertId}")`, sem corpo e sem retorno.
- `model/usecase/notification/DismissedAlerts.kt` — `object` com um `StateFlow<Set<String>>` dos ids dispensados nesta sessão. É a fonte única de quem some das listas: a remoção, a volta em caso de falha e a sincronia entre telas saem dele.
- `model/usecase/notification/DismissAlert.kt`
  - `execute(alertId)` marca o id em `DismissedAlerts` antes de chamar o `PATCH`, para o card sair na hora.
  - Falha: tira o id do conjunto e relança a exceção, para o card voltar.
  - `404`: tratado como sucesso. O alerta não pertence a esse usuário ou não existe, então não deve voltar à lista.
  - O `PATCH` roda em `NonCancellable`: sair da tela logo depois de dispensar não pode cancelar o fechamento.
  - A chamada vem de um parâmetro do construtor (padrão `AdmCoreClient`), para os testes.

### Deep link do push

- `PendingAlertDeepLink` guarda também o `alertId` (`stash`/`takeAlertId`).
- `consumePendingAlertDeepLink()` dispara o `DismissAlert` em um escopo próprio, antes de resolver a rota. Assim o push sem rota também dispensa.
- Só consome o `alertId` com `AdmCoreClient.isReady()` e token salvo. Caso contrário ele fica pendente, e a Splash, o login ou o cadastro o consomem depois.
- **Dependência do back-end:** hoje o push ainda não envia o `alertId`. O app assume o nome `alertId` em `message.data`, a confirmar na documentação. Sem o campo, nada é fechado pelo push e o resto do fluxo não muda.

### ViewModels

- `AlertCentralViewModel`
  - Pede só `status=OPEN`. Sem o filtro, a API devolve abertos e fechados.
  - Guarda os alertas carregados e expõe em `state.notifications` os que não estão em `DismissedAlerts`, observando o `StateFlow`.
  - `onAlertDismiss(notification)`: chama `DismissAlert`; em falha, preenche `errorMessage` com `errorDescription`.
  - `onAlertClick(notification)`: navega e dispensa, sem popup em caso de falha.
  - Paginação: fechar alertas desloca as páginas seguintes, e `nextPage` pularia itens. A página passa a ser `visíveis / PageSize` (função pura); repetidos já são descartados por `distinctBy`.
  - Se a lista visível esvaziar e `hasMore` for verdadeiro, carrega a próxima página. O `LoadMoreEffect` não dispara com `itemCount == 0`.
- `ManagerHomeViewModel`
  - Mesma observação de `DismissedAlerts`, com o mesmo `onAlertDismiss` e `onAlertClick`.
  - Guarda a página inteira e exibe os `HOME_ALERTS_LIMIT` primeiros visíveis, para o próximo subir na hora.
  - Recarrega os alertas ao voltar para a tela (`LifecycleResumeEffect`), sem skeleton.

### Componentes

- Sem biblioteca nova: `SwipeToDismissBox` (Material 3) e `Modifier.animateItem()` (foundation) já vêm pela BOM `2025.09.00`. O ícone é o `ZeraIcon.Close`.
- `Notification` ganha `onDismiss: (() -> Unit)?`. Quando preenchido, mostra o X no canto do card.
- `view/components/cards/DismissibleNotification.kt` (novo) — envolve o `Notification` com o deslize nos dois sentidos e o fundo revelado no gesto. Com KDoc e `@Preview`.
- `NotificationList` e `notificationItems` ganham `onItemDismiss` opcional e animam a entrada e a saída dos itens. Com `null`, o card fica como hoje: a Central de Trabalho e a home do Funcionário não mudam.

### Telas

- `AlertCentralScreen` e `ManagerHomeScreen` passam `viewModel::onAlertDismiss` como `onItemDismiss`.
- `ManagerHomeScreen` adiciona o `LifecycleResumeEffect` que recarrega os alertas.
- A Central não recarrega ao voltar: ela é recriada a cada entrada, e recarregar a página 0 perderia a posição da rolagem. `DismissedAlerts` já a mantém atualizada.

## Critérios de aceite

- [ ] `DismissAlert` fecha o alerta na API, volta o id em falha e trata `404` como sucesso
- [ ] Central de Alertas lista só alertas `OPEN`
- [ ] Deslizar ou tocar no X remove o card com animação e fecha o alerta
- [ ] "Resolver agora" navega e fecha o alerta
- [ ] Falha ao deslizar ou no X devolve o card à posição e mostra o erro
- [ ] Toque no push com `alertId` fecha o alerta, com ou sem rota, e espera o login se preciso
- [ ] Dispensar não pula itens na paginação
- [ ] Dispensar na Central reflete na home do Gestor
- [ ] Central de Trabalho e home do Funcionário continuam sem deslize e sem X
- [ ] Testes unitários do use case, do deep link e das regras dos ViewModels
- [ ] Docs atualizadas

## Edge cases considerados

- Lista esvaziada com páginas restantes: carrega a próxima em vez de mostrar "Nenhuma notificação".
- `GET` em andamento durante o `PATCH`: o alerta pode voltar na resposta, mas o filtro por `DismissedAlerts` o mantém fora da lista.
- Dispensar e sair da tela logo depois: o `PATCH` termina mesmo com o `viewModelScope` cancelado.
- Tocar duas vezes ou repetir a dispensa: o `PATCH` é idempotente.
- Intent reentregue ao recriar a Activity: pode repetir o `PATCH`, sem efeito.
- Push tocado com a Activity aberta durante o boot ou deslogado: `alertId` fica pendente, sem chamar um client ainda não iniciado.
- Falha de um card já desfeita pelo usuário: o gesto não pode ficar aplicado quando o card volta à lista.

## Fora de escopo desta spec

- "Resolver agora" abrir o item específico. A resposta documentada do `GET` não traz `deepLink`; será tratado depois.
- Enviar `alertId` no push: mudança do back-end.
- Dois pushes de mesmo título se substituem na bandeja, porque o id da notificação vem de `title.hashCode()`. Só o último `alertId` chega ao app; os anteriores seguem na lista até serem dispensados.
- Desfazer a dispensa.

## Referências

- Confluence (espaço Back-end): "5. API ms-administrative-core", "4. Contratos entre serviços" e "6. Motor de regras, alertas e previsão de falha"
- Contrato local: [../contrato-adm-core-alertas/contrato.md](../contrato-adm-core-alertas/contrato.md)
- Erros e tela de conexão: [telas-de-erro.md](telas-de-erro.md)
- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md)

## Checklist de execução

Uma etapa por vez, com revisão antes da próxima.

1. [x] `closeAlert` + `DismissedAlerts` + `DismissAlert` + testes
2. [x] Dispensa pelo push em `PendingAlertDeepLink` (`alertId` provisório) + testes
3. [x] `AlertCentralViewModel`: filtro `OPEN`, `DismissedAlerts`, paginação, `onAlertDismiss` e `onAlertClick` + testes
4. [x] `ManagerHomeViewModel`: `DismissedAlerts`, limite da home, recarga, `onAlertDismiss` e `onAlertClick` + testes
5. [ ] `Notification` com X + `DismissibleNotification`
6. [ ] `NotificationList` e `notificationItems` com `onItemDismiss` e animação
7. [ ] Ligar `AlertCentralScreen` e `ManagerHomeScreen`
8. [ ] Atualizar docs (`contrato-adm-core-alertas/contrato.md`, `06-contratos-api.md`, `03-catalogo-componentes.md`, `09-stack-e-dependencias.md`, status desta spec) e conferir o nome `alertId` na documentação do back-end

# Spec — Descartes agendados e Detalhes do agendamento

## Metadados

- **Feature:** Lista de descartes agendados e tela de detalhes de um agendamento
- **Perfil(is) envolvido(s):** Gestor
- **Status:** em progresso (UI e navegação prontas; dados mockados)
- **Última atualização:** 2026-10-06

## Contexto

`Route.ScheduledDisposals` ("Descartes agendados") é aberta pela `ManagerSideBar` e lista os descartes já agendados com uma recicladora. Tocar em um item abre `Route.SchedulingDetails` ("Agendamento"), com data/horário, ações de alteração, contato com a recicladora e cancelamento. Ambas usam dados mockados nos ViewModels (`ScheduledDisposalsViewModel`, `SchedulingDetailsViewModel`); o contrato de API ainda não foi definido.

## User story

Como Gestor, quero ver os descartes agendados e abrir os detalhes de cada um, para alterar o agendamento, contatar a recicladora ou cancelar.

## Cenários (Given/When/Then)

### Cenário: Abrir a lista

- **Given** o Gestor abre a `ManagerSideBar`
- **When** escolhe "Descartes agendados"
- **Then** navega para `Route.ScheduledDisposals`, que mostra o card "Próximo descarte" (quando há), o botão "Agendar próximo descarte" e a `OptionList` de descartes

### Cenário: Abrir os detalhes

- **Given** a lista de descartes agendados está visível
- **When** o Gestor toca em um item
- **Then** navega (animação `SlideHorizontal`) para `Route.SchedulingDetails`

### Cenário: Contato desativado

- **Given** `SchedulingDetailsState.contactButtonsEnabled` é `false`
- **When** a tela é exibida
- **Then** os botões "E-mail" e "Telefone" ficam desabilitados

## Critérios de aceite

- [x] `ScheduledDisposals` acessível pela sidebar; item da lista navega para `SchedulingDetails`
- [x] `SchedulingDetailsScreen` usa `OptionList(hasIcon = false)` para "Alterar data ou horário" e "Adicionar ou remover produtos"
- [x] `contactButtonsEnabled` controla E-mail e Telefone em conjunto
- [ ] `Route.SchedulingDetails` receber o id do agendamento (hoje todo item abre o mesmo mock)
- [ ] Ações implementadas: "Gerar relatório", alterar data/horário, editar produtos, e-mail, telefone, cancelar agendamento (hoje `TODO` no `SchedulingDetailsViewModel`)
- [ ] "Agendar próximo descarte" e o card "Próximo descarte" navegarem

## Edge cases considerados

- Sem descartes agendados: `OptionList` mostra "Nenhum descarte agendado".
- Sem próximo descarte (`nextDisposalDate` vazio): o card azul não é exibido.

## Fora de escopo desta spec

- Fluxo de criação do agendamento (`RecyclingScreen` → `SchedulingSuccessScreen`).
- Menu lateral em `SchedulingDetailsScreen`: usa `Scaffold` simples, então o botão de menu da `UpperNavBar` não abre nada.

## Referências

- Componentes: `../03-catalogo-componentes.md` (`OptionList`/`OptionListItem` com `hasIcon`, `Notification`)

# Spec — Home do Operário (Visão Geral)

## Metadados

- **Feature:** Tela inicial do Operário
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (UI pronta; dados mockados e navegação pendente)
- **Última atualização:** 2026-10-04

## Contexto

Primeira tela do Operário após o login (`Route.EmployeeHome`). Reaproveita os componentes da home do Gestor ([manager-home-dashboard.md](manager-home-dashboard.md)) dentro do `EmployeeScaffold`, com textos próprios do fluxo de Operário. Só nome e cargo vêm de dado real (`GetSelfUser`); ocupação, atalhos, pendências e últimos itens são mock no `EmployeeHomeViewModel` até o back expor um dashboard do Operário.

## User story

Como Operário, quero ver a ocupação do estoque, meus atalhos de manutenção/descarte, minhas pendências de cadastro e os últimos itens, para retomar o trabalho rapidamente.

## Cenários (Given/When/Then)

### Cenário: Exibição da home

- **Given** o Operário está autenticado e abre `Route.EmployeeHome`
- **When** a tela é composta
- **Then** mostra nome e cargo (de `GetSelfUser`), `StockOccupationCard`, botões "Escanear" e "Adicionar Modelo", atalhos "Manutenção" e "Descartes", a lista de pendências e os últimos itens

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
- [x] Pendência exibida com `NotificationItem(style = Red, redirectLabel = "Continuar preenchimento")`
- [ ] Dados reais de ocupação, manutenção, descartes, pendências e últimos itens
- [ ] Navegação de "Escanear", "Adicionar Modelo", "Manutenção", "Descartes", pendências e "Ver Todos" (hoje `TODO` no `EmployeeHomeViewModel`)

## Edge cases considerados

- Lista de pendências vazia: `NotificationList` mostra "Nenhuma notificação".

## Fora de escopo desta spec

- Sidebar do Operário (o botão de menu da `UpperNavBar` ainda não faz nada).
- Navegação da `EmployeeBottomNavBar` (ver "Inconsistências conhecidas" em [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md)).

## Referências

- Componentes: `../03-catalogo-componentes.md` (`EmployeeScaffold`, `Notification`)
- Spec relacionada: [manager-home-dashboard.md](manager-home-dashboard.md)

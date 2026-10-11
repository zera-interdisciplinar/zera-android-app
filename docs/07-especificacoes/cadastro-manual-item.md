# Spec — Cadastro manual de item (Operário)

## Metadados

- **Feature:** Cadastro de item sem etiqueta (`ManualRegisterScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (formulário, modelos da unidade e `POST /items` prontos)
- **Última atualização:** 2026-10-11

## Contexto

Itens são cadastrados à mão em `Route.ManualRegister(modelName: String? = null, barcode: String? = null)`. A tela é aberta da `ScanScreen` ("Cadastrar sem etiqueta"), do scan quando o código não existe (`barcode` preenchido), da `EmployeeItemsScreen` ("Adicionar novo item") e da `ModelItemsScreen` na visão do Operário (com o `modelName` do modelo aberto).

Os modelos vêm de `GET /api/v1/models` (`GetModels`, unidade no `X-Unit-Id`). Condição e tipo de dano são listas fixas alinhadas aos enums do contrato. O envio é `POST /api/v1/items` (`CreateItem`).

## User story

Como Operário, quero cadastrar manualmente um item sem etiqueta, para que ele entre no estoque mesmo sem código para escanear.

## Cenários (Given/When/Then)

### Cenário: Abrir sem modelo definido

- **Given** o Operário está em `ScanScreen` ou `EmployeeItemsScreen`
- **When** toca em "Cadastrar sem etiqueta" / "Adicionar novo item"
- **Then** navega para `Route.ManualRegister()` com todos os campos vazios

### Cenário: Abrir com barcode lido no scan

- **Given** o Operário leu um código que o back não conhece (`404`)
- **When** a busca termina
- **Then** navega para `Route.ManualRegister(barcode = <código>)` e o campo "Código de barras" já vem preenchido

### Cenário: Abrir a partir de um modelo

- **Given** o Operário está na lista de itens de um modelo
- **When** toca em "Adicionar novo item"
- **Then** navega para `Route.ManualRegister(modelName = <modelo>)` e o campo "Modelo" já vem preenchido

### Cenário: Modelo recebido fora das opções

- **Given** o `modelName` recebido não está na página de modelos
- **When** a tela abre
- **Then** o nome é acrescentado ao fim das opções; sem `id` o envio continua desabilitado (`canSubmit` exige `selectedModelId`)

### Cenário: Falha ao carregar modelos

- **Given** `GET /models` falha
- **When** a tela abre
- **Then** o dropdown fica desabilitado, `modelsError` aparece e o barcode (se houver) permanece

### Cenário: Habilitar o envio

- **Given** o formulário está aberto
- **When** número de série, item, modelo com id, ano de fabricação, data de aquisição e condição estão preenchidos (e, se "Possui danos?" está marcado, o tipo de dano também)
- **Then** `ManualRegisterState.canSubmit` fica `true` e "Concluir Cadastro" é habilitado

### Cenário: Marcar e desmarcar "Possui danos?"

- **Given** o formulário está aberto
- **When** o Operário alterna "Possui danos?" (`ZeraRadioButton`)
- **Then** marcado, aparece o dropdown "Tipo de Dano"; desmarcado, o dropdown some e `damageType` é limpo

### Cenário: Concluir cadastro

- **Given** o formulário está válido
- **When** toca em "Concluir Cadastro"
- **Then** o app envia `POST /api/v1/items` (barcode omitido se vazio; data em `YYYY-MM-DD`; condição e dano nos enums do contrato) e, em sucesso, abre `Route.ItemDetails` removendo o formulário da pilha

### Cenário: Erro no envio

- **Given** o `POST` falha (rede, `400` ou `409`)
- **When** o envio termina
- **Then** a tela permanece e `submitError` é exibido

### Cenário: Cancelar

- **Given** o formulário está aberto
- **When** toca em "Cancelar"
- **Then** volta com `ZeraNavigator.goBack()` sem enviar

## Critérios de aceite

- [x] Campos: código de barras, número de série, item, modelo, ano de fabricação, data de aquisição (`ZeraDateInput`), condição, intensidade de uso (`ZeraSliderInput`, padrão 1), "Possui danos?" (`ZeraRadioButton`), tipo de dano e observação
- [x] Só a observação é opcional; o tipo de dano só é exigido quando há dano; o barcode não entra em `canSubmit` (cadastro sem etiqueta continua válido)
- [x] `modelName` e `barcode` opcionais na rota, com a `key` do `ViewModel` (`manual-register-$modelName-$barcode`) para não reaproveitar o estado
- [x] Enviar o cadastro ao back (`POST /api/v1/items`); sucesso abre o detalhe do item
- [x] Carregar modelos cadastrados do back no lugar da lista fixa
- [ ] Carregar condições e tipos de dano de uma fonte oficial (hoje enums fixos do contrato)
- [ ] Mostrar erro de validação por campo (hoje só o botão fica desabilitado)

## Edge cases considerados

- Abrir sem `modelName` mantém o campo "Modelo" vazio até o Operário escolher.
- Abrir sem `barcode` deixa o campo "Código de barras" vazio e editável.
- Desmarcar "Possui danos?" descarta o tipo de dano já escolhido.
- Ano de fabricação usa teclado numérico (`ZeraInputType.Number`); valor não numérico impede o POST.
- Segundo toque em "Concluir Cadastro" enquanto `isSubmitting` é ignorado.

## Fora de escopo desta spec

- Cadastro de modelo.
- Decodificação da etiqueta (ver [scan-item.md](scan-item.md)).

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`ZeraDateInput`, `ZeraSliderInput`, `ZeraRadioButton`, `ZeraDropdownInput`)
- Specs relacionadas: [scan-item.md](scan-item.md), [model-items.md](model-items.md), [employee-items.md](employee-items.md)

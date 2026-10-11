# Spec — Cadastro manual de item (Operário)

## Metadados

- **Feature:** Cadastro de item sem etiqueta (`ManualRegisterScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (formulário e navegação prontos; envio ao back pendente)
- **Última atualização:** 2026-10-09

## Contexto

Itens que não têm etiqueta para escanear são cadastrados à mão em `Route.ManualRegister(modelName: String? = null)`. A tela é aberta de dois lugares: da `ScanScreen` ("Cadastrar sem etiqueta", sem modelo) e da `ModelItemsScreen` na visão do Operário ("Adicionar novo item", com o `modelName` do modelo aberto). Os campos seguem o payload esperado pelo back para um item; as opções dos dropdowns (modelos, condições e tipos de dano) são fixas no `ManualRegisterViewModel` até o back expor essas listas.

## User story

Como Operário, quero cadastrar manualmente um item sem etiqueta, para que ele entre no estoque mesmo sem código para escanear.

## Cenários (Given/When/Then)

### Cenário: Abrir sem modelo definido

- **Given** o Operário está em `ScanScreen`
- **When** toca em "Cadastrar sem etiqueta"
- **Then** navega para `Route.ManualRegister()` com todos os campos vazios

### Cenário: Abrir a partir de um modelo

- **Given** o Operário está na lista de itens de um modelo
- **When** toca em "Adicionar novo item"
- **Then** navega para `Route.ManualRegister(modelName = <modelo>)` e o campo "Modelo" já vem preenchido

### Cenário: Modelo recebido fora das opções

- **Given** o `modelName` recebido não está em `modelOptions`
- **When** a tela abre
- **Then** o modelo é acrescentado ao fim das opções, para o dropdown poder exibir o valor selecionado

### Cenário: Habilitar o envio

- **Given** o formulário está aberto
- **When** número de série, item, modelo, ano de fabricação, data de aquisição e condição estão preenchidos (e, se "Possui danos?" está marcado, o tipo de dano também)
- **Then** `ManualRegisterState.canSubmit` fica `true` e "Concluir Cadastro" é habilitado

### Cenário: Marcar e desmarcar "Possui danos?"

- **Given** o formulário está aberto
- **When** o Operário alterna "Possui danos?" (`ZeraRadioButton`)
- **Then** marcado, aparece o dropdown "Tipo de Dano"; desmarcado, o dropdown some e `damageType` é limpo

### Cenário: Concluir ou cancelar

- **Given** o formulário está aberto
- **When** toca em "Concluir Cadastro" (habilitado) ou "Cancelar"
- **Then** o teclado é fechado (só na conclusão) e a tela volta com `ZeraNavigator.goBack()`; nenhum dado é enviado ao back por enquanto

## Critérios de aceite

- [x] Campos: número de série, item, modelo, ano de fabricação, data de aquisição (`ZeraDateInput`), condição, intensidade de uso (`ZeraSliderInput`, padrão 1), "Possui danos?" (`ZeraRadioButton`), tipo de dano e observação
- [x] Só a observação é opcional; o tipo de dano só é exigido quando há dano
- [x] `modelName` opcional na rota, com a `key` do `ViewModel` por modelo (`manual-register-$modelName`) para não reaproveitar o estado de outro modelo
- [ ] Enviar o cadastro ao back (`onConfirmClick` só volta à tela anterior)
- [ ] Carregar modelos cadastrados do back no lugar da lista fixa
- [ ] Carregar condições e tipos de dano de uma fonte oficial
- [ ] Mostrar erro de validação por campo (hoje só o botão fica desabilitado)

## Edge cases considerados

- Abrir sem `modelName` mantém o campo "Modelo" vazio e as opções fixas.
- Desmarcar "Possui danos?" descarta o tipo de dano já escolhido.
- Ano de fabricação usa teclado numérico (`ZeraInputType.Number`), mas não há validação de faixa.

## Fora de escopo desta spec

- Contrato de criação de item com o back.
- Cadastro de item por leitura da etiqueta (ver [scan-item.md](scan-item.md)).

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`ZeraDateInput`, `ZeraSliderInput`, `ZeraRadioButton`, `ZeraDropdownInput`)
- Specs relacionadas: [scan-item.md](scan-item.md), [model-items.md](model-items.md)

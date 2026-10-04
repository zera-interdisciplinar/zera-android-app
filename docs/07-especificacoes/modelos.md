# Spec — Modelos do Gestor

## Metadados

- **Feature:** Consulta, orientação e cadastro de modelos (`ModelsScreen`)
- **Perfil(is) envolvido(s):** Gestor
- **Status:** interface em progresso; persistência pendente
- **Última atualização:** 2026-10-03

## Contexto

Modelos representam tipos de item reutilizáveis. A interface atual apresenta a área de modelos, uma orientação de uso, o formulário de cadastro e uma confirmação visual.

## User story

Como Gestor, quero preencher os dados de um modelo e ver uma confirmação do fluxo, para preparar seu uso nos próximos cadastros de item.

## Cenários (Given/When/Then)

### Cenário: Orientação de uso

- **Given** o Gestor abre a tela de modelos
- **When** `isModelUsageSheetVisible` está ativo
- **Then** a tela apresenta “Como usar o Modelo”; o estado atualmente inicia visível em uma nova instância do ViewModel

### Cenário: Abrir o formulário

- **Given** o Gestor está na área de modelos
- **When** toca em “Adicionar novo modelo”
- **Then** navega para `Route.ModelCreation`

### Cenário: Preencher os dados

- **Given** o Gestor está no formulário
- **When** informa nome, material, marca e observação
- **Then** os valores ficam no estado de `ModelCreationViewModel`; material é selecionado em `ZeraDropdownInput`

### Cenário: Exibir confirmação visual

- **Given** existem valores no formulário
- **When** toca em “Concluir Cadastro”
- **Then** navega para `Route.ModelCreationSuccess` e exibe os valores enviados pela rota

### Cenário: Cadastrar outro modelo ou ir para início

- **Given** a confirmação está aberta
- **When** toca em “Registrar outro modelo”, “Ir para home” ou fechar
- **Then** abre um formulário novo ou retorna à home do Gestor, conforme a ação

## Critérios de aceite

- [x] Rotas separadas para listagem, formulário e confirmação
- [x] BottomSheet de orientação controlado pelo estado de ModelsViewModel
- [x] Campos controlados pelo `ModelCreationViewModel`
- [x] Confirmação mostra nome, material, marca e observação quando preenchidos
- [ ] Validar campos e persistir o modelo pela API
- [ ] Carregar opções de material de uma fonte oficial
- [ ] Implementar regra durável para mostrar a orientação somente no primeiro acesso

## Edge cases considerados

- Campos opcionais em branco não aparecem no resumo de confirmação.
- A lista de materiais atual é placeholder.
- A tela de confirmação não prova gravação: o `onCreateClick` ainda não chama backend.

## Fora de escopo desta spec

- Definição do contrato de criação/aprovação de modelo com o backend.
- Uso do modelo no cadastro de itens.

## Referências

- Regras: [../05-regras-de-negocio/modelos.md](../05-regras-de-negocio/modelos.md)
- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md)

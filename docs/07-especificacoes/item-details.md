# Spec — Página de Detalhes do Item (PDI)

## Metadados

- **Feature:** Detalhe de um item (`ItemDetailsScreen`)
- **Perfil(is) envolvido(s):** Gestor
- **Status:** implementado (somente leitura; Editar/Aprovar ainda sem API)
- **Última atualização:** 2026-09-26

## Contexto

A PDI era mockada. A rota só entrega `itemId`. O detalhe precisa de `GET /api/v1/items/{id}` para não depender do objeto da lista (que é magro e pode estar velho).

## User story

Como Gestor, quero ver os dados reais de um item ao abri-lo, para conferir status, modelo, categoria e cadastro antes de decidir editar ou aprovar.

## Cenários (Given/When/Then)

### Cenário: Carga com sucesso

- **Given** a navegação abre `Route.ItemDetails(itemId)`
- **When** `GetItemDetails.execute(itemId)` responde 200
- **Then** o card mostra nome, `model.name` como subtítulo, chip de status e condição; as linhas mostram categoria, materiais, condição, cadastrado por e data

### Cenário: Item de outra unidade ou inexistente

- **Given** a API responde 404
- **When** a tela termina o load
- **Then** `errorMessage` aparece; o restante do layout permanece (campos vazios)

### Cenário: Voltar

- **Given** a PDI está aberta
- **When** o Gestor toca voltar
- **Then** `ZeraNavigator.goBack()`

### Cenário: Editar / Aprovar

- **Given** os botões estão visíveis
- **When** o Gestor toca Editar ou Aprovar
- **Then** nada é enviado à API (TODOs no `ItemDetailsViewModel`)

## Critérios de aceite

- [x] Sem mock; carga por `itemId` da rota
- [x] Mapeamento do contrato (nome, modelo, status, categoria, materiais, condição, autor, data)
- [x] Erro de rede/404 visível
- [ ] `POST .../approve` no botão Aprovar
- [ ] `PATCH .../{id}` no botão Editar / lápis
- [ ] Botão Recusar (`POST .../reject`)

## Edge cases considerados

- `itemId` em branco: `loadItem` não chama a API.
- `createdAt` que não parseia como `LocalDateTime`: a tela mostra o ISO cru.
- Vários materiais: “A, B e C”.
- Status desconhecido: chip some (`status` null).

## Fora de escopo desta spec

- Listagem (`itens.md`).
- Fluxo escrito de aprovação/edição (regra de negócio ainda pendente).
- `ItemApprovedScreen` (tela irmã, não ligada a este GET).

## Referências

- Regras: [../05-regras-de-negocio/itens.md](../05-regras-de-negocio/itens.md)
- Contratos: [../contrato-pdi/contrato-detalhes-itens.md](../contrato-pdi/contrato-detalhes-itens.md), [../contrato-pdi/contexto.md](../contrato-pdi/contexto.md)
- Spec de listagem: [itens.md](itens.md)

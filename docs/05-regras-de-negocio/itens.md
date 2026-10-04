# Regras de Negócio — Catálogo e Detalhe de Itens

## Metadados

- **Domínio:** Inventário (listagem e PDI)
- **Perfil(is) envolvido(s):** Gestor
- **Última atualização:** 2026-10-03

## Visão geral

Como o app traduz `GET /api/v1/items`, `GET /api/v1/items/{id}` e `GET /api/v1/categories` para filtros, lista e detalhe. O backend filtra por unidade (`X-Unit-Id`); o app não mistura unidades no cliente.

## Regras

### Fonte da listagem

Catálogo = `GET /api/v1/items`. Não usar `dashboard/home.recentItems` nem `dashboard/work-center`.

### Busca

`q` só vai na request se o texto trimado não for vazio. Casa `displayCode`, nome, modelo e material. Não casa UUID `id`.

### Filtros do catálogo

Os filtros são escolhidos no BottomSheet aberto pelo botão de filtro. Status e Categoria permitem seleção múltipla. Os chips exibidos abaixo da busca representam somente filtros aplicados; tocar em um chip remove aquele valor e recarrega a lista. Abrir e fechar o BottomSheet sem aplicar descarta o rascunho de seleção.

Seleções dentro do mesmo grupo usam OR; Status e Categoria combinados usam AND. Como `GET /api/v1/items` aceita um `status` e um `categoryId` por chamada, o app consulta cada combinação do produto cartesiano entre os valores escolhidos e mescla as páginas por `id`. Sem seleção em um grupo, esse parâmetro fica ausente. Sem nenhum filtro, é feita uma consulta sem `status` e sem `categoryId`.

O grupo Status usa os valores visíveis de `ItemStatus`: Pendente, Recusado, Aprovado, Em manutenção, Em aprovação e Descartado. O grupo Categoria usa os nomes obtidos por `GET /api/v1/categories`, convertidos para seus UUIDs em `categoryId`. A seção Situação ainda não tem opções nem regra e não aparece no filtro ativo.

### Status na UI

`ItemStatus.fromBackend`: `PENDING_APPROVAL`/`PENDING` → Pendente; `REJECTED` → Recusado; `IN_STOCK`/`APPROVED` → Aprovado; `IN_MAINTENANCE` → Em manutenção; `AWAITING_EVALUATION` → Em aprovação; `DISPOSED` → Descartado. `DRAFT` e `REMOVED` não têm chip.

### Página da lista

Tamanho 20. Título = `totalElements` formatado (`1 Item` / `N Itens`). Próxima página só se `loadedPage + 1 < totalPages` e não houver load em andamento.

### Detalhe independente da lista

A PDI sempre busca `GET /api/v1/items/{id}`. Subtítulo = `model.name`. Categoria = `model.category.name`. Materiais = nomes do modelo, em português com “e”. Condição: `NEW` Novo, `USED` Usado, `SEMI_DAMAGED` Semidanificado, `DAMAGED` Danificado.

### Escrita

- **Aprovar:** `POST /api/v1/items/{id}/approve`, sem corpo, papel `MANAGER`. Sucesso → `ItemApprovedScreen`. `409` = transição inválida.
- **Recusar:** `POST /api/v1/items/{id}/reject` com `reason` obrigatório.
- **Editar:** `PATCH /api/v1/items/{id}` parcial. A PDI envia só `name` ou só `condition`. `name` 1–120. Categoria/material não vão no PATCH.

## Relacionamento com perfis

Implementado no app para o Gestor (`Route.Itens` / `Route.ItemDetails`). O interceptor manda o `unitId` da sessão.

## Referências

- Specs: [../07-especificacoes/itens.md](../07-especificacoes/itens.md), [../07-especificacoes/item-details.md](../07-especificacoes/item-details.md)
- Contratos: [../contrato-pdi/contrato-detalhes-itens.md](../contrato-pdi/contrato-detalhes-itens.md)
- Modelo: [../04-modelo-de-dados.md](../04-modelo-de-dados.md)

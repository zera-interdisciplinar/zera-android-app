# Regras de Negócio — Catálogo e Detalhe de Itens

## Metadados

- **Domínio:** Inventário (listagem e PDI)
- **Perfil(is) envolvido(s):** Gestor
- **Última atualização:** 2026-09-26

## Visão geral

Como o app traduz `GET /api/v1/items`, `GET /api/v1/items/{id}` e `GET /api/v1/categories` para chips, lista e card de detalhe. O backend filtra por unidade (`X-Unit-Id`); o app não mistura unidades no cliente.

## Regras

### Fonte da listagem

Catálogo = `GET /api/v1/items`. Não usar `dashboard/home.recentItems` nem `dashboard/work-center`.

### Busca

`q` só vai na request se o texto trimado não for vazio. Casa `displayCode`, nome, modelo e material. Não casa UUID `id`.

### Chips → query

| Chip | Query |
|---|---|
| Todos | sem `status`, sem `categoryId` |
| Pendentes | duas requests: `PENDING_APPROVAL` e `AWAITING_EVALUATION`; merge por `id`; `totalElements` somado |
| Nome de categoria | `categoryId` = UUID da categoria; sem `status` |

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

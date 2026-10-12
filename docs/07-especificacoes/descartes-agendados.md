# Spec — Histórico de descartes e detalhe

## Metadados

- **Feature:** Lista e detalhe de descartes já registrados
- **Perfil(is) envolvido(s):** Gestor (lista + cancelar); Operário (lista/detalhe sem cancelar)
- **Status:** implementado (API; nomes de rota `ScheduledDisposals` / `SchedulingDetails` são legado de UI)
- **Última atualização:** 2026-10-11

## Contexto

A v1 **não agenda** descarte. `Route.ScheduledDisposals` é o histórico (`GET /api/v1/disposals`). O detalhe lê `GET /disposals/{id}`. Cancelar: `PATCH /disposals/{id}/cancel`, só gestor. Novo registro continua no mapa (`POST /disposals`).

Fonte: [API ms-inventory](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55869441), [contrato-descarte.md](../contrato-places-api/contrato-descarte.md).

## User story

Como Gestor ou Operário, quero ver os descartes da unidade e o detalhe de um registro, para conferir destino, itens e (gestor) cancelar um registro errado.

## Cenários (Given/When/Then)

### Cenário: Abrir a lista

- **Given** o usuário autenticado abre a sidebar
- **When** escolhe o histórico de descartes
- **Then** navega para `Route.ScheduledDisposals` com `GET /disposals` (paginação, `q` e filtros de destino/data)

### Cenário: Abrir os detalhes

- **Given** a lista está visível
- **When** toca em um item
- **Then** navega para `Route.SchedulingDetails` com o `id` do descarte

### Cenário: Contato sem ficha

- **Given** o descarte só tem `placeId`/`placeName`
- **When** a tela não tem e-mail/telefone do adm-core
- **Then** botões de contato visíveis e desabilitados — não é bug da listagem

### Cenário: Cancelar (gestor)

- **Given** o Gestor está no detalhe
- **When** confirma cancelar
- **Then** `PATCH /disposals/{id}/cancel`, `204`, o registro some da lista

## Critérios de aceite

- [x] Lista e detalhe usam a API (não mock)
- [x] Operário não vê cancelar
- [x] Relatório PDF via AI core com o `id` do descarte

## Referências

- [employee-descarte.md](employee-descarte.md)
- [contrato-ai-core-api/reports.md](../contrato-ai-core-api/reports.md)

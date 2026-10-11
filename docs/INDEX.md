# Índice da Documentação — Zera Android

Esta pasta é a fonte de verdade **do app Android**. Contratos de back-end vêm do Confluence (espaço Back-end). O código do app e o Confluence vencem qualquer texto antigo desta pasta.

Fonte canônica do back-end (outubro 2026):

- [Zera Back-end v1](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55803906)
- [1. Domínio](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55541769)
- [2. Máquina de estados](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55836674)
- [3. API ms-inventory](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55869441)
- [4. Contratos entre serviços](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55345170)
- [5. API ms-administrative-core](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55902210)
- [6. Motor de regras e alertas](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55934977)

## 1. Resumo dos documentos

| Documento | Conteúdo |
|---|---|
| [00-contexto-geral.md](00-contexto-geral.md) | Produto, dois papéis (`MANAGER` / `EMPLOYEE`), fluxos do app. |
| [01-arquitetura.md](01-arquitetura.md) | Camadas, `AdmCoreClient` / `InventoryClient` / `ScrapyClient`, `SqliteManager`, navegação. |
| [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md) | Nomenclatura, pastas, Compose/Kotlin. |
| [03-catalogo-componentes.md](03-catalogo-componentes.md) | Design System. |
| [04-modelo-de-dados.md](04-modelo-de-dados.md) | DTOs e sessão em SQLite. |
| [05-regras-de-negocio/](05-regras-de-negocio/) | Home/indicadores, catálogo/PDI, modelos, máquina de estados do item. |
| [06-contratos-api.md](06-contratos-api.md) | O que o app chama hoje (Scrapy, adm-core, inventory, IA). |
| [contrato-scrapy/](contrato-scrapy/) | `POST /v1/boot`, `POST /v1/flags`. |
| [contrato-dashboard/](contrato-dashboard/) | `GET /dashboard/home`, `/work-center`, `/indicators`. |
| [contrato-pdi/](contrato-pdi/) | Itens, categorias, barcode, PATCH/approve/reject. |
| [contrato-adm-core-membros/](contrato-adm-core-membros/) | Users, convites, perfil. **Não existe `POST /users`.** |
| [contrato-adm-core-alertas/](contrato-adm-core-alertas/) | `GET/POST /notifications/alerts`. |
| [contrato-places-api/](contrato-places-api/) | Locais, recicladoras, `POST /disposals`, cancelamento, PDF. |
| [contrato-ai-core-api/](contrato-ai-core-api/) | Chat e `POST /reports`. |
| [07-especificacoes/](07-especificacoes/) | Specs Given/When/Then. |
| [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/) | ADRs. |
| [09-stack-e-dependencias.md](09-stack-e-dependencias.md) | Libs e SQLite. |
| [10-fluxos-de-trabalho.md](10-fluxos-de-trabalho.md) | Checklist por tipo de tarefa. |
| [mudanca-shared-para-sqlite/contexto.md](mudanca-shared-para-sqlite/contexto.md) | Motivo da sessão em SQLite. |

## 2. Guia de leitura por tipo de tarefa

Ver [10-fluxos-de-trabalho.md](10-fluxos-de-trabalho.md).

## 3. Regra de atualização da documentação

> Ao concluir uma feature, indique quais docs mudam. Contratos de inventory/adm-core devem seguir o Confluence acima, não o mapeamento antigo (marcado DESATUALIZADO no espaço Back-end).

## 4. Regra de feedback sobre novas implementações

> Ao final da implementação, compare com `02-padroes-e-convencoes.md`, `01-arquitetura.md` e as regras em `05-regras-de-negocio/`.

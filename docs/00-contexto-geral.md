# 00 — Contexto Geral

Fonte de papéis e domínio: [Back-end v1](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55803906).

## Visão do produto

O Zera gerencia o ciclo de vida de equipamentos eletrônicos **por unidade** de uma organização, do cadastro até o descarte, com alertas de regra e previsão de falha no back-end. O app Android é o ponto operacional: funcionário e gestor cadastram, acompanham e destinam itens.

O back-end da v1 são dois serviços (`ms-administrative-core` + `ms-inventory`) mais o serviço preditivo (time de IA) e o AI core (MCP/PDF). O app não lê o grafo nem o Postgres direto: chama as APIs com JWT do adm-core e `X-Unit-Id`.

## Público-alvo

Empresas com operação de TI e volume de eletrônicos. Piloto: Instituto J&F / JBS. Usuários do app: operários e gestores da unidade, não o público externo.

## Perfis de usuário

A v1 tem **dois papéis**, não três. Claim `role` do access token: `MANAGER` ou `EMPLOYEE`. **Não há Administrador** no modelo de autorização.

- **Funcionário (`EMPLOYEE`)** — leitura autenticada; cadastra e edita itens e modelos; registra descarte; manutenção e avaliação.
- **Gestor (`MANAGER`)** — o mesmo, mais categorias, regras, aprovações, exclusões, transferência de unidade, cancelamento de descarte, convites e equipe.

Leitura no inventory exige só autenticação. `unitId` vai no header `X-Unit-Id`, **nunca no corpo**. Recurso de outra unidade responde `404`, não `403`.

## Principais fluxos de usuário (app hoje)

1. **Onboarding/login** — splash (`POST /v1/boot` Scrapy) → welcome → login (`POST /auth/login`) ou convite (`POST /invitations/redeem`; **não** há `POST /users`) → `POST /v1/flags` → home por `role`. Refresh: `POST /auth/refresh` (ver spec).
2. **Home do gestor** — `GET /dashboard/home` (ocupação, recentes). Indicadores: `GET /dashboard/indicators`. Equipe: `GET /users/count-by-manager` e lista.
3. **Home do operário** — mesma home de ocupação/recentes + pendências via `GET /dashboard/work-center` e Central de Trabalho.
4. **Catálogo e PDI** — `GET /items`, `GET /items/{id}`, approve/reject/PATCH. Item nasce **sempre `DRAFT`**; sai pelo `submit`. Foto **não** é obrigatória.
5. **Modelos e categorias** — listagem/criação no inventory. Modelo de operário nasce `PENDING`; de gestor, `APPROVED`.
6. **Scan** — `GET /items/by-barcode/{barcode}`. `barcode` ≠ `displayCode` (este é o código de 6 dígitos da UI).
7. **Descarte** — registro de algo **já feito** (`POST /disposals`). Não há agendamento; `disposedAt` nunca é futuro. Histórico: `GET /disposals`. Cancelar: `PATCH /disposals/{id}/cancel` **só gestor**. PDF: AI core `POST /reports`. Mapa: `GET /recycling-places`.

## Fora de escopo (produto v1)

- Agendar descarte futuro.
- Catálogo próprio de recicladoras parceiras no inventory (só `placeId`/`placeName` do Places).
- Galeria de fotos por item (uma foto).
- Meta de reciclagem.
- Perfil Administrador no app.
- Automação física da coleta.

Previsão de falha **existe no back-end** (job + serviço preditivo). O app consome `predictedFailureDate` quando a API devolve; não chama o preditivo direto.

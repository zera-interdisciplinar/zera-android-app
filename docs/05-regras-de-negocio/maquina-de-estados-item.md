# Regras de Negócio — Máquina de estados do item

Fonte: [Confluence — Máquina de estados](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55836674).

## Metadados

- **Domínio:** Inventário
- **Perfil(is) envolvido(s):** Gestor, Operário
- **Última atualização:** 2026-10-11

## Estados

| Estado | Significa | Ativo |
|---|---|---|
| `DRAFT` | Cadastro salvo; ainda não entrou no estoque. | Sim |
| `PENDING_APPROVAL` | Fila do gestor (submit do operário). | Sim |
| `REJECTED` | Reprovado; corrige e reenvia. | Sim |
| `IN_STOCK` | No estoque. | Sim |
| `IN_MAINTENANCE` | Em manutenção. | Sim |
| `AWAITING_EVALUATION` | Voltou da manutenção, espera avaliação — **não** é a fila de cadastro. | Sim |
| `DISPOSED` | Descartado. Volta só se o **gestor** cancelar o descarte. | Não |
| `REMOVED` | Remoção lógica. | Não |

Ativo = tudo menos `DISPOSED` e `REMOVED`. `GET /items` **sem** `status` lista só ativos.

## Regras que o app deve respeitar

1. Todo item nasce `DRAFT`. Quem tira do rascunho é sempre `POST /items/{id}/submit`. Não reintroduzir atalho de cadastro completo → `PENDING_APPROVAL`.
2. Submit do **gestor** vai a `IN_STOCK` (e aprova o modelo criado junto). Submit do **operário** vai a `PENDING_APPROVAL`.
3. Rascunho incompleto no submit: `422` + `missingFields`. Foto **não** entra em `missingFields`.
4. Preferir o endpoint do passo (`submit`, `approve`, `reject`, `maintenance/*`, `evaluate`, `POST /disposals`) a `PATCH /items/{id}/status` (não tem os efeitos colaterais).
5. `409` em transição: recarregar o item; não é falha de rede.
6. Ordem natural: criar → foto opcional → submit.

Campos para sair do rascunho (ordem do domínio): `barcode`, `name`, `model`, `condition`, `hasDamages` (+ `damages` se true), `usageIntensity` (inteiro 0–10).

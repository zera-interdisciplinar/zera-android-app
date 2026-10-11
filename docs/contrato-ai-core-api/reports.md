# contexto

O fluxo em que o gestor escolhe um ponto no mapa, seleciona os itens e confirma o registro deixa de se chamar agendamento. É o **fluxo de descarte**: o descarte já aconteceu (ou está sendo registrado agora). Não há data futura nem remarcação.

A tela que hoje se chama "Descartes agendados" deixa de ser uma lista de agendamentos e passa a ser o **histórico de descartes**. O detalhe deixa de ser "Agendamento" e passa a ser o registro daquele descarte.

Hoje a geração de relatório de descarte existe no app e falha. O histórico precisa listar os descartes já registrados e abrir o detalhe certo.

## seu papel

Consultar o contrato de descartes em `/api/v1/disposals` (Confluence e `docs/contrato-places-api/contrato-descarte.md`) e o de PDF em `POST /api/v1/reports` (mesma doc, seção de relatório). Não usar `/api/v1/disposals/schedules`: esse contrato de agendamento não vale mais para este fluxo.

Depois:

- listar o histórico com `GET /api/v1/disposals` (paginado)
- no detalhe (`GET /api/v1/disposals/{id}`), mostrar data, local e os itens já descartados
- **remover** a opção "Alterar data ou horário" (o bottom sheet some junto)
- a opção que hoje é "Adicionar ou remover produtos" vira só **visualização** dos itens descartados; não edita a lista
- o botão "Cancelar agendamento" passa a se chamar **"Cancelar descarte"** e chama `PATCH /api/v1/disposals/{id}/cancel` (sem corpo, `204`, só gestor)
- "Gerar relatório" continua em `POST /api/v1/reports` com o `id` do descarte já gravado

## o que sai deste fluxo

- card "Próximo descarte" e botão "Agendar próximo descarte": eram de agendamento. Novo descarte continua no mapa (`Recycling`), fora do histórico
- alterar data ou horário
- adicionar ou remover produtos num registro já feito

Contato com a recicladora (e-mail e telefone) permanece no detalhe.

## planejamento

Primeiro um plano com o passo a passo (rótulos, lista, detalhe somente leitura, relatório e cancelar). Implementar em seguida. Testes por último.

## caso de duvida

Se ficar com qualquer dúvida bloqueante, primeiro veja as docs. Caso contrário, avise.

# ideia refinada

## Fluxo de descarte (mapa)

Escolhe o ponto, seleciona os itens, confirma. O app já chama `POST /api/v1/disposals` em `CreateDisposal` / `RecyclingResume`. `disposedAt` ausente = hoje. **Nunca no futuro.** Itens vão a `DISPOSED`.

## Fluxo de histórico

| Método | Path | Uso neste fluxo |
| --- | --- | --- |
| `GET` | `/api/v1/disposals` | Lista paginada (`page` zero-based, `size` máx. 100). Filtros opcionais, combinados com E: `q`, `destination` (`RECYCLING`, `LANDFILL`, `DONATION`), `disposedFrom`/`disposedTo`, `createdFrom`/`createdTo`, `createdBy`, `placeId`, `itemId`. Envelope `content`, `page`, `size`, `totalElements`, `totalPages`. |
| `GET` | `/api/v1/disposals/{id}` | Detalhe: data, local, itens. `404` se outra unidade. |
| `POST` | `/api/v1/reports` | PDF de um descarte já gravado. `disposal_id` = `id` do descarte. |
| `PATCH` | `/api/v1/disposals/{id}/cancel` | Sem corpo. `204`. Só gestor. Some do histórico e devolve os itens ao estado anterior. `403` operário, `404` inexistente ou já fora da lista, `409` já cancelado. |

Headers do inventory: `Authorization`, `apiKey`, `X-Unit-Id`.

`PATCH /api/v1/disposals/{id}` só corrige o destino. Não entra neste fluxo (não remarca, não mexe em itens).

## Detalhe do histórico

- título e cópia falam em descarte, não em agendamento
- data e local só leitura
- sem "Alterar data ou horário"
- uma opção abre a lista dos itens descartados, somente leitura
- "Cancelar descarte" no lugar de "Cancelar agendamento", via `PATCH .../cancel`
- "Gerar relatório" usa o `id` do descarte, não um id de agendamento

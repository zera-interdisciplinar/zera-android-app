# contexto

Hoje existe uma integração de chatbot no app, mas ela não funciona. Basicamente hoje é possivel gerar relatório de descarte, mas no entanto essa ação dá erro. Além disso, é necessário popular a página de descartes agendados, que hoje só está mockada.

## seu papel

Voce deve consultar no confluence pelo contrato dos endpoints de agendamentos, em /api/v1/disposals. Pode complementar com contratos em outras docs do repositório.

Após isso, voce deve:

- realizar a listagem correta de descartes agendados
- dar funcionalidade ao botão de agendar próximo descarte (direcionando para a tela do mapa)
- mostrar corretamente o próximo descarte no componente do topo da tela
- popular a página de agendamento especifico, mostrando corretamente o horário, local, possibilidades de adicionar/remover produtos, etc, além do cancelamento do agendamento.

## planejamento

Primeiro, realize um plano, identificando como fazer e o passo a passo, depois, implemente com subagent-driven.
Os testes são as ultimas coisas a rodar, pois geram gargalos.

## caso de duvida

Se ficar com qualquer dúvida bloqueante, primeiro veja as docs, caso contrário, me avise.


# ideia refinada

## O que o contrato permite

Confluence [3. API ms-inventory](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55869441) e `docs/contrato-places-api/contrato-descarte.md`:

| Método | Path | Uso |
| --- | --- | --- |
| `POST` | `/api/v1/disposals` | Registra descarte **já feito**. `disposedAt` ausente = hoje. **Nunca no futuro.** Itens vão a `DISPOSED`. |
| `GET` | `/api/v1/disposals` | Lista paginada (`page` zero-based, `size` máx. 100). Envelope `content`, `page`, `size`, `totalElements`, `totalPages`. |
| `GET` | `/api/v1/disposals/{id}` | Detalhe. `404` se outra unidade. |
| `PATCH` | `/api/v1/disposals/{id}` | Só corrige o **destino**. |

Headers: `Authorization`, `apiKey`, `X-Unit-Id`. Não há cancelar, remarcar horário, nem adicionar/remover itens. `POST /api/v1/reports` gera PDF de um descarte já gravado.

O app já chama `POST` em `CreateDisposal` / `RecyclingResume`. A tela de mapa (`Recycling`) já é o fluxo de novo descarte.

## Contrato de agendamento (atualizado)

`/api/v1/disposals/schedules`: `POST`, `GET` (`from`, paginado, ordem `scheduledAt`), `GET /{id}`, `PATCH` (data e itens; o ponto não muda), `DELETE` (`204`). O agendamento não vira descarte sozinho. PDF continua em `POST /api/v1/reports` depois do `POST /disposals`.

Implementado no app: lista e card com `GET ?from=agora`, botão abre `Route.Recycling`, detalhe com horário, local, itens e contato, cancelar com `DELETE`. Alterar data e editar produtos ainda não têm tela (o `PATCH` existe no contrato).
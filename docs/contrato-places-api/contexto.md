# Recicladoras próximas e fluxo de descarte (gestor)

O mapa e o contato da parceira usam o **ms-administrative-core**. O registro do lote é o **ms-inventory**. O PDF é o **ms-artificial-intelligence-core**. O app **não** chama Google Places: GPS → `GET /api/v1/recycling-places?lat=&lng=` no nosso backend → pins. A chave Places fica no servidor. `maps.api.key` só desenha o mapa.

Contratos HTTP: [contrato-places-api.md](contrato-places-api.md) (adm-core: pin, ficha, telefone) e [contrato-descarte.md](contrato-descarte.md) (`POST /disposals` + `POST /reports`). Catálogo `IN_STOCK` na seleção: [contrato-pdi](../contrato-pdi/contrato-detalhes-itens.md).

O `ApiClient` já usa base URL com `api/v1/`. O Retrofit declara só `recycling-places` (sem prefixo duplicado).

Não há endpoint faltando. `disposal_report` no adm-core continua sem HTTP. O app não chama Google Places.

---

## Fluxo (UI)

Registro de um descarte **já feito**. O sistema não agenda: `disposedAt` não pode ser futuro. O inventory grava `destination`, `placeId` e `placeName`. A ficha da parceira (e-mail, telefone) fica no adm-core, ligada ao pin.

Dados entre telas vão no **bundle** (`Route` serializable), não em nova chamada de inventário. Só o gestor. A IA só gera o PDF; sem chat neste fluxo.

```
Recycling
  → ItensSelection(placeId, placeName, placeAddress, distanceMeters)
  → RecyclingResume(+ itemIds, itemNames)
  → ItensResume(itemIds, itemNames)          // voltar = editar na seleção
  → SchedulingSuccess(recycler, data, materiais, email, phone, itemNames, disposalId)
  → ManagerHome (fechar / “voltar para o início”)
```

1. `RecyclingScreen` carrega o mapa. O gestor escolhe um pin.
2. `ItensSelectionScreen` lista itens `IN_STOCK`. Confirmar leva pin + itens no bundle.
3. `RecyclingResumeScreen` mostra o resumo. Horário, “aberto agora” e descrição vêm do pin (`isOpen`, `openingHours`, `description`). “Visualizar itens” abre `ItensResumeScreen` (somente leitura, só bundle).
4. “Confirmar descarte” chama `POST /api/v1/disposals` com `destination=RECYCLING`, `placeId`, `placeName` e `itemIds`. O `id` da resposta segue no bundle.
5. `SchedulingSuccessScreen` (nome de tela legado):
   - E-mail e WhatsApp usam `email` do pin e o telefone de `GET /telephone/recyclings?recyclingBusinessId=`. Sem vínculo, os botões ficam sem contato — não casar pelo nome.
   - “Gerar relatório” chama `POST /api/v1/reports` com o `id` do inventory.
   - “Voltar” vai para a home do gestor.

| Passo | O que o app faz hoje |
|---|---|
| Mapa | `GetNearbyRecyclingPlaces`. Pin selecionado habilita “selecionar itens”. |
| Seleção | `GetItems` (`IN_STOCK`). Confirmar empurra pin + itens no bundle. |
| Resumo | Preenche nome/endereço/distância/itens e `isOpen` / `description` / `openingHours` do pin (bundle). E-mail vem do pin; telefone só se houver `recyclingBusinessId` (`GET /telephone/recyclings`). Sem vínculo, contato fica vazio. Confirmar chama `POST /api/v1/disposals` e navega com o `id` devolvido. |
| Itens read-only | Só o bundle. Sem clique para editar. |
| Sucesso | E-mail → `mailto:`. Telefone → WhatsApp `wa.me`. Relatório → `POST /api/v1/reports` (`AiClient`) e abre `report_url`. Home via `pushAndPopAll`. |

---

## Camadas

### Mapa

| Peça | Onde |
|---|---|
| DTO da lista | `RecyclingPlaceResponseDTO` — pin + `isOpen`, `description`, `openingHours`, `recyclingBusinessId`, `email` |
| Erro RFC 7807 | `ProblemDetailDTO` (`detail`) |
| Entidade | `RecyclingPlace` |
| Service | `RecyclingPlacesService.getNearby` em `ApiClient` |
| Use case | `GetNearbyRecyclingPlaces` |
| View model | `NearbyRecyclingPlacesViewModel` |

- Busca só com GPS (`onUserLocation`). Sem permissão, nada é chamado.
- Marcadores em `lat`/`lng`. POI e trânsito do mapa base desligados.
- Card do pin: nome, endereço, distância. Sem lista no fundo azul.
- `200 []` → “Nenhuma recicladora encontrada por aqui.” `503`/5xx → `detail` + retry. Não misturar os dois.
- Distância: `< 1000` em metros; senão `1,2 km` (pt-BR).
- Raio enviado: `radiusMeters=20000` (teto do contrato). Logs `TEMP_PLACES` ainda no mapa — remover depois.

### Resto do descarte

| Peça | Onde |
|---|---|
| Contato parceira | `GetRecyclingContact`: e-mail do pin + `TelephoneService.getByRecyclingBusiness` quando `recyclingBusinessId` vem preenchido. Sem id, não chama a lista de fichas. |
| Relatório IA | `AiClient` + `ReportsService` + `GenerateDisposalReport`. Boot: `ms-ai-url` / `ms-ai-api-key` em `Environments` (default vazio; se vazio, falha clara). |
| Confirmar descarte | `CreateDisposal` → `InventoryService.createDisposal`. Body: `destination=RECYCLING`, `placeId`, `placeName`, `itemIds`. Sem `disposedAt` (o backend usa hoje; data futura é inválida). O `id` da resposta vai para `SchedulingSuccess` e para `GenerateDisposalReport`. |

### Testes

Mapa: `RecyclingPlaceResponseDTOTest`, `ProblemDetailMessageTest`, `NearbyRecyclingPlacesMappingTest`.

Descarte: `ItensSelectionMappingTest`, `RecyclingResumeMappingTest`, `ItensResumeMappingTest`, `SchedulingSuccessContactTest`, `AiClientAuthTest`, parse de `ms-ai-url` em `EnvironmentsTest`.

---

## Contrato vs app

| # | Contrato | App |
|---|---|---|
| 1 | Pin traz `isOpen`, `description`, `openingHours` | Mapeados no DTO e no bundle até o resumo. Bloco vazio continua oculto. |
| 2 | Pin traz `recyclingBusinessId` e `email`; telefone é `GET /telephone/recyclings` | E-mail do pin; telefone só com vínculo. Sem ficha, botões avisam que o contato não está cadastrado. |
| 3 | `POST /disposals` devolve o `id` e marca `DISPOSED`. Não agenda | Já usado em `onConfirmClick`. Título e card falam em descarte registrado. O nome da rota `SchedulingSuccess` continua legado. Não manda `disposedAt`. |
| 4 | `ms-ai-url` no boot Scrapy | `AiClient` lê `Environments`; sem a URL no boot o relatório falha com mensagem clara |

Não fazer no app: client Google Places, detalhe de lugar via Maps SDK, gravar `disposal_report` no adm-core.

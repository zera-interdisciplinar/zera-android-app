# Recicladoras próximas no mapa

O fluxo de reciclagem mostra a localização do usuário e as cooperativas próximas vindas do `ms-administrative-core`. O contrato de campos está em [contrato-places-api.md](contrato-places-api.md).

O `ApiClient` já usa um base URL que termina em `api/v1/`, então o Retrofit declara só `recycling-places`. Prefixar `api/v1/` no path gerava `404` com a URL duplicada `api/v1/api/v1/recycling-places`.

## Camadas

| Peça | Onde |
|---|---|
| DTO da lista | `model/dto/places/RecyclingPlaceResponseDTO` (`placeId`, `name`, `address`, `lat`, `lng`, `distanceMeters`) |
| Erro RFC 7807 | `model/dto/places/ProblemDetailDTO` (`detail`), lido em `problemDetailMessage` |
| Entidade | `model/entity/places/RecyclingPlace` |
| Service | `RecyclingPlacesService.getNearby(lat, lng, radiusMeters)` registrado em `ApiClient.recyclingPlacesService` |
| Use case | `GetNearbyRecyclingPlaces` mapeia o DTO e, em `HttpException`, lança `NearbyRecyclingPlacesException` com o `detail` |
| View model | `NearbyRecyclingPlacesViewModel` na tela de reciclagem |
| Rota seguinte | `Route.ItensSelection(placeId, placeName, placeAddress, distanceMeters)` |

## Mapa e seleção

- A busca só dispara quando o GPS devolve coordenada (`onUserLocation`). Sem permissão, nada é chamado.
- Cada item vira um marcador em `lat`/`lng`. O mapa esconde pontos de interesse e trânsito do Google (`poi` e `transit` com `visibility: off`); restaurantes e lojas do mapa base não aparecem.
- Toque no marcador seleciona a cooperativa e abre um card claro (nome, endereço, distância). Não há lista de lugares no fundo da tela: nesse fundo azul o texto ficava ilegível.
- **Selecionar itens para descarte** só habilita com uma cooperativa selecionada e leva nome, endereço, `placeId` e distância para `ItensSelectionScreen`, que mostra esses dados no topo.
- `200` com lista vazia: “Nenhuma recicladora encontrada por aqui.” `503` (ou outro 5xx): mensagem do `detail` e “Tentar de novo”. Falha não usa a mesma mensagem de lista vazia.
- Distância: abaixo de 1000 m fica em metros; senão, quilômetros com uma casa (`1240` → `1,2 km`).

## Debug temporário (remover depois)

Tag de log `TEMP_PLACES` (`Log.i`) em `RecyclingScreen`, `CurrentLocationMap` e `NearbyRecyclingPlacesViewModel`. O raio enviado hoje é `radiusMeters=20000`, o teto do contrato (sem o parâmetro o servidor usava 5000).

## Testes

`RecyclingPlaceResponseDTOTest`, `ProblemDetailMessageTest` e `NearbyRecyclingPlacesMappingTest` (lista vazia vs erro, seleção e formatação de distância).

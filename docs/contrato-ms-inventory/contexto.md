# contexto

A API Inventory Dashboard (`ms-inventory`) alimenta a home do gestor e a tela de indicadores. O contrato fechado está em [contrato-inventory-dashboard.md](contrato-inventory-dashboard.md).

## Integração no app

O client segue as camadas do app (não vive em `config`):

```
model
├── entity/config/Environments.kt          ms-inventory-url e ms-inventory-api-key (boot)
├── dto/inventory/                         DTOs slimados do dashboard
├── remote/client/InventoryClient.kt       Retrofit próprio do ms-inventory
├── remote/service/InventoryService.kt     GET api/v1/dashboard/home e .../indicators
└── usecase/inventory/
    ├── GetManagerHome.kt                  home do gestor
    └── GetIndicators.kt                   tela de indicadores
```

`Boot.execute()` chama `ScrapyClient.boot()`, grava em `AppConfig` e inicializa `ApiClient` **e** `InventoryClient` com as URLs/keys do boot.

`InventoryClient`:

- **Base URL:** `ms-inventory-url`. Se o valor terminar em `/api/v1`, esse sufixo é removido — o Retrofit já prefixa `api/v1/...` nas rotas do service.
- **Headers em toda request:** `apiKey` (`ms-inventory-api-key`, ou fallback `ms-adm-core-api-key` se a key de inventory vier vazia), `Authorization: Bearer <accessToken>` do usuário logado, `X-Unit-Id` do `unitId` gravado no login.
- Sem `apiKey` o Kong responde **401**. Sem token, **401**. Sem `X-Unit-Id`, **400**.

O `unitId` é persistido em `SharedPreferencesManager.saveUnitId` depois de `GET users/{userId}` no `SingIn`. `clearSession()` também apaga a unidade.

## Endpoints consumidos

| Rota | Use case | Tela |
|---|---|---|
| `GET /api/v1/dashboard/home` | `GetManagerHome` | `ManagerHomeScreen` |
| `GET /api/v1/dashboard/indicators` | `GetIndicators` | `IndexesScreen` |

`GET /api/v1/dashboard/work-center` existe no contrato e **não** é chamado pelo app (nenhuma tela usa).

Os DTOs no app só declaram os campos que as telas usam; o restante do JSON é ignorado (`ignoreUnknownKeys`).

## O que fazer em caso de dúvida de contrato

Pare imediatamente de desenvolver e informe de forma estruturada o contexto, o problema e o que te afeta. Não invente payload nem campo extra.

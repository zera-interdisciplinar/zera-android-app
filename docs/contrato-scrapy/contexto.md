# contexto

A API Scrapy carrega as informações dinâmicas do app (envs no boot, flags depois do login). O contrato fechado está em [contrato-scrapy-api.md](contrato-scrapy-api.md).

## Integração no app

O client não vive no pacote `config` previsto no rascunho original. A integração atual segue as camadas do app:

```
model
├── config/AppConfig.kt              environments + flags em memória
├── entity/config/Environments.kt    DTO do POST /v1/boot
├── entity/scrapy/FlagsRequest.kt    body do POST /v1/flags
├── remote/client/ScrapyClient.kt    Retrofit próprio da Scrapy
├── remote/service/ScrapyService.kt  POST v1/boot e POST v1/flags
└── usecase/config/
    ├── Boot.kt                      splash, antes do login
    └── LoadFlags.kt                 depois do login, com attrs do usuário
```

`Boot.execute()` chama `ScrapyClient.boot()`, grava em `AppConfig` e inicializa o `AdmCoreClient` (`ms-adm-core-url` / `ms-adm-core-api-key`) e o `InventoryClient` (`ms-inventory-url` / `ms-inventory-api-key`). `LoadFlags.execute(selfUser)` chama `ScrapyClient.flags(attrs)`.

## Conteúdo (markdown) no app

O `/v1/flags` também traz o conteúdo cadastrado na aba Conteúdo do admin. O app separa pelo prefixo da chave:

- `category_*` → documentos da Central de Ajuda. 1ª linha = título, 2ª = subtítulo, resto = corpo em markdown. Lidos de `AppConfig.flags` por `model/usecase/helpCentral/GetHelpDocuments.kt` (`model/entity/helpCentral/HelpDocument.kt`), sem chamada nova. Spec: [../07-especificacoes/central-de-ajuda.md](../07-especificacoes/central-de-ajuda.md).
- `label_*` → ainda não usados.

Como as flags só são buscadas na entrada do app (restauração de sessão ou login), conteúdo novo aparece depois de reabrir o app. Ainda não há cache por `ETag`/`If-None-Match`.

## Propósito

A API do scrapy faz com que o app possa ser atualizado sem a necessidade de uma nova versão do app, além de facilitar comportamento dinâmico com diferentes segmentações, feature flags, etc. Dessa forma, poderemos mudar o comportamento do app sem a necessidade de uma nova versão, ou fazer build, etc.

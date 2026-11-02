# zera-android-app

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF.svg)](https://kotlinlang.org/)
[![Android](https://img.shields.io/badge/Android-minSdk%2028-3DDC84.svg)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Aplicativo Android do **Zera**, sistema de gestão e controle de lixo eletrônico corporativo,
desenvolvido inicialmente para o Instituto J&F / JBS. É o ponto de entrada operacional do sistema:
funcionários e gestores cadastram, classificam e acompanham o ciclo de vida dos equipamentos
eletrônicos, do registro inicial até a destinação final. Fala com os serviços
[`ms-administrative-core`](https://github.com/zera-interdisciplinar/ms-administrative-core)
(usuários, organizações e autenticação), [`ms-inventory`](https://github.com/zera-interdisciplinar/ms-inventory)
(itens, descarte, dashboard) e `ms-artificial-intelligence-core` (assistente e relatórios), e lê a
configuração de boot e as feature flags do [`scrapy`](https://github.com/zera-interdisciplinar/scrapy).

## Sumário

- [Domínio](#domínio)
- [Arquitetura](#arquitetura)
- [Stack](#stack)
- [Executando localmente](#executando-localmente)
- [Configuração](#configuração)
- [Integrações](#integrações)
- [Documentação](#documentação)
- [Testes](#testes)
- [Deploy](#deploy)
- [Links úteis](#links-úteis)

## Domínio

O Zera gerencia o ciclo de vida de equipamentos eletrônicos **por unidade** de uma organização. O
app tem dois perfis, definidos pela claim `role` do access token:

- **Funcionário (`EMPLOYEE`)** — cadastra e edita itens e modelos, registra descarte, acompanha
  manutenção e avaliação.
- **Gestor (`MANAGER`)** — o mesmo, mais categorias, regras, aprovações, exclusões, transferência de
  unidade, cancelamento de descarte, convites e equipe.

Não há perfil Administrador na v1. Fluxos principais:

1. **Onboarding e login** — splash (`POST /v1/boot` no scrapy) → boas-vindas → login ou resgate de
   convite → `POST /v1/flags` → home conforme o perfil.
2. **Home** — ocupação do estoque e itens recentes; o gestor também vê indicadores e equipe, e o
   funcionário vê as pendências na Central de Trabalho.
3. **Catálogo de itens** — listagem, detalhe, aprovação/reprovação e edição. O item nasce sempre
   como rascunho; a foto é opcional.
4. **Scan** — busca do item pelo código de barras (câmera + ML Kit). O `barcode` é diferente do
   `displayCode`, o código de 6 dígitos mostrado na interface.
5. **Descarte** — registro de um descarte **já realizado** (destino, local e data), com mapa de
   recicladoras próximas. Não há agendamento.

Regras de negócio, máquina de estados do item e o que está fora do escopo da v1:
[`docs/00-contexto-geral.md`](docs/00-contexto-geral.md) e [`docs/05-regras-de-negocio/`](docs/05-regras-de-negocio/).

## Arquitetura

Módulo Android único (`:app`), em camadas inspiradas em MVVM, com um Design System próprio. Não há
camada de `Repository` nem injeção de dependência: o `usecase` fala direto com os clients Retrofit.

```
app/src/main/java/com/zera/android/
├── model/
│   ├── config/         AppConfig — envs e flags do scrapy, em memória
│   ├── dto/            DTOs de rede (@Serializable), por domínio
│   ├── entity/         Modelos de domínio e de configuração
│   ├── local/          SqliteManager — sessão local (zera.db)
│   ├── remote/         Clients e interfaces Retrofit (AdmCoreClient, InventoryClient, ScrapyClient, AiClient)
│   └── usecase/        Acesso a dados e regra, por domínio
├── viewmodel/          Um ViewModel por tela ou feature, espelhando view/screens
└── view/
    ├── screens/        Telas Compose (auth, preferences, profile, main/<fluxo>)
    ├── components/     Design System reutilizável
    ├── navigation/     Rotas e ZeraNavigator (navegação centralizada)
    ├── theme/          Tokens: cores, tipografia, espaçamento, raio
    └── transition/     Transições entre telas
```

A `View` só renderiza o `State` e encaminha eventos; quem decide regra e navegação é o
`ViewModel`. Detalhes e decisões em [`docs/01-arquitetura.md`](docs/01-arquitetura.md) e
[`docs/08-decisoes-arquiteturais/`](docs/08-decisoes-arquiteturais/).

## Stack

| | |
|---|---|
| Linguagem | **Kotlin** 2.0 |
| UI | Jetpack Compose (Material 3), com Design System próprio |
| Navegação | Navigation Compose, centralizada em `ZeraNavigator` |
| Rede | Retrofit + OkHttp + kotlinx.serialization |
| Imagens | Coil |
| Mapa e localização | Google Maps Compose + Play Services Location |
| Scan de código de barras | CameraX + ML Kit |
| Push | Firebase Cloud Messaging |
| Build | Gradle (via `./gradlew`) + Android Gradle Plugin 9, `compileSdk`/`targetSdk` 36, `minSdk` 28 |

## Executando localmente

Pré-requisitos: JDK 17 (o mesmo usado pelo CI) e o Android SDK (Android Studio ou linha de comando).

```bash
cp local.properties.example local.properties   # e preencha os valores — veja "Configuração"
./gradlew assembleDebug                        # APK em app/build/outputs/apk/debug/
```

Para rodar num emulador ou aparelho, abra o projeto no Android Studio e execute a configuração
`app`. O `local.properties` está no `.gitignore` — nunca o versione.

## Configuração

O app lê só três valores de build, todos em `local.properties`. Todo o resto (URLs e chaves do
admin-core, do inventory e do AI core) chega em runtime, na resposta do `POST /v1/boot` do scrapy —
trocar de ambiente é trocar a chave do scrapy, não recompilar com outras URLs.

| Propriedade | Efeito |
|---|---|
| `scrapy.api.url` | URL do scrapy: o prefixo do gateway sem `/v1` (ex.: `http://<gateway>/qa/scrapy`) ou a URL direta do serviço, com `/v1` |
| `scrapy.api.key` | API key do scrapy (header `apikey`), presa a um env (`qa` ou `prod`) |
| `maps.api.key` | Chave do Google Maps. Opcional: sem ela o app compila e roda, só os tiles do mapa não carregam |

As notificações push (Firebase Cloud Messaging) são configuradas por `app/google-services.json`, versionado no repositório.

## Integrações

| Serviço | Para quê | Autenticação |
|---|---|---|
| `scrapy` | Boot (URLs e chaves dos demais serviços) e feature flags | Header `apikey` |
| `ms-administrative-core` | Login, refresh, convites, usuários, alertas, recicladoras | `apikey` + `Authorization: Bearer` (JWT); o `TokenRefreshAuthenticator` renova o token num `401` |
| `ms-inventory` | Itens, modelos, categorias, descartes, dashboard | `apikey` + JWT; a unidade vai no header `X-Unit-Id` |
| `ms-artificial-intelligence-core` | Chat e relatórios em PDF | `apikey` + JWT |

Contratos e como o app consome cada um: [`docs/06-contratos-api.md`](docs/06-contratos-api.md).
Fotos de item e avatar podem ser enviadas ao Supabase Storage quando o boot informa `supabase-url`
e os buckets; sem isso o recurso fica indisponível.

## Documentação

Toda a documentação técnica — arquitetura, padrões e convenções, catálogo de componentes, modelo de
dados, contratos de API, regras de negócio e specs de feature — vive em [`docs/`](docs/), com
[`docs/INDEX.md`](docs/INDEX.md) como ponto de entrada. Antes de contribuir com código novo, comece
por lá; ao concluir uma feature, indique quais docs mudam.

## Testes

```bash
./gradlew testDebugUnitTest    # testes unitários (JUnit 4)
```

Os testes ficam em `app/src/test/` (ViewModels, navegação e mapeamento de erros) e
`app/src/androidTest/` (instrumentados, exigem emulador ou aparelho).

## Deploy

O app não vai para a Play Store: o workflow `release-apk.yml` roda ao **publicar uma GitHub
Release** e anexa dois APKs de debug à release, `zera-<tag>-debug-QA.apk` e
`zera-<tag>-debug-PROD.apk`. Os dois diferem só na API key do scrapy
(`SCRAPY_API_KEY_QA` / `SCRAPY_API_KEY_PROD`), que o workflow grava no `local.properties` a partir dos
secrets do repositório, junto com `SCRAPY_API_URL` e `MAPS_API_KEY`.

## Links úteis

- [Figma do projeto](https://www.figma.com/design/UgR9qvEmfkTsEUAdIxLR2g/Zera-app?node-id=0-1&p=f&t=Bcj64C5VjBm7clvd-0)
- [Engenharia do projeto (Confluence)](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Engenharia/pages/917676/Escopo+Geral+v2)

## Licença

[MIT](LICENSE)

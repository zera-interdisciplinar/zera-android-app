# 09 — Stack e Dependências

## Linguagem e toolchain

- **Kotlin** 2.0.21, com o plugin `kotlin-compose` (compilador do Compose) e `kotlin-serialization`.
- **Android Gradle Plugin (AGP)** 9.0.1.
- `compileSdk`/`targetSdk` 36, `minSdk` 28.
- `sourceCompatibility`/`targetCompatibility`: Java 11.
- Um único módulo Gradle: `:app` (namespace/`applicationId` `com.zera.android`).

## UI

- **Jetpack Compose** via `androidx.compose:compose-bom:2025.09.00` (BOM — as versões individuais das libs de Compose seguem o que o BOM fixa).
- **Material 3** (`androidx.compose.material3:material3`) como sistema de componentes base, customizado pelo Design System do app (`view/theme`, `view/components` — ver [03-catalogo-componentes.md](03-catalogo-componentes.md)).
- **Navigation Compose** (`androidx.navigation:navigation-compose:2.9.8`), usado com rotas tipadas (`@Serializable`) — ver a seção "Navegação" em [01-arquitetura.md](01-arquitetura.md).
- `androidx-lifecycle-viewmodel-compose` / `androidx-lifecycle-runtime-ktx` (2.9.3) — integração do `ViewModel` com Compose (`viewModel()`, `viewModelScope`).
- `androidx-activity-compose` (1.13.0) — `ComponentActivity.setContent`.
- Fonte customizada **Inter** (`res/font/`), aplicada via `Type.kt` sobre o type scale padrão do Material 3.

## Networking e serialização

- **Retrofit** 3.0.0 + **OkHttp** 5.2.1 (com `okhttp-logging-interceptor`) para as chamadas HTTP.
- **kotlinx.serialization** 1.11.0 como formato de serialização, integrado ao Retrofit via `retrofit2-kotlinx-serialization-converter` 1.0.0 (em vez de Gson/Moshi).

Ver configuração completa na seção "Configuração base (ApiClient)" de [06-contratos-api.md](06-contratos-api.md).

## Persistência local

- Apenas `android.content.SharedPreferences` (via `androidx.core.content.edit` da `androidx-core-ktx`), encapsulado em `SharedPreferencesManager`. Não há Room nem outro banco embarcado no projeto hoje — ver a seção "Persistência local (SharedPreferences)" em [04-modelo-de-dados.md](04-modelo-de-dados.md).

## Firebase (configurado, não utilizado)

O projeto tem o plugin `com.google.gms.google-services` (versão 4.5.0) aplicado, um `google-services.json` versionado em `app/`, e a BOM `com.google.firebase:firebase-bom:34.18.0` na lista de dependências — mas **nenhuma biblioteca Firebase concreta é declarada** (nem `firebase-auth`, nem `firebase-messaging`, nem `firebase-analytics`, etc.) e **nenhum código do app referencia a API do Firebase**. Ou seja, o SDK está preparado no build mas nenhum produto Firebase está de fato integrado. Antes de remover essa configuração ou de adicionar um produto Firebase (ex.: push notifications para os alertas de lote crítico), vale confirmar a intenção original e registrar a decisão em [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/).

## Testes

- **JUnit 4** (`junit:junit:4.13.2`) para testes locais (`app/src/test`).
- **AndroidX Test** (`androidx-junit` 1.3.0, `androidx-espresso-core` 3.7.0) e **Compose UI Test** (`ui-test-junit4`, `ui-test-manifest`) para testes instrumentados (`app/src/androidTest`).
- Hoje só existem os testes-padrão gerados pelo Android Studio na criação do projeto (`ExampleUnitTest`, `ExampleInstrumentedTest`) — nenhum teste específico do domínio do Zera foi escrito ainda.

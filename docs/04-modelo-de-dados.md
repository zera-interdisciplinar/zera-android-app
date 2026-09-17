# 04 — Modelo de Dados

## DTOs de autenticação

Pacote `model/entity/auth/`, `@Serializable` (kotlinx.serialization).

- **`SingInRequestDTO(email: String, password: String)`** — corpo do `POST auth/login`. Também é reaproveitado (hoje, sem campos suficientes) pelo `POST invitations/redeem` — ver ressalva na seção "Autenticação (AuthService)" de [06-contratos-api.md](06-contratos-api.md).
- **`SingInResponseDTO(userId, accessToken, refreshToken, tokenType, expiresIn: Long)`** — resposta do login; campos `var` (mutáveis), sem valor padrão.

> Nota de nomenclatura: a grafia "Sing" (em vez de "Sign") é a mesma usada nas classes de código — ver "Inconsistências conhecidas" em [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md).

## DTOs de usuário

Pacote `model/entity/user/`.

- **`SelfUserResponseDTO(userId, name, email, role, status, unitId, createdAt, updatedAt, managerId: String)`** — retornado por `GET users/{userId}`. `role` é uma `String` livre (valores observados: `"MANAGER"`, `"EMPLOYEE"`), não um enum Kotlin — a decisão de rota (`SingInViewModel.signIn()`) faz `when (selfUser.role) { "EMPLOYEE" -> ...; "MANAGER" -> ...; else -> erro }`. Não há um terceiro valor para Administrador (coerente com [00-contexto-geral.md](00-contexto-geral.md), que registra que esse perfil não tem fluxo no app).

## Persistência local (SharedPreferences)

Único mecanismo de persistência local hoje é `SharedPreferencesManager` (`model/local/SharedPreferencesManager.kt`), um `object` sobre um único arquivo de preferências (`"zera_prefs"`):

- `saveAccessToken` / `getAccessToken`
- `saveRefreshToken` / `getRefreshToken`

Precisa ser inicializado explicitamente (`SharedPreferencesManager.init(context)`, hoje chamado em `MainActivity.onCreate`) antes de qualquer leitura/escrita — chamar antes disso lança `UninitializedPropertyAccessException`. Não há cache local de dados de domínio (perfil do usuário, inventário, etc.) — apenas os tokens de sessão. Não há banco de dados local (Room ou similar) no projeto.

## Mapeamentos entre camadas

Não existe uma camada de mapeamento DTO → modelo de domínio (`toDomain()`/`toUiModel()`): os `ViewModels` hoje ou (a) usam o DTO apenas de forma transiente, sem guardá-lo no `State` (ex.: `SingInViewModel` recebe `SelfUserResponseDTO` só para decidir a rota de navegação, sem persistir seus campos), ou (b) usam dados totalmente mockados no `State`, sem nenhum DTO envolvido ainda (ex.: `ManagerHomeState`, preenchido com valores fixos até a integração com o backend — `loadDashboard()` é um `TODO`). Ao integrar o dashboard do Gestor à API, avalie se cabe introduzir um modelo de UI dedicado em vez de reaproveitar o DTO de rede diretamente no `State`.

## Modelo de dados esperado (backlog, ainda não implementado)

Resumo leve do modelo de dados previsto pelo escopo de produto — sem detalhar schema aqui, já que ainda não há código correspondente:

- Hierarquia **Categoria → Modelo → Produto**, com o código de barras do produto físico codificando `id_produto-id_modelo-id_categoria`.
- Status de triagem por produto: reutilizável, aproveitável (peças/upgrade) ou descartável.
- Metadados de manutenção por produto: vida útil esperada, histórico de manutenções, desgaste estimado, frequência de uso.
- Registro de tempo de armazenamento de itens descartáveis, para os alertas de lote crítico.

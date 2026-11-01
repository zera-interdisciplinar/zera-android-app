# contexto

As telas de time do gestor (card "Funcionários" na home e `EmployeesScreen`) vêm do `ms-administrative-core` via `AdmCoreClient`. Contrato: [contrato-team-management.md](contrato-team-management.md). **Não existe `POST /users`.**

Indicadores (`Route.Indexes`) **não** entram nesta integração: continuam numa única chamada de inventory (`GetIndicators` → `GET /api/v1/dashboard/indicators`).

## Integração no app

O client é o Retrofit do adm-core (`AdmCoreClient` / `UsersService` + `InvitationService`). Headers: `apiKey` e `Authorization: Bearer`. **Não** se envia `X-Unit-Id` nestas rotas.

`managerId` em query e body é o `userId` gravado na sessão (`SqliteManager.getUserId()`).

```
model
├── dto/user/
│   ├── SelfUserResponseDTO.kt          item de GET /users (mesmo shape do self)
│   └── ManagerEmployeeCountDTO.kt      linha de count-by-manager
├── dto/invitation/
│   ├── PendingInvitationDTO.kt         GET /invitations/pending
│   ├── CreateInvitationRequestDTO.kt   POST /invitations
│   ├── CreateInvitationResponseDTO.kt
│   └── RedeemRequestDTO.kt / RedeemResponseDTO.kt  cadastro do funcionário (já existia)
├── entity/user/UserStatus.kt
├── entity/team/
│   ├── TeamEmployee.kt
│   └── PendingInvite.kt
├── remote/client/AdmCoreClient.kt      usersService + invitationService
├── remote/service/UsersService.kt      GET users, GET users/count-by-manager
├── remote/service/InvitationService.kt GET invitations/pending, POST invitations, POST redeem
└── usecase/team/
    ├── CountActiveEmployees.kt
    ├── ListTeamEmployees.kt
    ├── ListPendingInvitations.kt
    └── CreateInvitation.kt
```

ViewModels:

- `ManagerHomeViewModel` — `ManagerHomeScreen` (`Route.ManagerHome`): preenche o atalho "Funcionários"
- `EmployeesViewModel` — `EmployeesScreen` (`Route.Employees`): header, pendentes, lista
- `EmployeeRegisterViewModel` — `EmployeeRegisterScreen` (`Route.EmployeeRegister`): criar convite
- `EmployeeDetailsViewModel` — `EmployeeDetailsScreen` (`Route.EmployeeDetails`): dados do operário

`InviteCard`, `EmployeeList` e `Avatar` já existiam.

## Endpoints consumidos

| Rota | Use case | Tela |
|---|---|---|
| `GET /api/v1/users/count-by-manager` | `CountActiveEmployees` | home (card) e header "Equipe ativa" |
| `GET /api/v1/users?role=EMPLOYEE&status=ACTIVE&managerId=` | `ListTeamEmployees` | lista de colaboradores |
| `GET /api/v1/invitations/pending?managerId=` | `ListPendingInvitations` | `InviteCard` + linhas "Pendente" |
| `POST /api/v1/invitations` | `CreateInvitation` | botão "Novo" → `EmployeeRegisterScreen` |
| `GET /api/v1/users/{id}` | `GetSelfUser` | `EmployeeDetailsScreen` |
| `GET /api/v1/telephone/user?userId=` | `GetTelephone` | `EmployeeDetailsScreen` (`404` = sem telefone) |
| `POST /api/v1/invitations/redeem` | `InvitationUseCase` | `SignUpScreen` (não é fluxo do gestor) |

O microserviço em si não valida `apiKey`; o interceptor do app ainda manda o header por causa do gateway. Paths Retrofit **não** prefixam `api/v1` — a base URL do boot já inclui esse sufixo (`users`, `invitations/pending`, …).

## O que foi feito na home e por quê

- **Use case `CountActiveEmployees`.** `GET /users/count-by-manager` devolve um array; o app pega a linha do `managerId` logado. Gestor ausente no array → `0`. A rota **já conta só** `EMPLOYEE` `ACTIVE`, então serve para o card "ativos" sem segundo filtro.
- **Por que não `GET /users` para o número.** A lista não tem `totalElements`; `count-by-manager` não depende de `page`/`size`.
- **Mesma fonte que o header da tela de colaboradores.** Home e "Equipe ativa" não podem divergir.
- **Assíncrono.** `GetSelfUser`, `GetManagerHome` (inventory) e o count disparam em coroutines paralelas. Cada uma atualiza só a fatia do state. Falha no count não derruba ocupação/itens.

## O que foi feito em colaboradores e por quê

- **Três canais em paralelo** no `EmployeesViewModel`: count, lista de users, convites pendentes. Cada retorno publica a sua parte; a lista composta só junta o que já chegou.
- **Lista de ativos.** `GET /users` com `role=EMPLOYEE`, `status=ACTIVE`, `managerId`, `size=50`. Array simples. Cargo na UI é mapeamento (`EMPLOYEE` → "Operador"); o back não manda rótulo.
- **Pendentes.** `GET /invitations/pending` (não `GET /invitations?status=`). O back já filtra `PENDING` com `expiresAt` no futuro. Payload só `code`, `inviteeName`, `expiresAt`. Vários itens → vários `InviteCard`.
- **"Expira em Nh".** `expiresAt` (ISO `LocalDateTime` sem timezone) menos `now()`, em horas inteiras, mínimo 0.
- **Composição.** `isPending` **não** vem de status de usuário. Convites viram linhas "Pendente" (`id = invite-{code}`) **antes** dos funcionários ativos. Não existe user `INACTIVE` esperando redeem.
- **Criar convite.** "Novo +" abre `EmployeeRegisterScreen`. A tela pede nome, e-mail e unidade, mas o contrato só recebe `inviteeName`: os outros campos ficam como TODO até o back aceitá-los, e o dropdown de unidade fica vazio (não há rota de listagem de unidades). `managerId` sai da sessão. TTL 7 dias é constante do back. Sucesso volta para a lista, que recarrega os pendentes no `onResume`. `400`/`404` viram mensagem no form.
- **Copiar código.** `InviteCard` usa a clipboard do Compose.
- **Detalhe do operário.** Clique num ativo abre `EmployeeDetailsScreen` (linha pendente não navega, não há usuário). `GET /users/{id}` preenche nome, e-mail, cargo e foto; telefone vem de `GET /telephone/user` e, se falhar, a tela mostra o resto com aviso. "Excluir perfil" é TODO: não há rota de exclusão/desativação de usuário.

## Testes

Mapeamento puro (sem Retrofit) em:

- `CountActiveEmployeesTest` — escolhe a linha do gestor; ausente → `0`
- `EmployeesMappingTest` — horas até expirar, ordem pendente/ativo, card de convite
- `PendingInvitationDTOTest` / `ManagerEmployeeCountDTOTest` — serialização

## Fora deste recorte

- Detalhe do colaborador, chatbot, alertas da home.
- `LocalClipboardManager` na tela de colaboradores está deprecated (funciona; o Compose novo prefere `LocalClipboard`).
- `EmployeeList` (`LazyColumn`) continua dentro da `Column` rolável do `ManagerScaffold`.

## O que fazer em caso de dúvida de contrato

Pare imediatamente de desenvolver e informe de forma estruturada o contexto, o problema e o que te afeta. Não invente payload nem campo extra.

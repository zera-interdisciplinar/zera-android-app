# contexto

A tela de perfil (`ProfileScreen`, compartilhada por gestor e funcionário) já faz **leitura** do usuário: `ProfileViewModel` + `GetProfile` → `GET /api/v1/users/{id}` (nome, e-mail, cargo, `imageUrl`).

Próximo recorte: **editar nome, e-mail e telefone** conforme `contrato-team-management.md` §4. Foto fica **fora**. Cargo continua só leitura.

Fonte de verdade da API: [contrato-team-management.md](contrato-team-management.md). **Não inventar payload nem rota.**

## o que já está feito (não refazer)

- `ProfileViewModel` + `GetProfile` + `ProfileUser` + `imageUrl` no DTO
- Loading / erro na tela
- Lápis de Nome / E-mail / Telefone abre `ZeraInputPopup`, mas `onConfirm` **não persiste**

## fora de escopo

- `PATCH /users/{id}/image` e upload
- Configurações
- Edição de cargo / empresa
- `DELETE /telephone/{id}` (não há “apagar telefone” na UI desta tela)

## plano de versão

nunca realize commits nem push, se achar que deve commitar, me consulte.

## casos de dúvida

se tiver alguma dúvida, bloqueie a tarefa, pare de fazer tudo e me consulte (contexto, detalhe, problema).

---

## contrato fechado (substitui a hipótese de PATCH único)

**Não há** `PATCH /users/{id}` com body único. Cada campo é sub-recurso. Escritas de sucesso: **`204`**, exceto criar telefone (`201`).

| Campo | Ler | Escrever | Auth escrita |
|---|---|---|---|
| Nome | `name` em `GET /users/{id}` | `PATCH /users/{id}/rename` `{ "name": "..." }` | **só `MANAGER`** |
| E-mail | `email` em `GET /users/{id}` | `PATCH /users/{id}/email` `{ "email": "..." }` | **`SELF_OR_MANAGER`** |
| Telefone | `GET /telephone/user?userId=` — **não** vem no `UserOutput` | sem telefone: `POST /telephone/user` `{ userId, number }` (`201`); com `telephoneId`: `PATCH /telephone/{telephoneId}/number` `{ "number": "..." }` (`204`) | **só `MANAGER`** |
| Imagem | `imageUrl` | fora deste recorte | — |

`GET /telephone/user?userId=` — qualquer autenticado. Sem telefone: **`404`** (não é erro de tela; campo vazio). Número: 10 ou 11 dígitos após normalização; o app pode enviar máscara.

E-mail duplicado: **`400`** (`Email already in use`), não `409`. Mesmo e-mail atual: `204` no-op. Usuário inexistente no rename: `400`. Telefone inválido: `400`. Já tem telefone e o app mandar `POST`: `409` — aí usar PATCH.

`id` / `userId` nesta tela = usuário **logado** (`SharedPreferencesManager.getUserId()`).

### permissão na UI (regra desta spec)

A tela serve gestor e funcionário, mas o back **não** deixa o funcionário gravar nome nem telefone.

- Papel vem do `role` já carregado no perfil (`MANAGER` / `EMPLOYEE`).
- **Gestor:** lápis em Nome, E-mail e Telefone.
- **Funcionário:** lápis **só em E-mail**. Nome e Telefone só leitura (sem `onEditClick`).
- Não disparar PATCH/POST que o papel não pode; não “tentar e mostrar 403” nesses dois campos.

Se essa regra de UI estiver errada (ex.: funcionário deveria editar o próprio nome), **parar e consultar** — o contrato hoje proíbe.

---

## plano de implementação (código só após autorização desta spec)

Padrão: `ItemDetailsViewModel` + `EditableFieldRow.onConfirm`.

1. **DTOs**
   - `RenameUserRequestDTO` `{ name }`
   - `UpdateEmailRequestDTO` `{ email }`
   - `TelephoneResponseDTO` (GET/POST: `telephoneId`, `number`, demais campos do GET com default/`ignoreUnknownKeys`)
   - `CreateTelephoneRequestDTO` `{ userId, number }`
   - `UpdateTelephoneNumberRequestDTO` `{ number }`
   - **Não** colocar `phone` em `SelfUserResponseDTO`.

2. **Entity**
   - Estender `ProfileUser` (ou state da VM) com `telephoneId: String?` e `phone: String` (número). `404` no GET → `telephoneId = null`, `phone = ""`.

3. **Services** (`ApiClient`, mesma base URL `api/v1`)
   - `UsersService` / `SelfUserService`: `PATCH users/{id}/rename`, `PATCH users/{id}/email` → `204` (`Response<Unit>` ou equivalente já usado no projeto).
   - `TelephoneService` novo: `GET telephone/user?userId=`, `POST telephone/user`, `PATCH telephone/{telephoneId}/number`.

4. **Use cases**
   - `GetTelephone` — `404` → ausência, não exception de tela.
   - `RenameUser`, `UpdateUserEmail`, `CreateTelephone`, `UpdateTelephoneNumber`.
   - `GetProfile` passa a também buscar telefone (ou a VM dispara os dois em paralelo, como a home). Falha no GET user continua erro da tela; falha de telefone que não seja 404 vira mensagem, sem zerar nome/e-mail.

5. **ViewModel**
   - Guardar `userId` e `telephoneId`.
   - `onNameConfirm` / `onEmailConfirm` / `onPhoneConfirm`.
   - Validar: nome não em branco; e-mail com `@`; telefone 10–11 dígitos (contrato).
   - `isSaving`; um save por vez.
   - Sucesso `204`: atualizar o campo no state (e-mail/nome) **sem** exigir body. Telefone `201`: guardar `telephoneId` + número normalizado. Depois de PATCH de número, atualizar `phone` no state.
   - Erros: `400` e-mail em uso / número inválido / user not found; `403` permissão; `409` no POST de telefone → se aparecer, tratar como “já existe” e não inventar retry cego sem `telephoneId`.
   - Recarregar perfil inteiro só se for mais simples e barato; preferir atualizar o campo salvo.

6. **Tela**
   - Ligar `onConfirm` nos três campos.
   - `onEditClick` só se `canEditName` / `canEditEmail` / `canEditPhone` (e-mail sempre para self; nome/telefone só `MANAGER`).
   - Cargo sem lápis. Foto sem mudança.

7. **Testes**
   - Parse do GET telefone; `404` = vazio.
   - Escolha POST vs PATCH conforme `telephoneId`.
   - `canEdit*` por role.
   - Mensagens de `400` de e-mail.

### UX

Lápis → popup. Confirmar → API. Dismiss → nada. `validate` no popup impede PATCH inválido.

### o que não fazer

- PATCH único em `/users/{id}`.
- Colocar telefone no DTO de usuário.
- Implementar imagem neste recorte.
- Deixar funcionário chamar rename ou escrita de telefone.

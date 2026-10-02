# contexto

A tela de perfil (`ProfileScreen`, compartilhada por gestor e funcionário) lê o usuário (`GET /users/{id}`), o telefone (`GET /telephone/user`) e já persiste nome / e-mail / telefone conforme o contrato.

Este recorte: **foto de perfil**. Upload no **Supabase Storage** com config vinda do **Scrapy boot**; depois `PATCH /users/{id}/image` com a URL pública. Sem signed URL no back (atalho consciente: anon key no client, mesmo nível das outras envs do boot).

Fonte de verdade da API de usuário: [contrato-team-management.md](contrato-team-management.md). Config do Storage: [contrato-scrapy/contrato-scrapy-api.md](../contrato-scrapy/contrato-scrapy-api.md) (`POST /v1/boot`).

## o que já está feito (não refazer)

- Leitura de perfil + telefone; edição de nome / e-mail / telefone; lápis por papel (gestor: três campos; funcionário: só e-mail)

## fora de escopo

- Signed upload no MS Zera / `service_role`
- Configurações, cargo, empresa
- `DELETE /telephone/{id}`
- RLS / ponte JWT Zera → Supabase (fica no projeto Supabase, não neste app)

## plano de versão

nunca realize commits nem push, se achar que deve commitar, me consulte.

## casos de dúvida

se tiver alguma dúvida, bloqueie a tarefa, pare de fazer tudo e me consulte.

---

## foto — fluxo

1. Boot já carregou envs (`AppConfig.environments`).
2. Usuário toca o avatar → picker de imagem do sistema.
3. App sobe o arquivo no Storage:  
   `POST {supabase-url}/storage/v1/object/{bucket}/{userId}/profile.{ext}`  
   headers `apikey` e `Authorization: Bearer` = **anon key** do boot; `x-upsert: true`.
4. URL pública:  
   `{supabase-url}/storage/v1/object/public/{bucket}/{userId}/profile.{ext}`
5. `PATCH /users/{id}/image` `{ "imageUrl": "<url pública>" }` (`SELF_OR_MANAGER`, `204`).
6. State `photoUrl` atualiza; `Avatar` carrega a URL (Coil).

`userId` = usuário logado. Gestor e funcionário editam a **própria** foto nesta tela.

Se `supabase-url`, `supabase-anon-key` ou `supabase-avatars-bucket` vierem vazios no boot: não sobe; mensagem na tela. Não inventar fallback em `local.properties`.

### chaves no `POST /v1/boot` (Scrapy, `boot_only`)

| Chave | Uso |
|---|---|
| `supabase-url` | origem do projeto, sem barra final obrigatória (app normaliza) |
| `supabase-anon-key` | chave **anon** (pública). Nunca `service_role` |
| `supabase-avatars-bucket` | nome do bucket (ex. `avatars`) |

Ausentes no JSON → string vazia no DTO (`ignoreUnknownKeys`). Quem opera o Scrapy precisa cadastrar as três entries.

### o que não fazer

- `service_role` no boot ou no APK
- Upload de telefone/org/unidade
- PATCH de imagem sem ter URL do Storage (exceto limpar avatar, fora deste recorte)

---

## plano de implementação

1. `Environments`: os três campos, default `""`. Teste de parse.
2. `UpdateImageRequestDTO` `{ imageUrl }` + `PATCH users/{id}/image` no `SelfUserService`.
3. Client OkHttp do Storage (não SDK Supabase): upload + montagem da URL pública (funções testáveis).
4. Use cases `UploadAvatar` e `UpdateUserImage`.
5. `ProfileViewModel.onPhotoPicked(bytes, mime)`: valida MIME (jpeg/png/webp) e tamanho (ex. 5 MB) → upload → PATCH → `photoUrl`.
6. `ProfileScreen`: `PickVisualMedia` (imagens), lê bytes via `ContentResolver`, chama a VM. Avatar clicável.
7. Coil no `Avatar` para `photoUrl`.
8. Testes: URL pública; parse do boot; MIME/tamanho.

## permissão (já fechada)

| Campo | Quem edita nesta tela |
|---|---|
| Nome, telefone | só `MANAGER` |
| E-mail, foto | self (gestor e funcionário) |

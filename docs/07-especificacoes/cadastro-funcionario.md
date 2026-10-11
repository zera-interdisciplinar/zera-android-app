# Spec — Cadastro de Funcionário (via convite)

## Metadados

- **Feature:** Cadastro via código de convite
- **Perfil(is) envolvido(s):** Funcionário
- **Status:** implementado (`POST /invitations/redeem` + login em seguida)
- **Última atualização:** 2026-10-11

## Contexto

Não existe autorregistro nem `POST /users`. Conta nasce só em `POST /invitations/redeem` (público). O gestor emite o convite (`POST /invitations`). Papéis da v1: só `MANAGER` e `EMPLOYEE`.

## User story

Como Funcionário convidado, quero me cadastrar informando meu nome, email, senha e o código de convite recebido, para obter acesso ao app com minha própria conta.

## Cenários (Given/When/Then)

> `SignUpViewModel.signUp()` chama `InvitationUseCase` (`POST invitations/redeem`) e depois `SingIn`.

### Cenário: Cadastro bem-sucedido

- **Given** o usuário está na tela de cadastro (`Route.SignUp`) com um código de convite válido em mãos
- **When** preenche nome, email, senha e os 6 dígitos do código de convite, e toca em "Entrar"
- **Then** a conta é criada, o usuário é autenticado automaticamente e redirecionado para a área de Funcionário

### Cenário: Código de convite inválido ou expirado

- **Given** o código de convite informado não existe ou já expirou
- **When** o usuário toca em "Entrar"
- **Then** uma mensagem de erro é exibida e o cadastro não é concluído

### Cenário: Campos obrigatórios incompletos

- **Given** nome, email, senha estão vazios, ou o código de convite tem menos de 6 dígitos
- **When** o usuário toca em "Entrar"
- **Then** o cadastro não é submetido (validação no cliente, no mesmo padrão usado em [login.md](login.md))

## Critérios de aceite

- [x] `signUp()` → redeem + login
- [x] Contrato: `RedeemRequestDTO(code, name, email, rawPassword)`
- [x] Validação client-side dos obrigatórios
- [x] Redirecionamento pela `role` devolvida / login seguinte

## Edge cases considerados

- Código de convite incompleto — `ZeraTokenInput` já limita a 6 dígitos numéricos, mas `onFilled` não está conectado a nenhuma ação ainda.
- Convite já utilizado por outra conta — comportamento a definir com o backend.
- Convite já `USED` não volta a `PENDING`. Gestor também entra por convite se o convite tiver esse papel.

## Fora de escopo desta spec

- Emissão do código (já coberta em gestão de equipe / convites do gestor).
- Recuperação de senha.

## Referências

- Regras de negócio: `../05-regras-de-negocio/` (ainda não escritas para o domínio de autenticação)
- Contratos de API: [../06-contratos-api.md](../06-contratos-api.md), seção "Autenticação (AuthService)"
- Padrões de `ViewModel`/`State`: [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenções de ViewModel e State"

# Spec — Cadastro de Funcionário (via convite)

## Metadados

- **Feature:** Cadastro via código de convite
- **Perfil(is) envolvido(s):** Funcionário
- **Status:** em progresso (tela pronta, `ViewModel` sem integração com back-end)
- **Última atualização:** 2026-09-17

## Contexto

Não existe autorregistro livre: um novo Funcionário só cria sua conta a partir de um código de convite gerado por alguém com permissão para isso (Gestor ou Administrador, conforme o escopo de produto — a emissão do convite em si não tem tela no app ainda). A tela de cadastro (`SignUpScreen`) e o `SignUpViewModel` já existem com a forma final de dados esperada, mas a ação de submeter (`signUp()`) ainda não está implementada.

## User story

Como Funcionário convidado, quero me cadastrar informando meu nome, email, senha e o código de convite recebido, para obter acesso ao app com minha própria conta.

## Cenários (Given/When/Then)

> Os cenários abaixo descrevem o comportamento **planejado**; hoje `signUp()` não faz nenhuma chamada de API nem navega (ver Status acima).

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

- [ ] `SignUpViewModel.signUp()` implementado, seguindo o mesmo roteiro de `SingInViewModel.signIn()` (`isLoading`/`errorMessage`, `viewModelScope.launch`, tratamento de exceção)
- [ ] Contrato de API de cadastro definido e alinhado com os campos coletados pela tela (nome, email, senha, código de convite) — ver ressalva em [../06-contratos-api.md](../06-contratos-api.md), seção "Autenticação (AuthService)"
- [ ] Validação client-side dos campos obrigatórios antes de chamar a API
- [ ] Redirecionamento para a área correta após cadastro bem-sucedido

## Edge cases considerados

- Código de convite incompleto — `ZeraTokenInput` já limita a 6 dígitos numéricos, mas `onFilled` não está conectado a nenhuma ação ainda.
- Convite já utilizado por outra conta — comportamento a definir com o backend.
- Cadastro de Gestor via convite — o escopo de produto não deixa claro se Gestores também entram pelo mesmo fluxo de convite ou por outro processo; confirmar antes de implementar.

## Fora de escopo desta spec

- Emissão do código de convite (tela para Gestor/Administrador gerar convites) — ainda não tem spec própria.
- Recuperação de senha.

## Referências

- Regras de negócio: [../05-regras-de-negocio/](../05-regras-de-negocio/) (ainda não escritas para o domínio de autenticação)
- Contratos de API: [../06-contratos-api.md](../06-contratos-api.md), seção "Autenticação (AuthService)"
- Padrões de `ViewModel`/`State`: [../02-padroes-e-convencoes.md](../02-padroes-e-convencoes.md), seção "Convenções de ViewModel e State"

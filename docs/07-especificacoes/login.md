# Spec — Login

## Metadados

- **Feature:** Login
- **Perfil(is) envolvido(s):** Funcionário, Gestor
- **Status:** implementado
- **Última atualização:** 2026-09-17

## Contexto

Ponto de entrada autenticado do app. Todo usuário (Funcionário ou Gestor) precisa fazer login antes de acessar qualquer área interna; o próprio login descobre o perfil do usuário e decide para onde redirecioná-lo.

## User story

Como usuário cadastrado (Funcionário ou Gestor), quero entrar com meu email e senha, para acessar a área correspondente ao meu perfil.

## Cenários (Given/When/Then)

### Cenário: Login bem-sucedido como Funcionário

- **Given** o usuário está na tela de login (`Route.SignIn`) com uma conta cadastrada como `EMPLOYEE`
- **When** preenche email e senha corretos e toca em "Entrar"
- **Then** os tokens de acesso são salvos, e o usuário é redirecionado para `Route.EmployeeHome`, sem poder voltar para a tela de login

### Cenário: Login bem-sucedido como Gestor

- **Given** o usuário está na tela de login com uma conta cadastrada como `MANAGER`
- **When** preenche email e senha corretos e toca em "Entrar"
- **Then** os tokens de acesso são salvos, e o usuário é redirecionado para `Route.ManagerHome`, sem poder voltar para a tela de login

### Cenário: Campos obrigatórios vazios

- **Given** o campo de email ou de senha está vazio
- **When** o usuário toca em "Entrar"
- **Then** a mensagem "Email and password cannot be empty" é exibida e nenhuma chamada de API é feita

### Cenário: Credenciais inválidas ou erro de servidor

- **Given** o usuário informa credenciais incorretas, ou a API está indisponível
- **When** toca em "Entrar"
- **Then** a mensagem de erro (texto bruto da exceção) é exibida abaixo dos campos, e o usuário permanece na tela de login

### Cenário: Perfil desconhecido retornado pela API

- **Given** o login foi bem-sucedido, mas `GET users/{userId}` retorna um `role` diferente de `EMPLOYEE`/`MANAGER`
- **When** o app processa a resposta
- **Then** a mensagem "Invalid role" é exibida e o usuário é mantido na tela de login

## Critérios de aceite

- [x] Campos de email e senha são obrigatórios (validação no cliente antes de chamar a API)
- [x] Tokens de acesso e refresh são persistidos após login bem-sucedido
- [x] Redirecionamento correto por perfil (`EMPLOYEE` → `EmployeeHome`, `MANAGER` → `ManagerHome`)
- [x] Erro exibido em caso de falha (validação, credenciais, rede, perfil desconhecido)
- [x] Pilha de navegação anterior é removida ao redirecionar após login (`pushAndPop`)
- [ ] Mensagens de erro amigáveis (hoje é o texto bruto da exceção — ver "Tratamento de erros" em [06-contratos-api.md](../06-contratos-api.md))

## Edge cases considerados

- Campos vazios — bloqueado no cliente, sem chamada de API.
- Erro de rede/servidor — tratado genericamente (`catch (e: Exception)`), sem diferenciar tipo de erro.
- `role` inesperado — tratado, mas redireciona de volta para a própria tela de login em vez de, por exemplo, oferecer contato com suporte.
- Múltiplos toques em "Entrar" durante o carregamento — botão desabilitado (`enabled = !state.isLoading`) e texto muda para "Entrando...".

## Fora de escopo desta spec

- "Esqueci minha senha" — o link existe na UI (`CaptionText` em `SignInScreen`) mas sem ação implementada (`onClick = {}`).
- Refresh automático de token expirado — não implementado (ver "Tratamento de erros" em [06-contratos-api.md](../06-contratos-api.md)).
- Cadastro de novo usuário — ver [cadastro-funcionario.md](cadastro-funcionario.md).

## Referências

- Regras de negócio: [../05-regras-de-negocio/](../05-regras-de-negocio/) (ainda não escritas para o domínio de autenticação)
- Contratos de API: [../06-contratos-api.md](../06-contratos-api.md), seção "Autenticação (AuthService)"
- Arquitetura / navegação: [../01-arquitetura.md](../01-arquitetura.md), seção "Navegação"

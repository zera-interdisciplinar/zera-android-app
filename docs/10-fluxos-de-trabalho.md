# 10 — Fluxos de Trabalho por Tipo de Tarefa

Este documento expande a seção 2 do [INDEX.md](INDEX.md) num checklist por tipo de tarefa: o que checar no **código** antes de mexer, o que checar na **documentação**, e principalmente **quando parar e perguntar ao usuário** em vez de decidir sozinho. Vale tanto para uma pessoa quanto para uma IA trabalhando no projeto.

## Como usar

Cada tarefa abaixo tem três blocos:

- **Antes de começar** — o que ler/checar no código e nos docs antes de escrever qualquer linha.
- **Durante** — convenções e regras a seguir enquanto implementa.
- **Quando envolver o usuário** — sinais de que a decisão não é sua para tomar sozinho.

E termina com **Ao terminar**, lembrando quais docs sugerir atualizar (ver regra geral na seção 4 do [INDEX.md](INDEX.md)).

## 1. Criar uma nova tela

**Antes de começar**
- Docs: [00-contexto-geral.md](00-contexto-geral.md) (a que fluxo/perfil ela pertence), [01-arquitetura.md](01-arquitetura.md) (camadas e a regra de separação View/lógica), [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md) (nomenclatura e pasta), [03-catalogo-componentes.md](03-catalogo-componentes.md) (o que já existe pra montar a tela), e a spec em [07-especificacoes/](07-especificacoes/) — se não existir, é sinal de que a regra de negócio ainda não foi definida.
- Código: telas existentes do mesmo domínio (`view/screens/<domínio>/`) para replicar o padrão; se a tela precisa de uma rota nova, `view/navigation/Route.kt` e `ZeraNavHost.kt`.
- **Levantamento de componentes (obrigatório, faça antes de escrever a tela):** liste todo componente de UI "principal" que a tela vai precisar (botão, input, card, tag, lista, etc.) e confira cada um em [03-catalogo-componentes.md](03-catalogo-componentes.md). Isso é especialmente crítico quando o usuário fornece uma **imagem/mockup de referência** — não modele visualmente algo parecido "na mão" dentro da tela. **Se algum componente necessário ainda não existir no catálogo, pare antes de montar a tela e proponha ao usuário o fluxo de criação desse componente** (item 2 deste documento). Só continue montando a tela depois que os componentes que faltam existirem.

**Durante**
- `ViewModel` novo (ou reaproveitado) coordena dados e navegação; a `Screen` só renderiza `state` e encaminha eventos — ver "Separação entre View e lógica (regra estrita)" em [01-arquitetura.md](01-arquitetura.md).
- **Composição restrita a componentes existentes:** o corpo da tela só pode usar (a) componentes do Design System já catalogados em `03-catalogo-componentes.md` e (b) primitivas de layout genéricas do Compose usadas apenas para alinhar/conter esses componentes (`Box`, `Row`, `Column`, `Spacer`, `LazyColumn`, `PaddingValues`, etc.). É **proibido** usar diretamente um widget "cru" do Material/Compose que duplique o papel de um componente do catálogo (`Text`, `Button`, `TextField`, `Icon`, `Card` do Material direto na tela, por exemplo). Se o catálogo não cobre o caso, a solução é criar o componente que falta (ver item 2 abaixo) — nunca contornar com um widget genérico improvisado.
- **Zero medida livre:** é **proibido** usar valores soltos de cor, espaçamento ou raio de borda. Cor sempre via `ZeraColorFamily`/`.palette()` (ou um papel do `MaterialTheme.colorScheme` quando não há família de cor aplicável); espaçamento sempre via `Spacing.*`; raio de borda sempre via `Radius.*`. A única exceção tolerada é para `width`/`height` pontuais de um elemento específico do layout (ex.: a largura de um ícone maior que o padrão, a altura de um card) — e mesmo essa exceção deve virar uma `private val` nomeada no topo do arquivo, nunca um número solto inline (ver "Estilo de código Kotlin" em [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md)).

**Quando envolver o usuário**
- Falta um componente do catálogo para atender a uma imagem de referência (ou a qualquer necessidade visual da tela) — é o próprio gatilho do "pare e proponha o fluxo de criação" acima; nunca resolver sozinho com um substituto improvisado ou com medidas/cores livres "só para essa tela".
- A tela pertence a um domínio sem spec em `07-especificacoes/` nem regra em `05-regras-de-negocio/` — não invente a regra de negócio, pergunte antes de codar o comportamento.
- A tela implica uma rota/fluxo para o perfil Administrador — hoje o app não tem nenhum fluxo desse perfil ([00-contexto-geral.md](00-contexto-geral.md)); confirmar antes de introduzir o primeiro.
- A tela lida com dado sensível (token, dado pessoal) de um jeito não coberto pelos padrões já existentes.

**Ao terminar:** sugerir criar/atualizar a spec em `07-especificacoes/` e, se criou componente novo, o catálogo em `03-catalogo-componentes.md`.

## 2. Criar ou ajustar um componente do Design System

**Antes de começar**
- Docs: [03-catalogo-componentes.md](03-catalogo-componentes.md) (existe algo parecido?), [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md) (critério de prefixo `Zera`, pasta por família, tokens).

**Durante**
- **Recomendado, não proibido** (diferente da regra estrita de telas — ver item 1): prefira cor via `ZeraColorFamily.palette()` e espaçamento/raio via `Spacing`/`Radius`. Um componente de baixo nível às vezes precisa de uma medida própria que não faz sentido como token global (ex.: diâmetro de um círculo, largura de uma célula de OTP) — nesse caso, use um valor direto, mas sempre como `private val` nomeada no topo do arquivo, nunca um número solto inline (ver "Estilo de código Kotlin" em [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md)).
- `@Preview` cobrindo o caso comum e variantes relevantes (erro, vazio, com/sem ícone).

**Quando envolver o usuário**
- Dúvida se o componente deve levar o prefixo `Zera` ou não — o critério em `02` é uma heurística (fundamental/reutilizado amplamente vs. composição específica), não uma regra fechada para todo caso.
- Mudança que afeta um token global (cor base de `ZeraColorFamily`, escala de `Spacing`/`Radius`) — isso se propaga para todo componente existente.
- Criação de uma "família" de pasta nova em `view/components/` (nenhuma das existentes — `buttons`, `cards`, `containers`, `inputs`, `lists`, `logo`, `navigation`, `outros`, `progressbars`, `texts`, e as ainda vazias `images`/`overlays` — parece caber).

**Ao terminar:** documentar em `03-catalogo-componentes.md` com assinatura, parâmetros e exemplo de uso.

## 3. Alterar uma regra de negócio existente

**Antes de começar**
- Docs: o arquivo do domínio em [05-regras-de-negocio/](05-regras-de-negocio/) e a spec correspondente em [07-especificacoes/](07-especificacoes/).
- Código: todos os pontos que implementam a regra — tipicamente `usecase` e `ViewModel`; a `View` não deveria ter nenhuma lógica de regra para ajustar (se tiver, é ela mesma uma inconsistência a reportar, não a replicar).

**Durante**
- A regra de negócio em si mora no `usecase` (e, quando envolve decisão de fluxo — ex.: para onde navegar —, no `ViewModel`). Nunca na `View` nem embutida num DTO. Se a regra passar a ser usada por mais de um `usecase`/`ViewModel`, avalie se já vale a pena extrair um ponto comum — hoje não há camada de `Repository` nem serviço de domínio compartilhado (ver "Visão geral das camadas" em [01-arquitetura.md](01-arquitetura.md)).

**Quando envolver o usuário**
- **Sempre que a regra não estiver clara ou completa no documento, ou conflitar com o que o código faz hoje** — regra de negócio é decisão de produto, não algo para o modelo inferir a partir do código.
- Mudança que afeta permissões/comportamento entre perfis (Funcionário/Gestor) — combinada com a nota de que o Administrador não tem fluxo no app hoje, qualquer coisa que pareça "precisar" de um terceiro perfil na UI merece confirmação antes.

**Ao terminar:** atualizar o arquivo em `05-regras-de-negocio/` e o `Status` da spec em `07-especificacoes/`.

## 4. Integrar um novo endpoint ou mudar um contrato existente

**Antes de começar**
- Docs: [06-contratos-api.md](06-contratos-api.md) e [04-modelo-de-dados.md](04-modelo-de-dados.md).
- Código: `model/remote/service/` (interface do serviço), `model/entity/<domínio>/` (DTOs), `ApiClient` (para expor o novo `service`, se for o caso).

**Durante**
- A chamada de rede em si fica no `usecase`, nunca direto na `Screen` nem "solta" no `ViewModel` sem passar por um `usecase`. O `usecase` fala com `ApiClient.<service>` e, quando precisar, com `SharedPreferencesManager` — não existe camada de `Repository` intermediária hoje (ver "Visão geral das camadas" em [01-arquitetura.md](01-arquitetura.md)).

**Quando envolver o usuário**
- **Qualquer contrato ainda não fechado com o backend** — não adivinhar formato de payload (o caso já conhecido: `invitations/redeem` reaproveitando um DTO que não cobre todos os campos da tela de cadastro). Confirmar o contrato real antes de implementar contra um payload suposto.
- Mudança que quebra um contrato já documentado em `06-contratos-api.md` e usado por telas existentes.
- Qualquer decisão sobre tratamento de erro padronizado ou refresh de token — hoje não existe um padrão definido; introduzir um é uma decisão de arquitetura, não uma escolha local de quem está integrando um endpoint específico.

**Ao terminar:** atualizar `06-contratos-api.md` e `04-modelo-de-dados.md`.

## 5. Adicionar uma dependência

**Antes de começar**
- Docs: [09-stack-e-dependencias.md](09-stack-e-dependencias.md) — confirmar que a necessidade não é já coberta por algo existente.
- Código: declarar em `gradle/libs.versions.toml` (padrão do projeto), não inline em `build.gradle.kts`.

**Durante**
- Onde a dependência "mora" depende do tipo dela — bibliotecas de rede/serialização/persistência ficam confinadas a `model/remote/` ou `model/local/` (nunca referenciadas direto de `view/` ou `viewmodel/`); bibliotecas de UI (ex.: uma lib de ícones extra) só devem ser referenciadas em `view/`. Evite que uma dependência vaze para uma camada que não deveria conhecê-la.

**Quando envolver o usuário**
- Qualquer dependência que implica uma decisão arquitetural (framework de DI, banco local, biblioteca de imagens/analytics) — isso é candidato a [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/) e não deve ser adicionado sem confirmar com o usuário.
- Dependência utilitária pequena, sem impacto arquitetural (ex.: uma lib de formatação de data), pode ser adicionada com um aviso simples, sem necessariamente bloquear.

**Ao terminar:** atualizar `09-stack-e-dependencias.md`; registrar ADR em `08-decisoes-arquiteturais/` se a dependência for uma decisão estrutural.

## 6. Corrigir um bug

**Antes de começar**
- Confirmar que é de fato um bug e não uma regra de negócio ainda não documentada — checar `05-regras-de-negocio/` e a spec em `07-especificacoes/` antes de mudar o comportamento.

**Durante**
- Corrigir só o necessário para o bug relatado — não aproveitar para refatorar código não relacionado (ver instruções gerais do projeto sobre escopo mínimo).

**Quando envolver o usuário**
- A correção implica mudar um contrato de API já documentado.
- O "bug" é, na verdade, uma inconsistência já registrada em "Inconsistências conhecidas", [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md) (ex.: o erro de digitação "Sing"/"Sign", ou `ViewModel.state` expondo `MutableState`) — **não corrigir de forma incidental dentro de uma tarefa não relacionada**; um rename ou mudança de assinatura desse tipo afeta múltiplos arquivos e merece ser uma tarefa própria, combinada antes com o usuário.

**Ao terminar:** feedback padrão (seção 5 do [INDEX.md](INDEX.md)).

## 7. Criar um domínio novo do zero (ex.: inventário, triagem, manutenção)

**Antes de começar**
- Docs: a seção "Visão futura" em [00-contexto-geral.md](00-contexto-geral.md) dá só um resumo leve, de propósito — não é uma spec. Antes de modelar qualquer coisa, confirmar com o usuário o escopo exato da primeira fatia a implementar.
- Código: seguir a estrutura por camada já estabelecida (`model/entity/<domínio>/`, `model/usecase/<domínio>/`, `viewmodel/<domínio>/`, `view/screens/<domínio>/`), replicando o padrão do domínio `auth`/`manager`.

**Durante**
- Reforçando onde cada responsabilidade fica, para o domínio novo inteiro: DTOs em `model/entity/<domínio>/`; chamadas de API e regra de acesso a dado em `model/usecase/<domínio>/` (sem `Repository`, como no resto do projeto); coordenação de estado e navegação em `viewmodel/<domínio>/`; apresentação pura em `view/screens/<domínio>/` — seguindo a regra estrita de separação View/lógica em [01-arquitetura.md](01-arquitetura.md).

**Quando envolver o usuário**
- Modelagem de dados da hierarquia Categoria → Modelo → Produto e do código de barras composto — tem decisões em aberto (chave composta vs. entidades relacionadas, por exemplo) que não devem ser resolvidas sozinho.
- Qualquer parâmetro numérico de regra de negócio (tempo para "lote crítico", parâmetros da análise preventiva de manutenção) precisa vir do usuário/produto, nunca ser inventado para preencher a lacuna.

**Ao terminar:** um domínio novo tende a exigir `04`, `05`, `06` e `07` juntos — sugerir os quatro, não só um.

## 8. Mexer em navegação (rotas, `ZeraNavigator`, telas por perfil)

**Antes de começar**
- Docs: [01-arquitetura.md](01-arquitetura.md) (seção "Navegação") e [08-decisoes-arquiteturais/0001-navegacao-centralizada-zeranavigator.md](08-decisoes-arquiteturais/0001-navegacao-centralizada-zeranavigator.md); [00-contexto-geral.md](00-contexto-geral.md) para a nota sobre o Administrador não ter fluxo hoje.

**Durante**
- Rota nova sempre `sealed interface Route` + `@Serializable`; navegação disparada pelo `ViewModel` (regra estrita em `01`), exceto as duas telas incondicionais já registradas como exceção (`SplashScreen`, `WelcomeScreen`).

**Quando envolver o usuário**
- Introdução da primeira rota/área exclusiva do perfil Administrador — contradiz o que está documentado hoje; confirmar antes.
- Mudança em como a pilha de navegação é gerenciada para uma tela existente (`push` → `pushAndPop`, por exemplo) — isso muda o comportamento de "voltar" percebido pelo usuário final, não é um detalhe puramente técnico.

**Ao terminar:** se a mudança contradiz uma decisão registrada no ADR-0001, criar um **novo ADR** que a substitui — não reescrever o ADR-0001 (decisões antigas não são apagadas, ficam registradas com seu contexto histórico).

## Quando em dúvida, pare e pergunte

Resumo dos gatilhos que se repetem acima:

- Regra de negócio não documentada ou ambígua.
- Contrato de API ainda não confirmado com o backend.
- Qualquer coisa que envolva o perfil Administrador ter um fluxo no app.
- Corrigir uma inconsistência já registrada em `02-padroes-e-convencoes.md` como parte de uma tarefa não relacionada a ela.
- Decisão que merece um ADR (nova dependência estrutural, mudança de padrão arquitetural).
- Modelagem de dados para um domínio ainda não implementado.

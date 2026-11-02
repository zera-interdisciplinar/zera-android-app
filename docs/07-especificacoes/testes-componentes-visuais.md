# Spec — Testes de comportamento dos componentes visuais

## Metadados

- **Feature:** Testes de comportamento dos componentes de `view/components`, com Compose UI Test e Robolectric, sem emulador
- **Perfil(is) envolvido(s):** Não se aplica (qualidade interna; quem usa é o time de desenvolvimento)
- **Status:** em progresso (item 7 de 19)
- **Última atualização:** 2026-11-02

## Contexto

O projeto tem testes JVM para DTOs, clients, use cases e mapeamentos de `ViewModel`, mas nenhum teste renderiza um componente. Regressões em estados como carregando, desabilitado, erro e seleção só aparecem usando o app.

O que já existe:

| Peça | Situação |
|---|---|
| `src/test` | JUnit 4 puro. No pacote `view`, só testes de lógica (`ItemDeepLinkTest`, `AlertDeepLinkTest`, `ScreenAnimationTest`) |
| `src/androidTest` | Só o `ExampleInstrumentedTest` do template |
| `ui-test-junit4` | Declarado apenas para `androidTest` |
| `ui-test-manifest` | Já declarado como `debugImplementation` |
| `testTag` | Nenhum no código de produção |
| Componentes | Quase todos recebem estado e callbacks por parâmetro, o que dispensa fakes de `ViewModel` |

### Decisões

- **Testes rodam com Robolectric**, em `src/test`, sem emulador.
- **Sem testes de screenshot** nesta spec.
- **Sem CI** nesta spec: os testes rodam localmente.

## User story

Como desenvolvedor, quero testes que verifiquem o comportamento dos componentes visuais, para alterar a UI sabendo que estados e interações continuam funcionando, sem precisar abrir o app em um emulador.

## Cenários (Given/When/Then)

### Cenário: botão em carregamento

- **Given** um `ZeraButton` com `loading = true`
- **When** o componente é renderizado
- **Then** o texto não aparece, o indicador de progresso aparece e o botão fica desabilitado

### Cenário: entrada filtrada no token

- **Given** um `ZeraTokenInput` com `size = 6`
- **When** o usuário informa `12a345678`
- **Then** `onValueChange` recebe `123456` e `onFilled` recebe `123456`

### Cenário: paginação depois de uma falha

- **Given** uma lista paginada com o usuário no fim e `itemCount` sem mudar, como quando a página falha
- **When** a lista é recomposta
- **Then** `onEndReached` não é chamado de novo
- **And** só volta a ser chamado quando o usuário sobe e desce novamente até o fim

### Cenário: formulário enviando

- **Given** um formulário com `isSubmitting = true`
- **When** o componente é renderizado
- **Then** os campos e os botões ficam desabilitados e o botão principal mostra o texto de envio

## Como os testes são escritos

### Organização

- Cada arquivo espelha o pacote do componente: `view/components/buttons/ZeraButton.kt` é testado em `src/test/.../view/components/buttons/ZeraButtonTest.kt`.
- Métodos em inglês e camelCase, como nos testes atuais (`disablesClickWhileLoading`).
- Componentes são renderizados dentro do `ZeraTheme`, por meio de um helper único (`setZeraContent`).
- Comando padrão: `gradlew testDebugUnitTest`. O projeto não tem `testReleaseUnitTest`: no AGP 9, os testes unitários só existem na variante debug, a mesma do `ui-test-manifest`.

### Configuração do Robolectric

Valores globais em `app/src/test/resources/robolectric.properties`:

| Chave | Valor | Motivo |
|---|---|---|
| `application` | `android.app.Application` | Evita o `ZeraApp`, que inicializa o `SqliteManager`, algo de que os componentes não precisam |
| `sdk` | `36` | Igual ao `targetSdk`. Se a JDK local não suportar, usar `35` e registrar aqui |
| `qualifiers` | `w411dp-h891dp`, tela fixa de celular | A tela padrão do Robolectric é pequena e deixaria formulários e botões fora da área visível |

### O que testar

- Texto e ícones com `contentDescription` que aparecem ou somem conforme os parâmetros.
- Callbacks: se são chamados, quantas vezes e com qual valor.
- Estados: habilitado, desabilitado, carregando, erro, selecionado, vazio.
- Lógica interna do componente: filtro de dígitos, conversão de data, cálculo da seleção, alternância de senha.

### O que não testar

- Cores, `dp`, fontes e outros detalhes de aparência.
- Comportamento interno do Material, como tocar fora de um `Dialog` ou arrastar um `ModalBottomSheet`.
- Regras de negócio, que continuam nos testes de `ViewModel`.

### Como localizar elementos

| Elemento | Como localizar |
|---|---|
| Texto, botões e chips | `onNodeWithText` |
| Ícones com descrição | `onNodeWithContentDescription` |
| Rótulos dos inputs | Em maiúsculas (`"NOME"`), porque o `OverlineText` converte o texto |
| Campos de texto | Pelo texto atual, usando valores únicos em cada teste. O rótulo não é associado ao campo |
| Senha mascarada | Comparando `SemanticsProperties.EditableText` com o valor. A semântica `Password` só indica o tipo do teclado e não muda ao mostrar a senha |
| Campos desabilitados | Pelo texto atual. Campo desabilitado não expõe a ação de `SetText`, então `hasSetTextAction()` não o encontra |
| Botão em carregamento | `hasClickAction()`, já que o texto some |
| Indicador de progresso | `hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)`. É um nó separado do botão, então o estado desabilitado não aparece nele |
| `ZeraRadioButton` | `isSelectable()` |
| `SelectBoxItem` e "Selecionar todos" | Pelo texto, com `assertIsOn` e `assertIsOff` |
| Slider | `SemanticsActions.SetProgress` e `assertRangeInfoEquals` |

Não adicionar `testTag` ao código de produção sem discutir antes. Conteúdo criado pelo próprio teste, como os filhos usados no `AdaptiveRow`, pode usar `testTag` livremente.

### Tempo e animações

- Nunca usar `Thread.sleep`. Para esperas com tempo, como o `SwipeResetDelay`, usar `mainClock`.
- Teste instável não é aceito. Se a interação não ficar confiável no Robolectric, ela sai do teste e é registrada em [Limitações encontradas](#limitações-encontradas).

### Quando um teste revelar um problema

Os testes não alteram código de produção. Um problema encontrado segue este fluxo:

1. Registrar em [Problemas conhecidos](#problemas-conhecidos).
2. Escrever o teste com o comportamento **esperado**, marcado com `@Ignore("<motivo>")`, sem transformar o bug em comportamento esperado.
3. Decidir com o time se a correção entra em outro commit ou em outro card.

## Problemas conhecidos

Encontrados na leitura do código, antes de qualquer teste:

| # | Componente | Problema | Impacto |
|---|---|---|---|
| 1 | `SignUpViewModel` | **Corrigido.** `onFilled` dispara ao digitar o último dígito e de novo na ação "Concluído" do teclado, e o `signUp()` não verificava `isLoading`. Agora ele ignora chamadas durante o envio e o campo fica desabilitado. O `onFilled` repetido foi mantido no componente, porque permite tentar de novo depois de um erro | Podia enviar dois pedidos de cadastro com o mesmo convite |
| 2 | `EditItemForm` + `ZeraRadioButton` | O radio não tem parâmetro `enabled`, então "Possui danos?" continua ativo com `isSubmitting = true` | O usuário altera o formulário durante o envio |
| 3 | `ProductListItem` e `OptionListItem` | Com `onClickDisabled = true`, a seta e a tag somem, mas a `Surface` continua clicável, com efeito de toque e sem ação | O item parece clicável. A `Tag` de status também some, o que só está documentado no `OptionListItem` |
| 4 | `ZeraButton` | Com `loading = true`, o texto é removido e o indicador não tem descrição | O leitor de tela não informa qual é o botão |
| 5 | `ZeraTextInput` e `ZeraRadioButton` | O rótulo não é associado ao campo na semântica | O leitor de tela não anuncia o nome do campo |
| 6 | `PopupDialog` | A linha 34 tem um `feat: ` perdido no meio do KDoc | Só cosmético |

## Linha de base da suíte atual

`gradlew testDebugUnitTest` em 2026-11-02, antes do item 1: **360 testes, 0 falhas**. A suíte tinha 3 falhas, corrigidas antes de iniciar o checklist:

| Teste | Causa | Correção |
|---|---|---|
| `AlertDeepLinkTest.itemDeepLinkOpensItemDetails` | Problema do teste: `android.net.Uri` não existe no JUnit puro | A classe passou a rodar com Robolectric |
| `ItensMappingTest.omitsStatusTagWhenBackendHasNoChip` | Teste desatualizado: `DRAFT` agora tem o chip "Rascunho" | Novo teste `showsDraftTag`; o caso sem chip passou a usar `REMOVED` |
| `TokenRefreshAuthenticatorTest.onlyPublicRoutesSkipTheAccessToken` | Bug de produção: o merge `0c9f1b7` trouxe de volta a exclusão de `/auth/logout`, removida em `873769f`, e o logout ia sem `Authorization` | Exclusão removida do `shouldSendAccessToken`, como manda o `06-contratos-api.md` |

## Riscos técnicos

Validar nos itens 1 e 2 do checklist, antes de escrever o restante:

| Risco | Como tratar |
|---|---|
| Compatibilidade do Robolectric com AGP 9 e SDK 36 | **Validada** no item 1: Robolectric 4.17 roda o Compose UI Test |
| JDK para o SDK 36 | **Validada na JDK 25**, com `--add-exports=java.base/jdk.internal.access=ALL-UNNAMED` na JVM de teste. Ainda não testada na JDK 21 do Android Studio nem na 17 do workflow de release, o que precisará ser revisto quando os testes forem para o CI |
| `ContentProvider`s de Firebase e ML Kit no manifesto mesclado, inicializados pelo Robolectric | **Sem impacto** no item 1, que já roda com `isIncludeAndroidResources = true` |
| `Dialog`, menu do `ExposedDropdownMenuBox`, `ModalBottomSheet` e `DatePicker` | **Validados** `Dialog`, `ModalBottomSheet`, o menu e o `DatePicker`. Campo de texto dentro de `Dialog` não funciona: ver [Limitações encontradas](#limitações-encontradas) |
| Primeiro uso baixa o `android-all` do Maven | Precisa de rede só na primeira execução |

## Critérios de aceite

- [ ] `gradlew testDebugUnitTest` roda sem emulador e sem nenhuma falha, com os testes novos e os existentes.
- [ ] Nenhum teste usa `Thread.sleep` nem depende de rede, exceto o download inicial do Robolectric.
- [ ] Nenhuma alteração em código de produção vem junto com os testes. Correções decididas com o time ficam em commits `fix:` próprios.
- [ ] Todo componente do escopo cobre os casos listados no checklist, ou tem a ausência justificada em [Limitações encontradas](#limitações-encontradas).
- [x] No item 1, o `ZeraButtonTest` é visto falhando ao quebrar o componente temporariamente, sem commitar essa quebra.
- [ ] `docs/09-stack-e-dependencias.md` descreve a nova stack e o comando para rodar os testes.

## Edge cases considerados

- **Fuso horário em `ZeraDateInput`:** a data não pode mudar de dia ao ir e voltar do `DatePicker`. O teste define `America/Sao_Paulo` como fuso padrão e restaura o original no fim.
- **Valor inválido em `ZeraDateInput`:** abrir o seletor com um valor fora do formato não pode quebrar.
- **Seleção com paginação em `SelectBoxList`:** "Selecionar todos" age só nas opções carregadas e preserva as seleções que não estão na lista.
- **Lista curta em `LoadMoreEffect`:** quando a lista não enche a tela, a próxima página é pedida sozinha.
- **`AdaptiveRow`:** os filhos do teste têm largura fixa, para o resultado não depender da fonte renderizada no Robolectric.

## Checklist de execução

Um item por vez, um commit por item (`test: ...`), parando para revisão.

### Preparação

- [x] **1. Stack e `ZeraButton`.** Adicionar `testImplementation` de Compose BOM e `ui-test-junit4`, `isIncludeAndroidResources = true`, `qualifiers` no `robolectric.properties` e o helper `setZeraContent`. Robolectric e `robolectric.properties` já foram adicionados na correção da linha de base. `ZeraButtonTest`: mostra o texto; toque chama `onClick`; desabilitado não chama; `loading` esconde o texto, mostra o progresso e bloqueia o toque.
- [x] **2. `PopupDialog` e `BottomSheet`.** Valida as janelas no Robolectric. `PopupDialog`: mostra o conteúdo; o X chama `onDismissRequest`; `hasDismissButton = false` esconde o X. `BottomSheet`: mostra o conteúdo.

### Componentes base

- [x] **3. `IconButton`, `Tag` e `ItemStatus`.** `IconButton`: localizável pela descrição; toque e estado desabilitado. `Tag`: mostra o texto. `ItemStatusTag`: mostra o rótulo de cada status. `fromBackend`: normaliza caixa e espaços, aceita os apelidos `PENDING` e `APPROVED` e devolve `null` para valor desconhecido.
- [x] **4. `ZeraTextInput`.** Digitar chama `onValueChange`; rótulo omitido quando vazio; placeholder só com valor vazio; erro só com `isError` e mensagem; desabilitado; senha mascarada por padrão, com "Mostrar" e "Ocultar" alternando; campo comum sem alternância.
- [x] **5. `ZeraSearchInput`.** Digitar chama `onValueChange`; placeholder só com valor vazio; "Limpar busca" só com texto e envia `""`; ação do teclado chama `onSearch`.
- [x] **6. `ZeraTokenInput`.** Filtra para dígitos e respeita `size`; `value` externo com não dígitos é normalizado; `onFilled` só ao completar; "Concluído" incompleto não chama `onFilled`; completo chama; desabilitado; erro.
- [x] **7. `ZeraDropdownInput` e `ZeraSelectInput`.** Dropdown: abre, lista as opções, escolher chama `onValueChange` e fecha; desabilitado não abre; erro. Select: valor ou placeholder; toque chama `onClick`; desabilitado não chama; erro.
- [ ] **8. `ZeraDateInput`.** Toque abre o seletor; "Confirmar" devolve a mesma data em `dd/MM/yyyy`, inclusive em `America/Sao_Paulo`; "Cancelar" fecha sem chamar `onValueChange`; "Confirmar" sem data fecha sem chamar; valor inválido abre sem quebrar; desabilitado não abre. Não testar a seleção de dias no calendário, que é do Material.
- [ ] **9. `ZeraChipsGroup`, `ZeraRadioButton` e `ZeraSliderInput`.** Chips: seleção única chama `onSelectedChange`; múltipla adiciona e remove; desabilitado não responde; layout em linha e empilhado. Radio: estado selecionado, toque e rótulo. Slider: mostra rótulo e limites; `SetProgress` devolve inteiro; desabilitado.
- [ ] **10. `SelectBoxItem` e `SelectBoxList`.** Item: estado marcado e desmarcado; toque; descrição opcional. Lista: toque adiciona e remove; "Selecionar todos" marca, desmarca e preserva seleções de fora; marcada automaticamente com tudo selecionado; lista vazia sem a linha; `isLoadingMore` mostra progresso.

### Componentes com lógica

- [ ] **11. `AdaptiveRow`.** Filhos que cabem ficam na mesma linha e com a mesma largura; filho largo demais empilha todos; ordem preservada.
- [ ] **12. Popups.** `FeedbackPopup`: título e subtítulo opcional. `ErrorFeedbackPopup`: título fixo e descrição. `ConfirmationPopup`: textos, confirmar e cancelar. `DenyReasonPopup` ficou fora: ver [Limitações encontradas](#limitações-encontradas).
- [ ] **13. Formulários simples.** `CreateInviteForm`, `EditCategoryForm`, `EditModelForm` e `SelectMaterialsForm`: mostram os valores; edição chama o callback certo; erros por campo; salvar e cancelar; estado enviando; confirmar seleção de materiais.
- [ ] **14. `EditProfileForm` e `EditItemForm`.** Perfil: `*Enabled` bloqueia só o campo indicado; erros por campo; `errorMessage`; estado enviando. Item: tipo de dano só com `hasDamage`; `onHasDamageChange` recebe o valor atual; erros; estado enviando. Problema conhecido 2 como teste com `@Ignore`.
- [ ] **15. `ProductList` e `OptionList`.** Itens e toque com o item certo; lista vazia com o conteúdo padrão e customizado; `isLoadingMore`; `statusText` troca a seta pela tag; `ProductListItem` mostra `ID` com 7 caracteres; `OptionListItem` sem ícone e sem descrição em branco. Problema conhecido 3 como teste com `@Ignore`.
- [ ] **16. `EmployeeList`, `InviteList`, `Notification` e `NotificationList`.** Colaboradores: "Pendente" e "Ativo"; toque; vazia. Convites: dados e "Copiar código"; vazia. Notificação: ação só com `redirect`; X só com `onDismiss`. Lista: "Resolver agora" só com `resolvable` e `onItemClick`; vazia.
- [ ] **17. `LoadMoreEffect`.** Longe do fim não chama; chegar perto chama uma vez; `itemCount` novo com o usuário no fim chama de novo; sem mudança de `itemCount` não repete; subir e descer chama de novo; lista curta chama sozinha; respeita o `threshold`.
- [ ] **18. `DismissibleNotification`.** X chama `onDismiss`; deslizar para os dois lados chama `onDismiss`; item que continua na tela volta ao lugar depois do `SwipeResetDelay`, com `mainClock`.

### Fechamento

- [ ] **19. Documentação.** Atualizar `docs/09-stack-e-dependencias.md` e o `Status` desta spec, com as limitações e os problemas pendentes.

## Limitações encontradas

Preencher durante a execução, com o componente, a interação que ficou fora e o motivo.

| Componente | O que ficou fora | Motivo |
|---|---|---|
| `DenyReasonPopup` | Todos os testes: textos, validação, limpeza do erro, confirmar e cancelar | Um campo de texto dentro de `Dialog` nunca fica ocioso no Robolectric (`AppNotIdleException`), e o teste acaba estourando a memória. Reproduz com `Dialog { BasicTextField(...) }`, sem código do app, e também com `@GraphicsMode(NATIVE)`. Para testar, seria preciso separar o conteúdo do popup em um composable próprio ou usar `androidTest` |

## Fora de escopo desta spec

- CI e testes de screenshot.
- Correção dos [Problemas conhecidos](#problemas-conhecidos), salvo decisão específica.
- Telas (`view/screens`) e `ViewModel`s.
- Barras de navegação e scaffolds, incluindo o `ManagerBottomNavBar`, que usa `ViewModel`.
- `CurrentLocationMap`, `ScanArea` e `BarcodeAnalyzer`, que dependem de mapa ou câmera.
- Gráficos e suas animações, componentes de chat, carrosséis, skeletons, `MarkdownText` e cards não citados no checklist.

## Referências

- Stack atual: `../09-stack-e-dependencias.md`
- Catálogo de componentes: `../03-catalogo-componentes.md`
- Regra de paginação: `telas-de-erro.md`

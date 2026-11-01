# Spec — Central de Ajuda (Operário)

## Metadados

- **Feature:** Central de Ajuda (`HelpCentralHubScreen`) e detalhe de documento (`HelpDocumentScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** planejado
- **Última atualização:** 2026-11-01

## Contexto

A Central de Ajuda reúne os documentos de ajuda cadastrados no **Scrapy** como conteúdo (markdown), no scope `mobile`. É aberta pelo atalho fixo "Central de Ajuda" da `SideBar` (`Route.HelpCentralHub`).

Os documentos são as entries cuja chave começa com `category_`. Elas já chegam ao app no `POST /v1/flags`, que o `LoadFlags` chama ao entrar no app (restauração de sessão na splash ou login) e guarda em `AppConfig.flags`. Não há chamada nova ao back: a Central de Ajuda só lê esse mapa.

Formato de cada entry (documentação do Scrapy no Confluence, "9. Scrapy — configuração ao vivo do app mobile"):

```
"category_vidro": "Vidro\nSepare, embale e identifique\n<resto em markdown>"
```

- 1ª linha = título do documento.
- 2ª linha = subtítulo.
- Resto = corpo em markdown, exibido na tela de detalhe.

Conteúdo novo ou alterado no admin só aparece depois que o app recarrega as flags (fechar e abrir o app, ou novo login). Isso é aceito nesta versão.

## User story

Como Operário, quero buscar e ler os documentos de ajuda, para resolver dúvidas sem sair do app.

## Cenários (Given/When/Then)

### Cenário: Listar documentos

- **Given** o `AppConfig.flags` tem entries `category_*`
- **When** o Operário abre a Central de Ajuda
- **Then** a lista mostra um item por documento (nome = título, descrição = subtítulo), ordenada pelo título

### Cenário: Buscar documento

- **Given** a lista de documentos está visível
- **When** o Operário digita no campo de busca
- **Then** a lista mostra só os documentos cujo título ou subtítulo contém o termo (sem diferenciar maiúsculas/minúsculas, ignorando espaços nas pontas); termo vazio mostra todos

### Cenário: Abrir documento

- **Given** a lista de documentos está visível
- **When** o Operário toca em um documento
- **Then** o app abre `Route.HelpDocument(documentKey)`, que mostra o título e o corpo renderizado como markdown

### Cenário: Nenhum documento

- **Given** não há entries `category_*` (flags vazias, falha ou nada cadastrado)
- **When** o Operário abre a Central de Ajuda
- **Then** a lista mostra "Nenhum documento encontrado"

### Cenário: Documento que não existe mais

- **Given** a rota `HelpDocument` foi aberta com uma chave que não está em `AppConfig.flags` (ex.: flags limpas)
- **When** a tela é composta
- **Then** a tela mostra "Documento não encontrado"

### Cenário: Falar com o Zack

- **Given** a Central de Ajuda está aberta
- **When** o Operário toca em "Conversar"
- **Then** o app abre uma conversa nova com o assistente (`Route.ChatBot`), como já acontece hoje

## Desenho da implementação

### Modelo

- `model/entity/helpCentral/HelpDocument.kt` — `data class HelpDocument(key, title, subtitle, body)`.
  - `companion object { const val KEY_PREFIX = "category_"; fun fromEntry(key: String, value: String): HelpDocument? }`
  - `fromEntry` separa as linhas: 1ª = título, 2ª = subtítulo, resto = corpo (`trim` em cada parte). Linhas em branco antes do título e do subtítulo são puladas, e um `#` de cabeçalho no começo deles é removido. Título em branco → `null` (entry descartada). Sem 2ª linha → subtítulo e corpo vazios.
- `model/usecase/helpCentral/GetHelpDocuments.kt`
  - `execute(): List<HelpDocument>` — lê `AppConfig.flags` e devolve os documentos ordenados pelo título (ordem alfabética pt-BR, sem diferenciar acento e maiúscula). As flags vêm de um parâmetro `readFlags` (padrão `AppConfig.flags`), para os testes.
  - `find(key: String): HelpDocument?` — um documento pela chave.
  - `companion object { internal fun documentsFrom(flags: JsonObject): List<HelpDocument> }` — função pura (testável): filtra chaves com `KEY_PREFIX`, aceita só valores `JsonPrimitive` string e ignora o resto (bool, número, objeto).

### Componente

- Dependência nova: `com.mikepenz:multiplatform-markdown-renderer-m3` (versão compatível com Kotlin 2.0.21 e a BOM `2025.09.00`, fixada no `libs.versions.toml`).
- `view/components/texts/MarkdownText.kt` — `MarkdownText(text, modifier, color)`: envolve o `Markdown` da biblioteca aplicando a tipografia (`Type.kt`, Inter) e as cores do tema do app, para o markdown ficar igual aos outros textos (`BodyText`, `TitleText`). Com KDoc e `@Preview`.

### Central de Ajuda (já existe, será ligada)

- `HelpCentralHubViewModel`
  - Carrega os documentos de `GetHelpDocuments` no `init` (leitura em memória, sem loading).
  - `HelpCentralHubState`: `searchQuery`, `documents: List<OptionItem>` (`id` = chave, `name` = título, `description` = subtítulo, `icon` = `InfoCircle`). Saem `isLoading` e `errorMessage`, que não têm mais uso.
  - `onSearchQueryChange` filtra localmente (função pura `visibleDocuments(documents, query)` no `companion`).
  - `onDocumentClick` → `ZeraNavigator.push(Route.HelpDocument(documentKey = option.id))`.
  - `onSearch` deixa de existir; a busca já filtra ao digitar.
- `HelpCentralHubScreen`: tira o ramo de skeleton/erro. O placeholder da busca passa a ser "Pesquisar por título ou assunto..." (o do layout, "ID, Nome ou Material", não descreve o que é buscado aqui).

### Detalhe do documento (novo)

- `Route.HelpDocument(val documentKey: String)`, registrada no `ZeraNavHost`.
- `viewmodel/main/helpCentral/HelpDocument.kt`
  - `HelpDocumentState(title, body, notFound)`.
  - `HelpDocumentViewModel.load(documentKey)` busca via `GetHelpDocuments.find`.
- `view/screens/main/helpCentral/HelpDocumentScreen.kt`
  - `HelpDocumentScreen(documentKey, viewModel)` com `LaunchedEffect(documentKey) { viewModel.load(documentKey) }` (mesmo padrão da `ModelDetailsScreen`).
  - `EmployeeScaffold(title = "Central de ajuda", goBack = true, fabIcon = null)`.
  - Conteúdo: `TitleText` (título, negrito) + `MarkdownText` (corpo). Se `notFound`, `BodyText("Documento não encontrado")`.
  - Previews: com documento e não encontrado.

## Critérios de aceite

- [x] Dependência de markdown adicionada e `MarkdownText` criado em `view/components/texts`
- [x] `HelpDocument.fromEntry` separa título, subtítulo e corpo
- [x] `GetHelpDocuments` lê só as chaves `category_*` com valor string, ordenadas pelo título
- [x] Central de Ajuda lista os documentos reais e filtra pela busca
- [x] Tocar num documento abre `HelpDocumentScreen` com título e corpo em markdown
- [x] Testes unitários do parse, do filtro de chaves e da busca
- [ ] Docs atualizadas (catálogo de componentes, stack, contexto do Scrapy)

## Edge cases considerados

- Entry `category_*` com valor que não é string (bool, número, objeto): ignorada.
- Entry com título em branco: ignorada.
- Entry só com título: aparece na lista sem descrição; o detalhe mostra só o título.
- Quebras de linha `\r\n` (texto colado no admin): tratadas como `\n`.
- Flags vazias (falha no `/v1/flags` ou logout): lista vazia com "Nenhum documento encontrado".

## Fora de escopo desta spec

- Recarregar o conteúdo sem reiniciar o app (pull-to-refresh, polling, ETag/`If-None-Match`).
- Central de Ajuda para o Gestor: o atalho fixo da `SideBar` também aparece para ele, mas a tela usa `EmployeeScaffold`. Decidir separadamente.
- Uso dos `label_*` para os textos da tela.
- Hoje `LoadFlags` não está em `runCatching`: se o `/v1/flags` falhar, o login falha junto. Comportamento já existente, não muda aqui.

## Referências

- Contrato: [../contrato-scrapy/contrato-scrapy-api.md](../contrato-scrapy/contrato-scrapy-api.md)
- Integração do Scrapy no app: [../contrato-scrapy/contexto.md](../contrato-scrapy/contexto.md)
- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`EmployeeScaffold`, `ZeraBox`, `ZeraSearchInput`, `OptionList`)
- Confluence: "9. Scrapy — configuração ao vivo do app mobile" (espaço Back-end)

## Checklist de execução

Uma etapa por vez, com revisão antes da próxima.

1. [x] Dependência de markdown + componente `MarkdownText`
2. [x] `HelpDocument` (entity + `fromEntry`) + testes
3. [x] `GetHelpDocuments` (use case + `documentsFrom`) + testes
4. [x] Ligar `HelpCentralHubViewModel`/`HelpCentralHubScreen` aos documentos + teste da busca
5. [x] `Route.HelpDocument` + `HelpDocumentViewModel` + `HelpDocumentScreen` + `ZeraNavHost`
6. [ ] Atualizar docs (`03-catalogo-componentes.md`, `09-stack-e-dependencias.md`, `contrato-scrapy/contexto.md`, status desta spec)

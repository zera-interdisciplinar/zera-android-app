# Spec — Escanear item (Operário)

## Metadados

- **Feature:** Tela de escanear item (`ScanScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (leitura + busca por barcode e cadastro de item prontos; cadastro de modelo pendente)
- **Última atualização:** 2026-10-11

## Contexto

`Route.Scan` é aberta pelo botão central (`ZeraIcon.QrCode`) da `EmployeeBottomNavBar`. A tela mostra a `ScanArea` com a câmera do usuário e dois atalhos de cadastro. A permissão de câmera é checada e pedida na própria tela (precisa de `Context`/`Activity`); o resultado chega ao `ScanViewModel` por `onCameraPermissionChanged`.

O valor lido (QR ou Code 128) é o `barcode` do item. A busca usa `GET /api/v1/items/by-barcode/{barcode}` (`GetItemByBarcode`): 200 abre o detalhe; 404 abre o cadastro com o código preenchido.

## User story

Como Operário, quero apontar a câmera para a etiqueta de um item, para identificá-lo ou cadastrá-lo sem digitar tudo.

## Cenários (Given/When/Then)

### Cenário: Câmera permitida

- **Given** o app tem a permissão `CAMERA`
- **When** a tela é exibida ou retomada
- **Then** `ScanArea` mostra a câmera (`hasCameraPermission = true`)

### Cenário: Câmera sem permissão

- **Given** o app não tem a permissão `CAMERA`
- **When** a tela é exibida
- **Then** `ScanArea` mostra o botão "Permitir câmera"; ao tocar, a tela dispara o pedido de permissão

### Cenário: Permissão alterada fora do app

- **Given** o Operário concede ou revoga a permissão pelas configurações do sistema
- **When** volta ao app (`ON_RESUME`)
- **Then** a tela reconfere a permissão e atualiza `hasCameraPermission`

### Cenário: Item encontrado pelo código

- **Given** a etiqueta lida existe na unidade
- **When** a câmera aceita a leitura (fora da janela de debounce)
- **Then** o app chama `GET /items/by-barcode/{código}` e navega para `Route.ItemDetails(itemId)`

### Cenário: Item não encontrado

- **Given** a etiqueta lida não existe na unidade (`404`)
- **When** a busca termina
- **Then** navega para `Route.ManualRegister(barcode = <código>)` com o campo já preenchido

### Cenário: Falha de rede ou outro HTTP

- **Given** a busca falha (rede ou status diferente de 404)
- **When** a leitura é aceita
- **Then** a tela de scan permanece; o status mostra o erro; uma nova leitura pode ser tentada

### Cenário: Cadastrar sem etiqueta

- **Given** a tela de scan está aberta
- **When** o Operário toca em "Cadastrar sem etiqueta"
- **Then** navega para `Route.ManualRegister()` (ver [cadastro-manual-item.md](cadastro-manual-item.md))

## Critérios de aceite

- [x] Permissão de câmera conferida a cada `ON_RESUME` e pedida pelo botão "Permitir câmera"
- [x] "Cadastrar sem etiqueta" leva ao cadastro manual
- [x] Ler a etiqueta/QR code e abrir o item correspondente (`GET /items/by-barcode/{barcode}`)
- [x] Código desconhecido abre o cadastro com o barcode preenchido
- [x] "Cadastrar modelo" navega para `Route.Models` (lista, orientação e cadastro já existentes)

## Edge cases considerados

- Permissão negada: a tela continua utilizável, e os dois atalhos de cadastro seguem disponíveis.
- A tela esconde as ações da `UpperNavBar` (`showActions = false`) e o FAB (`fabIcon = null`).
- Enquanto a busca está em andamento, novas leituras da câmera são ignoradas.
- O mesmo código é ignorado por 2 s enquanto permanece no quadro.

## Fora de escopo desta spec

- Tela de cadastro de modelo do Operário.
- Envio do cadastro de item (`POST /items`) — ver [cadastro-manual-item.md](cadastro-manual-item.md).

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`EmployeeScaffold`, `EmployeeBottomNavBar`)
- Specs relacionadas: [cadastro-manual-item.md](cadastro-manual-item.md), [employee-home.md](employee-home.md)

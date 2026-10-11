# Spec — Escanear item (Operário)

## Metadados

- **Feature:** Tela de escanear item (`ScanScreen`)
- **Perfil(is) envolvido(s):** Operário (funcionário)
- **Status:** em progresso (permissão de câmera e atalhos prontos; leitura do código e cadastro de modelo pendentes)
- **Última atualização:** 2026-10-09

## Contexto

`Route.Scan` é aberta pelo botão central (`ZeraIcon.QrCode`) da `EmployeeBottomNavBar`. A tela mostra a `ScanArea` com a câmera do usuário e dois atalhos de cadastro. A permissão de câmera é checada e pedida na própria tela (precisa de `Context`/`Activity`); o resultado chega ao `ScanViewModel` por `onCameraPermissionChanged`.

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

### Cenário: Cadastrar sem etiqueta

- **Given** a tela de scan está aberta
- **When** o Operário toca em "Cadastrar sem etiqueta"
- **Then** navega para `Route.ManualRegister()` (ver [cadastro-manual-item.md](cadastro-manual-item.md))

## Critérios de aceite

- [x] Permissão de câmera conferida a cada `ON_RESUME` e pedida pelo botão "Permitir câmera"
- [x] "Cadastrar sem etiqueta" leva ao cadastro manual
- [ ] Ler a etiqueta/QR code e abrir o item correspondente
- [ ] "Cadastrar modelo" navegar para o cadastro de modelo do Operário (hoje `TODO` em `onRegisterModelClick`; a tela de destino ainda não existe)

## Edge cases considerados

- Permissão negada: a tela continua utilizável, e os dois atalhos de cadastro seguem disponíveis.
- A tela esconde as ações da `UpperNavBar` (`showActions = false`) e o FAB (`fabIcon = null`).

## Fora de escopo desta spec

- Decodificação do código de barras/QR e busca do item no back.
- Tela de cadastro de modelo do Operário.

## Referências

- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md) (`EmployeeScaffold`, `EmployeeBottomNavBar`)
- Specs relacionadas: [cadastro-manual-item.md](cadastro-manual-item.md), [employee-home.md](employee-home.md)

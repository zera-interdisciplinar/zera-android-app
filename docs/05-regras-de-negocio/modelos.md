# Regras de Negócio — Modelos

## Metadados

- **Domínio:** Inventário (modelos)
- **Perfil(is) envolvido(s):** Gestor e Operário
- **Última atualização:** 2026-11-02

## Visão geral

Este documento descreve o fluxo de consulta e cadastro de modelos disponível na interface do Gestor.

## Regras

### Orientação de uso

A tela de modelos apresenta a orientação “Como usar o Modelo”, controlada por `isModelUsageSheetVisible` em `ModelsViewModel`. A sheet aparece só no primeiro acesso; ao dispensar, a marca fica em `SqliteManager` e sobrevive ao logout.

### Campos do cadastro

O formulário pede nome do modelo, categoria, materiais, marca e observação. As opções de categoria e de material vêm da API (`GET /categories`, `GET /materials`). O modelo tem ao menos um material, escolhido em um bottom sheet de seleção múltipla.

Efeitos de escolher vários materiais, definidos no back-end ([Domínio](https://zerainterdisciplinar.atlassian.net/wiki/spaces/Backend/pages/55541769)):

- O modelo é perigoso quando qualquer material dele for perigoso (`BATTERY`, `CIRCUIT_BOARD`, `SCREEN`).
- Nos indicadores, o peso do item é dividido igualmente entre os materiais do modelo.

### Confirmação

Ao concluir, o app valida os campos e chama `POST /models`. Com sucesso, navega para a tela de confirmação com os valores devolvidos pela API. “Registrar outro modelo” abre um formulário novo; “Ir para home” e o botão de fechar limpam a pilha e retornam ao início do Gestor.

## Relacionamento com perfis

O fluxo está em `Route.Models`, `Route.ModelDetails`, `Route.ModelCreation` e `Route.ModelCreationSuccess`. Clique na lista abre o detalhe (`GET /models/{id}`). Não existe `POST /models/{id}/approve`. Modelo `PENDING`: o gestor vê Aprovar/Recusar. O clique busca o item em `GET /models/{id}/items` (fallback `GET /items?modelId=&status=PENDING_APPROVAL`) e chama `POST /items/{id}/approve` ou `/reject`. Sem item pendente, o botão permanece e a API responde com aviso. O operário edita nome e marca (`PATCH` por campo).

## Referências

- Spec: [../07-especificacoes/modelos.md](../07-especificacoes/modelos.md)
- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md)
- Modelo de dados: [../04-modelo-de-dados.md](../04-modelo-de-dados.md)

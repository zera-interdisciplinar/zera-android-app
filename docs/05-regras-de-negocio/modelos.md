# Regras de Negócio — Modelos

## Metadados

- **Domínio:** Inventário (modelos)
- **Perfil(is) envolvido(s):** Gestor e Operário
- **Última atualização:** 2026-10-03

## Visão geral

Este documento descreve o fluxo de consulta e cadastro de modelos disponível na interface do Gestor. A integração de criação com o backend ainda não está implementada.

## Regras

### Orientação de uso

A tela de modelos apresenta a orientação “Como usar o Modelo”, controlada por `isModelUsageSheetVisible` em `ModelsViewModel`. A sheet aparece só no primeiro acesso; ao dispensar, a marca fica em `SqliteManager` e sobrevive ao logout.

### Campos do cadastro

O formulário mantém localmente nome do modelo, material, marca e observação. Material é selecionado em um dropdown; a lista atual (`Plastico`, `Ferro`, `Aço`, `Seila`) é temporária e deve ser substituída por opções definidas pelo produto ou fornecidas pelo backend.

### Confirmação

Ao concluir, a interface navega para a tela de sucesso levando os valores preenchidos. “Registrar outro modelo” abre um formulário novo; “Ir para home” e o botão de fechar limpam a pilha e retornam ao início do Gestor.

**Limitação atual:** a confirmação é somente visual. Não há validação de negócio, chamada de API, persistência do modelo nem aprovação real; a mensagem de sucesso não deve ser interpretada como confirmação do backend.

## Relacionamento com perfis

O fluxo está em `Route.Models`, `Route.ModelDetails`, `Route.ModelCreation` e `Route.ModelCreationSuccess`. Clique na lista abre o detalhe (`GET /models/{id}`). Não existe `POST /models/{id}/approve`. Modelo `PENDING`: o gestor vê Aprovar/Recusar. O clique busca o item em `GET /models/{id}/items` (fallback `GET /items?modelId=&status=PENDING_APPROVAL`) e chama `POST /items/{id}/approve` ou `/reject`. Sem item pendente, o botão permanece e a API responde com aviso. O operário edita nome e marca (`PATCH` por campo).

## Referências

- Spec: [../07-especificacoes/modelos.md](../07-especificacoes/modelos.md)
- Componentes: [../03-catalogo-componentes.md](../03-catalogo-componentes.md)
- Modelo de dados: [../04-modelo-de-dados.md](../04-modelo-de-dados.md)

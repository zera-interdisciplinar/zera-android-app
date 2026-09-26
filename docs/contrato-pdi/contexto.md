# contexto
As telas de PDI (Páginas de detalhes do item) precisam ser populadas de acordo com cada item individualmente, e a página de listagem de itens precisa ser atualizada para deixar de mostrar os dados mockados e mostrar os dados da API Inventory Dashboard.

## plano de ação
### Baseado no contrato-detalhes-itens.md, faça as seguintes alterações para popular as telas de PDI:

- Crie novo usecase para popular as telas de PDI dentro de inventory do usecase.
- Crie novos métodos (se necessário) dentro do client de inventory para fazer as novas requests.
- Crie novos métodos (se necessário) dentro do service de inventory para fazer as novas requests.
- Atualize as telas de PDI para ser compativel com as alterações feitas no usecase e no client.
- Atualize/crie a viewmodel responsável pela página de detalhes do item.

### Baseado no contrato-detalhes-itens.md, faça as seguintes alterações para popular a página de listagem de itens:
- Crie novo usecase para popular a página de listagem de itens dentro de inventory do usecase.
- Crie novos métodos (se necessário) dentro do client de inventory para fazer as novas requests.
- Crie novos métodos (se necessário) dentro do service de inventory para fazer as novas requests.
- Atualize a tela de listagem de itens para ser compativel com as alterações feitas no usecase e no client.
- Atualize/crie a viewmodel responsável pela página de listagem de itens.

## O que fazer em caso de duvida de contrato
Pare imediatamente de desenvolver e informe de forma estruturada o contexto, o problema e o que te afeta, não diga a solução, apenas o problema.

## Como validar se as alterações foram feitas corretamente
- Não faça nada, me chame para validar as alterações, debugamos juntos.

## Padrões de projeto
Faça as alterações seguindo os padrões de projeto do projeto já existente, assim como padrão de nome de variavel, idioma, funções quando necessárias, etc.
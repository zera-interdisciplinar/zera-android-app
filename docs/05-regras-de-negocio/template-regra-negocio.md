# Template de Regras de Negócio

> Copie este arquivo para criar o arquivo de regras de negócio de um novo domínio (ex.: autenticação, inventário, triagem). Preencha todas as seções; remova apenas exemplos entre colchetes. Nomeie o arquivo copiado pelo domínio (ex.: `autenticacao.md`, `inventario.md`).

## Metadados

- **Domínio:** [nome do domínio]
- **Perfil(is) envolvido(s):** [Funcionário / Gestor / Administrador]
- **Última atualização:** [AAAA-MM-DD]

## Visão geral

[Resumo de uma ou duas frases do que esse domínio de regras cobre e por que essas regras existem.]

## Regras

### [Nome da regra 1]

[Descrição da regra: condição, comportamento esperado, exceções.]

### [Nome da regra 2]

[Descrição da regra: condição, comportamento esperado, exceções.]

## Parâmetros configuráveis

[Se alguma regra depender de um parâmetro de negócio (ex.: tempo para "lote crítico", quantidade mínima de manutenções), liste aqui o parâmetro, seu valor atual e de onde ele vem — configuração, cadastro do usuário, valor fixo no código, etc. Remova esta seção se não houver nenhum.]

## Relacionamento com perfis

[Quem pode acionar cada regra, ou ver o resultado dela — diferencie Funcionário, Gestor e, se algum dia se aplicar, Administrador.]

## Referências

- Specs relacionadas: `../07-especificacoes/[arquivo].md`
- Contratos de API: `../06-contratos-api.md`
- Modelo de dados: `../04-modelo-de-dados.md`

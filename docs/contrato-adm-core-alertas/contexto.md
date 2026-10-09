# contexto

Hoje em dia o projeto até tem suporte para alertas, mas de uma forma que não segue o back, o contrato implementado é errado. A UI é correta, e a mudança dela é OOS (out-of-scope).

Sua tarefa é corrigir a forma que o contrato é implementado, mudando o back do mobile, seguinto estritamente o contrato em `contrato.md`.

## detalhes

- mude a viewmodel e model para atualizar o back do mobile
- atualize os contratos em dto
- mudar UI é fora de escopo, mudança permitida apenas para mudar o conteudo que pega do viewmodel (state)

## restrições

- não altere a UI, apenas o back do mobile
- não altere nada de contrato que NAO seja relacionado a alertas
- não altere nada de UI que NAO seja relacionado a alertas

## DOD

- Os alertas agora são exibidos de forma dinamica de acordo com o conteudo passado pela api
- O projeto tem testes mobile para cobertura de 100% de código novo implementado


## padrão de implementação

- desenvolva usando TDD
- desenvolva usando o padrão de projeto MVVM
- desenvolva usando o padrão subagent-driven
- se tiver qualquer incoerencia me consulte imediatamente
- coloque o minimo de nova complexidade, evite dead code, funções atoa, etc.
- siga o mesmo padrão de nome de variaveis do projeto, prefira nomes completos e explicativos do que os curtos.
- rode um subagente para validar o código, com enfase também nos padrões de projeto que disse acima (os 2 pontos acima)


## Rodar testes mobile

- Não rode testes do seu lado usando sandbox, utilize o shell mesmo. Rode de forma rápida, não fique muito travado
- Só rode testes após o término do desenvolvimento e após a correção de todos os bugs encontrados e revisados
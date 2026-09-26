# 00 — Contexto Geral

## Visão do produto

O Zera é um sistema de gestão e controle de lixo eletrônico corporativo, desenvolvido inicialmente para o Instituto J&F / JBS. O app Android é o ponto de entrada operacional do sistema: é nele que funcionários e gestores registram, classificam e acompanham os equipamentos eletrônicos armazenados, do cadastro inicial até a destinação final (reuso, aproveitamento de peças ou descarte).

O produto resolve um problema hoje resolvido de forma manual e desorganizada (planilhas, controle informal), trazendo rastreabilidade via identificação individual dos itens, um fluxo obrigatório de triagem e alertas para evitar acúmulo de resíduos tóxicos além do tempo seguro de armazenamento.

## Público-alvo

Empresas de médio/grande porte com operação de TI própria e volume relevante de descarte de equipamentos eletrônicos. O piloto é o Instituto J&F / JBS. Os usuários finais do app são colaboradores do time de TI/almoxarifado (funcionários) e seus responsáveis (gestores), não o público externo.

## Perfis de usuário

O app distingue três perfis, com permissões cumulativas (Administrador ⊃ Gestor e Funcionário):

- **Funcionário** — opera o dia a dia: triagem de produtos, escaneamento de código de barras, consulta ao inventário, registro de manutenções, criação de modelos.
- **Gestor** — além do que o Funcionário faz, acompanha a operação: geração e visualização de relatórios, gestão de alertas, análise preventiva de manutenção, consulta ao inventário, criação de categorias e modelos.
- **Administrador** — acesso total, incluindo gestão de usuários, configuração de parâmetros do sistema, confirmação de descartes e envio de relatórios para recicladoras.

O papel do usuário autenticado (`role` em `SelfUserResponseDTO`) define para qual área do app ele é direcionado após o login — ver `05-regras-de-negocio/` (regras de autenticação ainda não escritas; usar `template-regra-negocio.md` como base).

> O Administrador é um perfil de permissões do sistema (ver escopo de produto), mas **não tem, até o momento, nenhum fluxo ou tela específica no app** — as seções a seguir cobrem apenas Gestor e Funcionário.

## Principais fluxos de usuário

Implementados no app hoje:

1. **Onboarding/login** — splash (`POST /v1/boot` na Scrapy, header `apikey`) → tela de boas-vindas → login ou primeiro acesso (cadastro via código de convite) → `POST /v1/flags` → redirecionamento por perfil (Gestor ou Funcionário).
2. **Visão geral do Gestor** — dashboard com ocupação de estoque, atalhos (itens/funcionários), alertas e últimos itens cadastrados.

Visão futura (resumo do escopo de produto para Gestor e Funcionário; cada item ganhará spec própria em [07-especificacoes/](07-especificacoes/) quando entrar em desenvolvimento):

- **Cadastro de inventário (Funcionário/Gestor)** — hierarquia Categoria → Modelo → Produto, com código de barras `id_produto-id_modelo-id_categoria` impresso e aderido ao equipamento físico.
- **Triagem (Funcionário)** — classificação de cada item como reutilizável, aproveitável (peças/upgrade) ou descartável, incluindo checklist de periculosidade para componentes sensíveis (ex.: baterias).
- **Acompanhamento de alertas e relatórios (Gestor)** — alertas de lote crítico (item descartável armazenado por tempo excessivo) e geração de relatório consolidado de descarte, com prévia para revisão manual antes do envio à recicladora.
- **Acompanhamento de manutenção (Gestor, com registro pelo Funcionário)** — análise preventiva baseada em regras de negócio simples (vida útil, histórico de manutenções, desgaste estimado, frequência de uso), sem sensores ou IA avançada.

## Fora de escopo

- Automação física do descarte (o sistema organiza e comunica, não substitui a coleta física).
- Sensores físicos ou monitoramento em tempo real dos equipamentos.
- Modelos preditivos de IA avançada — as análises de manutenção usam regras de negócio simples sobre dados cadastrados manualmente.
- Impressão dos códigos de barras pelo próprio app (o app gera o código; a impressão e a fixação física são um processo externo).

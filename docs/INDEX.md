# Índice da Documentação — Zera Android

Esta pasta é a fonte de verdade técnica do app Android do Zera, seguindo práticas de **Spec-Driven Development (SDD)**. Antes de gerar código novo (por humano ou por IA), leia os documentos relevantes listados aqui.

## 1. Resumo dos documentos

| Documento | Conteúdo |
|---|---|
| [00-contexto-geral.md](00-contexto-geral.md) | Visão do produto, público-alvo, perfis de usuário e principais fluxos hoje implementados. |
| [01-arquitetura.md](01-arquitetura.md) | Camadas do app, organização de pacotes, fluxo de dados, navegação e (ausência de) injeção de dependência. |
| [02-padroes-e-convencoes.md](02-padroes-e-convencoes.md) | Convenções de nomenclatura, organização de pastas, estilo de código Compose/Kotlin, inconsistências conhecidas. |
| [03-catalogo-componentes.md](03-catalogo-componentes.md) | Design System (tokens, textos, botões, containers, inputs, cards, listas, navegação, ícones) e componentes de domínio reutilizáveis. |
| [04-modelo-de-dados.md](04-modelo-de-dados.md) | DTOs, persistência local (SharedPreferences) e mapeamentos entre camadas. |
| [05-regras-de-negocio/](05-regras-de-negocio/) | Um arquivo por domínio de negócio, com `template-regra-negocio.md` como modelo padrão. |
| [06-contratos-api.md](06-contratos-api.md) | Endpoints consumidos, payloads, autenticação e tratamento de erros (adm-core e Scrapy). |
| [contrato/contrato-scrapy-api.md](contrato/contrato-scrapy-api.md) | Contrato da API mobile Scrapy (`POST /v1/boot`, `POST /v1/flags`, header `apikey`, prefixo Kong). |
| [contrato/contexto.md](contrato/contexto.md) | Como o app Android integra a Scrapy (`ScrapyClient`, `Boot`, `LoadFlags`, `AppConfig`). |
| [07-especificacoes/](07-especificacoes/) | Um arquivo por feature/user story no formato Given/When/Then, com `template-spec.md` como modelo padrão. |
| [08-decisoes-arquiteturais/](08-decisoes-arquiteturais/) | ADRs curtos: decisão, alternativas consideradas e motivo da escolha. |
| [09-stack-e-dependencias.md](09-stack-e-dependencias.md) | Bibliotecas usadas, versões e motivo da escolha (inclui nota sobre Firebase configurado mas não utilizado). |
| [10-fluxos-de-trabalho.md](10-fluxos-de-trabalho.md) | Checklist por tipo de tarefa: o que checar no código, o que checar nos docs, e quando parar e envolver o usuário. |

## 2. Guia de leitura por tipo de tarefa

Ver [10-fluxos-de-trabalho.md](10-fluxos-de-trabalho.md) — cobre criação de tela, criação/ajuste de componente do Design System, alteração de regra de negócio, integração de endpoint, adição de dependência, correção de bug, criação de domínio novo, mudanças de navegação e atualização da documentação a partir do diff da branch, cada um com o que checar antes/durante e os gatilhos para parar e perguntar ao usuário em vez de decidir sozinho.

## 3. Regra de atualização da documentação

> Ao concluir a implementação de uma feature ou componente, sempre sugira ao usuário atualizar os documentos impactados, indicando quais arquivos seriam afetados (spec em `07-especificacoes/`, regras em `05-regras-de-negocio/`, catálogo de componentes em `03-catalogo-componentes.md`, contratos de API em `06-contratos-api.md` e `contrato/` quando for Scrapy, e ADRs em `08-decisoes-arquiteturais/` caso alguma decisão técnica relevante tenha sido tomada durante a implementação) e o que mudaria em cada um. Nunca edite a documentação automaticamente sem confirmação explícita do usuário.

## 4. Regra de feedback sobre novas implementações

> Ao final da implementação de qualquer feature ou componente novo, além de sugerir a atualização da documentação, dê um feedback sobre a implementação: aderência aos padrões definidos em `02-padroes-e-convencoes.md`, alinhamento com a arquitetura em `01-arquitetura.md`, cobertura das regras de negócio relacionadas em `05-regras-de-negocio/`, e pontos de possível melhoria ou risco técnico.

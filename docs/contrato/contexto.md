# contexto

Iremos implementar todo o contrato de API para a API de Scrapy. Ela irá carregar todas as informações dinamicas do app. Para isso, iremos criar um novo pacote chamado `config`. Este pacote irá conter todas as informações necessárias para o app funcionar corretamente.

## Estrutura do pacote `config`

```
config
├── api
├── auth
├── cache
├── environment --> nossas variaveis carregadas do scrapy
```

## Proposito

A API do scrapy faz com que o app possa ser atualizado sem a necessidade de uma nova versão do app, além de facilitar comportamento dinâmico com diferentes segmentações, feature flags, etc. Dessa forma, poderemos mudar o comportamento do app sem a necessidade de uma nova versão, ou fazer build, etc.

## API

Todo o contrato de API será definido em um arquivo chamado `contrato-scrapy-api.md`. Este arquivo irá conter todas as informações necessárias para o app funcionar corretamente.
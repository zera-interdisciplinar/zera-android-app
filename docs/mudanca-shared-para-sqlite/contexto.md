# Contexto
Atualmente, o app zera guarda as informações do usuário pelo shared preferences. Isso é uma pratica insegura, pois as informações são armazenadas em um arquivo local e podem ser acessadas por qualquer individuo com facilidade.

Para resolver isso, vamos mudar o app para usar o SQLite como banco de dados. O SQLite é um banco de dados leve e fácil de usar, e é o banco de dados padrão do Android.

## Plano de implementação

A mudança será feita no pacote `com.zera.android.model.local`, que atualmente tem o shared preferences manager. Perceba que o arquivo é preparado para servir todas as models de forma ideal, a intenção é gerar o minimo de impacto nas outras partes. A mudança será a crição de um novo pacote, e a exclusão do pacote legado.

## em caso de dúvida

Pare imediatamente de fazer qualquer alteração, e me chame para discutir a melhor forma de resolver o problema.

# Testes
Há testes para mudar, primeiro troque o código e só depois teste tudo de uma só vez - quando voce começar a rodar o comando de testes, todo código já deve estar no formato ideal para rodar testes sem falhar.
## Banco Malvader - Introdução

- Projeto desenvolvido em colaboração com @gustavo_souto.
- O projeto consiste em um sistema para um banco fictício denominado **Malvader**.
- Foi implementado utilizando **Java** como linguagem de programação, com integração a um banco de dados relacional **MySQL**.

## Descrição do Projeto

- A arquitetura do projeto segue o padrão **DAO (Data Access Object)**, **Controller** e **Model**, proporcionando uma modularização coerente e de fácil compreensão, mesmo com a presença de um volume significativo de código.
- O sistema permite a criação de contas para diferentes tipos de usuários (funcionários e clientes). 
- Funcionários possuem acesso a funcionalidades administrativas, como:
  - Cadastro de novos clientes;
  - Criação de contas bancárias;
  - Edição de informações de contas existentes;
  - Exclusão de contas;
  - Entre outras operações.

## Como Configurar o Projeto Localmente

- Para executar o projeto localmente, siga os passos abaixo:

1. **Adicionar Bibliotecas Necessárias:**
   - Certifique-se de adicionar as bibliotecas (libraries) requeridas à IDE utilizada. O processo de referência das bibliotecas pode variar dependendo da IDE escolhida.

2. **Configurar Conexão com o Banco de Dados:**
   - Edite o arquivo `ConnectionFactory.java`, localizado na pasta **DAO**, ajustando o trecho relacionado às variáveis de conexão:

```java
// Variáveis para conexão
private static String url = "jdbc:mysql://db-malvader.c7miwyc2szll.us-east-2.rds.amazonaws.com:3306/db_malvader"; 
private static String user = "AllanBanco"; 
private static String password = "Banco.lula13";
```

- Substitua:
  - **`url`** pelo link do seu servidor de banco de dados;
  - **`user`** pelo nome de usuário para acesso ao banco de dados;
  - **`password`** pela senha correspondente.

3. **Executar a Classe Principal:**
   - Navegue até a classe `BancoMalvader.java`, localizada na pasta **App**, e execute o arquivo para iniciar o sistema.

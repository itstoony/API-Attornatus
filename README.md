# Avaliação Back-end - Attornatus

<div align = "center">
<h3><b>## Desafio Attornatus</b></h3></div>

    ### Tecnologias e bibliotecas utilizadas:

* Java 17
* Spring Boot
* Spring Data JPA
* Spring Cloud Open Feign
* JUnit 5
* Mockito
* Lombok
* ModelMapper
* Banco de Dados H2
* Maven
* Jacoco
* Swagger OpenAPI
* MySQL

<hr>

* Projeto inteiramente desenvolvido sob TDD.
* Consome o Web Serviço "https://viacep.com.br/" para consultar de CEP.
* Acessar "http://localhost:8080/actuator/logfile" para LOG's da aplicação
* Rodar "mvn test" para execução de testes.
* Acessar "http://localhost:8080/swagger-ui/index.html" para Documentação Swagger-ui (localmente).
* Deployment da aplicação em Nuvem disponível em: https://attornatus.up.railway.app/
* Documentação em Nuvem: https://attornatus.up.railway.app/swagger-ui/index.html
<h4>Caso utilizar localmente, alterar o arquivo application.properties: "spring.profiles.active=prod" para "spring.profiles.active=dev"</h4>



<hr>

### Desafio
O objetivo desse desafio é a criação de uma API REST para gerenciamento de Pessoas.

### Requisitos
Segue abaixo o resumo dos requisitos que devem ser desenvolvidos:
* Criar uma pessoa
* Editar uma pessoa;
* Consultar uma pessoa;
* Listar pessoas;
* Criar endereço para pessoa;
* Listar endereços da pessoa;
* Poder informar qual endereço é o principal da pessoa;
* Todas as respostas da API devem ser JSON
* Banco de dados H2

###Diferencial
* Testes;
* Clean Code;

<hr>

##

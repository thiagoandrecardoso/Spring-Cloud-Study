## Preparação do ambiente:
- JDK 11.0.15;
- Docker;
- Apache Maven;
- IntelliJ IDEA;

## Como funciona uma arquitetura de Microservices na prática:

Considere a hipótese de que milhares de clientes estejam enviando requisições para o mesmo microserviço. Essa situação pode potencialmente desencadear uma série de problemas, incluindo:
- **Escalabilidade**: Aumento de carga, tornando-o lento.
- **Concorrência**: Múltiplos clientes fazendo requisições simultâneas podem criar problemas de concorrência. Se o microserviço não for projetado para lidar com múltiplos threads/processos de maneira eficiente, podem ocorrer condições de corrida e inconsistências nos dados.

**Bad Architecture:**

![Pasted image 20230920204356.png](image%2FPasted%20image%2020230920204356.png)


Se tentar resolver criando várias portas, como a 8081, 8082... Você terá que criar várias DNS:
http://empresa.com/api/clientes deixando assim, impossível de ser gerenciado. 

**A arquitetura que iremos abordar nesse curso:**

![Pasted image 20230920212117.png](image%2FPasted%20image%2020230920212117.png)

- **Discovery Server** (Servidor de Descoberta): Ajuda a identificar onde cada serviço está em um sistema distribuído.
- **Gateway**: É responsável por receber todas as solicitações de clientes e encaminhá-las para os serviços apropriados com base nas informações do Discovery Server. Ele atua como um ponto de entrada único para todas as solicitações, simplificando o acesso aos microserviços. 
- **Load Balancer**: Garante que as solicitações dos clientes sejam distribuídas de maneira uniforme entre vários servidores de um mesmo serviço, garantindo um desempenho consistente e evitando sobrecarga em um servidor específico.

Então, resumindo:
- O **Discovery Server** ajuda a encontrar onde estão os serviços.
- O **Gateway** é uma porta de entrada única para acessar esses serviços.
- O **Load Balancer** distribui as solicitações de maneira equilibrada entre os servidores para um melhor desempenho.

## Criação e configuração do Discovery Server

Start Spring:
- Maven Project
- Java 11
- Spring Boot 2.6.4 
- Packaging Jar
- Dependências:
  - Eureka Server

#### application.yml

```yml
spring:  
  application:  
    name: eurekaserver  
  
server:  
  port: 8761  
  
# register-with-eureka como false, para ele não se auto registrar.  
# fetch-registry como false, para ele não buscar os serviços dos microserviços
eureka:  
  client:  
    register-with-eureka: false  
    fetch-registry: false
```

## Criação e configuração do Microserviço Client

Start Spring:
- Maven Project
- Java 11
- Spring Boot 2.6.4 
- Packaging Jar
- Dependências:
  - Spring Web
  - Lombok
  - Spring Boot DevTools
  - H2 database

#### pom.xml
Configurações adicionais - Spring Cloud:

```xml
<dependencyManagement>  
    <dependencies>  
        <dependency>  
            <groupId>org.springframework.cloud</groupId>  
            <artifactId>spring-cloud-dependencies</artifactId>  
            <version>${spring-cloud.version}</version>  
            <type>pom</type>  
            <scope>import</scope>  
        </dependency>  
    </dependencies>  
</dependencyManagement>
```

Nas dependências: Informar que é um cliente Eureka. 

```xml
<dependency>  
    <groupId>org.springframework.cloud</groupId>    
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId></dependency>
```

#### application.yml

```yaml
spring:  
  application:  
    name: msclients  
  
# Quando se coloca a porta 0, ele coloca numa porta aleatória.  
# Devemos fazer isso, pois se subimos várias aplicações, iriam ficar na mesma porta.  
server:  
  port: 0  
  
eureka:  
  client:  
    service-url:  
      defaultZone: http://localhost:8761/eureka  
  instance:  
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}  
# instance-id deve ser o nome mais um valor aleatória, para não ficarem todas as instâncias com o mesmo nome.
```


### Documentação do Microserviço client:
### Visão Geral  
Este microserviço fornece dois endpoints HTTP para criar e recuperar informações de clientes.  

#### Endpoint POST: Criar um Cliente  
Crie um cliente fornecendo os detalhes necessários no corpo da solicitação.  
  
* **URL**: `/clients`  
* **Método**: POST  
* **Tipo de conteúdo**: JSON  
  
```json  
{  
  "nome": "Nome do Cliente",  
  "cpf": "123.456.789-00",  
  "email": "cliente@email.com",  
  "telefone": "123-456-7890"  
}  
```  
Resposta de Sucesso  
* **Código de Status**: 201 Created  
* **Corpo da Resposta(JSON)**: O novo cliente criado com um ID gerado pelo sistema.  
  
```json  
{  
  "id": 1,  
  "nome": "Nome do Cliente",  
  "cpf": "123.456.789-00",  
  "email": "cliente@email.com",  
  "telefone": "123-456-7890"  
}  
```  
Erros Possíveis:  
* **Código de Status**: 400 Bad Request  
  * Corpo de solicitação inválido ou ausente.   
  
#### Endpoint GET: Obter um cliente por CPF  
Recupere as informações de um cliente específico fornecendo o CPF como parâmetro da consulta.  
  
* **URL**: `/clients?cpf={cpf}`  
* **Método**: GET  
* **Tipo de conteúdo**: JSON  
* **Parâmetro da Solicitação**:  
  * `cpf` (String, obrigatório): O CPF do cliente que você deseja recuperar.  
  
Resposta de Sucesso  
* **Código de Sucesso**: 200 OK  
* **Corpo da Resposta(JSON)**: As informações do cliente correspondentes ao CPF fornecido.  
  
```json  
{  
  "id": 1,  
  "nome": "Nome do Cliente",  
  "cpf": "123.456.789-00",  
  "email": "cliente@email.com",  
  "telefone": "123-456-7890"  
}  
```  
Resposta falha  
* **Código de Status**: 404 Not Found  
* **Corpo da Resposta(JSON)**: Nenhuma correspondência encontrada para o CPF fornecido.  
```json  
{  
  "mensagem": "Cliente não encontrado"  
}  
```


## Criação do Cloud Gateway:
#### pom.xml
Configurações adicionais - Spring Cloud:

```xml
<dependency>  
    <groupId>org.springframework.boot</groupId>    
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
<dependency>  
    <groupId>org.springframework.cloud</groupId>    
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>  
    <groupId>org.springframework.cloud</groupId>    
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId></dependency>
```

#### application.yml
```yaml
spring:  
  application:  
    name: mscloudgateway  
  cloud:  
    gateway:  
      discovery:  
        locator:  
          enabled: true  
          lower-case-service-id: true  
  
server:  
  port: 8080  
eureka:  
  client:  
    fetch-registry: true  
    register-with-eureka: true  
    service-url:  
      defaultZone: http://localhost:8761/eureka
```

#### Na classe de aplicação, adicionar as rotas:

```java
@Bean  
public RouteLocator routes(RouteLocatorBuilder builder){  
    return builder  
          .routes()  
             .route(r -> r.path("/clients/**").uri("lb://msclients"))  
          .build();  
  
}
```

#### Observação:
Ordem para subir os serviços:
- Eureka
- Client
- Gateway

#### Load Balance:
Para testar o funcionamento, devemos rodar 3 instancias do cliente. 
Abra o terminal e rode o comando: 

```bash
./mvnw spring-boot:run
```


## Criação e configuração do Microserviço Cartões

### Documentação do Microserviço cartões:
### Visão Geral  
Este microserviço fornece dois endpoints HTTP para criar e recuperar informações dos cartões.  

#### Endpoint POST: Criar um Cartão  
Crie um cartão fornecendo os detalhes necessários no corpo da solicitação.  
  
* **URL**: `/cards`  
* **Método**: POST  
* **Tipo de conteúdo**: JSON  
  
```json  
{  
  "nome": "ITAÚ",  
  "flag": "MASTER CARD",  
  "rent": 10000,  
  "limit": 20000 
}  
```  
Resposta de Sucesso  
* **Código de Status**: 201 Created  
* **Corpo da Resposta(JSON)**: O novo cliente criado com um ID gerado pelo sistema.  
  
```json  
{  
  "id": 1,  
  "nome": "ITAÚ",  
  "flag": "MASTER CARD",  
  "rent": 10000,  
  "limit": 20000 
}  
```  
Erros Possíveis:  
* **Código de Status**: 400 Bad Request  
  * Corpo de solicitação inválido ou ausente.   


#### Endpoint GET: Obter Cartões com renda até um valor informado:  
Este endpoint permite aos utilizadores recuperar uma lista de cartões de crédito com base em um limite máximo de renda informado.
  
* **URL**: `/api/cartoes`
* **Método**: GET  
* **Parâmetro da Solicitação**:  
  *  `renda_maxima` (obrigatório): O valor máximo de renda que os cartões devem ter para serem incluídos na lista. Deve ser um número inteiro ou decimal positivo. 

Exemplo de uso:

```bash
GET /api/cards?rent=5000
```

  
Resposta de Sucesso  
* **Código de Sucesso**: 200 OK  
* **Corpo da Resposta(JSON)**: As informações do cliente correspondentes ao CPF fornecido.  

**Resposta**:

```json  
{
   "cards":[
      {
         "id":1,
         "name":"ITAÚ",
         "flag":"MASTER CARD",
         "rent":10000,
         "limit":20000
      },
      {
         "id":2,
         "name":"CAIXA",
         "flag":"CIELO",
         "rent":10000,
         "limit":60000
      },
      {
         "id":3,
         "name":"INTER",
         "flag":"VISA",
         "rent":40000,
         "limit":200000
      }
   ]
}
```

**Códigos de Resposta HTTP**:

- 200 OK: A solicitação foi bem-sucedida, e os cartões foram retornados no corpo da resposta.
- 400 Bad Request: A solicitação possui parâmetros inválidos, como um valor de renda máximo não numérico ou negativo.
- 500 Internal Server Error: Ocorreu um erro interno no servidor ao processar a solicitação.


#### Endpoint GET: Buscar Cartões pelo CPF do Cliente
Este endpoint permite aos utilizadores buscar cartões de crédito associados a um cliente específico com base no CPF e retornar informações como nome, bandeira e limite do cartão.

- **URL**: `/api/cards`
- **Método HTTP**: GET
- **Parâmetros de Consulta**:
    - `cpf` (obrigatório): O número de CPF do cliente para o qual os cartões devem ser encontrados. Deve ser uma string no formato XXX.XXX.XXX-XX.

Exemplo de uso:

```bash
GET /api/cards?cpf=123.456.789-00
``` 

Resposta:

```json
{
  "cards": [
    {
      "name": "Cartão Ouro",
      "flag": "MasterCard",
      "limit": 10000
    },
    {
      "name": "Cartão Prata",
      "flag": "Visa",
      "limit": 5000
    }
  ]
}
```

**Códigos de Resposta HTTP**:

- 200 OK: A solicitação foi bem-sucedida, e os cartões associados ao cliente foram retornados no corpo da resposta.
- 404 Not Found: Não foram encontrados cartões associados ao CPF fornecido.
- 400 Bad Request: A solicitação possui um formato de CPF inválido.

## Criação e configuração do Microserviço Avaliador de Crédito

### Documentação do Microserviço Avaliador de cartões:
### Visão Geral  
Este microserviço fornece três endpoints HTTP. 
#### Endpoint GET: Consultar situação do cliente 
Este endpoint permite consultar a situação de um cliente com base no CPF fornecido e retorna os dados do cartão e do cliente.
  
* **URL**: `/credit-assessor/client-situation`
* **Método**: GET  
* **Tipo de conteúdo**: JSON  

**Parâmetros:**
- `cpf` (‘String’, parâmetro de consulta): O CPF do cliente que você deseja consultar.

**Resposta de Sucesso:**
- **Código:** 200 OK
- **Exemplo de Resposta:**
  
```json  
{
    "cliente": {
        "cpf": "123.456.789-00",
        "name": "João da Silva",
        "age": 35
    },
    "cartao": {
        "name": "João da Silva",
        "flagCard": "Visa",
        "rent": 1000.00,
        "limitRent": 5000.00
    }
}  
```

**Resposta de Erro:**

- **Código:** 404 Not Found
- **Exemplo de Resposta:**
```json
{
    "mensagem": "Cliente não encontrado."
}
```

- **Código:** 500 Internal Server Error (ou outro código de status conforme apropriado)
- **Exemplo de Resposta:**
```json
{
    "mensagem": "Erro na comunicação com os microserviços."
}
```

**Exemplo de Uso:**
```http
GET /api/client-situation?cpf=12345678900
```

#### Endpoint POST: Executar Avaliações
Este endpoint permite executar avaliações com base nos dados fornecidos e retorna o feedback do cliente.

* **URL**: `/credit-assessor`
* **Método**: POST
* **Tipo de conteúdo**: JSON

**Corpo da Solicitação:**
- `cpf` (‘String’): O CPF do cliente para avaliação.
- `rent` (Double): O valor da renda a ser avaliado.

**Resposta de Sucesso:**
- **Código:** 200 OK
- **Exemplo de Resposta:**
```json
{
  "feedback": [
    {
      "cardName": "Cartão Ouro",
      "flagCard": "MasterCard",
      "rent": 1000.00,
      "limitRent": 5000.00
    },
    {
      "cardName": "Cartão Prata",
      "flagCard": "Visa",
      "rent": 800.00,
      "limitRent": 3000.00
    }
  ]
}
```

**Resposta de Erro:**

- **Código:** 500 Internal Server Error (ou outro código de status conforme apropriado)
- **Exemplo de Resposta:**
```json
{
    "mensagem": "Erro na comunicação com os microserviços."
}
```

**Exemplo de Uso:**
```http
POST /credit-assessor
Content-Type: application/json
{
    "cpf": "123.456.789-00",
    "rent": 1000.00
}
```

#### Endpoint POST: Solicita cartão (WIP)
Este endpoint permite solicitar a emissão de um cartão com base nos dados fornecidos e retorna o protocolo de solicitação.

* **URL**: `/credit-assessor/request-card`
* **Método**: POST
* **Tipo de conteúdo**: JSON

**Parâmetros:**
- `idCard` (Long): O identificador único do cartão.
- `cpf` (String): O CPF do cliente para o qual o cartão será solicitado.
- `address` (String): O endereço do cliente para envio do cartão.
- `limitFree` (BigDecimal): O limite de crédito desejado para o cartão.

**Resposta de Sucesso:**
- **Código:** 200 OK
- **Exemplo de Resposta:**

```json
{
  "protocol": "1a2b3c4d-5e6f-7g8h-9i10j11k12l"
} 
```

**Resposta de Erro:**

- **Código:** 500 Internal Server Error
- **Exemplo de Resposta:**

```json
{
  "mensagem": "Erro na solicitação de emissão do cartão."
}
```

**Exemplo de Uso:**
```http
POST /credit-assessor/request-card
Content-Type: application/json

{
    "idCard": 123456,
    "cpf": "123.456.789-00",
    "address": "Rua Exemplo, 123",
    "limitFree": 2000.00
}
```

### Spring Cloud Feign

O Spring Cloud Feign é uma biblioteca do ecossistema Spring Cloud que simplifica a comunicação entre microserviços, permitindo que você faça chamadas a serviços RESTful de forma declarativa, sem a necessidade de escrever código de chamada HTTP manualmente. Isso ajuda a simplificar e agilizar o desenvolvimento de microserviços em um ambiente de nuvem.

[Open Feign](https://spring.io/projects/spring-cloud-openfeign)

#### Dependência:

```xml
<dependency>  
    <groupId>org.springframework.cloud</groupId>    
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### Adicionar @notações: 
- @EnableFeignClients no Application

```java
@EnableEurekaClient  
@SpringBootApplication  
@EnableFeignClients  
public class MscreditassessorApplication {  
    public static void main(String[] args) {  
        SpringApplication.run(MscreditassessorApplication.class, args);  
    }  
}
```

### Serviço de Mensageria:

![SCR-20231022-nzrz.png](image%2FSCR-20231022-nzrz.png)


#### Instalando o RabbitMQ:
Site: [rabbitMQ](https://www.rabbitmq.com)

##### Instalando via Docker:

```shell
# latest RabbitMQ 3.12
docker run -it --name creditassessorrabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.9-management
```

##### Acessando:
Após instalado, acessar: http://localhost:15672
Entrar com usuário e senha padrão: user: guest | senha: guest

##### Criando o Subscriber para a fila de emissão de cartões:

Adicionar no pom.xml do projeto `Card` a dependência do amqp:

```xml
<dependency>  
    <groupId>org.springframework.boot</groupId>    
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

Adicionar nova fila no RabbitMQ (http://localhost:15672)
- Queues
  - Add a new queue
  - Coloque apenas o nome da fila: issuing-cards
  - Add queue

No application.yml adicionar as seguintes linhas:

```yml
mq:  
  queues:  
    issuing-cards: issuing-cards
```

Na Application:
Adicionar a anotação `@EnableRabbit` 

```java
import org.springframework.amqp.rabbit.annotation.EnableRabbit;  
import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.SpringBootApplication;  
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;  
  
@EnableEurekaClient  
@SpringBootApplication  
@EnableRabbit  
public class CardApplication {  
    public static void main(String[] args) {  
        SpringApplication.run(CardApplication.class, args);  
    }  
}
```

Criar dentro do pacote infra ou client a Classe que irá conectar com a fila do RabbitMQ.
Essa classe deverá ser um `@Componet` 

O método que irá escutar a fila, deverá ser anotado com `@RabbitListener` e devemos repassar qual fila ele deverá escutar.  Abaixo um exemplo:

```java
@RabbitListener(queues = "${mq.queues.issuing-cards}")
public void receivingCardIssueRequest(@Payload String payload){  
    System.out.println(payload);  
}
```

Para informar qual o endereço da fila que iremos escutar, devemos adicionar as seguintes configurações do **rabbitmq** no `application.yml`

```yml
spring:  
  application:  
    name: mscards  
  rabbitmq:  
    host: localhost  
    port: 5672  
    username: guest  
    password: guest
```

Rode o eureka e o serviço de cartões, e no RabbitMQ vá em:
- Queue e click no que você criou;
- Publish message;
- Escreva uma mensagem no Payload e confira se apareceu no console da IDE. 


#### Criando endpoint para requisição de MS de cartões no Avaliador de Cartões:
Link do commit: [github](https://github.com/thiagoandrecardoso/Spring-Cloud-Study/commit/91a232e9cc4e155c568ed43e9fb9013d886521aa)


Adicionar no pom.xml do projeto `mscreditassessor` a dependência do amqp:

```xml
<dependency>  
    <groupId>org.springframework.boot</groupId>    
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

No arquivo `application.yml` adicionar as mesmas configurações que foram colocadas no MS de cartões.

```yml
spring:  
  application:  
    name: mscards  
  rabbitmq:  
    host: localhost  
    port: 5672  
    username: guest  
    password: guest

mq:
  queues:
    issuing-cards: issuing-cards
```

Na classe `com.example.card.infra.mqueue.IssuingCardSubscriber` é onde iremos deserializar o payload com a mensagem enviada pelo MS de cartões e salvar esses dados em `ClientCardRepository`. 

Adicionar a anotação `@EnableRabbit` na classe de aplicação `MscreditassessorApplication`. 

Criar a classe: `com.example.mscreditassessor.infra.mqueue.RequestPublisherCard`:
Essa classe, chamada "RequestPublisherCard," é um componente de publicação em um sistema que utiliza o RabbitMQ para enviar mensagens. Ela tem a responsabilidade de converter um objeto do tipo "CardIssueData" em formato JSON e publicá-lo em uma fila do RabbitMQ usando um "RabbitTemplate." Essencialmente, ela encaminha os dados de emissão de cartão (cardIssueData) como mensagens JSON para a fila especificada no RabbitMQ.

### Segurança - Keycloak:
https://www.keycloak.org

#### Instalação via Docker: 
https://www.keycloak.org/getting-started/getting-started-docker

- Comando para instalar via Docker:
```shell
docker run --name keycloak -p 8081:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:22.0.5 start-dev
```
- Acessar online pelo link: http://localhost:8081
  - Para fazer ‘login’, click em Administration Console e utilize o ‘login’ e senha configurados no comando acima: admim : admim.

- Criar um REALM:
  - http://localhost:8081/admin/master/console/#/master/add-realm
  - Adicione um nome: mscourserealm
  - Clicar em CREATE

![img.png](image/img.png) 

- Criar um Client: 
  - http://localhost:8081/admin/master/console/#/mscourserealm/clients/add-client

![img.png](image/createclient.png)

![img.png](image/mscreditKC.png)

![img_1.png](image/accesssettings.png)

![img_2.png](image/copability.png)

![img_3.png](image/logingsettings.png)

- Clique em Salvar / Criar

#### Configurando o Api Gateway
Adicionar a dependência:

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```
No arquivo `application.yml` adicionar as mesmas configurações:

Acesse: [Realm Settings](http://localhost:8081/admin/master/console/#/mscourserealm/realm-settings)
Clique em: **OpenID Endpoint Configuration** [link](http://localhost:8081/realms/mscourserealm/.well-known/openid-configuration)
Pegue a URL do campo "issuer": "http://localhost:8081/realms/mscourserealm" e cole no `issue-uri` de security

```yaml
spring:
  application:
    name: mscloudgateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8081/realms/mscourserealm
```

Configurar o cliente com o token:
Marque e selecione o que tiver sendo apontado pelas setas **vermelhas** e preencha o destaque em **amarelo** com a seguinte
informação:
Vá novamente em: **OpenID Endpoint Configuration**
Cole no campo destacado em **amarelo** a URL da linha: 
"token_endpoint": "http://localhost:8081/realms/mscourserealm/protocol/openid-connect/token"

![img_4.png](image/insomiaoauth2.png)

No campo CLIENT ID, cole o nome do seu clien id que você configurou no keycloak.

![img_5.png](image/clientidKC.png)

Em CLIENT SECRET, cole o Secret que está no keycloak.

![img_6.png](image/credentials.png)

Resultado:

![img_7.png](image/insomiatoken.png)

Vá em https://jwt.io e cole o seu Token gerado. 

Usando o ‘Token’ na requisição:

![img_8.png](image/addtokeninsomia.png)


## OpenAPI: Gettting Started
https://springdoc.org/

Access: http://localhost:8080/swagger-ui/index.html
trocar 8080 pela porta atual do ms. 

#### Dependência:
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-ui</artifactId>
  <version>1.6.14</version>
</dependency>
```

## Docker:

### Construção do Artefato
Primeiro, você precisa construir o artefato do seu projeto, que é um arquivo JAR executável. No diretório raiz do seu projeto, execute o seguinte comando Maven para limpar quaisquer builds anteriores e construir o artefato, pulando os testes para agilizar o processo:

```shell
./mvnw clean package -DskipTests
```

Esse comando gera o arquivo JAR executável dentro do diretório target do seu projeto.

### Executando o Artefato (Opcional)
Para testar se o artefato foi construído com sucesso, você pode executar o arquivo JAR diretamente:
```shell
cd target
java -jar eurekaserver-0.0.1-SNAPSHOT.jar
```
Isso iniciará o servidor Eureka localmente. Certifique-se de que tudo está funcionando conforme esperado antes de prosseguir para a próxima etapa.

### Criando o Dockerfile
Volte ao diretório raiz do seu projeto e crie um arquivo chamado Dockerfile (sem extensão). Esse arquivo contém as instruções para construir a imagem Docker do seu servidor Eureka. Adicione o seguinte conteúdo ao Dockerfile:
https://hub.docker.com/_/openjdk 

```dockerfile
# Use a imagem base oficial do OpenJDK
FROM openjdk:11
# Define o diretório de trabalho dentro do contêiner
WORKDIR /app
# Copia o arquivo JAR do servidor Eureka para o contêiner
COPY ./target/eurekaserver-0.0.1-SNAPSHOT.jar app.jar
# Expõe a porta 8761 no contêiner para permitir o acesso ao servidor Eureka
EXPOSE 8761
# Define o comando para iniciar o servidor Eureka
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Construindo a Imagem Docker
No terminal, no diretório raiz do seu projeto, execute o seguinte comando para construir a imagem Docker do seu servidor Eureka:
```shell
docker build --tag eureka-server .
```
Este comando lê o Dockerfile no diretório atual e constrói uma imagem Docker chamada eureka-server.

### Executando o Contêiner Docker
Após a construção da imagem, você pode iniciar um contêiner com base nessa imagem usando o seguinte comando:
```shell
docker run --name eureka-server -p 8761:8761 eureka-server
```
Este comando inicia um contêiner chamado eureka-server, mapeando a porta 8761 do contêiner para a porta 8761 do host, permitindo que você acesse o servidor Eureka através da porta 8761 do seu host.



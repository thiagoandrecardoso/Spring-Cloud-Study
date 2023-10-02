# Spring Cloud

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


<img width="817" alt="Pasted image 20230920204356" src="https://github.com/thiagoandrecardoso/Spring-Cloud-Study/assets/33556919/2f751724-6d17-4f47-ad17-4a4f9aa1efc1">


Se tentar resolver criando várias portas, como a 8081, 8082... Você terá que criar várias DNS:
http://empresa.com/api/clientes deixando assim, impossível de ser gerenciado. 

**A arquitetura que iremos abordar nesse curso:**

<img width="963" alt="Pasted image 20230920212117" src="https://github.com/thiagoandrecardoso/Spring-Cloud-Study/assets/33556919/decaae20-eac0-4c89-b7c5-0c1ac20882e6">


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
  
# Quando se coloca a porta 0, ele coloca em uma porta aleatória.  
# Devemos fazer isso, pois se subimos varias aplicações, iriam ficar na mesma porta.  
server:  
  port: 0  
  
eureka:  
  client:  
    service-url:  
      defaultZone: http://localhost:8761/eureka  
  instance:  
    instance-id: ${spring.application.name}:${spring.application.instance_id:${random.value}}  
# instance-id deve ser o nome mais um valor aleatória, para não ficarem todas as instancias com o mesmo nome.
```


### Documentação do Microserviço client:
### Visão Geral  
Este microserviço fornece dois endpoints HTTP para criar e recuperar informações de clientes.  

#### Endpoint POST: Criar um Cliente  
Crie um novo cliente fornecendo os detalhes necessários no corpo da solicitação.  
  
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
Crie um novo cartão fornecendo os detalhes necessários no corpo da solicitação.  
  
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
Este endpoint permite aos usuários recuperar uma lista de cartões de crédito com base em um limite máximo de renda informado.
  
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
Este endpoint permite aos usuários buscar cartões de crédito associados a um cliente específico com base no CPF e retornar informações como nome, bandeira e limite do cartão.

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

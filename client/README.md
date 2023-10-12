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




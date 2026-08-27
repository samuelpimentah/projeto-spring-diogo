# API de Contratação de Funcionários

Backend do desafio de gerenciamento de candidatos do processo de contratação, desenvolvido como uma API REST em Spring Boot.

## Tecnologias

- Java 17
- Spring Boot 3.5.5
- Spring MVC
- Bean Validation
- Maven
- `ArrayList` em memória

Não são utilizados Lombok, banco de dados ou Spring Security.

## Como executar

Na raiz do monorepo, entre no diretório do backend:

```bash
cd backend
./mvnw spring-boot:run
```

No Windows PowerShell também é possível executar:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

A API estará disponível em `http://localhost:8080`. Ao iniciar, quatro funcionários fictícios são cadastrados automaticamente, um em cada status.

A documentação interativa dos endpoints fica disponível no Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

A especificação OpenAPI em JSON pode ser consultada em `http://localhost:8080/v3/api-docs`.

> **Importante:** todos os dados ficam somente em uma `ArrayList`. Eles são perdidos sempre que a aplicação é reiniciada. Esse comportamento é esperado nesta etapa e não é um erro.

## Endpoints

| Método | Caminho | Objetivo | Sucesso | Erros |
| --- | --- | --- | --- | --- |
| POST | `/funcionarios` | Cadastrar funcionário | 201 | 400 |
| GET | `/funcionarios` | Listar todos ou filtrar | 200 | - |
| GET | `/funcionarios/{id}` | Consultar por ID | 200 | 404 |
| PUT | `/funcionarios/{id}` | Atualizar todos os dados | 200 | 400, 404 |
| PATCH | `/funcionarios/{id}` | Atualizar somente os campos enviados | 200 | 400, 404 |
| DELETE | `/funcionarios/{id}` | Excluir funcionário | 200 | 404 |
| GET | `/funcionarios/indicadores` | Consultar totais por status | 200 | - |

Os filtros `nome`, `cargo` e `status` podem ser combinados no `GET /funcionarios`. Nome e cargo usam busca por trecho, sem diferenciar letras maiúsculas de minúsculas. Os filtros combinados usam a regra **AND**, ou seja, o funcionário precisa atender a todos eles.

## Exemplos de uso

### Cadastrar funcionário

`POST /funcionarios`

Requisição:

```json
{
  "nome": "Eduarda Rocha",
  "email": "eduarda.rocha@email.com",
  "telefone": "11955550000",
  "cargo": "Analista de Dados",
  "departamento": "Tecnologia",
  "salario": 4500.00,
  "cidade": "São Paulo"
}
```

Resposta `201 Created`:

```json
{
  "id": 5,
  "nome": "Eduarda Rocha",
  "email": "eduarda.rocha@email.com",
  "telefone": "11955550000",
  "cargo": "Analista de Dados",
  "departamento": "Tecnologia",
  "salario": 4500.0,
  "cidade": "São Paulo",
  "status": "EM_ANALISE"
}
```

O `id` é sempre gerado pelo servidor. Se o cliente enviar um `id`, ele será ignorado. Quando `status` não é informado no cadastro, o valor utilizado é `EM_ANALISE`.

### Listar todos

`GET /funcionarios`

Resposta `200 OK`:

```json
[
  {
    "id": 1,
    "nome": "Ana Souza",
    "email": "ana.souza@email.com",
    "telefone": "11999990000",
    "cargo": "Analista de Dados",
    "departamento": "Tecnologia",
    "salario": 4500.0,
    "cidade": "São Paulo",
    "status": "EM_ANALISE"
  }
]
```

A resposta real inicial contém os quatro funcionários fictícios.

### Listar com filtros

`GET /funcionarios?nome=ana&cargo=dados&status=EM_ANALISE`

Resposta `200 OK`:

```json
[
  {
    "id": 1,
    "nome": "Ana Souza",
    "email": "ana.souza@email.com",
    "telefone": "11999990000",
    "cargo": "Analista de Dados",
    "departamento": "Tecnologia",
    "salario": 4500.0,
    "cidade": "São Paulo",
    "status": "EM_ANALISE"
  }
]
```

### Consultar por ID

`GET /funcionarios/1`

Resposta `200 OK`:

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana.souza@email.com",
  "telefone": "11999990000",
  "cargo": "Analista de Dados",
  "departamento": "Tecnologia",
  "salario": 4500.0,
  "cidade": "São Paulo",
  "status": "EM_ANALISE"
}
```

Quando o ID não existe, a resposta é `404 Not Found`:

```json
{
  "mensagem": "Funcionário com id 99 não encontrado."
}
```

### Atualizar completamente

`PUT /funcionarios/1`

Requisição:

```json
{
  "nome": "Ana Souza Ferreira",
  "email": "ana.ferreira@email.com",
  "telefone": "11911112222",
  "cargo": "Analista de Dados Sênior",
  "departamento": "Tecnologia",
  "salario": 6200.00,
  "cidade": "São Paulo",
  "status": "APROVADO"
}
```

Resposta `200 OK`:

```json
{
  "id": 1,
  "nome": "Ana Souza Ferreira",
  "email": "ana.ferreira@email.com",
  "telefone": "11911112222",
  "cargo": "Analista de Dados Sênior",
  "departamento": "Tecnologia",
  "salario": 6200.0,
  "cidade": "São Paulo",
  "status": "APROVADO"
}
```

### Atualizar parcialmente

`PATCH /funcionarios/1`

Requisição:

```json
{
  "status": "CONTRATADO",
  "salario": 6500.00
}
```

Resposta `200 OK`:

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana.souza@email.com",
  "telefone": "11999990000",
  "cargo": "Analista de Dados",
  "departamento": "Tecnologia",
  "salario": 6500.0,
  "cidade": "São Paulo",
  "status": "CONTRATADO"
}
```

Os campos que não aparecem na requisição permanecem inalterados.

### Excluir funcionário

`DELETE /funcionarios/1`

Resposta `200 OK`:

```json
{
  "mensagem": "Funcionário com id 1 excluído com sucesso."
}
```

### Consultar indicadores

`GET /funcionarios/indicadores`

Resposta inicial `200 OK`:

```json
{
  "total": 4,
  "emAnalise": 1,
  "aprovados": 1,
  "reprovados": 1,
  "contratados": 1
}
```

## Exemplo de erro de validação

Uma tentativa de cadastro com nome vazio e e-mail inválido devolve `400 Bad Request`:

```json
{
  "mensagem": "Erro de validação",
  "erros": {
    "nome": "não pode estar em branco",
    "email": "deve ser um e-mail válido"
  }
}
```

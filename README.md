# bank-api

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=crqcastro_bank-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=crqcastro_bank-api)

API REST para banco digital com movimentacoes financeiras, gestao de clientes e contas, e notificacoes assincronas.

## Stack

| Tecnologia | Versao | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.4.x | Framework web e DI |
| PostgreSQL | 16 | Banco de dados relacional |
| RabbitMQ | 3.13 | Mensageria para notificacoes assincronas |
| Flyway | -- | Migracoes de banco de dados |
| Springdoc/OpenAPI | 2.7 | Documentacao automatica da API |
| Micrometer + Prometheus | -- | Metricas |
| OpenTelemetry | 1.44 | Rastreamento distribuido |
| Testcontainers | 1.20 | Testes de integracao com containers reais |
| ArchUnit | 1.3 | Testes de arquitetura |

## Decisoes Arquiteturais

### Arquitetura Hexagonal (Ports & Adapters)

O projeto segue a arquitetura hexagonal para garantir:
- **Dominio isolado**: entidades de negocio sem dependencias de framework
- **Testabilidade**: use cases testaveis sem Spring, sem banco de dados
- **Substituibilidade**: trocar PostgreSQL ou RabbitMQ sem alterar regras de negocio

```
domain/model/          -> Entidades JPA, enums, events
application/port/in/   -> Interfaces de use case
application/port/out/  -> Interfaces de saida
application/usecase/   -> Implementacoes das regras de negocio
adapter/in/web/        -> Controllers REST
adapter/out/           -> Persistencia JPA + mensageria RabbitMQ
infrastructure/        -> Config Spring, constantes, tratamento de erros
```

### Estrategia de Concorrencia

- **Deposito / Saque**: Optimistic Locking (@Version na entidade Account)
- **Transferencia**: Pessimistic Write Locking (SELECT FOR UPDATE) com ordem deterministica de UUID para evitar deadlock

### Tipos monetarios

NUMERIC(19,4) no PostgreSQL e BigDecimal em todo o Java. Nunca double para valores financeiros.

### Notificacoes

Assincronas via RabbitMQ. A API publica eventos no exchange bank.notifications. Dead-letter queue configurada.

## Como Executar

### Pre-requisitos
- Docker e Docker Compose instalados

### Subir o ambiente completo

```bash
mvn package -DskipTests
docker compose up --build -d
```

### Executar localmente (sem Docker para a API)

```bash
docker compose up postgres rabbitmq -d
mvn spring-boot:run
```

### Postman

A pasta `postman/` contém a collection e o environment prontos para uso:

| Arquivo | Descricao |
|---|---|
| `postman/bank-api.postman_collection.json` | Todos os endpoints organizados por recurso |
| `postman/bank-api-local.postman_environment.json` | Environment apontando para `localhost:8080` |

Para importar: abra o Postman → **Import** → selecione os dois arquivos.


### Popular o banco com dados de exemplo

O script `scripts/seed.sh` cria 5 clientes, 6 contas e realiza depositos, saques e transferencias via API.

```bash
# Ambiente local (padrao: localhost:8080)
./scripts/seed.sh

# Ambiente customizado
./scripts/seed.sh http://meu-servidor:8080
```

O script pode ser executado multiplas vezes — os documentos e e-mails sao gerados com timestamp unico a cada execucao.


## URLs

| Servico | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| API Docs (JSON) | http://localhost:8080/api-docs |
| Actuator Health | http://localhost:8080/actuator/health |
| Prometheus Metrics | http://localhost:8080/actuator/prometheus |
| RabbitMQ Management | http://localhost:15672 (bankapi/bankapi) |
| Grafana | http://localhost:3000 (admin/admin) |
| Prometheus | http://localhost:9090 |
| MailHog (e-mails) | http://localhost:8025 |

## Notificacoes

Apos cada transacao (deposito, saque ou transferencia) a API publica um evento no RabbitMQ.
Um consumer integrado le a mensagem e envia um e-mail HTML ao titular da conta.

| Campo | Valor |
|---|---|
| Remetente | `notification@bank-api.com.br` |
| Exchange | `bank.notifications` |
| Fila | `bank.notifications.transactions` |
| Dead-letter | `bank.notifications.transactions.dlq` |

### Visualizar e-mails em desenvolvimento

Em ambiente local/Docker o [MailHog](http://localhost:8025) captura todos os e-mails enviados sem entrega-los de fato.

```bash
# Subir o MailHog junto com o resto do stack
docker compose up -d

# Acessar a caixa de entrada
open http://localhost:8025
```

## Qualidade de Codigo

### Checkstyle

O projeto utiliza o plugin `maven-checkstyle-plugin` (v3.3.1) integrado a fase `validate`, ou seja, o build falha imediatamente se alguma violacao for encontrada.

A configuracao fica em `config/checkstyle/` e segue o mesmo padrao do projeto interno `preply/backend`.

```bash
# Executar apenas o checkstyle
mvn checkstyle:check
```

#### Regras ativas

| Categoria | Regra | Detalhe |
|---|---|---|
| Importacoes | `AvoidStarImport` | Proibido `import foo.*` |
| Importacoes | `IllegalImport` | Proibido `junit`, `org.hamcrest` e `java.util.logging.Logger` |
| Importacoes | `RedundantImport` | Importacoes duplicadas nao sao permitidas |
| Importacoes | `UnusedImports` | Importacoes nao utilizadas nao sao permitidas |
| Nomenclatura | `PackageName` | Apenas letras minusculas e pontos |
| Nomenclatura | `TypeName` | Classes e interfaces em `PascalCase` |
| Nomenclatura | `MethodName` | Metodos em `camelCase` |
| Nomenclatura | `ConstantName` | Constantes (`static final`) em `UPPER_SNAKE_CASE` |
| Formatacao | `Indentation` | 4 espacos (sem tab) |
| Formatacao | `FileTabCharacter` | Caractere tab proibido em qualquer arquivo |
| Formatacao | `LineLength` | Maximo 136 caracteres por linha |
| Formatacao | `NeedBraces` | Todo `if`/`for`/`while` deve usar `{}` |
| Formatacao | `EmptyBlock` | Blocos vazios proibidos |
| Formatacao | `LeftCurly` | `{` deve estar na mesma linha da declaracao |
| Formatacao | `WhitespaceAround` | Espacos ao redor de operadores e `{}` |
| Formatacao | `WhitespaceAfter` | Espaco apos virgulas e ponto-e-virgulas |
| Formatacao | `NoWhitespaceBefore` | Sem espaco antes de `;` e `,` |
| Formatacao | `NoWhitespaceAfter` | Sem espaco apos `-` unario, `!`, `~` |
| Formatacao | `ParenPad` | Sem espaco dentro de `()` |
| Formatacao | `MethodParamPad` | Sem espaco entre nome do metodo e `(` |
| Formatacao | `ArrayTypeStyle` | `String[]` em vez de `String args[]` |
| Modificadores | `ModifierOrder` | Ordem padrao Java: `public static final ...` |
| Modificadores | `RedundantModifier` | Sem `public abstract` em interfaces |
| Boas praticas | `EqualsHashCode` | `equals` e `hashCode` devem ser implementados juntos |
| Boas praticas | `MultipleVariableDeclarations` | Uma variavel por declaracao |
| Boas praticas | `UnnecessaryParentheses` | Parenteses redundantes proibidos |
| Boas praticas | `DefaultComesLast` | `default` deve ser o ultimo caso no `switch` |
| Boas praticas | `SuperClone` / `SuperFinalize` | `clone()` e `finalize()` devem chamar `super` |
| Complexidade | `CyclomaticComplexity` | Maximo 14 por metodo |
| Complexidade | `NPathComplexity` | Maximo 200 por metodo (padrao) |
| Complexidade | `ClassFanOutComplexity` | Maximo 30 dependencias por classe |

#### Excecoes configuradas

| Escopo | Regra suspensa | Motivo |
|---|---|---|
| `src/test/java/**` | `IllegalImport` para `junit` e `org.junit.*` | JUnit e necessario nos testes |
| `src/test/java/**` | `AvoidStarImport` para `org.assertj.*` e `org.mockito.*` | Star imports do AssertJ e Mockito sao idiomaticos em testes |
| `domain/**` | `AvoidStarImport` para `jakarta.persistence.*` e `lombok.*` | Convencional em entidades JPA com Lombok |
| Qualquer arquivo | `WhitespaceAround` para `{` e `}` | Corpos de records e classes vazios `{}` sao Java valido |
| `architecture/**` | `ConstantName` | Campos `@ArchTest static final ArchRule` usam `snake_case` por convencao do ArchUnit |
| `TransactionController.java` | `ClassFanOutComplexity` | Agrega 4 portas de use case por design; fan-out e estruturalmente inevitavel |
| `target/**` | Todas | Codigo gerado nao e verificado |

## Executar Testes

```bash
# Todos os testes com relatorio de cobertura
mvn verify

# Ver relatorio JaCoCo
open target/site/jacoco/index.html
```

## Endpoints

### Clientes /api/v1/customers
| Metodo | Path | Descricao |
|---|---|---|
| POST | /customers | Criar cliente |
| GET | /customers/{id} | Buscar por ID |
| GET | /customers | Listar (paginado) |
| PATCH | /customers/{id} | Atualizar dados |
| DELETE | /customers/{id}/deactivate | Desativacao logica |
| DELETE | /customers/{id} | Exclusao permanente |

### Contas /api/v1/accounts
| Metodo | Path | Descricao |
|---|---|---|
| POST | /accounts | Criar conta |
| GET | /accounts/{id} | Buscar por ID |
| GET | /accounts?customerId=... | Listar por cliente |
| PATCH | /accounts/{id}/inactivate | Inativar |
| PATCH | /accounts/{id}/block | Bloquear |
| PATCH | /accounts/{id}/unblock | Desbloquear |
| DELETE | /accounts/{id} | Excluir |

### Transacoes /api/v1/transactions
| Metodo | Path | Descricao |
|---|---|---|
| POST | /transactions/deposit | Deposito |
| POST | /transactions/withdraw | Saque |
| POST | /transactions/transfer | Transferencia |
| GET | /transactions/{id} | Buscar transacao |
| GET | /transactions?accountId=... | Extrato paginado |

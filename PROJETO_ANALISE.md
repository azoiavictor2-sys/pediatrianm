# 📋 Análise Completa do Projeto Ambulato

> Documento gerado em 22/03/2026. Contém descrição do projeto, estrutura de pastas, erros encontrados e sugestões de melhoria.

---

## 🏥 Sobre o Projeto

O **Ambulato** é uma API REST desenvolvida em **Java com Spring Boot** para auxiliar acadêmicos de medicina (alunos da turma T16 - FEMA) no gerenciamento de **prontuários médicos pediátricos**. O sistema permite que cada aluno faça login, registre atendimentos e visualize seus próprios prontuários.

**Tecnologias utilizadas:**
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.4 |
| Spring Data JPA | (incluso no Boot) |
| Banco de dados | H2 (arquivo local) |
| Build | Maven |

---

## 📁 Estrutura do Projeto

```
ambulato/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties       # Configuração da versão do Maven
├── src/
│   ├── main/
│   │   ├── java/com/fema/ambulato/
│   │   │   ├── AmbulatoApplication.java   # Classe principal - ponto de entrada da aplicação
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java    # Endpoints de autenticação (login)
│   │   │   │   └── ProntuarioController.java # Endpoints de prontuários (CRUD)
│   │   │   ├── model/
│   │   │   │   ├── Usuario.java           # Entidade JPA de usuário/aluno
│   │   │   │   └── Prontuario.java        # Entidade JPA de prontuário clínico
│   │   │   └── repository/
│   │   │       ├── UsuarioRepository.java # Interface de acesso ao banco para usuários
│   │   │       └── ProntuarioRepository.java # Interface de acesso ao banco para prontuários
│   │   └── resources/
│   │       └── application.properties    # Configurações de banco de dados e JPA
│   └── test/
│       └── java/com/fema/ambulato/
│           └── AmbulatoApplicationTests.java # Teste de carregamento do contexto Spring
├── dados_ambulato.mv.db                  # Arquivo do banco de dados H2 (persistido em disco)
├── pom.xml                               # Dependências e configurações do Maven
└── PROJETO_ANALISE.md                    # Este documento
```

---

## 🔗 Endpoints da API

### Autenticação (`/api/auth`)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/auth/login` | Realiza login com username (CPF) e senha |

**Corpo do request (JSON):**
```json
{
  "username": "48473011880",
  "senha": "haloHAL2"
}
```

### Prontuários (`/api/prontuarios`)
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/prontuarios/salvar` | Cria/salva um novo prontuário |
| `GET` | `/api/prontuarios/listar/{nomeDoAluno}` | Lista prontuários de um aluno |
| `DELETE` | `/api/prontuarios/deletar/{id}` | Exclui um prontuário por ID |

---

## 🗃️ Modelos de Dados

### `Usuario`
| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Chave primária (auto gerada) |
| `username` | String | CPF do aluno (usado como login) |
| `senha` | String | Senha em texto puro |
| `nomeCompleto` | String | Nome completo do aluno |
| `turma` | String | Turma do aluno (ex: T16) |

### `Prontuario`
| Campo | Tipo | Descrição |
|---|---|---|
| `id` | Long | Chave primária (auto gerada) |
| `pacienteNome` | String | Nome do paciente |
| `celularContato` | String | Telefone de contato |
| `tipagemSanguinea` | String | Tipo sanguíneo |
| `dnpmAndarQuando` | String | DNPM - quando andou |
| `dnpmFalaQuando` | String | DNPM - quando falou |
| `dnpmDesfraldeQuando` | String | DNPM - quando desfralhou |
| `subjetivo` | TEXT | Queixa principal (SOAP - S) |
| `objetivo` | TEXT | Dados objetivos do exame (SOAP - O) |
| `avaliacao` | TEXT | Avaliação clínica (SOAP - A) |
| `plano` | TEXT | Plano terapêutico (SOAP - P) |
| `nomeAluno` | String | Nome do aluno que registrou |
| `turma` | String | Turma do aluno |
| `dataAtendimento` | LocalDateTime | Data/hora do atendimento (automática) |

---

## 🔴 Erros e Problemas Identificados

### 1. ❗ CRÍTICO — Senhas armazenadas em texto puro
**Arquivo:** `Usuario.java` e `AuthController.java`

```java
// PROBLEMA: A senha é salva e comparada sem nenhum tipo de criptografia
private String senha;
Optional<Usuario> findByUsernameAndSenha(String username, String senha);
```

**Por que é grave:** Caso o banco de dados seja acessado por alguém não autorizado, todas as senhas (e CPFs) dos alunos estarão visíveis. Isso é uma violação da **LGPD** e boas práticas de segurança.

---

### 2. ❗ CRÍTICO — Credenciais de alunos hardcoded no código-fonte
**Arquivo:** `AuthController.java` (linhas 36 a 145)

```java
// PROBLEMA: CPFs e senhas reais de pessoas estão escritos diretamente no código
u1.setUsername("48473011880");
u1.setSenha("haloHAL2");
```

**Por que é grave:** Esses dados foram enviados para um repositório público no GitHub. Qualquer pessoa pode ver os CPFs e senhas de todos os alunos cadastrados. Isso é uma gravíssima violação de privacidade e da **LGPD**.

---

### 3. ⚠️ GRAVE — `@CrossOrigin(origins = "*")` ativado nos dois controllers
**Arquivos:** `AuthController.java` e `ProntuarioController.java`

```java
@CrossOrigin(origins = "*")
```

**Por que é grave:** Isso permite que **qualquer site na internet** faça requisições para a API. Em produção, isso deve ser restrito apenas ao domínio do front-end.

---

### 4. ⚠️ GRAVE — Dependências incorretas no `pom.xml`
**Arquivo:** `pom.xml`

```xml
<!-- ERRO: Estes artifactIds não existem no Maven. O correto é spring-boot-starter-web. -->
<artifactId>spring-boot-h2console</artifactId>
<artifactId>spring-boot-starter-webmvc</artifactId>
<artifactId>spring-boot-starter-data-jpa-test</artifactId>
<artifactId>spring-boot-starter-webmvc-test</artifactId>
```

**Por que é grave:** Essas dependências têm nomes incorretos e provavelmente causarão **falha no build (compilação)** do projeto. Os nomes corretos são:
- `spring-boot-starter-web` (em vez de `spring-boot-starter-webmvc`)
- `spring-boot-starter-test` (em vez de `spring-boot-starter-webmvc-test` e `spring-boot-starter-data-jpa-test`)
- O H2 Console é habilitado via `application.properties`, não como dependência separada.

---

### 5. ⚠️ MÉDIO — Comentários errados no `AuthController.java`
**Arquivo:** `AuthController.java` (linhas 77, 87, 97, 107, 117, 127, 137)

```java
// "Verifica e cria a Bianca" — comentário repetido para outros 7 usuários diferentes
if (repository.findByUsernameAndSenha("43125267889", "123femafer").isEmpty()) { // Fernanda
```

Os comentários dizem "Verifica e cria a Bianca" para a Fernanda, Sara, Pedro, André, Irys, Daniel e Victoria. Isso indica falta de atenção na manutenção do código.

---

### 6. ⚠️ MÉDIO — Banco de dados H2 em arquivo comitado no Git
**Arquivo:** `dados_ambulato.mv.db`

O arquivo `.mv.db` (banco de dados com todos os dados reais) foi incluído no repositório Git. Isso significa que **dados de pacientes podem estar sendo versionados e compartilhados publicamente.**

---

### 7. 🟡 LEVE — `pom.xml` sem `<name>` e `<description>`
**Arquivo:** `pom.xml`

```xml
<name/>
<description/>
```
As tags `<name>` e `<description>` estão vazias. Não é um erro que impede a compilação, mas é uma boa prática preenchê-las para identificar o projeto.

---

### 8. 🟡 LEVE — Nenhum teste real implementado
**Arquivo:** `AmbulatoApplicationTests.java`

Apenas o teste padrão `contextLoads()` existe. Não há testes de integração ou unitários para os controllers e repositórios.

---

## ✅ Sugestões de Melhoria

### 🔐 Segurança

| # | Sugestão | Prioridade |
|---|---|---|
| 1 | **Criptografar senhas** usando BCrypt (Spring Security já inclui suporte) | 🔴 Urgente |
| 2 | **Remover dados hardcoded** do código. Usar um arquivo SQL de inicialização ou variáveis de ambiente | 🔴 Urgente |
| 3 | **Implementar JWT** (JSON Web Token) para autenticação stateless e segura | 🟠 Alta |
| 4 | **Restringir CORS** ao domínio do front-end em vez de `*` | 🟠 Alta |
| 5 | **Adicionar `dados_ambulato.mv.db` ao `.gitignore`** para não versionar dados reais | 🔴 Urgente |

### 🏗️ Arquitetura

| # | Sugestão | Prioridade |
|---|---|---|
| 6 | **Adicionar camada de Service** entre Controller e Repository, separando as responsabilidades | 🟠 Alta |
| 7 | **Usar DTOs** (Data Transfer Objects) para não expor a entidade `Usuario` (com senha) diretamente na resposta do login | 🟠 Alta |
| 8 | **Corrigir as dependências do `pom.xml`** para os nomes corretos | 🔴 Urgente |
| 9 | **Relacionar `Prontuario` com `Usuario`** via chave estrangeira (`@ManyToOne`) em vez de guardar apenas o nome como texto | 🟡 Média |

### 🧪 Qualidade

| # | Sugestão | Prioridade |
|---|---|---|
| 10 | **Implementar testes unitários** para os controllers usando `MockMvc` | 🟡 Média |
| 11 | **Adicionar validações** nos campos dos modelos com `@NotBlank`, `@NotNull` (Jakarta Validation) | 🟡 Média |
| 12 | **Corrigir comentários** no `AuthController.java` para refletirem o usuário correto | 🟢 Baixa |
| 13 | **Adicionar tratamento global de exceções** com `@ControllerAdvice` | 🟡 Média |

---

## 🚀 Próximos Passos Recomendados (Por Ordem de Prioridade)

1. **Imediatamente:** Deletar o repositório GitHub ou torná-lo privado, pois CPFs e senhas reais estão expostos publicamente.
2. **Imediatamente:** Adicionar `dados_ambulato.mv.db` ao `.gitignore`.
3. **Curto prazo:** Corrigir as dependências do `pom.xml` para garantir que o projeto compila.
4. **Curto prazo:** Implementar criptografia de senha com BCrypt.
5. **Médio prazo:** Refatorar a arquitetura adicionando camada de Service e DTOs.
6. **Médio prazo:** Substituir os usuários hardcoded por um sistema de cadastro ou arquivo de configuração seguro.

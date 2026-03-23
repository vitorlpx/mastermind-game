# 🎯 Mastermind Pro

Uma versão web funcional do clássico jogo Mastermind, desenvolvida como case técnico full-stack.

---

## 📋 Sobre o Projeto

O jogador tenta adivinhar uma combinação secreta de 4 cores em até 10 tentativas. A cada tentativa, o backend retorna um feedback posicional indicando acertos exatos e cores corretas em posições erradas — sem nunca revelar a combinação secreta.

---

## 🛠️ Decisões Técnicas

### Stack

| Camada | Tecnologia | Justificativa |
|---|---|---|
| Backend | Java 17 + Spring Boot 3.5 | LTS estável, ecossistema enterprise maduro |
| Frontend | Angular 18 | Standalone components e signals — moderno sem riscos de compatibilidade |
| Banco | PostgreSQL 16 | Dados relacionais (User → Match), queries de ranking simples via SQL |
| Auth | JWT (jjwt 0.12.3) | Stateless, token decodificável no frontend para extrair nome sem chamada extra |
| Infra | Docker + Docker Compose | Ambiente totalmente reproduzível com um único comando |

### Decisões relevantes

- **JWT com claims customizados**: o token carrega `name` e `email`, eliminando chamada extra ao backend após login
- **JSONB no PostgreSQL**: a matriz de tentativas é armazenada como `jsonb`, aproveitando a flexibilidade do Postgres sem abrir mão do modelo relacional
- **`bestScore` desnormalizado no `User`**: evita agregação custosa em toda query de ranking
- **Feedback posicional**: o backend retorna um array `["hit", "near", "empty"]` por posição, permitindo visualização fiel ao Mastermind clássico
- **Arquitetura em camadas**: `Controller → Service → Repository`, com DTOs separando o contrato da API das entidades JPA
- **Frontend containerizado com Nginx**: garante ambiente idêntico independente da versão do Node instalada na máquina do avaliador
- **Três níveis de dificuldade**: Easy (6 cores), Medium (8 cores) e Hard (10 cores), com pools de cores distintos no backend

## ✨ Diferenciais do Projeto

Além dos requisitos funcionais obrigatórios, o projeto foi desenvolvido com atenção a práticas e funcionalidades que vão além do mínimo esperado:

| Diferencial | Descrição |
|---|---|
| 🐳 **Docker completo** | Backend, frontend e banco 100% containerizados — basta ter o Docker instalado para rodar tudo com um único comando |
| 📊 **Logs com SLF4J** | Logs estruturados nos pontos críticos do backend (autenticação, início de partida, tentativas, erros) — facilita observabilidade e debug |
| 🌿 **Git Flow** | Branches organizadas por tipo (`feat/`, `chore/`, `fix/`), commits padronizados com Conventional Commits  |
| ⚙️ **CI com GitHub Actions** | Pipeline de integração contínua implementado para o backend, com build e testes automáticos a cada push em `backend/**` na branch `master` e em pull requests para `master` (sem etapa de CD no momento) |
| 🎯 **Níveis de dificuldade** | Três níveis jogáveis — Fácil (6 cores), Médio (8 cores) e Difícil (10 cores) — com pools de cores distintos gerados pelo backend |
| 📖 **Swagger/OpenAPI** | Documentação interativa da API disponível em `/swagger-ui/index.html`, com suporte a autenticação JWT direto na interface |
| 🎓 **Tela "Como Jogar"** | Página dedicada explicando as regras, o significado do feedback e a diferença entre as dificuldades |
| 🔒 **Validação robusta** | Regex de senha no cadastro exigindo maiúscula, minúscula, número e caractere especial — validado tanto no frontend quanto no backend |
| ⏱️ **Timer e score em tempo real** | O jogador acompanha o tempo decorrido e o score potencial diminuindo a cada tentativa — criando tensão e incentivando acertar rápido |
| 🎯 **Feedback posicional** | O backend retorna um array de feedback por posição (`hit`, `near`, `empty`), permitindo visualização fiel ao Mastermind clássico |
| 🏆 **Ranking com destaque do usuário** | A tela de ranking destaca visualmente o jogador logado na lista global |
| 🧪 **Testes unitários** | Cobertura nos pontos críticos do backend (JUnit 5 + Mockito) e do frontend (Jasmine + Karma) |

---

## ✅ Pré-requisitos

- [Docker](https://www.docker.com/) e Docker Compose

> Apenas o Docker é necessário. Backend, frontend e banco rodam inteiramente via containers.

---

## 🚀 Como Rodar

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/mastermind-game.git
cd mastermind-game
```

### 2. Configure as variáveis de ambiente

Este projeto usa dois arquivos `.env`:

### Por que existem dois `.env`?

- `.env` na raiz:
  arquivo lido pelo `docker compose`, usado para injetar variáveis nos containers (backend e banco).
- `backend/api/.env`:
  arquivo também lido no ambiente Docker atual (via `env_file` do serviço `db` no `docker-compose.yml`) e usado no backend local fora do Docker.

Em resumo: na configuração atual do projeto, para rodar tudo com Docker os dois arquivos precisam existir.

**Para rodar com Docker (obrigatório):**
```bash
cp .env.example .env
cp backend/api/.env.example backend/api/.env
```

```powershell
Copy-Item .env.example .env
Copy-Item backend/api/.env.example backend/api/.env
```

Edite os arquivos com seus valores.

Conteúdo esperado em `.env` (raiz, usado pelo `docker-compose.yml`):
```dotenv
DB_NAME=mastermind_db
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_com_no_minimo_32_caracteres
JWT_EXPIRATION=3600000
```

Conteúdo esperado em `backend/api/.env` (necessário no Docker atual e também no backend local):
```dotenv
DB_URL=jdbc:postgresql://localhost:5433/mastermind_db
DB_NAME=mastermind_db
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_com_no_minimo_32_caracteres
JWT_EXPIRATION=3600000
```

### 3. Suba todos os serviços
```bash
docker compose up --build
```

> Se sua instalação ainda usa o comando legado, utilize `docker-compose up --build`.

Aguarde as mensagens:
```
mastermind-db       | database system is ready to accept connections
mastermind-backend  | Started Application in X.XXX seconds
mastermind-frontend | ready
```

### 4. Acesse a aplicação

| Serviço | URL |
|---|---|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |

---

## 🗂️ Estrutura do Projeto
```
mastermind-game/
├── backend/
│   └── api/
│       ├── src/
│       │   ├── main/java/com/br/mastermind/api/
│       │   │   ├── controller/        # Endpoints REST
│       │   │   ├── dto/               # Objetos de entrada e saída da API
│       │   │   ├── entity/            # Entidades JPA (User, Match)
│       │   │   ├── enums/             # Enumerações (MatchStatus, MatchDifficulty)
│       │   │   ├── infra/
│       │   │   │   ├── exception/     # Exceções customizadas e handler global
│       │   │   │   ├── security/
│       │   │   │   │   ├── config/    # SecurityConfig, CorsConfig
│       │   │   │   │   ├── filter/    # JwtFilter
│       │   │   │   │   └── util/      # JwtUtil
│       │   │   │   └── springdoc/     # SwaggerConfig
│       │   │   ├── repository/        # Interfaces JPA (acesso ao banco)
│       │   │   └── service/           # Regras de negócio
│       │   └── test/                  # Testes unitários (JUnit 5 + Mockito)
│       ├── Dockerfile
│       ├── .env.example
│       └── .env                       # não versionado
│
├── frontend/
│   └── src/
│       └── app/
│           ├── core/
│           │   ├── guards/            # Proteção de rotas autenticadas
│           │   ├── interceptors/      # Interceptor de token JWT
│           │   ├── models/            # Interfaces TypeScript
│           │   └── services/          # Comunicação com a API
│           └── features/
│               ├── about/             # Tela "Como Jogar"
│               ├── dashboard/         # Tela inicial pós-login
│               ├── game/              # Tabuleiro do jogo
│               ├── login/             # Tela de login
│               ├── register/          # Tela de cadastro
│               └── ranking/           # Tela de ranking global
│
├── docker-compose.yml
├── .env.example
├── .env                               # não versionado
└── README.md
```

---

## 🔌 Variáveis de Ambiente

| Variável | Descrição |
|---|---|
| `DB_NAME` | Nome do banco de dados |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Chave secreta para assinatura do JWT (mín. 32 chars) |
| `JWT_EXPIRATION` | Tempo de expiração do token em ms (`3600000` = 1h) |

> O `DB_URL` é montado automaticamente pelo `docker-compose.yml` e não precisa ser definido manualmente ao usar Docker.

---

## 📡 Documentação da API

Com os containers rodando, acesse o Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

### Endpoints

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Cadastro de usuário | ❌ |
| POST | `/api/auth/login` | Login e geração de token | ❌ |
| POST | `/api/game/start` | Inicia nova partida | ✅ |
| POST | `/api/game/guess/{matchId}` | Submete uma tentativa | ✅ |
| GET | `/api/ranking` | Lista ranking global | ✅ |
| GET | `/api/user/me` | Perfil do usuário logado | ✅ |
| GET | `/api/match/history` | Histórico de partidas | ✅ |

---

## 🎮 Regras do Jogo

- Combinação secreta gerada aleatoriamente pelo backend com 4 cores
- O frontend **nunca** recebe a combinação secreta
- Máximo de 10 tentativas por partida
- Feedback por posição a cada tentativa:
  - ⚫ **Preto** → cor correta na posição correta
  - ⚪ **Branco** → cor correta na posição errada
  - ○ **Vazio** → cor não presente na combinação
- Pontuação: `tentativas_restantes × 100`

### Dificuldades

| Nível | Cores disponíveis |
|---|---|
| Fácil | 6 cores |
| Médio | 8 cores |
| Difícil | 10 cores |

---

## 🧪 Testes

### Backend
```bash
cd backend/api
./mvnw test
```

```powershell
cd backend/api
.\mvnw.cmd test
```

**Cobertura:**
- `AuthServiceTest` — register, email duplicado, login, credenciais inválidas
- `GameServiceTest` — startMatch, partida não encontrada, vitória com 4 hits

### Frontend
```bash
cd frontend
ng test --watch=false
```

**Cobertura:**
- `AuthService` spec — login, logout, isAuthenticated
- `LoginComponent` spec — formulário inválido, login válido, erro de autenticação

---

## 🐳 Docker

Parar os containers:
```bash
docker compose down
```

Parar e resetar o banco (apaga todos os dados):
```bash
docker compose down -v
```

Rebuildar após mudanças no código:
```bash
docker compose up --build
```

---

## 🛠️ Tecnologias

**Backend:** Java 17, Spring Boot 3.5, Spring Security, Spring Data JPA, PostgreSQL 16, JWT (jjwt 0.12.3), Lombok, Swagger/OpenAPI, SLF4J

**Frontend:** Angular 18, TypeScript, SCSS, Reactive Forms, HttpClient, JWT Decode, Nginx

**Testes:** JUnit 5, Mockito · Jasmine, Karma

**Infra:** Docker, Docker Compose, Git Flow, Conventional Commits

## 🚀 Possíveis Melhorias

Funcionalidades e melhorias identificadas que podem serem adicionados no projeto:

### Backend
- **Deploy na AWS** — containerizar e subir o projeto no ECS/Fargate com RDS para PostgreSQL, tornando a aplicação acessível publicamente
- **Autenticação OAuth2 com Google** — permitir login social além do fluxo tradicional de email e senha
- **Ranking com paginação** — implementar paginação no endpoint de ranking para suportar grandes volumes de jogadores
- **Cache no ranking** — adicionar cache com Redis para evitar queries frequentes na tabela de usuários
- **Refresh token** — implementar fluxo de refresh token para renovar sessões sem forçar novo login
- **Cobertura de testes** — expandir os testes unitários e adicionar testes de integração com `@SpringBootTest`
- **CD com GitHub Actions** — adicionar etapa de deploy contínuo (atualmente o projeto possui apenas CI)

### Frontend
- **Animações no tabuleiro** — transições visuais ao submeter tentativas e revelar o feedback
- **Modo daltônico** — opção de acessibilidade que substitui as cores por símbolos ou padrões
- **Histórico detalhado** — tela de detalhes de uma partida específica mostrando todas as tentativas realizadas
- **PWA** — transformar o frontend em Progressive Web App para instalação no dispositivo

### Jogo
- **Partidas multiplayer** — modo onde dois jogadores competem para adivinhar a mesma combinação
- **Combinações com repetição de cores** — variante mais difícil onde a combinação secreta pode ter cores repetidas
- **Sistema de conquistas** — badges por performance (ex: acertou na primeira tentativa, sequência de vitórias)
- **Tutorial interativo** — walkthrough guiado para novos jogadores na primeira vez que acessam o jogo

------------

*Feito com 🩷 por Vitor*.
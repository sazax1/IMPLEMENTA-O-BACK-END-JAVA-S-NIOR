# 🎵 Teste - SEPLAG MT

API REST para gerenciamento de Artistas e Álbuns.


---

## 🚀 Como Executar a Aplicação

### Pré-requisitos
- Docker e Docker Compose instalados

### Execução

```bash
# 1. Clonar o repositório
git clone https://github.com/[seu-usuario]/music-api.git
cd music-api

# 2. Iniciar os containers (API + PostgreSQL + MinIO)
docker-compose up --build

# 3. Aguardar inicialização (~60 segundos)
```

### URLs de Acesso

| Serviço | URL |
|---------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **API Docs** | http://localhost:8080/api-docs |
| **Health Check** | http://localhost:8080/actuator/health |
| **MinIO Console** | http://localhost:9001 (user: minioadmin / pass: minioadmin) |

---

## 🔐 Autenticação

### Credenciais de Teste

| Username | Password |
|----------|----------|
| admin | admin123 |
| user | admin123 |

### Obter Token (cURL)

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Resposta:**
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 300
}
```

O token expira em **5 minutos**. Use o refresh token para renovar.

---

## 🧪 Como Executar os Testes

```bash
# Via Docker (durante o build)
docker build -t music-api .

# Localmente (requer Java 21 + Maven)
./mvnw test
```

### Testes Implementados

| Classe | Descrição |
|--------|-----------|
| `JwtServiceTest` | Geração e validação de tokens JWT |
| `ArtistServiceTest` | CRUD de artistas com filtros |
| `AlbumServiceTest` | CRUD de álbuns e notificação WebSocket |
| `RegionalServiceTest` | Lógica de sincronização de regionais |

---

## 📚 Endpoints da API

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/auth/login` | Login e obtenção de tokens |
| POST | `/api/v1/auth/refresh` | Renovar token de acesso |

### Artistas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/artists` | Listar (paginado, filtros) |
| GET | `/api/v1/artists/{id}` | Buscar por ID |
| POST | `/api/v1/artists` | Criar artista |
| PUT | `/api/v1/artists/{id}` | Atualizar artista |

**Parâmetros GET:** `name`, `type` (SOLO/BAND), `page`, `size`, `sort`

### Álbuns

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/albums` | Listar (paginado, filtros) |
| GET | `/api/v1/albums/{id}` | Buscar por ID |
| POST | `/api/v1/albums` | Criar álbum |
| PUT | `/api/v1/albums/{id}` | Atualizar álbum |
| POST | `/api/v1/albums/{id}/covers` | Upload de capas |

**Parâmetros GET:** `title`, `artistName`, `artistType` (SOLO/BAND), `page`, `size`, `sort`

### Regionais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/regionais` | Listar regionais |
| POST | `/api/v1/regionais/sync` | Sincronizar com API externa |

---

## 🏗️ Decisões de Arquitetura

### Stack Tecnológica

| Componente | Tecnologia | Justificativa |
|------------|------------|---------------|
| Framework | Spring Boot 3.2 | Maturidade, ecossistema robusto |
| Linguagem | Java 21 | LTS, recursos modernos |
| Banco de Dados | PostgreSQL 15 | Robusto, ACID compliance |
| Object Storage | MinIO | Compatível S3, fácil containerização |
| Autenticação | JWT (jjwt) | Stateless, escalável |
| Rate Limiting | Bucket4j | Simples e eficiente |
| Migrations | Flyway | Versionamento de schema |
| Docs API | SpringDoc OpenAPI | Swagger UI integrado |

### Modelo de Dados

```
ARTIST (id, name, type, created_at, updated_at)
   |
   |-- N:N --> ALBUM (id, title, release_year, created_at, updated_at)
                  |
                  |-- 1:N --> ALBUM_COVER (id, file_key, original_name)

REGIONAL (id, external_id, nome, ativo, created_at, updated_at)

APP_USER (id, username, password, role, enabled, created_at)
```

### Sincronização de Regionais

Lógica implementada conforme requisito:
1. **Novo no endpoint** → inserir com `ativo=true`
2. **Ausente no endpoint** → marcar `ativo=false`
3. **Atributo alterado** → inativar registro antigo e criar novo

---

## ✅ Requisitos Implementados

### Requisitos Gerais
- [x] Segurança: bloqueio de domínios externos (CORS)
- [x] Autenticação JWT com expiração 5 minutos + refresh
- [x] Endpoints POST, PUT, GET
- [x] Paginação na consulta dos álbuns
- [x] Consultas parametrizadas (cantores/bandas via `artistType`)
- [x] Consultas por nome do artista com ordenação (asc/desc)
- [x] Upload de uma ou mais imagens de capa
- [x] Armazenamento das imagens no MinIO (S3)
- [x] Links pré-assinados com expiração de 30 minutos
- [x] Versionamento de endpoints (`/api/v1`)
- [x] Flyway Migrations para criar e popular tabelas
- [x] Documentação com OpenAPI/Swagger

### Requisitos Sênior
- [x] Health Checks e Liveness/Readiness
- [x] Testes unitários
- [x] WebSocket para notificar a cada novo álbum cadastrado
- [x] Rate limit: 10 requisições por minuto por usuário
- [x] Endpoint de regionais com sincronização inteligente

---

## 📁 Estrutura do Projeto

```
src/main/java/com/seplag/musicapi/
├── config/           # Configurações (Security, MinIO, WebSocket, OpenAPI)
├── controller/       # REST Controllers versionados
├── dto/              # DTOs de request e response
├── entity/           # Entidades JPA
├── exception/        # Tratamento global de exceções
├── repository/       # Repositórios Spring Data JPA
├── security/         # JWT Service e Filtros
└── service/          # Camada de serviços

src/main/resources/
├── db/migration/     # Scripts Flyway
└── application.yml   # Configurações da aplicação

src/test/java/        # Testes unitários
```

---

## 📝 Dados Iniciais (Flyway)

Os seguintes artistas e álbuns são criados automaticamente:

| Artista | Tipo | Álbuns |
|---------|------|--------|
| Serj Tankian | SOLO | Harakiri, Black Blooms, The Rough Dog |
| Mike Shinoda | SOLO | The Rising Tied, Post Traumatic, Post Traumatic EP, Where'd You Go |
| Michel Teló | SOLO | Bem Sertanejo, Bem Sertanejo - O Show (Ao Vivo), Bem Sertanejo - (1ª Temporada) - EP |
| Guns N' Roses | BAND | Use Your Illusion I, Use Your Illusion II, Greatest Hits |

---

## 🐳 Docker

### Serviços Orquestrados

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| api | 8080 | Aplicação Spring Boot |
| postgres | 5432 | Banco de dados |
| minio | 9000/9001 | Object Storage |

### Variáveis de Ambiente

Configuráveis no `docker-compose.yml`:
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET`
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`
- `CORS_ORIGINS`

---

## 📜 Licença

Projeto desenvolvido para processo seletivo SEPLAG-MT.

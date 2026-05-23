# FlowPay Monorepo

Monorepo com dois projetos:

- `api/`: backend Java 17 + Spring Boot 3
- `front/`: frontend React + Vite

## Estrutura

```text
flow-pay/
  api/
    src/
    database/
    postman/
    pom.xml
  front/
    src/
    package.json
```

## Subir API

1. Entre em `api/`
2. Configure `api/.env` (base no `api/.env.example`)
3. Rode:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger:

- `http://localhost:8080/flow-pay/swagger-ui.html`

## Subir API + Postgres com Docker Compose

No diretorio `api/`:

```bash
docker compose up --build
```

A API sobe com limite de memoria de container em `512m` e heap JVM em `-Xmx384m`.

## Subir Front

1. Entre em `front/`
2. Instale dependencias
3. Rode em desenvolvimento

```bash
npm install
npm run dev
```

Defina o backend no arquivo `front/.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/flow-pay
```

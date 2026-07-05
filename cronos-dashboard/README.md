# Cronos Dashboard

Modern React dashboard for [Cronos](../README.md) job observability.

The dashboard is **embedded in `cronos-spring-boot-starter`**. When a host application adds the Cronos dependency and starts, the UI is served automatically at `/cronos/` alongside the API at `/cronos/api`.

## Stack

- React 19 + TypeScript
- Vite 8
- Ant Design 5 (dark theme, Turkish locale)
- React Router

## Features

- **Genel Bakış** — health status, job counts, recent executions
- **Job Listesi** — search and filter by status
- **Job Detayı** — metadata, manual trigger, paginated execution history
- Auto-refresh every 15 seconds on the overview page

## Embedded mode (default)

Add `cronos-spring-boot-starter` to your Spring Boot app and run it:

```
http://localhost:8080/cronos/
```

No separate frontend server is required. Static assets are built during `mvn package` and packaged inside the starter JAR.

## Standalone development

For hot-reload UI development against a running Spring Boot app:

```bash
cd cronos-dashboard
npm install
npm run dev
```

Open [http://localhost:5173/cronos/](http://localhost:5173/cronos/). Vite proxies `/cronos/api` to `http://localhost:8080`.

## Production Build

```bash
npm run build
```

Output goes to `dist/` with `base: /cronos/` for subpath deployment. The Maven build copies these files into `cronos-spring-boot-starter` classpath at `static/cronos/`.

## Configuration

Host application properties:

```yaml
cronos:
  ui-enabled: true      # set false to disable embedded UI
  ui-base-path: /cronos # dashboard URL prefix
  api-base-path: /cronos/api
```

## API Endpoints Used

| Method | Endpoint |
|---|---|
| GET | `/cronos/api/health` |
| GET | `/cronos/api/jobs` |
| GET | `/cronos/api/jobs/{id}` |
| GET | `/cronos/api/jobs/{id}/executions` |
| POST | `/cronos/api/jobs/{id}/trigger` |

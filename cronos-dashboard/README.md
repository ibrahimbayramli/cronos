# Cronos Dashboard

Modern React dashboard for [Cronos](../README.md) job observability.

## Stack

- React 19 + TypeScript
- Vite 8
- Tailwind CSS 4
- React Router
- Lucide icons

## Features

- **Genel Bakış** — health status, job counts, recent executions
- **Job Listesi** — search and filter by status
- **Job Detayı** — metadata, manual trigger, paginated execution history
- Auto-refresh every 15 seconds on the overview page

## Development

Start your Spring Boot app with Cronos enabled (default port `8080`), then:

```bash
cd cronos-dashboard
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173). Vite proxies `/cronos/api` to `http://localhost:8080`.

## Production Build

```bash
npm run build
```

Static files are emitted to `dist/`. Serve them behind your Spring Boot app or a reverse proxy that forwards API calls to `/cronos/api`.

## API Endpoints Used

| Method | Endpoint |
|---|---|
| GET | `/cronos/api/health` |
| GET | `/cronos/api/jobs` |
| GET | `/cronos/api/jobs/{id}` |
| GET | `/cronos/api/jobs/{id}/executions` |
| POST | `/cronos/api/jobs/{id}/trigger` |

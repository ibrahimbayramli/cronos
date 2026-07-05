# Cronos

**Cronos** is a Spring Boot observability starter that passively discovers, tracks, and exposes scheduled jobs — regardless of how they were defined (`@Scheduled`, Quartz, etc.). Existing job code stays unchanged; Cronos finds jobs at runtime and surfaces them through a REST API (and eventually a dashboard).

## Architecture

```
Developer App (existing @Scheduled / Quartz jobs)
        │
        ▼
cronos-spring-boot-starter
  ├── JobSourceAdapter (discovery plugins)
  ├── Execution Tracker (AOP / listeners)
  ├── Manual Trigger Service
  ├── REST API  (/cronos/api/**)
  └── Job history persistence (H2 by default)
```

## Modules

| Module | Description |
|---|---|
| `cronos-core` | Domain entities (`JobDescriptor`, `JobExecution`) and `JobSourceAdapter` SPI |
| `cronos-spring-boot-starter` | Auto-configuration, Spring `@Scheduled` adapter, REST API |
| `cronos-dashboard` | React/Vite dashboard UI |

## Quick Start

### 1. Add dependency

```xml
<dependency>
    <groupId>dev.cronos</groupId>
    <artifactId>cronos-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### 2. Enable scheduling (if not already)

```java
@SpringBootApplication
@EnableScheduling
public class MyApplication { ... }
```

### 3. Run and explore

Cronos auto-configures an embedded H2 database (zero extra config) and exposes:

| Method | Endpoint | Description |
|---|---|---|
| GET | `/cronos/api/jobs` | All discovered jobs with last status and next run |
| GET | `/cronos/api/jobs/{id}` | Job detail |
| GET | `/cronos/api/jobs/{id}/executions` | Paginated execution history |
| POST | `/cronos/api/jobs/{id}/trigger` | Manual trigger |
| GET | `/cronos/api/health` | Starter health |

## Configuration

```yaml
cronos:
  enabled: true
  api-base-path: /cronos/api
  execution-retention: 90d
  manual-trigger-pool-size: 4
  datasource:
    url: jdbc:h2:file:./data/cronos;DB_CLOSE_DELAY=-1
    username: sa
    password: ""
    driver-class-name: org.h2.Driver
```

When no `DataSource` bean is present, Cronos provisions an embedded H2 database using `cronos.datasource.*` (defaults above).

## Current Status (v0.1.0-SNAPSHOT)

Implemented phases:

- **Faz 0** — Maven multi-module skeleton, domain models
- **Faz 1** — Spring `@Scheduled` job discovery via `ScheduledTaskHolder`
- **Faz 2** — AOP execution tracking with JPA persistence (Flyway + H2)
- **Faz 3** — Manual trigger via reflection (separate thread pool)
- **Faz 4** — REST API endpoints
- **Faz 5** — React/Vite dashboard MVP (`cronos-dashboard/`)

Planned next:

- **Faz 6** — WebSocket live updates
- **Faz 7** — Quartz adapter
- **Faz 8** — Auth (API key / JWT)
- **Faz 9** — Plugin adapter architecture (Spring Batch, db-scheduler, …)

## Design Decisions

| Topic | Choice |
|---|---|
| Persistence default | H2 embedded (zero config); Postgres supported via standard Spring datasource config |
| MVP scope | Spring `@Scheduled` only; Quartz in a later phase |
| Execution retention | Configurable via `cronos.execution-retention` (default 90 days) |
| Maven coordinates | `dev.cronos` (adjust to `io.github.<user>.cronos` before Maven Central publish) |

## Building

```bash
mvn clean verify
```

Requires Java 17+.

### Dashboard

```bash
cd cronos-dashboard
npm install
npm run dev
```

See [cronos-dashboard/README.md](cronos-dashboard/README.md) for details.

## License

TBD

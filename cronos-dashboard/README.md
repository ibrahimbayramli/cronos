# Cronos Dashboard

React/Ant Design frontend bundled into `cronos-spring-boot-starter`.

<p align="center">
  <img src="../docs/banner.svg" alt="Cronos" width="720" />
</p>

## Embedded mode (production)

When your app depends on `cronos-spring-boot-starter`, the dashboard is served automatically:

```
http://localhost:8080/cronos/
```

No separate deployment step is required.

## Standalone development

```bash
npm install
npm run dev
```

Open http://localhost:5173/cronos/ — API requests proxy to `http://localhost:8080`.

## Stack

- React 19 + TypeScript + Vite 8
- Ant Design 5 (dark theme, Turkish locale)
- React Router

## Build

```bash
npm run build
```

Output is copied into the starter JAR during `mvn package` at `classpath:/static/cronos/`.

## Configuration

```yaml
cronos:
  port: 9090
  ui-enabled: true
  ui-base-path: /cronos
  api-base-path: /cronos/api
```

See the [main README](../README.md#port-binding) for port configuration details.

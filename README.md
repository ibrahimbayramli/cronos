<p align="center">
  <img src="docs/banner.svg" alt="Cronos — Spring Boot Job Observability" width="100%" />
</p>

<p align="center">
  <a href="https://github.com/ibrahimbayramli/cronos/packages"><img src="https://img.shields.io/badge/GitHub%20Packages-Maven%20%26%20Gradle-24292f?style=flat-square&logo=github" alt="GitHub Packages" /></a>
  <a href="https://github.com/ibrahimbayramli/cronos/releases/tag/v0.1.0"><img src="https://img.shields.io/github/v/release/ibrahimbayramli/cronos?style=flat-square&label=release" alt="Release" /></a>
  <a href="https://github.com/ibrahimbayramli/cronos/actions/workflows/publish.yml"><img src="https://img.shields.io/github/actions/workflow/status/ibrahimbayramli/cronos/publish.yml?style=flat-square&label=Publish" alt="Publish workflow" /></a>
  <img src="https://img.shields.io/badge/Java-17+-blue?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot 3.3" />
  <img src="https://img.shields.io/badge/license-MIT-green?style=flat-square" alt="MIT License" />
</p>

<p align="center">
  <strong>Cronos</strong> is a zero-config Spring Boot starter that discovers your scheduled jobs at runtime,
  tracks every execution, exposes a REST API, and ships a modern embedded dashboard — without changing your job code.
</p>

---

## What does Cronos do?

You already have `@Scheduled` jobs in your Spring Boot app. Cronos plugs in as a dependency and automatically:

| Capability | Description |
|---|---|
| **Discovery** | Finds all `@Scheduled` methods when the app starts |
| **Tracking** | Records start/end time, duration, status, and errors |
| **Dashboard** | Serves a React + Ant Design UI at `/cronos/` |
| **REST API** | Exposes jobs, history, health, and manual trigger at `/cronos/api` |
| **Persistence** | Stores execution history in embedded H2 (zero config) or your own database |

```mermaid
flowchart TB
    subgraph App["Your Spring Boot Application"]
        J1["@Scheduled job A"]
        J2["@Scheduled job B"]
    end

    subgraph Cronos["cronos-spring-boot-starter"]
        D["Job Discovery"]
        T["Execution Tracker (AOP)"]
        API["REST API /cronos/api"]
        UI["Dashboard UI /cronos/"]
        DB[("H2 / Postgres")]
    end

    J1 --> D
    J2 --> D
    D --> T
    T --> DB
    API --> DB
    UI --> API
```

> **No code changes required.** Add the dependency, keep `@EnableScheduling`, run your app.

---

## Published artifacts

Cronos is published to **GitHub Packages** and can be consumed from both **Maven** and **Gradle** projects.

| Artifact | Coordinates | Purpose |
|---|---|---|
| Starter (use this) | `dev.cronos:cronos-spring-boot-starter:0.1.0` | Auto-config, API, embedded UI |
| Core | `dev.cronos:cronos-core:0.1.0` | Domain models and SPI |

**Registry URL:** `https://maven.pkg.github.com/ibrahimbayramli/cronos`

---

## Installation

### Prerequisites

1. A GitHub account with access to this repository
2. A [Personal Access Token (classic)](https://github.com/settings/tokens) with `read:packages` scope
3. Java 17+ and Spring Boot 3.x

### Maven

**Step 1 — Add the GitHub Packages repository** to `pom.xml`:

```xml
<repositories>
    <repository>
        <id>github-cronos</id>
        <url>https://maven.pkg.github.com/ibrahimbayramli/cronos</url>
    </repository>
</repositories>
```

**Step 2 — Add credentials** to `~/.m2/settings.xml`:

```xml
<settings>
  <servers>
    <server>
      <id>github-cronos</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

> The `<id>` must match the repository `<id>` in your `pom.xml`.

**Step 3 — Add the dependency:**

```xml
<dependency>
    <groupId>dev.cronos</groupId>
    <artifactId>cronos-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Step 4 — Enable scheduling** (if not already):

```java
@SpringBootApplication
@EnableScheduling
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

**Step 5 — Run your app:**

```bash
mvn spring-boot:run
```

Open **http://localhost:8080/cronos/** — dashboard and API are live.

---

### Gradle

**Step 1 — Add the repository** in `settings.gradle.kts` (Gradle 7+):

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            name = "GitHubPackagesCronos"
            url = uri("https://maven.pkg.github.com/ibrahimbayramli/cronos")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

Or in `build.gradle.kts` (legacy projects):

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/ibrahimbayramli/cronos")
        credentials {
            username = findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
```

**Step 2 — Store credentials** in `~/.gradle/gradle.properties`:

```properties
gpr.user=YOUR_GITHUB_USERNAME
gpr.key=YOUR_GITHUB_TOKEN
```

**Step 3 — Add the dependency** in `build.gradle.kts`:

```kotlin
dependencies {
    implementation("dev.cronos:cronos-spring-boot-starter:0.1.0")
}
```

**Step 4 — Enable scheduling and run:**

```kotlin
// Kotlin example — Java projects use the same annotation
@SpringBootApplication
@EnableScheduling
class MyApplication

fun main(args: Array<String>) {
    runApplication<MyApplication>(*args)
}
```

```bash
./gradlew bootRun
```

Open **http://localhost:8080/cronos/**

---

## What you get out of the box

| Endpoint | URL |
|---|---|
| Dashboard UI | `http://localhost:8080/cronos/` |
| REST API base | `http://localhost:8080/cronos/api` |
| Job list | `GET /cronos/api/jobs` |
| Job detail | `GET /cronos/api/jobs/{id}` |
| Execution history | `GET /cronos/api/jobs/{id}/executions` |
| Manual trigger | `POST /cronos/api/jobs/{id}/trigger` |
| Health | `GET /cronos/api/health` |

On startup, Cronos logs the dashboard and API URLs automatically.

---

## Configuration

```yaml
cronos:
  enabled: true
  api-base-path: /cronos/api
  ui-enabled: true
  ui-base-path: /cronos
  execution-retention: 90d
  manual-trigger-pool-size: 4
  datasource:
    url: jdbc:h2:file:./data/cronos;DB_CLOSE_DELAY=-1
    username: sa
    password: ""
    driver-class-name: org.h2.Driver
```

When your app already has a `DataSource` bean, Cronos uses it. Otherwise it provisions embedded H2 with the settings above.

---

## Project structure

| Module | Description |
|---|---|
| `cronos-core` | Domain entities and `JobSourceAdapter` SPI |
| `cronos-spring-boot-starter` | Auto-configuration, REST API, embedded UI |
| `cronos-dashboard` | React/Vite/Ant Design frontend (bundled into starter JAR) |

---

## Building from source

```bash
# Full build (includes dashboard UI)
mvn clean verify

# Skip UI build for faster CI
mvn clean verify -Dcronos.ui.build.skip=true

# Publish to GitHub Packages (maintainers)
export GITHUB_TOKEN=ghp_xxx
export GITHUB_ACTOR=your-username
mvn deploy -DskipTests -s .github/maven/settings.xml

# Or via Gradle wrapper
./gradlew publishToGitHubPackages
```

---

## Roadmap

- [x] Spring `@Scheduled` discovery
- [x] Execution tracking with JPA + Flyway
- [x] Manual trigger
- [x] REST API
- [x] Embedded dashboard UI
- [x] GitHub Packages (Maven & Gradle)
- [ ] WebSocket live updates
- [ ] Quartz adapter
- [ ] API key / JWT auth

---

## License

MIT — see [LICENSE](LICENSE).

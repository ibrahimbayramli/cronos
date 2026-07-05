<p align="center">
  <img src="docs/banner.svg" alt="Cronos — Spring Boot Job Observability" width="100%" />
</p>

<p align="center">
  <a href="https://github.com/ibrahimbayramli/cronos/packages"><img src="https://img.shields.io/badge/GitHub%20Packages-Maven%20%26%20Gradle-24292f?style=for-the-badge&logo=github" alt="GitHub Packages" /></a>
  <a href="https://github.com/ibrahimbayramli/cronos/releases/tag/v0.1.0"><img src="https://img.shields.io/github/v/release/ibrahimbayramli/cronos?style=for-the-badge&label=release&color=0891b2" alt="Release v0.1.0" /></a>
  <a href="https://github.com/ibrahimbayramli/cronos/actions/workflows/publish.yml"><img src="https://img.shields.io/github/actions/workflow/status/ibrahimbayramli/cronos/publish.yml?style=for-the-badge&label=CI" alt="Publish workflow" /></a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-blue?style=flat-square&logo=openjdk&logoColor=white" alt="Java 17+" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot 3.3" />
  <img src="https://img.shields.io/badge/license-MIT-green?style=flat-square" alt="MIT License" />
</p>

<p align="center">
  <strong>Cronos</strong>, <code>@Scheduled</code> işlerinizi otomatik keşfeden, her çalıştırmayı izleyen,
  bir REST API sunan ve modern gömülü bir kontrol paneli sağlayan sıfır yapılandırmalı bir Spring Boot starter'ıdır —
  iş kodunuzu değiştirmenize gerek kalmaz.
</p>

<p align="center">
  <a href="#hızlı-başlangıç">Hızlı Başlangıç</a> ·
  <a href="#projenize-ekleyin">Projenize Ekleyin</a> ·
  <a href="#yayınlanan-artifaktlar">Yayınlanan Artifaktlar</a> ·
  <a href="#yapılandırma">Yapılandırma</a>
</p>

---

## Cronos ne yapar?

Spring Boot uygulamaları genellikle kritik arka plan işlerini `@Scheduled` ile çalıştırır; ancak gözlemlenebilirlik çoğu zaman sonradan düşünülür — merkezi bir iş listesi yoktur, çalıştırma geçmişi tutulmaz, manuel tetikleme için pratik bir yol bulunmaz.

Cronos tek bir bağımlılık olarak devreye girer ve otomatik olarak şunları sağlar:

| Özellik | Ne sunar |
|---|---|
| **Keşif** | Başlangıçta tüm `@Scheduled` metodlarını bulur |
| **İzleme** | Başlangıç/bitiş zamanı, süre, durum ve hata durumunda stack trace kaydeder |
| **Kontrol paneli** | `/cronos/` adresinde React + Ant Design arayüzü sunar |
| **REST API** | `/cronos/api` altında işler, geçmiş, sağlık ve manuel tetikleme |
| **Kalıcılık** | Varsayılan olarak gömülü H2 veya uygulamanızın mevcut `DataSource`'u |

```mermaid
flowchart LR
    subgraph YourApp["Spring Boot Uygulamanız"]
        J1["@Scheduled iş A"]
        J2["@Scheduled iş B"]
    end

    subgraph Cronos["cronos-spring-boot-starter"]
        D["Keşif"]
        T["İzleyici (AOP)"]
        API["REST /cronos/api"]
        UI["Arayüz /cronos/"]
        DB[("H2 / Postgres")]
    end

    J1 --> D
    J2 --> D
    D --> T
    T --> DB
    API --> DB
    UI --> API
```

> **Sıfır kod değişikliği.** Bağımlılığı ekleyin, `@EnableScheduling` kullanmaya devam edin, uygulamanızı başlatın.

---

## Hızlı başlangıç

**1. Bağımlılığı ekleyin** (Maven veya Gradle — bkz. [Projenize ekleyin](#projenize-ekleyin))

**2. Zamanlamayı etkinleştirin** (henüz yoksa):

```java
@SpringBootApplication
@EnableScheduling
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

**3. Çalıştırın ve kontrol panelini açın:**

```bash
# Maven
mvn spring-boot:run

# Gradle
./gradlew bootRun
```

| Kaynak | URL |
|---|---|
| Kontrol paneli | http://localhost:8080/cronos/ |
| REST API | http://localhost:8080/cronos/api |
| Sağlık | http://localhost:8080/cronos/api/health |

Cronos başlangıçta kontrol paneli ve API URL'lerini loglar.

---

## Yayınlanan artifaktlar

Cronos **GitHub Packages** üzerinde yayınlanır ve hem **Maven** hem **Gradle** projeleri tarafından aynı Maven uyumlu kayıt defterinden tüketilir.

<p align="center">
  <img src="docs/packages-flow.svg" alt="Maven ve Gradle, Cronos'u GitHub Packages üzerinden tüketir" width="720" />
</p>

| Paket | Koordinatlar | Kullanım |
|---|---|---|
| **Starter** (önerilen) | `dev.cronos:cronos-spring-boot-starter:0.1.0` | Otomatik yapılandırma, REST API, gömülü arayüz |
| Core | `dev.cronos:cronos-core:0.1.0` | Domain modelleri ve SPI (ileri düzey) |

**Kayıt defteri URL'si:** `https://maven.pkg.github.com/ibrahimbayramli/cronos`

**GitHub'daki canlı paketler:**

- [cronos-spring-boot-starter](https://github.com/ibrahimbayramli/cronos/packages/3114732)
- [cronos-core](https://github.com/users/ibrahimbayramli/packages?repo_name=cronos)
- [Tüm paketler](https://github.com/ibrahimbayramli/cronos/packages)
- [v0.1.0 sürümü](https://github.com/ibrahimbayramli/cronos/releases/tag/v0.1.0)

---

## Projenize ekleyin

### Maven

<details open>
<summary><strong>Adım adım</strong></summary>

**1. Depo** — `pom.xml` dosyanıza ekleyin:

```xml
<repositories>
    <repository>
        <id>github-cronos</id>
        <url>https://maven.pkg.github.com/ibrahimbayramli/cronos</url>
    </repository>
</repositories>
```

**2. Bağımlılık:**

```xml
<dependency>
    <groupId>dev.cronos</groupId>
    <artifactId>cronos-spring-boot-starter</artifactId>
    <version>0.1.0</version>
</dependency>
```

**3. Senkronize edin ve çalıştırın:**

```bash
mvn clean compile
mvn spring-boot:run
```

</details>

### Gradle

<details open>
<summary><strong>Adım adım</strong></summary>

**1. Depo** — `settings.gradle.kts` içinde (Gradle 7+, önerilen):

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven {
            name = "GitHubPackagesCronos"
            url = uri("https://maven.pkg.github.com/ibrahimbayramli/cronos")
        }
    }
}
```

Eski projelerde aynı `maven { ... }` bloğunu `build.gradle.kts` içindeki `repositories` bölümüne ekleyin.

**2. Bağımlılık** — `build.gradle.kts` içinde:

```kotlin
dependencies {
    implementation("dev.cronos:cronos-spring-boot-starter:0.1.0")
}
```

**3. Senkronize edin ve çalıştırın:**

```bash
./gradlew clean build
./gradlew bootRun
```

</details>

### Çözümlemeyi doğrulayın

```bash
# Maven — dev.cronos:cronos-spring-boot-starter:0.1.0 indirilmeli
mvn dependency:get -Dartifact=dev.cronos:cronos-spring-boot-starter:0.1.0

# Gradle — koordinatları yazdır
./gradlew verifyConsumerGradleSnippet
```

---

## Kutudan çıktığı gibi ne alırsınız

| Uç nokta | Metot | Açıklama |
|---|---|---|
| `/cronos/` | GET | Gömülü kontrol paneli arayüzü |
| `/cronos/api/jobs` | GET | Keşfedilen işleri listeler |
| `/cronos/api/jobs/{id}` | GET | İş detayı + sonraki çalıştırma |
| `/cronos/api/jobs/{id}/executions` | GET | Çalıştırma geçmişi |
| `/cronos/api/jobs/{id}/trigger` | POST | Manuel tetikleme |
| `/cronos/api/health` | GET | Cronos sağlık kontrolü |

---

## Yapılandırma

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
    driver-class-name: org.h2.Driver
```

Uygulamanız zaten bir `DataSource` bean'i tanımlıyorsa Cronos onu yeniden kullanır. Aksi halde yukarıdaki ayarlarla gömülü H2 sağlar.

---

## Proje yapısı

| Modül | Açıklama |
|---|---|
| [`cronos-core`](cronos-core/) | Domain varlıkları ve `JobSourceAdapter` SPI |
| [`cronos-spring-boot-starter`](cronos-spring-boot-starter/) | Otomatik yapılandırma, REST API, gömülü arayüz |
| [`cronos-dashboard`](cronos-dashboard/) | React/Vite/Ant Design frontend (starter JAR'ına paketlenir) |

---

## Kaynaktan derleme

```bash
# Tam derleme (kontrol paneli arayüzü dahil)
mvn clean verify

# Daha hızlı CI için arayüz derlemesini atla
mvn clean verify -Dcronos.ui.build.skip=true

# Yayın koordinatlarını göster
./gradlew printPublishingInfo
```

---

## Yayınlama (bakımcılar)

Artifaktlar her [GitHub Release](https://github.com/ibrahimbayramli/cronos/releases) yayınlandığında **GitHub Packages**'a gönderilir. Aynı Maven kayıt defteri hem Maven hem Gradle tüketicilerine hizmet verir.

**CI:** [`.github/workflows/publish.yml`](.github/workflows/publish.yml), bir release yayınlandığında `mvn deploy` çalıştırır.

**Yerel yayın:**

```bash
# Maven
mvn deploy -DskipTests -s .github/maven/settings.xml

# Gradle wrapper (Maven deploy'a delege eder)
./gradlew publishToGitHubPackages
```

---

## Sorun giderme

| Sorun | Çözüm |
|---|---|
| Kontrol paneli 404 | `cronos.ui-enabled=true` olduğundan ve `spring.web.resources` ile çakışma olmadığından emin olun |
| İşler listelenmiyor | `@EnableScheduling` mevcut olduğunu ve metodların `@Scheduled` kullandığını doğrulayın |
| Gradle artifaktı bulamıyor | Depoyu yalnızca `build.gradle.kts`'e değil, `settings.gradle.kts`'e de ekleyin |

---

## Yol haritası

- [x] Spring `@Scheduled` keşfi
- [x] JPA + Flyway ile çalıştırma izleme
- [x] Manuel tetikleme
- [x] REST API
- [x] Gömülü kontrol paneli arayüzü
- [x] GitHub Packages (Maven ve Gradle)
- [ ] WebSocket ile canlı güncellemeler
- [ ] Quartz adaptörü
- [ ] API anahtarı / JWT kimlik doğrulama

---

## Lisans

MIT — bkz. [LICENSE](LICENSE).

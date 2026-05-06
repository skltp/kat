# AGENTS.md

## Project Overview

KAT is a Spring Boot 3.4 / Java 17 SOAP web service producer within the Swedish SKLTP (Tjänsteplattformen) ecosystem. It serves two RIV-TA service contracts:

- `GetSupportedServiceContracts/v2`
- `GetLogicalAddresseesByServiceContract/v2`

KAT fetches authorization data ("behörighet") from a TAK service via the [takcache](https://github.com/skltp/takcache) library, caches it in-memory, and responds to SOAP queries against that cache.

## Architecture

```
┌─────────────────────┐        ┌───────────────────┐
│  SOAP Clients       │──CXF──>│  kat-application  │
└─────────────────────┘        │  (Spring Boot)    │
                               │                   │
┌─────────────────────┐  REST  │  /kat/resetcache  │
│  Ops/Monitoring     │───────>│                   │
└─────────────────────┘        └────────┬──────────┘
                                        │ takcache lib
                                        v
                               ┌───────────────────┐
                               │  TAK Services     │
                               │  (SokVagvalsInfo) │
                               └───────────────────┘
```

### Key modules

| Module             | Purpose                                                                           |
|--------------------|-----------------------------------------------------------------------------------|
| `kat-application/` | Main Spring Boot app — SOAP endpoints, cache logic, REST reset, health indicators |
| `kat-tak-mock/`    | Mock TAK service for local development (port 8882)                                |
| `kat-soapui-test/` | SoapUI project for integration testing                                            |
| `helm/`            | Helm chart for Kubernetes deployment                                              |
| `report/`          | JaCoCo aggregation module                                                         |

### Critical data flow

1. On startup, `ApplicationEventListener` calls `TakCacheService.refresh()` to populate the cache from the TAK endpoint.
2. SOAP requests are handled by CXF endpoints configured in `KatWebServiceConfig`, delegating to `TakCacheServiceImpl`.
3. Cache refresh can be triggered at runtime via `GET /kat/resetcache`.
4. A custom health indicator (`TakCacheHealthIndicator`) reports readiness based on cache state.

## Build & Run

```powershell
# Full build (from repo root)
mvn clean install

# Run locally — start mock first, then app
cd kat-tak-mock; mvn spring-boot:run   # starts on port 8882
cd kat-application; mvn spring-boot:run  # starts on port 8880, uses -dev profile

# Run tests with coverage
mvn clean verify -P test-coverage
```

The `kat-tak-mock` module is also a `<scope>test</scope>` dependency of `kat-application`, so integration tests start the mock automatically.

### Key dependency versions (in `kat-application/pom.xml`)

| Dependency       | Version | Notes                                  |
|------------------|---------|----------------------------------------|
| Spring Boot      | 3.4.2   | Parent POM BOM                         |
| Apache CXF       | 4.1.0   | Jakarta EE namespace (`jakarta.xml.*`) |
| takcache         | 2.2.0   | SKLTP cache library                    |
| Log4j2           | 2.24.3  | Managed in parent POM properties       |
| JaCoCo           | 0.8.12  | Test coverage (profile `test-coverage`)|

## Conventions & Patterns

- **Lombok everywhere**: Use `@AllArgsConstructor`, `@Data`, `@Slf4j`. Constructors are preferred over field injection (see `KatWebServiceConfig`).
- **Configuration binding**: App-specific properties use `@ConfigurationProperties(prefix = "kat")` in `KatProperties.java`. TakCache properties use the `takcache.*` prefix provided by the library.
- **CXF SOAP endpoints**: Defined as Spring `@Bean` of type `jakarta.xml.ws.Endpoint` in `KatWebServiceConfig`. Each impl class lives in its own sub-package under `ws/`. Note: this project uses the Jakarta EE namespace throughout (CXF 4.x), not the legacy `javax.*` namespace.
- **Logging**: Log4j2 with ECS layout for production (`log4j2-ecslogging.xml`), plain for dev (`log4j2.xml`). Switched via `log4j.appender` Helm value. Logback is explicitly excluded — do not add it.
- **Active profile**: `spring.profiles.active=dev` is set in `application.properties` (used by default for local development); production overrides come from environment variables via ConfigMaps.
- **Actuator**: Only `health`, `info`, `metrics`, `prometheus` exposed. Management runs on a separate port (8089) in production.
- **Helm deployment**: Environment config is injected via `envFrom` ConfigMaps and Secrets (see `helm/templates/deployment.yaml`). The `app-of-apps/valuefiles/kat-values.yaml` provides environment-specific overrides.

## Testing

- Unit tests: `*Test.java` in `kat-application/src/test/`
- Integration tests use the embedded `kat-tak-mock` — no external dependencies needed.
- SoapUI tests in `kat-soapui-test/` require both mock and app running (manual workflow).

## Deployment (Kubernetes)

- Docker image: `eclipse-temurin:17-jre-alpine`, non-root user, deployed via Helm.
- Container ports: `8082` (service), `8089` (actuators).
- ConfigMaps supply Spring properties as env vars (`SERVER_PORT`, `TAKCACHE_ENDPOINT_ADDRESS`, etc.).
- Readiness depends on TAK cache being populated (custom health indicator).

## Key Files Reference

| Concern              | File                                                             |
|----------------------|------------------------------------------------------------------|
| App entry point      | `kat-application/src/main/java/se/skltp/tak/KatApplication.java` |
| SOAP endpoint wiring | `...tak/ws/KatWebServiceConfig.java`                             |
| Core business logic  | `...tak/services/impl/TakCacheServiceImpl.java`                  |
| Properties class     | `...tak/config/KatProperties.java`                               |
| Cache reset REST     | `...tak/rest/ResetCacheController.java`                          |
| Health indicator     | `...tak/actuator/TakCacheHealthIndicator.java`                   |
| Info contributor     | `...tak/contributors/developerInfo.java`                         |
| Startup init         | `...tak/init/ApplicationEventListener.java`                      |
| Helm values          | `helm/values.yaml`                                               |
| CI config            | `Jenkins.properties`                                             |


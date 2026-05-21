# Helm Values Reference

This page documents every configurable value in `helm/values.yaml` for the KAT (Katalogtjänst) Helm chart.

---

## repository

| Key          | Description                                                                                                                                        |
|--------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| `repository` | Container image registry prefix (e.g. `docker.drift.inera.se/ntjp/`). Prepended to the image name when constructing the full image reference. **Must be overridden per environment.** |

---

## deployment

General deployment settings for the KAT pod.

| Key                            | Description                                                                                                                                             |
|--------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| `deployment.replicaCount`      | Number of pod replicas to run.                                                                                                                          |
| `deployment.elasticGrokFilter` | Value injected as a label for Elastic log pipeline grok-filter matching.                                                                                |
| `deployment.resources`         | Kubernetes resource requests and limits (`cpu`, `memory`). Set to `{}` to omit. Structure follows the standard `requests.cpu`, `requests.memory`, `limits.cpu`, `limits.memory` format. |

---

## container

| Key                          | Description                                                                                     |
|------------------------------|-------------------------------------------------------------------------------------------------|
| `container.image.tag`        | Image tag / version for the KAT container. Defaults to the Helm chart `appVersion` when not set. |
| `container.image.pullPolicy` | Kubernetes image pull policy (`Always`, `IfNotPresent`, `Never`).                               |

---

## vip

Backwards-compatible Kubernetes Service for environments that reference KAT by a legacy service name.

| Key        | Description                                                  |
|------------|--------------------------------------------------------------|
| `vip.name` | Name of the backwards-compatible Kubernetes Service resource. |
| `vip.port` | Port exposed by the VIP Service.                             |

---

## paths

File-system paths inside the KAT container. Used by other values via Go template expressions (e.g. `{{ .Values.paths.log }}`).

| Key          | Description                                       |
|--------------|---------------------------------------------------|
| `paths.base` | Base installation directory for KAT.              |
| `paths.log`  | Directory for application and access log files.   |

---

## server

Embedded Tomcat web server settings.

| Key               | Description                                                                                  |
|-------------------|----------------------------------------------------------------------------------------------|
| `server.port`     | Port for the main HTTP service (SOAP endpoints and REST). Maps to `SERVER_PORT`.             |
| `server.shutdown` | Shutdown mode (`graceful` or `immediate`). Maps to `SERVER_SHUTDOWN`.                        |

### server.tomcat.accesslog

Tomcat access log configuration.

| Key                                          | Description                                                                                       |
|----------------------------------------------|---------------------------------------------------------------------------------------------------|
| `server.tomcat.accesslog.buffered`           | Whether access log writes are buffered.                                                           |
| `server.tomcat.accesslog.directory`          | Directory for access log files. Supports template expressions (e.g. `{{ .Values.paths.log }}`).   |
| `server.tomcat.accesslog.enabled`            | Enable or disable Tomcat access logging.                                                          |
| `server.tomcat.accesslog.fileDateFormat`     | Date format suffix appended to access log filenames.                                              |
| `server.tomcat.accesslog.pattern`            | Access log format pattern (e.g. `common`, `combined`).                                            |
| `server.tomcat.accesslog.prefix`             | Filename prefix for access log files.                                                             |
| `server.tomcat.accesslog.renameOnRotate`     | Whether to rename the current log file on rotation.                                               |
| `server.tomcat.accesslog.requestAttributesEnabled` | Whether request attributes (e.g. remote user) are included in the log.                      |
| `server.tomcat.accesslog.rotate`             | Enable daily rotation of access log files.                                                        |
| `server.tomcat.accesslog.suffix`             | Filename suffix for access log files.                                                             |
| `server.tomcat.accesslog.maxDays`            | Maximum number of days to retain rotated access log files.                                        |

---

## management

Spring Boot Actuator / management endpoint configuration.

| Key                                      | Description                                                                                 |
|------------------------------------------|---------------------------------------------------------------------------------------------|
| `management.endpointsWebExposureInclude` | List of Actuator endpoint IDs to expose over HTTP (e.g. `health`, `metrics`, `prometheus`). |
| `management.serverPort`                  | Port for the management / Actuator HTTP server. Maps to `MANAGEMENT_SERVER_PORT`.           |

---

## cxf

Apache CXF SOAP framework settings.

| Key        | Description                                                                 |
|------------|-----------------------------------------------------------------------------|
| `cxf.path` | Base context path for all CXF SOAP endpoints. Maps to `CXF_PATH`.          |

---

## kat

KAT application-specific settings — SOAP endpoint sub-paths and REST endpoints.

| Key                                              | Description                                                                                                  |
|--------------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| `kat.getSupportedServiceContractsV2Path`         | Sub-path (relative to `cxf.path`) for the `GetSupportedServiceContracts/v2` SOAP endpoint.                   |
| `kat.getLogicalAddresseesByServiceContractV2Path`| Sub-path (relative to `cxf.path`) for the `GetLogicalAddresseesByServiceContract/v2` SOAP endpoint.          |
| `kat.resetcachePath`                             | Path for the REST cache-reset endpoint. Maps to `KAT_RESETCACHE_PATH`.                                      |

---

## takcache

Connection and caching settings for the TAK (Tjänsteadresseringskatalogen) service.

| Key                           | Description                                                                                                          |
|-------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `takcache.useBehorighetCache` | Enable the in-memory *behörighet* (authorization) cache. Maps to `TAKCACHE_USE_BEHORIGHET_CACHE`.                    |
| `takcache.useVagvalCache`     | Enable the in-memory *vägval* (routing) cache. Maps to `TAKCACHE_USE_VAGVAL_CACHE`.                                  |
| `takcache.persistentFileName` | File path for the persisted local TAK cache. Used as fallback when the remote TAK service is unreachable.            |
| `takcache.endpointAddress`    | URL of the TAK web service used to populate the local cache. **Override per environment.**                            |
| `takcache.headerUserAgent`    | Value of the `User-Agent` header sent to the TAK service. Maps to `TAKCACHE_HEADER_USER_AGENT`.                      |

---

## log4j

Log4j2 configuration.

| Key              | Description                                                                                                          |
|------------------|----------------------------------------------------------------------------------------------------------------------|
| `log4j.appender` | Log appender mode. `ECS` uses the ECS JSON layout (`log4j2-ecslogging.xml`); omit or set to another value for plain text. Maps to `LOG4J_APPENDER`. |

---

## environment

ConfigMap and Secret references injected into the KAT container as environment variables.

| Key                                          | Description                                                                                         |
|----------------------------------------------|-----------------------------------------------------------------------------------------------------|
| `environment.variables._default_config_maps` | List of ConfigMap names whose keys are injected as environment variables by default.                |
| `environment.variables.config_maps`          | Additional ConfigMap names to inject. Override per environment.                                     |
| `environment.variables.secrets`              | Kubernetes Secret names whose keys are injected as environment variables. Override per environment. |

---

## probes

Kubernetes health probes for the KAT container.

### probes.startupProbe

| Key                                       | Description                                                                                                  |
|-------------------------------------------|--------------------------------------------------------------------------------------------------------------|
| `probes.startupProbe.httpGet.path`        | HTTP path to probe (Actuator readiness endpoint).                                                            |
| `probes.startupProbe.httpGet.port`        | Named or numeric port to probe.                                                                              |
| `probes.startupProbe.httpGet.scheme`      | Protocol scheme (`HTTP` or `HTTPS`).                                                                         |
| `probes.startupProbe.initialDelaySeconds` | Seconds to wait before the first probe after container start.                                                |
| `probes.startupProbe.periodSeconds`       | Seconds between probe attempts.                                                                              |
| `probes.startupProbe.timeoutSeconds`      | Seconds before a single probe attempt times out.                                                             |
| `probes.startupProbe.successThreshold`    | Number of consecutive successes required to mark the container as started.                                   |
| `probes.startupProbe.failureThreshold`    | Number of consecutive failures before the container is restarted.                                            |

### probes.livenessProbe

| Key                                        | Description                                                            |
|--------------------------------------------|------------------------------------------------------------------------|
| `probes.livenessProbe.httpGet.path`        | HTTP path to probe (Actuator liveness endpoint).                       |
| `probes.livenessProbe.httpGet.port`        | Named or numeric port to probe.                                        |
| `probes.livenessProbe.httpGet.scheme`      | Protocol scheme (`HTTP` or `HTTPS`).                                   |
| `probes.livenessProbe.initialDelaySeconds` | Seconds to wait before the first liveness probe.                       |
| `probes.livenessProbe.periodSeconds`       | Seconds between liveness probes.                                       |
| `probes.livenessProbe.timeoutSeconds`      | Seconds before a probe attempt times out.                              |
| `probes.livenessProbe.failureThreshold`    | Consecutive failures before the container is killed and restarted.     |
| `probes.livenessProbe.successThreshold`    | Consecutive successes to clear a failed state.                         |

### probes.readinessProbe

| Key                                          | Description                                                          |
|----------------------------------------------|----------------------------------------------------------------------|
| `probes.readinessProbe.httpGet.path`         | HTTP path to probe (Actuator readiness endpoint).                    |
| `probes.readinessProbe.httpGet.port`         | Named or numeric port to probe.                                      |
| `probes.readinessProbe.httpGet.scheme`       | Protocol scheme (`HTTP` or `HTTPS`).                                 |
| `probes.readinessProbe.initialDelaySeconds`  | Seconds to wait before the first readiness probe.                    |
| `probes.readinessProbe.periodSeconds`        | Seconds between readiness probes.                                    |
| `probes.readinessProbe.timeoutSeconds`       | Seconds before a probe attempt times out.                            |
| `probes.readinessProbe.failureThreshold`     | Consecutive failures before the pod is removed from service endpoints. |
| `probes.readinessProbe.successThreshold`     | Consecutive successes to mark the pod as ready.                      |

---

## See Also

- [AGENTS.md](../AGENTS.md) — Project overview, architecture, build instructions, and conventions.
- `helm/templates/kat-configmap-default.yaml` — ConfigMap template that consumes these values.
- `helm/templates/deployment.yaml` — Deployment template referencing probes, resources, and environment.


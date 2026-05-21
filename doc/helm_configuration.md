# App-of-Apps Helm Configuration Guide

This guide explains how to deploy **KAT** using the **app-of-apps** pattern with ArgoCD. It is aimed at operators setting up a new environment from scratch.

For per-key documentation of all KAT Helm values, see [Helm Values Reference].

---

## 1. The App-of-Apps Pattern

The SKLTP platform uses the [ArgoCD App-of-Apps](https://argo-cd.readthedocs.io/en/stable/operator-manual/cluster-bootstrapping/#app-of-apps-pattern) approach to manage multiple applications from a single Git repository.

### How It Works

You create a **central repository** (the "app-of-apps" repo) containing a Helm chart. When rendered, this chart produces an ArgoCD **ApplicationSet** resource that drives the deployment of all platform services — including KAT.

The **ApplicationSet** uses a **list generator** that iterates over an `applications[]` list in `values.yaml`. For each entry it:

1. Reads `valuefiles/common-values.yaml` (shared settings: image registry, ingress hostnames, SKLTP instance ID).
2. Reads `valuefiles/<name>-values.yaml` (application-specific overrides).
3. Merges both into the `helm.values` field of the generated ArgoCD Application.
4. Points ArgoCD at the application's own Git repository + `helm/` path + pinned tag.

ArgoCD then renders each application's Helm chart (e.g. `kat/helm/`) with the merged values and syncs the resulting Kubernetes resources to the target cluster.

### Value Precedence (highest → lowest)

1. `valuefiles/kat-values.yaml` (environment-specific overrides)
2. `valuefiles/common-values.yaml` (shared across all apps)
3. `kat/helm/values.yaml` (chart defaults in the KAT repository)

---

## 2. Setting Up Your App-of-Apps Repository

Create a new Git repository with the following structure:

```
my-platform-apps/
├── Chart.yaml
├── values.yaml
├── valuefiles/
│   ├── common-values.yaml
│   └── kat-values.yaml
└── templates/
    ├── applicationset.yaml
    ├── configmaps/
    │   ├── common-configmap.yaml
    │   └── kat-configmap.yaml
    └── secrets/
        └── (SealedSecrets or references)
```

### 2.1 `Chart.yaml`

```yaml
apiVersion: v2
name: my-platform-applicationset
description: App-of-apps chart for deploying SKLTP services
type: application
version: 0.1.0
appVersion: "0.0.1"
```

### 2.2 `values.yaml` — Cluster & Application List

This is the top-level values file for your app-of-apps chart. It defines the target cluster/namespace and lists which applications to deploy.

```yaml
destination:
  cluster: a                              # CHANGE: cluster identifier
  environment: myenv                      # CHANGE: environment name (dev, qa, prod, etc.)
  project: my-platform-project            # CHANGE: ArgoCD project name
  namespace: my-platform-myenv            # CHANGE: target Kubernetes namespace
  server: https://kubernetes.default.svc

repo:
  path: helm                              # Path within each app repo where Helm chart lives

applications:
- name: kat
  repourl: https://github.com/skltp/kat.git
  targetrevision: v1.5.0                  # CHANGE: pin to desired KAT release tag
```

### 2.3 `templates/applicationset.yaml` — The List Generator

This is the core template that generates one ArgoCD Application per entry in `applications[]`. It merges `common-values.yaml` and the per-app values file into the Helm values for each application.

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: {{ .Chart.Name }}-{{ .Values.destination.environment }}
  namespace: argocd
spec:
  generators:
  - list:
      elements:
      {{- range .Values.applications }}
      - application: {{ .name }}-{{ $.Values.destination.environment }}
        repourl: {{ .repourl }}
        targetrevision: {{ .targetrevision }}
        app-values: | {{ $.Files.Get "valuefiles/common-values.yaml" | nindent 10 }}
          {{ $.Files.Get (printf "valuefiles/%s-values.yaml" .name) | nindent 10 }}
      {{- end }}
  template:
    metadata:
      name: '{{`{{application}}`}}'
    spec:
      destination:
        namespace: {{ .Values.destination.namespace }}
        server: {{ .Values.destination.server }}
      project: {{ .Values.destination.project }}
      source:
        repoURL: '{{`{{repourl}}`}}'
        path: {{ .Values.repo.path }}
        targetRevision: '{{`{{targetrevision}}`}}'
        helm:
          values: '{{`{{app-values}}`}}'
```

> **Key points about this template:**
> - It uses `$.Files.Get` to read the value files from your app-of-apps repo and inject them as inline Helm values.
> - The double-brace escaping (`{{` `` ` `` `{{...}}` `` ` `` `}}`) is required because the inner `{{application}}`, `{{repourl}}`, etc. are ArgoCD ApplicationSet template parameters — not Go template expressions.
> - Each application gets its own ArgoCD Application resource pointing at the application's own Git repo and Helm chart.

---

## 3. Minimal KAT Deployment Configuration

### 3.1 `valuefiles/common-values.yaml` — Shared Values

Settings consumed by KAT and potentially other SKLTP services you deploy:

```yaml
repository: registry.example.com/skltp/        # CHANGE: your container registry prefix
```

### 3.2 `valuefiles/kat-values.yaml` — KAT-Specific Overrides

Minimum overrides for KAT:

| Concern               | Keys to set                                                          |
|-----------------------|----------------------------------------------------------------------|
| Scaling               | `deployment.replicaCount`, `deployment.resources`                    |
| Image                 | `container.image.tag`                                                |
| VIP service name      | `vip.name`                                                           |
| Environment variables | `environment.variables.config_maps`, `environment.variables.secrets` |

See section 4 for the full example.

### 3.3 Kubernetes Resources (Created via `templates/`)

These must exist in the target namespace before (or alongside) the KAT deployment. Create them as additional templates in your app-of-apps chart:

| Resource                     | Purpose                                                                        |
|------------------------------|--------------------------------------------------------------------------------|
| `ConfigMap/common-configmap` | TAK endpoint address shared across services.                                   |
| `ConfigMap/kat-configmap`    | KAT-specific environment variables (e.g. `JAVA_OPTS` for CXF tuning).         |
| `Secret/kat-secrets`         | Any sensitive values KAT needs at runtime (placeholder if none required yet).  |
| `Secret/regcred`             | Image-pull credentials for the container registry.                             |

> **Note:** Secrets should be provisioned via SealedSecrets, external-secrets-operator, or your organization's secret management solution. Never commit plaintext secrets to Git.

#### About `regcred` (Image-Pull Secret)

The `regcred` Secret is a Kubernetes `kubernetes.io/dockerconfigjson` secret that stores credentials for authenticating against the container image registry. Without it, the kubelet cannot pull the KAT container image and pods will fail with `ErrImagePull` / `ImagePullBackOff`.

The KAT Helm chart references this secret via `imagePullSecrets`:

```yaml
imagePullSecrets:
  - name: regcred
```

**Creating `regcred` manually** (for testing/bootstrapping):

```bash
kubectl create secret docker-registry regcred \
  --namespace=<your-namespace> \
  --docker-server=registry.example.com \
  --docker-username=<service-account> \
  --docker-password=<token-or-password>
```

**In production**, use SealedSecrets or an external-secrets-operator to manage this secret declaratively. The secret must exist in the same namespace as the KAT Deployment.

---

## 4. Complete Minimal App-of-Apps Example

Below is a self-contained set of all files needed in your app-of-apps repository to deploy KAT. Each file is separated by `---` with a header comment.

> Replace placeholder values (marked with `# CHANGE`) with your environment-specific settings.

```yaml
##############################################################################
# FILE: Chart.yaml
##############################################################################
apiVersion: v2
name: my-platform-applicationset
description: App-of-apps chart for deploying SKLTP services
type: application
version: 0.1.0
appVersion: "0.0.1"
---
##############################################################################
# FILE: values.yaml — Cluster & application list
##############################################################################
destination:
  cluster: a                                    # CHANGE: cluster identifier
  environment: myenv                            # CHANGE: environment name
  project: my-platform-project                  # CHANGE: ArgoCD project
  namespace: my-platform-myenv                  # CHANGE: target namespace
  server: https://kubernetes.default.svc

repo:
  path: helm

applications:
- name: kat
  repourl: https://github.com/skltp/kat.git
  targetrevision: v1.5.0                       # CHANGE: desired KAT version
---
##############################################################################
# FILE: templates/applicationset.yaml — The list generator
##############################################################################
apiVersion: argoproj.io/v1alpha1
kind: ApplicationSet
metadata:
  name: {{ .Chart.Name }}-{{ .Values.destination.environment }}
  namespace: argocd
spec:
  generators:
  - list:
      elements:
      {{- range .Values.applications }}
      - application: {{ .name }}-{{ $.Values.destination.environment }}
        repourl: {{ .repourl }}
        targetrevision: {{ .targetrevision }}
        app-values: | {{ $.Files.Get "valuefiles/common-values.yaml" | nindent 10 }}
          {{ $.Files.Get (printf "valuefiles/%s-values.yaml" .name) | nindent 10 }}
      {{- end }}
  template:
    metadata:
      name: '{{`{{application}}`}}'
    spec:
      destination:
        namespace: {{ .Values.destination.namespace }}
        server: {{ .Values.destination.server }}
      project: {{ .Values.destination.project }}
      source:
        repoURL: '{{`{{repourl}}`}}'
        path: {{ .Values.repo.path }}
        targetRevision: '{{`{{targetrevision}}`}}'
        helm:
          values: '{{`{{app-values}}`}}'
---
##############################################################################
# FILE: valuefiles/common-values.yaml — Shared values for all applications
##############################################################################
repository: registry.example.com/skltp/         # CHANGE: your registry prefix
---
##############################################################################
# FILE: valuefiles/kat-values.yaml — KAT-specific overrides
##############################################################################
deployment:
  replicaCount: 1
  elasticGrokFilter: camel
  resources:
    limits:
      memory: 512Mi
    requests:
      cpu: 50m
      memory: 512Mi

container:
  image:
    tag:                                        # CHANGE: override to pin a specific image tag

# Environment-specific backwards-compatible service name
vip:
  name: ind-dtjp-vp-vip                         # CHANGE: legacy service name for your environment

environment:
  variables:
    config_maps:
      - common-configmap
      - kat-configmap
    secrets:
      - kat-secrets
---
##############################################################################
# FILE: templates/configmaps/common-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: common-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  TAKCACHE_ENDPOINT_ADDRESS: "http://tak-services-svc:8080/tak-services/SokVagvalsInfo/v2"
---
##############################################################################
# FILE: templates/configmaps/kat-configmap.yaml
##############################################################################
apiVersion: v1
kind: ConfigMap
metadata:
  name: kat-configmap
  namespace: {{ .Values.destination.namespace }}
data:
  JAVA_OPTS: '-Dorg.apache.cxf.stax.maxChildElements=150000'
---
##############################################################################
# FILE: templates/secrets/kat-secrets.yaml (placeholder — use SealedSecret)
##############################################################################
# apiVersion: bitnami.com/v1alpha1
# kind: SealedSecret
# metadata:
#   name: kat-secrets
#   namespace: {{ .Values.destination.namespace }}
# spec:
#   encryptedData:
#     SOME_SECRET: <sealed-value>
---
##############################################################################
# FILE: templates/secrets/regcred.yaml (placeholder — use SealedSecret)
##############################################################################
# apiVersion: bitnami.com/v1alpha1
# kind: SealedSecret
# metadata:
#   name: regcred
#   namespace: {{ .Values.destination.namespace }}
# spec:
#   encryptedData:
#     .dockerconfigjson: <sealed-value>
#   template:
#     type: kubernetes.io/dockerconfigjson
```

---

## 5. Deployment Workflow

1. **Create your app-of-apps repository** — Use the structure and files from section 4.
2. **Provision secrets** — Create SealedSecrets (or use your secrets operator) for image-pull credentials and any KAT-specific secrets.
3. **Register in ArgoCD** — Create an ArgoCD Application that points at your app-of-apps repository (the "root" application). ArgoCD will render the chart, producing the ApplicationSet.
4. **Sync** — ArgoCD detects the ApplicationSet, generates one Application per entry in `applications[]`, renders each app's Helm chart with the merged values, and applies the resources to the cluster.
5. **Verify** — Check pod status, Actuator health (`/actuator/health` on port 8089), and TAK cache initialisation logs.

### Registering the Root Application in ArgoCD

The "root application" is the single ArgoCD Application that bootstraps everything else. It tells ArgoCD where your app-of-apps repository lives and how to render it. Without this, ArgoCD has no knowledge of your chart.

You can create the root application declaratively or via the ArgoCD UI/CLI:

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: my-platform-apps
  namespace: argocd
spec:
  project: my-platform-project                                       # CHANGE: must exist in ArgoCD
  source:
    repoURL: https://git.example.com/my-org/my-platform-apps.git     # CHANGE: your app-of-apps repo
    path: .                                                          # Chart.yaml is at the repo root
    targetRevision: main                                             # CHANGE: branch or tag to track
  destination:
    server: https://kubernetes.default.svc                           # The cluster ArgoCD runs on
    namespace: argocd                                                # ApplicationSet is created here
  syncPolicy:
    automated:
      prune: true       # Remove resources ArgoCD no longer manages
      selfHeal: true    # Revert manual drift automatically
```

Once this root Application is synced, ArgoCD renders your `Chart.yaml` + `values.yaml` + templates, producing the ApplicationSet which in turn creates the KAT Application (and any other applications you list).

---

## 6. Additional Override Examples

### 6.1 Resource Limits

```yaml
deployment:
  replicaCount: 2
  resources:
    limits:
      memory: 1Gi
    requests:
      cpu: 100m
      memory: 1Gi
```

### 6.2 Log4j Appender Mode

Switch between ECS JSON logging (production) and plain-text logging (development):

```yaml
# Production — ECS JSON format (default in chart)
log4j:
  appender: ECS

# Development — plain text
log4j:
  appender: PLAIN
```

### 6.3 CXF Tuning via JAVA_OPTS

Increase the limit on XML child elements for large TAK responses:

```yaml
# In templates/configmaps/kat-configmap.yaml:
data:
  JAVA_OPTS: '-Dorg.apache.cxf.stax.maxChildElements=200000'
```

---

## See Also

- [Helm Values Reference] — complete per-key documentation of `helm/values.yaml`.
- [AGENTS.md](../AGENTS.md) — project overview, architecture, build instructions, and conventions.

[//]: # (Reference links)

[Helm Values Reference]: <helm_values.md>


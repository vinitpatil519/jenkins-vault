# Diagram Atlas

> Every key picture in one place, for whiteboard revision. Hand-drawn (Miro-style) look where GitHub's Mermaid supports it; plain Mermaid elsewhere.

[← README](../README.md)

---

## 1. Jenkins architecture

```mermaid
---
config:
  look: handDrawn
---
flowchart TB
    Dev([Developer]) -->|push| SCM[(Git)]
    SCM -->|webhook| C
    subgraph C [Controller]
      UI[UI / API] --- Q[Queue] --- S[Scheduler]
      CPS[Pipeline engine] --- H[(JENKINS_HOME)]
    end
    S -->|remoting| A1[VM agent]
    S -->|remoting| A2[K8s pod agent]
    S -->|remoting| A3[Docker agent]
    A2 --> R[(Registry / Nexus)]
```

## 2. Build lifecycle

```mermaid
sequenceDiagram
    autonumber
    participant G as Git
    participant C as Controller
    participant K as Kubernetes
    participant A as Agent pod
    G->>C: webhook
    C->>C: load Jenkinsfile, start CPS
    C->>K: create pod
    K->>A: start jnlp + tools
    A->>C: connect
    C->>A: run sh steps
    A-->>C: logs + exit code
    C->>K: delete pod
    C->>G: commit status
```

## 3. CI/CD flow (end to end)

```mermaid
---
config:
  look: handDrawn
---
flowchart LR
    C[Commit] --> B[Build] --> T[Unit tests] --> S[SAST/SCA/secrets] --> I[Image] --> IS[Image scan + SBOM] --> SG[Sign]
    SG --> G[GitOps bump] --> ST[Staging] --> E[E2E + DAST] --> AP{Approve} --> P[Prod canary] --> F[100%]
```

## 4. Agent types

```mermaid
flowchart LR
    C[Controller] -->|SSH :22| V[Static VM]
    I[Inbound agent] -->|TCP 50000 / WebSocket 443| C
    C -->|K8s API| K[(Cluster)] --> P[Ephemeral pods]
    C -->|Cloud API| E[EC2 / Azure VM]
```

## 5. Everything as code

```mermaid
---
config:
  look: handDrawn
---
flowchart TB
    subgraph Git
      IMG[Dockerfile + plugins.txt]
      CASC[jenkins.yaml JCasC]
      DSL[seed/*.groovy Job DSL]
      LIB[platform-lib]
      JF[Jenkinsfile per repo]
    end
    IMG --> CTRL[Controller image]
    CASC --> CTRL
    CTRL --> SEED[Seed job] --> DSL
    DSL --> JOBS[Folders + multibranch]
    JOBS --> JF --> LIB
```

## 6. Credentials flow

```mermaid
flowchart LR
    V[(Vault / AWS SM)] -->|ESO| KS[K8s Secret] -->|env| C[Controller]
    C -->|JCasC ${VAR}| CR[Credential store]
    CR -->|withCredentials| P[Step]
    SA[Agent SA] -->|IRSA / OIDC| AWS[(Cloud APIs, no keys)]
```

## 7. Security layers

```mermaid
---
config:
  look: handDrawn
---
flowchart TB
    N[Network: VPN, allow-list, only webhook public] --> A[AuthN: SSO + MFA]
    A --> Z[AuthZ: roles by IdP group]
    Z --> CT[Controller: 0 executors, patched, minimal plugins]
    CT --> AG[Agents: ephemeral, non-root, NetworkPolicy]
    AG --> SE[Secrets: external manager, folder scope, masked]
    SE --> SC[Supply chain: scan, SBOM, sign, admission policy]
    SC --> AU[Audit: logs → SIEM]
```

## 8. Push vs GitOps deploy

```mermaid
flowchart TB
    subgraph Push
      J1[Jenkins] -->|kubectl / helm + cluster creds| K1[(Cluster)]
    end
    subgraph GitOps
      J2[Jenkins] -->|commit tag| R[(Env repo)]
      A[Argo CD in cluster] -->|pull| R
      A -->|apply| K2[(Cluster)]
    end
```

## 9. Deployment strategies

```mermaid
flowchart LR
    subgraph Rolling
      R1[v1 v1 v1] --> R2[v2 v1 v1] --> R3[v2 v2 v1] --> R4[v2 v2 v2]
    end
    subgraph BlueGreen
      LB[LB] -->|100%| B[Blue v1]
      LB -.->|switch| G[Green v2]
    end
    subgraph Canary
      LB2[LB] -->|95%| S[v1]
      LB2 -->|5%| CN[v2] --> M{metrics ok?}
    end
```

## 10. DevSecOps gates

```mermaid
flowchart LR
    PR[PR] --> GL[gitleaks] --> SQ[SonarQube gate] --> TF[Trivy fs/config] --> OK{pass?}
    OK -->|no| X[block merge]
    OK -->|yes| M[main] --> TI[Trivy image] --> SB[SBOM] --> CS[cosign] --> D[deploy] --> Z[ZAP]
```

## 11. Monitoring stack

```mermaid
flowchart LR
    J[/prometheus endpoint/] --> P[(Prometheus)] --> G[Grafana]
    P --> AM[Alertmanager] --> SL[Slack / PagerDuty]
    J2[Jenkins logs] --> L[(Loki)] --> G
    J3[OTel plugin] --> T[(Tempo)] --> G
```

## 12. Scaling: shard controllers, share agents

```mermaid
flowchart TB
    C1[Controller A] --> POOL[(Shared K8s agent pool<br/>Karpenter spot)]
    C2[Controller B] --> POOL
    C3[Controller C] --> POOL
    BASE[(Shared JCasC base + platform-lib)] -.-> C1 & C2 & C3
```

## 13. Backup & DR

```mermaid
flowchart LR
    G[(Git: config)] --> R[Rebuild controller]
    S[(Snapshots: history + secrets)] --> R
    I[(Image registry: core + plugins)] --> R
    R --> V[Verify: login, agents, smoke job, webhooks]
```

## 14. Pipeline anatomy (declarative)

```mermaid
flowchart TD
    P[pipeline] --> A[agent]
    P --> O[options · parameters · environment · triggers · tools]
    P --> SS[stages]
    SS --> S[stage] --> W[when] & ST[steps] & PA[parallel] & MX[matrix] & SP[post]
    P --> PO[post: always · success · failure · unstable · fixed · regression · cleanup]
```

## 15. Mega setup

See [04-mega-setup/README.md](../04-mega-setup/README.md#1-target-architecture) for the full platform diagram.

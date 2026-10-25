# 22 · Jenkins vs Other CI/CD Tools

> **Goal:** answer "why Jenkins?", "why not GitHub Actions?", and map Jenkins concepts to every other tool.

[← Troubleshooting](21-troubleshooting.md) · [Back to README →](../README.md)

---

## 1. Comparison

| | Jenkins | GitHub Actions | GitLab CI | Tekton | Argo CD |
|--|---------|----------------|-----------|--------|---------|
| Hosting | Self-hosted | SaaS (+ self-hosted runners) | SaaS / self-managed | K8s-native | K8s-native |
| Config | Groovy `Jenkinsfile` | YAML workflows | YAML `.gitlab-ci.yml` | CRDs (Task, Pipeline) | Git manifests |
| Extensibility | ~2000 plugins | Marketplace actions | Built-in features, CI components | Tasks catalog | Plugins (CMP) |
| Strength | Flexibility, any SCM, on-prem, complex flows | Zero-ops, GitHub-native, huge ecosystem | All-in-one DevSecOps | Cloud-native, serverless pipelines | GitOps CD, drift |
| Weakness | Ops burden, plugin hell, security patching, Groovy quirks | Vendor lock-in, cost at scale, YAML limits | Tied to GitLab | Verbose, DIY UI | CD only |
| CI or CD | Both | Both | Both | CI (+CD) | CD |

## 2. When Jenkins wins
- On-prem / air-gapped / regulated environments.
- Multiple SCMs (GitHub + Bitbucket + Perforce + SVN).
- Very complex orchestration, custom hardware (GPU, devices, mainframe), long-running jobs.
- Existing big investment (shared libs, thousands of jobs).
- Full control of data, cost at very high build volume.

## 3. When it doesn't
- Small team on GitHub, cloud-native → GitHub Actions.
- Team using GitLab → GitLab CI.
- K8s deploys → pair any CI with Argo CD/Flux for CD.

## 4. Concept mapping

| Jenkins | GitHub Actions | GitLab CI | Azure DevOps | Tekton |
|---------|----------------|-----------|--------------|--------|
| Jenkinsfile | `.github/workflows/*.yml` | `.gitlab-ci.yml` | `azure-pipelines.yml` | Pipeline CRD |
| Stage | Job | Stage | Stage | Task (in Pipeline) |
| Step | Step | Script line | Step/Task | Step |
| Agent / label | `runs-on` | runner tags | pool / demands | Pod |
| Shared library | Reusable workflow / composite action | `include` / CI components | Templates | Task catalog |
| Credentials | Secrets / OIDC | CI/CD variables (masked/protected) | Variable groups / service connections | K8s Secrets |
| `input` | Environment protection rules | `when: manual` | Approvals & checks | Custom |
| `stash` | `actions/upload-artifact` | `artifacts:` | publish/download artifact | Workspaces |
| Multibranch | Triggers `on: push/pull_request` | Pipelines per branch/MR | Branch triggers | Triggers (EventListener) |
| Plugins | Actions | Built-ins | Extensions | Tasks |
| `post { always }` | `if: always()` | `after_script` / `when: always` | `condition: always()` | `finally` |
| Matrix | `strategy.matrix` | `parallel: matrix` | `strategy: matrix` | Matrix |

## 5. GitHub Actions equivalent of a Jenkinsfile (for reference)

```yaml
name: ci
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    permissions: { contents: read, id-token: write }
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: '21', cache: maven }
      - run: mvn -B verify
      - uses: aquasecurity/trivy-action@0.28.0   # pin to a full commit SHA in real use
        with: { scan-type: fs, severity: CRITICAL, exit-code: '1' }
```

## 6. Interview: "Jenkins vs GitHub Actions?"

> Jenkins: self-hosted, infinitely flexible, plugin ecosystem, any SCM, strong for on-prem and complex orchestration — but we own patching, scaling, plugin compatibility and security. GitHub Actions: managed, tightly integrated with GitHub (PRs, OIDC, environments), fast to adopt — but less control, YAML-only logic, and cost/lock-in at scale. I'd pick based on SCM, compliance, existing investment and team ops capacity — and use GitOps (Argo CD) for K8s CD either way.

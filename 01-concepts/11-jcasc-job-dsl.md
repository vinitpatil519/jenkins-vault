# 11 · Configuration as Code: JCasC + Job DSL

> **Goal:** zero-click Jenkins. Controller config and every job come from Git. Rebuild Jenkins from scratch in minutes.

[← Security](10-security.md) · [Next: Plugins →](12-plugins.md)

---

## 1. The "everything as code" stack

| Layer | Tool | What it codifies |
|-------|------|------------------|
| Infra | Terraform / Helm | Cluster, controller deployment, storage, ingress |
| Image | Dockerfile + `plugins.txt` | Jenkins version + pinned plugins |
| Controller config | **JCasC** (`jenkins.yaml`) | Security, clouds, credentials, tools, global libs |
| Jobs | **Job DSL** (seed job) / Org Folders | Folders, multibranch jobs, views |
| Pipelines | `Jenkinsfile` + shared library | Build logic |

```mermaid
---
config:
  look: handDrawn
---
flowchart LR
    G[(Git: jenkins-config repo)] -->|plugins.txt| IMG[Custom image]
    G -->|jenkins.yaml| CASC[JCasC]
    G -->|seed/*.groovy| SEED[Seed job → Job DSL]
    IMG --> C[Controller boots]
    CASC --> C
    C --> SEED
    SEED --> JOBS[Folders + Multibranch jobs]
    JOBS -->|Jenkinsfile| APP[(App repos)]
    APP -->|@Library| LIB[(Shared lib)]
```

Result: controller is **cattle**. Lose it → redeploy → identical. Only build history needs backup.

---

## 2. JCasC basics

Plugin: `configuration-as-code`. Load from `CASC_JENKINS_CONFIG` (file, directory of YAMLs, or URL). Default: `$JENKINS_HOME/jenkins.yaml`.

```yaml
jenkins:
  systemMessage: "Managed by JCasC — changes in UI will be lost"
  numExecutors: 0
  mode: EXCLUSIVE
  securityRealm:
    oic:                                          # oic-auth plugin
      clientId: jenkins
      clientSecret: "${OIDC_CLIENT_SECRET}"
      wellKnownOpenIDConfigurationUrl: https://sso.example.com/realms/eng/.well-known/openid-configuration
      groupsFieldName: groups
  authorizationStrategy:
    roleBased:
      roles:
        global:
          - name: admin
            permissions: ["Overall/Administer"]
            entries: [{ group: "platform-admins" }]
          - name: viewer
            permissions: ["Overall/Read", "Job/Read"]
            entries: [{ group: "engineering" }]
        items:
          - name: payments-dev
            pattern: "payments/.*"
            permissions: ["Job/Build", "Job/Read", "Job/Cancel", "Job/Workspace"]
            entries: [{ group: "team-payments" }]
  crumbIssuer: standard                           # CSRF on (default). Old `excludeClientIPFromCrumb` key no longer exists → boot fails
  slaveAgentPort: -1                              # disable TCP inbound port (use WebSocket)
  globalNodeProperties:
    - envVars:
        env:
          - key: REGISTRY
            value: 123456789012.dkr.ecr.eu-west-1.amazonaws.com

security:
  scriptApproval:
    approvedSignatures: []                        # keep empty / reviewed
  globalJobDslSecurityConfiguration:
    useScriptSecurity: true

credentials:
  system:
    domainCredentials:
      - credentials:
          - string:
              id: slack-token
              scope: GLOBAL
              secret: "${SLACK_TOKEN}"
          - gitHubApp:
              id: github-app
              appID: "123456"
              privateKey: "${GITHUB_APP_KEY}"

unclassified:
  location:
    url: https://jenkins.example.com/
    adminAddress: platform@example.com
  globalLibraries:
    libraries:
      - name: platform-lib
        defaultVersion: v2
        retriever:
          modernSCM:
            scm:
              git:
                remote: https://github.com/acme/platform-lib.git
                credentialsId: github-app
  slackNotifier:
    teamDomain: acme
    tokenCredentialId: slack-token

tool:
  git:
    installations:
      - name: Default
        home: git

jobs:
  - file: /var/jenkins_home/casc/seed.groovy       # job-dsl script run at boot
```

### Secrets in JCasC

`${VAR}` resolves from (in order): env vars, files in `SECRETS` dir (default `/run/secrets/`), Vault/AWS SM/Azure KV if those plugins configured. Variants: `${VAR:-default}`, `^${VAR}` escapes, `${base64:...}`, `${file:/path}`, `${readFile:/path}`.

### Workflow
1. Click-configure in a **sandbox** Jenkins.
2. **Manage Jenkins → Configuration as Code → View / Download** current config.
3. Copy the relevant blocks into `jenkins.yaml` (exported YAML is verbose — trim).
4. Schema reference: `/configuration-as-code/reference` on your instance.
5. Commit → apply: restart, **Reload existing configuration** button, `POST /configuration-as-code/reload`, or Helm chart's config-reload sidecar (watches ConfigMaps).

Gotcha: JCasC **overwrites** what it manages; things it doesn't mention stay as is (not fully declarative for everything, e.g. jobs created via UI).

---

## 3. Job DSL

Plugin: `job-dsl`. Groovy DSL that generates jobs. A **seed job** runs DSL scripts from Git.

```groovy
// seed/teams.groovy
def teams = [
  [name: 'payments', repos: ['payments-api', 'ledger']],
  [name: 'search',   repos: ['search-api']]
]

teams.each { t ->
  folder(t.name) {
    displayName(t.name.capitalize())
    description("Owned by team-${t.name}")
  }
  t.repos.each { repo ->
    multibranchPipelineJob("${t.name}/${repo}") {
      branchSources {
        branchSource {
          source {
            github {
              id("${t.name}-${repo}")
              repoOwner('acme')
              repository(repo)
              repositoryUrl("https://github.com/acme/${repo}")
              configuredByUrl(true)
              credentialsId('github-app')
              traits {
                gitHubBranchDiscovery { strategyId(1) }          // exclude branches filed as PRs
                gitHubPullRequestDiscovery { strategyId(1) }     // merge with target
                gitHubForkDiscovery {
                  strategyId(1)
                  trust { gitHubTrustPermissions() }
                }
              }
            }
          }
        }
      }
      orphanedItemStrategy { discardOldItems { numToKeep(10) } }
      triggers { periodicFolderTrigger { interval('1d') } }
    }
  }
}

pipelineJob('ops/rotate-certs') {
  parameters { choiceParam('ENV', ['staging', 'prod']) }
  triggers { cron('H 4 * * 1') }
  definition {
    cpsScm {
      scm { git { remote { url('https://github.com/acme/ops.git'); credentials('github-app') }; branch('main') } }
      scriptPath('jobs/rotate-certs/Jenkinsfile')
      lightweight(true)
    }
  }
}
```

- Docs for your exact plugins: `https://<jenkins>/plugin/job-dsl/api-viewer/index.html` (dynamic DSL covers every installed plugin).
- Seed job config: *Process Job DSLs* step → *Look on filesystem* `seed/**/*.groovy`, *Action for removed jobs*: Delete/Disable.
- Seed job itself created by JCasC (`jobs:` block). Chicken-and-egg solved.

### Job DSL vs Org Folders
- **Org Folder**: automatic, zero config per repo, less control.
- **Job DSL**: explicit, any job type, per-team folders/permissions. ⭐ Combine: Job DSL creates team folders (+ creds/RBAC), org-folder/multibranch inside.

---

## 4. Other config tools (know they exist)

| Tool | What |
|------|------|
| `jenkins-plugin-cli` | Install plugins from `plugins.txt` / YAML with versions |
| Jenkins CLI / REST | Scripted changes (`create-job < config.xml`) |
| Groovy init scripts | `$JENKINS_HOME/init.groovy.d/*.groovy` run at boot (pre-JCasC era, still useful) |
| Jenkinsfile Runner | Run a Jenkinsfile in a throwaway Jenkins (CI for CI) |
| Terraform Jenkins provider | Manage jobs/folders via TF (niche) |

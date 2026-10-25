# 09 · Credentials & Secrets

> **Goal:** store, scope, use and rotate secrets without leaking them. Know external secret managers.

[← Agents](08-agents-distributed-builds.md) · [Next: Security →](10-security.md)

---

## 1. Credential types

| Type | Use |
|------|-----|
| **Username with password** | Registry, Nexus, basic auth |
| **Secret text** | API tokens (Sonar, Slack, GitHub PAT) |
| **Secret file** | kubeconfig, GCP JSON key, `.npmrc` |
| **SSH Username with private key** | Git over SSH, SSH agents, Ansible |
| **Certificate** (PKCS#12) | mTLS clients, code signing |
| **GitHub App** | ⭐ GitHub auth (short-lived installation tokens, higher rate limits) |
| **AWS Credentials** | Access key pair (plugin) — prefer IRSA/instance roles |
| **Docker Host Certificate** | Remote Docker TLS |

## 2. Scope & stores

| Scope | Visible to |
|-------|-----------|
| **System** | Jenkins itself only (agent connections, email server). NOT jobs. |
| **Global** | All jobs (and items) |
| **Folder store** | Jobs inside that folder only ⭐ team isolation |
| User store | That user |

Best practice: team credentials in **folder stores**, not Global. Prod deploy credentials only in prod-deploy folder.

---

## 3. How they're stored

- `$JENKINS_HOME/credentials.xml` (+ folder `config.xml`) — values encrypted with AES using `secrets/hudson.util.Secret`, which is encrypted by `secrets/master.key`.
- Anyone with `JENKINS_HOME` (or Script Console) can decrypt everything:

```groovy
// Script Console — proves why admin = god
println(hudson.util.Secret.decrypt('{AQAAABAAAAAQ...}'))
```

→ Restrict admin, Script Console, controller filesystem. Keep 0 executors on controller.

---

## 4. Using credentials in pipelines

```groovy
// Declarative environment
environment { REG = credentials('registry-creds') }   // REG_USR / REG_PSW

// withCredentials — scoped, preferred
withCredentials([
  usernamePassword(credentialsId: 'registry-creds', usernameVariable: 'U', passwordVariable: 'P'),
  string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN'),
  file(credentialsId: 'kubeconfig-staging', variable: 'KUBECONFIG'),
  sshUserPrivateKey(credentialsId: 'deploy-ssh', keyFileVariable: 'KEY', usernameVariable: 'SSH_USER')
]) {
  sh 'echo "$P" | docker login -u "$U" --password-stdin registry.example.com'
  sh 'kubectl get pods'                       // KUBECONFIG env picked up automatically
  sh 'ssh -i "$KEY" "$SSH_USER@host" uptime'
}

// SSH Agent plugin
sshagent(credentials: ['deploy-ssh']) { sh 'git push origin v1.2.0' }

// Docker Pipeline helper
docker.withRegistry('https://registry.example.com', 'registry-creds') { img.push() }
```

### Masking rules
- Jenkins masks the exact secret value in console output (`****`).
- Masking **fails** if the secret is transformed (base64, split, URL-encoded) or printed char by char.
- Never `set -x` with secrets; never `echo` them; never interpolate into Groovy strings (`"${TOKEN}"`) → use `'$TOKEN'`.
- `--password-stdin` instead of `-p`.

---

## 5. External secret managers (⭐ prod)

| Option | How |
|--------|-----|
| **HashiCorp Vault plugin** | `withVault(configuration: [...], vaultSecrets: [[path: 'secret/data/app', secretValues: [[envVar: 'DB_PASS', vaultKey: 'password']]]]) { }` — auth via AppRole/K8s auth |
| **Kubernetes Credentials Provider** | K8s `Secret` with label `jenkins.io/credentials-type` appears as a Jenkins credential (read-only) |
| **AWS Secrets Manager Credentials Provider** | Secrets tagged in ASM appear as credentials |
| **Azure Key Vault / GCP Secret Manager providers** | Same idea |
| **JCasC + env/files** | `${SECRET_NAME}` in YAML resolved from env vars, files (`/run/secrets`), or Vault at startup |
| **External Secrets Operator** | Syncs Vault/ASM → K8s Secret → mounted into Jenkins / used by JCasC |

```mermaid
flowchart LR
    V[(Vault / AWS SM)] -->|ESO sync| KS[K8s Secret]
    KS -->|env / file| J[Jenkins controller]
    J -->|JCasC ${VAR}| CR[Jenkins credentials]
    CR -->|withCredentials| P[Pipeline step]
    V -->|withVault: dynamic, short-lived| P
```

### Even better: no static secrets
- **OIDC / workload identity** from agents: K8s agent SA → AWS IRSA / EKS Pod Identity / GCP Workload Identity → cloud creds with no keys stored.
- **GitHub App** instead of PATs.
- **Vault dynamic secrets** (DB creds valid for 1h).

---

## 6. Rotation & hygiene checklist

- [ ] Credential IDs meaningful (`ecr-push-prod`), with description + owner.
- [ ] Least privilege per credential (push-only registry token, namespace-scoped kubeconfig).
- [ ] Folder-scoped, not global.
- [ ] Rotate on schedule + on staff exit. Prefer short-lived tokens.
- [ ] Audit: *Credentials* plugin shows usage via fingerprints ("Usage tracking").
- [ ] Secret scanning in pipeline (gitleaks/trufflehog) to catch secrets committed to repos.
- [ ] Fork PRs **cannot** access credentials (trust policy in multibranch).

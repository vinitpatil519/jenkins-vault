# Step 08 · GitHub App, Webhooks, Seed Job

[← Kubernetes agents](07-kubernetes-agents.md) · [Next: Shared library →](09-shared-library.md)

---

## 1. GitHub App

Org → Settings → Developer settings → GitHub Apps → New:

| Field | Value |
|-------|-------|
| Webhook URL | `https://jenkins.example.com/github-webhook/` |
| Webhook secret | random, store in Secrets Manager |
| Repo permissions | Contents R, Metadata R, Pull requests R, Checks R/W, Commit statuses R/W, Administration R (optional: auto-manage hooks) |
| Events | Push, Pull request, Repository, Check run |

Generate private key → PKCS#8 → Secrets Manager (`ci/jenkins/github-app`). Install app on the org.

## 2. Expose only the webhook path publicly

Main ingress is IP-allow-listed. Add a second ingress for GitHub:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: jenkins-webhook
  namespace: jenkins
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
    # GitHub hook IP ranges: curl https://api.github.com/meta | jq .hooks
    nginx.ingress.kubernetes.io/whitelist-source-range: "192.30.252.0/22,185.199.108.0/22,140.82.112.0/20,143.55.64.0/20"
spec:
  ingressClassName: nginx
  tls: [{ hosts: [hooks.jenkins.example.com], secretName: jenkins-hooks-tls }]
  rules:
    - host: hooks.jenkins.example.com
      http:
        paths:
          - path: /github-webhook/
            pathType: Exact
            backend: { service: { name: jenkins, port: { number: 8080 } } }
```

## 3. Seed job

1. `seed/teams.yaml` in `jenkins-config` lists teams + repos.
2. JCasC `jobs:` creates `seed` pipeline job that runs `jobDsl targets: 'seed/*.groovy', removedJobAction: 'DELETE'` on every merge.
3. Seed creates team folders + multibranch jobs ([03-templates/job-dsl/seed.groovy](../../03-templates/job-dsl/seed.groovy)).

```groovy
// jenkins-config/jobs/seed/Jenkinsfile
pipeline {
  agent { kubernetes { inheritFrom 'default' } }
  triggers { githubPush() }
  stages {
    stage('Seed') {
      steps { jobDsl targets: 'seed/*.groovy', removedJobAction: 'DELETE', removedViewAction: 'DELETE', lookupStrategy: 'SEED_JOB' }
    }
  }
}
```

## 4. Onboarding a new repo = one PR

```yaml
- name: payments
  repos: [payments-api, ledger, payouts]   # ← add here, merge, done
```

## 5. Branch protection on each app repo

Require check from Jenkins + 1 review + up-to-date branch. Fork PR trust = collaborators only.

✅ Done when: PR on an app repo shows Jenkins check within seconds.

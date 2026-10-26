# Step 05 · Install Controller with Helm + JCasC

[← Secrets](04-secrets.md) · [Next: SSO + RBAC →](06-sso-rbac.md)

---

## 1. Split JCasC into Helm `configScripts`

Each key under `controller.JCasC.configScripts` becomes a ConfigMap. The reload sidecar watches them.

```yaml
controller:
  JCasC:
    configScripts:
      security: |
        jenkins:
          securityRealm: ...          # from casc/jenkins.yaml
          authorizationStrategy: ...
        security: ...
      clouds: |
        jenkins:
          clouds: ...
      credentials: |
        credentials: ...
      unclassified: |
        unclassified: ...
        jobs:
          - script: |
              // seed job that runs seed/*.groovy from jenkins-config
              pipelineJob('seed') { ... }
```

> First boot tip: install with a temporary **local admin** (`securityRealm.local`) until SSO is verified in step 06.

## 2. Install

```bash
helm repo add jenkins https://charts.jenkins.io && helm repo update
helm upgrade --install jenkins jenkins/jenkins -n jenkins -f helm/values.yaml --wait --timeout 15m
kubectl -n jenkins get pods,pvc,ingress
kubectl -n jenkins logs sts/jenkins -c jenkins | grep -iE 'severe|casc|ready' | tail
```

## 3. Verify JCasC

- `https://jenkins.example.com/manage/configuration-as-code/` → **View Configuration** matches Git.
- System message shows "Managed by JCasC".
- Built-in node shows 0 executors.

## 4. Change flow from now on

```mermaid
flowchart LR
    PR[PR to jenkins-config/casc] --> REV[Review] --> M[Merge]
    M --> H[helm upgrade (CI job or Argo CD app)]
    H --> CM[ConfigMap updated] --> SC[Reload sidecar] --> J[JCasC applied, no restart]
```

Manage Jenkins itself with Argo CD (Helm chart + values from `jenkins-config`) for full GitOps.

✅ Done when: UI reachable over HTTPS, JCasC view shows your config.

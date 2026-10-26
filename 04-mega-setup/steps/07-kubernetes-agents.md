# Step 07 · Kubernetes Agents

[← SSO + RBAC](06-sso-rbac.md) · [Next: GitHub + seed jobs →](08-github-seed-jobs.md)

---

## 1. Namespace, RBAC, quotas, network policy, caches

```bash
kubectl apply -f k8s/agents-namespace.yaml
kubectl apply -f k8s/caches-pvc.yaml
```

## 2. Cloud config

`jenkins.clouds.kubernetes` block in JCasC (namespace `jenkins-agents`, `containerCap 200`, `podRetention never`, default template with `jnlp` limits, nodeSelector `workload=ci` + toleration).

## 3. Agent IAM

`jenkins-agent` SA annotated with an IAM role allowing ECR push for `team/*` repos. Builds get AWS creds automatically (no keys).

## 4. Smoke test

Create a throwaway pipeline:

```groovy
pipeline {
  agent { kubernetes { yaml '''
spec:
  containers:
  - name: tools
    image: alpine:3.20
    command: ["sleep"]
    args: ["infinity"]
''' ; defaultContainer 'tools' } }
  stages { stage('hi') { steps { sh 'uname -a; id; cat /etc/os-release | head -2' } } }
}
```

```bash
kubectl -n jenkins-agents get pods -w        # pod appears → Running → Terminating
kubectl get nodeclaims                       # Karpenter adds a spot node if needed
```

## 5. Tuning

| Setting | Value |
|---------|-------|
| `waitForPodSec` | 600 (spot node boot) |
| `idleMinutes` | 0 default; 10 for hot labels |
| Pod `activeDeadlineSeconds` | Guard against zombie pods |
| Image pulls | Pre-pull CI images via ECR pull-through cache + `imagePullPolicy: IfNotPresent` |

✅ Done when: smoke pipeline green, pod deleted after build, node scales down after 5 min idle.

# Step 13 · Backup, DR, Upgrades

[← Monitoring](12-monitoring.md) · [Next: Go-live checklist →](14-go-live-checklist.md)

---

## 1. What protects what

| Asset | Protection | RPO |
|-------|-----------|-----|
| Controller config, jobs, libs | Git (JCasC, seed, platform-lib) | 0 |
| Plugins + core | Image tag in ECR | 0 |
| Secrets | AWS Secrets Manager (versioned) | 0 |
| `secrets/master.key`, build history, users' API tokens | Velero / EBS snapshots hourly → S3 (cross-region copy) | 1 h |

```bash
kubectl apply -f k8s/velero-schedule.yaml
velero backup get | head
```

## 2. DR drill (quarterly)

```bash
# 1. New cluster or namespace
velero restore create --from-backup jenkins-hourly-<timestamp> --include-namespaces jenkins
# 2. Or fresh: helm install with same values + restore PV snapshot
# 3. Verify
curl -fs https://jenkins.example.com/login
# run golden pipeline smoke job, check agents, webhook delivery
```

Record: actual RTO, issues found, update runbook.

## 3. Upgrade cadence

| What | When | How |
|------|------|-----|
| Security advisories | Within 48 h (critical) | Hotfix image |
| Plugins | Weekly | PR bumps `plugins.txt` → staging controller → prod |
| LTS core | Every LTS (12 weeks) | Same flow + read upgrade guide |
| Agent / CI images | Weekly rebuild | Base image patches |

## 4. Staging controller

Second Helm release `jenkins-staging` in namespace `jenkins-staging`: same values + image candidate, JCasC with a `smoke/` folder only. CI job deploys candidate, runs smoke pipelines (one per language template), reports.

## 5. Prod upgrade runbook

```bash
curl -X POST -u admin:$TOKEN https://jenkins.example.com/quietDown       # stop new builds
velero backup create jenkins-pre-upgrade --include-namespaces jenkins --wait
helm upgrade jenkins jenkins/jenkins -n jenkins -f helm/values.yaml       # new image tag
kubectl -n jenkins rollout status sts/jenkins --timeout=15m
kubectl -n jenkins logs sts/jenkins -c jenkins | grep -E 'SEVERE|Failed Loading' || echo clean
# smoke pipelines, then announce
```

Rollback: previous image tag via `helm rollback jenkins`; restore `jenkins-pre-upgrade` only if data format changed.

✅ Done when: one DR drill completed with documented RTO.

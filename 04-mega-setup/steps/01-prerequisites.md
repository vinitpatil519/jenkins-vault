# Step 01 · Prerequisites & Repo Layout

[← Mega setup](../README.md) · [Next: Cluster add-ons →](02-cluster-addons.md)

---

## Have these

| Need | Notes |
|------|-------|
| EKS cluster `platform-ci` (1.30+) | Separate from workload clusters. Node group `workload=platform` for controller |
| CLI tools | `kubectl`, `helm` 3.14+, `aws`, `eksctl` (optional), `jq`, `yq`, `cosign`, `gh` |
| Domain + DNS | `jenkins.example.com`, `sonar.example.com`, `grafana.example.com` |
| IdP | Keycloak / Okta / Entra ID with groups: `jenkins-admins`, `engineering`, `team-*`, `release-managers` |
| GitHub org admin | To create the GitHub App |
| AWS | ECR repos, Secrets Manager, S3 bucket for backups, IAM for IRSA |

## Create the Git repos

```bash
gh repo create acme/jenkins-config --private
gh repo create acme/platform-lib  --private
gh repo create acme/gitops        --private
```

Seed them from this vault:

```bash
VAULT=~/jenkins-vault
mkdir -p jenkins-config/{image,helm,casc,seed,k8s,jobs} && cd jenkins-config
cp $VAULT/04-mega-setup/config/{Dockerfile,plugins.txt}   image/
cp $VAULT/04-mega-setup/config/helm-values.yaml          helm/values.yaml
cp $VAULT/03-templates/jcasc/jenkins.yaml                casc/
cp $VAULT/03-templates/job-dsl/seed.groovy               seed/
cp $VAULT/03-templates/k8s/*.yaml $VAULT/04-mega-setup/config/{external-secrets,servicemonitor,velero-schedule,karpenter-ci-nodepool}.yaml k8s/
cp -r $VAULT/03-templates/shared-library/. ../platform-lib/
```

## Verify

```bash
kubectl get nodes -L workload
aws sts get-caller-identity
helm version
```

✅ Done when: cluster reachable, three repos exist, IdP groups exist.

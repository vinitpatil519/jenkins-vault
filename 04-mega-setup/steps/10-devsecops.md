# Step 10 · DevSecOps: SonarQube, Trivy, Signing

[← Shared library](09-shared-library.md) · [Next: GitOps →](11-gitops-argocd.md)

---

## 1. SonarQube on the CI cluster

```bash
helm repo add sonarqube https://SonarSource.github.io/helm-chart-sonarqube
helm upgrade --install sonarqube sonarqube/sonarqube -n sonarqube --create-namespace \
  --set community.enabled=true \
  --set postgresql.enabled=false \
  --set jdbcOverwrite.enabled=true \
  --set jdbcOverwrite.jdbcUrl=jdbc:postgresql://sonar-db.xxxx.rds.amazonaws.com:5432/sonar \
  --set ingress.enabled=true --set ingress.hosts[0].name=sonar.example.com
```

> Chart values change between versions — check `helm show values sonarqube/sonarqube`. Use RDS for the DB in prod.

Then: OIDC/SAML login, token for Jenkins → Secrets Manager `ci/jenkins/sonar`, webhook `https://jenkins.example.com/sonarqube-webhook/`, Quality Gate "Platform Way" (new code: coverage ≥ 80%, 0 new vulns, 0 new bugs, hotspots reviewed).

## 2. Trivy

- Baked into CI images; DB cache on `trivy-cache` PVC.
- Optional `trivy server` deployment in cluster → clients use `--server http://trivy.trivy:4954` (one DB download for all builds).
- Policy: fail CRITICAL fixable; report HIGH.

## 3. Image signing (cosign)

```bash
cosign generate-key-pair --kms awskms:///alias/jenkins-cosign   # key never leaves KMS
```

Agent IAM role gets `kms:Sign` on that key. Pipeline: `cosign sign --key awskms:///alias/jenkins-cosign $IMAGE@$DIGEST`.

## 4. Enforce at deploy (workload clusters)

Kyverno policy: only images from our ECR signed by our key run.

```yaml
apiVersion: kyverno.io/v1
kind: ClusterPolicy
metadata: { name: verify-signed-images }
spec:
  validationFailureAction: Enforce
  webhookTimeoutSeconds: 30
  rules:
    - name: check-signature
      match: { any: [{ resources: { kinds: [Pod] } }] }
      verifyImages:
        - imageReferences: ["123456789012.dkr.ecr.eu-west-1.amazonaws.com/*"]
          attestors:
            - entries:
                - keys:
                    kms: awskms:///arn:aws:kms:eu-west-1:123456789012:alias/jenkins-cosign
```

## 5. Gates summary

| Stage | Tool | Blocks |
|-------|------|--------|
| PR | gitleaks | any secret |
| PR | SonarQube | quality gate |
| PR | Trivy fs/config | CRITICAL vuln, HIGH misconfig |
| main | Trivy image | CRITICAL fixable |
| main | cosign | unsigned = can't deploy |
| staging | ZAP baseline | FAIL rules |

✅ Done when: a repo with a planted fake secret fails the PR; a clean one passes and its image shows a signature (`cosign verify`).

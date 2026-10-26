# Step 04 · Secrets: AWS Secrets Manager → ESO → Jenkins

[← Controller image](03-controller-image.md) · [Next: Install →](05-install-helm-jcasc.md)

---

```mermaid
flowchart LR
    SM[(AWS Secrets Manager<br/>ci/jenkins/*)] -->|IRSA| ESO[External Secrets Operator]
    ESO --> S1[Secret jenkins-casc-secrets]
    ESO --> S2[Secret gitops-deploy-key<br/>label jenkins.io/credentials-type]
    S1 -->|envFrom| C[Controller]
    C -->|JCasC ${VAR}| CR[System credentials]
    S2 -->|kubernetes-credentials-provider| CR2[Credential id gitops-deploy-key]
```

## 1. Create secrets in AWS

```bash
aws secretsmanager create-secret --name ci/jenkins/oidc       --secret-string '{"client_secret":"..."}'
aws secretsmanager create-secret --name ci/jenkins/github-app --secret-string "$(jq -n --arg id 123456 --rawfile k key-pkcs8.pem '{app_id:$id, private_key_pkcs8:$k}')"
aws secretsmanager create-secret --name ci/jenkins/sonar      --secret-string '{"token":"..."}'
aws secretsmanager create-secret --name ci/jenkins/slack      --secret-string '{"token":"xoxb-..."}'
aws secretsmanager create-secret --name ci/jenkins/nvd        --secret-string '{"api_key":"..."}'
aws secretsmanager create-secret --name ci/jenkins/gitops-deploy-key --secret-string "$(jq -n --rawfile k deploy_key '{private_key:$k}')"
```

## 2. IAM role for the `jenkins` service account (IRSA)

Policy: `secretsmanager:GetSecretValue` + `DescribeSecret` on `arn:aws:secretsmanager:eu-west-1:123456789012:secret:ci/jenkins/*`. Trust: OIDC provider of the cluster, `system:serviceaccount:jenkins:jenkins`. Annotate via Helm values (`serviceAccount.annotations`).

## 3. Apply ESO resources

```bash
kubectl create namespace jenkins
kubectl apply -f k8s/external-secrets.yaml
kubectl -n jenkins get externalsecret        # STATUS SecretSynced
kubectl -n jenkins get secret jenkins-casc-secrets -o jsonpath='{.data}' | jq 'keys'
```

## Rules
- No secret value in Git, Helm values, or Jenkins UI.
- Rotation = update in Secrets Manager → ESO refresh (1h) → JCasC reload (or restart).
- AWS access for builds: IRSA on `jenkins-agent` SA (no AWS keys as credentials).

✅ Done when: `SecretSynced` on both ExternalSecrets.

# Step 06 · SSO (OIDC) + Role-Based Access

[← Install](05-install-helm-jcasc.md) · [Next: Kubernetes agents →](07-kubernetes-agents.md)

---

## 1. IdP client (Keycloak example)

- Client ID `jenkins`, confidential, standard flow.
- Redirect URI: `https://jenkins.example.com/securityRealm/finishLogin`.
- Mapper: **groups** claim (Group Membership, full path off) in ID token + userinfo.
- Enforce MFA in the realm.

Okta / Entra ID: same idea; Entra may need group IDs or app roles in the token.

## 2. JCasC

Use `securityRealm.oic` + `authorizationStrategy.roleBased` from [03-templates/jcasc/jenkins.yaml](../../03-templates/jcasc/jenkins.yaml).

## 3. Role design

| Role | Type | Pattern | Permissions | IdP group |
|------|------|---------|-------------|-----------|
| admin | global | — | Overall/Administer | `jenkins-admins` (2–4 people) |
| engineer | global | — | Overall/Read, Job/Read | `engineering` |
| team-X | item | `X(/.*)?` | Job/Build, Cancel, Read, Workspace | `team-X` |
| release-managers | item | `deploy/prod(/.*)?` | Job/Build, Read | `release-managers` |
| prometheus | global | — | Overall/Read, Metrics/View | service user + API token |

Nobody except admins gets `Job/Configure`, `Run/Replay`, `Credentials/*`, Script Console. Pipeline changes go through PRs (multibranch).

## 4. Verify

1. Log in as a `team-payments` user → sees only `payments/` jobs build buttons.
2. Log in as `engineering` only → read-only.
3. `https://jenkins.example.com/whoAmI/` shows groups as authorities.
4. Remove local admin from JCasC; keep a documented break-glass (oic-auth escape hatch user stored in Secrets Manager).

✅ Done when: SSO login works, groups map to roles, local users removed.

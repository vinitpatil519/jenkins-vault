# Step 14 · Go-Live Checklist

[← Backup, DR, upgrades](13-backup-dr-upgrades.md) · [Mega setup home →](../README.md)

---

## Security
- [ ] SSO + MFA; no local users except documented break-glass
- [ ] Role-based RBAC by IdP groups; `Job/Configure`, `Replay`, Script Console admin-only
- [ ] 0 executors on controller
- [ ] UI behind allow-list/VPN; only `/github-webhook/` public (GitHub IP ranges)
- [ ] No secrets in Git/UI; all from Secrets Manager via ESO
- [ ] No static AWS keys (IRSA everywhere)
- [ ] Agent pods: non-root, no privileged, NetworkPolicy, no SA token
- [ ] Fork PR trust = collaborators; untrusted PRs get no credentials
- [ ] Script approvals list empty or reviewed
- [ ] Audit trail → Loki/SIEM
- [ ] Plugin security warnings = 0

## Reliability
- [ ] Controller resources + heap set; GC logging on
- [ ] PV `Retain`, encrypted, snapshot schedule running
- [ ] DR drill done, RTO recorded
- [ ] Staging controller + upgrade pipeline working
- [ ] Build retention configured globally
- [ ] Karpenter limits + quota sized; spot interruptions tested (build re-run)

## Observability
- [ ] Prometheus scraping, dashboard live
- [ ] Alerts route to Slack/PagerDuty (test fired)
- [ ] Logs in Loki
- [ ] DORA metrics captured from deploy stages

## Developer experience
- [ ] Onboarding = one line in `teams.yaml`
- [ ] Golden pipeline docs (`goldenPipeline.txt` + README in platform-lib)
- [ ] PR feedback < 10 min for sample repos
- [ ] Slack notifications on failure/fix only
- [ ] `#platform-ci` support channel + runbooks linked from Jenkins system message

## Delivery
- [ ] Images signed; Kyverno enforces in staging/prod
- [ ] GitOps: staging auto, prod via reviewed PR
- [ ] Rollback tested (`git revert` → Argo sync)

When every box is ticked: announce, migrate first team, then onboard others in waves.

# Step 12 · Monitoring & Alerting

[← GitOps](11-gitops-argocd.md) · [Next: Backup, DR, upgrades →](13-backup-dr-upgrades.md)

---

## 1. Metrics

- Helm values `controller.prometheus.enabled: true` → chart creates ServiceMonitor.
- JCasC `prometheusConfiguration.useAuthenticatedEndpoint: true` → Prometheus uses `prometheus` user + API token (Secret `prometheus-jenkins`).
- Alert rules: [config/servicemonitor.yaml](../config/servicemonitor.yaml) (`PrometheusRule`).

## 2. Dashboards

Grafana (from kube-prometheus-stack):
- Import [03-templates/monitoring/grafana/provisioning/dashboards/jenkins-overview.json](../../03-templates/monitoring/grafana/provisioning/dashboards/jenkins-overview.json).
- Add: Karpenter nodes for `workload=ci`, agent pod count, pod Pending time, PVC usage.

## 3. Logs

Promtail / Fluent Bit DaemonSet → Loki. Useful LogQL:

```logql
{namespace="jenkins", container="jenkins"} |= "SEVERE"
{namespace="jenkins-agents"} |= "ERROR"
```

## 4. Traces (optional, high value)

`opentelemetry` plugin → OTel Collector → Tempo. Each build = trace; find slow stages across all pipelines.

```yaml
unclassified:
  openTelemetry:
    endpoint: "http://otel-collector.monitoring:4317"
    serviceName: jenkins
```

## 5. Alert routing

| Alert | Severity | Route |
|-------|----------|-------|
| JenkinsDown, JenkinsDiskLow | critical | PagerDuty platform on-call |
| Queue backlog, agents slow, heap high | warning | Slack `#platform-ci` |
| Security warnings (plugin advisories) | warning | Slack + weekly ticket |

## 6. Platform SLOs

| SLO | Target |
|-----|--------|
| Jenkins UI availability | 99.5% monthly |
| Time in queue p95 | < 2 min |
| Webhook → build start p95 | < 30 s |
| Golden pipeline PR feedback p50 | < 10 min |

✅ Done when: Grafana shows queue/executors/heap, test alert reaches Slack.

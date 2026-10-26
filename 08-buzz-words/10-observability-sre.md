# Observability & SRE Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| Alert fatigue | Too many alerts → people ignore them |
| APM | Application performance monitoring (traces + metrics per service) |
| Blameless postmortem | Incident review focused on systems, not people |
| Burn rate | Speed you're consuming the error budget |
| Cardinality | Number of unique label combinations in metrics |
| Chaos engineering | Inject failures deliberately to test resilience |
| Correlation ID / trace ID | ID passed across services to link logs/spans |
| Dashboard sprawl | Too many unused dashboards |
| Error budget | Allowed unreliability = 100% − SLO |
| Exemplar | Metric sample linked to a trace |
| Four golden signals | Latency, traffic, errors, saturation |
| Game day | Planned failure exercise |
| Incident commander | Person coordinating incident response |
| MTBF / MTTD / MTTR / MTTA | Between failures / to detect / to recover / to acknowledge |
| Observability vs monitoring | Ask new questions of system state vs watch known failure modes |
| On-call / pager rotation | Who responds to alerts and when |
| OpenTelemetry (OTel) | Vendor-neutral standard + SDKs for traces/metrics/logs |
| p50 / p95 / p99 | Percentile latencies (median / tail) |
| RED method | Rate, Errors, Duration (services) |
| USE method | Utilization, Saturation, Errors (resources) |
| Runbook / playbook | Step-by-step response docs |
| Sampling (head / tail) | Keep subset of traces decided at start / after seeing them |
| Severity levels (SEV1–4) | Incident impact classification |
| SLI / SLO / SLA | Measured indicator / internal target / contractual promise |
| Span | One timed operation inside a trace |
| Structured logging | Logs as JSON key/values |
| Synthetic monitoring | Scripted probes simulating users |
| Telemetry pipeline | Collect, process, route signals (OTel Collector, Vector) |
| Three pillars | Metrics, logs, traces |
| Toil | Repetitive manual ops work (SRE caps it ~50%) |
| Uptime "nines" | 99.9% ≈ 43 min/month down; 99.99% ≈ 4.3 min/month |
| White-box vs black-box monitoring | Internal metrics vs external probes |
| Postmortem action items | Concrete fixes tracked after incident |
| Error budget policy | What happens (freeze features) when budget exhausted |
| LGTM / ELK / EFK | Loki-Grafana-Tempo-Mimir / Elasticsearch-Logstash-Kibana / with Fluentd |

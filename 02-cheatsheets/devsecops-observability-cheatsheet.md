# DevSecOps + Observability Cheat Sheet

> Trivy, SonarQube, OWASP DC, ZAP, gitleaks, cosign, syft, Prometheus, Grafana — commands only.

[← README](../README.md) · Concepts: [05-devsecops](../05-devsecops/README.md) · [06-observability](../06-observability/prometheus.md)

---

## Trivy

```bash
trivy image --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 app:1.0
trivy image --format json -o r.json app:1.0
trivy image --format sarif -o r.sarif app:1.0
trivy image --format cyclonedx -o sbom.json app:1.0
trivy fs --scanners vuln,secret,misconfig,license .
trivy config ./k8s ./terraform
trivy repo https://github.com/org/repo
trivy k8s --report summary cluster
trivy sbom sbom.json
trivy image --download-db-only ; trivy clean --all
trivy server --listen 0.0.0.0:4954 ; trivy image --server http://trivy:4954 app:1.0
```

## SonarQube

```bash
sonar-scanner -Dsonar.projectKey=app -Dsonar.sources=src -Dsonar.host.url=$URL -Dsonar.token=$TOKEN
mvn sonar:sonar   |   ./gradlew sonar   |   dotnet sonarscanner begin /k:app ...
curl -u $TOKEN: "$URL/api/qualitygates/project_status?projectKey=app" | jq .projectStatus.status
```

```groovy
withSonarQubeEnv('sonar') { sh 'sonar-scanner' }
timeout(10) { waitForQualityGate abortPipeline: true }     // needs Sonar webhook → /sonarqube-webhook/
```

## OWASP Dependency-Check / ZAP

```bash
dependency-check.sh --scan . --format HTML --format XML --out reports --nvdApiKey $KEY --failOnCVSS 7 --data /odc
dependency-check.sh --updateonly --nvdApiKey $KEY --data /odc        # nightly DB refresh
mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7

docker run --rm -v $PWD:/zap/wrk zaproxy/zap-stable zap-baseline.py -t https://stg -r zap.html -I
docker run --rm -v $PWD:/zap/wrk zaproxy/zap-stable zap-api-scan.py -t https://stg/openapi.json -f openapi -r api.html
docker run --rm -v $PWD:/zap/wrk zaproxy/zap-stable zap-full-scan.py -t https://stg -r full.html
```

## Secrets, SBOM, signing

```bash
gitleaks detect --source . --redact --report-format sarif --report-path g.sarif
gitleaks protect --staged                          # pre-commit
trufflehog git file://. --only-verified
syft app:1.0 -o spdx-json > sbom.spdx.json
grype app:1.0 --fail-on critical
cosign generate-key-pair [--kms awskms:///alias/k]
cosign sign --key cosign.key reg/app@sha256:...   |   cosign sign --yes reg/app@sha256:...   # keyless
cosign verify --key cosign.pub reg/app@sha256:...
cosign attest --key cosign.key --type cyclonedx --predicate sbom.json reg/app@sha256:...
crane digest reg/app:1.0 ; crane copy reg/app:1.0 reg2/app:1.0 ; skopeo inspect docker://reg/app:1.0
```

## Prometheus

```bash
promtool check config prometheus.yml ; promtool check rules rules.yml
curl -X POST localhost:9090/-/reload                       # --web.enable-lifecycle
curl -s localhost:9090/api/v1/query --data-urlencode 'query=up' | jq
curl -s localhost:9090/api/v1/targets | jq '.data.activeTargets[]|{job:.labels.job,health}'
```

```promql
up == 0
rate(http_requests_total[5m])
sum by (code) (rate(http_requests_total[5m]))
sum(rate(http_requests_total{code=~"5.."}[5m])) / sum(rate(http_requests_total[5m]))
histogram_quantile(0.99, sum by (le) (rate(http_request_duration_seconds_bucket[5m])))
increase(x_total[1h])  ·  avg_over_time(g[10m])  ·  topk(5, ...)  ·  absent(up{job="x"})
predict_linear(node_filesystem_avail_bytes[6h], 4*3600) < 0
jenkins_queue_size_value  ·  jenkins_node_offline_value  ·  jenkins_executor_in_use_value / jenkins_executor_count_value
default_jenkins_builds_last_build_result_ordinal  ·  default_jenkins_builds_last_build_duration_milliseconds
```

## Grafana

```bash
curl -s -u admin:pass localhost:3000/api/health
curl -s -u admin:pass "localhost:3000/api/search?query=Jenkins"
curl -s -H "Authorization: Bearer $T" -X POST $G/api/annotations -H 'Content-Type: application/json' -d '{"tags":["deploy"],"text":"v1.2 prod"}'
```

Dashboard IDs: Node Exporter Full **1860** · Jenkins **9964** · K8s cluster **15757**.

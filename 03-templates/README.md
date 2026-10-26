# 03 · Templates

> Copy-paste starting points. Replace `example.com`, `acme`, registry and credential IDs.

[← README](../README.md)

---

| Path | What | Use when |
|------|------|----------|
| [docker/](docker/) | Runnable lab: JCasC controller + SSH agent (+ Prometheus/Grafana/SonarQube profiles) | Learning, local testing ([quick start](../00-quick-start/README.md)) |
| [jenkinsfiles/Jenkinsfile.java-maven](jenkinsfiles/Jenkinsfile.java-maven) | Maven build, JUnit, JaCoCo, Sonar, quality gate, Nexus deploy | Java service |
| [jenkinsfiles/Jenkinsfile.node](jenkinsfiles/Jenkinsfile.node) | npm ci, parallel lint/test, coverage | Node service |
| [jenkinsfiles/Jenkinsfile.python](jenkinsfiles/Jenkinsfile.python) | venv, ruff, mypy, pytest | Python service |
| [jenkinsfiles/Jenkinsfile.go](jenkinsfiles/Jenkinsfile.go) | vet, race tests, static binary | Go service |
| [jenkinsfiles/Jenkinsfile.docker-build-push](jenkinsfiles/Jenkinsfile.docker-build-push) | Rootless BuildKit → Trivy → cosign sign | Any containerized app |
| [jenkinsfiles/Jenkinsfile.devsecops](jenkinsfiles/Jenkinsfile.devsecops) | gitleaks, Sonar, OWASP DC, Trivy fs/config/image, SBOM, sign, ZAP | Full security pipeline |
| [jenkinsfiles/Jenkinsfile.k8s-helm-deploy](jenkinsfiles/Jenkinsfile.k8s-helm-deploy) | Param deploy, prod approval, lock, atomic Helm, smoke test | Push-based K8s deploy |
| [jenkinsfiles/Jenkinsfile.gitops-promote](jenkinsfiles/Jenkinsfile.gitops-promote) | Bump tag in GitOps repo; prod via PR | Argo CD / Flux setups |
| [jenkinsfiles/Jenkinsfile.terraform](jenkinsfiles/Jenkinsfile.terraform) | fmt/validate/plan, approve, apply saved plan | Infra repos |
| [jenkinsfiles/Jenkinsfile.monorepo](jenkinsfiles/Jenkinsfile.monorepo) | Build only changed services | Monorepos |
| [jenkinsfiles/Jenkinsfile.matrix](jenkinsfiles/Jenkinsfile.matrix) | OS × JDK matrix with excludes | Libraries, multi-platform |
| [jenkinsfiles/Jenkinsfile.scripted](jenkinsfiles/Jenkinsfile.scripted) | Scripted: try/catch/finally, dynamic parallel | Dynamic flows |
| [shared-library/](shared-library/) | `platform-lib`: golden pipeline + steps + pod templates | Platform team standard |
| [jcasc/jenkins.yaml](jcasc/jenkins.yaml) | Production JCasC: OIDC, role-based RBAC, K8s cloud, creds, libs, Sonar, Slack, Prometheus | Controller config |
| [job-dsl/seed.groovy](job-dsl/seed.groovy) | Team folders + multibranch per repo from `teams.yaml` | Job generation |
| [k8s/](k8s/) | Agent namespace (quota, RBAC, NetworkPolicy), cache PVCs, pod templates | K8s agents |
| [monitoring/](monitoring/) | Prometheus scrape, Jenkins alert rules, Grafana provisioning + dashboard | Observability |

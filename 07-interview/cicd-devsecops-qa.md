# CI/CD, DevSecOps, GitOps & Monitoring Q&A

[← Interview](README.md)

---

## CI/CD design

**1. CI vs Continuous Delivery vs Continuous Deployment?** CI: build+test every change. Delivery: every green build releasable, prod deploy is manual click. Deployment: auto to prod.

**2. What makes a good pipeline?** Fast feedback (<10 min), deterministic, pipeline-as-code, build once deploy many, immutable versioned artifacts, security gates, observable, easy rollback.

**3. Build once, deploy many — why?** Rebuilding per env risks different artifacts (deps drift). Promote the same digest; only config differs.

**4. How do you version artifacts?** SemVer for releases + git SHA for traceability (`1.4.2-a1b2c3d`). Never overwrite tags; deploy by digest.

**5. Trunk-based vs GitFlow?** Trunk: short-lived branches, frequent merges, feature flags → true CI. GitFlow: long-lived develop/release branches → merge pain, slower.

**6. Feature flags role?** Decouple deploy from release; ship dark code, toggle per user/%; instant kill switch.

**7. Monorepo CI challenges?** Build only affected parts (path filters, dependency graph tools), caching, ownership, long checkouts (sparse/shallow).

**8. Make a 40-min pipeline 10 min?** Measure stages; parallelize; cache deps (proxy + volumes); pre-built agent images; shallow clones; only build changed; shard tests; move E2E post-merge.

**9. Handle flaky tests?** Detect via history, quarantine with ticket, fix root cause (timing, shared state); don't blanket-retry.

**10. Test pyramid?** Many unit, fewer integration/contract, few E2E. Faster and cheaper at the bottom.

**11. Database migrations in CD?** Versioned migrations (Flyway/Liquibase), expand/contract for backward compatibility, run before app rollout, avoid destructive changes in same release.

**12. Deployment strategies?** Recreate, rolling, blue/green, canary, A/B, shadow, feature flags. Canary + automated analysis for high-traffic prod.

**13. Blue/green vs canary?** Blue/green: two full envs, instant switch, 2× cost. Canary: gradual traffic shift with metrics, less cost, slower.

**14. Rollback strategy?** Keep previous artifact; `helm rollback` / `kubectl rollout undo` / `git revert` in GitOps; flags off; DB backward compatible.

**15. Roll back vs roll forward?** Roll back if fast and safe; roll forward if fix is tiny or rollback is risky (migrations ran).

**16. Environments and promotion?** dev → staging → prod, same artifact, gates between (tests, scans, approvals), config per env (values files / ConfigMaps).

**17. Secrets in CI/CD?** External manager (Vault/ASM), short-lived creds (OIDC/IRSA), scoped per env, masked, never in repo; secret scanning.

**18. Preview environments?** Per-PR ephemeral namespace deployed by pipeline, E2E tests, auto-destroy on merge/close.

**19. Pipeline for 200 microservices?** Shared library/golden path, org folder auto-discovery, ephemeral K8s agents, standard contract (Makefile/Dockerfile), GitOps CD, central security gates, dashboards per team.

**20. Artifact repository role?** Store/version/proxy artifacts (Nexus/Artifactory/ECR); caching remote deps; retention policies; provenance.

## GitOps

**21. What is GitOps?** Desired state in Git; an in-cluster agent (Argo CD/Flux) continuously reconciles cluster to Git. Pull-based.

**22. Push vs pull deploy?** Push: CI runs kubectl/helm with cluster creds. Pull: in-cluster operator pulls from Git; no cluster creds in CI, drift correction, Git audit.

**23. Jenkins + Argo CD flow?** Jenkins builds/tests/scans/signs image → commits new tag to env repo (prod via PR) → Argo syncs → Argo Rollouts canary.

**24. Drift?** Live state differs from Git. Argo detects and self-heals (if enabled).

**25. App-of-apps / ApplicationSet?** Argo patterns to manage many Applications from one definition (generators: git dirs, clusters, lists).

## DevSecOps

**26. DevSecOps?** Security integrated into every pipeline stage, automated, owned by dev teams; shift left.

**27. SAST vs DAST vs SCA vs IAST?** SAST: source code analysis. DAST: attack running app. SCA: known-vuln dependencies/licenses. IAST: instrumented runtime analysis.

**28. Where do scans go in pipeline?** Pre-commit/PR: secrets, SAST, SCA, IaC. After build: image scan + SBOM + sign. Staging: DAST. Runtime: admission policy, continuous scanning.

**29. What fails the build?** Secrets found, quality gate failed, CRITICAL fixable CVEs, high-severity IaC misconfig. Report the rest; ratchet thresholds over time.

**30. Trivy scans what?** Images, filesystems, repos, IaC configs, K8s clusters, SBOMs; vulns, misconfigs, secrets, licenses.

**31. `--ignore-unfixed`?** Skip CVEs without an available fix → gate only actionable issues.

**32. Reduce image CVEs?** Minimal/distroless base, multi-stage builds, regular base rebuilds, remove build tools, pin and update deps.

**33. SonarQube quality gate vs profile?** Profile = rule set per language. Gate = pass/fail conditions (coverage on new code, new bugs/vulns, hotspots reviewed).

**34. Why `waitForQualityGate` needs webhook?** Analysis processed async on server; webhook notifies Jenkins of the result.

**35. OWASP Top 10?** A01 Broken Access Control, A02 Cryptographic Failures, A03 Injection, A04 Insecure Design, A05 Security Misconfiguration, A06 Vulnerable Components, A07 Auth Failures, A08 Software/Data Integrity Failures, A09 Logging/Monitoring Failures, A10 SSRF (2021 list).

**36. OWASP Dependency-Check?** SCA tool matching dependencies to NVD CVEs; needs NVD API key; `--failOnCVSS`; suppression file for false positives.

**37. OWASP ZAP?** DAST proxy/scanner. Baseline (passive, CI-safe), full (active attacks), API scan (OpenAPI).

**38. SBOM?** Software Bill of Materials: inventory of components (SPDX/CycloneDX); enables fast response to new CVEs (e.g. "where do we use log4j?").

**39. Image signing?** cosign signs digest (key/KMS or keyless OIDC); admission controller (Kyverno/policy-controller) only admits signed images.

**40. SLSA?** Supply-chain Levels for Software Artifacts: framework for build integrity (provenance, hermetic, isolated builds) in levels.

**41. Poisoned Pipeline Execution?** Attacker modifies CI config in a PR to run code with pipeline secrets. Mitigate: untrusted PR isolation, no secrets for forks, protected CI files.

**42. Secrets leaked in repo — response?** Revoke/rotate immediately, purge history (filter-repo) only after rotation, scan for usage, add pre-commit + CI secret scanning.

## Monitoring & SRE

**43. Prometheus architecture?** Pull-based scraper + TSDB + rule engine; exporters expose metrics; Alertmanager routes alerts; Grafana visualizes.

**44. Metric types?** Counter, gauge, histogram, summary.

**45. rate vs irate vs increase?** rate: avg per-second over window. irate: last two samples (spiky). increase: total growth over window.

**46. Histogram vs summary?** Histogram buckets aggregate across instances (server-side quantiles). Summary quantiles computed client-side, not aggregatable.

**47. High cardinality?** Too many unique label values (user IDs) → memory blowup. Avoid unbounded labels.

**48. Four golden signals?** Latency, traffic, errors, saturation. RED for services, USE for resources.

**49. SLI/SLO/SLA/error budget?** SLI = measured indicator. SLO = target. SLA = contractual promise. Error budget = 100% − SLO; spend it on change velocity.

**50. Alerting best practice?** Alert on symptoms/SLO burn, not causes; actionable with runbook; route by severity; group/inhibit to cut noise.

**51. What to monitor in CI/CD?** Queue time, build duration, success rate, agent availability, controller heap/disk, deploy frequency, lead time, change failure rate, MTTR.

**52. Logs vs metrics vs traces?** Metrics: numeric aggregates over time (alerting). Logs: discrete events (debug). Traces: request path across services (latency breakdown).

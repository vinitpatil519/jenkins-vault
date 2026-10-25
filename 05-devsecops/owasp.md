# OWASP: Top 10, Dependency-Check, ZAP

> OWASP = Open Worldwide Application Security Project. Non-profit; standards + free tools.

[← DevSecOps](README.md)

---

## 1. OWASP Top 10 (2021 — the one interviews quote; a 2025 revision exists)

| # | Category | One-liner | Pipeline control |
|---|----------|-----------|------------------|
| A01 | Broken Access Control | Users act outside permissions (IDOR, missing authz) | Tests, DAST, code review |
| A02 | Cryptographic Failures | Weak/no encryption of sensitive data | SAST, config scans |
| A03 | Injection (SQL, OS, LDAP, XSS) | Untrusted input executed | SAST, DAST, parameterized queries |
| A04 | Insecure Design | Flaws in design/threat model | Threat modeling |
| A05 | Security Misconfiguration | Defaults, open buckets, verbose errors | IaC scan, hardening |
| A06 | Vulnerable & Outdated Components | Known-CVE dependencies | **SCA: Dependency-Check / Trivy** |
| A07 | Identification & Authentication Failures | Weak sessions/passwords, no MFA | DAST, libraries |
| A08 | Software & Data Integrity Failures | Unsigned updates, insecure CI/CD, deserialization | **Signing, SLSA, pinned deps** |
| A09 | Security Logging & Monitoring Failures | Attacks go unnoticed | Logging standards |
| A10 | SSRF | Server fetches attacker-controlled URLs | Input validation, egress control |

> The 2025 list reshuffles categories (e.g. adds software supply chain failures, mishandling of exceptional conditions; SSRF merged into access control). Know 2021 cold, mention 2025 exists.

Related: **OWASP ASVS** (verification standard), **OWASP SAMM** (maturity model), **OWASP Top 10 CI/CD Security Risks** (CICD-SEC-1..10: insufficient flow control, inadequate IAM, dependency chain abuse, poisoned pipeline execution (PPE), insufficient PBAC, credential hygiene, insecure system config, ungoverned 3rd-party services, improper artifact integrity validation, insufficient logging), **Cheat Sheet Series**, **Juice Shop** (vulnerable app for practice).

**Poisoned Pipeline Execution (PPE)** = attacker changes pipeline config (Jenkinsfile, Makefile, scripts) via PR to run malicious code with CI privileges. Jenkins defense: fork trust settings, no creds for untrusted PRs, trusted Jenkinsfile from separate repo for sensitive jobs.

## 2. OWASP Dependency-Check (SCA)

Scans dependencies, matches them to CPEs, checks NVD CVEs.

```bash
# CLI (needs Java). NVD API key strongly recommended — without it updates are extremely slow
dependency-check.sh --project payments --scan . \
  --format HTML --format XML --format JSON --out reports \
  --nvdApiKey "$NVD_API_KEY" --failOnCVSS 7 \
  --suppression dependency-check-suppressions.xml \
  --data /odc-data                    # persistent DB cache
```

Jenkins (plugin `dependency-check-jenkins-plugin`):

```groovy
stage('OWASP Dependency-Check') {
  steps {
    withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {
      dependencyCheck odcInstallation: 'dc', additionalArguments: '''
        --scan . --format XML --format HTML --out reports
        --nvdApiKey ${NVD_API_KEY} --data /odc-data --failOnCVSS 9
      '''.stripIndent()
    }
  }
  post {
    always { dependencyCheckPublisher pattern: 'reports/dependency-check-report.xml',
                                      failedTotalCritical: 1, unstableTotalHigh: 5 }
  }
}
```

Maven: `mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7`. Gradle plugin: `org.owasp.dependencycheck`.

Tips:
- First run downloads the whole NVD DB (long). Keep `--data` on a persistent volume / dedicated nightly update job (`--updateonly`), scans run with `--noupdate`.
- False positives common (CPE matching) → suppression XML with notes.
- Many teams use Trivy/Grype for speed; DC is still asked in interviews.

## 3. OWASP ZAP (DAST)

Zed Attack Proxy — attacks the running app. Now maintained as **ZAP by Checkmarx**; image `zaproxy/zap-stable`.

| Scan | What | Time |
|------|------|------|
| `zap-baseline.py` | Spider + **passive** scan; safe for prod-like envs | Minutes ⭐ CI |
| `zap-full-scan.py` | Spider + **active** attacks | Long; staging only |
| `zap-api-scan.py` | Scan from OpenAPI/GraphQL/SOAP definition | APIs ⭐ |

```groovy
stage('DAST - ZAP baseline') {
  steps {
    sh '''
      docker run --rm -v $(pwd):/zap/wrk:rw -t zaproxy/zap-stable \
        zap-baseline.py -t https://staging.example.com -r zap.html -J zap.json -c zap-rules.conf -I
    '''
  }
  post { always { archiveArtifacts artifacts: 'zap.*', allowEmptyArchive: true } }
}
```

Exit codes: 0 pass, 1 FAIL alerts, 2 WARN alerts, 3 other failure. `-I` = don't fail on warnings. `zap-rules.conf` sets each rule to IGNORE/WARN/FAIL.

## 4. Interview one-liners
- **SAST vs DAST?** SAST reads code (white-box, early, false positives). DAST attacks running app (black-box, late, real exploitable issues).
- **SCA?** Finds known CVEs/licenses in third-party dependencies (A06).
- **How do you secure the CI/CD pipeline itself?** OWASP CI/CD Top 10: least-privilege creds, no PPE (untrusted PR isolation), pinned deps, signed artifacts, audited config, logging.

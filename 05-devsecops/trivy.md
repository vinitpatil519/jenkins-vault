# Trivy

> All-in-one scanner (Aqua Security): images, filesystems, repos, IaC, K8s clusters, SBOMs, secrets, licenses.

[← DevSecOps](README.md)

---

## 1. Concepts

| Term | Means |
|------|-------|
| **Targets** | `image`, `fs`, `repo`, `config`, `k8s`, `sbom`, `vm`, `rootfs` |
| **Scanners** | `vuln` (CVEs), `misconfig` (IaC), `secret`, `license` |
| **Vulnerability DB** | Downloaded from GHCR (`trivy-db`), updated every few hours; cache it |
| **Severity** | UNKNOWN, LOW, MEDIUM, HIGH, CRITICAL |
| **`--ignore-unfixed`** | Skip CVEs with no fixed version available (actionable only) |
| **`.trivyignore`** | CVE IDs to suppress (with comment + expiry) |
| **Exit code** | `--exit-code 1` → non-zero if findings at chosen severity → fails pipeline |
| **Client/server mode** | `trivy server` holds DB; clients scan with `--server` (fast CI, one DB) |

## 2. Install

```bash
# Debian/Ubuntu
sudo apt-get install -y wget gnupg
wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | gpg --dearmor | sudo tee /usr/share/keyrings/trivy.gpg >/dev/null
echo "deb [signed-by=/usr/share/keyrings/trivy.gpg] https://aquasecurity.github.io/trivy-repo/deb generic main" | sudo tee /etc/apt/sources.list.d/trivy.list
sudo apt-get update && sudo apt-get install -y trivy

# Or container (pin the version)
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:0.57.0 image nginx:1.27
```

> Pin the Trivy version (and verify checksums/signatures) — scanners are supply-chain targets themselves.

## 3. Commands

```bash
trivy image nginx:1.27-alpine                                         # scan image
trivy image --severity HIGH,CRITICAL --ignore-unfixed --exit-code 1 app:1.0
trivy image --format json -o trivy.json app:1.0
trivy image --format template --template "@contrib/html.tpl" -o trivy.html app:1.0
trivy image --format sarif -o trivy.sarif app:1.0                     # GitHub code scanning
trivy image --input app.tar                                           # scan tarball (no daemon)
trivy fs --scanners vuln,secret,misconfig .                           # repo/lockfiles on disk
trivy repo https://github.com/acme/app                                # remote repo
trivy config ./terraform ./k8s ./Dockerfile                           # IaC misconfig
trivy k8s --report summary cluster                                    # live cluster
trivy image --format cyclonedx -o sbom.cdx.json app:1.0               # generate SBOM
trivy sbom sbom.cdx.json                                              # scan an SBOM
trivy image --download-db-only                                        # warm cache
trivy clean --all                                                     # clear caches
```

## 4. Jenkins usage

```groovy
stage('Trivy image scan') {
  steps {
    sh '''
      trivy image --cache-dir /trivy-cache --no-progress \
        --severity HIGH,CRITICAL --format json -o trivy.json ${IMAGE}:${TAG}
      trivy image --cache-dir /trivy-cache --no-progress \
        --severity CRITICAL --ignore-unfixed --exit-code 1 ${IMAGE}:${TAG}
    '''
  }
  post {
    always {
      archiveArtifacts artifacts: 'trivy.json', allowEmptyArchive: true
      recordIssues tools: [trivy(pattern: 'trivy.json')]     // warnings-ng parses Trivy JSON
    }
  }
}
```

Pattern: **first run = report everything** (archive), **second run = gate** on CRITICAL fixable. Cache dir on PVC to avoid DB download each build.

### K8s pod container

```yaml
- name: trivy
  image: aquasec/trivy:0.57.0
  command: ["sleep"]
  args: ["infinity"]
  volumeMounts: [{ name: trivy-cache, mountPath: /trivy-cache }]
```

## 5. `.trivyignore` & config

```text
# .trivyignore
# CVE-2024-12345 — not reachable, base image fix pending, review by 2026-12-31
CVE-2024-12345
```

```yaml
# trivy.yaml (auto-loaded from cwd)
severity: [HIGH, CRITICAL]
exit-code: 1
vulnerability:
  ignore-unfixed: true
scan:
  skip-dirs: [node_modules/.cache, test/fixtures]
```

## 6. Interview one-liners

- **fs vs image scan?** `fs` = lockfiles/source before build (fast, SCA). `image` = everything in the final image incl. OS packages.
- **Why `--ignore-unfixed`?** Gate on actionable vulns only; report the rest.
- **Reduce CVEs?** Smaller base (distroless/alpine/chainguard), multi-stage builds, update base regularly, remove build tools from runtime image.

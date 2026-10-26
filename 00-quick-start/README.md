# 00 · Quick Start

> Jenkins running with an agent and demo jobs in 5 minutes. Then: add Jenkins to any project in 10 minutes.

[← README](../README.md)

---

## A. Local lab (Docker): 5 minutes

Needs Docker 24+ with Compose v2.

```bash
git clone https://github.com/vinitpatil519/jenkins-vault.git
cd jenkins-vault/03-templates/docker
./setup.sh                                  # SSH key for agent + .env with admin password
docker compose up -d --build                # first build downloads plugins (~3–8 min)
```

Open http://localhost:8080 → user `admin`, password printed by `setup.sh` (also in `.env`).

You get (all from code, no wizard):

| Thing | Where it comes from |
|-------|---------------------|
| Controller, 0 executors | `casc/jenkins.yaml` |
| SSH agent `agent-1` (label `lab`) | `casc/jenkins.yaml` + `jenkins/ssh-agent` container |
| Jobs `lab/hello-pipeline`, `lab/nightly-cron` | `casc/seed.groovy` (Job DSL) |
| Plugins | `plugins.txt` baked into image |

Optional:

```bash
docker compose --profile monitoring up -d   # Prometheus :9090, Grafana :3000 (admin/admin) with Jenkins dashboard
docker compose --profile sonar up -d        # SonarQube :9000 (admin/admin) — needs: sudo sysctl -w vm.max_map_count=524288
docker compose --profile "*" down -v        # delete everything
```

Change config: edit `casc/jenkins.yaml` → **Manage Jenkins → Configuration as Code → Reload existing configuration**.

### Trigger via API (proves auth + crumb rules)

```bash
P=$(grep JENKINS_ADMIN_PASSWORD .env | cut -d= -f2)
# Password auth needs a crumb + session cookie:
CR=$(curl -s -c /tmp/j -u admin:$P localhost:8080/crumbIssuer/api/json | jq -r '.crumbRequestField+":"+.crumb')
curl -s -b /tmp/j -u admin:$P -H "$CR" -X POST localhost:8080/job/lab/job/hello-pipeline/build
# API token auth (User → Security → API Token) needs no crumb:
curl -s -u admin:$TOKEN -X POST localhost:8080/job/lab/job/hello-pipeline/build
# Note: -g stops curl treating [ ] in tree= queries as globs
curl -sg -u admin:$TOKEN 'localhost:8080/computer/api/json?tree=computer[displayName,offline]'
```

---

## B. Add Jenkins to any project: 10 minutes

1. **Pick a template** from [03-templates/jenkinsfiles](../03-templates/jenkinsfiles/) → copy to repo root as `Jenkinsfile`.
2. **Match agent** to your setup: `agent { label 'lab' }` (lab), `agent { docker { image '...' } }` (Docker agent), `agent { kubernetes { ... } }` (K8s).
3. **Create credentials** the template uses (Manage Jenkins → Credentials → Global, or folder): registry, Sonar token, kubeconfig.
4. **Create job**: New Item → **Multibranch Pipeline** → Branch Source GitHub → repo URL + credential → Save. Jenkins scans and builds every branch with a `Jenkinsfile`.
5. **Webhook**: GitHub repo → Settings → Webhooks → `https://<jenkins>/github-webhook/`, content type `application/json`, events push + PR. (Local lab behind NAT: use smee.io or rely on periodic scan.)
6. **Branch protection**: require Jenkins check on `main`.
7. **Retention + timeout**: keep `buildDiscarder` and `timeout` options from the template.

Minimum viable Jenkinsfile:

```groovy
pipeline {
  agent any
  options { timeout(time: 20, unit: 'MINUTES'); buildDiscarder(logRotator(numToKeepStr: '20')) }
  stages {
    stage('Build') { steps { sh 'make build' } }
    stage('Test')  { steps { sh 'make test' } }
  }
  post { always { junit allowEmptyResults: true, testResults: '**/reports/*.xml' } }
}
```

---

## C. Production path

Go to [04-mega-setup](../04-mega-setup/README.md): Kubernetes + Helm + JCasC + SSO + ephemeral agents + DevSecOps + GitOps + monitoring.

## D. Install cheats (other platforms)

| Platform | Command | Doc |
|----------|---------|-----|
| Docker one-liner | `docker run -d -p 8080:8080 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts-jdk21` | [03](../01-concepts/03-installation.md#2-docker-one-command) |
| Ubuntu | apt repo + `apt install jenkins` | [03](../01-concepts/03-installation.md#3-ubuntu--debian-apt) |
| Kubernetes | `helm upgrade --install jenkins jenkins/jenkins -n jenkins` | [03](../01-concepts/03-installation.md#4-kubernetes-helm--production-path) |
| WAR | `java -jar jenkins.war` | [03](../01-concepts/03-installation.md#5-war-file) |

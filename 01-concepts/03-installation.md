# 03 · Installation

> **Goal:** install Jenkins five ways and know which one fits. Copy-paste ready.

[← Architecture](02-architecture.md) · [Next: Jobs & Builds →](04-jobs-and-builds.md)

---

## 1. Pick a method

| Method | Use for |
|--------|---------|
| **Docker / Compose** | Local lab, small teams, fast start ⭐ |
| **Kubernetes (Helm)** | Production platform ⭐ |
| **Linux package (apt/dnf)** | Single VM, classic setup |
| **WAR file** | Anywhere with Java; testing |
| Cloud marketplace / Bitnami | Quick VM image |

Requirements: Java **17 or 21** (current LTS lines require Java 17+), 2 GB+ RAM, 10 GB+ disk.

---

## 2. Docker (one command)

```bash
docker volume create jenkins_home
docker run -d --name jenkins \
  -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  --restart unless-stopped \
  jenkins/jenkins:lts-jdk21

docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
# open http://localhost:8080
```

Port 8080 = UI. Port 50000 = inbound agents (skip if using WebSocket).

### Custom image with plugins baked in (⭐ reproducible)

```dockerfile
FROM jenkins/jenkins:lts-jdk21
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV CASC_JENKINS_CONFIG=/var/jenkins_home/casc/jenkins.yaml
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
COPY casc/ /var/jenkins_home/casc/
```

Full working lab: [templates/docker/](../03-templates/docker/) and [QUICKSTART](../00-quick-start/README.md).

### Running Docker builds from Jenkins-in-Docker

| Option | How | Risk |
|--------|-----|------|
| Mount host socket | `-v /var/run/docker.sock:/var/run/docker.sock` | Container = root on host ⚠️ |
| DinD sidecar | `docker:dind` privileged + `DOCKER_HOST=tcp://dind:2376` | Privileged container |
| Rootless builders | BuildKit rootless, Buildah, Kaniko-style on agents | ⭐ Safest |

---

## 3. Ubuntu / Debian (apt)

```bash
sudo apt update && sudo apt install -y fontconfig openjdk-21-jre
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" \
  | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update && sudo apt install -y jenkins
sudo systemctl enable --now jenkins
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

> The signing key rotates occasionally. If `apt update` fails with NO_PUBKEY, get the current key URL from jenkins.io/doc/book/installing/linux.

| Thing | Path |
|-------|------|
| Home | `/var/lib/jenkins` |
| Logs | `journalctl -u jenkins -f` |
| Settings | `sudo systemctl edit jenkins` → `Environment="JAVA_OPTS=-Xmx2g"`, `Environment="JENKINS_PORT=8080"` |
| User | `jenkins` |

RHEL/Fedora: repo `https://pkg.jenkins.io/redhat-stable/jenkins.repo`, `dnf install jenkins java-21-openjdk`.

---

## 4. Kubernetes (Helm) — production path

```bash
helm repo add jenkins https://charts.jenkins.io && helm repo update
kubectl create namespace jenkins
helm upgrade --install jenkins jenkins/jenkins -n jenkins -f values.yaml
kubectl -n jenkins get secret jenkins -o jsonpath='{.data.jenkins-admin-password}' | base64 -d
kubectl -n jenkins port-forward svc/jenkins 8080:8080
```

Minimal `values.yaml`:

```yaml
controller:
  image:
    tag: lts-jdk21
  numExecutors: 0
  resources:
    requests: { cpu: "1", memory: "4Gi" }
    limits:   { memory: "4Gi" }
  javaOpts: "-XX:MaxRAMPercentage=70"
  installPlugins:
    - kubernetes
    - workflow-aggregator
    - git
    - configuration-as-code
  JCasC:
    configScripts:
      welcome: |
        jenkins:
          systemMessage: "Managed by JCasC. UI changes will be overwritten."
  ingress:
    enabled: true
    ingressClassName: nginx
    hostName: jenkins.example.com
persistence:
  enabled: true
  size: 50Gi
agent:
  enabled: true      # default pod template for the kubernetes plugin
```

The chart already wires: kubernetes plugin cloud, JCasC sidecar (reloads config from ConfigMaps), service account. Full production values: [mega-setup/config/helm-values.yaml](../04-mega-setup/config/helm-values.yaml).

---

## 5. WAR file

```bash
java -jar jenkins.war --httpPort=8080
# JENKINS_HOME defaults to ~/.jenkins
```

---

## 6. First-boot checklist

1. Unlock with `initialAdminPassword`.
2. Install suggested plugins (or skip — use `plugins.txt`).
3. Create admin user. Don't keep using `admin`.
4. Set **Jenkins URL** (Manage → System) — webhooks and links break without it.
5. Built-in node → executors **0**.
6. Security → enable CSRF (default on), set authorization (Matrix / Role-based).
7. Add an agent or cloud.
8. Install plugin updates; pin versions.
9. Configure backups.
10. Move everything to **JCasC** so the UI isn't the source of truth.

---

## 7. Important system properties / env

| Setting | Purpose |
|---------|---------|
| `-Djenkins.install.runSetupWizard=false` | Skip wizard (use with JCasC) |
| `CASC_JENKINS_CONFIG` | Path/URL to JCasC YAML |
| `JENKINS_HOME` | Data dir |
| `-Dhudson.model.DirectoryBrowserSupport.CSP=...` | Content Security Policy for archived HTML |
| `-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=600` | Fix "process apparently never started" on slow agents |
| `JENKINS_OPTS="--prefix=/jenkins"` | Serve under a path |
| `-Xms/-Xmx` or `-XX:MaxRAMPercentage` | Heap |

---

## 8. Reverse proxy (Nginx) essentials

```nginx
location / {
    proxy_pass         http://127.0.0.1:8080;
    proxy_set_header   Host $host;
    proxy_set_header   X-Forwarded-Proto $scheme;
    proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_http_version 1.1;
    proxy_set_header   Upgrade $http_upgrade;     # WebSocket agents
    proxy_set_header   Connection "upgrade";
    proxy_read_timeout 90s;
    proxy_request_buffering off;                 # large uploads / CLI
}
```

"It appears that your reverse proxy set up is broken" → Jenkins URL mismatch or missing `X-Forwarded-*` headers.

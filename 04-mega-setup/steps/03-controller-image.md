# Step 03 · Custom Controller Image

[← Cluster add-ons](02-cluster-addons.md) · [Next: Secrets →](04-secrets.md)

---

Why: plugins baked in = same plugins everywhere, no runtime downloads, rollback by tag.

## 1. Pin plugins

```bash
cd jenkins-config/image
docker run --rm -v "$PWD:/w" -w /w jenkins/jenkins:lts-jdk21 \
  jenkins-plugin-cli --plugin-file plugins.txt --available-updates --output txt > plugins.pinned.txt
mv plugins.pinned.txt plugins.txt      # every line now id:version
```

## 2. Build, scan, push

```bash
TAG="$(docker run --rm jenkins/jenkins:lts-jdk21 cat /usr/share/jenkins/ref/../jenkins.war.version 2>/dev/null || echo lts)-$(date +%Y%m%d)-$(git rev-parse --short HEAD)"
REPO=123456789012.dkr.ecr.eu-west-1.amazonaws.com/platform/jenkins-controller

aws ecr get-login-password | docker login --username AWS --password-stdin ${REPO%%/*}
docker build -t $REPO:$TAG .
trivy image --severity CRITICAL --ignore-unfixed --exit-code 1 $REPO:$TAG
docker push $REPO:$TAG
```

> Tag format: `<jenkins version>-<date>-<sha>`. Simplest: read the version from the image label or use `lts-$(date +%Y%m%d)-<sha>`.

## 3. Automate it

Pipeline in `jenkins-config` (runs on Jenkins itself after bootstrap):
PR → build image + Trivy + boot test (`docker run` + wait for `/login` 200) → merge → push → bump `helm/values.yaml` tag via PR.

Boot test snippet:

```bash
docker run -d --name jtest -e JAVA_OPTS=-Djenkins.install.runSetupWizard=false $REPO:$TAG
timeout 180 bash -c 'until docker exec jtest curl -fs localhost:8080/login >/dev/null; do sleep 5; done'
docker logs jtest 2>&1 | grep -E 'SEVERE|Failed Loading plugin' && exit 1 || true
docker rm -f jtest
```

✅ Done when: image in ECR, Trivy clean, boot test passes.

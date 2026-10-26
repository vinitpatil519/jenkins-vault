# Jenkins Cheat Sheet (ops)

> URLs, CLI, REST API, Script Console, files, env vars. Pipeline syntax → [pipeline-syntax.md](pipeline-syntax.md).

[← README](../README.md)

---

## Contents
[Magic URLs](#magic-urls) · [REST API](#rest-api) · [CLI](#cli) · [Script Console](#script-console) · [Files & paths](#files--paths) · [Env vars](#env-vars) · [Docker/K8s ops](#dockerk8s-ops) · [Cron](#cron) · [Ports](#ports)

---

## Magic URLs

| URL | Does |
|-----|------|
| `/manage` | Manage Jenkins |
| `/script` | Groovy Script Console (admin) |
| `/safeRestart` · `/restart` | Restart after builds finish · now |
| `/quietDown` · `/cancelQuietDown` | Stop / resume scheduling builds |
| `/reload` | Reload config from disk |
| `/configuration-as-code` | JCasC: view, reload, export |
| `/configuration-as-code/reference` | JCasC schema for installed plugins |
| `/plugin/job-dsl/api-viewer/index.html` | Job DSL API for installed plugins |
| `/env-vars.html` | Built-in env vars |
| `/job/<job>/pipeline-syntax` | Snippet generator |
| `/job/<job>/directive-generator` | Declarative directive generator |
| `/whoAmI` | Current user + authorities (debug SSO groups) |
| `/threadDump` | JVM threads |
| `/systemInfo` · `/log/all` | System properties · logs |
| `/computer` · `/computer/<node>/log` | Nodes · agent log |
| `/prometheus/` | Metrics (prometheus plugin) |
| `/jnlpJars/agent.jar` · `/jnlpJars/jenkins-cli.jar` | Agent / CLI jars |
| `/job/<j>/<n>/consoleText` | Raw log |
| `/job/<j>/<n>/stop` → `/term` → `/kill` | Abort escalation |
| `/job/<j>/lastSuccessfulBuild/artifact/<path>` | Download artifact |

## REST API

```bash
J=https://jenkins.example.com; A="-u user:API_TOKEN"     # API token → no crumb needed
curl -sg $A "$J/api/json?tree=jobs[name,color]"                         # -g: don't glob [ ]
curl -s  $A "$J/job/app/api/json?tree=lastBuild[number,result]"
curl -s  $A -X POST "$J/job/app/build"
curl -s  $A -X POST "$J/job/app/buildWithParameters?ENV=staging&TAG=1.2.3"
curl -s  $A "$J/job/app/lastBuild/api/json" | jq -r .result
curl -s  $A "$J/job/app/lastBuild/consoleText"
curl -s  $A "$J/job/app/config.xml" > app.xml                           # export job
curl -s  $A -X POST "$J/createItem?name=app2" -H 'Content-Type: application/xml' --data-binary @app.xml
curl -s  $A -X POST "$J/job/app/doDelete"
curl -s  $A "$J/queue/api/json"                                         # queue
curl -s  $A -X POST "$J/queue/cancelItem?id=123"
curl -s  $A "$J/pluginManager/api/json?depth=1&tree=plugins[shortName,version]" | jq -r '.plugins[]|"\(.shortName):\(.version)"'
# password/session auth needs crumb:
CR=$(curl -s -c c.txt -u user:pass "$J/crumbIssuer/api/json" | jq -r '.crumbRequestField+":"+.crumb')
curl -s -b c.txt -u user:pass -H "$CR" -X POST "$J/job/app/build"
# validate a declarative Jenkinsfile
curl -s $A -X POST -F "jenkinsfile=<Jenkinsfile" "$J/pipeline-model-converter/validate"
```

Nested jobs: `/job/folder/job/repo/job/main/` (each level = `job/<name>`; branch names URL-encoded: `feature%2Fx`).

## CLI

```bash
curl -sO $J/jnlpJars/jenkins-cli.jar
alias jcli='java -jar jenkins-cli.jar -s $J -auth user:API_TOKEN -webSocket'
jcli help
jcli who-am-i
jcli list-jobs
jcli build app -p ENV=staging -s -v          # -s wait, -v stream log
jcli console app 42
jcli get-job app > app.xml ; jcli create-job app2 < app.xml ; jcli update-job app < app.xml
jcli list-plugins ; jcli install-plugin prometheus -restart
jcli safe-restart ; jcli quiet-down ; jcli cancel-quiet-down
jcli reload-jcasc-configuration
jcli groovy = < script.groovy                # run Groovy (admin)
jcli disconnect-node agent-1 ; jcli offline-node agent-1 -m "maintenance" ; jcli online-node agent-1
```

## Script Console

```groovy
// Jenkins version + plugins
println Jenkins.VERSION
Jenkins.instance.pluginManager.plugins.sort{it.shortName}.each{ println "${it.shortName}:${it.version}" }

// All jobs + last result
Jenkins.instance.getAllItems(Job).each { println "${it.fullName} → ${it.lastBuild?.result}" }

// Abort all running builds of a job
Jenkins.instance.getItemByFullName('team/app/main').builds.findAll{ it.isBuilding() }.each{ it.doStop() }

// Kill zombie pipeline build
Jenkins.instance.getItemByFullName('team/app/main').getBuildByNumber(42).doKill()

// Clear queue
Jenkins.instance.queue.clear()

// Delete builds older than N, keep last 50
Jenkins.instance.getItemByFullName('app').builds.drop(50).each { it.delete() }

// Disk-heavy jobs
Jenkins.instance.getAllItems(Job).collect{ [it.fullName, it.builds.size()] }.sort{-it[1]}.take(10).each{ println it }

// Offline agents
Jenkins.instance.computers.findAll{ it.offline }.each{ println "${it.name}: ${it.offlineCauseReason}" }

// Decrypt a secret (why admin = god)
println hudson.util.Secret.decrypt('{AQAAABAAAAA...}')

// List credential IDs (not values)
com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
  com.cloudbees.plugins.credentials.common.StandardCredentials, Jenkins.instance, null, null
).each { println "${it.id} (${it.class.simpleName})" }
```

## Files & paths

| Path | What |
|------|------|
| `/var/jenkins_home` (Docker) · `/var/lib/jenkins` (pkg) | `JENKINS_HOME` |
| `$JENKINS_HOME/secrets/initialAdminPassword` | First unlock |
| `$JENKINS_HOME/secrets/master.key` | Master key — back up separately |
| `$JENKINS_HOME/config.xml` | Global config |
| `$JENKINS_HOME/jobs/<job>/config.xml` · `builds/<n>/log` | Job + build log |
| `$JENKINS_HOME/plugins/*.jpi` · `*.jpi.disabled` | Plugins / disable without UI |
| `$JENKINS_HOME/init.groovy.d/*.groovy` | Boot scripts |
| `/usr/share/jenkins/ref/` (Docker) | Files copied to `JENKINS_HOME` on start |
| `journalctl -u jenkins` · `/var/log/jenkins/` | Logs (pkg) |

## Env vars

`BUILD_NUMBER` `BUILD_ID` `BUILD_URL` `BUILD_TAG` `JOB_NAME` `JOB_BASE_NAME` `JOB_URL` `WORKSPACE` `JENKINS_URL` `NODE_NAME` `NODE_LABELS` `EXECUTOR_NUMBER` `GIT_COMMIT` `GIT_PREVIOUS_COMMIT` `GIT_BRANCH` `GIT_URL` `BRANCH_NAME` `CHANGE_ID` `CHANGE_TARGET` `CHANGE_BRANCH` `CHANGE_AUTHOR` `CHANGE_URL` `TAG_NAME`

## Docker/K8s ops

```bash
docker exec -it jenkins bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
docker logs -f jenkins
docker run --rm -v jenkins_home:/data -v $PWD:/b alpine tar czf /b/jh.tgz -C /data --exclude=workspace .   # backup volume

kubectl -n jenkins get pods,pvc,svc,ingress
kubectl -n jenkins logs sts/jenkins -c jenkins -f
kubectl -n jenkins logs sts/jenkins -c config-reload            # JCasC sidecar
kubectl -n jenkins exec -it jenkins-0 -c jenkins -- bash
kubectl -n jenkins get secret jenkins -o jsonpath='{.data.jenkins-admin-password}' | base64 -d
kubectl -n jenkins-agents get pods -w
kubectl -n jenkins-agents describe pod <agent-pod>
helm -n jenkins get values jenkins ; helm -n jenkins history jenkins ; helm -n jenkins rollback jenkins <rev>
```

## Cron

```text
MIN HOUR DOM MON DOW      H = hash of job name (spread load)
H/15 * * * *              every 15 min
H 2 * * 1-5               weekdays 02:xx
H H(0-7) * * *            once between 00:00–07:59
@daily @hourly @weekly @midnight
```

## Ports

| Port | Use |
|------|-----|
| 8080 | Web UI / API / webhooks / WebSocket agents |
| 50000 | Inbound TCP agents (JNLP) |
| 22 | SSH agents (controller → agent) |
| 9000 | SonarQube · 9090 Prometheus · 3000 Grafana · 8081 Nexus |

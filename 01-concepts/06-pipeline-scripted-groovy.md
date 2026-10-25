# 06 · Scripted Pipeline, Groovy, CPS & Sandbox

> **Goal:** know when scripted beats declarative, the Groovy you actually need, and why CPS/sandbox errors happen.

[← Declarative](05-pipeline-declarative.md) · [Next: Shared Libraries →](07-shared-libraries.md)

---

## 1. Declarative vs Scripted

| | Declarative | Scripted |
|--|-------------|----------|
| Starts with | `pipeline { }` | `node { }` |
| Structure | Fixed, validated before run | Free Groovy |
| Learning curve | Low | Higher |
| `post`, `when`, `options`, restart from stage | ✅ built-in | Manual (try/catch/finally) |
| Dynamic stages / complex logic | via `script {}` | ✅ natural |
| Linting | `declarative-linter` | none |
| Recommendation | ⭐ Default | Shared-lib internals, very dynamic flows |

Both compile to the same engine (CPS). Can mix: scripted inside `script {}`.

---

## 2. Scripted skeleton

```groovy
properties([
  buildDiscarder(logRotator(numToKeepStr: '20')),
  disableConcurrentBuilds(),
  parameters([choice(name: 'ENV', choices: ['dev', 'prod'])])
])

node('linux') {
  try {
    stage('Checkout') { checkout scm }
    stage('Build')    { sh 'make build' }
    stage('Test') {
      parallel(
        unit: { sh 'make unit' },
        lint: { sh 'make lint' }
      )
    }
    if (env.BRANCH_NAME == 'main') {
      stage('Deploy') {
        timeout(time: 15, unit: 'MINUTES') { input 'Deploy?' }
        withCredentials([string(credentialsId: 'token', variable: 'TOKEN')]) {
          sh './deploy.sh'
        }
      }
    }
    currentBuild.result = 'SUCCESS'
  } catch (err) {
    currentBuild.result = 'FAILURE'
    throw err
  } finally {
    junit allowEmptyResults: true, testResults: '**/reports/*.xml'
    cleanWs()
  }
}
```

### Dynamic parallel (classic scripted win)

```groovy
def services = sh(script: 'ls services', returnStdout: true).trim().split('\n')
def branches = [:]
services.each { svc ->
  branches[svc] = { node('docker') { checkout scm; sh "make -C services/${svc} test" } }
}
branches.failFast = true
parallel branches
```

---

## 3. Groovy you actually need

```groovy
def name = 'api'                      // variable
String tag = "v${BUILD_NUMBER}"       // GString interpolation (double quotes)
def raw = 'no ${interp}'              // single quotes = literal
def list = ['a', 'b']; list << 'c'
def map  = [env: 'dev', replicas: 2]; map.env; map['replicas']
list.each { println it }
list.eachWithIndex { v, i -> echo "${i}=${v}" }
def up = list.collect { it.toUpperCase() }
def found = list.find { it == 'b' }
def filtered = list.findAll { it != 'a' }
def safe = obj?.field ?: 'default'    // safe-nav + elvis
def multi = """
line ${name}
"""
if (x ==~ /v\d+/) { }                 // regex full match
```

### Quotes & secrets (⚠️ classic interview)

```groovy
withCredentials([string(credentialsId: 'tok', variable: 'TOKEN')]) {
  sh 'curl -H "Authorization: Bearer $TOKEN" ...'   // ✅ shell expands, secret not in Groovy string
  sh "curl -H 'Authorization: Bearer ${TOKEN}' ..."  // ❌ Groovy interpolates → secret in process args/
                                                     //    Jenkins warns "insecure interpolation"
}
```

Rule: **secrets → single-quoted `sh`**, let the shell expand them.

---

## 4. CPS — why Pipeline Groovy is weird

Pipeline Groovy is transformed into **Continuation Passing Style** so the program state can be **serialized to disk** (`program.dat`). That's how pipelines **survive controller restarts** and pause on `input` for days.

Consequences:

| Symptom | Cause | Fix |
|---------|-------|-----|
| `java.io.NotSerializableException: java.util.regex.Matcher` | Non-serializable object alive across a step | Use it inside a `@NonCPS` method, or null it before next step |
| Closures in `.each`/`.sort` behave oddly (old versions) | CPS transforms closures | `for (x in list)` loops, or `@NonCPS` |
| Pipeline slow, controller CPU high | Heavy Groovy logic on controller | Move to `sh` script / `@NonCPS` |
| `method code too large` | Jenkinsfile > JVM 64KB method limit | Split into shared library / functions |

```groovy
@NonCPS                                   // runs as plain Groovy: fast, not resumable
def parseVersions(String text) {
  return text.readLines().findAll { it ==~ /v\d+\.\d+/ }
}
```

`@NonCPS` methods **must not call pipeline steps** (`sh`, `echo`, `node`...).

**Durability hints**: `MAX_SURVIVABILITY` (default, writes state often), `SURVIVABLE_NONATOMIC`, `PERFORMANCE_OPTIMIZED` (fewer writes, may lose running state on crash). Set per job or globally.

---

## 5. Groovy Sandbox & Script Approval

- Untrusted Pipeline Groovy runs in the **Script Security sandbox** — only whitelisted methods.
- Non-whitelisted call → `RejectedAccessException: Scripts not permitted to use method ...`.
- Admin approves in **Manage Jenkins → In-process Script Approval**.

| Code location | Sandbox? |
|---------------|----------|
| `Jenkinsfile` in SCM | Yes |
| Folder-level shared library | Yes (untrusted) |
| **Global shared library** (Manage Jenkins) | **No** — trusted, full Java access ⚠️ |
| Script Console (`/script`) | No — admin only, full power |
| Job DSL scripts | Configurable (sandbox if not admin) |

Approving signatures like `java.lang.Runtime exec` or `staticMethod jenkins.model.Jenkins getInstance` = giving users controller code execution. **Don't approve blindly.** Put privileged logic in a reviewed global library instead.

---

## 6. `currentBuild` & `env` & `params`

```groovy
currentBuild.result            // null (running) / SUCCESS / FAILURE ...
currentBuild.currentResult     // never null
currentBuild.displayName = "#${BUILD_NUMBER} ${params.ENV}"
currentBuild.description = "image: ${TAG}"
currentBuild.previousBuild?.result
currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')   // who triggered
currentBuild.changeSets        // commits in this build
env.MY_VAR = 'x'               // global env var (always String)
params.ENV                     // typed parameter value
```

---

## 7. Error handling patterns

```groovy
retry(3) { sh 'flaky-network-call' }

timeout(time: 5, unit: 'MINUTES') { waitUntil(initialRecurrencePeriod: 5000) {
  sh(script: 'curl -fs http://svc/health', returnStatus: true) == 0
} }

catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') { sh 'optional-check' }
warnError('Lint failed') { sh 'make lint' }    // stage UNSTABLE, continues
unstable('coverage below 80%')

def rc = sh(script: 'test.sh', returnStatus: true)
if (rc != 0) { error "tests failed rc=${rc}" }
```

`error` = fail build. `throw` exceptions inside script also fail. `org.jenkinsci.plugins.workflow.steps.FlowInterruptedException` = abort/timeout — **re-throw it**, don't swallow it in `catch (e)`.

---

## 8. `load` and splitting files

```groovy
def utils = load 'ci/utils.groovy'     // file must end with `return this`
utils.deploy('dev')
```

Fine for one repo. For reuse across repos → shared library.

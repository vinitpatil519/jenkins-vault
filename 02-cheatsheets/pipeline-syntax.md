# Pipeline Syntax Cheat Sheet

> One page. Every directive and step you'll write. Details → [01-concepts/05](../01-concepts/05-pipeline-declarative.md), [06](../01-concepts/06-pipeline-scripted-groovy.md).

[← README](../README.md)

---

## Declarative skeleton

```groovy
@Library('platform-lib@v2') _                          // optional shared lib
pipeline {
  agent none | any | { label 'x' } | { docker {...} } | { dockerfile {...} } | { kubernetes {...} }
  options     { timeout(time: 30, unit: 'MINUTES'); buildDiscarder(logRotator(numToKeepStr: '20'))
                disableConcurrentBuilds(abortPrevious: true); timestamps(); skipDefaultCheckout()
                retry(1); ansiColor('xterm'); preserveStashes(); quietPeriod(5) }
  parameters  { string(name:'A', defaultValue:''); choice(name:'E', choices:['dev','prod'])
                booleanParam(name:'B', defaultValue:false); text(name:'T'); password(name:'P') }
  environment { X = 'v'; C = credentials('id') }       // C_USR / C_PSW for user+pass
  triggers    { cron('H 2 * * *'); pollSCM('H/5 * * * *'); upstream('job-a') }
  tools       { maven 'm3'; jdk 'jdk21'; nodejs 'node20' }
  stages {
    stage('S') {
      agent { ... }      when { ... }      environment { ... }      options { ... }
      input { message 'OK?'; submitter 'grp'; parameters { ... } }
      steps { ... }  |  parallel { stage(...) {...} }  |  stages { ... }  |  matrix { ... }
      post { ... }
      failFast true      // with parallel
    }
  }
  post { always{} success{} failure{} unstable{} aborted{} changed{} fixed{} regression{} unsuccessful{} cleanup{} }
}
```

## `when`

```groovy
branch 'main' | branch pattern: 'rel-\\d+', comparator: 'REGEXP'
tag 'v*' | buildingTag() | changeRequest() | changeRequest target: 'main'
environment name: 'E', value: 'prod' | expression { params.B } | equals expected: 1, actual: x
changeset 'svc/**' | changelog '.*\\[deploy\\].*' | triggeredBy 'TimerTrigger'
allOf { ... } | anyOf { ... } | not { ... }
beforeAgent true | beforeInput true | beforeOptions true
```

## Steps

```groovy
sh 'cmd'                                         // bat / powershell on Windows
def out = sh(script: 'cmd', returnStdout: true).trim()
def rc  = sh(script: 'cmd', returnStatus: true)
echo 'msg' ; error 'fail' ; unstable 'warn'
checkout scm ; git url: 'u', branch: 'main', credentialsId: 'c'
dir('sub') { ... } ; withEnv(['A=1']) { ... } ; container('maven') { ... }
timeout(time: 5, unit: 'MINUTES') { ... } ; retry(3) { ... } ; sleep 10 ; waitUntil { ... }
catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') { ... } ; warnError('m') { ... }
stash name: 's', includes: 'dist/**' ; unstash 's'
archiveArtifacts artifacts: 'build/*.jar', fingerprint: true
junit '**/TEST-*.xml'
input message: 'Go?', ok: 'Yes', submitter: 'ops', parameters: [...]
build job: 'deploy', parameters: [string(name: 'TAG', value: v)], wait: true, propagate: true
lock('res') { ... } ; milestone(1)
cleanWs() ; deleteDir()
readFile 'f' ; writeFile file: 'f', text: 't' ; fileExists 'f'
readJSON file: 'a.json' ; readYaml file: 'a.yaml' ; writeJSON file: 'o.json', json: m   // pipeline-utility-steps
script { ... }                                    // any Groovy inside declarative
```

## Credentials

```groovy
withCredentials([
  string(credentialsId: 'tok', variable: 'TOKEN'),
  usernamePassword(credentialsId: 'up', usernameVariable: 'U', passwordVariable: 'P'),
  file(credentialsId: 'kcfg', variable: 'KUBECONFIG'),
  sshUserPrivateKey(credentialsId: 'ssh', keyFileVariable: 'KEY', usernameVariable: 'USER'),
  certificate(credentialsId: 'cert', keystoreVariable: 'KS', passwordVariable: 'KSP')
]) { sh 'use "$TOKEN"' }                           // single quotes!
sshagent(['ssh']) { sh 'git push' }
docker.withRegistry('https://reg', 'up') { img.push() }
```

## Agents

```groovy
agent { label 'linux && docker' }
agent { docker { image 'node:20'; args '-v cache:/c'; reuseNode true } }
agent { dockerfile { filename 'Dockerfile.ci'; additionalBuildArgs '--pull' } }
agent { kubernetes { yamlFile 'ci/pod.yaml'; defaultContainer 'build'; inheritFrom 'default' } }
```

## Parallel & matrix

```groovy
parallel { stage('a') { steps {...} } stage('b') { steps {...} } }
matrix {
  axes { axis { name 'OS'; values 'linux','win' } axis { name 'V'; values '17','21' } }
  excludes { exclude { axis { name 'OS'; values 'win' }; axis { name 'V'; values '17' } } }
  stages { stage('t') { steps { echo "${OS} ${V}" } } }
}
```

## Scripted equivalents

```groovy
properties([buildDiscarder(logRotator(numToKeepStr: '20')), pipelineTriggers([cron('H 2 * * *')])])
node('linux') {
  stage('b') { checkout scm; sh 'make' }
  parallel a: { sh 'x' }, b: { sh 'y' }
  try { sh 'risky' } catch (e) { currentBuild.result = 'FAILURE'; throw e } finally { cleanWs() }
}
docker.image('node:20').inside('-v c:/c') { sh 'npm ci' }
podTemplate(yaml: '...') { node(POD_LABEL) { container('maven') { sh 'mvn -v' } } }
```

## Groovy quick

```groovy
"interp ${x}"  vs  'literal'  vs  """multi ${x}"""  vs  '''multi literal'''
def m = [a: 1]; m.a; m['a']; def l = [1, 2]; l << 3
l.each { }  l.collect { }  l.findAll { }  l.find { }  l.any { }  l.join(',')
x?.y ?: 'default'
@NonCPS def parse(s) { ... }     // no steps inside
currentBuild.result / .currentResult / .displayName / .description / .previousBuild
env.FOO = 'x'   params.FOO
```

## Shared library

```groovy
@Library('lib@v2') _              // vars/foo.groovy → foo()
@Library('lib') import org.x.Y    // src/org/x/Y.groovy
library 'lib@main'                // dynamic
libraryResource 'org/x/pod.yaml'  // resources/
```

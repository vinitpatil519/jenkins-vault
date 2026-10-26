# Jenkins Q&A

[← Interview](README.md)

---

## Basics

**1. What is Jenkins?** Open-source Java automation server for CI/CD. Core is small; features come from ~2000 plugins. Pipelines defined as code in a `Jenkinsfile`.

**2. Controller vs agent?** Controller: UI, API, config, scheduling, pipeline Groovy execution, credentials. Agent: runs build steps. Connected via remoting.

**3. Executor?** A build slot on a node. N executors = N concurrent builds on that node.

**4. Node vs agent vs label?** Node = any machine (controller or agent). Agent = node that runs builds. Label = tag used to route builds (`linux && docker`).

**5. Where is Jenkins data stored?** `JENKINS_HOME` as XML files: `config.xml`, `jobs/*/config.xml`, `builds/`, `plugins/`, `secrets/`, `credentials.xml`. No database.

**6. Freestyle vs Pipeline?** Freestyle = UI-configured, not versioned, no stages. Pipeline = code, versioned, stages, survives restarts, parallel, reusable via libraries.

**7. Declarative vs Scripted?** Declarative: structured `pipeline {}`, validated, `post`/`when`/`options` built in. Scripted: `node {}` free Groovy, more flexible. Default declarative; use `script {}` or shared libs for logic.

**8. Multibranch pipeline?** Auto-creates a job per branch/PR/tag containing a Jenkinsfile; deletes when branch is gone.

**9. Organization folder?** Scans a GitHub org / Bitbucket team and creates multibranch jobs for every repo with a Jenkinsfile.

**10. Ways to trigger a build?** Webhook, poll SCM, cron, upstream job, `build` step, REST API / CLI, manual, Generic Webhook Trigger.

**11. pollSCM vs webhook?** Polling asks Git periodically (load, delay). Webhook = Git pushes event (instant). Use webhooks; polling only as fallback.

**12. What does `H` in cron mean?** Hash of job name → spreads start times so jobs don't all fire at the same minute.

**13. Build results?** SUCCESS, UNSTABLE (tests failed/quality warnings), FAILURE, ABORTED, NOT_BUILT.

**14. UNSTABLE vs FAILURE?** FAILURE = step failed (non-zero exit). UNSTABLE = build completed but quality issues (e.g. `junit` recorded failures, `unstable()`).

**15. LTS vs weekly?** Weekly = latest features. LTS = every 12 weeks, stabilized, backports. Prod uses LTS.

**16. Default ports?** 8080 HTTP, 50000 inbound agents.

**17. Initial admin password?** `$JENKINS_HOME/secrets/initialAdminPassword`.

## Pipelines

**18. Sections of declarative pipeline?** `agent`, `stages/stage/steps` required; optional `options`, `parameters`, `environment`, `triggers`, `tools`, `when`, `input`, `post`, `parallel`, `matrix`.

**19. `post` conditions?** always, success, failure, unstable, aborted, changed, fixed, regression, unsuccessful, cleanup (runs last).

**20. Run stage only on main?** `when { branch 'main' }`. Add `beforeAgent true` to skip agent allocation.

**21. Pass data between stages?** Env var set in `script { env.X = ... }`, files in shared workspace (same agent), `stash/unstash` across agents, artifacts/repo for large files.

**22. stash vs archiveArtifacts?** stash = temp within one build between agents (stored on controller). archive = kept with build record for later download.

**23. Parallel stages?** `parallel { stage(...) }`; `failFast true` aborts siblings on failure.

**24. Matrix?** Run same stages across axis combos (OS × version), with `excludes`.

**25. Approval gate?** `input` step/directive with `submitter`; wrap in `timeout`; use `agent none` on that stage to not hold an executor.

**26. Timeouts and retries?** `options { timeout(...) }` / `timeout(...) {}` step; `retry(n) {}`.

**27. Prevent concurrent builds?** `disableConcurrentBuilds()`; `abortPrevious: true` cancels older running build on new push.

**28. Lock shared resource across jobs?** Lockable Resources `lock('staging') {}`.

**29. Skip default checkout?** `options { skipDefaultCheckout() }` then `checkout scm` where needed.

**30. Return value from shell?** `sh(script: '...', returnStdout: true).trim()`; exit code via `returnStatus: true`.

**31. Trigger another job?** `build job: 'x', parameters: [...], wait: true, propagate: false`.

**32. Replay?** Re-run a build with modified Groovy without committing. Debug only; restricted permission.

**33. Restart from stage?** Declarative only; reruns from chosen stage; needs `preserveStashes` if stages use stash.

**34. What is CPS?** Continuation Passing Style transformation of pipeline Groovy so state can be serialized → pipelines survive controller restarts. Causes `NotSerializableException` quirks.

**35. `@NonCPS`?** Method runs as plain Groovy (fast, not serialized); cannot call pipeline steps.

**36. Where does pipeline code execute?** Groovy on controller; `sh`/`bat`/file steps on agent.

**37. Groovy sandbox?** Restricts untrusted pipeline code to whitelisted methods. Others require admin script approval.

**38. Why avoid heavy Groovy in Jenkinsfile?** Runs on controller → CPU/memory pressure for everyone. Do work in `sh` scripts.

**39. `Method code too large`?** Jenkinsfile exceeds JVM 64 KB method limit. Split into functions / shared library.

**40. Validate Jenkinsfile without running?** `POST /pipeline-model-converter/validate`, VS Code Jenkins linter, or Jenkinsfile Runner.

**41. Parameters in Jenkinsfile not shown on first run?** They register after first execution; first run uses defaults.

## Shared libraries

**42. What is a shared library?** Git repo of reusable pipeline code: `vars/` (global steps), `src/` (classes), `resources/` (files). Loaded with `@Library`.

**43. Trusted vs untrusted library?** Global libs (Manage Jenkins) are trusted → run outside sandbox. Folder libs are sandboxed.

**44. How do you version libraries?** Git tags (semver); consumers pin `@Library('lib@v2')`; test branches via `@Library('lib@branch')`.

**45. Implicit load?** `implicit: true` loads library automatically for all pipelines.

**46. How to test shared library code?** JenkinsPipelineUnit (unit), Jenkinsfile Runner / test Jenkins (integration), canary repos.

**47. Why `Serializable` in `src/` classes?** CPS serializes pipeline state including your objects.

## Agents

**48. Agent connection methods?** SSH (controller → agent), inbound TCP 50000 (agent → controller), inbound WebSocket (over 443), cloud plugins.

**49. Why 0 executors on controller?** Builds on controller can read `JENKINS_HOME` (secrets, master.key) and starve controller resources.

**50. Kubernetes plugin how it works?** Per build, controller creates a pod from template (jnlp + tool containers), agent connects inbound, build runs, pod deleted.

**51. `container('x')` step?** Run steps in a specific container of the agent pod (default is jnlp unless `defaultContainer`).

**52. Static vs ephemeral agents?** Ephemeral: clean, secure, elastic, cheaper at scale. Static: warm caches, special hardware, faster start.

**53. Docker agent vs Docker plugin?** `agent { docker }` (Docker Pipeline) runs steps in a container on an existing agent. Docker plugin = cloud that provisions agent containers.

**54. Build Docker images in K8s agents safely?** Rootless BuildKit, Buildah, Kaniko (maintained fork), Jib/ko. Not docker.sock, not privileged DinD.

**55. Agent goes offline — causes?** Network/firewall, wrong Jenkins URL/tunnel, Java version mismatch, disk full, agent JVM OOM, controller restart.

**56. Speed up K8s agents?** Pre-built CI images, image pull-through cache, node warm pools/`idleMinutes`, cache PVCs, remote build caches, right-size requests.

## Credentials & security

**57. Credential types?** Username/password, secret text, secret file, SSH key, certificate, GitHub App, AWS, etc.

**58. Credential scopes?** System (Jenkins internal only), Global (all jobs), folder-level (jobs in folder), user.

**59. How are credentials stored?** Encrypted in `credentials.xml` with `hudson.util.Secret`, key protected by `secrets/master.key`.

**60. Use secrets safely in pipeline?** `withCredentials`, single-quoted `sh` (shell expands), no `echo`/`set -x`, `--password-stdin`, folder scope, external manager.

**61. Why is `sh "echo ${TOKEN}"` bad?** Groovy interpolates the secret into the command string → visible in process args and possibly logs; Jenkins warns.

**62. Jenkins + Vault?** HashiCorp Vault plugin (`withVault`) with AppRole/K8s auth, or ESO syncing to K8s Secrets / JCasC.

**63. Authentication options?** Jenkins DB, LDAP/AD, OIDC, SAML, GitHub OAuth.

**64. Authorization options?** Matrix, project-based matrix, role-based strategy plugin.

**65. Dangerous permissions?** Overall/Administer, Job/Configure, Run/Replay, Script Console, Credentials/View, Agent/Connect.

**66. CSRF crumb?** Anti-CSRF token required for POST with session auth. API tokens are exempt.

**67. Protect against malicious PRs?** Fork trust = collaborators; no credentials for untrusted PRs; Jenkinsfile from trusted branch for forks; review changes to CI files.

**68. Hardening checklist?** LTS + patching, SSO/MFA, RBAC, 0 controller executors, minimal pinned plugins, no anonymous access, HTTPS, network isolation, audit trail, JCasC, backups of secrets.

**69. Notable Jenkins CVE?** CVE-2024-23897: CLI argument parsing allowed arbitrary file read (incl. secrets) → potential RCE. Fix: upgrade, disable CLI.

## Config as code

**70. JCasC?** Configuration-as-Code plugin: Jenkins global config from YAML (`CASC_JENKINS_CONFIG`), reloadable.

**71. Job DSL?** Groovy DSL generating jobs; seed job runs DSL scripts from Git.

**72. JCasC vs Job DSL?** JCasC = system config (security, clouds, creds). Job DSL = jobs/folders/views.

**73. Secrets in JCasC?** `${VAR}` resolved from env vars, `/run/secrets` files, Vault/AWS SM providers. Never literal.

**74. Fully reproducible Jenkins?** Custom image (core + pinned plugins) + JCasC + Job DSL + shared libs + Jenkinsfiles in Git; only build history needs backup.

## Plugins

**75. Must-have plugins?** workflow-aggregator, git, github-branch-source, kubernetes, configuration-as-code, job-dsl, credentials-binding, role-strategy/matrix-auth, oic-auth, timestamper, ws-cleanup, junit, warnings-ng, prometheus.

**76. Manage plugins safely?** `plugins.txt` with pinned versions, `jenkins-plugin-cli`, baked into image, upgrade weekly through staging controller, remove unused.

**77. Jenkins won't start after plugin update?** Check logs for SEVERE; roll back image; or disable plugin by creating `plugins/x.jpi.disabled`.

## Scaling, HA, backup

**78. Is Jenkins HA?** OSS: single active controller. HA via fast recovery (K8s + PV + JCasC) or active/passive shared storage. Active/active = CloudBees CI.

**79. Scale Jenkins?** Agents horizontally (K8s/cloud autoscaling); controllers by sharding per team/domain; tune heap/GC/IO; retention; webhooks instead of polling.

**80. Jenkins is slow — steps?** Determine UI vs queue vs build; heap/GC/thread dumps; retention; agent capacity/labels; pipeline caching/parallelism; monitor.

**81. What to back up?** `JENKINS_HOME` minus workspace/caches; especially `secrets/`, configs, jobs, users. Or config-as-code + snapshots.

**82. Upgrade Jenkins safely?** Read changelog/upgrade guide, test on staging controller, backup/snapshot, quietDown, deploy new image, smoke test, rollback = previous tag.

**83. Two controllers on same `JENKINS_HOME`?** Never simultaneously active — corruption.

## Integrations

**84. GitHub App vs PAT?** App: not tied to person, fine-grained, short-lived tokens, higher rate limits, Checks API. PAT: personal, long-lived.

**85. Commit status to GitHub?** github-branch-source/github-checks report automatically in multibranch; or `githubNotify` / `publishChecks`.

**86. SonarQube integration?** `withSonarQubeEnv('sonar') { scanner }` + `waitForQualityGate` (Sonar webhook to `/sonarqube-webhook/`).

**87. Notify Slack only when needed?** `post { failure {...} fixed {...} }`.

**88. Deploy to K8s from Jenkins?** Push: `helm upgrade --install --atomic` with scoped kubeconfig. Better: GitOps — Jenkins updates env repo, Argo CD syncs.

**89. Terraform in Jenkins?** fmt/validate/plan on PR, plan as artifact, approval, `apply tfplan` on main, state in remote backend with locking.

## Observability

**90. Monitor Jenkins?** prometheus plugin `/prometheus` → Prometheus → Grafana; alerts on queue, offline agents, heap, health score; OpenTelemetry plugin for traces; logs to Loki.

**91. Key metrics?** `jenkins_queue_size_value`, `jenkins_executor_in_use_value`, `jenkins_node_offline_value`, `jenkins_health_check_score`, JVM heap, build duration/result per job.

**92. Measure DORA from Jenkins?** Record deploy events (time, commit, result) from deploy stages → metrics DB / DevLake.

## Misc / gotchas

**93. Workspace `@2` / `@tmp`?** Concurrent builds get suffixed workspaces; `@tmp` holds temp/durable-task files.

**94. Clean workspace?** `cleanWs()` in `post { cleanup }` or `deleteDir()`.

**95. Exit code 137?** Process killed (SIGKILL), usually OOM → raise container memory limit.

**96. Exit code 127?** Command not found in agent image.

**97. `RejectedAccessException`?** Sandbox blocked a method → approve if safe or move to trusted library.

**98. `No such DSL method`?** Plugin missing or library not loaded / typo.

**99. Blue Ocean?** Old UX plugin, no longer actively developed. Use Pipeline Graph View.

**100. Jenkins vs GitHub Actions?** Jenkins: self-hosted, max flexibility, any SCM, on-prem, ops burden. GHA: managed, GitHub-native, less control, cost at scale. See [22](../01-concepts/22-jenkins-vs-others.md).

# Scenario Questions

> Structure every answer: **clarify → diagnose (data first) → fix → prevent**.

[← Interview](README.md)

---

### 1. Builds sit in the queue for 20 minutes.
Check queue reason (hover: "waiting for executor on label X"). Causes: label typo/too specific, agents offline, K8s cloud `containerCap` reached, namespace quota, pods `Pending` (no nodes, autoscaler limits, image pull errors). Fix the bottleneck; add autoscaling; alert on `jenkins_queue_size_value`.

### 2. Jenkins UI takes 30 s per page.
Heap/GC (GC log, `jstat`), thread dump for blocked threads, huge build history, a misbehaving plugin, slow disk IO. Fix: retention, heap tuning, upgrade/remove plugin, faster storage; long term split controller.

### 3. Controller pod restarted mid-deploy. What happens to running pipelines?
Durable pipelines resume from last checkpoint after restart if agents reconnect. K8s agent pods may be lost → step fails. Design idempotent deploy steps, use `helm --atomic`, retry logic, GitOps (Argo continues regardless).

### 4. A secret appeared in console logs.
Rotate the secret now. Find the cause: Groovy interpolation, `set -x`, transformed value (base64) not masked, tool printing it. Fix pipeline (single quotes, `--password-stdin`), purge affected build logs, add secret scanning.

### 5. A contractor's fork PR tried to print credentials.
Multibranch fork trust should block secrets for untrusted forks. Verify trust = collaborators, PR builds from forks run without credentials, Jenkinsfile taken from target branch. Audit logs; rotate any exposed creds.

### 6. Plugin update broke all pipelines on Monday.
Roll back to previous controller image tag (plugins baked in). Root cause from changelog. Prevent: pinned `plugins.txt`, staging controller with smoke pipelines, weekly controlled updates.

### 7. Disk on controller is 95% full.
Find biggest: `du -sh $JENKINS_HOME/jobs/*/builds | sort -h`. Usually old builds/artifacts/logs, workspaces on built-in node. Fix: `buildDiscarder` everywhere, artifacts to S3/Nexus, 0 executors on controller, expand volume, alert on disk.

### 8. `docker build` fails in K8s agent: "Cannot connect to the Docker daemon".
Pods don't have Docker daemon. Use rootless BuildKit/Buildah/Kaniko-style builders, or a DinD sidecar (privileged, isolated node pool). Don't mount host socket.

### 9. Build passes locally, fails in Jenkins.
Environment diff: tool versions, env vars, missing files (`.gitignore`d), user/permissions (uid in container), network/proxy, shallow clone (`git describe` fails), resource limits (137). Pin tools via images; replicate with the same container locally.

### 10. Deploy to prod failed halfway.
Helm `--atomic` auto-rollback or `helm rollback`; GitOps `git revert`. Communicate, check DB migrations (expand/contract makes rollback safe), postmortem, add canary analysis.

### 11. Need same pipeline for 150 repos with minimal duplication.
Shared library golden pipeline, org folder or Job DSL from a teams file, standard contract (`make ci`, Dockerfile), versioned library with canary rollout, opt-in parameters.

### 12. Security team wants every image scanned and only signed images in prod.
Pipeline: Trivy gate + SBOM + cosign sign (KMS key). Cluster: Kyverno/policy-controller verifies signatures for registry pattern. Continuous scanning of running images; exceptions with expiry.

### 13. Migrate 500 freestyle jobs to pipeline.
Inventory and group by pattern; build shared library steps for common patterns; generate Jenkinsfiles/Job DSL; migrate team by team; run old+new in parallel; delete freestyle after confidence.

### 14. Design CI/CD for a regulated (bank) environment.
On-prem Jenkins (air-gapped mirrors for plugins/deps/images), SSO + RBAC + audit trail to SIEM, segregation of duties (dev can't approve own prod deploy: `input submitter` + PR approvals), signed artifacts and provenance, immutable logs retention, change ticket linkage, DR tested.

### 15. Pipeline takes 45 min; devs complain.
Stage timings first. Then: parallelize, cache, pre-baked images, only affected modules, shard tests, move E2E to post-merge/nightly, bigger agents for heavy stages. Target PR feedback < 10 min.

### 16. Agents randomly disconnect.
Agent logs + controller logs. Causes: idle timeouts on LB/proxy (use WebSocket/keepalives), spot interruption, OOM kills, network policies, Java mismatch. Fix per cause; make builds retry on agent loss (`retry(conditions: [agent(), kubernetesAgent()])`).

### 17. How do you know your CI/CD is improving?
DORA metrics (deploy frequency, lead time, change failure rate, recovery time) + pipeline metrics (duration p50/p95, queue time, success rate, flaky rate) on a dashboard, reviewed monthly.

### 18. Controller lost (volume corrupted). Recover.
Restore latest snapshot to new PV; or fresh install from image + JCasC + seed (config back in minutes), restore `secrets/` to decrypt credentials. Verify agents, webhooks, sample pipelines. Record RTO.

### 19. Webhooks from GitHub return 403.
CSRF/crumb if posting to wrong endpoint, SSO proxy intercepting, IP allow-list missing GitHub ranges, wrong Jenkins URL. Check "Recent Deliveries" response body; expose only `/github-webhook/` publicly.

### 20. Someone asks to approve a script signature `java.lang.Runtime exec`.
Deny: that's arbitrary command execution on the controller. Move the need into a reviewed trusted shared library step or `sh` on an agent.

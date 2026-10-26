# DevOps & CI/CD Buzzwords

[← Buzzwords index](README.md)

| Term | Meaning |
|------|---------|
| A/B testing | Send user groups to different versions to compare a metric |
| Air-gapped | Environment with no internet; everything mirrored internally |
| Artifact | Build output kept and deployed (jar, image, chart) |
| Artifact promotion | Moving the same artifact dev → staging → prod without rebuilding |
| Blue/green deployment | Two identical envs; switch traffic from old (blue) to new (green) |
| Branch protection | Repo rule: merges need passing checks/reviews |
| Build matrix | Same job run over combinations (OS × version) |
| Build once, deploy many | One artifact, promoted across all environments |
| Canary release | New version gets small % of traffic first, grows if healthy |
| CalVer | Version from date (`2026.10.1`) |
| ChatOps | Running ops actions from chat (Slack bots) |
| CI (Continuous Integration) | Merge often; every change auto built + tested |
| CD (Continuous Delivery) | Every green build is releasable; prod deploy one click |
| CD (Continuous Deployment) | Every green build auto deploys to prod |
| Change failure rate | % of deploys causing incident/rollback (DORA) |
| Change freeze | Period where no deploys allowed |
| CAB | Change Advisory Board: humans approving changes |
| Cherry-pick | Apply one specific commit onto another branch |
| CODEOWNERS | File mapping paths to required reviewers |
| Config drift | Real system no longer matches declared config |
| Conventional Commits | Commit format `feat:`, `fix:` enabling auto versioning |
| Dark launch | Deploy feature hidden from users; enable later |
| Dependency hell | Conflicting/transitive dependency versions |
| Deployment frequency | How often you deploy to prod (DORA) |
| Deployment pipeline | Automated path from commit to production |
| DevEx (DX) | Developer experience: how easy/fast it is to ship |
| DevOps | Culture + practices uniting dev and ops: automation, ownership, fast feedback |
| DevSecOps | DevOps with security built into every stage |
| DORA metrics | Deploy frequency, lead time, change failure rate, recovery time |
| Dogfooding | Using your own product internally |
| Ephemeral environment | Short-lived env per PR/test, destroyed after |
| Fail fast | Run cheapest checks first; stop at first failure |
| Feature branch | Branch per feature, merged via PR |
| Feature flag / toggle | Runtime switch enabling code paths without deploy |
| Flaky test | Test that passes/fails without code change |
| Gated check-in | Change merges only if build/tests pass |
| GitFlow | Branching model with develop/release/hotfix branches |
| GitHub Flow | Branch → PR → main → deploy |
| GitOps | Git is source of truth; agent reconciles system to Git |
| Golden image | Pre-baked, approved VM/container base |
| Golden path / paved road | Supported, opinionated default way to build/deploy |
| Hermetic build | Build with all inputs declared, no network surprises; reproducible |
| Hotfix | Urgent prod fix outside normal release cadence |
| IaC (Infrastructure as Code) | Infra defined in versioned code (Terraform) |
| Idempotent | Running it twice = same result as once |
| Immutable infrastructure | Replace servers/containers instead of patching in place |
| Inner loop / outer loop | Local code-build-test cycle / CI-review-deploy cycle |
| Integration hell | Painful merges from long-lived branches |
| Lead time for changes | Commit → running in prod (DORA) |
| Merge queue | Serializes merges, testing each against latest main |
| Monorepo / polyrepo | All code in one repo / repo per service |
| MTTR | Mean time to recover/restore |
| Mutable vs immutable tag | Tag that can be re-pointed (`latest`) vs never changes |
| Nightly build | Scheduled daily full build/test |
| Pipeline as code | Pipeline defined in versioned file (Jenkinsfile) |
| Platform engineering | Team building internal self-service platforms for devs |
| Pre-commit hook | Script run before commit (lint, secrets scan) |
| Progressive delivery | Gradual rollout with automated checks (canary, flags) |
| Promotion | Moving a build to next environment |
| Provenance | Verifiable record of how/where an artifact was built |
| Pull request (PR) / Merge request (MR) | Proposed change for review before merge |
| Quality gate | Pass/fail threshold blocking promotion |
| Rebase | Replay commits on new base for linear history |
| Release train | Releases at fixed schedule, whatever is ready |
| Reproducible build | Same source → bit-identical output |
| Ring deployment | Rollout in widening user rings |
| Rollback / roll forward | Revert to previous version / fix with new version |
| Runner / agent | Machine executing CI jobs |
| Semantic versioning (SemVer) | MAJOR.MINOR.PATCH: breaking.feature.fix |
| Shadow deployment | Mirror real traffic to new version, discard responses |
| Shift left | Move testing/security earlier in lifecycle |
| Shift right | Test/observe in production (canary, chaos) |
| Smoke test | Quick check that the basic thing works after deploy |
| Snowflake server | Hand-configured, unreproducible server |
| Squash merge | Combine PR commits into one |
| Staging | Prod-like pre-release environment |
| Toil | Manual, repetitive, automatable ops work |
| Trunk-based development | Everyone merges small changes to main daily |
| Twelve-Factor App | 12 principles for cloud-native apps (config in env, stateless, etc.) |
| Value stream | All steps from idea to customer value |
| Webhook | HTTP callback triggered by an event |
| YAML engineering | Pejorative: complexity living in config files |

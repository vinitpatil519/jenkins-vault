# Shared library template (`platform-lib`)

Copy this folder into its own Git repo, tag `v2.0.0`, register it as a Global Trusted Pipeline Library (see [01-concepts/07-shared-libraries.md](../../01-concepts/07-shared-libraries.md)).

```text
vars/goldenPipeline.groovy   whole pipeline as one step (+ .txt help)
vars/buildImage.groovy       BuildKit rootless build + push + Trivy gate
vars/securityScan.groovy     gitleaks + Trivy fs/config + Sonar + quality gate
vars/gitopsPromote.groovy    bump image tag in GitOps repo
vars/notifySlack.groovy      notify on failure / recovery only
src/org/acme/ci/Semver.groovy   Serializable helper class
resources/org/acme/ci/pod-*.yaml   agent pod templates per language
```

App repo `Jenkinsfile`:

```groovy
@Library('platform-lib@v2') _
goldenPipeline(language: 'java', image: 'payments-api', deploy: true)
```

Replace `registry.example.com`, `acme`, credential IDs (`gitops-deploy-key`, `sonar`) with yours.

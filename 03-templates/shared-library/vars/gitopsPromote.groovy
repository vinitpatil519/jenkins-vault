// gitopsPromote(app: 'api', tag: 'abc1234', env: 'staging') — bump tag in GitOps repo; Argo CD syncs.
def call(Map args) {
  def repo = args.get('repo', 'git@github.com:acme/gitops.git')
  withEnv(["APP=${args.app}", "TAG=${args.tag}", "TARGET_ENV=${args.env}", "GITOPS_REPO=${repo}"]) {
    sshagent(credentials: ['gitops-deploy-key']) {
      sh '''
        rm -rf .gitops && git clone --depth 1 "$GITOPS_REPO" .gitops && cd .gitops
        git config user.name jenkins-bot && git config user.email jenkins-bot@example.com
        yq -i ".image.tag = \\"$TAG\\"" "envs/$TARGET_ENV/$APP/values.yaml"
        git diff --quiet && { echo "no change"; exit 0; }
        git commit -am "$APP: $TAG -> $TARGET_ENV ($BUILD_URL)"
        git push origin HEAD:main
      '''
    }
  }
}

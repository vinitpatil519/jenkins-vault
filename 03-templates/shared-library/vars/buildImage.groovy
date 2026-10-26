// buildImage(name: 'api', tag: 'abc1234') — rootless BuildKit build + push + Trivy gate.
// Requires a `buildkit` container in the pod template and registry auth in ~/.docker/config.json.
def call(Map args) {
  def registry = args.get('registry', env.REGISTRY ?: 'registry.example.com')
  def ref = "${registry}/${args.name}:${args.tag}"

  container('buildkit') {
    sh """
      buildctl-daemonless.sh build \
        --frontend dockerfile.v0 --local context=${args.get('context', '.')} --local dockerfile=${args.get('context', '.')} \
        --output type=image,name=${ref},push=true \
        --export-cache type=registry,ref=${registry}/${args.name}:buildcache,mode=max \
        --import-cache type=registry,ref=${registry}/${args.name}:buildcache
    """
  }
  container('build') {
    sh "trivy image --no-progress --severity CRITICAL --ignore-unfixed --exit-code 1 ${ref}"
  }
  return ref
}

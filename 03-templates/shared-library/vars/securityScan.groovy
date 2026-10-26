// securityScan(sonar: true) — secrets, SCA, IaC in parallel; optional SonarQube + quality gate.
def call(Map args = [:]) {
  def checks = [
    'secrets': { sh 'gitleaks detect --source . --no-banner --redact' },
    'sca'    : { sh 'trivy fs --no-progress --scanners vuln --severity CRITICAL --ignore-unfixed --exit-code 1 .' },
    'iac'    : { sh 'trivy config --severity HIGH,CRITICAL --exit-code 1 .' }
  ]
  if (args.get('sonar', true)) {
    checks['sast'] = { withSonarQubeEnv('sonar') { sh 'sonar-scanner' } }
  }
  parallel checks

  if (args.get('sonar', true)) {
    timeout(time: 10, unit: 'MINUTES') { waitForQualityGate abortPipeline: true }
  }
}

// Go: vet, staticcheck, race tests, static binary
pipeline {
  agent { docker { image 'golang:1.23'; args '-v go-cache:/root/.cache -u root' } }
  options { timeout(time: 20, unit: 'MINUTES'); timestamps() }
  environment {
    GOFLAGS = '-mod=readonly'
    CGO_ENABLED = '0'
  }
  stages {
    stage('Vet')  { steps { sh 'go vet ./...' } }
    stage('Test') {
      steps {
        sh '''
          go install gotest.tools/gotestsum@latest
          CGO_ENABLED=1 gotestsum --junitfile reports/junit.xml -- -race -coverprofile=coverage.out ./...
        '''
      }
    }
    stage('Build') {
      steps { sh 'go build -trimpath -ldflags="-s -w -X main.version=${GIT_COMMIT}" -o bin/app ./cmd/app' }
    }
  }
  post {
    always  { junit allowEmptyResults: true, testResults: 'reports/junit.xml' }
    success { archiveArtifacts artifacts: 'bin/app', fingerprint: true }
    cleanup { cleanWs() }
  }
}

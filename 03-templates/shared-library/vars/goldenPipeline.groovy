// Golden-path pipeline. App repos call:
//   @Library('platform-lib@v2') _
//   goldenPipeline(language: 'java', image: 'payments-api', deploy: true)
def call(Map cfg = [:]) {
  def language = cfg.get('language', 'java')
  def imageName = cfg.get('image', env.JOB_NAME.tokenize('/')[-2])
  def deploy = cfg.get('deploy', false)
  def timeoutMin = cfg.get('timeoutMinutes', 45)

  pipeline {
    agent {
      kubernetes {
        yaml libraryResource("org/acme/ci/pod-${language}.yaml")
        defaultContainer 'build'
      }
    }
    options {
      timeout(time: timeoutMin, unit: 'MINUTES')
      disableConcurrentBuilds(abortPrevious: true)
      buildDiscarder(logRotator(numToKeepStr: '30'))
      timestamps()
    }
    environment {
      TAG = "${env.GIT_COMMIT.take(7)}"
    }
    stages {
      stage('Build & Test') {
        steps { sh 'make ci' }      // contract: every repo exposes `make ci` (build + unit tests)
        post { always { junit allowEmptyResults: true, testResults: '**/reports/**/*.xml, **/surefire-reports/*.xml' } }
      }
      stage('Security') {
        steps { securityScan(sonar: cfg.get('sonar', true)) }
      }
      stage('Image') {
        when { anyOf { branch 'main'; buildingTag() } }
        steps { buildImage(name: imageName, tag: env.TAG) }
      }
      stage('Promote to staging') {
        when { allOf { branch 'main'; expression { deploy } } }
        steps { gitopsPromote(app: imageName, tag: env.TAG, env: 'staging') }
      }
    }
    post {
      always { notifySlack(currentBuild.currentResult) }
      cleanup { cleanWs() }
    }
  }
}

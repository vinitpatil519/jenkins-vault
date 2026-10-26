// Job DSL run by JCasC at boot: creates demo jobs so the lab is useful immediately.

folder('lab') {
  description('Demo jobs created by Job DSL (03-templates/docker/casc/seed.groovy)')
}

pipelineJob('lab/hello-pipeline') {
  description('Declarative pipeline on the SSH agent: parallel stages, post, artifacts')
  definition {
    cps {
      sandbox(true)
      script('''
pipeline {
  agent { label 'lab' }
  options { timeout(time: 10, unit: 'MINUTES'); buildDiscarder(logRotator(numToKeepStr: '10')) }
  parameters { choice(name: 'ENV', choices: ['dev', 'staging'], description: 'Fake target') }
  stages {
    stage('Info') { steps { sh 'hostname; java -version 2>&1 | head -1; env | sort | head -20' } }
    stage('Checks') {
      parallel {
        stage('Unit') { steps { sh 'sleep 2; echo unit ok' } }
        stage('Lint') { steps { sh 'sleep 1; echo lint ok' } }
      }
    }
    stage('Package') {
      steps {
        sh 'mkdir -p out && echo "build ${BUILD_NUMBER} for ${ENV}" > out/app.txt'
        archiveArtifacts artifacts: 'out/*.txt', fingerprint: true
      }
    }
    stage('Approve') {
      when { expression { params.ENV == 'staging' } }
      steps { input message: "Deploy to ${params.ENV}?", ok: 'Go' }
    }
  }
  post {
    success { echo 'green' }
    failure { echo 'red' }
    cleanup { cleanWs() }
  }
}
'''.stripIndent())
    }
  }
}

pipelineJob('lab/nightly-cron') {
  description('Cron trigger with H hash')
  properties { pipelineTriggers { triggers { cron { spec('H 2 * * *') } } } }
  definition {
    cps {
      sandbox(true)
      script("pipeline { agent { label 'lab' }; stages { stage('Nightly') { steps { sh 'date' } } } }")
    }
  }
}

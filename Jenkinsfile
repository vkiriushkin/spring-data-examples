pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'docker build -t demo-build:latest -f Docker.build .'
        sh 'docker run --rm demo-build:latest'
      }
    }
  }
}
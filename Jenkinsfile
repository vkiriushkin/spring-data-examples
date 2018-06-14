pipeline {
  agent {
    docker {
      image '3.5.3-jdk-8-alpine'
    }

  }
  stages {
    stage('build') {
      steps {
        sh 'mvn --version'
      }
    }
  }
}
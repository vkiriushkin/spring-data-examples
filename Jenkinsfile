pipeline {
  agent {
    dockerfile {
      filename 'Docker.build'
    }

  }
  stages {
    stage('build') {
      steps {
        sh 'printenv'
      }
    }
  }
}
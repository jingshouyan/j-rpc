#!/usr/bin/env groovy

pipeline {
  agent {
    docker {
      image 'jingshouyan/maven:thrift-0.14.1'
      args '-v /root/.m2:/root/.m2'
    }

  }
  stages {
    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }

  }
}
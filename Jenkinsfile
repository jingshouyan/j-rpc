#!/usr/bin/env groovy

pipeline {
  agent {
    docker {
      image 'jingshouyan/maven:thrift-0.12.0'
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
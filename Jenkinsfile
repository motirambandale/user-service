pipeline {

    agent any

    tools {
        maven 'Maven-3'
        jdk 'JDK17'
    }

    environment {
        IMAGE_NAME = "user-service"
    }

    stages {

        stage('Source Code Checkout from GitHub') {

            steps {
                checkout scm
            }
        }

        stage('Extract Version from Git Tag') {

            steps {

                script {

                    env.VERSION = sh(
                        script: "git describe --tags --abbrev=0 || echo v0.0.${BUILD_NUMBER}",
                        returnStdout: true
                    ).trim()

                    env.IMAGE_TAG = env.VERSION

                    echo "Using Version: ${env.VERSION}"
                }
            }
        }

        stage('Pipeline Initialization') {

            steps {

                echo "Pipeline Started Successfully"
                echo "Application Version: ${env.VERSION}"
            }
        }

        stage('Maven Compile - Build Application') {

            steps {

                sh 'mvn clean compile'
                
            }
        }
        
          stage('Run JUnit Test Cases') {
            steps {
                   sh 'mvn clean test -Drevision=v0.0.11'
            }
        }
    }

    post {

        success {

            echo "Pipeline Completed Successfully"
        }

        failure {

            echo "Pipeline Execution Failed"
        }

        always {

            cleanWs()
        }
    }
}
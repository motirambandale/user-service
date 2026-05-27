pipeline {

    agent any

    tools {
        maven 'Maven-3'
        jdk 'JDK17'
    }

    environment {

        IMAGE_NAME = "user-service"

        // SonarQube
        SONARQUBE_ENV = "SonarQubeServer"

        // Nexus Maven Repository
        NEXUS_RELEASE_URL =
            "http://host.docker.internal:8081/repository/maven-releases/"

        // Nexus Docker Registry
        NEXUS_DOCKER_URL = "host.docker.internal:8082"
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
                        script: "git describe --tags --abbrev=0 2>/dev/null || echo v0.0.${BUILD_NUMBER}",
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

        stage('Maven Build') {

            steps {

                sh "mvn clean compile"
            }
        }

        stage('Run JUnit Test Cases') {

            steps {

                sh "mvn test -Drevision=${env.VERSION}"
            }
        }

        stage('SonarQube Analysis') {

            steps {

                withSonarQubeEnv("${SONARQUBE_ENV}") {

                    sh """
                        mvn sonar:sonar \
                        -Drevision=${env.VERSION} \
                        -Dsonar.projectKey=user-service \
                        -Dsonar.projectName=user-service
                    """
                }
            }
        }

        stage('Quality Gate') {

            steps {

                timeout(time: 2, unit: 'MINUTES') {

                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build & Package Artifact') {

            steps {

                sh """
                    mvn clean package \
                    -DskipTests \
                    -Drevision=${env.VERSION}
                """
            }
        }

        stage('Deploy Artifact to Nexus (Maven)') {

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'nexus-pass',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )
                ]) {

                    sh """
                        mvn clean deploy \
                        -DskipTests \
                        -Drevision=${env.VERSION} \
                        -DaltDeploymentRepository=nexus::default::${NEXUS_RELEASE_URL} \
                        --settings /var/jenkins_home/.m2/settings.xml
                    """
                }
            }
        }

        stage('Build Docker Image') {

            steps {

                script {

                    env.DOCKER_IMAGE =
                        "${IMAGE_NAME}:${env.VERSION}"

                    sh """
                        docker build \
                        -t ${env.DOCKER_IMAGE} .
                    """
                }
            }
        }

        stage('Docker Login to Nexus') {

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'nexus-pass',
                        usernameVariable: 'NEXUS_USER',
                        passwordVariable: 'NEXUS_PASS'
                    )
                ]) {

                    sh '''
                        echo $NEXUS_PASS | docker login host.docker.internal:8082 \
                        -u $NEXUS_USER \
                        --password-stdin
                    '''
                }
            }
        }

        stage('Push Docker Image to Nexus') {

            steps {

                script {

                    env.FULL_IMAGE =
                        "${NEXUS_DOCKER_URL}/${IMAGE_NAME}:${env.VERSION}"

                    echo "Pushing Docker Image: ${env.FULL_IMAGE}"

                    sh """
                        docker tag ${env.DOCKER_IMAGE} ${env.FULL_IMAGE}

                        docker push ${env.FULL_IMAGE}
                    """
                }
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
pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk 'JDK21'
    }

    environment {
        DOCKER_HUB_USER = 'sugandhasawhney'
        IMAGE_NAME = 'job-alert-portal'
        IMAGE_TAG = "${BUILD_NUMBER}"
        FULL_IMAGE = "${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"
        K8S_NAMESPACE = 'default'
    }

    stages {

        stage('1. Checkout') {
            steps {
                echo '=== Pulling source code from Git ==='
                checkout scm
            }
        }

        stage('2. Build') {
            steps {
                echo '=== Compiling with Maven ==='
                bat 'mvn clean compile -B'
            }
        }

        stage('3. Test') {
            steps {
                echo '=== Running Tests ==='
                bat 'mvn test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('4. Package') {
            steps {
                echo '=== Packaging JAR ==='
                bat 'mvn package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('5. Docker Build') {
            steps {
                echo "=== Building Docker Image ==="
                bat "docker build -t ${FULL_IMAGE} ."
                bat "docker tag ${FULL_IMAGE} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }

        stage('6. Docker Push') {
            steps {
                echo '=== Pushing to Docker Hub ==='
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    bat "docker push ${FULL_IMAGE}"
                    bat "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }

        stage('7. Kubernetes Deploy') {
            steps {
                echo '=== Deploying to Kubernetes ==='
                bat '''
                kubectl apply -f k8s/deployment.yaml -n default
                kubectl apply -f k8s/service.yaml -n default
                '''
            }
        }

        stage('8. Smoke Test') {
            steps {
                echo '=== Smoke Test ==='
                bat '''
                timeout /t 10
                kubectl get pods -n default
                '''
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline SUCCESS — ${FULL_IMAGE}"
        }
        failure {
            echo "❌ Pipeline FAILED"
        }
        always {
            bat "docker rmi ${FULL_IMAGE} || exit 0"
            bat "docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest || exit 0"
        }
    }
}
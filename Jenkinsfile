pipeline {
    agent any

    // ── Tool versions configured in Jenkins Global Tool Configuration ──
    tools {
        maven 'Maven3'
        jdk   'JDK21'
    }

    environment {
        // Change these to match your Docker Hub username and image name
        DOCKER_HUB_USER  = 'your-dockerhub-username'
        IMAGE_NAME       = 'job-alert-portal'
        IMAGE_TAG        = "${BUILD_NUMBER}"           // e.g. :42
        FULL_IMAGE       = "${DOCKER_HUB_USER}/${IMAGE_NAME}:${IMAGE_TAG}"

        // Kubernetes namespace
        K8S_NAMESPACE    = 'default'
    }

    stages {

        // ──────────────────────────────────────────────
        // Stage 1: Pull latest code from Git
        // ──────────────────────────────────────────────
        stage('1. Checkout') {
            steps {
                echo '=== Pulling source code from Git ==='
                // If this Jenkinsfile lives in the repo, SCM checkout is automatic.
                // For a remote repo, use:
                // git url: 'https://github.com/your-username/job-alert-portal.git', branch: 'main'
                checkout scm
            }
        }

        // ──────────────────────────────────────────────
        // Stage 2: Compile with Maven
        // ──────────────────────────────────────────────
        stage('2. Build') {
            steps {
                echo '=== Compiling the application with Maven ==='
                bat 'mvn clean compile -B'
            }
        }

        // ──────────────────────────────────────────────
        // Stage 3: Run unit tests
        // ──────────────────────────────────────────────
        stage('3. Test') {
            steps {
                echo '=== Running unit tests ==='
                bat 'mvn test -B'
            }
            post {
                always {
                    // Publish JUnit test results in Jenkins UI
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        // ──────────────────────────────────────────────
        // Stage 4: Package the JAR
        // ──────────────────────────────────────────────
        stage('4. Package') {
            steps {
                echo '=== Packaging application JAR ==='
                bat 'mvn package -DskipTests -B'
                // Archive the JAR as a Jenkins build artifact
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        // ──────────────────────────────────────────────
        // Stage 5: Build Docker image
        // ──────────────────────────────────────────────
        stage('5. Docker Build') {
            steps {
                echo "=== Building Docker image: ${FULL_IMAGE} ==="
                bat "docker build -t ${FULL_IMAGE} ."
                bat "docker tag ${FULL_IMAGE} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }

        // ──────────────────────────────────────────────
        // Stage 6: Push image to Docker Hub
        //   Add a 'dockerhub-credentials' secret in
        //   Jenkins → Manage Credentials
        // ──────────────────────────────────────────────
        stage('6. Docker Push') {
            steps {
                echo '=== Pushing image to Docker Hub ==='
                withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS')]) {
                    bat "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                    bat "docker push ${FULL_IMAGE}"
                    bat "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
                }
            }
        }

        // ──────────────────────────────────────────────
        // Stage 7: Deploy to Kubernetes
        //   Requires kubectl configured on Jenkins agent
        // ──────────────────────────────────────────────
        stage('7. Kubernetes Deploy') {
            steps {
                echo '=== Deploying to Kubernetes cluster ==='
                // Substitute the image tag in the deployment YAML
                bat """
                    bat """
                    powershell -Command "(Get-Content k8s/deployment.yaml) -replace 'IMAGE_PLACEHOLDER','${FULL_IMAGE}' | Set-Content k8s/deployment.yaml"
                    kubectl apply -f k8s/deployment.yaml -n ${K8S_NAMESPACE}
                    kubectl apply -f k8s/service.yaml -n ${K8S_NAMESPACE}
                    kubectl rollout status deployment/job-alert-portal -n ${K8S_NAMESPACE}
                    """
                    kubectl apply -f k8s/deployment.yaml -n ${K8S_NAMESPACE}
                    kubectl apply -f k8s/service.yaml    -n ${K8S_NAMESPACE}
                    kubectl rollout status deployment/job-alert-portal -n ${K8S_NAMESPACE}
                """
            }
        }

        // ──────────────────────────────────────────────
        // Stage 8: Smoke test the live deployment
        // ──────────────────────────────────────────────
        stage('8. Smoke Test') {
            steps {
                echo '=== Running smoke test against deployed app ==='
                bat """
                timeout /t 10
                kubectl get pods -n ${K8S_NAMESPACE} -l app=job-alert-portal
                """
            }
        }
    }

    // ──────────────────────────────────────────────────
    // Post-build notifications
    // ──────────────────────────────────────────────────
    post {
        success {
            echo "✅ Pipeline SUCCESS — Image: ${FULL_IMAGE} is live in Kubernetes!"
        }
        failure {
            echo "❌ Pipeline FAILED — check the logs above."
        }
        always {
            // Clean up local Docker images to save disk space
            bat "docker rmi ${FULL_IMAGE} || exit 0"
            bat "docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest || exit 0"
            bat "docker rmi ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest || true"
        }
    }
}

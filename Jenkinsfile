pipeline {
    agent any

    environment {
        APP_NAME = "springboot-api"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                echo "Stopping previous app if running..."
                pkill -f ${APP_NAME} || true

                echo "Starting new version..."
                JAR=$(ls build/libs/*.jar | head -n 1)

                nohup java -jar $JAR > app.log 2>&1 &
                '''
            }
        }

    }

    post {

        success {
            echo '✅ Build and deployment successful!'
        }

        failure {
            echo '❌ Pipeline failed.'
        }

        always {
            echo 'Pipeline finished.'
        }

    }
}
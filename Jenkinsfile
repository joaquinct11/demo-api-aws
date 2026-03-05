pipeline {
    agent any

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

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
                sh '''
                echo "Preparing environment..."
                chmod +x gradlew
                '''
            }
        }

        stage('Build') {
            steps {
                sh '''
                echo "Building application..."
                ./gradlew clean build -x test --no-daemon --max-workers=1
                '''
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
                echo "Stopping previous application..."
                pkill -f ${APP_NAME} || true

                echo "Starting new version..."
                JAR=$(ls build/libs/*.jar | head -n 1)

                nohup java -jar $JAR > app.log 2>&1 &

                echo "Application started"
                '''
            }
        }

    }

    post {
        success {
            echo "✅ Deployment successful"
        }
        failure {
            echo "❌ Pipeline failed"
        }
        always {
            echo "Pipeline finished"
        }
    }
}
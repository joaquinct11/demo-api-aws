pipeline {
    agent any

    environment {
        APP_NAME = "springboot-api"
        JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Prepare') {
            steps { sh 'chmod +x gradlew' }
        }

        stage('Check Java') {
            steps {
                sh 'java -version'
                sh 'javac -version'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test --no-daemon --max-workers=1'
            }
        }

        stage('Archive Artifact') {
            steps { archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true }
        }

        stage('Deploy') {
            steps {
                sh '''
                pkill -f ${APP_NAME} || true
                JAR=$(ls build/libs/*.jar | head -n 1)
                nohup java -jar $JAR > app.log 2>&1 &
                '''
            }
        }

    }

    post {
        success { echo '✅ Build and deployment successful!' }
        failure { echo '❌ Pipeline failed.' }
        always { echo 'Pipeline finished.' }
    }
}
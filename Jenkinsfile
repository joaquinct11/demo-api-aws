pipeline {

    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
        EC2_HOST = "18.XXX.XXX.XXX"
        EC2_USER = "deploy"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                        url: 'https://github.com/joaquinct11/demo-api-aws.git'
            }
        }

        stage('Prepare Gradle') {
            steps {
                sh 'chmod +x gradlew'
            }
        }

        stage('Check Java Version') {
            steps {
                sh 'java -version'
            }
        }

        stage('Build Project') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Verify Artifact') {
            steps {
                sh 'ls -l build/libs'
            }
        }

        stage('Copy JAR to EC2') {
            steps {
                sh """
                scp -o StrictHostKeyChecking=no \
                build/libs/*.jar \
                ${EC2_USER}@${EC2_HOST}:/home/deploy/app/app.jar
                """
            }
        }

        stage('Deploy in EC2') {
            steps {
                sh """
                ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} '
                
                echo "Stopping old app..."
                pkill -f app.jar || true
                
                echo "Starting new app..."
                nohup java -jar /home/deploy/app/app.jar > app.log 2>&1 &
                
                echo "Application deployed!"
                
                '
                """
            }
        }

    }

    post {

        success {
            echo '✅ CI/CD SUCCESS - APP DEPLOYED'
        }

        failure {
            echo '❌ PIPELINE FAILED'
        }

    }

}
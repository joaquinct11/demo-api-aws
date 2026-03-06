pipeline {

    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"

        EC2_HOST = "44.210.133.71"
        EC2_USER = "ubuntu"
        SSH_KEY = "/var/jenkins_home/spring-key.pem"

        JAR_FILE = "build/libs/demo-api-0.0.1-SNAPSHOT.jar"
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
                scp -i ${SSH_KEY} -o StrictHostKeyChecking=no \
                ${JAR_FILE} \
                ${EC2_USER}@${EC2_HOST}:/home/ubuntu/app/app.jar
                """
            }
        }

        stage('Deploy in EC2') {
            steps {
                sh """
                ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'EOF'

                echo "Stopping old application..."
                pkill -f app.jar || true

                echo "Starting new application..."
                nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &

                echo "Deployment finished"

                EOF
                """
            }
        }

    }

    post {

        success {
            echo '✅ CI/CD SUCCESS - Application deployed'
        }

        failure {
            echo '❌ CI/CD FAILED'
        }

    }

}
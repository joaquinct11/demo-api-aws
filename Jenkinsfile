pipeline {

    agent any

    environment {
        JAVA_HOME = "/usr/lib/jvm/java-21-openjdk-amd64"
        PATH = "${JAVA_HOME}/bin:${env.PATH}"

        EC2_HOST = "44.210.133.71"
        EC2_USER = "ubuntu"
        SSH_KEY = "/var/jenkins_home/spring-key.pem"
    }

    stages {

        stage('Checkout') {
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

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Deploy to EC2') {
            steps {

                sh """
                scp -i ${SSH_KEY} -o StrictHostKeyChecking=no \
                build/libs/demo-api-0.0.1-SNAPSHOT.jar \
                ${EC2_USER}@${EC2_HOST}:/home/ubuntu/app/app.jar
                """

                sh """
                ssh -i ${SSH_KEY} -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} "
                
                pkill -f app.jar || true
                
                nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                
                "
                """
            }
        }
    }

    post {
        success {
            echo '✅ CI/CD SUCCESS - Application deployed'
        }
        failure {
            echo '❌ PIPELINE FAILED'
        }
    }
}
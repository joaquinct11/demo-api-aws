pipeline {
    agent any

    environment {
        EC2_IP = "44.210.133.71"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/joaquinct11/demo-api-aws.git'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Prepare Jar') {
            steps {
                script {
                    def JAR_FILE = sh(
                            script: "ls build/libs/*-SNAPSHOT.jar | grep -v plain | head -n 1",
                            returnStdout: true
                    ).trim()
                    echo "Jar to deploy: ${JAR_FILE}"
                    env.JAR_FILE = JAR_FILE
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['ec2-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} 'mkdir -p /home/ubuntu/app/'
                        scp -o StrictHostKeyChecking=no ${env.JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                            pkill -f app.jar || true
                            nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                        '
                    """
                }
            }
        }
    }
}
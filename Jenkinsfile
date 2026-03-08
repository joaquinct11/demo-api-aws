pipeline {
    agent any
    environment {
        EC2_IP = "98.81.24.205"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/joaquinct11/demo-api-aws'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
//                sh './gradlew clean build'
                sh './gradlew clean bootJar'
            }
        }

        stage('Prepare Jar') {
            steps {
                script {
                    // Tomamos el .jar que no tiene "plain" en el nombre
                    env.JAR_FILE = "build/libs/demo-0.0.1-SNAPSHOT.jar"
                    echo "Jar to deploy: ${env.JAR_FILE}"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['ssh-agent']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "mkdir -p /home/ubuntu/app"
        
                        scp -o StrictHostKeyChecking=no ${JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar
        
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "
                            pkill -f 'java -jar app.jar' || true
                            sleep 2
                            cd /home/ubuntu/app
                            nohup java -jar app.jar > app.log 2>&1 &
                        "
                    """
                }
            }
        }
    }
}
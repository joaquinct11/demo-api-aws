pipeline {
    agent any

    environment {
        EC2_IP = "98.81.24.205"
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
                    def jarFiles = sh(script: "ls build/libs/*.jar | grep -v plain", returnStdout: true).trim()
                    env.JAR_FILE = jarFiles.split("\n")[0]
                    echo "Jar to deploy: ${env.JAR_FILE}"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['ssh-agent']) {

                    // 1️⃣ Preparar carpeta y matar app vieja
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                            mkdir -p /home/ubuntu/app &&
                            pkill -f app.jar || true &&
                            rm -f /home/ubuntu/app/app.jar /home/ubuntu/app/app.log
                        '
                    """

                    // 2️⃣ Subir JAR
                    sh """
                        scp -o StrictHostKeyChecking=no ${JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar
                    """

                    // 3️⃣ Levantar la app
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                            cd /home/ubuntu/app &&
                            nohup java -jar app.jar > app.log 2>&1 &
                        '
                    """
                }
            }
        }
    }
}
pipeline {
    agent any
    environment {
        EC2_IP = "98.81.24.205"
        SSH_CREDENTIALS = "ssh-agent" // tu ID de credenciales en Jenkins
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
                sh './gradlew clean bootJar'
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
                    sh """
                echo "Deploying ${env.JAR_FILE} to EC2 ${EC2_IP}"

                # Crear carpeta en EC2
                ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "mkdir -p /home/ubuntu/app"

                # Copiar jar
                scp -o StrictHostKeyChecking=no ${env.JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar

                # Detener app existente y arrancar en background, ignorando código de salida
                ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                    pkill -f "java -jar app.jar" || true
                    sleep 2
                    cd /home/ubuntu/app
                    setsid java -jar app.jar > app.log 2>&1 < /dev/null & || true
                '
            """
                }
            }
        }
    }
}
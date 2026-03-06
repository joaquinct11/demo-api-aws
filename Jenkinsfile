pipeline {
    agent any

    environment {
        EC2_IP = "44.210.133.71"
        JAR_FILE = "" // se asignará dinámicamente
        PEM_FILE = "/var/jenkins_home/.ssh/ec2-key.pem"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main',
                        url: 'https://github.com/joaquinct11/demo-api-aws.git'
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
                    // Tomar el primer jar que no sea "plain"
                    JAR_FILE = sh(
                            script: "ls build/libs/*-SNAPSHOT.jar | grep -v plain | head -n 1",
                            returnStdout: true
                    ).trim()
                    echo "Jar to deploy: ${JAR_FILE}"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sh """
                    # Crear carpeta si no existe
                    ssh -i ${PEM_FILE} -o StrictHostKeyChecking=no ubuntu@${EC2_IP} 'mkdir -p /home/ubuntu/app/'

                    # Copiar el jar a EC2
                    scp -i ${PEM_FILE} -o StrictHostKeyChecking=no ${JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar

                    # Detener la app antigua y ejecutar la nueva
                    ssh -i ${PEM_FILE} -o StrictHostKeyChecking=no ubuntu@${EC2_IP} \"
                        pkill -f app.jar || true
                        nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                    \"
                """
            }
        }
    }
}
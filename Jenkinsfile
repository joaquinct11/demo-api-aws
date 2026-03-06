pipeline {
    agent any

    environment {
        EC2_IP = "18.232.181.253"
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
                    def jars = sh(script: "ls build/libs/*.jar | grep -v plain", returnStdout: true).trim().split("\n")
                    def jarToDeploy = jars[0]
                    echo "Jar to deploy: ${jarToDeploy}"
                    env.JAR_FILE = jarToDeploy
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                sshagent(['ec2-key']) {
                    // Crear carpeta en EC2
                    sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} 'mkdir -p /home/ubuntu/app/'"

                    // Copiar el jar
                    sh "scp -o StrictHostKeyChecking=no ${env.JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar"

                    // Detener la app anterior y ejecutar la nueva
                    sh """
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
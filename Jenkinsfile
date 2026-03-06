pipeline {
    agent any
    environment {
        EC2_IP = "18.232.181.253"
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
                    // Tomamos el .jar que no tiene "plain" en el nombre
                    def jarFiles = sh(script: "ls build/libs/*.jar | grep -v plain", returnStdout: true).trim()
                    env.JAR_FILE = jarFiles.split("\n")[0]
                    echo "Jar to deploy: ${env.JAR_FILE}"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                // Aquí usamos la credencial de Jenkins
                sshagent(['ec2-key']) {
                    sh """
                        echo 'Creando carpeta en EC2...'
                        ssh -o StrictHostKeyChecking=no ubuntu@$EC2_IP "mkdir -p /home/ubuntu/app/"

                        echo 'Copiando jar al EC2...'
                        scp -o StrictHostKeyChecking=no ${env.JAR_FILE} ubuntu@$EC2_IP:/home/ubuntu/app/app.jar

                        echo 'Reiniciando la app...'
                        ssh -o StrictHostKeyChecking=no ubuntu@$EC2_IP "
                            pkill -f app.jar || true
                            nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                        "
                    """
                }
            }
        }
    }
}
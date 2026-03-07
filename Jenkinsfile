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
                sshagent(['ssh-agent']) { // <- ID de la credencial en Jenkins
                    sh '''
                echo "Creando carpeta en EC2..."
                ssh -o StrictHostKeyChecking=no ubuntu@98.81.24.205 "mkdir -p /home/ubuntu/app/"

                echo "Subiendo el JAR..."
                scp -o StrictHostKeyChecking=no build/libs/demo-api-0.0.1-SNAPSHOT.jar ubuntu@98.81.24.205:/home/ubuntu/app/app.jar

                echo "Reiniciando la app..."
                ssh -o StrictHostKeyChecking=no ubuntu@98.81.24.205 "
                    # Matar cualquier instancia anterior
                    pkill -f app.jar || true

                    # Arrancar la app en segundo plano y guardar logs
                    nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &

                    # Guardar PID real del proceso para futuros reinicios
                    echo \\$! > /home/ubuntu/app/app.pid
                "
            '''
                }
            }
        }
    }
}
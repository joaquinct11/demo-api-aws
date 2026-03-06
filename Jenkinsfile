pipeline {
    agent any

    environment {
        EC2_IP = "44.210.133.71"
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

        stage('Deploy to EC2') {
            steps {
                sshagent(['ec2-key']) {
                    sh '''
                    # Obtener el primer .jar generado
                    JAR_FILE=$(ls build/libs/*.jar | head -n 1)
                    echo "Deploying $JAR_FILE to $EC2_IP"

                    # Copiar el jar a EC2
                    scp -o StrictHostKeyChecking=no $JAR_FILE ubuntu@$EC2_IP:/home/ubuntu/app/app.jar

                    # Detener la app anterior y ejecutar la nueva
                    ssh -o StrictHostKeyChecking=no ubuntu@$EC2_IP "
                        pkill -f app.jar || true
                        nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                    "
                    '''
                }
            }
        }
    }
}
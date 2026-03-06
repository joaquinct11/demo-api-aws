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
                    sh """
                        # Copiar jar
                        scp -o StrictHostKeyChecking=no build/libs/demo-api-0.0.1-SNAPSHOT.jar ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar
                
                        # Ejecutar jar en background
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                            pkill -f app.jar || true
                            nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                            exit 0
                        '
                    """
                }
            }
        }
    }
}
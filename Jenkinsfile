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
//                sh './gradlew clean bootJar'
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
                sshagent(['ssh-agent']) {
                    sh """
                        echo "Creating app directory..."
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "mkdir -p /home/ubuntu/app"
        
                        echo "Copying new JAR to EC2..."
                        scp -o StrictHostKeyChecking=no ${JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar
        
                        echo "Restarting application..."
                        ssh -f -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "cd /home/ubuntu/app && nohup java -jar app.jar > app.log 2>&1 &"
        
                        echo "Deployment completed"
                    """
                }
            }
        }
    }
}
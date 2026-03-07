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
                sshagent(['ssh-agent']) {
                    sh """
                        echo "Deploying app..."
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} << 'ENDSSH'
                            mkdir -p /home/ubuntu/app
                            pkill -f app.jar || true
                            cd /home/ubuntu/app
                            nohup java -jar app.jar > app.log 2>&1 &
                            echo "Deployment done"
                            exit 0
                        ENDSSH
                    """
                }
            }
        }
    }
}
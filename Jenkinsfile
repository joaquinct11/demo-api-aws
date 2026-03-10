pipeline {
    agent any
    environment {
        EC2_IP = "44.197.201.123"
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

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh './gradlew sonar'
                }
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
                        set +e
                        
                        echo "Creating app directory..."
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "mkdir -p /home/ubuntu/app"
        
                        echo "Copying new JAR to EC2..."
                        scp -o StrictHostKeyChecking=no ${JAR_FILE} ubuntu@${EC2_IP}:/home/ubuntu/app/app.jar

                        echo "Killing old application..."
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "pkill -f app.jar"
        
                        echo "Restarting application..."
                        ssh -f -o StrictHostKeyChecking=no ubuntu@${EC2_IP} "cd /home/ubuntu/app && nohup java -jar app.jar > app.log 2>&1 &"
        
                        echo "Deployment completed"
                    """
                }
            }
        }
    }
}
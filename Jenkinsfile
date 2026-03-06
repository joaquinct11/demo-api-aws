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
                    scp -o StrictHostKeyChecking=no \
                    build/libs/demo-api-0.0.1-SNAPSHOT.jar \
                    ubuntu@$EC2_IP:/home/ubuntu/app/app.jar
                    '''

                    sh '''
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
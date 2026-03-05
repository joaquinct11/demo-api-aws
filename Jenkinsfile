pipeline {
    agent any

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main',
                url: 'https://github.com/joaquinct11/demo-api-aws.git'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }

        stage('Run Jar') {
            steps {
                sh '''
                pkill -f jar || true
                nohup java -jar build/libs/*.jar > app.log 2>&1 &
                '''
            }
        }

    }
}
pipeline {
    agent any

    environment {
        // Configura tu usuario y host de EC2
        EC2_USER = 'ubuntu'
        EC2_HOST = '98.81.24.205'
        // Ruta donde se desplegará la app
        DEPLOY_PATH = '/home/ubuntu/app'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/joaquinct11/demo-api-aws'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x ./gradlew && ./gradlew clean build'
            }
        }

        stage('Deploy') {
            steps {
                // Copiamos el artefacto al servidor EC2
                sshagent(['ssh-agent']) {
                    sh """
                        scp build/libs/*.jar ${EC2_USER}@${EC2_HOST}:${DEPLOY_PATH}/app.jar
                        ssh ${EC2_USER}@${EC2_HOST} 'pkill -f app.jar || true'
                        ssh ${EC2_USER}@${EC2_HOST} 'nohup java -jar ${DEPLOY_PATH}/app.jar > ${DEPLOY_PATH}/app.log 2>&1 &'
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deploy completado exitosamente!'
        }
        failure {
            echo 'El deploy falló 😢'
        }
    }
}
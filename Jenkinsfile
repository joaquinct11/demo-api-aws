stage('Deploy to EC2') {
    steps {
        sshagent(['ec2-key']) {
            sh '''
                echo "Creando carpeta en EC2..."
                ssh -o StrictHostKeyChecking=no ubuntu@18.232.181.253 "mkdir -p /home/ubuntu/app/"
                
                echo "Subiendo JAR..."
                scp -o StrictHostKeyChecking=no build/libs/demo-api-0.0.1-SNAPSHOT.jar ubuntu@18.232.181.253:/home/ubuntu/app/app.jar
                
                echo "Deteniendo y levantando app..."
                ssh -o StrictHostKeyChecking=no ubuntu@18.232.181.253 "
                    pkill -f app.jar || true
                    nohup java -jar /home/ubuntu/app/app.jar > /home/ubuntu/app/app.log 2>&1 &
                "
            '''
        }
    }
}
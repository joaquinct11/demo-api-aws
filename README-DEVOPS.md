# CI/CD Pipeline - Spring Boot + Jenkins + AWS EC2

## 1. Descripción

Este proyecto implementa un pipeline de **CI/CD (Continuous Integration / Continuous Deployment)** para una aplicación **Spring Boot** utilizando:

- Jenkins para automatización del pipeline
- Gradle para compilar el proyecto
- SonarQube para análisis de calidad de código
- SSH para despliegue remoto
- AWS EC2 como servidor de ejecución

El pipeline realiza automáticamente:

1. Descargar el código desde GitHub
2. Compilar la aplicación
3. Ejecutar análisis de código con SonarQube
4. Generar el archivo `.jar`
5. Conectarse al servidor EC2
6. Copiar el `.jar`
7. Detener la aplicación anterior
8. Levantar la nueva versión

---

# 2. Arquitectura del Pipeline
Developer --> push code --> GitHub Repository --> webhook / manual build --> Jenkins --> Build Gradle
--> SonarQube Analysis --> Deploy --> AWS EC2 --> Spring Boot Application Running


---

# 3. Requisitos

Antes de iniciar debes tener:

- Cuenta en AWS
- Instancia EC2 creada
- Jenkins instalado
- Java 17 instalado
- Gradle Wrapper en el proyecto
- Repositorio en GitHub
- Acceso SSH al servidor EC2

---

# 4. Crear instancia EC2

Crear una instancia en AWS con:

- OS: Ubuntu
- Tipo: t2.micro
- Abrir puertos:

| Puerto | Uso |
|------|------|
| 22 | SSH |
| 8080 | Spring Boot |
| 8081 | Jenkins |
| 9000 | SonarQube |

Descargar el **key pair (.pem)** al crear la instancia.

Ejemplo conexión:

```bash
ssh -i demo-key.pem ubuntu@IP_PUBLICA
```

# 5. Instalar Java en EC2
Conectarse al servidor:
```bash
ssh -i demo-key.pem ubuntu@IP_PUBLICA
```
Actualizar paquetes:
```bash
sudo apt update
```
Instalar Java 21:
```bash
sudo apt install openjdk-21-jdk -y
```
Verificar:
```bash
java -verion
```

# 6. Instalar Jenkins
Instalar dependencias:
```bash
sudo apt install fontconfig openjdk-21-jre -y
```
Agregar repositorio de Jenkins:
```bash
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
  https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
 
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc]" \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
```
Actualizar repositorios:
```bash
sudo apt update
```
Instalar Jenkins:
```bash
sudo apt install jenkins -y
```
Iniciar Jenkins:
```bash
sudo systemctl start jenkins
```
Verificar estado:
```bash
sudo systemctl status jenkins
```

# 7. Acceder a Jenkins
Abrir en navegador:
```bash
http://IP_PUBLICA:8080
```
Obtener contraseña inicial:
```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```
Completar instalación de plugins sugeridos.

# 8. Generar llaves SSH para Jenkins
Entrar al contenedor o servidor Jenkins.

Si Jenkins está en Docker:
```bash
docker exec -it jenkins bash
```
Generar llaves:
```bash
ssh-keygen -t rsa -b 4096
```
Presionar Enter en todas las opciones.

Se generarán:
```bash
~/.ssh/id_rsa
~/.ssh/id_rsa.pub
```
| Archivo    | Descripción   |
| ---------- | ------------- |
| id_rsa     | Clave privada |
| id_rsa.pub | Clave pública |

# 9. Agregar clave pública a EC2
Mostrar clave pública:
```bash
cat ~/.ssh/id_rsa.pub
```
Copiar el contenido.

Conectarse a EC2:
```bash
ssh -i demo-key.pem ubuntu@IP_PUBLICA
```
Crear carpeta SSH:
```bash
mkdir -p ~/.ssh
```
Editar archivo:
```bash
nano ~/.ssh/authorized_keys
```
Pegar la clave pública.

Guardar.

Asignar permisos:
```bash
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
```

# 10. Agregar clave privada a Jenkins
Mostrar clave privada:
```bash
cat ~/.ssh/id_rsa
```
Copiar todo el contenido.

Ir a Jenkins:
```bash
Manage Jenkins
Credentials
Global
Add Credentials
```
Seleccionar:
```bash
SSH Username with private key
```
Configurar:
```bash
ID: ssh-agent
Username: ubuntu
Private Key: Enter directly
```
Pegar la clave privada.

Guardar.

# 11. Crear pipeline en Jenkins
Ir a:
```bash
New Item
```
Seleccionar:
```bash
Pipeline
```
Configurar:
```bash
Pipeline script from SCM
```
Repositorio:
```bash
https://github.com/joaquinct11/demo-api-aws.git
```
Branch:
```bash
main
```
Script Path:
```bash
Jenkinsfile
```
Guardar.

# 12. Ejecutar pipeline
En Jenkins presionar:
```bash
Build Now
```
El pipeline ejecutará:
1. Checkout código
2. Build con Gradle
3. Análisis SonarQube
4. Generación del JAR
5. Deploy automático en EC2

# 13. Verificar aplicación
Entrar al servidor:
```bash
ssh ubuntu@IP_PUBLICA
```
Ver procesos Java:
```bash
ps aux | grep java
```
Ver logs:
```bash
cd /home/ubuntu/app
tail -f app.log
```
Probar API:
```bash
http://IP_PUBLICA:8080
```


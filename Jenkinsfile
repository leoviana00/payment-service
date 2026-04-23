properties([
    parameters([
        string(name: 'APP_NAME', defaultValue: '', description: 'Nome da aplicação (opcional, lê do pom.xml se vazio)'),
        string(name: 'APP_PORT', defaultValue: '', description: 'Porta da aplicação (opcional, lê do application.yaml se vazio)'),
        string(name: 'IMAGE_TAG', defaultValue: 'latest', description: 'Tag da imagem Docker'),
        string(name: 'CONTAINER_NAME', defaultValue: '', description: 'Nome do container (opcional, usa APP_NAME se vazio)')
    ])
])

node {

    def appName       = ''
    def appPort       = ''
    def imageTag      = params.IMAGE_TAG ?: 'latest'
    def containerName = ''
    def imageName     = ''
    def version       = ''

    try {

        stage('Checkout') {
            checkout scm
        }

        stage('Load Variables') {

            def pomAppName = sh(
                script: "./mvnw help:evaluate -Dexpression=project.artifactId -q -DforceStdout",
                returnStdout: true
            ).trim()

            version = sh(
                script: "./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout",
                returnStdout: true
            ).trim()

            def yamlPort = sh(
                script: "grep 'port:' src/main/resources/application.yaml | awk '{print \$2}'",
                returnStdout: true
            ).trim()

            appName = params.APP_NAME?.trim() ? params.APP_NAME.trim() : pomAppName
            appPort = params.APP_PORT?.trim() ? params.APP_PORT.trim() : yamlPort
            containerName = params.CONTAINER_NAME?.trim() ? params.CONTAINER_NAME.trim() : appName

            if (imageTag == 'latest') {
                imageName = "${appName}:latest"
            } else {
                imageName = "${appName}:${imageTag}"
            }

            echo "APP_NAME       : ${appName}"
            echo "VERSION        : ${version}"
            echo "APP_PORT       : ${appPort}"
            echo "CONTAINER_NAME : ${containerName}"
            echo "IMAGE_NAME     : ${imageName}"
        }

        stage('Build') {
            sh './mvnw clean package'
        }

        stage('Tests') {
            sh './mvnw test'
        }

        stage('Docker Build') {
            sh "docker build -t ${imageName} ."
        }

        stage('Stop Old Container') {
            sh "docker rm -f ${containerName} || true"
        }

        stage('Deploy Container') {
            sh """
                docker run -d \
                --name ${containerName} \
                -p ${appPort}:${appPort} \
                ${imageName}
            """
        }

        stage('Health Check') {
            sleep 10

            sh "curl -f http://host.docker.internal:${appPort}/actuator/health"
        }

        stage('Generate Evidence') {

            writeFile file: 'evidence.json', text: """
{
  "application": "${appName}",
  "version": "${version}",
  "image": "${imageName}",
  "container": "${containerName}",
  "port": "${appPort}",
  "buildNumber": "${env.BUILD_NUMBER}",
  "status": "SUCCESS",
  "executedBy": "Jenkins"
}
"""

            archiveArtifacts artifacts: 'evidence.json'
        }

        currentBuild.result = 'SUCCESS'

    } catch (Exception ex) {

        currentBuild.result = 'FAILURE'
        throw ex

    } finally {

        cleanWs()
    }
}
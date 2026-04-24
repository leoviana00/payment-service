properties([
    parameters([
        string(name: 'CHANGE_ID', defaultValue: '', description: 'ID da mudança aprovada'),
        string(name: 'APP_NAME', defaultValue: '', description: 'Nome da aplicação (opcional)'),
        string(name: 'APP_PORT', defaultValue: '', description: 'Porta da aplicação (opcional)'),
        string(name: 'IMAGE_TAG', defaultValue: 'latest', description: 'Tag da imagem Docker'),
        string(name: 'CONTAINER_NAME', defaultValue: '', description: 'Nome do container (opcional)')
    ])
])

node {

    def appName       = ''
    def appPort       = ''
    def imageTag      = params.IMAGE_TAG ?: 'latest'
    def containerName = ''
    def imageName     = ''
    def version       = ''
    def changeId      = params.CHANGE_ID?.trim()

    try {

        stage('Checkout') {
            checkout scm
        }

        stage('Prepare') {
            sh 'chmod +x mvnw'
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

            imageName = "${appName}:${imageTag}"

            echo "APP_NAME       : ${appName}"
            echo "VERSION        : ${version}"
            echo "APP_PORT       : ${appPort}"
            echo "CONTAINER_NAME : ${containerName}"
            echo "IMAGE_NAME     : ${imageName}"
            echo "CHANGE_ID      : ${changeId}"
        }

        stage('Validate Parameters') {

            if (!changeId) {
                error("CHANGE_ID is required")
            }
        }

        stage('Governance Check') {

            def response = sh(
                script: """
                    curl -s http://host.docker.internal:8081/api/change-requests/${changeId}
                """,
                returnStdout: true
            ).trim()

            echo "Governance Response: ${response}"

            if (!response.contains('"status":"APPROVED"')) {
                error("Change request ${changeId} is not approved")
            }
        }

        stage('Build') {
            sh './mvnw clean package -DskipTests'
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
            sleep 15
            sh "curl -f http://host.docker.internal:${appPort}/actuator/health"
        }

        stage('Update Governance Status') {

            sh """
                curl -X PUT \
                http://host.docker.internal:8081/api/change-requests/${changeId}/deploy
            """
        }

        stage('Generate Evidence') {

            writeFile file: 'evidence.json', text: """
{
  "application": "${appName}",
  "version": "${version}",
  "image": "${imageName}",
  "container": "${containerName}",
  "port": "${appPort}",
  "changeId": "${changeId}",
  "buildNumber": "${env.BUILD_NUMBER}",
  "status": "SUCCESS",
  "executedBy": "Jenkins"
}
"""

            archiveArtifacts artifacts: 'evidence.json', fingerprint: true
        }

        currentBuild.result = 'SUCCESS'

    } catch (Exception ex) {

        currentBuild.result = 'FAILURE'
        throw ex

    } finally {

        cleanWs()
    }
}
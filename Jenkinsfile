node {
    stage('Clone repository') {
        checkout scm
    }

    stage("Build image") {
        sh "./gradlew bootBuildImage --imageName=jeongjin984/lollipop_apiserver"
    }

    stage("Docker login") {
        environment {
            DOCKER_HUB_LOGIN = credentials('docker-hub')
        }
        sh 'docker login --username=$DOCKER_HUB_LOGIN_USR --password=$DOCKER_HUB_LOGIN_PSW'
    }

    stage("Image push") {
        sh "docker push jeongjin984/lollipop_apiserver"
    }

    stage("Resource cleanup") {
        sh "docker image prune -a"
    }
}
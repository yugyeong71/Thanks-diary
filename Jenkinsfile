pipeline {
  agent any

  tools {
    jdk 'JDK 17'
  }

  environment {
    ECR_REGISTRY = "${ECR_REGISTRY}"
    SSH_CONNECTION = "${SSH_CONNECTION}"
    AWS_CREDENTIALS = "${AWS_CREDENTIALS}"
    AWS_REGION = "${AWS_REGION}"
    SSH_CONNECTION_CREDENTIAL = "${SSH_CONNECTION_CREDENTIAL}"
  }

  stages {
    stage('Set Environment') {
      steps {
        script {
          switch(BRANCH_NAME) {
            case 'develop':
              env.ECR_REGISTRY
              env.SSH_CONNECTION
              break
//             case 'prod':
//               env.ECR_REGISTRY
//               env.SSH_CONNECTION
//               break
          }
        }
      }
    }

    stage('Clean Build') {
      steps {
        script {
          sh "chmod +x ./gradlew; \
              SPRING_PROFILES_ACTIVE=${BRANCH_NAME} ./gradlew clean build;"
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          docker.build("${ECR_REGISTRY}/${ECR_REPOSITORY}:${BUILD_NUMBER}", "--build-arg SPRING_PROFILES_ACTIVE=${BRANCH_NAME} .")
        }
      }
    }

    stage('Push Docker Image') {
      steps {
        script {
          withCredentials([aws(credentialsId: AWS_CREDENTIALS, region: AWS_REGION)]) {
            sh '''
              aws ecr get-login-password --region '${AWS_REGION}' | docker login --username AWS --password-stdin ${ECR_REGISTRY}
              docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:${BUILD_NUMBER}
              docker push ${ECR_REGISTRY}/${ECR_REPOSITORY}:latest
            '''
          }
        }
      }
    }

    stage('Remove Docker Image') {
      steps {
        sh "docker system prune -a --volumes -f"
      }
    }

    stage('Deploy to EC2') {
        steps {
          script {
            switch(BRANCH_NAME) {
                case 'develop' :
                    sshagent(credentials: [SSH_CONNECTION_CREDENTIAL]) {
                      sh "ssh -o StrictHostKeyChecking=no ${SSH_CONNECTION} 'IMAGE_NAME=${IMAGE_NAME} ECR_REGISTRY=${ECR_REGISTRY} BUILD_NUMBER=${BUILD_NUMBER} /home/ubuntu/dev/deploy.sh'"
                    }
                    break
//               case 'prod' :
//                     sshagent(credentials: [SSH_CONNECTION_CREDENTIAL]) {
//                       sh "ssh -o StrictHostKeyChecking=no ${SSH_CONNECTION} 'IMAGE_NAME=${IMAGE_NAME} ECR_REGISTRY=${ECR_REGISTRY} BUILD_NUMBER=${BUILD_NUMBER} /home/ubuntu/prod/deploy.sh'"
//                     }
//                     break
            }
        }
      }
    }
  }
}

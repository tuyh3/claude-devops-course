// Jenkinsfile - Jenkins Pipeline 配置
// 适用于 Jenkins 2.x+ (Declarative Pipeline)

pipeline {
    agent any

    // 环境变量配置
    environment {
        // 项目配置
        PROJECT_NAME = 'claude-devops-course'
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'

        // Docker 配置
        DOCKER_REGISTRY = 'registry.example.com'
        DOCKER_IMAGE_NAME = "${DOCKER_REGISTRY}/${PROJECT_NAME}"
        DOCKER_CREDENTIALS_ID = 'docker-registry-credentials'

        // 部署服务器配置
        DEV_SERVER = 'dev-server.example.com'
        TEST_SERVER = 'test-server.example.com'
        PROD_SERVER = 'prod-server.example.com'

        // SSH 凭证
        SSH_CREDENTIALS_ID = 'ssh-deployment-key'

        // SonarQube 配置（可选）
        SONAR_HOST_URL = 'http://sonarqube.example.com'
        SONAR_PROJECT_KEY = 'claude-devops-course'
    }

    // 定义构建参数
    parameters {
        choice(
            name: 'DEPLOY_ENV',
            choices: ['dev', 'test', 'uat', 'prod'],
            description: '选择部署环境'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: '是否跳过测试（生产环境强制运行测试）'
        )
        booleanParam(
            name: 'CLEAN_BUILD',
            defaultValue: false,
            description: '是否清理构建缓存'
        )
    }

    // 触发器配置
    triggers {
        // 定时构建（每天凌晨2点）
        cron('H 2 * * *')

        // GitLab Webhook 触发
        gitlab(
            triggerOnPush: true,
            triggerOnMergeRequest: true,
            branchFilterType: 'All',
            secretToken: "${env.GITLAB_WEBHOOK_SECRET}"
        )
    }

    // 构建选项
    options {
        // 保留最近10次构建
        buildDiscarder(logRotator(numToKeepStr: '10'))

        // 构建超时（60分钟）
        timeout(time: 60, unit: 'MINUTES')

        // 不允许并发构建
        disableConcurrentBuilds()

        // 显示时间戳
        timestamps()

        // GitLab 连接
        gitLabConnection('GitLab Connection')
    }

    stages {
        // ==================== 阶段1：环境准备 ====================
        stage('Prepare') {
            steps {
                script {
                    echo "========================================="
                    echo "开始构建 ${PROJECT_NAME}"
                    echo "分支: ${env.GIT_BRANCH}"
                    echo "提交: ${env.GIT_COMMIT}"
                    echo "构建号: ${env.BUILD_NUMBER}"
                    echo "部署环境: ${params.DEPLOY_ENV}"
                    echo "========================================="
                }

                // 清理工作空间（可选）
                script {
                    if (params.CLEAN_BUILD) {
                        echo "清理工作空间..."
                        cleanWs()
                    }
                }

                // 检出代码
                checkout scm

                // 更新 GitLab 构建状态
                updateGitlabCommitStatus name: 'build', state: 'running'
            }
        }

        // ==================== 阶段2：编译构建 ====================
        stage('Build') {
            steps {
                script {
                    echo "========================================="
                    echo "开始编译构建..."
                    echo "========================================="
                }

                // 使用 Gradle Wrapper 构建
                sh '''
                    chmod +x gradlew
                    ./gradlew clean build -x test --stacktrace --info
                '''

                // 保存构建产物
                archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
            }
        }

        // ==================== 阶段3：单元测试 ====================
        stage('Test') {
            when {
                expression {
                    // 生产环境强制运行测试
                    return !params.SKIP_TESTS || params.DEPLOY_ENV == 'prod'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "运行单元测试..."
                    echo "========================================="
                }

                sh './gradlew test jacocoTestReport'

                // 发布测试报告
                junit 'build/test-results/test/*.xml'

                // 发布代码覆盖率报告
                jacoco(
                    execPattern: 'build/jacoco/*.exec',
                    classPattern: 'build/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: 'src/test*'
                )

                // 发布 HTML 测试报告
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'build/reports/tests/test',
                    reportFiles: 'index.html',
                    reportName: 'Test Report'
                ])

                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'build/reports/jacoco/test/html',
                    reportFiles: 'index.html',
                    reportName: 'Coverage Report'
                ])
            }
        }

        // ==================== 阶段4：代码质量检查 ====================
        stage('Code Quality') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    echo "========================================="
                    echo "代码质量检查..."
                    echo "========================================="
                }

                // Checkstyle 检查
                sh './gradlew checkstyleMain checkstyleTest'

                // PMD 检查（可选）
                // sh './gradlew pmdMain pmdTest'

                // SpotBugs 检查（可选）
                // sh './gradlew spotbugsMain spotbugsTest'

                // 发布 Checkstyle 报告
                recordIssues(
                    enabledForFailure: true,
                    tool: checkStyle(pattern: 'build/reports/checkstyle/*.xml')
                )
            }
        }

        // ==================== 阶段5：SonarQube 分析（可选）====================
        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "SonarQube 代码分析..."
                    echo "========================================="
                }

                withSonarQubeEnv('SonarQube') {
                    sh """
                        ./gradlew sonarqube \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.host.url=${SONAR_HOST_URL}
                    """
                }
            }
        }

        // ==================== 阶段6：构建 Docker 镜像 ====================
        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "构建 Docker 镜像..."
                    echo "========================================="

                    // 确定镜像标签
                    def imageTag = ''
                    if (env.GIT_BRANCH == 'main') {
                        // main 分支使用 git tag 或 latest
                        imageTag = sh(
                            script: "git describe --tags --exact-match 2>/dev/null || echo 'latest'",
                            returnStdout: true
                        ).trim()
                    } else if (env.GIT_BRANCH == 'develop') {
                        // develop 分支使用 dev-{build_number}
                        imageTag = "dev-${env.BUILD_NUMBER}"
                    }

                    env.DOCKER_IMAGE_TAG = imageTag

                    echo "镜像标签: ${DOCKER_IMAGE_NAME}:${imageTag}"

                    // 构建 Docker 镜像
                    sh """
                        docker build \
                            -t ${DOCKER_IMAGE_NAME}:${imageTag} \
                            -t ${DOCKER_IMAGE_NAME}:latest \
                            --build-arg BUILD_NUMBER=${env.BUILD_NUMBER} \
                            --build-arg GIT_COMMIT=${env.GIT_COMMIT} \
                            .
                    """
                }
            }
        }

        // ==================== 阶段7：推送 Docker 镜像 ====================
        stage('Push Docker Image') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "推送 Docker 镜像到仓库..."
                    echo "========================================="
                }

                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS_ID}",
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh """
                        echo \$DOCKER_PASSWORD | docker login ${DOCKER_REGISTRY} -u \$DOCKER_USERNAME --password-stdin
                        docker push ${DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}
                        docker push ${DOCKER_IMAGE_NAME}:latest
                        docker logout ${DOCKER_REGISTRY}
                    """
                }
            }
        }

        // ==================== 阶段8：部署 ====================
        stage('Deploy') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo "========================================="
                    echo "部署到 ${params.DEPLOY_ENV} 环境..."
                    echo "========================================="

                    // 根据环境选择服务器
                    def targetServer = ''
                    switch(params.DEPLOY_ENV) {
                        case 'dev':
                            targetServer = DEV_SERVER
                            break
                        case 'test':
                            targetServer = TEST_SERVER
                            break
                        case 'prod':
                            targetServer = PROD_SERVER
                            break
                    }

                    // 部署到目标服务器
                    sshagent([SSH_CREDENTIALS_ID]) {
                        sh """
                            ssh -o StrictHostKeyChecking=no deployer@${targetServer} << 'EOF'
                                # 拉取最新镜像
                                docker pull ${DOCKER_IMAGE_NAME}:${env.DOCKER_IMAGE_TAG}

                                # 停止旧容器
                                docker-compose -f /opt/app/docker-compose.yml down

                                # 启动新容器
                                export IMAGE_TAG=${env.DOCKER_IMAGE_TAG}
                                docker-compose -f /opt/app/docker-compose.yml up -d

                                # 检查容器状态
                                docker-compose -f /opt/app/docker-compose.yml ps

                                # 清理未使用的镜像
                                docker image prune -f
EOF
                        """
                    }

                    // 健康检查
                    sleep 10
                    sh """
                        curl -f http://${targetServer}:8080/actuator/health || exit 1
                    """

                    echo "部署成功！"
                }
            }
        }
    }

    // ==================== 后置处理 ====================
    post {
        always {
            // 清理 Docker 资源
            script {
                sh 'docker system prune -f || true'
            }

            // 清理工作空间（可选）
            // cleanWs()
        }

        success {
            script {
                echo "========================================="
                echo "✅ 构建成功！"
                echo "========================================="

                // 更新 GitLab 状态
                updateGitlabCommitStatus name: 'build', state: 'success'

                // 发送成功通知（邮件、钉钉、企业微信等）
                emailext(
                    subject: "✅ Jenkins 构建成功: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                        <h2>构建成功</h2>
                        <p><b>项目:</b> ${env.JOB_NAME}</p>
                        <p><b>分支:</b> ${env.GIT_BRANCH}</p>
                        <p><b>构建号:</b> ${env.BUILD_NUMBER}</p>
                        <p><b>提交:</b> ${env.GIT_COMMIT}</p>
                        <p><b>部署环境:</b> ${params.DEPLOY_ENV}</p>
                        <p><a href="${env.BUILD_URL}">查看构建详情</a></p>
                    """,
                    to: '${DEFAULT_RECIPIENTS}',
                    mimeType: 'text/html'
                )
            }
        }

        failure {
            script {
                echo "========================================="
                echo "❌ 构建失败！"
                echo "========================================="

                // 更新 GitLab 状态
                updateGitlabCommitStatus name: 'build', state: 'failed'

                // 发送失败通知
                emailext(
                    subject: "❌ Jenkins 构建失败: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: """
                        <h2 style="color: red;">构建失败</h2>
                        <p><b>项目:</b> ${env.JOB_NAME}</p>
                        <p><b>分支:</b> ${env.GIT_BRANCH}</p>
                        <p><b>构建号:</b> ${env.BUILD_NUMBER}</p>
                        <p><b>提交:</b> ${env.GIT_COMMIT}</p>
                        <p><a href="${env.BUILD_URL}console">查看控制台输出</a></p>
                    """,
                    to: '${DEFAULT_RECIPIENTS}',
                    mimeType: 'text/html'
                )
            }
        }

        unstable {
            script {
                echo "========================================="
                echo "⚠️ 构建不稳定（测试失败或代码质量问题）"
                echo "========================================="

                updateGitlabCommitStatus name: 'build', state: 'failed'
            }
        }
    }
}

# CI/CD 配置指南

> 📋 **文档目的**：指导如何配置 Jenkins 和 GitLab CI/CD
> 👥 **目标读者**：运维工程师、DevOps 工程师、技术负责人
> ⏱️ **阅读时长**：约 40-50 分钟

---

## 📖 目录

- [引言](#引言)
- [第1章：Jenkins CI/CD 配置](#第1章jenkins-cicd-配置)
  - [1.1 Jenkins 环境准备](#11-jenkins-环境准备)
  - [1.2 创建 Jenkins Pipeline 任务](#12-创建-jenkins-pipeline-任务)
  - [1.3 配置 GitLab Webhook](#13-配置-gitlab-webhook)
  - [1.4 配置凭证](#14-配置凭证)
- [第2章：GitLab CI/CD 配置](#第2章gitlab-cicd-配置)
  - [2.1 GitLab Runner 安装](#21-gitlab-runner-安装)
  - [2.2 注册 Runner](#22-注册-runner)
  - [2.3 配置环境变量](#23-配置环境变量)
  - [2.4 配置 Docker Registry](#24-配置-docker-registry)
- [第3章：Docker 部署](#第3章docker-部署)
  - [3.1 本地开发环境](#31-本地开发环境)
  - [3.2 测试环境部署](#32-测试环境部署)
  - [3.3 生产环境部署](#33-生产环境部署)
- [第4章：监控配置](#第4章监控配置)
  - [4.1 Prometheus 配置](#41-prometheus-配置)
  - [4.2 Grafana 配置](#42-grafana-配置)
- [第5章：常见问题与解决方案](#第5章常见问题与解决方案)

---

## 引言

本项目包含完整的 CI/CD 配置，支持 **Jenkins** 和 **GitLab CI/CD** 两种方式。

### CI/CD 流程图

```
代码提交 → GitLab
    ↓
触发 CI/CD（Jenkins 或 GitLab CI）
    ↓
    ├─ 编译构建
    ├─ 运行测试
    ├─ 代码质量检查
    ├─ 构建 Docker 镜像
    ├─ 推送到镜像仓库
    └─ 部署到目标环境
    ↓
健康检查 → 部署成功/失败通知
```

---

## 第1章：Jenkins CI/CD 配置

### 1.1 Jenkins 环境准备

#### 安装 Jenkins

**方式1：Docker 安装（推荐）**

```bash
# 1. 创建 Jenkins 数据目录
mkdir -p ~/jenkins_home

# 2. 启动 Jenkins 容器
docker run -d \
  --name jenkins \
  -p 8080:8080 \
  -p 50000:50000 \
  -v ~/jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

# 3. 查看初始密码
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# 4. 访问 Jenkins
# http://your-server:8080
```

**方式2：Ubuntu/Debian 安装**

```bash
# 添加 Jenkins 仓库
wget -q -O - https://pkg.jenkins.io/debian/jenkins.io.key | sudo apt-key add -
sudo sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'

# 安装 Jenkins
sudo apt update
sudo apt install jenkins openjdk-21-jdk

# 启动 Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins
```

#### 安装必要的插件

登录 Jenkins 后，安装以下插件：

```
必装插件：
1. Git Plugin
2. GitLab Plugin
3. Pipeline Plugin
4. Docker Pipeline Plugin
5. Credentials Binding Plugin
6. Email Extension Plugin
7. Jacoco Plugin
8. HTML Publisher Plugin
9. SSH Agent Plugin
10. Blue Ocean（可选，更好的UI）
```

**安装步骤**：
```
1. 进入 Jenkins → Manage Jenkins → Manage Plugins
2. 切换到 "Available" 标签
3. 搜索并勾选上述插件
4. 点击 "Install without restart"
```

---

### 1.2 创建 Jenkins Pipeline 任务

#### Step 1：新建任务

```
1. 点击 "New Item"
2. 输入任务名称：claude-devops-course
3. 选择 "Pipeline"
4. 点击 "OK"
```

#### Step 2：配置任务

**General 配置**：
```
☑ GitHub project（如果使用 GitHub）
  或
☑ GitLab connection

☑ This project is parameterized
  添加参数：
  - String Parameter: DEPLOY_ENV (默认值: dev)
  - Boolean Parameter: SKIP_TESTS (默认值: false)
  - Boolean Parameter: CLEAN_BUILD (默认值: false)
```

**Build Triggers**：
```
☑ Build when a change is pushed to GitLab
  GitLab webhook URL: http://your-jenkins-url/project/claude-devops-course

  高级选项：
  - Allowed branches: main, develop
  - Secret token: 生成一个 token（记录下来，后面配置 GitLab 时需要）
```

**Pipeline 配置**：
```
Definition: Pipeline script from SCM

SCM: Git
  Repository URL: https://gitlab.example.com/your-group/claude-devops-course.git
  Credentials: 选择 GitLab 凭证（见下一节）
  Branches to build: */main, */develop

Script Path: Jenkinsfile
```

---

### 1.3 配置 GitLab Webhook

#### Step 1：在 GitLab 项目中配置 Webhook

```
1. 进入 GitLab 项目
2. Settings → Webhooks
3. 填写信息：
   - URL: http://your-jenkins-url/project/claude-devops-course
   - Secret Token: （填入 Jenkins 生成的 token）
   - Trigger: ☑ Push events, ☑ Merge request events
   - Enable SSL verification: ☑ (如果使用 HTTPS)
4. 点击 "Add webhook"
5. 点击 "Test" → "Push events" 测试连接
```

#### Step 2：验证配置

```bash
# 推送代码测试
git push origin main

# 检查 Jenkins 是否自动触发构建
# 进入 Jenkins → claude-devops-course → 查看构建历史
```

---

### 1.4 配置凭证

#### 配置 GitLab 凭证

```
1. Jenkins → Manage Jenkins → Manage Credentials
2. 选择 (global) domain
3. 点击 "Add Credentials"
4. 配置：
   - Kind: Username with password
   - Username: your-gitlab-username
   - Password: your-gitlab-password 或 Personal Access Token
   - ID: gitlab-credentials
   - Description: GitLab Credentials
5. 点击 "OK"
```

#### 配置 Docker Registry 凭证

```
1. 添加 Credentials
2. 配置：
   - Kind: Username with password
   - Username: docker-registry-username
   - Password: docker-registry-password
   - ID: docker-registry-credentials
   - Description: Docker Registry Credentials
```

#### 配置 SSH 凭证

```
1. 添加 Credentials
2. 配置：
   - Kind: SSH Username with private key
   - ID: ssh-deployment-key
   - Username: deployer
   - Private Key: Enter directly → 粘贴私钥内容
   - Passphrase: （如果私钥有密码）
   - Description: SSH Deployment Key
```

---

## 第2章：GitLab CI/CD 配置

### 2.1 GitLab Runner 安装

#### Docker 方式安装 Runner

```bash
# 1. 拉取 GitLab Runner 镜像
docker pull gitlab/gitlab-runner:latest

# 2. 创建配置目录
mkdir -p /srv/gitlab-runner/config

# 3. 启动 GitLab Runner 容器
docker run -d \
  --name gitlab-runner \
  --restart always \
  -v /srv/gitlab-runner/config:/etc/gitlab-runner \
  -v /var/run/docker.sock:/var/run/docker.sock \
  gitlab/gitlab-runner:latest
```

#### Linux 系统安装 Runner

**Ubuntu/Debian**:
```bash
# 添加 GitLab 官方仓库
curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.deb.sh | sudo bash

# 安装 GitLab Runner
sudo apt-get install gitlab-runner

# 启动服务
sudo systemctl start gitlab-runner
sudo systemctl enable gitlab-runner
```

**CentOS/RHEL**:
```bash
# 添加 GitLab 官方仓库
curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.rpm.sh | sudo bash

# 安装 GitLab Runner
sudo yum install gitlab-runner

# 启动服务
sudo systemctl start gitlab-runner
sudo systemctl enable gitlab-runner
```

---

### 2.2 注册 Runner

#### 获取注册信息

```
1. 进入 GitLab 项目
2. Settings → CI/CD → Runners
3. 展开 "Specific runners" 部分
4. 记录：
   - Registration token
   - GitLab URL
```

#### 注册 Runner

```bash
# 交互式注册
docker exec -it gitlab-runner gitlab-runner register

# 或使用命令行参数注册
docker exec -it gitlab-runner gitlab-runner register \
  --non-interactive \
  --url "https://gitlab.example.com/" \
  --registration-token "YOUR_REGISTRATION_TOKEN" \
  --executor "docker" \
  --docker-image "gradle:8.5-jdk21" \
  --description "Docker Runner for Spring Boot" \
  --tag-list "docker,gradle,spring-boot" \
  --docker-privileged \
  --docker-volumes "/var/run/docker.sock:/var/run/docker.sock" \
  --docker-volumes "/cache"
```

**注册时的配置说明**：
```
Enter the GitLab instance URL: https://gitlab.example.com/
Enter the registration token: YOUR_REGISTRATION_TOKEN
Enter a description: Docker Runner for Spring Boot
Enter tags: docker,gradle,spring-boot
Enter executor: docker
Enter default Docker image: gradle:8.5-jdk21
```

#### 验证 Runner

```bash
# 查看已注册的 Runner
docker exec -it gitlab-runner gitlab-runner list

# 在 GitLab 页面验证
# Settings → CI/CD → Runners → 查看是否有绿色的激活状态
```

---

### 2.3 配置环境变量

#### 在 GitLab 项目中配置 CI/CD 变量

```
1. 进入 GitLab 项目
2. Settings → CI/CD → Variables
3. 点击 "Add variable"
4. 添加以下变量：
```

**必要的环境变量**：

| Key | Value | Protected | Masked | 说明 |
|-----|-------|-----------|--------|------|
| `DOCKER_REGISTRY` | `registry.example.com` | ☐ | ☐ | Docker 仓库地址 |
| `CI_REGISTRY_USER` | `your-username` | ☑ | ☐ | Docker 仓库用户名 |
| `CI_REGISTRY_PASSWORD` | `your-password` | ☑ | ☑ | Docker 仓库密码 |
| `DEV_SERVER` | `dev-server.example.com` | ☐ | ☐ | 开发服务器地址 |
| `TEST_SERVER` | `test-server.example.com` | ☐ | ☐ | 测试服务器地址 |
| `PROD_SERVER` | `prod-server.example.com` | ☑ | ☐ | 生产服务器地址 |
| `SSH_PRIVATE_KEY` | `-----BEGIN...-----` | ☑ | ☑ | SSH 私钥 |
| `SONAR_HOST_URL` | `http://sonarqube.example.com` | ☐ | ☐ | SonarQube 地址 |
| `SONAR_TOKEN` | `your-sonar-token` | ☑ | ☑ | SonarQube Token |
| `WEBHOOK_URL` | `https://webhook-url` | ☐ | ☑ | 通知 Webhook |

**变量类型说明**：
- **Protected**: 只在受保护的分支（main, develop）上可用
- **Masked**: 在日志中隐藏变量值

---

### 2.4 配置 Docker Registry

#### 配置 Docker 私有仓库凭证

**方式1：使用 GitLab Container Registry**

```bash
# 1. 在项目设置中启用 Container Registry
# Settings → General → Visibility → Container Registry: ☑ Enabled

# 2. 查看 Registry URL
# 通常是：registry.gitlab.example.com/group/project

# 3. 登录（本地测试）
docker login registry.gitlab.example.com
# 用户名：GitLab 用户名
# 密码：Personal Access Token（scope: read_registry, write_registry）
```

**方式2：使用第三方 Docker Registry**

```bash
# 在 .gitlab-ci.yml 中使用凭证登录
before_script:
  - echo "$CI_REGISTRY_PASSWORD" | docker login $DOCKER_REGISTRY -u $CI_REGISTRY_USER --password-stdin
```

---

## 第3章：Docker 部署

### 3.1 本地开发环境

#### Step 1：创建 .env 文件

```bash
# 复制示例文件
cp .env.example .env

# 编辑配置
vim .env
```

```ini
# 开发环境配置
SPRING_PROFILES_ACTIVE=dev
APP_PORT=8080
LOG_LEVEL=DEBUG

# 使用 PostgreSQL（本地开发）
POSTGRES_DB=devopsdb
POSTGRES_USER=devops
POSTGRES_PASSWORD=dev123

# Redis
REDIS_PASSWORD=dev123
```

#### Step 2：启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f app

# 健康检查
curl http://localhost:8080/actuator/health
```

#### Step 3：本地开发调试

```bash
# 只启动依赖服务（PostgreSQL + Redis）
docker-compose up -d postgres redis

# 在 IDE 中启动 Spring Boot 应用进行调试
./gradlew bootRun
```

---

### 3.2 测试环境部署

#### 服务器准备

```bash
# 1. 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com | sh
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 2. 创建部署目录
mkdir -p /opt/app
cd /opt/app

# 3. 创建 .env 文件
cat > .env << 'EOF'
SPRING_PROFILES_ACTIVE=test
DOCKER_REGISTRY=registry.example.com
IMAGE_TAG=latest
# ... 其他配置
EOF

# 4. 复制 docker-compose.yml
# 从 Git 仓库拉取或手动复制

# 5. 启动服务
docker-compose up -d
```

#### 自动化部署脚本

在服务器上创建 `/opt/app/deploy.sh`：

```bash
#!/bin/bash
set -e

echo "========================================="
echo "开始部署到测试环境..."
echo "========================================="

# 切换到部署目录
cd /opt/app

# 拉取最新镜像
echo "拉取最新镜像..."
docker-compose pull

# 停止旧容器
echo "停止旧容器..."
docker-compose down

# 启动新容器
echo "启动新容器..."
docker-compose up -d

# 检查容器状态
echo "检查容器状态..."
docker-compose ps

# 健康检查
echo "等待应用启动..."
sleep 10

if curl -f http://localhost:8080/actuator/health; then
    echo "✅ 部署成功！"
else
    echo "❌ 健康检查失败！"
    exit 1
fi

# 清理未使用的镜像
echo "清理未使用的镜像..."
docker image prune -f

echo "========================================="
echo "部署完成！"
echo "========================================="
```

```bash
# 添加执行权限
chmod +x /opt/app/deploy.sh
```

---

### 3.3 生产环境部署

#### 生产环境配置

**.env 文件**（生产环境）：

```ini
# 生产环境配置
SPRING_PROFILES_ACTIVE=prod
DOCKER_REGISTRY=registry.example.com
IMAGE_TAG=v1.0.0  # 使用固定版本tag

# 数据库配置（生产Oracle）
DB_URL=jdbc:oracle:thin:@//(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=scan-ip)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=PRODDB)))
DB_USERNAME=prod_user
DB_PASSWORD=strong_password_here

# Redis 配置
REDIS_PASSWORD=strong_redis_password

# JVM 配置（生产环境调整）
JAVA_OPTS=-Xms2g -Xmx4g -XX:+UseG1GC

# 日志级别
LOG_LEVEL=WARN
```

#### 蓝绿部署脚本

```bash
#!/bin/bash
# blue-green-deploy.sh - 蓝绿部署脚本

set -e

NEW_VERSION=$1
if [ -z "$NEW_VERSION" ]; then
    echo "使用方法: ./blue-green-deploy.sh <version>"
    exit 1
fi

echo "========================================="
echo "蓝绿部署: $NEW_VERSION"
echo "========================================="

# 当前环境颜色
CURRENT_COLOR=$(cat /opt/app/.current_color || echo "blue")
if [ "$CURRENT_COLOR" == "blue" ]; then
    NEW_COLOR="green"
else
    NEW_COLOR="blue"
fi

echo "当前环境: $CURRENT_COLOR"
echo "新环境: $NEW_COLOR"

# 1. 部署新环境
cd /opt/app/$NEW_COLOR
export IMAGE_TAG=$NEW_VERSION
docker-compose up -d

# 2. 健康检查
sleep 15
for i in {1..10}; do
    if curl -f http://localhost:8081/actuator/health; then
        echo "✅ 新环境健康检查通过"
        break
    fi
    echo "等待新环境启动... ($i/10)"
    sleep 5
done

# 3. 切换 Nginx 流量到新环境
echo "切换流量到 $NEW_COLOR 环境..."
sed -i "s/upstream_$CURRENT_COLOR/upstream_$NEW_COLOR/g" /etc/nginx/conf.d/app.conf
nginx -s reload

# 4. 验证切换
sleep 5
if curl -f http://localhost/actuator/health; then
    echo "✅ 流量切换成功"

    # 5. 停止旧环境
    cd /opt/app/$CURRENT_COLOR
    docker-compose down

    # 6. 更新当前颜色标记
    echo "$NEW_COLOR" > /opt/app/.current_color

    echo "========================================="
    echo "✅ 蓝绿部署完成！"
    echo "当前版本: $NEW_VERSION"
    echo "当前环境: $NEW_COLOR"
    echo "========================================="
else
    echo "❌ 切换后健康检查失败，回滚！"
    sed -i "s/upstream_$NEW_COLOR/upstream_$CURRENT_COLOR/g" /etc/nginx/conf.d/app.conf
    nginx -s reload
    cd /opt/app/$NEW_COLOR
    docker-compose down
    exit 1
fi
```

---

## 第4章：监控配置

### 4.1 Prometheus 配置

#### 创建 Prometheus 配置文件

`docker/prometheus/prometheus.yml`：

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  # Spring Boot Actuator
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8080']
        labels:
          application: 'claude-devops-course'
          environment: 'production'

  # Prometheus 自身监控
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # Docker 容器监控（需要 cAdvisor）
  - job_name: 'docker'
    static_configs:
      - targets: ['cadvisor:8080']
```

---

### 4.2 Grafana 配置

#### 配置数据源

1. 访问 Grafana：http://localhost:3000
2. 登录（默认：admin/admin）
3. 添加数据源：
   - Configuration → Data Sources → Add data source
   - 选择 Prometheus
   - URL: http://prometheus:9090
   - Save & Test

#### 导入 Dashboard

**推荐的 Dashboard**：
- Spring Boot 2.1 System Monitor: Dashboard ID `11378`
- JVM (Micrometer): Dashboard ID `4701`
- Docker Container & Host Metrics: Dashboard ID `179`

---

## 第5章：常见问题与解决方案

### 5.1 Jenkins 相关问题

**Q1: Jenkins 无法连接到 GitLab**

```
解决方案：
1. 检查网络连接：ping gitlab.example.com
2. 检查防火墙配置
3. 确认 GitLab URL 配置正确
4. 检查 GitLab Webhook 设置
```

**Q2: Pipeline 构建失败：权限不足**

```
解决方案：
# 将 Jenkins 用户添加到 docker 组
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

---

### 5.2 GitLab Runner 相关问题

**Q1: Runner 无法注册**

```
解决方案：
1. 检查 Registration Token 是否正确
2. 确认 GitLab URL 可访问
3. 检查防火墙和网络配置
```

**Q2: Pipeline 运行缓慢**

```
解决方案：
# 配置 Runner 缓存
在 config.toml 中添加：
[[runners]]
  [runners.docker]
    volumes = ["/cache", "/var/run/docker.sock:/var/run/docker.sock"]
  [runners.cache]
    Type = "s3"
    Shared = true
```

---

### 5.3 Docker 相关问题

**Q1: 镜像构建失败：网络超时**

```
解决方案：
# 配置 Docker 镜像加速
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.docker-cn.com"
  ]
}
EOF
sudo systemctl restart docker
```

**Q2: 容器启动后立即退出**

```
解决方案：
# 查看容器日志
docker logs <container-id>

# 常见原因：
1. 环境变量配置错误
2. 数据库连接失败
3. 端口被占用
```

---

**文档版本**: v1.0
**最后更新**: 2025-11-17
**维护**: DevOps Course Team

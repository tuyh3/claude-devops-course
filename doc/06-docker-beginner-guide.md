# Docker 零基础入门指南（适合完全不懂容器的开发者）

> 📢 **写给 Docker 小白**：如果你听说过 Docker 但完全不懂它是什么、有什么用、怎么用，这份文档将从零开始，手把手教你在工作中使用 Docker。

---

## 📋 目录

- [第零章：Docker 是什么（完全零基础必读）](#第零章docker-是什么完全零基础必读)
- [第一章：安装 Docker](#第一章安装-docker)
- [第二章：核心概念](#第二章核心概念)
- [第三章：常用命令速查](#第三章常用命令速查)
- [第四章：Dockerfile 编写](#第四章dockerfile-编写)
- [第五章：Docker Compose 多容器编排](#第五章docker-compose-多容器编排)
- [第六章：实战：部署 Spring Boot 应用](#第六章实战部署-spring-boot-应用)
- [第七章：常见问题与解决方案](#第七章常见问题与解决方案)
- [第八章：工作中的最佳实践](#第八章工作中的最佳实践)

---

## 第零章：Docker 是什么（完全零基础必读）

### 0.1 一个真实的问题

**场景**：你开发了一个 Spring Boot 应用，在你电脑上跑得好好的...

```
你："代码写完了，测试通过，可以部署了！"

运维："部署到服务器后启动失败..."

你："不可能啊，我这里跑得好好的！"

运维："服务器是 CentOS，你是 Mac..."
运维："服务器 JDK 是 8，你用的 21..."
运维："服务器没装你用的那个库..."

你："......"
```

**这就是经典的**：**"在我电脑上能跑啊！"**

### 0.2 Docker 解决什么问题

**Docker 的核心思想**：把应用和它的运行环境打包在一起。

```
传统部署：
  应用代码 → 部署到服务器 → 祈祷环境一致 → 经常出问题

Docker 部署：
  应用代码 + JDK + 依赖库 + 配置 → 打包成镜像 → 在任何地方运行 → 环境完全一致
```

### 0.3 用搬家来理解 Docker

**传统方式（不用 Docker）**：
```
搬家时：
  1. 把家具拆了
  2. 搬到新家
  3. 重新组装
  4. 发现螺丝少了、尺寸不对...
```

**Docker 方式**：
```
搬家时：
  1. 把整个房间（包括家具、装修、电器）装进一个集装箱
  2. 集装箱运到新地址
  3. 打开集装箱，房间和原来一模一样
```

**Docker 就是这个"集装箱"**！

### 0.4 Docker 的核心概念预览

| 概念 | 类比 | 说明 |
|------|------|------|
| **镜像（Image）** | 房间设计图 | 包含应用和环境的模板，只读 |
| **容器（Container）** | 根据设计图建的房间 | 镜像的运行实例，可以启动、停止 |
| **仓库（Registry）** | 设计图仓库 | 存放镜像的地方，如 Docker Hub |
| **Dockerfile** | 设计图纸 | 描述如何构建镜像的文件 |

### 0.5 为什么要学 Docker

**工作中必须会的理由**：

1. **部署标准化**：开发、测试、生产环境完全一致
2. **快速部署**：秒级启动应用
3. **资源隔离**：多个应用互不影响
4. **易于扩展**：需要更多实例？复制容器即可
5. **CI/CD 必备**：现代 DevOps 的基础

**你会遇到的场景**：
- 运维给你一个 Docker 镜像，让你本地测试
- 需要把应用打包成 Docker 镜像交付
- 使用 Docker Compose 启动开发环境
- Jenkins 流水线中构建 Docker 镜像

---

## 第一章：安装 Docker

> 💡 **说明**：工作中 Docker 主要运行在 Linux 服务器上，本章只介绍 Linux 安装。

### 1.1 Ubuntu 安装

```bash
# 1. 更新包索引
sudo apt-get update

# 2. 安装依赖
sudo apt-get install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg \
    lsb-release

# 3. 添加 Docker 官方 GPG 密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 4. 设置仓库
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 5. 安装 Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# 6. 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 7. 将当前用户加入 docker 组（免 sudo）
sudo usermod -aG docker $USER
# 需要重新登录生效

# 8. 验证
docker --version
docker run hello-world
```

### 1.2 CentOS 安装

```bash
# 1. 安装依赖
sudo yum install -y yum-utils

# 2. 添加仓库
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# 3. 安装 Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 4. 启动
sudo systemctl start docker
sudo systemctl enable docker

# 5. 验证
docker --version
docker run hello-world
```

### 1.3 离线安装（生产环境无外网）

> 💡 **重要**：生产环境通常无法连接外网，需要使用离线安装方式。

**步骤1：下载离线安装包**（在可联网机器上）

```bash
# 从 Docker 官方下载二进制包
# https://download.docker.com/linux/static/stable/x86_64/

wget https://download.docker.com/linux/static/stable/x86_64/docker-24.0.7.tgz

# 拷贝到生产服务器
scp docker-24.0.7.tgz user@prod-server:/tmp/
```

**步骤2：离线安装**（在生产服务器上）

```bash
# 1. 解压
cd /tmp
tar -xzvf docker-24.0.7.tgz

# 2. 复制到系统目录
sudo cp docker/* /usr/bin/

# 3. 创建 systemd 服务文件
sudo tee /etc/systemd/system/docker.service <<-'EOF'
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
After=network-online.target firewalld.service
Wants=network-online.target

[Service]
Type=notify
ExecStart=/usr/bin/dockerd
ExecReload=/bin/kill -s HUP $MAINPID
LimitNOFILE=infinity
LimitNPROC=infinity
LimitCORE=infinity
TimeoutStartSec=0
Delegate=yes
KillMode=process
Restart=on-failure
StartLimitBurst=3
StartLimitInterval=60s

[Install]
WantedBy=multi-user.target
EOF

# 4. 启动 Docker
sudo systemctl daemon-reload
sudo systemctl start docker
sudo systemctl enable docker

# 5. 验证安装
docker --version
docker info
```

**步骤3：配置用户权限**（免 sudo）

```bash
# 创建 docker 组
sudo groupadd docker

# 将当前用户加入 docker 组
sudo usermod -aG docker $USER

# 重新登录后生效，或执行
newgrp docker

# 验证
docker ps
```

### 1.4 配置镜像加速（可联网环境）

> 如果生产环境无法联网，跳过此步骤，使用离线方式导入镜像。

由于网络原因，从 Docker Hub 拉取镜像很慢，需要配置国内镜像源。

```bash
# 创建配置文件
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com"
  ]
}
EOF

# 重启 Docker
sudo systemctl daemon-reload
sudo systemctl restart docker
```

**验证加速是否生效**：
```bash
docker info | grep -A 5 "Registry Mirrors"
```

---

## 第二章：核心概念

### 2.1 镜像（Image）

**什么是镜像？**

镜像 = 应用 + 运行环境 + 依赖库 + 配置

```
一个 Java 应用的镜像包含：
├── Linux 操作系统（精简版）
├── JDK 21
├── 你的 Spring Boot JAR 包
└── 启动命令
```

**镜像是只读的**，就像一个模板。

**常用镜像命令**：
```bash
# 搜索镜像
docker search nginx

# 拉取镜像
docker pull nginx              # 拉取最新版
docker pull nginx:1.24         # 拉取指定版本
docker pull openjdk:21-jdk     # 拉取 JDK 21

# 查看本地镜像
docker images

# 删除镜像
docker rmi nginx
docker rmi nginx:1.24
```

**镜像命名规则**：
```
[仓库地址/]镜像名[:标签]

示例：
nginx                           # 官方镜像，默认 latest 标签
nginx:1.24                      # 官方镜像，指定版本
mysql:8.0                       # MySQL 8.0
openjdk:21-jdk-slim             # OpenJDK 21 精简版
registry.example.com/myapp:1.0  # 私有仓库的镜像
```

### 2.2 容器（Container）

**什么是容器？**

容器 = 镜像的运行实例

```
镜像 → 容器
（类）→（对象）
（设计图）→（房间）
```

**一个镜像可以创建多个容器**：
```
nginx 镜像
  ├── nginx 容器 1（端口 8080）
  ├── nginx 容器 2（端口 8081）
  └── nginx 容器 3（端口 8082）
```

**容器生命周期**：
```
创建（create）→ 启动（start）→ 运行中（running）
                                    ↓
              删除（rm）← 停止（stop）
```

**常用容器命令**：
```bash
# 创建并启动容器
docker run nginx

# 后台运行容器
docker run -d nginx

# 指定容器名称
docker run -d --name my-nginx nginx

# 端口映射（重要！）
docker run -d -p 8080:80 nginx
# 含义：把容器的 80 端口映射到主机的 8080 端口
# 访问：http://localhost:8080

# 查看运行中的容器
docker ps

# 查看所有容器（包括停止的）
docker ps -a

# 停止容器
docker stop my-nginx

# 启动已停止的容器
docker start my-nginx

# 重启容器
docker restart my-nginx

# 删除容器
docker rm my-nginx

# 强制删除运行中的容器
docker rm -f my-nginx

# 进入容器内部
docker exec -it my-nginx /bin/bash
# 或
docker exec -it my-nginx sh

# 查看容器日志
docker logs my-nginx
docker logs -f my-nginx  # 实时查看（类似 tail -f）
```

### 2.3 端口映射详解

**为什么需要端口映射？**

容器是隔离的，外部无法直接访问容器内的服务。

```
没有端口映射：
┌─────────────┐      ┌─────────────┐
│  你的电脑    │  ✗   │   容器      │
│             │ ───→ │  nginx:80   │
└─────────────┘      └─────────────┘
无法访问

有端口映射：
┌─────────────┐      ┌─────────────┐
│  你的电脑    │      │   容器      │
│  :8080     │ ───→ │  nginx:80   │
└─────────────┘      └─────────────┘
docker run -p 8080:80
访问 localhost:8080 → 转发到容器的 80 端口
```

**端口映射格式**：
```bash
-p 主机端口:容器端口

# 示例
-p 8080:80         # 主机 8080 → 容器 80
-p 3306:3306       # 主机 3306 → 容器 3306
-p 127.0.0.1:8080:80  # 只允许本机访问
```

### 2.4 数据卷（Volume）

**问题**：容器删除后，里面的数据就丢失了。

**解决**：使用数据卷，把数据保存在主机上。

```bash
# 挂载主机目录到容器
docker run -d \
  -p 3306:3306 \
  -v /my/data:/var/lib/mysql \
  mysql:8.0

# -v 主机目录:容器目录
# 容器的 /var/lib/mysql 数据会保存到主机的 /my/data
```

**常见挂载场景**：
```bash
# MySQL 数据持久化
-v /data/mysql:/var/lib/mysql

# Nginx 配置文件
-v /my/nginx.conf:/etc/nginx/nginx.conf

# 应用日志
-v /logs/app:/app/logs

# 多个挂载
docker run -d \
  -v /data/mysql:/var/lib/mysql \
  -v /my/config:/etc/mysql/conf.d \
  mysql:8.0
```

### 2.5 环境变量

很多镜像需要通过环境变量配置。

```bash
# 设置单个环境变量
docker run -d \
  -e MYSQL_ROOT_PASSWORD=123456 \
  mysql:8.0

# 设置多个环境变量
docker run -d \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=mydb \
  -e MYSQL_USER=myuser \
  -e MYSQL_PASSWORD=mypassword \
  mysql:8.0

# 从文件读取环境变量
docker run -d --env-file .env mysql:8.0
```

### 2.6 仓库（Registry）

**仓库 = 存放镜像的地方**

| 仓库 | 地址 | 说明 |
|------|------|------|
| Docker Hub | hub.docker.com | 官方公共仓库 |
| 阿里云 | cr.console.aliyun.com | 国内速度快 |
| 私有仓库 | registry.company.com | 公司内部 |

**推送镜像到仓库**：
```bash
# 1. 登录
docker login registry.example.com

# 2. 给镜像打标签
docker tag myapp:1.0 registry.example.com/myapp:1.0

# 3. 推送
docker push registry.example.com/myapp:1.0

# 4. 其他人拉取
docker pull registry.example.com/myapp:1.0
```

---

## 第三章：常用命令速查

### 3.1 镜像命令

```bash
# 搜索镜像
docker search 镜像名

# 拉取镜像
docker pull 镜像名:标签

# 查看本地镜像
docker images

# 删除镜像
docker rmi 镜像名

# 删除所有未使用的镜像
docker image prune -a

# 查看镜像详情
docker inspect 镜像名

# 查看镜像构建历史
docker history 镜像名

# 导出镜像为文件
docker save -o myapp.tar myapp:1.0

# 从文件导入镜像
docker load -i myapp.tar
```

### 3.2 容器命令

```bash
# 创建并运行容器
docker run [选项] 镜像名

# 常用选项
-d                  # 后台运行
-p 主机端口:容器端口  # 端口映射
-v 主机目录:容器目录  # 数据卷挂载
-e KEY=VALUE        # 环境变量
--name 容器名        # 指定容器名
--restart always    # 自动重启
--network 网络名     # 指定网络

# 完整示例
docker run -d \
  --name my-mysql \
  -p 3306:3306 \
  -v /data/mysql:/var/lib/mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  --restart always \
  mysql:8.0

# 查看容器
docker ps           # 运行中的
docker ps -a        # 所有的

# 启动/停止/重启
docker start 容器名
docker stop 容器名
docker restart 容器名

# 删除容器
docker rm 容器名
docker rm -f 容器名  # 强制删除

# 删除所有停止的容器
docker container prune

# 进入容器
docker exec -it 容器名 /bin/bash
docker exec -it 容器名 sh

# 在容器中执行命令
docker exec 容器名 ls /app

# 查看日志
docker logs 容器名
docker logs -f 容器名           # 实时查看
docker logs --tail 100 容器名   # 最后100行

# 查看容器资源使用
docker stats

# 查看容器详情
docker inspect 容器名

# 复制文件
docker cp 本地文件 容器名:容器路径
docker cp 容器名:容器路径 本地文件
```

### 3.3 实用组合命令

```bash
# 停止所有容器
docker stop $(docker ps -q)

# 删除所有容器
docker rm $(docker ps -aq)

# 删除所有镜像
docker rmi $(docker images -q)

# 清理所有未使用的资源（镜像、容器、网络、缓存）
docker system prune -a

# 查看 Docker 磁盘使用
docker system df
```

---

## 第四章：Dockerfile 编写

### 4.1 什么是 Dockerfile

**Dockerfile = 构建镜像的脚本**

告诉 Docker：
1. 基于什么镜像
2. 要复制什么文件
3. 要运行什么命令
4. 如何启动应用

### 4.2 Dockerfile 基本结构

```dockerfile
# 基础镜像
FROM openjdk:21-jdk-slim

# 维护者信息
LABEL maintainer="your-email@example.com"

# 设置工作目录
WORKDIR /app

# 复制文件
COPY target/myapp.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
CMD ["java", "-jar", "app.jar"]
```

### 4.3 常用指令详解

#### FROM - 基础镜像

```dockerfile
# 官方镜像
FROM ubuntu:22.04
FROM openjdk:21-jdk-slim
FROM node:18-alpine
FROM python:3.11

# 选择原则：
# - 官方镜像优先
# - alpine 版本更小（推荐）
# - slim 版本去除了不必要的包
```

#### WORKDIR - 工作目录

```dockerfile
WORKDIR /app

# 相当于 cd /app
# 后续的 COPY、RUN、CMD 都在这个目录下执行
```

#### COPY - 复制文件

```dockerfile
# 复制单个文件
COPY app.jar /app/app.jar

# 复制目录
COPY src/ /app/src/

# 复制多个文件
COPY package.json package-lock.json ./

# 使用通配符
COPY *.jar /app/
```

#### RUN - 执行命令

```dockerfile
# 安装软件
RUN apt-get update && apt-get install -y curl

# 创建目录
RUN mkdir -p /app/logs

# 多个命令用 && 连接（减少镜像层数）
RUN apt-get update \
    && apt-get install -y curl wget \
    && rm -rf /var/lib/apt/lists/*
```

#### ENV - 环境变量

```dockerfile
# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx1024m"
ENV APP_HOME=/app

# 使用环境变量
WORKDIR $APP_HOME
```

#### EXPOSE - 暴露端口

```dockerfile
# 声明容器监听的端口
EXPOSE 8080
EXPOSE 8080 8443

# 注意：这只是声明，实际映射需要 docker run -p
```

#### CMD - 启动命令

```dockerfile
# exec 格式（推荐）
CMD ["java", "-jar", "app.jar"]

# shell 格式
CMD java -jar app.jar

# 使用环境变量
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

#### ENTRYPOINT - 入口点

```dockerfile
# 与 CMD 类似，但更适合作为可执行程序
ENTRYPOINT ["java", "-jar", "app.jar"]

# ENTRYPOINT + CMD 组合
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]
# 运行时可以覆盖 CMD：docker run myapp other.jar
```

### 4.4 Spring Boot 应用的 Dockerfile

**简单版本**：

```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

**生产版本（多阶段构建）**：

```dockerfile
# 阶段1：构建
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# 先复制依赖文件，利用缓存
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 复制源码并构建
COPY src ./src
RUN gradle clean build -x test --no-daemon

# 阶段2：运行
FROM eclipse-temurin:21-jre-alpine

# 创建非 root 用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# 从构建阶段复制 JAR
COPY --from=builder /app/build/libs/*.jar app.jar

# 设置时区
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

# 切换到非 root 用户
USER appuser

EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 4.5 构建镜像

```bash
# 基本构建
docker build -t myapp:1.0 .

# 指定 Dockerfile
docker build -t myapp:1.0 -f Dockerfile.prod .

# 不使用缓存
docker build --no-cache -t myapp:1.0 .

# 查看构建的镜像
docker images | grep myapp

# 运行测试
docker run -d -p 8080:8080 --name myapp myapp:1.0

# 查看日志
docker logs -f myapp
```

### 4.6 .dockerignore 文件

类似 `.gitignore`，排除不需要的文件：

```
# .dockerignore
.git
.gitignore
.idea
*.md
node_modules
target
build
*.log
.env
docker-compose*.yml
Dockerfile*
```

---

## 第五章：Docker Compose 多容器编排

### 5.1 为什么需要 Docker Compose

**场景**：你的应用需要多个服务

```
Spring Boot 应用
├── 应用本身
├── MySQL 数据库
├── Redis 缓存
└── Nginx 反向代理
```

**不用 Compose**：
```bash
# 需要执行多条命令
docker run -d --name mysql ...
docker run -d --name redis ...
docker run -d --name app --link mysql --link redis ...
docker run -d --name nginx --link app ...
```

**用 Compose**：
```bash
# 一条命令启动所有服务
docker-compose up -d
```

### 5.2 docker-compose.yml 基本结构

```yaml
version: '3.8'

services:
  # 服务1：应用
  app:
    image: myapp:1.0
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - db
      - redis

  # 服务2：数据库
  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=123456
    volumes:
      - mysql-data:/var/lib/mysql

  # 服务3：缓存
  redis:
    image: redis:7-alpine

volumes:
  mysql-data:
```

### 5.3 完整示例

```yaml
# docker-compose.yml
version: '3.8'

services:
  # Spring Boot 应用
  app:
    build: .  # 从当前目录的 Dockerfile 构建
    # 或者使用现有镜像
    # image: myapp:1.0
    container_name: myapp
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/mydb
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=123456
      - SPRING_REDIS_HOST=redis
    depends_on:
      db:
        condition: service_healthy
      redis:
        condition: service_started
    restart: unless-stopped
    networks:
      - app-network

  # MySQL 数据库
  db:
    image: mysql:8.0
    container_name: mysql
    environment:
      - MYSQL_ROOT_PASSWORD=123456
      - MYSQL_DATABASE=mydb
      - TZ=Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql  # 初始化脚本
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped
    networks:
      - app-network

  # Redis 缓存
  redis:
    image: redis:7-alpine
    container_name: redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes
    restart: unless-stopped
    networks:
      - app-network

  # Nginx 反向代理（可选）
  nginx:
    image: nginx:alpine
    container_name: nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    restart: unless-stopped
    networks:
      - app-network

# 数据卷
volumes:
  mysql-data:
  redis-data:

# 网络
networks:
  app-network:
    driver: bridge
```

### 5.4 Docker Compose 常用命令

```bash
# 启动所有服务（后台）
docker-compose up -d

# 启动并重新构建
docker-compose up -d --build

# 查看运行状态
docker-compose ps

# 查看日志
docker-compose logs
docker-compose logs -f app  # 实时查看某个服务

# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v

# 重启某个服务
docker-compose restart app

# 进入某个服务的容器
docker-compose exec app sh
docker-compose exec db mysql -uroot -p

# 查看服务配置
docker-compose config
```

### 5.5 多环境配置

**开发环境**：`docker-compose.yml`
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
```

**生产环境**：`docker-compose.prod.yml`
```yaml
version: '3.8'
services:
  app:
    image: registry.example.com/myapp:1.0
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    deploy:
      replicas: 2
      resources:
        limits:
          memory: 1G
```

**使用**：
```bash
# 开发环境
docker-compose up -d

# 生产环境
docker-compose -f docker-compose.prod.yml up -d
```

---

## 第六章：实战：部署 Spring Boot 应用

### 6.1 项目结构

```
claude-devops-course/
├── src/
├── build.gradle
├── Dockerfile
├── docker-compose.yml
└── .dockerignore
```

### 6.2 创建 Dockerfile

```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# 复制 Gradle 文件
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 下载依赖
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true

# 复制源码并构建
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# 运行阶段
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 6.3 创建 docker-compose.yml

```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    build: .
    container_name: claude-devops-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:oracle:thin:@//db:1521/ORCL
      - SPRING_DATASOURCE_USERNAME=TCBS
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
    depends_on:
      - db
    restart: unless-stopped
    networks:
      - app-network

  # 如果使用 Oracle（需要自己构建镜像）
  # db:
  #   image: oracle/database:19.3.0-ee
  #   ...

  # 或者使用 PostgreSQL 替代测试
  db:
    image: postgres:15-alpine
    container_name: postgres
    environment:
      - POSTGRES_USER=TCBS
      - POSTGRES_PASSWORD=${DB_PASSWORD}
      - POSTGRES_DB=devops
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    restart: unless-stopped
    networks:
      - app-network

volumes:
  postgres-data:

networks:
  app-network:
    driver: bridge
```

### 6.4 创建环境变量文件

```bash
# .env
DB_PASSWORD=your_secure_password
```

### 6.5 构建和运行

```bash
# 1. 构建并启动
docker-compose up -d --build

# 2. 查看状态
docker-compose ps

# 3. 查看应用日志
docker-compose logs -f app

# 4. 测试接口
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/customers

# 5. 停止服务
docker-compose down
```

### 6.6 常见部署场景

**场景1：本地开发测试**
```bash
# 快速启动依赖服务
docker-compose up -d db redis

# 本地运行应用（连接容器中的数据库）
./gradlew bootRun
```

**场景2：完整容器化部署**
```bash
# 所有服务都在容器中
docker-compose up -d
```

**场景3：生产环境部署**
```bash
# 拉取镜像
docker pull registry.example.com/myapp:1.0

# 使用生产配置
docker-compose -f docker-compose.prod.yml up -d
```

### 6.7 离线部署（生产环境无外网）

> 💡 **重要**：生产环境通常无法连接外网，需要使用离线部署方式。

#### 方案一：镜像文件导出/导入（简单直接）

把镜像导出为 tar 文件，拷贝到生产服务器后导入。

**在可联网的机器上导出**：
```bash
# 1. 导出单个镜像
docker save -o myapp.tar myapp:1.0

# 2. 导出多个镜像
docker save -o all-images.tar myapp:1.0 mysql:8.0 redis:7-alpine

# 3. 查看导出的文件
ls -lh *.tar
```

**拷贝到生产服务器**：
```bash
# 使用 scp
scp all-images.tar user@prod-server:/tmp/

# 或使用 U盘、FTP 等方式
```

**在生产服务器上导入**：
```bash
# 1. 导入镜像
docker load -i /tmp/all-images.tar

# 2. 验证
docker images

# 3. 运行
docker run -d -p 8080:8080 myapp:1.0
```

#### 方案二：私有镜像仓库（推荐长期使用）

在内网搭建私有镜像仓库（如 Harbor），所有镜像先推到私有仓库，生产环境从私有仓库拉取。

```
外网环境                    内网环境
┌─────────┐              ┌─────────┐              ┌─────────┐
│Docker Hub│ → 拉取 →    │  Harbor  │  → 拉取 →   │ 生产服务器│
└─────────┘              │(私有仓库) │              └─────────┘
                         └─────────┘
```

**使用流程**：
```bash
# 1. 在可联网的机器上拉取镜像
docker pull openjdk:21-jre-alpine

# 2. 重新打标签
docker tag openjdk:21-jre-alpine harbor.company.com/library/openjdk:21-jre-alpine

# 3. 推送到私有仓库
docker push harbor.company.com/library/openjdk:21-jre-alpine

# 4. 生产服务器从私有仓库拉取
docker pull harbor.company.com/library/openjdk:21-jre-alpine
```

> 💡 **注意**：如果生产服务器没有安装 Docker，请参考 [1.3 离线安装](#13-离线安装生产环境无外网)。

#### 完整的离线部署流程

**步骤1：准备部署包**（在可联网环境）

```bash
#!/bin/bash
# pack-for-offline.sh

VERSION=${1:-"1.0.0"}
PACKAGE_NAME="myapp-${VERSION}-offline"

echo "=== 构建镜像 ==="
docker build -t myapp:${VERSION} .

echo "=== 导出镜像 ==="
docker save -o images.tar \
  myapp:${VERSION} \
  mysql:8.0 \
  redis:7-alpine

echo "=== 打包部署文件 ==="
mkdir -p ${PACKAGE_NAME}
mv images.tar ${PACKAGE_NAME}/
cp docker-compose.yml ${PACKAGE_NAME}/
cp .env.example ${PACKAGE_NAME}/.env

tar -czvf ${PACKAGE_NAME}.tar.gz ${PACKAGE_NAME}
rm -rf ${PACKAGE_NAME}

echo "=== 完成 ==="
echo "部署包: ${PACKAGE_NAME}.tar.gz"
ls -lh ${PACKAGE_NAME}.tar.gz
```

**步骤2：部署到生产服务器**

```bash
#!/bin/bash
# deploy.sh

echo "=== 解压部署包 ==="
tar -xzvf myapp-*-offline.tar.gz
cd myapp-*-offline

echo "=== 导入镜像 ==="
docker load -i images.tar

echo "=== 停止旧服务 ==="
docker-compose down 2>/dev/null || true

echo "=== 启动新服务 ==="
docker-compose up -d

echo "=== 等待服务启动 ==="
sleep 10

echo "=== 检查服务状态 ==="
docker-compose ps

echo "=== 健康检查 ==="
curl -s http://localhost:8080/actuator/health | grep -q "UP" && \
  echo "部署成功!" || echo "部署失败，请检查日志"
```

#### 离线部署方案选择

| 场景 | 推荐方案 |
|------|---------|
| 临时部署/测试 | 镜像导出/导入 |
| 长期生产环境 | 搭建 Harbor 私有仓库 |
| 多个项目/团队 | Harbor + CI/CD 集成 |

---

## 第七章：常见问题与解决方案

### 7.1 网络问题

**问题：拉取镜像超时**
```
Error response from daemon: Get "https://registry-1.docker.io/v2/": net/http: request canceled
```

**解决**：配置镜像加速（见 1.4 节）

---

**问题：容器之间无法通信**

**解决**：使用 Docker 网络
```yaml
# docker-compose.yml
services:
  app:
    networks:
      - my-network
  db:
    networks:
      - my-network

networks:
  my-network:
```

容器之间用服务名访问：`jdbc:mysql://db:3306/mydb`

### 7.2 端口问题

**问题：端口被占用**
```
Error: Bind for 0.0.0.0:8080 failed: port is already allocated
```

**解决**：
```bash
# 查看占用端口的进程
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# 使用其他端口
docker run -p 8081:8080 myapp
```

### 7.3 存储问题

**问题：容器删除后数据丢失**

**解决**：使用数据卷
```bash
docker run -v /my/data:/var/lib/mysql mysql
```

---

**问题：磁盘空间不足**
```
no space left on device
```

**解决**：
```bash
# 查看 Docker 磁盘使用
docker system df

# 清理未使用的资源
docker system prune -a

# 清理未使用的数据卷
docker volume prune
```

### 7.4 权限问题

**问题：Permission denied**

**解决**：
```bash
# Linux 上将用户加入 docker 组
sudo usermod -aG docker $USER
# 重新登录

# 或者使用 sudo
sudo docker ps
```

---

**问题：容器内无法写入挂载目录**

**解决**：
```bash
# 检查目录权限
ls -la /my/data

# 修改权限
chmod 777 /my/data
# 或者
chown 1000:1000 /my/data
```

### 7.5 构建问题

**问题：Dockerfile 构建失败**

**排查步骤**：
```bash
# 1. 检查 Dockerfile 语法
docker build -t test .

# 2. 查看构建日志
docker build -t test . 2>&1 | tee build.log

# 3. 分步调试
# 在失败的那一行前加一个 RUN ls -la 看看文件状态
```

---

**问题：构建缓存问题**

**解决**：
```bash
# 不使用缓存重新构建
docker build --no-cache -t myapp .
```

### 7.6 容器运行问题

**问题：容器启动后立即退出**

**排查**：
```bash
# 查看退出原因
docker logs 容器名

# 查看容器详情
docker inspect 容器名

# 交互式运行调试
docker run -it myapp sh
```

**常见原因**：
- CMD 命令执行完就退出了（应该是一个持续运行的进程）
- 应用启动失败
- 配置错误

---

**问题：容器内无法访问外网**

**解决**：
```bash
# 检查 DNS
docker run --rm alpine ping -c 3 google.com

# 指定 DNS
docker run --dns 8.8.8.8 myapp
```

---

## 第八章：工作中的最佳实践

### 8.1 镜像最佳实践

```dockerfile
# ✅ 好的做法

# 1. 使用具体版本，不用 latest
FROM openjdk:21-jdk-slim  # ✅
FROM openjdk:latest       # ❌

# 2. 使用轻量级基础镜像
FROM alpine        # ~5MB
FROM debian-slim   # ~50MB
FROM ubuntu        # ~70MB

# 3. 合并 RUN 命令，减少层数
RUN apt-get update \
    && apt-get install -y curl \
    && rm -rf /var/lib/apt/lists/*

# 4. 把不常变的放前面（利用缓存）
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package

# 5. 使用 .dockerignore 排除无用文件

# 6. 不要在镜像中存储敏感信息
# 密码、密钥通过环境变量传入
```

### 8.2 安全最佳实践

```dockerfile
# 1. 使用非 root 用户运行
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 2. 不要硬编码敏感信息
ENV DB_PASSWORD=123456  # ❌
# 运行时传入：docker run -e DB_PASSWORD=xxx

# 3. 使用官方镜像或可信来源

# 4. 定期更新基础镜像
```

### 8.3 日志最佳实践

```bash
# 应用日志输出到 stdout/stderr
# Docker 会自动收集

# 查看日志
docker logs myapp

# 配置日志驱动
docker run --log-driver json-file \
  --log-opt max-size=10m \
  --log-opt max-file=3 \
  myapp
```

### 8.4 资源限制

```bash
# 限制内存
docker run -m 512m myapp

# 限制 CPU
docker run --cpus=1.5 myapp

# docker-compose 中
services:
  app:
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '1.5'
```

### 8.5 健康检查

```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
```

```yaml
# docker-compose.yml
services:
  app:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 60s
```

### 8.6 常用命令别名

添加到 `~/.bashrc` 或 `~/.zshrc`：

```bash
# Docker 别名
alias d='docker'
alias dc='docker-compose'
alias dps='docker ps'
alias dpsa='docker ps -a'
alias di='docker images'
alias dex='docker exec -it'
alias dlog='docker logs -f'
alias dstop='docker stop $(docker ps -q)'
alias drm='docker rm $(docker ps -aq)'
alias dprune='docker system prune -a'
```

---

## 总结

### 学习路径

```
第1周：理解概念 + 基本命令
  - docker run/ps/stop/rm
  - docker pull/images/rmi
  - 端口映射、数据卷

第2周：Dockerfile
  - 编写简单的 Dockerfile
  - 构建自己的镜像
  - 部署 Spring Boot 应用

第3周：Docker Compose
  - 多容器编排
  - 搭建开发环境
  - 网络和数据卷管理

第4周：生产实践
  - 最佳实践
  - 安全性
  - CI/CD 集成
```

### 必会命令清单

```bash
# 日常必用（5个）
docker run -d -p 8080:80 nginx
docker ps
docker logs -f 容器名
docker exec -it 容器名 sh
docker-compose up -d

# 管理镜像（3个）
docker pull 镜像名
docker images
docker build -t 镜像名 .

# 管理容器（3个）
docker stop 容器名
docker rm 容器名
docker-compose down
```

### 下一步学习

1. **Kubernetes**：容器编排平台，管理大规模容器
2. **Docker Swarm**：Docker 原生集群管理
3. **Harbor**：企业级镜像仓库
4. **监控**：Prometheus + Grafana 监控容器

---

**文档版本**: v1.0
**最后更新**: 2025-11-18
**作者**: Claude DevOps Course

**恭喜！** 🎉 你已经掌握了 Docker 的基础知识，可以在工作中使用 Docker 部署应用了！

# ========================================
# Multi-stage Dockerfile for Spring Boot Application
# ========================================

# ==================== 阶段1：构建阶段 ====================
FROM gradle:8.5-jdk21 AS builder

# 设置工作目录
WORKDIR /app

# 复制 Gradle 配置文件（利用 Docker 缓存）
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# 下载依赖（单独一层，利用缓存）
RUN gradle dependencies --no-daemon || true

# 复制源代码
COPY src ./src

# 构建应用（跳过测试，因为 CI/CD 已经运行过测试）
RUN gradle clean build -x test --no-daemon --stacktrace

# 解压 JAR 文件以利用分层（Spring Boot 2.3+）
WORKDIR /app/build/libs
RUN java -Djarmode=layertools -jar *.jar extract

# ==================== 阶段2：运行阶段 ====================
FROM eclipse-temurin:21-jre-alpine

# 维护者信息
LABEL maintainer="DevOps Course Team <devops@example.com>"
LABEL version="1.0"
LABEL description="Claude DevOps Course - Spring Boot Application"

# 构建参数
ARG BUILD_NUMBER=unknown
ARG GIT_COMMIT=unknown
ARG BUILD_DATE=unknown

# 环境变量
ENV BUILD_NUMBER=${BUILD_NUMBER} \
    GIT_COMMIT=${GIT_COMMIT} \
    BUILD_DATE=${BUILD_DATE} \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE="prod"

# 标签（记录构建信息）
LABEL build.number="${BUILD_NUMBER}"
LABEL build.git-commit="${GIT_COMMIT}"
LABEL build.date="${BUILD_DATE}"

# 创建应用用户（安全最佳实践：不使用 root 用户运行应用）
RUN addgroup -g 1000 appgroup && \
    adduser -D -u 1000 -G appgroup appuser

# 安装必要的工具
RUN apk add --no-cache \
    curl \
    tzdata \
    ca-certificates && \
    # 设置时区为上海
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    # 清理缓存
    rm -rf /var/cache/apk/*

# 创建应用目录
WORKDIR /app

# 创建日志目录
RUN mkdir -p /var/log/app && \
    chown -R appuser:appgroup /var/log/app

# 从构建阶段复制分层的 JAR 文件
# 这样做的好处：依赖层很少变化，可以被 Docker 缓存
COPY --from=builder --chown=appuser:appgroup /app/build/libs/dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/build/libs/spring-boot-loader/ ./
COPY --from=builder --chown=appuser:appgroup /app/build/libs/snapshot-dependencies/ ./
COPY --from=builder --chown=appuser:appgroup /app/build/libs/application/ ./

# 切换到非 root 用户
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
# 使用 exec 格式以确保 JVM 可以接收 SIGTERM 信号
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    org.springframework.boot.loader.launch.JarLauncher"]

# 默认参数（可以被 docker run 的参数覆盖）
CMD []

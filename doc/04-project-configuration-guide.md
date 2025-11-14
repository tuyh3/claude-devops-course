# 项目配置完全指南

> 📢 **写给新手的配置指南**：本文档详细讲解项目中每个配置文件的作用，以及如何根据你的实际环境进行配置。

---

## 📋 目录

- [配置文件概览](#配置文件概览)
- [快速配置（5分钟）](#快速配置5分钟)
- [详细配置说明](#详细配置说明)
- [不同场景的配置](#不同场景的配置)
- [常见问题](#常见问题)

---

## 配置文件概览

### 1. 配置文件结构

```
src/main/resources/
├── application.yml              # 主配置文件（已提交到 Git）
├── application.yml.example      # 配置模板（已提交到 Git）
├── application-local.yml        # 本地配置（不提交，你自己创建）
├── application-dev.yml          # 开发环境配置（可选）
├── application-test.yml         # 测试环境配置（自动使用 H2）
└── application-prod.yml         # 生产环境配置（可选）
```

### 2. 配置文件的作用

| 文件名 | 用途 | 是否提交 Git | 何时使用 |
|-------|------|------------|---------|
| `application.yml` | 默认配置，包含占位符 | ✅ 是 | 项目默认启动 |
| `application.yml.example` | 配置模板和说明 | ✅ 是 | 参考示例 |
| `application-local.yml` | 你的本地真实配置 | ❌ 否 | 本地开发 |
| `application-dev.yml` | 开发环境配置 | ✅ 是 | 开发服务器 |
| `application-test.yml` | 测试配置（H2数据库） | ✅ 是 | 运行测试 |
| `application-prod.yml` | 生产环境配置 | ❌ 否 | 生产部署 |

### 3. 配置文件优先级

Spring Boot 按以下顺序加载配置（后面的覆盖前面的）：

```
application.yml (最低优先级)
    ↓
application-{profile}.yml
    ↓
环境变量
    ↓
命令行参数 (最高优先级)
```

**示例**：
```bash
# 使用 application-local.yml
./gradlew bootRun --args='--spring.profiles.active=local'

# 使用环境变量覆盖密码
export ORACLE_PASSWORD=myPassword123
./gradlew bootRun
```

---

## 快速配置（5分钟）

### 场景1：我有 Oracle 数据库

**步骤1**: 复制配置模板
```bash
cd /Users/tuyh3/Desktop/WEB3/claude-devops-course
cp src/main/resources/application.yml.example src/main/resources/application-local.yml
```

**步骤2**: 修改配置文件
```bash
vim src/main/resources/application-local.yml
```

**步骤3**: 修改以下 3 个配置项：

```yaml
spring:
  datasource:
    # ① 修改数据库连接地址（如果不是 192.168.1.66/67）
    url: jdbc:oracle:thin:@//你的数据库IP:1521/你的SERVICE_NAME

    # ② 修改用户名（如果不是 TCBS）
    username: 你的用户名

    # ③ 修改密码（必改！）
    password: 你的实际密码
```

**步骤4**: 启动项目
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 场景2：我没有 Oracle 数据库

项目已经配置了 H2 内存数据库用于测试，你可以直接运行（但数据库相关功能会报错）：

```bash
# 直接启动（会尝试连接 Oracle，但不影响基础功能）
./gradlew bootRun

# 或者运行测试（使用 H2 数据库）
./gradlew test
```

**可以使用的功能**：
- ✅ 基础 REST API (`/api/hello`, `/api/health`)
- ❌ 客户管理 API (`/api/customers`) - 需要数据库

---

## 详细配置说明

### 数据库配置详解

#### 1. 数据库连接 URL

**格式说明**：
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@(DESCRIPTION=
           (ADDRESS_LIST=
             (ADDRESS=(PROTOCOL=TCP)(HOST=主机1)(PORT=端口1))
             (ADDRESS=(PROTOCOL=TCP)(HOST=主机2)(PORT=端口2))
             (LOAD_BALANCE=ON)
             (FAILOVER=ON))
           (CONNECT_DATA=
             (SERVICE_NAME=服务名)
             (FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))
```

**📝 需要修改的地方**：

| 参数 | 当前值 | 说明 | 如何获取 |
|------|-------|------|---------|
| HOST | `192.168.1.66`, `192.168.1.67` | 数据库服务器 IP | 问 DBA 或运维 |
| PORT | `1521` | Oracle 监听端口 | 通常是 1521，问 DBA |
| SERVICE_NAME | `dbpv` | Oracle 服务名 | 问 DBA |

**如何获取这些信息？**

方法1：问 DBA（数据库管理员）
```
你：我需要连接 Oracle 数据库，能给我以下信息吗？
    - 数据库 IP 地址
    - 端口（默认 1521）
    - SERVICE_NAME
    - 用户名和密码
```

方法2：查看已有的配置文件或文档
```bash
# 可能在这些地方
- 公司内部 Wiki
- Confluence 文档
- README 文件
- tnsnames.ora 文件
```

方法3：如果有 SQL Developer 连接配置，可以导出
```
SQL Developer → 连接 → 右键 → 导出 → 查看连接字符串
```

**常见连接 URL 格式**：

```yaml
# 格式1：简单格式（单机）
url: jdbc:oracle:thin:@//192.168.1.66:1521/dbpv

# 格式2：RAC 格式（双机）- 推荐
url: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.66)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.67)(PORT=1521))(LOAD_BALANCE=ON)(FAILOVER=ON))(CONNECT_DATA=(SERVICE_NAME=dbpv)))

# 格式3：SCAN 格式（RAC 推荐）
url: jdbc:oracle:thin:@//scan-host:1521/dbpv

# 格式4：SID 格式（老版本）
url: jdbc:oracle:thin:@192.168.1.66:1521:ORCL
```

#### 2. 用户名和密码

```yaml
spring:
  datasource:
    username: TCBS              # ← 修改为你的用户名
    password: your_password_here # ← 修改为你的密码
```

**📝 需要修改**：
- `username`: 你的 Oracle 用户名（本项目用的是 TCBS）
- `password`: 你的 Oracle 密码（**必须修改**）

**安全最佳实践**：

方法1：使用环境变量（推荐生产环境）
```yaml
spring:
  datasource:
    password: ${ORACLE_PASSWORD}  # 从环境变量读取
```

```bash
# 设置环境变量
export ORACLE_PASSWORD=你的真实密码
./gradlew bootRun
```

方法2：使用独立配置文件（推荐开发环境）
```bash
# 创建 application-local.yml（不提交到 Git）
cp application.yml.example application-local.yml
vim application-local.yml  # 修改密码

# 启动时指定 profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

方法3：使用 Spring Cloud Config Server（推荐微服务）
```yaml
spring:
  cloud:
    config:
      uri: http://config-server:8888
```

#### 3. 连接池配置

```yaml
spring:
  datasource:
    hikari:
      pool-name: OracleHikariCP       # 连接池名称
      minimum-idle: 5                  # 最小空闲连接数
      maximum-pool-size: 20            # 最大连接数
      max-lifetime: 1800000            # 连接最大生命周期（30分钟）
      connection-timeout: 30000        # 连接超时时间（30秒）
      idle-timeout: 600000             # 空闲超时时间（10分钟）
      connection-test-query: SELECT 1 FROM DUAL
```

**📝 什么时候需要修改？**

| 参数 | 默认值 | 何时修改 | 修改建议 |
|------|-------|---------|---------|
| `minimum-idle` | 5 | 高并发应用 | 增加到 10-20 |
| `maximum-pool-size` | 20 | 高并发应用 | 增加到 50-100 |
| `connection-timeout` | 30秒 | 网络慢 | 增加到 60秒 |

**常见场景**：

```yaml
# 场景1：开发环境（默认值即可）
hikari:
  minimum-idle: 5
  maximum-pool-size: 20

# 场景2：生产环境（高并发）
hikari:
  minimum-idle: 20
  maximum-pool-size: 100
  max-lifetime: 1800000

# 场景3：测试环境（节省资源）
hikari:
  minimum-idle: 2
  maximum-pool-size: 10
```

### JPA/Hibernate 配置详解

```yaml
spring:
  jpa:
    database-platform: org.hibernate.dialect.Oracle12cDialect
    show-sql: true              # 是否打印 SQL
    hibernate:
      ddl-auto: validate        # 数据库表结构验证策略
```

**📝 `show-sql` 配置**：

| 环境 | 推荐值 | 说明 |
|-----|-------|------|
| 开发 | `true` | 方便调试，查看执行的 SQL |
| 测试 | `false` | 减少日志输出 |
| 生产 | `false` | **必须关闭**，避免日志过多 |

**📝 `ddl-auto` 配置**：

| 值 | 作用 | 使用场景 | 风险 |
|----|------|---------|------|
| `validate` | 仅验证表结构 | ⭐ 生产环境 | ✅ 安全 |
| `update` | 自动更新表结构 | 开发环境 | ⚠️ 可能误改表 |
| `create` | 每次启动重建表 | 测试环境 | ❌ 删除数据 |
| `create-drop` | 启动创建，关闭删除 | 单元测试 | ❌ 删除数据 |
| `none` | 不做任何操作 | 生产环境 | ✅ 最安全 |

**推荐配置**：

```yaml
# 开发环境 - application-dev.yml
spring:
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate  # 或 update（谨慎！）

# 生产环境 - application-prod.yml
spring:
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate  # 或 none
```

### 服务器配置

```yaml
server:
  port: 8080                    # 服务端口
  servlet:
    context-path: /             # 应用上下文路径
```

**📝 何时需要修改？**

| 场景 | 修改配置 | 示例 |
|------|---------|-----|
| 端口被占用 | `port: 8081` | 改为其他可用端口 |
| 部署多个实例 | `port: ${PORT:8080}` | 使用环境变量 |
| 添加统一前缀 | `context-path: /api` | 所有接口加 /api 前缀 |

**常见配置**：

```yaml
# 场景1：开发环境（默认）
server:
  port: 8080
  servlet:
    context-path: /

# 场景2：多实例部署
server:
  port: ${SERVER_PORT:8080}  # 从环境变量读取

# 场景3：API 网关后面
server:
  port: 8080
  servlet:
    context-path: /devops-course
```

### 日志配置

```yaml
logging:
  level:
    root: INFO                      # 根日志级别
    com.devops.course: DEBUG        # 应用日志级别
    org.hibernate.SQL: DEBUG        # SQL 日志
```

**📝 日志级别说明**：

| 级别 | 含义 | 使用场景 |
|-----|------|---------|
| `TRACE` | 最详细 | 排查复杂问题 |
| `DEBUG` | 调试信息 | 开发环境 |
| `INFO` | 一般信息 | ⭐ 生产环境 |
| `WARN` | 警告信息 | 生产环境 |
| `ERROR` | 错误信息 | 所有环境 |

**推荐配置**：

```yaml
# 开发环境
logging:
  level:
    root: INFO
    com.devops.course: DEBUG
    org.hibernate.SQL: DEBUG

# 生产环境
logging:
  level:
    root: WARN
    com.devops.course: INFO
    org.hibernate.SQL: WARN
```

---

## 不同场景的配置

### 场景1：本地开发（有 Oracle 数据库）

**文件**: `application-local.yml`

```yaml
spring:
  application:
    name: claude-devops-course

  datasource:
    url: jdbc:oracle:thin:@//192.168.1.66:1521/dbpv
    username: TCBS
    password: 你的实际密码  # ← 修改这里
    driver-class-name: oracle.jdbc.OracleDriver

    hikari:
      minimum-idle: 5
      maximum-pool-size: 20

  jpa:
    show-sql: true  # 开发环境打开
    hibernate:
      ddl-auto: validate

server:
  port: 8080

logging:
  level:
    root: INFO
    com.devops.course: DEBUG
    org.hibernate.SQL: DEBUG
```

**启动命令**：
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 场景2：本地开发（无数据库，只测试基础功能）

**文件**: 使用默认 `application.yml`

```bash
# 直接启动（数据库功能会报错，但不影响基础 API）
./gradlew bootRun

# 测试基础 API
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/health
```

### 场景3：运行测试

**文件**: `application-test.yml`（已自动配置 H2）

```bash
# 运行所有测试
./gradlew test

# 运行单个测试
./gradlew test --tests "HelloControllerTest"
```

### 场景4：生产环境部署

**文件**: `application-prod.yml`（不提交到 Git）

```yaml
spring:
  datasource:
    url: ${ORACLE_URL}              # 从环境变量读取
    username: ${ORACLE_USERNAME}
    password: ${ORACLE_PASSWORD}

    hikari:
      minimum-idle: 20              # 生产环境增加连接数
      maximum-pool-size: 100

  jpa:
    show-sql: false                 # 生产环境关闭 SQL 日志
    hibernate:
      ddl-auto: validate            # 生产环境只验证

server:
  port: ${SERVER_PORT:8080}

logging:
  level:
    root: WARN                      # 生产环境提高日志级别
    com.devops.course: INFO
    org.hibernate.SQL: WARN
```

**部署脚本**：
```bash
#!/bin/bash
# deploy.sh

export ORACLE_URL="jdbc:oracle:thin:@//prod-db:1521/dbpv"
export ORACLE_USERNAME="TCBS"
export ORACLE_PASSWORD="生产环境密码"
export SERVER_PORT=8080

java -jar -Dspring.profiles.active=prod build/libs/claude-devops-course-1.0.0.jar
```

### 场景5：Docker 容器部署

**文件**: `docker-compose.yml`

```yaml
version: '3.8'
services:
  app:
    image: claude-devops-course:1.0.0
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - ORACLE_URL=jdbc:oracle:thin:@//db-server:1521/dbpv
      - ORACLE_USERNAME=TCBS
      - ORACLE_PASSWORD=your_password
      - SERVER_PORT=8080
    ports:
      - "8080:8080"
```

---

## 常见问题

### Q1: 如何确认我的配置是否正确？

**方法1**: 启动应用查看日志

```bash
./gradlew bootRun --args='--spring.profiles.active=local'

# 查看日志中的关键信息
# ✅ 成功连接：
# HikariPool-1 - Starting...
# HikariPool-1 - Start completed.

# ❌ 连接失败：
# Unable to acquire JDBC Connection
# ORA-01017: invalid username/password
```

**方法2**: 测试数据库连接

创建测试代码：
```java
@Autowired
private DataSource dataSource;

@GetMapping("/test-db")
public String testDatabase() {
    try (Connection conn = dataSource.getConnection()) {
        return "数据库连接成功！";
    } catch (Exception e) {
        return "数据库连接失败：" + e.getMessage();
    }
}
```

访问：`http://localhost:8080/api/test-db`

### Q2: 密码错误，如何确认？

```bash
# 使用 SQL*Plus 测试（如果有安装）
sqlplus TCBS/你的密码@192.168.1.66:1521/dbpv

# 或使用 SQL Developer 连接测试
```

### Q3: 不知道数据库 IP 和 SERVICE_NAME 怎么办？

**方法1**: 问 DBA

**方法2**: 查看 tnsnames.ora 文件
```bash
# 通常在
# Linux: $ORACLE_HOME/network/admin/tnsnames.ora
# Windows: C:\app\oracle\product\19.0.0\dbhome\network\admin\tnsnames.ora

cat $ORACLE_HOME/network/admin/tnsnames.ora
```

示例内容：
```
DBPV =
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = 192.168.1.66)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = dbpv)
    )
  )
```

**方法3**: 使用 lsnrctl 查看
```bash
lsnrctl status
```

### Q4: 端口 8080 被占用怎么办？

**方法1**: 修改端口
```yaml
# application-local.yml
server:
  port: 8081
```

**方法2**: 找到占用进程并停止
```bash
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Q5: 如何切换不同的配置文件？

```bash
# 方法1：命令行参数
./gradlew bootRun --args='--spring.profiles.active=local'

# 方法2：环境变量
export SPRING_PROFILES_ACTIVE=local
./gradlew bootRun

# 方法3：IDEA 运行配置
# Run → Edit Configurations → Program arguments
# 填入：--spring.profiles.active=local
```

### Q6: 多个 profile 可以同时使用吗？

可以！使用逗号分隔：

```bash
./gradlew bootRun --args='--spring.profiles.active=local,debug'
```

配置会按顺序合并：
```
application.yml → application-local.yml → application-debug.yml
```

### Q7: 如何在 IDEA 中配置？

**步骤1**: 打开 Run Configuration
```
Run → Edit Configurations → Application → Main
```

**步骤2**: 配置 VM Options 或 Program Arguments
```
Program arguments: --spring.profiles.active=local
```

**步骤3**: 配置环境变量（可选）
```
Environment variables: ORACLE_PASSWORD=your_password
```

### Q8: 生产环境密码如何管理？

**推荐方案**：

1. **使用环境变量**（最简单）
```bash
export ORACLE_PASSWORD=your_password
java -jar app.jar
```

2. **使用 Vault**（企业推荐）
```yaml
spring:
  cloud:
    vault:
      host: vault-server
      port: 8200
      authentication: TOKEN
```

3. **使用 Kubernetes Secrets**（K8s 环境）
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: db-secret
data:
  password: base64编码的密码
```

---

## 配置检查清单

在启动项目前，请检查：

- [ ] 已复制 `application.yml.example` 为 `application-local.yml`
- [ ] 已修改数据库 IP 地址（如果需要）
- [ ] 已修改数据库用户名（如果需要）
- [ ] 已修改数据库密码（**必须**）
- [ ] 已确认数据库可以连接（ping 通，端口开放）
- [ ] 已确认 `application-local.yml` 没有提交到 Git
- [ ] 已设置正确的 Spring Profile

---

## 完整配置示例

### 示例1：完整的本地开发配置

**文件**: `application-local.yml`

```yaml
spring:
  application:
    name: claude-devops-course

  # Oracle RAC Database Configuration
  datasource:
    url: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.66)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.67)(PORT=1521))(LOAD_BALANCE=ON)(FAILOVER=ON))(CONNECT_DATA=(SERVICE_NAME=dbpv)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))
    username: TCBS
    password: MySecretPassword123  # ← 你的实际密码
    driver-class-name: oracle.jdbc.OracleDriver

    hikari:
      pool-name: OracleHikariCP
      minimum-idle: 5
      maximum-pool-size: 20
      max-lifetime: 1800000
      connection-timeout: 30000
      idle-timeout: 600000
      connection-test-query: SELECT 1 FROM DUAL

  # JPA Configuration
  jpa:
    database-platform: org.hibernate.dialect.Oracle12cDialect
    show-sql: true
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
        implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true

# Server Configuration
server:
  port: 8080
  servlet:
    context-path: /

# Logging Configuration
logging:
  level:
    root: INFO
    com.devops.course: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

# Development Tools
spring.devtools:
  restart:
    enabled: true
  livereload:
    enabled: true
```

---

## 总结

### 关键要点

1. **配置文件不要提交真实密码到 Git**
2. **使用 `application-local.yml` 存储本地配置**
3. **生产环境使用环境变量**
4. **根据环境选择合适的 profile**

### 推荐工作流程

```
1. 克隆项目
   ↓
2. 复制 application.yml.example → application-local.yml
   ↓
3. 修改 application-local.yml 中的数据库配置
   ↓
4. 启动: ./gradlew bootRun --args='--spring.profiles.active=local'
   ↓
5. 测试: curl http://localhost:8080/api/hello
```

---

**文档版本**: v1.0
**最后更新**: 2025-11-14
**维护者**: DevOps Course Team

如有疑问，请参考其他文档或提交 Issue。

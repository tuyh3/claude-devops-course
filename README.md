# Claude DevOps Course

> 🎓 一个用 Claude Code 辅助学习各种语言、中间件和数据库的实战项目

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.2.0-blue.svg)](https://gradle.org/)
[![Oracle](https://img.shields.io/badge/Oracle-19c%20RAC-red.svg)](https://www.oracle.com/database/)

## 📖 项目简介

本项目是一个完整的企业级应用学习项目，集成了业界主流的技术栈，专注于：

- 🏗️ **现代化架构**：Spring Boot 3.x + Java 21
- 💾 **企业级数据库**：Oracle 19c RAC（高可用集群）
- 📚 **完善的文档**：详细的教程和最佳实践
- 🔧 **实战案例**：基于真实的电信业务场景

### 业务场景

本项目模拟了一个**电信运营商业务系统**，包含：

- **TCBS (Telecom Business System)** - 电信业务系统
  - 客户管理（CRM）
  - 产品管理
  - 账单系统
  - 使用记录

- **TCOA (Telecom Open API)** - 能力开放平台
  - API 应用管理
  - API 调用日志

## ⚡ 快速开始（5分钟）

### 前置要求

确保你已安装：

- ✅ Java 21 或更高版本
- ✅ Git

**不需要安装：**
- ❌ Gradle（项目自带 Gradle Wrapper）
- ❌ Maven

### 1. 克隆项目

```bash
git clone <repository-url>
cd claude-devops-course
```

### 2. 配置数据库（可选）

如果你有Oracle数据库，配置连接信息：

```bash
# 复制配置模板
cp src/main/resources/application.yml.example src/main/resources/application-local.yml

# 编辑配置文件，填入你的数据库信息
vim src/main/resources/application-local.yml
```

**如果没有数据库**：项目可以在没有数据库的情况下启动，只是数据库相关功能会报错。

### 3. 构建项目

```bash
# 首次构建（会自动下载 Gradle 9.2.0）
./gradlew clean build
```

### 4. 运行项目

```bash
# 启动应用
./gradlew bootRun

# 或使用本地配置启动
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 5. 访问应用

打开浏览器访问：

- 主页：http://localhost:8080
- API端点：http://localhost:8080/api/hello
- 健康检查：http://localhost:8080/api/health

## 🛠️ 技术栈

### 当前使用版本

根据业界主流技术公司的技术选型：

| 技术栈 | 版本 | 说明 |
|--------|------|------|
| **核心框架** |
| Java (LTS) | 21 | ⭐ 项目使用 |
| Spring Boot | 3.3.8 | ⭐ 项目使用 |
| Gradle | 9.2.0 | ⭐ 项目使用 |
| **数据库** |
| Oracle | 19c RAC | ⭐ 项目使用 |
| MySQL | 8.4 LTS | 规划中 |
| PostgreSQL | 16 | 规划中 |
| Redis | 7.0 LTS | 规划中 |
| **中间件** |
| Kafka | 4.0.1 | 规划中 |
| Nginx | 1.29.3 | 规划中 |
| **监控工具** |
| Prometheus | 3.5.0 (LTS) | 规划中 |
| Grafana | v12.x | 规划中 |
| Zabbix | 7.0 LTS | 规划中 |
| ELK Stack | 9.2.1 | 规划中 |
| Filebeat | 9.2.1 | 规划中 |
| **容器与编排** |
| Docker | 24.x LTS | 规划中 |
| Kubernetes | v1.34 | 规划中 |
| **CI/CD** |
| GitLab | 18 LTS | 规划中 |
| Jenkins | 2.528.2 LTS | 规划中 |
| **IaC工具** |
| Terraform | OpenTofu 1.6.x | 规划中 |
| Ansible | AAP 2.6 | 规划中 |
| **其他语言** |
| Python | 3.11 | 规划中 |
| Go | 1.24.x (N-1) | 规划中 |

**注意：** Maven 在本课程中不使用，统一使用 Gradle。

## 📚 文档导航

### 入门文档

新手必读，按顺序阅读：

1. **[Gradle + Spring Boot 项目上手指南（Maven用户版）](doc/01-gradle-springboot-project-initialization-guide.md)**
   - 适合：熟悉 Maven 的开发者
   - 内容：Maven vs Gradle 对比、命令对照表
   - 时长：30 分钟

2. **[IntelliJ IDEA 配置 Gradle 项目完全指南](doc/02-intellij-idea-gradle-project-setup.md)**
   - 适合：IDEA 用户
   - 内容：IDEA 完整配置、调试技巧
   - 时长：20 分钟

3. **[Spring Boot 集成 Oracle 19c RAC 数据库指南](doc/03-spring-boot-oracle-rac-integration.md)**
   - 适合：需要连接 Oracle 的开发者
   - 内容：RAC 配置、JPA 使用、常见问题
   - 时长：30 分钟

### 数据库资源

- **[Oracle 数据库创建脚本](doc/oracle_dbpv_create_data.sql)**
  - 完整的表结构和测试数据
  - 适用于 Oracle 19c ASM+RAC

### 完整文档列表

查看 [文档中心](doc/README.md) 获取所有文档列表。

## 🚀 常用命令

### Gradle 命令

```bash
# 构建项目
./gradlew build

# 清理构建
./gradlew clean build

# 运行应用
./gradlew bootRun

# 运行测试
./gradlew test

# 跳过测试构建（快速构建，常用！）
./gradlew clean build -x test --stacktrace

# 查看所有任务
./gradlew tasks

# 查看依赖树
./gradlew dependencies
```

### IDEA 中使用

1. **打开项目**：`File -> Open` -> 选择项目根目录
2. **等待 Gradle 同步**：IDEA 会自动识别并同步
3. **运行应用**：点击 Main 类旁边的绿色运行按钮 ▶️

详细配置请参考：[IDEA 配置指南](doc/02-intellij-idea-gradle-project-setup.md)

## 📁 项目结构

```
claude-devops-course/
├── doc/                                    # 📚 文档目录
│   ├── README.md                           # 文档索引
│   ├── 01-gradle-springboot-...guide.md   # Gradle 上手指南
│   ├── 02-intellij-idea-...setup.md       # IDEA 配置指南
│   ├── 03-spring-boot-oracle-...md        # Oracle 集成指南
│   └── oracle_dbpv_create_data.sql        # 数据库脚本
├── src/
│   ├── main/
│   │   ├── java/com/devops/course/
│   │   │   ├── Main.java                  # 应用入口
│   │   │   ├── controller/                # REST API 控制器
│   │   │   ├── service/                   # 业务逻辑层
│   │   │   ├── repository/                # 数据访问层
│   │   │   └── entity/                    # JPA 实体类
│   │   └── resources/
│   │       ├── application.yml            # 配置文件
│   │       └── application.yml.example    # 配置模板
│   └── test/                              # 测试代码
├── build.gradle                           # Gradle 构建脚本
├── settings.gradle                        # Gradle 设置
├── CLAUDE.md                              # Claude Code 项目指南
└── README.md                              # 本文件
```

## 🎯 使用 Claude Code 开发

本项目针对 Claude Code 进行了优化：

### 1. 查看项目文档

```bash
# 阅读 Claude Code 专用文档
cat CLAUDE.md
```

### 2. 常见任务

对 Claude Code 说：

```
"帮我运行这个Spring Boot项目"
"添加一个查询客户的REST API"
"解释一下Oracle RAC的连接配置"
"运行测试并修复失败的测试"
```

### 3. 文档齐全

- ✅ 所有配置都有详细注释
- ✅ 每个技术选型都有文档说明
- ✅ 常见问题都有解决方案
- ✅ 代码示例完整可运行

## 🔧 故障排查

### 构建失败

```bash
# 1. 清理缓存
./gradlew clean

# 2. 刷新依赖
./gradlew --refresh-dependencies

# 3. 重新构建
./gradlew build
```

### 找不到主类

```bash
# 清理并重新构建
./gradlew clean build
```

### Oracle 连接失败

1. 检查网络连接：`ping 192.168.1.66`
2. 检查配置文件：`application.yml` 或 `application-local.yml`
3. 查看详细文档：`doc/03-spring-boot-oracle-rac-integration.md`

### Gradle Wrapper 权限问题

```bash
# 添加执行权限
chmod +x gradlew
```

### 更多问题

查看各个文档中的"常见问题"章节：
- [Gradle 常见问题](doc/01-gradle-springboot-project-initialization-guide.md#常见问题排查)
- [IDEA 常见问题](doc/02-intellij-idea-gradle-project-setup.md#常见问题)
- [Oracle 常见问题](doc/03-spring-boot-oracle-rac-integration.md#常见问题)

## 🤝 贡献指南

欢迎贡献！请：

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目仅用于学习目的。

## 💬 联系方式

如有问题，请：

1. 查看 [文档目录](doc/README.md)
2. 提交 Issue
3. 或联系项目维护团队

---

**项目版本**: 1.0.0
**最后更新**: 2025-11-13
**维护团队**: DevOps Course Team

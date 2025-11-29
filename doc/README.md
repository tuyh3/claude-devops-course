# 文档中心

本目录包含了 Claude DevOps Course 项目的各类文档和教程。

## 📚 文档索引

### 入门指南

1. **[Gradle + Spring Boot 大型项目上手指南（Maven 用户版）](01-gradle-springboot-project-initialization-guide.md)**
   - 🎯 **适用场景**: 当你拿到一个公司的大型 Gradle + Spring Boot 项目时
   - 👥 **目标读者**: 熟悉 Maven 的开发者
   - 📖 **内容概要**:
     - Maven vs Gradle 核心对比
     - 命令对照表（超级实用！）
     - 配置文件映射（pom.xml vs build.gradle）
     - 项目结构分析
     - 运行和调试指南
     - 常见问题排查
     - 学习路径建议
   - ⏱️ **阅读时长**: 约 30 分钟
   - 💡 **建议**: Maven 用户必读，基于对比学习

2. **[IntelliJ IDEA 配置 Gradle 项目完全指南](02-intellij-idea-gradle-project-setup.md)**
   - 🎯 **适用场景**: 在 IntelliJ IDEA 中配置和使用 Gradle 项目
   - 👥 **目标读者**: IDEA 用户，特别是习惯 Maven 项目的开发者
   - 📖 **内容概要**:
     - 导入 Gradle 项目的正确方式
     - JDK 和 Gradle 配置详解
     - 推荐的 IDEA 设置
     - 运行、调试和测试
     - 常用 IDEA 操作
     - 常见问题完全排查
   - ⏱️ **阅读时长**: 约 20 分钟
   - 💡 **建议**: 配置 IDEA 必读

3. **[Spring Boot 集成 Oracle 19c RAC 数据库指南](03-spring-boot-oracle-rac-integration.md)**
   - 🎯 **适用场景**: Spring Boot 项目连接 Oracle 19c RAC 数据库
   - 👥 **目标读者**: 需要使用 Oracle 数据库的开发者
   - 📖 **内容概要**:
     - Oracle RAC 架构理解
     - 添加 Oracle JDBC 驱动和依赖
     - 配置数据源和连接池（HikariCP）
     - RAC 高可用连接配置
     - 创建 JPA Entity 和 Repository
     - 电信业务数据库实战示例
     - 常见问题完全排查
   - ⏱️ **阅读时长**: 约 30 分钟
   - 💡 **建议**: 使用 Oracle 数据库必读

4. **[项目配置完全指南](04-project-configuration-guide.md)** ⭐ 新手必读
   - 🎯 **适用场景**: 刚拿到项目，不知道如何修改配置文件
   - 👥 **目标读者**: 所有开发者，特别是新加入项目的成员
   - 📖 **内容概要**:
     - 配置文件结构和作用详解
     - 数据库连接配置详细说明（IP、端口、用户名、密码）
     - 如何获取配置信息（问 DBA、查看文档）
     - 不同场景的配置示例（有数据库、无数据库、测试、生产）
     - 配置文件优先级和 Profile 使用
     - 密码管理最佳实践
     - 完整的配置检查清单
     - 常见问题和解决方案
   - ⏱️ **阅读时长**: 约 15 分钟
   - 💡 **建议**: ⭐ 强烈推荐！配置项目前必读，可以避免 90% 的配置错误

5. **[Spring Boot 零基础入门指南](05-spring-boot-beginner-guide.md)** 🔥 完全零基础必读
   - 🎯 **适用场景**: 刚学完 Java 基础和数据库基础，完全不懂 Web 开发
   - 👥 **目标读者**: 只会 Java（类、对象、方法）和 SQL（增删改查）的初学者
   - 📖 **内容概要**:
     - **第0章**: Web 开发基础概念（客户端/服务器、HTTP、JSON、框架） ⭐ 新增
     - **第1章**: Spring Boot 核心概念（IoC、依赖注入、自动配置）
     - **第2章**: 项目三层架构详解（Controller-Service-Repository）
     - **第3章**: 10+ 个核心注解详细讲解（@SpringBootApplication, @RestController等）
     - **第4章**: 读懂现有代码（逐行解析项目代码）
     - **第5章**: 自己动手写代码（完整 CRUD 实战）
     - **第6章**: 调试与测试（IDEA 调试、日志、Postman）
     - **第7章**: 常见问题与解决方案（错误排查大全）
     - **第8章**: 学习资源与下一步（学习路径建议）
   - ⏱️ **阅读时长**: 约 120-150 分钟（含第0章）
   - 💡 **建议**: 🔥 完全零基础必读！从 Web 概念开始，手把手教学，通俗易懂

---

## 📚 实战指南系列

**学习路径**：学习指南（理论） → 实战指南（实践）

完成上述入门指南后，建议按顺序阅读以下三份实战指南：

6. **[实战阅读代码指南](reading-code-guide.md)** 📖 Phase 1: 会读代码
   - 🎯 **适用场景**: 刚接手一个项目，需要快速了解代码结构和定位问题
   - 👥 **目标读者**: 掌握 Java 基础 + Spring Boot 基础的开发者
   - 📖 **内容概要**:
     - **第1章**: 5分钟快速了解项目（项目结构、配置文件、启动流程）
     - **第2章**: 从界面功能定位到代码（HTTP 请求 → Controller → Service → Repository）
     - **第3章**: 使用日志定位问题（异常堆栈、日志级别、日志分析）
     - **第4章**: 使用断点调试（IDEA 调试技巧、变量查看、调用栈）
     - **第5章**: 阅读复杂代码的技巧（Lambda、Stream、注解）
     - **第6章**: 完整案例：定位一个 Bug（从用户反馈到问题根因）
   - ⏱️ **阅读时长**: 约 50-70 分钟
   - 💡 **建议**: 先学会读代码，才能写好代码

7. **[实战编写代码指南](writing-code-guide.md)** ✍️ Phase 2: 会写代码
   - 🎯 **适用场景**: 需要修复 Bug 或开发新功能
   - 👥 **目标读者**: 掌握代码阅读，需要动手编写代码的开发者
   - 📖 **内容概要**:
     - **第1章**: 完整的 Bug 修复流程（定位 → 修复 → 测试 → 验证）
     - **第2章**: 开发新功能的完整流程（需求分析 → 设计 → 编码 → 测试）
     - **第3章**: 单元测试实战（JUnit 5、Mockito、Spring Boot 测试）
     - **第4章**: AI 辅助开发实战（Claude Code 使用、学习新技术、生成代码）
     - **第5章**: Git 版本控制基础（分支管理、提交代码、合并分支）
     - **第6章**: 代码质量提升（命名规范、异常处理、日志记录、性能优化）
     - **第7章**: 常见问题与解决方案
     - **第8章**: 学习资源与下一步
   - ⏱️ **阅读时长**: 约 60-90 分钟
   - 💡 **建议**: 从修复 Bug 开始，逐步掌握开发新功能的能力

8. **[团队协作指南](team-collaboration-guide.md)** 🤝 Phase 3: 会协作
   - 🎯 **适用场景**: 与团队成员协作开发同一个项目
   - 👥 **目标读者**: 掌握个人开发能力，需要融入团队协作的开发者
   - 📖 **内容概要**:
     - **第1章**: Git Flow 工作流（分支策略、发布流程、紧急修复）
     - **第2章**: Pull Request / Merge Request 流程（创建 PR、Code Review）
     - **第3章**: 持续集成/持续部署（CI/CD 配置、自动化测试、自动化部署）
     - **第4章**: 环境管理（开发/测试/生产环境、配置管理、数据库迁移）
     - **第5章**: 监控与日志（应用监控、日志管理、告警机制、故障排查）
     - **第6章**: 团队沟通与协作（需求沟通、技术评审、问题升级、知识分享）
     - **第7章**: 最佳实践总结
     - **第8章**: 常见问题与解决方案
   - ⏱️ **阅读时长**: 约 60-80 分钟
   - 💡 **建议**: 团队协作是职业发展的关键能力

**实战指南学习路径**：
```
入门指南（05-spring-boot-beginner-guide.md）
    ↓ 掌握理论基础
实战阅读代码指南（reading-code-guide.md）
    ↓ 学会读懂代码
实战编写代码指南（writing-code-guide.md）
    ↓ 学会修复 Bug 和开发新功能
团队协作指南（team-collaboration-guide.md）
    ↓ 学会团队协作
成为专业开发者 🚀
```

---

## 🚀 运维与监控系列

9. **[Docker 零基础入门指南](06-docker-beginner-guide.md)** 🐳 完全零基础必读
   - 🎯 **适用场景**: 完全不懂 Docker，需要从零开始学习
   - 👥 **目标读者**: Docker 零基础的开发者
   - 📖 **内容概要**:
     - Docker 是什么（用生活中的例子讲解）
     - Docker 核心概念（镜像、容器、仓库）
     - Docker 基础命令速查
     - Dockerfile 编写实战
     - Docker Compose 多容器编排
     - 网络和数据卷管理
     - 实战案例：部署 Spring Boot 应用
     - 常见问题与解决方案
   - ⏱️ **阅读时长**: 约 60-80 分钟
   - 💡 **建议**: 🔥 Docker 零基础必读！通俗易懂，手把手教学

10. **[ELK Stack 零基础入门指南](09-elk-beginner-guide.md)** 🔍 新手必读
    - 🎯 **适用场景**: 完全不懂 ELK，需要从零开始学习日志管理
    - 👥 **目标读者**: ELK 零基础的开发者
    - 📖 **内容概要**:
      - **第1章**: 什么是 ELK（用餐馆类比通俗讲解）
      - **第2章**: ELK 三个组件详解（Elasticsearch、Logstash、Kibana）
      - **第3章**: ELK 完整工作流程（数据流向、时序图）
      - **第4章**: 实战：启动你的第一个 ELK 环境
      - **第5章**: Kibana 使用入门（搜索、可视化、仪表板）
      - **第6章**: 新手常见问题（完整排查清单）
      - **第7章**: 速查手册（命令、查询语法、配置文件）
    - ⏱️ **阅读时长**: 约 90-120 分钟
    - 💡 **建议**: 🔥 ELK 零基础必读！从概念到实战，一篇搞定

11. **[ELK Stack 生产环境部署指南](07-elk-deployment-guide.md)** 📦 进阶必读
    - 🎯 **适用场景**: 需要在生产环境部署 ELK Stack
    - 👥 **目标读者**: 掌握 ELK 基础，需要实际部署的运维或开发人员
    - 📖 **内容概要**:
      - ELK 架构设计（独立部署的优势）
      - ELK Stack 快速部署（Docker Compose）
      - 生产环境配置优化（内存、持久化、安全）
      - Spring Boot 应用集成（Logback 配置）
      - Kibana 使用指南（查询、可视化、仪表板）
      - 运维管理（索引管理、备份、性能监控）
      - 故障排查（完整问题诊断流程）
    - ⏱️ **阅读时长**: 约 60-90 分钟
    - 💡 **建议**: 先学习 09-elk-beginner-guide.md 掌握基础概念

12. **[日志管理最佳实践](08-logging-best-practices.md)** 📊 高级必读
    - 🎯 **适用场景**: 需要优化日志系统，降低成本，提高效率
    - 👥 **目标读者**: 掌握 ELK 基础，需要优化日志架构的开发者
    - 📖 **内容概要**:
      - 为什么需要混合日志存储（性能、成本、可靠性）
      - 企业级日志分流策略（按级别、按业务、按环境）
      - 实战配置示例（Logback + Logstash 完整配置）
      - 日志级别选择指南（ERROR/WARN/INFO/DEBUG/TRACE）
      - 性能优化建议（异步日志、队列配置、批处理）
      - 故障处理策略（降级、限流、备份）
      - 最佳实践清单（推荐做法 vs 避免的做法）
    - ⏱️ **阅读时长**: 约 40-60 分钟
    - 💡 **建议**: 解答"为什么有些用 Logstash，有些用 Filebeat"等实际问题

**ELK 学习路径**：
```
ELK 零基础入门指南（09-elk-beginner-guide.md）
    ↓ 掌握 ELK 基础概念和操作
ELK 生产环境部署指南（07-elk-deployment-guide.md）
    ↓ 学会独立部署和集成
日志管理最佳实践（08-logging-best-practices.md）
    ↓ 优化日志系统，降本增效
成为日志管理专家 🚀
```

---

## 🔧 DevOps 系列

13. **[CI/CD 流水线搭建指南](cicd-setup-guide.md)**
    - 🎯 **适用场景**: 搭建企业级 CI/CD 流水线
    - 👥 **目标读者**: DevOps 工程师和开发团队
    - 📖 **内容概要**:
      - CI/CD 核心概念和流程
      - Jenkins/GitLab CI/GitHub Actions 配置
      - 自动化测试集成
      - Docker 镜像构建和推送
      - 自动化部署策略
    - ⏱️ **阅读时长**: 约 50-70 分钟
    - 💡 **建议**: 提升团队开发效率的关键

14. **[发布管理指南](release-management-guide.md)**
    - 🎯 **适用场景**: 规范化版本发布流程
    - 👥 **目标读者**: 项目负责人、DevOps 工程师
    - 📖 **内容概要**:
      - 版本号管理规范（语义化版本）
      - 发布流程和检查清单
      - 回滚策略
      - 发布风险管理
      - 发布后监控
    - ⏱️ **阅读时长**: 约 30-40 分钟
    - 💡 **建议**: 保障生产环境稳定性

---

## 📋 文档规划

以下是计划编写的文档列表：

### 构建工具系列
- [ ] Maven vs Gradle 对比指南
- [ ] Gradle 多模块项目最佳实践
- [ ] Gradle 插件开发指南

### Spring Boot 系列
- [x] Spring Boot 配置管理完全指南 → 见 [04-project-configuration-guide.md](04-project-configuration-guide.md)
- [ ] Spring Boot 与数据库集成（JPA/MyBatis）
- [ ] Spring Boot RESTful API 设计规范
- [ ] Spring Boot 微服务架构实践

### 数据库系列
- [ ] MySQL 8.4 LTS 使用指南
- [ ] PostgreSQL 16 入门与实践
- [ ] Redis 7.0 缓存策略
- [ ] 数据库连接池配置与优化

### 中间件系列
- [ ] Kafka 4.0 消息队列实践
- [x] Docker 容器化部署指南 → 见 [06-docker-beginner-guide.md](06-docker-beginner-guide.md)
- [ ] Kubernetes 部署实战

### 监控与运维系列
- [ ] Prometheus + Grafana 监控体系搭建
- [x] 日志管理与 ELK Stack → 见 [09-elk-beginner-guide.md](09-elk-beginner-guide.md), [07-elk-deployment-guide.md](07-elk-deployment-guide.md), [08-logging-best-practices.md](08-logging-best-practices.md)
- [ ] 应用性能调优指南

### DevOps 系列
- [ ] Git 工作流与分支管理策略
- [x] CI/CD 流水线搭建 → 见 [cicd-setup-guide.md](cicd-setup-guide.md)
- [x] 发布管理指南 → 见 [release-management-guide.md](release-management-guide.md)
- [ ] Terraform/OpenTofu 基础设施即代码

---

## 🎯 文档使用指南

### 如何阅读这些文档

1. **按需阅读**: 根据你当前的工作需求选择对应文档
2. **循序渐进**: 建议先从入门指南开始，再深入专题文档
3. **动手实践**: 每篇文档都包含实际命令和示例，建议跟着操作
4. **记录笔记**: 在实践过程中记录自己的心得和遇到的问题

### 文档命名规范

文档按照以下规则命名：

```
[编号]-[主题]-[子主题].md

例如：
01-gradle-springboot-project-initialization-guide.md
02-spring-boot-configuration-management.md
03-mysql-integration-guide.md
```

---

## 💡 贡献指南

如果你想为文档库贡献内容：

1. 文档格式使用 Markdown
2. 包含清晰的目录结构
3. 提供实际可运行的代码示例
4. 添加常见问题排查章节
5. 注明文档版本和更新日期

---

## 📞 反馈与建议

如果你对文档有任何疑问或建议，请：

1. 在项目中提交 Issue
2. 或直接联系项目维护团队

---

**文档库版本**: v1.1
**最后更新**: 2025-01-20
**维护**: DevOps Course Team

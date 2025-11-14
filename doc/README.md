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

5. **[Spring Boot 零基础入门指南](05-spring-boot-beginner-guide.md)** 🔥 零基础必读
   - 🎯 **适用场景**: 有 Java 基础，但不懂 Spring Boot
   - 👥 **目标读者**: Java 开发者转型 Spring Boot，或完全零基础学习者
   - 📖 **内容概要**:
     - Spring Boot 核心概念（IoC、依赖注入、自动配置）
     - 项目三层架构详解（Controller-Service-Repository）
     - 10+ 个核心注解详细讲解（@SpringBootApplication, @RestController等）
     - 数据流转完整示例
     - RESTful API 开发入门
     - ResponseEntity 和 HTTP 状态码
     - JSON 请求和响应处理
     - 代码示例全程配图解释
   - ⏱️ **阅读时长**: 约 45-60 分钟
   - 💡 **建议**: 🔥 零基础学习 Spring Boot 必读！通俗易懂，配合项目代码学习

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
- [ ] Docker 容器化部署指南
- [ ] Kubernetes 部署实战

### 监控与运维系列
- [ ] Prometheus + Grafana 监控体系搭建
- [ ] 日志管理与 ELK Stack
- [ ] 应用性能调优指南

### DevOps 系列
- [ ] Git 工作流与分支管理策略
- [ ] CI/CD 流水线搭建
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

**文档库版本**: v1.0
**最后更新**: 2025-11-13
**维护**: DevOps Course Team

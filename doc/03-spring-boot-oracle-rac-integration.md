# Spring Boot 集成 Oracle 19c RAC 数据库指南

> 📢 **本文档**：详细讲解如何在 Spring Boot 项目中集成 Oracle 19c RAC (Real Application Clusters) 数据库

## 目录

- [环境信息](#环境信息)
- [第一步：添加依赖](#第一步添加依赖)
- [第二步：配置数据源](#第二步配置数据源)
- [第三步：创建实体类](#第三步创建实体类)
- [第四步：创建数据访问层](#第四步创建数据访问层)
- [第五步：测试连接](#第五步测试连接)
- [高级配置](#高级配置)
- [常见问题](#常见问题)

---

## 环境信息

本项目使用的Oracle数据库环境：

| 配置项 | 值 |
|-------|-----|
| **数据库版本** | Oracle 19c |
| **架构** | ASM + RAC（双节点高可用集群） |
| **节点1 IP** | 192.168.1.66 |
| **节点2 IP** | 192.168.1.67 |
| **Service Name** | dbpv |
| **默认端口** | 1521 |
| **业务Schema** | TCBS（电信业务）、TCOA（能力开放） |

### 数据库表结构概览

**TCBS Schema（电信业务系统）：**
- `CUSTOMERS` - 客户表
- `CUSTOMER_ACCOUNTS` - 客户账户表
- `TELECOM_PRODUCTS` - 电信产品表
- `SERVICE_CONTRACTS` - 服务合同表
- `CUSTOMER_BILLS` - 客户账单表
- `USAGE_RECORDS` - 使用记录表（含分区表）

**TCOA Schema（能力开放平台）：**
- `OPEN_API_APPS` - 开放API应用表
- `API_CALL_LOGS` - API调用日志表（含分区表）

---

## 第一步：添加依赖

### 1.1 修改 build.gradle

```groovy
dependencies {
    // Spring Boot Starter
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'  // ← 添加JPA支持

    // Oracle Database Driver
    runtimeOnly 'com.oracle.database.jdbc:ojdbc11:23.6.0.24.10'  // ← Oracle JDBC驱动
    runtimeOnly 'com.oracle.database.jdbc:ucp:23.6.0.24.10'      // ← UCP连接池（RAC必须）

    // 其他依赖...
}
```

### 1.2 依赖说明

| 依赖 | 作用 |
|-----|------|
| `spring-boot-starter-data-jpa` | 提供 JPA/Hibernate 支持 |
| `ojdbc11` | Oracle JDBC 驱动（适用于 JDK 11+） |
| `ucp` | Universal Connection Pool，Oracle 连接池（RAC 环境推荐） |

**重要提示：**
- `ojdbc11` 适用于 Java 11+（我们使用 Java 21）
- UCP 是 Oracle 官方推荐的连接池，特别适合 RAC 环境
- 使用 `runtimeOnly` 因为编译时不需要，只在运行时需要

### 1.3 刷新依赖

```bash
# 刷新 Gradle 依赖
./gradlew --refresh-dependencies

# 或在 IDEA 中
# Gradle 工具窗口 -> 点击刷新按钮 🔄
```

---

## 第二步：配置数据源

### 2.1 理解 Oracle RAC 连接

Oracle RAC（Real Application Clusters）是 Oracle 的高可用集群方案，有两种连接方式：

#### 方式1：SCAN（Single Client Access Name）- 推荐

```
jdbc:oracle:thin:@//scan-ip:port/service_name
```

**优点：** 简单、自动负载均衡、透明故障切换

#### 方式2：TNSNAMES - 传统方式

```
jdbc:oracle:thin:@(DESCRIPTION=
  (ADDRESS_LIST=
    (ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.66)(PORT=1521))
    (ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.67)(PORT=1521))
    (LOAD_BALANCE=ON)
    (FAILOVER=ON))
  (CONNECT_DATA=
    (SERVICE_NAME=dbpv)
    (FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))
```

**优点：** 精确控制、支持所有 RAC 特性

### 2.2 配置 application.yml

**完整配置示例：**

```yaml
spring:
  application:
    name: claude-devops-course

  # Oracle RAC Database Configuration
  datasource:
    # RAC 连接URL（使用 TNSNAMES 方式）
    url: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.66)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=192.168.1.67)(PORT=1521))(LOAD_BALANCE=ON)(FAILOVER=ON))(CONNECT_DATA=(SERVICE_NAME=dbpv)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))

    # 数据库用户名（TCBS 是电信业务 Schema）
    username: TCBS

    # 密码（建议使用环境变量或配置中心）
    password: ${ORACLE_PASSWORD:your_password_here}

    # 驱动类名
    driver-class-name: oracle.jdbc.OracleDriver

    # HikariCP 连接池配置（生产环境推荐）
    hikari:
      pool-name: OracleHikariCP
      minimum-idle: 5              # 最小空闲连接数
      maximum-pool-size: 20        # 最大连接数
      max-lifetime: 1800000        # 连接最大生命周期：30分钟
      connection-timeout: 30000    # 连接超时：30秒
      idle-timeout: 600000         # 空闲超时：10分钟
      connection-test-query: SELECT 1 FROM DUAL  # 连接测试SQL

  # JPA/Hibernate 配置
  jpa:
    database-platform: org.hibernate.dialect.Oracle12cDialect
    show-sql: true                 # 开发环境显示SQL
    hibernate:
      ddl-auto: validate           # 生产环境使用 validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
        implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
    properties:
      hibernate:
        format_sql: true           # 格式化SQL输出
        use_sql_comments: true     # SQL中添加注释
        jdbc:
          batch_size: 20           # 批量操作大小
        order_inserts: true        # 优化插入顺序
        order_updates: true        # 优化更新顺序
```

### 2.3 环境特定配置

创建多个配置文件适应不同环境：

**application-dev.yml（开发环境）：**
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@(DESCRIPTION=...)
    username: TCBS
    password: dev_password
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
```

**application-prod.yml（生产环境）：**
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@(DESCRIPTION=...)
    username: TCBS
    password: ${ORACLE_PASSWORD}  # 从环境变量读取
    hikari:
      maximum-pool-size: 50       # 生产环境增加连接数
  jpa:
    show-sql: false               # 生产环境关闭SQL日志
    hibernate:
      ddl-auto: validate          # 生产环境只验证
```

**启动时指定环境：**
```bash
# 开发环境
./gradlew bootRun --args='--spring.profiles.active=dev'

# 生产环境
./gradlew bootRun --args='--spring.profiles.active=prod'
```

---

## 第三步：创建实体类

### 3.1 创建 Customer 实体

```java
package com.devops.course.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CUSTOMERS", schema = "TCBS")
public class Customer {

    @Id
    @Column(name = "CUSTOMER_ID", nullable = false, length = 20)
    private String customerId;

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 100)
    private String customerName;

    @Column(name = "ID_CARD_NO", length = 18)
    private String idCardNo;

    @Column(name = "CUSTOMER_TYPE", length = 10)
    private String customerType;

    @Column(name = "CONTACT_PHONE", length = 20)
    private String contactPhone;

    @Column(name = "STATUS", length = 10)
    private String status;

    @Column(name = "CREATE_TIME")
    private LocalDateTime createTime;

    // Getters and Setters...
}
```

### 3.2 JPA 注解说明

| 注解 | 作用 | 示例 |
|-----|------|------|
| `@Entity` | 标记为JPA实体 | `@Entity` |
| `@Table` | 指定表名和Schema | `@Table(name="CUSTOMERS", schema="TCBS")` |
| `@Id` | 标记主键 | `@Id` |
| `@Column` | 映射列名 | `@Column(name="CUSTOMER_ID")` |

### 3.3 Oracle 特殊注意事项

**1. Schema 必须大写：**
```java
@Table(name = "CUSTOMERS", schema = "TCBS")  // ✅ 正确
@Table(name = "customers", schema = "tcbs")  // ❌ 错误
```

**2. 列名大写：**
```java
@Column(name = "CUSTOMER_ID")  // ✅ 正确
@Column(name = "customer_id")  // ❌ 错误（除非创建表时用了双引号）
```

**3. 时间类型映射：**
```java
// Oracle DATE -> LocalDateTime
@Column(name = "CREATE_TIME")
private LocalDateTime createTime;

// Oracle TIMESTAMP -> LocalDateTime
@Column(name = "UPDATE_TIME")
private LocalDateTime updateTime;
```

---

## 第四步：创建数据访问层

### 4.1 创建 Repository 接口

```java
package com.devops.course.repository;

import com.devops.course.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // 方法命名查询
    List<Customer> findByCustomerType(String customerType);

    List<Customer> findByStatus(String status);

    // 自定义JPQL查询
    @Query("SELECT c FROM Customer c WHERE c.creditLevel = :level")
    List<Customer> findByCreditLevel(String level);

    // 原生SQL查询
    @Query(value = "SELECT * FROM TCBS.CUSTOMERS WHERE STATUS = :status",
           nativeQuery = true)
    List<Customer> findByStatusNative(String status);
}
```

### 4.2 创建 Service 层

```java
package com.devops.course.service;

import com.devops.course.entity.Customer;
import com.devops.course.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> findActiveCustomers() {
        return customerRepository.findByStatus("ACTIVE");
    }

    @Transactional  // 写操作需要事务
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
```

### 4.3 创建 REST Controller

```java
package com.devops.course.controller;

import com.devops.course.entity.Customer;
import com.devops.course.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Customer>> getActiveCustomers() {
        List<Customer> customers = customerService.findActiveCustomers();
        return ResponseEntity.ok(customers);
    }
}
```

---

## 第五步：测试连接

### 5.1 创建数据库连接测试

```java
package com.devops.course.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Configuration
public class DatabaseConfig {

    @Bean
    public CommandLineRunner testDatabaseConnection(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("=================================");
                System.out.println("数据库连接测试成功！");
                System.out.println("数据库URL: " + connection.getMetaData().getURL());
                System.out.println("数据库用户: " + connection.getMetaData().getUserName());
                System.out.println("数据库产品: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());
                System.out.println("=================================");
            } catch (Exception e) {
                System.err.println("数据库连接失败: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
```

### 5.2 运行应用

```bash
# 启动应用
./gradlew bootRun

# 查看启动日志，应该看到：
# =================================
# 数据库连接测试成功！
# 数据库URL: jdbc:oracle:thin:@...
# 数据库用户: TCBS
# 数据库产品: Oracle
# 数据库版本: Oracle Database 19c Enterprise Edition...
# =================================
```

### 5.3 测试REST API

```bash
# 获取所有客户
curl http://localhost:8080/api/customers

# 获取活跃客户
curl http://localhost:8080/api/customers/active
```

---

## 高级配置

### 6.1 配置连接池监控

```yaml
spring:
  datasource:
    hikari:
      # 启用JMX监控
      register-mbeans: true

      # 连接泄漏检测（开发环境）
      leak-detection-threshold: 60000  # 60秒

      # 其他优化
      validation-timeout: 3000
      connection-init-sql: ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'
```

### 6.2 配置 Oracle 优化参数

```yaml
spring:
  jpa:
    properties:
      hibernate:
        # Oracle 分页优化
        use_sql_comments: true

        # 二级缓存（生产环境）
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory

        # 查询缓存
        cache:
          use_query_cache: true
```

### 6.3 配置多数据源

如果需要同时访问 TCBS 和 TCOA 两个 Schema：

```yaml
spring:
  datasource:
    tcbs:
      url: jdbc:oracle:thin:@...
      username: TCBS
      password: xxx

    tcoa:
      url: jdbc:oracle:thin:@...
      username: TCOA
      password: xxx
```

---

## 常见问题

### 问题1：连接超时

**症状：**
```
Could not connect to Oracle database
Connection timeout
```

**解决方案：**

1. 检查网络连接：
```bash
ping 192.168.1.66
ping 192.168.1.67
telnet 192.168.1.66 1521
```

2. 检查防火墙：
```bash
# 确保 1521 端口开放
```

3. 增加超时时间：
```yaml
spring:
  datasource:
    hikari:
      connection-timeout: 60000  # 增加到60秒
```

### 问题2：表名或列名找不到

**症状：**
```
ORA-00942: table or view does not exist
```

**解决方案：**

1. 确认 Schema 名称大写：
```java
@Table(name = "CUSTOMERS", schema = "TCBS")  // ← Schema必须大写
```

2. 确认表名大写：
```java
@Table(name = "CUSTOMERS")  // ← 表名必须大写
```

3. 在数据库中验证：
```sql
-- 查看当前用户的表
SELECT * FROM USER_TABLES;

-- 查看所有可访问的表
SELECT * FROM ALL_TABLES WHERE OWNER = 'TCBS';
```

### 问题3：字符集问题

**症状：**
中文显示为乱码

**解决方案：**

1. 配置连接参数：
```yaml
spring:
  datasource:
    hikari:
      data-source-properties:
        oracle.jdbc.defaultNChar: true
      connection-init-sql: |
        ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS';
        ALTER SESSION SET NLS_LANGUAGE = 'SIMPLIFIED CHINESE';
        ALTER SESSION SET NLS_TERRITORY = 'CHINA'
```

2. 或在URL中指定：
```
jdbc:oracle:thin:@...?oracle.jdbc.defaultNChar=true
```

### 问题4：RAC 负载不均衡

**症状：**
所有连接都集中在一个节点

**解决方案：**

1. 确认使用了 UCP 连接池：
```groovy
runtimeOnly 'com.oracle.database.jdbc:ucp:23.6.0.24.10'
```

2. 配置 LOAD_BALANCE：
```
(LOAD_BALANCE=ON)
(FAILOVER=ON)
```

3. 使用 SCAN 地址（如果有）：
```
jdbc:oracle:thin:@//scan-ip:1521/dbpv
```

---

## 快速参考

### 核心配置清单

- [ ] ✅ 添加 `ojdbc11` 和 `ucp` 依赖
- [ ] ✅ 配置 RAC 连接URL（包含两个节点IP）
- [ ] ✅ 配置 HikariCP 连接池
- [ ] ✅ 配置 JPA/Hibernate
- [ ] ✅ 创建 Entity 类（注意 Schema 和表名大写）
- [ ] ✅ 创建 Repository 接口
- [ ] ✅ 测试数据库连接

### 连接URL模板

```
# TNSNAMES 方式（推荐用于 RAC）
jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=IP1)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=IP2)(PORT=1521))(LOAD_BALANCE=ON)(FAILOVER=ON))(CONNECT_DATA=(SERVICE_NAME=SERVICE_NAME)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))

# 简单方式（单节点或SCAN）
jdbc:oracle:thin:@//hostname:1521/service_name
```

---

**文档版本**: v1.0
**最后更新**: 2025-11-13
**维护者**: DevOps Course Team
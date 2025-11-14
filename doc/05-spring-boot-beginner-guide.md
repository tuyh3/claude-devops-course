# Spring Boot 零基础入门指南（适合有 Java 基础的开发者）

> 📢 **写给 Java 开发者**：如果你会 Java 基础（类、对象、接口、注解），但不懂 Spring Boot，这份文档将手把手教你读懂这个项目，并能自己编写代码。

---

## 📋 目录

- [第一章：Spring Boot 是什么](#第一章spring-boot-是什么)
- [第二章：项目架构解析](#第二章项目架构解析)
- [第三章：注解详解（必看！）](#第三章注解详解必看)
- [第四章：读懂现有代码](#第四章读懂现有代码)
- [第五章：自己动手写代码](#第五章自己动手写代码)
- [第六章：调试技巧](#第六章调试技巧)
- [第七章：常见问题](#第七章常见问题)

---

## 第一章：Spring Boot 是什么

### 1.1 传统 Java Web 开发 vs Spring Boot

**传统方式（你可能见过的）**：
```java
// 传统 Servlet 方式
public class HelloServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.getWriter().println("Hello World");
    }
}
// 还需要配置 web.xml，部署到 Tomcat...
```

**Spring Boot 方式（现代方式）**：
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";  // 就这么简单！
    }
}
// 不需要 web.xml，不需要手动部署 Tomcat
```

### 1.2 Spring Boot 的三大优势

#### 优势1：内嵌服务器

**传统方式**：
```
1. 写代码
2. 打包成 WAR
3. 安装 Tomcat
4. 部署 WAR 到 Tomcat
5. 启动 Tomcat
```

**Spring Boot**：
```bash
./gradlew bootRun  # 一条命令，自动启动内嵌 Tomcat
```

#### 优势2：自动配置

**传统方式**：
```xml
<!-- 需要写 100 行 XML 配置 -->
<bean id="dataSource" class="...">
    <property name="url" value="..."/>
    <property name="username" value="..."/>
    ...
</bean>
```

**Spring Boot**：
```yaml
# 只需要几行 YAML
spring:
  datasource:
    url: jdbc:oracle:thin:@//localhost:1521/dbpv
    username: TCBS
    password: password
```

#### 优势3：起步依赖

**传统方式**：
```xml
<!-- 需要手动添加 20+ 个依赖和版本 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>6.1.0</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>6.1.0</version>
</dependency>
<!-- ... 还有十几个 -->
```

**Spring Boot**：
```groovy
// 一行搞定
implementation 'org.springframework.boot:spring-boot-starter-web'
```

### 1.3 核心概念速记

| 概念 | 简单理解 | 类比 |
|------|---------|------|
| **Spring Boot** | 一个让 Java Web 开发变简单的框架 | 像是给汽车装了自动驾驶 |
| **IoC 容器** | Spring 帮你管理对象（不用 `new`） | 像是一个对象仓库 |
| **依赖注入** | 需要对象时，Spring 自动给你 | 像是快递送货上门 |
| **注解** | 在类或方法上加 `@XXX`，告诉 Spring 干什么 | 像是给代码贴标签 |
| **自动配置** | 根据你的依赖，Spring 自动配置好一切 | 像是手机开机自动连 WiFi |

---

## 第二章：项目架构解析

### 2.1 整体架构图

```
                        ┌─────────────────┐
                        │   浏览器/客户端    │
                        └────────┬────────┘
                                 │ HTTP 请求
                                 ▼
                        ┌─────────────────┐
                        │   Controller     │  ← 接收请求，返回响应
                        │  (控制器层)       │
                        └────────┬────────┘
                                 │ 调用
                                 ▼
                        ┌─────────────────┐
                        │    Service       │  ← 业务逻辑处理
                        │   (服务层)        │
                        └────────┬────────┘
                                 │ 调用
                                 ▼
                        ┌─────────────────┐
                        │   Repository     │  ← 数据库操作
                        │  (数据访问层)      │
                        └────────┬────────┘
                                 │ JDBC/JPA
                                 ▼
                        ┌─────────────────┐
                        │     Database     │
                        │   (Oracle 19c)   │
                        └─────────────────┘
```

### 2.2 三层架构详解

#### Controller 层（控制器层）

**作用**：接收 HTTP 请求，调用 Service，返回响应

**类比**：餐厅的**服务员**（接单、传菜、结账）

**例子**：
```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;  // 注入 Service

    @GetMapping("/{id}")  // GET /api/customers/CUST001
    public Customer getCustomer(@PathVariable String id) {
        return customerService.findById(id);  // 调用 Service
    }
}
```

**职责**：
- ✅ 接收请求参数
- ✅ 调用 Service 处理业务
- ✅ 返回结果（通常是 JSON）
- ❌ 不应该写业务逻辑
- ❌ 不应该直接操作数据库

#### Service 层（服务层）

**作用**：处理业务逻辑

**类比**：餐厅的**厨师**（做菜、配菜）

**例子**：
```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;  // 注入 Repository

    public Customer findById(String id) {
        // 业务逻辑：查找客户
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("客户不存在"));
    }

    public void createCustomer(Customer customer) {
        // 业务逻辑：创建客户前的验证
        if (customer.getCustomerName() == null) {
            throw new RuntimeException("客户姓名不能为空");
        }
        customerRepository.save(customer);
    }
}
```

**职责**：
- ✅ 处理业务逻辑（验证、计算、组合数据）
- ✅ 调用 Repository 操作数据库
- ✅ 事务管理
- ❌ 不应该处理 HTTP 请求细节

#### Repository 层（数据访问层）

**作用**：操作数据库

**类比**：餐厅的**仓库管理员**（拿食材、记录库存）

**例子**：
```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // 不需要写实现！Spring Data JPA 自动实现

    List<Customer> findByCustomerType(String customerType);  // 自动生成 SQL

    @Query("SELECT c FROM Customer c WHERE c.status = :status")
    List<Customer> findActiveCustomers(@Param("status") String status);
}
```

**职责**：
- ✅ 数据库 CRUD 操作
- ✅ 定义查询方法
- ❌ 不应该有业务逻辑

#### Entity（实体类）

**作用**：数据库表的 Java 映射

**类比**：餐厅的**菜单**（定义菜品结构）

**例子**：
```java
@Entity
@Table(name = "CUSTOMERS", schema = "TCBS")
@Getter  // Lombok 自动生成 getter
@Setter  // Lombok 自动生成 setter
public class Customer {

    @Id  // 主键
    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    // ... 其他字段
}
```

**职责**：
- ✅ 定义数据结构
- ✅ 映射数据库表
- ❌ 不应该有业务逻辑

### 2.3 数据流转示例

**场景**：用户访问 `GET /api/customers/CUST001`

```
1. 浏览器发送 HTTP 请求
   ↓
2. CustomerController.getCustomer("CUST001") 接收请求
   ↓
3. customerService.findById("CUST001") 处理业务
   ↓
4. customerRepository.findById("CUST001") 查询数据库
   ↓
5. 数据库返回结果
   ↓
6. 数据逐层返回：Repository → Service → Controller
   ↓
7. Controller 将数据转成 JSON 返回给浏览器
```

**完整代码流程**：
```java
// 1. Controller 接收请求
@GetMapping("/{id}")
public Customer getCustomer(@PathVariable String id) {  // id = "CUST001"

    // 2. 调用 Service
    return customerService.findById(id);
}

// 3. Service 处理业务
public Customer findById(String id) {

    // 4. 调用 Repository
    return customerRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("客户不存在"));
}

// 5. Repository 查询数据库（自动生成 SQL）
// SELECT * FROM TCBS.CUSTOMERS WHERE CUSTOMER_ID = 'CUST001'
```

---

## 第三章：注解详解（必看！）

### 3.1 核心注解总览

| 注解 | 作用 | 使用位置 | 必须掌握 |
|------|------|---------|---------|
| `@SpringBootApplication` | 标记主类 | Main 类 | ⭐⭐⭐ |
| `@RestController` | 标记控制器类 | Controller 类 | ⭐⭐⭐ |
| `@Service` | 标记服务类 | Service 类 | ⭐⭐⭐ |
| `@Repository` | 标记数据访问类 | Repository 接口 | ⭐⭐⭐ |
| `@Entity` | 标记实体类 | Entity 类 | ⭐⭐⭐ |
| `@Autowired` | 自动注入依赖 | 字段/构造器 | ⭐⭐⭐ |
| `@GetMapping` | 处理 GET 请求 | Controller 方法 | ⭐⭐⭐ |
| `@PostMapping` | 处理 POST 请求 | Controller 方法 | ⭐⭐⭐ |
| `@RequestBody` | 接收 JSON 数据 | 方法参数 | ⭐⭐ |
| `@PathVariable` | 接收 URL 参数 | 方法参数 | ⭐⭐⭐ |

### 3.2 @SpringBootApplication（启动类注解）

**位置**：Main 类

**作用**：告诉 Spring Boot 这是入口类，启动整个应用

**例子**：
```java
@SpringBootApplication  // ← 这个注解是魔法的开始
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

**等价于三个注解**：
```java
@SpringBootConfiguration  // 配置类
@EnableAutoConfiguration  // 自动配置
@ComponentScan           // 扫描组件
public class Main { ... }
```

**你需要知道**：
- 必须加在主类上
- 一个项目只有一个
- 它会自动扫描同包及子包下的所有 `@Component`、`@Service`、`@Controller` 等

### 3.3 @RestController（控制器注解）

**位置**：Controller 类

**作用**：标记这是一个 REST API 控制器，返回 JSON 数据

**例子**：
```java
@RestController  // ← 告诉 Spring：这是个控制器
@RequestMapping("/api/customers")  // ← 所有方法的 URL 都以这个开头
public class CustomerController {

    @GetMapping("/{id}")  // 完整 URL: /api/customers/{id}
    public Customer getCustomer(@PathVariable String id) {
        return new Customer(id, "张三");
    }
}
```

**等价于**：
```java
@Controller  // 标记为控制器
@ResponseBody  // 所有方法返回 JSON（不是跳转页面）
public class CustomerController { ... }
```

**你需要知道**：
- `@RestController` = `@Controller` + `@ResponseBody`
- 返回值会自动转成 JSON
- 如果你要返回 HTML 页面，用 `@Controller`

### 3.4 @Service（服务层注解）

**位置**：Service 类

**作用**：标记这是一个服务类，Spring 会管理它

**例子**：
```java
@Service  // ← 告诉 Spring：这是个服务类，请管理它
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public Customer findById(String id) {
        return repository.findById(id).orElse(null);
    }
}
```

**你需要知道**：
- 加了 `@Service`，Spring 会创建这个类的实例（单例）
- 你不需要 `new CustomerService()`，Spring 自动管理
- 可以用 `@Autowired` 注入到其他地方

### 3.5 @Repository（数据访问层注解）

**位置**：Repository 接口

**作用**：标记这是数据访问层，Spring Data JPA 会自动实现

**例子**：
```java
@Repository  // ← 告诉 Spring：这是个数据访问接口
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // 不需要写实现！Spring Data JPA 自动生成

    // 方法名 → SQL 规则：
    // findBy + 字段名 → WHERE 字段名 = ?
    List<Customer> findByCustomerType(String customerType);
    // 自动生成：SELECT * FROM CUSTOMERS WHERE CUSTOMER_TYPE = ?
}
```

**你需要知道**：
- 继承 `JpaRepository<实体类, 主键类型>`
- 不需要写实现类
- 方法名有命名规则（见下表）

**方法名规则**：

| 方法名 | 生成的 SQL |
|-------|-----------|
| `findByName(String name)` | `WHERE name = ?` |
| `findByNameAndAge(String name, int age)` | `WHERE name = ? AND age = ?` |
| `findByNameLike(String name)` | `WHERE name LIKE ?` |
| `findByAgeGreaterThan(int age)` | `WHERE age > ?` |
| `findByAgeBetween(int min, int max)` | `WHERE age BETWEEN ? AND ?` |

### 3.6 @Entity（实体类注解）

**位置**：Entity 类

**作用**：标记这是一个数据库表的映射类

**例子**：
```java
@Entity  // ← 告诉 JPA：这是个实体类
@Table(name = "CUSTOMERS", schema = "TCBS")  // ← 对应哪个表
@Getter  // Lombok：自动生成 getter
@Setter  // Lombok：自动生成 setter
public class Customer {

    @Id  // ← 主键
    @Column(name = "CUSTOMER_ID")  // ← 对应数据库列名
    private String customerId;

    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    // 不需要写 getter/setter，Lombok 自动生成
}
```

**你需要知道**：
- `@Id`：标记主键字段
- `@Column(name = "XXX")`：映射数据库列名
- `@Table(name = "XXX")`：映射数据库表名
- 如果不写 `@Column`，默认用字段名（驼峰 → 下划线）

### 3.7 @Autowired（依赖注入注解）

**位置**：字段、构造器、方法

**作用**：自动注入依赖对象

**例子1：字段注入**（最常用）
```java
@Service
public class CustomerService {

    @Autowired  // ← Spring 自动注入 CustomerRepository 实例
    private CustomerRepository repository;

    // 不需要写：repository = new CustomerRepositoryImpl();
}
```

**例子2：构造器注入**（推荐方式）
```java
@Service
public class CustomerService {

    private final CustomerRepository repository;

    @Autowired  // ← 如果只有一个构造器，可以省略 @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }
}
```

**例子3：Setter 注入**
```java
@Service
public class CustomerService {

    private CustomerRepository repository;

    @Autowired
    public void setRepository(CustomerRepository repository) {
        this.repository = repository;
    }
}
```

**你需要知道**：
- Spring 会自动找到对应的 Bean 注入
- 如果有多个同类型的 Bean，可以用 `@Qualifier` 指定
- 推荐用构造器注入（便于测试）

### 3.8 @GetMapping / @PostMapping（请求映射注解）

**位置**：Controller 方法

**作用**：映射 HTTP 请求到方法

**例子1：GET 请求**
```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    // GET /api/customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    // GET /api/customers/CUST001
    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable String id) {
        return customerService.findById(id);
    }
}
```

**例子2：POST 请求**
```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    // POST /api/customers
    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        return customerService.save(customer);
    }
}
```

**完整的 HTTP 方法注解**：

| 注解 | HTTP 方法 | 用途 |
|------|----------|------|
| `@GetMapping` | GET | 查询数据 |
| `@PostMapping` | POST | 创建数据 |
| `@PutMapping` | PUT | 更新数据（全量） |
| `@PatchMapping` | PATCH | 更新数据（部分） |
| `@DeleteMapping` | DELETE | 删除数据 |

### 3.9 @RequestBody / @PathVariable / @RequestParam

#### @RequestBody（接收 JSON）

**例子**：
```java
@PostMapping("/customers")
public Customer createCustomer(@RequestBody Customer customer) {
    // customer 会自动从 JSON 转换
    return customerService.save(customer);
}
```

**客户端请求**：
```bash
POST /api/customers
Content-Type: application/json

{
  "customerId": "CUST001",
  "customerName": "张三"
}
```

#### @PathVariable（接收 URL 路径参数）

**例子**：
```java
@GetMapping("/customers/{id}")
public Customer getCustomer(@PathVariable String id) {
    // id 从 URL 路径中提取
    return customerService.findById(id);
}
```

**客户端请求**：
```bash
GET /api/customers/CUST001
# id = "CUST001"
```

#### @RequestParam（接收 URL 查询参数）

**例子**：
```java
@GetMapping("/customers")
public List<Customer> searchCustomers(
    @RequestParam String name,
    @RequestParam(required = false, defaultValue = "10") int pageSize
) {
    return customerService.search(name, pageSize);
}
```

**客户端请求**：
```bash
GET /api/customers?name=张三&pageSize=20
# name = "张三"
# pageSize = 20
```

**三者对比**：

| 注解 | 用途 | 示例 |
|------|------|------|
| `@RequestBody` | 接收 JSON 请求体 | `POST /api/customers` + JSON |
| `@PathVariable` | 接收 URL 路径参数 | `/api/customers/{id}` |
| `@RequestParam` | 接收 URL 查询参数 | `/api/customers?name=xxx` |

---

## 第四章：读懂现有代码

现在你已经了解了基本概念和注解，让我们一起来读懂项目中的真实代码。

### 4.1 读懂 Main.java（入口类）

**代码位置**：`src/main/java/com/devops/course/Main.java`

```java
package com.devops.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

**逐行解析**：

1. **`@SpringBootApplication`**：
   - 这是 Spring Boot 的"万能注解"
   - 它包含了三个注解的功能：
     - `@SpringBootConfiguration`：标记为配置类
     - `@EnableAutoConfiguration`：启用自动配置（Spring Boot 的魔法）
     - `@ComponentScan`：扫描当前包及子包下的所有组件

2. **`SpringApplication.run(Main.class, args)`**：
   - 启动 Spring Boot 应用
   - `Main.class`：告诉 Spring Boot 从哪里开始扫描
   - `args`：传递命令行参数

**这个类做了什么**？
- 启动 IoC 容器
- 扫描所有带 `@Component`、`@Service`、`@Repository`、`@Controller` 的类
- 自动配置数据库、Web 服务器等
- 启动内嵌的 Tomcat 服务器（默认端口 8080）

### 4.2 读懂 Controller（控制器层）

#### 4.2.1 简单例子：HelloController

**代码位置**：`src/main/java/com/devops/course/controller/HelloController.java`

```java
package com.devops.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello, Spring Boot!");
        response.put("status", "success");
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
```

**逐行解析**：

1. **`@RestController`**：
   - 标记这是一个 REST 控制器
   - 所有方法的返回值都会自动转换成 JSON

2. **`@RequestMapping("/api")`**：
   - 为整个类设置 URL 前缀
   - 所有方法的 URL 都会加上 `/api`

3. **`@GetMapping("/hello")`**：
   - 处理 `GET /api/hello` 请求
   - 完整 URL = 类前缀 `/api` + 方法路径 `/hello`

4. **`return response`**：
   - 返回一个 `Map<String, String>`
   - Spring Boot 会自动把它转换成 JSON：
     ```json
     {
       "message": "Hello, Spring Boot!",
       "status": "success"
     }
     ```

**测试**：
```bash
curl http://localhost:8080/api/hello
# 输出：{"message":"Hello, Spring Boot!","status":"success"}

curl http://localhost:8080/api/health
# 输出：{"status":"UP"}
```

#### 4.2.2 完整例子：CustomerController

**代码位置**：`src/main/java/com/devops/course/controller/CustomerController.java`

这是一个完整的 CRUD（增删改查）控制器，我们逐个方法分析：

**1. 查询所有客户**：
```java
@GetMapping
public ResponseEntity<List<Customer>> getAllCustomers() {
    List<Customer> customers = customerService.findAllCustomers();
    return ResponseEntity.ok(customers);
}
```

- **URL**：`GET /api/customers`
- **作用**：查询所有客户
- **流程**：
  1. Controller 调用 `customerService.findAllCustomers()`
  2. Service 调用 `customerRepository.findAll()`
  3. Repository 从数据库查询数据
  4. 返回结果自动转换成 JSON

**2. 根据 ID 查询客户**：
```java
@GetMapping("/{id}")
public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
    Optional<Customer> customer = customerService.findCustomerById(id);
    return customer.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

- **URL**：`GET /api/customers/CUST001`
- **作用**：根据 ID 查询单个客户
- **关键点**：
  - `@PathVariable String id`：从 URL 中提取 `CUST001`
  - `Optional<Customer>`：可能有值，也可能为空
  - `.map(ResponseEntity::ok)`：如果有值，返回 200 OK
  - `.orElse(ResponseEntity.notFound().build())`：如果没有值，返回 404 Not Found

**测试**：
```bash
# 客户存在 → 返回 200 + 客户数据
curl http://localhost:8080/api/customers/CUST001

# 客户不存在 → 返回 404
curl http://localhost:8080/api/customers/NOTEXIST
```

**3. 创建客户**：
```java
@PostMapping
public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    Customer savedCustomer = customerService.saveCustomer(customer);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
}
```

- **URL**：`POST /api/customers`
- **作用**：创建新客户
- **关键点**：
  - `@RequestBody Customer customer`：把 JSON 请求体转换成 `Customer` 对象
  - `HttpStatus.CREATED`：返回 201 状态码（表示资源已创建）

**测试**：
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": "CUST999",
    "customerName": "测试客户",
    "contactPhone": "13800138000"
  }'
```

**4. 更新客户**：
```java
@PutMapping("/{id}")
public ResponseEntity<Customer> updateCustomer(
        @PathVariable String id,
        @RequestBody Customer customer) {
    customer.setCustomerId(id);  // 确保 ID 一致
    Customer updatedCustomer = customerService.saveCustomer(customer);
    return ResponseEntity.ok(updatedCustomer);
}
```

- **URL**：`PUT /api/customers/CUST001`
- **作用**：更新指定客户
- **关键点**：
  - 同时使用 `@PathVariable` 和 `@RequestBody`
  - `customer.setCustomerId(id)`：确保 URL 中的 ID 和请求体一致

**5. 删除客户**：
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
    customerService.deleteCustomer(id);
    return ResponseEntity.noContent().build();
}
```

- **URL**：`DELETE /api/customers/CUST001`
- **作用**：删除指定客户
- **关键点**：
  - `ResponseEntity<Void>`：不返回数据，只返回状态码
  - `ResponseEntity.noContent()`：返回 204 No Content（表示成功删除）

### 4.3 读懂 Service（业务逻辑层）

**代码位置**：`src/main/java/com/devops/course/service/CustomerService.java`

```java
@Service
@Transactional(readOnly = true)
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    @Transactional
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId);
    }
}
```

**逐行解析**：

1. **`@Service`**：
   - 标记这是一个 Service 层组件
   - Spring 会自动创建这个类的实例并管理它

2. **`@Transactional(readOnly = true)`**（类级别）：
   - 为整个类的所有方法添加事务管理
   - `readOnly = true`：表示只读事务（用于查询方法，性能更好）
   - 如果方法需要写操作，需要在方法上加 `@Transactional`（覆盖类级别的配置）

3. **`@Autowired`**：
   - 自动注入 `CustomerRepository`
   - Spring 会自动找到 `CustomerRepository` 的实例并注入进来

4. **`@Transactional`**（方法级别）：
   - 覆盖类级别的 `readOnly = true`
   - 表示这个方法需要写事务（可以修改数据库）
   - 如果方法执行过程中出错，会自动回滚

**Service 层的作用**：
- 包含业务逻辑（虽然这个例子比较简单，只是调用 Repository）
- 管理事务（确保数据一致性）
- 可以调用多个 Repository 完成复杂业务

**更复杂的例子**：
```java
@Transactional
public void transferCustomerData(String fromId, String toId) {
    // 复杂业务逻辑：涉及多个表的操作
    Customer from = customerRepository.findById(fromId).orElseThrow();
    Customer to = customerRepository.findById(toId).orElseThrow();

    // ... 复杂的业务处理 ...

    customerRepository.save(from);
    customerRepository.save(to);
    // 如果中间任何一步失败，整个方法会回滚
}
```

### 4.4 读懂 Repository（数据访问层）

**代码位置**：`src/main/java/com/devops/course/repository/CustomerRepository.java`

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByCustomerType(String customerType);

    List<Customer> findByStatus(String status);

    List<Customer> findByCustomerNameContaining(String name);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.creditLevel = :creditLevel")
    Long countByCreditLevel(String creditLevel);
}
```

**逐行解析**：

1. **`@Repository`**：
   - 标记这是一个 Repository 层组件
   - Spring Data JPA 会自动创建这个接口的实现类

2. **`extends JpaRepository<Customer, String>`**：
   - 继承 `JpaRepository` 接口
   - `<Customer, String>`：实体类型是 `Customer`，主键类型是 `String`
   - 自动获得以下方法（不需要写实现）：
     - `findAll()`：查询所有
     - `findById(id)`：根据 ID 查询
     - `save(entity)`：保存或更新
     - `deleteById(id)`：根据 ID 删除
     - 还有更多...

3. **方法命名规范**（Spring Data JPA 的魔法）：

   Spring Data JPA 会根据方法名自动生成 SQL！

   | 方法名 | 生成的 SQL |
   |--------|-----------|
   | `findByCustomerType(String type)` | `SELECT * FROM customers WHERE customer_type = ?` |
   | `findByStatus(String status)` | `SELECT * FROM customers WHERE status = ?` |
   | `findByCustomerNameContaining(String name)` | `SELECT * FROM customers WHERE customer_name LIKE %?%` |

   **命名规则**：
   - `findBy` + 字段名：精确匹配
   - `findBy` + 字段名 + `Containing`：模糊查询（LIKE %xx%）
   - `findBy` + 字段名 + `StartingWith`：前缀匹配（LIKE xx%）
   - `findBy` + 字段名 + `And` + 另一个字段名：多条件查询

   **示例**：
   ```java
   // 自动生成：SELECT * FROM customers WHERE customer_type = ? AND status = ?
   List<Customer> findByCustomerTypeAndStatus(String type, String status);

   // 自动生成：SELECT * FROM customers WHERE create_time > ?
   List<Customer> findByCreateTimeAfter(LocalDateTime date);
   ```

4. **`@Query` 自定义查询**：

   如果方法名太复杂，或者需要复杂 SQL，可以使用 `@Query`：

   ```java
   @Query("SELECT COUNT(c) FROM Customer c WHERE c.creditLevel = :creditLevel")
   Long countByCreditLevel(String creditLevel);
   ```

   - 使用 JPQL（类似 SQL，但是用实体类名和字段名）
   - `:creditLevel`：命名参数，对应方法参数 `String creditLevel`

### 4.5 读懂 Entity（实体类）

**代码位置**：`src/main/java/com/devops/course/entity/Customer.java`

```java
@Entity
@Table(name = "CUSTOMERS", schema = "TCBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {

    @Id
    @Column(name = "CUSTOMER_ID", nullable = false, length = 20)
    private String customerId;

    @Column(name = "CUSTOMER_NAME", nullable = false, length = 100)
    private String customerName;

    @Column(name = "CONTACT_PHONE", length = 20)
    private String contactPhone;

    // ... 更多字段 ...
}
```

**逐行解析**：

1. **JPA 注解**（数据库映射）：

   | 注解 | 作用 |
   |------|------|
   | `@Entity` | 标记这是一个 JPA 实体类（对应数据库表） |
   | `@Table(name="CUSTOMERS", schema="TCBS")` | 指定表名和 schema |
   | `@Id` | 标记主键字段 |
   | `@Column(name="CUSTOMER_ID")` | 指定数据库列名 |
   | `nullable = false` | 对应数据库的 NOT NULL |
   | `length = 20` | 对应数据库的 VARCHAR(20) |

2. **Lombok 注解**（代码生成）：

   | 注解 | 生成的代码 |
   |------|----------|
   | `@Getter` | 为所有字段生成 `getXxx()` 方法 |
   | `@Setter` | 为所有字段生成 `setXxx()` 方法 |
   | `@NoArgsConstructor` | 生成无参构造器：`new Customer()` |
   | `@AllArgsConstructor` | 生成全参构造器：`new Customer(id, name, ...)` |
   | `@ToString` | 生成 `toString()` 方法 |

**没有 Lombok 的话，你需要写**：
```java
public class Customer {
    private String customerId;
    private String customerName;

    // Getter
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }

    // Setter
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    // 无参构造器
    public Customer() {}

    // 全参构造器
    public Customer(String customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    // toString
    @Override
    public String toString() {
        return "Customer{customerId='" + customerId + "', customerName='" + customerName + "'}";
    }
}
```

**有了 Lombok，只需要**：
```java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {
    private String customerId;
    private String customerName;
}
```

### 4.6 完整的数据流转示例

让我们跟踪一个完整的请求：**根据 ID 查询客户**

**1. 客户端发起请求**：
```bash
curl http://localhost:8080/api/customers/CUST001
```

**2. Controller 接收请求**：
```java
// CustomerController.java
@GetMapping("/{id}")
public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
    // id = "CUST001"
    Optional<Customer> customer = customerService.findCustomerById(id);
    return customer.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

**3. Service 处理业务逻辑**：
```java
// CustomerService.java
public Optional<Customer> findCustomerById(String customerId) {
    // 调用 Repository
    return customerRepository.findById(customerId);
}
```

**4. Repository 查询数据库**：
```java
// CustomerRepository.java
// 不需要写实现，Spring Data JPA 自动生成：
// SELECT * FROM TCBS.CUSTOMERS WHERE CUSTOMER_ID = 'CUST001'
```

**5. 数据库返回结果** → Repository → Service → Controller

**6. Controller 返回 JSON**：
```json
{
  "customerId": "CUST001",
  "customerName": "张三",
  "contactPhone": "13800138000",
  "status": "ACTIVE",
  "creditLevel": "A"
}
```

**完整流程图**：
```
客户端
  ↓ HTTP GET /api/customers/CUST001
Controller (CustomerController)
  ↓ customerService.findCustomerById("CUST001")
Service (CustomerService)
  ↓ customerRepository.findById("CUST001")
Repository (CustomerRepository)
  ↓ 执行 SQL: SELECT * FROM CUSTOMERS WHERE CUSTOMER_ID = 'CUST001'
数据库 (Oracle)
  ↓ 返回数据
Repository → Service → Controller
  ↓ 自动转换成 JSON
客户端收到响应
```

---

## 第五章：自己动手写代码

现在你已经了解了项目的结构和代码，让我们自己动手写一个完整的功能：**产品管理（Product）**。

### 5.1 需求分析

我们要实现一个产品管理功能，包括：
- 查询所有产品
- 根据 ID 查询产品
- 创建产品
- 更新产品
- 删除产品

**数据库表**（假设已存在）：
```sql
-- 表名：TCBS.PRODUCTS
CREATE TABLE TCBS.PRODUCTS (
    PRODUCT_ID VARCHAR(20) PRIMARY KEY,
    PRODUCT_NAME VARCHAR(100) NOT NULL,
    PRODUCT_TYPE VARCHAR(50),
    PRICE NUMBER(10, 2),
    STATUS VARCHAR(10)
);
```

### 5.2 第一步：创建 Entity（实体类）

**文件**：`src/main/java/com/devops/course/entity/Product.java`

```java
package com.devops.course.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "PRODUCTS", schema = "TCBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {

    @Id
    @Column(name = "PRODUCT_ID", nullable = false, length = 20)
    private String productId;

    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String productName;

    @Column(name = "PRODUCT_TYPE", length = 50)
    private String productType;

    @Column(name = "PRICE")
    private BigDecimal price;

    @Column(name = "STATUS", length = 10)
    private String status;
}
```

**关键点**：
- `@Entity` + `@Table`：映射到数据库表
- `@Id`：标记主键
- `@Column`：映射字段
- Lombok 注解：自动生成 getter/setter 等

### 5.3 第二步：创建 Repository（数据访问层）

**文件**：`src/main/java/com/devops/course/repository/ProductRepository.java`

```java
package com.devops.course.repository;

import com.devops.course.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    // 根据产品类型查询
    List<Product> findByProductType(String productType);

    // 根据状态查询
    List<Product> findByStatus(String status);

    // 根据产品名称模糊查询
    List<Product> findByProductNameContaining(String name);
}
```

**关键点**：
- `extends JpaRepository<Product, String>`：继承基础 CRUD 方法
- 方法名遵循命名规范，Spring 自动生成实现

### 5.4 第三步：创建 Service（业务逻辑层）

**文件**：`src/main/java/com/devops/course/service/ProductService.java`

```java
package com.devops.course.service;

import com.devops.course.entity.Product;
import com.devops.course.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    /**
     * 查询所有产品
     */
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    /**
     * 根据ID查询产品
     */
    public Optional<Product> findProductById(String productId) {
        return productRepository.findById(productId);
    }

    /**
     * 根据产品类型查询
     */
    public List<Product> findByProductType(String productType) {
        return productRepository.findByProductType(productType);
    }

    /**
     * 保存产品
     */
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * 删除产品
     */
    @Transactional
    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }
}
```

**关键点**：
- `@Service`：标记为 Service 层
- `@Transactional(readOnly = true)`：类级别只读事务
- `@Transactional`：方法级别可写事务（用于 save 和 delete）

### 5.5 第四步：创建 Controller（控制器层）

**文件**：`src/main/java/com/devops/course/controller/ProductController.java`

```java
package com.devops.course.controller;

import com.devops.course.entity.Product;
import com.devops.course.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 查询所有产品
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 根据ID查询产品
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.findProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据产品类型查询
     * GET /api/products/type/{type}
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Product>> getProductsByType(@PathVariable String type) {
        List<Product> products = productService.findByProductType(type);
        return ResponseEntity.ok(products);
    }

    /**
     * 创建产品
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    /**
     * 更新产品
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestBody Product product) {
        product.setProductId(id);
        Product updatedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * 删除产品
     * DELETE /api/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```

**关键点**：
- `@RestController`：标记为 REST 控制器
- `@RequestMapping("/api/products")`：统一 URL 前缀
- `@GetMapping`、`@PostMapping`、`@PutMapping`、`@DeleteMapping`：HTTP 方法映射
- `ResponseEntity`：统一返回格式

### 5.6 第五步：测试 API

#### 5.6.1 启动应用

```bash
./gradlew bootRun
```

#### 5.6.2 测试查询所有产品

```bash
curl http://localhost:8080/api/products
```

#### 5.6.3 测试创建产品

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD001",
    "productName": "5G套餐",
    "productType": "DATA",
    "price": 99.00,
    "status": "ACTIVE"
  }'
```

#### 5.6.4 测试查询单个产品

```bash
curl http://localhost:8080/api/products/PROD001
```

#### 5.6.5 测试更新产品

```bash
curl -X PUT http://localhost:8080/api/products/PROD001 \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "5G畅享套餐",
    "productType": "DATA",
    "price": 129.00,
    "status": "ACTIVE"
  }'
```

#### 5.6.6 测试删除产品

```bash
curl -X DELETE http://localhost:8080/api/products/PROD001
```

### 5.7 完整的开发流程总结

每次添加新功能，按照以下步骤：

1. **创建 Entity**（实体类）
   - 映射数据库表
   - 使用 JPA 和 Lombok 注解

2. **创建 Repository**（数据访问层）
   - 继承 `JpaRepository`
   - 添加自定义查询方法

3. **创建 Service**（业务逻辑层）
   - 注入 Repository
   - 实现业务逻辑
   - 管理事务

4. **创建 Controller**（控制器层）
   - 注入 Service
   - 定义 REST API
   - 处理 HTTP 请求和响应

5. **测试**
   - 使用 `curl` 或 Postman 测试 API

---

## 第六章：调试与测试

### 6.1 在 IntelliJ IDEA 中调试

#### 6.1.1 设置断点

1. 在代码行号左侧点击，出现红点 🔴
2. 断点设置在你想暂停的地方

**示例**：
```java
@GetMapping("/{id}")
public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
    Optional<Customer> customer = customerService.findCustomerById(id);  // 👈 在这里设置断点
    return customer.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

#### 6.1.2 启动调试模式

1. 点击 IDEA 右上角的 🐞 调试按钮（Debug 'Main'）
2. 或者右键 `Main.java` → `Debug 'Main.main()'`

#### 6.1.3 触发断点

```bash
# 发起请求，触发断点
curl http://localhost:8080/api/customers/CUST001
```

程序会在断点处暂停，你可以：
- 查看变量值（鼠标悬停在变量上）
- 单步执行（F8：下一行，F7：进入方法）
- 查看调用栈
- 计算表达式

#### 6.1.4 常用调试快捷键

| 快捷键 | 作用 |
|--------|------|
| **F8** | Step Over（单步跳过，执行下一行） |
| **F7** | Step Into（单步进入，进入方法内部） |
| **Shift + F8** | Step Out（跳出当前方法） |
| **F9** | Resume（继续执行到下一个断点） |
| **Ctrl + F8** | 添加/移除断点 |

### 6.2 查看日志

Spring Boot 默认使用 Logback 记录日志。

**日志级别**：
- `TRACE`：最详细
- `DEBUG`：调试信息
- `INFO`：一般信息 ⭐ 默认级别
- `WARN`：警告
- `ERROR`：错误

**配置日志级别**（`application.yml`）：
```yaml
logging:
  level:
    root: INFO
    com.devops.course: DEBUG  # 设置项目包的日志级别为 DEBUG
    org.springframework.web: DEBUG  # 查看 Spring Web 的详细日志
    org.hibernate.SQL: DEBUG  # 查看 SQL 语句
```

**在代码中使用日志**：
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    public Optional<Customer> findCustomerById(String customerId) {
        logger.debug("查询客户，ID: {}", customerId);  // DEBUG 级别日志

        Optional<Customer> customer = customerRepository.findById(customerId);

        if (customer.isPresent()) {
            logger.info("找到客户: {}", customer.get().getCustomerName());
        } else {
            logger.warn("客户不存在，ID: {}", customerId);
        }

        return customer;
    }
}
```

**查看日志输出**：
```
2025-11-14 10:30:15.123  DEBUG 12345 --- [nio-8080-exec-1] c.d.course.service.CustomerService : 查询客户，ID: CUST001
2025-11-14 10:30:15.456  INFO  12345 --- [nio-8080-exec-1] c.d.course.service.CustomerService : 找到客户: 张三
```

### 6.3 理解错误信息

#### 6.3.1 常见错误：NullPointerException

**错误信息**：
```
java.lang.NullPointerException: Cannot invoke "com.devops.course.entity.Customer.getCustomerName()"
because the return value of "java.util.Optional.get()" is null
```

**原因**：
```java
Optional<Customer> customer = customerService.findCustomerById("NOTEXIST");
String name = customer.get().getCustomerName();  // ❌ customer 是空的！
```

**正确做法**：
```java
Optional<Customer> customer = customerService.findCustomerById("NOTEXIST");

// 方法1：使用 orElse
Customer c = customer.orElse(null);
if (c != null) {
    String name = c.getCustomerName();
}

// 方法2：使用 orElseThrow
try {
    Customer c = customer.orElseThrow(() -> new RuntimeException("客户不存在"));
    String name = c.getCustomerName();
} catch (RuntimeException e) {
    // 处理异常
}

// 方法3：使用 ifPresent
customer.ifPresent(c -> {
    String name = c.getCustomerName();
    // ... 处理 ...
});
```

#### 6.3.2 常见错误：404 Not Found

**错误信息**：
```bash
curl http://localhost:8080/api/customer/CUST001
# 返回：404 Not Found
```

**可能原因**：
1. **URL 拼写错误**：
   ```java
   // Controller 定义的是 /api/customers（复数）
   @RequestMapping("/api/customers")

   // 但是请求的是 /api/customer（单数）❌
   ```

2. **Controller 没有被扫描到**：
   - 确保 Controller 类在 `com.devops.course` 包或子包下
   - 确保有 `@RestController` 注解

3. **方法映射错误**：
   ```java
   @GetMapping("/{id}")  // 正确
   @GetMapping("/id")    // ❌ 错误，缺少 {}
   ```

#### 6.3.3 常见错误：500 Internal Server Error

**错误信息**：
```
500 Internal Server Error
```

**查看详细错误**：
1. 查看 IDEA 控制台的完整堆栈跟踪
2. 查看日志文件

**常见原因**：
1. **数据库连接失败**
2. **SQL 语法错误**
3. **空指针异常**
4. **类型转换错误**

**示例**：
```java
@PostMapping
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    // 如果 product 为 null，会抛出 NullPointerException
    // 如果数据库连接失败，会抛出 SQLException
    Product savedProduct = productService.saveProduct(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
}
```

### 6.4 使用 Postman 测试 API

Postman 是一个强大的 API 测试工具，比 `curl` 更友好。

#### 6.4.1 下载安装

访问：https://www.postman.com/downloads/

#### 6.4.2 测试 GET 请求

1. 新建请求
2. 方法选择 `GET`
3. URL 输入：`http://localhost:8080/api/customers/CUST001`
4. 点击 `Send`

#### 6.4.3 测试 POST 请求

1. 新建请求
2. 方法选择 `POST`
3. URL 输入：`http://localhost:8080/api/customers`
4. 切换到 `Body` 标签
5. 选择 `raw` → `JSON`
6. 输入 JSON 数据：
   ```json
   {
     "customerId": "CUST999",
     "customerName": "测试客户",
     "contactPhone": "13800138000"
   }
   ```
7. 点击 `Send`

---

## 第七章：常见问题与解决方案

### 7.1 项目启动问题

#### 问题1：找不到主类

**错误信息**：
```
Error: Could not find or load main class com.devops.course.Main
```

**解决方案**：
```bash
# 清理并重新构建
./gradlew clean build
```

#### 问题2：端口被占用

**错误信息**：
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案1**：修改端口（`application.yml`）
```yaml
server:
  port: 8081  # 改成其他端口
```

**解决方案2**：杀掉占用端口的进程
```bash
# macOS/Linux
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### 7.2 数据库连接问题

#### 问题1：无法连接到数据库

**错误信息**：
```
java.sql.SQLException: No suitable driver found for jdbc:oracle:thin:@//192.168.1.66:1521/DBPV
```

**解决方案**：
1. 检查 Oracle JDBC 驱动是否已添加到 `build.gradle`
2. 检查网络连接：`ping 192.168.1.66`
3. 检查数据库是否启动
4. 检查用户名和密码是否正确

#### 问题2：表不存在

**错误信息**：
```
ORA-00942: table or view does not exist
```

**解决方案**：
1. 检查表名是否正确（注意大小写）
2. 检查 schema 是否正确（`TCBS.CUSTOMERS`）
3. 确认数据库中确实有这个表

### 7.3 JSON 解析问题

#### 问题1：字段名不匹配

**请求 JSON**：
```json
{
  "customer_id": "CUST001",
  "customer_name": "张三"
}
```

**Entity 定义**：
```java
public class Customer {
    private String customerId;   // 驼峰命名
    private String customerName;
}
```

**解决方案**：使用 `@JsonProperty` 注解
```java
import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("customer_name")
    private String customerName;
}
```

或者统一使用驼峰命名：
```json
{
  "customerId": "CUST001",
  "customerName": "张三"
}
```

### 7.4 事务问题

#### 问题：修改数据后没有保存

**代码**：
```java
public void updateCustomer(String id, String newName) {
    Customer customer = customerRepository.findById(id).orElseThrow();
    customer.setCustomerName(newName);
    // ❌ 忘记调用 save()
}
```

**解决方案**：
```java
@Transactional  // 👈 添加事务注解
public void updateCustomer(String id, String newName) {
    Customer customer = customerRepository.findById(id).orElseThrow();
    customer.setCustomerName(newName);
    customerRepository.save(customer);  // 👈 调用 save()
}
```

### 7.5 依赖注入问题

#### 问题：NullPointerException 在注入的字段

**错误代码**：
```java
@RestController
public class CustomerController {
    // ❌ 忘记 @Autowired
    private CustomerService customerService;  // 这里是 null！

    @GetMapping("/customers")
    public List<Customer> getAll() {
        return customerService.findAllCustomers();  // NullPointerException!
    }
}
```

**解决方案**：
```java
@RestController
public class CustomerController {
    @Autowired  // 👈 添加注解
    private CustomerService customerService;

    @GetMapping("/customers")
    public List<Customer> getAll() {
        return customerService.findAllCustomers();
    }
}
```

**更推荐的方式**（构造器注入）：
```java
@RestController
public class CustomerController {

    private final CustomerService customerService;

    // Spring 会自动调用这个构造器并注入依赖
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public List<Customer> getAll() {
        return customerService.findAllCustomers();
    }
}
```

### 7.6 Lombok 不生效

#### 问题：找不到 getter/setter 方法

**错误信息**：
```
Cannot resolve method 'getCustomerName' in 'Customer'
```

**解决方案**：

1. **确认 Lombok 依赖已添加**（`build.gradle`）：
   ```groovy
   dependencies {
       compileOnly 'org.projectlombok:lombok'
       annotationProcessor 'org.projectlombok:lombok'
   }
   ```

2. **在 IDEA 中安装 Lombok 插件**：
   - `File` → `Settings` → `Plugins`
   - 搜索 `Lombok`
   - 安装并重启 IDEA

3. **启用注解处理**：
   - `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
   - 勾选 `Enable annotation processing`

4. **重新构建项目**：
   ```bash
   ./gradlew clean build
   ```

### 7.7 快速排查问题的方法

#### 7.7.1 三步排查法

1. **看日志**：
   - 查看控制台输出
   - 查看完整的堆栈跟踪（Stack Trace）
   - 找到第一个错误的位置

2. **加断点**：
   - 在可能出错的地方加断点
   - 查看变量值是否符合预期

3. **加日志**：
   ```java
   logger.debug("变量值: {}", variable);
   logger.debug("方法开始执行");
   logger.debug("方法执行完成，结果: {}", result);
   ```

#### 7.7.2 常见错误关键词

| 错误关键词 | 可能原因 |
|-----------|---------|
| `NullPointerException` | 变量为 null |
| `404 Not Found` | URL 错误或 Controller 未注册 |
| `500 Internal Server Error` | 服务器内部错误（看日志） |
| `No suitable driver` | 数据库驱动未添加 |
| `table or view does not exist` | 表不存在或 schema 错误 |
| `Cannot resolve method` | Lombok 未生效或方法不存在 |
| `Port already in use` | 端口被占用 |

---

## 第八章：学习资源与下一步

### 8.1 官方文档

- **Spring Boot 官方文档**：https://spring.io/projects/spring-boot
- **Spring Data JPA 官方文档**：https://spring.io/projects/spring-data-jpa
- **Lombok 官方文档**：https://projectlombok.org/

### 8.2 推荐学习路径

1. **巩固基础**（1-2周）：
   - 熟悉三层架构（Controller-Service-Repository）
   - 理解所有核心注解
   - 能够独立编写简单的 CRUD 功能

2. **进阶学习**（2-4周）：
   - 学习异常处理（`@ControllerAdvice`）
   - 学习数据验证（`@Valid`、`@NotNull`）
   - 学习分页查询（`Pageable`）
   - 学习自定义查询（`@Query`）

3. **深入研究**（1-3个月）：
   - 学习 Spring Security（安全认证）
   - 学习 Spring Boot 配置管理
   - 学习微服务架构
   - 学习性能优化

### 8.3 实战练习建议

1. **模仿现有代码**：
   - 参考 `CustomerController`，写一个 `ProductController`
   - 参考 `CustomerService`，写一个 `ProductService`

2. **扩展功能**：
   - 添加分页查询
   - 添加搜索功能
   - 添加数据验证

3. **完善错误处理**：
   - 使用 `@ControllerAdvice` 统一处理异常
   - 返回友好的错误信息

### 8.4 总结

恭喜你读完了这份指南！现在你应该能够：

✅ **理解 Spring Boot 的核心概念**：
- IoC 容器和依赖注入
- 三层架构
- RESTful API

✅ **读懂项目代码**：
- 各种注解的含义
- Controller、Service、Repository、Entity 的作用
- 数据流转过程

✅ **自己动手写代码**：
- 创建完整的 CRUD 功能
- 测试 API

✅ **调试和排查问题**：
- 使用 IDEA 调试
- 理解错误信息
- 解决常见问题

**下一步**：
1. 动手实践！编写自己的功能模块
2. 遇到问题先看日志、加断点、查文档
3. 多参考现有代码
4. 不断练习，熟能生巧

祝你学习愉快！💪

---

**文档版本**: v1.0
**最后更新**: 2025-11-14
**作者**: DevOps Course Team
**适用项目**: Claude DevOps Course

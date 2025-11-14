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

### 4.1 读懂 Main.java（入口类）

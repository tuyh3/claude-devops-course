# 实战阅读代码指南 - 从界面到代码的完整定位流程

> 🎯 **本指南定位**：面向实际工作场景，教你如何快速定位和理解代码。这不是理论学习，而是实战技巧。

---

## 📋 目录

- [前置条件](#前置条件)
- [第一章：快速上手新项目](#第一章快速上手新项目)
- [第二章：从界面功能定位到代码](#第二章从界面功能定位到代码)
- [第三章：用日志定位问题](#第三章用日志定位问题)
- [第四章：用断点调试追踪流程](#第四章用断点调试追踪流程)
- [第五章：阅读复杂代码的技巧](#第五章阅读复杂代码的技巧)
- [第六章：实战案例 - 定位一个 Bug](#第六章实战案例---定位一个-bug)

---

## 前置条件

在阅读本指南之前，你应该：

✅ 已读完《Spring Boot 零基础入门指南》（`05-spring-boot-beginner-guide.md`）
✅ 理解三层架构（Controller-Service-Repository）
✅ 理解 IoC、依赖注入等核心概念
✅ 知道基本注解的含义（@RestController、@Service 等）

**如果没有上述基础，请先阅读入门指南！**

---

## 第一章：快速上手新项目

### 1.1 场景：第一天入职，拿到项目代码

你刚入职一家公司，团队 Leader 把项目代码克隆给你：

```bash
git clone <project-url>
cd claude-devops-course
```

**问题来了**：这个项目是干什么的？代码怎么组织的？从哪里开始看？

**不要慌！按照这个5分钟快速了解流程：**

### 1.2 第1分钟：看 README.md

**目的**：了解项目是什么、做什么用

```bash
cat README.md
```

**重点看**：
1. **项目简介**：这是一个电信业务系统（TCBS + TCOA）
2. **技术栈**：Spring Boot 3.3.8 + Java 21 + Oracle 19c RAC
3. **快速开始**：如何运行项目
4. **文档位置**：`doc/` 目录

**花费时间**：1 分钟

### 1.3 第2分钟：看 build.gradle（了解依赖）

**目的**：知道项目用了哪些技术框架

```bash
cat build.gradle
```

**重点看 dependencies 部分**：

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'      // Web 框架
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa' // 数据库 ORM
    implementation 'com.oracle.database.jdbc:ojdbc11'                      // Oracle 驱动
    compileOnly 'org.projectlombok:lombok'                                 // 简化代码
    // ...
}
```

**你现在知道了**：
- ✅ 这是一个 Web 项目（有 spring-boot-starter-web）
- ✅ 用 JPA 操作数据库（有 spring-boot-starter-data-jpa）
- ✅ 数据库是 Oracle（有 ojdbc11）
- ✅ 用了 Lombok（简化 getter/setter）

**花费时间**：1 分钟

### 1.4 第3分钟：看 application.yml（了解配置）

**目的**：知道项目连接了哪些服务

```bash
cat src/main/resources/application.yml
```

**重点看**：

```yaml
server:
  port: 8080  # 应用运行在 8080 端口

spring:
  datasource:
    url: jdbc:oracle:thin:@//192.168.1.66:1521/DBPV  # Oracle 数据库地址
    username: TCBS
    driver-class-name: oracle.jdbc.OracleDriver

  jpa:
    hibernate:
      ddl-auto: none  # 不自动创建表
    show-sql: true    # 打印 SQL（开发时很有用）
```

**你现在知道了**：
- ✅ 应用运行在 `localhost:8080`
- ✅ 连接到 Oracle 数据库（192.168.1.66:1521）
- ✅ 使用的 schema 是 `TCBS`
- ✅ 开发环境会打印 SQL（方便调试）

**花费时间**：1 分钟

### 1.5 第4分钟：看目录结构

**目的**：了解代码怎么组织的

```bash
tree src/main/java/com/devops/course -L 2
```

**输出**：

```
src/main/java/com/devops/course/
├── Main.java                    # 应用入口
├── controller/                  # 控制器层（接收 HTTP 请求）
│   ├── HelloController.java
│   └── CustomerController.java
├── service/                     # 服务层（业务逻辑）
│   └── CustomerService.java
├── repository/                  # 数据访问层（数据库操作）
│   └── CustomerRepository.java
└── entity/                      # 实体类（对应数据库表）
    └── Customer.java
```

**你现在知道了**：
- ✅ 这是标准的三层架构
- ✅ Controller 层在 `controller/` 目录
- ✅ Service 层在 `service/` 目录
- ✅ Repository 层在 `repository/` 目录
- ✅ Entity 在 `entity/` 目录

**花费时间**：1 分钟

### 1.6 第5分钟：找到入口点并运行

**目的**：找到应用的启动入口，运行起来

**步骤1：找入口**

```bash
cat src/main/java/com/devops/course/Main.java
```

```java
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

**步骤2：运行项目**

```bash
./gradlew bootRun
```

**步骤3：访问第一个 API**

打开浏览器访问：`http://localhost:8080/api/hello`

```json
{
  "message": "Hello, Spring Boot!",
  "status": "success"
}
```

**恭喜！5分钟你已经对项目有基本了解了！**

**花费时间**：1 分钟

---

## 第二章：从界面功能定位到代码

> 💡 **最重要的技能**：这是日常工作中最常用的技能，必须掌握！

### 2.1 场景1：用户点击"查询客户列表"按钮

**背景**：产品经理说"客户列表页面加载很慢，帮我看看是什么问题"。

**你的任务**：找到这个功能对应的代码在哪里。

#### 步骤1：打开浏览器开发者工具

1. 打开浏览器，访问客户列表页面
2. 按 `F12` 打开开发者工具
3. 切换到 `Network`（网络）面板
4. 点击"查询客户列表"按钮

#### 步骤2：找到 HTTP 请求

在 Network 面板中看到：

```
Method: GET
URL: http://localhost:8080/api/customers
Status: 200 OK
Time: 2.35s  ← 确实慢！
```

**关键信息**：
- **请求方法**：`GET`
- **URL 路径**：`/api/customers`

#### 步骤3：根据 URL 找 Controller

**规律**：URL 路径对应 Controller 的 `@RequestMapping` 和方法的 `@GetMapping`

```java
// 找 Controller 的思路：
// URL: /api/customers
// ↓
// @RequestMapping("/api/customers")
```

**在 IDEA 中搜索**：

1. 按 `Ctrl + Shift + F`（全局搜索）
2. 搜索：`@RequestMapping("/api/customers")`
3. 找到：`CustomerController.java`

```java
@RestController
@RequestMapping("/api/customers")  // ← 找到了！
public class CustomerController {

    @GetMapping  // ← GET 方法，没有额外路径，就是 /api/customers
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.findAllCustomers();
        return ResponseEntity.ok(customers);
    }
}
```

**你现在知道了**：
- ✅ 代码位置：`CustomerController.java:getAllCustomers()` 方法
- ✅ 调用了 `customerService.findAllCustomers()`

#### 步骤4：追踪到 Service 层

点击 `findAllCustomers()`，跳转到 `CustomerService.java`：

```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();  // ← 调用 Repository
    }
}
```

#### 步骤5：追踪到 Repository 层

点击 `findAll()`，跳转到 `CustomerRepository.java`：

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // findAll() 是继承自 JpaRepository 的方法
    // 自动生成 SQL: SELECT * FROM TCBS.CUSTOMERS
}
```

**完整调用链**：

```
浏览器
  ↓ GET /api/customers
CustomerController.getAllCustomers()
  ↓ customerService.findAllCustomers()
CustomerService.findAllCustomers()
  ↓ customerRepository.findAll()
CustomerRepository.findAll()
  ↓ 执行 SQL: SELECT * FROM TCBS.CUSTOMERS
Oracle 数据库
  ↓ 返回 10000 条数据
浏览器（显示客户列表）
```

**问题分析**：
- 数据库里有 10000 个客户
- 一次性查询所有客户，没有分页
- **解决方案**：添加分页功能

**定位时间**：2 分钟

---

### 2.2 场景2：界面显示错误信息

**背景**：用户点击"创建客户"按钮后，界面弹出错误："500 Internal Server Error"

#### 步骤1：看浏览器控制台（Console）

按 `F12` → `Console` 面板，看到：

```
POST http://localhost:8080/api/customers 500 (Internal Server Error)
```

#### 步骤2：看 Network 面板的 Response

点击这个请求 → `Response` 标签页：

```json
{
  "timestamp": "2025-11-17T10:30:00.123Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "could not execute statement; SQL [insert into TCBS.CUSTOMERS ...]",
  "path": "/api/customers"
}
```

**关键信息**：
- ❌ 数据库插入失败
- ❌ SQL 语句有问题

#### 步骤3：看服务端日志

切换到 IDEA 的 `Run` 窗口（或者命令行），看到完整的堆栈跟踪：

```
2025-11-17 10:30:00.123 ERROR 12345 --- [nio-8080-exec-1] o.h.engine.jdbc.spi.SqlExceptionHelper   :
ORA-00001: unique constraint (TCBS.CUSTOMERS_PK) violated

java.sql.SQLIntegrityConstraintViolationException: ORA-00001: unique constraint (TCBS.CUSTOMERS_PK) violated
    at oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:509)
    at com.devops.course.controller.CustomerController.createCustomer(CustomerController.java:45)
    at com.devops.course.service.CustomerService.saveCustomer(CustomerService.java:32)
    at com.devops.course.repository.CustomerRepository.save(CustomerRepository.java)
```

**关键信息**：
- ❌ `ORA-00001: unique constraint violated`：主键冲突
- ❌ 在 `CustomerController.java:45` 行

#### 步骤4：定位到具体代码行

打开 `CustomerController.java`，跳转到第 45 行：

```java
@PostMapping
public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    Customer savedCustomer = customerService.saveCustomer(customer);  // ← 第45行
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
}
```

**问题分析**：
- 用户试图创建一个 `customer_id` 已存在的客户
- 数据库主键冲突
- **解决方案**：在插入前检查 ID 是否已存在

**定位时间**：3 分钟

---

### 2.3 场景3：数据不对（查询结果有问题）

**背景**：产品经理说"客户列表里显示了已删除的客户，应该只显示活跃客户"

#### 步骤1：确认当前行为

访问：`http://localhost:8080/api/customers`

返回结果包含：

```json
[
  {
    "customerId": "CUST001",
    "customerName": "张三",
    "status": "ACTIVE"  ← 活跃客户
  },
  {
    "customerId": "CUST002",
    "customerName": "李四",
    "status": "DELETED"  ← 已删除的客户（不应该显示）
  }
]
```

#### 步骤2：找到对应代码

根据 URL `/api/customers`，找到 `CustomerController.getAllCustomers()`：

```java
@GetMapping
public ResponseEntity<List<Customer>> getAllCustomers() {
    List<Customer> customers = customerService.findAllCustomers();  // ← 查询所有
    return ResponseEntity.ok(customers);
}
```

#### 步骤3：追踪到 Service

```java
public List<Customer> findAllCustomers() {
    return customerRepository.findAll();  // ← 这里查询了所有客户，包括已删除的
}
```

#### 步骤4：检查 Repository

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // findAll() 查询所有记录，不过滤 status
}
```

**问题分析**：
- 当前代码：`findAll()` 查询所有客户
- 期望行为：只查询 `status = 'ACTIVE'` 的客户
- **解决方案**：使用 `findByStatus("ACTIVE")`

**修改代码**：

```java
// Service 层
public List<Customer> findAllCustomers() {
    return customerRepository.findByStatus("ACTIVE");  // 只查询活跃客户
}

// Repository 层（添加方法）
List<Customer> findByStatus(String status);
```

**定位时间**：2 分钟

---

### 2.4 定位技巧总结

| 场景 | 定位方法 | 关键工具 |
|------|---------|---------|
| **界面功能 → 代码** | URL → Controller → Service → Repository | F12 Network 面板 |
| **界面报错** | 看 Response → 看服务端日志 → 定位代码行 | Console + 日志 |
| **数据不对** | 确认当前行为 → 追踪代码逻辑 → 找到问题点 | 断点调试 |

**核心技能**：
1. ✅ 会用浏览器 F12（Network、Console）
2. ✅ 会看服务端日志（堆栈跟踪）
3. ✅ 会追踪代码调用链（Controller → Service → Repository）

---

## 第三章：用日志定位问题

### 3.1 理解日志级别

Spring Boot 使用 Logback 记录日志，有 5 个级别：

| 级别 | 含义 | 使用场景 | 是否打印到控制台 |
|------|------|---------|----------------|
| **ERROR** | 错误 | 程序出错了，影响功能 | ✅ 默认打印 |
| **WARN** | 警告 | 有问题，但不影响运行 | ✅ 默认打印 |
| **INFO** | 信息 | 一般信息（启动、关闭等） | ✅ 默认打印 |
| **DEBUG** | 调试 | 详细信息（方法调用、变量值） | ❌ 默认不打印 |
| **TRACE** | 追踪 | 超详细信息（每一步） | ❌ 默认不打印 |

**默认级别**：INFO（只打印 INFO、WARN、ERROR）

### 3.2 如何看堆栈跟踪（Stack Trace）

**示例日志**：

```
2025-11-17 10:30:00.123 ERROR 12345 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    :
Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception
[Request processing failed; nested exception is java.lang.NullPointerException: Cannot invoke
"com.devops.course.entity.Customer.getCustomerName()" because "customer" is null]
with root cause

java.lang.NullPointerException: Cannot invoke "getCustomerName()" because "customer" is null
    at com.devops.course.service.CustomerService.processCustomer(CustomerService.java:42)
    at com.devops.course.controller.CustomerController.updateCustomer(CustomerController.java:67)
    at jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
    at java.base/java.lang.reflect.Method.invoke(Method.java:580)
    at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(...)
    ...
```

**怎么看？**

#### 步骤1：看错误类型和原因

```
java.lang.NullPointerException: Cannot invoke "getCustomerName()" because "customer" is null
```

- **错误类型**：`NullPointerException`（空指针异常）
- **原因**：`customer` 是 `null`，无法调用 `getCustomerName()`

#### 步骤2：看第一行堆栈（最接近问题的地方）

```
at com.devops.course.service.CustomerService.processCustomer(CustomerService.java:42)
```

- **类**：`CustomerService`
- **方法**：`processCustomer`
- **文件**：`CustomerService.java`
- **行号**：**第 42 行** ← 问题就在这里！

#### 步骤3：在 IDEA 中跳转

1. 点击日志中的 `CustomerService.java:42`
2. IDEA 自动跳转到第 42 行
3. 看代码：

```java
public void processCustomer(String customerId) {
    Customer customer = customerRepository.findById(customerId).orElse(null);
    String name = customer.getCustomerName();  // ← 第42行，customer 是 null！
    // ...
}
```

**问题分析**：
- `findById` 没找到客户，返回 `null`
- 直接调用 `customer.getCustomerName()` 导致 NPE
- **解决方案**：先判断 `customer` 是否为 `null`

### 3.3 根据日志定位代码行

**技巧1：看堆栈的前3行**

```
java.lang.NullPointerException: ...
    at com.devops.course.service.CustomerService.processCustomer(CustomerService.java:42)  ← 问题所在
    at com.devops.course.controller.CustomerController.updateCustomer(CustomerController.java:67)  ← 谁调用的
    at jdk.internal.reflect...  ← Spring 框架内部，忽略
```

- **第1行**：问题发生的地方（CustomerService:42）
- **第2行**：谁调用了这个方法（CustomerController:67）
- **第3行及以后**：Spring 框架内部，通常忽略

**技巧2：过滤自己的代码**

堆栈跟踪很长，只看 `com.devops.course` 包下的代码：

```
at com.devops.course.service.CustomerService.processCustomer(CustomerService.java:42)  ← 看这个
at com.devops.course.controller.CustomerController.updateCustomer(CustomerController.java:67)  ← 看这个
at org.springframework...  ← 忽略
at java.base...  ← 忽略
```

### 3.4 日志关键词搜索技巧

**场景**：日志太多，怎么快速找到错误？

**技巧1：搜索 ERROR**

```bash
# 在日志文件中搜索 ERROR
grep "ERROR" application.log
```

**技巧2：搜索异常类名**

```bash
# 搜索 NullPointerException
grep "NullPointerException" application.log

# 搜索 SQLException
grep "SQLException" application.log
```

**技巧3：搜索自己的类名**

```bash
# 搜索 CustomerService 相关的日志
grep "CustomerService" application.log
```

**技巧4：在 IDEA 中搜索日志**

1. 打开 `Run` 窗口
2. 按 `Ctrl + F` 搜索
3. 输入关键词：`ERROR`、`NullPointerException`、`CustomerService` 等

---

## 第四章：用断点调试追踪流程

### 4.1 在 IDEA 中设置断点

#### 4.1.1 基本断点

**步骤**：
1. 打开 `CustomerController.java`
2. 找到你想暂停的行
3. 点击行号左侧（出现红点🔴）

**示例**：

```java
@GetMapping("/{id}")
public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
    Optional<Customer> customer = customerService.findCustomerById(id);  // ← 在这里设置断点
    return customer.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

#### 4.1.2 启动调试模式

**方式1**：点击 IDEA 右上角的 🐞（Debug 按钮）

**方式2**：右键 `Main.java` → `Debug 'Main.main()'`

**看到**：控制台显示 "Debugger attached"

#### 4.1.3 触发断点

发起请求：

```bash
curl http://localhost:8080/api/customers/CUST001
```

**IDEA 自动跳转到断点位置**，程序暂停。

### 4.2 调试控制按钮

| 按钮 | 快捷键 | 名称 | 作用 |
|------|--------|------|------|
| ▶️ | F9 | Resume | 继续执行到下一个断点 |
| ⏸️ | - | Pause | 暂停程序 |
| 🔽 | F8 | Step Over | 单步跳过（执行下一行） |
| ⤵️ | F7 | Step Into | 单步进入（进入方法内部） |
| ⤴️ | Shift + F8 | Step Out | 跳出当前方法 |
| 🔁 | Alt + F9 | Run to Cursor | 运行到光标位置 |

### 4.3 条件断点（只在特定情况下暂停）

**场景**：循环处理 1000 个客户，但只想看 `customerId = "CUST999"` 的情况

**步骤**：
1. 右键断点（红点🔴）
2. 选择 "Edit Breakpoint..."（或按 Ctrl + Shift + F8）
3. 在 "Condition" 输入框输入：`id.equals("CUST999")`
4. 点击 "Done"

**效果**：只有当 `id` 等于 "CUST999" 时才会暂停

```java
@GetMapping("/{id}")
public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
    Optional<Customer> customer = customerService.findCustomerById(id);  // ← 条件断点：id.equals("CUST999")
    return customer.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```

### 4.4 查看调用栈（Call Stack）

**作用**：看当前方法是被谁调用的

**位置**：IDEA 左下角 "Frames" 面板

**示例**：

```
main@1
  at com.devops.course.repository.CustomerRepository.findById(CustomerRepository.java)
  at com.devops.course.service.CustomerService.findCustomerById(CustomerService.java:32)  ← 当前位置
  at com.devops.course.controller.CustomerController.getCustomerById(CustomerController.java:45)
  at jdk.internal.reflect.DirectMethodHandleAccessor.invoke(...)
```

**解读**：
1. CustomerController 调用 CustomerService
2. CustomerService 调用 CustomerRepository
3. 当前暂停在 CustomerService:32

### 4.5 查看变量值（Variables）

**位置**：IDEA 左下角 "Variables" 面板

**示例**：

```
this = CustomerService@1234
customerRepository = CustomerRepository@5678
customerId = "CUST001"  ← 方法参数
customer = Optional[Customer@9999]  ← 局部变量
```

**技巧**：
- 鼠标悬停在变量上，也能看到值
- 右键变量 → "Evaluate Expression"，可以执行表达式

---

## 第五章：阅读复杂代码的技巧

### 5.1 从接口入手（看方法签名）

**不要直接看实现细节，先看接口！**

**示例**：

```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    // 先看方法签名，理解这个方法干什么
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        // 再看实现细节
        Optional<Customer> customer = customerService.findCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
```

**从方法签名知道**：
- 输入：`String id`（客户ID）
- 输出：`ResponseEntity<Customer>`（返回客户信息或404）
- 功能：根据 ID 查询客户

### 5.2 先看主流程，忽略细节

**原则**：第一遍只看主线逻辑，不要深入细节

**错误做法**：

```java
public void processOrder(Order order) {
    // 第一遍就深入每个方法
    validateOrder(order);  // ← 点进去看实现
        validateCustomer(...);  // ← 又点进去
            checkCustomerStatus(...);  // ← 又点进去
                // ... 陷入细节，迷失方向
}
```

**正确做法**：

```java
public void processOrder(Order order) {
    // 第一遍：只看主流程，理解做了哪几步
    validateOrder(order);          // 1. 验证订单
    calculatePrice(order);         // 2. 计算价格
    reserveInventory(order);       // 3. 预留库存
    createPayment(order);          // 4. 创建支付
    sendNotification(order);       // 5. 发送通知

    // 第二遍：再深入每个方法的细节
}
```

### 5.3 用 UML 图理解类关系（IDEA 自动生成）

**步骤**：
1. 打开 `CustomerController.java`
2. 右键类名 → "Diagrams" → "Show Diagram"
3. 选择 "Java Class Diagrams"

**看到**：

```
┌─────────────────────┐
│  CustomerController │
└──────────┬──────────┘
           │ uses
           ↓
┌─────────────────────┐
│   CustomerService   │
└──────────┬──────────┘
           │ uses
           ↓
┌─────────────────────┐
│ CustomerRepository  │
└─────────────────────┘
```

**理解**：
- CustomerController 依赖 CustomerService
- CustomerService 依赖 CustomerRepository
- 典型的三层架构

### 5.4 用注释标记重点逻辑

**阅读复杂代码时，加上自己的注释！**

```java
public void processComplexBusiness(String customerId) {
    // TODO: 搞清楚这里为什么要查两次数据库
    Customer customer = repository.findById(customerId).orElseThrow();
    Customer refreshed = repository.findById(customerId).orElseThrow();

    // FIXME: 这里有性能问题，循环查询数据库
    for (Order order : customer.getOrders()) {
        OrderDetail detail = orderRepository.findById(order.getId()).orElse(null);
        // ...
    }
}
```

### 5.5 如何用 Claude Code 帮你理解代码

**场景**：看到一段复杂代码，看不懂

**做法**：

1. 选中代码
2. 右键 → "Ask Claude"（或使用快捷键）
3. 问："这段代码是干什么的？"

**Claude 会回答**：

> 这段代码实现了客户订单的批量处理逻辑：
> 1. 首先查询客户信息
> 2. 遍历客户的所有订单
> 3. 对每个订单计算总金额
> 4. 如果订单金额超过1000元，标记为VIP订单
> 5. 最后批量更新订单状态

**更多用法**：
- "这里为什么要用 Optional？"
- "这个方法有什么潜在问题吗？"
- "如何优化这段代码的性能？"

---

## 第六章：实战案例 - 定位一个 Bug

### 6.1 问题描述

**用户报告**：

> 我无法创建新客户，点击"创建客户"按钮后，页面显示"500 Internal Server Error"，但是第一次创建是成功的，第二次就失败了。

### 6.2 从用户报错开始

**步骤1：复现问题**

1. 打开浏览器，访问客户管理页面
2. 填写客户信息：
   - 客户ID：CUST888
   - 客户名称：测试客户
3. 点击"创建客户"按钮
4. **第一次**：成功创建 ✅
5. 再次填写相同信息，点击"创建客户"
6. **第二次**：报错 ❌

**确认问题可复现**。

### 6.3 看日志找异常

**步骤2：查看浏览器 Network 面板**

```
POST http://localhost:8080/api/customers
Status: 500 Internal Server Error
```

**步骤3：查看 Response**

```json
{
  "timestamp": "2025-11-17T11:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "could not execute statement",
  "path": "/api/customers"
}
```

**步骤4：查看服务端日志**

```
2025-11-17 11:00:00.123 ERROR 12345 --- [nio-8080-exec-3] o.h.engine.jdbc.spi.SqlExceptionHelper   :
ORA-00001: unique constraint (TCBS.CUSTOMERS_PK) violated

java.sql.SQLIntegrityConstraintViolationException: ORA-00001: unique constraint (TCBS.CUSTOMERS_PK) violated
    at com.devops.course.service.CustomerService.saveCustomer(CustomerService.java:54)
    at com.devops.course.controller.CustomerController.createCustomer(CustomerController.java:45)
```

**关键信息**：
- 错误：`ORA-00001: unique constraint violated`（主键冲突）
- 位置：`CustomerController.java:45`

### 6.4 打断点追踪

**步骤5：在 CustomerController.java:45 设置断点**

```java
@PostMapping
public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    Customer savedCustomer = customerService.saveCustomer(customer);  // ← 断点
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
}
```

**步骤6：启动调试模式**

点击 IDEA 的 🐞 按钮

**步骤7：再次发起请求**

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"customerId":"CUST888","customerName":"测试客户"}'
```

**程序暂停在断点**

**步骤8：查看变量值**

```
customer = Customer(customerId=CUST888, customerName=测试客户, ...)
```

**步骤9：单步进入 saveCustomer 方法**（按 F7）

```java
@Transactional
public Customer saveCustomer(Customer customer) {
    return customerRepository.save(customer);  // ← 暂停在这里
}
```

**步骤10：继续执行**（按 F9）

**抛出异常**：`SQLIntegrityConstraintViolationException`

### 6.5 定位到具体代码行

**问题位置**：`CustomerController.createCustomer()`

**问题代码**：

```java
@PostMapping
public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
    // 问题：没有检查客户ID是否已存在，直接插入
    Customer savedCustomer = customerService.saveCustomer(customer);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
}
```

### 6.6 分析问题原因

**根本原因**：
1. 用户第一次创建 CUST888：成功 ✅
2. 用户第二次创建 CUST888：数据库主键冲突 ❌
3. 代码没有检查 ID 是否已存在

**解决方案**：
1. 在插入前检查 ID 是否已存在
2. 如果已存在，返回 409 Conflict

**修改后的代码**：

```java
@PostMapping
public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
    // 检查客户ID是否已存在
    if (customerRepository.existsById(customer.getCustomerId())) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("客户ID已存在: " + customer.getCustomerId());
    }

    // 不存在才创建
    Customer savedCustomer = customerService.saveCustomer(customer);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
}
```

**测试修复**：

```bash
# 第一次创建：成功
curl -X POST ... -d '{"customerId":"CUST999",...}'
# 返回：201 Created

# 第二次创建：返回友好错误
curl -X POST ... -d '{"customerId":"CUST999",...}'
# 返回：409 Conflict "客户ID已存在: CUST999"
```

**Bug 定位和修复完成！** ✅

---

## 总结

### 核心技能清单

阅读代码能力：

- ✅ **快速上手新项目**（5分钟流程）
- ✅ **从界面定位到代码**（URL → Controller → Service → Repository）
- ✅ **用日志定位问题**（看堆栈跟踪）
- ✅ **用断点调试**（设置断点、单步执行、查看变量）
- ✅ **阅读复杂代码**（从接口入手、先看主流程）
- ✅ **实战定位 Bug**（从报错到修复的完整流程）

### 下一步

**已完成**：阅读代码能力 ✅

**继续学习**：
- 《实战编写代码指南》（`writing-code-guide.md`）
- 《团队协作指南》（`team-collaboration-guide.md`）

---

**文档版本**: v1.0
**最后更新**: 2025-11-17
**作者**: DevOps Course Team

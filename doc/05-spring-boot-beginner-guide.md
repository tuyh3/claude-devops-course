# Spring Boot 零基础入门指南（适合刚学完 Java 基础的开发者）

> 📢 **写给 Java 初学者**：如果你刚学完 Java 基础（类、对象、方法）和数据库基础（增删改查），完全不懂 Web 开发，这份文档将从零开始，手把手教你读懂这个项目，并能自己编写代码。

---

## 📋 目录

- [第零章：Web 开发基础概念（完全零基础必读）](#第零章web-开发基础概念完全零基础必读)
- [第一章：Spring Boot 是什么](#第一章spring-boot-是什么)
- [第二章：项目架构解析](#第二章项目架构解析)
- [第三章：注解详解（必看！）](#第三章注解详解必看)
- [第四章：读懂现有代码](#第四章读懂现有代码)
- [第五章：自己动手写代码](#第五章自己动手写代码)
- [第六章：调试与测试](#第六章调试与测试)
- [第七章：常见问题与解决方案](#第七章常见问题与解决方案)
- [第八章：学习资源与下一步](#第八章学习资源与下一步)

---

## 第零章：Web 开发基础概念（完全零基础必读）

> 🎓 **如果你只会 Java 基础和 SQL，从这里开始！** 这一章用最简单的语言解释 Web 开发的基本概念。

### 0.1 什么是 Web 应用程序？

**简单来说**：能用浏览器访问的程序就是 Web 应用程序。

**你每天都在用的 Web 应用**：
- 淘宝、京东（电商网站）
- 微信网页版、QQ 邮箱（通信工具）
- B站、优酷（视频网站）
- 百度、Google（搜索引擎）

**对比你之前写的 Java 程序**：

| 类型 | 运行方式 | 交互方式 | 例子 |
|------|---------|---------|------|
| **控制台程序** | 在命令行运行 | 键盘输入，黑窗口输出 | 你写的 `HelloWorld.java` |
| **Web 程序** | 在浏览器访问 | 网页界面，鼠标点击 | 淘宝、B站 |

**你之前写的 Java 程序**：
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");  // 输出到黑色命令行窗口
    }
}
// 运行：java HelloWorld
// 输出：在控制台显示 "Hello World"
```

**Web 程序**：
```java
@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";  // 输出到浏览器
    }
}
// 访问：打开浏览器，输入 http://localhost:8080/hello
// 输出：在浏览器页面显示 "Hello World"
```

**关键区别**：
- **控制台程序**：你运行一次，输出一次，程序结束
- **Web 程序**：程序一直运行，等待浏览器访问，处理请求后返回结果，继续等待

### 0.2 客户端和服务器是什么？

**用餐厅来比喻**（这是理解 Web 开发最重要的概念）：

| 角色 | Web 开发中 | 餐厅里 | 做什么 |
|------|-----------|-------|--------|
| **客户端（Client）** | 你的浏览器 | 你（顾客） | 发起请求 |
| **服务器（Server）** | 你写的 Spring Boot 程序 | 厨房 | 处理请求，返回结果 |
| **请求（Request）** | "给我客户列表" | "我要一份宫保鸡丁" | 顾客提需求 |
| **响应（Response）** | 返回客户数据 | 端上一盘菜 | 厨房交付 |

**完整流程**：
```
1. 你（客户端）：打开浏览器，输入 http://localhost:8080/api/customers
   ↓
2. 浏览器：发送请求到服务器 → "给我客户列表"
   ↓
3. 服务器（你写的 Spring Boot 程序）：
   - 收到请求
   - 从数据库查询客户数据
   - 返回数据给浏览器
   ↓
4. 浏览器：显示客户列表
```

**图示**：
```
┌─────────────────┐                    ┌──────────────────────┐
│    浏览器        │ ──── 请求 ───→   │  Spring Boot 程序   │
│  (客户端/前端)   │                    │   (服务器/后端)      │
│                 │ ←─── 响应 ────   │                      │
│  你看到的页面    │                    │    你写的代码        │
└─────────────────┘                    └──────────────────────┘
                                                  ↓
                                         ┌──────────────┐
                                         │   数据库     │
                                         │  (存储数据)   │
                                         └──────────────┘
```

**形象理解**：
- 你写的 Spring Boot 程序 = 一个一直在运行的餐厅（24小时营业）
- 浏览器访问 = 顾客来点菜
- 程序返回数据 = 厨房做好菜端出来

### 0.3 HTTP 是什么？

**HTTP = HyperText Transfer Protocol（超文本传输协议）**

**简单来说**：浏览器和服务器之间对话的"语言规则"。

**类比**：
- 你和朋友聊天要用中文或英文
- 浏览器和服务器对话要用 HTTP

**一个 HTTP 请求长什么样**：

```
GET /api/customers/CUST001 HTTP/1.1        ← 请求行
Host: localhost:8080                        ← 请求头
Content-Type: application/json              ← 请求头

（这里可以有请求体，GET 请求通常没有）
```

**分解理解**：
1. **`GET`**：动作（我要"获取"数据）
2. **`/api/customers/CUST001`**：要什么（客户 ID 为 CUST001 的数据）
3. **`Host: localhost:8080`**：找谁（本地服务器，端口 8080）

### 0.4 HTTP 方法（动词）- 就像餐厅里的不同操作

**类比：你和餐厅服务员的对话**

| HTTP 方法 | 中文含义 | 餐厅场景 | Web 场景 | 代码 |
|-----------|---------|---------|---------|------|
| **GET** | 获取/查询 | "给我看菜单" | 查询客户列表 | `@GetMapping` |
| **POST** | 创建/添加 | "点一份宫保鸡丁" | 创建新客户 | `@PostMapping` |
| **PUT** | 更新/修改 | "把这道菜换成不辣的" | 更新客户信息 | `@PutMapping` |
| **DELETE** | 删除 | "撤掉这道菜" | 删除客户 | `@DeleteMapping` |

**示例对比**：

```bash
# GET：查询所有客户（只查询，不改数据）
GET http://localhost:8080/api/customers
→ 返回：[{客户1}, {客户2}, {客户3}]

# POST：创建新客户（添加数据）
POST http://localhost:8080/api/customers
请求体：{"customerId": "CUST001", "customerName": "张三"}
→ 返回：创建成功的客户信息

# PUT：更新客户（修改数据）
PUT http://localhost:8080/api/customers/CUST001
请求体：{"customerName": "李四"}
→ 返回：更新后的客户信息

# DELETE：删除客户（删除数据）
DELETE http://localhost:8080/api/customers/CUST001
→ 返回：删除成功
```

**对应你学过的数据库操作**：

| HTTP 方法 | 对应的 SQL | 说明 |
|-----------|-----------|------|
| `GET` | `SELECT * FROM customers` | 查询数据 |
| `POST` | `INSERT INTO customers VALUES (...)` | 插入数据 |
| `PUT` | `UPDATE customers SET ... WHERE ...` | 更新数据 |
| `DELETE` | `DELETE FROM customers WHERE ...` | 删除数据 |

### 0.5 URL 是什么？怎么找到服务器？

#### 0.5.1 URL（网址）

**URL = Uniform Resource Locator（统一资源定位符）**

**简单来说**：就是网址，用来找到服务器上的某个资源。

**分解一个 URL**：
```
http://localhost:8080/api/customers/CUST001
│      │         │    │                │
│      │         │    │                └─── 具体资源（客户ID）
│      │         │    └───────────────── 路径（找哪个功能）
│      │         └────────────────────── 端口号（找哪个程序）
│      └──────────────────────────────── 主机地址（找哪台电脑）
└─────────────────────────────────────── 协议（怎么通信）
```

**类比快递地址**：
- **协议（http）**：快递方式（顺丰、邮政）
- **主机（localhost）**：省市（北京市）
- **端口（8080）**：区县（朝阳区）
- **路径（/api/customers）**：街道门牌号（某小区某栋楼）
- **资源（CUST001）**：收件人（张三）

#### 0.5.2 IP 地址和端口号

**IP 地址**：电脑在网络上的"门牌号"

| IP 地址 | 含义 |
|---------|------|
| `127.0.0.1` 或 `localhost` | 你自己的电脑（本地开发用） |
| `192.168.1.66` | 局域网内的某台电脑（如公司数据库） |
| `220.181.38.148` | 互联网上的某台电脑（如百度服务器） |

**端口号**：一台电脑上的"房间号"

一台电脑可以运行多个程序，端口号用来区分"找哪个程序"。

| 端口号 | 用途 |
|--------|------|
| `8080` | Spring Boot 默认端口（你写的程序） |
| `3306` | MySQL 数据库 |
| `1521` | Oracle 数据库 |
| `80` | HTTP 默认端口（访问网站时可以省略） |

**示例**：
```
http://localhost:8080   ← 访问本地 8080 端口的程序（你的 Spring Boot）
http://192.168.1.66:1521 ← 访问局域网 192.168.1.66 的 1521 端口（Oracle 数据库）
```

### 0.6 JSON 是什么？为什么需要它？

**JSON = JavaScript Object Notation（JavaScript 对象表示法）**

**简单来说**：一种用纯文本表示数据的格式，浏览器和服务器都能看懂。

#### 0.6.1 为什么需要 JSON？

**问题**：浏览器不懂 Java 对象！

```java
// 服务器（Java 程序）
Customer customer = new Customer();
customer.setCustomerId("CUST001");
customer.setCustomerName("张三");

// ❌ 不能直接发给浏览器，浏览器看不懂 Java 对象！
```

**解决方案**：转换成 JSON（纯文本）

```java
// 服务器：把 Java 对象转成 JSON
Customer → {"customerId":"CUST001", "customerName":"张三"}

// 浏览器：收到 JSON，显示给用户
```

#### 0.6.2 Java 对象 vs JSON

**Java 对象**（只能在 Java 程序中用）：
```java
Customer customer = new Customer();
customer.setCustomerId("CUST001");
customer.setCustomerName("张三");
customer.setContactPhone("13800138000");
```

**JSON**（浏览器、Java、Python 等都能用）：
```json
{
  "customerId": "CUST001",
  "customerName": "张三",
  "contactPhone": "13800138000"
}
```

**类比**：
- Java 对象 = 你的方言（只有同乡能听懂）
- JSON = 普通话（大家都能听懂）

#### 0.6.3 Spring Boot 自动转换 JSON

**好消息**：你不需要手动转换！Spring Boot 自动帮你做。

```java
@GetMapping("/customers/{id}")
public Customer getCustomer(@PathVariable String id) {
    Customer customer = customerService.findById(id);
    return customer;
    // ✅ Spring Boot 自动把 Customer 对象转成 JSON 返回给浏览器
}
```

**浏览器收到的 JSON**：
```json
{
  "customerId": "CUST001",
  "customerName": "张三",
  "contactPhone": "13800138000",
  "status": "ACTIVE"
}
```

#### 0.6.4 JSON 基本语法

```json
{
  "字段名1": "字符串值",          // 字符串用双引号
  "字段名2": 123,                // 数字不用引号
  "字段名3": true,               // 布尔值：true 或 false
  "字段名4": null,               // 空值
  "字段名5": [1, 2, 3],          // 数组
  "字段名6": {                   // 嵌套对象
    "子字段1": "值"
  }
}
```

### 0.7 数据库在 Web 开发中的作用

你已经学过数据库的 SQL（增删改查），现在看看在 Web 开发中怎么用。

#### 0.7.1 为什么需要数据库？

**问题**：数据存在哪里？

```java
// ❌ 错误做法：数据存在内存里
List<Customer> customers = new ArrayList<>();
customers.add(new Customer("CUST001", "张三"));
// 问题：程序重启后，数据全部丢失！

// ✅ 正确做法：数据存在数据库（硬盘）里
customerRepository.save(new Customer("CUST001", "张三"));
// 程序重启后，数据还在！
```

#### 0.7.2 数据库表 vs Java 类

**数据库表**（存储在硬盘）：
```sql
CREATE TABLE CUSTOMERS (
    CUSTOMER_ID VARCHAR(20) PRIMARY KEY,
    CUSTOMER_NAME VARCHAR(100),
    CONTACT_PHONE VARCHAR(20)
);
```

**Java 实体类**（对应数据库表）：
```java
@Entity
@Table(name = "CUSTOMERS")
public class Customer {
    @Id
    private String customerId;
    private String customerName;
    private String contactPhone;
}
```

**Spring Boot 自动映射**：
- Java 类的字段 `customerId` ↔ 数据库列 `CUSTOMER_ID`
- Java 类的字段 `customerName` ↔ 数据库列 `CUSTOMER_NAME`

#### 0.7.3 SQL vs Spring Data JPA

**你学过的 SQL**：
```sql
-- 查询所有客户
SELECT * FROM customers;

-- 根据 ID 查询
SELECT * FROM customers WHERE customer_id = 'CUST001';

-- 插入数据
INSERT INTO customers (customer_id, customer_name) VALUES ('CUST001', '张三');

-- 更新数据
UPDATE customers SET customer_name = '李四' WHERE customer_id = 'CUST001';

-- 删除数据
DELETE FROM customers WHERE customer_id = 'CUST001';
```

**Spring Boot 中（不需要写 SQL！）**：
```java
// 查询所有客户
customerRepository.findAll();

// 根据 ID 查询
customerRepository.findById("CUST001");

// 插入/更新数据（save 方法自动判断是插入还是更新）
customerRepository.save(customer);

// 删除数据
customerRepository.deleteById("CUST001");
```

**好消息**：Spring Data JPA 自动把 Java 方法转换成 SQL！

**疑问：如果有很复杂的业务 SQL 怎么办？**

不用担心！Spring Boot 提供多种方案：
1. **简单查询**：用 JPA 方法命名（如 `findByStatus`）
2. **中等复杂**：用 `@Query` 注解写 JPQL 或 SQL
3. **超级复杂**：用 MyBatis 或 JdbcTemplate

大部分公司的实际做法：
- 70% 简单 CRUD → JPA 方法命名
- 20% 中等复杂 → `@Query` 注解
- 10% 超级复杂 → MyBatis 或原生 SQL

**详细内容在第四章 4.4 节会深入讲解！**

### 0.8 什么是框架？为什么需要 Spring Boot？

#### 0.8.1 什么是框架？

**简单来说**：框架就是一个"半成品程序"，帮你快速开发。

**类比**：

| 场景 | 不用框架 | 用框架 |
|------|---------|-------|
| **盖房子** | 你要自己烧砖、拌水泥、设计图纸 | 开发商提供毛坯房，你只需装修 |
| **做饭** | 你要自己种菜、养鸡、磨面粉 | 超市买食材，你只需烹饪 |
| **写程序** | 你要处理 HTTP、数据库、JSON... | Spring Boot 处理底层，你只需写业务 |

#### 0.8.2 不用框架有多麻烦？

**示例：写一个"查询客户"功能**

**不用 Spring Boot（100+ 行代码）**：
```java
public class CustomerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // 1. 解析 URL 参数
        String customerId = request.getParameter("id");

        // 2. 手动连接数据库（每次都要写）
        Connection conn = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@//192.168.1.66:1521/DBPV",
                "TCBS",
                "password"
            );

            // 3. 手动写 SQL
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM customers WHERE customer_id = ?"
            );
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            // 4. 手动转换成 Java 对象（每个字段都要写）
            Customer customer = null;
            if (rs.next()) {
                customer = new Customer();
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setCustomerName(rs.getString("customer_name"));
                customer.setContactPhone(rs.getString("contact_phone"));
                // ... 10 个字段要写 10 行 ...
            }

            // 5. 手动转换成 JSON（容易出错）
            String json = "{\"customerId\":\"" + customer.getCustomerId() +
                          "\",\"customerName\":\"" + customer.getCustomerName() + "\"}";

            // 6. 返回响应
            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7. 手动关闭连接（忘了会内存泄漏）
            if (conn != null) conn.close();
        }
    }
}
// 还要配置 web.xml（20 行）
// 还要下载和部署 Tomcat
```

**用 Spring Boot（10 行代码）**：
```java
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

// Spring Boot 自动帮你做了：
// ✅ HTTP 请求解析
// ✅ 数据库连接管理
// ✅ SQL 自动生成和执行
// ✅ Java 对象 ↔ 数据库记录 转换
// ✅ Java 对象 ↔ JSON 转换
// ✅ 异常处理
// ✅ Tomcat 服务器启动
```

#### 0.8.3 Spring Boot 到底帮你做了什么？

| 你需要的功能 | 不用框架（纯 Java） | 用 Spring Boot |
|-------------|------------------|---------------|
| 启动 Web 服务器 | 下载 Tomcat，配置，部署（30 分钟） | `./gradlew bootRun`（1 秒） |
| 连接数据库 | 每次手写连接代码（20 行） | 配置文件写 3 行 |
| 执行 SQL | 手写 SQL + 参数绑定（10 行） | 调用方法（1 行） |
| Java 对象转 JSON | 手动拼接字符串（易出错） | 自动转换 |
| 处理 HTTP 请求 | 写 Servlet，配置 XML（50 行） | 一个注解 `@GetMapping` |
| 管理对象创建 | 到处 `new`，容易内存泄漏 | Spring 自动管理 |

### 0.9 从控制台程序到 Web 程序

**你已经会的**（Java 基础）：
```java
// 1. 定义类
public class Customer {
    private String customerId;
    private String customerName;

    // 2. 构造方法
    public Customer(String id, String name) {
        this.customerId = id;
        this.customerName = name;
    }

    // 3. Getter/Setter
    public String getCustomerId() {
        return customerId;
    }

    // 4. 方法
    public String getInfo() {
        return "Customer: " + customerName;
    }
}

// 5. 使用
public static void main(String[] args) {
    Customer c = new Customer("001", "张三");
    System.out.println(c.getInfo());  // 输出到控制台
}
```

**现在要学的**（Spring Boot Web 开发）：
```java
// 1. 实体类（对应数据库表）
@Entity
@Table(name = "CUSTOMERS")
public class Customer {
    @Id
    private String customerId;
    private String customerName;
    // Lombok 自动生成 getter/setter，不用手写
}

// 2. Repository（数据访问层，接口即可，不需要写实现）
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    // Spring Data JPA 自动实现所有方法
}

// 3. Controller（接收浏览器请求）
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable String id) {
        return customerRepository.findById(id).orElse(null);
        // 返回给浏览器，自动转成 JSON
    }
}
```

**对比理解**：

| Java 基础概念 | Spring Boot 对应概念 | 说明 |
|--------------|-------------------|------|
| 类和对象 | Entity（实体类） | 对应数据库表 |
| 方法调用 | HTTP 请求 | 浏览器调用你的方法 |
| `new` 创建对象 | `@Autowired` 注入对象 | Spring 自动创建和管理对象 |
| `System.out.println()` | `return` 返回 JSON | 输出到浏览器而不是控制台 |
| `main` 方法 | `@GetMapping` 等注解 | 程序入口 |

**最关键的区别**：

| 控制台程序 | Web 程序 |
|-----------|---------|
| 运行一次就结束 | 一直运行，等待请求 |
| 你手动调用方法 | 浏览器通过 URL 调用方法 |
| 数据输出到黑窗口 | 数据返回给浏览器 |
| 一次性筷子（用完即弃） | 餐厅（一直营业） |

### 0.10 总结：你现在应该理解的概念

读完第零章，你应该理解了：

✅ **Web 程序 vs 控制台程序**
   - 控制台程序：运行一次，输出到黑窗口
   - Web 程序：一直运行，浏览器访问，返回数据到网页

✅ **客户端和服务器**
   - 客户端（浏览器）= 餐厅顾客
   - 服务器（你的程序）= 餐厅厨房
   - 请求和响应 = 点菜和上菜

✅ **HTTP 方法**
   - GET = 查询（SELECT）
   - POST = 创建（INSERT）
   - PUT = 更新（UPDATE）
   - DELETE = 删除（DELETE）

✅ **URL、IP、端口**
   - URL = 网址（怎么找到资源）
   - IP = 电脑的门牌号
   - 端口 = 房间号（找哪个程序）

✅ **JSON**
   - Java 对象 → JSON：Spring Boot 自动转换
   - 浏览器和服务器之间传数据都用 JSON

✅ **数据库的作用**
   - 存储数据（硬盘，永久保存）
   - SQL → Spring Data JPA 方法（自动转换）

✅ **框架（Spring Boot）的作用**
   - 不用框架：100 行代码
   - 用框架：10 行代码
   - Spring Boot 自动处理 HTTP、数据库、JSON

**下一步**：现在你已经具备了学习 Spring Boot 的基础知识，继续阅读第一章，深入学习 Spring Boot！

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

#### 4.4.1 处理复杂业务 SQL（重要！）

在实际项目中，你会遇到很复杂的业务查询，JPA 方法命名根本搞不定。这时候有多种方案。

##### 方案1：使用 `@Query` 注解（推荐，80% 场景适用）

**场景1：中等复杂的 JPQL 查询**

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // 简单查询：方法命名
    List<Customer> findByStatus(String status);

    // 中等复杂：@Query + JPQL（推荐）
    @Query("SELECT c FROM Customer c " +
           "WHERE c.creditLevel = :level " +
           "AND c.createTime > :startDate " +
           "ORDER BY c.createTime DESC")
    List<Customer> findHighValueCustomers(
        @Param("level") String level,
        @Param("startDate") LocalDateTime startDate
    );

    // 联表查询（假设有关联关系）
    @Query("SELECT c FROM Customer c " +
           "JOIN c.bills b " +  // 假设 Customer 有 bills 关联
           "WHERE b.billMonth = :month " +
           "AND b.amount > :minAmount")
    List<Customer> findCustomersWithHighBills(
        @Param("month") String month,
        @Param("minAmount") BigDecimal minAmount
    );
}
```

**JPQL 的优点**：
- ✅ 面向对象，用实体类名和字段名
- ✅ 数据库无关（换数据库不用改代码）
- ✅ 类型安全

**JPQL 的缺点**：
- ❌ 不支持复杂的数据库特性（如 Oracle 的分析函数）
- ❌ 语法有限制

##### 方案2：使用原生 SQL（更灵活）

**场景2：需要数据库特定功能**

```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // 使用原生 SQL（nativeQuery = true）
    @Query(value = "SELECT * FROM TCBS.CUSTOMERS c " +
                   "WHERE c.CREDIT_LEVEL = :level " +
                   "AND EXISTS (SELECT 1 FROM TCBS.BILLS b " +
                   "            WHERE b.CUSTOMER_ID = c.CUSTOMER_ID " +
                   "            AND b.AMOUNT > :amount)",
           nativeQuery = true)
    List<Customer> findCustomersWithHighBills(
        @Param("level") String level,
        @Param("amount") BigDecimal amount
    );

    // 复杂统计查询（返回多个字段）
    @Query(value = "SELECT c.CUSTOMER_TYPE, " +
                   "       COUNT(*) as customer_count, " +
                   "       AVG(b.AMOUNT) as avg_amount " +
                   "FROM TCBS.CUSTOMERS c " +
                   "LEFT JOIN TCBS.BILLS b ON c.CUSTOMER_ID = b.CUSTOMER_ID " +
                   "GROUP BY c.CUSTOMER_TYPE",
           nativeQuery = true)
    List<Object[]> getCustomerStatistics();

    // Oracle 特定功能：分析函数
    @Query(value = "SELECT * FROM ( " +
                   "  SELECT c.*, " +
                   "         ROW_NUMBER() OVER (PARTITION BY c.CUSTOMER_TYPE ORDER BY b.AMOUNT DESC) as rn " +
                   "  FROM TCBS.CUSTOMERS c " +
                   "  JOIN TCBS.BILLS b ON c.CUSTOMER_ID = b.CUSTOMER_ID " +
                   ") WHERE rn <= 10",
           nativeQuery = true)
    List<Customer> getTop10CustomersPerType();
}
```

**原生 SQL 的优点**：
- ✅ 可以写任意复杂的 SQL
- ✅ 可以使用数据库特定功能（Oracle 分析函数、MySQL 特定语法等）
- ✅ 性能可以极致优化

**原生 SQL 的缺点**：
- ❌ 数据库相关（换数据库可能要改）
- ❌ 字段名要写数据库列名（大写）

##### 方案3：MyBatis + JPA 混用（大型项目常见）

很多公司的做法：**简单 CRUD 用 JPA，复杂查询用 MyBatis**

**配置 MyBatis**（build.gradle）：
```groovy
dependencies {
    // Spring Data JPA
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    // MyBatis
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'
}
```

**JPA Repository**（简单 CRUD）：
```java
@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    List<Customer> findByStatus(String status);
}
```

**MyBatis Mapper**（复杂查询）：
```java
@Mapper
public interface CustomerMapper {

    // 使用注解方式
    @Select("SELECT c.*, COUNT(b.bill_id) as bill_count " +
            "FROM TCBS.CUSTOMERS c " +
            "LEFT JOIN TCBS.BILLS b ON c.customer_id = b.customer_id " +
            "WHERE c.status = #{status} " +
            "GROUP BY c.customer_id")
    List<Map<String, Object>> getCustomerBillSummary(@Param("status") String status);

    // 复杂查询用 XML（推荐）
    List<Customer> complexQuery(Map<String, Object> params);
}
```

**MyBatis XML 配置**（src/main/resources/mapper/CustomerMapper.xml）：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.devops.course.mapper.CustomerMapper">

    <!-- 复杂动态查询 -->
    <select id="complexQuery" resultType="com.devops.course.entity.Customer">
        SELECT
            c.*,
            COUNT(b.bill_id) as bill_count,
            SUM(b.amount) as total_amount
        FROM TCBS.CUSTOMERS c
        LEFT JOIN TCBS.BILLS b ON c.customer_id = b.customer_id
        WHERE 1=1

        <!-- 动态条件：如果有 status 参数才加这个条件 -->
        <if test="status != null">
            AND c.status = #{status}
        </if>

        <!-- 动态条件：如果有 creditLevel 参数才加这个条件 -->
        <if test="creditLevel != null">
            AND c.credit_level = #{creditLevel}
        </if>

        <!-- 动态条件：如果有 startDate 参数才加这个条件 -->
        <if test="startDate != null">
            AND c.create_time >= #{startDate}
        </if>

        GROUP BY c.customer_id

        <!-- 动态条件：如果有 minBillCount 参数才加这个条件 -->
        <if test="minBillCount != null">
            HAVING COUNT(b.bill_id) > #{minBillCount}
        </if>

        ORDER BY total_amount DESC
    </select>

</mapper>
```

**使用 MyBatis Mapper**：
```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;  // JPA

    @Autowired
    private CustomerMapper customerMapper;          // MyBatis

    // 简单查询用 JPA
    public List<Customer> findActiveCustomers() {
        return customerRepository.findByStatus("ACTIVE");
    }

    // 复杂查询用 MyBatis
    public List<Customer> complexSearch(String status, String creditLevel, Integer minBillCount) {
        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("creditLevel", creditLevel);
        params.put("minBillCount", minBillCount);

        return customerMapper.complexQuery(params);
    }
}
```

**MyBatis 的优点**：
- ✅ XML 中写 SQL，可读性好
- ✅ 动态 SQL 超级灵活（`<if>`, `<where>`, `<foreach>` 等）
- ✅ 适合复杂报表和统计
- ✅ 学习成本低（会 SQL 就会用）

##### 方案4：JdbcTemplate（终极方案）

如果连 MyBatis 都搞不定，可以用 JdbcTemplate 直接写 JDBC 代码：

```java
@Service
public class CustomerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 复杂报表查询
    public List<Map<String, Object>> getMonthlyReport(String month) {
        String sql = "SELECT " +
                     "    c.customer_type, " +
                     "    COUNT(DISTINCT c.customer_id) as customer_count, " +
                     "    COUNT(b.bill_id) as bill_count, " +
                     "    SUM(b.amount) as total_amount, " +
                     "    AVG(b.amount) as avg_amount " +
                     "FROM TCBS.CUSTOMERS c " +
                     "LEFT JOIN TCBS.BILLS b ON c.customer_id = b.customer_id " +
                     "WHERE b.bill_month = ? " +
                     "GROUP BY c.customer_type " +
                     "ORDER BY total_amount DESC";

        return jdbcTemplate.queryForList(sql, month);
    }

    // 批量操作
    public void batchUpdateCustomerLevel() {
        String sql = "UPDATE TCBS.CUSTOMERS " +
                     "SET credit_level = " +
                     "    CASE " +
                     "        WHEN total_spent > 10000 THEN 'VIP' " +
                     "        WHEN total_spent > 5000 THEN 'GOLD' " +
                     "        ELSE 'NORMAL' " +
                     "    END";

        jdbcTemplate.update(sql);
    }
}
```

#### 4.4.2 实际开发建议（超级重要！）

**根据复杂度选择方案**：

| 场景 | 推荐方案 | 原因 | 示例 |
|------|---------|------|------|
| **简单 CRUD** | JPA 方法命名 | 最简单，0 SQL 代码 | `findByStatus` |
| **单表查询，2-3 个条件** | JPA 方法命名 | 方法名就能表达清楚 | `findByStatusAndCreditLevel` |
| **单表查询，复杂条件** | `@Query` + JPQL | 面向对象，数据库无关 | 多条件 + 排序 + 分页 |
| **联表查询（2-3 张表）** | `@Query` + JPQL | 如果实体有关联关系 | `JOIN c.bills` |
| **联表查询（没关联关系）** | `@Query(nativeQuery=true)` | 直接写 SQL | `JOIN ... ON ...` |
| **统计报表** | MyBatis XML | 动态 SQL，可读性好 | 复杂分组统计 |
| **数据库特定功能** | `@Query(nativeQuery=true)` | Oracle 分析函数等 | `ROW_NUMBER() OVER` |
| **超复杂动态查询** | MyBatis XML | `<if>` 动态条件 | 10+ 个可选条件 |
| **批量操作/性能优化** | JdbcTemplate | 最灵活，性能最好 | 批量更新 10 万条 |

**真实项目的技术选型比例**：

```
大部分公司的实际做法：
├── 60% JPA 方法命名         （简单 CRUD）
├── 20% @Query 注解          （中等复杂）
├── 15% MyBatis              （复杂业务查询）
└── 5%  JdbcTemplate         （极端场景）
```

**混用示例（推荐实践）**：

```java
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;  // JPA

    @Autowired
    private CustomerMapper customerMapper;          // MyBatis

    @Autowired
    private JdbcTemplate jdbcTemplate;              // JDBC

    // 场景1：简单 CRUD → JPA
    public List<Customer> findActiveCustomers() {
        return customerRepository.findByStatus("ACTIVE");
    }

    // 场景2：中等复杂 → @Query
    public List<Customer> findRecentHighValueCustomers(String level, LocalDateTime date) {
        return customerRepository.findHighValueCustomers(level, date);
    }

    // 场景3：复杂业务查询 → MyBatis
    public List<Customer> complexSearch(Map<String, Object> params) {
        return customerMapper.complexQuery(params);
    }

    // 场景4：性能优化/批量操作 → JdbcTemplate
    public void batchUpdateLevels() {
        String sql = "UPDATE TCBS.CUSTOMERS SET credit_level = ...";
        jdbcTemplate.update(sql);
    }
}
```

**关键建议**：
1. **从简单开始**：先用 JPA 方法命名，不够用再升级
2. **不要过度设计**：别一上来就全用 MyBatis，大部分查询 JPA 就够了
3. **性能优先**：统计报表、大数据量用原生 SQL 或 MyBatis
4. **团队统一**：团队商量好技术选型，不要每个人一套

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

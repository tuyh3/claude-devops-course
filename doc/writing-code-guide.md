# 实战编写代码指南

> 🎯 **适用场景**：当你已经能读懂代码，需要修复 Bug 或开发新功能时
> 👥 **目标读者**：掌握 Java 基础 + Spring Boot 基础，会阅读代码的开发者
> ⏱️ **阅读时长**：约 60-90 分钟
> 📌 **前置文档**：建议先阅读 [实战阅读代码指南](reading-code-guide.md)

---

## 📖 目录

- [引言：从读代码到写代码](#引言从读代码到写代码)
- [第1章：完整的 Bug 修复流程](#第1章完整的-bug-修复流程)
  - [1.1 定位 Bug（回顾）](#11-定位-bug回顾)
  - [1.2 编写修复代码](#12-编写修复代码)
  - [1.3 编写单元测试](#13-编写单元测试)
  - [1.4 手动验证](#14-手动验证)
  - [1.5 完整实战案例](#15-完整实战案例)
- [第2章：开发新功能的完整流程](#第2章开发新功能的完整流程)
  - [2.1 需求分析](#21-需求分析)
  - [2.2 技术设计](#22-技术设计)
  - [2.3 编写代码（分层开发）](#23-编写代码分层开发)
  - [2.4 编写测试](#24-编写测试)
  - [2.5 完整实战案例](#25-完整实战案例)
- [第3章：单元测试实战](#第3章单元测试实战)
  - [3.1 为什么要写单元测试](#31-为什么要写单元测试)
  - [3.2 JUnit 5 基础](#32-junit-5-基础)
  - [3.3 Mockito 模拟依赖](#33-mockito-模拟依赖)
  - [3.4 Spring Boot 测试](#34-spring-boot-测试)
  - [3.5 测试最佳实践](#35-测试最佳实践)
- [第4章：AI 辅助开发实战](#第4章ai-辅助开发实战)
  - [4.1 Claude Code 快速上手](#41-claude-code-快速上手)
  - [4.2 用 AI 学习新技术](#42-用-ai-学习新技术)
  - [4.3 用 AI 生成代码](#43-用-ai-生成代码)
  - [4.4 用 AI 审查代码](#44-用-ai-审查代码)
  - [4.5 AI 使用的最佳实践](#45-ai-使用的最佳实践)
- [第5章：Git 版本控制基础](#第5章git-版本控制基础)
  - [5.1 Git 基本概念](#51-git-基本概念)
  - [5.2 分支管理](#52-分支管理)
  - [5.3 提交代码（Commit）](#53-提交代码commit)
  - [5.4 合并分支（Merge）](#54-合并分支merge)
  - [5.5 解决冲突](#55-解决冲突)
  - [5.6 常见场景实战](#56-常见场景实战)
- [第6章：代码质量提升](#第6章代码质量提升)
  - [6.1 代码规范](#61-代码规范)
  - [6.2 异常处理](#62-异常处理)
  - [6.3 日志记录](#63-日志记录)
  - [6.4 性能优化](#64-性能优化)
  - [6.5 Code Review 要点](#65-code-review-要点)
- [第7章：常见问题与解决方案](#第7章常见问题与解决方案)
- [第8章：学习资源与下一步](#第8章学习资源与下一步)

---

## 引言：从读代码到写代码

### 本文档的定位

**阅读代码指南**（上一份文档）教你：
- ✅ 如何快速了解项目
- ✅ 如何从界面定位到代码
- ✅ 如何使用日志和断点调试
- ✅ 如何阅读复杂代码

**本文档**（编写代码指南）教你：
- 🎯 如何修复定位到的 Bug
- 🎯 如何开发新功能
- 🎯 如何编写单元测试
- 🎯 如何使用 AI 辅助开发
- 🎯 如何使用 Git 管理代码
- 🎯 如何提升代码质量

### 学习路径

```
第1步：阅读代码指南（会读）
    ↓
第2步：编写代码指南（会写）← 你现在在这里
    ↓
第3步：团队协作指南（会协作）
```

### 本文档的特点

1. **实战导向**：所有示例都基于真实项目场景
2. **完整流程**：从需求分析到代码提交的完整闭环
3. **AI 辅助**：教你如何高效使用 AI 学习和开发
4. **最佳实践**：总结业界和本项目的最佳实践

---

## 第1章：完整的 Bug 修复流程

### 1.1 定位 Bug（回顾）

假设你已经通过**阅读代码指南**定位到了问题代码：

**Bug 描述**：查询客户列表时，返回了已删除的客户

**定位结果**：
```java
// src/main/java/com/devops/course/service/CustomerService.java:25
public List<Customer> findAllCustomers() {
    return customerRepository.findAll();  // ❌ 没有过滤已删除的客户
}
```

---

### 1.2 编写修复代码

#### Step 1：分析问题

**问题根因**：
- `findAll()` 返回所有客户，包括 `status = 'DELETED'` 的客户
- 需要只返回 `status = 'ACTIVE'` 的客户

#### Step 2：确定修复方案

**方案 A**：在 Service 层过滤（❌ 不推荐）
```java
public List<Customer> findAllCustomers() {
    return customerRepository.findAll().stream()
        .filter(c -> "ACTIVE".equals(c.getStatus()))
        .collect(Collectors.toList());
    // 缺点：从数据库取了所有数据再过滤，性能差
}
```

**方案 B**：在 Repository 层添加查询方法（✅ 推荐）
```java
// 在 CustomerRepository 添加方法
List<Customer> findByStatus(String status);

// 在 Service 中调用
public List<Customer> findAllCustomers() {
    return customerRepository.findByStatus("ACTIVE");
}
```

**方案 C**：使用 @Query（✅ 推荐，更灵活）
```java
@Query("SELECT c FROM Customer c WHERE c.status = :status")
List<Customer> findActiveCustomers(@Param("status") String status);
```

#### Step 3：实现修复（选择方案 B）

**修改 CustomerRepository.java**：
```java
// src/main/java/com/devops/course/repository/CustomerRepository.java
package com.devops.course.repository;

import com.devops.course.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // ✅ 新增：根据状态查询客户
    List<Customer> findByStatus(String status);

    // 原有方法
    List<Customer> findByCustomerType(String customerType);
}
```

**修改 CustomerService.java**：
```java
// src/main/java/com/devops/course/service/CustomerService.java:25
public List<Customer> findAllCustomers() {
    // ✅ 修复：只返回活跃客户
    return customerRepository.findByStatus("ACTIVE");
}
```

#### Step 4：代码审查（自我检查）

**检查清单**：
- ✅ 是否解决了根本问题？（是，从数据库层面过滤）
- ✅ 是否引入新问题？（否，方法名清晰，参数明确）
- ✅ 是否符合项目规范？（是，使用 JPA 命名查询）
- ✅ 是否需要处理异常？（否，JPA 自动处理）
- ✅ 是否需要日志？（可选，看项目需求）

---

### 1.3 编写单元测试

修复 Bug 后，**必须**编写测试确保：
1. Bug 确实被修复
2. 未来不会再出现（回归测试）

#### Step 1：创建测试类

```java
// src/test/java/com/devops/course/service/CustomerServiceTest.java
package com.devops.course.service;

import com.devops.course.entity.Customer;
import com.devops.course.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("客户服务测试")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer activeCustomer;
    private Customer deletedCustomer;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        activeCustomer = new Customer();
        activeCustomer.setCustomerId("C001");
        activeCustomer.setCustomerName("张三");
        activeCustomer.setStatus("ACTIVE");
        activeCustomer.setCreateTime(LocalDateTime.now());

        deletedCustomer = new Customer();
        deletedCustomer.setCustomerId("C002");
        deletedCustomer.setCustomerName("李四");
        deletedCustomer.setStatus("DELETED");
        deletedCustomer.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("查询所有客户时，应该只返回活跃客户")
    void testFindAllCustomers_ShouldReturnOnlyActiveCustomers() {
        // Given: 模拟 Repository 返回活跃客户
        List<Customer> activeCustomers = Arrays.asList(activeCustomer);
        when(customerRepository.findByStatus("ACTIVE")).thenReturn(activeCustomers);

        // When: 调用服务方法
        List<Customer> result = customerService.findAllCustomers();

        // Then: 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ACTIVE", result.get(0).getStatus());
        assertEquals("张三", result.get(0).getCustomerName());

        // 验证 Repository 方法被正确调用
        verify(customerRepository, times(1)).findByStatus("ACTIVE");
    }

    @Test
    @DisplayName("查询所有客户时，不应该返回已删除客户")
    void testFindAllCustomers_ShouldNotReturnDeletedCustomers() {
        // Given: 模拟 Repository 只返回活跃客户（不包含已删除）
        List<Customer> activeCustomers = Arrays.asList(activeCustomer);
        when(customerRepository.findByStatus("ACTIVE")).thenReturn(activeCustomers);

        // When: 调用服务方法
        List<Customer> result = customerService.findAllCustomers();

        // Then: 验证结果中没有已删除客户
        assertTrue(result.stream().noneMatch(c -> "DELETED".equals(c.getStatus())));
    }
}
```

#### Step 2：运行测试

**在 IDEA 中运行**：
```
1. 右键点击测试类
2. 选择 "Run 'CustomerServiceTest'"
3. 查看测试结果（绿色 = 通过，红色 = 失败）
```

**使用 Gradle 运行**：
```bash
./gradlew test --tests CustomerServiceTest
```

**查看测试报告**：
```bash
# 测试报告位置
build/reports/tests/test/index.html
```

---

### 1.4 手动验证

单元测试通过后，还需要手动验证：

#### Step 1：启动应用

```bash
./gradlew bootRun
```

#### Step 2：使用 Postman 测试 API

**请求**：
```http
GET http://localhost:8080/api/customers
```

**期望响应**：
```json
[
  {
    "customerId": "C001",
    "customerName": "张三",
    "status": "ACTIVE",
    ...
  }
  // ✅ 不应该包含 status = "DELETED" 的客户
]
```

#### Step 3：检查日志

```log
2025-11-17 10:30:15.123 INFO  --- CustomerController : 查询所有活跃客户
2025-11-17 10:30:15.456 DEBUG --- Hibernate:
    select * from TCBS.CUSTOMERS where status = 'ACTIVE'
```

**验证要点**：
- ✅ SQL 查询包含 `WHERE status = 'ACTIVE'`
- ✅ 返回数据中没有已删除客户
- ✅ 响应时间正常

---

### 1.5 完整实战案例

**场景**：修复"客户详情页显示错误电话号码"Bug

#### 问题定位

**Bug 描述**：
- 页面显示：`138****1234`（脱敏）
- 数据库实际：`13812341234`
- 问题：脱敏逻辑错误，应显示 `138****1234`，但实际显示了 `138****1234`（最后4位错误）

**定位到代码**：
```java
// CustomerController.java:45
@GetMapping("/{id}")
public ResponseEntity<CustomerDTO> getCustomer(@PathVariable String id) {
    Customer customer = customerService.findCustomerById(id)
        .orElseThrow(() -> new ResourceNotFoundException("客户不存在"));

    CustomerDTO dto = new CustomerDTO();
    dto.setCustomerId(customer.getCustomerId());
    dto.setCustomerName(customer.getCustomerName());

    // ❌ Bug: 脱敏逻辑错误
    String phone = customer.getContactPhone();
    if (phone != null && phone.length() == 11) {
        dto.setContactPhone(phone.substring(0, 3) + "****" + phone.substring(7, 10));
        // 问题：substring(7, 10) 只取了3位，应该是 substring(7, 11)
    }

    return ResponseEntity.ok(dto);
}
```

#### 修复代码

```java
// ✅ 修复：正确的脱敏逻辑
String phone = customer.getContactPhone();
if (phone != null && phone.length() == 11) {
    dto.setContactPhone(phone.substring(0, 3) + "****" + phone.substring(7));
    // 或者更清晰：
    // dto.setContactPhone(phone.substring(0, 3) + "****" + phone.substring(7, 11));
}
```

**重构建议**：抽取为工具方法
```java
// 新建 PhoneUtils.java
public class PhoneUtils {
    /**
     * 手机号脱敏
     * @param phone 原始手机号（11位）
     * @return 脱敏后的手机号（138****1234）
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}

// 在 Controller 中使用
dto.setContactPhone(PhoneUtils.maskPhone(customer.getContactPhone()));
```

#### 编写单元测试

```java
@Test
@DisplayName("手机号脱敏应该正确显示前3位和后4位")
void testMaskPhone() {
    // Given
    String originalPhone = "13812341234";

    // When
    String maskedPhone = PhoneUtils.maskPhone(originalPhone);

    // Then
    assertEquals("138****1234", maskedPhone);
    assertEquals(11, maskedPhone.length());  // 长度不变
}

@Test
@DisplayName("非11位手机号不应该脱敏")
void testMaskPhone_InvalidLength() {
    assertEquals("12345", PhoneUtils.maskPhone("12345"));
    assertEquals(null, PhoneUtils.maskPhone(null));
}

@Test
@DisplayName("获取客户详情时，手机号应该被脱敏")
void testGetCustomer_PhoneShouldBeMasked() {
    // Given
    Customer customer = new Customer();
    customer.setCustomerId("C001");
    customer.setContactPhone("13812341234");
    when(customerRepository.findById("C001")).thenReturn(Optional.of(customer));

    // When
    ResponseEntity<CustomerDTO> response = customerController.getCustomer("C001");

    // Then
    assertEquals(200, response.getStatusCodeValue());
    CustomerDTO dto = response.getBody();
    assertNotNull(dto);
    assertEquals("138****1234", dto.getContactPhone());
}
```

#### 提交代码

```bash
# 1. 查看修改
git status
git diff

# 2. 添加修改文件
git add src/main/java/com/devops/course/controller/CustomerController.java
git add src/main/java/com/devops/course/util/PhoneUtils.java
git add src/test/java/com/devops/course/util/PhoneUtilsTest.java

# 3. 提交
git commit -m "fix: 修复客户详情手机号脱敏错误

- 修复 substring 索引错误（应为 7-11，而非 7-10）
- 抽取 PhoneUtils.maskPhone() 工具方法
- 添加单元测试覆盖正常和异常情况

Issue: #123"

# 4. 推送到远程（如果在 feature 分支）
git push origin feature/fix-phone-mask
```

---

## 第2章：开发新功能的完整流程

### 2.1 需求分析

**需求示例**：
> 需求 #234：支持按信用等级批量查询客户，并统计每个等级的客户数量

#### Step 1：理解需求

**核心问题**：
1. 输入是什么？→ 信用等级列表（如：`["A", "B"]`）
2. 输出是什么？→ 客户列表 + 每个等级的数量统计
3. 谁会用？→ 客户管理人员（通过 Web 界面）
4. 有什么限制？→ 只查询活跃客户，按等级分组

#### Step 2：需求拆解

**功能点**：
1. API 接口：`POST /api/customers/search-by-credit`
2. 请求参数：`{ "creditLevels": ["A", "B", "C"] }`
3. 响应数据：
```json
{
  "customers": [ /* 客户列表 */ ],
  "statistics": {
    "A": 10,
    "B": 20,
    "C": 5
  },
  "total": 35
}
```

#### Step 3：技术分析

**涉及的层次**：
- ✅ Entity：已有 `Customer`
- ✅ Repository：需要新增查询方法
- ✅ Service：需要新增业务逻辑
- ✅ Controller：需要新增 API 端点
- ✅ DTO：需要新增请求和响应对象

**难点分析**：
- 批量查询：`WHERE credit_level IN (...)`
- 统计分组：`GROUP BY credit_level`

---

### 2.2 技术设计

#### Step 1：设计数据库查询

**方案 A**：两次查询（❌ 不推荐）
```java
// 第1次：查询客户列表
List<Customer> customers = repository.findByCreditLevelIn(levels);

// 第2次：分组统计
Map<String, Long> stats = customers.stream()
    .collect(Collectors.groupingBy(Customer::getCreditLevel, Collectors.counting()));
```

**方案 B**：一次查询返回列表，内存分组（✅ 推荐，数据量小时）
```java
List<Customer> customers = repository.findByCreditLevelInAndStatus(levels, "ACTIVE");
// 内存中统计
```

**方案 C**：使用原生 SQL（✅ 推荐，性能最优）
```sql
-- 一次查询同时返回列表和统计
SELECT credit_level, COUNT(*) as count
FROM TCBS.CUSTOMERS
WHERE credit_level IN ('A', 'B', 'C')
  AND status = 'ACTIVE'
GROUP BY credit_level
```

**本例选择方案 B**（简单场景）

#### Step 2：设计类结构

**新增文件**：
```
src/main/java/com/devops/course/
├── controller/
│   └── CustomerController.java  (新增方法)
├── service/
│   └── CustomerService.java     (新增方法)
├── repository/
│   └── CustomerRepository.java  (新增方法)
└── dto/
    ├── CreditSearchRequest.java (新建)
    └── CreditSearchResponse.java (新建)
```

---

### 2.3 编写代码（分层开发）

#### Step 1：创建 DTO（数据传输对象）

```java
// src/main/java/com/devops/course/dto/CreditSearchRequest.java
package com.devops.course.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 按信用等级搜索请求
 */
@Data
public class CreditSearchRequest {

    @NotEmpty(message = "信用等级列表不能为空")
    private List<String> creditLevels;
}
```

```java
// src/main/java/com/devops/course/dto/CreditSearchResponse.java
package com.devops.course.dto;

import com.devops.course.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 按信用等级搜索响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditSearchResponse {

    /**
     * 客户列表
     */
    private List<Customer> customers;

    /**
     * 统计信息：{信用等级: 客户数量}
     */
    private Map<String, Long> statistics;

    /**
     * 总数
     */
    private long total;
}
```

#### Step 2：Repository 层（数据访问）

```java
// src/main/java/com/devops/course/repository/CustomerRepository.java
package com.devops.course.repository;

import com.devops.course.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    // 已有方法
    List<Customer> findByStatus(String status);
    List<Customer> findByCustomerType(String customerType);

    /**
     * ✅ 新增：根据信用等级列表和状态查询客户
     *
     * 方法名规则（Spring Data JPA 自动实现）：
     * - findBy: 查询
     * - CreditLevel: 字段名
     * - In: SQL IN 操作符
     * - And: SQL AND
     * - Status: 另一个字段名
     *
     * 生成的 SQL：
     * SELECT * FROM TCBS.CUSTOMERS
     * WHERE credit_level IN (?, ?, ...)
     *   AND status = ?
     */
    List<Customer> findByCreditLevelInAndStatus(List<String> creditLevels, String status);
}
```

**JPA 方法命名规则**（自动生成 SQL）：

| 方法名 | SQL |
|--------|-----|
| `findByName(String name)` | `WHERE name = ?` |
| `findByNameAndAge(String name, int age)` | `WHERE name = ? AND age = ?` |
| `findByAgeGreaterThan(int age)` | `WHERE age > ?` |
| `findByNameLike(String pattern)` | `WHERE name LIKE ?` |
| `findByIdIn(List<Long> ids)` | `WHERE id IN (?, ?, ...)` |
| `findByOrderByNameAsc()` | `ORDER BY name ASC` |

#### Step 3：Service 层（业务逻辑）

```java
// src/main/java/com/devops/course/service/CustomerService.java
package com.devops.course.service;

import com.devops.course.dto.CreditSearchResponse;
import com.devops.course.entity.Customer;
import com.devops.course.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // ... 已有方法 ...

    /**
     * ✅ 新增：按信用等级批量查询并统计
     *
     * @param creditLevels 信用等级列表（如：["A", "B", "C"]）
     * @return 客户列表 + 统计信息
     */
    public CreditSearchResponse searchByCredit(List<String> creditLevels) {
        // 1. 查询客户列表（只查询活跃客户）
        List<Customer> customers = customerRepository
            .findByCreditLevelInAndStatus(creditLevels, "ACTIVE");

        // 2. 统计每个等级的数量
        Map<String, Long> statistics = customers.stream()
            .collect(Collectors.groupingBy(
                Customer::getCreditLevel,    // 分组键：信用等级
                Collectors.counting()         // 聚合操作：计数
            ));

        // 3. 计算总数
        long total = customers.size();

        // 4. 封装响应
        return new CreditSearchResponse(customers, statistics, total);
    }
}
```

**代码解析**：

```java
// Stream API 详解
customers.stream()
    .collect(Collectors.groupingBy(
        Customer::getCreditLevel,  // 等价于：c -> c.getCreditLevel()
        Collectors.counting()      // 等价于：计算每组的数量
    ));

// 等价的传统写法：
Map<String, Long> statistics = new HashMap<>();
for (Customer customer : customers) {
    String level = customer.getCreditLevel();
    statistics.put(level, statistics.getOrDefault(level, 0L) + 1);
}
```

#### Step 4：Controller 层（API 接口）

```java
// src/main/java/com/devops/course/controller/CustomerController.java
package com.devops.course.controller;

import com.devops.course.dto.CreditSearchRequest;
import com.devops.course.dto.CreditSearchResponse;
import com.devops.course.entity.Customer;
import com.devops.course.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ... 已有方法 ...

    /**
     * ✅ 新增：按信用等级批量查询客户
     *
     * 请求示例：
     * POST /api/customers/search-by-credit
     * {
     *   "creditLevels": ["A", "B", "C"]
     * }
     *
     * 响应示例：
     * {
     *   "customers": [ /* 客户列表 */ ],
     *   "statistics": {
     *     "A": 10,
     *     "B": 20,
     *     "C": 5
     *   },
     *   "total": 35
     * }
     *
     * @param request 搜索请求（包含信用等级列表）
     * @return 客户列表和统计信息
     */
    @PostMapping("/search-by-credit")
    public ResponseEntity<CreditSearchResponse> searchByCredit(
            @Valid @RequestBody CreditSearchRequest request) {

        CreditSearchResponse response = customerService
            .searchByCredit(request.getCreditLevels());

        return ResponseEntity.ok(response);
    }
}
```

**注解说明**：
- `@PostMapping("/search-by-credit")`：处理 POST 请求
- `@Valid`：触发参数校验（检查 `@NotEmpty`）
- `@RequestBody`：从 HTTP 请求体中解析 JSON

---

### 2.4 编写测试

#### Step 1：Repository 层测试（可选）

```java
// src/test/java/com/devops/course/repository/CustomerRepositoryTest.java
package com.devops.course.repository;

import com.devops.course.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest  // 自动配置内存数据库（H2）用于测试
@DisplayName("客户 Repository 测试")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("应该能按信用等级和状态查询客户")
    void testFindByCreditLevelInAndStatus() {
        // Given: 准备测试数据
        Customer c1 = new Customer();
        c1.setCustomerId("C001");
        c1.setCustomerName("张三");
        c1.setCreditLevel("A");
        c1.setStatus("ACTIVE");
        customerRepository.save(c1);

        Customer c2 = new Customer();
        c2.setCustomerId("C002");
        c2.setCustomerName("李四");
        c2.setCreditLevel("B");
        c2.setStatus("ACTIVE");
        customerRepository.save(c2);

        Customer c3 = new Customer();
        c3.setCustomerId("C003");
        c3.setCustomerName("王五");
        c3.setCreditLevel("C");
        c3.setStatus("DELETED");  // 已删除，不应该被查到
        customerRepository.save(c3);

        // When: 执行查询
        List<String> levels = Arrays.asList("A", "B", "C");
        List<Customer> result = customerRepository.findByCreditLevelInAndStatus(levels, "ACTIVE");

        // Then: 验证结果
        assertEquals(2, result.size());  // 只有2个活跃客户
        assertTrue(result.stream().allMatch(c -> "ACTIVE".equals(c.getStatus())));
    }
}
```

#### Step 2：Service 层测试

```java
// src/test/java/com/devops/course/service/CustomerServiceTest.java
@Test
@DisplayName("按信用等级查询应该返回正确的统计信息")
void testSearchByCredit() {
    // Given: 准备测试数据
    Customer c1 = createCustomer("C001", "A");
    Customer c2 = createCustomer("C002", "A");
    Customer c3 = createCustomer("C003", "B");
    List<Customer> mockCustomers = Arrays.asList(c1, c2, c3);

    when(customerRepository.findByCreditLevelInAndStatus(
        Arrays.asList("A", "B"), "ACTIVE"))
        .thenReturn(mockCustomers);

    // When: 调用服务方法
    CreditSearchResponse response = customerService
        .searchByCredit(Arrays.asList("A", "B"));

    // Then: 验证结果
    assertNotNull(response);
    assertEquals(3, response.getTotal());
    assertEquals(3, response.getCustomers().size());

    // 验证统计信息
    Map<String, Long> stats = response.getStatistics();
    assertEquals(2L, stats.get("A"));  // A等级2个
    assertEquals(1L, stats.get("B"));  // B等级1个
}

private Customer createCustomer(String id, String creditLevel) {
    Customer c = new Customer();
    c.setCustomerId(id);
    c.setCreditLevel(creditLevel);
    c.setStatus("ACTIVE");
    return c;
}
```

#### Step 3：Controller 层测试（集成测试）

```java
// src/test/java/com/devops/course/controller/CustomerControllerTest.java
package com.devops.course.controller;

import com.devops.course.dto.CreditSearchRequest;
import com.devops.course.dto.CreditSearchResponse;
import com.devops.course.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@DisplayName("客户 Controller 测试")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @Test
    @DisplayName("POST /api/customers/search-by-credit 应该返回正确的JSON")
    void testSearchByCredit() throws Exception {
        // Given: 准备测试数据
        Map<String, Long> stats = new HashMap<>();
        stats.put("A", 10L);
        stats.put("B", 20L);

        CreditSearchResponse mockResponse = new CreditSearchResponse(
            Arrays.asList(),  // 空列表（简化测试）
            stats,
            30L
        );

        when(customerService.searchByCredit(anyList()))
            .thenReturn(mockResponse);

        // When & Then: 发送请求并验证响应
        CreditSearchRequest request = new CreditSearchRequest();
        request.setCreditLevels(Arrays.asList("A", "B"));

        mockMvc.perform(post("/api/customers/search-by-credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(30))
            .andExpect(jsonPath("$.statistics.A").value(10))
            .andExpect(jsonPath("$.statistics.B").value(20));
    }

    @Test
    @DisplayName("信用等级列表为空时应该返回400错误")
    void testSearchByCredit_EmptyList() throws Exception {
        CreditSearchRequest request = new CreditSearchRequest();
        request.setCreditLevels(Arrays.asList());  // 空列表

        mockMvc.perform(post("/api/customers/search-by-credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());  // 400 Bad Request
    }
}
```

#### Step 4：运行所有测试

```bash
# 运行所有测试
./gradlew test

# 只运行某个测试类
./gradlew test --tests CustomerServiceTest

# 查看测试覆盖率报告
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

---

### 2.5 完整实战案例

**需求**：支持导出客户列表为 CSV 文件

#### 分析需求

**功能**：
- API：`GET /api/customers/export?format=csv`
- 参数：`format`（暂时只支持 CSV）
- 响应：文件下载

**技术点**：
- CSV 生成
- HTTP 文件下载响应
- 中文乱码处理

#### 实现代码

**Step 1：添加 CSV 工具依赖**

```groovy
// build.gradle
dependencies {
    implementation 'com.opencsv:opencsv:5.9'  // CSV 库
    // ... 其他依赖
}
```

**Step 2：Service 层（生成 CSV）**

```java
// CustomerService.java
public String exportCustomersToCSV() {
    List<Customer> customers = customerRepository.findByStatus("ACTIVE");

    StringWriter writer = new StringWriter();
    try (CSVWriter csvWriter = new CSVWriter(writer)) {
        // 写入表头
        String[] header = {"客户ID", "客户名称", "联系电话", "信用等级", "创建时间"};
        csvWriter.writeNext(header);

        // 写入数据行
        for (Customer c : customers) {
            String[] row = {
                c.getCustomerId(),
                c.getCustomerName(),
                c.getContactPhone(),
                c.getCreditLevel(),
                c.getCreateTime().toString()
            };
            csvWriter.writeNext(row);
        }
    } catch (IOException e) {
        throw new RuntimeException("导出CSV失败", e);
    }

    return writer.toString();
}
```

**Step 3：Controller 层（文件下载）**

```java
// CustomerController.java
@GetMapping("/export")
public ResponseEntity<byte[]> exportCustomers(
        @RequestParam(defaultValue = "csv") String format) {

    if (!"csv".equalsIgnoreCase(format)) {
        return ResponseEntity.badRequest().build();
    }

    String csvContent = customerService.exportCustomersToCSV();

    // 转换为字节数组（使用 UTF-8 BOM 解决 Excel 乱码）
    byte[] bytes;
    try {
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = csvContent.getBytes(StandardCharsets.UTF_8);
        bytes = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, bytes, 0, bom.length);
        System.arraycopy(content, 0, bytes, bom.length, content.length);
    } catch (Exception e) {
        return ResponseEntity.status(500).build();
    }

    // 设置响应头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("text/csv"));
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename("customers.csv", StandardCharsets.UTF_8)
        .build());

    return ResponseEntity.ok()
        .headers(headers)
        .body(bytes);
}
```

**Step 4：测试**

```java
@Test
@DisplayName("导出CSV应该包含正确的表头和数据")
void testExportCustomersToCSV() {
    // Given
    Customer c = new Customer();
    c.setCustomerId("C001");
    c.setCustomerName("张三");
    c.setContactPhone("13812341234");
    c.setCreditLevel("A");
    c.setCreateTime(LocalDateTime.of(2025, 1, 1, 10, 0));
    when(customerRepository.findByStatus("ACTIVE"))
        .thenReturn(Arrays.asList(c));

    // When
    String csv = customerService.exportCustomersToCSV();

    // Then
    assertTrue(csv.contains("客户ID"));  // 包含表头
    assertTrue(csv.contains("C001"));   // 包含数据
    assertTrue(csv.contains("张三"));
}
```

**手动测试**：
```bash
# 浏览器访问
http://localhost:8080/api/customers/export?format=csv

# 或使用 curl
curl -O http://localhost:8080/api/customers/export?format=csv
```

---

## 第3章：单元测试实战

### 3.1 为什么要写单元测试

#### 真实场景对比

**不写测试的开发流程**：
```
1. 写代码（30分钟）
2. 启动应用（2分钟）
3. 手动测试（10分钟）
4. 发现Bug → 修改代码
5. 重新启动应用（2分钟）
6. 再次手动测试（10分钟）
7. 重复3-6...
```
**总耗时**：60+ 分钟，且无法保证质量

**写测试的开发流程**：
```
1. 写测试用例（10分钟）
2. 写代码（30分钟）
3. 运行测试（5秒）✅ 全部通过
4. 手动验证（5分钟，仅验证UI）
```
**总耗时**：45 分钟，质量更高

#### 单元测试的价值

1. **快速反馈**：5秒运行，无需启动整个应用
2. **回归测试**：确保修改不会破坏已有功能
3. **文档作用**：测试代码展示了如何使用API
4. **重构信心**：有测试保护，放心重构
5. **强制思考**：写测试时会发现设计问题

#### 测试金字塔

```
         /\
        /  \  E2E 测试（少量）
       /____\
      /      \
     / 集成测试 \ （适量）
    /___________\
   /             \
  /  单元测试     \ （大量）
 /_________________\

推荐比例：70% 单元测试 + 20% 集成测试 + 10% E2E 测试
```

---

### 3.2 JUnit 5 基础

JUnit 5 是 Java 最流行的测试框架。

#### 基本注解

```java
import org.junit.jupiter.api.*;

class MyTest {

    @BeforeAll
    static void beforeAll() {
        // 所有测试开始前执行一次（必须是static）
        System.out.println("测试类初始化");
    }

    @BeforeEach
    void beforeEach() {
        // 每个测试方法执行前都会执行
        System.out.println("准备测试数据");
    }

    @Test
    @DisplayName("测试加法功能")
    void testAdd() {
        int result = 1 + 1;
        assertEquals(2, result);
    }

    @Test
    @Disabled("暂时跳过这个测试")
    void testSkip() {
        // 这个测试不会执行
    }

    @AfterEach
    void afterEach() {
        // 每个测试方法执行后都会执行
        System.out.println("清理测试数据");
    }

    @AfterAll
    static void afterAll() {
        // 所有测试结束后执行一次（必须是static）
        System.out.println("测试类销毁");
    }
}
```

**执行顺序**：
```
beforeAll()
    beforeEach()
    testAdd()
    afterEach()

    beforeEach()
    testSkip()  ← 被跳过
    afterEach()
afterAll()
```

#### 常用断言（Assertions）

```java
import static org.junit.jupiter.api.Assertions.*;

@Test
void testAssertions() {
    // 1. 基本断言
    assertEquals(2, 1 + 1);                    // 相等
    assertNotEquals(3, 1 + 1);                 // 不相等
    assertTrue(5 > 3);                         // 为真
    assertFalse(5 < 3);                        // 为假
    assertNull(null);                          // 为null
    assertNotNull("hello");                    // 不为null

    // 2. 数组/集合断言
    assertArrayEquals(new int[]{1,2}, new int[]{1,2});
    assertIterableEquals(List.of(1,2), List.of(1,2));

    // 3. 异常断言
    Exception ex = assertThrows(
        IllegalArgumentException.class,
        () -> { throw new IllegalArgumentException("错误"); }
    );
    assertEquals("错误", ex.getMessage());

    // 4. 超时断言
    assertTimeout(Duration.ofSeconds(1), () -> {
        Thread.sleep(500);  // 必须在1秒内完成
    });

    // 5. 组合断言
    assertAll("客户信息校验",
        () -> assertEquals("张三", customer.getName()),
        () -> assertEquals("13812341234", customer.getPhone()),
        () -> assertEquals("A", customer.getCreditLevel())
    );
}
```

#### 参数化测试

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

class ParameterizedTests {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("测试多个数字是否都大于0")
    void testPositive(int num) {
        assertTrue(num > 0);
    }

    @ParameterizedTest
    @CsvSource({
        "A, 优秀",
        "B, 良好",
        "C, 及格"
    })
    @DisplayName("测试信用等级映射")
    void testCreditMapping(String level, String description) {
        String result = mapCreditLevel(level);
        assertEquals(description, result);
    }
}
```

---

### 3.3 Mockito 模拟依赖

在单元测试中，我们需要**隔离**被测试的类，不真正调用数据库、网络等外部依赖。

#### 为什么需要 Mock

**问题场景**：测试 `CustomerService`
```java
@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;  // 依赖数据库

    public Customer findById(String id) {
        return repository.findById(id).orElse(null);
    }
}
```

**不使用 Mock 的问题**：
- ❌ 需要真实数据库
- ❌ 测试慢（数据库IO）
- ❌ 测试不稳定（数据库状态变化）

**使用 Mock 的好处**：
- ✅ 不需要数据库
- ✅ 测试快（内存操作）
- ✅ 测试稳定（Mock 返回固定值）

#### Mockito 基本用法

```java
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;  // 创建 Mock 对象

    @InjectMocks
    private CustomerService service;  // 自动注入 Mock 对象

    @Test
    void testFindById() {
        // Given: 模拟 Repository 的行为
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId("C001");
        mockCustomer.setCustomerName("张三");

        when(repository.findById("C001"))
            .thenReturn(Optional.of(mockCustomer));

        // When: 调用被测试的方法
        Customer result = service.findById("C001");

        // Then: 验证结果
        assertNotNull(result);
        assertEquals("张三", result.getCustomerName());

        // 验证 Repository 方法被调用了1次
        verify(repository, times(1)).findById("C001");
    }
}
```

#### Mock 高级用法

```java
@Test
void testMockAdvanced() {
    // 1. 返回不同的值（多次调用）
    when(repository.findById("C001"))
        .thenReturn(Optional.of(customer1))   // 第1次调用返回customer1
        .thenReturn(Optional.of(customer2))   // 第2次调用返回customer2
        .thenReturn(Optional.empty());        // 第3次调用返回empty

    // 2. 抛出异常
    when(repository.findById("C999"))
        .thenThrow(new RuntimeException("数据库连接失败"));

    // 3. 参数匹配器
    when(repository.findById(anyString()))       // 任意字符串
        .thenReturn(Optional.of(customer));

    when(repository.findByStatus(eq("ACTIVE")))  // 精确匹配
        .thenReturn(List.of(customer));

    // 4. 模拟 void 方法
    doNothing().when(repository).deleteById("C001");

    // 5. 模拟 void 方法抛异常
    doThrow(new RuntimeException("删除失败"))
        .when(repository).deleteById("C999");

    // 6. 验证方法被调用
    verify(repository).findById("C001");           // 至少1次
    verify(repository, times(2)).findById("C001"); // 恰好2次
    verify(repository, atLeastOnce()).findById("C001");  // 至少1次
    verify(repository, never()).deleteById("C001");      // 从未调用

    // 7. 验证参数
    verify(repository).findById(argThat(id -> id.startsWith("C")));
}
```

#### Spy vs Mock

```java
@Test
void testSpyVsMock() {
    // Mock: 完全模拟的对象，所有方法都需要when()指定返回值
    List<String> mockList = mock(List.class);
    when(mockList.size()).thenReturn(100);
    System.out.println(mockList.size());  // 100
    System.out.println(mockList.get(0));  // null（未指定返回值）

    // Spy: 部分模拟的对象，未指定的方法会调用真实方法
    List<String> spyList = spy(new ArrayList<>());
    spyList.add("a");
    spyList.add("b");
    when(spyList.size()).thenReturn(100);
    System.out.println(spyList.size());  // 100（被Mock）
    System.out.println(spyList.get(0));  // "a"（真实方法）
}
```

---

### 3.4 Spring Boot 测试

#### 测试注解选择

| 测试类型 | 注解 | 加载内容 | 速度 | 适用场景 |
|---------|------|---------|------|---------|
| 单元测试 | `@ExtendWith(MockitoExtension.class)` | 无 | 最快 | Service, Util |
| Repository 测试 | `@DataJpaTest` | JPA + H2数据库 | 快 | Repository |
| Controller 测试 | `@WebMvcTest` | Spring MVC | 快 | Controller |
| 集成测试 | `@SpringBootTest` | 完整Spring容器 | 慢 | 端到端测试 |

#### Repository 测试

```java
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest  // 自动配置H2内存数据库
@ActiveProfiles("test")  // 使用test配置文件
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private TestEntityManager entityManager;  // 用于准备测试数据

    @Test
    void testFindByStatus() {
        // Given: 准备测试数据
        Customer customer = new Customer();
        customer.setCustomerId("C001");
        customer.setStatus("ACTIVE");
        entityManager.persist(customer);
        entityManager.flush();

        // When: 执行查询
        List<Customer> result = repository.findByStatus("ACTIVE");

        // Then: 验证结果
        assertEquals(1, result.size());
        assertEquals("C001", result.get(0).getCustomerId());
    }
}
```

**测试配置文件**（`src/test/resources/application-test.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb  # 使用H2内存数据库
  jpa:
    hibernate:
      ddl-auto: create-drop  # 每次测试后删除表
    show-sql: true  # 显示SQL（调试用）
```

#### Controller 测试

```java
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)  // 只加载CustomerController
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;  // 模拟HTTP请求

    @MockBean
    private CustomerService service;  // Mock Service层

    @Test
    void testGetAllCustomers() throws Exception {
        // Given
        Customer customer = new Customer();
        customer.setCustomerId("C001");
        customer.setCustomerName("张三");
        when(service.findAllCustomers())
            .thenReturn(List.of(customer));

        // When & Then
        mockMvc.perform(get("/api/customers"))
            .andExpect(status().isOk())                    // 状态码200
            .andExpect(jsonPath("$[0].customerId").value("C001"))
            .andExpect(jsonPath("$[0].customerName").value("张三"));
    }

    @Test
    void testCreateCustomer() throws Exception {
        // Given
        String requestBody = """
            {
                "customerId": "C001",
                "customerName": "张三",
                "contactPhone": "13812341234"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())  // 201
            .andExpect(header().exists("Location"));
    }
}
```

#### 集成测试

```java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;  // 真实的HTTP客户端

    @Test
    void testFullFlow() {
        // 1. 创建客户
        Customer customer = new Customer();
        customer.setCustomerId("C001");
        customer.setCustomerName("张三");

        ResponseEntity<Customer> createResponse = restTemplate.postForEntity(
            "/api/customers", customer, Customer.class);
        assertEquals(201, createResponse.getStatusCodeValue());

        // 2. 查询客户
        ResponseEntity<Customer> getResponse = restTemplate.getForEntity(
            "/api/customers/C001", Customer.class);
        assertEquals(200, getResponse.getStatusCodeValue());
        assertEquals("张三", getResponse.getBody().getCustomerName());

        // 3. 删除客户
        restTemplate.delete("/api/customers/C001");

        // 4. 验证删除
        ResponseEntity<Customer> deleteCheck = restTemplate.getForEntity(
            "/api/customers/C001", Customer.class);
        assertEquals(404, deleteCheck.getStatusCodeValue());
    }
}
```

---

### 3.5 测试最佳实践

#### 测试命名规范

```java
// ❌ 不好的命名
@Test
void test1() { }

// ✅ 好的命名（方法名）
@Test
void findByIdShouldReturnCustomerWhenIdExists() { }

// ✅ 更好的命名（使用@DisplayName）
@Test
@DisplayName("根据ID查询客户时，如果ID存在应该返回客户对象")
void testFindById() { }

// ✅ 最佳命名（中文描述，英文方法名）
@Test
@DisplayName("当客户ID不存在时，应该抛出ResourceNotFoundException")
void shouldThrowExceptionWhenCustomerNotFound() { }
```

#### AAA 模式（Arrange-Act-Assert）

```java
@Test
void testUpdateCustomer() {
    // Arrange（准备）：准备测试数据和环境
    Customer existingCustomer = new Customer();
    existingCustomer.setCustomerId("C001");
    existingCustomer.setCustomerName("张三");
    when(repository.findById("C001"))
        .thenReturn(Optional.of(existingCustomer));

    // Act（执行）：调用被测试的方法
    Customer updated = new Customer();
    updated.setCustomerName("李四");
    Customer result = service.updateCustomer("C001", updated);

    // Assert（断言）：验证结果
    assertNotNull(result);
    assertEquals("李四", result.getCustomerName());
    verify(repository).save(any(Customer.class));
}
```

#### 测试覆盖率目标

```
Controller:  80%+  （关注主要业务流程）
Service:     90%+  （核心业务逻辑必须全覆盖）
Repository:  60%+  （Spring Data JPA 自动生成的方法可以不测）
Util:        100%  （工具类必须全覆盖）
```

**查看覆盖率**：
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

#### 测试数据构建器模式

```java
// ❌ 每个测试都重复创建对象
@Test
void test1() {
    Customer c = new Customer();
    c.setCustomerId("C001");
    c.setCustomerName("张三");
    c.setStatus("ACTIVE");
    // ...
}

@Test
void test2() {
    Customer c = new Customer();
    c.setCustomerId("C002");
    c.setCustomerName("李四");
    c.setStatus("ACTIVE");
    // ...
}

// ✅ 使用Builder模式
class CustomerBuilder {
    private Customer customer = new Customer();

    public CustomerBuilder withId(String id) {
        customer.setCustomerId(id);
        return this;
    }

    public CustomerBuilder withName(String name) {
        customer.setCustomerName(name);
        return this;
    }

    public CustomerBuilder active() {
        customer.setStatus("ACTIVE");
        return this;
    }

    public Customer build() {
        return customer;
    }

    public static CustomerBuilder aCustomer() {
        return new CustomerBuilder();
    }
}

// 使用Builder
@Test
void testWithBuilder() {
    Customer customer = aCustomer()
        .withId("C001")
        .withName("张三")
        .active()
        .build();
}
```

---

## 第4章：AI 辅助开发实战

### 4.1 Claude Code 快速上手

#### 什么是 Claude Code

Claude Code 是 Anthropic 提供的 AI 编程助手，可以：
- 📖 理解和解释代码
- 🛠️ 生成代码和测试
- 🐛 帮助调试和修复 Bug
- 📚 学习新技术和框架
- 🔍 分析代码质量

#### 基本使用方式

**方式1：在终端中使用**
```bash
# 安装 Claude Code CLI
npm install -g claude-code

# 启动交互式会话
claude-code
```

**方式2：在 IDE 中使用**（VSCode 插件）
```
1. 安装 "Claude Code" 插件
2. Ctrl+Shift+P → "Claude Code: Chat"
3. 开始对话
```

**方式3：通过 API 集成**（高级用法）

#### 有效提问的技巧

**❌ 不好的问题**：
```
"这个代码有什么问题？"
（没有提供代码）

"帮我写一个Service"
（需求不明确）
```

**✅ 好的问题**：
```
"我有以下CustomerService代码：
[粘贴代码]
问题：调用findAllCustomers()时返回了已删除的客户
预期：应该只返回status='ACTIVE'的客户
请帮我分析问题并给出修复方案"

"需求：添加一个REST API支持按信用等级批量查询客户
输入：信用等级列表（如['A','B']）
输出：客户列表 + 每个等级的统计数量
技术栈：Spring Boot 3.3.8 + JPA
请帮我设计实现方案并生成代码"
```

**提问模板**：
```
【背景】：我在做什么项目/功能
【问题】：遇到什么具体问题（包含代码、错误信息）
【尝试】：我已经尝试了什么（可选）
【期望】：期望的结果是什么
【约束】：有什么限制条件（技术栈、性能要求等）
```

---

### 4.2 用 AI 学习新技术

#### 场景：需要学习 Redis 缓存

**Step 1：了解概念**

**提问**：
```
我是一个Spring Boot开发者，完全没用过Redis。
请用最通俗的语言解释：
1. Redis是什么？
2. 为什么需要Redis？
3. Redis在Web应用中的典型使用场景
4. 举一个具体的例子说明如何提升性能
```

**AI 回答**（示例）：
```
Redis 是一个内存数据库，可以理解为：

1. 什么是 Redis？
   - 一个超快的"临时仓库"
   - 数据存在内存中（不是硬盘），所以访问速度极快
   - 类比：Redis = 你的口袋，MySQL = 你家的柜子

2. 为什么需要？
   - 数据库查询慢（硬盘IO）
   - 有些数据经常被访问，但很少变化

3. 典型场景：
   - 缓存热门数据（如首页商品列表）
   - 会话存储（用户登录信息）
   - 计数器（文章浏览量）

4. 性能对比：
   - 不用 Redis：每次查询客户列表都访问数据库（100ms）
   - 使用 Redis：第1次查数据库（100ms），存入Redis；
                 第2次开始从Redis读（1ms）
   → 性能提升100倍
```

**Step 2：学习如何集成**

**提问**：
```
我的项目是 Spring Boot 3.3.8 + Gradle。
请告诉我：
1. 如何添加 Redis 依赖？
2. 如何配置 Redis 连接？
3. 给我一个最简单的示例：缓存客户列表
4. 如何验证 Redis 是否生效？
```

**AI 会给出**：
1. `build.gradle` 依赖配置
2. `application.yml` Redis 配置
3. 完整的代码示例（带注释）
4. 测试步骤

**Step 3：边学边做**

**提问**：
```
我按照你的指导添加了Redis缓存，但是遇到了问题：
[粘贴错误日志]

我的配置文件：
[粘贴配置]

请帮我分析问题
```

**学习技巧**：
1. ✅ **从小问题开始**：不要一次性问"如何搭建整个系统"
2. ✅ **要求解释原理**："为什么这样做？"而不仅仅"怎么做？"
3. ✅ **要求提供示例**："请给我一个完整的代码示例"
4. ✅ **追问细节**："这个注解是什么意思？"
5. ✅ **边学边实践**：每学一步就在项目中尝试

---

### 4.3 用 AI 生成代码

#### 场景1：生成 CRUD 代码

**提问**：
```
请为我生成完整的 Product（产品）模块代码，包括：
- Entity: Product（产品ID、产品名称、价格、状态、创建时间）
- Repository: 支持按状态查询
- Service: 增删改查 + 按状态查询
- Controller: RESTful API（GET/POST/PUT/DELETE）
- 单元测试：Service 和 Controller 的测试

技术栈：Spring Boot 3.3.8 + JPA + Lombok
表名：TCBS.PRODUCTS
主键：PRODUCT_ID（字符串类型）

请遵循本项目的代码规范（参考 Customer 模块）
```

**AI 会生成**：
1. ✅ `Product.java`（Entity）
2. ✅ `ProductRepository.java`
3. ✅ `ProductService.java`
4. ✅ `ProductController.java`
5. ✅ `ProductServiceTest.java`
6. ✅ `ProductControllerTest.java`

**你需要做的**：
1. 复制代码到项目中
2. 运行测试：`./gradlew test`
3. 检查代码规范和逻辑
4. 手动验证功能

#### 场景2：生成复杂查询

**提问**：
```
请帮我实现一个复杂查询：

需求：查询"高价值客户"
定义：同时满足以下条件的客户
  1. 信用等级 = 'A'
  2. 过去6个月账单总金额 > 10000元
  3. 状态 = 'ACTIVE'

涉及表：
  - TCBS.CUSTOMERS（客户表）
  - TCBS.BILLS（账单表）

返回：客户信息 + 账单总金额

请提供：
1. Repository 接口方法（使用 @Query）
2. Service 方法
3. DTO 类（封装返回数据）
4. 单元测试
```

**AI 会生成**：
```java
// 1. DTO
@Data
public class HighValueCustomerDTO {
    private String customerId;
    private String customerName;
    private BigDecimal totalBillAmount;
}

// 2. Repository
@Query("""
    SELECT new com.devops.course.dto.HighValueCustomerDTO(
        c.customerId,
        c.customerName,
        SUM(b.amount)
    )
    FROM Customer c
    JOIN Bill b ON c.customerId = b.customerId
    WHERE c.creditLevel = 'A'
      AND c.status = 'ACTIVE'
      AND b.billDate >= :startDate
    GROUP BY c.customerId, c.customerName
    HAVING SUM(b.amount) > 10000
    """)
List<HighValueCustomerDTO> findHighValueCustomers(
    @Param("startDate") LocalDateTime startDate
);

// 3. Service
public List<HighValueCustomerDTO> findHighValueCustomers() {
    LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
    return customerRepository.findHighValueCustomers(sixMonthsAgo);
}

// 4. 测试
@Test
void testFindHighValueCustomers() {
    // ... 测试代码
}
```

---

### 4.4 用 AI 审查代码

#### 场景：代码审查（Code Review）

**提问**：
```
请审查以下代码，从以下维度给出建议：
1. 代码规范（命名、格式）
2. 潜在Bug
3. 性能问题
4. 安全隐患
5. 可读性和可维护性

代码：
[粘贴你的代码]
```

**示例**：

**你的代码**：
```java
@GetMapping("/customers")
public List<Customer> getAll() {
    return customerRepository.findAll().stream()
        .filter(c -> c.getStatus().equals("ACTIVE"))
        .collect(Collectors.toList());
}
```

**AI 审查结果**：
```
问题1：性能问题（严重）
- 问题：从数据库取所有数据再过滤
- 影响：如果有10万条数据，会全部加载到内存
- 建议：在数据库层面过滤
  List<Customer> findByStatus(String status);

问题2：空指针风险（中等）
- 问题：c.getStatus() 可能为 null
- 影响：抛出 NullPointerException
- 建议：使用 Optional 或 null 检查
  .filter(c -> "ACTIVE".equals(c.getStatus()))

问题3：返回类型（轻微）
- 问题：直接返回 Entity 暴露所有字段
- 影响：可能泄露敏感信息（如内部ID）
- 建议：使用 DTO 封装返回数据

修复后的代码：
@GetMapping("/customers")
public ResponseEntity<List<CustomerDTO>> getAll() {
    List<Customer> customers = customerService.findActiveCustomers();
    List<CustomerDTO> dtos = customers.stream()
        .map(CustomerDTO::from)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
}
```

---

### 4.5 AI 使用的最佳实践

#### 1. AI 擅长的事情

✅ **适合使用 AI 的场景**：
- 生成重复性代码（CRUD、DTO、测试）
- 解释复杂代码和算法
- 学习新技术和框架
- 调试和修复明确的 Bug
- 代码重构建议
- 生成测试用例

#### 2. AI 不擅长的事情

❌ **不适合使用 AI 的场景**：
- 复杂的业务逻辑设计（需要领域知识）
- 架构决策（需要综合考虑多种因素）
- 性能调优（需要实际测量数据）
- 安全审计（需要专业安全知识）

#### 3. 代码审查清单

**AI 生成代码后，必须检查**：
- ✅ 代码是否符合项目规范？
- ✅ 是否有明显的 Bug？
- ✅ 异常处理是否完善？
- ✅ 是否有安全隐患？
- ✅ 测试是否覆盖主要场景？
- ✅ 代码是否可读？

#### 4. 学习技巧

**错误方式**：
```
❌ 直接复制AI代码 → 粘贴到项目 → 提交
（不理解代码，未来无法维护）
```

**正确方式**：
```
✅ AI 生成代码
   → 仔细阅读和理解每一行
   → 在本地测试
   → 提问不理解的部分
   → 修改和优化
   → 运行测试
   → 提交
```

**边做边学**：
```
1. 让 AI 生成代码
2. 要求 AI 解释每一部分的作用
3. 自己手动修改一些细节
4. 对比 AI 版本和你的版本
5. 思考：AI 的版本为什么更好？或者你的版本为什么更好？
```

#### 5. 提问模板库

**学习新技术**：
```
"我想学习[技术名称]，我的背景是[已掌握的技术]。
请：
1. 用类比的方式解释核心概念
2. 列出最常用的5个功能
3. 给我一个Hello World示例
4. 推荐学习路径"
```

**调试问题**：
```
"我遇到了以下错误：
[错误信息]

相关代码：
[代码]

我尝试了：
[已尝试的方法]

请帮我分析根因并给出解决方案"
```

**生成代码**：
```
"请生成[功能描述]的代码
输入：[输入数据]
输出：[输出数据]
技术栈：[使用的技术]
约束：[限制条件]

请包含：
1. 完整代码
2. 关键部分的注释
3. 单元测试
4. 使用示例"
```

**代码审查**：
```
"请从以下维度审查代码：
1. 性能
2. 安全
3. 可读性
4. 最佳实践

代码：
[粘贴代码]

请指出问题并给出改进建议"
```

---

## 第5章：Git 版本控制基础

### 5.1 Git 基本概念

#### 什么是 Git

Git 是一个**分布式版本控制系统**，可以：
- 📸 记录代码的每一次修改（像拍照一样保存历史）
- 🔄 回退到任何历史版本
- 🌿 支持多人协作开发
- 🔀 合并不同人的代码修改

#### 类比理解 Git

**类比1：游戏存档**
```
游戏存档     →  Git Commit
读取存档     →  Git Checkout
多个存档     →  Git Branch
合并存档     →  Git Merge
```

**类比2：文档版本管理**
```
项目_v1.doc
项目_v2_张三修改.doc
项目_v3_最终版.doc
项目_v4_真正的最终版.doc
项目_v5_这次真的是最终版.doc  ← ❌ 混乱！

Git 的方式：
项目.doc（只有一个文件）
+ Git 历史记录（v1, v2, v3, v4, v5）✅ 清晰！
```

#### Git 的三个区域

```
工作区（Working Directory）
    ↓  git add
暂存区（Staging Area）
    ↓  git commit
本地仓库（Local Repository）
    ↓  git push
远程仓库（Remote Repository）
```

**示例**：
```bash
# 1. 工作区：修改文件
vim CustomerService.java

# 2. 暂存区：标记要提交的文件
git add CustomerService.java

# 3. 本地仓库：提交到本地历史
git commit -m "修复客户查询Bug"

# 4. 远程仓库：推送到团队共享的服务器
git push origin main
```

---

### 5.2 分支管理

#### 什么是分支

**分支**是代码的一个独立副本，你可以在分支上开发新功能，而不影响主分支。

**类比**：
```
主干道（main分支）
  ├─ 修路工程（feature/road-repair）
  │   └─ 在这个分支上修路，不影响主干道通行
  │   └─ 修完后合并回主干道
  │
  └─ 应急抢修（hotfix/urgent-fix）
      └─ 紧急修复问题后合并回主干道
```

#### 常见分支类型

```
main（主分支）
  ├─ develop（开发分支）
  │    ├─ feature/user-login（功能分支）
  │    ├─ feature/export-csv（功能分支）
  │    └─ feature/redis-cache（功能分支）
  │
  └─ hotfix/fix-phone-mask（紧急修复分支）
```

**分支命名规范**：
- `main`：生产环境代码
- `develop`：开发环境代码
- `feature/功能名称`：新功能开发
- `bugfix/Bug描述`：Bug 修复
- `hotfix/紧急修复`：生产环境紧急修复

#### 分支操作

```bash
# 查看所有分支
git branch -a

# 创建新分支
git branch feature/export-csv

# 切换到新分支
git checkout feature/export-csv

# 创建并切换（简写）
git checkout -b feature/export-csv

# 查看当前分支
git branch
# * feature/export-csv  ← 当前分支
#   main

# 删除分支
git branch -d feature/export-csv

# 强制删除（未合并的分支）
git branch -D feature/export-csv
```

---

### 5.3 提交代码（Commit）

#### 提交前检查

```bash
# 1. 查看哪些文件被修改了
git status

# 2. 查看具体修改内容
git diff

# 3. 查看某个文件的修改
git diff src/main/java/com/devops/course/service/CustomerService.java
```

#### 添加文件到暂存区

```bash
# 添加单个文件
git add src/main/java/com/devops/course/service/CustomerService.java

# 添加多个文件
git add src/main/java/com/devops/course/service/*.java

# 添加所有修改（❌ 不推荐，容易误提交）
git add .

# 交互式添加（✅ 推荐）
git add -p
# 会逐个询问每个修改是否要添加
```

#### 提交代码

```bash
# 提交（会打开编辑器输入消息）
git commit

# 提交（直接指定消息）
git commit -m "修复客户查询Bug"

# 提交（详细消息）
git commit -m "fix: 修复客户详情手机号脱敏错误

- 修复 substring 索引错误（应为 7-11，而非 7-10）
- 抽取 PhoneUtils.maskPhone() 工具方法
- 添加单元测试覆盖正常和异常情况

Issue: #123"
```

#### 提交消息规范

**常用格式（Conventional Commits）**：
```
<类型>: <简短描述>

<详细描述（可选）>

<关联的Issue（可选）>
```

**类型**：
- `feat`：新功能
- `fix`：Bug 修复
- `refactor`：重构（不改变功能）
- `test`：添加测试
- `docs`：文档修改
- `style`：代码格式（不影响逻辑）
- `chore`：构建配置等

**示例**：
```bash
# 新功能
git commit -m "feat: 添加按信用等级批量查询客户功能"

# Bug 修复
git commit -m "fix: 修复客户列表返回已删除客户的问题"

# 重构
git commit -m "refactor: 抽取手机号脱敏为工具方法"

# 测试
git commit -m "test: 添加 CustomerService 单元测试"

# 文档
git commit -m "docs: 更新 README 添加 Redis 配置说明"
```

---

### 5.4 合并分支（Merge）

#### 合并流程

**场景**：你在 `feature/export-csv` 分支开发了导出功能，现在要合并到 `develop` 分支

```bash
# 1. 切换到目标分支（develop）
git checkout develop

# 2. 拉取最新代码（避免冲突）
git pull origin develop

# 3. 合并功能分支
git merge feature/export-csv

# 4. 如果没有冲突，推送到远程
git push origin develop

# 5. 删除已合并的功能分支
git branch -d feature/export-csv
```

#### 两种合并方式

**方式1：Fast-Forward（快进合并）**
```
Before:
  main:     A---B
                 \
  feature:        C---D

After:
  main:     A---B---C---D
```
**特点**：
- 没有合并提交
- 历史记录是线性的
- 适合：简单的功能分支

**方式2：Three-Way Merge（三方合并）**
```
Before:
  main:     A---B---E
                 \
  feature:        C---D

After:
  main:     A---B---E---M
                 \     /
  feature:        C---D
```
**特点**：
- 有一个合并提交（M）
- 历史记录有分叉
- 适合：复杂的功能分支

**强制使用三方合并**：
```bash
git merge --no-ff feature/export-csv
```

---

### 5.5 解决冲突

#### 什么时候会冲突

**冲突场景**：
```
两个人同时修改了同一个文件的同一行

张三：
  CustomerService.java:25
  return customerRepository.findByStatus("ACTIVE");

李四：
  CustomerService.java:25
  return customerRepository.findAll();

合并时 → 冲突！Git不知道该保留谁的修改
```

#### 冲突标记

```java
<<<<<<< HEAD（当前分支）
return customerRepository.findByStatus("ACTIVE");
=======
return customerRepository.findAll();
>>>>>>> feature/export-csv（要合并的分支）
```

**解读**：
- `<<<<<<< HEAD`：当前分支的代码
- `=======`：分隔符
- `>>>>>>> feature/export-csv`：要合并的分支的代码

#### 解决冲突步骤

```bash
# 1. 尝试合并
git merge feature/export-csv
# Auto-merging src/main/java/com/devops/course/service/CustomerService.java
# CONFLICT (content): Merge conflict in CustomerService.java

# 2. 查看冲突文件
git status
# Unmerged paths:
#   both modified:   CustomerService.java

# 3. 打开文件，手动解决冲突
vim src/main/java/com/devops/course/service/CustomerService.java

# 修改前：
# <<<<<<< HEAD
# return customerRepository.findByStatus("ACTIVE");
# =======
# return customerRepository.findAll();
# >>>>>>> feature/export-csv

# 修改后（保留正确的代码，删除冲突标记）：
return customerRepository.findByStatus("ACTIVE");

# 4. 标记为已解决
git add src/main/java/com/devops/course/service/CustomerService.java

# 5. 完成合并
git commit -m "merge: 合并 feature/export-csv，解决冲突"

# 6. 推送
git push origin develop
```

#### 避免冲突的技巧

1. **频繁同步代码**
```bash
# 每天开始工作前
git checkout develop
git pull origin develop
git checkout feature/your-feature
git merge develop  # 将最新的 develop 合并到你的分支
```

2. **小步提交**
```
❌ 开发一周后一次性提交1000行代码
✅ 每完成一个小功能就提交（每天2-3次提交）
```

3. **明确分工**
```
❌ 多人同时修改同一个文件的同一个方法
✅ 不同人负责不同的模块/文件
```

---

### 5.6 常见场景实战

#### 场景1：开发新功能

```bash
# 1. 从 develop 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/export-csv

# 2. 开发代码
vim CustomerController.java
vim CustomerService.java

# 3. 提交代码
git add .
git commit -m "feat: 添加客户列表导出CSV功能"

# 4. 推送到远程
git push origin feature/export-csv

# 5. 在 GitLab/GitHub 上创建 Merge Request / Pull Request

# 6. 代码审查通过后，合并到 develop
git checkout develop
git pull origin develop
git merge feature/export-csv
git push origin develop

# 7. 删除功能分支
git branch -d feature/export-csv
git push origin --delete feature/export-csv
```

#### 场景2：修复 Bug

```bash
# 1. 从 develop 创建 bugfix 分支
git checkout develop
git pull origin develop
git checkout -b bugfix/fix-phone-mask

# 2. 修复 Bug
vim CustomerController.java

# 3. 运行测试
./gradlew test

# 4. 提交
git add CustomerController.java
git commit -m "fix: 修复客户详情手机号脱敏错误"

# 5. 合并回 develop
git checkout develop
git merge bugfix/fix-phone-mask
git push origin develop

# 6. 删除 bugfix 分支
git branch -d bugfix/fix-phone-mask
```

#### 场景3：紧急修复生产环境 Bug

```bash
# 1. 从 main 创建 hotfix 分支
git checkout main
git pull origin main
git checkout -b hotfix/urgent-fix

# 2. 修复 Bug
vim CustomerService.java

# 3. 测试
./gradlew test

# 4. 提交
git commit -m "hotfix: 修复生产环境客户查询崩溃问题"

# 5. 合并到 main（生产环境）
git checkout main
git merge hotfix/urgent-fix
git tag v1.0.1  # 打标签
git push origin main --tags

# 6. 同时合并到 develop（避免下次发布时Bug重现）
git checkout develop
git merge hotfix/urgent-fix
git push origin develop

# 7. 删除 hotfix 分支
git branch -d hotfix/urgent-fix
```

#### 场景4：撤销错误的提交

```bash
# 情况1：还没 push，想修改最后一次提交
git commit --amend -m "新的提交消息"

# 情况2：已经 push，但想回退
git log  # 找到要回退的提交
git revert <commit-hash>  # 创建一个新提交来撤销

# 情况3：想完全丢弃本地修改（危险！）
git reset --hard HEAD  # 丢弃所有未提交的修改
```

#### 场景5：暂存当前工作（Stash）

```bash
# 场景：正在开发功能A，突然需要紧急修复Bug B

# 1. 暂存当前工作
git stash save "功能A开发到一半"

# 2. 切换分支修复 Bug
git checkout -b bugfix/urgent

# 3. 修复完成后，切回原分支
git checkout feature/A

# 4. 恢复暂存的工作
git stash pop

# 查看所有暂存
git stash list

# 应用特定暂存
git stash apply stash@{0}
```

---

## 第6章：代码质量提升

### 6.1 代码规范

#### Java 命名规范

```java
// ✅ 类名：大驼峰（PascalCase）
public class CustomerService { }

// ✅ 方法名：小驼峰（camelCase）
public Customer findCustomerById(String id) { }

// ✅ 常量：全大写 + 下划线
public static final String DEFAULT_STATUS = "ACTIVE";

// ✅ 变量：小驼峰
String customerName = "张三";

// ❌ 避免无意义的命名
String s;  // 什么s？
int a1, a2, a3;  // 无法理解含义

// ✅ 有意义的命名
String customerId;
int activeCustomerCount;
```

#### 方法设计原则

**单一职责原则**：
```java
// ❌ 一个方法做太多事
public Customer processCustomer(String id) {
    Customer c = findById(id);
    c.setStatus("ACTIVE");
    sendEmail(c);
    updateCache(c);
    logAudit(c);
    return c;
}

// ✅ 拆分为多个方法
public Customer activateCustomer(String id) {
    Customer customer = findById(id);
    customer.setStatus("ACTIVE");
    saveCustomer(customer);
    return customer;
}

public void notifyCustomerActivation(Customer customer) {
    sendEmail(customer);
}

public void recordCustomerActivation(Customer customer) {
    logAudit(customer);
}
```

**方法长度**：
```
建议：
- 方法长度：< 50 行
- 参数个数：< 5 个
- 嵌套深度：< 3 层
```

---

### 6.2 异常处理

#### 异常处理最佳实践

```java
// ❌ 不好的异常处理
@GetMapping("/{id}")
public Customer getCustomer(@PathVariable String id) {
    try {
        return customerService.findById(id).get();  // 可能抛 NoSuchElementException
    } catch (Exception e) {
        e.printStackTrace();  // 只打印，不处理
        return null;  // 返回 null，调用者无法知道发生了什么
    }
}

// ✅ 好的异常处理
@GetMapping("/{id}")
public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
    return customerService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResourceNotFoundException(
            "客户不存在: " + id));
}
```

#### 自定义异常

```java
// src/main/java/com/devops/course/exception/ResourceNotFoundException.java
package com.devops.course.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// 全局异常处理器
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            404,
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {
        ErrorResponse error = new ErrorResponse(
            500,
            "服务器内部错误",
            LocalDateTime.now()
        );
        return ResponseEntity.status(500).body(error);
    }
}
```

---

### 6.3 日志记录

#### 日志级别

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public Customer findById(String id) {
        log.debug("查询客户，ID: {}", id);  // DEBUG：详细调试信息

        Optional<Customer> customer = repository.findById(id);

        if (customer.isEmpty()) {
            log.warn("客户不存在: {}", id);  // WARN：警告
            return null;
        }

        log.info("查询客户成功: {}", customer.get().getCustomerName());  // INFO：一般信息
        return customer.get();
    }

    public void deleteCustomer(String id) {
        try {
            repository.deleteById(id);
            log.info("删除客户成功: {}", id);
        } catch (Exception e) {
            log.error("删除客户失败: {}", id, e);  // ERROR：错误信息 + 异常栈
            throw e;
        }
    }
}
```

**日志级别**：
```
TRACE < DEBUG < INFO < WARN < ERROR

开发环境：DEBUG
测试环境：INFO
生产环境：WARN
```

#### 日志最佳实践

```java
// ✅ 使用占位符（性能更好）
log.info("客户: {}, 状态: {}", customerId, status);

// ❌ 字符串拼接（性能差）
log.info("客户: " + customerId + ", 状态: " + status);

// ✅ 记录关键业务操作
log.info("客户激活: customerId={}, operator={}", id, currentUser);

// ✅ 记录异常
log.error("导出CSV失败", e);  // 自动打印堆栈

// ❌ 不要记录敏感信息
log.info("用户登录: password={}", password);  // ❌ 泄露密码

// ✅ 脱敏后记录
log.info("用户登录: phone={}", PhoneUtils.maskPhone(phone));
```

---

### 6.4 性能优化

#### 常见性能问题

**问题1：N+1 查询**
```java
// ❌ N+1 查询问题
List<Customer> customers = customerRepository.findAll();  // 1次查询
for (Customer c : customers) {
    List<Bill> bills = billRepository.findByCustomerId(c.getId());  // N次查询
    c.setBills(bills);
}
// 总共：1 + N 次查询（如果有1000个客户，就是1001次查询）

// ✅ 使用 JOIN 一次性查询
@Query("SELECT c FROM Customer c LEFT JOIN FETCH c.bills")
List<Customer> findAllWithBills();
```

**问题2：大数据量分页**
```java
// ❌ 一次性加载所有数据
List<Customer> customers = customerRepository.findAll();  // 10万条数据！

// ✅ 分页查询
Page<Customer> page = customerRepository.findAll(
    PageRequest.of(0, 20));  // 第1页，每页20条
```

**问题3：频繁数据库查询**
```java
// ❌ 每次都查数据库
public List<Product> getProducts() {
    return productRepository.findAll();  // 高频访问
}

// ✅ 使用缓存
@Cacheable("products")
public List<Product> getProducts() {
    return productRepository.findAll();  // 第1次查数据库，后续从缓存读
}
```

---

### 6.5 Code Review 要点

#### 审查清单

**功能正确性**：
- ✅ 是否实现了需求？
- ✅ 是否处理了异常情况？
- ✅ 边界条件是否处理？

**代码质量**：
- ✅ 命名是否清晰？
- ✅ 方法是否过长？
- ✅ 是否有重复代码？
- ✅ 是否符合项目规范？

**测试覆盖**：
- ✅ 是否有单元测试？
- ✅ 测试覆盖率是否达标？
- ✅ 是否测试了异常情况？

**性能**：
- ✅ 是否有N+1查询？
- ✅ 是否有性能瓶颈？
- ✅ 是否需要缓存？

**安全**：
- ✅ 是否有SQL注入风险？
- ✅ 是否验证了输入参数？
- ✅ 是否泄露敏感信息？

---

## 第7章：常见问题与解决方案

### 7.1 编译/构建问题

**问题**：找不到类或方法
```
error: cannot find symbol
  symbol:   class CustomerService
```
**解决**：
```bash
# 1. 刷新 Gradle
./gradlew clean build

# 2. IDEA 中：File → Invalidate Caches → Restart

# 3. 检查 import 语句是否正确
```

---

### 7.2 测试问题

**问题**：Mock 不生效
```
NullPointerException at CustomerService.findById()
```
**解决**：
```java
// 检查是否使用了 @ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;
}
```

---

### 7.3 Git 问题

**问题**：合并冲突
```bash
# 解决步骤
git status  # 查看冲突文件
vim <冲突文件>  # 手动解决冲突
git add <冲突文件>
git commit -m "解决冲突"
```

---

## 第8章：学习资源与下一步

### 8.1 推荐学习资源

**官方文档**：
- Spring Boot: https://spring.io/projects/spring-boot
- JUnit 5: https://junit.org/junit5/docs/current/user-guide/
- Mockito: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html

**视频教程**：
- 黑马程序员 Spring Boot
- 尚硅谷 Git 教程

**书籍**：
- 《Spring Boot 实战》
- 《重构：改善既有代码的设计》
- 《代码整洁之道》

### 8.2 下一步学习

完成本文档后，建议学习：
1. 📚 **团队协作指南**（下一份文档）
   - Git Flow 工作流
   - CI/CD 持续集成
   - 生产环境部署

2. 🔧 **中间件学习**：
   - Redis 缓存
   - Kafka 消息队列
   - Docker 容器化

3. 🏗️ **架构进阶**：
   - 微服务架构
   - 分布式系统
   - 高并发优化

---

**文档版本**: v1.0
**最后更新**: 2025-11-17
**维护**: DevOps Course Team

**下一步**：阅读 [团队协作指南](team-collaboration-guide.md)

# Gradle + Spring Boot 大型项目上手指南（Maven 用户版）

> 📢 **写给 Maven 用户的指南**：如果你熟悉 Maven，这份文档将通过对比 Maven 和 Gradle，帮助你快速理解和使用 Gradle 项目。

## 为什么要写这份对比指南？

如果你习惯了 Maven，突然看到 Gradle 项目可能会觉得陌生。但其实：
- **核心概念是一样的**：依赖管理、生命周期、插件系统
- **只是语法和工具不同**：`pom.xml` vs `build.gradle`、`mvn` vs `./gradlew`
- **学习成本很低**：只要理解了对应关系，30分钟就能上手

---

## ⚡ 5分钟快速开始（给着急的人）

如果你很着急想快速上手，只需记住这几个命令：

```bash
# 1. 给 gradlew 添加执行权限（首次需要）
chmod +x gradlew

# 2. 构建项目（就像 mvn clean install）
./gradlew clean build

# 3. 运行 Spring Boot 应用（就像 mvn spring-boot:run）
./gradlew bootRun

# 4. 运行测试（就像 mvn test）
./gradlew test

# 5. 查看所有可用命令（就像 mvn help:describe）
./gradlew tasks
```

**就这么简单！** 把 `mvn` 换成 `./gradlew`，把 `install` 换成 `build`，其他基本一样。

如果遇到问题，再看后面的详细对比章节。

## 目录

- [Maven vs Gradle 核心对比](#maven-vs-gradle-核心对比)
- [第一步：认识 Gradle 项目结构](#第一步认识-gradle-项目结构)
- [第二步：环境准备](#第二步环境准备)
- [第三步：Gradle 命令对照表](#第三步gradle-命令对照表)
- [第四步：理解 build.gradle（就像 pom.xml）](#第四步理解-buildgradle就像-pomxml)
- [第五步：运行项目](#第五步运行项目)
- [第六步：日常开发](#第六步日常开发)
- [常见问题排查](#常见问题排查)

---

## Maven vs Gradle 核心对比

### 一句话总结

| 概念 | Maven | Gradle |
|-----|-------|--------|
| **配置文件** | `pom.xml` | `build.gradle` |
| **命令工具** | `mvn` | `./gradlew` |
| **依赖管理** | `<dependencies>` | `dependencies { }` |
| **生命周期** | `clean`, `compile`, `test`, `package` | `clean`, `build`, `test`, `bootJar` |
| **仓库配置** | `<repositories>` | `repositories { }` |
| **模块管理** | 父子 POM + `<modules>` | `settings.gradle` + 多模块 |

### 核心理念对比

**Maven 的理念**：
- "约定优于配置" - Convention over Configuration
- 用 XML 定义项目
- 固定的生命周期

**Gradle 的理念**：
- 同样遵循 "约定优于配置"
- 用 Groovy/Kotlin 代码定义项目（更灵活）
- 任务依赖模型（更强大）

**结论**：**项目结构是一样的！只是配置文件格式不同。**

---

## 第一步：认识 Gradle 项目结构

### 1.1 项目目录对比

**好消息：项目目录结构完全一样！**

```
Maven 项目:                    Gradle 项目:
my-project/                   my-project/
├── pom.xml          ←→       ├── build.gradle       (核心差异在这里)
├── src/                      ├── settings.gradle    (项目名称配置)
│   ├── main/                 ├── gradle.properties  (可选配置)
│   │   ├── java/             ├── src/
│   │   └── resources/        │   ├── main/
│   └── test/                 │   │   ├── java/
│       ├── java/             │   │   └── resources/
│       └── resources/        │   └── test/
└── target/          ←→       │       ├── java/
    (构建输出目录)               │       └── resources/
                              └── build/
                                  (构建输出目录)
```

**关键结论**：
- ✅ 源代码目录：`src/main/java` - **完全一样**
- ✅ 资源文件目录：`src/main/resources` - **完全一样**
- ✅ 测试目录：`src/test/java` - **完全一样**
- ⚠️ 配置文件：`pom.xml` → `build.gradle` - **这是唯一的区别**
- ⚠️ 构建目录：`target/` → `build/` - **名字不同而已**

---

## 第二步：环境准备

### 2.1 Maven Wrapper vs Gradle Wrapper

你在 Maven 项目中可能见过 `mvnw`（Maven Wrapper），Gradle 也有类似的机制：

| Maven | Gradle | 说明 |
|-------|--------|------|
| `mvnw` | `./gradlew` | 命令工具 |
| `.mvn/wrapper/` | `gradle/wrapper/` | Wrapper 配置目录 |
| 不需要安装 Maven | 不需要安装 Gradle | 都是自动下载对应版本 |

**示例对比：**

```bash
# Maven 项目
./mvnw clean install

# Gradle 项目
./gradlew clean build
```

---

## 第三步：Gradle 命令对照表

### 3.1 核心命令对比

这是**最重要的对照表**，把它打印出来贴在桌上！

| Maven 命令 | Gradle 命令 | 说明 |
|-----------|------------|------|
| `mvn clean` | `./gradlew clean` | 清理构建目录 |
| `mvn compile` | `./gradlew compileJava` | 编译源代码 |
| `mvn test` | `./gradlew test` | 运行测试 |
| `mvn package` | `./gradlew build` | 打包项目 |
| `mvn install` | `./gradlew build` | 构建并安装到本地 |
| `mvn clean install` | `./gradlew clean build` | 清理并构建 |
| `mvn clean install -DskipTests` | `./gradlew clean build -x test` | 跳过测试构建 |
| `mvn dependency:tree` | `./gradlew dependencies` | 查看依赖树 |
| `mvn spring-boot:run` | `./gradlew bootRun` | 运行 Spring Boot 应用 |
| `mvn verify` | `./gradlew check` | 运行检查任务 |

### 3.2 常用参数对比

| Maven | Gradle | 说明 |
|-------|--------|------|
| `-DskipTests` | `-x test` | 跳过测试 |
| `-U` | `--refresh-dependencies` | 强制刷新依赖 |
| `-o` (offline) | `--offline` | 离线模式 |
| `-X` (debug) | `--debug` | 调试模式 |
| `-q` (quiet) | `--quiet` | 静默模式 |

### 3.3 实战示例对比

#### 场景1：首次构建项目

```bash
# Maven
./mvnw clean install

# Gradle (等价操作)
./gradlew clean build
```

#### 场景2：快速构建（跳过测试）

```bash
# Maven
./mvnw clean install -DskipTests

# Gradle (等价操作)
./gradlew clean build -x test
```

#### 场景3：运行 Spring Boot 应用

```bash
# Maven
./mvnw spring-boot:run

# Gradle (等价操作)
./gradlew bootRun
```

#### 场景4：运行单个测试

```bash
# Maven
./mvnw test -Dtest=UserServiceTest

# Gradle (等价操作)
./gradlew test --tests "UserServiceTest"
```

#### 场景5：查看依赖

```bash
# Maven
./mvnw dependency:tree

# Gradle (等价操作)
./gradlew dependencies
```

#### 场景6：跳过测试并显示错误详情（常用！）

```bash
# Maven (常用命令)
./mvnw clean install -e -Dmaven.test.skip

# Gradle (完全对应的命令)
./gradlew clean build -x test --stacktrace

# 说明：
# -x test        相当于 -Dmaven.test.skip (跳过测试)
# --stacktrace   相当于 -e (显示错误堆栈)
```

**错误日志级别对照：**

| Maven | Gradle | 详细程度 |
|-------|--------|---------|
| `mvn ... -e` | `./gradlew ... --stacktrace` | 显示堆栈跟踪 ⭐推荐 |
| `mvn ... -X` | `./gradlew ... --info` | INFO 级别日志 |
| `mvn ... -X` (debug) | `./gradlew ... --debug` | DEBUG 级别日志（最详细） |
| `mvn ... -q` | `./gradlew ... --quiet` | 静默模式 |

---

## 第四步：理解 build.gradle（就像 pom.xml）

### 4.1 配置文件对比

`build.gradle` 就是 Gradle 版本的 `pom.xml`，但用 Groovy 语言而不是 XML。

#### 示例1：项目基本信息

**Maven (pom.xml):**
```xml
<project>
    <groupId>com.company</groupId>
    <artifactId>my-project</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
</project>
```

**Gradle (build.gradle):**
```groovy
group = 'com.company'
version = '1.0.0'
// artifactId 在 settings.gradle 中定义
```

#### 示例2：Java 版本配置

**Maven (pom.xml):**
```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

**Gradle (build.gradle):**
```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

#### 示例3：Spring Boot 插件

**Maven (pom.xml):**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.8</version>
</parent>
```

**Gradle (build.gradle):**
```groovy
plugins {
    id 'org.springframework.boot' version '3.3.8'
    id 'io.spring.dependency-management' version '1.1.7'
}
```

#### 示例4：依赖管理

**Maven (pom.xml):**
```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

**Gradle (build.gradle):**
```groovy
dependencies {
    // Spring Boot Web
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // MySQL
    runtimeOnly 'mysql:mysql-connector-java'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

#### 示例5：Maven 仓库配置

**Maven (pom.xml):**
```xml
<repositories>
    <repository>
        <id>aliyun</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

**Gradle (build.gradle):**
```groovy
repositories {
    maven { url 'https://maven.aliyun.com/repository/public' }
    mavenCentral()
}
```

### 4.2 依赖范围对比（Scope）

Maven 的 `<scope>` 在 Gradle 中叫 Configuration：

| Maven Scope | Gradle Configuration | 说明 |
|------------|---------------------|------|
| `compile` (默认) | `implementation` | 编译和运行时需要 |
| `provided` | `compileOnly` | 编译时需要，运行时由容器提供 |
| `runtime` | `runtimeOnly` | 运行时需要 |
| `test` | `testImplementation` | 测试时需要 |
| - | `api` | 编译时传递依赖（用于库项目） |

**示例对比：**

```xml
<!-- Maven -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

```groovy
// Gradle
dependencies {
    compileOnly 'org.projectlombok:lombok'
}
```

#### 🔥 依赖配置详解（重点！Maven 用户必看）

很多 Maven 用户看到 Gradle 的依赖配置会困惑：**为什么依赖前面的单词都不一样**（`implementation`、`runtimeOnly`、`testImplementation` 等）？

这些单词叫做 **Dependency Configuration（依赖配置）**，相当于 Maven 中的 **`<scope>`**。

##### 📊 完整对照表

| Maven Scope | Gradle Configuration | 什么时候用 | 典型例子 |
|-------------|---------------------|-----------|---------|
| `<scope>compile</scope>` | `implementation` | 编译和运行都需要 | Spring Boot Starter、业务代码依赖 |
| `<scope>runtime</scope>` | `runtimeOnly` | 只在运行时需要 | 数据库驱动（JDBC Driver） |
| `<scope>provided</scope>` | `compileOnly` | 编译时需要，运行时由容器提供 | Servlet API、Lombok |
| `<scope>test</scope>` | `testImplementation` | 只在测试时需要 | JUnit、Mockito、Spring Test |
| （无对应） | `testRuntimeOnly` | 测试运行时需要 | H2 内存数据库（测试用） |
| （无对应） | `developmentOnly` | 开发时需要，打包时排除 | Spring Boot DevTools |
| （无对应） | `annotationProcessor` | 编译时注解处理 | Lombok、配置处理器 |
| `compile`（传递） | `api` | 编译时传递给依赖方 | 库项目的公共 API |

##### 🔍 实际项目示例解析

以本项目的 `build.gradle` 为例：

```groovy
dependencies {
    // ① implementation = Maven 的 compile scope
    // 说明：编译和运行都需要，会传递给依赖它的模块
    // 使用场景：你的业务代码里要 import 这些类
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    // ② runtimeOnly = Maven 的 runtime scope
    // 说明：只在运行时需要，编译时不需要
    // 为什么？因为代码里用的是接口，不直接 import 具体实现
    runtimeOnly 'com.oracle.database.jdbc:ojdbc11:23.6.0.24.10'
    // 你的代码：@Autowired DataSource dataSource;  ← 用的是 javax.sql.DataSource 接口
    // 没有直接 import oracle.jdbc.OracleDriver，所以编译不需要 ojdbc11

    // ③ developmentOnly = Gradle 特有（Maven 没有对应）
    // 说明：只在开发环境用，打包成 JAR 时不会包含
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    // DevTools 用于热部署，生产环境不需要，所以打包时排除

    // ④ annotationProcessor = 编译时注解处理器
    // 说明：编译时处理注解，生成代码，不会打包到最终 JAR
    annotationProcessor 'org.springframework.boot:spring-boot-configuration-processor'
    // 处理 @ConfigurationProperties 注解，生成元数据，方便 IDE 自动补全

    // ⑤ testImplementation = Maven 的 test scope
    // 说明：只在 src/test 目录编译和运行时需要
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // ⑥ testRuntimeOnly = 测试运行时才需要（Maven 没有对应）
    // 说明：测试编译时不需要，测试运行时需要
    testRuntimeOnly 'com.h2database:h2'
    // H2 用于测试，代码里用的是 DataSource 接口，所以编译不需要 H2
}
```

##### 💡 为什么要这么设计？

**Maven 的问题**：
- 只有 4 个 scope（`compile`、`runtime`、`provided`、`test`），不够精细
- 无法区分"开发时需要但生产不需要"的依赖
- 无法区分"测试编译需要"和"测试运行需要"

**Gradle 的优势**：
1. **更精细的控制**：区分编译、运行、测试、开发等不同场景
2. **更快的构建**：只在需要的时候加载依赖
3. **更小的 JAR 包**：`developmentOnly` 的依赖不会打包进去
4. **更清晰的意图**：一眼就能看出依赖的用途

##### 🎯 记忆口诀

如果觉得难记，记住这 3 个最常用的就够了：

```groovy
dependencies {
    // 1️⃣ implementation - 最常用（80%的依赖都用这个）
    //    我的代码里要 import 这个包的类 → 用 implementation
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // 2️⃣ runtimeOnly - 数据库驱动专用
    //    我的代码用的是 JDBC 接口，不直接用驱动类 → 用 runtimeOnly
    runtimeOnly 'com.oracle.database.jdbc:ojdbc11'

    // 3️⃣ testImplementation - 测试专用
    //    测试代码里要 import JUnit/Mockito → 用 testImplementation
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

##### 📝 实战对比：Spring Boot + Oracle 项目

**Maven 写法**：
```xml
<dependencies>
    <!-- 业务代码需要 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- 运行时需要 -->
    <dependency>
        <groupId>com.oracle.database.jdbc</groupId>
        <artifactId>ojdbc11</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- 测试需要 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- 开发时热部署（Maven 没有好办法排除） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional>  <!-- 只能用 optional -->
    </dependency>
</dependencies>
```

**Gradle 写法**（更清晰）：
```groovy
dependencies {
    // 业务代码需要
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // 运行时需要
    runtimeOnly 'com.oracle.database.jdbc:ojdbc11:23.6.0.24.10'

    // 测试需要
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // 开发时热部署（打包时自动排除）
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    // 测试时用内存数据库（Maven 很难做到这么清晰）
    testRuntimeOnly 'com.h2database:h2'
}
```

##### ❓ 常见疑问

**Q1: 为什么数据库驱动要用 `runtimeOnly` 而不是 `implementation`？**

A: 因为你的代码里用的是 `DataSource`、`Connection` 这些 JDBC 接口，不会直接 `import oracle.jdbc.OracleDriver`。编译时只需要 JDBC API（已包含在 JDK 中），运行时才需要具体的驱动实现。

```java
// 你的代码（编译时不需要 ojdbc11）
@Autowired
private DataSource dataSource;  // ← javax.sql.DataSource 接口

// 不会这样写（如果这样写才需要 implementation）
import oracle.jdbc.OracleDriver;  // ✗ 不推荐直接用
```

**Q2: `implementation` 和 `api` 有什么区别？**

A: 只有在开发库（library）项目时才需要关心：
- `implementation`：依赖不传递给使用方（推荐，编译更快）
- `api`：依赖传递给使用方（相当于 Maven 的默认行为）

对于应用项目（如 Spring Boot 应用），统一用 `implementation` 就行。

**Q3: `developmentOnly` 和 `compileOnly` 有什么区别？**

A:
- `compileOnly`：编译时需要，运行时由容器提供（如 Servlet API）
- `developmentOnly`：开发时需要，打包时自动排除（如 DevTools）

### 4.3 多模块项目对比

#### Maven 多模块

**父 POM (pom.xml):**
```xml
<project>
    <packaging>pom</packaging>
    <modules>
        <module>module-api</module>
        <module>module-service</module>
        <module>module-common</module>
    </modules>
</project>
```

**子模块 POM:**
```xml
<project>
    <parent>
        <groupId>com.company</groupId>
        <artifactId>parent-project</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>module-api</artifactId>
</project>
```

#### Gradle 多模块

**settings.gradle:**
```groovy
rootProject.name = 'parent-project'
include 'module-api'
include 'module-service'
include 'module-common'
```

**根 build.gradle:**
```groovy
subprojects {
    apply plugin: 'java'

    group = 'com.company'
    version = '1.0.0'
}
```

**子模块 build.gradle:**
```groovy
dependencies {
    implementation project(':module-common')  // 依赖其他模块
}
```

---

## 第五步：运行项目

### 5.1 首次构建对比

```bash
# Maven 用户习惯
./mvnw clean install

# Gradle 对应命令
./gradlew clean build

# 💡 提示：build 相当于 Maven 的 package + install
```

### 5.2 运行 Spring Boot 应用

```bash
# Maven
./mvnw spring-boot:run

# Gradle
./gradlew bootRun

# 💡 两者功能完全一样
```

### 5.3 打包生成 JAR

```bash
# Maven
./mvnw package
# 输出：target/my-project-1.0.0.jar

# Gradle
./gradlew bootJar
# 输出：build/libs/my-project-1.0.0.jar
```

---

## 第六步：日常开发

### 6.1 常用开发命令对比

```bash
# 1. 增量编译（快速验证代码）
./gradlew compileJava

# 2. 运行单个测试类
./gradlew test --tests "com.company.service.UserServiceTest"

# 3. 运行单个测试方法
./gradlew test --tests "com.company.service.UserServiceTest.testCreateUser"

# 4. 连续测试模式（代码改动自动运行测试）
./gradlew test --continuous

# 5. 检查代码风格（如果配置了）
./gradlew checkstyleMain
```

### 6.2 数据库相关

如果项目使用数据库（JPA/MyBatis）：

```bash
# 查看配置文件
cat src/main/resources/application.yml

# 常见数据库配置：
# spring:
#   datasource:
#     url: jdbc:mysql://localhost:3306/mydb
#     username: root
#     password: password
#   jpa:
#     hibernate:
#       ddl-auto: update  # 开发环境

# 启动本地数据库（Docker 方式）
docker run -d \
  --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=mydb \
  -p 3306:3306 \
  mysql:8.4
```

### 6.3 查看和修改配置

```bash
# 查看生效的配置
./gradlew bootRun --args='--debug'

# 或在 application.yml 中设置
logging:
  level:
    root: DEBUG
```

### 6.4 代码格式化和质量检查

```bash
# 如果项目配置了 Spotless
./gradlew spotlessApply    # 自动格式化代码

# 如果项目配置了 Checkstyle
./gradlew checkstyleMain

# 如果项目配置了 PMD
./gradlew pmdMain

# 运行所有质量检查
./gradlew check
```

---

## 常见问题排查

### 问题1: Gradle 依赖下载失败

```bash
# 症状：
Could not resolve all dependencies...
Connection timed out

# 解决方案1：配置国内镜像
# 在 build.gradle 中添加：
repositories {
    maven { url 'https://maven.aliyun.com/repository/public' }
    maven { url 'https://maven.aliyun.com/repository/spring' }
    mavenCentral()
}

# 解决方案2：清理缓存重试
./gradlew clean build --refresh-dependencies

# 解决方案3：删除缓存目录
rm -rf ~/.gradle/caches/
./gradlew build
```

### 问题2: Java 版本不匹配

```bash
# 症状：
Gradle requires JVM 17 or later to run.
Your build is currently configured to use JVM 8.

# 解决方案1：在 gradle.properties 中指定 Java 路径
org.gradle.java.home=/path/to/java-21

# 解决方案2：设置 JAVA_HOME 环境变量
export JAVA_HOME=$(/usr/libexec/java_home -v 21)  # macOS
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk    # Linux
```

### 问题3: 端口被占用

```bash
# 症状：
Port 8080 is already in use

# 解决方案1：更改端口（application.yml）
server:
  port: 8081

# 解决方案2：找到并终止占用进程
lsof -ti:8080 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :8080   # Windows
```

### 问题4: 测试失败

```bash
# 查看详细测试报告
open build/reports/tests/test/index.html

# 跳过测试继续构建（不推荐，仅用于紧急情况）
./gradlew build -x test

# 运行失败的测试（带详细输出）
./gradlew test --tests "FailingTest" --info
```

### 问题5: 内存不足

```bash
# 症状：
Java heap space
OutOfMemoryError

# 解决方案：增加 Gradle JVM 内存（gradle.properties）
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g

# 或使用环境变量
export GRADLE_OPTS="-Xmx4g"
```

### 问题6: 找不到主类

```bash
# 症状：
Could not find or load main class com.company.Main

# 解决方案1：检查 build.gradle 中的主类配置
application {
    mainClass = 'com.company.Main'  // ← 确认类名正确
}

# 解决方案2：清理重新构建
./gradlew clean build
```

---

## 最佳实践

### 1. 代码分支管理

```bash
# 拉取项目后，立即创建开发分支
git checkout -b feature/your-feature-name

# 保持主分支更新
git fetch origin
git rebase origin/main
```

### 2. 依赖管理

```groovy
// 在 build.gradle 中使用 BOM 管理版本
dependencies {
    // 使用 Spring Boot BOM
    implementation platform('org.springframework.boot:spring-boot-dependencies:3.3.8')

    // 不需要指定版本号
    implementation 'org.springframework.boot:spring-boot-starter-web'
}
```

### 3. 配置管理

```yaml
# 使用配置文件区分环境
# application.yml (默认配置)
# application-dev.yml (开发环境)
# application-test.yml (测试环境)
# application-prod.yml (生产环境)

# 启动时指定：
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### 4. 日志配置

```yaml
# application.yml
logging:
  level:
    root: INFO
    com.company: DEBUG  # 只对公司代码开启 DEBUG
  file:
    name: logs/application.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

### 5. 开发工具配置

```yaml
# 开启热重载（application-dev.yml）
spring:
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true
```

### 6. 性能优化

```properties
# gradle.properties
# 启用并行构建
org.gradle.parallel=true

# 启用构建缓存
org.gradle.caching=true

# 启用 Gradle Daemon
org.gradle.daemon=true

# 配置 Worker 数量（根据 CPU 核心数）
org.gradle.workers.max=8
```

### 7. 团队协作

```bash
# 提交前必做检查
./gradlew clean build  # 确保构建成功
./gradlew test         # 确保测试通过

# 代码审查前生成报告
./gradlew javadoc      # 生成文档
./gradlew test         # 生成测试覆盖率报告
```

---

## 快速参考卡片

### 关键命令速查表

| 任务 | 命令 |
|-----|------|
| 构建项目 | `./gradlew build` |
| 清理构建 | `./gradlew clean` |
| 运行应用 | `./gradlew bootRun` |
| 运行测试 | `./gradlew test` |
| 查看任务 | `./gradlew tasks` |
| 查看依赖 | `./gradlew dependencies` |
| 生成 JAR | `./gradlew bootJar` |
| 刷新依赖 | `./gradlew --refresh-dependencies` |

### 关键文件速查表

| 文件 | 用途 |
|-----|------|
| `build.gradle` | 构建配置（依赖、插件） |
| `settings.gradle` | 项目/模块配置 |
| `gradle.properties` | Gradle 属性配置 |
| `application.yml` | Spring Boot 配置 |
| `gradle/wrapper/` | Gradle Wrapper 文件 |

---

## 总结

当你拿到一个大型 Gradle + Spring Boot 项目时：

1. ✅ **先看文档** - README.md, CLAUDE.md
2. ✅ **检查环境** - Java 版本、Gradle 版本
3. ✅ **分析结构** - 单模块 vs 多模块
4. ✅ **首次构建** - `./gradlew clean build`
5. ✅ **运行项目** - `./gradlew bootRun`
6. ✅ **熟悉流程** - 测试、打包、部署
7. ✅ **遇到问题** - 查看本文档的"常见问题排查"章节

记住：大型项目上手需要时间，不要急于修改代码，先理解项目结构和运行流程！

---

## 🎯 Maven 用户的学习路径建议

### 第一阶段：基础映射（1天）
- ✅ 阅读本文档的"Maven vs Gradle 核心对比"章节
- ✅ 记住"命令对照表"（建议打印贴在显示器上）
- ✅ 试着用 Gradle 命令完成日常任务

### 第二阶段：配置理解（2-3天）
- ✅ 对照 pom.xml 和 build.gradle，理解配置映射
- ✅ 理解依赖 Scope 到 Configuration 的转换
- ✅ 学会查看和分析依赖树

### 第三阶段：深度应用（1周）
- ✅ 理解 Gradle 的任务（Task）机制
- ✅ 学习编写自定义 Gradle 任务
- ✅ 掌握多模块项目管理

### 第四阶段：性能优化（可选）
- ✅ 配置构建缓存
- ✅ 使用并行构建
- ✅ 优化依赖解析策略

---

## 📚 延伸阅读

### 官方文档
- [Gradle 官方文档](https://docs.gradle.org/)
- [Gradle vs Maven Comparison](https://gradle.org/maven-vs-gradle/)
- [Spring Boot with Gradle](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)

### 推荐教程
- Gradle User Guide - Getting Started
- Building Java Projects with Gradle
- Multi-project Builds with Gradle

---

## 💬 反馈与改进

如果你在使用过程中遇到了本文档未涵盖的问题，或者有任何改进建议，请：

1. 在项目中提交 Issue
2. 或将你的问题和解决方案补充到本文档

**你的反馈将帮助更多 Maven 用户更快地掌握 Gradle！**

---

**文档版本**: v2.0 (Maven 用户专版)
**最后更新**: 2025-11-13
**维护者**: DevOps Course Team

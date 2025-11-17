# 团队协作指南

> 🎯 **适用场景**：当你需要与团队成员协作开发同一个项目时
> 👥 **目标读者**：掌握 Git 基础，了解代码开发流程的开发者
> ⏱️ **阅读时长**：约 60-80 分钟
> 📌 **前置文档**：建议先阅读 [实战编写代码指南](writing-code-guide.md)

---

## 📖 目录

- [引言：从个人开发到团队协作](#引言从个人开发到团队协作)
- [第1章：Git Flow 工作流](#第1章git-flow-工作流)
  - [1.1 Git Flow 核心概念](#11-git-flow-核心概念)
  - [1.2 分支策略详解](#12-分支策略详解)
  - [1.3 完整开发流程](#13-完整开发流程)
  - [1.4 实战案例](#14-实战案例)
- [第2章：Pull Request / Merge Request 流程](#第2章pull-request--merge-request-流程)
  - [2.1 什么是 PR/MR](#21-什么是-prmr)
  - [2.2 创建 PR 的标准流程](#22-创建-pr-的标准流程)
  - [2.3 Code Review 指南](#23-code-review-指南)
  - [2.4 处理 Review 意见](#24-处理-review-意见)
- [第3章：持续集成/持续部署（CI/CD）](#第3章持续集成持续部署cicd)
  - [3.1 CI/CD 基本概念](#31-cicd-基本概念)
  - [3.2 配置 GitLab CI/CD](#32-配置-gitlab-cicd)
  - [3.3 自动化测试](#33-自动化测试)
  - [3.4 自动化部署](#34-自动化部署)
- [第4章：环境管理](#第4章环境管理)
  - [4.1 开发/测试/生产环境](#41-开发测试生产环境)
  - [4.2 配置管理](#42-配置管理)
  - [4.3 数据库迁移](#43-数据库迁移)
  - [4.4 版本发布](#44-版本发布)
- [第5章：监控与日志](#第5章监控与日志)
  - [5.1 应用监控](#51-应用监控)
  - [5.2 日志管理](#52-日志管理)
  - [5.3 告警机制](#53-告警机制)
  - [5.4 故障排查](#54-故障排查)
- [第6章：团队沟通与协作](#第6章团队沟通与协作)
  - [6.1 需求沟通](#61-需求沟通)
  - [6.2 技术方案评审](#62-技术方案评审)
  - [6.3 问题升级机制](#63-问题升级机制)
  - [6.4 知识分享](#64-知识分享)
- [第7章：最佳实践总结](#第7章最佳实践总结)
- [第8章：常见问题与解决方案](#第8章常见问题与解决方案)

---

## 引言：从个人开发到团队协作

### 个人开发 vs 团队协作

**个人开发**：
```
你一个人：
  ├─ 想改就改
  ├─ 想提交就提交
  ├─ 想部署就部署
  └─ 不需要沟通
```

**团队协作**：
```
你和团队：
  ├─ 需要遵循分支规范
  ├─ 需要 Code Review
  ├─ 需要通过 CI/CD 流程
  ├─ 需要沟通和协调
  └─ 需要保证代码质量和稳定性
```

### 团队协作的挑战

1. **代码冲突**：多人修改同一文件
2. **质量参差**：不同人的代码水平不同
3. **进度协调**：A 的功能依赖 B 的功能
4. **环境差异**："在我电脑上可以运行"
5. **沟通成本**：信息不对称，理解偏差

### 本文档的目标

通过学习本文档，你将掌握：
- ✅ 如何使用 Git Flow 工作流
- ✅ 如何创建和审查 Pull Request
- ✅ 如何配置和使用 CI/CD
- ✅ 如何管理多环境部署
- ✅ 如何监控和排查生产问题
- ✅ 如何高效沟通协作

---

## 第1章：Git Flow 工作流

### 1.1 Git Flow 核心概念

#### 什么是 Git Flow

Git Flow 是一种 Git 分支管理策略，定义了：
- 哪些分支是长期存在的
- 哪些分支是临时的
- 什么时候创建分支
- 什么时候合并分支
- 如何发布版本

#### Git Flow 分支模型

```
生产环境（main）
    ↓
    ├─── hotfix/v1.0.1 ────→ 紧急修复 → 合并回 main 和 develop
    │
    ├─── release/v1.1.0 ───→ 发布准备 → 合并回 main 和 develop
    │
开发环境（develop）
    ↓
    ├─── feature/user-login ────→ 新功能开发 → 合并回 develop
    ├─── feature/export-csv ────→ 新功能开发 → 合并回 develop
    └─── bugfix/fix-phone ──────→ Bug 修复 → 合并回 develop
```

#### 五种分支类型

| 分支类型 | 命名规范 | 生命周期 | 来源 | 合并目标 |
|---------|---------|---------|-----|---------|
| **main** | `main` | 永久 | - | - |
| **develop** | `develop` | 永久 | main | - |
| **feature** | `feature/功能名` | 临时 | develop | develop |
| **release** | `release/v版本号` | 临时 | develop | main + develop |
| **hotfix** | `hotfix/修复描述` | 临时 | main | main + develop |

---

### 1.2 分支策略详解

#### Main 分支（生产分支）

**作用**：
- 存放生产环境的代码
- **随时可部署**
- 每次合并到 main 都代表一个新版本

**规则**：
- ❌ 不允许直接提交到 main
- ✅ 只能通过 merge release 或 hotfix
- ✅ 每次合并都打 tag（如 v1.0.0）

```bash
# 查看 main 分支历史
git checkout main
git log --oneline --graph

# 示例输出：
# * a1b2c3d (tag: v1.0.1) Merge hotfix/urgent-fix
# * d4e5f6g (tag: v1.0.0) Merge release/v1.0.0
# * g7h8i9j (tag: v0.9.0) Initial release
```

#### Develop 分支（开发分支）

**作用**：
- 日常开发的主分支
- 集成所有已完成的功能
- 随时可以发布到测试环境

**规则**：
- ❌ 不直接在 develop 上开发
- ✅ 从 develop 创建 feature/bugfix 分支
- ✅ feature/bugfix 完成后合并回 develop

```bash
# 从 develop 创建功能分支
git checkout develop
git pull origin develop
git checkout -b feature/user-login
```

#### Feature 分支（功能分支）

**作用**：
- 开发新功能
- 隔离开发，互不影响

**生命周期**：
```
1. 从 develop 创建
2. 开发功能
3. 提交 Pull Request
4. Code Review
5. 合并到 develop
6. 删除 feature 分支
```

**示例**：
```bash
# 1. 创建 feature 分支
git checkout develop
git pull origin develop
git checkout -b feature/export-csv

# 2. 开发功能（多次提交）
git add .
git commit -m "feat: 添加 CSV 导出 Service"
git add .
git commit -m "feat: 添加 CSV 导出 Controller"
git add .
git commit -m "test: 添加 CSV 导出测试"

# 3. 推送到远程
git push origin feature/export-csv

# 4. 在 GitLab/GitHub 创建 Merge Request

# 5. Review 通过后，合并到 develop（在Web界面操作）

# 6. 删除本地和远程分支
git checkout develop
git pull origin develop
git branch -d feature/export-csv
git push origin --delete feature/export-csv
```

#### Release 分支（发布分支）

**作用**：
- 准备发布新版本
- 只能修复 Bug，不能添加新功能
- 测试通过后合并到 main 和 develop

**生命周期**：
```
1. 从 develop 创建 release/v1.1.0
2. 修复发现的 Bug
3. 测试通过
4. 合并到 main（打 tag v1.1.0）
5. 合并到 develop（同步 Bug 修复）
6. 删除 release 分支
```

**示例**：
```bash
# 1. 创建 release 分支
git checkout develop
git pull origin develop
git checkout -b release/v1.1.0

# 2. 修改版本号（如果有）
vim build.gradle
# version = '1.1.0'
git commit -am "chore: 更新版本号为 1.1.0"

# 3. 测试发现 Bug，修复
git commit -am "fix: 修复导出功能在 IE 浏览器的兼容性问题"

# 4. 测试通过，合并到 main
git checkout main
git pull origin main
git merge --no-ff release/v1.1.0
git tag -a v1.1.0 -m "Release version 1.1.0"
git push origin main --tags

# 5. 同时合并到 develop
git checkout develop
git pull origin develop
git merge --no-ff release/v1.1.0
git push origin develop

# 6. 删除 release 分支
git branch -d release/v1.1.0
git push origin --delete release/v1.1.0
```

#### Hotfix 分支（紧急修复分支）

**作用**：
- 紧急修复生产环境的严重 Bug
- 直接从 main 分支创建
- 修复后立即部署到生产

**场景**：
```
周五晚上 8 点，客户反馈：
"客户详情页打不开，影响所有用户！"

→ 这是生产环境的紧急Bug，需要立即修复
→ 使用 hotfix 分支
```

**示例**：
```bash
# 1. 从 main 创建 hotfix 分支
git checkout main
git pull origin main
git checkout -b hotfix/fix-customer-detail-crash

# 2. 快速定位和修复 Bug
vim src/main/java/com/devops/course/controller/CustomerController.java
git commit -am "hotfix: 修复客户详情页空指针异常"

# 3. 运行测试
./gradlew test

# 4. 合并到 main（生产环境）
git checkout main
git merge --no-ff hotfix/fix-customer-detail-crash
git tag -a v1.0.1 -m "Hotfix: 修复客户详情页崩溃"
git push origin main --tags

# 5. 立即部署到生产环境
# （触发 CI/CD 自动部署，或手动部署）

# 6. 合并到 develop（避免下次发布时 Bug 重现）
git checkout develop
git merge --no-ff hotfix/fix-customer-detail-crash
git push origin develop

# 7. 删除 hotfix 分支
git branch -d hotfix/fix-customer-detail-crash
```

---

### 1.3 完整开发流程

#### 场景1：日常功能开发

**需求**：开发"客户列表导出 CSV"功能

**流程**：
```
1. 【创建分支】从 develop 创建 feature/export-csv
2. 【开发代码】编写 Service、Controller、测试
3. 【本地测试】运行单元测试和手动测试
4. 【提交代码】多次小提交
5. 【推送远程】git push origin feature/export-csv
6. 【创建 PR】在 GitLab/GitHub 上创建 Merge Request
7. 【CI 自动化】自动运行测试、代码检查
8. 【Code Review】团队成员审查代码
9. 【修改反馈】根据 Review 意见修改
10. 【合并代码】Review 通过后合并到 develop
11. 【部署测试环境】自动或手动部署到测试环境
12. 【验收测试】测试人员验收功能
13. 【删除分支】功能完成，删除 feature 分支
```

**时间估算**：
- 开发：2-3 天
- Review：0.5-1 天
- 测试：1-2 天
- 总计：4-6 天

#### 场景2：版本发布

**目标**：发布 v1.1.0 版本

**流程**：
```
1. 【确认功能】确认 develop 上的所有功能已完成
2. 【创建 release】git checkout -b release/v1.1.0
3. 【更新版本号】修改 build.gradle 中的版本号
4. 【部署预发布环境】部署到与生产环境相同的预发布环境
5. 【回归测试】完整的测试所有功能
6. 【修复 Bug】如果发现问题，在 release 分支上修复
7. 【测试通过】所有测试通过
8. 【合并到 main】git merge --no-ff release/v1.1.0
9. 【打标签】git tag -a v1.1.0 -m "Release 1.1.0"
10. 【部署生产】部署到生产环境
11. 【同步到 develop】将 Bug 修复同步到 develop
12. 【删除 release 分支】git branch -d release/v1.1.0
```

**发布检查清单**：
- ✅ 所有功能已开发完成
- ✅ 所有测试用例通过
- ✅ 代码 Review 完成
- ✅ 数据库迁移脚本准备完成
- ✅ 配置文件已更新
- ✅ 发布说明文档已准备
- ✅ 回滚方案已准备

#### 场景3：紧急修复

**问题**：生产环境客户详情页崩溃

**流程**：
```
1. 【确认问题】客户反馈 + 监控告警
2. 【定位 Bug】查看日志、错误堆栈
3. 【创建 hotfix】git checkout -b hotfix/fix-customer-crash
4. 【快速修复】修复 Bug，运行测试
5. 【合并到 main】git merge hotfix
6. 【打标签】git tag v1.0.1
7. 【紧急部署】立即部署到生产环境
8. 【验证修复】确认问题已解决
9. 【同步到 develop】git merge hotfix
10. 【通知团队】发送修复通知
```

**时间要求**：
- P0（系统不可用）：< 1 小时
- P1（核心功能不可用）：< 4 小时
- P2（部分功能异常）：< 1 天

---

### 1.4 实战案例

#### 完整案例：导出功能开发与发布

**背景**：
- 需求：客户管理模块需要支持导出 CSV
- 排期：本周开发，下周测试，下下周发布
- 团队：3 个开发者（张三、李四、王五）

**第1周：功能开发**

**张三（负责导出功能）**：
```bash
# 周一：创建分支
git checkout develop
git pull origin develop
git checkout -b feature/export-csv

# 周一-周三：开发代码
git add .
git commit -m "feat: 添加 CSV 导出 Service"
git add .
git commit -m "feat: 添加 CSV 导出 Controller"
git add .
git commit -m "feat: 添加前端导出按钮"

# 周三：自测完成，推送代码
git push origin feature/export-csv

# 周三下午：创建 Merge Request
# 标题：feat: 客户列表导出 CSV 功能
# 描述：
#   - 支持导出所有客户数据为 CSV
#   - 支持中文文件名
#   - 处理 Excel 中文乱码问题
#   - 添加单元测试和集成测试
# 测试人员：@李四
# Reviewer：@王五
```

**李四（Code Reviewer）**：
```bash
# 周四：Review 代码
# 在 GitLab/GitHub 上查看 Merge Request
# 发现问题：
#   1. CSV 导出缺少错误处理
#   2. 大数据量时可能内存溢出
#   3. 测试覆盖率只有 60%

# 在 MR 上留评论：
# "建议：
# 1. 添加 try-catch 处理文件生成失败的情况
# 2. 大数据量时使用流式处理
# 3. 补充测试用例，覆盖异常情况"
```

**张三（修改代码）**：
```bash
# 周四下午：根据 Review 意见修改
git checkout feature/export-csv
vim src/main/java/com/devops/course/service/CustomerService.java
# 添加异常处理和流式处理

git add .
git commit -m "refactor: 优化 CSV 导出，添加错误处理和流式处理"

git add .
git commit -m "test: 补充异常情况测试"

git push origin feature/export-csv

# 在 MR 上回复："已修改，请再次审查"
```

**王五（最终审批）**：
```bash
# 周五：二次 Review
# 代码质量满足要求
# 点击"Approve"按钮

# 合并代码（点击 Merge 按钮）
# develop 分支现在包含导出功能
```

**第2周：测试**

```bash
# 周一：自动部署到测试环境
# CI/CD 自动触发：
#   1. 运行测试
#   2. 构建 Docker 镜像
#   3. 部署到测试环境

# 周一-周五：测试人员测试
# 发现 Bug：导出大数据量时超时

# 张三：修复 Bug
git checkout develop
git pull origin develop
git checkout -b bugfix/export-timeout

# 修复代码
git commit -am "fix: 优化导出性能，增加超时时间"

# 提交 MR，快速 Review，合并到 develop
```

**第3周：发布**

```bash
# 周一：创建 release 分支
git checkout develop
git pull origin develop
git checkout -b release/v1.1.0

# 更新版本号
vim build.gradle
# version = '1.1.0'
git commit -am "chore: bump version to 1.1.0"

git push origin release/v1.1.0

# 周一-周三：预发布环境测试
# 部署到预发布环境，完整回归测试

# 周四：测试通过，准备发布
git checkout main
git pull origin main
git merge --no-ff release/v1.1.0
git tag -a v1.1.0 -m "Release v1.1.0

Features:
- 客户列表导出 CSV 功能

Fixes:
- 优化导出性能

Contributors:
- @张三 @李四 @王五"

git push origin main --tags

# 周四晚上：部署生产环境
# 发布公告：v1.1.0 已发布

# 周五：同步到 develop，删除 release 分支
git checkout develop
git merge --no-ff release/v1.1.0
git push origin develop

git branch -d release/v1.1.0
git push origin --delete release/v1.1.0
```

---

## 第2章：Pull Request / Merge Request 流程

### 2.1 什么是 PR/MR

#### 定义

**Pull Request（GitHub）/ Merge Request（GitLab）**：
- 一种代码审查和合并机制
- 请求将你的分支合并到目标分支（如 develop）
- 在合并前，团队成员可以审查代码、提出意见、讨论

**类比**：
```
PR/MR = 作业提交 + 老师批改 + 修改重交

你写代码（做作业）
  ↓
提交 PR（交作业）
  ↓
团队 Review（老师批改）
  ↓
修改代码（根据意见修改）
  ↓
Review 通过（老师批准）
  ↓
合并代码（作业归档）
```

---

### 2.2 创建 PR 的标准流程

#### Step 1：推送分支到远程

```bash
git checkout feature/export-csv
git push origin feature/export-csv
```

#### Step 2：在 Web 界面创建 MR

**GitLab 示例**：
```
1. 访问 GitLab 项目页面
2. 左侧菜单：Merge Requests → New Merge Request
3. 选择源分支：feature/export-csv
4. 选择目标分支：develop
5. 点击"Compare branches and continue"
```

#### Step 3：填写 MR 信息

**标题规范**：
```
类型: 简短描述（50字符内）

示例：
✅ feat: 客户列表导出 CSV 功能
✅ fix: 修复客户详情手机号脱敏错误
✅ refactor: 优化客户查询性能
❌ 导出功能（太简略）
❌ 修改了一些代码（无意义）
```

**描述模板**：
```markdown
## 📝 变更描述

本 MR 添加了客户列表导出 CSV 功能，支持导出所有客户数据。

## 🎯 关联 Issue

Closes #123

## ✨ 主要变更

- 添加 `CustomerService.exportToCSV()` 方法
- 添加 `CustomerController.exportCustomers()` API 端点
- 处理中文乱码问题（添加 UTF-8 BOM）
- 支持大数据量流式处理

## 🧪 测试

- [x] 单元测试通过（覆盖率 85%）
- [x] 集成测试通过
- [x] 手动测试通过（测试了1000条、10000条数据）

## 📸 截图

（如果是 UI 变更，贴上截图）

## ⚠️ 注意事项

- 需要升级 opencsv 依赖到 5.9 版本
- 大数据量导出可能需要 30 秒

## ✅ 检查清单

- [x] 代码符合项目规范
- [x] 添加了单元测试
- [x] 更新了相关文档
- [x] 本地测试通过
- [ ] 需要数据库迁移（如果有，请说明）
```

#### Step 4：指定 Reviewer

```
选择审查人员：
- 必须审查：@王五（技术负责人）
- 可选审查：@李四（同模块开发者）
```

#### Step 5：等待 CI/CD 自动检查

```
自动触发的检查：
✅ 编译成功
✅ 单元测试通过（100/100）
✅ 代码覆盖率 > 80%
✅ 代码规范检查通过
⏳ 正在运行...
```

---

### 2.3 Code Review 指南

#### Reviewer 的职责

**技术审查**：
- ✅ 代码逻辑是否正确
- ✅ 是否有潜在 Bug
- ✅ 是否符合设计模式和最佳实践
- ✅ 性能是否有问题

**质量审查**：
- ✅ 命名是否清晰
- ✅ 代码是否可读
- ✅ 是否有重复代码
- ✅ 是否符合项目规范

**测试审查**：
- ✅ 测试是否充分
- ✅ 测试用例是否合理
- ✅ 边界条件是否覆盖

#### 如何进行 Code Review

**好的 Review 评论**：

```markdown
✅ 建设性意见：
"建议：这里使用 Stream API 可以简化代码：
```java
customers.stream()
    .filter(c -> "ACTIVE".equals(c.getStatus()))
    .collect(Collectors.toList());
```
"

✅ 提出问题：
"疑问：这里为什么使用 findAll() 而不是 findByStatus()？
findAll() 会加载所有数据，可能影响性能。"

✅ 给出理由：
"问题：缺少异常处理。
如果文件写入失败，用户会看到500错误，无法知道具体原因。
建议添加 try-catch 并返回友好的错误信息。"

✅ 正面反馈：
"👍 这个工具方法抽取得很好，代码复用性提高了。"
```

**不好的 Review 评论**：

```markdown
❌ 过于笼统：
"代码有问题，重新写。"

❌ 情绪化：
"这代码写得什么玩意儿？"

❌ 没有建议：
"性能不好。"（应该说明哪里不好，如何改进）

❌ 过于吹毛求疵：
"这个变量名应该叫 customerList 而不是 customers。"
（如果不影响理解，不必强求）
```

#### Review 优先级

```
P0（必须修改）：
  - 严重 Bug（如空指针、SQL 注入）
  - 性能问题（如 N+1 查询）
  - 安全漏洞

P1（强烈建议修改）：
  - 逻辑错误
  - 代码质量问题
  - 缺少必要的测试

P2（可选修改）：
  - 命名优化
  - 注释补充
  - 代码风格
```

---

### 2.4 处理 Review 意见

#### 开发者如何响应

**场景**：Reviewer 提出了3个问题

**Step 1：理解意见**
```
仔细阅读每条评论，理解：
- 为什么提出这个问题？
- 这个问题的根因是什么？
- 建议的解决方案是什么？

如果不理解，及时提问：
"@王五 你说的性能问题具体是指哪部分？
我已经用了分页查询，还有其他优化空间吗？"
```

**Step 2：修改代码**
```bash
# 切换到 feature 分支
git checkout feature/export-csv

# 根据意见修改代码
vim src/main/java/com/devops/course/service/CustomerService.java

# 提交修改
git add .
git commit -m "refactor: 根据 Review 意见优化异常处理"

# 推送更新
git push origin feature/export-csv
```

**Step 3：回复评论**
```markdown
在 MR 评论中回复：

"@王五 已修改，主要改动：
1. 添加了 try-catch 异常处理
2. 使用流式处理优化大数据量导出
3. 补充了异常情况的测试用例

请再次审查，谢谢！

改动见提交：abc123d"
```

**Step 4：再次 Review**
```
Reviewer 再次审查：
- 确认问题已解决
- 点击"Approve"批准合并
```

---

## 第3章：持续集成/持续部署（CI/CD）

### 3.1 CI/CD 基本概念

#### 什么是 CI（持续集成）

**Continuous Integration（持续集成）**：
- 开发者频繁地（每天多次）将代码合并到主分支
- 每次合并都通过自动化构建和测试验证
- 快速发现和修复集成问题

**传统方式 vs CI**：
```
传统方式（周/月集成）：
  开发1周 → 集成 → 发现100个冲突 → 解决3天 ❌

CI 方式（每天集成）：
  开发1天 → 集成 → 发现2个冲突 → 解决10分钟 ✅
```

#### 什么是 CD（持续部署）

**Continuous Deployment（持续部署）**：
- 代码合并后自动部署到测试/生产环境
- 无需人工干预
- 快速交付价值给用户

**部署流程**：
```
代码提交
  ↓ 自动
编译构建
  ↓ 自动
运行测试
  ↓ 自动
部署到测试环境
  ↓ 人工确认
部署到生产环境
```

---

### 3.2 配置 GitLab CI/CD

#### GitLab CI 配置文件

在项目根目录创建 `.gitlab-ci.yml`：

```yaml
# .gitlab-ci.yml
# GitLab CI/CD 配置文件

# 定义阶段
stages:
  - build      # 构建
  - test       # 测试
  - deploy     # 部署

# 定义变量
variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false"
  GRADLE_USER_HOME: "$CI_PROJECT_DIR/.gradle"

# 缓存 Gradle 依赖
cache:
  paths:
    - .gradle/wrapper
    - .gradle/caches

# === 构建阶段 ===
build:
  stage: build
  image: gradle:8.5-jdk21  # 使用 Gradle 8.5 + Java 21 镜像
  script:
    - echo "开始构建..."
    - ./gradlew clean build -x test --stacktrace
  artifacts:
    paths:
      - build/libs/*.jar  # 保留构建产物
    expire_in: 1 week
  only:
    - main
    - develop
    - merge_requests

# === 测试阶段 ===
test:
  stage: test
  image: gradle:8.5-jdk21
  script:
    - echo "运行单元测试..."
    - ./gradlew test
    - echo "运行代码覆盖率检查..."
    - ./gradlew jacocoTestReport
  coverage: '/Total.*?([0-9]{1,3})%/'  # 提取覆盖率
  artifacts:
    when: always
    reports:
      junit: build/test-results/test/TEST-*.xml
    paths:
      - build/reports/tests/test
      - build/reports/jacoco/test/html
  only:
    - main
    - develop
    - merge_requests

# 代码质量检查
code-quality:
  stage: test
  image: gradle:8.5-jdk21
  script:
    - echo "运行代码质量检查..."
    - ./gradlew checkstyleMain
  allow_failure: true  # 允许失败（不阻塞流程）
  only:
    - merge_requests

# === 部署到测试环境 ===
deploy:test:
  stage: deploy
  image: docker:latest
  services:
    - docker:dind
  script:
    - echo "构建 Docker 镜像..."
    - docker build -t claude-devops-course:test .
    - echo "推送到镜像仓库..."
    - docker tag claude-devops-course:test registry.example.com/claude-devops-course:test
    - docker push registry.example.com/claude-devops-course:test
    - echo "部署到测试环境..."
    - ssh user@test-server "docker pull registry.example.com/claude-devops-course:test"
    - ssh user@test-server "docker-compose up -d"
  environment:
    name: test
    url: https://test.example.com
  only:
    - develop

# === 部署到生产环境 ===
deploy:production:
  stage: deploy
  image: docker:latest
  services:
    - docker:dind
  script:
    - echo "构建生产镜像..."
    - docker build -t claude-devops-course:$CI_COMMIT_TAG .
    - docker tag claude-devops-course:$CI_COMMIT_TAG registry.example.com/claude-devops-course:$CI_COMMIT_TAG
    - docker push registry.example.com/claude-devops-course:$CI_COMMIT_TAG
    - echo "部署到生产环境..."
    - ssh user@prod-server "docker pull registry.example.com/claude-devops-course:$CI_COMMIT_TAG"
    - ssh user@prod-server "docker-compose up -d"
  environment:
    name: production
    url: https://www.example.com
  when: manual  # 手动触发
  only:
    - tags  # 只在打 tag 时运行
```

#### 触发流程

**1. 推送代码到 feature 分支**：
```bash
git push origin feature/export-csv
```
**触发的 Job**：
- ✅ build（构建）
- ✅ test（测试）
- ✅ code-quality（代码检查）

**2. 合并到 develop 分支**：
```bash
git merge feature/export-csv
git push origin develop
```
**触发的 Job**：
- ✅ build
- ✅ test
- ✅ deploy:test（自动部署到测试环境）

**3. 打 tag 发布**：
```bash
git tag v1.1.0
git push origin v1.1.0
```
**触发的 Job**：
- ✅ build
- ✅ test
- ⏸️ deploy:production（等待手动触发）

---

### 3.3 自动化测试

#### 测试金字塔在 CI 中的应用

```yaml
# 快速测试（每次提交都运行）
test:unit:
  stage: test
  script:
    - ./gradlew test
  only:
    - merge_requests

# 中速测试（合并到 develop 时运行）
test:integration:
  stage: test
  script:
    - ./gradlew integrationTest
  only:
    - develop
    - main

# 慢速测试（发布前运行）
test:e2e:
  stage: test
  script:
    - ./gradlew e2eTest
  only:
    - tags
```

---

### 3.4 自动化部署

#### Docker 部署示例

**Dockerfile**：
```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# 复制构建产物
COPY build/libs/claude-devops-course-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml**：
```yaml
# docker-compose.yml
version: '3.8'

services:
  app:
    image: registry.example.com/claude-devops-course:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:oracle:thin:@//db:1521/ORCL
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: oracle/database:19.3.0-ee
    environment:
      - ORACLE_SID=ORCL
      - ORACLE_PDB=ORCLPDB1
    volumes:
      - oracle-data:/opt/oracle/oradata
    restart: unless-stopped

volumes:
  oracle-data:
```

---

## 第4章：环境管理

### 4.1 开发/测试/生产环境

#### 环境划分

| 环境 | 用途 | 数据 | 访问权限 | 稳定性要求 |
|------|------|------|---------|-----------|
| **开发环境（DEV）** | 日常开发 | 模拟数据 | 所有开发者 | 低 |
| **测试环境（TEST）** | 功能测试 | 测试数据 | 开发者+测试 | 中 |
| **预发布环境（UAT）** | 验收测试 | 生产副本 | 测试+业务 | 高 |
| **生产环境（PROD）** | 真实用户 | 真实数据 | 运维 | 最高 |

#### 环境隔离

```
开发环境：
  - 数据库：dev-db.internal
  - Redis：dev-redis.internal
  - URL：http://dev.example.com

测试环境：
  - 数据库：test-db.internal
  - Redis：test-redis.internal
  - URL：http://test.example.com

生产环境：
  - 数据库：prod-db.example.com
  - Redis：prod-redis.example.com
  - URL：https://www.example.com
```

---

### 4.2 配置管理

#### Spring Boot Profile 配置

**application.yml（公共配置）**：
```yaml
spring:
  application:
    name: claude-devops-course
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: none

server:
  port: 8080
```

**application-dev.yml（开发环境）**：
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//dev-db:1521/DEVDB
    username: dev_user
    password: dev_password
  jpa:
    show-sql: true  # 开发环境显示 SQL

logging:
  level:
    com.devops.course: DEBUG
```

**application-test.yml（测试环境）**：
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//test-db:1521/TESTDB
    username: test_user
    password: ${DB_PASSWORD}  # 从环境变量读取

logging:
  level:
    com.devops.course: INFO
```

**application-prod.yml（生产环境）**：
```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@//(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=scan-ip)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=PRODDB)))
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

logging:
  level:
    com.devops.course: WARN
  file:
    name: /var/log/app/application.log
```

#### 启动时指定 Profile

```bash
# 本地开发
./gradlew bootRun --args='--spring.profiles.active=dev'

# Docker 部署
docker run -e SPRING_PROFILES_ACTIVE=prod app:latest

# Kubernetes 部署
kubectl set env deployment/app SPRING_PROFILES_ACTIVE=prod
```

---

### 4.3 数据库迁移

#### Flyway 数据库版本管理

**添加依赖**：
```groovy
// build.gradle
dependencies {
    implementation 'org.flywaydb:flyway-core'
}
```

**配置 Flyway**：
```yaml
# application.yml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

**创建迁移脚本**：
```sql
-- src/main/resources/db/migration/V1__init.sql
CREATE TABLE TCBS.CUSTOMERS (
    CUSTOMER_ID VARCHAR2(20) PRIMARY KEY,
    CUSTOMER_NAME VARCHAR2(100) NOT NULL,
    STATUS VARCHAR2(10) DEFAULT 'ACTIVE'
);

-- src/main/resources/db/migration/V2__add_email_column.sql
ALTER TABLE TCBS.CUSTOMERS ADD EMAIL VARCHAR2(100);

-- src/main/resources/db/migration/V3__create_products_table.sql
CREATE TABLE TCBS.PRODUCTS (
    PRODUCT_ID VARCHAR2(20) PRIMARY KEY,
    PRODUCT_NAME VARCHAR2(100) NOT NULL,
    PRICE NUMBER(10, 2)
);
```

**运行迁移**：
```bash
# 自动运行（应用启动时）
./gradlew bootRun

# 手动运行
./gradlew flywayMigrate

# 查看迁移状态
./gradlew flywayInfo
```

---

### 4.4 版本发布

#### 语义化版本（Semantic Versioning）

```
格式：主版本号.次版本号.修订号（MAJOR.MINOR.PATCH）

示例：v1.2.3
  1 = 主版本号（不兼容的 API 变更）
  2 = 次版本号（向下兼容的功能新增）
  3 = 修订号（向下兼容的 Bug 修复）
```

**版本号规则**：
```
v1.0.0 → v1.0.1  修复 Bug
v1.0.1 → v1.1.0  添加新功能
v1.1.0 → v2.0.0  不兼容的重大变更
```

#### 发布检查清单

```markdown
## 发布前检查清单

### 代码质量
- [ ] 所有测试通过
- [ ] 代码覆盖率 > 80%
- [ ] Code Review 完成
- [ ] 没有已知的 P0/P1 Bug

### 文档
- [ ] 更新 CHANGELOG.md
- [ ] 更新 API 文档
- [ ] 更新用户手册

### 数据库
- [ ] 数据库迁移脚本已准备
- [ ] 已在测试环境验证
- [ ] 有回滚脚本

### 配置
- [ ] 生产环境配置已更新
- [ ] 密钥和密码已更新

### 部署
- [ ] 部署脚本已测试
- [ ] 有回滚方案
- [ ] 通知相关人员

### 监控
- [ ] 配置告警规则
- [ ] 准备值班人员
```

---

## 第5章：监控与日志

### 5.1 应用监控

#### Spring Boot Actuator

**添加依赖**：
```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

**配置监控端点**：
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

**访问监控端点**：
```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 应用信息
curl http://localhost:8080/actuator/info

# 指标数据
curl http://localhost:8080/actuator/metrics

# JVM 内存使用
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

### 5.2 日志管理

#### 日志配置

**logback-spring.xml**：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/app/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/app/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 错误日志单独文件 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/var/log/app/error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/var/log/app/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 根日志级别 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>

    <!-- 特定包的日志级别 -->
    <logger name="com.devops.course" level="DEBUG"/>
    <logger name="org.springframework" level="INFO"/>
    <logger name="org.hibernate" level="WARN"/>
</configuration>
```

---

### 5.3 告警机制

#### 告警规则示例

```yaml
# 告警配置示例
alerts:
  # 应用不可用
  - name: app_down
    condition: health_check_failed
    duration: 1m
    severity: critical
    notify: ["oncall", "tech-lead"]

  # 错误率过高
  - name: high_error_rate
    condition: error_rate > 5%
    duration: 5m
    severity: warning
    notify: ["dev-team"]

  # 响应时间过长
  - name: slow_response
    condition: avg_response_time > 2s
    duration: 10m
    severity: warning
    notify: ["dev-team"]

  # 内存使用率过高
  - name: high_memory
    condition: memory_usage > 90%
    duration: 5m
    severity: warning
    notify: ["devops"]
```

---

### 5.4 故障排查

#### 故障排查流程

```
1. 【发现问题】
   - 用户反馈
   - 监控告警
   - 错误日志

2. 【确认影响】
   - 影响范围（所有用户 or 部分用户）
   - 影响程度（完全不可用 or 功能异常）
   - 影响时长

3. 【定位问题】
   - 查看日志（/var/log/app/error.log）
   - 查看监控（CPU、内存、数据库连接）
   - 检查最近的变更（代码发布、配置修改）

4. 【快速修复】
   - 如果是代码问题：回滚到上一版本
   - 如果是配置问题：修改配置重启
   - 如果是资源问题：扩容或重启

5. 【验证修复】
   - 功能恢复正常
   - 监控指标恢复

6. 【根因分析】
   - 为什么会发生这个问题？
   - 为什么没有提前发现？
   - 如何避免再次发生？

7. 【改进措施】
   - 修复根本问题
   - 添加监控和告警
   - 更新文档和流程
```

---

## 第6章：团队沟通与协作

### 6.1 需求沟通

#### 需求评审会议

**参与人员**：
- 产品经理
- 开发团队
- 测试人员
- UI/UX 设计师

**流程**：
```
1. 产品经理讲解需求（30分钟）
2. 开发提问和讨论（20分钟）
3. 技术方案初步评估（10分钟）
4. 确定排期（10分钟）
```

**输出**：
- 需求文档
- UI 设计稿
- 技术方案（初稿）
- 排期计划

---

### 6.2 技术方案评审

#### 方案评审要点

**技术方案文档应包含**：
```markdown
## 1. 背景和目标
- 业务背景
- 要解决的问题
- 目标和非目标

## 2. 方案设计
- 架构设计
- 数据库设计
- API 设计
- 关键技术点

## 3. 方案对比
- 方案 A vs 方案 B
- 优缺点分析
- 推荐方案

## 4. 风险和挑战
- 技术风险
- 性能风险
- 兼容性问题

## 5. 测试计划
- 单元测试
- 集成测试
- 性能测试

## 6. 上线计划
- 分阶段发布
- 回滚方案
- 监控指标
```

---

### 6.3 问题升级机制

#### 问题等级定义

| 等级 | 描述 | 响应时间 | 解决时间 | 示例 |
|------|------|---------|---------|------|
| **P0** | 系统不可用 | 15分钟 | 1小时 | 应用崩溃、数据库宕机 |
| **P1** | 核心功能不可用 | 1小时 | 4小时 | 支付失败、登录异常 |
| **P2** | 部分功能异常 | 4小时 | 1天 | 导出功能报错 |
| **P3** | 体验问题 | 1天 | 1周 | UI 显示错位 |

#### 升级流程

```
P2/P3 问题：
  开发者自己解决
    ↓ 超过预期时间未解决
  向 Tech Lead 求助
    ↓ 仍未解决
  升级为 P1

P1 问题：
  Tech Lead 介入
    ↓ 需要其他团队支持
  拉群讨论，协调资源

P0 问题：
  立即通知所有相关人员
  CTO/技术总监介入
  所有人 All-hands-on-deck
```

---

### 6.4 知识分享

#### 技术分享会

**频率**：每周/每两周一次

**形式**：
- 技术专题分享（30-45分钟）
- 项目复盘（15-20分钟）
- 新技术调研（10-15分钟）

**主题示例**：
- "Spring Boot 性能优化实战"
- "我们如何解决 XXX 问题"
- "Redis 缓存最佳实践"
- "从 Bug 到优化：XXX 功能的演进"

#### 文档沉淀

**团队 Wiki**：
- 项目文档
- 技术方案
- 故障复盘
- 最佳实践
- FAQ

---

## 第7章：最佳实践总结

### 7.1 Git 最佳实践

```
✅ DO（推荐做法）
- 频繁提交（每天至少1次）
- 提交消息清晰（遵循规范）
- 小步迭代（一次只做一件事）
- 经常同步（每天拉取最新代码）
- 及时删除已合并分支

❌ DON'T（避免做法）
- 一周不提交代码
- 提交消息随意（"fix bug"）
- 一次提交1000行代码
- 长期不同步代码
- 在 main 分支直接开发
```

### 7.2 Code Review 最佳实践

```
✅ DO
- 小批量 Review（< 400 行）
- 24 小时内完成 Review
- 提供建设性意见
- 肯定好的代码

❌ DON'T
- 一次 Review 2000 行代码
- 拖延一周才 Review
- 只说"有问题"不说怎么改
- 过于吹毛求疵
```

### 7.3 部署最佳实践

```
✅ DO
- 使用自动化部署
- 有回滚方案
- 非工作时间发布
- 灰度发布（逐步放量）
- 发布后监控

❌ DON'T
- 手动 SSH 部署
- 没有回滚方案
- 工作时间发布
- 一次性全量发布
- 发布后不监控
```

---

## 第8章：常见问题与解决方案

### 8.1 Git 相关问题

**Q1：合并时冲突太多，怎么办？**
```bash
# 方案1：取消合并，先同步最新代码
git merge --abort
git checkout develop
git pull origin develop
git checkout feature/your-branch
git merge develop  # 先在 feature 分支解决冲突

# 方案2：使用 rebase（谨慎使用）
git rebase develop
```

**Q2：误提交了敏感信息（密码），怎么办？**
```bash
# 1. 立即修改配置文件，移除密码
# 2. 修改密码（如果已经泄露）
# 3. 提交新的 commit 覆盖
git add .
git commit -m "chore: 移除配置文件中的敏感信息"

# 4. 如果需要从历史中完全删除
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch config/password.txt" \
  --prune-empty --tag-name-filter cat -- --all
```

---

### 8.2 CI/CD 相关问题

**Q1：CI 构建失败，本地明明可以运行？**
```
排查步骤：
1. 检查 CI 使用的 Java 版本是否和本地一致
2. 检查依赖是否都在 build.gradle 中声明
3. 检查是否有环境变量依赖
4. 本地运行：./gradlew clean build -x test
```

**Q2：部署后应用无法启动？**
```
排查步骤：
1. 查看应用日志：docker logs <container-id>
2. 检查配置文件是否正确
3. 检查环境变量是否设置
4. 检查数据库连接是否正常
5. 检查端口是否被占用
```

---

**文档版本**: v1.0
**最后更新**: 2025-11-17
**维护**: DevOps Course Team

**恭喜！** 🎉

你已经完成了三份实战指南的学习：
1. ✅ **阅读代码指南** - 会读代码
2. ✅ **编写代码指南** - 会写代码
3. ✅ **团队协作指南** - 会协作

**下一步**：
- 🚀 实践：将学到的知识应用到真实项目中
- 📚 深入：学习微服务、分布式、高并发等进阶主题
- 🤝 分享：将你的经验分享给团队成员

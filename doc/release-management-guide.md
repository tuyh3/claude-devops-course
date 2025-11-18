# 发布管理指南

> 📋 **文档目的**：规范化发布流程，确保每次上线的质量和可追溯性
> 👥 **目标读者**：所有开发人员、测试人员、发布负责人
> ⏱️ **阅读时长**：约 30-40 分钟

---

## 📖 目录

- [引言](#引言)
- [第1章：发布流程概述](#第1章发布流程概述)
- [第2章：Patch 列表管理](#第2章patch-列表管理)
- [第3章：发布前准备](#第3章发布前准备)
- [第4章：发布执行](#第4章发布执行)
- [第5章：发布后验证](#第5章发布后验证)
- [第6章：回滚方案](#第6章回滚方案)
- [第7章：最佳实践](#第7章最佳实践)
- [附录A：模板和工具](#附录a模板和工具)

---

## 引言

### 为什么需要 Patch 列表

在企业级开发中，每次发布都需要明确记录：
- ✅ **变更内容**：本次发布包含哪些功能和修复
- ✅ **影响范围**：修改了哪些文件，影响哪些模块
- ✅ **责任人**：每个变更的负责人是谁
- ✅ **测试情况**：是否经过充分测试
- ✅ **回滚方案**：如果出问题如何快速回滚

### 发布流程全景图

```
开发阶段
  ↓
开发人员提交 Patch 记录
  ↓
技术负责人审查汇总
  ↓
发布前准备（数据库脚本、配置变更、依赖检查）
  ↓
预发布环境验证
  ↓
生产环境发布
  ↓
发布后验证
  ↓
完成/回滚
```

---

## 第1章：发布流程概述

### 1.1 发布周期

**典型的发布周期**：

```
Week 1-2: 功能开发
  - 开发人员在 feature 分支开发
  - 提交 Code Review
  - 合并到 develop 分支

Week 3: 集成测试
  - 在测试环境验证所有功能
  - 记录和修复 Bug
  - 提交 Patch 列表

Week 4: 发布准备
  - 周一：创建 release 分支
  - 周二-周三：预发布环境验证
  - 周四：汇总所有 Patch 列表
  - 周五：生产环境发布
```

### 1.2 发布角色与职责

| 角色 | 职责 |
|------|------|
| **开发人员** | 提交 Patch 记录、准备数据库脚本、编写发布说明 |
| **测试人员** | 验证功能、填写测试报告 |
| **技术负责人** | 审查 Patch 列表、确认技术方案、批准发布 |
| **DBA** | 审查数据库变更、执行数据库脚本 |
| **运维人员** | 执行部署、监控系统状态 |
| **发布经理** | 协调发布流程、汇总文档、沟通各方 |

### 1.3 发布类型

**常规发布**（Regular Release）：
- 周期：每 2-4 周
- 内容：新功能 + Bug 修复
- 流程：完整的发布流程

**紧急发布**（Hotfix Release）：
- 触发：生产环境严重 Bug
- 内容：单个关键修复
- 流程：简化流程，优先发布

**补丁发布**（Patch Release）：
- 触发：小版本 Bug 修复
- 内容：多个小 Bug 修复
- 流程：快速发布流程

---

## 第2章：Patch 列表管理

### 2.1 什么是 Patch 列表

Patch 列表是一份记录所有变更的清单，包括：
- 变更编号
- 变更类型（功能/Bug修复/优化）
- 变更描述
- 影响的文件
- 开发人员
- 测试情况
- 关联的 Issue/需求

### 2.2 Patch 列表模板

**基本信息**：
```markdown
## Patch 基本信息

- **Patch ID**: PATCH-2025-001
- **版本**: v1.2.0
- **提交人**: 张三
- **提交日期**: 2025-01-15
- **类型**: 新功能 / Bug修复 / 性能优化 / 重构
- **优先级**: P0 / P1 / P2 / P3
```

**变更详情**：
```markdown
## 变更描述

### 功能说明
客户列表支持按信用等级批量导出 CSV 功能

### 需求来源
- 需求编号: REQ-2025-0123
- 需求链接: https://jira.example.com/browse/REQ-2025-0123

### 技术方案
1. 添加 CustomerService.exportByCredit() 方法
2. 使用 OpenCSV 库生成 CSV 文件
3. 支持 UTF-8 BOM（解决 Excel 中文乱码）
```

**影响范围**：
```markdown
## 影响的文件

### 新增文件
- src/main/java/com/devops/course/dto/CreditSearchRequest.java
- src/main/java/com/devops/course/dto/CreditSearchResponse.java

### 修改文件
- src/main/java/com/devops/course/service/CustomerService.java
- src/main/java/com/devops/course/controller/CustomerController.java
- build.gradle (添加 opencsv 依赖)

### 删除文件
- 无

### 数据库变更
- 无（或列出 SQL 脚本）
```

**测试情况**：
```markdown
## 测试情况

### 单元测试
- [x] 新增测试用例：CustomerServiceTest.testExportByCredit()
- [x] 覆盖率: 85%
- [x] 所有测试通过

### 集成测试
- [x] 测试环境验证通过
- [x] 测试报告: TEST-2025-001

### 性能测试
- [x] 1000 条数据导出时间: 2.3s
- [x] 10000 条数据导出时间: 18.5s
```

**依赖和配置**：
```markdown
## 依赖变更

### 新增依赖
```groovy
implementation 'com.opencsv:opencsv:5.9'
```

### 配置变更
- 无

### 环境要求
- JDK 21
- 无其他特殊要求
```

**回滚方案**：
```markdown
## 回滚方案

### 回滚步骤
1. 回滚到上一版本: v1.1.0
2. 重启应用

### 数据回滚
- 无数据变更，无需回滚

### 预计回滚时间
- 5 分钟
```

### 2.3 Patch 列表提交流程

#### Step 1：开发人员填写 Patch 记录

```bash
# 1. 复制模板
cp docs/templates/PATCH_TEMPLATE.md patches/PATCH-2025-001-export-customer.md

# 2. 填写 Patch 信息
vim patches/PATCH-2025-001-export-customer.md

# 3. 提交到 Git
git add patches/PATCH-2025-001-export-customer.md
git commit -m "docs: 添加导出客户功能的 Patch 记录"
git push origin feature/export-csv
```

#### Step 2：技术负责人审查

```
1. 检查 Patch 记录是否完整
2. 确认技术方案是否合理
3. 检查是否有遗漏的依赖或配置
4. 批准或要求补充
```

#### Step 3：发布经理汇总

```bash
# 使用自动化脚本生成汇总列表
./scripts/generate-patch-list.sh v1.2.0

# 输出：patches/RELEASE-v1.2.0-SUMMARY.md
```

### 2.4 Patch 编号规范

**格式**：`PATCH-YYYY-XXX`

**示例**：
- `PATCH-2025-001`：2025 年第 1 个 Patch
- `PATCH-2025-002`：2025 年第 2 个 Patch

**特殊编号**：
- `HOTFIX-2025-001`：紧急修复
- `DB-2025-001`：数据库变更

---

## 第3章：发布前准备

### 3.1 发布检查清单

**代码检查**：
```markdown
- [ ] 所有 feature 分支已合并到 develop
- [ ] 所有 Code Review 已完成
- [ ] 代码冲突已解决
- [ ] 代码质量检查通过（SonarQube）
- [ ] 测试覆盖率 > 80%
```

**测试检查**：
```markdown
- [ ] 单元测试全部通过
- [ ] 集成测试通过
- [ ] 回归测试通过
- [ ] 性能测试通过（如有需要）
- [ ] 安全测试通过（如有需要）
```

**文档检查**：
```markdown
- [ ] 所有 Patch 列表已提交
- [ ] 发布说明已编写
- [ ] API 文档已更新
- [ ] 用户手册已更新（如有变更）
```

**数据库检查**：
```markdown
- [ ] 数据库变更脚本已准备
- [ ] 脚本已在测试环境验证
- [ ] DBA 已审查脚本
- [ ] 准备了回滚脚本
```

**配置检查**：
```markdown
- [ ] 配置文件已更新
- [ ] 环境变量已确认
- [ ] 依赖版本已确认
- [ ] 证书/密钥已准备（如有需要）
```

**部署检查**：
```markdown
- [ ] 部署脚本已测试
- [ ] 回滚方案已准备
- [ ] 监控和告警已配置
- [ ] 值班人员已安排
```

### 3.2 创建 Release 分支

```bash
# 1. 从 develop 创建 release 分支
git checkout develop
git pull origin develop
git checkout -b release/v1.2.0

# 2. 更新版本号
vim build.gradle
# version = '1.2.0'

git commit -am "chore: bump version to 1.2.0"

# 3. 推送到远程
git push origin release/v1.2.0
```

### 3.3 生成发布说明

**RELEASE_NOTES.md** 示例：

```markdown
# Release Notes - v1.2.0

**发布日期**: 2025-01-20
**发布类型**: 常规发布

## 🎉 新功能

### 客户管理模块
- 支持按信用等级批量导出 CSV（PATCH-2025-001）
  - 开发人员：张三
  - 测试：已通过
  - 影响范围：CustomerService, CustomerController

- 支持客户详情查看历史记录（PATCH-2025-002）
  - 开发人员：李四
  - 测试：已通过
  - 影响范围：CustomerController, 新增 CustomerHistoryService

## 🐛 Bug 修复

- 修复客户详情手机号脱敏错误（PATCH-2025-003）
  - 开发人员：王五
  - 问题：substring 索引错误导致显示不正确
  - 影响范围：CustomerController

- 修复大数据量查询超时问题（PATCH-2025-004）
  - 开发人员：赵六
  - 问题：未使用分页导致 OOM
  - 影响范围：CustomerRepository

## 🔧 技术改进

- 升级 Spring Boot 到 3.3.8（PATCH-2025-005）
- 优化数据库连接池配置（PATCH-2025-006）

## 📊 统计信息

- 总 Patch 数：6
- 新功能：2
- Bug 修复：2
- 技术改进：2
- 涉及开发人员：4 人
- 代码变更：+1234 -567 行

## 📦 依赖变更

### 新增依赖
- com.opencsv:opencsv:5.9

### 升级依赖
- org.springframework.boot:spring-boot-starter-parent:3.3.8

## 🗄️ 数据库变更

- 无

## ⚠️ 注意事项

1. 本次发布包含依赖升级，请确认测试环境验证通过
2. 导出功能可能消耗较多内存，请关注服务器资源使用情况

## 📞 联系人

- 发布经理：张经理
- 技术负责人：李工
- 值班人员：王工（18:00-24:00）
```

---

## 第4章：发布执行

### 4.1 预发布环境验证

**目的**：在与生产环境完全一致的环境中进行最后验证

```bash
# 1. 部署到预发布环境
ssh deployer@uat-server
cd /opt/app
git fetch origin
git checkout release/v1.2.0
docker-compose pull
docker-compose up -d

# 2. 执行数据库脚本（如有）
psql -h uat-db -U admin -d proddb -f scripts/db-migration-v1.2.0.sql

# 3. 验证功能
# - 手动测试核心功能
# - 运行冒烟测试
# - 检查日志无异常

# 4. 性能测试（可选）
ab -n 1000 -c 10 http://uat-server:8080/api/customers
```

### 4.2 生产环境发布

**发布窗口**：
- 时间：通常在非业务高峰期（如：晚上 21:00-23:00）
- 人员：发布经理、技术负责人、运维、DBA、值班开发
- 沟通：建立发布群，实时沟通

**发布步骤**：

```bash
# === 发布前 ===

# 1. 备份当前版本
ssh deployer@prod-server
cd /opt/app
tar -czf backup/app-$(date +%Y%m%d-%H%M%S).tar.gz .
docker-compose ps > backup/containers-$(date +%Y%m%d-%H%M%S).txt

# 2. 备份数据库（DBA 执行）
# pg_dump / mysqldump / Oracle exp

# === 发布执行 ===

# 3. 停止应用
docker-compose down

# 4. 切换到新版本
git fetch origin
git checkout v1.2.0

# 5. 更新配置（如有变更）
cp /opt/configs/prod/application.yml config/

# 6. 执行数据库脚本（DBA 执行，如有）
psql -h prod-db -U admin -d proddb -f scripts/db-migration-v1.2.0.sql

# 7. 启动新版本
export IMAGE_TAG=v1.2.0
docker-compose up -d

# 8. 检查容器状态
docker-compose ps
docker-compose logs -f app

# === 发布后验证 ===

# 9. 健康检查
for i in {1..10}; do
  curl -f http://localhost:8080/actuator/health && break
  echo "等待应用启动... ($i/10)"
  sleep 5
done

# 10. 烟雾测试
curl http://localhost:8080/api/customers
curl http://localhost:8080/api/health

# 11. 监控检查
# 查看 Grafana/Prometheus
# 查看应用日志
# 查看错误率
```

### 4.3 发布沟通模板

**发布开始通知**：
```
【发布通知】
版本：v1.2.0
开始时间：2025-01-20 21:00
预计时长：30 分钟
影响：应用会短暂不可用（约 2 分钟）
负责人：张经理
```

**发布完成通知**：
```
【发布完成】
版本：v1.2.0
完成时间：2025-01-20 21:28
状态：✅ 成功
验证：健康检查通过，核心功能正常
监控：无异常告警
```

**发布异常通知**：
```
【发布异常】
版本：v1.2.0
问题：应用启动后健康检查失败
影响：应用不可用
处理：正在回滚到 v1.1.0
预计恢复时间：5 分钟
```

---

## 第5章：发布后验证

### 5.1 验证清单

**功能验证**：
```markdown
- [ ] 登录功能正常
- [ ] 核心业务流程正常
- [ ] 本次发布的新功能正常
- [ ] 高频 API 响应正常
```

**性能验证**：
```markdown
- [ ] 应用响应时间正常（< 2s）
- [ ] CPU 使用率正常（< 70%）
- [ ] 内存使用率正常（< 80%）
- [ ] 数据库连接正常
```

**监控验证**：
```markdown
- [ ] 无错误日志
- [ ] 无异常告警
- [ ] 应用指标正常
- [ ] 数据库指标正常
```

### 5.2 监控观察期

**发布后 1 小时**：
- 密切监控系统指标
- 关注错误日志
- 响应用户反馈

**发布后 24 小时**：
- 持续观察系统稳定性
- 分析业务数据
- 收集用户反馈

**发布后 1 周**：
- 确认长期稳定性
- 分析性能趋势
- 总结发布经验

---

## 第6章：回滚方案

### 6.1 回滚决策

**立即回滚**（P0）：
- 应用无法启动
- 核心功能不可用
- 数据安全问题
- 严重性能问题

**计划回滚**（P1）：
- 部分功能异常
- 性能轻微下降
- 非核心功能不可用

### 6.2 回滚步骤

```bash
# === 应用回滚 ===

# 1. 停止当前版本
ssh deployer@prod-server
cd /opt/app
docker-compose down

# 2. 回滚到上一版本
git checkout v1.1.0

# 3. 启动上一版本
export IMAGE_TAG=v1.1.0
docker-compose up -d

# 4. 验证
curl http://localhost:8080/actuator/health

# === 数据库回滚 ===

# 5. 执行回滚脚本（如有数据库变更）
psql -h prod-db -U admin -d proddb -f scripts/db-rollback-v1.2.0.sql

# === 验证 ===

# 6. 健康检查
# 7. 功能验证
# 8. 监控检查
```

### 6.3 回滚后处理

```markdown
1. 通知所有相关人员
2. 记录回滚原因和详细日志
3. 分析问题根因
4. 修复问题并重新测试
5. 安排下次发布
```

---

## 第7章：最佳实践

### 7.1 发布时间选择

**推荐时间**：
- 周二/周三（避免周一和周五）
- 非业务高峰期（晚上 21:00 之后）
- 确保有值班人员可支持

**避免时间**：
- 周五（出问题无人处理）
- 节假日前
- 业务高峰期
- 大型活动前

### 7.2 灰度发布

**什么是灰度发布**：
逐步将新版本推广到所有用户，而不是一次性全量发布

**灰度策略**：
```
阶段1: 5% 用户（内部员工） → 观察 1 小时
阶段2: 20% 用户 → 观察 2 小时
阶段3: 50% 用户 → 观察 4 小时
阶段4: 100% 用户
```

### 7.3 自动化发布

**推荐工具**：
- Jenkins Pipeline
- GitLab CI/CD
- ArgoCD（Kubernetes）
- Spinnaker

**自动化流程**：
```
1. 代码合并到 main → 触发 CI/CD
2. 自动运行测试
3. 自动构建 Docker 镜像
4. 自动部署到预发布环境
5. 手动批准 → 部署到生产环境
6. 自动健康检查
7. 自动通知
```

### 7.4 发布后总结

**发布总结会议**：
- 时间：发布后 1-2 天
- 参与：所有发布相关人员
- 内容：
  - 发布过程回顾
  - 问题和改进点
  - 最佳实践分享
  - 下次发布优化

**发布总结文档**：
```markdown
# 发布总结 - v1.2.0

## 基本信息
- 发布日期：2025-01-20
- 发布时长：28 分钟
- 参与人员：6 人

## 成功经验
1. 提前准备充分，所有 Patch 列表齐全
2. 预发布环境验证有效发现 1 个问题
3. 回滚方案准备充分

## 遇到的问题
1. 数据库脚本执行比预期慢（10分钟 vs 预期5分钟）
2. 某个配置文件遗漏，临时补充

## 改进建议
1. 数据库脚本需要提前在预发布环境测试性能
2. 配置文件检查清单需要更详细
3. 建议增加自动化健康检查脚本

## 指标数据
- 计划时长：30 分钟
- 实际时长：28 分钟
- 回滚次数：0
- 发现问题：0（生产环境）
```

---

## 附录A：模板和工具

### A.1 文件组织结构

```
项目根目录/
├── patches/                  # Patch 列表目录
│   ├── 2025/
│   │   ├── PATCH-2025-001-export-customer.md
│   │   ├── PATCH-2025-002-customer-history.md
│   │   └── ...
│   └── RELEASE-v1.2.0-SUMMARY.md  # 发布汇总
│
├── releases/                 # 发布文档
│   ├── v1.2.0/
│   │   ├── RELEASE_NOTES.md      # 发布说明
│   │   ├── RELEASE_CHECKLIST.md  # 检查清单
│   │   ├── ROLLBACK_PLAN.md      # 回滚方案
│   │   └── POST_RELEASE_SUMMARY.md  # 发布总结
│   └── ...
│
├── scripts/                  # 自动化脚本
│   ├── generate-patch-list.sh    # 生成 Patch 汇总
│   ├── deploy-prod.sh            # 生产部署脚本
│   ├── rollback.sh               # 回滚脚本
│   └── health-check.sh           # 健康检查脚本
│
└── docs/templates/          # 模板文件
    ├── PATCH_TEMPLATE.md
    ├── RELEASE_NOTES_TEMPLATE.md
    └── RELEASE_CHECKLIST_TEMPLATE.md
```

### A.2 相关工具

**Patch 列表生成工具**：
- 见：`scripts/generate-patch-list.sh`

**发布检查清单**：
- 见：`docs/templates/RELEASE_CHECKLIST.md`

**Git 命令参考**：
```bash
# 查看两个版本之间的所有提交
git log v1.1.0..v1.2.0 --oneline

# 查看变更的文件列表
git diff v1.1.0..v1.2.0 --name-only

# 查看详细变更
git diff v1.1.0..v1.2.0

# 生成变更统计
git diff v1.1.0..v1.2.0 --stat
```

---

**文档版本**: v1.0
**最后更新**: 2025-11-17
**维护**: DevOps Course Team

**相关文档**：
- [团队协作指南](team-collaboration-guide.md)
- [CI/CD 配置指南](cicd-setup-guide.md)
- [Git Flow 工作流](../GIT_BRANCHES.md)

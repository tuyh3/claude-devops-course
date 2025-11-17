# Git 分支说明

本项目采用 **Git Flow** 工作流，包含以下分支：

## 🌳 分支结构

```
main (生产分支)
  │
  ├─── develop (开发分支)
  │      │
  │      ├─── feature/example-customer-export (示例：客户导出功能)
  │      ├─── feature/example-redis-cache (示例：Redis 缓存功能)
  │      └─── bugfix/example-fix-phone-mask (示例：修复手机号脱敏Bug)
  │
  └─── hotfix/* (紧急修复分支，从 main 创建)
```

## 📋 分支说明

### main（生产分支）
- **用途**：生产环境代码
- **规则**：
  - ❌ 不允许直接提交
  - ✅ 只能通过 merge release 或 hotfix
  - ✅ 每次合并都打 tag（如 v1.0.0）
- **状态**：随时可部署到生产环境

### develop（开发分支）
- **用途**：日常开发的主分支
- **规则**：
  - ❌ 不直接在 develop 上开发
  - ✅ 从 develop 创建 feature/bugfix 分支
  - ✅ feature/bugfix 完成后合并回 develop
- **状态**：随时可部署到测试环境

### feature/* （功能分支）
- **命名规范**：`feature/功能名称`
- **示例**：
  - `feature/example-customer-export`：客户列表导出 CSV 功能
  - `feature/example-redis-cache`：Redis 缓存集成
- **生命周期**：
  1. 从 develop 创建
  2. 开发功能
  3. 提交 Pull Request
  4. Code Review
  5. 合并到 develop
  6. 删除分支

### bugfix/* （Bug 修复分支）
- **命名规范**：`bugfix/Bug描述`
- **示例**：
  - `bugfix/example-fix-phone-mask`：修复手机号脱敏错误
- **流程**：与 feature 分支类似，从 develop 创建，修复后合并回 develop

### hotfix/* （紧急修复分支）
- **命名规范**：`hotfix/修复描述`
- **特点**：
  - 从 main 分支创建（而不是 develop）
  - 修复生产环境的紧急 Bug
  - 完成后合并到 main 和 develop

## 🚀 使用示例

### 查看所有分支
```bash
git branch -a
```

### 切换到 develop 分支
```bash
git checkout develop
```

### 从 develop 创建新功能分支
```bash
git checkout develop
git pull origin develop
git checkout -b feature/your-feature-name
```

### 从 main 创建紧急修复分支
```bash
git checkout main
git pull origin main
git checkout -b hotfix/urgent-fix
```

## 📚 相关文档

详细的 Git Flow 工作流程请参考：
- **[团队协作指南](doc/team-collaboration-guide.md)** - 第1章：Git Flow 工作流
- **[实战编写代码指南](doc/writing-code-guide.md)** - 第5章：Git 版本控制基础

## ⚠️ 注意事项

1. **示例分支**：当前的 `feature/example-*` 和 `bugfix/example-*` 分支仅用于演示，不包含实际功能代码
2. **实际开发**：请根据实际需求创建对应的功能分支或修复分支
3. **分支删除**：功能开发完成并合并后，及时删除已合并的分支

---

**更新时间**：2025-11-17
**维护**：DevOps Course Team

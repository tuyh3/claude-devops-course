# Prometheus 使用与配置完全指南

> **适用场景**: 已完成 Prometheus 部署，需要深入了解配置和使用
> **前置要求**: 完成 [11-prometheus-grafana-quick-deployment.md](11-prometheus-grafana-quick-deployment.md) 部署
> **目标**: 掌握 Prometheus 配置、PromQL 查询、监控目标管理和告警规则

## 📋 目录

1. [Prometheus 核心概念](#prometheus-核心概念)
2. [配置文件详解](#配置文件详解)
3. [监控目标管理](#监控目标管理)
4. [PromQL 查询语言](#promql-查询语言)
5. [告警规则配置](#告警规则配置)
6. [服务发现机制](#服务发现机制)
7. [实战案例](#实战案例)
8. [性能优化](#性能优化)
9. [最佳实践](#最佳实践)

---

## Prometheus 核心概念

### 数据模型

Prometheus 存储的是**时间序列数据** (Time Series Data)，每条时间序列由以下部分组成:

```
指标名称{标签1="值1", 标签2="值2"} 数值 时间戳
```

**示例**:
```
http_requests_total{method="GET", endpoint="/api/users", status="200"} 1234 1638360000000
```

### 指标类型

#### 1. Counter (计数器)

**特点**: 只增不减的累计指标
**用途**: 请求总数、错误总数、完成任务数

```promql
# 示例: HTTP 请求总数
http_requests_total{job="spring-boot"}

# 计算请求速率 (每秒请求数 QPS)
rate(http_requests_total[5m])
```

#### 2. Gauge (仪表盘)

**特点**: 可增可减的瞬时值
**用途**: CPU使用率、内存使用量、温度

```promql
# 示例: JVM 内存使用量
jvm_memory_used_bytes{job="spring-boot"}

# 内存使用率
(jvm_memory_used_bytes / jvm_memory_max_bytes) * 100
```

#### 3. Histogram (直方图)

**特点**: 对观察值进行分桶统计
**用途**: 请求延迟、响应大小

```promql
# 示例: HTTP 请求延迟直方图
http_request_duration_seconds_bucket{le="0.1"}  # 延迟 ≤ 0.1s 的请求数
http_request_duration_seconds_bucket{le="0.5"}  # 延迟 ≤ 0.5s 的请求数
http_request_duration_seconds_sum               # 总延迟
http_request_duration_seconds_count             # 总请求数

# 计算 P95 延迟 (95% 的请求延迟在此值以下)
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))
```

#### 4. Summary (摘要)

**特点**: 类似 Histogram，但在客户端计算分位数
**用途**: 请求延迟、响应大小

```promql
# 示例: 已预计算的分位数
http_request_duration_seconds{quantile="0.5"}   # P50 (中位数)
http_request_duration_seconds{quantile="0.95"}  # P95
http_request_duration_seconds{quantile="0.99"}  # P99
```

### 任务和实例

- **Job (任务)**: 一组相同目的的监控目标，如 `spring-boot`、`node`
- **Instance (实例)**: 单个监控端点，如 `192.168.1.103:9100`

```yaml
# 配置示例
scrape_configs:
  - job_name: 'spring-boot'      # Job
    static_configs:
      - targets:
        - '192.168.1.103:8080'   # Instance 1
        - '192.168.1.104:8080'   # Instance 2
```

---

## 配置文件详解

### 全局配置 (global)

位置: `~/prometheus/prometheus.yml`

```yaml
global:
  # 抓取间隔 - 多久从目标抓取一次指标
  scrape_interval: 30s

  # 评估间隔 - 多久评估一次告警规则
  evaluation_interval: 30s

  # 抓取超时 - 单次抓取的超时时间
  scrape_timeout: 10s

  # 外部标签 - Prometheus 给自己贴的标签,用于标识数据来源
  # 适用场景: 多 Prometheus 实例、远程存储、联邦集群
  # 单机部署可省略此配置
  external_labels:
    cluster: 'production'   # 集群标识
    region: 'cn-north'      # 区域标识
```

**external_labels 详解**:

`external_labels` 是 Prometheus 的"身份标签",主要用于:

1. **多 Prometheus 实例场景**
   ```yaml
   # Prometheus 1 (北京机房)
   external_labels:
     cluster: 'beijing'
     datacenter: 'dc1'

   # Prometheus 2 (上海机房)
   external_labels:
     cluster: 'shanghai'
     datacenter: 'dc2'
   ```
   当数据汇总到统一的 AlertManager 或远程存储时,能区分来自哪个机房。

2. **告警消息中显示来源**
   ```yaml
   external_labels:
     env: 'production'
   ```
   触发的告警会自动带上 `env=production` 标签,便于识别环境。

3. **联邦集群和远程存储**
   与 Thanos、Cortex 等远程存储系统通信时,标识数据源。

**何时需要配置**:
- ✅ 多个 Prometheus 实例需要区分
- ✅ 使用 AlertManager 且需要在告警中显示集群信息
- ✅ 使用远程存储 (Thanos/Cortex/VictoriaMetrics)
- ❌ 单机部署且不使用远程存储 (可省略)

**配置建议**:
- `scrape_interval`: 15-60秒 (根据业务需求调整)
  - 高频监控: 15s (消耗更多资源)
  - 常规监控: 30s (推荐)
  - 低频监控: 60s (节省资源)
- `scrape_timeout`: 应小于 `scrape_interval`
- `evaluation_interval`: 通常与 `scrape_interval` 相同

### 告警管理器配置 (alerting)

```yaml
alerting:
  alertmanagers:
  - static_configs:
    - targets:
      - '127.0.0.1:9093'
    # 可选: 超时配置
    timeout: 10s
    # 可选: API 路径前缀
    path_prefix: /
```

### 告警规则文件 (rule_files)

```yaml
rule_files:
  - "/home/monitor/prometheus/rules/*.yml"
  - "/home/monitor/prometheus/rules/node/*.yml"
  - "/home/monitor/prometheus/rules/apps/*.yml"
```

**目录结构建议**:
```
~/prometheus/rules/
├── node/
│   ├── cpu.yml
│   ├── memory.yml
│   └── disk.yml
├── apps/
│   ├── spring-boot.yml
│   └── database.yml
└── infrastructure/
    └── network.yml
```

### 抓取配置 (scrape_configs)

#### 静态配置 (static_configs)

**适用场景**: 监控目标固定不变

```yaml
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['127.0.0.1:9090']
        labels:
          env: 'production'
          team: 'ops'
```

#### 文件服务发现 (file_sd_configs)

**适用场景**: 监控目标经常变动，但不想重启 Prometheus

```yaml
scrape_configs:
  - job_name: 'nodes'
    file_sd_configs:
    - files:
      - /home/monitor/prometheus/targets/nodes/*.json
      - /home/monitor/prometheus/targets/nodes/*.yaml
      refresh_interval: 1m  # 文件变更检测间隔
```

**目标文件示例** (`~/prometheus/targets/nodes/production.json`):

```json
[
  {
    "targets": [
      "192.168.1.103:9100",
      "192.168.1.104:9100",
      "192.168.1.105:9100"
    ],
    "labels": {
      "env": "production",
      "region": "cn-north",
      "team": "backend"
    }
  },
  {
    "targets": [
      "192.168.1.200:9100"
    ],
    "labels": {
      "env": "production",
      "region": "cn-north",
      "team": "database"
    }
  }
]
```

#### 重标签 (relabel_configs)

**用途**: 在抓取前修改或过滤目标

```yaml
scrape_configs:
  - job_name: 'nodes'
    file_sd_configs:
    - files: ['/home/monitor/prometheus/targets/nodes.json']

    # 重标签配置
    relabel_configs:
    # 1. 只保留 env=production 的目标
    - source_labels: [env]
      regex: 'production'
      action: keep

    # 2. 删除 team=test 的目标
    - source_labels: [team]
      regex: 'test'
      action: drop

    # 3. 重写 instance 标签
    - source_labels: [__address__]
      target_label: instance
      regex: '([^:]+)(:\d+)?'
      replacement: '${1}'

    # 4. 添加自定义标签
    - target_label: cluster
      replacement: 'k8s-prod'
```

**常用 action**:
- `keep`: 保留匹配的目标
- `drop`: 删除匹配的目标
- `replace`: 替换标签值
- `labelmap`: 批量重命名标签
- `labeldrop`: 删除标签
- `labelkeep`: 只保留指定标签

#### 指标重标签 (metric_relabel_configs)

**用途**: 在抓取后、存储前修改指标

```yaml
scrape_configs:
  - job_name: 'spring-boot'
    static_configs:
      - targets: ['127.0.0.1:8080']

    # 指标重标签 (在存储前处理)
    metric_relabel_configs:
    # 1. 删除不需要的指标
    - source_labels: [__name__]
      regex: 'jvm_gc_.*'
      action: drop

    # 2. 重写标签值
    - source_labels: [method]
      target_label: http_method
      action: replace
```

---

## 监控目标管理

### 添加新的监控目标

#### 方式一: 修改文件服务发现配置 (推荐)

```bash
# 编辑目标文件
vi ~/prometheus/targets/nodes.json

# 添加新节点
[
  {
    "targets": ["192.168.1.106:9100"],
    "labels": {
      "env": "production",
      "region": "cn-south"
    }
  }
]

# Prometheus 会在 refresh_interval 后自动检测到变更
# 无需重启服务!
```

#### 方式二: 热重载配置

```bash
# 修改 prometheus.yml 后执行
curl -X POST http://127.0.0.1:9090/-/reload

# 或发送 SIGHUP 信号
kill -HUP $(pgrep prometheus)
```

**注意**: `--web.enable-lifecycle` 必须启用 (部署脚本已配置)

### 查看监控目标状态

#### Web UI 查看

访问: `http://192.168.1.103:9090/targets`

#### API 查看

```bash
# 查看所有目标
curl http://127.0.0.1:9090/api/v1/targets | jq '.data.activeTargets[] | {job: .labels.job, instance: .labels.instance, health: .health, lastError: .lastError}'

# 输出示例
{
  "job": "node",
  "instance": "127.0.0.1:9100",
  "health": "up",
  "lastError": ""
}
{
  "job": "spring-boot",
  "instance": "127.0.0.1:8080",
  "health": "down",
  "lastError": "dial tcp 127.0.0.1:8080: connect: connection refused"
}
```

#### 过滤特定 job 的目标

```bash
# 只查看 node job 的目标
curl -s http://127.0.0.1:9090/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job == "node") | {instance: .labels.instance, health: .health}'
```

### 监控目标故障排查

#### 目标状态为 DOWN

**常见原因**:

1. **网络不通**
   ```bash
   # 检查网络连通性
   ping 192.168.1.106
   telnet 192.168.1.106 9100
   curl http://192.168.1.106:9100/metrics
   ```

2. **防火墙阻止**
   ```bash
   # 检查防火墙规则
   sudo firewall-cmd --list-ports

   # 开放端口
   sudo firewall-cmd --permanent --add-port=9100/tcp
   sudo firewall-cmd --reload
   ```

3. **Exporter 未运行**
   ```bash
   # 检查进程
   pgrep -f node_exporter

   # 启动 Node Exporter
   ~/node_exporter/start_node_exporter.sh
   ```

4. **抓取超时**
   ```yaml
   # 增加超时时间 (prometheus.yml)
   scrape_configs:
     - job_name: 'slow-targets'
       scrape_timeout: 30s  # 默认 10s
   ```

---

## PromQL 查询语言

### 基础查询

#### 即时向量查询

```promql
# 查询某个指标的当前值
node_memory_MemTotal_bytes

# 带标签过滤
node_memory_MemTotal_bytes{instance="127.0.0.1:9100"}

# 多个标签过滤 (AND)
http_requests_total{job="spring-boot", method="GET", status="200"}

# 标签值正则匹配
http_requests_total{method=~"GET|POST"}

# 标签值不等于
http_requests_total{status!="200"}

# 标签值正则不匹配
http_requests_total{status!~"2.."}
```

#### 区间向量查询

```promql
# 查询过去 5 分钟的数据
node_cpu_seconds_total[5m]

# 查询过去 1 小时的数据
http_requests_total[1h]

# 常用时间单位: s (秒), m (分), h (小时), d (天), w (周), y (年)
```

### 聚合函数

#### sum - 求和

```promql
# 所有实例的总内存
sum(node_memory_MemTotal_bytes)

# 按 job 分组求和
sum by (job) (node_memory_MemTotal_bytes)

# 不包含 instance 标签的求和
sum without (instance) (node_memory_MemTotal_bytes)
```

#### avg - 平均值

```promql
# 所有节点的平均 CPU 使用率
avg(rate(node_cpu_seconds_total{mode="idle"}[5m]))

# 按环境分组计算平均值
avg by (env) (node_memory_MemAvailable_bytes)
```

#### max / min - 最大值 / 最小值

```promql
# 所有节点中内存使用率最高的
max(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes)

# 每个 job 中的最小值
min by (job) (node_filesystem_avail_bytes)
```

#### count - 计数

```promql
# 统计在线的节点数量
count(up{job="node"} == 1)

# 统计每个环境的节点数
count by (env) (up{job="node"})
```

#### topk / bottomk - 前 K 个 / 后 K 个

```promql
# CPU 使用率最高的 5 个节点
topk(5, 100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100))

# 内存最少的 3 个节点
bottomk(3, node_memory_MemAvailable_bytes)
```

### 操作符

#### 算术运算符

```promql
# 内存使用量 (字节)
node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes

# 内存使用率 (百分比)
(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100

# 磁盘使用率
(node_filesystem_size_bytes - node_filesystem_avail_bytes) / node_filesystem_size_bytes * 100
```

#### 比较运算符

```promql
# 内存使用率 > 80% 的节点
(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100 > 80

# CPU 使用率 > 70% 的节点
100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 70

# 返回 0 或 1
up == 1  # 在线的节点
up == 0  # 离线的节点
```

#### 逻辑运算符

```promql
# 内存 > 80% 且 CPU > 70% 的节点
(node_memory_usage > 80) and (node_cpu_usage > 70)

# 内存 > 80% 或 CPU > 70% 的节点
(node_memory_usage > 80) or (node_cpu_usage > 70)

# 内存 > 80% 但 CPU < 50% 的节点
(node_memory_usage > 80) unless (node_cpu_usage > 50)
```

### 常用函数

#### rate - 计算增长率

**用途**: 计算 Counter 类型指标的每秒平均增长率

```promql
# HTTP 请求速率 (QPS)
rate(http_requests_total[5m])

# CPU 使用率
100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

# 网络流量速率 (字节/秒)
rate(node_network_receive_bytes_total[5m])
```

**注意**: 至少需要 2 个数据点，时间范围应 ≥ 2 × `scrape_interval`

#### irate - 瞬时增长率

**用途**: 计算最近两个数据点的增长率 (更敏感)

```promql
# 瞬时 QPS
irate(http_requests_total[5m])
```

**rate vs irate**:
- `rate`: 平滑，适合告警和长期趋势
- `irate`: 敏感，适合峰值检测和实时监控

#### increase - 增长量

```promql
# 过去 1 小时的请求总数
increase(http_requests_total[1h])

# 等价于
rate(http_requests_total[1h]) * 3600
```

#### delta - 变化量 (Gauge)

```promql
# 过去 5 分钟内存变化量
delta(node_memory_MemAvailable_bytes[5m])
```

#### predict_linear - 线性预测

```promql
# 预测 4 小时后的磁盘使用量
predict_linear(node_filesystem_avail_bytes[1h], 4 * 3600)

# 预测磁盘何时用尽 (秒)
(node_filesystem_avail_bytes - predict_linear(node_filesystem_avail_bytes[1h], 3600)) / abs(deriv(node_filesystem_avail_bytes[1h]))
```

#### histogram_quantile - 分位数计算

```promql
# P50 延迟 (中位数)
histogram_quantile(0.5, rate(http_request_duration_seconds_bucket[5m]))

# P95 延迟
histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))

# P99 延迟
histogram_quantile(0.99, rate(http_request_duration_seconds_bucket[5m]))
```

---

## 告警规则配置

### 告警规则结构

创建 `~/prometheus/rules/node/cpu.yml`:

```yaml
groups:
- name: node-cpu-alerts    # 规则组名称
  interval: 30s             # 评估间隔 (可选,默认使用 global.evaluation_interval)
  rules:
  - alert: HighCPUUsage    # 告警名称
    expr: |                 # PromQL 表达式
      100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
    for: 2m                 # 持续时间 (触发条件持续多久才告警)
    labels:                 # 告警标签
      severity: warning
      team: ops
    annotations:            # 告警注解 (描述信息)
      summary: "节点 {{ $labels.instance }} CPU 使用率过高"
      description: "当前 CPU 使用率: {{ $value | humanizePercentage }}"
```

**关键字段说明**:
- `alert`: 告警名称 (唯一标识)
- `expr`: PromQL 表达式 (返回结果触发告警)
- `for`: 持续时间 (避免瞬时波动引发告警)
- `labels`: 附加标签 (用于路由和分组)
- `annotations`: 描述信息 (显示在通知中)

### 常用告警规则

#### CPU 告警

```yaml
groups:
- name: cpu-alerts
  rules:
  # CPU 使用率过高
  - alert: HighCPUUsage
    expr: 100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
    for: 3m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} CPU 使用率过高"
      description: "当前使用率: {{ $value | printf \"%.2f\" }}%"

  # CPU 使用率严重过高
  - alert: CriticalCPUUsage
    expr: 100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 95
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "🚨 {{ $labels.instance }} CPU 负载严重"
      description: "当前使用率: {{ $value | printf \"%.2f\" }}%，请立即处理!"
```

#### 内存告警

```yaml
groups:
- name: memory-alerts
  rules:
  # 内存使用率过高
  - alert: HighMemoryUsage
    expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100 > 85
    for: 3m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} 内存使用率过高"
      description: "当前使用率: {{ $value | printf \"%.2f\" }}% (可用: {{ with query \"node_memory_MemAvailable_bytes{instance='\" }}{{ . | first | value | humanize1024 }}{{ end }}B)"

  # 内存即将耗尽
  - alert: MemoryPressure
    expr: node_memory_MemAvailable_bytes < 1073741824  # < 1GB
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "🚨 {{ $labels.instance }} 内存即将耗尽"
      description: "可用内存仅剩: {{ $value | humanize1024 }}B"
```

#### 磁盘告警

```yaml
groups:
- name: disk-alerts
  rules:
  # 磁盘使用率过高
  - alert: HighDiskUsage
    expr: (1 - (node_filesystem_avail_bytes{fstype!~"tmpfs|fuse.*"} / node_filesystem_size_bytes)) * 100 > 85
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} 磁盘 {{ $labels.mountpoint }} 使用率过高"
      description: "当前使用率: {{ $value | printf \"%.2f\" }}% (剩余: {{ with query (printf \"node_filesystem_avail_bytes{instance='%s',mountpoint='%s'}\" $labels.instance $labels.mountpoint) }}{{ . | first | value | humanize1024 }}{{ end }}B)"

  # 磁盘空间严重不足
  - alert: DiskAlmostFull
    expr: (1 - (node_filesystem_avail_bytes{fstype!~"tmpfs|fuse.*"} / node_filesystem_size_bytes)) * 100 > 95
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "🚨 {{ $labels.instance }} 磁盘即将写满"
      description: "{{ $labels.mountpoint }} 使用率: {{ $value | printf \"%.2f\" }}%"

  # 预测 4 小时后磁盘写满
  - alert: DiskWillFillIn4Hours
    expr: predict_linear(node_filesystem_avail_bytes{fstype!~"tmpfs|fuse.*"}[1h], 4 * 3600) < 0
    for: 10m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} 磁盘空间预计 4 小时内耗尽"
      description: "{{ $labels.mountpoint }} 当前可用: {{ with query (printf \"node_filesystem_avail_bytes{instance='%s',mountpoint='%s'}\" $labels.instance $labels.mountpoint) }}{{ . | first | value | humanize1024 }}{{ end }}B"
```

#### 服务存活告警

```yaml
groups:
- name: service-alerts
  rules:
  # 节点宕机
  - alert: NodeDown
    expr: up{job="node"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "🚨 节点 {{ $labels.instance }} 宕机"
      description: "节点已离线超过 1 分钟，请检查!"

  # 应用宕机
  - alert: SpringBootDown
    expr: up{job="spring-boot"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "🚨 应用 {{ $labels.app }} 宕机"
      description: "实例 {{ $labels.instance }} 无法访问"
```

#### 应用性能告警

```yaml
groups:
- name: spring-boot-alerts
  rules:
  # JVM 堆内存使用率过高
  - alert: HighJVMHeapUsage
    expr: (jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100 > 85
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} JVM 堆内存使用率过高"
      description: "当前使用率: {{ $value | printf \"%.2f\" }}%"

  # HTTP 5xx 错误率过高
  - alert: HighHttp5xxRate
    expr: |
      (
        sum by (instance) (rate(http_server_requests_total{status=~"5.."}[5m]))
        /
        sum by (instance) (rate(http_server_requests_total[5m]))
      ) * 100 > 5
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} HTTP 5xx 错误率过高"
      description: "当前错误率: {{ $value | printf \"%.2f\" }}%"

  # 请求延迟过高 (P95 > 1s)
  - alert: HighRequestLatency
    expr: histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m])) > 1
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} 请求延迟过高"
      description: "P95 延迟: {{ $value | printf \"%.2f\" }}s"

  # 数据库连接池耗尽
  - alert: DatabaseConnectionPoolExhausted
    expr: hikaricp_connections_active / hikaricp_connections_max > 0.9
    for: 3m
    labels:
      severity: critical
    annotations:
      summary: "{{ $labels.instance }} 数据库连接池即将耗尽"
      description: "当前使用率: {{ $value | printf \"%.2f\" }}% (活跃连接: {{ with query (printf \"hikaricp_connections_active{instance='%s'}\" $labels.instance) }}{{ . | first | value }}{{ end }} / 最大连接: {{ with query (printf \"hikaricp_connections_max{instance='%s'}\" $labels.instance) }}{{ . | first | value }}{{ end }})"
```

### 模板变量

在 `annotations` 中可以使用:

```yaml
annotations:
  # 标签引用
  summary: "实例 {{ $labels.instance }} 告警"

  # 指标值
  description: "当前值: {{ $value }}"

  # 格式化函数
  value_percent: "{{ $value | printf \"%.2f\" }}%"
  value_bytes: "{{ $value | humanize1024 }}B"

  # 查询其他指标
  extra_info: "内存总量: {{ with query \"node_memory_MemTotal_bytes\" }}{{ . | first | value | humanize1024 }}{{ end }}B"
```

**常用格式化函数**:
- `humanize`: 自动格式化数字 (如 1000000 → 1M)
- `humanize1024`: 按 1024 进制格式化 (字节)
- `humanizePercentage`: 格式化百分比
- `printf "%.2f"`: 保留 2 位小数

### 验证告警规则

```bash
# 1. 检查语法
~/prometheus/bin/promtool check rules ~/prometheus/rules/*.yml

# 2. 测试特定规则
~/prometheus/bin/promtool test rules ~/prometheus/rules/test.yml

# 3. 热重载配置
curl -X POST http://127.0.0.1:9090/-/reload

# 4. 查看已加载的规则
curl http://127.0.0.1:9090/api/v1/rules | jq '.data.groups[].rules[] | {alert: .name, state: .state}'

# 5. 查看当前触发的告警
curl http://127.0.0.1:9090/api/v1/alerts | jq '.data.alerts[] | {alert: .labels.alertname, state: .state, value: .value}'
```

---

## 服务发现机制

### 文件服务发现 (推荐)

**优点**: 简单、灵活、无需重启

#### JSON 格式

```json
[
  {
    "targets": ["192.168.1.103:9100", "192.168.1.104:9100"],
    "labels": {
      "env": "production",
      "datacenter": "dc1"
    }
  }
]
```

#### YAML 格式

```yaml
- targets:
  - '192.168.1.103:9100'
  - '192.168.1.104:9100'
  labels:
    env: production
    datacenter: dc1
```

#### 动态更新脚本

创建 `~/prometheus/scripts/update_targets.sh`:

```bash
#!/bin/bash

# 从 CMDB 或其他系统获取节点列表
NODES=$(curl -s http://cmdb.company.com/api/nodes?role=web)

# 生成 targets 文件
cat > /home/monitor/prometheus/targets/web-nodes.json << EOF
[
  {
    "targets": [
$(echo "$NODES" | jq -r '.[] | "      \"" + .ip + ":9100\","' | sed '$ s/,$//')
    ],
    "labels": {
      "env": "production",
      "role": "web"
    }
  }
]
EOF

echo "Targets updated: $(date)"
```

定时任务:
```bash
# 每 5 分钟更新一次
crontab -e
*/5 * * * * /home/monitor/prometheus/scripts/update_targets.sh
```

### Kubernetes 服务发现

```yaml
scrape_configs:
- job_name: 'kubernetes-pods'
  kubernetes_sd_configs:
  - role: pod
    namespaces:
      names:
      - default
      - production

  relabel_configs:
  # 只抓取带有 prometheus.io/scrape: "true" 注解的 Pod
  - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
    action: keep
    regex: true

  # 使用注解中的端口
  - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_port]
    action: replace
    target_label: __address__
    regex: (.+)
    replacement: $1

  # 添加 namespace 标签
  - source_labels: [__meta_kubernetes_namespace]
    action: replace
    target_label: kubernetes_namespace
```

### Consul 服务发现

```yaml
scrape_configs:
- job_name: 'consul-services'
  consul_sd_configs:
  - server: 'localhost:8500'
    datacenter: 'dc1'
    services: ['web', 'api', 'database']

  relabel_configs:
  - source_labels: [__meta_consul_service]
    target_label: service
```

---

## 实战案例

### 案例 1: 监控 Oracle 数据库

#### 1. 部署 Oracle Exporter

```bash
# 下载 oracledb_exporter
wget https://github.com/iamseth/oracledb_exporter/releases/download/v0.5.0/oracledb_exporter.linux-amd64.tar.gz
tar -xzf oracledb_exporter.linux-amd64.tar.gz
mv oracledb_exporter ~/oracle_exporter/

# 配置连接
cat > ~/oracle_exporter/.env << 'EOF'
DATA_SOURCE_NAME=TCBS/your_password@192.168.1.66:1521/dbpv
EOF

# 启动 Exporter
cd ~/oracle_exporter
nohup ./oracledb_exporter --web.listen-address=:9161 > logs/exporter.log 2>&1 &
```

#### 2. 配置 Prometheus

```bash
# 添加到 targets
cat > ~/prometheus/targets/databases.json << 'EOF'
[
  {
    "targets": ["127.0.0.1:9161"],
    "labels": {
      "job": "oracle",
      "database": "dbpv",
      "env": "production"
    }
  }
]
EOF

# 更新 prometheus.yml
cat >> ~/prometheus/prometheus.yml << 'EOF'
  - job_name: 'oracle'
    file_sd_configs:
    - files: ['/home/monitor/prometheus/targets/databases.json']
      refresh_interval: 1m
EOF

# 重载配置
curl -X POST http://127.0.0.1:9090/-/reload
```

#### 3. 常用查询

```promql
# 数据库会话数
oracledb_sessions_value

# 活跃会话数
oracledb_sessions_value{type="ACTIVE"}

# 表空间使用率
(oracledb_tablespace_bytes{type="used"} / oracledb_tablespace_bytes{type="max"}) * 100

# 等待事件
rate(oracledb_wait_time_total[5m])
```

### 案例 2: 监控 Spring Boot 应用

#### 1. 应用集成 Actuator

`build.gradle`:
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

`application.yml`:
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    prometheus:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
      env: production
```

#### 2. 配置 Prometheus

```json
[
  {
    "targets": ["192.168.1.103:8080"],
    "labels": {
      "job": "spring-boot",
      "app": "tcbs-system",
      "env": "production"
    }
  }
]
```

#### 3. 常用查询

```promql
# JVM 堆内存使用率
(jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"}) * 100

# HTTP 请求 QPS
sum(rate(http_server_requests_total[1m])) by (uri, method)

# P95 响应时间
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le, uri))

# 数据库连接池
hikaricp_connections_active
hikaricp_connections_idle
hikaricp_connections_pending
```

### 案例 3: 自定义 Exporter

创建 `~/custom_exporter/app_exporter.py`:

```python
#!/usr/bin/env python3
from prometheus_client import start_http_server, Gauge
import time
import psutil

# 定义指标
cpu_usage = Gauge('custom_cpu_usage_percent', 'CPU usage percentage')
memory_usage = Gauge('custom_memory_usage_percent', 'Memory usage percentage')

def collect_metrics():
    """收集指标"""
    while True:
        cpu_usage.set(psutil.cpu_percent(interval=1))
        memory_usage.set(psutil.virtual_memory().percent)
        time.sleep(15)  # 每 15 秒采集一次

if __name__ == '__main__':
    # 启动 HTTP 服务器 (默认端口 8000)
    start_http_server(9200)
    print("Custom Exporter started on :9200")
    collect_metrics()
```

启动:
```bash
pip3 install prometheus_client psutil
nohup python3 ~/custom_exporter/app_exporter.py > logs/exporter.log 2>&1 &
```

---

## 性能优化

### 存储优化

#### 1. 调整数据保留时间

```bash
# 启动参数 (修改 start_prometheus.sh)
--storage.tsdb.retention.time=15d   # 保留 15 天 (默认)
--storage.tsdb.retention.size=50GB  # 最大 50GB
```

#### 2. 压缩块大小

```bash
--storage.tsdb.min-block-duration=2h  # 最小块持续时间
--storage.tsdb.max-block-duration=36h # 最大块持续时间
```

### 查询优化

#### 1. 减少查询范围

```promql
# ❌ 不推荐: 查询时间太长
rate(http_requests_total[24h])

# ✅ 推荐: 5-10 分钟足够
rate(http_requests_total[5m])
```

#### 2. 使用 recording rules

对于复杂且频繁的查询，创建预计算规则:

```yaml
groups:
- name: recording-rules
  interval: 30s
  rules:
  # 预计算 CPU 使用率
  - record: instance:node_cpu_usage:rate5m
    expr: 100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100)

  # 预计算内存使用率
  - record: instance:node_memory_usage:ratio
    expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100
```

使用:
```promql
# 直接查询预计算的指标
instance:node_cpu_usage:rate5m > 80
```

### 抓取优化

#### 1. 调整抓取间隔

```yaml
global:
  scrape_interval: 30s  # 高频: 15s, 常规: 30s, 低频: 60s

scrape_configs:
  # 为不同 job 设置不同间隔
  - job_name: 'critical-services'
    scrape_interval: 15s  # 关键服务高频抓取

  - job_name: 'background-jobs'
    scrape_interval: 60s  # 后台任务低频抓取
```

#### 2. 过滤不需要的指标

```yaml
scrape_configs:
  - job_name: 'spring-boot'
    metric_relabel_configs:
    # 删除 JVM GC 详细指标
    - source_labels: [__name__]
      regex: 'jvm_gc_.*_seconds_(count|sum)'
      action: drop
```

---

## 最佳实践

### 1. 标签命名规范

```yaml
# ✅ 推荐
job: "spring-boot"
env: "production"
region: "cn-north"
team: "backend"

# ❌ 不推荐 (过于详细，导致高基数)
request_id: "req-123456789"
user_id: "user-987654321"
timestamp: "1638360000"
```

**原则**:
- 使用有限的标签值 (低基数)
- 避免唯一标签 (如 ID、时间戳)
- 使用下划线分隔 (如 `http_requests_total`)

### 2. 指标命名规范

```promql
# Counter (总数)
http_requests_total
errors_total

# Gauge (当前值)
memory_usage_bytes
temperature_celsius

# Histogram/Summary (分布)
http_request_duration_seconds
response_size_bytes
```

**后缀规范**:
- `_total`: Counter 指标
- `_bucket`: Histogram 桶
- `_sum`: 总和
- `_count`: 计数

### 3. 告警分级

```yaml
labels:
  severity: info       # 信息级 (记录即可)
  severity: warning    # 警告级 (需要关注)
  severity: critical   # 严重级 (立即处理)
```

### 4. 告警抑制

避免告警风暴:

```yaml
# AlertManager 配置
inhibit_rules:
- source_match:
    severity: 'critical'
  target_match:
    severity: 'warning'
  equal: ['instance']  # 同一实例的 warning 被 critical 抑制
```

### 5. 配置管理

```bash
# 使用版本控制
cd ~/prometheus
git init
git add prometheus.yml targets/ rules/
git commit -m "Initial Prometheus configuration"

# 修改前备份
cp prometheus.yml prometheus.yml.bak.$(date +%Y%m%d_%H%M%S)
```

### 6. 监控 Prometheus 自身

```promql
# Prometheus 自身状态
up{job="prometheus"}

# 抓取目标总数
count(up)

# 抓取失败的目标
count(up == 0)

# TSDB 块数量
prometheus_tsdb_blocks_loaded

# 数据大小
prometheus_tsdb_storage_blocks_bytes
```

---

## 常见问题排查

### 问题 1: 查询返回空结果

**检查步骤**:

1. 确认指标存在:
   ```promql
   {__name__=~".*"}  # 查看所有指标
   ```

2. 检查标签:
   ```promql
   up{job="node"}  # 确认 job 标签正确
   ```

3. 检查时间范围:
   ```promql
   node_cpu_seconds_total[5m]  # 确保有数据
   ```

### 问题 2: 告警未触发

**检查步骤**:

1. 验证规则语法:
   ```bash
   ~/prometheus/bin/promtool check rules ~/prometheus/rules/*.yml
   ```

2. 手动测试查询:
   ```bash
   # 在 Prometheus UI 执行告警表达式
   100 - (avg by (instance) (rate(node_cpu_seconds_total{mode="idle"}[5m])) * 100) > 80
   ```

3. 检查规则加载:
   ```bash
   curl http://127.0.0.1:9090/api/v1/rules | jq '.data.groups[].rules[] | select(.type == "alerting")'
   ```

### 问题 3: 内存占用过高

**优化措施**:

1. 减少数据保留时间
2. 减少抓取频率
3. 过滤不需要的指标
4. 使用 recording rules

---

## 下一步学习

1. **Grafana 可视化**: 创建美观的监控面板
2. **AlertManager**: 配置告警路由和通知
3. **服务发现**: Kubernetes、Consul 集成
4. **长期存储**: Thanos、Cortex、VictoriaMetrics
5. **高可用部署**: Prometheus 集群和联邦

---

## 参考资源

- 官方文档: https://prometheus.io/docs/
- PromQL 速查表: https://promlabs.com/promql-cheat-sheet/
- 告警规则示例: https://awesome-prometheus-alerts.grep.to/
- Grafana 面板库: https://grafana.com/grafana/dashboards/

---

**文档更新**: 2025-11-30
**作者**: Claude Code
**版本**: v1.0
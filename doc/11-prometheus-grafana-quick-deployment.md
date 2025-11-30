# Prometheus 3.5.0 + Grafana 12.3.0 快速部署指南

> **环境**: RedHat 7.6 / CentOS 7  
> **部署方式**: 二进制文件 + monitor 用户脚本管理  
> **目标**: 快速搭建生产级监控系统

## 📋 目录

1. [Prometheus 简介](#prometheus-简介)
2. [系统环境准备](#系统环境准备)  
3. [快速部署](#快速部署)
4. [配置监控目标](#配置监控目标)
5. [告警配置](#告警配置)
6. [Grafana 可视化](#grafana-可视化)

---

## Prometheus 简介

### 什么是 Prometheus

Prometheus 是一个开源的系统监控和告警工具包，最初由 SoundCloud 构建。2016年加入 Cloud Native Computing Foundation，成为继 Kubernetes 之后的第二个托管项目。

### 核心特性

- **多维数据模型** - 由指标键值对标识的时间序列数据
- **PromQL** - 强大灵活的查询语言  
- **独立自治** - 不依赖分布式存储，单节点自治
- **Pull 模式** - HTTP 拉取时间序列数据
- **服务发现** - 支持静态配置和动态服务发现
- **可视化支持** - 内置表达式浏览器，支持 Grafana

### 系统架构

```
┌──────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Grafana    │───▶│   Prometheus    │───▶│  AlertManager   │
│  (可视化)     │    │    Server       │    │   (告警管理)     │
│  Port: 3000  │    │   Port: 9090    │    │   Port: 9093    │
└──────────────┘    └─────────────────┘    └─────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │  Node Exporter  │
                    │   (系统指标)     │
                    │   Port: 9100    │
                    └─────────────────┘
```

### 核心组件

- **Prometheus Server** - 收集和存储时间序列数据
- **Node Exporter** - 收集系统和硬件指标
- **AlertManager** - 处理告警，支持邮件、钉钉等通知
- **Grafana** - 数据可视化和仪表板

### 监控目标

- **白盒监控** - 通过内部状态预判问题，主动优化
- **黑盒监控** - 监控服务异常，被动响应

---

## 系统环境准备

### 服务器要求

- **操作系统**: RedHat 7.6 / CentOS 7
- **最低配置**: 8GB 内存, 40GB 硬盘  
- **推荐配置**: 16GB 内存, 100GB 硬盘
- **网络**: 内网可达即可

### 系统优化

#### 1. 关闭 SELinux

```bash
# 临时关闭
setenforce 0

# 永久关闭 
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/sysconfig/selinux
sed -i 's/SELINUX=enforcing/SELINUX=disabled/g' /etc/selinux/config

# 验证状态
getenforce
```

#### 2. 配置防火墙

```bash
# 方式一: 关闭防火墙 (简单)
systemctl stop firewalld && systemctl disable firewalld

# 方式二: 开放端口 (生产推荐)
firewall-cmd --permanent --add-port=9090/tcp  # Prometheus
firewall-cmd --permanent --add-port=9093/tcp  # AlertManager  
firewall-cmd --permanent --add-port=9100/tcp  # Node Exporter
firewall-cmd --permanent --add-port=3000/tcp  # Grafana
firewall-cmd --reload
```

#### 3. 创建监控用户

```bash
# 创建 monitor 用户
useradd --create-home --shell /bin/bash monitor

# 切换到 monitor 用户
su - monitor

# 创建监控目录结构
mkdir -p ~/{prometheus,grafana,alertmanager,node_exporter}
cd ~
```

---

## 快速部署

### 1. 下载安装包

#### 在线下载

```bash
# 在 monitor 用户下执行
su - monitor
cd ~

# Prometheus 3.5.0 LTS
wget https://github.com/prometheus/prometheus/releases/download/v3.5.0/prometheus-3.5.0.linux-amd64.tar.gz

# Node Exporter 1.8.2  
wget https://github.com/prometheus/node_exporter/releases/download/v1.8.2/node_exporter-1.8.2.linux-amd64.tar.gz

# AlertManager 0.27.0
wget https://github.com/prometheus/alertmanager/releases/download/v0.27.0/alertmanager-0.27.0.linux-amd64.tar.gz

# Grafana 12.3.0
wget https://dl.grafana.com/oss/release/grafana-12.3.0.linux-amd64.tar.gz
```

#### 离线传输 (生产环境)

```bash
# 通过 scp/sftp 传输到目标服务器
scp *.tar.gz monitor@target-server:~/
```

### 2. 解压安装

```bash
su - monitor
cd ~

# 解压所有组件
tar -xzf prometheus-3.5.0.linux-amd64.tar.gz
tar -xzf node_exporter-1.8.2.linux-amd64.tar.gz  
tar -xzf alertmanager-0.27.0.linux-amd64.tar.gz
tar -xzf grafana-12.3.0.linux-amd64.tar.gz

# 创建目录结构并移动二进制文件
mkdir -p ~/prometheus/bin ~/node_exporter/bin ~/alertmanager/bin
mv prometheus-3.5.0.linux-amd64/* ~/prometheus/bin/
mv node_exporter-1.8.2.linux-amd64/* ~/node_exporter/bin/
mv alertmanager-0.27.0.linux-amd64/* ~/alertmanager/bin/
mv grafana-12.3.0 ~/grafana

# 清理临时目录
rm -rf prometheus-3.5.0.linux-amd64 node_exporter-1.8.2.linux-amd64 alertmanager-0.27.0.linux-amd64

# 设置可执行权限
chmod +x ~/prometheus/bin/prometheus ~/prometheus/bin/promtool
chmod +x ~/node_exporter/bin/node_exporter
chmod +x ~/alertmanager/bin/alertmanager
chmod +x ~/grafana/bin/grafana-server

# 验证安装
ls -la ~/prometheus/bin/
ls -la ~/node_exporter/bin/
ls -la ~/alertmanager/bin/
```

### 3. 配置 Prometheus

#### 主配置文件

创建 `~/prometheus/prometheus.yml`:

```yaml
# Prometheus 3.5.0 配置
global:
  scrape_interval: 30s
  evaluation_interval: 30s
  scrape_timeout: 10s

# 告警管理器配置
alerting:
  alertmanagers:
  - static_configs:
    - targets:
      - 127.0.0.1:9093

# 告警规则文件
rule_files:
  - "/home/monitor/prometheus/rules/*.yml"

# 监控目标配置
scrape_configs:
  # Prometheus 自监控
  - job_name: 'prometheus'
    static_configs:
      - targets: ['127.0.0.1:9090']

  # 节点监控 (使用文件服务发现)
  - job_name: 'nodes'
    file_sd_configs:
    - refresh_interval: 1m
      files:
      - /home/monitor/prometheus/targets/nodes.json

  # Spring Boot 应用监控
  - job_name: 'spring-boot'
    file_sd_configs:
    - refresh_interval: 1m  
      files:
      - /home/monitor/prometheus/targets/apps.json
```

#### 监控目标配置

```bash
# 创建目标配置目录
mkdir -p ~/prometheus/{rules,targets}
```

创建 `~/prometheus/targets/nodes.json`:

```json
[
  {
    "targets": ["127.0.0.1:9100"],
    "labels": {
      "job": "node",
      "env": "production",
      "region": "local"
    }
  }
]
```

创建 `~/prometheus/targets/apps.json`:

```json
[
  {
    "targets": ["127.0.0.1:8080"],
    "labels": {
      "job": "spring-boot",
      "app": "tcbs-system",
      "env": "production"
    }
  }
]
```

### 4. 配置 AlertManager

> **重要**: AlertManager 启动前必须有配置文件，否则会启动失败。

```bash
# 创建 AlertManager 默认配置文件
cat > ~/alertmanager/alertmanager.yml << 'EOF'
# AlertManager 默认配置
global:
  resolve_timeout: 5m

route:
  group_by: ['alertname', 'job']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 12h
  receiver: 'default'

receivers:
- name: 'default'
  # 默认不发送通知，后续可在"告警配置"章节配置邮件/钉钉等
EOF

echo "AlertManager 配置文件已创建: ~/alertmanager/alertmanager.yml"
```

### 5. 创建启动脚本

#### 创建 Prometheus 启动脚本

```bash
# 切换到 monitor 用户
su - monitor

# 创建 Prometheus 启动脚本
cat > ~/prometheus/start_prometheus.sh << 'EOF'
#!/bin/bash
cd ~/prometheus

# 检查进程是否已存在 (排除grep进程)
if pgrep -f "./bin/prometheus" > /dev/null; then
    echo "Prometheus 已在运行中"
    exit 1
fi

# 创建必要目录
mkdir -p logs data

# 启动 Prometheus
nohup ./bin/prometheus \
  --config.file=./prometheus.yml \
  --storage.tsdb.path=./data/ \
  --web.console.templates=./bin/consoles \
  --web.console.libraries=./bin/console_libraries \
  --web.listen-address=0.0.0.0:9090 \
  --web.enable-lifecycle \
  > ./logs/prometheus.log 2>&1 &

PID=$!
echo "Prometheus 已启动，PID: $PID"

# 等待进程启动并验证
sleep 2
if kill -0 $PID 2>/dev/null; then
    echo "Prometheus 启动成功，访问地址: http://localhost:9090"
else
    echo "Prometheus 启动失败，请查看日志: ~/prometheus/logs/prometheus.log"
    exit 1
fi
EOF

# 赋予执行权限
chmod +x ~/prometheus/start_prometheus.sh
```

#### 创建 Node Exporter 启动脚本

```bash
# 创建 Node Exporter 启动脚本
cat > ~/node_exporter/start_node_exporter.sh << 'EOF'
#!/bin/bash
cd ~/node_exporter

# 检查进程是否已存在 (排除grep进程)
if pgrep -f "./bin/node_exporter" > /dev/null; then
    echo "Node Exporter 已在运行中"
    exit 1
fi

# 创建必要目录
mkdir -p logs

# 启动 Node Exporter
nohup ./bin/node_exporter \
  --web.listen-address=:9100 \
  > ./logs/node_exporter.log 2>&1 &

PID=$!
echo "Node Exporter 已启动，PID: $PID"

# 等待进程启动并验证
sleep 2
if kill -0 $PID 2>/dev/null; then
    echo "Node Exporter 启动成功，访问地址: http://localhost:9100"
else
    echo "Node Exporter 启动失败，请查看日志: ~/node_exporter/logs/node_exporter.log"
    exit 1
fi
EOF

# 赋予执行权限
chmod +x ~/node_exporter/start_node_exporter.sh
```

#### 创建 AlertManager 启动脚本

```bash
# 创建 AlertManager 启动脚本
cat > ~/alertmanager/start_alertmanager.sh << 'EOF'
#!/bin/bash
cd ~/alertmanager

# 检查进程是否已存在 (排除grep进程)
if pgrep -f "./bin/alertmanager" > /dev/null; then
    echo "AlertManager 已在运行中"
    exit 1
fi

# 创建必要目录
mkdir -p logs data

# 启动 AlertManager
nohup ./bin/alertmanager \
  --config.file=./alertmanager.yml \
  --storage.path=./data/ \
  --web.listen-address=0.0.0.0:9093 \
  > ./logs/alertmanager.log 2>&1 &

PID=$!
echo "AlertManager 已启动，PID: $PID"

# 等待进程启动并验证
sleep 2
if kill -0 $PID 2>/dev/null; then
    echo "AlertManager 启动成功，访问地址: http://localhost:9093"
else
    echo "AlertManager 启动失败，请查看日志: ~/alertmanager/logs/alertmanager.log"
    exit 1
fi
EOF

# 赋予执行权限
chmod +x ~/alertmanager/start_alertmanager.sh
```

#### 创建停止脚本

```bash
# 创建统一停止脚本
cat > ~/stop_all.sh << 'EOF'
#!/bin/bash

echo "正在停止所有监控服务..."

# 停止 Prometheus
if pgrep -f "prometheus" > /dev/null; then
    pkill -f prometheus
    echo "Prometheus 已停止"
else
    echo "Prometheus 未运行"
fi

# 停止 Node Exporter
if pgrep -f "node_exporter" > /dev/null; then
    pkill -f node_exporter
    echo "Node Exporter 已停止"
else
    echo "Node Exporter 未运行"
fi

# 停止 AlertManager
if pgrep -f "alertmanager" > /dev/null; then
    pkill -f alertmanager
    echo "AlertManager 已停止"
else
    echo "AlertManager 未运行"
fi

echo "所有服务已停止"
EOF

# 赋予执行权限
chmod +x ~/stop_all.sh
```

#### 创建状态检查脚本

```bash
# 创建状态检查脚本
cat > ~/check_status.sh << 'EOF'
#!/bin/bash

echo "=== 监控服务状态检查 ==="

# 检查 Prometheus
if pgrep -f "./bin/prometheus" > /dev/null; then
    PID=$(pgrep -f "./bin/prometheus")
    echo "✓ Prometheus 运行中 (PID: $PID)"
    echo "  访问地址: http://127.0.0.1:9090"
else
    echo "✗ Prometheus 未运行"
fi

# 检查 Node Exporter  
if pgrep -f "./bin/node_exporter" > /dev/null; then
    PID=$(pgrep -f "./bin/node_exporter")
    echo "✓ Node Exporter 运行中 (PID: $PID)"
    echo "  访问地址: http://127.0.0.1:9100"
else
    echo "✗ Node Exporter 未运行"
fi

# 检查 AlertManager
if pgrep -f "./bin/alertmanager" > /dev/null; then
    PID=$(pgrep -f "./bin/alertmanager")
    echo "✓ AlertManager 运行中 (PID: $PID)"
    echo "  访问地址: http://127.0.0.1:9093"
else
    echo "✗ AlertManager 未运行"
fi

echo ""
echo "=== 端口监听状态 ==="
# 使用ss命令替代netstat，避免权限问题
if command -v ss > /dev/null; then
    ss -tln | grep -E "(9090|9100|9093|3000)" | while read line; do
        echo "$line"
    done
else
    # 如果没有ss命令，使用netstat但忽略进程信息
    netstat -tln 2>/dev/null | grep -E "(9090|9100|9093|3000)" | while read line; do
        echo "$line"
    done
fi
EOF

# 赋予执行权限
chmod +x ~/check_status.sh
```

### 6. 启动服务

```bash
# 确保以 monitor 用户身份执行
su - monitor

# 创建必要的目录
mkdir -p ~/prometheus/logs
mkdir -p ~/node_exporter/logs
mkdir -p ~/alertmanager/logs

# 启动所有服务
~/prometheus/start_prometheus.sh
~/node_exporter/start_node_exporter.sh
~/alertmanager/start_alertmanager.sh

# 检查服务状态
~/check_status.sh
```

### 7. 验证部署

```bash
# 检查 Prometheus
curl http://127.0.0.1:9090/api/v1/targets

# 检查 Node Exporter
curl http://127.0.0.1:9100/metrics | head -10

# 检查 AlertManager (使用 v2 API)
curl http://127.0.0.1:9093/api/v2/status

# 浏览器访问
# Prometheus: http://your-server:9090
# AlertManager: http://your-server:9093
```

---

## 配置监控目标

### 添加新的监控节点

#### 1. 部署 Node Exporter 到目标主机

```bash
# 在目标主机上执行
wget https://github.com/prometheus/node_exporter/releases/download/v1.8.2/node_exporter-1.8.2.linux-amd64.tar.gz
tar -xzf node_exporter-1.8.2.linux-amd64.tar.gz
./node_exporter-1.8.2.linux-amd64/node_exporter &
```

#### 2. 更新监控目标

编辑 `~/prometheus/targets/nodes.json`:

```json
[
  {
    "targets": ["127.0.0.1:9100"],
    "labels": {
      "job": "node",
      "env": "production",
      "region": "local"
    }
  },
  {
    "targets": ["192.168.1.100:9100"],
    "labels": {
      "job": "node",
      "env": "production",
      "region": "server1"
    }
  }
]
```

#### 3. 重载配置

```bash
# Prometheus 支持热重载
curl -X POST http://127.0.0.1:9090/-/reload

# 或重启 Prometheus 服务
~/stop_all.sh
~/start_all.sh
```

### Spring Boot 应用接入

#### 1. 应用添加依赖

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
}
```

#### 2. 配置 actuator

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
    export:
      prometheus:
        enabled: true
```

#### 3. 更新监控配置

编辑 `~/prometheus/targets/apps.json`:

```json
[
  {
    "targets": ["127.0.0.1:8080"],
    "labels": {
      "job": "spring-boot",
      "app": "tcbs-system",
      "env": "production"
    }
  }
]
```

---

## 告警配置

### 1. 配置 AlertManager

创建 `~/alertmanager/alertmanager.yml`:

```yaml
global:
  resolve_timeout: 5m
  smtp_smarthost: 'smtp.qq.com:587'
  smtp_from: 'your-email@qq.com'
  smtp_auth_username: 'your-email@qq.com' 
  smtp_auth_password: 'your-smtp-password'
  smtp_require_tls: true

route:
  group_by: ['alertname', 'job']
  group_wait: 30s
  group_interval: 5m
  repeat_interval: 12h
  receiver: 'default'
  routes:
  - match:
      severity: critical
    receiver: 'critical-alerts'

receivers:
- name: 'default'
  email_configs:
  - to: 'ops-team@company.com'
    subject: '[Prometheus] {{ .Status }} - {{ .GroupLabels.alertname }}'
    body: |
      {{ range .Alerts }}
      告警: {{ .Annotations.summary }}
      详情: {{ .Annotations.description }}
      时间: {{ .StartsAt.Format "2006-01-02 15:04:05" }}
      实例: {{ .Labels.instance }}
      {{ end }}

- name: 'critical-alerts'
  email_configs:
  - to: 'manager@company.com'
    subject: '[严重告警] {{ .GroupLabels.alertname }}'
    body: |
      🚨 严重告警 🚨
      {{ range .Alerts }}
      {{ .Annotations.summary }}
      {{ .Annotations.description }}
      {{ end }}
```

### 2. 创建告警规则

创建 `~/prometheus/rules/node-alerts.yml`:

```yaml
groups:
- name: node-alerts
  rules:
  # 节点宕机
  - alert: NodeDown
    expr: up{job="node"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "节点 {{ $labels.instance }} 宕机"
      description: "节点 {{ $labels.instance }} 已宕机超过 1 分钟"

  # CPU 使用率过高
  - alert: HighCPUUsage
    expr: 100 - (avg by(instance) (rate(node_cpu_seconds_total{mode="idle"}[2m])) * 100) > 80
    for: 2m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} CPU 使用率过高"
      description: "CPU 使用率: {{ $value }}%"

  # 内存使用率过高  
  - alert: HighMemoryUsage
    expr: (1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100 > 85
    for: 2m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} 内存使用率过高"
      description: "内存使用率: {{ $value }}%"

  # 磁盘使用率过高
  - alert: HighDiskUsage
    expr: (1 - (node_filesystem_avail_bytes / node_filesystem_size_bytes)) * 100 > 90
    for: 2m
    labels:
      severity: critical
    annotations:
      summary: "{{ $labels.instance }} 磁盘空间不足"
      description: "磁盘使用率: {{ $value }}%"
```

### 3. 应用告警规则

创建 `~/prometheus/rules/app-alerts.yml`:

```yaml
groups:
- name: spring-boot-alerts
  rules:
  # 应用宕机
  - alert: SpringBootDown
    expr: up{job="spring-boot"} == 0
    for: 1m
    labels:
      severity: critical
    annotations:
      summary: "应用 {{ $labels.app }} 宕机"
      description: "Spring Boot 应用 {{ $labels.instance }} 无法访问"

  # JVM 内存使用率过高
  - alert: HighJVMMemory
    expr: (jvm_memory_used_bytes / jvm_memory_max_bytes) * 100 > 85
    for: 2m
    labels:
      severity: warning
    annotations:
      summary: "{{ $labels.instance }} JVM 内存使用率过高"
      description: "JVM 内存使用率: {{ $value }}%"

  # HTTP 错误率过高
  - alert: HighHttpErrorRate
    expr: rate(http_server_requests_total{status=~"5.."}[5m]) / rate(http_server_requests_total[5m]) * 100 > 5
    for: 2m
    labels:
      severity: warning  
    annotations:
      summary: "{{ $labels.instance }} HTTP 错误率过高"
      description: "5xx 错误率: {{ $value }}%"
```

### 4. 重启服务

```bash
# 重启 AlertManager 和 Prometheus
~/stop_all.sh
~/alertmanager/start_alertmanager.sh
~/prometheus/start_prometheus.sh

# 验证告警规则
curl http://127.0.0.1:9090/api/v1/rules | jq '.data.groups[].rules[].name'
```

---

## Grafana 可视化

### 1. 部署 Grafana

#### 创建 Grafana 启动脚本

```bash
# 切换到 monitor 用户
su - monitor
cd ~/grafana

# 创建 Grafana 启动脚本
cat > ~/grafana/start_grafana.sh << 'EOF'
#!/bin/bash
cd ~/grafana

# 检查进程是否已存在
if pgrep -f "grafana-server" > /dev/null; then
    echo "Grafana 已在运行中"
    exit 1
fi

# 创建必要目录
mkdir -p logs data/log

# 启动 Grafana
nohup ./bin/grafana-server \
  --homepath=. \
  --config=./conf/defaults.ini \
  > ./logs/grafana.log 2>&1 &

echo "Grafana 已启动，PID: $!"
echo "访问地址: http://localhost:3000"
echo "默认账号: admin/admin"
EOF

# 赋予执行权限
chmod +x ~/grafana/start_grafana.sh
```

#### 更新停止脚本

```bash
# 更新统一停止脚本，添加 Grafana
cat > ~/stop_all.sh << 'EOF'
#!/bin/bash

echo "正在停止所有监控服务..."

# 停止 Prometheus
if pgrep -f "prometheus" > /dev/null; then
    pkill -f prometheus
    echo "Prometheus 已停止"
else
    echo "Prometheus 未运行"
fi

# 停止 Node Exporter
if pgrep -f "node_exporter" > /dev/null; then
    pkill -f node_exporter
    echo "Node Exporter 已停止"
else
    echo "Node Exporter 未运行"
fi

# 停止 AlertManager
if pgrep -f "alertmanager" > /dev/null; then
    pkill -f alertmanager
    echo "AlertManager 已停止"
else
    echo "AlertManager 未运行"
fi

# 停止 Grafana
if pgrep -f "grafana-server" > /dev/null; then
    pkill -f grafana-server
    echo "Grafana 已停止"
else
    echo "Grafana 未运行"
fi

echo "所有服务已停止"
EOF

# 赋予执行权限
chmod +x ~/stop_all.sh
```

#### 更新状态检查脚本

```bash
# 更新状态检查脚本，添加 Grafana
cat > ~/check_status.sh << 'EOF'
#!/bin/bash

echo "=== 监控服务状态检查 ==="

# 检查 Prometheus
if pgrep -f "./prometheus" > /dev/null; then
    PID=$(pgrep -f "./prometheus")
    echo "✓ Prometheus 运行中 (PID: $PID)"
    echo "  访问地址: http://localhost:9090"
else
    echo "✗ Prometheus 未运行"
fi

# 检查 Node Exporter  
if pgrep -f "./node_exporter" > /dev/null; then
    PID=$(pgrep -f "./node_exporter")
    echo "✓ Node Exporter 运行中 (PID: $PID)"
    echo "  访问地址: http://localhost:9100"
else
    echo "✗ Node Exporter 未运行"
fi

# 检查 AlertManager
if pgrep -f "./alertmanager" > /dev/null; then
    PID=$(pgrep -f "./alertmanager")
    echo "✓ AlertManager 运行中 (PID: $PID)"
    echo "  访问地址: http://localhost:9093"
else
    echo "✗ AlertManager 未运行"
fi

# 检查 Grafana
if pgrep -f "./bin/grafana-server" > /dev/null; then
    PID=$(pgrep -f "./bin/grafana-server")
    echo "✓ Grafana 运行中 (PID: $PID)"
    echo "  访问地址: http://localhost:3000"
else
    echo "✗ Grafana 未运行"
fi

echo ""
echo "=== 端口监听状态 ==="
# 使用ss命令替代netstat，避免权限问题
if command -v ss > /dev/null; then
    ss -tln | grep -E "(9090|9100|9093|3000)" | while read line; do
        echo "$line"
    done
else
    # 如果没有ss命令，使用netstat但忽略进程信息
    netstat -tln 2>/dev/null | grep -E "(9090|9100|9093|3000)" | while read line; do
        echo "$line"
    done
fi
EOF

# 赋予执行权限
chmod +x ~/check_status.sh
```

#### 创建启动所有服务脚本

```bash
# 创建一键启动脚本
cat > ~/start_all.sh << 'EOF'
#!/bin/bash

echo "正在启动所有监控服务..."

# 创建必要的日志目录
mkdir -p ~/prometheus/logs
mkdir -p ~/node_exporter/logs  
mkdir -p ~/alertmanager/logs
mkdir -p ~/grafana/logs

# 启动所有服务
echo "启动 Prometheus..."
~/prometheus/start_prometheus.sh

echo "启动 Node Exporter..."
~/node_exporter/start_node_exporter.sh

echo "启动 AlertManager..."
~/alertmanager/start_alertmanager.sh

echo "启动 Grafana..."
~/grafana/start_grafana.sh

echo ""
echo "等待服务启动..."
sleep 5

# 检查服务状态
~/check_status.sh
EOF

# 赋予执行权限
chmod +x ~/start_all.sh
```

### 2. 配置数据源

访问 `http://your-server:3000`，默认账号：`admin/admin`

#### 添加 Prometheus 数据源

1. **导航到**: Configuration → Data Sources
2. **添加数据源**: 选择 "Prometheus"
3. **配置 URL**: `http://127.0.0.1:9090`
4. **保存并测试**

### 3. 导入仪表板

#### 系统监控面板

```json
{
  "dashboard": {
    "title": "Node Exporter 系统监控",
    "panels": [
      {
        "title": "CPU 使用率",
        "targets": [
          {
            "expr": "100 - (avg by(instance) (rate(node_cpu_seconds_total{mode=\"idle\"}[2m])) * 100)"
          }
        ]
      },
      {
        "title": "内存使用率", 
        "targets": [
          {
            "expr": "(1 - (node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes)) * 100"
          }
        ]
      },
      {
        "title": "磁盘使用率",
        "targets": [
          {
            "expr": "(1 - (node_filesystem_avail_bytes / node_filesystem_size_bytes)) * 100"
          }
        ]
      }
    ]
  }
}
```

#### 导入官方模板

1. **访问**: https://grafana.com/grafana/dashboards/
2. **搜索**: "Node Exporter" 或 ID `1860`
3. **导入**: 复制 ID 到 Grafana Import 页面

### 4. Spring Boot 应用监控面板

```json
{
  "dashboard": {
    "title": "Spring Boot 应用监控",
    "panels": [
      {
        "title": "JVM 内存使用率",
        "targets": [
          {
            "expr": "(jvm_memory_used_bytes / jvm_memory_max_bytes) * 100"
          }
        ]
      },
      {
        "title": "HTTP 请求 QPS",
        "targets": [
          {
            "expr": "rate(http_server_requests_total[1m])"
          }
        ]
      },
      {
        "title": "HTTP 响应时间 P95",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))"
          }
        ]
      },
      {
        "title": "数据库连接池",
        "targets": [
          {
            "expr": "hikaricp_connections_active"
          }
        ]
      }
    ]
  }
}
```

---

## 常用操作

### 监控目标管理

```bash
# 添加新监控目标
echo '{"targets": ["192.168.1.101:9100"], "labels": {"job": "node", "env": "test"}}' >> ~/prometheus/targets/nodes.json

# 重载 Prometheus 配置
curl -X POST http://127.0.0.1:9090/-/reload

# 查看所有监控目标
curl http://127.0.0.1:9090/api/v1/targets | jq '.data.activeTargets[].labels'
```

### 查询调试

```bash
# PromQL 查询示例
curl 'http://127.0.0.1:9090/api/v1/query?query=up' | jq '.'
curl 'http://127.0.0.1:9090/api/v1/query?query=node_memory_MemTotal_bytes' | jq '.'
curl 'http://127.0.0.1:9090/api/v1/query?query=rate(node_cpu_seconds_total[5m])' | jq '.'
```

### 服务管理

```bash
# 查看所有监控服务状态
~/check_status.sh

# 重启所有服务
~/stop_all.sh
~/start_all.sh

# 查看服务日志
tail -f ~/prometheus/logs/prometheus.log
tail -f ~/grafana/logs/grafana.log
tail -f ~/node_exporter/logs/node_exporter.log
tail -f ~/alertmanager/logs/alertmanager.log
```

---

## 总结

通过本教程，您已经快速部署了一个完整的监控系统：

✅ **Prometheus** - 时序数据收集和存储  
✅ **Node Exporter** - 系统指标收集  
✅ **AlertManager** - 智能告警管理  
✅ **Grafana** - 数据可视化展示

**下一步建议:**
1. 根据业务需求调整告警规则
2. 创建更多自定义 Grafana 面板
3. 接入更多监控目标（数据库、中间件等）
4. 配置告警通知渠道（钉钉、微信等）

**关键访问地址:**
- Prometheus: http://your-server:9090
- Grafana: http://your-server:3000 (admin/admin)
- AlertManager: http://your-server:9093
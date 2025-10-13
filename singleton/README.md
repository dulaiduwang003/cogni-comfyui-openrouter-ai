# Conni-X-Pro Singleton

[English](./README_en-US.md) | [简体中文](./README.md) | [日本語](./README_ja-JP.md)

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen?style=flat-square)
![License](https://img.shields.io/badge/License-Open%20Source-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

**一个集成 AI 对话、ComfyUI 工作流、用户管理的平台**

[功能特性](#-功能特性) • [技术架构](#-技术架构) • [快速开始](#-快速开始) • [快速使用](#-快速使用指南) • [模块说明](#-模块说明) • [API 文档](#-api-文档) • [部署指南](#-部署指南)

</div>

---

## 📖 项目简介

Conni-X-Pro Singleton 是一个基于 Spring Boot 3.2.2 和 Java 21 构建的平台，采用模块化架构设计，提供完整的 AI 对话、ComfyUI 工作流管理、用户认证、文件存储等功能。

### 🎯 核心亮点

- 🤖 **AI 对话集成** - 支持多模型（OpenRouter）、流式响应、会话管理、PDF/音频解析
- 🎨 **ComfyUI 工作流** - 完整的工作流管理、任务队列、进度追踪、WebSocket 实时推送
- 🔐 **完善的认证体系** - Sa-Token 权限管理、邮箱验证码登录、密码登录、角色权限
- 💾 **分布式架构** - Redis 缓存、Redisson 分布式锁、异步任务处理
- 📊 **系统监控** - 实时资源监控、任务统计、用户统计、AI 调用统计
- 🎁 **积分系统** - 兑换码管理、积分冻结/消费、交易记录

---

## ✨ 功能特性

### 1. AI 对话模块 (LLM)

- ✅ **多模型支持**：集成 OpenRouter 支持免费模型 YML配置
- ✅ **流式响应**：基于 SSE 的实时流式对话
- ✅ **多模态输入**：支持文本、图片、PDF、音频文件
- ✅ **会话管理**：会话创建、删除、消息历史查询
- ✅ **模型注册中心**：动态模型列表、自动刷新、筛选分页
- ✅ **智能增强**：支持联网搜索、图片生成、推理模式

### 2. ComfyUI 工作流模块

- ✅ **工作流管理**：JSON 解析、节点识别、表单配置、分类管理
- ✅ **任务系统**：任务提交、进度追踪、取消/重做、结果保存
- ✅ **实时推送**：WebSocket 连接、任务状态实时更新
- ✅ **资源上传**：自动上传输入文件到 ComfyUI 服务器
- ✅ **多服务器支持**：负载均衡、服务器健康检查
- ✅ **积分扣费**：任务提交前冻结积分，完成后消费

### 3. 用户认证模块 (Auth)

- ✅ **多种登录方式**：密码登录、邮箱验证码登录
- ✅ **用户注册**：邮箱注册、验证码验证
- ✅ **密码管理**：忘记密码、密码重置
- ✅ **用户信息**：头像上传、昵称修改、积分查询
- ✅ **兑换码系统**：兑换码生成、使用、积分充值
- ✅ **交易记录**：积分交易历史、分页查询

### 4. 系统管理模块 (System)

- ✅ **用户管理**：用户列表、创建、编辑、删除、角色管理、密码重置
- ✅ **工作流管理**：工作流上架/下架、分类管理、配置编辑
- ✅ **兑换码管理**：批量生成、额度修改、删除
- ✅ **系统概况**：用户统计、AI 统计、任务统计、资源监控
- ✅ **公告管理**：系统公告设置、清除

### 5. 文件存储模块 (OSS)

- ✅ **阿里云 OSS**：文件上传、URL 生成
- ✅ **文件类型验证**：支持图片、视频、音频、PDF 等
- ✅ **文件大小限制**：可配置的文件大小限制
- ✅ **自动分类存储**：按文件类型自动分类存储

### 6. 通知模块 (Notice)

- ✅ **系统公告**：公告查询、缓存优化
- ✅ **邮件通知**：验证码邮件发送

---

## 🏗️ 技术架构

### 核心技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.2.2 | 应用框架 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0.33 | 关系型数据库 |
| Redis | - | 缓存数据库 |
| Redisson | 3.20.1 | 分布式锁 |
| Sa-Token | 1.38.0 | 权限认证 |
| Aliyun OSS | 3.17.0 | 对象存储 |
| Fastjson | 2.0.42 | JSON 处理 |
| Lombok | 1.18.30 | 代码简化 |
| Guava | 33.4.8 | 工具库 |

### 模块架构

```
singleton
├── application      # 应用启动模块（主入口）
├── common          # 公共模块（实体、工具、配置）
├── auth            # 认证模块（登录、注册、用户管理）
├── llm             # AI 对话模块（OpenRouter 集成）
├── comfyui         # ComfyUI 工作流模块
├── system          # 系统管理模块（后台管理）
├── oss             # 文件存储模块（阿里云 OSS）
└── notice          # 通知模块（公告）
```

### 设计特点

- 🔹 **模块化设计**：各模块独立开发、独立部署
- 🔹 **分层架构**：Controller → Service → Mapper 清晰分层
- 🔹 **统一异常处理**：全局异常拦截、统一响应格式
- 🔹 **限流保护**：基于 Guava RateLimiter 的接口限流
- 🔹 **异步处理**：@Async 异步任务、@Scheduled 定时任务
- 🔹 **分布式锁**：Redisson 分布式锁保证并发安全
- 🔹 **WebSocket 推送**：实时任务进度推送

---

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- ComfyUI 服务器（可选）

### 安装步骤

#### 1. 克隆项目

```bash
git clone https://github.com/dulaiduwang003/Conni-X-Pro.git
cd Conni-X-Pro/singleton
```

#### 2. 数据库配置

创建数据库：

```sql
CREATE DATABASE ghosts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

导入数据库表结构：

```bash
# 方式一：使用提供的 SQL 文件导入
mysql -u root -p ghosts < .sql/table.sql

# 方式二：在 MySQL 客户端中执行
mysql -u root -p
USE ghosts;
SOURCE /path/to/project/.sql/table.sql;
```

> 💡 **提示**：项目已提供完整的数据库表结构文件 `.sql/table.sql`，包含以下表：
> - `user` - 用户表
> - `user_credits` - 用户积分表
> - `credit_transactions` - 积分流水表
> - `redemption_codes` - 兑换码表
> - `workflow` - 工作流表
> - `workflow_category` - 工作流类别表
> - `workflow_form` - 工作流表单表
> - `workflow_output` - 工作流输出节点表
> - `workflow_result` - 工作流作品表

#### 3. 配置文件

修改 `application/src/main/resources/application-dev.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ghosts
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

# 阿里云 OSS 配置
ali:
  certified:
    access-key: your_access_key
    secret-key: your_secret_key
  oss:
    endpoint: oss-accelerate.aliyuncs.com
    bucket-name: your_bucket
    domain: https://your_bucket.oss-accelerate.aliyuncs.com

# OpenRouter 配置
openrouter:
  api-key: your_openrouter_api_key
  api-url: https://openrouter.ai/api/v1
  registry-url: https://openrouter.ai/api/v1/models

# ComfyUI 服务器配置
comfyui:
  servers:
    - url: http://localhost:8188
      max-concurrent-tasks: 3
```

#### 4. 编译运行

```bash
# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar application/target/application-1.0-SNAPSHOT.jar

# 或使用 Maven 直接运行
mvn spring-boot:run -pl application
```

#### 5. 访问应用

- 应用地址：`http://localhost:9898/api`
- 健康检查：`http://localhost:9898/api/actuator/health`
- 默认管理员账号：`admin@example.com` / `admin123`（首次启动自动创建）

---

## 📖 快速使用指南

### 首次登录

项目启动后，使用默认管理员账号登录：

**登录接口**：`POST /api/auth/password-login`

**请求示例**：

```bash
curl -X POST http://localhost:9898/api/auth/password-login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

**响应示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

返回的 `data` 字段即为 Token，后续请求需要在请求头中携带：

```
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### 常用操作

#### 1. 查看用户信息

```bash
curl -X GET http://localhost:9898/api/user/info \
  -H "Authorization: Bearer {your_token}"
```

#### 2. 获取系统概况（需要管理员权限）

```bash
curl -X GET http://localhost:9898/api/system/overview \
  -H "Authorization: Bearer {your_token}"
```

#### 3. 创建普通用户（需要管理员权限）

```bash
curl -X POST http://localhost:9898/api/system/user/create \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "user123",
    "nickname": "普通用户",
    "role": "USER"
  }'
```

#### 4. 生成兑换码（需要管理员权限）

```bash
curl -X POST http://localhost:9898/api/system/redemption-code/create \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "quantity": 10,
    "credits": 1000,
    "prefix": "CONNI"
  }'
```

#### 5. 提交 AI 对话

```bash
curl -X POST http://localhost:9898/api/llm/chat/submit \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "session-001",
    "content": "你好，请介绍一下自己"
  }'
```

#### 6. 流式对话（SSE）

```bash
curl -N http://localhost:9898/api/llm/chat/stream?sessionId=session-001&token={your_token}
```

#### 7. 提交 ComfyUI 工作流任务

```bash
curl -X POST http://localhost:9898/api/workflow/submit-task \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": 1,
    "formData": {
      "prompt": "a beautiful sunset",
      "width": 512,
      "height": 512
    }
  }'
```

#### 8. 查询任务进度

```bash
curl -X GET "http://localhost:9898/api/workflow/get-task-progress?taskId={task_id}" \
  -H "Authorization: Bearer {your_token}"
```

### WebSocket 连接（任务进度推送）

**连接地址**：`ws://localhost:9898/api/ws`

**连接示例**（JavaScript）：

```javascript
const token = 'your_token_here';
const ws = new WebSocket(`ws://localhost:9898/api/ws?token=${token}`);

ws.onopen = () => {
  console.log('WebSocket 连接成功');
};

ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log('收到消息:', message);
  
  if (message.type === 'TASK_PROGRESS') {
    console.log('任务进度:', message.data.progress + '%');
  } else if (message.type === 'TASK_COMPLETE') {
    console.log('任务完成:', message.data.resultUrl);
  }
};

ws.onerror = (error) => {
  console.error('WebSocket 错误:', error);
};

ws.onclose = () => {
  console.log('WebSocket 连接关闭');
};
```

### 文件上传

#### 上传头像

```bash
curl -X POST http://localhost:9898/api/oss/upload \
  -H "Authorization: Bearer {your_token}" \
  -F "file=@/path/to/avatar.jpg" \
  -F "type=AVATAR"
```

#### 上传工作流输入文件

```bash
curl -X POST http://localhost:9898/api/oss/upload \
  -H "Authorization: Bearer {your_token}" \
  -F "file=@/path/to/input.png" \
  -F "type=WORKFLOW_INPUT"
```

### 常见问题

#### Q1: 忘记管理员密码怎么办？

**方法一**：修改配置文件并重启（如果数据库中无管理员账号）

```yaml
admin:
  init:
    enabled: true
    email: admin@example.com
    password: new_password  # 修改密码
```

**方法二**：直接修改数据库

```sql
-- 密码使用 MD5 加密
UPDATE user SET password = MD5('new_password') WHERE email = 'admin@example.com';
```

#### Q2: 如何查看可用的 AI 模型？

```bash
curl -X POST http://localhost:9898/api/llm/get/available-model/page \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "page": 1,
    "size": 20
  }'
```

#### Q3: 如何禁用某个用户？

目前可以通过删除用户实现：

```bash
curl -X POST http://localhost:9898/api/system/user/delete \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123
  }'
```

#### Q4: 如何给用户充值积分？

使用兑换码系统：

1. 管理员创建兑换码
2. 将兑换码发送给用户
3. 用户使用兑换码充值

```bash
# 用户兑换
curl -X POST http://localhost:9898/api/redemption-code/redeem \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CONNI-XXXX-XXXX-XXXX"
  }'
```

---

## 📦 模块说明

### Application 模块

**职责**：应用启动入口、全局配置、过滤器、拦截器

**核心类**：
- `Application.java` - 启动类
- `GlobalInterceptor.java` - 全局异常拦截器
- `AuthInterfaceFilter.java` - 认证过滤器
- `RoutePathFilter.java` - 路由过滤器

### Common 模块

**职责**：公共实体、工具类、配置类、常量定义

**核心内容**：
- **实体类**：User、Workflow、WorkflowResult、RedemptionCode 等
- **工具类**：RedisUtils、CreditUtils、UploadUtil、JwtUtil 等
- **配置类**：RedisConfig、MybatisPlusConfig、WebClientConfiguration、AdminInitConfiguration 等
- **初始化器**：AdminAccountInitializer - 管理员账号自动初始化
- **枚举类**：RoleEnum、TaskStatusEnum、TransactionType 等

### Auth 模块

**职责**：用户认证、注册、登录、用户信息管理

**核心接口**：
- `POST /auth/password-login` - 密码登录
- `POST /auth/email-code-login` - 验证码登录
- `POST /auth/register` - 用户注册
- `POST /auth/forgot-password` - 忘记密码
- `GET /user/info` - 获取用户信息
- `POST /user/update-avatar` - 更新头像
- `POST /redemption-code/redeem` - 兑换码使用

### LLM 模块

**职责**：AI 对话、模型管理、会话管理

**核心接口**：
- `POST /llm/chat/submit` - 提交消息
- `GET /llm/chat/stream` - 流式对话（SSE）
- `POST /llm/get/available-model/page` - 获取模型列表
- `POST /llm/delete/session` - 删除会话

**特性**：
- 支持文本、图片、PDF、音频多模态输入
- 基于 Redis 的会话存储
- 动态模型注册中心
- 流式响应优化

### ComfyUI 模块

**职责**：工作流管理、任务调度、进度追踪

**核心接口**：
- `POST /workflow/submit-task` - 提交任务
- `GET /workflow/get-task-progress` - 获取任务进度
- `GET /workflow/get-task-progress-list` - 获取任务列表
- `POST /workflow/cancel-task` - 取消任务
- `POST /workflow/remake-task` - 重做任务
- `GET /workflow/get-workflows-page` - 获取工作流列表
- `GET /workflow-result/page` - 获取作品列表

**特性**：
- 自动解析 ComfyUI JSON 工作流
- 支持多服务器负载均衡
- WebSocket 实时进度推送
- 任务队列管理
- 积分冻结/消费机制

### System 模块

**职责**：后台管理、用户管理、工作流管理、系统监控

**核心接口**：
- `GET /system/overview` - 系统概况
- `POST /system/user/page` - 用户列表
- `POST /system/user/create` - 创建用户
- `POST /system/workflow/parsing` - 解析工作流
- `POST /system/workflow/save-config` - 保存工作流配置
- `POST /system/redemption-code/create` - 生成兑换码
- `POST /system/notice/set` - 设置公告

**特性**：
- 完整的 CRUD 操作
- 实时系统资源监控（CPU、内存、磁盘）
- 统计数据可视化
- 权限控制（需要 ADMIN 角色）

### OSS 模块

**职责**：文件上传、存储管理

**核心接口**：
- `POST /oss/upload` - 文件上传

**特性**：
- 支持多种文件类型
- 文件大小验证
- 自动生成 CDN 链接

### Notice 模块

**职责**：系统公告、通知管理

**核心接口**：
- `GET /notice/get` - 获取公告

---

## 📡 API 文档

### 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 429 | 请求过于频繁 |
| 500 | 服务器错误 |

### 认证说明

大部分接口需要在请求头中携带 Token：

```
Authorization: Bearer {token}
```

或使用 Sa-Token 的默认 Header：

```
satoken: {token}
```

### 限流说明

部分接口有限流保护：

- 登录接口：0.2 次/秒（IP 级别）
- 消息提交：1 次/秒（用户级别）
- 流式对话：2 次/秒（用户级别）

---

## 🔧 完整配置说明

### 主配置文件 (application.yml)

```yaml
spring:
  profiles:
    active: @spring.profiles.active@  # 激活的环境配置（dev/prod）
  codec:
    max-in-memory-size: 10485760  # 10MB 内存缓冲区
  jackson:
    date-format: yyyy/MM/dd HH:mm:ss
    time-zone: GMT+8
  servlet:
    multipart:
      file-size-threshold: 1024MB
      max-request-size: 10MB
      max-file-size: 10MB
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
  data:
    redis:
      timeout: 10s
      lettuce:
        pool:
          max-active: 200
          max-wait: -1ms
          max-idle: 10
          min-idle: 0

# Sa-Token 权限认证配置
sa-token:
  token-prefix: Bearer
  token-name: Authorization
  active-timeout: -1  # 永不过期
  is-concurrent: true  # 允许并发登录
  is-share: true  # 共享 Token
  is-log: false  # 关闭日志
  token-style: tik  # Token 风格

# MyBatis Plus 配置
mybatis-plus:
  typeAliasesPackage: com.cn.common.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    jdbc-type-for-null: 'null'

server:
  port: 9898
  servlet:
    context-path: /api
```

### 开发环境配置 (application-dev.yml)

```yaml
spring:
  # Redis 配置
  data:
    redis:
      database: 1
      host: 127.0.0.1
      port: 6379
      password: ""
  
  # MySQL 数据库配置
  datasource:
    username: root
    password: "your_password"
    url: jdbc:mysql://127.0.0.1:3306/ghosts?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
  
  # 邮件配置（用于发送验证码）
  mail:
    host: smtp.qq.com
    username: 'your_email@qq.com'
    password: 'your_auth_code'  # QQ邮箱授权码
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
    port: 587

# 管理员账号初始化配置
admin:
  init:
    enabled: true  # 是否启用管理员初始化
    email: admin@example.com  # 管理员邮箱
    password: admin123  # 管理员密码
    nickname: 系统管理员  # 管理员昵称
    initial-credits: 1000000  # 初始积分

# 阿里云配置
ali:
  certified:
    access-key: "your_access_key"
    secret-key: "your_secret_key"
  oss:
    endpoint: "oss-accelerate.aliyuncs.com"
    bucket-name: "your_bucket"
    domain: "https://your_bucket.oss-accelerate.aliyuncs.com"
    # 支持的文件类型配置
    supported-file-types:
      - extension: "jpg"
        mimeType: "image/jpeg"
        maxSizeInBytes: 5242880  # 5MB
      - extension: "jpeg"
        mimeType: "image/jpeg"
        maxSizeInBytes: 5242880
      - extension: "png"
        mimeType: "image/png"
        maxSizeInBytes: 5242880
      - extension: "webp"
        mimeType: "image/webp"
        maxSizeInBytes: 5242880
      - extension: "gif"
        mimeType: "image/gif"
        maxSizeInBytes: 10485760  # 10MB
      - extension: "mp4"
        mimeType: "video/mp4"
        maxSizeInBytes: 104857600  # 100MB
      - extension: "mp3"
        mimeType: "audio/mpeg"
        maxSizeInBytes: 20971520  # 20MB
      - extension: "pdf"
        mimeType: "application/pdf"
        maxSizeInBytes: 104857600  # 100MB

# ComfyUI 配置
comfyui:
  # 服务器列表（支持多服务器负载均衡）
  server:
    - name: COMFYUI-1号
      url: http://localhost:8188
  # 最大提交任务数量
  submit-task-max: 100
  # 任务配置
  task:
    # 连接超时时间（秒）
    max-retry-time: 30
    # 最大重试次数
    max-retries: 2
    # 超时检查间隔（分钟）
    timeout-check-interval: 30
  # ComfyUI 表单支持的文件类型
  supported-file-types:
    jpg: "image/jpeg"
    jpeg: "image/jpeg"
    png: "image/png"
    webp: "image/webp"
    gif: "image/gif"
    mp4: "video/mp4"
    mp3: "audio/mpeg"
    wav: "audio/x-wav"

# OpenRouter AI 配置
open-router:
  api-key: "your_openrouter_api_key"
  base-url: "https://openrouter.ai/api/v1"
  connect-timeout: 30000
  read-timeout: 60000
  
  # 会话配置
  chat:
    session-ttl-seconds: 2592000  # 会话TTL: 30天
  
  # 上下文截断配置
  truncation:
    response-token-reserve: 2000   # 为回复预留token
    text-chars-per-token: 4        # 文本字符/Token比例
    image-token-estimate: 1500     # 单张图片估算token
    file-token-estimate: 4000      # 单个文件估算token
    enable-compression: true       # 启用压缩
  
  # 插件配置
  plugins:
    # 联网搜索插件
    web:
      enabled: true
      max-results: 5
    # 文件解析插件
    file-parser:
      enabled: true
      pdf:
        # 处理引擎：mistral-ocr / pdf-text(免费) / native
        engine: pdf-text
        static-filename: document.pdf
  
  # 模型注册中心配置
  remote-registry:
    enabled: true
    url: "https://openrouter.ai/api/frontend/models"
    cron: "0 0/30 * * * ?"  # 每30分钟刷新一次
    read-timeout-seconds: 10
    # 过滤模式：ALL（所有模型）、FREE（免费模型）、PAID（付费模型）
    filter: FREE
    # 自动选择配置
    auto:
      modelId: 812bc18a-5b61-42d0-a550-f9ed8c2164c5  # 固定模型ID（可选）
      prefer: FREE  # 优先选择免费模型
  
  # 音频处理配置
  audio:
    max-size-bytes: 20971520  # 20MB
    allowed-formats:
      - wav
      - mp3
    default-format: wav
  
  # 深度思考配置
  reasoning:
    enabled: true
    max-tokens: 2000
  
  # 上传限制配置
  upload:
    max-attachment-count: 8  # 最多8个附件

server:
  port: 9898
  servlet:
    context-path: /api
```

### 生产环境配置 (application-prod.yml)

生产环境配置与开发环境类似，但需要注意：

1. **数据库配置**：使用生产数据库地址和强密码
2. **Redis 配置**：使用生产 Redis 地址和密码
3. **日志级别**：调整为 INFO 或 WARN
4. **连接池配置**：根据实际负载调整
5. **API 密钥**：使用生产环境的 API 密钥

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db-host:3306/ghosts
    username: prod_user
    password: "strong_password"
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
  data:
    redis:
      host: prod-redis-host
      port: 6379
      password: "redis_password"
      database: 1

# 生产环境建议关闭调试日志
logging:
  level:
    root: INFO
    com.cn: INFO
```

### 管理员账号初始化说明

系统首次启动时会自动创建管理员账号，无需手动创建。

#### 配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `admin.init.enabled` | 是否启用管理员初始化 | `true` |
| `admin.init.email` | 管理员邮箱（登录账号） | `admin@example.com` |
| `admin.init.password` | 管理员密码 | `admin123` |
| `admin.init.nickname` | 管理员昵称 | `系统管理员` |
| `admin.init.initial-credits` | 初始积分 | `1000000` |

#### 工作原理

1. **启动检测**：应用启动时自动运行 `AdminAccountInitializer`
2. **账号检查**：根据邮箱查询是否已存在管理员账号
3. **自动创建**：如果不存在，则创建管理员账号并初始化积分
4. **幂等性**：重复启动不会重复创建，安全可靠

#### 启动日志示例

```
管理员账号创建成功！
邮箱: admin@example.com
昵称: 系统管理员
角色: ADMIN
初始积分: 1000000
管理员账号初始化完成！
```

#### 安全建议

⚠️ **生产环境部署前请务必修改默认密码！**

```yaml
admin:
  init:
    enabled: true
    email: your_admin@company.com  # 修改为企业邮箱
    password: StrongP@ssw0rd!2024  # 使用强密码
    nickname: 管理员
    initial-credits: 10000000
```

#### 禁用自动初始化

如果不需要自动创建管理员账号，可以禁用此功能：

```yaml
admin:
  init:
    enabled: false  # 禁用管理员初始化
```

---

### 配置项说明

#### 1. 数据库连接池配置

| 配置项 | 说明 | 推荐值 |
|--------|------|--------|
| initial-size | 初始连接数 | 5-10 |
| min-idle | 最小空闲连接 | 5-10 |
| max-active | 最大活跃连接 | 20-50 |
| max-wait | 最大等待时间(ms) | 60000 |

#### 2. Redis 连接池配置

| 配置项 | 说明 | 推荐值 |
|--------|------|--------|
| max-active | 最大连接数 | 200 |
| max-idle | 最大空闲连接 | 10 |
| min-idle | 最小空闲连接 | 0 |
| timeout | 连接超时 | 10s |

#### 3. OpenRouter 模型过滤

- `ALL`：获取所有可用模型
- `FREE`：仅获取免费模型（推荐开发环境）
- `PAID`：仅获取付费模型（推荐生产环境）

#### 4. ComfyUI 任务配置

- `submit-task-max`：最大任务队列数（防止内存溢出）
- `max-retry-time`：单次连接超时时间
- `max-retries`：失败重试次数
- `timeout-check-interval`：超时检查间隔

### 环境变量配置（推荐）

为了安全，建议使用环境变量存储敏感信息：

```bash
# Linux/Mac
export DB_PASSWORD=your_password
export REDIS_PASSWORD=your_password
export ALI_ACCESS_KEY=your_access_key
export ALI_SECRET_KEY=your_secret_key
export OPENROUTER_API_KEY=your_api_key

# Windows
set DB_PASSWORD=your_password
set REDIS_PASSWORD=your_password
```

然后在配置文件中引用：

```yaml
spring:
  datasource:
    password: ${DB_PASSWORD}
  data:
    redis:
      password: ${REDIS_PASSWORD}

ali:
  certified:
    access-key: ${ALI_ACCESS_KEY}
    secret-key: ${ALI_SECRET_KEY}

open-router:
  api-key: ${OPENROUTER_API_KEY}
```

---

## 📊 数据库设计

> 📁 **完整的数据库表结构**：请参考项目根目录下的 `.sql/table.sql` 文件，该文件包含所有表的完整建表语句和索引定义。

### 核心表结构

#### user - 用户表
- `id` - 用户ID
- `email` - 邮箱
- `password` - 密码（加密）
- `nickname` - 昵称
- `avatar` - 头像
- `role` - 角色（USER/ADMIN）
- `created_time` - 创建时间

#### user_credits - 用户积分表
- `user_id` - 用户ID
- `available_credits` - 可用积分
- `frozen_credits` - 冻结积分
- `total_earned` - 总获得积分
- `total_consumed` - 总消费积分

#### credit_transaction - 积分交易表
- `id` - 交易ID
- `user_id` - 用户ID
- `amount` - 交易金额
- `type` - 交易类型（EARN/CONSUME/FREEZE/UNFREEZE）
- `description` - 描述
- `created_time` - 创建时间

#### workflow - 工作流表
- `id` - 工作流ID
- `name` - 名称
- `category_id` - 分类ID
- `json` - 工作流 JSON
- `credits_required` - 所需积分
- `created_time` - 创建时间

#### workflow_form - 工作流表单表
- `id` - 表单ID
- `workflow_id` - 工作流ID
- `node_key` - 节点键
- `label` - 标签
- `type` - 类型（TEXT/IMAGE/NUMBER 等）
- `required` - 是否必填
- `default_value` - 默认值

#### workflow_output - 工作流输出表
- `workflow_id` - 工作流ID
- `node_key` - 输出节点键
- `type` - 输出类型（IMAGE/VIDEO/AUDIO/MODEL）

#### workflow_result - 工作流结果表
- `id` - 结果ID
- `task_id` - 任务ID
- `user_id` - 用户ID
- `workflow_name` - 工作流名称
- `url` - 结果URL
- `type` - 结果类型
- `created_time` - 创建时间

#### redemption_code - 兑换码表
- `id` - 兑换码ID
- `code` - 兑换码
- `credits` - 积分额度
- `status` - 状态（UNUSED/USED）
- `used_by` - 使用者ID
- `used_time` - 使用时间
- `created_time` - 创建时间

---

## 🎯 最佳实践

### 1. 积分管理

```java
// 冻结积分
creditUtils.freezeCredits(userId, amount, "任务提交");

// 消费积分
creditUtils.consumeCredits(userId, amount, "任务完成");

// 解冻积分
creditUtils.unfreezeCredits(userId, amount, "任务取消");
```

### 2. 分布式锁

```java
RLock lock = redissonClient.getLock("LOCK_KEY:" + userId);
try {
    if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
        // 业务逻辑
    }
} finally {
    lock.unlock();
}
```

### 3. 限流使用

```java
@RateLimit(
    permitsPerSecond = 1.0,
    limitType = RateLimit.LimitType.USER,
    message = "操作过于频繁"
)
public Result someApi() {
    // API 逻辑
}
```

### 4. 异步任务

```java
@Async
public void asyncTask() {
    // 异步处理逻辑
}
```

---

## 🔐 安全建议

1. **生产环境配置**：
   - 修改默认密码
   - 使用强密码策略
   - 启用 HTTPS
   - 配置防火墙规则

2. **敏感信息保护**：
   - 使用环境变量存储敏感配置
   - 不要将密钥提交到代码仓库
   - 定期轮换 API 密钥

3. **接口安全**：
   - 启用接口限流
   - 实现请求签名验证
   - 添加 IP 白名单

4. **数据安全**：
   - 定期备份数据库
   - 加密敏感字段
   - 实现审计日志

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 添加必要的注释和文档
- 编写单元测试

---

## 🚢 部署指南

> ⚠️ **重要提醒**：部署生产环境前，请务必修改配置文件中的管理员账号密码、数据库密码、Redis 密码、API 密钥等敏感信息！

### 方式一：传统部署（推荐生产环境）

#### 1. 服务器环境准备

```bash
# 安装 JDK 21
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz
tar -xzf jdk-21_linux-x64_bin.tar.gz
sudo mv jdk-21 /usr/local/

# 配置环境变量
echo 'export JAVA_HOME=/usr/local/jdk-21' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# 验证安装
java -version
```

#### 2. 安装 MySQL 8.0

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server

# 启动 MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation
```

#### 3. 安装 Redis

```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# 启动 Redis
sudo systemctl start redis
sudo systemctl enable redis

# 配置 Redis 密码（可选）
sudo vim /etc/redis/redis.conf
# 找到 # requirepass foobared
# 取消注释并修改为：requirepass your_password
sudo systemctl restart redis
```

#### 4. 创建数据库

```bash
mysql -u root -p
```

```sql
CREATE DATABASE ghosts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'conni_user'@'%' IDENTIFIED BY 'strong_password';
GRANT ALL PRIVILEGES ON ghosts.* TO 'conni_user'@'%';
FLUSH PRIVILEGES;
EXIT;
```

#### 5. 导入数据库表结构

```bash
# 导入项目提供的表结构文件
cd /opt/conni-x-pro
mysql -u conni_user -p ghosts < .sql/table.sql
```

#### 6. 编译项目

```bash
# 克隆项目
git clone https://github.com/dulaiduwang003/Conni-X-Pro.git
cd Conni-X-Pro/singleton

# 修改配置文件
vim application/src/main/resources/application-prod.yml

# 编译打包
mvn clean package -DskipTests -P prod

# 打包后的文件位于
# application/target/application-1.0-SNAPSHOT.jar
```

#### 7. 部署运行

```bash
# 创建部署目录
sudo mkdir -p /opt/conni-x-pro
sudo cp application/target/application-1.0-SNAPSHOT.jar /opt/conni-x-pro/

# 创建启动脚本
sudo vim /opt/conni-x-pro/start.sh
```

启动脚本内容：

```bash
#!/bin/bash
APP_NAME=application-1.0-SNAPSHOT.jar
APP_PATH=/opt/conni-x-pro
LOG_PATH=/opt/conni-x-pro/logs

# 创建日志目录
mkdir -p $LOG_PATH

# JVM 参数配置
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"

# 启动应用
nohup java $JAVA_OPTS -jar $APP_PATH/$APP_NAME \
  > $LOG_PATH/app.log 2>&1 &

echo $! > $APP_PATH/app.pid
echo "Application started with PID: $(cat $APP_PATH/app.pid)"
```

停止脚本 (`stop.sh`)：

```bash
#!/bin/bash
APP_PATH=/opt/conni-x-pro

if [ -f $APP_PATH/app.pid ]; then
    PID=$(cat $APP_PATH/app.pid)
    kill -15 $PID
    echo "Application stopped (PID: $PID)"
    rm $APP_PATH/app.pid
else
    echo "PID file not found"
fi
```

```bash
# 赋予执行权限
sudo chmod +x /opt/conni-x-pro/start.sh
sudo chmod +x /opt/conni-x-pro/stop.sh

# 启动应用
sudo /opt/conni-x-pro/start.sh

# 查看日志
tail -f /opt/conni-x-pro/logs/app.log
```

#### 8. 配置 Systemd 服务（推荐）

```bash
sudo vim /etc/systemd/system/conni-x-pro.service
```

服务配置内容：

```ini
[Unit]
Description=Conni-X-Pro Application
After=syslog.target network.target mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/conni-x-pro
ExecStart=/usr/local/jdk-21/bin/java -Xms512m -Xmx2g -XX:+UseG1GC -Dspring.profiles.active=prod -jar /opt/conni-x-pro/application-1.0-SNAPSHOT.jar
ExecStop=/bin/kill -15 $MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
# 重载 systemd 配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start conni-x-pro

# 设置开机自启
sudo systemctl enable conni-x-pro

# 查看服务状态
sudo systemctl status conni-x-pro

# 查看日志
sudo journalctl -u conni-x-pro -f
```

#### 9. 配置 Nginx 反向代理

```bash
sudo apt install nginx
sudo vim /etc/nginx/sites-available/conni-x-pro
```

Nginx 配置：

```nginx
upstream conni_backend {
    server 127.0.0.1:9898;
}

server {
    listen 80;
    server_name your-domain.com;

    # 日志配置
    access_log /var/log/nginx/conni-access.log;
    error_log /var/log/nginx/conni-error.log;

    # 客户端上传大小限制
    client_max_body_size 100M;

    location /api/ {
        proxy_pass http://conni_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket 支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # 超时配置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # SSE 流式响应配置
    location /api/llm/chat/stream {
        proxy_pass http://conni_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # SSE 特殊配置
        proxy_http_version 1.1;
        proxy_set_header Connection '';
        proxy_buffering off;
        proxy_cache off;
        chunked_transfer_encoding on;
        proxy_read_timeout 3600s;
    }
}
```

```bash
# 启用站点
sudo ln -s /etc/nginx/sites-available/conni-x-pro /etc/nginx/sites-enabled/

# 测试配置
sudo nginx -t

# 重启 Nginx
sudo systemctl restart nginx
```

#### 10. 配置 HTTPS（推荐）

```bash
# 安装 Certbot
sudo apt install certbot python3-certbot-nginx

# 获取 SSL 证书
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

---

### 方式二：Docker 部署

#### 1. 创建 Dockerfile

在项目根目录创建 `Dockerfile`：

```dockerfile
FROM openjdk:21-jdk-slim

LABEL maintainer="dulaiduwang003 <2074055628@qq.com>"

# 设置工作目录
WORKDIR /app

# 复制 jar 包
COPY application/target/application-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 9898

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar /app/app.jar"]
```

#### 2. 创建 docker-compose.yml

```yaml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: conni-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: ghosts
      MYSQL_USER: conni_user
      MYSQL_PASSWORD: conni_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./.sql/table.sql:/docker-entrypoint-initdb.d/init.sql
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  # Redis 缓存
  redis:
    image: redis:7-alpine
    container_name: conni-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes --requirepass redis_password

  # 应用服务
  app:
    build: .
    container_name: conni-app
    restart: always
    ports:
      - "9898:9898"
    depends_on:
      - mysql
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=ghosts
      - DB_USER=conni_user
      - DB_PASSWORD=conni_password
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - REDIS_PASSWORD=redis_password
    volumes:
      - app_logs:/app/logs

volumes:
  mysql_data:
  redis_data:
  app_logs:
```

> 💡 **说明**：docker-compose.yml 中已配置将 `.sql/table.sql` 文件自动导入到 MySQL 容器中，容器启动时会自动创建所有数据表。

#### 3. 构建和运行

```bash
# 编译项目
mvn clean package -DskipTests -P prod

# 构建 Docker 镜像
docker-compose build

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

---

### 方式三：Kubernetes 部署

#### 1. 创建 Deployment

`k8s/deployment.yaml`：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: conni-x-pro
  labels:
    app: conni-x-pro
spec:
  replicas: 3
  selector:
    matchLabels:
      app: conni-x-pro
  template:
    metadata:
      labels:
        app: conni-x-pro
    spec:
      containers:
      - name: conni-x-pro
        image: your-registry/conni-x-pro:latest
        ports:
        - containerPort: 9898
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          value: "mysql-service"
        - name: REDIS_HOST
          value: "redis-service"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "2000m"
        livenessProbe:
          httpGet:
            path: /api/actuator/health
            port: 9898
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/actuator/health
            port: 9898
          initialDelaySeconds: 30
          periodSeconds: 5
```

#### 2. 创建 Service

`k8s/service.yaml`：

```yaml
apiVersion: v1
kind: Service
metadata:
  name: conni-x-pro-service
spec:
  selector:
    app: conni-x-pro
  ports:
  - protocol: TCP
    port: 80
    targetPort: 9898
  type: LoadBalancer
```

#### 3. 部署到 K8s

```bash
# 应用配置
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# 查看状态
kubectl get pods
kubectl get services

# 查看日志
kubectl logs -f deployment/conni-x-pro
```

---

### 部署检查清单

#### 启动前检查

- [ ] MySQL 数据库已创建并导入表结构
- [ ] Redis 服务正常运行
- [ ] 配置文件中的密码、密钥已修改
- [ ] 管理员账号配置已修改（邮箱、密码）
- [ ] 阿里云 OSS 配置正确
- [ ] OpenRouter API Key 有效
- [ ] ComfyUI 服务器可访问（如需要）
- [ ] 邮件服务配置正确

#### 启动后检查

```bash
# 1. 检查应用是否启动
curl http://localhost:9898/api/actuator/health

# 2. 检查数据库连接
mysql -h localhost -u conni_user -p -e "USE ghosts; SHOW TABLES;"

# 3. 检查 Redis 连接
redis-cli -a your_password ping

# 4. 查看应用日志（确认管理员账号创建成功）
tail -f /opt/conni-x-pro/logs/app.log | grep "管理员"

# 5. 验证管理员账号是否创建
mysql -h localhost -u conni_user -p -e "USE ghosts; SELECT email, nickname, role FROM user WHERE role='ADMIN';"

# 6. 测试 API 接口
curl http://localhost:9898/api/notice/get

# 7. 测试管理员登录
curl -X POST http://localhost:9898/api/auth/password-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

#### 性能优化建议

1. **JVM 参数优化**：
```bash
-Xms2g -Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/opt/conni-x-pro/logs/
```

2. **数据库连接池**：
```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
```

3. **Redis 连接池**：
```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 200
          max-idle: 20
          min-idle: 10
```

#### 监控和日志

1. **应用日志**：
```bash
# 实时查看日志
tail -f /opt/conni-x-pro/logs/app.log

# 查看错误日志
grep ERROR /opt/conni-x-pro/logs/app.log

# 日志轮转配置
vim /etc/logrotate.d/conni-x-pro
```

2. **系统监控**：
```bash
# CPU 和内存使用
top
htop

# 磁盘使用
df -h

# 网络连接
netstat -tunlp | grep 9898
```

---

## 📝 开发计划

- [ ] 支持更多 AI 模型提供商
- [ ] 添加 WebSocket 消息推送
- [ ] 实现任务优先级队列
- [ ] 支持工作流版本管理
- [ ] 添加用户行为分析
- [ ] 实现多租户支持
- [ ] 添加 GraphQL API
- [x] 支持 Docker 部署

---

## 📄 许可证

本项目采用开源许可证，具体请查看 [LICENSE](LICENSE) 文件。

---

## 👨‍💻 作者

**时间海 (dulaiduwang003)**

- GitHub: [@dulaiduwang003](https://github.com/dulaiduwang003)
- Email: 2074055628@qq.com
- Organization: bdth, Inc.

---



## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 [Issue](https://github.com/dulaiduwang003/Conni-X-Pro/issues)
- 发送邮件至：2074055628@qq.com

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给一个 Star！⭐**

Made with ❤️ by 时间海

</div>


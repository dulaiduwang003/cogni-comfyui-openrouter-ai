# Conni-X-Pro Singleton

[English](./README_en-US.md) | [简体中文](./README.md) | [日本語](./README_ja-JP.md)

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen?style=flat-square)
![License](https://img.shields.io/badge/License-Open%20Source-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

**A Platform Integrating AI Chat, ComfyUI Workflows, and User Management**

[Features](#-features) • [Technical-Architecture](#-technical-architecture) • [Quick-Start](#-quick-start) • [Usage-Guide](#-usage-guide) • [Module-Descriptions](#-module-descriptions) • [API-Documentation](#-api-documentation) • [Deployment-Guide](#-deployment-guide)

</div>

---

## 📖 Project Overview

Conni-X-Pro Singleton is a platform built on Spring Boot 3.2.2 and Java 21, featuring a modular architecture that provides comprehensive AI chat, ComfyUI workflow management, user authentication, and file storage capabilities.

### 🎯 Core Highlights

- 🤖 **AI Chat Integration** - Supports multiple models (OpenRouter), streaming responses, session management, and PDF/audio parsing.
- 🎨 **ComfyUI Workflows** - Complete workflow management, task queuing, progress tracking, and real-time WebSocket push notifications.
- 🔐 **Comprehensive Authentication** - Sa-Token for permission management, email verification code login, password login, and role-based access control.
- 💾 **Distributed Architecture** - Redis caching, Redisson distributed locks, and asynchronous task processing.
- 📊 **System Monitoring** - Real-time resource monitoring, task statistics, user statistics, and AI call tracking.
- 🎁 **Credits System** - Redemption code management, credit freezing/spending, and transaction records.

---

## ✨ Features

### 1. AI Chat Module (LLM)

- ✅ **Multi-Model Support**: Integrates with OpenRouter, supporting free models via YML configuration.
- ✅ **Streaming Responses**: Real-time streaming chat based on SSE.
- ✅ **Multi-Modal Input**: Supports text, images, PDF, and audio files.
- ✅ **Session Management**: Create, delete, and query message history for sessions.
- ✅ **Model Registry**: Dynamic model lists, auto-refresh, filtering, and pagination.
- ✅ **Intelligent Enhancements**: Supports web search, image generation, and reasoning modes.

### 2. ComfyUI Workflow Module

- ✅ **Workflow Management**: JSON parsing, node recognition, form configuration, and category management.
- ✅ **Task System**: Submit tasks, track progress, cancel/redo, and save results.
- ✅ **Real-Time Push**: WebSocket connection for real-time task status updates.
- ✅ **Resource Upload**: Automatically uploads input files to the ComfyUI server.
- ✅ **Multi-Server Support**: Load balancing and server health checks.
- ✅ **Credit Deduction**: Freezes credits before task submission and deducts upon completion.

### 3. User Authentication Module (Auth)

- ✅ **Multiple Login Methods**: Password login and email verification code login.
- ✅ **User Registration**: Email registration with verification code.
- ✅ **Password Management**: Forgot password and password reset functionalities.
- ✅ **User Information**: Avatar upload, nickname modification, and credit balance inquiry.
- ✅ **Redemption Code System**: Generate and use redemption codes for credit top-ups.
- ✅ **Transaction History**: Credit transaction history with pagination.

### 4. System Management Module (System)

- ✅ **User Management**: User list, create, edit, delete, role management, and password reset.
- ✅ **Workflow Management**: Publish/unpublish workflows, manage categories, and edit configurations.
- ✅ **Redemption Code Management**: Bulk generation, credit amount modification, and deletion.
- ✅ **System Overview**: User statistics, AI statistics, task statistics, and resource monitoring.
- ✅ **Announcement Management**: Set and clear system announcements.

### 5. File Storage Module (OSS)

- ✅ **Aliyun OSS**: File uploads and URL generation.
- ✅ **File Type Validation**: Supports images, videos, audio, PDF, etc.
- ✅ **File Size Limits**: Configurable file size restrictions.
- ✅ **Automatic Categorization**: Automatically stores files in categories based on their type.

### 6. Notification Module (Notice)

- ✅ **System Announcements**: Announcement queries with cache optimization.
- ✅ **Email Notifications**: Sends verification code emails.

---

## 🏗️ Technical Architecture

### Core Tech Stack

| Technology | Version | Description |
|---|---|---|
| Java | 21 | Programming Language |
| Spring Boot | 3.2.2 | Application Framework |
| MyBatis Plus | 3.5.5 | ORM Framework |
| MySQL | 8.0.33 | Relational Database |
| Redis | - | Cache Database |
| Redisson | 3.20.1 | Distributed Locks |
| Sa-Token | 1.38.0 | Authentication & Authorization |
| Aliyun OSS | 3.17.0 | Object Storage |
| Fastjson | 2.0.42 | JSON Processing |
| Lombok | 1.18.30 | Code Simplification |
| Guava | 33.4.8 | Utility Library |

### Module Architecture

```
singleton
├── application      # Application startup module (main entry point)
├── common          # Common modules (entities, utilities, configurations)
├── auth            # Authentication module (login, registration, user management)
├── llm             # AI chat module (OpenRouter integration)
├── comfyui         # ComfyUI workflow module
├── system          # System management module (backend administration)
├── oss             # File storage module (Aliyun OSS)
└── notice          # Notification module (announcements)
```

### Design Features

- 🔹 **Modular Design**: Each module can be developed and deployed independently.
- 🔹 **Layered Architecture**: Clear separation of concerns with Controller → Service → Mapper layers.
- 🔹 **Unified Exception Handling**: Global exception interception and standardized response formats.
- 🔹 **Rate Limiting**: API rate limiting based on Guava RateLimiter.
- 🔹 **Asynchronous Processing**: @Async for asynchronous tasks and @Scheduled for scheduled tasks.
- 🔹 **Distributed Locks**: Redisson for ensuring concurrency safety.
- 🔹 **WebSocket Push**: Real-time task progress notifications.

---

## 🚀 Quick Start

### Prerequisites

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- ComfyUI Server (optional)

### Installation Steps

#### 1. Clone the Project

```bash
git clone https://github.com/dulaiduwang003/Conni-X-Pro.git
cd Conni-X-Pro/singleton
```

#### 2. Database Configuration

Create the database:

```sql
CREATE DATABASE ghosts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Import the database schema:

```bash
# Method 1: Use the provided SQL file
mysql -u root -p ghosts < .sql/table.sql

# Method 2: Execute in MySQL client
mysql -u root -p
USE ghosts;
SOURCE /path/to/project/.sql/table.sql;
```

> 💡 **Tip**: The project includes a complete database schema file `.sql/table.sql`, which contains the following tables:
> - `user` - User table
> - `user_credits` - User credits table
> - `credit_transactions` - Credit transactions table
> - `redemption_codes` - Redemption codes table
> - `workflow` - Workflow table
> - `workflow_category` - Workflow category table
> - `workflow_form` - Workflow form table
> - `workflow_output` - Workflow output node table
> - `workflow_result` - Workflow result table

#### 3. Configuration File

Modify `application/src/main/resources/application-dev.yml`:

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

# Aliyun OSS Configuration
ali:
  certified:
    access-key: your_access_key
    secret-key: your_secret_key
  oss:
    endpoint: oss-accelerate.aliyuncs.com
    bucket-name: your_bucket
    domain: https://your_bucket.oss-accelerate.aliyuncs.com

# OpenRouter Configuration
openrouter:
  api-key: your_openrouter_api_key
  api-url: https://openrouter.ai/api/v1
  registry-url: https://openrouter.ai/api/v1/models

# ComfyUI Server Configuration
comfyui:
  servers:
    - url: http://localhost:8188
      max-concurrent-tasks: 3
```

#### 4. Compile and Run

```bash
# Compile the project
mvn clean package -DskipTests

# Run the project
java -jar application/target/application-1.0-SNAPSHOT.jar

# Or run directly with Maven
mvn spring-boot:run -pl application
```

#### 5. Access the Application

- Application URL: `http://localhost:9898/api`
- Health Check: `http://localhost:9898/api/actuator/health`
- Default Admin Account: `admin@example.com` / `admin123` (created automatically on first launch)

---

## 📖 Usage Guide

### First Login

After starting the project, log in with the default admin account:

**Login Endpoint**: `POST /api/auth/password-login`

**Request Example**:

```bash
curl -X POST http://localhost:9898/api/auth/password-login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

**Response Example**:

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

The `data` field contains the token, which should be included in the headers of subsequent requests:

```
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### Common Operations

#### 1. View User Information

```bash
curl -X GET http://localhost:9898/api/user/info \
  -H "Authorization: Bearer {your_token}"
```

#### 2. Get System Overview (Admin required)

```bash
curl -X GET http://localhost:9898/api/system/overview \
  -H "Authorization: Bearer {your_token}"
```

#### 3. Create a Regular User (Admin required)

```bash
curl -X POST http://localhost:9898/api/system/user/create \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "user123",
    "nickname": "Regular User",
    "role": "USER"
  }'
```

#### 4. Generate Redemption Codes (Admin required)

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

#### 5. Submit an AI Chat Message

```bash
curl -X POST http://localhost:9898/api/llm/chat/submit \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "session-001",
    "content": "Hello, please introduce yourself"
  }'
```

#### 6. Streaming Chat (SSE)

```bash
curl -N http://localhost:9898/api/llm/chat/stream?sessionId=session-001&token={your_token}
```

#### 7. Submit a ComfyUI Workflow Task

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

#### 8. Query Task Progress

```bash
curl -X GET "http://localhost:9898/api/workflow/get-task-progress?taskId={task_id}" \
  -H "Authorization: Bearer {your_token}"
```

### WebSocket Connection (Task Progress Push)

**Connection URL**: `ws://localhost:9898/api/ws`

**Connection Example** (JavaScript):

```javascript
const token = 'your_token_here';
const ws = new WebSocket(`ws://localhost:9898/api/ws?token=${token}`);

ws.onopen = () => {
  console.log('WebSocket connection successful');
};

ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log('Received message:', message);
  
  if (message.type === 'TASK_PROGRESS') {
    console.log('Task progress:', message.data.progress + '%');
  } else if (message.type === 'TASK_COMPLETE') {
    console.log('Task complete:', message.data.resultUrl);
  }
};

ws.onerror = (error) => {
  console.error('WebSocket error:', error);
};

ws.onclose = () => {
  console.log('WebSocket connection closed');
};
```

### File Upload

#### Upload Avatar

```bash
curl -X POST http://localhost:9898/api/oss/upload \
  -H "Authorization: Bearer {your_token}" \
  -F "file=@/path/to/avatar.jpg" \
  -F "type=AVATAR"
```

#### Upload Workflow Input File

```bash
curl -X POST http://localhost:9898/api/oss/upload \
  -H "Authorization: Bearer {your_token}" \
  -F "file=@/path/to/input.png" \
  -F "type=WORKFLOW_INPUT"
```

### FAQ

#### Q1: How to reset the admin password if forgotten?

**Method 1**: Modify the configuration file and restart (if no admin account exists in the database)

```yaml
admin:
  init:
    enabled: true
    email: admin@example.com
    password: new_password  # Change the password
```

**Method 2**: Modify the database directly

```sql
-- The password is encrypted with MD5
UPDATE user SET password = MD5('new_password') WHERE email = 'admin@example.com';
```

#### Q2: How to view available AI models?

```bash
curl -X POST http://localhost:9898/api/llm/get/available-model/page \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "page": 1,
    "size": 20
  }'
```

#### Q3: How to disable a user?

Currently, you can achieve this by deleting the user:

```bash
curl -X POST http://localhost:9898/api/system/user/delete \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123
  }'
```

#### Q4: How to add credits for a user?

Use the redemption code system:

1. Admin creates redemption codes.
2. Send the codes to the user.
3. The user redeems the codes for credits.

```bash
# User redemption
curl -X POST http://localhost:9898/api/redemption-code/redeem \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CONNI-XXXX-XXXX-XXXX"
  }'
```

---

## 📦 Module Descriptions

### Application Module

**Responsibilities**: Application entry point, global configurations, filters, and interceptors.

**Core Classes**:
- `Application.java` - Startup class
- `GlobalInterceptor.java` - Global exception interceptor
- `AuthInterfaceFilter.java` - Authentication filter
- `RoutePathFilter.java` - Route filter

### Common Module

**Responsibilities**: Common entities, utility classes, configuration classes, and constant definitions.

**Core Content**:
- **Entities**: User, Workflow, WorkflowResult, RedemptionCode, etc.
- **Utilities**: RedisUtils, CreditUtils, UploadUtil, JwtUtil, etc.
- **Configurations**: RedisConfig, MybatisPlusConfig, WebClientConfiguration, AdminInitConfiguration, etc.
- **Initializers**: AdminAccountInitializer - Automatic initialization of the admin account.
- **Enums**: RoleEnum, TaskStatusEnum, TransactionType, etc.

### Auth Module

**Responsibilities**: User authentication, registration, login, and user information management.

**Core Endpoints**:
- `POST /auth/password-login` - Password login
- `POST /auth/email-code-login` - Verification code login
- `POST /auth/register` - User registration
- `POST /auth/forgot-password` - Forgot password
- `GET /user/info` - Get user information
- `POST /user/update-avatar` - Update avatar
- `POST /redemption-code/redeem` - Redeem a code

### LLM Module

**Responsibilities**: AI chat, model management, and session management.

**Core Endpoints**:
- `POST /llm/chat/submit` - Submit a message
- `GET /llm/chat/stream` - Streaming chat (SSE)
- `POST /llm/get/available-model/page` - Get model list
- `POST /llm/delete/session` - Delete a session

**Features**:
- Supports text, image, PDF, and audio multi-modal inputs.
- Session storage based on Redis.
- Dynamic model registry.
- Optimized for streaming responses.

### ComfyUI Module

**Responsibilities**: Workflow management, task scheduling, and progress tracking.

**Core Endpoints**:
- `POST /workflow/submit-task` - Submit a task
- `GET /workflow/get-task-progress` - Get task progress
- `GET /workflow/get-task-progress-list` - Get task list
- `POST /workflow/cancel-task` - Cancel a task
- `POST /workflow/remake-task` - Redo a task
- `GET /workflow/get-workflows-page` - Get workflow list
- `GET /workflow-result/page` - Get results list

**Features**:
- Automatically parses ComfyUI JSON workflows.
- Supports multi-server load balancing.
- Real-time progress push via WebSocket.
- Task queue management.
- Credit freeze/consumption mechanism.

### System Module

**Responsibilities**: Backend administration, user management, workflow management, and system monitoring.

**Core Endpoints**:
- `GET /system/overview` - System overview
- `POST /system/user/page` - User list
- `POST /system/user/create` - Create a user
- `POST /system/workflow/parsing` - Parse a workflow
- `POST /system/workflow/save-config` - Save workflow configuration
- `POST /system/redemption-code/create` - Generate redemption codes
- `POST /system/notice/set` - Set an announcement

**Features**:
- Complete CRUD operations.
- Real-time system resource monitoring (CPU, memory, disk).
- Statistical data visualization.
- Access control (requires ADMIN role).

### OSS Module

**Responsibilities**: File uploads and storage management.

**Core Endpoints**:
- `POST /oss/upload` - File upload

**Features**:
- Supports various file types.
- File size validation.
- Automatic generation of CDN links.

### Notice Module

**Responsibilities**: System announcements and notification management.

**Core Endpoints**:
- `GET /notice/get` - Get announcements

---

## 📡 API Documentation

### Unified Response Format

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### Error Code Descriptions

| Error Code | Description |
|---|---|
| 200 | Success |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 429 | Too Many Requests |
| 500 | Internal Server Error |

### Authentication

Most endpoints require a token in the request header:

```
Authorization: Bearer {token}
```

Alternatively, use Sa-Token's default header:

```
satoken: {token}
```

### Rate Limiting

Some endpoints have rate limiting:

- Login: 0.2 requests/second (IP level)
- Message Submission: 1 request/second (user level)
- Streaming Chat: 2 requests/second (user level)

---

## 🔧 Full Configuration Details

### Main Configuration File (application.yml)

```yaml
spring:
  profiles:
    active: @spring.profiles.active@  # Active environment profile (dev/prod)
  codec:
    max-in-memory-size: 10485760  # 10MB memory buffer
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

# Sa-Token Authentication Configuration
sa-token:
  token-prefix: Bearer
  token-name: Authorization
  active-timeout: -1  # Never expires
  is-concurrent: true  # Allow concurrent logins
  is-share: true  # Share token
  is-log: false  # Disable logs
  token-style: tik  # Token style

# MyBatis Plus Configuration
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

### Development Environment Configuration (application-dev.yml)

```yaml
spring:
  # Redis Configuration
  data:
    redis:
      database: 1
      host: 127.0.0.1
      port: 6379
      password: ""
  
  # MySQL Database Configuration
  datasource:
    username: root
    password: "your_password"
    url: jdbc:mysql://127.0.0.1:3306/ghosts?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
  
  # Mail Configuration (for sending verification codes)
  mail:
    host: smtp.qq.com
    username: 'your_email@qq.com'
    password: 'your_auth_code'  # QQ Mail authorization code
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
    port: 587

# Admin Account Initialization Configuration
admin:
  init:
    enabled: true  # Enable admin initialization
    email: admin@example.com  # Admin email
    password: admin123  # Admin password
    nickname: System Administrator  # Admin nickname
    initial-credits: 1000000  # Initial credits

# Aliyun Configuration
ali:
  certified:
    access-key: "your_access_key"
    secret-key: "your_secret_key"
  oss:
    endpoint: "oss-accelerate.aliyuncs.com"
    bucket-name: "your_bucket"
    domain: "https://your_bucket.oss-accelerate.aliyuncs.com"
    # Supported file types configuration
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

# ComfyUI Configuration
comfyui:
  # Server list (supports multi-server load balancing)
  server:
    - name: COMFYUI-1
      url: http://localhost:8188
  # Maximum number of submitted tasks
  submit-task-max: 100
  # Task configuration
  task:
    # Connection timeout (seconds)
    max-retry-time: 30
    # Maximum number of retries
    max-retries: 2
    # Timeout check interval (minutes)
    timeout-check-interval: 30
  # Supported file types for ComfyUI forms
  supported-file-types:
    jpg: "image/jpeg"
    jpeg: "image/jpeg"
    png: "image/png"
    webp: "image/webp"
    gif: "image/gif"
    mp4: "video/mp4"
    mp3: "audio/mpeg"
    wav: "audio/x-wav"

# OpenRouter AI Configuration
open-router:
  api-key: "your_openrouter_api_key"
  base-url: "https://openrouter.ai/api/v1"
  connect-timeout: 30000
  read-timeout: 60000
  
  # Session Configuration
  chat:
    session-ttl-seconds: 2592000  # Session TTL: 30 days
  
  # Context Truncation Configuration
  truncation:
    response-token-reserve: 2000   # Reserve tokens for response
    text-chars-per-token: 4        # Text characters per token ratio
    image-token-estimate: 1500     # Estimated tokens per image
    file-token-estimate: 4000      # Estimated tokens per file
    enable-compression: true       # Enable compression
  
  # Plugin Configuration
  plugins:
    # Web Search Plugin
    web:
      enabled: true
      max-results: 5
    # File Parser Plugin
    file-parser:
      enabled: true
      pdf:
        # Processing engine: mistral-ocr / pdf-text(free) / native
        engine: pdf-text
        static-filename: document.pdf
  
  # Model Registry Configuration
  remote-registry:
    enabled: true
    url: "https://openrouter.ai/api/frontend/models"
    cron: "0 0/30 * * * ?"  # Refresh every 30 minutes
    read-timeout-seconds: 10
    # Filter mode: ALL (all models), FREE (free models), PAID (paid models)
    filter: FREE
    # Auto-selection configuration
    auto:
      modelId: 812bc18a-5b61-42d0-a550-f9ed8c2164c5  # Fixed model ID (optional)
      prefer: FREE  # Prefer free models
  
  # Audio Processing Configuration
  audio:
    max-size-bytes: 20971520  # 20MB
    allowed-formats:
      - wav
      - mp3
    default-format: wav
  
  # Deep Reasoning Configuration
  reasoning:
    enabled: true
    max-tokens: 2000
  
  # Upload Limit Configuration
  upload:
    max-attachment-count: 8  # Max 8 attachments

server:
  port: 9898
  servlet:
    context-path: /api
```

### Production Environment Configuration (application-prod.yml)

The production environment configuration is similar to the development one, but pay attention to the following:

1. **Database Configuration**: Use production database URL and a strong password.
2. **Redis Configuration**: Use production Redis URL and password.
3. **Log Level**: Adjust to INFO or WARN.
4. **Connection Pool**: Adjust based on actual load.
5. **API Keys**: Use production environment API keys.

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

# It is recommended to disable debug logs in production
logging:
  level:
    root: INFO
    com.cn: INFO
```

### Admin Account Initialization

The system automatically creates an admin account on the first startup, so there is no need to create it manually.

#### Configuration Parameters

| Parameter | Description | Default Value |
|---|---|---|
| `admin.init.enabled` | Whether to enable admin initialization | `true` |
| `admin.init.email` | Admin email (login account) | `admin@example.com` |
| `admin.init.password` | Admin password | `admin123` |
| `admin.init.nickname` | Admin nickname | `System Administrator` |
| `admin.init.initial-credits` | Initial credits | `1000000` |

#### How It Works

1. **Startup Check**: `AdminAccountInitializer` runs automatically on application startup.
2. **Account Check**: Checks if an admin account already exists based on the email.
3. **Auto-Creation**: If it doesn't exist, it creates the admin account and initializes credits.
4. **Idempotency**: It will not create duplicate accounts on repeated startups, ensuring safety.

#### Startup Log Example

```
Admin account created successfully!
Email: admin@example.com
Nickname: System Administrator
Role: ADMIN
Initial Credits: 1000000
Admin account initialization complete!
```

#### Security Recommendation

⚠️ **Be sure to change the default password before deploying to a production environment!**

```yaml
admin:
  init:
    enabled: true
    email: your_admin@company.com  # Change to your company email
    password: StrongP@ssw0rd!2024  # Use a strong password
    nickname: Administrator
    initial-credits: 10000000
```

#### Disabling Auto-Initialization

If you don't need to automatically create an admin account, you can disable this feature:

```yaml
admin:
  init:
    enabled: false  # Disable admin initialization
```

---

### Configuration Item Descriptions

#### 1. Database Connection Pool

| Configuration | Description | Recommended Value |
|---|---|---|
| initial-size | Initial number of connections | 5-10 |
| min-idle | Minimum idle connections | 5-10 |
| max-active | Maximum active connections | 20-50 |
| max-wait | Maximum wait time (ms) | 60000 |

#### 2. Redis Connection Pool

| Configuration | Description | Recommended Value |
|---|---|---|
| max-active | Maximum number of connections | 200 |
| max-idle | Maximum idle connections | 10 |
| min-idle | Minimum idle connections | 0 |
| timeout | Connection timeout | 10s |

#### 3. OpenRouter Model Filtering

- `ALL`: Get all available models.
- `FREE`: Get only free models (recommended for development).
- `PAID`: Get only paid models (recommended for production).

#### 4. ComfyUI Task Configuration

- `submit-task-max`: Maximum number of tasks in the queue (prevents memory overflow).
- `max-retry-time`: Single connection timeout.
- `max-retries`: Number of retries on failure.
- `timeout-check-interval`: Timeout check interval.

### Environment Variable Configuration (Recommended)

For security, it is recommended to use environment variables to store sensitive information:

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

Then reference them in the configuration file:

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

## 📊 Database Design

> 📁 **Complete Database Schema**: Please refer to the `.sql/table.sql` file in the project root for the complete table creation statements and index definitions.

### Core Table Structures

#### user - User Table
- `id` - User ID
- `email` - Email
- `password` - Password (encrypted)
- `nickname` - Nickname
- `avatar` - Avatar
- `role` - Role (USER/ADMIN)
- `created_time` - Creation time

#### user_credits - User Credits Table
- `user_id` - User ID
- `available_credits` - Available credits
- `frozen_credits` - Frozen credits
- `total_earned` - Total earned credits
- `total_consumed` - Total consumed credits

#### credit_transaction - Credit Transaction Table
- `id` - Transaction ID
- `user_id` - User ID
- `amount` - Transaction amount
- `type` - Transaction type (EARN/CONSUME/FREEZE/UNFREEZE)
- `description` - Description
- `created_time` - Creation time

#### workflow - Workflow Table
- `id` - Workflow ID
- `name` - Name
- `category_id` - Category ID
- `json` - Workflow JSON
- `credits_required` - Required credits
- `created_time` - Creation time

#### workflow_form - Workflow Form Table
- `id` - Form ID
- `workflow_id` - Workflow ID
- `node_key` - Node key
- `label` - Label
- `type` - Type (TEXT/IMAGE/NUMBER, etc.)
- `required` - Whether it's required
- `default_value` - Default value

#### workflow_output - Workflow Output Table
- `workflow_id` - Workflow ID
- `node_key` - Output node key
- `type` - Output type (IMAGE/VIDEO/AUDIO/MODEL)

#### workflow_result - Workflow Result Table
- `id` - Result ID
- `task_id` - Task ID
- `user_id` - User ID
- `workflow_name` - Workflow name
- `url` - Result URL
- `type` - Result type
- `created_time` - Creation time

#### redemption_code - Redemption Code Table
- `id` - Code ID
- `code` - Redemption code
- `credits` - Credit amount
- `status` - Status (UNUSED/USED)
- `used_by` - User ID of the user who used it
- `used_time` - Time of use
- `created_time` - Creation time

---

## 🎯 Best Practices

### 1. Credit Management

```java
// Freeze credits
creditUtils.freezeCredits(userId, amount, "Task submission");

// Consume credits
creditUtils.consumeCredits(userId, amount, "Task completion");

// Unfreeze credits
creditUtils.unfreezeCredits(userId, amount, "Task cancellation");
```

### 2. Distributed Locks

```java
RLock lock = redissonClient.getLock("LOCK_KEY:" + userId);
try {
    if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
        // Business logic
    }
} finally {
    lock.unlock();
}
```

### 3. Rate Limiting

```java
@RateLimit(
    permitsPerSecond = 1.0,
    limitType = RateLimit.LimitType.USER,
    message = "Operation is too frequent"
)
public Result someApi() {
    // API logic
}
```

### 4. Asynchronous Tasks

```java
@Async
public void asyncTask() {
    // Asynchronous processing logic
}
```

---

## 🔐 Security Recommendations

1. **Production Environment Configuration**:
   - Change default passwords.
   - Use a strong password policy.
   - Enable HTTPS.
   - Configure firewall rules.

2. **Sensitive Information Protection**:
   - Use environment variables for sensitive configurations.
   - Do not commit keys to the code repository.
   - Rotate API keys regularly.

3. **API Security**:
   - Enable API rate limiting.
   - Implement request signature verification.
   - Add IP whitelisting.

4. **Data Security**:
   - Back up the database regularly.
   - Encrypt sensitive fields.
   - Implement audit logs.

---

## 🤝 Contribution Guide

Contributions are welcome! Please follow these steps:

1. Fork this repository.
2. Create a feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

### Code Style

- Follow the Alibaba Java Development Manual.
- Use Lombok to simplify code.
- Add necessary comments and documentation.
- Write unit tests.

---

## 🚢 Deployment Guide

> ⚠️ **Important**: Before deploying to a production environment, be sure to change sensitive information such as admin account passwords, database passwords, Redis passwords, and API keys in the configuration files!

### Method 1: Traditional Deployment (Recommended for Production)

#### 1. Server Environment Preparation

```bash
# Install JDK 21
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz
tar -xzf jdk-21_linux-x64_bin.tar.gz
sudo mv jdk-21 /usr/local/

# Configure environment variables
echo 'export JAVA_HOME=/usr/local/jdk-21' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Verify installation
java -version
```

#### 2. Install MySQL 8.0

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server

# Start MySQL
sudo systemctl start mysql
sudo systemctl enable mysql

# Secure installation
sudo mysql_secure_installation
```

#### 3. Install Redis

```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# Start Redis
sudo systemctl start redis
sudo systemctl enable redis

# Configure Redis password (optional)
sudo vim /etc/redis/redis.conf
# Find # requirepass foobared
# Uncomment and change to: requirepass your_password
sudo systemctl restart redis
```

#### 4. Create Database

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

#### 5. Import Database Schema

```bash
# Import the provided schema file
cd /opt/conni-x-pro
mysql -u conni_user -p ghosts < .sql/table.sql
```

#### 6. Compile the Project

```bash
# Clone the project
git clone https://github.com/dulaiduwang003/Conni-X-Pro.git
cd Conni-X-Pro/singleton

# Modify the configuration file
vim application/src/main/resources/application-prod.yml

# Compile and package
mvn clean package -DskipTests -P prod

# The packaged file is located at
# application/target/application-1.0-SNAPSHOT.jar
```

#### 7. Deploy and Run

```bash
# Create deployment directory
sudo mkdir -p /opt/conni-x-pro
sudo cp application/target/application-1.0-SNAPSHOT.jar /opt/conni-x-pro/

# Create start script
sudo vim /opt/conni-x-pro/start.sh
```

Start script content:

```bash
#!/bin/bash
APP_NAME=application-1.0-SNAPSHOT.jar
APP_PATH=/opt/conni-x-pro
LOG_PATH=/opt/conni-x-pro/logs

# Create log directory
mkdir -p $LOG_PATH

# JVM options
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"

# Start the application
nohup java $JAVA_OPTS -jar $APP_PATH/$APP_NAME \
  > $LOG_PATH/app.log 2>&1 &

echo $! > $APP_PATH/app.pid
echo "Application started with PID: $(cat $APP_PATH/app.pid)"
```

Stop script (`stop.sh`):

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
# Grant execution permissions
sudo chmod +x /opt/conni-x-pro/start.sh
sudo chmod +x /opt/conni-x-pro/stop.sh

# Start the application
sudo /opt/conni-x-pro/start.sh

# View logs
tail -f /opt/conni-x-pro/logs/app.log
```

#### 8. Configure Systemd Service (Recommended)

```bash
sudo vim /etc/systemd/system/conni-x-pro.service
```

Service configuration content:

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
# Reload systemd configuration
sudo systemctl daemon-reload

# Start the service
sudo systemctl start conni-x-pro

# Enable on startup
sudo systemctl enable conni-x-pro

# Check service status
sudo systemctl status conni-x-pro

# View logs
sudo journalctl -u conni-x-pro -f
```

#### 9. Configure Nginx Reverse Proxy

```bash
sudo apt install nginx
sudo vim /etc/nginx/sites-available/conni-x-pro
```

Nginx configuration:

```nginx
upstream conni_backend {
    server 127.0.0.1:9898;
}

server {
    listen 80;
    server_name your-domain.com;

    # Log configuration
    access_log /var/log/nginx/conni-access.log;
    error_log /var/log/nginx/conni-error.log;

    # Client upload size limit
    client_max_body_size 100M;

    location /api/ {
        proxy_pass http://conni_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Timeout configuration
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # SSE streaming response configuration
    location /api/llm/chat/stream {
        proxy_pass http://conni_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # SSE specific configuration
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
# Enable the site
sudo ln -s /etc/nginx/sites-available/conni-x-pro /etc/nginx/sites-enabled/

# Test configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

#### 10. Configure HTTPS (Recommended)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Get SSL certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal
sudo certbot renew --dry-run
```

---

### Method 2: Docker Deployment

#### 1. Create a Dockerfile

Create a `Dockerfile` in the project root:

```dockerfile
FROM openjdk:21-jdk-slim

LABEL maintainer="dulaiduwang003 <2074055628@qq.com>"

# Set working directory
WORKDIR /app

# Copy jar file
COPY application/target/application-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 9898

# JVM options
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

# Start command
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar /app/app.jar"]
```

#### 2. Create docker-compose.yml

```yaml
version: '3.8'

services:
  # MySQL Database
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

  # Redis Cache
  redis:
    image: redis:7-alpine
    container_name: conni-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes --requirepass redis_password

  # Application Service
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

> 💡 **Note**: The docker-compose.yml is configured to automatically import the `.sql/table.sql` file into the MySQL container, which will create all tables on startup.

#### 3. Build and Run

```bash
# Compile the project
mvn clean package -DskipTests -P prod

# Build Docker images
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

---

### Method 3: Kubernetes Deployment

#### 1. Create a Deployment

`k8s/deployment.yaml`:

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

#### 2. Create a Service

`k8s/service.yaml`:

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

#### 3. Deploy to K8s

```bash
# Apply configurations
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# Check status
kubectl get pods
kubectl get services

# View logs
kubectl logs -f deployment/conni-x-pro
```

---

### Deployment Checklist

#### Pre-Launch Checklist

- [ ] MySQL database created and schema imported.
- [ ] Redis service is running.
- [ ] Passwords and keys in configuration files have been changed.
- [ ] Admin account configuration (email, password) has been changed.
- [ ] Aliyun OSS configuration is correct.
- [ ] OpenRouter API Key is valid.
- [ ] ComfyUI server is accessible (if needed).
- [ ] Mail service configuration is correct.

#### Post-Launch Checklist

```bash
# 1. Check if the application has started
curl http://localhost:9898/api/actuator/health

# 2. Check database connection
mysql -h localhost -u conni_user -p -e "USE ghosts; SHOW TABLES;"

# 3. Check Redis connection
redis-cli -a your_password ping

# 4. View application logs (confirm admin account creation)
tail -f /opt/conni-x-pro/logs/app.log | grep "Admin"

# 5. Verify admin account creation
mysql -h localhost -u conni_user -p -e "USE ghosts; SELECT email, nickname, role FROM user WHERE role='ADMIN';"

# 6. Test API endpoint
curl http://localhost:9898/api/notice/get

# 7. Test admin login
curl -X POST http://localhost:9898/api/auth/password-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

#### Performance Optimization Recommendations

1. **JVM Parameter Tuning**:
```bash
-Xms2g -Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/opt/conni-x-pro/logs/
```

2. **Database Connection Pool**:
```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
```

3. **Redis Connection Pool**:
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

#### Monitoring and Logging

1. **Application Logs**:
```bash
# View logs in real-time
tail -f /opt/conni-x-pro/logs/app.log

# View error logs
grep ERROR /opt/conni-x-pro/logs/app.log

# Log rotation configuration
vim /etc/logrotate.d/conni-x-pro
```

2. **System Monitoring**:
```bash
# CPU and memory usage
top
htop

# Disk usage
df -h

# Network connections
netstat -tunlp | grep 9898
```

---

## 📝 Development Plan

- [ ] Support more AI model providers.
- [ ] Add WebSocket message push.
- [ ] Implement task priority queue.
- [ ] Support workflow version management.
- [ ] Add user behavior analytics.
- [ ] Implement multi-tenancy support.
- [ ] Add GraphQL API.
- [x] Support Docker deployment.

---

## 📄 License

This project is licensed under an open-source license. See the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**时间海 (dulaiduwang003)**

- GitHub: [@dulaiduwang003](https://github.com/dulaiduwang003)
- Email: 2074055628@qq.com
- Organization: bdth, Inc.

---

## 📞 Contact

For questions or suggestions, please contact us via:

- Submit an [Issue](https://github.com/dulaiduwang003/Conni-X-Pro/issues)
- Email: 2074055628@qq.com

---

<div align="center">

**⭐ If this project helps you, please give it a Star! ⭐**

Made with ❤️ by 时间海

</div>


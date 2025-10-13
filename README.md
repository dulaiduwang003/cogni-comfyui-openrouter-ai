## Conni-X-Pro

<p align="center">
  <a href="./README_en-US.md">English</a> | <a href="./README.md">简体中文</a> | <a href="./README_ja-JP.md">日本語</a>
</p>

<p align="center">
  <img alt="Conni-X-Pro" src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white">
  <img alt="Java" src="https://img.shields.io/badge/Java-21+-red?logo=openjdk&logoColor=white">
  <img alt="Vue" src="https://img.shields.io/badge/Vue-3-42b883?logo=vue.js&logoColor=white">
  <img alt="Vite" src="https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/license-TBD-lightgrey">
</p>

<p align="center">
  <a href="#快速开始">快速开始</a> ·
  <a href="#功能特性">功能特性</a> ·
  <a href="#技术栈">技术栈</a> ·
  <a href="#模块架构">模块架构</a> ·
  <a href="#接口与约定">接口与约定</a> ·
  <a href="#贡献">贡献</a>
</p>

面向 AI 创作与工作流的全栈开源项目：前端基于 Vue 3 + Vite + TypeScript + Element Plus，后端采用 Spring Boot 3 多模块架构，集成登录鉴权、聊天（LLM/OpenRouter 流式）、ComfyUI 工作流编排、对象存储（阿里云 OSS）、通知邮件、系统管理等能力。

### 功能预览
<p align="center">

  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/082ef78b-8925-40cb-8652-88a6f974c029.png" alt="功能预览1" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/517989e3-c046-4192-9d5e-a5804f5202d9.png" alt="功能预览2" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/1cedf4ed-0602-4e9f-a2a0-6321f1df5114.png" alt="功能预览3" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/01d9a880-d453-40fd-95a8-f210b8d0ed31.png" alt="功能预览4" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/f7008912-3c91-4e29-badf-b7a38d031bba.png" alt="功能预览5" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/77833d26-22ff-49d1-b85b-c815c8232f9b.png" alt="功能预览6" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/c8d6e372-e28b-4839-8be1-bff83c172d7e.png" alt="功能预览7" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/e669f851-b760-4a7d-8cf5-07a657182d1a.png" alt="功能预览8" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/70218a85-4b32-424f-ba83-74a3a7e7239c.png" alt="功能预览9" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/3b5950c1-dfe3-4090-aa38-118d18d0ec1f.png" alt="功能预览10" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/604843f6-ef54-48ca-a6c8-26ced1931298.png" alt="功能预览11" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/4cad610a-3276-4888-b520-e3b66ccdb137.png" alt="功能预览12" width="150" />
</p>

### 功能特性
- **账号与鉴权**: 基于 Sa-Token，Bearer Token 方式，内置接口访问过滤与路由过滤。
- **聊天与AI能力**: 整合 OpenRouter，多模型注册与自动选择；SSE 流式对话，支持多语言与富文本高亮；(主要是这玩意有免费的API模型服务具体详见yml配置和具体后端文档)。
- **ComfyUI 工作流**: 表单化参数收集、任务提交与状态订阅、任务超时与重试策略、文件类型白名单、支持配置多个Comfyui服务、(提交任务、取消任务、重新制作)。
- **对象存储**: 阿里云 OSS 上传与回显、全局文件类型与大小限制配置。
- **消息通知**: 邮件验证码/通知（Thymeleaf 模板），系统公告与站内通知。
- **系统管理**: 用户/积分、公告、兑换码、统计概览等常见后台功能。


### 部署

#### 后端部署

后端是标准的 Spring Boot 多模块项目，可以打包成一个可执行的 JAR 文件进行部署。我们提供了非常详尽的部署文档，覆盖了传统服务器、Docker 容器以及 Kubernetes (K8s) 等多种部署方案。

👉 **[点击查看详细的后端部署指南](./singleton/README.md)**

#### 前端部署

前端项目基于 Vue 3 和 Vite 构建，部署时需编译成静态文件，并使用 Nginx 等 Web 服务器进行托管和反向代理。

👉 **[点击查看详细的前端部署指南](./vue/README.md)**

### 技术栈
- **前端**: Vue 3, Vite, TypeScript, Pinia, Vue Router, Element Plus, vue-i18n, highlight.js, lottie-web, @google/model-viewer。
- **后端**: Spring Boot 3, Web + WebFlux（SSE）, Sa-Token, MyBatis-Plus, Druid, MySQL, Redis（Redisson）, Spring Mail, WebSocket, Lombok, Guava。

### 模块架构
```
singleton/               # 后端多模块聚合工程（Maven）
  application/           # 可执行启动模块（聚合各业务模块）
  common/                # 公共依赖：配置、拦截、工具、存储、鉴权等
  auth/                  # 认证与用户相关接口
  comfyui/               # ComfyUI 任务编排与 WebSocket 推送
  llm/                   # LLM 对话与模型注册（OpenRouter）
  notice/                # 通知与公告
  oss/                   # 阿里云 OSS 能力封装
  system/                # 系统管理域
vue/                     # 前端工程（Vite）
```

### 快速开始
#### 前置条件
- JDK 21+（建议 22）
- Maven 3.9+
- Node.js 18+ / pnpm 或 npm
- MySQL 8+、Redis 6+

#### 配置后端
1. 复制或参考 `singleton/application/src/main/resources/application-dev.yml`，按需修改以下关键项：
   - `spring.datasource.url/username/password`（MySQL）
   - `spring.data.redis.host/port/password`（Redis）
   - `spring.mail.*`（可选，邮件验证码/通知）
   - `ali.oss.*`（阿里云 OSS）
   - `comfyui.server[*].url`（ComfyUI 服务地址）
   - `open-router.api-key`（OpenRouter API Key）
2. 启动（开发模式）：
   ```bash
   mvn -f singleton/pom.xml -pl application -am spring-boot:run -Pdev
   ```
   或打包：
   ```bash
   mvn -f singleton/pom.xml -Pdev clean package
   java -jar singleton/application/target/application-*.jar
   ```
3. 默认后端地址：`http://localhost:9898/api`（`server.servlet.context-path=/api`）。

#### 开发环境配置详解（application-dev.yml）

示例（请用你自己的值替换占位符）：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/ghosts?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true # MySQL 连接串
    username: root # 数据库用户
    password: "<your_mysql_password>" # 数据库密码
  data:
    redis:
      database: 1 # 逻辑库(0-15)
      host: 127.0.0.1 # Redis 主机
      port: 6379 # Redis 端口
      password: "" # 若无密码留空
  mail:
    host: smtp.qq.com # SMTP 服务器
    username: "<your_email>@qq.com" # 发信邮箱
    password: "<your_email_auth_code>" # 授权码/应用专用密码
    default-encoding: UTF-8 # 编码
    properties:
      mail:
        smtp:
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory # 启用 SSL
    port: 587 # SMTP 端口

admin:
  init:
    enabled: true # 启用首次自动创建管理员
    email: admin@example.com # 管理员邮箱
    password: admin123 # 管理员密码
    nickname: 系统管理员 # 管理员昵称
    initial-credits: 1000000 # 初始积分

ali:
  certified:
    access-key: "<your_ali_access_key>" # 阿里云 AK
    secret-key: "<your_ali_secret_key>" # 阿里云 SK
  oss:
    endpoint: "oss-accelerate.aliyuncs.com" # OSS 访问端点
    bucket-name: "<your_bucket_name>" # OSS 桶名
    domain: "https://<your_bucket_name>.oss-accelerate.aliyuncs.com" # oss域名
    supported-file-types:
      - extension: "jpg" # 扩展名
        mimeType: "image/jpeg" # MIME
        maxSizeInBytes: 5242880 # 单文件上限

comfyui:
  server:
    - name: COMFYUI-1号 # 标识
      url: http://localhost:8188 # ComfyUI 地址
  submit-task-max: 100 # 最大提交队列
  task:
    max-retry-time: 30 # 单次连接超时(秒)
    max-retries: 2 # 失败重试次数
    timeout-check-interval: 30 # 超时巡检间隔(分钟)
  supported-file-types:
    jpg: "image/jpeg" # 表单允许的上传类型
    png: "image/png"

open-router:
  api-key: "<your_openrouter_api_key>" # OpenRouter API Key
  base-url: "https://openrouter.ai/api/v1" # API 地址
  connect-timeout: 30000 # 连接超时(ms)
  read-timeout: 60000 # 读取超时(ms)
  chat:
    session-ttl-seconds: 2592000 # 会话 TTL(秒)
  truncation:
    response-token-reserve: 2000 # 为回复预留 Token
    text-chars-per-token: 4 # 文本字符/Token
    image-token-estimate: 1500 # 图片估算 Token
    file-token-estimate: 4000 # 文件估算 Token
    enable-compression: true # 文本压缩
  plugins:
    web:
      enabled: true # 联网搜索
      max-results: 5 # 结果上限
    file-parser:
      enabled: true # 文件解析
      pdf:
        engine: pdf-text # 识别引擎
        static-filename: document.pdf # 默认文件名
  remote-registry:
    enabled: true # 启用模型拉取
    url: "https://openrouter.ai/api/frontend/models" # 模型列表地址
    cron: "0 0/30 * * * ?" # 刷新间隔
    read-timeout-seconds: 10 # 读取超时(秒)
    filter: FREE # 模型范围 ALL/FREE/PAID
    auto:
      modelId: "<optional_model_id>" # 固定模型(可选)
      prefer: FREE # FREE/PAID
  audio:
    max-size-bytes: 20971520 # 最大音频大小
    allowed-formats:
      - wav
      - mp3
    default-format: wav # 默认格式
  reasoning:
    enabled: true # 开启深度思考
    max-tokens: 2000 # 思考 Token 上限
  upload:
    max-attachment-count: 8 # 单消息附件数

server:
  port: 9000 # 开发端口(可改 9898 以匹配前端)
  servlet:
    context-path: /api # API 前缀
```

#### 启动前端
```bash
cd vue
npm i
npm run dev
```
访问：`http://localhost:5173`

### 接口与约定
- 基础前缀：`/api`
- 认证头：`Authorization: Bearer <token>`（Sa-Token）
- 返回结构与状态码：遵循后端统一约定（见 `common` 模块与各 `controller`）。

### 安全与合规
- 请勿在仓库中提交敏感凭据（数据库、邮箱、OSS、OpenRouter Key 等）。
- 建议使用环境变量或私有配置文件覆盖样例配置。
- 公共演示场景请限制文件上传类型与大小（本项目已提供全局白名单与大小限制）。

### 路线图（建议）
- 跨模型会话共享与消息回放优化
- ComfyUI 节点模板管理与表单自动生成
- 更细粒度的权限与审计日志
- 完整的 E2E/单元测试与 CI/CD 工作流

### 贡献
欢迎提交 Issue 与 PR：
- 规范提交信息，尽量附上复现步骤或截图
- 对于新增模块，请补充相应文档与示例配置

### 支持作者
如果本项目对你有帮助，欢迎赞赏支持（微信）：

<p align="center">
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/0942b17f-9eec-4291-adcb-612507b9dce6.jpg" alt="微信赞赏码" width="280" />
</p>

### 许可证
当前仓库暂未显式声明许可证，建议在正式对外开源前补充 `LICENSE` 文件（常见选择：MIT/Apache-2.0）。



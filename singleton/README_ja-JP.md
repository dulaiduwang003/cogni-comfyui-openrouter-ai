# Conni-X-Pro シングルトン

[English](./README_en-US.md) | [简体中文](./README.md) | [日本語](./README_ja-JP.md)

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen?style=flat-square)
![License](https://img.shields.io/badge/License-Open%20Source-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)

**AIチャット、ComfyUIワークフロー、ユーザー管理を統合したプラットフォーム**

[特徴](#-特徴) • [技術アーキテクチャ](#-技術アーキテクチャ) • [クイックスタート](#-クイックスタート) • [使用ガイド](#-使用ガイド) • [モジュール説明](#-モジュール説明) • [APIドキュメント](#-apiドキュメント) • [デプロイガイド](#-デプロイガイド)

</div>

---

## 📖 プロジェクト概要

Conni-X-Pro シングルトンは、Spring Boot 3.2.2 と Java 21 を基盤に構築されたプラットフォームであり、モジュラーアーキテクチャを特徴とし、包括的なAIチャット、ComfyUIワークフロー管理、ユーザー認証、およびファイルストレージ機能を提供します。

### 🎯 主なハイライト

- 🤖 **AIチャット統合** - 複数モデル（OpenRouter）をサポート、ストリーミング応答、セッション管理、およびPDF/音声解析。
- 🎨 **ComfyUIワークフロー** - 完全なワークフロー管理、タスクキューイング、進捗追跡、およびリアルタイムのWebSocketプッシュ通知。
- 🔐 **包括的な認証** - Sa-Tokenによる権限管理、メール認証コードログイン、パスワードログイン、およびロールベースのアクセス制御。
- 💾 **分散アーキテクチャ** - Redisキャッシング、Redisson分散ロック、および非同期タスク処理。
- 📊 **システム監視** - リアルタイムのリソース監視、タスク統計、ユーザー統計、およびAIコールトラッキング。
- 🎁 **クレジットシステム** - 交換コード管理、クレジットの凍結/使用、および取引記録。

---

## ✨ 特徴

### 1. AIチャットモジュール (LLM)

- ✅ **マルチモデルサポート**: OpenRouterと統合し、YML設定を介して無料モデルをサポート。
- ✅ **ストリーミング応答**: SSEに基づくリアルタイムストリーミングチャット。
- ✅ **マルチモーダル入力**: テキスト、画像、PDF、および音声ファイルをサポート。
- ✅ **セッション管理**: セッションのメッセージ履歴の作成、削除、およびクエリ。
- ✅ **モデルレジストリ**: 動的モデルリスト、自動更新、フィルタリング、およびページネーション。
- ✅ **インテリジェント強化**: ウェブ検索、画像生成、および推論モードをサポート。

### 2. ComfyUIワークフローモジュール

- ✅ **ワークフロー管理**: JSON解析、ノード認識、フォーム設定、およびカテゴリ管理。
- ✅ **タスクシステム**: タスクの送信、進捗追跡、キャンセル/再実行、および結果の保存。
- ✅ **リアルタイムプッシュ**: リアルタイムのタスクステータス更新のためのWebSocket接続。
- ✅ **リソースアップロード**: 入力ファイルをComfyUIサーバーに自動的にアップロード。
- ✅ **マルチサーバーサポート**: 負荷分散とサーバーのヘルスチェック。
- ✅ **クレジット控除**: タスク送信前にクレジットを凍結し、完了時に控除。

### 3. ユーザー認証モジュール (Auth)

- ✅ **複数のログイン方法**: パスワードログインとメール認証コードログイン。
- ✅ **ユーザー登録**: 認証コード付きのメール登録。
- ✅ **パスワード管理**: パスワード忘れとパスワードリセット機能。
- ✅ **ユーザー情報**: アバターのアップロード、ニックネームの変更、およびクレジット残高の照会。
- ✅ **交換コードシステム**: クレジットチャージ用の交換コードの生成と使用。
- ✅ **取引履歴**: ページネーション付きのクレジット取引履歴。

### 4. システム管理モジュール (System)

- ✅ **ユーザー管理**: ユーザーリスト、作成、編集、削除、ロール管理、およびパスワードリセット。
- ✅ **ワークフロー管理**: ワークフローの公開/非公開、カテゴリの管理、および設定の編集。
- ✅ **交換コード管理**: 一括生成、クレジット額の変更、および削除。
- ✅ **システム概要**: ユーザー統計、AI統計、タスク統計、およびリソース監視。
- ✅ **お知らせ管理**: システムお知らせの設定とクリア。

### 5. ファイルストレージモジュール (OSS)

- ✅ **Aliyun OSS**: ファイルのアップロードとURLの生成。
- ✅ **ファイルタイプ検証**: 画像、動画、音声、PDFなどをサポート。
- ✅ **ファイルサイズ制限**: 設定可能なファイルサイズ制限。
- ✅ **自動分類**: ファイルをタイプに基づいてカテゴリに自動的に保存。

### 6. 通知モジュール (Notice)

- ✅ **システムお知らせ**: キャッシュ最適化付きのお知らせクエリ。
- ✅ **メール通知**: 認証コードメールの送信。

---

## 🏗️ 技術アーキテクチャ

### コア技術スタック

| 技術 | バージョン | 説明 |
|---|---|---|
| Java | 21 | プログラミング言語 |
| Spring Boot | 3.2.2 | アプリケーションフレームワーク |
| MyBatis Plus | 3.5.5 | ORMフレームワーク |
| MySQL | 8.0.33 | リレーショナルデータベース |
| Redis | - | キャッシュデータベース |
| Redisson | 3.20.1 | 分散ロック |
| Sa-Token | 1.38.0 | 認証・認可 |
| Aliyun OSS | 3.17.0 | オブジェクトストレージ |
| Fastjson | 2.0.42 | JSON処理 |
| Lombok | 1.18.30 | コードの簡素化 |
| Guava | 33.4.8 | ユーティリティライブラリ |

### モジュールアーキテクチャ

```
singleton
├── application      # アプリケーション起動モジュール (メインエントリポイント)
├── common          # 共通モジュール (エンティティ、ユーティリティ、設定)
├── auth            # 認証モジュール (ログイン、登録、ユーザー管理)
├── llm             # AIチャットモジュール (OpenRouter統合)
├── comfyui         # ComfyUIワークフローモジュール
├── system          # システム管理モジュール (バックエンド管理)
├── oss             # ファイルストレージモジュール (Aliyun OSS)
└── notice          # 通知モジュール (お知らせ)
```

### 設計上の特徴

- 🔹 **モジュラーデザイン**: 各モジュールは独立して開発およびデプロイ可能。
- 🔹 **階層化アーキテクチャ**: Controller → Service → Mapper層による関心事の明確な分離。
- 🔹 **統一された例外処理**: グローバルな例外インターセプトと標準化された応答形式。
- 🔹 **レート制限**: Guava RateLimiterに基づくAPIレート制限。
- 🔹 **非同期処理**: 非同期タスクのための@Asyncとスケジュールされたタスクのための@Scheduled。
- 🔹 **分散ロック**: 並行性の安全性を確保するためのRedisson。
- 🔹 **WebSocketプッシュ**: リアルタイムのタスク進捗通知。

---

## 🚀 クイックスタート

### 前提条件

- JDK 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- ComfyUIサーバー (オプション)

### インストール手順

#### 1. プロジェクトのクローン

```bash
git clone https://github.com/dulaiduwang003/Conni-X-Pro.git
cd Conni-X-Pro/singleton
```

#### 2. データベース設定

データベースを作成します:

```sql
CREATE DATABASE ghosts CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

データベーススキーマをインポートします:

```bash
# 方法1: 提供されたSQLファイルを使用する
mysql -u root -p ghosts < .sql/table.sql

# 方法2: MySQLクライアントで実行する
mysql -u root -p
USE ghosts;
SOURCE /path/to/project/.sql/table.sql;
```

> 💡 **ヒント**: プロジェクトには完全なデータベーススキーマファイル`.sql/table.sql`が含まれており、以下のテーブルが含まれています:
> - `user` - ユーザーテーブル
> - `user_credits` - ユーザークレジットテーブル
> - `credit_transactions` - クレジット取引テーブル
> - `redemption_codes` - 交換コードテーブル
> - `workflow` - ワークフローテーブル
> - `workflow_category` - ワークフローカテゴリテーブル
> - `workflow_form` - ワークフローフォームテーブル
> - `workflow_output` - ワークフロー出力ノードテーブル
> - `workflow_result` - ワークフロー結果テーブル

#### 3. 設定ファイル

`application/src/main/resources/application-dev.yml`を修正します:

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

# Aliyun OSS 設定
ali:
  certified:
    access-key: your_access_key
    secret-key: your_secret_key
  oss:
    endpoint: oss-accelerate.aliyuncs.com
    bucket-name: your_bucket
    domain: https://your_bucket.oss-accelerate.aliyuncs.com

# OpenRouter 設定
openrouter:
  api-key: your_openrouter_api_key
  api-url: https://openrouter.ai/api/v1
  registry-url: https://openrouter.ai/api/v1/models

# ComfyUI サーバー設定
comfyui:
  servers:
    - url: http://localhost:8188
      max-concurrent-tasks: 3
```

#### 4. コンパイルと実行

```bash
# プロジェクトをコンパイル
mvn clean package -DskipTests

# プロジェクトを実行
java -jar application/target/application-1.0-SNAPSHOT.jar

# またはMavenで直接実行
mvn spring-boot:run -pl application
```

#### 5. アプリケーションへのアクセス

- アプリケーションURL: `http://localhost:9898/api`
- ヘルスチェック: `http://localhost:9898/api/actuator/health`
- デフォルト管理者アカウント: `admin@example.com` / `admin123` (初回起動時に自動的に作成されます)

---

## 📖 使用ガイド

### 初回ログイン

プロジェクトを開始した後、デフォルトの管理者アカウントでログインします:

**ログインエンドポイント**: `POST /api/auth/password-login`

**リクエスト例**:

```bash
curl -X POST http://localhost:9898/api/auth/password-login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

**レスポンス例**:

```json
{
  "code": 200,
  "message": "success",
  "data": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..."
}
```

`data`フィールドにはトークンが含まれており、後続のリクエストのヘッダーに含める必要があります:

```
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...
```

### 一般的な操作

#### 1. ユーザー情報の表示

```bash
curl -X GET http://localhost:9898/api/user/info \
  -H "Authorization: Bearer {your_token}"
```

#### 2. システム概要の取得 (管理者が必要)

```bash
curl -X GET http://localhost:9898/api/system/overview \
  -H "Authorization: Bearer {your_token}"
```

#### 3. 通常ユーザーの作成 (管理者が必要)

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

#### 4. 交換コードの生成 (管理者が必要)

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

#### 5. AIチャットメッセージの送信

```bash
curl -X POST http://localhost:9898/api/llm/chat/submit \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "session-001",
    "content": "Hello, please introduce yourself"
  }'
```

#### 6. ストリーミングチャット (SSE)

```bash
curl -N http://localhost:9898/api/llm/chat/stream?sessionId=session-001&token={your_token}
```

#### 7. ComfyUIワークフロータスクの送信

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

#### 8. タスク進捗のクエリ

```bash
curl -X GET "http://localhost:9898/api/workflow/get-task-progress?taskId={task_id}" \
  -H "Authorization: Bearer {your_token}"
```

### WebSocket接続 (タスク進捗プッシュ)

**接続URL**: `ws://localhost:9898/api/ws`

**接続例** (JavaScript):

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

### ファイルアップロード

#### アバターのアップロード

```bash
curl -X POST http://localhost:9898/api/oss/upload \
  -H "Authorization: Bearer {your_token}" \
  -F "file=@/path/to/avatar.jpg" \
  -F "type=AVATAR"
```

#### ワークフロー入力ファイルのアップロード

```bash
curl -X POST http://localhost:9898/api/oss/upload \
  -H "Authorization: Bearer {your_token}" \
  -F "file=@/path/to/input.png" \
  -F "type=WORKFLOW_INPUT"
```

### FAQ

#### Q1: 管理者パスワードを忘れた場合のリセット方法は？

**方法1**: 設定ファイルを変更して再起動 (データベースに管理者アカウントが存在しない場合)

```yaml
admin:
  init:
    enabled: true
    email: admin@example.com
    password: new_password  # パスワードを変更
```

**方法2**: データベースを直接変更

```sql
-- パスワードはMD5で暗号化されています
UPDATE user SET password = MD5('new_password') WHERE email = 'admin@example.com';
```

#### Q2: 利用可能なAIモデルを表示する方法は？

```bash
curl -X POST http://localhost:9898/api/llm/get/available-model/page \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "page": 1,
    "size": 20
  }'
```

#### Q3: ユーザーを無効にする方法は？

現在、ユーザーを削除することでこれを実現できます:

```bash
curl -X POST http://localhost:9898/api/system/user/delete \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123
  }'
```

#### Q4: ユーザーにクレジットを追加する方法は？

交換コードシステムを使用します:

1. 管理者が交換コードを作成します。
2. ユーザーにコードを送信します。
3. ユーザーがコードをクレジットに交換します。

```bash
# ユーザーによる交換
curl -X POST http://localhost:9898/api/redemption-code/redeem \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CONNI-XXXX-XXXX-XXXX"
  }'
```

---

## 📦 モジュール説明

### アプリケーションモジュール

**責務**: アプリケーションのエントリポイント、グローバル設定、フィルター、およびインターセプター。

**コアクラス**:
- `Application.java` - 起動クラス
- `GlobalInterceptor.java` - グローバル例外インターセプター
- `AuthInterfaceFilter.java` - 認証フィルター
- `RoutePathFilter.java` - ルートフィルター

### 共通モジュール

**責務**: 共通のエンティティ、ユーティリティクラス、設定クラス、および定数定義。

**コアコンテンツ**:
- **エンティティ**: User, Workflow, WorkflowResult, RedemptionCodeなど。
- **ユーティリティ**: RedisUtils, CreditUtils, UploadUtil, JwtUtilなど。
- **設定**: RedisConfig, MybatisPlusConfig, WebClientConfiguration, AdminInitConfigurationなど。
- **イニシャライザ**: AdminAccountInitializer - 管理者アカウントの自動初期化。
- **列挙型**: RoleEnum, TaskStatusEnum, TransactionTypeなど。

### Authモジュール

**責務**: ユーザー認証、登録、ログイン、およびユーザー情報管理。

**コアエンドポイント**:
- `POST /auth/password-login` - パスワードログイン
- `POST /auth/email-code-login` - 認証コードログイン
- `POST /auth/register` - ユーザー登録
- `POST /auth/forgot-password` - パスワード忘れ
- `GET /user/info` - ユーザー情報の取得
- `POST /user/update-avatar` - アバターの更新
- `POST /redemption-code/redeem` - コードの交換

### LLMモジュール

**責務**: AIチャット、モデル管理、およびセッション管理。

**コアエンドポイント**:
- `POST /llm/chat/submit` - メッセージの送信
- `GET /llm/chat/stream` - ストリーミングチャット (SSE)
- `POST /llm/get/available-model/page` - モデルリストの取得
- `POST /llm/delete/session` - セッションの削除

**特徴**:
- テキスト、画像、PDF、および音声のマルチモーダル入力をサポート。
- Redisに基づくセッションストレージ。
- 動的モデルレジストリ。
- ストリーミング応答に最適化。

### ComfyUIモジュール

**責務**: ワークフロー管理、タスクスケジューリング、および進捗追跡。

**コアエンドポイント**:
- `POST /workflow/submit-task` - タスクの送信
- `GET /workflow/get-task-progress` - タスク進捗の取得
- `GET /workflow/get-task-progress-list` - タスクリストの取得
- `POST /workflow/cancel-task` - タスクのキャンセル
- `POST /workflow/remake-task` - タスクの再実行
- `GET /workflow/get-workflows-page` - ワークフローリストの取得
- `GET /workflow-result/page` - 結果リストの取得

**特徴**:
- ComfyUI JSONワークフローを自動的に解析。
- マルチサーバーの負荷分散をサポート。
- WebSocketによるリアルタイムの進捗プッシュ。
- タスクキュー管理。
- クレジットの凍結/消費メカニズム。

### システムモジュール

**責務**: バックエンド管理、ユーザー管理、ワークフロー管理、およびシステム監視。

**コアエンドポイント**:
- `GET /system/overview` - システム概要
- `POST /system/user/page` - ユーザーリスト
- `POST /system/user/create` - ユーザーの作成
- `POST /system/workflow/parsing` - ワークフローの解析
- `POST /system/workflow/save-config` - ワークフロー設定の保存
- `POST /system/redemption-code/create` - 交換コードの生成
- `POST /system/notice/set` - お知らせの設定

**特徴**:
- 完全なCRUD操作。
- リアルタイムのシステムリソース監視 (CPU、メモリ、ディスク)。
- 統計データの視覚化。
- アクセス制御 (ADMINロールが必要)。

### OSSモジュール

**責務**: ファイルのアップロードとストレージ管理。

**コアエンドポイント**:
- `POST /oss/upload` - ファイルアップロード

**特徴**:
- さまざまなファイルタイプをサポート。
- ファイルサイズの検証。
- CDNリンクの自動生成。

### Noticeモジュール

**責務**: システムのお知らせと通知管理。

**コアエンドポイント**:
- `GET /notice/get` - お知らせの取得

---

## 📡 APIドキュメント

### 統一された応答形式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### エラーコードの説明

| エラーコード | 説明 |
|---|---|
| 200 | 成功 |
| 400 | 不正なリクエスト |
| 401 | 未認証 |
| 403 | 禁止 |
| 429 | リクエストが多すぎます |
| 500 | 内部サーバーエラー |

### 認証

ほとんどのエンドポイントでは、リクエストヘッダーにトークンが必要です:

```
Authorization: Bearer {token}
```

あるいは、Sa-Tokenのデフォルトヘッダーを使用します:

```
satoken: {token}
```

### レート制限

一部のエンドポイントにはレート制限があります:

- ログイン: 0.2リクエスト/秒 (IPレベル)
- メッセージ送信: 1リクエスト/秒 (ユーザーレベル)
- ストリーミングチャット: 2リクエスト/秒 (ユーザーレベル)

---

## 🔧 完全な設定詳細

### メイン設定ファイル (application.yml)

```yaml
spring:
  profiles:
    active: @spring.profiles.active@  # アクティブな環境プロファイル (dev/prod)
  codec:
    max-in-memory-size: 10485760  # 10MBメモリバッファ
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

# Sa-Token 認証設定
sa-token:
  token-prefix: Bearer
  token-name: Authorization
  active-timeout: -1  # 有効期限なし
  is-concurrent: true  # 同時ログインを許可
  is-share: true  # トークンを共有
  is-log: false  # ログを無効化
  token-style: tik  # トークンスタイル

# MyBatis Plus 設定
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

### 開発環境設定 (application-dev.yml)

```yaml
spring:
  # Redis 設定
  data:
    redis:
      database: 1
      host: 127.0.0.1
      port: 6379
      password: ""
  
  # MySQL データベース設定
  datasource:
    username: root
    password: "your_password"
    url: jdbc:mysql://127.0.0.1:3306/ghosts?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true
  
  # メール設定 (認証コード送信のため)
  mail:
    host: smtp.qq.com
    username: 'your_email@qq.com'
    password: 'your_auth_code'  # QQ Mail 認証コード
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory
    port: 587

# 管理者アカウント初期化設定
admin:
  init:
    enabled: true  # 管理者初期化を有効にする
    email: admin@example.com  # 管理者メール
    password: admin123  # 管理者パスワード
    nickname: System Administrator  # 管理者ニックネーム
    initial-credits: 1000000  # 初期クレジット

# Aliyun 設定
ali:
  certified:
    access-key: "your_access_key"
    secret-key: "your_secret_key"
  oss:
    endpoint: "oss-accelerate.aliyuncs.com"
    bucket-name: "your_bucket"
    domain: "https://your_bucket.oss-accelerate.aliyuncs.com"
    # サポートされているファイルタイプの設定
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

# ComfyUI 設定
comfyui:
  # サーバーリスト (マルチサーバー負荷分散をサポート)
  server:
    - name: COMFYUI-1
      url: http://localhost:8188
  # 最大送信タスク数
  submit-task-max: 100
  # タスク設定
  task:
    # 接続タイムアウト (秒)
    max-retry-time: 30
    # 最大リトライ回数
    max-retries: 2
    # タイムアウトチェック間隔 (分)
    timeout-check-interval: 30
  # ComfyUIフォームでサポートされているファイルタイプ
  supported-file-types:
    jpg: "image/jpeg"
    jpeg: "image/jpeg"
    png: "image/png"
    webp: "image/webp"
    gif: "image/gif"
    mp4: "video/mp4"
    mp3: "audio/mpeg"
    wav: "audio/x-wav"

# OpenRouter AI 設定
open-router:
  api-key: "your_openrouter_api_key"
  base-url: "https://openrouter.ai/api/v1"
  connect-timeout: 30000
  read-timeout: 60000
  
  # セッション設定
  chat:
    session-ttl-seconds: 2592000  # セッションTTL: 30日
  
  # コンテキスト切り捨て設定
  truncation:
    response-token-reserve: 2000   # 応答用に予約するトークン
    text-chars-per-token: 4        # トークンあたりのテキスト文字比
    image-token-estimate: 1500     # 画像あたりの推定トークン
    file-token-estimate: 4000      # ファイルあたりの推定トークン
    enable-compression: true       # 圧縮を有効にする
  
  # プラグイン設定
  plugins:
    # Web検索プラグイン
    web:
      enabled: true
      max-results: 5
    # ファイルパーサープラグイン
    file-parser:
      enabled: true
      pdf:
        # 処理エンジン: mistral-ocr / pdf-text(無料) / native
        engine: pdf-text
        static-filename: document.pdf
  
  # モデルレジストリ設定
  remote-registry:
    enabled: true
    url: "https://openrouter.ai/api/frontend/models"
    cron: "0 0/30 * * * ?"  # 30分ごとに更新
    read-timeout-seconds: 10
    # フィルターモード: ALL (全モデル), FREE (無料モデル), PAID (有料モデル)
    filter: FREE
    # 自動選択設定
    auto:
      modelId: 812bc18a-5b61-42d0-a550-f9ed8c2164c5  # 固定モデルID (オプション)
      prefer: FREE  # 無料モデルを優先
  
  # 音声処理設定
  audio:
    max-size-bytes: 20971520  # 20MB
    allowed-formats:
      - wav
      - mp3
    default-format: wav
  
  # 深層推論設定
  reasoning:
    enabled: true
    max-tokens: 2000
  
  # アップロード制限設定
  upload:
    max-attachment-count: 8  # 最大8つの添付ファイル

server:
  port: 9898
  servlet:
    context-path: /api
```

### 本番環境設定 (application-prod.yml)

本番環境の設定は開発環境と似ていますが、以下の点に注意してください:

1. **データベース設定**: 本番用データベースのURLと強力なパスワードを使用します。
2. **Redis設定**: 本番用RedisのURLとパスワードを使用します。
3. **ログレベル**: INFOまたはWARNに調整します。
4. **接続プール**: 実際の負荷に基づいて調整します。
5. **APIキー**: 本番環境のAPIキーを使用します。

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

# 本番環境ではデバッグログを無効にすることをお勧めします
logging:
  level:
    root: INFO
    com.cn: INFO
```

### 管理者アカウントの初期化

システムは初回起動時に管理者アカウントを自動的に作成するため、手動で作成する必要はありません。

#### 設定パラメータ

| パラメータ | 説明 | デフォルト値 |
|---|---|---|
| `admin.init.enabled` | 管理者初期化を有効にするかどうか | `true` |
| `admin.init.email` | 管理者メール (ログインアカウント) | `admin@example.com` |
| `admin.init.password` | 管理者パスワード | `admin123` |
| `admin.init.nickname` | 管理者ニックネーム | `System Administrator` |
| `admin.init.initial-credits` | 初期クレジット | `1000000` |

#### 仕組み

1. **起動時チェック**: `AdminAccountInitializer`はアプリケーション起動時に自動的に実行されます。
2. **アカウントチェック**: メールに基づいて管理者アカウントが既に存在するかどうかを確認します。
3. **自動作成**: 存在しない場合、管理者アカウントを作成し、クレジットを初期化します。
4. **べき等性**: 繰り返し起動しても重複アカウントは作成されず、安全性が確保されます。

#### 起動ログの例

```
Admin account created successfully!
Email: admin@example.com
Nickname: System Administrator
Role: ADMIN
Initial Credits: 1000000
Admin account initialization complete!
```

#### セキュリティ推奨事項

⚠️ **本番環境にデプロイする前に、必ずデフォルトのパスワードを変更してください！**

```yaml
admin:
  init:
    enabled: true
    email: your_admin@company.com  # 会社のメールに変更
    password: StrongP@ssw0rd!2024  # 強力なパスワードを使用
    nickname: Administrator
    initial-credits: 10000000
```

#### 自動初期化の無効化

管理者アカウントを自動的に作成する必要がない場合は、この機能を無効にできます:

```yaml
admin:
  init:
    enabled: false  # 管理者初期化を無効にする
```

---

### 設定項目の説明

#### 1. データベース接続プール

| 設定 | 説明 | 推奨値 |
|---|---|---|
| initial-size | 初期接続数 | 5-10 |
| min-idle | 最小アイドル接続数 | 5-10 |
| max-active | 最大アクティブ接続数 | 20-50 |
| max-wait | 最大待機時間 (ms) | 60000 |

#### 2. Redis接続プール

| 設定 | 説明 | 推奨値 |
|---|---|---|
| max-active | 最大接続数 | 200 |
| max-idle | 最大アイドル接続数 | 10 |
| min-idle | 最小アイドル接続数 | 0 |
| timeout | 接続タイムアウト | 10s |

#### 3. OpenRouterモデルのフィルタリング

- `ALL`: 利用可能なすべてのモデルを取得します。
- `FREE`: 無料モデルのみを取得します (開発に推奨)。
- `PAID`: 有料モデルのみを取得します (本番に推奨)。

#### 4. ComfyUIタスク設定

- `submit-task-max`: キュー内の最大タスク数 (メモリオーバーフローを防ぐ)。
- `max-retry-time`: 単一接続のタイムアウト。
- `max-retries`: 失敗時のリトライ回数。
- `timeout-check-interval`: タイムアウトチェック間隔。

### 環境変数設定 (推奨)

セキュリティのため、機密情報は環境変数を使用して保存することをお勧めします:

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

そして、設定ファイルでそれらを参照します:

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

## 📊 データベース設計

> 📁 **完全なデータベーススキーマ**: 完全なテーブル作成ステートメントとインデックス定義については、プロジェクトルートの`.sql/table.sql`ファイルを参照してください。

### コアテーブル構造

#### user - ユーザーテーブル
- `id` - ユーザーID
- `email` - メール
- `password` - パスワード (暗号化)
- `nickname` - ニックネーム
- `avatar` - アバター
- `role` - ロール (USER/ADMIN)
- `created_time` - 作成日時

#### user_credits - ユーザークレジットテーブル
- `user_id` - ユーザーID
- `available_credits` - 利用可能クレジット
- `frozen_credits` - 凍結クレジット
- `total_earned` - 合計獲得クレジット
- `total_consumed` - 合計消費クレジット

#### credit_transaction - クレジット取引テーブル
- `id` - 取引ID
- `user_id` - ユーザーID
- `amount` - 取引額
- `type` - 取引タイプ (EARN/CONSUME/FREEZE/UNFREEZE)
- `description` - 説明
- `created_time` - 作成日時

#### workflow - ワークフローテーブル
- `id` - ワークフローID
- `name` - 名前
- `category_id` - カテゴリID
- `json` - ワークフローJSON
- `credits_required` - 必要クレジット
- `created_time` - 作成日時

#### workflow_form - ワークフローフォームテーブル
- `id` - フォームID
- `workflow_id` - ワークフローID
- `node_key` - ノードキー
- `label` - ラベル
- `type` - タイプ (TEXT/IMAGE/NUMBERなど)
- `required` - 必須かどうか
- `default_value` - デフォルト値

#### workflow_output - ワークフロー出力テーブル
- `workflow_id` - ワークフローID
- `node_key` - 出力ノードキー
- `type` - 出力タイプ (IMAGE/VIDEO/AUDIO/MODEL)

#### workflow_result - ワークフロー結果テーブル
- `id` - 結果ID
- `task_id` - タスクID
- `user_id` - ユーザーID
- `workflow_name` - ワークフロー名
- `url` - 結果URL
- `type` - 結果タイプ
- `created_time` - 作成日時

#### redemption_code - 交換コードテーブル
- `id` - コードID
- `code` - 交換コード
- `credits` - クレジット額
- `status` - ステータス (UNUSED/USED)
- `used_by` - 使用したユーザーのID
- `used_time` - 使用日時
- `created_time` - 作成日時

---

## 🎯 ベストプラクティス

### 1. クレジット管理

```java
// クレジットを凍結
creditUtils.freezeCredits(userId, amount, "Task submission");

// クレジットを消費
creditUtils.consumeCredits(userId, amount, "Task completion");

// クレジットの凍結解除
creditUtils.unfreezeCredits(userId, amount, "Task cancellation");
```

### 2. 分散ロック

```java
RLock lock = redissonClient.getLock("LOCK_KEY:" + userId);
try {
    if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
        // ビジネスロジック
    }
} finally {
    lock.unlock();
}
```

### 3. レート制限

```java
@RateLimit(
    permitsPerSecond = 1.0,
    limitType = RateLimit.LimitType.USER,
    message = "Operation is too frequent"
)
public Result someApi() {
    // APIロジック
}
```

### 4. 非同期タスク

```java
@Async
public void asyncTask() {
    // 非同期処理ロジック
}
```

---

## 🔐 セキュリティ推奨事項

1. **本番環境設定**:
   - デフォルトのパスワードを変更する。
   - 強力なパスワードポリシーを使用する。
   - HTTPSを有効にする。
   - ファイアウォールルールを設定する。

2. **機密情報の保護**:
   - 機密設定には環境変数を使用する。
   - コードリポジトリにキーをコミットしない。
   - APIキーを定期的にローテーションする。

3. **APIセキュリティ**:
   - APIレート制限を有効にする。
   - リクエスト署名検証を実装する。
   - IPホワイトリストを追加する。

4. **データセキュリティ**:
   - データベースを定期的にバックアップする。
   - 機密フィールドを暗号化する。
   - 監査ログを実装する。

---

## 🤝 貢献ガイド

貢献を歓迎します！以下の手順に従ってください:

1. このリポジトリをフォークします。
2. 機能ブランチを作成します (`git checkout -b feature/AmazingFeature`)。
3. 変更をコミットします (`git commit -m 'Add some AmazingFeature'`)。
4. ブランチにプッシュします (`git push origin feature/AmazingFeature`)。
5. プルリクエストを開きます。

### コードスタイル

- Alibaba Java開発マニュアルに従います。
- Lombokを使用してコードを簡素化します。
- 必要なコメントとドキュメントを追加します。
- ユニットテストを作成します。

---

## 🚢 デプロイガイド

> ⚠️ **重要**: 本番環境にデプロイする前に、設定ファイル内の管理者アカウントのパスワード、データベースのパスワード、Redisのパスワード、APIキーなどの機密情報を必ず変更してください！

### 方法1: 従来型デプロイ (本番環境に推奨)

#### 1. サーバー環境の準備

```bash
# JDK 21 をインストール
wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.tar.gz
tar -xzf jdk-21_linux-x64_bin.tar.gz
sudo mv jdk-21 /usr/local/

# 環境変数を設定
echo 'export JAVA_HOME=/usr/local/jdk-21' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# インストールを確認
java -version
```

#### 2. MySQL 8.0 のインストール

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# CentOS/RHEL
sudo yum install mysql-server

# MySQL を起動
sudo systemctl start mysql
sudo systemctl enable mysql

# セキュアインストール
sudo mysql_secure_installation
```

#### 3. Redis のインストール

```bash
# Ubuntu/Debian
sudo apt install redis-server

# CentOS/RHEL
sudo yum install redis

# Redis を起動
sudo systemctl start redis
sudo systemctl enable redis

# Redis パスワードの設定 (オプション)
sudo vim /etc/redis/redis.conf
# # requirepass foobared を見つける
# コメントを解除し、requirepass your_password に変更
sudo systemctl restart redis
```

#### 4. データベースの作成

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

#### 5. データベーススキーマのインポート

```bash
# 提供されたスキーマファイルをインポート
cd /opt/conni-x-pro
mysql -u conni_user -p ghosts < .sql/table.sql
```

#### 6. プロジェクトのコンパイル

```bash
# プロジェクトをクローン
git clone https://github.com/dulaiduwang003/Conni-X-Pro.git
cd Conni-X-Pro/singleton

# 設定ファイルを修正
vim application/src/main/resources/application-prod.yml

# コンパイルとパッケージ化
mvn clean package -DskipTests -P prod

# パッケージ化されたファイルは以下にあります
# application/target/application-1.0-SNAPSHOT.jar
```

#### 7. デプロイと実行

```bash
# デプロイディレクトリを作成
sudo mkdir -p /opt/conni-x-pro
sudo cp application/target/application-1.0-SNAPSHOT.jar /opt/conni-x-pro/

# 起動スクリプトを作成
sudo vim /opt/conni-x-pro/start.sh
```

起動スクリプトの内容:

```bash
#!/bin/bash
APP_NAME=application-1.0-SNAPSHOT.jar
APP_PATH=/opt/conni-x-pro
LOG_PATH=/opt/conni-x-pro/logs

# ログディレクトリを作成
mkdir -p $LOG_PATH

# JVM オプション
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"

# アプリケーションを起動
nohup java $JAVA_OPTS -jar $APP_PATH/$APP_NAME \
  > $LOG_PATH/app.log 2>&1 &

echo $! > $APP_PATH/app.pid
echo "Application started with PID: $(cat $APP_PATH/app.pid)"
```

停止スクリプト (`stop.sh`)：

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
# 実行権限を付与
sudo chmod +x /opt/conni-x-pro/start.sh
sudo chmod +x /opt/conni-x-pro/stop.sh

# アプリケーションを起動
sudo /opt/conni-x-pro/start.sh

# ログを表示
tail -f /opt/conni-x-pro/logs/app.log
```

#### 8. Systemdサービスの設定 (推奨)

```bash
sudo vim /etc/systemd/system/conni-x-pro.service
```

サービス設定の内容:

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
# systemd設定をリロード
sudo systemctl daemon-reload

# サービスを開始
sudo systemctl start conni-x-pro

# 起動時に有効化
sudo systemctl enable conni-x-pro

# サービスステータスを確認
sudo systemctl status conni-x-pro

# ログを表示
sudo journalctl -u conni-x-pro -f
```

#### 9. Nginxリバースプロキシの設定

```bash
sudo apt install nginx
sudo vim /etc/nginx/sites-available/conni-x-pro
```

Nginx設定:

```nginx
upstream conni_backend {
    server 127.0.0.1:9898;
}

server {
    listen 80;
    server_name your-domain.com;

    # ログ設定
    access_log /var/log/nginx/conni-access.log;
    error_log /var/log/nginx/conni-error.log;

    # クライアントアップロードサイズ制限
    client_max_body_size 100M;

    location /api/ {
        proxy_pass http://conni_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket サポート
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # タイムアウト設定
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # SSE ストリーミング応答設定
    location /api/llm/chat/stream {
        proxy_pass http://conni_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # SSE 固有の設定
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
# サイトを有効化
sudo ln -s /etc/nginx/sites-available/conni-x-pro /etc/nginx/sites-enabled/

# 設定をテスト
sudo nginx -t

# Nginx を再起動
sudo systemctl restart nginx
```

#### 10. HTTPSの設定 (推奨)

```bash
# Certbot をインストール
sudo apt install certbot python3-certbot-nginx

# SSL証明書を取得
sudo certbot --nginx -d your-domain.com

# 自動更新
sudo certbot renew --dry-run
```

---

### 方法2: Dockerデプロイ

#### 1. Dockerfileの作成

プロジェクトルートに`Dockerfile`を作成します:

```dockerfile
FROM openjdk:21-jdk-slim

LABEL maintainer="dulaiduwang003 <2074055628@qq.com>"

# 作業ディレクトリを設定
WORKDIR /app

# jar ファイルをコピー
COPY application/target/application-1.0-SNAPSHOT.jar app.jar

# ポートを公開
EXPOSE 9898

# JVM オプション
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

# 起動コマンド
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar /app/app.jar"]
```

#### 2. docker-compose.ymlの作成

```yaml
version: '3.8'

services:
  # MySQL データベース
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

  # Redis キャッシュ
  redis:
    image: redis:7-alpine
    container_name: conni-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes --requirepass redis_password

  # アプリケーションサービス
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

> 💡 **注意**: docker-compose.ymlは、`.sql/table.sql`ファイルをMySQLコンテナに自動的にインポートするように設定されており、起動時にすべてのテーブルを作成します。

#### 3. ビルドと実行

```bash
# プロジェクトをコンパイル
mvn clean package -DskipTests -P prod

# Dockerイメージをビルド
docker-compose build

# 全てのサービスを起動
docker-compose up -d

# ログを表示
docker-compose logs -f app

# サービスを停止
docker-compose down

# サービスを停止してボリュームを削除
docker-compose down -v
```

---

### 方法3: Kubernetesデプロイ

#### 1. Deploymentの作成

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

#### 2. Serviceの作成

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

#### 3. K8sへのデプロイ

```bash
# 設定を適用
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml

# ステータスを確認
kubectl get pods
kubectl get services

# ログを表示
kubectl logs -f deployment/conni-x-pro
```

---

### デプロイチェックリスト

#### 起動前チェックリスト

- [ ] MySQLデータベースが作成され、スキーマがインポートされている。
- [ ] Redisサービスが実行中である。
- [ ] 設定ファイル内のパスワードとキーが変更されている。
- [ ] 管理者アカウント設定 (メール、パスワード) が変更されている。
- [ ] Aliyun OSS設定が正しい。
- [ ] OpenRouter APIキーが有効である。
- [ ] ComfyUIサーバーがアクセス可能である (必要な場合)。
- [ ] メールサービス設定が正しい。

#### 起動後チェックリスト

```bash
# 1. アプリケーションが起動したか確認
curl http://localhost:9898/api/actuator/health

# 2. データベース接続を確認
mysql -h localhost -u conni_user -p -e "USE ghosts; SHOW TABLES;"

# 3. Redis接続を確認
redis-cli -a your_password ping

# 4. アプリケーションログを表示 (管理者アカウント作成を確認)
tail -f /opt/conni-x-pro/logs/app.log | grep "Admin"

# 5. 管理者アカウント作成を確認
mysql -h localhost -u conni_user -p -e "USE ghosts; SELECT email, nickname, role FROM user WHERE role='ADMIN';"

# 6. APIエンドポイントをテスト
curl http://localhost:9898/api/notice/get

# 7. 管理者ログインをテスト
curl -X POST http://localhost:9898/api/auth/password-login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

#### パフォーマンス最適化の推奨事項

1. **JVMパラメータチューニング**:
```bash
-Xms2g -Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/opt/conni-x-pro/logs/
```

2. **データベース接続プール**:
```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000
```

3. **Redis接続プール**:
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

#### 監視とロギング

1. **アプリケーションログ**:
```bash
# リアルタイムでログを表示
tail -f /opt/conni-x-pro/logs/app.log

# エラーログを表示
grep ERROR /opt/conni-x-pro/logs/app.log

# ログローテーション設定
vim /etc/logrotate.d/conni-x-pro
```

2. **システム監視**:
```bash
# CPUとメモリ使用率
top
htop

# ディスク使用量
df -h

# ネットワーク接続
netstat -tunlp | grep 9898
```

---

## 📝 開発計画

- [ ] より多くのAIモデルプロバイダーをサポートする。
- [ ] WebSocketメッセージプッシュを追加する。
- [ ] タスク優先度キューを実装する。
- [ ] ワークフローのバージョン管理をサポートする。
- [ ] ユーザー行動分析を追加する。
- [ ] マルチテナンシーサポートを実装する。
- [ ] GraphQL APIを追加する。
- [x] Dockerデプロイをサポートする。

---

## 📄 ライセンス

このプロジェクトはオープンソースライセンスの下でライセンスされています。詳細は[LICENSE](LICENSE)ファイルを参照してください。

---

## 👨‍💻 作者

**時間海 (dulaiduwang003)**

- GitHub: [@dulaiduwang003](https://github.com/dulaiduwang003)
- メール: 2074055628@qq.com
- 組織: bdth, Inc.

---

## 📞 連絡先

質問や提案がある場合は、以下の方法でお問い合わせください:

- [Issue](https://github.com/dulaiduwang003/Conni-X-Pro/issues)を送信
- メール: 2074055628@qq.com

---

<div align="center">

**⭐ このプロジェクトが役に立ったら、Starを付けてください！ ⭐**

Made with ❤️ by 時間海

</div>


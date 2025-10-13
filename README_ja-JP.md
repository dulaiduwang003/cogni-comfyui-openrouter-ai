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
  <a href="#quick-start">クイックスタート</a> ·
  <a href="#features">機能</a> ·
  <a href="#tech-stack">技術スタック</a> ·
  <a href="#module-architecture">モジュールアーキテクチャ</a> ·
  <a href="#api--conventions">APIと規約</a> ·
  <a href="#contributing">コントリビューション</a>
</p>

AI作成とワークフローのためのフルスタックオープンソースプロジェクト：フロントエンドはVue 3 + Vite + TypeScript + Element Plusで構築され、バックエンドはSpring Boot 3のマルチモジュールアーキテクチャを採用しています。認証、チャット（LLM/OpenRouterストリーミング）、ComfyUIワークフローオーケストレーション、オブジェクトストレージ（Aliyun OSS）、メール通知、システム管理などの機能を統合しています。

### デモ用ComfyUIスタートキット​
クラウドストレージで共有されたファイル：「2_整合包ComfyUI-aki-1.6.rar」など2つのファイル
リンク：https://pan.baidu.com/s/1hcsZiz367FtK4pJHRWVa_g?pwd=iavj  抽出コード：iavj

### 機能プレビュー
<p align="center">

  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/082ef78b-8925-40cb-8652-88a6f974c029.png" alt="機能プレビュー1" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/517989e3-c046-4192-9d5e-a5804f5202d9.png" alt="機能プレビュー2" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/1cedf4ed-0602-4e9f-a2a0-6321f1df5114.png" alt="機能プレビュー3" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/01d9a880-d453-40fd-95a8-f210b8d0ed31.png" alt="機能プレビュー4" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/f7008912-3c91-4e29-badf-b7a38d031bba.png" alt="機能プレビュー5" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/77833d26-22ff-49d1-b85b-c815c8232f9b.png" alt="機能プレビュー6" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/c8d6e372-e28b-4839-8be1-bff83c172d7e.png" alt="機能プレビュー7" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/e669f851-b760-4a7d-8cf5-07a657182d1a.png" alt="機能プレビュー8" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/70218a85-4b32-424f-ba83-74a3a7e7239c.png" alt="機能プレビュー9" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/3b5950c1-dfe3-4090-aa38-118d18d0ec1f.png" alt="機能プレビュー10" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/604843f6-ef54-48ca-a6c8-26ced1931298.png" alt="機能プレビュー11" width="150" />
  <img src="https://hyperbot.oss-accelerate.aliyuncs.com/TEMP/4cad610a-3276-4888-b520-e3b66ccdb137.png" alt="機能プレビュー12" width="150" />
</p>

### 機能
- **認証と認可**: Sa-Tokenに基づき、Bearer Token方式を採用。APIアクセスフィルタリングとルートフィルタリングを内蔵。
- **チャットとAI機能**: OpenRouterと統合し、複数モデルの登録と自動選択を実現。SSEストリーミングチャット、リッチテキストハイライト付きの多言語対応（主に無料APIモデルサービスを活用 - 詳細はyml設定とバックエンドドキュメントを参照）。
- **ComfyUIワークフロー**: フォームベースのパラメータ収集、タスクの提出とステータス購読、タイムアウトとリトライ戦略、ファイルタイプのホワイトリスト、複数ComfyUIサーバーのサポート（タスクの提出、キャンセル、再作成）。
- **オブジェクトストレージ**: Aliyun OSSのアップロードと表示、グローバルなファイルタイプとサイズの制限設定。
- **通知**: メール認証コード/通知（Thymeleafテンプレート）、システムアナウンスとアプリ内通知。
- **システム管理**: ユーザー/クレジット管理、アナウンス、引き換えコード、統計ダッシュボードなど、一般的な管理者機能。


### デプロイメント

#### バックエンドのデプロイメント

バックエンドは標準的なSpring Bootマルチモジュールプロジェクトで、実行可能なJARファイルにパッケージ化してデプロイできます。従来のサーバー、Dockerコンテナ、Kubernetes（K8s）のデプロイシナリオをカバーする包括的なデプロイメントドキュメントを提供しています。

👉 **[詳細なバックエンドデプロイメントガイドを見る](./singleton/README.md)**

#### フロントエンドのデプロイメント

フロントエンドプロジェクトはVue 3とViteで構築されています。デプロイメントには、静的ファイルにコンパイルし、NginxなどのWebサーバーでリバースプロキシ設定を使用してホストする必要があります。

👉 **[詳細なフロントエンドデプロイメントガイドを見る](./vue/README.md)**

### 技術スタック
- **フロントエンド**: Vue 3, Vite, TypeScript, Pinia, Vue Router, Element Plus, vue-i18n, highlight.js, lottie-web, @google/model-viewer.
- **バックエンド**: Spring Boot 3, Web + WebFlux (SSE), Sa-Token, MyBatis-Plus, Druid, MySQL, Redis (Redisson), Spring Mail, WebSocket, Lombok, Guava.

### モジュールアーキテクチャ
```
singleton/               # バックエンドマルチモジュール集約プロジェクト (Maven)
  application/           # 実行可能な起動モジュール (ビジネスモジュールを集約)
  common/                # 共通依存関係: 設定、インターセプター、ユーティリティ、ストレージ、認証など
  auth/                  # 認証とユーザー関連API
  comfyui/               # ComfyUIタスクオーケストレーションとWebSocketプッシュ
  llm/                   # LLMチャットとモデル登録 (OpenRouter)
  notice/                # 通知とアナウンス
  oss/                   # Aliyun OSS機能ラッパー
  system/                # システム管理ドメイン
vue/                     # フロントエンドプロジェクト (Vite)
```

### クイックスタート
#### 前提条件
- JDK 21+ (22を推奨)
- Maven 3.9+
- Node.js 18+ / pnpm または npm
- MySQL 8+, Redis 6+

#### バックエンド設定
1. `singleton/application/src/main/resources/application-dev.yml` をコピーまたは参照し、必要に応じて以下の主要項目を修正します：
   - `spring.datasource.url/username/password` (MySQL)
   - `spring.data.redis.host/port/password` (Redis)
   - `spring.mail.*` (オプション、メール認証コード/通知)
   - `ali.oss.*` (Aliyun OSS)
   - `comfyui.server[*].url` (ComfyUIサービスアドレス)
   - `open-router.api-key` (OpenRouter APIキー)
2. 起動（開発モード）:
   ```bash
   mvn -f singleton/pom.xml -pl application -am spring-boot:run -Pdev
   ```
   またはパッケージ化:
   ```bash
   mvn -f singleton/pom.xml -Pdev clean package
   java -jar singleton/application/target/application-*.jar
   ```
3. デフォルトのバックエンドアドレス: `http://localhost:9898/api` (`server.servlet.context-path=/api`)。

#### 開発環境設定の詳細 (application-dev.yml)

例（プレースホルダーを独自の値に置き換えてください）:

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/ghosts?useUnicode=true&serverTimezone=Asia/Shanghai&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&autoReconnect=true&allowMultiQueries=true&useSSL=true # MySQL接続文字列
    username: root # データベースユーザー
    password: "<your_mysql_password>" # データベースパスワード
  data:
    redis:
      database: 1 # 論理DB (0-15)
      host: 127.0.0.1 # Redisホスト
      port: 6379 # Redisポート
      password: "" # パスワードがない場合は空のまま
  mail:
    host: smtp.qq.com # SMTPサーバー
    username: "<your_email>@qq.com" # 送信者メール
    password: "<your_email_auth_code>" # 認証コード/アプリ固有パスワード
    default-encoding: UTF-8 # エンコーディング
    properties:
      mail:
        smtp:
          socketFactory:
            class: javax.net.ssl.SSLSocketFactory # SSLを有効化
    port: 587 # SMTPポート

admin:
  init:
    enabled: true # 管理者の自動初期化を有効化
    email: admin@example.com # 管理者メール
    password: admin123 # 管理者パスワード
    nickname: System Admin # 管理者ニックネーム
    initial-credits: 1000000 # 初期クレジット

ali:
  certified:
    access-key: "<your_ali_access_key>" # Aliyun AK
    secret-key: "<your_ali_secret_key>" # Aliyun SK
  oss:
    endpoint: "oss-accelerate.aliyuncs.com" # OSSエンドポイント
    bucket-name: "<your_bucket_name>" # OSSバケット名
    domain: "https://<your_bucket_name>.oss-accelerate.aliyuncs.com" # OSSドメイン
    supported-file-types:
      - extension: "jpg" # ファイル拡張子
        mimeType: "image/jpeg" # MIMEタイプ
        maxSizeInBytes: 5242880 # ファイルごとの最大サイズ

comfyui:
  server:
    - name: COMFYUI-1 # サーバー識別子
      url: http://localhost:8188 # ComfyUIアドレス
  submit-task-max: 100 # 最大タスクキュー
  task:
    max-retry-time: 30 # 単一接続タイムアウト (秒)
    max-retries: 2 # 失敗時のリトライ回数
    timeout-check-interval: 30 # タイムアウトチェック間隔 (分)
  supported-file-types:
    jpg: "image/jpeg" # フォームで許可されるアップロードタイプ
    png: "image/png"

open-router:
  api-key: "<your_openrouter_api_key>" # OpenRouter APIキー
  base-url: "https://openrouter.ai/api/v1" # APIベースURL
  connect-timeout: 30000 # 接続タイムアウト (ms)
  read-timeout: 60000 # 読み取りタイムアウト (ms)
  chat:
    session-ttl-seconds: 2592000 # セッションTTL (秒)
  truncation:
    response-token-reserve: 2000 # レスポンス用トークン予約
    text-chars-per-token: 4 # トークンあたりのテキスト文字数
    image-token-estimate: 1500 # 画像あたりのトークン推定値
    file-token-estimate: 4000 # ファイルあたりのトークン推定値
    enable-compression: true # テキスト圧縮を有効化
  plugins:
    web:
      enabled: true # Web検索を有効化
      max-results: 5 # 最大結果数
    file-parser:
      enabled: true # ファイル解析を有効化
      pdf:
        engine: pdf-text # OCRエンジン
        static-filename: document.pdf # デフォルトのファイル名
  remote-registry:
    enabled: true # モデルのプルを有効化
    url: "https://openrouter.ai/api/frontend/models" # モデルリストURL
    cron: "0 0/30 * * * ?" # 更新間隔
    read-timeout-seconds: 10 # 読み取りタイムアウト (秒)
    filter: FREE # モデルスコープ: ALL/FREE/PAID
    auto:
      modelId: "<optional_model_id>" # 固定モデル (オプション)
      prefer: FREE # FREE/PAID
  audio:
    max-size-bytes: 20971520 # 最大音声サイズ
    allowed-formats:
      - wav
      - mp3
    default-format: wav # デフォルトフォーマット
  reasoning:
    enabled: true # ディープシンキングを有効化
    max-tokens: 2000 # リーズニングの最大トークン数
  upload:
    max-attachment-count: 8 # メッセージあたりの最大添付ファイル数

server:
  port: 9000 # 開発ポート (フロントエンドに合わせて9898に変更可能)
  servlet:
    context-path: /api # APIプレフィックス
```

#### フロントエンドの起動
```bash
cd vue
npm i
npm run dev
```
アクセス: `http://localhost:5173`

### APIと規約
- ベースプレフィックス: `/api`
- 認証ヘッダー: `Authorization: Bearer <token>` (Sa-Token)
- レスポンス構造とステータスコード: バックエンドの統一規約に従う (詳細は`common`モジュールとコントローラーを参照)。

### セキュリティとコンプライアンス
- 機密情報（データベース、メール、OSS、OpenRouterキーなど）をリポジトリにコミットしないでください。
- 環境変数やプライベートな設定ファイルを使用してサンプル設定を上書きすることを推奨します。
- 公開デモシナリオでは、ファイルアップロードのタイプとサイズを制限してください（このプロジェクトはグローバルなホワイトリストとサイズ制限を提供します）。

### ロードマップ（提案）
- クロスモデルのセッション共有とメッセージリプレイの最適化
- ComfyUIノードテンプレート管理と自動フォーム生成
- より詳細な権限と監査ログ
- 完全なE2E/ユニットテストとCI/CDワークフロー

### コントリビューション
IssueとPRを歓迎します:
- コミットメッセージの規約に従い、可能であれば再現手順やスクリーンショットを添付してください
- 新しいモジュールについては、関連するドキュメントとサンプル設定を補足してください

### 作者をサポート
このプロジェクトが役に立った場合は、WeChatでの寄付を歓迎します:

<p align="center">
  <img src="https://github.com/user-attachments/assets/f18e4cef-79c9-4fb4-8019-869f30a666ad" alt="WeChat Donation Code" width="280" />
</p>


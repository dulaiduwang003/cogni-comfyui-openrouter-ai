[English](./README_en-US.md) | [简体中文](./README.md) | [日本語](./README_ja-JP.md)

# Cogni X - クイックデプロイガイド

## 📖 プロジェクト概要

Cogni XはVue 3をベースにした最新のAIアプリケーションプラットフォームで、多様なAIサービスを提供します。

### 技術スタック

- **フロントエンドフレームワーク**: Vue 3.5.13 + TypeScript
- **ビルドツール**: Vite 6.2.0
- **UIコンポーネントライブラリ**: Element Plus 2.9.11
- **状態管理**: Pinia 3.0.3
- **ルーティング**: Vue Router 4.5.0
- **国際化**: Vue I18n 9.14.5
- **HTTPクライアント**: Axios 1.9.0
- **その他の機能**: Markdownレンダリング、コードハイライト、3Dモデルプレビュー、masonryレイアウト

### 主な機能モジュール

- 🤖 **AIチャット**: インテリジェントな対話
- 🎨 **ComfyUIワークフロー**: 視覚的なAIワークフロー管理
- 📁 **作品管理**: ユーザー生成作品の表示と管理
- 👤 **パーソナルセンター**: ユーザー情報とクレジット管理
- 🔐 **ユーザー認証**: ログイン、登録、アクセス制御
- 🛠️ **システム管理**: バックエンド管理機能（管理者権限が必要）

### 更新履歴

- **2025-10-28**: 
  - ComfyUI画像塗りつぶしコンポーネントを追加
  - 作品の一括削除機能を追加
    - 複数の作品を選択して一括削除をサポート
    - ダークテーマのチェックボックススタイルを最適化
    - ダークテーマのボタンとダイアログスタイルを最適化
    - 削除確認のインタラクション体験を改善

---

## ⚙️ 環境要件

### 開発環境

- **Node.js**: >= 18.x (18.x または 20.x LTSを推奨)
- **npm**: >= 9.x または **pnpm**: >= 8.x
- **オペレーティングシステム**: Windows / macOS / Linux

### 本番環境

- **Nginx**: >= 1.18.x
- **オペレーティングシステム**: Linuxを推奨 (Ubuntu 20.04+, CentOS 8+, など)

---

## 🚀 ローカル開発

### 1. プロジェクトのクローン

```bash
git clone <your-repository-url>
cd vue
```

### 2. 依存関係のインストール

npmを使用:
```bash
npm install
```

またはpnpmを使用:
```bash
pnpm install
```

### 3. 環境変数の設定

プロジェクトのルートに`.env.development`ファイルを作成します:

```env
# 開発用の環境変数

# 開発APIのURL
VITE_API_BASE_URL=http://localhost:9898/api 

# WebSocketのURL
VITE_API_WS_URL=ws://127.0.0.1:9898/api
```

**注意**:
- `VITE_API_BASE_URL`: APIエンドポイントの完全なURL（プロトコル、ドメイン、ポートを含む）
- `VITE_API_WS_URL`: WebSocket接続URL

### 4. 開発プロキシの設定

`vite.config.ts`を編集してプロキシのターゲットアドレスを変更します:

```typescript
server: {
  proxy: {
    [env.VITE_API_BASE_URL]: {
      target: 'http://your-backend-api-url', // 実際のバックエンドAPIのURLに変更してください
      changeOrigin: true,
      rewrite: (path) =>
        path.replace(new RegExp('^' + env.VITE_API_BASE_URL), '')
    }
  }
}
```

### 5. 開発サーバーの起動

```bash
npm run dev
```

`http://localhost:5173`にアクセスしてアプリケーションを表示します。

---

## 📦 本番ビルド

### 1. 本番環境変数の設定

プロジェクトのルートに`.env.production`ファイルを作成します:

```env
# 本番用の環境変数

# 本番APIのURL
VITE_API_BASE_URL=http://localhost:9898/api 

# WebSocketのURL
VITE_API_WS_URL=ws://127.0.0.1:9898/api
```

**注意**:
- `VITE_API_BASE_URL`: 本番APIの完全なURL（実際のバックエンドURLに置き換えてください。例：`https://api.yourdomain.com`）
- `VITE_API_WS_URL`: 本番WebSocketのURL（実際のWebSocket URLに置き換えてください。例：`wss://api.yourdomain.com`）
- フロントエンドをサブパス（ルートではない）にデプロイする場合は、`BASE_URL`を設定する必要があります（例：`BASE_URL=/app/`）

### 2. ビルドコマンドの実行

```bash
npm run build
```

ビルドが完了すると、プロジェクトのルートにすべての静的アセットを含む`dist`ディレクトリが生成されます。

**ビルド成果物**:
- 📦 **コードの最小化と難読化**: Terserを使用した詳細な圧縮と変数名のマングリング
- 🗜️ **Gzip圧縮**: 転送サイズを削減するために`.gz`ファイルを自動的に生成
- 🎯 **コード分割**: 初回読み込み速度を向上させるためのスマートなチャンク分割
- 🧹 **デバッグコードの削除**: `console.log`、`debugger`などを自動的に削除
- 📊 **ビルド分析レポート**: バンドルサイズを分析するために`dist/stats.html`を生成

### 3. ビルドのプレビュー（オプション）

```bash
npm run preview
```

### 4. ビルド分析レポートの表示（オプション）

ビルド後、`dist/stats.html`を開くと、詳細なバンドル分析が表示されます:
- 各モジュールのサイズ貢献度
- Gzip圧縮後のサイズ
- 依存関係の可視化

---

## 🌐 Nginxデプロイ設定

### オプション1: ドメインルートへのデプロイ

これは、アプリケーションをドメインのルート（例：`https://www.yourdomain.com`）にデプロイする場合に適用されます。

#### 1. ビルドファイルのアップロード

`dist`ディレクトリからすべてのファイルをサーバーにアップロードします。推奨パス:

```bash
/var/www/cogni-x/
```

#### 2. Nginx設定ファイル

Nginx設定ファイル`/etc/nginx/sites-available/cogni-x`を作成または編集します:

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name yourdomain.com www.yourdomain.com;

    # HTTPSへの強制リダイレクト（SSL証明書を推奨）
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL証明書の設定
    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;
    
    # SSLセキュリティ設定
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # ウェブサイトのルートディレクトリ
    root /var/www/cogni-x;
    index index.html;

    # Gzip圧縮
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript 
               application/json application/javascript application/xml+rss 
               application/rss+xml font/truetype font/opentype 
               application/vnd.ms-fontobject image/svg+xml;

    # 静的アセットのキャッシュ
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # APIプロキシ（Nginx経由でバックエンドAPIをプロキシする場合）
    location /api/ {
        proxy_pass http://your-backend-server:port/;
        proxy_http_version 1.1;
        
        # プロキシヘッダー
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocketサポート（APIがWebSocketを使用する場合）
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # タイムアウト設定
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Vue Router Historyモードのサポート
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }

    # セキュリティヘッダー
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # ロギング
    access_log /var/log/nginx/cogni-x-access.log;
    error_log /var/log/nginx/cogni-x-error.log;
}
```

#### 3. 設定の有効化とNginxの再起動

```bash
# シンボリックリンクを作成して設定を有効化
sudo ln -s /etc/nginx/sites-available/cogni-x /etc/nginx/sites-enabled/

# 設定の正しさをテスト
sudo nginx -t

# Nginxを再起動
sudo systemctl restart nginx
```

---

### オプション2: サブパスへのデプロイ

これは、アプリケーションをサブパス（例：`https://www.yourdomain.com/app/`）にデプロイする場合に適用されます。

#### 1. 環境変数の変更

`.env.production`を編集します:

```env
# 本番用の環境変数

# 本番APIのURL
VITE_API_BASE_URL=https://api.yourdomain.com

# WebSocketのURL
VITE_API_WS_URL=wss://api.yourdomain.com

# サブパスデプロイ設定
BASE_URL=/app/
```

#### 2. 再ビルド

```bash
npm run build
```

#### 3. ビルドファイルのアップロード

`dist`ディレクトリからすべてのファイルを以下にアップロードします:

```bash
/var/www/html/app/
```

#### 4. Nginx設定ファイル

```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;

    # ルートディレクトリ（他のアプリケーションがある場合）
    root /var/www/html;

    # サブパスアプリケーション設定
    location /app {
        alias /var/www/html/app;
        index index.html;
        
        # Vue Router Historyモードのサポート
        try_files $uri $uri/ /app/index.html;
        
        # HTMLのキャッシュ無効化
        location ~* /app/index\.html$ {
            add_header Cache-Control "no-cache, no-store, must-revalidate";
        }
        
        # 静的アセットのキャッシュ
        location ~* /app/.*\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # APIプロキシ
    location /api/ {
        proxy_pass http://your-backend-server:port/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

### オプション3: SSLなし（HTTPのみ、本番環境では非推奨）

SSL証明書がなく、テスト目的の場合:

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    root /var/www/cogni-x;
    index index.html;

    # Gzip圧縮
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # 静的アセットのキャッシュ
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public";
    }

    # APIプロキシ
    location /api/ {
        proxy_pass http://your-backend-server:port/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Vue Routerのサポート
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

---

## 🔧 高度な設定

### 1. 無料SSL証明書の設定 (Let's Encrypt)

```bash
# Certbotのインストール
sudo apt update
sudo apt install certbot python3-certbot-nginx

# SSL証明書の自動設定
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# 自動更新のテスト
sudo certbot renew --dry-run
```

### 2. パフォーマンス最適化

メインのNginx設定ファイル`/etc/nginx/nginx.conf`に以下を追加します:

```nginx
http {
    # Gzip圧縮の有効化
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript 
               application/json application/javascript application/xml+rss 
               application/rss+xml font/truetype font/opentype 
               application/vnd.ms-fontobject image/svg+xml;

    # Brotli圧縮の有効化（モジュールのインストールが必要）
    # brotli on;
    # brotli_comp_level 6;
    # brotli_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # クライアントバッファサイズ
    client_body_buffer_size 128k;
    client_max_body_size 20m;
    client_header_buffer_size 1k;
    large_client_header_buffers 4 4k;

    # FastCGIキャッシュ（使用する場合）
    fastcgi_buffer_size 128k;
    fastcgi_buffers 4 256k;
    fastcgi_busy_buffers_size 256k;

    # 接続タイムアウト
    keepalive_timeout 65;
    send_timeout 60;
}
```

### 3. セキュリティ強化

```nginx
server {
    # ... その他の設定 ...

    # 隠しファイルへのアクセスを拒否
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }

    # リクエストメソッドの制限
    if ($request_method !~ ^(GET|HEAD|POST|PUT|DELETE|OPTIONS)$ ) {
        return 405;
    }

    # クリックジャッキング防止
    add_header X-Frame-Options "SAMEORIGIN" always;
    
    # XSSフィルタリングの有効化
    add_header X-XSS-Protection "1; mode=block" always;
    
    # コンテンツタイプスニッフィングの無効化
    add_header X-Content-Type-Options "nosniff" always;
    
    # コンテンツセキュリティポリシー（必要に応じて調整）
    add_header Content-Security-Policy "default-src 'self' https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https:; style-src 'self' 'unsafe-inline' https:; img-src 'self' data: https:; font-src 'self' data: https:;" always;
    
    # HSTS（HTTPSを強制）
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
}
```

### 4. ログローテーション設定

`/etc/logrotate.d/cogni-x`を作成します:

```
/var/log/nginx/cogni-x-*.log {
    daily
    rotate 14
    missingok
    notifempty
    compress
    delaycompress
    sharedscripts
    postrotate
        [ -f /var/run/nginx.pid ] && kill -USR1 `cat /var/run/nginx.pid`
    endscript
}
```

---

## ⚡ ビルド最適化

このプロジェクトは、本番環境でのコードのセキュリティと高速な読み込みを確保するために、コードの最小化、難読化、およびパフォーマンス最適化が完全に設定されています。

### 最適化機能

#### 1. コードの最小化と難読化
- ✅ **Terserによる最小化**: JavaScriptコードを詳細に最小化します。
- ✅ **変数マングリング**: 変数名と関数名を難読化してコードのセキュリティを向上させます。
- ✅ **コメントの削除**: すべてのコードコメントを削除します。
- ✅ **デバッグコードの削除**: `console.log`、`debugger`、その他のデバッグ文を自動的に削除します。

#### 2. Gzip圧縮
- ✅ **.gzファイルの自動生成**: 10KBを超えるファイルは自動的にGzipで圧縮されます。
- ✅ **転送サイズの削減**: 通常、ファイルサイズを60-80%削減します。
- ✅ **元のファイルの保持**: 元のファイルと圧縮されたファイルの両方が保持されます。

#### 3. コード分割
- ✅ **Vueフレームワークのバンドル**: `vue`、`vue-router`、`pinia`は別々にバンドルされます。
- ✅ **UIフレームワークのバンドル**: `element-plus`は別々にバンドルされます。
- ✅ **サードパーティライブラリの分割**: `axios`、`markdown-it`、`highlight.js`などは別々にバンドルされます。
- ✅ **キャッシュヒット率の向上**: フレームワークコードは更新頻度が低いため、キャッシュの利用率が向上します。

#### 4. ビルド分析
- ✅ **視覚的なレポート**: ビルド後に`dist/stats.html`分析レポートを生成します。
- ✅ **サイズ分析**: 各モジュールのサイズ貢献度を表示します。
- ✅ **依存関係の可視化**: モジュールの依存関係を視覚的に検査します。

### 設定

すべての最適化設定は`vite.config.ts`にあります:

```typescript
build: {
  // Terserの最小化オプション
  minify: 'terser',
  terserOptions: {
    compress: {
      drop_console: true,      // consoleを削除
      drop_debugger: true,     // debuggerを削除
      pure_funcs: ['console.log', 'console.info'],
      passes: 2                // 2回圧縮
    },
    mangle: {
      toplevel: true,          // トップレベルスコープの変数をマングル
    }
  }
}
```

### ビルド最適化の結果

典型的な最適化結果（実際の結果はプロジェクトのサイズによります）:

| 最適化 | 前 | 後 | 改善 |
|---|---|---|---|
| **JSサイズ** | ~800KB | ~350KB | 56% ↓ |
| **Gzipサイズ** | ~280KB | ~120KB | 57% ↓ |
| **初回読み込み** | 2.5s | 1.2s | 52% ↑ |
| **コード可読性** | 完全に可読 | 難読化 | 🔒 安全 |

### 最適化の無効化（デバッグ用）

デバッグのために本番ビルドで`console`文を残したり、難読化を無効にする必要がある場合は、`vite.config.ts`を変更できます:

```typescript
terserOptions: {
  compress: {
    drop_console: false,  // consoleを保持
    drop_debugger: false, // debuggerを保持
  },
  mangle: false           // 難読化を無効化
}
```

---

## 🔍 環境変数

### 開発用 `.env.development`

| 変数 | 説明 | 値の例 | 必須 |
|---|---|---|---|
| `VITE_API_BASE_URL` | APIベースパス | `http://localhost:9898/api` | ✅ |
| `VITE_API_WS_URL` | WebSocket URL | `ws://127.0.0.1:9898/api` | ✅ |

### 本番用 `.env.production`

| 変数 | 説明 | 値の例 | 必須 |
|---|---|---|---|
| `VITE_API_BASE_URL` | 完全なAPI URL | `https://api.yourdomain.com` | ✅ |
| `VITE_API_WS_URL` | WebSocket URL | `wss://api.yourdomain.com` | ✅ |
| `BASE_URL` | フロントエンドのデプロイベースパス | `/` または `/app/` | ❌ |

**注意**:
- `VITE_`で始まるすべての変数はViteによって処理され、フロントエンドコードで利用可能になります。
- `BASE_URL`はルーティングと静的アセットパスに影響し、サブパスにデプロイする際に設定する必要があります。
- 本番環境変数はビルド時にコードにコンパイルされるため、変更後は再ビルドが必要です。

---

## 🐛 一般的な問題と解決策

### 1. ページリフレッシュ時の404エラー

**原因**: Vue RouterはHistoryモードを使用しており、サーバー側のサポートが必要です。

**解決策**: Nginx設定に`try_files $uri $uri/ /index.html;`が含まれていることを確認してください。

### 2. APIリクエストのCORS問題

**原因**: フロントエンドのドメインとバックエンドAPIのドメインが一致しません。

**解決策**:
- オプションA: Nginx経由でAPIをプロキシする（推奨）。
- オプションB: バックエンドでCORSを設定してクロスオリジンリクエストを許可する。

### 3. 静的アセットの404

**原因**: `BASE_URL`の設定が間違っているか、デプロイパスが一致していません。

**解決策**:
- `.env.production`の`BASE_URL`設定を確認してください。
- Nginxで設定されたパスが実際のデプロイパスと一致していることを確認してください。

### 4. CSS/JSファイルの読み込み失敗

**原因**: リソースパスが間違っているか、キャッシュの問題です。

**解決策**:
```bash
# ブラウザのキャッシュをクリアするか、強制リフレッシュ（Ctrl + F5）
# ネットワークパネルで実際のリクエストパスを確認
# distディレクトリが完全にアップロードされていることを確認
```

### 5. ビルドファイルが大きすぎる

**解決策**:
- NginxのGzip圧縮を有効にしてください。
- 不要に大きな依存関係がないか確認してください。
- コード分割と遅延読み込みの設定を検討してください。

### 6. WebSocket接続の失敗

**原因**: NginxプロキシがWebSocketを正しくサポートするように設定されていません。

**解決策**: NginxのAPIプロキシ設定に以下を追加してください:
```nginx
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

### 7. パーミッションによるNginxのファイルアクセス不可

**解決策**:
```bash
# 正しいファイルパーミッションを設定
sudo chown -R www-data:www-data /var/www/cogni-x
sudo chmod -R 755 /var/www/cogni-x
```

---

## 📋 デプロイチェックリスト

デプロイする前に、以下を確認してください:

- [ ] `.env.production`の本番環境変数が正しく設定されている。
- [ ] ビルドコマンド`npm run build`が実行された。
- [ ] `dist`ディレクトリのファイルがサーバーに完全にアップロードされた。
- [ ] Nginx設定ファイルが正しく記述され、テストされている。
- [ ] SSL証明書が設定されている（本番環境では必須）。
- [ ] APIプロキシまたはCORSが設定されている。
- [ ] Gzip圧縮が有効になっている。
- [ ] 静的アセットのキャッシュが設定されている。
- [ ] Vue Routerのリフレッシュが正しく動作する。
- [ ] APIリクエストが成功する。
- [ ] ロギングと監視が設定されている。

---

## 📞 テクニカルサポート

デプロイで問題が発生した場合は、以下を確認してください:
1. Nginxのエラーログ: `/var/log/nginx/cogni-x-error.log`
2. ブラウザコンソールのエラーメッセージ
3. ネットワークリクエストの詳細（ネットワークパネル）

---

## 📄 ライセンス

[プロジェクトのライセンスに従って記入してください]

---

**最終更新日**: 2025-10-13

[English](./README_en-US.md) | [简体中文](./README.md) | [日本語](./README_ja-JP.md)

# Cogni X - Quick Deployment Guide

## 📖 Project Introduction

Cogni X is a modern AI application platform based on Vue 3, offering a variety of AI-powered services.

### Tech Stack

- **Frontend Framework**: Vue 3.5.13 + TypeScript
- **Build Tool**: Vite 6.2.0
- **UI Component Library**: Element Plus 2.9.11
- **State Management**: Pinia 3.0.3
- **Routing**: Vue Router 4.5.0
- **Internationalization**: Vue I18n 9.14.5
- **HTTP Client**: Axios 1.9.0
- **Other Features**: Markdown rendering, code highlighting, 3D model preview, masonry layout

### Main Functional Modules

- 🤖 **AI Chat**: Intelligent conversational interaction
- 🎨 **ComfyUI Workflow**: Visual AI workflow management
- 📁 **Creations Management**: Display and manage user-generated works
- 👤 **Personal Center**: User information and credits management
- 🔐 **User Authentication**: Login, registration, and access control
- 🛠️ **System Management**: Backend administration functions (requires admin privileges)

### Changelog

- **2025-10-28**: Added ComfyUI image scribble component

---

## ⚙️ Environment Requirements

### Development Environment

- **Node.js**: >= 18.x (18.x or 20.x LTS recommended)
- **npm**: >= 9.x or **pnpm**: >= 8.x
- **Operating System**: Windows / macOS / Linux

### Production Environment

- **Nginx**: >= 1.18.x
- **Operating System**: Linux recommended (Ubuntu 20.04+, CentOS 8+, etc.)

---

## 🚀 Local Development

### 1. Clone the Project

```bash
git clone <your-repository-url>
cd vue
```

### 2. Install Dependencies

Using npm:
```bash
npm install
```

Or using pnpm:
```bash
pnpm install
```

### 3. Configure Environment Variables

Create a `.env.development` file in the project root:

```env
# Environment variables for development

# Development API URL
VITE_API_BASE_URL=http://localhost:9898/api 

# WebSocket URL
VITE_API_WS_URL=ws://127.0.0.1:9898/api
```

**Note**:
- `VITE_API_BASE_URL`: The full URL of the API endpoint (including protocol, domain, and port)
- `VITE_API_WS_URL`: The WebSocket connection URL

### 4. Configure Development Proxy

Edit `vite.config.ts` and modify the proxy target address:

```typescript
server: {
  proxy: {
    [env.VITE_API_BASE_URL]: {
      target: 'http://your-backend-api-url', // Change to your actual backend API URL
      changeOrigin: true,
      rewrite: (path) =>
        path.replace(new RegExp('^' + env.VITE_API_BASE_URL), '')
    }
  }
}
```

### 5. Start the Development Server

```bash
npm run dev
```

Access `http://localhost:5173` to see the application.

---

## 📦 Production Build

### 1. Configure Production Environment Variables

Create a `.env.production` file in the project root:

```env
# Environment variables for production

# Production API URL
VITE_API_BASE_URL=http://localhost:9898/api 

# WebSocket URL
VITE_API_WS_URL=ws://127.0.0.1:9898/api
```

**Note**:
- `VITE_API_BASE_URL`: The full production API URL (please replace with your actual backend URL, e.g., `https://api.yourdomain.com`)
- `VITE_API_WS_URL`: The production WebSocket URL (please replace with your actual WebSocket URL, e.g., `wss://api.yourdomain.com`)
- If the frontend is deployed in a subpath (not the root), you need to configure `BASE_URL` (e.g., `BASE_URL=/app/`)

### 2. Run the Build Command

```bash
npm run build
```

After the build is complete, a `dist` directory containing all static assets will be generated in the project root.

**Build Artifacts**:
- 📦 **Code Minification and Obfuscation**: Deep compression and variable name mangling using Terser
- 🗜️ **Gzip Compression**: Automatically generates `.gz` files to reduce transfer size
- 🎯 **Code Splitting**: Smart chunking to improve initial load speed
- 🧹 **Debug Code Removal**: Automatically removes `console.log`, `debugger`, etc.
- 📊 **Build Analysis Report**: Generates `dist/stats.html` to analyze bundle size

### 3. Preview the Build (Optional)

```bash
npm run preview
```

### 4. View Build Analysis Report (Optional)

After the build, you can open `dist/stats.html` to see a detailed bundle analysis:
- Size contribution of each module
- Size after Gzip compression
- Dependency visualization

---

## 🌐 Nginx Deployment Configuration

### Option 1: Deploying to the Domain Root

This applies when deploying the application to the root of a domain, e.g., `https://www.yourdomain.com`

#### 1. Upload Build Files

Upload all files from the `dist` directory to the server, recommended path:

```bash
/var/www/cogni-x/
```

#### 2. Nginx Configuration File

Create or edit the Nginx configuration file `/etc/nginx/sites-available/cogni-x`:

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name yourdomain.com www.yourdomain.com;

    # Force redirect to HTTPS (SSL certificate recommended)
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    # SSL certificate configuration
    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;
    
    # SSL security configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Website root directory
    root /var/www/cogni-x;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript 
               application/json application/javascript application/xml+rss 
               application/rss+xml font/truetype font/opentype 
               application/vnd.ms-fontobject image/svg+xml;

    # Static asset caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # API proxy (if proxying backend API through Nginx)
    location /api/ {
        proxy_pass http://your-backend-server:port/;
        proxy_http_version 1.1;
        
        # Proxy headers
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support (if API uses WebSocket)
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Timeout settings
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Vue Router History mode support
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Logging
    access_log /var/log/nginx/cogni-x-access.log;
    error_log /var/log/nginx/cogni-x-error.log;
}
```

#### 3. Enable Configuration and Restart Nginx

```bash
# Create a symbolic link to enable the configuration
sudo ln -s /etc/nginx/sites-available/cogni-x /etc/nginx/sites-enabled/

# Test configuration for correctness
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

---

### Option 2: Deploying to a Subpath

This applies when deploying the application to a subpath, e.g., `https://www.yourdomain.com/app/`

#### 1. Modify Environment Variables

Edit `.env.production`:

```env
# Environment variables for production

# Production API URL
VITE_API_BASE_URL=https://api.yourdomain.com

# WebSocket URL
VITE_API_WS_URL=wss://api.yourdomain.com

# Subpath deployment configuration
BASE_URL=/app/
```

#### 2. Rebuild

```bash
npm run build
```

#### 3. Upload Build Files

Upload all files from the `dist` directory to:

```bash
/var/www/html/app/
```

#### 4. Nginx Configuration File

```nginx
server {
    listen 443 ssl http2;
    server_name yourdomain.com;

    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;

    # Root directory (if there are other applications)
    root /var/www/html;

    # Subpath application configuration
    location /app {
        alias /var/www/html/app;
        index index.html;
        
        # Vue Router History mode support
        try_files $uri $uri/ /app/index.html;
        
        # Disable caching for HTML
        location ~* /app/index\.html$ {
            add_header Cache-Control "no-cache, no-store, must-revalidate";
        }
        
        # Static asset caching
        location ~* /app/.*\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }

    # API proxy
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

### Option 3: No SSL (HTTP only, not recommended for production)

If you don't have an SSL certificate and are just testing:

```nginx
server {
    listen 80;
    server_name yourdomain.com;

    root /var/www/cogni-x;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Static asset caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public";
    }

    # API proxy
    location /api/ {
        proxy_pass http://your-backend-server:port/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # Vue Router support
    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

---

## 🔧 Advanced Configuration

### 1. Configure a Free SSL Certificate (Let's Encrypt)

```bash
# Install Certbot
sudo apt update
sudo apt install certbot python3-certbot-nginx

# Automatically configure SSL certificate
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Test auto-renewal
sudo certbot renew --dry-run
```

### 2. Performance Optimization

Add the following to your main Nginx configuration file `/etc/nginx/nginx.conf`:

```nginx
http {
    # Enable Gzip compression
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript 
               application/json application/javascript application/xml+rss 
               application/rss+xml font/truetype font/opentype 
               application/vnd.ms-fontobject image/svg+xml;

    # Enable Brotli compression (requires module installation)
    # brotli on;
    # brotli_comp_level 6;
    # brotli_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

    # Client buffer sizes
    client_body_buffer_size 128k;
    client_max_body_size 20m;
    client_header_buffer_size 1k;
    large_client_header_buffers 4 4k;

    # FastCGI cache (if used)
    fastcgi_buffer_size 128k;
    fastcgi_buffers 4 256k;
    fastcgi_busy_buffers_size 256k;

    # Connection timeouts
    keepalive_timeout 65;
    send_timeout 60;
}
```

### 3. Security Hardening

```nginx
server {
    # ... other configurations ...

    # Deny access to hidden files
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }

    # Restrict request methods
    if ($request_method !~ ^(GET|HEAD|POST|PUT|DELETE|OPTIONS)$ ) {
        return 405;
    }

    # Prevent clickjacking
    add_header X-Frame-Options "SAMEORIGIN" always;
    
    # Enable XSS filtering
    add_header X-XSS-Protection "1; mode=block" always;
    
    # Disable content type sniffing
    add_header X-Content-Type-Options "nosniff" always;
    
    # Content Security Policy (adjust as needed)
    add_header Content-Security-Policy "default-src 'self' https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https:; style-src 'self' 'unsafe-inline' https:; img-src 'self' data: https:; font-src 'self' data: https:;" always;
    
    # HSTS (force HTTPS)
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
}
```

### 4. Log Rotation Configuration

Create `/etc/logrotate.d/cogni-x`:

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

## ⚡ Build Optimization

The project is fully configured for code minification, obfuscation, and performance optimization to ensure code security and fast loading in a production environment.

### Optimization Features

#### 1. Code Minification and Obfuscation
- ✅ **Terser Minification**: Deeply minifies JavaScript code.
- ✅ **Variable Mangling**: Obfuscates variable and function names to improve code security.
- ✅ **Comment Removal**: Removes all code comments.
- ✅ **Debug Code Removal**: Automatically removes `console.log`, `debugger`, and other debug statements.

#### 2. Gzip Compression
- ✅ **Auto-generates .gz files**: Files larger than 10KB are automatically compressed with Gzip.
- ✅ **Reduces Transfer Size**: Typically reduces file size by 60-80%.
- ✅ **Keeps Original Files**: Both original and compressed files are retained.

#### 3. Code Splitting
- ✅ **Vue Framework Bundling**: `vue`, `vue-router`, and `pinia` are bundled separately.
- ✅ **UI Framework Bundling**: `element-plus` is bundled separately.
- ✅ **Third-Party Library Splitting**: `axios`, `markdown-it`, `highlight.js`, etc., are bundled separately.
- ✅ **Improves Cache Hit Rate**: Framework code is updated less frequently, leading to better cache utilization.

#### 4. Build Analysis
- ✅ **Visual Report**: Generates a `dist/stats.html` analysis report after the build.
- ✅ **Size Analysis**: View the size contribution of each module.
- ✅ **Dependency Visualization**: Visually inspect module dependencies.

### Configuration

All optimization settings are located in `vite.config.ts`:

```typescript
build: {
  // Terser minification options
  minify: 'terser',
  terserOptions: {
    compress: {
      drop_console: true,      // Remove console
      drop_debugger: true,     // Remove debugger
      pure_funcs: ['console.log', 'console.info'],
      passes: 2                // Compress twice
    },
    mangle: {
      toplevel: true,          // Mangle top-level scope variables
    }
  }
}
```

### Build Optimization Results

Typical optimization results (actual results depend on project size):

| Optimization | Before | After | Improvement |
|---|---|---|---|
| **JS Size** | ~800KB | ~350KB | 56% ↓ |
| **Gzip Size** | ~280KB | ~120KB | 57% ↓ |
| **Initial Load** | 2.5s | 1.2s | 52% ↑ |
| **Code Readability** | Fully readable | Obfuscated | 🔒 Secure |

### Disabling Optimizations (for debugging)

If you need to keep `console` statements or disable obfuscation in a production build for debugging, you can modify `vite.config.ts`:

```typescript
terserOptions: {
  compress: {
    drop_console: false,  // Keep console
    drop_debugger: false, // Keep debugger
  },
  mangle: false           // Disable obfuscation
}
```

---

## 🔍 Environment Variables

### Development `.env.development`

| Variable | Description | Example Value | Required |
|---|---|---|---|
| `VITE_API_BASE_URL` | API base path | `http://localhost:9898/api` | ✅ |
| `VITE_API_WS_URL` | WebSocket URL | `ws://127.0.0.1:9898/api` | ✅ |

### Production `.env.production`

| Variable | Description | Example Value | Required |
|---|---|---|---|
| `VITE_API_BASE_URL` | Full API URL | `https://api.yourdomain.com` | ✅ |
| `VITE_API_WS_URL` | WebSocket URL | `wss://api.yourdomain.com` | ✅ |
| `BASE_URL` | Frontend deployment base path | `/` or `/app/` | ❌ |

**Note**:
- All variables prefixed with `VITE_` will be processed by Vite and made available in the frontend code.
- `BASE_URL` affects routing and static asset paths and must be configured when deploying to a subpath.
- Production environment variables are compiled into the code at build time; a rebuild is required after modification.

---

## 🐛 Common Issues and Solutions

### 1. 404 Error on Page Refresh

**Cause**: Vue Router uses History mode, which requires server-side support.

**Solution**: Ensure your Nginx configuration includes `try_files $uri $uri/ /index.html;`

### 2. API Request CORS Issues

**Cause**: Frontend domain and backend API domain do not match.

**Solutions**:
- Option A: Proxy the API through Nginx (recommended).
- Option B: Configure CORS on the backend to allow cross-origin requests.

### 3. Static Asset 404s

**Cause**: Incorrect `BASE_URL` configuration or mismatched deployment path.

**Solutions**:
- Check the `BASE_URL` configuration in `.env.production`.
- Ensure the path configured in Nginx matches the actual deployment path.

### 4. CSS/JS Files Fail to Load

**Cause**: Incorrect resource path or caching issues.

**Solutions**:
```bash
# Clear browser cache or force refresh (Ctrl + F5)
# Check the Network panel to see the actual request path
# Confirm that the dist directory has been fully uploaded
```

### 5. Build Files are Too Large

**Solutions**:
- Enable Nginx Gzip compression.
- Check for unnecessarily large dependencies.
- Consider configuring code splitting and lazy loading.

### 6. WebSocket Connection Fails

**Cause**: Nginx proxy is not correctly configured for WebSocket support.

**Solution**: Add the following to your Nginx API proxy configuration:
```nginx
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

### 7. Nginx Cannot Access Files Due to Permissions

**Solution**:
```bash
# Set correct file permissions
sudo chown -R www-data:www-data /var/www/cogni-x
sudo chmod -R 755 /var/www/cogni-x
```

---

## 📋 Deployment Checklist

Before deploying, please confirm the following:

- [ ] Production environment variables in `.env.production` are correctly configured.
- [ ] The build command `npm run build` has been executed.
- [ ] Files from the `dist` directory have been fully uploaded to the server.
- [ ] The Nginx configuration file is correctly written and tested.
- [ ] An SSL certificate is configured (required for production).
- [ ] API proxy or CORS is configured.
- [ ] Gzip compression is enabled.
- [ ] Static asset caching is configured.
- [ ] Vue Router refresh works correctly.
- [ ] API requests are successful.
- [ ] Logging and monitoring are configured.

---

## 📞 Technical Support

If you encounter deployment issues, please check:
1. Nginx error logs: `/var/log/nginx/cogni-x-error.log`
2. Browser console error messages
3. Network request details (Network panel)

---

## 📄 License

[Fill in according to your project's license]

---

**Last Updated**: 2025-10-13

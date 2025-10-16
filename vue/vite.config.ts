import { defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'
import viteCompression from 'vite-plugin-compression'
import { visualizer } from 'rollup-plugin-visualizer'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {

  const isProduction = mode === 'production'

  return {
    plugins: [
      vue(),
      // Gzip 压缩插件（生产环境）
      isProduction &&
        viteCompression({
          verbose: true, // 输出压缩成功信息
          disable: false, // 是否禁用
          threshold: 10240, // 只压缩大于 10KB 的文件
          algorithm: 'gzip', // 压缩算法
          ext: '.gz', // 压缩后的文件扩展名
          deleteOriginFile: false // 不删除原文件
        }),
      // 构建分析插件（可选，帮助查看打包体积）
      isProduction &&
        visualizer({
          open: false, // 构建完成后不自动打开报告
          gzipSize: true, // 收集 gzip 大小
          brotliSize: true, // 收集 brotli 大小
          filename: 'dist/stats.html' // 分析报告文件
        })
    ].filter(Boolean), // 过滤掉 false 值
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      port: 8080
    },
    // 构建优化配置
    build: {
      // 输出目录
      outDir: 'dist',
      // 生成静态资源的存放目录
      assetsDir: 'assets',
      // 小于此阈值的导入或引用资源将内联为 base64 编码（单位：字节）
      assetsInlineLimit: 4096,
      // 启用/禁用 CSS 代码拆分
      cssCodeSplit: true,
      // 构建后是否生成 source map 文件
      sourcemap: false,
      // 当设置为 true，构建后将会生成 manifest.json 文件
      manifest: false,
      // chunk 大小警告的限制（单位：KB）
      chunkSizeWarningLimit: 2000,
      // Rollup 打包配置
      rollupOptions: {
        output: {
          // 静态资源分类打包
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
          // 分包策略
          manualChunks: (id) => {
            // 将 node_modules 中的代码单独打包成一个 JS 文件
            if (id.includes('node_modules')) {
              // 将一些大的依赖库单独打包
              if (id.includes('element-plus')) {
                return 'element-plus'
              }
              if (id.includes('vue') || id.includes('@vue')) {
                return 'vue-vendor'
              }
              if (id.includes('axios')) {
                return 'axios'
              }
              if (id.includes('markdown-it')) {
                return 'markdown-it'
              }
              if (id.includes('highlight.js')) {
                return 'highlight'
              }
              // 其他第三方库
              return 'vendor'
            }
          }
        }
      },
      // Terser 压缩配置（生产环境）
      minify: 'terser',
      terserOptions: {
        compress: {
          // 生产环境移除 console
          drop_console: true,
          // 生产环境移除 debugger
          drop_debugger: true,
          // 移除无用代码
          pure_funcs: ['console.log', 'console.info', 'console.debug'],
          // 传递常量以启用更优化
          passes: 2
        },
        mangle: {
          // 混淆变量名
          toplevel: true,
          // 混淆属性（谨慎使用）
          properties: false
        },
        format: {
          // 删除注释
          comments: false
        }
      }
    },
    // 性能优化
    esbuild: {
      // 生产环境移除 console 和 debugger（esbuild 层面）
      drop: isProduction ? ['console', 'debugger'] : []
    }
  }
})

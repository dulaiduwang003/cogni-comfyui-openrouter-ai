<template>
  <div class="markdown-renderer" ref="root" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import { ElNotification } from 'element-plus'

const { t } = useI18n()

interface Props {
  content: string
  options?: {
    breaks?: boolean
    linkify?: boolean
    typographer?: boolean
  }
}

const props = withDefaults(defineProps<Props>(), {
  content: '',
  options: () => ({
    breaks: true,      
    linkify: true,     
    typographer: true 
  })
})

// 配置markdown-it实例
const md: MarkdownIt = new MarkdownIt({
  html: true,        
  xhtmlOut: false,
  breaks: props.options.breaks,
  linkify: props.options.linkify,
  typographer: props.options.typographer,
  highlight: (str: string, lang: string): string => {
    // 代码高亮处理
    if (lang && hljs.getLanguage(lang)) {
      try {
        const highlighted = hljs.highlight(str, { 
          language: lang,
          ignoreIllegals: true 
        }).value
        const copyLabel = t('markdown.copyCode')
        return `<div class="code-block"><pre class="hljs"><code class="language-${lang}">${highlighted}</code></pre><button class="code-copy-btn" aria-label="${copyLabel}" title="${copyLabel}"><svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg></button></div>`
      } catch (err) {
        console.warn('代码高亮失败:', err)
      }
    }
    
    // 无语言指定的代码块
    const escaped = md.utils.escapeHtml(str)
    const copyLabel = t('markdown.copyCode')
    return `<div class="code-block"><pre class="hljs"><code>${escaped}</code></pre><button class="code-copy-btn" aria-label="${copyLabel}" title="${copyLabel}"><svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" stroke-width="2"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg></button></div>`
  }
})

// 自定义渲染规则
md.renderer.rules.table_open = () => '<div class="table-wrapper"><table class="markdown-table">'
md.renderer.rules.table_close = () => '</table></div>'

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!props.content) return ''
  try {
    return md.render(props.content)
  } catch (error) {
    console.error('Markdown渲染失败:', error)
    return `<p class="markdown-error">${t('markdown.renderError')}</p>`
  }
})

const root = ref<HTMLElement | null>(null)

const handleClick = async (e: MouseEvent) => {
  const target = e.target as HTMLElement
  const btn = target.closest?.('.code-copy-btn') as HTMLButtonElement | null
  if (!btn) return
  const block = btn.closest('.code-block') as HTMLElement | null
  if (!block) return
  const codeEl = block.querySelector('pre code') as HTMLElement | null
  if (!codeEl) return
  try {
    const text = codeEl.innerText
    await navigator.clipboard.writeText(text)
    ElNotification.success(t('markdown.copySuccess'))
  } catch (err) {
    ElNotification.error(t('markdown.copyFailed'))
  }
}

onMounted(() => {
  root.value?.addEventListener('click', handleClick)
})

onBeforeUnmount(() => {
  root.value?.removeEventListener('click', handleClick)
})

// 当内容变化时，确保事件仍挂载在容器（委托无需额外处理）
watch(renderedContent, () => {
  // no-op; event delegation handles dynamic content
})
</script>

<style scoped>
.markdown-renderer {
  line-height: 1.55;
  color: var(--el-text-color-primary);
  white-space: normal;
}

/* 标题样式 */
.markdown-renderer :deep(h1),
.markdown-renderer :deep(h2),
.markdown-renderer :deep(h3),
.markdown-renderer :deep(h4),
.markdown-renderer :deep(h5),
.markdown-renderer :deep(h6) {
  margin: 1.1em 0 0.35em 0;
  font-weight: 600;
  line-height: 1.25;
  color: var(--el-text-color-primary);
}

.markdown-renderer :deep(h1) { font-size: 1.8em; }
.markdown-renderer :deep(h2) { font-size: 1.5em; }
.markdown-renderer :deep(h3) { font-size: 1.3em; }
.markdown-renderer :deep(h4) { font-size: 1.1em; }

/* 段落样式 */
.markdown-renderer :deep(p) {
  margin: 0.5em 0;
}

/* 列表样式 */
.markdown-renderer :deep(ul),
.markdown-renderer :deep(ol) {
  margin: 0.6em 0;
  padding-left: 1.5em;
}

.markdown-renderer :deep(li) {
  margin: 0.2em 0;
}

/* 引用块样式 */
.markdown-renderer :deep(blockquote) {
  margin: 0.8em 0;
  padding: 0.6em 0.9em;
  border-left: 4px solid var(--el-color-primary);
  background: var(--el-bg-color-page);
  border-radius: 4px;
}

.markdown-renderer :deep(blockquote p) {
  margin: 0;
}

/* 行内代码样式 */
.markdown-renderer :deep(code:not(.hljs code)) {
  background: var(--el-fill-color-light);
  color: var(--el-color-danger);
  padding: 0.2em 0.4em;
  border-radius: 3px;
  font-size: 0.9em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

/* 代码块包装，加入复制按钮 */
.markdown-renderer :deep(.code-block) {
  position: relative;
}

/* 代码块样式 */
.markdown-renderer :deep(pre) {
  margin: 0.8em 0;
  padding: 1em;

  border-radius: 8px;
  overflow-x: auto;
  border: 1px solid var(--el-border-color-light);
}

.markdown-renderer :deep(pre code) {
  background: none;
  color: inherit;
  padding: 0;
  font-size: 0.9em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

/* 复制按钮 */
.markdown-renderer :deep(.code-copy-btn) {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  background: var(--el-bg-color);
  color: var(--el-text-color-secondary);
  opacity: 0;
  transition: opacity 0.15s ease, background 0.15s ease, color 0.15s ease, border-color 0.15s ease;
  cursor: pointer;
}

.markdown-renderer :deep(.code-block:hover .code-copy-btn) {
  opacity: 1;
}

.markdown-renderer :deep(.code-copy-btn:hover) {
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color-darker);
}

/* 表格样式 */
.markdown-renderer :deep(.table-wrapper) {
  overflow-x: auto;
  margin: 0.8em 0;
}

.markdown-renderer :deep(.markdown-table) {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
}

.markdown-renderer :deep(.markdown-table th),
.markdown-renderer :deep(.markdown-table td) {
  padding: 0.5em 0.9em;
  text-align: left;
  border-bottom: 1px solid var(--el-border-color-light);
}

.markdown-renderer :deep(.markdown-table th) {
  background: var(--el-fill-color-light);
  font-weight: 600;
}

.markdown-renderer :deep(.markdown-table tr:last-child td) {
  border-bottom: none;
}

/* 链接样式 */
.markdown-renderer :deep(a) {
  color: var(--el-color-primary);
  text-decoration: none;
}

.markdown-renderer :deep(a:hover) {
  text-decoration: underline;
}

/* 分割线样式 */
.markdown-renderer :deep(hr) {
  margin: 1.2em 0;
  border: none;
  border-top: 1px solid var(--el-border-color-light);
}

/* 强调样式 */
.markdown-renderer :deep(strong) {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.markdown-renderer :deep(em) {
  font-style: italic;
}

/* 错误提示样式 */
.markdown-renderer :deep(.markdown-error) {
  color: var(--el-color-danger);
  background: var(--el-color-danger-light-9);
  padding: 0.5em;
  border-radius: 4px;
  margin: 0.5em 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .markdown-renderer :deep(pre) {
    padding: 0.8em;
    font-size: 0.85em;
  }
  
  .markdown-renderer :deep(.table-wrapper) {
    font-size: 0.9em;
  }
}
</style> 
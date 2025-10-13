<template>
  <div class="composer" :class="{ 'chat-mode': chatMode }">
    <div class="composer-inner">
      <!-- 建议区域 -->
      <div class="suggestions-container" v-if="!chatMode" :class="{ 'animate-in': isAnimating }">
        <button 
          class="scroll-arrow scroll-arrow-left" 
          @click="scrollLeft"
          :class="{ disabled: !canScrollLeft }"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <polyline points="15,18 9,12 15,6" stroke="currentColor" stroke-width="2"/>
          </svg>
        </button>
        
        <div class="suggestions" ref="suggestionsRef" @scroll="updateScrollState">
          <button 
            v-for="(s, idx) in suggestions"
            :key="idx"
            class="suggestion" 
            :class="{ 'animate-in': isAnimating }"
            :style="{ 'animation-delay': `${idx * 100}ms` }"
            @click="handleSuggestionClick(s.message, $event)"
          >
            {{ s.title }}
            <span v-if="s.subtitle" class="suggestion-subtitle">{{ s.subtitle }}</span>
          </button>
        </div>
        
        <button 
          class="scroll-arrow scroll-arrow-right" 
          @click="scrollRight"
          :class="{ disabled: !canScrollRight }"
        >
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
            <polyline points="9,18 15,12 9,6" stroke="currentColor" stroke-width="2"/>
          </svg>
        </button>
      </div>



      <!-- 附件浮动面板（上传中或已有附件时显示） -->
      <div class="attachments-panel" v-if="uploading || totalAttachmentCount > 0">
        <div class="attachments-list">
          <!-- 图片附件 - 根据模型能力显示 -->
          <div 
            class="attachment-item image" 
            v-for="(url, idx) in attachedImageUrls" 
            :key="'img-' + idx"
            v-if="modelCapabilities.canUploadImages"
          >
            <img :src="url" alt="" class="thumb" />
            <button class="remove" @click="removeImage(idx)" aria-label="移除">×</button>
          </div>

          <!-- PDF附件 - 根据模型能力显示 -->
          <div 
            class="attachment-item pdf" 
            v-for="(file, idx) in attachedPdfFiles" 
            :key="'pdf-' + idx"
            v-if="modelCapabilities.canUploadPdf"
          >
            <span class="badge">PDF</span>
            <a class="name" :href="getPdfOpenUrl(file.url)" target="_blank" rel="noopener">{{ file.filename }}</a>
            <button class="remove" @click="removePdf(idx)" aria-label="移除">×</button>
          </div>

          <!-- 音频附件 - 根据模型能力显示 -->
          <div 
            class="attachment-item audio" 
            v-for="(file, idx) in attachedAudioFiles" 
            :key="'aud-' + idx"
            v-if="modelCapabilities.canUploadAudio"
          >
            <span class="badge">AUDIO</span>
            <audio class="audio-player" :src="file.url" controls></audio>
            <button class="remove" @click="removeAudio(idx)" aria-label="移除">×</button>
          </div>
        </div>
        <!-- 进度遮罩 -->
        <div v-if="uploading" class="attachments-upload-overlay">
          <div class="progress-text">{{ Math.round(uploadProgress) }}%</div>
          <div class="progress-bar">
            <div class="progress-bar-inner" :style="{ width: uploadProgress + '%' }"></div>
          </div>
        </div>
        <button class="clear-all" @click="resetAttachments">{{ t('chat.composer.clearAll') }}</button>
      </div>

      <div class="composer-bar">
        <div class="composer-left">
          <!-- 生成图片按钮 - 根据模型输出能力显示 -->
          <button 
            class="chip" 
            :class="{ active: imageGenEnabledComputed }" 
            @click="toggleImageGen" 
            :aria-pressed="imageGenEnabledComputed"
            v-if="modelCapabilities.canGenerateImages"
          >
            <el-icon><Picture /></el-icon>
            {{ t('chat.composer.generateImage') }}
          </button>
        </div>

        <div class="composer-input">
          <textarea
            class="message-input"
            :placeholder="t('chat.composer.placeholder')"
            rows="1"
            ref="messageInput"
            v-model="inputMessage"
            @input="autoResize"
            @keydown="handleKeyDown"
            maxlength="10000"
          ></textarea>
          <div class="input-footer">
            <div class="character-count" :class="{ 'approaching-limit': isApproachingLimit, 'at-limit': isAtLimit }">
              {{ inputMessage.length }}/10000
            </div>
          </div>
          <div class="tools">
         
            <!-- 附件上传按钮 - 根据模型能力显示 -->
            <button 
              class="icon-btn" 
              aria-label="Attach" 
              @click="openFilePicker"
              v-if="modelCapabilities.canUploadImages || modelCapabilities.canUploadPdf || modelCapabilities.canUploadAudio"
            >
              <img class="icon" :src="attachUrl" alt="" aria-hidden="true" />
            </button>
            <input
              ref="fileInputEl"
              type="file"
              :accept="fileAcceptTypes"
              multiple
              style="display: none"
              @change="onFilesSelected"
            />
            <div class="websearch" v-if="modelCapabilities.canWebSearch">
              <span class="icon-wrap">
                <img class="icon" :src="globeUrl" alt="" aria-hidden="true" />
              </span>
              <span class="label">{{ t('chat.composer.webSearch') }}</span>
              <label class="switch">
                <input type="checkbox" :checked="webSearchEnabledComputed" @change="onWebSearchChange" />
                <span class="slider"></span>
              </label>
            </div>
          </div>
          <button 
            class="send-btn" 
            @click="props.isTyping ? stopReply() : sendMessage()" 
            :disabled="isSendDisabled"
            :class="{ 'stop-mode': props.isTyping }"
          >
            <span v-if="props.isTyping">■</span>
            <span v-else>↑</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import attachUrl from '@/assets/svg/attach.svg'
import globeUrl from '@/assets/svg/globe.svg'
import { ElNotification } from 'element-plus'
import { ossApi } from '@/api/oss/oss'
import type { SubmitMessageApi } from '@/api/chat/types'
import { Picture } from '@element-plus/icons-vue'
import type { ModelItem } from '@/api/chat/types'
import { InputTypeEnum, OutputTypeEnum } from '@/enums/chat'

const { t } = useI18n()

// 定义 props
interface Props {
  chatMode?: boolean
  isTyping?: boolean
  disabled?: boolean
  webSearchEnabled?: boolean
  imageGenEnabled?: boolean
  selectedModel?: ModelItem | null
}

const props = withDefaults(defineProps<Props>(), {
  chatMode: false,
  isTyping: false,
  disabled: false,
  webSearchEnabled: undefined,
  imageGenEnabled: undefined,
  selectedModel: null
})

// 定义事件
const emit = defineEmits<{
  'send-message': [payload: { content: string; imageUrls?: string[]; pdfFiles?: SubmitMessageApi.PdfFileDto[]; audioFiles?: SubmitMessageApi.AudioFileDto[] }]
  'stop-reply': []
  'new-chat': []
  'websearch-change': [enabled: boolean]
  'imagegen-change': [enabled: boolean]
}>()

const messageInput = ref<HTMLTextAreaElement | null>(null)
const inputMessage = ref('')

// 字符计数相关
const isApproachingLimit = computed(() => inputMessage.value.length > 8000) // 80%警告
const isAtLimit = computed(() => inputMessage.value.length >= 10000)

// 附件相关
const fileInputEl = ref<HTMLInputElement | null>(null)
const uploading = ref(false)
const uploadProgress = ref(0)
let uploadProgressTimer: any = null
const attachedImageUrls = ref<string[]>([])
const attachedPdfFiles = ref<SubmitMessageApi.PdfFileDto[]>([])
const attachedAudioFiles = ref<SubmitMessageApi.AudioFileDto[]>([])

const totalAttachmentCount = computed(() => attachedImageUrls.value.length + attachedPdfFiles.value.length + attachedAudioFiles.value.length)

const openFilePicker = () => {
  if (uploading.value) return
  fileInputEl.value?.click()
}

// 根据模型能力计算界面元素的显示状态
const modelCapabilities = computed(() => {
  if (!props.selectedModel) {
    return {
      canUploadImages: true,
      canUploadPdf: true,
      canUploadAudio: true,
      canGenerateImages: false,
      canWebSearch: false,
      acceptAllFiles: false
    }
  }

  const model = props.selectedModel
  const inputTypes = model.inputType || []
  const outputTypes = model.outputType || []

  // 检查是否支持文件类型
  const hasFileInput = inputTypes.includes(InputTypeEnum.FILE)
  const hasImageInput = inputTypes.includes(InputTypeEnum.IMAGE)
  const hasAudioInput = inputTypes.includes(InputTypeEnum.AUDIO)
  
  // 检查输出能力
  const canGenerateImages = outputTypes.includes(OutputTypeEnum.IMAGE)

  return {
    // 支持图片上传
    canUploadImages: hasImageInput || hasFileInput,
    // 支持PDF上传
    canUploadPdf: model.supportPdf || hasFileInput,
    // 支持音频上传
    canUploadAudio: hasAudioInput || hasFileInput,
    // 支持图片生成
    canGenerateImages,
    // 支持联网搜索
    canWebSearch: model.supportWeb,
    // 接受所有文件类型
    acceptAllFiles: hasFileInput
  }
})

// 根据模型能力动态生成文件选择器的accept属性
const fileAcceptTypes = computed(() => {
  // 支持所有文件类型
  if (modelCapabilities.value.acceptAllFiles) {
    return ''
  }
  
  // 根据能力组装accept类型
  const acceptTypes: string[] = []
  if (modelCapabilities.value.canUploadImages) {
    // 只支持 jpg、jpeg、png 格式
    acceptTypes.push('image/jpeg', 'image/jpg', 'image/png')
  }
  if (modelCapabilities.value.canUploadPdf) {
    acceptTypes.push('application/pdf')
  }
  if (modelCapabilities.value.canUploadAudio) {
    acceptTypes.push('audio/wav', 'audio/mp3')
  }
  
  return acceptTypes.join(',')
})



// 更新文件选择逻辑
const onFilesSelected = async (e: Event) => {
  const input = e.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  const currentCount = totalAttachmentCount.value
  const remaining = Math.max(0, 8 - currentCount)
  const files = Array.from(input.files)
  if (remaining === 0) {
    ElNotification.warning('最多可上传 8 个文件')
    input.value = ''
    return
  }
  const selected = files.slice(0, remaining)

  try {
    uploading.value = true
    uploadProgress.value = 0
    if (uploadProgressTimer) {
      clearInterval(uploadProgressTimer)
      uploadProgressTimer = null
    }
    // 模拟上传进度至 90%
    uploadProgressTimer = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value = Math.min(90, uploadProgress.value + Math.random() * 8 + 2)
      }
    }, 300)

    const uploads = selected.map(async (file) => {
      // 判断类型：图片、pdf、音频
      const isImage = file.type.startsWith('image/')
      const isPdf = file.type === 'application/pdf'
      const isAudio = file.type.startsWith('audio/') || file.type === 'audio/wav' || file.type === 'audio/mp3' 

      // 检查模型能力
      const capabilities = modelCapabilities.value
      
      if (isImage && !capabilities.canUploadImages) {
        return Promise.reject(new Error('当前模型不支持图片上传'))
      }
      
      // 检查图片格式，只支持 jpg、jpeg、png
      if (isImage && capabilities.canUploadImages) {
        const supportedImageTypes = ['image/jpeg', 'image/jpg', 'image/png']
        if (!supportedImageTypes.includes(file.type)) {
          return Promise.reject(new Error('图片格式不支持，仅支持 JPG、JPEG、PNG 格式'))
        }
      }
      
      if (isPdf && !capabilities.canUploadPdf) {
        return Promise.reject(new Error('当前模型不支持PDF文件'))
      }
      
      if (isAudio && !capabilities.canUploadAudio) {
        return Promise.reject(new Error('当前模型不支持音频文件'))
      }

      // 如果不支持所有文件类型，检查是否为支持的类型
      if (!capabilities.acceptAllFiles && !isImage && !isPdf && !isAudio) {
        return Promise.reject(new Error('不支持的文件类型'))
      }

      const url = await ossApi.uploadFile({ file })

      if (isImage) {
        attachedImageUrls.value.push(url)
      } else if (isPdf) {
        attachedPdfFiles.value.push({ url, filename: file.name })
      } else if (isAudio) {
        attachedAudioFiles.value.push({ url })
      }
    })

    await Promise.all(uploads)
    // 完成：冲到 100%，短暂停留后复位
    if (uploadProgressTimer) {
      clearInterval(uploadProgressTimer)
      uploadProgressTimer = null
    }
    uploadProgress.value = 100
    ElNotification.success('文件上传成功')
    setTimeout(() => {
      uploading.value = false
      uploadProgress.value = 0
    }, 400)
  } catch (err) {
    if (uploadProgressTimer) {
      clearInterval(uploadProgressTimer)
      uploadProgressTimer = null
    }
    uploadProgress.value = 0
    uploading.value = false
    ElNotification.error((err as Error).message || '文件上传失败')
  }

  // 清空 input 以便可重复选择相同文件
  input.value = ''
}

// 受控/非受控处理
const internalWebSearch = ref(false)
const isControlled = computed(() => typeof props.webSearchEnabled === 'boolean')
const webSearchEnabledComputed = computed({
  get: () => (isControlled.value ? (props.webSearchEnabled as boolean) : internalWebSearch.value),
  set: (val: boolean) => {
    if (isControlled.value) {
      emit('websearch-change', val)
    } else {
      internalWebSearch.value = val
      emit('websearch-change', val)
    }
  }
})

// 生成图片开关：受控/非受控
const internalImageGen = ref(false)
const isImageGenControlled = computed(() => typeof props.imageGenEnabled === 'boolean')
const imageGenEnabledComputed = computed({
  get: () => (isImageGenControlled.value ? (props.imageGenEnabled as boolean) : internalImageGen.value),
  set: (val: boolean) => {
    if (isImageGenControlled.value) {
      emit('imagegen-change', val)
    } else {
      internalImageGen.value = val
      emit('imagegen-change', val)
    }
  }
})

const toggleImageGen = () => {
  imageGenEnabledComputed.value = !imageGenEnabledComputed.value
}

watch(() => props.webSearchEnabled, (val) => {
  if (isControlled.value && typeof val === 'boolean') {
    // sync visual when parent drives
  }
})

const onWebSearchChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  webSearchEnabledComputed.value = !!target.checked
}

// 建议滚动相关
const suggestionsRef = ref<HTMLDivElement | null>(null)
const canScrollLeft = ref(false)
const canScrollRight = ref(false)

// 生成建议数据
const suggestions = computed(() => [
  { title: t('chat.composer.suggestions.creativeWriting'), subtitle: t('chat.composer.suggestions.creativeWritingSubtitle'), message: t('chat.composer.suggestions.creativeWritingMessage') },
  { title: t('chat.composer.suggestions.mathProblem'), subtitle: t('chat.composer.suggestions.mathProblemSubtitle'), message: t('chat.composer.suggestions.mathProblemMessage') },
  { title: t('chat.composer.suggestions.wordGame'), subtitle: t('chat.composer.suggestions.wordGameSubtitle'), message: t('chat.composer.suggestions.wordGameMessage') },
  { title: t('chat.composer.suggestions.poetry'), subtitle: t('chat.composer.suggestions.poetrySubtitle'), message: t('chat.composer.suggestions.poetryMessage') },
  { title: t('chat.composer.suggestions.finance'), subtitle: t('chat.composer.suggestions.financeSubtitle'), message: t('chat.composer.suggestions.financeMessage') },
  { title: t('chat.composer.suggestions.programming'), subtitle: t('chat.composer.suggestions.programmingSubtitle'), message: t('chat.composer.suggestions.programmingMessage') },
  { title: t('chat.composer.suggestions.health'), subtitle: t('chat.composer.suggestions.healthSubtitle'), message: t('chat.composer.suggestions.healthMessage') },
  { title: t('chat.composer.suggestions.travel'), subtitle: t('chat.composer.suggestions.travelSubtitle'), message: t('chat.composer.suggestions.travelMessage') },
  { title: t('chat.composer.suggestions.learning'), subtitle: t('chat.composer.suggestions.learningSubtitle'), message: t('chat.composer.suggestions.learningMessage') },
  { title: t('chat.composer.suggestions.career'), subtitle: t('chat.composer.suggestions.careerSubtitle'), message: t('chat.composer.suggestions.careerMessage') }
])

const autoResize = () => {
  const el = messageInput.value
  if (!el) return
  el.style.height = 'auto'
  const maxCss = getComputedStyle(el).maxHeight
  const maxPx = maxCss && maxCss !== 'none' ? parseFloat(maxCss) : Number.POSITIVE_INFINITY
  const target = Math.min(el.scrollHeight, Number.isFinite(maxPx) ? maxPx : el.scrollHeight)
  el.style.height = `${target}px`
}

const isSendDisabled = computed(() => {
  if (props.isTyping) return false
  const hasText = !!inputMessage.value.trim()
  return uploading.value || (!hasText && totalAttachmentCount.value === 0)
})

const resetAttachments = () => {
  attachedImageUrls.value = []
  attachedPdfFiles.value = []
  attachedAudioFiles.value = []
}

const removeImage = (index: number) => {
  attachedImageUrls.value.splice(index, 1)
}

const removePdf = (index: number) => {
  attachedPdfFiles.value.splice(index, 1)
}

const removeAudio = (index: number) => {
  attachedAudioFiles.value.splice(index, 1)
}

const sendMessage = () => {
  const content = inputMessage.value.trim()
  if (!content && totalAttachmentCount.value === 0) {
    return
  }

  // 添加发送动画
  const inputEl = messageInput.value
  const composerEl = document.querySelector('.composer-bar')
  
  if (inputEl) {
    inputEl.classList.add('sending')
    setTimeout(() => {
      inputEl.classList.remove('sending')
    }, 300)
  }
  
  if (composerEl) {
    ;(composerEl as HTMLElement).classList.add('sending')
    setTimeout(() => {
      ;(composerEl as HTMLElement).classList.remove('sending')
    }, 300)
  }

  emit('send-message', {
    content,
    imageUrls: attachedImageUrls.value.length ? attachedImageUrls.value : undefined,
    pdfFiles: attachedPdfFiles.value.length ? attachedPdfFiles.value : undefined,
    audioFiles: attachedAudioFiles.value.length ? attachedAudioFiles.value : undefined
  })

  inputMessage.value = ''
  resetAttachments()
  nextTick(() => autoResize())
}

const stopReply = () => {
  emit('stop-reply')
}

const handleKeyDown = (e: KeyboardEvent) => {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

const handleSuggestionClick = (suggestion: string, _event: MouseEvent) => {
  // 将建议内容填入输入框
  inputMessage.value = suggestion
  
  // 触发自动调整大小
  nextTick(() => {
    autoResize()
    // 聚焦到输入框
    messageInput.value?.focus()
  })
  
  // 自动发送（可选）
  // sendMessage()
}

// 滚动相关方法
const updateScrollState = () => {
  const el = suggestionsRef.value
  if (!el) return
  
  canScrollLeft.value = el.scrollLeft > 0
  canScrollRight.value = el.scrollLeft < el.scrollWidth - el.clientWidth
}

const scrollLeft = () => {
  const el = suggestionsRef.value
  if (!el) return
  
  const scrollAmount = el.clientWidth * 0.8
  el.scrollBy({ left: -scrollAmount, behavior: 'smooth' })
}

const scrollRight = () => {
  const el = suggestionsRef.value
  if (!el) return
  
  const scrollAmount = el.clientWidth * 0.8
  el.scrollBy({ left: scrollAmount, behavior: 'smooth' })
}

// 动画控制
const isAnimating = ref(true)

// 构造 PDF 打开链接，默认降低缩放比例，避免显示过大
const getPdfOpenUrl = (url: string) => {
  try {
    const hasHash = url.includes('#')
    // 使用通用的 zoom=100（多数浏览器内置PDF查看器支持），避免页面过大
    return hasHash ? `${url}&zoom=100` : `${url}#zoom=100`
  } catch {
    return url
  }
}

onMounted(() => {
  nextTick(() => {
    autoResize()
    updateScrollState()
    // 开屏动画延迟
    setTimeout(() => {
      isAnimating.value = false
    }, 100)
  })
})
</script>

<style scoped>
.composer {
  position: relative;
  padding: 8px 0;
  background: transparent;
}

/* 当作为聊天界面底部输入框时的样式 */
.composer.chat-mode {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 12px;
  padding: 6px 20px 8px;
  background: linear-gradient(180deg, rgba(0,0,0,0) 0%, var(--el-bg-color-page) 30%);
}

.composer-inner {
  max-width: 1000px;
  margin: 0 auto;
}

/* 建议容器 */
.suggestions-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  position: relative;
  opacity: 1;
  transform: translateY(0);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.suggestions-container.animate-in {
  opacity: 0;
  transform: translateY(20px);
}

.suggestions-container::before,
.suggestions-container::after {
  content: '';
  position: absolute;
  top: 4px;
  bottom: 4px;
  width: 20px;
  z-index: 1;
  pointer-events: none;
  transition: opacity 0.3s ease;
}

.suggestions-container::before {
  left: 44px;
  background: linear-gradient(to right, var(--el-bg-color-page) 0%, rgba(255, 255, 255, 0) 100%);
}

.suggestions-container::after {
  right: 44px;
  background: linear-gradient(to left, var(--el-bg-color-page) 0%, rgba(255, 255, 255, 0) 100%);
}

/* 深色主题下的渐变效果 */
html[class^="theme-dark"] .suggestions-container::before,
html[class*=" theme-dark"] .suggestions-container::before {
  background: linear-gradient(to right, var(--el-bg-color-page) 0%, rgba(0, 0, 0, 0) 100%);
}

html[class^="theme-dark"] .suggestions-container::after,
html[class*=" theme-dark"] .suggestions-container::after {
  background: linear-gradient(to left, var(--el-bg-color-page) 0%, rgba(0, 0, 0, 0) 100%);
}

.suggestions {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  scroll-behavior: smooth;
  flex: 1;
  padding: 4px 0;
  /* 隐藏滚动条 */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.suggestions::-webkit-scrollbar {
  display: none;
}

/* 滚动箭头 */
.scroll-arrow {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color);
  color: var(--el-text-color-regular);
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.scroll-arrow:hover:not(.disabled) {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
  color: var(--el-color-primary);
}

.scroll-arrow.disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.suggestion {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  padding: 12px 14px;
  color: var(--el-text-color-primary);
  font-size: 13px;
  font-weight: 500;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-height: 60px;
  min-width: 160px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  transform: translateY(0) scale(1);
  opacity: 1;
  animation: suggestion-enter 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
}

.suggestion.animate-in {
  opacity: 0;
  transform: translateY(30px) scale(0.9);
  animation: none;
}

@keyframes suggestion-enter {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.suggestion:hover {
  border-color: var(--el-color-primary-light-7);
  background: var(--el-color-primary-light-9);
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12), 0 4px 12px rgba(0, 0, 0, 0.08);
}

.suggestion:active {
  transform: translateY(0) scale(0.98);
  transition: all 0.1s ease;
}

/* 波纹效果 */
.ripple-effect {
  position: absolute;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.3);
  transform: scale(0);
  animation: ripple-animation 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  pointer-events: none;
}

@keyframes ripple-animation {
  0% {
    transform: scale(0);
    opacity: 1;
  }
  100% {
    transform: scale(2);
    opacity: 0;
  }
}

.suggestion-subtitle {
  font-size: 11px;
  color: var(--el-text-color-secondary);
  font-weight: 400;
  line-height: 1.3;
}

/* 底部功能按钮 */
.bottom-actions {
  margin-bottom: 16px;
}

.action-group {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  color: var(--el-text-color-regular);
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  border-color: var(--el-color-primary-light-7);
  background: var(--el-color-primary-light-9);
  color: var(--el-text-color-primary);
}

.action-btn svg {
  flex-shrink: 0;
}

.composer-bar {
  background: var(--el-bg-color);
  border: none;
  border-radius: 24px;
  padding: 12px 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  transform: scale(1);
}

.composer-bar.sending {
  transform: scale(0.98);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06), 0 1px 4px rgba(0, 0, 0, 0.02);
}

/* 深色主题下的阴影效果 */
html[class^="theme-dark"] .composer-bar,
html[class*=" theme-dark"] .composer-bar {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25), 0 2px 8px rgba(0, 0, 0, 0.15);
}

.composer-left {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  height: 26px;
  padding: 0 10px;
  font-size: 12px;
  font-weight: 500;
  border-radius: 999px;
  background: var(--el-fill-color);
  border: 1px solid var(--el-border-color-light);
  color: var(--el-text-color-regular);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  line-height: 1;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* 让 el-icon 跟随文字颜色并统一尺寸 */
.chip .el-icon {
  font-size: 14px;
  line-height: 1;
}

.chip:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
  color: var(--el-text-color-primary);
}

.chip.active {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
}

.chip .icon {
  width: 14px;
  height: 14px;
}

.composer-input {
  display: grid;
  grid-template-columns: 1fr auto;
  grid-template-rows: auto auto;
  align-items: center;
  gap: 4px;
}

.message-input {
  width: 100%;
  min-height: 40px;
  border-radius: 12px;
  border: none;
  padding: 8px 12px;
  font-size: 14px;
  background: transparent;
  color: var(--el-text-color-primary);
  line-height: 20px;
  resize: none;
  outline: none;
  box-shadow: none;
  overflow: auto;
  max-height: 180px;
  -ms-overflow-style: none;
  scrollbar-width: none;
  grid-column: 1 / 3;
  transition: all 0.3s ease;
  opacity: 1;
  transform: translateY(0);
}

.message-input.sending {
  opacity: 0.3;
  transform: translateY(-2px);
}

.message-input::-webkit-scrollbar {
  display: none;
}

.message-input:focus {
  border-color: transparent;
  background: transparent;
}

.message-input::placeholder {
  color: var(--el-text-color-placeholder);
}

.tools {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  grid-column: 1 / 2;
}

.icon-btn {
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--el-text-color-secondary);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.icon-btn:nth-child(2) {
  margin-left: -4px;
}

.icon {
  width: 16px;
  height: 16px;
  display: block;
  filter: none;
}

/* Dark mode icon filter */
html[class^="theme-dark"] .icon,
html[class*=" theme-dark"] .icon {
  filter: brightness(0) invert(1);
}

html[class^="theme-light"] .icon,
html[class*=" theme-light"] .icon {
  filter: none;
}

.websearch {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 26px;
}

.icon-wrap {
  width: 16px;
  height: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.websearch .label {
  line-height: 1;
}

.switch {
  position: relative;
  width: 32px;
  height: 16px;
  display: inline-block;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  inset: 0;
  background: var(--el-fill-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 999px;
  transition: all 0.2s ease;
}

.slider::before {
  content: '';
  position: absolute;
  width: 12px;
  height: 12px;
  left: 2px;
  top: 1px;
  background: var(--el-color-white);
  border: 1px solid var(--el-border-color);
  border-radius: 50%;
  transition: transform 0.2s ease;
}

.switch input:checked + .slider {
  background: var(--el-color-primary-light-7, rgba(64, 158, 255, 0.25));
  border-color: var(--el-color-primary-light-5, #a3c8ff);
}

.switch input:checked + .slider::before {
  transform: translateX(14px);
  border-color: var(--el-color-primary);
}

.send-btn {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  border: 1px solid var(--el-color-primary);
  background: var(--el-color-primary);
  color: var(--el-color-white);
  font-size: 16px;
  grid-column: 2 / 3;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: scale(1);
}

.send-btn:active:not(:disabled) {
  transform: scale(0.95);
  transition: all 0.1s ease;
}


.send-btn:hover:not(:disabled) {
  background: var(--el-color-primary-dark-2);
  border-color: var(--el-color-primary-dark-2);
}

.send-btn:disabled {
  background: var(--el-fill-color-light);
  border-color: var(--el-border-color);
  color: var(--el-text-color-placeholder);
  cursor: not-allowed;
  opacity: 0.7;
}

.send-btn.stop-mode {
  background: var(--el-color-danger);
  border-color: var(--el-color-danger);
  color: white;
}

.send-btn.stop-mode:hover {
  background: var(--el-color-danger-light-3);
  border-color: var(--el-color-danger-light-3);
}

@media (max-width: 960px) {
  .suggestions-container {
    gap: 8px;
  }
  
  .suggestions-container::before {
    left: 36px;
    width: 16px;
  }
  
  .suggestions-container::after {
    right: 36px;
    width: 16px;
  }
  
  .scroll-arrow {
    width: 28px;
    height: 28px;
  }
  
  .suggestion {
    min-width: 140px;
  }
  
  .composer.chat-mode {
    padding: 6px 16px 8px;
  }
}

@media (max-width: 720px) {
  .suggestions-container {
    gap: 6px;
  }
  
  .suggestions-container::before {
    left: 30px;
    width: 12px;
  }
  
  .suggestions-container::after {
    right: 30px;
    width: 12px;
  }
  
  .scroll-arrow {
    width: 24px;
    height: 24px;
  }
  
  .suggestion {
    min-width: 120px;
    min-height: 50px;
    padding: 8px 12px;
    font-size: 12px;
  }
  
  .composer.chat-mode {
    padding: 6px 12px 8px;
  }
  
  .composer-bar {
    padding: 8px 12px;
    border-radius: 20px;
  }
  
  .message-input {
    font-size: 16px; /* 防止iOS缩放 */
  }
}

.attachments-panel {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 12px;
  padding: 10px 12px;
  margin: 10px 0 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
  position: relative;
}

.attachments-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.attachment-item {
  position: relative;
  border: 1px solid var(--el-border-color-lighter);
  background: var(--el-bg-color);
  border-radius: 10px;
  padding: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.attachment-item.image .thumb {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}

.attachment-item .remove {
  position: absolute;
  top: -8px;
  right: -8px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 1px solid var(--el-border-color);
  background: var(--el-bg-color);
  color: var(--el-text-color-regular);
  cursor: pointer;
  line-height: 18px;
  text-align: center;
}

.attachments-upload-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  backdrop-filter: blur(2px);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  gap: 8px;
}

.attachments-upload-overlay .progress-text {
  color: #fff;
  font-weight: 600;
}

.attachments-upload-overlay .progress-bar {
  width: 60%;
  height: 6px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.25);
  overflow: hidden;
}

.attachments-upload-overlay .progress-bar-inner {
  height: 100%;
  background: var(--el-color-primary);
  width: 0%;
  transition: width 0.2s ease;
}

.attachment-item.pdf .badge,
.attachment-item.audio .badge {
  font-size: 10px;
  font-weight: 600;
  color: var(--el-color-primary);
  background: var(--el-color-primary-light-9);
  border-radius: 6px;
  padding: 2px 6px;
}

.attachment-item.pdf .name {
  max-width: 220px;
  color: var(--el-text-color-primary);
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
}

.attachment-item.audio .audio-player {
  width: 180px;
}

.clear-all {
  margin-left: auto;
  background: transparent;
  color: var(--el-text-color-secondary);
  border: none;
  cursor: pointer;
  padding: 4px 6px;
}

.input-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 4px;
  padding-right: 8px;
}

.character-count {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  font-weight: 400;
}

.character-count.approaching-limit {
  color: var(--el-color-warning);
}

.character-count.at-limit {
  color: var(--el-color-danger);
}
</style> 
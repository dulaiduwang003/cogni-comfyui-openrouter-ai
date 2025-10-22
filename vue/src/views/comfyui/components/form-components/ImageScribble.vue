<template>
  <div class="image-scribble">
    <el-form-item
      :prop="fieldKey"
      :required="formItem.required"
    >
      <template #label>
        <div class="label-with-tooltip">
          <span>{{ formItem.tips }}</span>
          <el-tooltip v-if="formItem.template" :content="formItem.template" placement="top">
            <el-icon class="tooltip-icon">
              <QuestionFilled />
            </el-icon>
          </el-tooltip>
        </div>
      </template>
      
      <div class="scribble-container">
        <div v-if="!modelValue" class="upload-trigger-wrapper">
          <el-upload
            :key="uploadKey"
            :http-request="customUpload"
            accept="image/*"
            :limit="1"
            :before-upload="beforeUpload"
            :show-file-list="false"
            :disabled="uploading || !canUpload"
            class="image-uploader"
          >
            <div class="upload-trigger">
              <el-icon class="upload-icon">
                <Picture />
              </el-icon>
              <div class="upload-text">{{ t('comfyui.imageScribble.uploadTrigger') }}</div>
              <div class="upload-hint">{{ t('comfyui.imageScribble.uploadHint', { size: getFileSizeLimit() }) }}</div>
            </div>
          </el-upload>
        </div>
        
        <div v-if="modelValue" class="scribble-preview" @mouseenter="showOverlay = true" @mouseleave="showOverlay = false">
          <img :src="modelValue" :alt="t('comfyui.imageScribble.dialogTitle')" class="preview-image" />
          
          <div v-show="showOverlay" class="preview-overlay">
            <el-button type="primary" @click="openDialog">{{ t('comfyui.imageScribble.reScribble') }}</el-button>
          </div>
        </div>
      </div>
    </el-form-item>

    <el-dialog
      v-model="dialogVisible"
      :title="t('comfyui.imageScribble.dialogTitle')"
      width="90%"
      style="max-width: 800px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="scribble-dialog"
      @close="handleDialogClose"
    >
      <div class="dialog-content">
        <div v-if="uploading" class="upload-progress-overlay">
          <div class="progress-text">{{ Math.round(uploadProgress) }}%</div>
        </div>
        
        <div v-if="uploadedImageUrl" class="canvas-wrapper">
          <div class="canvas-area" ref="canvasContainer">
            <canvas
              ref="canvas"
              @mousedown="startDrawing"
              @mousemove="draw"
              @mouseup="stopDrawing"
              @mouseleave="stopDrawing"
              @touchstart="handleTouchStart"
              @touchmove="handleTouchMove"
              @touchend="stopDrawing"
            ></canvas>
          </div>
          
          <div class="canvas-toolbar">
            <div class="button-group">
              <div 
                class="custom-button" 
                :class="{ active: drawMode === 'draw' }"
                @click="setDrawMode('draw')"
              >
                <el-icon><Edit /></el-icon>
                <span>{{ t('comfyui.imageScribble.draw') }}</span>
              </div>
              <div 
                class="custom-button" 
                :class="{ active: drawMode === 'erase' }"
                @click="setDrawMode('erase')"
              >
                <el-icon><Delete /></el-icon>
                <span>{{ t('comfyui.imageScribble.erase') }}</span>
              </div>
            </div>
            
            <div class="brush-size-control">
              <span class="control-label">{{ t('comfyui.imageScribble.brushSize') }}</span>
              <el-slider v-model="brushSize" :min="5" :max="50" :show-tooltip="false" style="width: 120px" />
              <span class="control-value">{{ brushSize }}</span>
            </div>
            
            <div class="toolbar-actions">
              <div class="custom-button" @click="clearCanvas">
                <el-icon><RefreshLeft /></el-icon>
                <span>{{ t('comfyui.imageScribble.clear') }}</span>
              </div>
              <div class="custom-button" @click="cancelScribble">
                <el-icon><Close /></el-icon>
                <span>{{ t('comfyui.imageScribble.cancel') }}</span>
              </div>
              <div class="custom-button primary" @click="confirmScribble" :class="{ loading: submitting }">
                <el-icon v-if="!submitting"><Check /></el-icon>
                <span>{{ submitting ? t('comfyui.imageScribble.uploading') : t('comfyui.imageScribble.confirm') }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElFormItem, ElUpload, ElIcon, ElNotification, ElTooltip, ElSlider, ElDialog, ElLoading } from 'element-plus'
import { Picture, QuestionFilled, Edit, Delete, RefreshLeft, Close, Check } from '@element-plus/icons-vue'
import type { UploadRequestOptions } from 'element-plus'
import { ossApi } from '@/api/oss/oss'
import { useAuthStore } from '@/stores'
import { useI18n } from 'vue-i18n'

interface Props {
  formItem: {
    inputs: string
    nodeKey: string
    tips: string
    type: string
    required: boolean
    options: string
    template?: string | null
    size?: number
  }
  modelValue: string
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:model-value': [value: string]
}>()

const { t } = useI18n()

const canvas = ref<HTMLCanvasElement>()
const canvasContainer = ref<HTMLDivElement>()
const ctx = ref<CanvasRenderingContext2D | null>(null)
const maskCanvas = ref<HTMLCanvasElement | null>(null)
const maskCtx = ref<CanvasRenderingContext2D | null>(null)
const lastPos = ref({ x: 0, y: 0 })

const dialogVisible = ref(false)
const uploadedImageUrl = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)
const submitting = ref(false)
const showOverlay = ref(false)
const canUpload = ref(true)
const uploadKey = ref(0)

const isDrawing = ref(false)
const drawMode = ref<'draw' | 'erase'>('draw')
const brushSize = ref(20)
const scribbleColor = ref('#353535') // 涂抹颜色，根据背景自动调整
const backgroundImage = new Image()

const fieldKey = computed(() => {
  return `${props.formItem.nodeKey}_${props.formItem.inputs}`
})

const getFileSizeLimit = () => {
  return props.formItem.size || 10
}

const beforeUpload = (file: File) => {
  const sizeLimit = getFileSizeLimit()
  const isValidSize = file.size / 1024 / 1024 < sizeLimit
  
  if (!isValidSize) {
    ElNotification.error({
      title: t('common.error'),
      message: t('comfyui.imageScribble.messages.fileSizeExceeded', { size: sizeLimit })
    })
    return false
  }
  
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElNotification.error({
      title: t('common.error'),
      message: t('comfyui.imageScribble.messages.onlyImageAllowed')
    })
    return false
  }
  
  return true
}

const customUpload = async (options: UploadRequestOptions) => {
  try {
    const { file } = options
    
    // 打开弹窗
    dialogVisible.value = true
    
    uploading.value = true
    uploadProgress.value = 0
    
    const progressInterval = setInterval(() => {
      if (uploadProgress.value < 90) {
        uploadProgress.value += Math.random() * 10
      }
    }, 200)
    
    const reader = new FileReader()
    reader.onload = (e) => {
      clearInterval(progressInterval)
      uploadProgress.value = 100
      
      setTimeout(() => {
        uploading.value = false
        uploadProgress.value = 0
        uploadedImageUrl.value = e.target?.result as string
        
        nextTick(() => {
          initCanvas()
        })
        
        ElNotification.success({
          title: t('common.success'),
          message: t('comfyui.imageScribble.messages.uploadSuccess')
        })
      }, 500)
    }
    
    reader.onerror = () => {
      clearInterval(progressInterval)
      uploading.value = false
      uploadProgress.value = 0
      
      ElNotification.error({
        title: t('common.error'),
        message: t('comfyui.imageScribble.messages.uploadFailed')
      })
    }
    
    reader.readAsDataURL(file as File)
    
  } catch (error) {
    console.error('Upload error:', error)
    uploading.value = false
    uploadProgress.value = 0
    
    ElNotification.error({
      title: t('common.error'),
      message: t('comfyui.imageScribble.messages.uploadFailed')
    })
  }
}

const openDialog = () => {
  dialogVisible.value = true
  if (props.modelValue) {
    uploadedImageUrl.value = props.modelValue
    nextTick(() => {
      initCanvas()
    })
  }
}

const handleDialogClose = () => {

  setTimeout(() => {
    if (!props.modelValue) {
      uploadedImageUrl.value = ''
      if (ctx.value && canvas.value) {
        ctx.value.clearRect(0, 0, canvas.value.width, canvas.value.height)
      }
      maskCanvas.value = null
      maskCtx.value = null

      uploadKey.value++
    }
  }, 300)
}

const initCanvas = () => {
  if (!canvas.value || !uploadedImageUrl.value) return
  
  backgroundImage.onload = () => {
    if (!canvas.value || !canvasContainer.value) return
    
    const maxWidth = Math.min(800, canvasContainer.value.clientWidth - 40)
    const scale = maxWidth / backgroundImage.width
    const scaledHeight = backgroundImage.height * scale
    
    canvas.value.width = maxWidth
    canvas.value.height = scaledHeight

    maskCanvas.value = document.createElement('canvas')
    maskCanvas.value.width = maxWidth
    maskCanvas.value.height = scaledHeight
    maskCtx.value = maskCanvas.value.getContext('2d')
    
    ctx.value = canvas.value.getContext('2d', { willReadFrequently: true })
    if (!ctx.value) return
    
    ctx.value.drawImage(backgroundImage, 0, 0, canvas.value.width, canvas.value.height)
    
    // 根据背景图片的亮度自动调整涂抹颜色
    updateScribbleColor()
  }
  
  backgroundImage.crossOrigin = 'anonymous'
  backgroundImage.src = uploadedImageUrl.value
}

const setDrawMode = (mode: 'draw' | 'erase') => {
  drawMode.value = mode
}

// 计算图片平均亮度，返回值 0-255
const calculateAverageBrightness = (imageData: ImageData): number => {
  const data = imageData.data
  let totalBrightness = 0
  const pixelCount = data.length / 4
  
  for (let i = 0; i < data.length; i += 4) {
    const r = data[i]
    const g = data[i + 1]
    const b = data[i + 2]
    // 使用感知亮度公式（人眼对绿色更敏感）
    const brightness = 0.299 * r + 0.587 * g + 0.114 * b
    totalBrightness += brightness
  }
  
  return totalBrightness / pixelCount
}

// 根据背景亮度设置涂抹颜色
const updateScribbleColor = () => {
  if (!ctx.value || !canvas.value) return
  
  try {
    const imageData = ctx.value.getImageData(0, 0, canvas.value.width, canvas.value.height)
    const avgBrightness = calculateAverageBrightness(imageData)
    
    // 如果背景较暗（亮度 < 128），使用浅色涂抹；否则使用深色
    if (avgBrightness < 128) {
      scribbleColor.value = '#CCCCCC' // 浅灰色，用于深色背景
    } else {
      scribbleColor.value = '#353535' // 深灰色，用于浅色背景
    }
  } catch (error) {
    console.error('Failed to calculate brightness:', error)
    scribbleColor.value = '#353535' // 默认使用深色
  }
}

const getPosition = (e: MouseEvent | Touch) => {
  if (!canvas.value) return { x: 0, y: 0 }
  
  const rect = canvas.value.getBoundingClientRect()
  return {
    x: e.clientX - rect.left,
    y: e.clientY - rect.top
  }
}

const startDrawing = (e: MouseEvent) => {
  if (!ctx.value) return
  
  isDrawing.value = true
  const pos = getPosition(e)
  lastPos.value = pos
  
  const radius = brushSize.value / 2
  
  if (drawMode.value === 'draw') {
    drawContinuousCircle(ctx.value, pos.x, pos.y, pos.x, pos.y, radius, scribbleColor.value, 'source-over')
  } else {
    drawContinuousCircle(ctx.value, pos.x, pos.y, pos.x, pos.y, radius, 'transparent', 'destination-out')
  }
  
  if (maskCtx.value) {
    if (drawMode.value === 'draw') {
      drawContinuousCircle(maskCtx.value, pos.x, pos.y, pos.x, pos.y, radius, 'white', 'source-over')
    } else {
      drawContinuousCircle(maskCtx.value, pos.x, pos.y, pos.x, pos.y, radius, 'black', 'source-over')
    }
  }
}

const drawContinuousCircle = (ctx: CanvasRenderingContext2D, fromX: number, fromY: number, toX: number, toY: number, radius: number, color: string, compositeOperation: GlobalCompositeOperation) => {
  ctx.save()
  ctx.fillStyle = color
  ctx.globalCompositeOperation = compositeOperation
  ctx.imageSmoothingEnabled = false
  
  const distance = Math.sqrt((toX - fromX) ** 2 + (toY - fromY) ** 2)
  
  if (distance === 0) {
    ctx.beginPath()
    ctx.arc(toX, toY, radius, 0, Math.PI * 2)
    ctx.fill()
  } else {
    const steps = Math.ceil(distance / (radius * 0.5))
    
    for (let i = 0; i <= steps; i++) {
      const t = i / steps
      const x = fromX + (toX - fromX) * t
      const y = fromY + (toY - fromY) * t
      
      ctx.beginPath()
      ctx.arc(x, y, radius, 0, Math.PI * 2)
      ctx.fill()
    }
  }
  
  ctx.restore()
}

const draw = (e: MouseEvent) => {
  if (!isDrawing.value || !ctx.value || !lastPos.value) return
  
  const pos = getPosition(e)
  const radius = brushSize.value / 2
  
  if (drawMode.value === 'draw') {
    drawContinuousCircle(ctx.value, lastPos.value.x, lastPos.value.y, pos.x, pos.y, radius, scribbleColor.value, 'source-over')
  } else {
    drawContinuousCircle(ctx.value, lastPos.value.x, lastPos.value.y, pos.x, pos.y, radius, 'transparent', 'destination-out')
  }

  if (maskCtx.value) {
    if (drawMode.value === 'draw') {
      drawContinuousCircle(maskCtx.value, lastPos.value.x, lastPos.value.y, pos.x, pos.y, radius, 'white', 'source-over')
    } else {
      drawContinuousCircle(maskCtx.value, lastPos.value.x, lastPos.value.y, pos.x, pos.y, radius, 'black', 'source-over')
    }
  }

  lastPos.value = pos
}

const stopDrawing = () => {
  if (!ctx.value) return
  isDrawing.value = false
  ctx.value.closePath()
  
  if (maskCtx.value) {
    maskCtx.value.closePath()
  }
}

const handleTouchStart = (e: TouchEvent) => {
  e.preventDefault()
  if (e.touches.length > 0) {
    const touch = e.touches[0]
    const mouseEvent = new MouseEvent('mousedown', {
      clientX: touch.clientX,
      clientY: touch.clientY
    })
    startDrawing(mouseEvent)
  }
}

const handleTouchMove = (e: TouchEvent) => {
  e.preventDefault()
  if (e.touches.length > 0) {
    const touch = e.touches[0]
    const mouseEvent = new MouseEvent('mousemove', {
      clientX: touch.clientX,
      clientY: touch.clientY
    })
    lastPos.value = getPosition(touch)
    draw(mouseEvent)
  }
}

const clearCanvas = () => {
  if (!canvas.value || !ctx.value) return
  
  ctx.value.drawImage(backgroundImage, 0, 0, canvas.value.width, canvas.value.height)

  if (maskCtx.value && maskCanvas.value) {
    maskCtx.value.clearRect(0, 0, maskCanvas.value.width, maskCanvas.value.height)
  }
}

const cancelScribble = () => {
  dialogVisible.value = false

  setTimeout(() => {
    uploadedImageUrl.value = ''
    if (ctx.value && canvas.value) {
      ctx.value.clearRect(0, 0, canvas.value.width, canvas.value.height)
    }
    maskCanvas.value = null
    maskCtx.value = null

    uploadKey.value++
  }, 300)
}

const confirmScribble = async () => {
  if (!canvas.value || !ctx.value || !maskCanvas.value || !maskCtx.value) {
    ElNotification.error({
      title: t('common.error'),
      message: t('comfyui.imageScribble.messages.canvasNotInitialized')
    })
    return
  }

  const loading = ElLoading.service({
    lock: true,
    text: t('comfyui.imageScribble.processing'),
    background: 'rgba(0, 0, 0, 0.7)'
  })
  
  try {
    submitting.value = true
    
    const originalWidth = backgroundImage.width
    const originalHeight = backgroundImage.height
    
    const originalCanvas = document.createElement('canvas')
    originalCanvas.width = originalWidth
    originalCanvas.height = originalHeight
    const originalCtx = originalCanvas.getContext('2d', { willReadFrequently: true })
    if (!originalCtx) {
      throw new Error('无法创建原图画布上下文')
    }
    originalCtx.drawImage(backgroundImage, 0, 0, originalWidth, originalHeight)
    
    const finalMaskCanvas = document.createElement('canvas')
    finalMaskCanvas.width = originalWidth
    finalMaskCanvas.height = originalHeight
    const finalMaskCtx = finalMaskCanvas.getContext('2d', { willReadFrequently: true })
    if (!finalMaskCtx) {
      throw new Error('无法创建蒙版画布上下文')
    }
    finalMaskCtx.imageSmoothingEnabled = false
    finalMaskCtx.clearRect(0, 0, finalMaskCanvas.width, finalMaskCanvas.height)
    finalMaskCtx.drawImage(maskCanvas.value, 0, 0, originalWidth, originalHeight)
    
    const originalImageData = originalCtx.getImageData(0, 0, originalCanvas.width, originalCanvas.height)
    const maskImageData = finalMaskCtx.getImageData(0, 0, finalMaskCanvas.width, finalMaskCanvas.height)
    
    
    const originalData = originalImageData.data
    const maskData = maskImageData.data
    
    let paintedPixels = 0

    
    for (let i = 0; i < maskData.length; i += 4) {
      const r = maskData[i]
      const g = maskData[i + 1]
      const b = maskData[i + 2]
      const alpha = maskData[i + 3]
      
      if (r > 200 && g > 200 && b > 200 && alpha > 10) {
        maskData[i] = 0
        maskData[i + 1] = 0
        maskData[i + 2] = 0
        maskData[i + 3] = 0
        paintedPixels++
      } else {
        maskData[i] = 255
        maskData[i + 1] = 255
        maskData[i + 2] = 255
        maskData[i + 3] = 255
      }
    }
    
    
    
    const outputCanvas = document.createElement('canvas')
    const outputCtx = outputCanvas.getContext('2d')
    if (!outputCtx) throw new Error('无法创建输出画布')
    
    outputCanvas.width = originalCanvas.width
    outputCanvas.height = originalCanvas.height
    
    const outputImageData = outputCtx.createImageData(outputCanvas.width, outputCanvas.height)
    const outputData = outputImageData.data
    
    for (let i = 0; i < outputData.length; i += 4) {
      outputData[i] = originalData[i]
      outputData[i + 1] = originalData[i + 1]
      outputData[i + 2] = originalData[i + 2]
      outputData[i + 3] = maskData[i + 3]
    }
    
    outputCtx.putImageData(outputImageData, 0, 0)
    
    
    
    const blob = await new Promise<Blob>((resolve, reject) => {
      outputCanvas.toBlob(
        (b) => {
          if (b) {
            resolve(b)
          } else {
            reject(new Error('转换失败'))
          }
        },
        'image/png',
        1.0
      )
    })
    
    const file = new File([blob], 'inpainting-image.png', { type: 'image/png' })
    const ossUrl = await ossApi.uploadFile({ file })
    
    emit('update:model-value', ossUrl)
    
    uploadedImageUrl.value = ''
    dialogVisible.value = false
    
    ElNotification.success({
      title: t('common.success'),
      message: t('comfyui.imageScribble.messages.scribbleComplete')
    })
    
  } catch (error) {
    console.error('Confirm scribble error:', error)
    ElNotification.error({
      title: t('common.error'),
      message: t('comfyui.imageScribble.messages.processingFailed')
    })
  } finally {
    submitting.value = false
    loading.close()
  }
}

const authStore = useAuthStore()
canUpload.value = authStore.isLoggedIn

onMounted(() => {
  const unwatch = authStore.$subscribe(() => {
    canUpload.value = authStore.isLoggedIn
  })
  
  return () => {
    unwatch()
  }
})
</script>

<style scoped>
.image-scribble {
  width: 100%;
}

.scribble-container {
  position: relative;
}

.upload-trigger-wrapper {
  display: inline-block;
}

.dialog-content {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-wrapper {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-uploader :deep(.el-upload) {
  display: block;
  width: 180px;
  height: 180px;
  border: 2px dashed var(--el-border-color);
  border-radius: 8px;
  transition: all 0.3s ease;
  background: transparent;
  cursor: pointer;
}

.image-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 16px;
}

.upload-icon {
  font-size: 32px;
  color: var(--el-color-primary);
  margin-bottom: 8px;
}

.upload-text {
  font-size: 13px;
  color: var(--el-text-color-regular);
  text-align: center;
  margin-bottom: 6px;
}

.upload-hint {
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  text-align: center;
  line-height: 1.4;
}

.upload-progress-overlay {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  height: 300px;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  z-index: 20;
}

.progress-text {
  font-size: 24px;
  font-weight: 600;
  color: #ffffff;
}

.canvas-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.canvas-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  margin-top: 16px;
  flex-wrap: wrap;
  width: 100%;
  max-width: 1000px;
}

.brush-size-control {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.control-label {
  font-size: 13px;
  color: var(--el-text-color-regular);
  white-space: nowrap;
}

.control-value {
  font-size: 13px;
  color: var(--el-text-color-primary);
  font-weight: 500;
  min-width: 24px;
}

.toolbar-actions {
  display: flex;
  gap: 6px;
  margin-left: auto;
  flex-shrink: 0;
}

.canvas-area {
  border: 2px solid var(--el-border-color);
  border-radius: 8px;
  overflow: hidden;
  background: var(--el-bg-color);
  display: inline-block;
}

canvas {
  display: block;
  cursor: crosshair;
  max-width: 100%;
}

.scribble-preview {
  width: 180px;
  height: 180px;
  border-radius: 8px;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid var(--el-border-color);
}

.scribble-preview:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.scribble-preview:hover .preview-overlay {
  opacity: 1;
}

.label-with-tooltip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.tooltip-icon {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  cursor: help;
  transition: color 0.3s ease;
}

.tooltip-icon:hover {
  color: var(--el-color-primary);
}

.el-form-item {
  margin-bottom: 0;
}

.el-form-item :deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
}

.scribble-dialog :deep(.el-dialog__body) {
  padding: 20px 30px;
}

.scribble-dialog :deep(.el-dialog__header) {
    padding: 20px 30px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }
  
  /* 自定义按钮样式 */
  .button-group {
    display: flex;
    gap: 0;
    border-radius: 4px;
    overflow: hidden;
    flex-shrink: 0;
  }
  
  .button-group .custom-button {
    border-radius: 0;
    border-right: 1px solid var(--el-border-color);
  }
  
  .button-group .custom-button:last-child {
    border-right: none;
  }
  
  .custom-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 6px 12px;
    font-size: 13px;
    border-radius: 4px;
    border: 1px solid var(--el-border-color);
    background-color: var(--el-fill-color-light);
    color: var(--el-text-color-primary);
    cursor: pointer;
    transition: all 0.2s ease;
    user-select: none;
    white-space: nowrap;
  }
  
  .custom-button:hover {
    background-color: var(--el-fill-color);
    border-color: var(--el-border-color-hover);
    color: var(--el-color-primary);
  }
  
  .custom-button:active {
    background-color: var(--el-fill-color-dark);
  }
  
  .custom-button.active {
    background-color: var(--el-color-primary);
    border-color: var(--el-color-primary);
    color: #ffffff;
  }
  
  .custom-button.active:hover {
    background-color: var(--el-color-primary-light-3);
    border-color: var(--el-color-primary-light-3);
  }
  
  .custom-button.primary {
    background-color: var(--el-color-primary);
    border-color: var(--el-color-primary);
    color: #ffffff;
  }
  
  .custom-button.primary:hover {
    background-color: var(--el-color-primary-light-3);
    border-color: var(--el-color-primary-light-3);
  }
  
  .custom-button.loading {
    cursor: not-allowed;
    opacity: 0.6;
  }
  
  .custom-button .el-icon {
    font-size: 14px;
  }
  </style>


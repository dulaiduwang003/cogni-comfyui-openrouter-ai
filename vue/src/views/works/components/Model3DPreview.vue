<template>
  <div class="model-3d-preview">
    <div v-if="!props.modelSrc" class="no-model-placeholder">
      <el-icon size="48" color="#ccc"><Warning /></el-icon>
      <p>{{ t('works.model3D.noModel') }}</p>
    </div>
    <div v-else class="model-container">
      <div v-if="isLoading" class="loading-placeholder">
        <el-icon class="loading-icon" size="24"><Loading /></el-icon>
        <p>{{ t('works.model3D.loading') }}</p>
      </div>
      <div 
        ref="modelContainer"
        class="model-viewer-container"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { Loading, Warning } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// Props
const props = defineProps({
  modelSrc: {
    type: String,
    required: true
  },
  alt: {
    type: String,
    default: ''
  },
  autoRotate: {
    type: Boolean,
    default: true
  },
  cameraControls: {
    type: Boolean,
    default: true
  },
  environmentImage: {
    type: String,
    default: 'neutral'
  },
  exposure: {
    type: Number,
    default: 1
  },
  shadowIntensity: {
    type: Number,
    default: 1
  },
  toneMapping: {
    type: String,
    default: 'neutral'
  }
})

// Emits
const emit = defineEmits(['load', 'error'])

// Refs
const modelContainer = ref(null)
const isLoading = ref(true)

// 组件挂载后注册 model-viewer
onMounted(async () => {
  if (!props.modelSrc) {
    isLoading.value = false
    return
  }

  try {
    console.log('Model3DPreview mounted with modelSrc:', props.modelSrc)
    
    // 动态导入 model-viewer
    await import('@google/model-viewer')
    
    await nextTick()
    
    // 创建 model-viewer 元素
    const modelViewer = document.createElement('model-viewer')
    modelViewer.src = props.modelSrc
    modelViewer.alt = props.alt || t('works.model3D.alt')
    modelViewer.setAttribute('auto-rotate', props.autoRotate.toString())
    modelViewer.setAttribute('camera-controls', props.cameraControls.toString())
    modelViewer.setAttribute('environment-image', props.environmentImage)
    modelViewer.setAttribute('exposure', props.exposure.toString())
    modelViewer.setAttribute('shadow-intensity', props.shadowIntensity.toString())
    modelViewer.setAttribute('tone-mapping', props.toneMapping)
    modelViewer.setAttribute('loading', 'lazy')
    
    // 添加事件监听器
    modelViewer.addEventListener('load', (event) => {
      console.log('Model loaded successfully')
      isLoading.value = false
      emit('load', event)
    })
    
    modelViewer.addEventListener('error', (event) => {
      console.error('Model loading error:', event)
      isLoading.value = false
      emit('error', event)
    })
    
    // 添加到容器
    if (modelContainer.value) {
      modelContainer.value.appendChild(modelViewer)
    }
    
  } catch (error) {
    console.error('Failed to load model-viewer:', error)
    isLoading.value = false
    emit('error', error)
  }
})
</script>

<style scoped>
.model-3d-preview {
  width: 100%;
  height: 100%;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background-color: black;
}

.model-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.model-viewer-container {
  width: 100%;
  height: 100%;
}

.model-viewer-container :deep(model-viewer) {
  width: 100%;
  height: 100%;
  background-color: transparent;
  --progress-bar-color: var(--el-color-primary);
  --progress-bar-height: 2px;
}

.loading-placeholder,
.no-model-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.loading-icon {
  animation: rotate 2s linear infinite;
  margin-bottom: 8px;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-placeholder p,
.no-model-placeholder p {
  margin: 8px 0 0 0;
  text-align: center;
}
</style> 
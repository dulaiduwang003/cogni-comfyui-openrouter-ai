<template>
  <div class="right-panel">
    <div class="header">
      <div class="workflow-header-info">
        <div class="workflow-avatar">
          <img :src="item?.url" :alt="item?.name" class="avatar-img" />
        </div>
        <div class="workflow-details">
          <div class="workflow-name">{{ item?.name || t('comfyui.panel.title') }}</div>
          <div class="workflow-meta">
            <span class="meta-item">
              <span class="meta-label">{{ t('comfyui.panel.category') }}</span>
              <span class="meta-value">{{ item?.categoryName || '-' }}</span>
            </span>
            <span class="meta-separator">|</span>
            <span class="meta-item">
              <span class="meta-label">{{ t('comfyui.panel.aiGenerated') }}</span>
            </span>
          </div>
        </div>
      </div>
      <button @click="$emit('close')" class="close-btn">×</button>
    </div>
    <div class="content" v-if="item">
      <div class="workflow-info">
        <div class="workflow-intro">
          <div class="intro-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9V7L15 1H5C3.89 1 3 1.89 3 3V21C3 22.11 3.89 23 5 23H19C20.11 23 21 22.11 21 21V9M19 9H14V4H5V21H19V9Z" fill="currentColor"/>
            </svg>
          </div>
          <div class="intro-content">
            <p class="intro-text">{{ item.description || t('comfyui.panel.configPrompt') }}</p>
          </div>
        </div>
      </div>

      <!-- 表单渲染器 -->
      <div class="form-section">
        <ComfyuiFormRenderer
          ref="formRendererRef"
          :workflow-id="item.workflowId"
          @submit="handleFormSubmit"
          :hide-submit-button="false"
        />
      </div>
    </div>




  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification } from 'element-plus'
import ComfyuiFormRenderer from './ComfyuiFormRenderer.vue'
import { comfyuiTaskApi } from '@/api/workflow-task/workflow-task'
import type { SubmitTaskApi } from '@/api/workflow-task/types'
import { WorkflowFormTypeEnum } from '@/enums'
import { useTaskWebSocketStore } from '@/stores/modules/taskWebsocket'
import { useUserStore } from '@/stores/modules/user'

const { t } = useI18n()

interface WorkflowItem {
  workflowId: number
  name: string
  description: string
  url: string
  categoryName: string
}

interface Props {
  item: WorkflowItem | null
}

defineProps<Props>()
const emit = defineEmits<{
  close: []
}>()

const formRendererRef = ref<InstanceType<typeof ComfyuiFormRenderer> | null>(null)

const webSocketStore = useTaskWebSocketStore()

const userStore = useUserStore()

const handleFormSubmit = async (submitData: Record<string, any>) => {
  console.log('Form submitted:', submitData)

  try {
    // 获取工作流接口配置
    const workflowInterface = await comfyuiTaskApi.reqGetWorkflowResultModelflowsInterface({
      workflowId: submitData.workflowId
    })

    // 根据 formContainer 和用户输入的 formData 构造 NodeContainer
    const nodeContainer: SubmitTaskApi.Params['nodeContainer'] = 
      workflowInterface.formContainer.map(formItem => {
        const fieldKey = `${formItem.nodeKey}_${formItem.inputs}`
        const nodeValue = submitData.formData[fieldKey] || ''
        
        // 判断是否为上传类型（包括图片涂抹）
        const isUpload = [
          WorkflowFormTypeEnum.IMAGE_UPLOAD, 
          WorkflowFormTypeEnum.IMAGE_SCRIBBLE, 
          WorkflowFormTypeEnum.AUDIO_UPLOAD, 
          WorkflowFormTypeEnum.VIDEO_UPLOAD
        ].includes(formItem.type)
        
        return {
          nodeKey: formItem.nodeKey,
          inputs: formItem.inputs,
          nodeValue: String(nodeValue),
          isUpload
        }
      })

    const submitParams: SubmitTaskApi.Params = {
      workflowId: submitData.workflowId,
      nodeContainer: nodeContainer
    }

    // 调用提交接口
    await comfyuiTaskApi.reqSubmitComfyuiTask(submitParams)

    if (formRendererRef.value) {
      formRendererRef.value.resetForm()
    }

    ElNotification.success({
      title: t('comfyui.panel.submitSuccess'),
      message: t('comfyui.panel.submitSuccessMessage'),
      duration: 5000,
      showClose: true
    })

    // 自动打开任务面板让用户查看进度
    setTimeout(() => {
      webSocketStore.triggerTaskPanelOpen()
    }, 500)

    // 刷新用户积分
    userStore.refreshUserCredits()

    // 主动刷新任务列表
    if (webSocketStore.isConnected) {
      // 延迟一小段时间再刷新
      setTimeout(() => {
        webSocketStore.refreshTasks()
      }, 1000)
    }

    console.log('任务提交成功，等待WebSocket推送更新')

    emit('close')

  } catch (error) {
    console.error('Submit task failed:', error)
  } finally {
    // 重置loading状态
    if (formRendererRef.value) {

      formRendererRef.value.setSubmitting(false)
    }
  }
}


</script>

<style scoped>
.right-panel {
  width: 420px;
  height: 100%;
  flex-shrink: 0;
  background: var(--el-bg-color);
  display: flex;
  flex-direction: column;
  border-radius: 12px 0 0 12px;
  box-shadow: -2px 0 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0;
  padding: 16px 20px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  flex-shrink: 0;
  position: relative;
  z-index: 1;
}

.workflow-header-info {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  flex: 1;
}

.workflow-avatar {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  overflow: hidden;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.workflow-details {
  flex: 1;
  min-width: 0;
  padding-top: 2px;
}

.workflow-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 280px;
}

.workflow-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--el-text-color-regular);
  line-height: 1.4;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 2px;
}

.meta-label {
  color: var(--el-text-color-secondary);
}

.meta-value {
  color: var(--el-color-primary);
  font-weight: 500;
}

.meta-separator {
  color: var(--el-border-color);
  margin: 0 4px;
}

.close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: var(--el-text-color-regular);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s ease;
  flex-shrink: 0;
  margin-top: 8px;
}

.close-btn:hover {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

.content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  min-height: 0;
}

.workflow-info {
  padding: 20px 20px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-extra-light);
  flex-shrink: 0;
}

.workflow-intro {
  display: flex;
  align-items: flex-start;
  gap: 10px;


  border-radius: 6px;
 
}

.intro-icon {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  color: var(--el-color-primary);
  margin-top: 2px;
}

.intro-content {
  flex: 1;
}

.intro-text {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-regular);
  line-height: 1.6;
}

.form-section {
  flex: 1;

  display: flex;
  flex-direction: column;
  background: var(--el-bg-color);
  min-height: 0;

}

@media (max-width: 1200px) {
  .right-panel {
    width: 380px;
    height: 100%;
  }
}

@media (max-width: 768px) {
  .right-panel {
    width: 100%;
    height: 100%;
    border-radius: 0;
    box-shadow: none;
  }

  .header {
    padding: 14px 16px;
  }

  .workflow-name {
    font-size: 15px;
    max-width: 240px;
  }

  .workflow-avatar {
    width: 42px;
    height: 42px;
  }

  .workflow-info {
    padding: 14px 16px;
  }

  .form-section {
    padding: 14px 16px 18px 16px;
  }
}
</style>
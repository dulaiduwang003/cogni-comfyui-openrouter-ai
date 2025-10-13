<script setup lang="ts">
import { ElDialog, ElForm, ElFormItem, ElInput, ElButton, ElNotification, type FormRules } from 'element-plus'
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import emitter, { OPEN_REDEMPTION_DIALOG } from '@/utils/eventBusUtil'
import { redemptionCodeApi } from '@/api/redemption-code/redemption-code'
import { useUserStore } from '@/stores/modules/user'

const { t } = useI18n()

const visible = ref(false)
const loading = ref(false)
const formRef = ref()
const userStore = useUserStore()

const redemptionForm = reactive({
  code: ''
})

const rules = computed<FormRules>(() => ({
  code: [
    { required: true, message: t('redemption.pleaseEnterCode'), trigger: 'blur' },
    { min: 6, message: t('redemption.codeMinLength'), trigger: 'blur' }
  ]
}))

const handleClose = () => {
  visible.value = false
  // 重置表单
  redemptionForm.code = ''
  formRef.value?.resetFields()
}

const openDialog = () => {
  visible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true
      try {
        // 调用兑换码API
        await redemptionCodeApi.reqRedeemCode({ 
          code: redemptionForm.code 
        })
        
        ElNotification.success({
          title: t('redemption.success'),
          message: t('redemption.successMessage')
        })
        
        // 兑换成功后刷新用户积分信息
        await userStore.fetchUserCredits()
        
        handleClose()
      } catch (error: any) {
        console.log(error)
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  emitter.on(OPEN_REDEMPTION_DIALOG, openDialog)
})

onUnmounted(() => {
  emitter.off(OPEN_REDEMPTION_DIALOG, openDialog)
})
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="t('redemption.title')"
    width="400px"
    :before-close="handleClose"
    align-center
    class="redemption-dialog"
    :append-to-body="true"
    :lock-scroll="true"
    :close-on-click-modal="false"
  >
    <div class="redemption-content">
      <el-form
        ref="formRef"
        :model="redemptionForm"
        :rules="rules"
        @submit.prevent
        label-position="top"
      >
        <el-form-item :label="t('redemption.enterCode')" prop="code">
          <el-input
            v-model="redemptionForm.code"
            :placeholder="t('redemption.enterCode')"
            size="large"
            maxlength="20"
            @keyup.enter="handleSubmit"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleSubmit"
            class="submit-button"
          >
            {{ loading ? t('redemption.redeeming') : t('redemption.redeem') }}
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="tips">
        <p class="tip-text">
          <span class="tip-icon">💡</span>
          {{ t('redemption.tips') }}
        </p>
        <ul class="tip-list">
          <li>{{ t('redemption.tip1') }}</li>
          <li>{{ t('redemption.tip2') }}</li>
          <li>{{ t('redemption.tip3') }}</li>
        </ul>
      </div>
    </div>
  </el-dialog>
</template>

<style scoped>
.redemption-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

.redemption-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.submit-button {
  width: 100%;
  border-radius: 8px;
  font-weight: 600;
}

.tips {
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid var(--el-color-primary);
}

.tip-text {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px 0;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.tip-icon {
  font-size: 16px;
}

.tip-list {
  margin: 0;
  padding-left: 20px;
  color: var(--el-text-color-regular);
}

.tip-list li {
  margin-bottom: 6px;
  font-size: 12px;
  line-height: 1.5;
}

.tip-list li:last-child {
  margin-bottom: 0;
}

/* 输入框主题样式 */
:deep(.el-input) {
  --el-input-bg-color: var(--el-bg-color);
}

:deep(.el-input__wrapper) {
  background-color: var(--el-bg-color);
  box-shadow: 0 0 0 1px var(--el-border-color) inset;
}

:deep(.el-input__wrapper.is-focus) {
  background-color: var(--el-bg-color);
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}


</style> 
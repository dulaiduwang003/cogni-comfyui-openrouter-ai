<template>
  <div class="redemption-container">
    <el-card shadow="never" class="content-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>{{ t('system.redemption.title') }}</span>
          <div class="header-actions">
            <el-button type="primary" @click="openCreateDialog">
              <el-icon><Plus /></el-icon>
              {{ t('system.redemption.create') }}
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="query.keyword"
          :placeholder="t('system.redemption.search.keyword')"
          clearable
          style="width: 240px"
          @keyup.enter="loadPage(1)"
          @clear="onSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="query.status" :placeholder="t('system.redemption.search.status')" clearable style="width: 160px" @change="onSearch">
          <el-option :value="1" :label="t('system.redemption.status.valid')" />
          <el-option :value="0" :label="t('system.redemption.status.used')" />
          <el-option :value="-1" :label="t('system.redemption.status.disabled')" />
        </el-select>
        <el-button type="primary" @click="onSearch">
          <el-icon><Search /></el-icon>
          {{ t('system.redemption.search.query') }}
        </el-button>
        <el-button class="reset-btn" @click="onReset">
          <el-icon><RefreshRight /></el-icon>
          {{ t('system.redemption.search.reset') }}
        </el-button>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <el-table :data="table.items" class="rc-table" height="100%" border stripe>
          <el-table-column prop="id" :label="t('system.redemption.table.id')" width="80" />
          <el-table-column prop="code" :label="t('system.redemption.table.code')" min-width="180" />
          <el-table-column prop="creditsAmount" :label="t('system.redemption.table.credits')" width="100" />
          <el-table-column :label="t('system.redemption.table.status')" width="110">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="usedByUserId" :label="t('system.redemption.table.usedBy')" width="120">
            <template #default="{ row }">{{ row.usedByUserId ?? '-' }}</template>
          </el-table-column>
          <el-table-column prop="usedTime" :label="t('system.redemption.table.usedTime')" min-width="160">
            <template #default="{ row }">{{ formatTime(row.usedTime) }}</template>
          </el-table-column>
          <el-table-column prop="expireTime" :label="t('system.redemption.table.expireTime')" min-width="160">
            <template #default="{ row }">{{ formatTime(row.expireTime) }}</template>
          </el-table-column>
          <el-table-column prop="description" :label="t('system.redemption.table.description')" min-width="160" show-overflow-tooltip />
          <el-table-column prop="createTime" :label="t('system.redemption.table.createTime')" min-width="160">
            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column :label="t('system.redemption.table.actions')" fixed="right" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="openEditDialog(row)">{{ t('system.redemption.table.edit') }}</el-button>
              <el-popconfirm :title="t('system.redemption.messages.deleteConfirm')" @confirm="onDelete(row)">
                <template #reference>
                  <el-button size="small" type="danger">{{ t('system.redemption.table.delete') }}</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="table.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onSizeChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <!-- 新建对话框 -->
    <el-dialog v-model="createDialog.visible" :title="t('system.redemption.dialog.createTitle')" width="520px" class="rc-dialog" @close="onCreateDialogClose">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-position="top">
        <el-form-item :label="t('system.redemption.dialog.credits')" prop="creditsAmount">
          <el-input-number v-model="createForm.creditsAmount" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.prefix')" prop="prefix">
          <el-input v-model="createForm.prefix" :placeholder="t('system.redemption.dialog.prefixPlaceholder')" maxlength="16" show-word-limit />
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.length')" prop="length">
          <el-input-number v-model="createForm.length" :min="4" :max="64" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.expireTime')" prop="expireTime">
          <el-date-picker
            v-model="createForm.expireTime"
            type="datetime"
            :placeholder="t('system.redemption.dialog.expireTimePlaceholder')"
            value-format="YYYY-MM-DDTHH:mm:ss[Z]"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.description')" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4 }"
            maxlength="200"
            show-word-limit
            :placeholder="t('system.redemption.dialog.descriptionPlaceholder')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="cancel-btn" :disabled="createSubmitting" @click="createDialog.visible = false">{{ t('system.redemption.dialog.cancel') }}</el-button>
        <el-button type="primary" :loading="createSubmitting" @click="onSubmitCreate">{{ t('system.redemption.dialog.createBtn') }}</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editDialog.visible" :title="t('system.redemption.dialog.editTitle')" width="520px" class="rc-dialog" @close="onEditDialogClose">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-position="top">
        <el-form-item :label="t('system.redemption.table.id')">
          <el-text>{{ editForm.id }}</el-text>
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.codeLabel')">
          <el-text type="info" style="font-family: monospace;">{{ editDialog.record?.code }}</el-text>
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.credits')" prop="creditsAmount">
          <el-input-number v-model="editForm.creditsAmount" :min="0" :disabled="editDialog.record?.status === 0" controls-position="right" style="width: 100%" />
          <el-text type="info" v-if="editDialog.record?.status === 0">{{ t('system.redemption.dialog.usedNotEditable') }}</el-text>
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.statusLabel')" prop="status">
          <el-select v-model="editForm.status" :placeholder="t('system.redemption.dialog.statusPlaceholder')" style="width: 100%">
            <el-option :value="1" :label="t('system.redemption.status.valid')" />
            <el-option :value="0" :label="t('system.redemption.status.used')" />
            <el-option :value="-1" :label="t('system.redemption.status.disabled')" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.expireTime')" prop="expireTime">
          <el-date-picker
            v-model="editForm.expireTime"
            type="datetime"
            :placeholder="t('system.redemption.dialog.expireTimePlaceholder')"
            value-format="YYYY-MM-DDTHH:mm:ss[Z]"
            style="width: 100%"
            clearable
          />
        </el-form-item>
        <el-form-item :label="t('system.redemption.dialog.description')" prop="description">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4 }"
            maxlength="200"
            show-word-limit
            :placeholder="t('system.redemption.dialog.descriptionPlaceholder')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="cancel-btn" :disabled="editSubmitting" @click="editDialog.visible = false">{{ t('system.redemption.dialog.cancel') }}</el-button>
        <el-button type="primary" :loading="editSubmitting" @click="onSubmitEdit">{{ t('system.redemption.dialog.saveBtn') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { SystemRedemptionApi } from '@/api/system-redemption/types'
import { systemRedemptionApi } from '@/api/system-redemption/system-redemption'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const loading = ref(false)

const query = reactive<{ page: number; size: number; keyword: string; status?: 1 | 0 | -1 | undefined }>({
  page: 1,
  size: 10,
  keyword: '',
  status: undefined
})

const table = reactive<{ total: number; items: SystemRedemptionApi.SystemRedemptionCodeVo[] }>({ total: 0, items: [] })

const loadPage = async (toPage?: number) => {
  if (typeof toPage === 'number') query.page = toPage
  loading.value = true
  try {
    const params: SystemRedemptionApi.FetchCodesParams = {
      page: query.page,
      size: query.size,
      keyword: query.keyword || undefined,
      status: query.status
    }
    const data = await systemRedemptionApi.fetchCodes(params)
    table.total = data.total
    table.items = data.items
  } catch (e: any) {
    console.error(e)
    ElMessage.error(e?.message || t('system.redemption.messages.loadFailed'))
  } finally {
    loading.value = false
  }
}

const onPageChange = (p: number) => {
  query.page = p
  loadPage()
}
const onSizeChange = (s: number) => {
  query.size = s
  loadPage(1)
}

const onSearch = () => {
  loadPage(1)
}
const onReset = () => {
  query.keyword = ''
  query.status = undefined
  loadPage(1)
}

const formatTime = (iso: string | null) => {
  if (!iso) return '-'
  try {
    const d = new Date(iso)
    if (Number.isNaN(d.getTime())) return iso
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  } catch {
    return iso
  }
}

const statusText = (s: 1 | 0 | -1) => {
  if (s === 1) return t('system.redemption.status.valid')
  if (s === 0) return t('system.redemption.status.used')
  return t('system.redemption.status.disabled')
}
const statusTagType = (s: 1 | 0 | -1): 'success' | 'warning' | 'info' | 'danger' => {
  if (s === 1) return 'success'
  if (s === 0) return 'warning'
  return 'info'
}

// Dialogs
const createDialog = reactive({ visible: false })
const editDialog = reactive<{ visible: boolean; record: SystemRedemptionApi.SystemRedemptionCodeVo | null }>({ visible: false, record: null })

const openCreateDialog = () => {
  createDialog.visible = true
}
const openEditDialog = (row: SystemRedemptionApi.SystemRedemptionCodeVo) => {
  editDialog.record = row
  editDialog.visible = true
  // initialize edit form from record
  editForm.id = row.id
  editForm.creditsAmount = row.creditsAmount
  editForm.status = row.status
  editForm.expireTime = row.expireTime
  editForm.description = row.description ?? ''
}

const onDelete = async (row: SystemRedemptionApi.SystemRedemptionCodeVo) => {
  try {
    await systemRedemptionApi.deleteCode(row.id)
    ElNotification.success(t('system.redemption.messages.deleteSuccess'))
    loadPage()
  } catch (e: any) {
    console.error(e)
    ElMessage.error(e?.message || t('system.redemption.messages.deleteFailed'))
  }
}

onMounted(() => {
  loadPage(1)
})

// Create form
type CreateForm = {
  creditsAmount: number | null
  prefix?: string
  length?: number | null
  expireTime?: string | null
  description?: string
}

const createForm = reactive<CreateForm>({
  creditsAmount: 0,
  prefix: '',
  length: 12,
  expireTime: null,
  description: ''
})

const createFormRef = ref<FormInstance>()
const createSubmitting = ref(false)

const createRules = computed<FormRules<CreateForm>>(() => ({
  creditsAmount: [
    { required: true, message: t('system.redemption.validation.creditsRequired'), trigger: 'blur' },
    {
      validator: (_r, v, cb) => {
        if (v == null || Number.isNaN(Number(v))) return cb(new Error(t('system.redemption.validation.creditsMustBeNumber')))
        if (Number(v) < 0) return cb(new Error(t('system.redemption.validation.creditsNotNegative')))
        return cb()
      },
      trigger: 'blur'
    }
  ],
  prefix: [{ min: 0, max: 16, message: t('system.redemption.validation.prefixMaxLength'), trigger: 'blur' }],
  length: [
    { required: true, message: t('system.redemption.validation.lengthRequired'), trigger: 'blur' },
    {
      validator: (_r, v, cb) => {
        if (v == null) return cb(new Error(t('system.redemption.validation.lengthNotEmpty')))
        if (v < 4) return cb(new Error(t('system.redemption.validation.lengthMin')))
        if (v > 64) return cb(new Error(t('system.redemption.validation.lengthMax')))
        return cb()
      },
      trigger: 'blur'
    }
  ],
  description: [{ min: 0, max: 200, message: t('system.redemption.validation.descriptionMaxLength'), trigger: 'blur' }]
}))

const resetCreateForm = () => {
  createForm.creditsAmount = 0
  createForm.prefix = ''
  createForm.length = 12
  createForm.expireTime = null
  createForm.description = ''
  createFormRef.value?.clearValidate()
}

const onCreateDialogClose = () => {
  resetCreateForm()
}

const onSubmitCreate = () => {
  if (!createFormRef.value) return
  createFormRef.value.validate(async (valid) => {
    if (!valid) return
    createSubmitting.value = true
    try {
      await systemRedemptionApi.createCode({
        creditsAmount: Number(createForm.creditsAmount ?? 0),
        prefix: createForm.prefix || undefined,
        length: createForm.length ?? undefined,
        expireTime: createForm.expireTime || undefined,
        description: createForm.description || undefined
      })
      ElNotification.success(t('system.redemption.messages.createSuccess'))
      createDialog.visible = false
      await loadPage(1)
    } catch (e: any) {
      console.error(e)
      ElMessage.error(e?.message || t('system.redemption.messages.createFailed'))
    } finally {
      createSubmitting.value = false
    }
  })
}

// Edit form
type EditForm = {
  id: number | null
  creditsAmount: number | null
  status: 1 | 0 | -1 | null
  expireTime: string | null
  description: string
}

const editForm = reactive<EditForm>({
  id: null,
  creditsAmount: null,
  status: 1,
  expireTime: null,
  description: ''
})

const editFormRef = ref<FormInstance>()
const editSubmitting = ref(false)

const editRules = computed<FormRules<EditForm>>(() => ({
  creditsAmount: [
    { required: true, message: t('system.redemption.validation.creditsRequired'), trigger: 'blur' },
    {
      validator: (_r, v, cb) => {
        if (v == null || Number.isNaN(Number(v))) return cb(new Error(t('system.redemption.validation.creditsMustBeNumber')))
        if (Number(v) < 0) return cb(new Error(t('system.redemption.validation.creditsNotNegative')))
        return cb()
      },
      trigger: 'blur'
    }
  ],
  status: [{ required: true, message: t('system.redemption.validation.statusRequired'), trigger: 'change' }],
  description: [{ min: 0, max: 200, message: t('system.redemption.validation.descriptionMaxLength'), trigger: 'blur' }]
}))

const resetEditForm = () => {
  editForm.id = null
  editForm.creditsAmount = null
  editForm.status = 1
  editForm.expireTime = null
  editForm.description = ''
  editFormRef.value?.clearValidate()
}

const onEditDialogClose = () => {
  resetEditForm()
  editDialog.record = null
}

const onSubmitEdit = () => {
  if (!editFormRef.value) return
  editFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (editForm.id == null) return
    editSubmitting.value = true
    try {
      await systemRedemptionApi.updateCode({
        id: editForm.id,
        creditsAmount: editDialog.record?.status === 0 ? undefined : (editForm.creditsAmount ?? undefined),
        status: (editForm.status ?? undefined) as 1 | 0 | -1 | undefined,
        expireTime: editForm.expireTime ?? undefined,
        description: editForm.description ? editForm.description : undefined
      })
      ElNotification.success(t('system.redemption.messages.saveSuccess'))
      editDialog.visible = false
      await loadPage()
    } catch (e: any) {
      console.error(e)
      ElMessage.error(e?.message || t('system.redemption.messages.saveFailed'))
    } finally {
      editSubmitting.value = false
    }
  })
}
</script>

<style scoped>
.redemption-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--el-bg-color-overlay);
}

.content-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.content-card :deep(.el-card__header) {
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.search-bar :deep(.el-input__wrapper),
.search-bar :deep(.el-select__wrapper) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  transition: all 0.2s ease;
}

.search-bar :deep(.el-input__wrapper:hover),
.search-bar :deep(.el-select__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.search-bar :deep(.el-input__wrapper.is-focus),
.search-bar :deep(.el-select__wrapper.is-focused) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.reset-btn {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}

.reset-btn:hover {
  background-color: var(--el-fill-color-light);
}

.table-wrapper {
  flex: 1;
  min-height: 0;
  margin-bottom: 16px;
}

.rc-table {
  --el-table-bg-color: var(--el-bg-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-tr-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.pagination-container :deep(.el-pagination) {
  --el-pagination-bg-color: var(--el-bg-color);
  --el-pagination-text-color: var(--el-text-color-primary);
  --el-pagination-button-disabled-bg-color: var(--el-fill-color-lighter);
}

.pagination-container :deep(.el-pagination .btn-prev),
.pagination-container :deep(.el-pagination .btn-next),
.pagination-container :deep(.el-pager li) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
}

.pagination-container :deep(.el-pager li.is-active) {
  background-color: var(--el-color-primary);
  color: #fff;
  border-color: var(--el-color-primary);
}

.pagination-container :deep(.el-select__wrapper),
.pagination-container :deep(.el-input__wrapper) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  color: var(--el-text-color-primary);
  transition: all 0.2s ease;
}

.pagination-container :deep(.el-select__wrapper:hover),
.pagination-container :deep(.el-input__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.pagination-container :deep(.el-select__wrapper.is-focused),
.pagination-container :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

/* Dialog styles */
:deep(.rc-dialog .el-dialog) {
  background-color: var(--el-bg-color-overlay);
  color: var(--el-text-color-primary);
}
:deep(.rc-dialog .el-dialog__header),
:deep(.rc-dialog .el-dialog__footer) {
  background-color: var(--el-bg-color-overlay);
}
:deep(.rc-dialog .el-dialog__header) {
  border-bottom: 1px solid var(--el-border-color-light);
}
:deep(.rc-dialog .el-dialog__footer) {
  border-top: 1px solid var(--el-border-color-light);
}
:deep(.rc-dialog .el-dialog__title) {
  color: var(--el-text-color-primary);
}
:deep(.rc-dialog .el-dialog__body) {
  background-color: var(--el-bg-color-overlay);
}
:deep(.rc-dialog .el-form-item__label) {
  color: var(--el-text-color-primary);
}
:deep(.rc-dialog .el-input__wrapper),
:deep(.rc-dialog .el-select__wrapper),
:deep(.rc-dialog .el-input-number),
:deep(.rc-dialog .el-textarea__inner) {
  background-color: var(--el-bg-color) !important;
  border: 1px solid var(--el-border-color);
  transition: all 0.2s ease;
  box-shadow: none;
}

:deep(.rc-dialog .el-input__wrapper:hover),
:deep(.rc-dialog .el-select__wrapper:hover),
:deep(.rc-dialog .el-input-number:hover),
:deep(.rc-dialog .el-textarea__inner:hover) {
  border-color: var(--el-border-color-hover);
}

:deep(.rc-dialog .el-input__wrapper.is-focus),
:deep(.rc-dialog .el-select__wrapper.is-focused),
:deep(.rc-dialog .el-input-number.is-focus),
:deep(.rc-dialog .el-textarea__inner:focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

:deep(.rc-dialog .el-input-number .el-input__inner) {
  color: var(--el-text-color-primary);
}

.cancel-btn {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}
.cancel-btn:hover {
  background-color: var(--el-fill-color-light);
}

/* Select Dropdown */
:deep(.el-select__popper) {
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color);
}
:deep(.el-select__popper .el-select-dropdown) {
  background-color: var(--el-bg-color-overlay);
}
:deep(.el-select__popper .el-select-dropdown__item) {
  color: var(--el-text-color-regular);
}
:deep(.el-select__popper .el-select-dropdown__item.is-hovering) {
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}
:deep(.el-select__popper .el-select-dropdown__item.is-selected) {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

/* Dialog Form */
:deep(.rc-dialog .el-form--label-top .el-form-item__label) {
  padding-bottom: 6px;
  line-height: 1.2;
  font-weight: 500;
  text-align: left;
}
:deep(.rc-dialog .el-form--label-top .el-form-item) {
  margin-bottom: 16px;
}
</style>



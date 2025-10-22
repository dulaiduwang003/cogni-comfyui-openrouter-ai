<template>
  <div class="workflow-management-view">
    <el-card shadow="never" class="content-card">
      <template #header>
        <div class="card-header">
          <span>{{ t('system.workflow.title') }}</span>
          <div style="display: flex; gap: 8px; align-items: center;">
            <el-button class="secondary-btn" @click="openCategoryDialog">{{ t('system.workflow.categoryManagement') }}</el-button>
            <el-button type="primary" @click="openCreateDialog">
              <el-icon><Plus /></el-icon>
              {{ t('system.workflow.create') }}
            </el-button>
          </div>
        </div>
      </template>
      <div class="content">
        <div style="width: 100%; padding: 12px; display: flex; flex-direction: column; gap: 12px; min-height: 0;">
          <div class="search-bar" style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
            <el-input v-model="query.keyword" :placeholder="t('system.workflow.search.keyword')" clearable style="width: 260px" @clear="handleReset" @keyup.enter="handleSearch">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="query.categoryId" clearable :placeholder="t('system.workflow.search.category')" style="width: 220px" @change="handleSearch">
              <el-option :value="undefined" :label="t('system.workflow.search.allCategories')" />
              <el-option v-for="c in categoryList" :key="c.categoryId" :label="c.name" :value="c.categoryId" />
            </el-select>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              {{ t('system.workflow.search.query') }}
            </el-button>
            <el-button class="reset-btn" @click="handleReset">
              <el-icon><RefreshRight /></el-icon>
              {{ t('system.workflow.search.reset') }}
            </el-button>
          </div>

          <div class="table-wrapper">
          <el-table :data="table.items" border stripe style="width: 100%" height="100%" class="workflow-table">
            <el-table-column :label="t('system.workflow.table.cover')" width="84">
              <template #default="{ row }">
                <img
                  v-if="row.url"
                  :src="row.url"
                  :alt="t('system.workflow.table.cover')"
                  style="width: 56px; height: 56px; object-fit: cover; border-radius: 4px; background: var(--el-fill-color);"
                  @error="(e: Event) => ((e.target as HTMLImageElement).style.display = 'none')"
                />
                <div v-else style="width: 56px; height: 56px; border-radius: 4px; background: var(--el-fill-color);"></div>
              </template>
            </el-table-column>
            <el-table-column prop="workflowId" :label="t('system.workflow.table.id')" width="90" />
            <el-table-column prop="name" :label="t('system.workflow.table.name')" min-width="200" show-overflow-tooltip />
            <el-table-column prop="description" :label="t('system.workflow.table.description')" min-width="260" show-overflow-tooltip />
            <el-table-column prop="categoryName" :label="t('system.workflow.table.category')" width="160" show-overflow-tooltip />
            <el-table-column prop="creditsDeducted" :label="t('system.workflow.table.credits')" width="110" />
            <el-table-column :label="t('system.workflow.table.actions')" width="200" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="openEditDialog(row)">{{ t('system.workflow.table.edit') }}</el-button>
                <el-button link type="danger" @click="confirmDelete(row)">{{ t('system.workflow.table.delete') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          </div>

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
        </div>
      </div>
    </el-card>
    <!-- 新建工作流对话框 -->
    <el-dialog v-model="createDialogVisible" :title="t('system.workflow.dialog.createTitle')" width="880px" class="workflow-dialog" @close="resetAll">
      <div class="dialog-content">
        <!-- 基本信息 -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">{{ t('system.workflow.dialog.basicInfo') }}</div>
          </template>
          <div class="base-form">
            <el-form ref="createFormRef" :model="baseForm" :rules="createFormRules" label-width="100px" label-position="left">
              <el-form-item :label="t('system.workflow.dialog.name')" prop="name">
                <el-input v-model="baseForm.name" :placeholder="t('system.workflow.dialog.namePlaceholder')" />
              </el-form-item>
              <el-form-item :label="t('system.workflow.dialog.cover')">
                <div class="cover-row">
                  <el-input v-model="baseForm.url" :placeholder="t('system.workflow.dialog.coverPlaceholder')" />
                  <el-upload :show-file-list="false" :http-request="handleCoverUpload" accept="image/*">
                    <el-button class="secondary-btn">{{ t('system.workflow.dialog.upload') }}</el-button>
                  </el-upload>
                </div>
              </el-form-item>
              <el-form-item :label="t('system.workflow.dialog.description')">
                <el-input v-model="baseForm.description" type="textarea" :rows="2" :placeholder="t('system.workflow.dialog.descriptionPlaceholder')" />
              </el-form-item>
              <el-form-item :label="t('system.workflow.dialog.category')" prop="workflowCategoryId">
                <el-select v-model="baseForm.workflowCategoryId" :placeholder="t('system.workflow.dialog.categoryPlaceholder')" style="width: 260px">
                  <el-option v-for="c in categoryList" :key="c.categoryId" :label="c.name" :value="c.categoryId" />
                </el-select>
              </el-form-item>
              <el-form-item :label="t('system.workflow.dialog.credits')" prop="creditsDeducted">
                <el-input-number v-model="baseForm.creditsDeducted" :min="0" :max="100000" />
              </el-form-item>
            </el-form>
          </div>
        </el-card>

        <!-- 解析工作流 -->
        <el-card shadow="never" class="section-card">
          <template #header>
            <div class="section-header">{{ t('system.workflow.dialog.parseWorkflow') }}</div>
          </template>
          <div class="parse-row">
            <input ref="jsonFileRef" type="file" accept="application/json,.json" @change="onWorkflowFileChange" v-show="false" />
            <el-button class="secondary-btn" :loading="parsing" @click="triggerChooseFile">{{ t('system.workflow.dialog.chooseFile') }}</el-button>
            <el-text type="info" v-if="parseResult.json">{{ t('system.workflow.dialog.parsed') }}</el-text>
          </div>
          <div class="parse-summary" v-if="parseResult.formNodeList.length || parseResult.allNodeList.length">
            <el-alert type="success" show-icon :closable="false">
              <template #title>
                {{ t('system.workflow.dialog.parseSummary', { formNodes: parseResult.formNodeList.length, allNodes: parseResult.allNodeList.length }) }}
              </template>
            </el-alert>
          </div>
        </el-card>

        <!-- 配置输入表单节点 -->
        <el-card shadow="never" class="section-card" v-if="configFormNodes.length">
          <template #header>
            <div class="section-header">{{ t('system.workflow.dialog.inputConfig') }}</div>
          </template>
          <div class="form-nodes">
            <div class="form-node" v-for="(item, idx) in configFormNodes" :key="item.nodeKey + '-' + idx">
              <div class="row-1">
                <div class="label">{{ item.tips || t('system.workflow.dialog.unnamedNode') }} ({{ item.nodeKey }})</div>
                <el-checkbox v-model="item.enabled">{{ t('system.workflow.dialog.enableForm') }}</el-checkbox>
                <el-select v-model="item.type" style="width: 200px">
                  <el-option v-for="type in getAvailableTypes(item)" :key="type" :label="t('system.workflow.formTypes.' + type)" :value="type" />
                </el-select>
                <el-switch v-model="item.required" :active-text="t('system.workflow.dialog.required')" :inactive-text="t('system.workflow.dialog.optional')" />
              </div>
              <div class="row-2">
                <el-input v-model="item.tips" :disabled="!item.enabled" :placeholder="t('system.workflow.dialog.formLabel')" />
                <el-input v-model="item.template" :disabled="!item.enabled" :placeholder="t('system.workflow.dialog.defaultTemplate')" />
                <el-input-number v-model="item.size" :disabled="!item.enabled" :min="0" :max="100000" :controls="false" :placeholder="t('system.workflow.dialog.sizeLength')" />
              </div>
              <div class="row-3" v-if="(item.type === WorkflowFormTypeEnum.RADIO_SELECTOR || item.type === WorkflowFormTypeEnum.CHECKBOX_SELECTOR) && item.enabled">
                <el-input type="textarea" v-model="item.options" :rows="3" :placeholder="t('system.workflow.dialog.optionsPlaceholder')" />
              </div>
              <div class="row-4">{{ t('system.workflow.dialog.inputField') }}：<el-tag type="info">{{ item.inputs }}</el-tag></div>
            </div>
          </div>
        </el-card>

        <!-- 配置输出节点 -->
        <el-card shadow="never" class="section-card" v-if="parseResult.allNodeList.length">
          <template #header>
            <div class="section-header">{{ t('system.workflow.dialog.outputConfig') }}</div>
          </template>
          <div class="output-config">
            <el-button v-if="outputNodes.length < 1" type="primary" link @click="addOutputConfig">{{ t('system.workflow.dialog.addOutput') }}</el-button>
            <div class="output-row" v-for="(o, i) in outputNodes" :key="i">
              <el-select v-model="o.nodeKey" :placeholder="t('system.workflow.dialog.selectNode')" style="width: 320px">
                <el-option v-for="n in parseResult.allNodeList" :key="n.nodeKey" :label="formatNodeLabel(n)" :value="n.nodeKey" />
              </el-select>
              <el-select v-model="o.type" :placeholder="t('system.workflow.dialog.selectType')" style="width: 160px">
                <el-option v-for="(label, value) in outputTypeLabelMap" :key="value" :label="label" :value="value" />
              </el-select>
              <el-button link type="danger" @click="removeOutputConfig(i)">{{ t('system.workflow.dialog.removeOutput') }}</el-button>
            </div>
          </div>
        </el-card>
      </div>
      <template #footer>
        <el-button class="cancel-btn" @click="createDialogVisible = false">{{ t('system.workflow.dialog.cancel') }}</el-button>
        <el-button type="primary" :disabled="!canSubmit" :loading="saving" @click="handleSave">{{ t('system.workflow.dialog.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- 编辑工作流对话框 -->
    <el-dialog v-model="editDialogVisible" :title="t('system.workflow.dialog.editTitle')" width="520px" class="workflow-edit-dialog" @close="onEditDialogClose">
      <el-form ref="editFormRef" :model="editForm" :rules="editFormRules" label-width="100px">
        <el-form-item :label="t('system.workflow.dialog.name')" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item :label="t('system.workflow.dialog.category')" prop="workflowCategoryId">
          <el-select v-model="editForm.workflowCategoryId" :placeholder="t('system.workflow.dialog.categoryPlaceholder')" style="width: 260px">
            <el-option v-for="c in categoryList" :key="c.categoryId" :label="c.name" :value="c.categoryId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="cancel-btn" @click="editDialogVisible = false">{{ t('system.workflow.dialog.cancel') }}</el-button>
        <el-button type="primary" :loading="updating" @click="submitUpdate">{{ t('system.workflow.dialog.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- 类别管理对话框 -->
    <el-dialog v-model="categoryDialogVisible" :title="t('system.workflow.dialog.categoryTitle')" width="620px" class="workflow-category-dialog">
      <div style="display: flex; justify-content: flex-end; margin-bottom: 12px;">
        <el-button type="primary" @click="openCreateCategoryDialog">{{ t('system.workflow.dialog.createCategory') }}</el-button>
      </div>
      <el-table :data="categoryList" border size="small" class="workflow-category-table">
        <el-table-column prop="categoryId" :label="t('system.workflow.table.id')" width="90" />
        <el-table-column :label="t('system.workflow.dialog.name')">
          <template #default="{ row }">
            <el-input v-model="row.name" />
          </template>
        </el-table-column>
        <el-table-column :label="t('system.workflow.table.actions')" width="160">
          <template #default="{ row }">
            <el-button link type="primary" :loading="row.__updating" @click="updateCategory(row)">{{ t('system.workflow.dialog.save') }}</el-button>
            <el-button link type="danger" :loading="row.__deleting" @click="deleteCategory(row)">{{ t('system.workflow.table.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增类别对话框 -->
    <el-dialog v-model="createCategoryDialogVisible" :title="t('system.workflow.dialog.createCategoryTitle')" width="400px" class="workflow-category-create-dialog">
      <el-form :model="categoryForm" :rules="categoryFormRules" ref="categoryFormRef" label-width="80px">
        <el-form-item :label="t('system.workflow.dialog.categoryName')" prop="name">
          <el-input v-model="categoryForm.name" :placeholder="t('system.workflow.dialog.categoryNamePlaceholder')" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="cancel-btn" @click="createCategoryDialogVisible = false">{{ t('system.workflow.dialog.cancel') }}</el-button>
        <el-button type="primary" :loading="creatingCategory" @click="submitCreateCategory">{{ t('system.workflow.dialog.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue';
import { ElNotification, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { workflowApi } from '@/api/system-workflow/system-workflow'
import type { ParsingWorkflowVo, FormNodeConfig, OutputNodeConfig, WorkflowListItem } from '@/api/system-workflow/types'
import { ossApi } from '@/api/oss/oss'
import { WorkflowFormTypeEnum, WorkflowResultModelTypeEnum, WorkflowResultModelDigitalEnum } from '@/enums/workflow'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const createDialogVisible = ref(false)
const parsing = ref(false)
const saving = ref(false)
const updating = ref(false)

const jsonFileRef = ref<HTMLInputElement | null>(null)
const createFormRef = ref<FormInstance>()
const editFormRef = ref<FormInstance>()

const baseForm = reactive({
  name: '',
  description: '',
  url: '',
  workflowCategoryId: '',
  creditsDeducted: 0
})

// 创建工作流表单验证规则
const createFormRules = computed<FormRules>(() => ({
  name: [
    { required: true, message: t('system.workflow.validation.nameRequired'), trigger: 'blur' },
    { min: 1, max: 100, message: t('system.workflow.validation.nameLength'), trigger: 'blur' }
  ],
  workflowCategoryId: [
    { required: true, message: t('system.workflow.validation.categoryRequired'), trigger: 'change' }
  ],
  creditsDeducted: [
    { required: true, message: t('system.workflow.validation.creditsRequired'), trigger: 'blur' },
    { type: 'number', min: 0, message: t('system.workflow.validation.creditsMin'), trigger: 'blur' }
  ]
}))

const parseResult = reactive<ParsingWorkflowVo>({
  json: '',
  allNodeList: [],
  formNodeList: []
})

type ConfigFormNode = {
  nodeKey: string
  type: WorkflowFormTypeEnum
  inputs: WorkflowResultModelDigitalEnum
  tips: string
  options?: string
  template?: string
  required: boolean
  size?: number
  enabled: boolean
}

const configFormNodes = ref<ConfigFormNode[]>([])
const outputNodes = ref<OutputNodeConfig[]>([])

const outputTypeLabelMap = computed(() => ({
  [WorkflowResultModelTypeEnum.IMAGE]: t('system.workflow.outputTypes.image'),
  [WorkflowResultModelTypeEnum.VIDEO]: t('system.workflow.outputTypes.video'),
  [WorkflowResultModelTypeEnum.AUDIO]: t('system.workflow.outputTypes.audio')
}))

const openCreateDialog = () => {
  resetAll()
  createDialogVisible.value = true
}

// ---------- 列表、查询、分页 ----------
const query = reactive<{ page: number; size: number; keyword?: string; categoryId?: number | undefined }>({ page: 1, size: 10, keyword: '', categoryId: undefined })
const table = reactive<{ total: number; items: WorkflowListItem[] }>({ total: 0, items: [] })

const categoryList = ref<{ categoryId: number; name: string; url?: string; __updating?: boolean; __deleting?: boolean }[]>([])

const loadCategories = async () => {
  try {
    const list = await workflowApi.getCategoryList()
    categoryList.value = list.map((c: any) => ({ ...c }))
  } catch (e) {
    console.error(e)
  }
}

const loadPage = async (toPage?: number) => {
  if (typeof toPage === 'number') query.page = toPage
  const params: any = { page: query.page, size: query.size }
  if (query.keyword) params.keyword = query.keyword
  if (query.categoryId) params.categoryId = query.categoryId
  try {
    const data = await workflowApi.getWorkflowPage(params)
    table.total = data.total
    table.items = data.items
  } catch (e) {
    console.error(e)
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

// 与用户管理一致的查询/重置
const handleSearch = () => {
  query.page = 1
  loadPage(1)
}
const handleReset = () => {
  query.keyword = ''
  query.categoryId = undefined
  query.page = 1
  loadPage(1)
}

onMounted(() => {
  loadCategories()
  loadPage(1)
})

// ---------- 编辑、删除 ----------
const editDialogVisible = ref(false)
const editForm = reactive<{ workflowId: number | null; name: string; workflowCategoryId: number | null }>({ workflowId: null, name: '', workflowCategoryId: null })

// 编辑工作流表单验证规则
const editFormRules = computed<FormRules>(() => ({
  name: [
    { required: true, message: t('system.workflow.validation.nameRequired'), trigger: 'blur' },
    { min: 1, max: 100, message: t('system.workflow.validation.nameLength'), trigger: 'blur' }
  ],
  workflowCategoryId: [
    { required: true, message: t('system.workflow.validation.categoryRequired'), trigger: 'change' }
  ]
}))

const openEditDialog = (row: { workflowId: number; name: string; categoryName: string }) => {
  editForm.workflowId = row.workflowId
  editForm.name = row.name
  // 默认选择第一个类别
  editForm.workflowCategoryId = categoryList.value[0]?.categoryId ?? null
  editDialogVisible.value = true
}

const submitUpdate = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (!editForm.workflowId) return
    
    try {
      updating.value = true
      await workflowApi.updateWorkflow({
        workflowId: editForm.workflowId,
        name: editForm.name,
        workflowCategoryId: editForm.workflowCategoryId!
      })
      ElNotification.success(t('system.workflow.messages.updateSuccess'))
      editDialogVisible.value = false
      loadPage()
    } catch (e) {
      console.error(e)
    } finally {
      updating.value = false
    }
  })
}

const confirmDelete = async (row: { workflowId: number; name: string }) => {
  try {
    await ElMessageBox.confirm(
      t('system.workflow.messages.deleteConfirm', { name: row.name }),
      t('system.workflow.messages.deleteTitle'),
      { type: 'warning', confirmButtonText: t('system.workflow.table.delete'), cancelButtonText: t('system.workflow.messages.cancel') }
    )
    await workflowApi.deleteWorkflow({ workflowId: row.workflowId })
    ElNotification.success(t('system.workflow.messages.deleteSuccess'))
    loadPage()
  } catch (e) {
    // 用户取消或请求异常
    if (e) console.error(e)
  }
}

// ---------- 类别管理 ----------
const categoryDialogVisible = ref(false)
const createCategoryDialogVisible = ref(false)
const categoryForm = reactive<{ name: string }>({ name: '' })
const categoryFormRef = ref<FormInstance>()
const categoryFormRules = computed<FormRules>(() => ({
  name: [
    { required: true, message: t('system.workflow.validation.categoryNameRequired'), trigger: 'blur' },
    { min: 1, max: 50, message: t('system.workflow.validation.categoryNameLength'), trigger: 'blur' }
  ]
}))
const creatingCategory = ref(false)

const openCategoryDialog = () => {
  categoryDialogVisible.value = true
}

const openCreateCategoryDialog = () => {
  categoryForm.name = ''
  createCategoryDialogVisible.value = true
}

const submitCreateCategory = async () => {
  if (!categoryFormRef.value) return
  await categoryFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      creatingCategory.value = true
      await workflowApi.createCategory({ name: categoryForm.name })
      ElNotification.success(t('system.workflow.messages.categoryCreateSuccess'))
      createCategoryDialogVisible.value = false
      categoryForm.name = ''
      await loadCategories()
    } catch (e) {
      console.error(e)
    } finally {
      creatingCategory.value = false
    }
  })
}

const updateCategory = async (row: any) => {
  try {
    row.__updating = true
    await workflowApi.updateCategory({ categoryId: row.categoryId, name: row.name })
    ElNotification.success(t('system.workflow.messages.categorySaveSuccess'))
    await loadCategories()
    // 刷新工作流列表
    await loadPage(query.page)
  } catch (e) {
    console.error(e)
  } finally {
    row.__updating = false
  }
}

const deleteCategory = async (row: any) => {
  try {
    await ElMessageBox.confirm(t('system.workflow.messages.categoryDeleteConfirm', { name: row.name }), t('system.workflow.messages.deleteTitle'), { type: 'warning' })
    row.__deleting = true
    await workflowApi.deleteCategory({ categoryId: row.categoryId })
    ElNotification.success(t('system.workflow.messages.categoryDeleteSuccess'))
    await loadCategories()
    // 清除类别筛选
    if (query.categoryId === row.categoryId) {
      query.categoryId = undefined
      await loadPage(1)
    } else {
      // 刷新工作流列表
      await loadPage(query.page)
    }
  } catch (e) {
    if (e) console.error(e)
  } finally {
    row.__deleting = false
  }
}

const triggerChooseFile = () => {
  jsonFileRef.value?.click()
}

const onWorkflowFileChange = async (e: Event) => {
  const input = e.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return
  const file = input.files[0]
  if (!file.name.endsWith('.json')) {
    ElNotification.error(t('system.workflow.validation.fileFormatError'))
    input.value = ''
    return
  }
  try {
    parsing.value = true
    const data = await workflowApi.parseWorkflow(file)
    parseResult.json = data.json
    parseResult.allNodeList = data.allNodeList
    parseResult.formNodeList = data.formNodeList
    // 初始化可编辑表单配置
    configFormNodes.value = data.formNodeList.map(n => ({
      nodeKey: n.nodeKey,
      type: n.type === WorkflowFormTypeEnum.TEXT_CONFIGURABLE 
        ? WorkflowFormTypeEnum.TEXT_PROMPT 
        : n.type === WorkflowFormTypeEnum.IMAGE_CONFIGURABLE 
        ? WorkflowFormTypeEnum.IMAGE_UPLOAD 
        : (n.type as any),
      inputs: n.nodeDigital as WorkflowResultModelDigitalEnum,
      tips: n.tips || '',
      required: true,
      size: n.type === WorkflowFormTypeEnum.IMAGE_UPLOAD 
        || n.type === WorkflowFormTypeEnum.IMAGE_CONFIGURABLE 
        || n.type === WorkflowFormTypeEnum.IMAGE_SCRIBBLE 
        || n.type === WorkflowFormTypeEnum.VIDEO_UPLOAD 
        || n.type === WorkflowFormTypeEnum.AUDIO_UPLOAD ? 10 : 500,
      template: '',
      options: n.type === WorkflowFormTypeEnum.TEXT_CONFIGURABLE ? '' : undefined,
      enabled: true
    }))
    ElNotification.success(t('system.workflow.dialog.parseSuccess'))
  } catch (err: any) {
    console.error(err)
  } finally {
    parsing.value = false
    input.value = ''
  }
}

const getAvailableTypes = (item: ConfigFormNode) => {
  // 文本类可切换三种
  if (item.inputs === WorkflowResultModelDigitalEnum.TEXT || item.inputs === WorkflowResultModelDigitalEnum.MULTI_LINE_PROMPT || item.inputs === WorkflowResultModelDigitalEnum.RESOLUTION) {
    return [WorkflowFormTypeEnum.TEXT_PROMPT, WorkflowFormTypeEnum.RADIO_SELECTOR, WorkflowFormTypeEnum.CHECKBOX_SELECTOR]
  }
  // 图片类可切换两种：IMAGE_UPLOAD / IMAGE_SCRIBBLE
  if (item.inputs === WorkflowResultModelDigitalEnum.IMAGE) {
    return [WorkflowFormTypeEnum.IMAGE_UPLOAD, WorkflowFormTypeEnum.IMAGE_SCRIBBLE]
  }
  // 其他文件类固定
  if (item.inputs === WorkflowResultModelDigitalEnum.VIDEO) return [WorkflowFormTypeEnum.VIDEO_UPLOAD]
  if (item.inputs === WorkflowResultModelDigitalEnum.AUDIO) return [WorkflowFormTypeEnum.AUDIO_UPLOAD]
  return [WorkflowFormTypeEnum.TEXT_PROMPT]
}

const addOutputConfig = () => {
  if (outputNodes.value.length > 0) return
  outputNodes.value.push({ nodeKey: '', type: WorkflowResultModelTypeEnum.IMAGE })
}
const removeOutputConfig = (idx: number) => {
  outputNodes.value.splice(idx, 1)
}
const formatNodeLabel = (n: { nodeKey: string; tips: string | null }) => {
  return t('system.workflow.dialog.nodeLabel', { tips: n.tips || t('system.workflow.dialog.unnamedNode'), key: n.nodeKey })
}

const canSubmit = computed(() => {
  const enabledFormNodes = configFormNodes.value.filter(n => n.enabled)
  return !!baseForm.name && !!parseResult.json && enabledFormNodes.length > 0 && outputNodes.value.length > 0 && outputNodes.value.every(o => o.nodeKey && o.type)
})

const handleSave = async () => {
  if (!createFormRef.value) return
  
  // 先验证基础表单
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (!canSubmit.value) return
  
  // 选择器需要校验 options
  const enabledFormNodes = configFormNodes.value.filter(n => n.enabled)
  for (const item of enabledFormNodes) {
    if ((item.type === WorkflowFormTypeEnum.RADIO_SELECTOR || item.type === WorkflowFormTypeEnum.CHECKBOX_SELECTOR)) {
      if (!item.options) {
        ElNotification.error(t('system.workflow.validation.optionsRequired'))
        return
      }
      try {
        const obj = JSON.parse(item.options)
        if (!obj || typeof obj !== 'object' || Array.isArray(obj) || Object.keys(obj).length === 0) {
          ElNotification.error(t('system.workflow.validation.optionsInvalidFormat'))
          return
        }
      } catch {
        ElNotification.error(t('system.workflow.validation.optionsJsonError'))
        return
      }
    }
  }

  const payload = {
    name: baseForm.name,
    description: baseForm.description || undefined,
    url: baseForm.url || undefined,
    json: parseResult.json,
    workflowCategoryId: baseForm.workflowCategoryId || '1',
    creditsDeducted: baseForm.creditsDeducted,
    formNodeList: enabledFormNodes.map<FormNodeConfig>(i => ({
      nodeKey: i.nodeKey,
      type: i.type as WorkflowFormTypeEnum,
      inputs: i.inputs,
      tips: i.tips,
      options: i.options,
      template: i.template,
      required: i.required ? 1 : 0,
      size: i.size
    })),
    outputNodeList: outputNodes.value
  }

  try {
    saving.value = true
    await workflowApi.saveWorkflowConfig(payload)
    ElNotification.success(t('system.workflow.messages.saveSuccess'))
    createDialogVisible.value = false
    await loadPage()
  } catch (err: any) {
    console.error(err)
  } finally {
    saving.value = false
  }
}

const handleCoverUpload = async (req: any) => {
  try {
    const url = await ossApi.uploadFile({ file: req.file })
    baseForm.url = url
    ElNotification.success(t('system.workflow.dialog.coverUploadSuccess'))
  } catch (e) {
    console.error(e)
  }
}

const resetAll = () => {
  baseForm.name = ''
  baseForm.description = ''
  baseForm.url = ''
  baseForm.workflowCategoryId = ''
  baseForm.creditsDeducted = 0
  parseResult.json = ''
  parseResult.allNodeList = []
  parseResult.formNodeList = []
  configFormNodes.value = []
  outputNodes.value = []
  createFormRef.value?.clearValidate()
}

// 编辑对话框关闭时清除验证
const onEditDialogClose = () => {
  editFormRef.value?.clearValidate()
}
</script>

<style scoped>
.workflow-management-view {
  height: 100%;
}

.content-card {
  height: 100%;
  background-color: var(--el-bg-color-overlay);
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

.content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.content-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  height: calc(100% - 52px);
}

.table-wrapper {
  flex: 1;
  min-height: 0;
  margin-bottom: 16px;
}

.workflow-table {
  margin-bottom: 0;
  /* unify table tokens in dark theme */
  --el-table-bg-color: var(--el-bg-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-tr-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
}

.workflow-table :deep(.el-table),
.workflow-table :deep(.el-table__inner-wrapper),
.workflow-table :deep(.el-table__body-wrapper),
.workflow-table :deep(.el-scrollbar__wrap),
.workflow-table :deep(.el-scrollbar__view) {
  background-color: var(--el-bg-color);
}

.workflow-table :deep(.el-table__empty-block),
.workflow-table :deep(.el-table__header) {
  background-color: var(--el-bg-color);
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

/* 
  对话框样式修正（参考 UserManagementView.vue）
  使用 :deep() 穿透 scoped 样式，确保对 teleport 到 body 的 el-dialog 生效 
*/
:deep(.workflow-dialog .el-dialog),
:deep(.workflow-edit-dialog .el-dialog),
:deep(.workflow-category-dialog .el-dialog),
:deep(.workflow-category-create-dialog .el-dialog) {
  background-color: var(--el-bg-color-overlay);
  color: var(--el-text-color-primary);
}

:deep(.workflow-dialog .el-dialog__header),
:deep(.workflow-dialog .el-dialog__footer),
:deep(.workflow-edit-dialog .el-dialog__header),
:deep(.workflow-edit-dialog .el-dialog__footer),
:deep(.workflow-category-dialog .el-dialog__header),
:deep(.workflow-category-dialog .el-dialog__footer),
:deep(.workflow-category-create-dialog .el-dialog__header),
:deep(.workflow-category-create-dialog .el-dialog__footer) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.workflow-dialog .el-dialog__body),
:deep(.workflow-edit-dialog .el-dialog__body),
:deep(.workflow-category-dialog .el-dialog__body),
:deep(.workflow-category-create-dialog .el-dialog__body) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.workflow-dialog .el-input__wrapper),
:deep(.workflow-dialog .el-select__wrapper),
:deep(.workflow-edit-dialog .el-input__wrapper),
:deep(.workflow-edit-dialog .el-select__wrapper),
:deep(.workflow-category-dialog .el-input__wrapper),
:deep(.workflow-category-dialog .el-select__wrapper),
:deep(.workflow-category-create-dialog .el-input__wrapper) {
  background-color: var(--el-bg-color) !important; /* 强制覆盖 */
  border: 1px solid var(--el-border-color);
  transition: all 0.2s ease;
}

:deep(.workflow-dialog .el-input__wrapper:hover),
:deep(.workflow-dialog .el-select__wrapper:hover),
:deep(.workflow-edit-dialog .el-input__wrapper:hover),
:deep(.workflow-edit-dialog .el-select__wrapper:hover),
:deep(.workflow-category-dialog .el-input__wrapper:hover),
:deep(.workflow-category-dialog .el-select__wrapper:hover),
:deep(.workflow-category-create-dialog .el-input__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

:deep(.workflow-dialog .el-input__wrapper.is-focus),
:deep(.workflow-dialog .el-select__wrapper.is-focused),
:deep(.workflow-edit-dialog .el-input__wrapper.is-focus),
:deep(.workflow-edit-dialog .el-select__wrapper.is-focused),
:deep(.workflow-category-dialog .el-input__wrapper.is-focus),
:deep(.workflow-category-dialog .el-select__wrapper.is-focused),
:deep(.workflow-category-create-dialog .el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

/* 对话框中的文本域样式 */
:deep(.workflow-dialog .el-textarea__inner),
:deep(.workflow-edit-dialog .el-textarea__inner),
:deep(.workflow-category-dialog .el-textarea__inner),
:deep(.workflow-category-create-dialog .el-textarea__inner) {
  background-color: var(--el-bg-color) !important;
  border: 1px solid var(--el-border-color);
  color: var(--el-text-color-primary);
  transition: all 0.2s ease;
  box-shadow: none; /* 移除默认的 focus shadow */
}

:deep(.workflow-dialog .el-textarea__inner:hover),
:deep(.workflow-edit-dialog .el-textarea__inner:hover),
:deep(.workflow-category-dialog .el-textarea__inner:hover),
:deep(.workflow-category-create-dialog .el-textarea__inner:hover) {
  border-color: var(--el-border-color-hover);
}

:deep(.workflow-dialog .el-textarea__inner:focus),
:deep(.workflow-edit-dialog .el-textarea__inner:focus),
:deep(.workflow-category-dialog .el-textarea__inner:focus),
:deep(.workflow-category-create-dialog .el-textarea__inner:focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}


/* 统一取消按钮样式和用户管理一致 */
:deep(.cancel-btn) {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}

:deep(.cancel-btn:hover) {
  background-color: var(--el-fill-color-light);
}

/* 次级按钮与用户管理一致（例如：类别管理、上传、选择文件） */
.secondary-btn {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}

.secondary-btn:hover {
  background-color: var(--el-fill-color-light);
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-card {
  background-color: var(--el-bg-color-overlay);
}

.section-header {
  font-weight: 600;
}

.base-form .cover-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.parse-row {
  display: flex;
  gap: 8px;
  align-items: center;
}

.form-nodes {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-node {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
}

/* 搜索栏控件暗色样式 */
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

/* 重置按钮与用户管理一致 */
.reset-btn {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}

.reset-btn:hover {
  background-color: var(--el-fill-color-light);
}

/* 类别管理对话框中的表格暗色样式 */
.workflow-category-table {
  --el-table-bg-color: var(--el-bg-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-tr-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
}

.workflow-category-table :deep(.el-table),
.workflow-category-table :deep(.el-table__inner-wrapper),
.workflow-category-table :deep(.el-table__body-wrapper),
.workflow-category-table :deep(.el-scrollbar__wrap),
.workflow-category-table :deep(.el-scrollbar__view),
.workflow-category-table :deep(.el-table__empty-block),
.workflow-category-table :deep(.el-table__header) {
  background-color: var(--el-bg-color);
}

/* 分页中的下拉与输入框（sizes/jumper）与用户管理一致 */
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

/* Select 下拉弹层暗色（与用户管理同步） */
:deep(.el-select__popper) {
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color);
}

:deep(.el-select__popper .el-select-dropdown) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.el-select__popper .el-select-dropdown__item) {
  color: var(--el-text-color-regular);
  transition: background-color 0.15s ease, color 0.15s ease;
}

:deep(.el-select__popper .el-select-dropdown__item.is-hovering) {
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

:deep(.el-select__popper .el-select-dropdown__item.is-selected) {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

/* 虚拟化 SelectV2 兼容 */
:deep(.el-select__popper .el-select-dropdown__item.selected),
:deep(.el-select__popper .el-select-dropdown__item.hover) {
  background-color: var(--el-fill-color-light);
}

.form-node .row-1,
.form-node .row-2 {
  display: flex;
  gap: 12px;
  align-items: center;
}

.form-node .row-1 :deep(.el-checkbox__label) {
  color: var(--el-text-color-primary);
}

.form-node .row-1 :deep(.el-checkbox .el-checkbox__inner) {
  background-color: var(--el-bg-color);
  border-color: var(--el-border-color);
}

.form-node .row-1 :deep(.el-checkbox:hover .el-checkbox__inner) {
  border-color: var(--el-border-color-hover);
}

.form-node .row-1 :deep(.el-checkbox.is-checked .el-checkbox__inner) {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
}

.form-node .row-4 :deep(.el-tag) {
  background-color: var(--el-fill-color-lighter);
  border-color: var(--el-border-color);
  color: var(--el-text-color-regular);
}

.form-node .row-2 > * {
  flex: 1;
}

.output-config {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.output-row {
  display: flex;
  gap: 12px;
  align-items: center;
}
.parse-summary{
  padding-top: 12px;
}
</style>


<template>
  <div class="model-dropdown-content">
    <!-- 搜索输入框 -->
    <div class="search-section">
      <div class="search-and-filter-wrapper">
        <div class="search-input-wrapper">
          <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
            <path d="m21 21-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <input 
            v-model="searchQuery"
            type="text" 
            class="search-input"
            :placeholder="t('chat.model.searchPlaceholder')"
            @input="handleSearch"
          />
          <button v-if="searchQuery" class="clear-search-btn" @click="clearSearch">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
              <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        
        <!-- 筛选展开/收起按钮 -->
        <div class="filter-toggle-section">
          <button 
            class="filter-toggle-btn"
            @click="isFilterExpanded = !isFilterExpanded"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            {{ t('chat.model.filter') }}
            <svg 
              class="filter-toggle-arrow"
              :class="{ expanded: isFilterExpanded }"
              width="14" height="14" 
              viewBox="0 0 24 24" 
              fill="none"
            >
              <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          
          <!-- 活跃筛选数量提示 -->
          <span v-if="hasActiveFilters && !isFilterExpanded" class="active-filter-count">
            {{ activeFilterCount }}
          </span>
        </div>
      </div>
      
      <!-- 筛选区域 -->
      <div 
        class="filter-collapse-container"
        :class="{ expanded: isFilterExpanded }"
      >
        <div class="filter-collapse-content">
          <!-- 付费类型筛选 -->
          <!-- <div class="filter-section">
            <label class="filter-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="filter-label-icon">
                <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                <path d="M16 8l-4 4-4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              付费类型
            </label>
            <div class="filter-buttons">
              <button 
                :class="['filter-btn', { active: tempPaymentFilter === 'all' }]"
                @click="setPaymentFilter('all')"
              >
                全部
              </button>
              <button 
                :class="['filter-btn', { active: tempPaymentFilter === 'free' }]"
                @click="setPaymentFilter('free')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                  <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                免费
              </button>
              <button 
                :class="['filter-btn', { active: tempPaymentFilter === 'paid' }]"
                @click="setPaymentFilter('paid')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                  <path d="M12 1v6m0 6v6m8-9a8 8 0 1 1-16 0 8 8 0 0 1 16 0ZM8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                付费
              </button>
            </div>
          </div> -->
          
          <!-- 输入类型筛选 -->
          <div class="filter-section">
            <label class="filter-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="filter-label-icon">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="16" y1="13" x2="8" y2="13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="16" y1="17" x2="8" y2="17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="10,9 9,9 8,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('chat.model.inputType') }}
            </label>
            <div class="filter-tags">
              <button
                v-for="type in inputTypes"
                :key="type.value"
                class="filter-tag"
                :class="{ active: tempInputTypeFilter.includes(type.value) }"
                @click="toggleInputType(type.value)"
              >
                {{ t(`chat.model.${type.value}`) }}
              </button>
            </div>
          </div>
          
          <!-- 输出类型筛选 -->
          <div class="filter-section">
            <label class="filter-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="filter-label-icon">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <polyline points="14,2 14,8 20,8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="18" cy="15" r="3" stroke="currentColor" stroke-width="2"/>
                <path d="M16.5 16.5L21 21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('chat.model.outputType') }}
            </label>
            <div class="filter-tags">
              <button
                v-for="type in outputTypes"
                :key="type.value"
                class="filter-tag"
                :class="{ active: tempOutputTypeFilter.includes(type.value) }"
                @click="toggleOutputType(type.value)"
              >
                {{ t(`chat.model.${type.value}`) }}
              </button>
            </div>
          </div>
          
          <!-- 推理支持筛选 -->
          <div class="filter-section">
            <label class="filter-label">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" class="filter-label-icon">
                <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
              </svg>
              {{ t('chat.model.reasoningSupport') }}
            </label>
            <div class="filter-tabs">
              <button
                class="filter-tab"
                :class="{ active: tempSupportReasoningFilter === 'all' }"
                @click="setSupportReasoningFilter('all')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                  <circle cx="12" cy="12" r="3" fill="currentColor"/>
                </svg>
                {{ t('chat.model.all') }}
              </button>
              <button
                class="filter-tab"
                :class="{ active: tempSupportReasoningFilter === 'yes' }"
                @click="setSupportReasoningFilter('yes')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                  <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
                </svg>
                {{ t('chat.model.support') }}
              </button>
              <button
                class="filter-tab"
                :class="{ active: tempSupportReasoningFilter === 'no' }"
                @click="setSupportReasoningFilter('no')"
              >
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                  <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
                  <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="2"/>
                  <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="2"/>
                </svg>
                {{ t('chat.model.notSupport') }}
              </button>
            </div>
          </div>
          
          <!-- 筛选操作按钮 -->
          <div class="filter-actions">
            <button 
              class="apply-filters-btn" 
              @click="applyFilters"
              :disabled="!hasFilterChanges"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
              </svg>
              {{ t('chat.model.applyFilters') }}
            </button>
            <button 
              v-if="hasActiveFilters" 
              class="clear-filters-btn" 
              @click="clearAllFilters"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
                <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              {{ t('chat.model.all') }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="dropdown-content" v-show="!isFilterExpanded">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>{{ t('chat.sidebar.loading') }}</span>
      </div>
      
      <div v-else-if="error" class="error-state">
        <span>{{ error }}</span>
        <button class="retry-btn" @click="fetchModels">{{ t('common.retry') }}</button>
      </div>
      
      <div v-else class="models-list" ref="modelsListRef" @scroll="handleScroll">
        <!-- 显示搜索结果数量 -->
        <div v-if="hasActiveFilters && models.length > 0" class="search-results-info">
          {{ t('chat.model.foundModels', { total: total, shown: models.length }) }}
        </div>


        <!-- 无搜索结果提示 -->
        <div v-if="hasActiveFilters && models.length === 0 && !loading" class="no-results">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" class="no-results-icon">
            <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
            <path d="m21 21-4.35-4.35" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <p class="no-results-text">{{ t('chat.model.noResults') }}</p>
          <p class="no-results-hint">{{ t('chat.model.tryAdjustFilters') }}</p>
        </div>

        <template v-for="(model, index) in models" :key="model.id">
          <div 
            class="model-item"
            @click="selectModel(model)"
          >
            <div class="model-info">
              <div class="model-header">
                <img 
                  v-if="model.icon && !imageErrors[model.id]" 
                  :src="model.icon" 
                  :alt="model.name" 
                  class="model-icon" 
                  @error="handleImageError(model.id)"
                />
                <div class="model-icon-placeholder" v-else>
                  <img src="@/assets/svg/model-icon.svg" alt="Default Model Icon" class="model-icon-fallback" />
                </div>
                <div class="model-name-section">
                  <h4 class="model-name">{{ model.name }}</h4>
                  <!-- 支付模式标签 -->
                  <!-- <div class="payment-mode-section">
                    <span :class="['payment-mode-tag', model.paymentMode === 'FREE' ? 'free-tag' : 'paid-tag']">
                      <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                        <path v-if="model.paymentMode === 'FREE'" d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path v-else d="M12 1v6m0 6v6m8-9a8 8 0 1 1-16 0 8 8 0 0 1 16 0ZM8 12h8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                      {{ model.paymentMode === 'FREE' ? '免费' : '付费' }}
                    </span>
                  </div> -->
                </div>
              </div>
              
              <div class="model-capabilities">
                <div class="capability-tags-container">
                  <!-- 深度思考标签 -->
                  <span v-if="model.supportReasoning" class="capability-tag">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none">
                      <path d="M9 12l2 2 4-4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ t('chat.model.reasoning') }}
                  </span>
                  
                  <!-- 输入能力标签 -->
                  <span 
                    v-for="inputType in model.inputType" 
                    :key="`input-${inputType}`" 
                    class="capability-tag input-tag"
                  >
                    <el-icon size="12">
                      <Edit v-if="inputType === 'text'" />
                      <Picture v-else-if="inputType === 'image'" />
                      <Microphone v-else-if="inputType === 'audio'" />
                      <Document v-else-if="inputType === 'file'" />
                    </el-icon>
                    {{ getInputTypeLabel(inputType) }}
                  </span>
                  
                  <!-- 输出能力标签 -->
                  <span 
                    v-for="outputType in model.outputType" 
                    :key="`output-${outputType}`" 
                    v-show="outputType !== 'text'"
                    class="capability-tag output-tag"
                  >
                    <el-icon size="12">
                      <Edit v-if="outputType === 'text'" />
                      <Picture v-else-if="outputType === 'image'" />
                    </el-icon>
                    {{ getOutputTypeLabel(outputType) }}
                  </span>
                  
                  
                  <!-- 最大上下文标签 -->
                  <span 
                    v-if="model.maxTokens && model.maxTokens > 0"
                    class="capability-tag context-tag"
                  >
                    <el-icon size="12">
                      <Document />
                    </el-icon>
                    {{ formatTokens(model.maxTokens) }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 模型间分割线 -->
          <div v-if="index < models.length - 1" class="model-divider"></div>
        </template>
        
        <!-- 加载更多状态 -->
        <div v-if="loadingMore" class="loading-more">
          <el-icon class="is-loading">
            <Loading />
          </el-icon>
          <span>{{ t('chat.model.loadingMore') }}</span>
        </div>
        
        <!-- 已加载完毕提示 -->
        <div v-if="!hasMore && models.length > 0 && !loading" class="no-more">
          <span>{{ t('chat.model.allLoaded') }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed} from 'vue'
import { useI18n } from 'vue-i18n'
import { chatApi } from '@/api/chat/chat'
import type { GetModelsPageApi,ModelItem } from '@/api/chat/types'

import { Edit, Picture, Microphone, Document, Loading } from '@element-plus/icons-vue'
import { useChatStore } from '@/stores/modules/chat'

const { t } = useI18n()
const chatStore = useChatStore()


const emit = defineEmits<{
  'model-selected': [model: ModelItem | null]
}>()

const loading = ref(false)
const loadingMore = ref(false)
const error = ref('')
const models = ref<ModelItem[]>([])
const searchQuery = ref('')
const paymentFilter = ref<'all' | 'free' | 'paid'>('all')
const inputTypeFilter = ref<string[]>([])
const outputTypeFilter = ref<string[]>([])
const supportReasoningFilter = ref<'all' | 'yes' | 'no'>('all')
const isFilterExpanded = ref(false)

// 分页相关状态
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const hasMore = ref(true)

// 图片加载错误状态追踪
const imageErrors = ref<Record<string, boolean>>({})

// 临时筛选状态（用于批量应用）
const tempPaymentFilter = ref<'all' | 'free' | 'paid'>('all')
const tempInputTypeFilter = ref<string[]>([])
const tempOutputTypeFilter = ref<string[]>([])
const tempSupportReasoningFilter = ref<'all' | 'yes' | 'no'>('all')

// 滚动容器引用
const modelsListRef = ref<HTMLElement>()

// 输入类型选项
const inputTypes = [
  { value: 'text' },
  { value: 'image' },
  { value: 'audio' },
  { value: 'file' }
]

// 输出类型选项
const outputTypes = [
  { value: 'text' },
  { value: 'image' },
  { value: 'audio' },
  { value: 'file' }
]

// 搜索处理函数 - 使用防抖来避免频繁请求
let searchTimer: number | null = null
const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    fetchModels() // 重置并重新搜索
  }, 300) // 300ms 防抖
}

// 清除搜索
const clearSearch = () => {
  searchQuery.value = ''
}

// // 设置支付模式筛选
// const setPaymentFilter = (filter: 'all' | 'free' | 'paid') => {
//   tempPaymentFilter.value = filter
// }

// 设置输入类型筛选
const toggleInputType = (type: string) => {
  const index = tempInputTypeFilter.value.indexOf(type)
  if (index > -1) {
    tempInputTypeFilter.value.splice(index, 1)
  } else {
    tempInputTypeFilter.value.push(type)
  }
}

// 设置输出类型筛选
const toggleOutputType = (type: string) => {
  const index = tempOutputTypeFilter.value.indexOf(type)
  if (index > -1) {
    tempOutputTypeFilter.value.splice(index, 1)
  } else {
    tempOutputTypeFilter.value.push(type)
  }
}

// 设置推理支持筛选
const setSupportReasoningFilter = (filter: 'all' | 'yes' | 'no') => {
  tempSupportReasoningFilter.value = filter
}

// 应用筛选
const applyFilters = () => {
  paymentFilter.value = tempPaymentFilter.value
  inputTypeFilter.value = [...tempInputTypeFilter.value]
  outputTypeFilter.value = [...tempOutputTypeFilter.value]
  supportReasoningFilter.value = tempSupportReasoningFilter.value
  fetchModels()
  // 应用后收起筛选面板
  isFilterExpanded.value = false
}

// 清除所有筛选
const clearAllFilters = () => {
  searchQuery.value = ''
  paymentFilter.value = 'all'
  inputTypeFilter.value = []
  outputTypeFilter.value = []
  supportReasoningFilter.value = 'all'
  // 同时清除临时状态
  tempPaymentFilter.value = 'all'
  tempInputTypeFilter.value = []
  tempOutputTypeFilter.value = []
  tempSupportReasoningFilter.value = 'all'
  fetchModels()
}

// 检查是否有活跃的筛选条件
const hasActiveFilters = computed(() => {
  return searchQuery.value.trim() !== '' ||
         paymentFilter.value !== 'all' ||
         inputTypeFilter.value.length > 0 ||
         outputTypeFilter.value.length > 0 ||
         supportReasoningFilter.value !== 'all'
})

// 计算活跃筛选的数量
const activeFilterCount = computed(() => {
  let count = 0
  if (searchQuery.value.trim() !== '') count++
  if (paymentFilter.value !== 'all') count++
  if (inputTypeFilter.value.length > 0) count++
  if (outputTypeFilter.value.length > 0) count++
  if (supportReasoningFilter.value !== 'all') count++
  return count
})

// 检查是否有筛选变化（临时状态与当前状态是否不同）
const hasFilterChanges = computed(() => {
  return (
    tempPaymentFilter.value !== paymentFilter.value ||
    !arraysEqual(tempInputTypeFilter.value, inputTypeFilter.value) ||
    !arraysEqual(tempOutputTypeFilter.value, outputTypeFilter.value) ||
    tempSupportReasoningFilter.value !== supportReasoningFilter.value
  )
})

// 数组比较辅助函数
const arraysEqual = (a: string[], b: string[]) => {
  if (a.length !== b.length) return false
  return a.every(item => b.includes(item)) && b.every(item => a.includes(item))
}

// 获取输入类型标签
const getInputTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    'text': 'textInput',
    'image': 'imageInput',
    'audio': 'audioInput',
    'file': 'fileInput'
  }
  const key = labelMap[type]
  return key ? t(`chat.model.capabilities.${key}`) : type
}

// 获取输出类型标签
const getOutputTypeLabel = (type: string) => {
  const labelMap: Record<string, string> = {
    'text': 'textOutput',
    'image': 'imageOutput'
  }
  const key = labelMap[type]
  return key ? t(`chat.model.capabilities.${key}`) : type
}

// 格式化 tokens 数量
const formatTokens = (tokens: number) => {
  if (tokens >= 1000000) {
    return `${(tokens / 1000000).toFixed(1)}M`
  } else if (tokens >= 1000) {
    return `${(tokens / 1000).toFixed(0)}K`
  }
  return tokens.toString()
}

// 构建查询参数
const buildParams = (page: number = 1): GetModelsPageApi.Params => {
  const params: GetModelsPageApi.Params = {
    page,
    size: pageSize.value
  }
  
  // 支付模式筛选
  // if (paymentFilter.value === 'free') {
  //   params.paymentMode = PaymentModeEnum.FREE
  // } else if (paymentFilter.value === 'paid') {
  //   params.paymentMode = PaymentModeEnum.PAID
  // }
  
  // 搜索名称
  if (searchQuery.value.trim()) {
    params.name = searchQuery.value.trim()
  }
  
  // 输入类型筛选
  if (inputTypeFilter.value.length > 0) {
    params.inputType = inputTypeFilter.value.join(',')
  }
  
  // 输出类型筛选
  if (outputTypeFilter.value.length > 0) {
    params.outputType = outputTypeFilter.value.join(',')
  }
  
  // 推理支持筛选
  if (supportReasoningFilter.value === 'yes') {
    params.supportReasoning = true
  } else if (supportReasoningFilter.value === 'no') {
    params.supportReasoning = false
  }
  
  return params
}

// 获取模型列表（重置）
const fetchModels = async () => {
  try {
    loading.value = true
    error.value = ''
    currentPage.value = 1
    
    const params = buildParams(1)
    const response = await chatApi.reqGetModelsPage(params)
    
    models.value = response?.items || []
    total.value = response?.total || 0
    hasMore.value = models.value.length < total.value
  } catch (err) {
    error.value = t('chat.model.fetchFailed')
    console.error('Failed to fetch models:', err)
  } finally {
    loading.value = false
  }
}

// 加载更多模型
const loadMoreModels = async () => {
  if (loadingMore.value || !hasMore.value) return
  
  try {
    loadingMore.value = true
    const nextPage = currentPage.value + 1
    
    const params = buildParams(nextPage)
    const response = await chatApi.reqGetModelsPage(params)
    
    if (response?.items) {
      models.value.push(...response.items)
      currentPage.value = nextPage
      total.value = response.total
      hasMore.value = models.value.length < total.value
    }
  } catch (err) {
    console.error('Failed to load more models:', err)
  } finally {
    loadingMore.value = false
  }
}

// 滚动事件处理
const handleScroll = (event: Event) => {
  const element = event.target as HTMLElement
  const scrollHeight = element.scrollHeight
  const scrollTop = element.scrollTop
  const clientHeight = element.clientHeight
  
  // 当滚动到底部附近时加载更多
  if (scrollHeight - scrollTop - clientHeight < 100 && hasMore.value && !loadingMore.value) {
    loadMoreModels()
  }
}

// 选择模型
const selectModel = (model:ModelItem) => {
  // 确保有活跃会话，但不传递模型ID
  if (!chatStore.activeSessionId) {
    chatStore.ensureActiveSessionOnEnter(null)
  }
  // 模型选择通过emit传递，由useChatStream的handleModelSelected处理
  emit('model-selected', model)
}


// 处理图片加载错误
const handleImageError = (modelId: string) => {
  imageErrors.value[modelId] = true
}

// 初始化临时筛选状态
const initTempFilters = () => {
  tempPaymentFilter.value = paymentFilter.value
  tempInputTypeFilter.value = [...inputTypeFilter.value]
  tempOutputTypeFilter.value = [...outputTypeFilter.value]
  tempSupportReasoningFilter.value = supportReasoningFilter.value
}

onMounted(() => {
  fetchModels()
  initTempFilters()
})
</script>

<style scoped>
.model-dropdown-content {
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 搜索区域样式 */
.search-section {
  padding: 16px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
  background: var(--el-bg-color);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.search-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: var(--el-text-color-placeholder);
  z-index: 1;
}

.search-input {
  width: 100%;
  padding: 10px 12px 10px 40px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  font-size: 14px;
  background: var(--el-fill-color-extra-light);
  color: var(--el-text-color-primary);
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--el-color-primary);
  background: var(--el-bg-color);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.search-input::placeholder {
  color: var(--el-text-color-placeholder);
}

.clear-search-btn {
  position: absolute;
  right: 8px;
  background: none;
  border: none;
  color: var(--el-text-color-placeholder);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.clear-search-btn:hover {
  color: var(--el-text-color-regular);
  background: var(--el-fill-color-light);
}

/* 筛选区域样式 */
.filter-section {
  margin-top: 4px;
}

.filter-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  background: var(--el-fill-color-extra-light);
  color: var(--el-text-color-regular);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.filter-btn:hover {
  background: var(--el-fill-color-light);
  border-color: var(--el-border-color);
  color: var(--el-text-color-primary);
}

.filter-btn.active {
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
  color: white;
}

.filter-btn svg {
  opacity: 0.8;
}

.filter-btn.active svg {
  opacity: 1;
}

/* 搜索结果信息样式 */
.search-results-info {
  padding: 12px 16px;
  font-size: 13px;
  color: var(--el-text-color-regular);
  background: var(--el-fill-color-extra-light);
  border-radius: 6px;
  margin: 8px 16px;
  text-align: center;
}

/* 无搜索结果样式 */
.no-results {
  padding: 40px 20px;
  text-align: center;
  color: var(--el-text-color-placeholder);
}

.no-results-icon {
  opacity: 0.5;
  margin-bottom: 16px;
}

.no-results-text {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-regular);
  margin: 0 0 8px 0;
}

.no-results-hint {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
  margin: 0;
}

.dropdown-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dropdown-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0;
}

.dropdown-content {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}

.loading-state,
.error-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--el-text-color-regular);
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--el-border-color-light);
  border-top: 2px solid var(--el-color-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.retry-btn {
  margin-top: 12px;
  padding: 6px 16px;
  background: var(--el-color-primary);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.models-list {
  padding: 0;
  overflow-y: auto;
  max-height: calc(80vh - 200px);
  min-height: 200px;
}

.model-item {
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
  margin: 8px;
}

.model-item:hover {
  background: var(--el-fill-color-light);
  border-color: var(--el-color-primary-light-7);
}

.model-info {
  width: 100%;
}

.model-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 8px;
}

.model-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  object-fit: cover;
  background: #000000;
  padding: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.model-icon-placeholder {
  width: 32px;
  height: 32px;
  background: #000000;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  border: 1px solid #333333;
}

.model-icon-fallback {
  width: 18px;
  height: 18px;
  color: #ffffff;
}

.model-name-section {
  flex: 1;
  min-width: 0;
}

.model-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 6px 0;
  line-height: 1.3;
}

/* 支付模式标签区域样式 */
.payment-mode-section {
  margin-top: 4px;
}

.payment-mode-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  border: 1px solid;
  transition: all 0.2s ease;
}

/* 免费标签样式 */
.free-tag {
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  color: white;
  border-color: #52c41a;
}

/* 付费标签样式 */
.paid-tag {
  background: var(--el-color-primary);
  color: white;
  border-color: var(--el-color-primary);
}

.model-capabilities {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-divider {
  height: 1px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    var(--el-border-color-lighter) 15%, 
    var(--el-border-color) 50%, 
    var(--el-border-color-lighter) 85%, 
    transparent 100%
  );
  margin: 12px 0;
  opacity: 0.8;
}

.capability-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.capability-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 500;
  line-height: 1;
  transition: all 0.2s ease;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-regular);
  border: 1px solid var(--el-border-color-light);
}

.capability-tag:hover {
  background: var(--el-fill-color);
  border-color: var(--el-border-color);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.capability-tag svg {
  opacity: 0.7;
  transition: opacity 0.2s ease;
}

.capability-tag:hover svg {
  opacity: 1;
}



/* 深度思考标签样式 */
.reasoning-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: 1px solid #667eea;
}

.reasoning-tag:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}




/* 新筛选器样式 */
.filter-section {
  margin-bottom: 16px;
}

.filter-label {
  display: block;
  color: var(--el-text-color-regular);
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.filter-tag {
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-light);
  color: var(--el-text-color-regular);
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tag:hover {
  background: var(--el-fill-color);
  border-color: var(--el-border-color);
  color: var(--el-text-color-primary);
}

.filter-tag.active {
  background: var(--el-color-primary);
  border-color: var(--el-color-primary);
  color: white;
  font-weight: 600;
}

/* 筛选操作按钮容器 */
.filter-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-extra-light);
  margin-top: 16px;
}

/* 应用筛选按钮样式 */
.apply-filters-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: var(--el-color-primary);
  border: 1px solid var(--el-color-primary);
  border-radius: 6px;
  color: white;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
  flex: 1;
  justify-content: center;
}

.apply-filters-btn:hover:not(:disabled) {
  background: var(--el-color-primary-dark-2);
  border-color: var(--el-color-primary-dark-2);
}

.apply-filters-btn:disabled {
  background: var(--el-fill-color-light);
  border-color: var(--el-border-color-light);
  color: var(--el-text-color-disabled);
  cursor: not-allowed;
}

.clear-filters-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-light);
  color: var(--el-color-danger);
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  justify-content: center;
}

.clear-filters-btn:hover {
  background: var(--el-fill-color);
  border-color: var(--el-color-danger-light-6);
}

.clear-filters-btn svg {
  flex-shrink: 0;
}

/* 推理筛选按钮样式 */
.filter-tabs {
  display: flex;
  background: var(--el-fill-color-extra-light);
  border-radius: 10px;
  padding: 3px;
  gap: 2px;
  border: 1px solid var(--el-border-color-extra-light);
}

.filter-tab {
  flex: 1;
  padding: 8px 12px;
  border: none;
  background: transparent;
  color: var(--el-text-color-regular);
  font-size: 13px;
  font-weight: 500;
  border-radius: 7px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  white-space: nowrap;
  text-align: center;
}

.filter-tab:hover:not(.active) {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  transform: translateY(-1px);
}

.filter-tab.active {
  background:var(--el-color-primary);
  color: white;
  font-weight: 600;

  transform: translateY(-1px);
}

.filter-tab.active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, transparent 100%);
  border-radius: 7px;
  pointer-events: none;
}

.filter-tab svg {
  margin-right: 4px;
  vertical-align: middle;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.filter-label-icon {
  color: var(--el-color-primary);
}

/* 搜索和筛选布局 */
.search-and-filter-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input-wrapper {
  flex: 1;
}

/* 筛选折叠按钮样式 */
.filter-toggle-section {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.filter-toggle-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid var(--el-border-color-light);
  background: var(--el-fill-color-extra-light);
  color: var(--el-text-color-primary);
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.filter-toggle-btn:hover {
  background: var(--el-fill-color-light);
  border-color: var(--el-border-color);
  transform: translateY(-1px);
}

.filter-toggle-arrow {
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.filter-toggle-arrow.expanded {
  transform: rotate(180deg);
}

.active-filter-count {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  background: var(--el-color-primary);
  color: white;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  padding: 0 6px;
}

/* 筛选折叠容器样式 */
.filter-collapse-container {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.filter-collapse-container.expanded {
  max-height: 60vh; /* 允许筛选面板占据更多空间 */
  overflow-y: auto; /* 如果筛选内容太多，允许滚动 */
}

.filter-collapse-content {
  padding-top: 8px;
}

/* 加载更多样式 */
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.loading-more .el-icon {
  color: var(--el-color-primary);
}

/* 已加载完毕样式 */
.no-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  color: var(--el-text-color-placeholder);
  font-size: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
  margin-top: 8px;
}

/* 使用全局滚动条样式 */
</style> 
<template>
  <div class="content-section">
    <div class="section-header">
      <div class="section-title">
        <el-icon><List /></el-icon>
        <h2>{{ t('profile.transactions.title') }}</h2>
      </div>
      <div class="section-actions">
        <el-select 
          v-model="selectedTransactionType" 
          @change="handleFilterChange"
          size="small"
          class="filter-select"
          :placeholder="t('profile.transactions.selectType')"
        >
          <el-option
            v-for="option in filterOptions"
            :key="option.value"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
        <el-button 
          @click="refreshTransactions" 
          :loading="transactionsLoading" 
          size="small"
          class="refresh-button"
        >
          <el-icon><Refresh /></el-icon>
          {{ t('profile.transactions.refresh') }}
        </el-button>
      </div>
    </div>
    
    <div class="transactions-container" ref="transactionsContainer">
      <div v-if="transactionsLoading && transactions.length === 0" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="transactions.length === 0" class="empty-state">
        <div class="empty-icon">
          <el-icon><DocumentRemove /></el-icon>
        </div>
        <p>{{ t('profile.transactions.noRecords') }}</p>
      </div>
      
      <div v-else class="transactions-list">
        <TransactionItem 
          v-for="transaction in transactions" 
          :key="transaction.id"
          :transaction="transaction"
        />
      </div>

      <div v-if="transactionsLoading && transactions.length > 0" class="scroll-loading">
        <div class="loading-spinner">
          <el-icon class="is-loading"><Loading /></el-icon>
        </div>
        <span class="loading-text">{{ t('profile.transactions.loading') }}</span>
      </div>

      <div v-else-if="transactions.length > 0 && !hasMoreTransactions" class="no-more">
        <el-divider>
          <span class="no-more-text">{{ t('profile.transactions.allLoaded') }}</span>
        </el-divider>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElNotification, ElSkeleton } from 'element-plus'
import { 
  List, Refresh, DocumentRemove, Loading
} from '@element-plus/icons-vue'
import { userApi } from '@/api/user/user'
import type { GetCreditTransactionsApi } from '@/api/user/types'
import { TransactionType } from '@/enums/user'
import TransactionItem from './TransactionItem.vue'

const { t } = useI18n()

// Reactive data
const transactionsContainer = ref(null)
const transactions = ref<GetCreditTransactionsApi.CreditTransaction[]>([])
const transactionsLoading = ref(false)
const currentPage = ref(1)
const totalTransactions = ref(0)
const selectedTransactionType = ref<TransactionType | 'ALL'>('ALL')

// 筛选选项配置
const filterOptions = computed(() => [
  { value: 'ALL', label: t('profile.transactions.allTransactions') },
  { value: TransactionType.RECHARGE, label: t('profile.transactions.recharge') },
  { value: TransactionType.CONSUME, label: t('profile.transactions.consume') },
  { value: TransactionType.REFUND, label: t('profile.transactions.refund') },
  { value: TransactionType.FREEZE, label: t('profile.transactions.freeze') }
])

// Computed properties
const hasMoreTransactions = computed(() => {
  return transactions.value.length < totalTransactions.value
})

// Fetch credit transactions
const fetchTransactions = async (page: number = 1, append: boolean = false) => {
  if (transactionsLoading.value) return
  
  transactionsLoading.value = true
  
  try {
    const params: any = {
      page,
      size: 10
    }
    
    if (selectedTransactionType.value !== 'ALL') {
      params.transactionType = selectedTransactionType.value
    }
    
    const result = await userApi.reqGetCreditTransactions(params)
    
    if (append) {
      transactions.value.push(...result.items)
    } else {
      transactions.value = result.items
    }
    
    totalTransactions.value = result.total
    currentPage.value = page
  } catch (error) {
    console.error('获取积分流水失败:', error)
    ElNotification.error(t('profile.transactions.fetchError'))
  } finally {
    transactionsLoading.value = false
  }
}

const refreshTransactions = () => {
  currentPage.value = 1
  fetchTransactions(1, false)
}

const loadMoreTransactions = () => {
  if (hasMoreTransactions.value && !transactionsLoading.value) {
    fetchTransactions(currentPage.value + 1, true)
  }
}

// 页面滚动事件处理
const handleScroll = () => {
  if (transactionsLoading.value || !hasMoreTransactions.value) {
    return
  }

  // 使用页面滚动而不是容器滚动
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop
  const windowHeight = window.innerHeight
  const documentHeight = document.documentElement.scrollHeight

  // 当滚动到距离底部200px时开始加载
  if (scrollTop + windowHeight >= documentHeight - 200) {
    loadMoreTransactions()
  }
}

// 筛选器改变处理
const handleFilterChange = () => {
  currentPage.value = 1
  fetchTransactions(1, false)
}

// Lifecycle
onMounted(async () => {
  // Load transactions
  await fetchTransactions()
  
  // Add scroll listener to window for page-level scrolling
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  // Remove scroll listener from window
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.content-section {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
  border: 1px solid var(--el-border-color-lighter);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-primary);
}

.section-title h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.section-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  min-width: 120px;
}

/* 筛选框和按钮样式 */
.filter-select :deep(.el-select) {
  width: 100%;
}

.filter-select :deep(.el-select__wrapper) {
  background-color: var(--el-fill-color-lighter) !important;
  border: 1px solid var(--el-border-color) !important;
  transition: all 0.2s ease !important;
  box-shadow: none !important;
  border-radius: 8px !important;
}

.filter-select :deep(.el-select__wrapper):hover {
  border-color: var(--el-border-color-dark) !important;
  background-color: var(--el-fill-color) !important;
  box-shadow: none !important;
}

.filter-select :deep(.el-select__wrapper.is-focused) {
  border-color: var(--el-color-primary) !important;
  background-color: var(--el-fill-color) !important;
  box-shadow: 0 0 0 1px var(--el-color-primary) !important;
}

.filter-select :deep(.el-select__placeholder) {
  color: var(--el-text-color-placeholder) !important;
}

.filter-select :deep(.el-select__selected-item) {
  color: var(--el-text-color-primary) !important;
}

.filter-select :deep(.el-select__caret) {
  color: var(--el-text-color-regular) !important;
}

.refresh-button {
  border: 1px solid var(--el-border-color) !important;
  background-color: var(--el-fill-color-lighter) !important;
  color: var(--el-text-color-regular) !important;
  transition: all 0.2s ease !important;
}

.refresh-button:hover {
  border-color: var(--el-border-color-dark) !important;
  background-color: var(--el-fill-color) !important;
  color: var(--el-text-color-primary) !important;
}

.refresh-button.is-loading {
  border-color: var(--el-color-primary) !important;
  background-color: var(--el-fill-color) !important;
  color: var(--el-color-primary) !important;
}

.transactions-container {
  min-height: 200px;
  /* 移除 max-height 和 overflow-y，让内容自然撑开 */
  border-radius: 8px;
}

.loading-state {
  padding: 20px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--el-text-color-placeholder);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.scroll-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px 0;
  color: var(--el-text-color-regular);
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.loading-text {
  font-size: 14px;
}

.no-more {
  padding: 20px 0;
  text-align: center;
}

.no-more-text {
  font-size: 14px;
  color: var(--el-text-color-placeholder);
}

/* Responsive Design */
@media (max-width: 768px) {
  .content-section {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .section-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .section-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .filter-select {
    flex: 1;
    min-width: auto;
    max-width: 200px;
  }
}

/* 全局下拉面板样式 */
:global(.el-select-dropdown) {
  background-color: var(--el-bg-color-overlay) !important;
  border: 1px solid var(--el-border-color) !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1) !important;
}

:global(.el-select-dropdown__item) {
  color: var(--el-text-color-primary) !important;
  background-color: transparent !important;
  transition: all 0.2s ease !important;
}

:global(.el-select-dropdown__item):hover {
  background-color: var(--el-fill-color-lighter) !important;
}

:global(.el-select-dropdown__item.is-selected) {
  background-color: transparent !important;
  color: var(--el-color-primary) !important;
  font-weight: 500 !important;
}
</style> 
<template>
  <div class="search-box">
    <div class="search-wrapper">
      <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none">
        <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
        <path d="m21 21-4.35-4.35" stroke="currentColor" stroke-width="2"/>
      </svg>
      <input 
        v-model="searchValue"
        class="search-input" 
        :placeholder="placeholder"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
        @keydown.enter="handleEnter"
      />
      <button 
        v-if="searchValue" 
        class="clear-btn"
        @click="clearSearch"
        title="清空搜索"
      >
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none">
          <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  placeholder?: string
  modelValue?: string
  debounce?: number
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: 'Search rooms...',
  modelValue: '',
  debounce: 300
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'search': [value: string]
  'focus': []
  'blur': []
  'enter': [value: string]
  'clear': []
}>()

const searchValue = ref(props.modelValue)
const isFocused = ref(false)
let debounceTimer: number | null = null

// 监听外部传入的值变化
watch(() => props.modelValue, (newValue) => {
  searchValue.value = newValue
})

// 监听搜索值变化并发出事件
watch(searchValue, (newValue) => {
  emit('update:modelValue', newValue)
  
  // 防抖搜索
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  
  debounceTimer = window.setTimeout(() => {
    emit('search', newValue)
  }, props.debounce)
})

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  searchValue.value = target.value
}

const handleFocus = () => {
  isFocused.value = true
  emit('focus')
}

const handleBlur = () => {
  isFocused.value = false
  emit('blur')
}

const handleEnter = () => {
  emit('enter', searchValue.value)
}

const clearSearch = () => {
  searchValue.value = ''
  emit('clear')
}
</script>

<style scoped>
.search-box {
  padding: 12px 12px 16px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
}

.search-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 12px;
  color: var(--el-text-color-placeholder);
  z-index: 1;
  transition: color 0.2s ease;
}

.search-input {
  width: 100%;
  height: 36px;
  border: 1px solid transparent;
  border-radius: 10px;
  padding: 0 40px 0 36px;
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
}

.search-input:focus {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-9);
}

.search-input:focus + .search-icon {
  color: var(--el-color-primary);
}

.search-input::placeholder {
  color: var(--el-text-color-placeholder);
}

.clear-btn {
  position: absolute;
  right: 8px;
  width: 24px;
  height: 24px;
  border: none;
  background: transparent;
  color: var(--el-text-color-placeholder);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 1;
}

.clear-btn:hover {
  background: var(--el-fill-color);
  color: var(--el-text-color-regular);
}

.clear-btn:active {
  transform: scale(0.95);
}

/* 当搜索框获得焦点时的样式 */
.search-wrapper:focus-within .search-icon {
  color: var(--el-color-primary);
}
</style> 
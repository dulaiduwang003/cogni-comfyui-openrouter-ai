<template>
  <el-image-viewer
    v-if="visible"
    :url-list="props.urls"
    :initial-index="currentIndex"
    :hide-on-click-modal="true"
    :teleported="true"
    @close="handleClose"
    @switch="onSwitch"
  />
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  modelValue: boolean
  urls: string[]
  startIndex?: number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  urls: () => [],
  startIndex: 0
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'closed'): void
}>()

const visible = ref<boolean>(props.modelValue)
const currentIndex = ref<number>(Math.min(Math.max(props.startIndex || 0, 0), Math.max(props.urls.length - 1, 0)))

watch(() => props.modelValue, (v) => { visible.value = v })
watch(visible, (v) => {
  emit('update:modelValue', v)
  if (!v) emit('closed')
})

watch(() => props.startIndex, (newIndex) => {
  if (typeof newIndex === 'number') {
    currentIndex.value = Math.min(Math.max(newIndex, 0), Math.max(props.urls.length - 1, 0))
  }
})

const handleClose = () => {
  visible.value = false
}

const onSwitch = (index: number) => {
  currentIndex.value = index
}
</script>

<style scoped>
/* Element Plus ImageViewer 自带完整功能，无需额外样式 */
</style>

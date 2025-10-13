<template>
  <div class="waterfall-container" ref="containerRef">
    <Waterfall
      v-if="containerWidth > 0"
      ref="waterfallRef"
      :list="processedItems"
      :gutter="10"
      :breakpoints="{
        1200: { rowPerView: 5 },
        1000: { rowPerView: 4 },
        800: { rowPerView: 3 },
        500: { rowPerView: 2 },
        0: { rowPerView: 1 }
      }"
      :hasAroundGutter="false"
      backgroundColor="transparent"
      :animation-effect="'fadeInUp'"
      :animation-delay="100"
      :lazyload="true"
      :delay="300"
    >
      <template #default="{ item }">
        <div class="waterfall-item" :key="item.id" @click="handleItemClick(item)">
          <!-- 普通图片预览 -->
          <div v-if="!imageErrors[item.id]" class="image-wrapper">
            <LazyImg 
              :url="item.imageUrl" 
              :alt="item.title"
              class="waterfall-image"
              @error="() => handleImageError(item)"
              @loaded="() => handleImageLoad(item)"
            />
          </div>
          <!-- 错误状态 -->
          <div v-else class="image-error-placeholder">
            <el-icon size="48" color="#ccc"><Picture /></el-icon>
            <p>图片加载失败</p>
          </div>
          
          <div class="tag">{{ item.categoryName }}</div>
          <div class="title-overlay">
            <h3>{{ item.title }}</h3>
          </div>
        </div>
      </template>
    </Waterfall>
    <!-- 加载状态 -->
    <div v-else-if="containerWidth === 0" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { LazyImg, Waterfall } from 'vue-waterfall-plugin-next'
import { Picture } from '@element-plus/icons-vue'
import 'vue-waterfall-plugin-next/dist/style.css'

interface WaterfallItem {
  id: number;
  imageUrl: string;
  title: string;
  categoryName: string;
  imageError?: boolean;
}

const { items } = defineProps<{
  items: WaterfallItem[]
}>();

const containerRef = ref<HTMLElement | null>(null);
const waterfallRef = ref<any>(null);
const containerWidth = ref(0);
const imageErrors = ref<Record<number, boolean>>({});
const imageLoaded = ref<Record<number, boolean>>({});
let resizeObserver: ResizeObserver | null = null;
let refreshTimer: ReturnType<typeof setTimeout> | null = null;


const processedItems = computed(() => {
  return items.map(item => ({
    ...item,

    key: `workflow-${item.id}`
  }));
});

// 刷新瀑布流布局（防抖版本）
const refreshLayout = async () => {
  if (refreshTimer) {
    clearTimeout(refreshTimer);
  }
  
  refreshTimer = setTimeout(async () => {
    if (waterfallRef.value) {
      // 等待 DOM 更新
      await nextTick();
      // 再等待一帧确保图片完全渲染
      requestAnimationFrame(() => {
        waterfallRef.value?.renderer();
      });
    }
  }, 100);
};



const onScroll = () => {
  if (!containerRef.value) return;
  const { scrollTop, scrollHeight, clientHeight } = containerRef.value;
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    emit('reach-bottom');
  }
};

onMounted(async () => {
  if (containerRef.value) {
    containerRef.value.addEventListener('scroll', onScroll);
    resizeObserver = new ResizeObserver(entries => {
      for (const entry of entries) {
        containerWidth.value = entry.contentRect.width;
      }
    });
    resizeObserver.observe(containerRef.value);
  }
  
  // 组件挂载后刷新一次布局
  await nextTick();
  setTimeout(() => {
    refreshLayout();
  }, 500);
});

onUnmounted(() => {
  if (containerRef.value && resizeObserver) {
    resizeObserver.unobserve(containerRef.value);
  }
  if (containerRef.value) {
    containerRef.value.removeEventListener('scroll', onScroll);
  }
});

const emit = defineEmits(['item-click', 'reach-bottom']);

const handleItemClick = (item: WaterfallItem) => {
  emit('item-click', item);
};

const handleImageError = (item: WaterfallItem) => {
  console.warn('图片加载失败:', item.imageUrl, 'ID:', item.id);

  imageErrors.value[item.id] = true;
};

const handleImageLoad = async (item: WaterfallItem) => {
  console.log('图片加载成功:', item.imageUrl, 'ID:', item.id);
 
  imageLoaded.value[item.id] = true;
  imageErrors.value[item.id] = false;
  
  // 等待图片渲染完成后刷新布局
  await nextTick();
  setTimeout(() => {
    refreshLayout();
  }, 50);
};


watch(() => items, async (newItems) => {
  const currentIds = new Set(newItems.map(item => item.id));

  Object.keys(imageErrors.value).forEach(key => {
    const id = Number(key);
    if (!currentIds.has(id)) {
      delete imageErrors.value[id];
      delete imageLoaded.value[id];
    }
  });
  
  // 数据变化时刷新布局，给图片加载留出时间
  await nextTick();
  setTimeout(() => {
    refreshLayout();
  }, 500);
}, { deep: true });

// 监听容器宽度变化，刷新布局
watch(containerWidth, async () => {
  await nextTick();
  setTimeout(() => {
    refreshLayout();
  }, 100);
});
</script>

<style>
.waterfall-container {
  width: 100%;
  height: 100%;
  overflow: auto;

}

.waterfall-item {
  background-color: transparent;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;

  min-height: 200px;
}

.waterfall-item:hover {
  transform: translateY(-2px);
}

.image-wrapper {
  width: 100%;
  position: relative;
  /* 防止图片加载时布局跳动 */
  display: block;
  min-height: 200px;
  background-color: var(--el-fill-color-light);
  border-radius: 8px;
}

.waterfall-item .tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 8px;
  border-radius: 15px;
  font-size: 12px;
  z-index: 1;
}

.title-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0) 100%);
  padding: 20px 12px 10px 12px;
  color: white;
}

.title-overlay h3 {
  margin: 0;
  font-size: 14px;
  font-weight: normal;
  text-align: left;
}


.lazy__img[lazy=loading] {
  padding: 5em 0;
  width: 48px;
}

.lazy__img[lazy=loaded] {
  width: 100%;
  border-radius: 8px;
  transition: transform 0.3s ease;
}

.lazy__img[lazy=loaded]:hover {
  transform: scale(1.02);
}

.waterfall-image {
  width: 100%;
  border-radius: 8px;
  transition: transform 0.3s ease;
  display: block;
  height: auto;
}

.waterfall-image:hover {
  transform: scale(1.02);
}

.lazy__img {
  width: 100% !important;
  height: auto !important;
  display: block !important;
}

.lazy__img[lazy=error] {
  padding: 5em 0;
  width: 48px;
}

.image-error-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
}

.image-error-placeholder p {
  margin: 8px 0 0 0;
  font-size: 14px;
  color: var(--el-text-color-placeholder);
}

.loading-container {
  padding: 20px;
  width: 100%;
}
</style>

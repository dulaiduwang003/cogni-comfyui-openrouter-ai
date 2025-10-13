<template>
  <div class="comfyui-page-container page-container" :class="{ 'has-right-panel': selectedItem }">
    <!-- 左侧 -->
    <div class="left-panel">
        <Banner class="header-enter" />
        <SearchBar 
          class="searchbar-enter"
          v-model:search-input="searchParams.prompt"
          v-model:active-tab="searchParams.categoryId"
          :filters="filters"
        />
        <!-- 工作流瀑布流 -->
        <WaterfallFlow 
          class="content-enter"
          v-if="workflow.length > 0"
          :items="workflow" 
          @item-click="handleItemClick" 
        />
        
        <!-- 空状态 -->
        <EmptyState 
          class="empty-state-enter"
          v-else-if="!pagination.loading" 
        />
        
        <div ref="sentinel" class="scroll-sentinel"></div>
    </div>
    <!-- 右侧 -->
    <transition name="slide">
      <RightPanel
        v-if="selectedItem"
        class="right-panel-sticky"
        :item="selectedItem"
        @close="selectedItem = null"
      />
    </transition>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue';
import Banner from './components/Banner.vue';
import SearchBar from './components/SearchBar.vue';
import WaterfallFlow from './components/WaterfallFlow.vue';
import RightPanel from './components/RightPanel.vue';
import EmptyState from './components/EmptyState.vue';
import { comfyuiTaskApi } from '@/api/workflow-task/workflow-task';

const selectedItem = ref(null);
const sentinel = ref(null);
const workflow = ref([]);
const filters = ref([]);

const pagination = reactive({
  page: 1,
  hasMore: true,
  loading: false,
});

const searchParams = reactive({
  prompt: '',
  categoryId: 'all',
});

const fetchWorkflows = async (isNewSearch = false) => {
  if (pagination.loading) return;
  if (isNewSearch) {
    pagination.page = 1;
    pagination.hasMore = true;
    workflow.value = [];
  }
  if (!pagination.hasMore) return;

  pagination.loading = true;
  try {
    const params = {
      page: pagination.page.toString(),
      prompt: searchParams.prompt,
    };
    if (searchParams.categoryId && searchParams.categoryId !== 'all') {
      params.categoryId = Number(searchParams.categoryId);
    }

    const result = await comfyuiTaskApi.reqGetWorkflowResultModelflowsPage(params);
    const newItems = result.items.map(item => ({
      id: item.workflowId,
      imageUrl: item.url,
      title: item.name,
      categoryName: item.categoryName,
      originalData: item
    }));

    workflow.value.push(...newItems);
    if (workflow.value.length >= result.total) {
      pagination.hasMore = false;
    } else {
      pagination.page++;
    }
  } catch (error) {
    console.error('Failed to fetch workflow:', error);
  } finally {
    pagination.loading = false;
  }
};

const fetchFilters = async () => {
  try {
    const result = await comfyuiTaskApi.reqGetWorkflowResultModelflowFilterList();
    filters.value = result;
  } catch (error) {
    console.error('Failed to fetch filters:', error);
  }
};

let observer;

const initObserver = () => {
  if (!sentinel.value) return;
  observer = new IntersectionObserver((entries) => {
    const entry = entries[0];
    if (entry.isIntersecting && !pagination.loading && pagination.hasMore) {
      fetchWorkflows();
    }
  }, {
    root: null,
    rootMargin: '0px',
    threshold: 0.1,
  });
  observer.observe(sentinel.value);
};


watch(searchParams, () => {
  fetchWorkflows(true);
});


onMounted(() => {
  fetchFilters();
  fetchWorkflows(true);
  initObserver();
});

onUnmounted(() => {
  if (observer && sentinel.value) {
    observer.unobserve(sentinel.value);
    observer.disconnect();
  }
});


const handleItemClick = (item) => {
  // 传递完整的工作流数据给 RightPanel
  selectedItem.value = {
    workflowId: item.originalData.workflowId,
    name: item.originalData.name,
    description: item.originalData.description,
    url: item.originalData.url,
    categoryName: item.originalData.categoryName
  };
};
</script>

<style scoped>
.comfyui-page-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-start;
  align-items: flex-start;
  gap: 20px;
  background-color: var(--el-bg-color-page);
  width: 100%;
  margin: 0 auto;
  position: relative;
  padding: 10px;
}

.left-panel {
  flex: 1;
  margin: 0 auto;
  transition: margin-right 0.3s ease;
}


.comfyui-page-container.has-right-panel .left-panel {
  margin-right: 408px; 
}

.right-panel-sticky {
  position: fixed;
  top: 63px; 
  right: 10px; 
  height: calc(100vh - 80px); 
  z-index: 999;
  width: 400px;
}

.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.slide-leave-to {
  transform: translateX(100%);
  opacity: 0;
}

.slide-enter-to,
.slide-leave-from {
  transform: translateX(0);
  opacity: 1;
}
</style>
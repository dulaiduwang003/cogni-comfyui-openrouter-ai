<template>
    <div class="search-bar-wrapper">
        <!-- 左侧筛选按钮 -->
        <div class="filter-component" ref="filterRef">
            <div class="search-tabs" ref="tabsRef">
                <!-- 当前选中标签显示 -->
                <span 
                    v-if="activeTab" 
                    class="tab-item active"
                    @click="handleTabClick(activeTab)"
                >
                    {{ getTabLabel(activeTab) }}
                </span>
                <span 
                    v-for="tab in visibleFilterTabs" 
                    :key="tab.value"
                    class="tab-item" 
                    @click="handleTabClick(tab.value)"
                >
                    {{ tab.label }}
                </span>
            </div>
            <!-- 折叠按钮 -->
            <el-popover
                v-if="hasHiddenTabs"
                placement="bottom-start"
                :width="200"
                trigger="click"
                popper-class="filter-popover"
                v-model:visible="popoverVisible"
            >
                <template #reference>
                    <div class="more-button">
                        <el-icon><More /></el-icon>
                    </div>
                </template>
                <div class="hidden-tabs">
                    <span
                        v-for="tab in hiddenFilterTabs"
                        :key="tab.value"
                        class="tab-item"
                        @click="handleHiddenTabClick(tab.value)"
                    >
                        {{ tab.label }}
                    </span>
                </div>
            </el-popover>
        </div>

        <!-- 右侧搜索框 -->
        <div class="search-component">
            <el-icon :size="20" class="search-icon">
                <Search />
            </el-icon>
            <el-input 
                :model-value="searchInput"
                @update:model-value="emit('update:searchInput', $event)"
                class="custom-search-input" 
                :placeholder="t('comfyui.search.placeholder')" 
                clearable 
            />
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { Search, More } from '@element-plus/icons-vue';

const { t } = useI18n();

const props = defineProps({
    searchInput: String,
    activeTab: String,
    filters: {
        type: Array,
        default: () => []
    }
});

const emit = defineEmits(['update:searchInput', 'update:activeTab']);

const popoverVisible = ref(false);
const filterRef = ref(null);
const tabsRef = ref(null);

// 所有标签数据
const allTabs = computed(() => {
    const tabs = props.filters.map(f => ({ label: f.name, value: f.categoryId.toString() }));
    tabs.unshift({ label: t('comfyui.search.all'), value: 'all' });
    return tabs;
});

// 获取标签显示文本
const getTabLabel = (value) => {
    const tab = allTabs.value.find(t => t.value === value);
    return tab ? tab.label : '';
};

// 过滤掉当前选中的标签
const filterTabs = computed(() => {
    return allTabs.value.filter(tab => tab.value !== props.activeTab);
});

const visibleTabsCount = ref(allTabs.value.length);
const hasHiddenTabs = computed(() => visibleTabsCount.value < filterTabs.value.length);
const visibleFilterTabs = computed(() => filterTabs.value.slice(0, visibleTabsCount.value));
const hiddenFilterTabs = computed(() => filterTabs.value.slice(visibleTabsCount.value));

// 处理标签点击
const handleTabClick = (value) => {
    if (props.activeTab === value) {
        emit('update:activeTab', 'all'); // 再次点击取消选中
    } else {
        emit('update:activeTab', value);
    }
    nextTick(() => {
        checkOverflow();
    });
};

// 检查标签是否溢出
const checkOverflow = async () => {
    await nextTick();
    if (!filterRef.value || !tabsRef.value) return;

    const containerWidth = filterRef.value.clientWidth - 50; // 减去更多按钮的宽度
    let totalWidth = 0;
    let count = 0;

    const tabElements = tabsRef.value.children;
    for (let tab of tabElements) {
        totalWidth += tab.offsetWidth;
        if (totalWidth > containerWidth) break;
        count++;
    }

    visibleTabsCount.value = Math.max(1, count);
};

// 监听窗口大小变化
let resizeObserver;
onMounted(() => {
    checkOverflow();
    
    resizeObserver = new ResizeObserver(() => {
        checkOverflow();
    });

    if (filterRef.value) {
        resizeObserver.observe(filterRef.value);
    }
});

onUnmounted(() => {
    if (resizeObserver) {
        resizeObserver.disconnect();
    }
});

// 处理隐藏标签的点击
const handleHiddenTabClick = (value) => {
    handleTabClick(value);
    popoverVisible.value = false; // 关闭弹出面板
};
</script>

<style scoped>
.search-bar-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0px;
    width: 100%;
    margin: 0 auto;
    box-sizing: border-box;
    gap: 20px;
}

.filter-component {
    display: flex;
    align-items: center;
    background-color: var(--el-bg-color);
    border: 1px solid var(--el-border-color-light);
    border-radius: 20px;
    padding: 4px 10px;
    padding-left: 4px;
    box-shadow: var(--el-box-shadow-light);
    max-width: 100%;
    overflow: hidden;
}

.search-component {
    display: flex;
    align-items: center;
    background-color: var(--el-bg-color);
    border: 1px solid var(--el-border-color-light);
    border-radius: 20px;
    padding: 4px 16px;
    width: 300px;
    box-shadow: var(--el-box-shadow-light);
    flex-shrink: 0;
}

.search-tabs {
    display: flex;
    align-items: center;
    flex-wrap: nowrap;
    overflow: hidden;
}

.search-tabs .tab-item {
    padding: 4px 12px;
    margin-right: 4px;
    cursor: pointer;
    border-radius: 14px;
    color: var(--el-text-color-secondary);
    font-size: 0.85em;
    font-weight: 500;
    white-space: nowrap;
    transition: all 0.2s ease;
    flex-shrink: 0;
}

.more-button {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 26px;
    cursor: pointer;
    color: var(--el-text-color-secondary);
    border-radius: 14px;
    transition: all 0.2s ease;
    margin-left: 4px;
    flex-shrink: 0;
}

.more-button:hover {
    background-color: var(--el-fill-color-light);
    color: var(--el-text-color-primary);
}

.hidden-tabs {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 4px;
}

.hidden-tabs .tab-item {
    padding: 6px 12px;
    cursor: pointer;
    border-radius: 4px;
    transition: all 0.2s ease;
}

.tab-item:hover {
    color: var(--el-text-color-primary);
    background-color: var(--el-fill-color-light);
}

.tab-item.active {
    background-color: var(--el-color-primary);
    color: white;
}

.tab-item.active:hover {
    background-color: var(--el-color-primary-light-3);
    color: white;
}

.search-icon {
    flex-shrink: 0;
    margin-right: 6px;
    color: var(--el-text-color-placeholder);
}

.custom-search-input {
    flex-grow: 1;
}

.custom-search-input :deep(.el-input__wrapper) {
    background-color: transparent !important;
    border: none !important;
    box-shadow: none !important;
    padding: 0 !important;
    border-radius: 0;
}

.custom-search-input :deep(.el-input__inner) {
    color: var(--el-text-color-primary);
    font-size: 0.85em;
    height: 26px;
    line-height: 26px;
}

.custom-search-input :deep(.el-input__inner::placeholder) {
    color: var(--el-text-color-placeholder);
    font-weight: 400;
}

/* 深色主题适配 */
html[class*="theme-dark"] .filter-component,
html[class*="theme-dark"] .search-component {
    background-color: var(--el-bg-color-overlay);
}

html[class*="theme-dark"] .tab-item.active {
    background-color: var(--el-color-primary);
    color: white;
}

html[class*="theme-dark"] .tab-item.active:hover {
    background-color: var(--el-color-primary-light-3);
    color: white;
}

:deep(.filter-popover) {
    padding: 8px;
    min-width: 120px;
}
</style>
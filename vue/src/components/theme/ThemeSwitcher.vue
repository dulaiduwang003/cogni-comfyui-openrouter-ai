<template>
    <div class="theme-switcher">
        <el-dropdown trigger="click" @command="switchTheme" class="theme-dropdown">
            <span class="theme-trigger">
                <el-icon class="trigger-icon" :class="{ 'is-active': isDropdownVisible }">
                    <component :is="currentThemeIcon" />
                </el-icon>
            </span>
            <template #dropdown>
                <el-dropdown-menu class="theme-menu">
                    <el-dropdown-item
                        v-for="item in themeOptions"
                        :key="item.value"
                        :command="item.value"
                        :class="{ 'is-active': selectedTheme === item.value }"
                    >
                        <div class="theme-option">
                            <el-icon class="theme-icon">
                                <component :is="item.icon" />
                            </el-icon>
                            <span class="theme-label">{{ t(item.labelKey) }}</span>
                        </div>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { useTheme } from '@/composables/useTheme';

import { themeOptions, type ThemeName } from '@/constants/theme';

const { t } = useI18n();

const { currentTheme, setTheme } = useTheme();
const selectedTheme = ref<ThemeName>(currentTheme.value);
const isDropdownVisible = ref(false);



const currentThemeIcon = computed(() => {
    return themeOptions.find(item => item.value === selectedTheme.value)?.icon || themeOptions[0].icon;
});

const switchTheme = (themeName: ThemeName) => {
    selectedTheme.value = themeName;
    setTheme(themeName);
};
</script>

<style scoped>
.theme-switcher {
    display: inline-flex;
    align-items: center;
    margin: 0;
    width: auto;
}

.theme-dropdown {
    position: relative;
    width: auto;
}

.theme-trigger {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    padding: 8px;
    background-color: transparent;
    border: 1px solid var(--el-border-color);
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
    user-select: none;
}

.theme-trigger:hover {
    background-color: var(--el-fill-color-lighter);
    border-color: var(--el-border-color-dark);
}

.trigger-icon {
    font-size: 16px;
    color: var(--el-text-color-regular);
    flex-shrink: 0;
}

.trigger-text {
    display: none;
}

.arrow-icon {
    display: none;
}

.is-active .arrow-icon {
    transform: rotate(180deg);
}

:deep(.el-popper.is-light) {
    border-color: var(--el-border-color-light);
    background-color: var(--el-bg-color);
}

:deep(.el-popper.is-light .el-popper__arrow::before) {
    border-color: var(--el-border-color-light);
    background-color: var(--el-bg-color);
}

:deep(.el-dropdown-menu) {
    --el-dropdown-menuItem-hover-fill: var(--el-bg-color-overlay);
    --el-dropdown-menu-bg-color: var(--el-bg-color);
    background-color: var(--el-bg-color);
    border-color: var(--el-border-color-light);
}

:deep(.theme-menu) {
    width: auto;
    min-width: 160px;
    padding: 8px;
    border-radius: 12px;
    border: 1px solid var(--el-border-color-light);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    backdrop-filter: blur(10px);
    background-color: rgba(255, 255, 255, 0.95);
}

.theme-option {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 12px;
    font-size: 14px;
    border-radius: 8px;
    transition: all 0.2s ease;
}

:deep(.el-dropdown-menu__item) {
    padding: 2px;
    margin: 2px 0;
    color: var(--el-text-color-primary);
    border-radius: 8px;
}

:deep(.el-dropdown-menu__item.is-active) {
    color: var(--el-color-primary);
    font-weight: 500;
    background-color: transparent;
}

:deep(.el-dropdown-menu__item:hover) {
    background-color: transparent !important;
    color: var(--el-text-color-primary) !important;
}

:deep(.el-dropdown-menu__item.is-active:hover) {
    background-color: transparent !important;
    color: var(--el-color-primary) !important;
    font-weight: 500;
}

/* 强制覆盖所有可能的悬浮状态 */
:deep(.el-dropdown-menu__item:focus),
:deep(.el-dropdown-menu__item:focus-visible) {
    background-color: var(--el-bg-color-overlay) !important;
    color: var(--el-text-color-primary) !important;
}

:deep(.el-dropdown-menu__item.is-active:focus),
:deep(.el-dropdown-menu__item.is-active:focus-visible) {
    background-color: var(--el-bg-color-overlay) !important;
    color: var(--el-color-primary) !important;
}

.theme-icon {
    font-size: 16px;
    flex-shrink: 0;
}

.theme-label {
    font-size: 13px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* 动画效果 */
:deep(.el-dropdown-menu) {
    transform-origin: center top;
    animation: dropdownAnimation 0.2s ease-out;
}

@keyframes dropdownAnimation {
    from {
        opacity: 0;
        transform: scaleY(0.8) translateY(-10px);
    }
    to {
        opacity: 1;
        transform: scaleY(1) translateY(0);
    }
}

/* 深色主题下的面板样式 */
html[class^="theme-dark"] :deep(.theme-menu),
html[class*=" theme-dark"] :deep(.theme-menu) {
    background-color: rgba(30, 30, 30, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}
</style>
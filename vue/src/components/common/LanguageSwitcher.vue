<template>
  <el-dropdown @command="handleLanguageChange" trigger="click">
    <button class="navbar-button language-switch">
      <img :src="LanguageIcon" alt="language" class="navbar-icon" />
      <span class="navbar-text">{{ currentLanguageName }}</span>
      <el-icon class="dropdown-icon">
        <arrow-down />
      </el-icon>
    </button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in supportedLanguages"
          :key="lang.code"
          :command="lang.code"
          :class="{ 'is-active': currentLanguage === lang.code }"
        >
          <span class="language-item">
            <span v-if="lang.flag" class="language-flag">{{ lang.flag }}</span>
            <span class="language-name">{{ lang.name }}</span>
            <el-icon v-if="currentLanguage === lang.code" class="check-icon">
              <check />
            </el-icon>
          </span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElDropdown, ElDropdownMenu, ElDropdownItem, ElIcon } from 'element-plus'
import { ArrowDown, Check } from '@element-plus/icons-vue'
import LanguageIcon from '@/assets/svg/language.svg'
import { supportedLanguages, getLanguageName } from '@/i18n/languages.config'

const { locale } = useI18n()

// 当前语言
const currentLanguage = computed(() => locale.value)

// 当前语言名称
const currentLanguageName = computed(() => getLanguageName(locale.value))

// 切换语言
const handleLanguageChange = (languageCode: string) => {
  if (languageCode === locale.value) {
    return 
  }
  
  locale.value = languageCode

  localStorage.setItem('app-locale', languageCode)

  window.location.reload()
}
</script>

<style scoped>
.navbar-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background-color: transparent;
  border: 1px solid var(--el-border-color);
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--el-text-color-regular);
}

.navbar-button:hover {
  background-color: var(--el-fill-color-lighter);
  border-color: var(--el-border-color-dark);
}

.navbar-icon {
  width: 16px;
  height: 16px;
  filter: none;
}

html[class^="theme-dark"] .navbar-icon,
html[class*=" theme-dark"] .navbar-icon {
  filter: brightness(0) invert(1);
}

.navbar-text {
  font-weight: 500;
  white-space: nowrap;
}

.dropdown-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.language-item {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
}

.language-flag {
  font-size: 18px;
  line-height: 1;
}

.language-name {
  flex: 1;
}

.check-icon {
  color: var(--el-color-primary);
  font-size: 14px;
}

:deep(.el-dropdown-menu__item.is-active) {
  background-color: var(--el-fill-color-light);
}

/* 响应式适配 */
@media (max-width: 768px) {
  .navbar-text {
    display: none;
  }
}
</style> 
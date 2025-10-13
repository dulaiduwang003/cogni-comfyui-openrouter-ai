<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterView } from 'vue-router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
import { ElConfigProvider } from 'element-plus'

const { locale } = useI18n()

// 根据当前语言动态切换 Element Plus 的语言包
const elementLocale = computed(() => {
  return locale.value === 'en-US' ? en : zhCn
})
</script>

<template>
  <el-config-provider :locale="elementLocale">
    <RouterView />
  </el-config-provider>
</template>

<style>
/* 全局样式可以放在这里 */
</style>

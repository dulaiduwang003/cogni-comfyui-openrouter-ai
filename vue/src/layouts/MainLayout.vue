<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute } from 'vue-router'
import lottie from 'lottie-web'
import logoAnimation from '@/assets/lottie/logo.json'
import emitter, { OPEN_AUTH_DIALOG, OPEN_NOTICE_ANNOUNCER } from '@/utils/eventBusUtil'


import SidebarLogo from './components/SidebarLogo.vue'
import SidebarMenu from './components/SidebarMenu.vue'
import SidebarFooter from './components/SidebarFooter.vue'
import TopNavbar from './components/TopNavbar.vue'
import AuthDialog from '@/components/auth/AuthDialog.vue'
import RedemptionCodeDialog from '@/components/common/RedemptionCodeDialog.vue'
import NoticeAnnouncer from '@/components/notice/NoticeAnnouncer.vue'

const route = useRoute()

const shouldHideSidebar = computed(() => {
  return route.meta?.hideSidebar === true
})


const logoContainer = ref<HTMLElement | null>(null)
const noticeAnnouncerRef = ref<InstanceType<typeof NoticeAnnouncer> | null>(null)

const handleOpenNotice = () => {
  console.log('handleOpenNotice called, ref:', noticeAnnouncerRef.value)
  noticeAnnouncerRef.value?.manualOpen()
}

onMounted(() => {

  if (logoContainer.value) {
    lottie.loadAnimation({
      container: logoContainer.value,
      renderer: 'svg',
      loop: true,
      autoplay: true,
      animationData: logoAnimation
    })
  }

  console.log('Registering OPEN_NOTICE_ANNOUNCER listener')
  emitter.on(OPEN_NOTICE_ANNOUNCER, handleOpenNotice)
})

onBeforeUnmount(() => {

  emitter.off(OPEN_NOTICE_ANNOUNCER, handleOpenNotice)
})

const handleLogin = () => {
  emitter.emit(OPEN_AUTH_DIALOG)
}
</script>

<template>
  <div>
    <div class="layout-container">

      <div v-if="!shouldHideSidebar" class="sidebar">
        <SidebarLogo />
        <SidebarMenu />
        <SidebarFooter @login-click="handleLogin" />
      </div>

      <div class="main-content" :class="{ 'full-width': shouldHideSidebar }">
        <TopNavbar @login-click="handleLogin" />
        <div class="content-area">
          <router-view></router-view>
        </div>
      </div>
    </div>
    <AuthDialog />
    <RedemptionCodeDialog />
    <NoticeAnnouncer ref="noticeAnnouncerRef" />
  </div>
</template>

<style scoped>

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.layout-container {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  background-color: var(--el-bg-color-page);

}

/* Sidebar Styles */
.sidebar {
  width: 200px;
  min-width: 240px;
  background-color: var(--el-bg-color);
  color: var(--el-text-color-primary);

  display: flex;
  flex-direction: column;
  padding: 20px 12px;
  transition: width 0.3s ease, background-color 0.3s ease, color 0.3s ease;

  border-right: 1px solid var(--el-border-color-dark);

}

.main-content {
  flex: 1;
  background-color: var(--el-bg-color-page);

  color: var(--el-text-color-primary);
 
  display: flex;
  flex-direction: column;

  --grid-line-color: var(--el-border-color-lighter);

  background-image: radial-gradient(circle, var(--grid-line-color) 1px, transparent 1px);
  background-size: 30px 30px;
  overflow: hidden;
  transition: background-color 0.3s ease, color 0.3s ease;

}

.main-content.full-width {
  width: 100vw;
}

html[class^="theme-dark"] .main-content,
html[class*=" theme-dark"] .main-content {
  --grid-line-color: var(--el-border-color-darker);
}

.content-area {
  flex: 1;

  overflow-y: auto;

  height: calc(100vh - 60px);
}

.content-area.no-scroll {
  overflow-y: auto;
  height: auto;
  min-height: calc(100vh - 60px);
}

html[class^="theme-dark"] .login-button,
html[class*=" theme-dark"] .login-button {
  background-color: var(--custom-login-button-bg);
  color: var(--custom-login-button-text);
}

html[class^="theme-dark"] .login-button:hover,
html[class*=" theme-dark"] .login-button:hover {
  background-color: var(--custom-login-button-bg);
  filter: brightness(1.1);
}
</style>
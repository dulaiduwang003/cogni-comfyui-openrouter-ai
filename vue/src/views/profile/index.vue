<template>
  <div class="profile-page page-container">

    <ProfileBanner 
      class="header-enter"
      :user-info="userInfo || undefined"
      :user-credits="userCredits || undefined"
      @avatar-click="showAvatarDialog = true"
      @edit-nickname="showEditNicknameDialog = true"
    />

    <div class="profile-content content-enter">
 
      <CreditTransactions />
    </div>

    <el-dialog 
    align-center
      v-model="showAvatarDialog" 
      :title="t('profile.dialogs.changeAvatar')" 
      width="400px"
      :before-close="handleAvatarDialogClose"
      @closed="handleAvatarDialogClosed"
    >
      <AvatarUpload 
        ref="avatarUploadRef"
        @avatar-updated="showAvatarDialog = false"
        @cancel="showAvatarDialog = false"
      />
    </el-dialog>

    <el-dialog 
    align-center
      v-model="showEditNicknameDialog" 
      :title="t('profile.dialogs.editNickname')" 
      width="400px"
      @closed="handleEditNicknameDialogClosed"
    >
      <EditNickname 
        ref="editNicknameRef"
        :current-nickname="userInfo?.nickname || ''"
        @nickname-updated="showEditNicknameDialog = false"
        @cancel="showEditNicknameDialog = false"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/modules/user'
import ProfileBanner from './components/ProfileBanner.vue'
import CreditTransactions from './components/CreditTransactions.vue'
import AvatarUpload from './components/AvatarUpload.vue'
import EditNickname from './components/EditNickname.vue'

const { t } = useI18n()
const userStore = useUserStore()

const showAvatarDialog = ref(false)
const showEditNicknameDialog = ref(false)

const avatarUploadRef = ref()
const editNicknameRef = ref()

const userInfo = computed(() => userStore.getUserInfo)
const userCredits = computed(() => userStore.getUserCredits)

const handleAvatarDialogClose = () => {
  showAvatarDialog.value = false
}

const handleAvatarDialogClosed = () => {
  // 对话框关闭后重置组件状态
  if (avatarUploadRef.value?.reset) {
    avatarUploadRef.value.reset()
  }
}

const handleEditNicknameDialogClosed = () => {
  // 对话框关闭后重置组件状态
  if (editNicknameRef.value?.reset) {
    editNicknameRef.value.reset()
  }
}



onMounted(async () => {

  if (!userInfo.value) {
    await userStore.fetchUserInfo()
  }
  if (!userCredits.value) {
    await userStore.fetchUserCredits()
  }
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--el-bg-color-page);
  padding: 10px;
}

.profile-content {
  margin: 0 auto;
  /* 确保内容区域可以自然扩展 */
}

@media (max-width: 768px) {
  .profile-page {
    padding: 10px 5px;
  }
}
</style> 
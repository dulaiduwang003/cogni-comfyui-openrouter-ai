<template>
  <div class="user-management-view">
    <el-card shadow="never" class="content-card">
      <template #header>
        <div class="card-header">
          <span>{{ t('system.users.title') }}</span>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            {{ t('system.users.createUser') }}
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          :placeholder="t('system.users.searchPlaceholder')"
          clearable
          style="width: 240px"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.role"
          :placeholder="t('system.users.userRole')"
          clearable
          style="width: 160px"
          @change="handleSearch"
        >
          <el-option :label="t('system.users.roles.user')" :value="Role.USER" />
          <el-option :label="t('system.users.roles.admin')" :value="Role.ADMIN" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          {{ t('system.users.search') }}
        </el-button>
        <el-button class="reset-btn" @click="handleReset">
          <el-icon><RefreshRight /></el-icon>
          {{ t('system.users.reset') }}
        </el-button>
      </div>

      <!-- 用户列表 -->
      <div class="table-wrapper">
        <el-table
          v-loading="loading"
          element-loading-background="var(--el-bg-color-overlay)"
          :data="userList"
          row-key="id"
          border
          stripe
          style="width: 100%"
          class="user-table"
          height="100%"
        >
        <el-table-column prop="id" :label="t('system.users.table.id')" width="80" />
        <el-table-column :label="t('system.users.table.avatar')" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.nickname?.charAt(0) || 'U' }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="email" :label="t('system.users.table.email')" min-width="200" />
        <el-table-column prop="nickname" :label="t('system.users.table.nickname')" min-width="150" />
        <el-table-column :label="t('system.users.table.role')" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.role === Role.ADMIN" type="danger" effect="dark">{{ t('system.users.roles.admin') }}</el-tag>
            <el-tag v-else type="info" effect="dark">{{ t('system.users.roles.user') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :label="t('system.users.table.createTime')" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" :label="t('system.users.table.updateTime')" width="180">
          <template #default="{ row }">
            {{ formatTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('system.users.table.actions')" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              {{ t('system.users.table.edit') }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              {{ t('system.users.table.delete') }}
            </el-button>
          </template>
        </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      class="user-dialog"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="formRules"
        label-position="top"
        label-width="80px"
      >
        <el-form-item :label="t('system.users.form.email')" prop="email" class="email-item">
          <el-input
            v-model="userForm.email"
            :placeholder="t('system.users.form.emailPlaceholder')"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item v-if="!isEdit" :label="t('system.users.form.password')" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            :placeholder="t('system.users.form.passwordPlaceholder')"
            show-password
          />
        </el-form-item>
        <el-form-item :label="t('system.users.form.nickname')" prop="nickname">
          <el-input v-model="userForm.nickname" :placeholder="t('system.users.form.nicknamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('system.users.form.avatar')" prop="avatar">
          <div class="avatar-uploader">
            <div class="avatar-preview" @click="openAvatarPicker">
              <el-avatar :src="userForm.avatar" :size="84">
                {{ userForm.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="avatar-overlay">
                <el-icon class="overlay-icon"><Plus /></el-icon>
                {{ t('system.users.form.changeAvatar') }}
              </div>
            </div>
            <div class="avatar-side">
              <div class="avatar-actions">
                <el-button type="primary" @click="openAvatarPicker" :loading="uploadingAvatar">{{ t('system.users.form.uploadAvatar') }}</el-button>
                <el-button v-if="userForm.avatar" link type="danger" @click="clearAvatar" :disabled="uploadingAvatar">{{ t('system.users.form.removeAvatar') }}</el-button>
              </div>
              <div class="avatar-hint">{{ t('system.users.form.avatarHint') }}</div>
            </div>
            <input ref="avatarInputRef" type="file" accept="image/jpeg,image/jpg,image/png" style="display: none" @change="onAvatarSelected" />
          </div>
        </el-form-item>
        <el-form-item :label="t('system.users.form.role')" prop="role">
          <el-select v-model="userForm.role" :placeholder="t('system.users.form.rolePlaceholder')" style="width: 100%">
            <el-option :label="t('system.users.roles.user')" :value="Role.USER" />
            <el-option :label="t('system.users.roles.admin')" :value="Role.ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button class="cancel-btn" @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessageBox, ElNotification, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Search, RefreshRight, Edit, Delete } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'
import { systemUserApi } from '@/api/system-user/system-user'
import type { GetUserPageApi } from '@/api/system-user/types'
import { Role } from '@/enums/user'
import dayjs from 'dayjs'
import { ossApi } from '@/api/oss/oss'

const { t } = useI18n()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  role: undefined as Role | undefined
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 用户列表
const userList = ref<GetUserPageApi.SystemUser[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const uploadingAvatar = ref(false)
const avatarInputRef = ref<HTMLInputElement | null>(null)

const dialogTitle = computed(() => isEdit.value ? t('system.users.editUser') : t('system.users.createUser'))

// 表单
const formRef = ref<FormInstance>()
const userForm = reactive({
  id: 0,
  email: '',
  password: '',
  nickname: '',
  avatar: '',
  role: Role.USER
})

// 表单验证规则
const formRules = computed<FormRules>(() => ({
  email: [
    { required: true, message: t('system.users.validation.emailRequired'), trigger: 'blur' },
    { type: 'email', message: t('system.users.validation.emailFormat'), trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('system.users.validation.passwordRequired'), trigger: 'blur' },
    { min: 6, message: t('system.users.validation.passwordMinLength'), trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: t('system.users.validation.nicknameRequired'), trigger: 'blur' },
    { max: 50, message: t('system.users.validation.nicknameMaxLength'), trigger: 'blur' }
  ],
  role: [
    { required: true, message: t('system.users.validation.roleRequired'), trigger: 'change' }
  ]
}))

// 头像上传
const openAvatarPicker = () => {
  if (uploadingAvatar.value) return
  avatarInputRef.value?.click()
}

const onAvatarSelected = async (e: Event) => {
  const input = e.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return
  const file = input.files[0]

  // 校验类型与大小
  const supported = ['image/jpeg', 'image/jpg', 'image/png']
  if (!supported.includes(file.type)) {
    ElNotification.error(t('system.users.validation.avatarFormatError'))
    input.value = ''
    return
  }
  const maxSize = 2 * 1024 * 1024
  if (file.size > maxSize) {
    ElNotification.error(t('system.users.validation.avatarSizeError'))
    input.value = ''
    return
  }

  try {
    uploadingAvatar.value = true
    const url = await ossApi.uploadFile({ file })
    userForm.avatar = url
    ElNotification.success(t('system.users.messages.avatarUploadSuccess'))
  } catch (err) {
    console.error(err)

  } finally {
    uploadingAvatar.value = false
    input.value = ''
  }
}

const clearAvatar = () => {
  userForm.avatar = ''
}

// 获取用户列表
const getUserList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      role: searchForm.role
    }
    const res = await systemUserApi.reqGetUserPage(params)
    userList.value = res.items
    pagination.total = res.total
  } catch (error) {

    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  getUserList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.role = undefined
  pagination.page = 1
  getUserList()
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.page = 1
  getUserList()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.page = page
  getUserList()
}

// 新增用户
const handleCreate = () => {
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row: GetUserPageApi.SystemUser) => {
  isEdit.value = true
  userForm.id = row.id
  userForm.email = row.email
  userForm.nickname = row.nickname
  userForm.avatar = row.avatar
  userForm.role = row.role
  dialogVisible.value = true
}

// 删除用户
const handleDelete = (row: GetUserPageApi.SystemUser) => {
  ElMessageBox.confirm(
    t('system.users.messages.deleteConfirm', { name: row.nickname || row.email }),
    t('system.users.messages.deleteTitle'),
    {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning'
    }
  ).then(async () => {
    try {
      await systemUserApi.reqDeleteUser({ id: row.id })
      ElNotification.success(t('system.users.messages.deleteSuccess'))
      getUserList()
    } catch (error) {

      console.error(error)
    }
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value) {
        // 更新用户
        await systemUserApi.reqUpdateUser({
          id: userForm.id,
          email: userForm.email,
          nickname: userForm.nickname,
          avatar: userForm.avatar,
          role: userForm.role
        })
        ElNotification.success(t('system.users.messages.updateSuccess'))
      } else {
        // 创建用户
        await systemUserApi.reqCreateUser({
          email: userForm.email,
          password: userForm.password,
          nickname: userForm.nickname,
          avatar: userForm.avatar,
          role: userForm.role
        })
        ElNotification.success(t('system.users.messages.createSuccess'))
      }
      dialogVisible.value = false
      getUserList()
    } catch (error) {
    
      console.error(error)
    } finally {
      submitting.value = false
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
  userForm.id = 0
  userForm.email = ''
  userForm.password = ''
  userForm.nickname = ''
  userForm.avatar = ''
  userForm.role = Role.USER
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 初始化
onMounted(() => {
  getUserList()
})
</script>

<style scoped>
.user-management-view {
  height: 100%;
}

.content-card {
  height: 100%;
  background-color: var(--el-bg-color-overlay);
  display: flex;
  flex-direction: column;
}
/* 让 Card 的 body 填满剩余空间，内部再做列布局 */
.content-card :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0; /* 允许子元素正确计算百分比高度并出现滚动 */
}

.content-card :deep(.el-card__header) {
  padding: 14px 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.search-bar :deep(.el-input__wrapper),
.search-bar :deep(.el-select__wrapper) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  transition: all 0.2s ease;
}

.search-bar :deep(.el-input__wrapper:hover),
.search-bar :deep(.el-select__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.search-bar :deep(.el-input__wrapper.is-focus),
.search-bar :deep(.el-select__wrapper.is-focused) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

.reset-btn {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}

.reset-btn:hover {
  background-color: var(--el-fill-color-light);
}

.table-wrapper {
  flex: 1;
  min-height: 0; /* 修复子元素在 flex 容器中无法收缩导致溢出的问题 */
  margin-bottom: 16px; /* 与分页留出间距 */
}

.user-table {
  margin-bottom: 0;
  /* 统一表格在暗色主题下的背景 */
  --el-table-bg-color: var(--el-bg-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-tr-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
}

.user-table :deep(.el-table),
.user-table :deep(.el-table__inner-wrapper),
.user-table :deep(.el-table__body-wrapper),
.user-table :deep(.el-scrollbar__wrap),
.user-table :deep(.el-scrollbar__view) {
  background-color: var(--el-bg-color);
}

.user-table :deep(.el-table__empty-block) {
  background-color: var(--el-bg-color);
}

.user-table :deep(.el-table__empty-text) {
  color: var(--el-text-color-secondary);
}

.user-table :deep(.el-table__header) {
  background-color: var(--el-bg-color);
}

.user-table :deep(.el-table__body tr:hover > td) {
  background-color: var(--el-fill-color-light);
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.pagination-container :deep(.el-pagination) {
  --el-pagination-bg-color: var(--el-bg-color);
  --el-pagination-text-color: var(--el-text-color-primary);
  --el-pagination-button-disabled-bg-color: var(--el-fill-color-lighter);
}

.pagination-container :deep(.el-pagination .btn-prev),
.pagination-container :deep(.el-pagination .btn-next),
.pagination-container :deep(.el-pager li) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
}

.pagination-container :deep(.el-pager li.is-active) {
  background-color: var(--el-color-primary);
  color: #fff;
  border-color: var(--el-color-primary);
}

.pagination-container :deep(.el-select__wrapper),
.pagination-container :deep(.el-input__wrapper) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  color: var(--el-text-color-primary);
  transition: all 0.2s ease;
}

.pagination-container :deep(.el-select__wrapper:hover),
.pagination-container :deep(.el-input__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

.pagination-container :deep(.el-select__wrapper.is-focused),
.pagination-container :deep(.el-input__wrapper.is-focus) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

/* 头像上传区域样式 */
.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 预览容器，带悬浮蒙层 */
.avatar-preview {
  position: relative;
  width: 84px;
  height: 84px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.avatar-preview:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  gap: 6px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.overlay-icon {
  font-size: 14px;
}

.avatar-side {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.avatar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.avatar-hint {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

/* 对话框样式（使用 :deep 以匹配 Teleport 到 body 的内容） */
:deep(.user-dialog .el-dialog) {
  background-color: var(--el-bg-color-overlay);
  color: var(--el-text-color-primary);
}

:deep(.user-dialog .el-dialog__header),
:deep(.user-dialog .el-dialog__footer) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.user-dialog .el-dialog__header) {
  border-bottom: 1px solid var(--el-border-color-light);
}

:deep(.user-dialog .el-dialog__footer) {
  border-top: 1px solid var(--el-border-color-light);
}

:deep(.user-dialog .el-dialog__title) {
  color: var(--el-text-color-primary);
}

:deep(.user-dialog .el-dialog__body) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.user-dialog .el-form-item__label) {
  color: var(--el-text-color-primary);
}

:deep(.user-dialog .el-input__wrapper),
:deep(.user-dialog .el-select__wrapper) {
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  transition: all 0.2s ease;
}

:deep(.user-dialog .el-input__wrapper:hover),
:deep(.user-dialog .el-select__wrapper:hover) {
  border-color: var(--el-border-color-hover);
}

:deep(.user-dialog .el-input__wrapper.is-focus),
:deep(.user-dialog .el-select__wrapper.is-focused) {
  border-color: var(--el-color-primary);
  box-shadow: 0 0 0 2px var(--el-color-primary-light-8);
}

:deep(.cancel-btn) {
  background-color: var(--el-fill-color-lighter);
  color: var(--el-text-color-primary);
  border-color: var(--el-border-color);
}

:deep(.cancel-btn:hover) {
  background-color: var(--el-fill-color-light);
}

/* Select 下拉弹层与选项 hover/selected 样式（弹层 Teleport 到 body） */
:deep(.el-select__popper) {
  background-color: var(--el-bg-color-overlay);
  border: 1px solid var(--el-border-color);
}

:deep(.el-select__popper .el-select-dropdown) {
  background-color: var(--el-bg-color-overlay);
}

:deep(.el-select__popper .el-select-dropdown__item) {
  color: var(--el-text-color-regular);
  transition: background-color 0.15s ease, color 0.15s ease;
}

:deep(.el-select__popper .el-select-dropdown__item.is-hovering) {
  background-color: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
}

:deep(.el-select__popper .el-select-dropdown__item.is-selected) {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

/* 虚拟化的 SelectV2 兼容（若项目使用 el-select-v2） */
:deep(.el-select__popper .el-select-dropdown__item.selected),
:deep(.el-select__popper .el-select-dropdown__item.hover) {
  background-color: var(--el-fill-color-light);
}

/* 下拉滚动条在暗色下的对比度增强 */
:deep(.el-select__popper .el-select-dropdown__wrap::-webkit-scrollbar-thumb) {
  background-color: var(--el-fill-color-darker);
}

/* 顶部左侧标签样式优化（对话框内表单） */
:deep(.user-dialog .el-form--label-top .el-form-item__label) {
  padding-bottom: 6px;
  line-height: 1.2;
  font-weight: 500;
  text-align: left;
}

:deep(.user-dialog .el-form--label-top .el-form-item) {
  margin-bottom: 16px;
}

::deep(.user-dialog .email-item) {
  margin-top: 8px;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
  }

  .search-bar .el-input,
  .search-bar .el-select {
    width: 100% !important;
  }
}
</style>

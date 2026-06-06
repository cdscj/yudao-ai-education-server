<template>
  <div v-if="route.path === '/login'" style="min-height:100vh">
    <router-view />
  </div>
  <div v-else-if="authLoading" class="loading-screen">
    <div class="loading-spinner">
      <el-icon :size="36" class="spin"><Loading /></el-icon>
      <span>加载中...</span>
    </div>
  </div>
  <el-container v-else class="app-layout">
    <!-- 侧边栏 -->
    <el-aside :width="collapsed ? '64px' : '220px'" class="app-sidebar">
      <!-- Logo -->
      <div class="sidebar-logo" @click="$router.push('/profile')">
        <span class="logo-icon">🎓</span>
        <transition name="fade">
          <span v-show="!collapsed" class="logo-text">AI 学习平台</span>
        </transition>
      </div>

      <!-- 用户卡片 -->
      <div class="sidebar-user">
        <el-avatar :size="collapsed ? 36 : 44" :src="memberUser?.avatar" style="background:linear-gradient(135deg,#409eff,#66b1ff);flex-shrink:0">
          {{ memberUser?.nickname?.charAt(0) || '?' }}
        </el-avatar>
        <transition name="fade">
          <div v-show="!collapsed" class="user-info">
            <div class="user-name">{{ memberUser?.nickname || '学习者' }}</div>
            <div class="user-tag">Lv.1 学习新手</div>
          </div>
        </transition>
      </div>

      <!-- 导航菜单 -->
      <el-menu :default-active="route.path" :collapse="collapsed" :router="true" class="sidebar-menu">
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon><span>我的画像</span>
        </el-menu-item>

        <el-menu-item-group title="AI 学习中心">
          <el-menu-item index="/resources">
            <el-icon><Reading /></el-icon><span>学习资源</span>
          </el-menu-item>
          <el-menu-item index="/questionbank">
            <el-icon><Collection /></el-icon><span>题库</span>
          </el-menu-item>
          <el-menu-item index="/path">
            <el-icon><MapLocation /></el-icon><span>学习路径</span>
          </el-menu-item>
          <el-menu-item index="/tutoring">
            <el-icon><ChatLineSquare /></el-icon><span>智能辅导</span>
          </el-menu-item>
          <el-menu-item index="/evaluation">
            <el-icon><DataAnalysis /></el-icon><span>学习评估</span>
          </el-menu-item>
          <el-menu-item index="/homework">
            <el-icon><Document /></el-icon><span>作业</span>
          </el-menu-item>
          <el-menu-item index="/exam">
            <el-icon><Edit /></el-icon><span>模拟考试</span>
          </el-menu-item>
          <el-menu-item index="/dashboard">
            <el-icon><PieChart /></el-icon><span>学习看板</span>
          </el-menu-item>
          <el-menu-item index="/study-plan">
            <el-icon><Clock /></el-icon><span>学习计划</span>
          </el-menu-item>
        </el-menu-item-group>

        <el-menu-item-group title="学习激励">
          <el-menu-item index="/checkin">
            <el-icon><Calendar /></el-icon><span>每日签到</span>
          </el-menu-item>
          <el-menu-item index="/leaderboard">
            <el-icon><Trophy /></el-icon><span>排行榜</span>
          </el-menu-item>
          <el-menu-item index="/goals">
            <el-icon><Flag /></el-icon><span>学习目标</span>
          </el-menu-item>
          <el-menu-item index="/wrongbook">
            <el-icon><EditPen /></el-icon><span>错题本</span>
          </el-menu-item>
        </el-menu-item-group>

        <el-menu-item-group title="社交">
          <el-menu-item index="/friends">
            <el-icon><Connection /></el-icon><span>好友</span>
          </el-menu-item>
          <el-menu-item index="/activity">
            <el-icon><Bell /></el-icon><span>动态</span>
          </el-menu-item>
        </el-menu-item-group>

        <el-menu-item-group title="校园">
          <el-menu-item index="/school">
            <el-icon><OfficeBuilding /></el-icon><span>学校信息</span>
          </el-menu-item>
          <el-menu-item index="/schedule">
            <el-icon><Timer /></el-icon><span>课程表</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-menu>

      <!-- 底部操作 -->
      <div class="sidebar-footer">
        <el-tooltip content="收起菜单" placement="right" :disabled="!collapsed">
          <div class="collapse-btn" @click="collapsed = !collapsed">
            <el-icon :size="18"><component :is="collapsed ? 'DArrowRight' : 'DArrowLeft'" /></el-icon>
          </div>
        </el-tooltip>
        <transition name="fade">
          <el-button v-show="!collapsed" text size="small" @click="handleCommand('edit')">
            <el-icon><Edit /></el-icon>编辑资料
          </el-button>
        </transition>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{path:'/'}">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="消息通知" placement="bottom">
            <el-badge :value="unreadCount" :hidden="!unreadCount" :max="99">
              <el-button text circle @click="openNotices">
                <el-icon :size="18"><Bell /></el-icon>
              </el-button>
            </el-badge>
          </el-tooltip>
          <el-tooltip content="切换主题" placement="bottom">
            <el-button text circle @click="toggleTheme">
              <el-icon :size="18"><Moon v-if="isDark" /><Sunny v-else /></el-icon>
            </el-button>
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="header-user">
              <el-badge is-dot :hidden="!hasNotice" style="margin-right:4px">
                <el-avatar :size="32" :src="memberUser?.avatar" style="background:linear-gradient(135deg,#409eff,#66b1ff)">
                  {{ memberUser?.nickname?.charAt(0) || '?' }}
                </el-avatar>
              </el-badge>
              <span class="header-nickname">{{ memberUser?.nickname || '已登录' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit"><el-icon><Edit /></el-icon> 编辑资料</el-dropdown-item>
                <el-dropdown-item command="profile"><el-icon><User /></el-icon> 我的画像</el-dropdown-item>
                <el-dropdown-item divided command="logout"><el-icon><SwitchButton /></el-icon> 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>

  <!-- 通知抽屉 -->
  <el-drawer v-model="showNotice" title="消息通知" size="380px" :close-on-click-modal="true">
    <div class="notice-list">
      <div class="notice-bar"><el-button text size="small" @click="markAllRead" v-if="notices.length">全部已读</el-button></div>
      <div v-for="n in notices" :key="n.id" class="notice-item" :class="{unread:!n.isRead}" @click="markRead(n.id)">
        <div class="ni-title">{{ n.title }}</div>
        <div class="ni-content">{{ n.content || '' }}</div>
        <div class="ni-time">{{ n.createTime }}</div>
      </div>
      <el-empty v-if="!notices.length" description="暂无通知" :image-size="60" />
    </div>
  </el-drawer>

  <!-- 编辑资料弹窗 -->
  <el-dialog v-model="showEdit" title="编辑个人资料" width="440px" :close-on-click-modal="false" class="edit-dialog">
    <div class="edit-avatar-section">
      <el-avatar :size="80" :src="avatarPreview || undefined" style="background:linear-gradient(135deg,#409eff,#66b1ff);font-size:32px">
        {{ form.nickname?.charAt(0) || '?' }}
      </el-avatar>
      <el-upload :show-file-list="false" :before-upload="onUpload" accept="image/*">
        <el-button size="small" type="primary" text :loading="uploading">更换头像</el-button>
      </el-upload>
    </div>
    <el-form :model="form" label-width="60px" label-position="left">
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" placeholder="给自己取个名字吧" maxlength="30" size="large" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.sex">
          <el-radio-button :value="0">保密</el-radio-button>
          <el-radio-button :value="1">👦 男</el-radio-button>
          <el-radio-button :value="2">👧 女</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="showEdit = false" size="large">取消</el-button>
        <el-button type="primary" :loading="saving" @click="onSave" size="large">保存修改</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { authApi, memberApi, uploadApi } from '@/api/index'
import { ElMessageBox, ElMessage } from 'element-plus'
import { ArrowDown, Moon, Sunny, Bell } from '@element-plus/icons-vue'
import { notificationApi } from '@/api/index.js'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const memberUser = ref(null)
const authLoading = ref(true)
const collapsed = ref(false)
const isDark = ref(false)
const unreadCount = ref(0)
const showNotice = ref(false)
const notices = ref([])
async function fetchUnread() { try { const r = await notificationApi.unreadCount(); unreadCount.value = r.data?.data || 0 } catch(e){} }
async function openNotices() { showNotice.value = true; try { const r = await notificationApi.page({pageNo:1,pageSize:20}); notices.value = r.data?.data?.list || [] } catch(e){} fetchUnread() }
async function markRead(id) { await notificationApi.markRead(id); fetchUnread() }
async function markAllRead() { await notificationApi.markAllRead(); notices.value = []; fetchUnread() }
setInterval(fetchUnread, 60000) // poll every 60s

const pageTitle = computed(() => {
  const map = {
    '/profile':'我的画像','/resources':'学习资源','/questionbank':'题库','/path':'学习路径','/tutoring':'智能辅导',
    '/evaluation':'学习评估','/checkin':'每日签到','/leaderboard':'排行榜','/friends':'好友',
    '/goals':'学习目标','/wrongbook':'错题本','/homework':'作业','/exam':'模拟考试','/dashboard':'学习看板',
    '/study-plan':'学习计划','/activity':'动态','/school':'学校信息','/schedule':'课程表'
  }
  return map[route.path] || ''
})

async function fetchMemberUser() {
  if (!authStore.isLoggedIn) return
  try {
    const r = await memberApi.get()
    if (r.data?.data) memberUser.value = r.data.data
  } catch (e) {
    console.error('Failed to fetch member user:', e)
    authStore.logout(); router.push('/login')
  }
}

onMounted(async () => {
  if (!authStore.isLoggedIn) {
    if (route.path !== '/login') router.push('/login')
    authLoading.value = false; return
  }
  await fetchMemberUser(); fetchUnread(); authLoading.value = false
})

watch(() => route.path, (path, oldPath) => {
  if (oldPath === '/login' && path !== '/login' && authStore.isLoggedIn) fetchMemberUser()
})

function toggleTheme() { isDark.value = !isDark.value }
async function handleLogout() {
  try { await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', { type: 'warning', confirmButtonText:'退出', cancelButtonText:'取消' }) } catch { return }
  try { await authApi.logout() } catch (e) { console.error('Logout API call failed:', e) }
  authStore.logout(); window.location.href = '/login'
}

const showEdit = ref(false); const saving = ref(false); const uploading = ref(false)
const avatarPreview = ref('')
const form = ref({ nickname: '', sex: 0, avatar: '' })

function onEdit() {
  form.value = { nickname: memberUser.value?.nickname || '', sex: memberUser.value?.sex ?? 0, avatar: memberUser.value?.avatar || '' }
  avatarPreview.value = memberUser.value?.avatar || ''; showEdit.value = true
}
async function onUpload(file) {
  uploading.value = true
  try { const r = await uploadApi.file(file, 'avatar'); if (r.data?.data) { form.value.avatar = r.data.data; avatarPreview.value = r.data.data } }
  catch (e) { console.error('Avatar upload failed:', e); ElMessage.error('头像上传失败') }
  uploading.value = false; return false
}
async function onSave() {
  saving.value = true
  try {
    await memberApi.update({ nickname: form.value.nickname, sex: form.value.sex, avatar: form.value.avatar })
    ElMessage.success('保存成功'); showEdit.value = false
    const r = await memberApi.get(); if (r.data?.data) memberUser.value = r.data.data
  } catch (e) { console.error('Failed to save profile:', e); ElMessage.error('保存失败，请重试') }
  saving.value = false
}
function handleCommand(cmd) {
  if (cmd === 'edit') onEdit()
  if (cmd === 'profile') router.push('/profile')
  if (cmd === 'logout') handleLogout()
}
</script>

<style>
* { margin:0; padding:0; box-sizing:border-box }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif }
#app { height:100vh }
</style>

<style scoped>
.app-layout { height:100vh }

/* ===== Sidebar ===== */
.app-sidebar {
  background:linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  color:#fff;
  display:flex; flex-direction:column;
  transition:width .3s ease;
  overflow:hidden;
  box-shadow:2px 0 12px rgba(0,0,0,0.1);
  z-index:100;
}
.sidebar-logo {
  display:flex; align-items:center; gap:10px; padding:20px 18px;
  cursor:pointer; border-bottom:1px solid rgba(255,255,255,0.08);
}
.logo-icon { font-size:28px }
.logo-text { font-size:18px; font-weight:800; letter-spacing:1px; white-space:nowrap }

.sidebar-user {
  display:flex; align-items:center; gap:12px; padding:16px 18px;
  border-bottom:1px solid rgba(255,255,255,0.08);
}
.user-info { min-width:0 }
.user-name { font-size:14px; font-weight:600; overflow:hidden; text-overflow:ellipsis; white-space:nowrap }
.user-tag { font-size:11px; color:rgba(255,255,255,0.5); margin-top:2px }

.sidebar-menu {
  flex:1; overflow-y:auto; overflow-x:hidden;
  border-right:none !important;
  background:transparent !important;
  padding:8px 0;
}
.sidebar-menu :deep(.el-menu-item) {
  color:rgba(255,255,255,0.75) !important;
  border-radius:8px; margin:2px 8px;
  height:42px; line-height:42px;
  transition:all .2s;
}
.sidebar-menu :deep(.el-menu-item:hover) {
  color:#fff !important; background:rgba(255,255,255,0.08) !important;
}
.sidebar-menu :deep(.el-menu-item.is-active) {
  color:#fff !important;
  background:linear-gradient(135deg, rgba(64,158,255,0.3), rgba(102,177,255,0.15)) !important;
  border-left:3px solid #409eff;
}
.sidebar-menu :deep(.el-menu-item-group__title) {
  padding:16px 18px 6px;
  font-size:11px; font-weight:700; color:rgba(255,255,255,0.35);
  text-transform:uppercase; letter-spacing:1.5px;
}
.sidebar-menu :deep(.el-icon) { font-size:18px }

.sidebar-footer {
  display:flex; align-items:center; justify-content:space-between;
  padding:12px 16px; border-top:1px solid rgba(255,255,255,0.08);
}
.collapse-btn {
  width:34px; height:34px; display:flex; align-items:center; justify-content:center;
  border-radius:8px; cursor:pointer; color:rgba(255,255,255,0.5); transition:all .2s;
}
.collapse-btn:hover { background:rgba(255,255,255,0.1); color:#fff }

/* ===== Header ===== */
.app-header {
  display:flex; align-items:center; justify-content:space-between;
  background:#fff; height:56px; padding:0 24px;
  box-shadow:0 1px 4px rgba(0,0,0,0.04);
  z-index:50;
}
.header-left { display:flex; align-items:center }
.header-right { display:flex; align-items:center; gap:8px }
.header-user {
  display:flex; align-items:center; gap:6px; cursor:pointer;
  padding:4px 10px; border-radius:20px; transition:background .2s;
}
.header-user:hover { background:#f5f7fa }
.header-nickname { font-size:13px; color:#606266; font-weight:500 }

/* ===== Main ===== */
.app-main {
  background:#f0f2f5;
  padding:20px 24px;
  overflow-y:auto;
  height:calc(100vh - 56px);
}

/* ===== Loading ===== */
.loading-screen {
  height:100vh; display:flex; align-items:center; justify-content:center;
  background:#f0f2f5;
}
.loading-spinner {
  display:flex; flex-direction:column; align-items:center; gap:12px;
  color:#909399; font-size:15px;
}
.spin { animation:spin 1s linear infinite }
@keyframes spin { from{transform:rotate(0deg)} to{transform:rotate(360deg)} }

/* ===== Notice ===== */
.notice-list { padding:0 4px }
.notice-bar { text-align:right; margin-bottom:8px }
.notice-item { padding:12px; border-radius:8px; cursor:pointer; margin-bottom:6px; transition:background .2s }
.notice-item:hover { background:#f5f7fa }
.notice-item.unread { background:#ecf5ff }
.ni-title { font-weight:600; font-size:14px; color:#303133 }
.ni-content { font-size:12px; color:#909399; margin-top:4px }
.ni-time { font-size:11px; color:#c0c4cc; margin-top:4px }

/* ===== Edit Dialog ===== */
.edit-avatar-section {
  display:flex; flex-direction:column; align-items:center; gap:10px;
  margin-bottom:24px; padding-bottom:20px; border-bottom:1px solid #f0f0f0;
}
.dialog-footer { display:flex; justify-content:flex-end; gap:10px }

/* ===== Transitions ===== */
.fade-enter-active, .fade-leave-active { transition:opacity .2s ease }
.fade-enter-from, .fade-leave-to { opacity:0 }
</style>

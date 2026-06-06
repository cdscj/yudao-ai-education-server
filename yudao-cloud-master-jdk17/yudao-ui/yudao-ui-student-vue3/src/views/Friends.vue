<template>
  <div class="friends-page" v-loading="loading">
    <!-- 页头 -->
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><Connection /></el-icon> 好友圈</h2>
      <div style="display:flex;gap:8px">
        <el-input v-model="searchInput" placeholder="搜索用户ID添加好友" size="default" style="width:220px" clearable />
        <el-button type="primary" @click="doSendRequest" :loading="sending"><el-icon><Plus /></el-icon> 添加</el-button>
      </div>
    </div>

    <el-row :gutter="16">
      <!-- 好友申请 -->
      <el-col :xs="24" v-if="pendingList.length">
        <div class="notice-card">
          <div class="notice-title"><el-icon><Bell /></el-icon> 新的好友申请 ({{ pendingList.length }})</div>
          <div class="notice-list">
            <div v-for="req in pendingList" :key="req.id" class="notice-item">
              <div class="ni-left">
                <el-avatar :size="40" style="background:#409eff">{{ req.nickname?.charAt(0) || 'U' }}</el-avatar>
                <div>
                  <div class="ni-name">{{ req.nickname || '用户'+req.userId }}</div>
                  <div class="ni-remark">{{ req.remark || '请求添加你为好友' }}</div>
                </div>
              </div>
              <div class="ni-actions">
                <el-button type="success" round size="small" @click="doAccept(req.id)" :icon="Check">接受</el-button>
                <el-button round size="small" @click="doReject(req.id)" :icon="Close">拒绝</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 好友列表 -->
      <el-col :xs="24">
        <div class="friends-grid" v-if="friendList.length">
          <div v-for="f in friendList" :key="f.id" class="friend-card">
            <div class="fc-top">
              <el-avatar :size="52" style="background:linear-gradient(135deg,#409eff,#66b1ff);font-size:20px">{{ f.nickname?.charAt(0) || '?' }}</el-avatar>
              <div class="fc-badge" v-if="f.level">Lv.{{ f.level }}</div>
            </div>
            <div class="fc-name">{{ f.nickname || '用户'+f.friendUserId }}</div>
            <div class="fc-title">{{ f.rankTitle || '学习新手' }}</div>
            <div class="fc-stats">
              <div class="fcs-item"><span class="fcs-num">{{ f.resourceCount || 0 }}</span>资源</div>
              <div class="fcs-item"><span class="fcs-num">{{ f.checkInDays || 0 }}</span>签到</div>
              <div class="fcs-item"><span class="fcs-num">{{ f.points || 0 }}</span>积分</div>
            </div>
            <div class="fc-actions">
              <el-button v-if="!f.isFollowing" size="small" plain round @click="doFollow(f)"><el-icon><Plus /></el-icon> 关注</el-button>
              <el-button v-else size="small" type="success" round plain>已关注</el-button>
              <el-button size="small" text type="danger" @click="doRemove(f)">删除</el-button>
            </div>
          </div>
        </div>
        <div v-else class="empty-card">
          <el-empty description="还没有好友，通过用户ID添加学习伙伴吧!" :image-size="100">
            <el-button type="primary" round>发现学习伙伴</el-button>
          </el-empty>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { friendApi, followApi } from '@/api/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Close } from '@element-plus/icons-vue'

const loading = ref(false)
const searchInput = ref(''); const sending = ref(false)
const friendList = ref([]); const pendingList = ref([])

async function load() {
  loading.value = true
  try {
    const [fl, pl] = await Promise.all([
      friendApi.list({ pageNo: 1, pageSize: 100 }),
      friendApi.pending()
    ])
    friendList.value = fl.data?.data?.list || []
    pendingList.value = pl.data?.data || []
  } catch (e) {
    console.error('Failed to load friends:', e)
    ElMessage.error('加载好友列表失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

async function doSendRequest() {
  if (!searchInput.value) { ElMessage.warning('请输入用户ID'); return }
  sending.value = true
  try {
    await friendApi.sendRequest(Number(searchInput.value), '')
    ElMessage.success('好友申请已发送')
    searchInput.value = ''
    await load()
  } catch (e) {
    console.error('Failed to send friend request:', e)
    ElMessage.error(e?.response?.data?.msg || '发送好友申请失败，请检查用户ID是否正确')
  } finally { sending.value = false }
}
async function doAccept(id) {
  try {
    await friendApi.accept(id)
    ElMessage.success('已添加为好友')
    await load()
  } catch (e) {
    console.error('Failed to accept friend request:', e)
    ElMessage.error('接受好友申请失败，请重试')
  }
}
async function doReject(id) {
  try {
    await friendApi.reject(id)
    ElMessage.success('已拒绝')
    await load()
  } catch (e) {
    console.error('Failed to reject friend request:', e)
    ElMessage.error('操作失败，请重试')
  }
}
async function doRemove(f) {
  try { await ElMessageBox.confirm('确定删除该好友?', '提示', { type: 'warning', confirmButtonText:'确定', cancelButtonText:'取消' }) } catch { return }
  try {
    await friendApi.remove(f.friendUserId)
    ElMessage.success('已删除')
    await load()
  } catch (e) {
    console.error('Failed to remove friend:', e)
    ElMessage.error('删除好友失败，请重试')
  }
}
async function doFollow(f) {
  try {
    await followApi.follow(f.friendUserId)
    ElMessage.success('已关注')
    await load()
  } catch (e) {
    console.error('Failed to follow:', e)
    ElMessage.error('关注失败，请重试')
  }
}

onMounted(load)
</script>

<style scoped>
.friends-page { max-width:1000px; margin:0 auto }
.page-head { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }

.notice-card { background:#fff; border-radius:16px; padding:20px; margin-bottom:16px; box-shadow:0 2px 12px rgba(0,0,0,0.04); border-left:4px solid #e6a23c }
.notice-title { display:flex; align-items:center; gap:6px; font-weight:600; font-size:15px; color:#303133; margin-bottom:12px }
.notice-item { display:flex; align-items:center; justify-content:space-between; padding:10px 0; border-bottom:1px solid #f5f5f5 }
.notice-item:last-child { border-bottom:none }
.ni-left { display:flex; align-items:center; gap:12px }
.ni-name { font-weight:600; font-size:14px }
.ni-remark { font-size:12px; color:#909399; margin-top:2px }

.friends-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(220px,1fr)); gap:14px }
.friend-card { background:#fff; border-radius:16px; padding:24px 18px 18px; text-align:center; box-shadow:0 2px 12px rgba(0,0,0,0.04); transition:all .3s; position:relative }
.friend-card:hover { transform:translateY(-4px); box-shadow:0 8px 25px rgba(0,0,0,0.08) }
.fc-top { display:flex; justify-content:center; margin-bottom:10px; position:relative }
.fc-badge { position:absolute; top:-4px; right:calc(50% - 38px); background:linear-gradient(135deg,#f7ba2a,#ffd666); color:#fff; font-size:10px; font-weight:700; padding:2px 8px; border-radius:10px }
.fc-name { font-weight:700; font-size:15px; color:#303133 }
.fc-title { font-size:12px; color:#909399; margin-top:2px }
.fc-stats { display:flex; justify-content:center; gap:16px; margin:14px 0; padding:10px 0; border-top:1px solid #f5f5f5; border-bottom:1px solid #f5f5f5 }
.fcs-item { font-size:11px; color:#909399; text-align:center }
.fcs-num { display:block; font-size:15px; font-weight:700; color:#303133 }
.fc-actions { display:flex; gap:8px; justify-content:center; margin-top:8px }

.empty-card { background:#fff; border-radius:16px; padding:40px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
</style>

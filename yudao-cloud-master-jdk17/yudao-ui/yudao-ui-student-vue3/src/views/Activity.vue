<template>
  <div class="feed-page" v-loading="loading">
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><Bell /></el-icon> 学习动态</h2>
      <el-tag type="info" size="default" round>好友动态</el-tag>
    </div>
    <div class="feed-list" v-if="activities.length">
      <div v-for="act in activities" :key="act.id" class="feed-item">
        <div class="fi-icon" :style="{background: actIconBg(act.activityType)}">{{ actIcon(act.activityType) }}</div>
        <div class="fi-body">
          <div class="fi-content">
            <b>{{ act.nickname || '用户'+act.userId }}</b>
            {{ actLabel(act.activityType) }}
          </div>
          <div class="fi-extra" v-if="act.content">{{ act.content }}</div>
          <div class="fi-time">{{ formatTime(act.createTime) }}</div>
        </div>
      </div>
    </div>
    <div v-else class="empty-card">
      <el-empty description="还没有动态，签到或完成学习目标后会显示在这里!" :image-size="100" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { activityApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const activities = ref([])

function actIcon(t) {
  const map = { CHECK_IN:'🔥', COMPLETE_RESOURCE:'📚', EARN_POINTS:'⭐', LEVEL_UP:'🎉', ADD_FRIEND:'🤝', COMPLETE_GOAL:'🏆' }
  return map[t] || '📌'
}
function actIconBg(t) {
  const map = { CHECK_IN:'#fff7e6', COMPLETE_RESOURCE:'#e6f7ff', EARN_POINTS:'#fef0f0', LEVEL_UP:'#f0f9eb', ADD_FRIEND:'#f3f0ff', COMPLETE_GOAL:'#fff0f6' }
  return map[t] || '#f5f5f5'
}
function actLabel(t) {
  const map = { CHECK_IN:'完成了每日签到', COMPLETE_RESOURCE:'完成了一个学习资源', EARN_POINTS:'获得了积分', LEVEL_UP:'升级了!', ADD_FRIEND:'添加了新好友', COMPLETE_GOAL:'完成了一个学习目标' }
  return map[t] || '有一条新动态'
}
function formatTime(t) {
  if (!t) return ''
  const d = new Date(t); const now = new Date()
  const diff = now - d; const mins = Math.floor(diff/60000)
  if (mins < 1) return '刚刚'; if (mins < 60) return mins+'分钟前'
  const hours = Math.floor(mins/60); if (hours < 24) return hours+'小时前'
  const days = Math.floor(hours/24); if (days < 7) return days+'天前'
  return d.toLocaleDateString('zh-CN')
}

async function load() {
  loading.value = true
  try {
    const r = await activityApi.feed({ pageNo:1, pageSize:30 })
    activities.value = r.data?.data || []
  } catch (e) {
    console.error('Failed to load activities:', e)
    ElMessage.error('加载动态失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.feed-page { max-width:700px; margin:0 auto }
.page-head { display:flex; align-items:center; justify-content:space-between; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }
.feed-list { display:flex; flex-direction:column; gap:12px }
.feed-item { display:flex; gap:14px; background:#fff; padding:16px 18px; border-radius:14px; box-shadow:0 2px 12px rgba(0,0,0,0.03); transition:all .2s }
.feed-item:hover { box-shadow:0 4px 16px rgba(0,0,0,0.06) }
.fi-icon { width:44px; height:44px; border-radius:14px; display:flex; align-items:center; justify-content:center; font-size:20px; flex-shrink:0 }
.fi-body { flex:1; min-width:0 }
.fi-content { font-size:14px; color:#303133; line-height:1.5 }
.fi-extra { font-size:13px; color:#606266; margin-top:4px; background:#fafafa; padding:6px 10px; border-radius:6px }
.fi-time { font-size:12px; color:#c0c4cc; margin-top:4px }
.empty-card { background:#fff; border-radius:16px; padding:40px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
</style>

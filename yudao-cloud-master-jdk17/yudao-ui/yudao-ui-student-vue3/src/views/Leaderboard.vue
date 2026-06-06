<template>
  <div class="lb-page" v-loading="loading">
    <!-- 顶部切换 -->
    <div class="lb-header">
      <h2 class="page-title"><el-icon :size="24"><Trophy /></el-icon> 学习排行榜</h2>
      <el-radio-group v-model="period" @change="load" class="period-switch">
        <el-radio-button value="DAILY">日榜</el-radio-button>
        <el-radio-button value="WEEKLY">周榜</el-radio-button>
        <el-radio-button value="MONTHLY">月榜</el-radio-button>
      </el-radio-group>
    </div>

    <!-- TOP3 领奖台 -->
    <div class="podium" v-if="top3.length">
      <div class="podium-item second" v-if="top3[1]">
        <div class="p-avatar"><el-avatar :size="56" style="background:#c0c4cc;font-size:24px">{{ top3[1].nickname?.charAt(0) || '?' }}</el-avatar></div>
        <div class="p-name">{{ top3[1].nickname || '用户'+top3[1].userId }}</div>
        <div class="p-score">⭐ {{ top3[1].score }}</div>
        <div class="p-bar silver">🥈</div>
      </div>
      <div class="podium-item first" v-if="top3[0]">
        <div class="p-avatar"><el-avatar :size="64" style="background:#ffd666;font-size:28px;color:#333">{{ top3[0].nickname?.charAt(0) || '?' }}</el-avatar></div>
        <div class="p-name">{{ top3[0].nickname || '用户'+top3[0].userId }}</div>
        <div class="p-score">⭐ {{ top3[0].score }}</div>
        <div class="p-bar gold">🥇👑</div>
      </div>
      <div class="podium-item third" v-if="top3[2]">
        <div class="p-avatar"><el-avatar :size="50" style="background:#ffbb96;font-size:22px">{{ top3[2].nickname?.charAt(0) || '?' }}</el-avatar></div>
        <div class="p-name">{{ top3[2].nickname || '用户'+top3[2].userId }}</div>
        <div class="p-score">⭐ {{ top3[2].score }}</div>
        <div class="p-bar bronze">🥉</div>
      </div>
    </div>

    <!-- 我的排名卡片 -->
    <div v-if="myRank" class="my-rank-card">
      <div class="mr-left">
        <span class="mr-rank">#{{ myRank.rank }}</span>
        <span class="mr-label">当前排名</span>
      </div>
      <div class="mr-divider"></div>
      <div class="mr-item">
        <span class="mr-val">⭐ {{ myRank.score }}</span>
        <span class="mr-label">积分</span>
      </div>
      <div class="mr-divider"></div>
      <div class="mr-item">
        <span class="mr-val">{{ myRank.title || '学习新手' }}</span>
        <span class="mr-label">称号</span>
      </div>
    </div>

    <!-- 完整排名列表 -->
    <div class="rank-list-card">
      <div class="list-header">全部排名 ({{ list.length }})</div>
      <div v-if="list.length" class="rank-table">
        <div v-for="(item,i) in list" :key="i" class="rank-row" :class="{ me: item.userId === myRank?.userId }">
          <div class="rk-num">
            <span v-if="i===0">🥇</span><span v-else-if="i===1">🥈</span><span v-else-if="i===2">🥉</span>
            <span v-else :class="{ top10: i < 9 }">{{ i+1 }}</span>
          </div>
          <el-avatar :size="38" style="background:#409eff;flex-shrink:0">{{ item.nickname?.charAt(0) || '?' }}</el-avatar>
          <div class="rk-info">
            <div class="rk-name">{{ item.nickname || '用户'+item.userId }}</div>
            <div class="rk-level">{{ item.rankTitle || '学习新手' }} · Lv.{{ item.level || 1 }}</div>
          </div>
          <div class="rk-score">
            <span class="score-num">⭐ {{ item.score }}</span>
            <div class="score-bar-bg"><div class="score-bar-fill" :style="{width: (item.score/maxScore*100)+'%'}"></div></div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无排行数据" :image-size="80" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { leaderboardApi, pointsApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const period = ref('WEEKLY')
const list = ref([])
const myRank = ref(null)

const top3 = computed(() => list.value.slice(0,3))
const maxScore = computed(() => Math.max(1, ...list.value.map(i=>i.score||0)))

async function load() {
  loading.value = true
  try {
    const [lr, mr] = await Promise.all([
      leaderboardApi.list(period.value, 30),
      leaderboardApi.myRank(period.value)
    ])
    list.value = lr.data?.data || []
    const pts = (await pointsApi.my().catch(() => ({}))).data?.data || {}
    myRank.value = { ...(mr.data?.data || {}), title: pts.rankTitle, level: pts.level }
  } catch (e) {
    console.error('Failed to load leaderboard:', e)
    ElMessage.error('加载排行榜失败，请重试')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.lb-page { max-width:900px; margin:0 auto }
.lb-header { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }
.period-switch :deep(.el-radio-button__inner) { padding:8px 20px }

.podium { display:flex; align-items:flex-end; justify-content:center; gap:16px; margin-bottom:24px; padding:30px 16px 24px; background:linear-gradient(180deg,#fff 0%,#f8f9fb 100%); border-radius:20px; box-shadow:0 2px 16px rgba(0,0,0,0.04) }
.podium-item { display:flex; flex-direction:column; align-items:center; gap:8px; cursor:pointer; transition:transform .2s }
.podium-item:hover { transform:translateY(-4px) }
.podium-item.first { order:2 }
.podium-item.second { order:1 }
.podium-item.third { order:3 }
.p-name { font-weight:600; color:#303133; font-size:14px; text-align:center; max-width:80px; overflow:hidden; text-overflow:ellipsis }
.p-score { font-size:13px; color:#e6a23c; font-weight:600 }
.p-bar { font-size:28px; margin-top:4px }
.p-bar.gold { font-size:32px }

.my-rank-card { display:flex; align-items:center; gap:16px; background:linear-gradient(135deg,#409eff,#66b1ff); border-radius:14px; padding:16px 24px; margin-bottom:16px; color:#fff }
.mr-left { display:flex; flex-direction:column; align-items:center }
.mr-rank { font-size:32px; font-weight:800 }
.mr-label { font-size:12px; opacity:0.8 }
.mr-divider { width:1px; height:40px; background:rgba(255,255,255,0.3) }
.mr-item { display:flex; flex-direction:column; align-items:center }
.mr-val { font-size:16px; font-weight:700 }

.rank-list-card { background:#fff; border-radius:16px; padding:20px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
.list-header { font-weight:600; color:#303133; margin-bottom:12px; font-size:15px }
.rank-row { display:flex; align-items:center; gap:12px; padding:10px 12px; border-radius:10px; transition:all .2s }
.rank-row:hover { background:#f5f7fa }
.rank-row.me { background:#e6f7ff; border:1px solid #91d5ff }
.rk-num { width:36px; text-align:center; font-size:18px; font-weight:700; color:#909399 }
.rk-num .top10 { color:#409eff }
.rk-info { flex:1; min-width:0 }
.rk-name { font-weight:600; color:#303133; font-size:14px }
.rk-level { font-size:12px; color:#909399; margin-top:2px }
.rk-score { text-align:right; min-width:120px }
.score-num { font-weight:700; color:#e6a23c; font-size:14px }
.score-bar-bg { width:100%; height:4px; background:#f0f0f0; border-radius:2px; margin-top:4px }
.score-bar-fill { height:100%; background:linear-gradient(90deg,#f7ba2a,#f9d44c); border-radius:2px; transition:width .5s }
</style>

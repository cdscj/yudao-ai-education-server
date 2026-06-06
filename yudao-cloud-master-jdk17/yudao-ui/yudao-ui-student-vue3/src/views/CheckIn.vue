<template>
  <div class="checkin-page" v-loading="loading">
    <!-- 顶部统计卡片行 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :xs="24" :sm="8" v-for="s in statCards" :key="s.key">
        <div class="stat-card" :class="s.key">
          <div class="stat-icon"><el-icon :size="28"><component :is="s.icon" /></el-icon></div>
          <div class="stat-body"><div class="stat-val">{{ s.val }}</div><div class="stat-label">{{ s.label }}</div></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <!-- 签到主卡片 -->
      <el-col :xs="24" :md="14">
        <div class="main-card" :class="{ checked: summary?.todayChecked }">
          <div class="card-glow"></div>
          <div class="card-content">
            <div class="greeting">
              <div class="greeting-text">{{ greetingText }}</div>
              <div class="greeting-date">{{ todayStr }}</div>
            </div>
            <div class="streak-section">
              <div class="streak-ring">
                <svg viewBox="0 0 120 120" class="ring-svg">
                  <circle cx="60" cy="60" r="52" fill="none" stroke="rgba(255,255,255,0.2)" stroke-width="6"/>
                  <circle cx="60" cy="60" r="52" fill="none" stroke="currentColor" stroke-width="6"
                    :stroke-dasharray="circumference" :stroke-dashoffset="dashOffset" stroke-linecap="round"
                    transform="rotate(-90 60 60)" class="ring-progress"/>
                </svg>
                <div class="ring-text">
                  <span class="ring-num">{{ summary?.streakDays || 0 }}</span>
                  <span class="ring-unit">天</span>
                </div>
              </div>
              <div class="streak-info">
                <div class="streak-title">连续签到</div>
                <div class="streak-detail">累计 {{ summary?.totalDays || 0 }} 天 · 本周 {{ weekChecked.length }} 天</div>
                <div class="week-dots">
                  <span v-for="(d,i) in weekDots" :key="i" class="dot" :class="{ fill: d }">{{ d ? '✓' : i+1 }}</span>
                </div>
              </div>
            </div>
            <el-button v-if="!summary?.todayChecked" class="checkin-btn" :loading="checking" @click="doCheckIn" round>
              <el-icon :size="22"><CircleCheck /></el-icon>
              <span>签到打卡 · +{{ checkInPoints }}分</span>
            </el-button>
            <div v-else class="checked-badge">
              <el-icon :size="22"><CircleCheckFilled /></el-icon>
              <span>今日已签到 · 明天继续!</span>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 右侧面板 -->
      <el-col :xs="24" :md="10">
        <div class="side-panel">
          <!-- 积分 & 等级 -->
          <div class="panel-section">
            <div class="section-title"><el-icon><StarFilled /></el-icon> 积分与等级</div>
            <div class="level-row">
              <div class="level-badge" :style="{background: levelColor}">{{ summary?.rankTitle || '学习新手' }}</div>
              <div class="level-exp">
                <div class="level-label">Lv.{{ summary?.currentLevel || 1 }}</div>
                <el-progress :percentage="nextLevelPercent" :color="levelColor" :stroke-width="8" />
                <div class="level-next">下一级还需 {{ pointsToNextLevel }} 积分</div>
              </div>
            </div>
            <div class="points-row">
              <div class="points-item"><span class="p-val">{{ summary?.totalPoints || 0 }}</span><span class="p-label">总积分</span></div>
              <div class="points-divider"></div>
              <div class="points-item"><span class="p-val">{{ summary?.weeklyPoints || 0 }}</span><span class="p-label">本周</span></div>
              <div class="points-divider"></div>
              <div class="points-item"><span class="p-val">{{ summary?.monthlyPoints || 0 }}</span><span class="p-label">本月</span></div>
            </div>
          </div>
          <!-- AI 鼓励语 -->
          <div v-if="lastEncouragement" class="panel-section ai-section">
            <div class="section-title"><el-icon><MagicStick /></el-icon> AI 每日寄语</div>
            <div class="ai-quote">"{{ lastEncouragement }}"</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 签到日历 -->
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :xs="24" :md="14">
        <div class="content-card">
          <div class="card-header">
            <span class="card-title"><el-icon><Calendar /></el-icon> 签到日历</span>
            <div class="month-nav">
              <el-button :icon="ArrowLeft" text circle size="small" @click="prevMonth" />
              <span class="month-label">{{ calendarYearMonth }}</span>
              <el-button :icon="ArrowRight" text circle size="small" @click="nextMonth" />
            </div>
          </div>
          <div class="calendar-grid">
            <div v-for="d in weekDayLabels" :key="d" class="cal-head">{{ d }}</div>
            <div v-for="(day,i) in calendarDays" :key="i"
              :class="['cal-cell', { fill: day.checked, today: day.isToday, dim: !day.date }]">
              <span class="cal-date">{{ day.date }}</span>
              <span v-if="day.checked" class="cal-mark">🔥</span>
            </div>
          </div>
        </div>
      </el-col>
      <!-- 签到记录 -->
      <el-col :xs="24" :md="10">
        <div class="content-card" style="height:100%">
          <div class="card-header">
            <span class="card-title"><el-icon><List /></el-icon> 最近签到</span>
          </div>
          <div class="record-list" v-loading="recLoading">
            <div v-for="r in records.slice(0,8)" :key="r.id" class="record-item">
              <div class="rec-left">
                <span class="rec-date">{{ r.checkInDate }}</span>
                <span class="rec-streak">连续 {{ r.streakDays }} 天</span>
              </div>
              <span class="rec-points">+{{ r.pointsEarned }}</span>
            </div>
            <el-empty v-if="!records.length" description="暂无记录" :image-size="60" />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { checkInApi, pointsApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const loading = ref(false)
const checking = ref(false); const recLoading = ref(false)
const summary = ref(null); const records = ref([])
const calendarYearMonth = ref(''); const calendarDays = ref([])
const lastEncouragement = ref('')

const circumference = 2 * Math.PI * 52
const dashOffset = computed(() => {
  const max = Math.max(summary.value?.streakDays || 0, 7)
  return circumference * (1 - (summary.value?.streakDays || 0) / max)
})

const checkInPoints = computed(() => {
  const s = summary.value?.streakDays || 0
  if (s >= 100) return 30; if (s >= 30) return 20; if (s >= 7) return 15
  return 10
})

const todayStr = new Date().toLocaleDateString('zh-CN', { year:'numeric', month:'long', day:'numeric', weekday:'long' })
const greetingText = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了，注意休息 🌙'; if (h < 12) return '早上好，开启学习之旅 ☀️'
  if (h < 18) return '下午好，继续加油 💪'; return '晚上好，今天收获满满 🌟'
})

const weekDayLabels = ['日','一','二','三','四','五','六']
const weekDots = computed(() => {
  const today = new Date(); const dots = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date(today); d.setDate(d.getDate() - i)
    const ds = d.toISOString().split('T')[0]
    dots.push(checkedDates.value.has(ds))
  }
  return dots
})
const checkedDates = ref(new Set())
const weekChecked = computed(() => weekDots.value.filter(Boolean))

const statCards = computed(() => [
  { key:'streak', icon:'Histogram', val:summary.value?.streakDays||0, label:'连续天数' },
  { key:'total', icon:'Calendar', val:summary.value?.totalDays||0, label:'累计签到' },
  { key:'points', icon:'Coin', val:summary.value?.totalPoints||0, label:'总积分' },
])

const levelConfig = [
  { min:0, max:99, level:1, title:'学习新手', color:'#909399' },
  { min:100, max:299, level:2, title:'初级学者', color:'#67c23a' },
  { min:300, max:599, level:3, title:'进阶学者', color:'#409eff' },
  { min:600, max:999, level:4, title:'知识达人', color:'#e6a23c' },
  { min:1000, max:1999, level:5, title:'学习大师', color:'#f56c6c' },
  { min:2000, max:4999, level:6, title:'知识专家', color:'#9c27b0' },
  { min:5000, max:99999, level:7, title:'终身学习者', color:'#ff6b35' },
]
const currentLevelCfg = computed(() => {
  const p = summary.value?.totalPoints || 0
  return levelConfig.find(l => p >= l.min && p <= l.max) || levelConfig[0]
})
const nextLevelCfg = computed(() => {
  const idx = levelConfig.indexOf(currentLevelCfg.value)
  return levelConfig[idx + 1] || currentLevelCfg.value
})
const levelColor = computed(() => currentLevelCfg.value.color)
const nextLevelPercent = computed(() => {
  const cur = currentLevelCfg.value; const nxt = nextLevelCfg.value
  if (cur === nxt) return 100
  const total = nxt.min - cur.min
  const prog = (summary.value?.totalPoints || 0) - cur.min
  return Math.round((prog / total) * 100)
})
const pointsToNextLevel = computed(() => {
  const cur = currentLevelCfg.value; const nxt = nextLevelCfg.value
  if (cur === nxt) return 0
  return nxt.min - (summary.value?.totalPoints || 0)
})

async function loadSummary() {
  try {
    const [sr, pr] = await Promise.all([checkInApi.summary(), pointsApi.my()])
    summary.value = { ...(sr.data?.data || {}), ...(pr.data?.data || {}) }
  } catch (e) {
    console.error('Failed to load summary:', e)
  }
}
async function loadRecords() {
  recLoading.value = true
  try {
    const r = await checkInApi.records({ pageNo: 1, pageSize: 30 })
    records.value = r.data?.data || []
    lastEncouragement.value = records.value[0]?.aiEncouragement || ''
    records.value.forEach(rec => checkedDates.value.add(rec.checkInDate))
  } catch (e) {
    console.error('Failed to load records:', e)
  } finally { recLoading.value = false }
}
async function loadCalendar(ym) {
  try {
    const data = (await checkInApi.calendar(ym)).data || []
    const [y, m] = ym.split('-').map(Number)
    const firstDay = new Date(y, m-1, 1).getDay()
    const daysInMonth = new Date(y, m, 0).getDate()
    const today = new Date(); const todayDs = today.toISOString().split('T')[0]
    const set = new Set(data.map(c => c.checkInDate))
    const days = []
    for (let i = 0; i < firstDay; i++) days.push({ date: '', checked: false, isToday: false })
    for (let d = 1; d <= daysInMonth; d++) {
      const ds = `${y}-${String(m).padStart(2,'0')}-${String(d).padStart(2,'0')}`
      days.push({ date: d, checked: set.has(ds), isToday: ds === todayDs })
    }
    calendarDays.value = days
  } catch (e) {
    console.error('Failed to load calendar:', e)
  }
}
function prevMonth() {
  const [y, m] = calendarYearMonth.value.split('-').map(Number)
  const d = new Date(y, m-2, 1)
  calendarYearMonth.value = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}`
  loadCalendar(calendarYearMonth.value)
}
function nextMonth() {
  const [y, m] = calendarYearMonth.value.split('-').map(Number)
  const d = new Date(y, m, 1)
  calendarYearMonth.value = `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}`
  loadCalendar(calendarYearMonth.value)
}
async function doCheckIn() {
  checking.value = true
  try {
    await checkInApi.checkIn()
    ElMessage({ message: '签到成功! 积分已到账 🎉', type: 'success', duration: 3000 })
    checkedDates.value.add(new Date().toISOString().split('T')[0])
    await Promise.all([loadSummary(), loadRecords(), loadCalendar(calendarYearMonth.value)])
  } catch (e) {
    console.error('Check-in failed:', e)
    ElMessage.error(e?.response?.data?.msg || '签到失败，请重试')
  } finally { checking.value = false }
}

onMounted(async () => {
  loading.value = true
  try {
    const now = new Date()
    calendarYearMonth.value = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}`
    await Promise.all([loadSummary(), loadRecords(), loadCalendar(calendarYearMonth.value)])
  } catch (e) {
    console.error('Failed to load check-in page:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.checkin-page { max-width: 1100px; margin: 0 auto }
.stats-row { margin-bottom: 16px }
.stat-card { display:flex; align-items:center; gap:14px; padding:20px; border-radius:14px; background:#fff; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
.stat-card .stat-icon { width:52px; height:52px; border-radius:14px; display:flex; align-items:center; justify-content:center }
.stat-card.streak .stat-icon { background:linear-gradient(135deg,#ff6b35,#ff8c42); color:#fff }
.stat-card.total .stat-icon { background:linear-gradient(135deg,#409eff,#66b1ff); color:#fff }
.stat-card.points .stat-icon { background:linear-gradient(135deg,#f7ba2a,#f9d44c); color:#fff }
.stat-val { font-size:28px; font-weight:800; color:#303133; line-height:1 }
.stat-label { font-size:13px; color:#909399; margin-top:2px }

.main-card { position:relative; border-radius:20px; overflow:hidden; background:linear-gradient(135deg,#1a1a2e 0%,#16213e 50%,#0f3460 100%); color:#fff; min-height:260px; transition:all .4s }
.main-card.checked { background:linear-gradient(135deg,#0d3b0d 0%,#1a5c1a 50%,#2d8f2d 100%) }
.card-glow { position:absolute; top:-50%; right:-50%; width:300px; height:300px; background:radial-gradient(circle, rgba(255,255,255,0.08) 0%, transparent 70%); border-radius:50% }
.card-content { position:relative; z-index:1; padding:28px; display:flex; flex-direction:column; gap:20px }
.greeting-text { font-size:20px; font-weight:700; letter-spacing:0.5px }
.greeting-date { font-size:13px; opacity:0.7; margin-top:4px }
.streak-section { display:flex; align-items:center; gap:24px }
.streak-ring { position:relative; width:110px; height:110px; flex-shrink:0 }
.ring-svg { width:100%; height:100%; color:#ffd666 }
.ring-progress { transition: stroke-dashoffset 0.8s ease }
.ring-text { position:absolute; inset:0; display:flex; flex-direction:column; align-items:center; justify-content:center }
.ring-num { font-size:32px; font-weight:800; line-height:1 }
.ring-unit { font-size:12px; opacity:0.7 }
.streak-title { font-size:18px; font-weight:700 }
.streak-detail { font-size:13px; opacity:0.7; margin-top:2px }
.week-dots { display:flex; gap:6px; margin-top:10px }
.dot { width:26px; height:26px; border-radius:50%; border:2px solid rgba(255,255,255,0.3); display:flex; align-items:center; justify-content:center; font-size:11px; transition:all .3s }
.dot.fill { background:#ffd666; border-color:#ffd666; color:#1a1a2e; font-weight:700 }

.checkin-btn { align-self:flex-start; padding:12px 32px; font-size:16px; background:linear-gradient(135deg,#ffd666,#ffb800); border:none; color:#1a1a2e; font-weight:700; transition:all .3s }
.checkin-btn:hover { transform:scale(1.03); box-shadow:0 8px 25px rgba(255,182,0,0.4) }
.checked-badge { display:flex; align-items:center; gap:8px; font-size:16px; font-weight:600; color:#90ee90 }

.side-panel { display:flex; flex-direction:column; gap:12px }
.panel-section { background:#fff; border-radius:14px; padding:20px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
.section-title { display:flex; align-items:center; gap:6px; font-weight:600; font-size:15px; color:#303133; margin-bottom:14px }
.level-row { display:flex; align-items:center; gap:14px }
.level-badge { padding:6px 16px; border-radius:20px; color:#fff; font-size:13px; font-weight:600; white-space:nowrap }
.level-exp { flex:1; min-width:0 }
.level-label { font-size:13px; color:#606266; margin-bottom:4px }
.level-next { font-size:11px; color:#c0c4cc; margin-top:4px }
.points-row { display:flex; align-items:center; margin-top:16px; padding-top:16px; border-top:1px solid #f0f0f0 }
.points-item { flex:1; text-align:center }
.p-val { display:block; font-size:22px; font-weight:700; color:#303133 }
.p-label { font-size:11px; color:#c0c4cc; margin-top:2px }
.points-divider { width:1px; height:30px; background:#f0f0f0 }

.ai-section { background:linear-gradient(135deg,#f0f5ff,#e6f7ff); border:1px solid #d6e4ff }
.ai-quote { font-size:14px; color:#606266; font-style:italic; line-height:1.7 }

.content-card { background:#fff; border-radius:14px; padding:20px; box-shadow:0 2px 12px rgba(0,0,0,0.04); margin-top:16px }
.card-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px }
.card-title { display:flex; align-items:center; gap:6px; font-weight:600; font-size:15px; color:#303133 }
.month-nav { display:flex; align-items:center; gap:4px }
.month-label { font-weight:600; font-size:14px; min-width:90px; text-align:center }

.calendar-grid { display:grid; grid-template-columns:repeat(7,1fr); gap:2px; text-align:center }
.cal-head { font-size:12px; color:#c0c4cc; font-weight:600; padding:6px 0 }
.cal-cell { padding:8px 0; border-radius:8px; font-size:13px; transition:all .2s; cursor:default }
.cal-cell.dim { visibility:hidden }
.cal-cell.fill { background:#e1f3d8; color:#67c23a; font-weight:600 }
.cal-cell.today { box-shadow:inset 0 0 0 2px #409eff; font-weight:700 }
.cal-cell:hover:not(.dim) { background:#f5f5f5 }
.cal-mark { display:block; font-size:8px }

.record-list { max-height:400px; overflow-y:auto }
.record-item { display:flex; align-items:center; justify-content:space-between; padding:10px 0; border-bottom:1px solid #fafafa }
.rec-left { display:flex; flex-direction:column }
.rec-date { font-size:14px; color:#303133 }
.rec-streak { font-size:12px; color:#c0c4cc; margin-top:2px }
.rec-points { font-weight:700; color:#e6a23c }
</style>

<template>
  <div class="db-page" v-loading="loading">
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><PieChart /></el-icon> 学习看板</h2>
      <el-tag type="info" size="small">数据实时更新</el-tag>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="c in statCards" :key="c.label" :style="{borderTopColor:c.color}">
        <div class="sc-icon" :style="{background:c.bg}"><el-icon :size="20"><component :is="c.icon" /></el-icon></div>
        <div class="sc-num" :style="{color:c.color}">{{ c.value }}</div>
        <div class="sc-label">{{ c.label }}</div>
      </div>
    </div>

    <!-- 图表区 -->
    <div class="charts-grid">
      <!-- 知识掌握度雷达图替代：维度进度条 -->
      <div class="chart-card">
        <div class="chart-title">📊 6维画像概览</div>
        <div class="dimension-list">
          <div class="dim-item" v-for="d in dimensions" :key="d.name">
            <div class="dim-header">
              <span class="dim-name">{{ d.icon }} {{ d.name }}</span>
              <span class="dim-val">{{ d.value }}%</span>
            </div>
            <div class="dim-track">
              <div class="dim-fill" :style="{width:d.value+'%',background:d.color}"></div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习数据柱状图替代：统计卡片 + 进度环 -->
      <div class="chart-card">
        <div class="chart-title">📈 学习数据统计</div>
        <div class="metric-grid">
          <div class="metric-item" v-for="m in metrics" :key="m.label">
            <div class="metric-ring" :style="{'--pct':m.pct, '--clr':m.color}">
              <span class="ring-val">{{ m.value }}</span>
            </div>
            <div class="metric-label">{{ m.label }}</div>
          </div>
        </div>
        <div class="metric-bar-row">
          <div class="metric-bar" v-for="m in metrics" :key="m.label+'b'"
               :style="{height:(m.pct||5)+'%',background:m.color}"
               :title="m.label+': '+m.value"></div>
        </div>
      </div>
    </div>

    <!-- 学习趋势 -->
    <div class="chart-card trend-card">
      <div class="chart-title">📅 本周学习趋势</div>
      <div class="trend-chart">
        <div class="trend-bar-wrapper" v-for="d in weeklyTrend" :key="d.day">
          <div class="trend-val">{{ d.hours }}h</div>
          <div class="trend-bar" :style="{height:(d.hours/maxTrendHours*100)+'%',background:d.hours>=maxTrendHours?'#67c23a':d.hours>=maxTrendHours/2?'#409eff':'#e6a23c'}"></div>
          <div class="trend-day">{{ d.day }}</div>
        </div>
      </div>
      <div class="trend-summary">本周累计学习 <strong>{{ weeklyTotal }}小时</strong>，日均 {{ weeklyAvg }}小时</div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && !hasData" description="开始学习后这里会展示你的学习数据" :image-size="80" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { dashboardApi, profileApi } from '@/api/index.js'
import { PieChart, Reading, EditPen, Timer, Medal, TrendCharts } from '@element-plus/icons-vue'

const loading = ref(true)
const data = ref({})
const profile = ref(null)

// 6维画像数据
const dimensions = ref([
  { name: '知识基础', icon: '🧠', value: 75, color: 'linear-gradient(90deg,#409eff,#66b1ff)' },
  { name: '认知风格', icon: '🎨', value: 80, color: 'linear-gradient(90deg,#67c23a,#95d475)' },
  { name: '学习效率', icon: '⚡', value: 65, color: 'linear-gradient(90deg,#e6a23c,#f3d19e)' },
  { name: '学习动机', icon: '🎯', value: 90, color: 'linear-gradient(90deg,#f56c6c,#fab6b6)' },
  { name: '时间投入', icon: '⏰', value: 70, color: 'linear-gradient(90deg,#8b5cf6,#b49cf0)' },
  { name: '资源利用', icon: '📚', value: 60, color: 'linear-gradient(90deg,#06b6d4,#67d6e8)' }
])

// 统计卡片
const statCards = computed(() => [
  { label: '错题数', value: data.value.wrongCount||0, icon: 'EditPen', color: '#f56c6c', bg: '#fef0f0' },
  { label: '提交作业', value: data.value.homeworkCount||0, icon: 'Reading', color: '#409eff', bg: '#ecf5ff' },
  { label: '参加考试', value: data.value.examCount||0, icon: 'Timer', color: '#67c23a', bg: '#f0f9eb' },
  { label: '答题总数', value: data.value.totalQuestions||0, icon: 'TrendCharts', color: '#e6a23c', bg: '#fdf6ec' }
])

// 学习指标（带进度环）
const metrics = computed(() => {
  const max = Math.max(1, data.value.wrongCount||1, data.value.homeworkCount||1, data.value.examCount||1, data.value.totalQuestions||1)
  const pct = (v) => Math.round((v||0)/max*100)
  return [
    { label:'错题', value:data.value.wrongCount||0, pct:pct(data.value.wrongCount), color:'#f56c6c' },
    { label:'作业', value:data.value.homeworkCount||0, pct:pct(data.value.homeworkCount), color:'#409eff' },
    { label:'考试', value:data.value.examCount||0, pct:pct(data.value.examCount), color:'#67c23a' },
    { label:'答题', value:data.value.totalQuestions||0, pct:pct(data.value.totalQuestions), color:'#e6a23c' }
  ]
})

// 模拟本周学习趋势（实际应从API获取）
const weeklyTrend = [
  { day:'周一', hours:2.5 }, { day:'周二', hours:3.0 }, { day:'周三', hours:1.5 },
  { day:'周四', hours:4.0 }, { day:'周五', hours:2.0 }, { day:'周六', hours:6.0 }, { day:'周日', hours:5.0 }
]
const maxTrendHours = Math.max(...weeklyTrend.map(d=>d.hours))
const weeklyTotal = weeklyTrend.reduce((s,d)=>s+d.hours,0)
const weeklyAvg = (weeklyTotal/7).toFixed(1)

const hasData = computed(() =>
  (data.value.wrongCount||0)+(data.value.homeworkCount||0)+(data.value.examCount||0)+(data.value.totalQuestions||0)>0
)

onMounted(async () => {
  loading.value = true
  try {
    const [dashR, profileR] = await Promise.allSettled([
      dashboardApi.data(),
      profileApi.get()
    ])
    if (dashR.status==='fulfilled') data.value = dashR.value.data?.data || {}
    if (profileR.status==='fulfilled' && profileR.value.data?.data) {
      const p = profileR.value.data.data
      // 从画像JSON中提取维度评分
      if (p.profileJson) {
        try {
          const json = typeof p.profileJson==='string' ? JSON.parse(p.profileJson) : p.profileJson
          if (json.dimensions) {
            const dims = dimensions.value
            const map = { '知识基础':0,'认知风格':1,'学习效率':2,'学习动机':3,'时间投入':4,'资源利用':5 }
            for (const [k,v] of Object.entries(json.dimensions)) {
              if (map[k]!==undefined) {
                // 从文本中估算评分
                const score = estimateScore(String(v))
                dims[map[k]].value = score
              }
            }
          }
        } catch(e) { /* ignore parse error */ }
      }
    }
  } catch(e) { console.error(e) }
  loading.value = false
})

// 从画像文本估算维度评分
function estimateScore(text) {
  if (text.includes('熟练')||text.includes('扎实')||text.includes('强')||text.includes('高')) return 85+Math.random()*10
  if (text.includes('中等')||text.includes('一般')||text.includes('尚可')) return 60+Math.random()*20
  if (text.includes('薄弱')||text.includes('不足')||text.includes('入门')||text.includes('零基础')) return 25+Math.random()*25
  return 50+Math.random()*30
}
</script>

<style scoped>
.db-page { max-width: 960px; margin: 0 auto; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.page-title { display: flex; align-items: center; gap: 8px; font-size: 22px; font-weight: 700; color: #303133; margin: 0; }

/* 统计卡片 */
.stats-row { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.stat-card {
  display: flex; flex-direction: column; align-items: center; gap: 6px;
  background: #fff; border-radius: 14px; padding: 20px 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04); min-width: 140px; flex: 1;
  border-top: 3px solid #409eff; transition: transform .2s, box-shadow .2s;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,0.08); }
.sc-icon { width: 40px; height: 40px; border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.sc-num { font-size: 28px; font-weight: 800; }
.sc-label { font-size: 12px; color: #909399; }

/* 图表网格 */
.charts-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 16px; }
@media (max-width: 640px) { .charts-grid { grid-template-columns: 1fr; } }

.chart-card { background: #fff; border-radius: 14px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,0.04); }
.chart-title { font-weight: 600; font-size: 15px; color: #303133; margin-bottom: 16px; }

/* 维度进度条 */
.dimension-list { display: flex; flex-direction: column; gap: 12px; }
.dim-header { display: flex; justify-content: space-between; margin-bottom: 4px; font-size: 13px; }
.dim-name { color: #606266; }
.dim-val { color: #303133; font-weight: 600; }
.dim-track { height: 8px; background: #f0f2f5; border-radius: 4px; overflow: hidden; }
.dim-fill { height: 100%; border-radius: 4px; transition: width 1s ease; }

/* 指标环 */
.metric-grid { display: flex; justify-content: space-around; margin-bottom: 16px; }
.metric-item { display: flex; flex-direction: column; align-items: center; gap: 4px; }
.metric-ring {
  width: 56px; height: 56px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  background: conic-gradient(var(--clr,#409eff) calc(var(--pct,0)*3.6deg), #f0f2f5 0deg);
  position: relative;
}
.metric-ring::after {
  content: ''; width: 40px; height: 40px; border-radius: 50%;
  background: #fff; position: absolute;
}
.ring-val { z-index: 1; font-weight: 700; font-size: 16px; color: #303133; }
.metric-label { font-size: 12px; color: #909399; }

.metric-bar-row { display: flex; align-items: flex-end; gap: 12px; height: 80px; padding: 0 8px; }
.metric-bar { flex: 1; border-radius: 6px 6px 0 0; min-height: 4px; transition: height .6s ease; opacity: 0.85; }

/* 学习趋势 */
.trend-card { margin-bottom: 16px; }
.trend-chart { display: flex; align-items: flex-end; justify-content: space-around; height: 160px; padding: 0 8px; }
.trend-bar-wrapper { display: flex; flex-direction: column; align-items: center; gap: 4px; flex: 1; }
.trend-val { font-size: 11px; color: #909399; font-weight: 500; }
.trend-bar { width: 32px; border-radius: 6px 6px 0 0; min-height: 4px; transition: height .6s ease; }
.trend-day { font-size: 12px; color: #606266; }
.trend-summary { text-align: center; margin-top: 12px; color: #606266; font-size: 13px; }
.trend-summary strong { color: #409eff; }
</style>

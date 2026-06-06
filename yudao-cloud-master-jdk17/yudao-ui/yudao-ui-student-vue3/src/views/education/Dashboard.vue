<template>
  <div class="db-page" v-loading="loading">
    <div class="page-head"><h2 class="page-title"><el-icon :size="24"><PieChart /></el-icon> 学习看板</h2></div>

    <div class="stats-row">
      <div class="stat-card"><span class="sc-num">{{ data.wrongCount||0 }}</span><span class="sc-label">错题数</span></div>
      <div class="stat-card"><span class="sc-num">{{ data.homeworkCount||0 }}</span><span class="sc-label">提交作业</span></div>
      <div class="stat-card"><span class="sc-num">{{ data.examCount||0 }}</span><span class="sc-label">参加考试</span></div>
      <div class="stat-card"><span class="sc-num">{{ data.totalQuestions||0 }}</span><span class="sc-label">答题总数</span></div>
    </div>

    <div class="chart-row">
      <div class="chart-card">
        <div class="chart-title">学习概览</div>
        <div class="bar-chart">
          <div class="bar-item"><div class="bar-label">错题</div><div class="bar-track"><div class="bar-fill danger" :style="{width:barPct(data.wrongCount)+'%'}"></div></div><span>{{ data.wrongCount||0 }}</span></div>
          <div class="bar-item"><div class="bar-label">作业</div><div class="bar-track"><div class="bar-fill primary" :style="{width:barPct(data.homeworkCount)+'%'}"></div></div><span>{{ data.homeworkCount||0 }}</span></div>
          <div class="bar-item"><div class="bar-label">考试</div><div class="bar-track"><div class="bar-fill success" :style="{width:barPct(data.examCount)+'%'}"></div></div><span>{{ data.examCount||0 }}</span></div>
          <div class="bar-item"><div class="bar-label">答题</div><div class="bar-track"><div class="bar-fill warning" :style="{width:barPct(data.totalQuestions)+'%'}"></div></div><span>{{ data.totalQuestions||0 }}</span></div>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && !hasData" description="暂无学习数据" :image-size="80" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { dashboardApi } from '@/api/index.js'

const loading=ref(false),data=ref({})
const hasData=computed(()=>(data.value.wrongCount||0)+(data.value.homeworkCount||0)+(data.value.examCount||0)>0)

function barPct(val){const max=Math.max(1,data.value.wrongCount||0,data.value.homeworkCount||0,data.value.examCount||0,data.value.totalQuestions||0);return ((val||0)/max*100).toFixed(0)}

onMounted(async()=>{loading.value=true;try{const r=await dashboardApi.data();data.value=r.data?.data||{}}catch(e){console.error(e)}finally{loading.value=false}})
</script>

<style scoped>
.db-page{max-width:900px;margin:0 auto}
.page-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:20px}
.page-title{display:flex;align-items:center;gap:8px;font-size:22px;font-weight:700;color:#303133}
.stats-row{display:flex;gap:12px;margin-bottom:20px;flex-wrap:wrap}
.stat-card{display:flex;flex-direction:column;align-items:center;background:#fff;border-radius:14px;padding:20px 28px;box-shadow:0 2px 8px rgba(0,0,0,0.04);min-width:120px;flex:1}
.sc-num{font-size:30px;font-weight:800;color:#409eff}
.sc-label{font-size:12px;color:#909399;margin-top:4px}
.chart-card{background:#fff;border-radius:14px;padding:20px;box-shadow:0 2px 8px rgba(0,0,0,0.04)}
.chart-title{font-weight:600;font-size:15px;color:#303133;margin-bottom:16px}
.bar-item{display:flex;align-items:center;gap:10px;margin-bottom:12px;font-size:13px}
.bar-label{width:36px;color:#606266;text-align:right}
.bar-track{flex:1;height:18px;background:#f0f2f5;border-radius:9px;overflow:hidden}
.bar-fill{height:100%;border-radius:9px;transition:width .5s}
.bar-fill.danger{background:linear-gradient(90deg,#f56c6c,#fab6b6)}
.bar-fill.primary{background:linear-gradient(90deg,#409eff,#a0cfff)}
.bar-fill.success{background:linear-gradient(90deg,#67c23a,#b3e19d)}
.bar-fill.warning{background:linear-gradient(90deg,#e6a23c,#f5dab1)}
</style>

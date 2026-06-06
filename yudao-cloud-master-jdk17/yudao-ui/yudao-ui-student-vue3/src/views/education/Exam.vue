<template>
  <div class="ex-page" v-loading="loading">
    <div class="page-head"><h2 class="page-title"><el-icon :size="24"><Edit /></el-icon> 模拟考试</h2></div>

    <div v-if="list.length" class="ex-list">
      <div v-for="e in list" :key="e.id" class="ex-card" @click="viewExam(e)">
        <div class="ex-left">
          <div class="ex-title">{{ e.title }}</div>
          <div class="ex-meta">满分{{ e.totalScore }} · 限时{{ e.timeLimit }}分钟 · 难度Lv.{{ e.difficulty||1 }}</div>
        </div>
        <el-button type="primary" size="small" @click.stop="startExam(e)">开始考试</el-button>
      </div>
      <div class="pager" v-if="total>pageSize">
        <el-pagination layout="prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNo" @change="load" small />
      </div>
    </div>
    <el-empty v-else description="暂无考试" :image-size="80" />

    <!-- 考试中 -->
    <el-dialog v-model="examVisible" :title="activeExam?.title" width="700px" fullscreen :close-on-click-modal="false" :show-close="false">
      <div v-if="activeExam" class="exam-body">
        <div class="exam-timer">⏱ 剩余 {{ timerDisplay }}</div>
        <el-input v-model="examAnswer" placeholder="请输入你的答案..." type="textarea" :rows="10" />
      </div>
      <template #footer>
        <el-button type="primary" @click="doSubmit" :loading="submitting">交卷</el-button>
      </template>
    </el-dialog>

    <!-- 考试记录 -->
    <el-dialog v-model="recordVisible" title="历史记录" width="560px">
      <div v-if="records.length" class="rec-list">
        <div v-for="r in records" :key="r.id" class="rec-item">
          <span>考试{{ r.examId }} · 得分{{ r.totalScore||'-' }}</span>
          <el-tag size="small" :type="r.status==='SUBMITTED'?'success':'info'">{{ r.status }}</el-tag>
          <span class="rec-time">{{ r.submitTime }}</span>
        </div>
      </div>
      <el-empty v-else description="暂无记录" :image-size="60" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref,computed,onMounted,onUnmounted } from 'vue'
import { examApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading=ref(false),list=ref([]),pageNo=ref(1),pageSize=ref(10),total=ref(0)
const examVisible=ref(false),activeExam=ref(null),examAnswer=ref(''),submitting=ref(false)
const recordVisible=ref(false),records=ref([])
const timer=ref(0),timerHandle=ref(null)
const timerDisplay=computed(()=>{const m=Math.floor(timer.value/60),s=timer.value%60;return `${m}:${String(s).padStart(2,'0')}`})

async function load(){loading.value=true
  try{const r=await examApi.list({pageNo:pageNo.value,pageSize:pageSize.value});const d=r.data?.data;list.value=d?.list||[];total.value=d?.total||0}
  catch(e){ElMessage.error('加载失败')}finally{loading.value=false}
}

async function viewExam(e){
  recordVisible.value=true
  try{const r=await examApi.history({pageNo:1,pageSize:50});records.value=r.data?.data?.list||[]}
  catch(e){records.value=[]}
}

async function startExam(e){
  try{await examApi.start(e.id);activeExam.value=e;examAnswer.value='';timer.value=(e.timeLimit||120)*60;examVisible.value=true
    timerHandle.value=setInterval(()=>{if(timer.value>0) timer.value--; else {clearInterval(timerHandle.value);ElMessage.warning('时间到');doSubmit()}},1000)}
  catch(e){ElMessage.error(e?.response?.data?.msg||'开始失败')}
}

async function doSubmit(){
  if(!examAnswer.value.trim())return ElMessage.warning('请输入答案')
  submitting.value=true
  try{await examApi.submit({examId:activeExam.value.id,answers:examAnswer.value,durationSeconds:((activeExam.value.timeLimit||120)*60)-timer.value})
    ElMessage.success('交卷成功');examVisible.value=false;if(timerHandle.value)clearInterval(timerHandle.value);load()}
  catch(e){ElMessage.error(e?.response?.data?.msg||'交卷失败')}finally{submitting.value=false}
}

onMounted(load)
onUnmounted(()=>{if(timerHandle.value)clearInterval(timerHandle.value)})
</script>

<style scoped>
.ex-page{max-width:900px;margin:0 auto}
.page-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:20px}
.page-title{display:flex;align-items:center;gap:8px;font-size:22px;font-weight:700;color:#303133}
.ex-card{display:flex;align-items:center;justify-content:space-between;background:#fff;border-radius:12px;padding:16px 20px;margin-bottom:10px;cursor:pointer;box-shadow:0 2px 8px rgba(0,0,0,0.04);transition:transform .2s}
.ex-card:hover{transform:translateY(-2px);box-shadow:0 4px 16px rgba(0,0,0,0.08)}
.ex-left{flex:1;min-width:0}
.ex-title{font-weight:600;font-size:15px;color:#303133}
.ex-meta{font-size:12px;color:#909399;margin-top:4px}
.pager{display:flex;justify-content:center;padding-top:16px}
.exam-body{padding:8px 0}
.exam-timer{font-size:20px;font-weight:700;color:#e6a23c;margin-bottom:16px;text-align:center}
.rec-item{display:flex;align-items:center;gap:12px;padding:8px 0;border-bottom:1px solid #f5f5f5;font-size:13px}
.rec-time{font-size:11px;color:#909399;margin-left:auto}
</style>

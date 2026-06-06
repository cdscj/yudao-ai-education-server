<template>
  <div class="qb-page" v-loading="loading">
    <div class="page-head"><h2 class="page-title"><el-icon :size="24"><Reading /></el-icon> 题库</h2></div>

    <div class="filter-bar">
      <el-select v-model="filterSubjectId" placeholder="选择学科" clearable style="width:150px" @change="load">
        <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
      </el-select>
      <el-select v-model="filterType" placeholder="题目类型" clearable style="width:130px" @change="load">
        <el-option label="选择题" value="CHOICE" /><el-option label="判断题" value="JUDGE" />
        <el-option label="简答题" value="SHORT_ANSWER" /><el-option label="编程题" value="PROGRAMMING" />
      </el-select>
      <el-select v-model="filterDifficulty" placeholder="难度" clearable style="width:100px" @change="load">
        <el-option :value="1" label="Lv.1" /><el-option :value="2" label="Lv.2" />
        <el-option :value="3" label="Lv.3" /><el-option :value="4" label="Lv.4" /><el-option :value="5" label="Lv.5" />
      </el-select>
    </div>

    <div class="qb-list" v-if="list.length">
      <div v-for="q in list" :key="q.id" class="qb-card" @click="showDetail(q)">
        <div class="qc-left">
          <div class="qc-title">{{ q.title }}</div>
          <div class="qc-meta">
            <el-tag size="small" type="info">{{ q.questionType }}</el-tag>
            <span>难度 Lv.{{ q.difficulty }}</span>
          </div>
        </div>
        <el-icon><ArrowRight /></el-icon>
      </div>
      <div class="pager" v-if="total>pageSize">
        <el-pagination layout="prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNo" @change="load" small />
      </div>
    </div>
    <el-empty v-else description="暂无题目" :image-size="80" />

    <el-dialog v-model="detailVisible" title="题目详情" width="560px">
      <div v-if="detail" class="detail-body">
        <div class="d-title">{{ detail.title }}</div>
        <div v-if="detail.questionType==='CHOICE' && detail.options" class="d-options">
          <div v-for="(v,k) in parseOptions(detail.options)" :key="k" class="d-opt">{{ k }}. {{ v }}</div>
        </div>
        <div class="d-answer"><strong>答案：</strong>{{ detail.answer || '(无)' }}</div>
        <div v-if="detail.analysis" class="d-analysis"><strong>解析：</strong>{{ detail.analysis }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { questionApi, subjectApi } from '@/api/index.js'

const loading=ref(false),list=ref([]),total=ref(0),pageNo=ref(1),pageSize=ref(10)
const filterSubjectId=ref(null),filterType=ref(null),filterDifficulty=ref(null)
const subjects=ref([])
const detailVisible=ref(false),detail=ref(null)

async function loadSubjects(){ try{const r=await subjectApi.list();subjects.value=r.data?.data||[]}catch(e){} }
async function load(){ loading.value=true; try{const r=await questionApi.page({subjectId:filterSubjectId.value,questionType:filterType.value,difficulty:filterDifficulty.value,pageNo:pageNo.value,pageSize:pageSize.value});const d=r.data?.data;list.value=d?.list||[];total.value=d?.total||0}catch(e){} finally{loading.value=false} }
function showDetail(q){ detail.value=q; detailVisible.value=true }
function parseOptions(opts){ try{return JSON.parse(opts)}catch(e){return{}} }

onMounted(async()=>{await loadSubjects();load()})
</script>

<style scoped>
.qb-page{max-width:900px;margin:0 auto}
.page-head{margin-bottom:20px}
.page-title{display:flex;align-items:center;gap:8px;font-size:22px;font-weight:700;color:#303133}
.filter-bar{display:flex;gap:10px;margin-bottom:16px;flex-wrap:wrap}
.qb-card{display:flex;align-items:center;justify-content:space-between;background:#fff;border-radius:12px;padding:16px 20px;margin-bottom:10px;cursor:pointer;box-shadow:0 2px 8px rgba(0,0,0,0.04);transition:transform .2s}
.qb-card:hover{transform:translateY(-2px)}
.qc-left{flex:1;min-width:0}
.qc-title{font-weight:600;font-size:14px;color:#303133}
.qc-meta{display:flex;align-items:center;gap:8px;margin-top:6px;font-size:12px;color:#909399}
.pager{display:flex;justify-content:center;padding-top:16px}
.detail-body{padding:8px 0}
.d-title{font-weight:600;font-size:16px;margin-bottom:16px;color:#303133}
.d-options{margin-bottom:14px}
.d-opt{padding:6px 10px;margin-bottom:4px;background:#f8f9fb;border-radius:6px;font-size:14px}
.d-answer,.d-analysis{font-size:14px;margin-bottom:10px;line-height:1.6}
</style>

<template>
  <div class="sp-page" v-loading="loading">
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><Clock /></el-icon> 学习计划</h2>
      <el-button type="primary" @click="showCreate=true"><el-icon><Plus /></el-icon> 创建计划</el-button>
    </div>

    <!-- 当前计划 -->
    <div v-if="active" class="active-card">
      <div class="ac-header">
        <div class="ac-title">{{ active.title }}</div>
        <el-tag :type="active.status==='ACTIVE'?'success':'info'" size="small">{{ active.status==='ACTIVE'?'进行中':'已完成' }}</el-tag>
      </div>
      <div class="ac-meta">{{ active.startDate }} ~ {{ active.endDate }} · {{ active.planType==='WEEKLY'?'周计划':'月计划' }}</div>
      <div class="ac-goal" v-if="active.goal">🎯 {{ active.goal }}</div>
      <div class="ac-progress">
        <div class="acp-label">完成进度</div>
        <el-progress :percentage="active.progress||0" :stroke-width="12" :color="active.progress>=80?'#67c23a':'#409eff'" />
      </div>
      <el-button type="success" size="small" @click="doComplete(active.id)" style="margin-top:12px" :loading="completing">标记完成</el-button>
    </div>
    <el-empty v-else description="暂无进行中的学习计划" :image-size="80" style="margin:24px 0" />

    <!-- 历史计划 -->
    <div class="history-card" v-if="history.length">
      <div class="hc-title">历史计划</div>
      <div v-for="p in history" :key="p.id" class="hc-item">
        <div class="hci-left">
          <span class="hci-title">{{ p.title }}</span>
          <span class="hci-date">{{ p.startDate }} ~ {{ p.endDate }}</span>
        </div>
        <el-tag size="small" :type="p.status==='COMPLETED'?'success':'info'">{{ p.status==='COMPLETED'?'已完成':p.status }}</el-tag>
      </div>
    </div>

    <!-- 创建计划对话框 -->
    <el-dialog v-model="showCreate" title="创建学习计划" width="500px" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px">
        <el-form-item label="计划标题"><el-input v-model="form.title" placeholder="如：高等数学复习计划" /></el-form-item>
        <el-form-item label="计划类型">
          <el-radio-group v-model="form.planType"><el-radio-button value="WEEKLY">周计划</el-radio-button><el-radio-button value="MONTHLY">月计划</el-radio-button></el-radio-group>
        </el-form-item>
        <el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" style="width:100%" /></el-form-item>
        <el-form-item label="目标"><el-input v-model="form.goal" type="textarea" :rows="3" placeholder="如：掌握导数与微分章节" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate=false">取消</el-button>
        <el-button type="primary" @click="doCreate" :loading="creating">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { studyPlanApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading=ref(false),active=ref(null),history=ref([]),creating=ref(false),completing=ref(false)
const showCreate=ref(false)
const form=ref({title:'',planType:'WEEKLY',startDate:'',endDate:'',goal:''})

async function load() { loading.value=true
  try {
    const [a,h]=await Promise.all([
      studyPlanApi.active().catch(()=>({data:null})),
      studyPlanApi.history({pageNo:1,pageSize:20}).catch(()=>({data:{data:{list:[]}}}))
    ])
    active.value=a.data?.data; history.value=h.data?.data?.list||[]
  } catch(e){ console.error(e) } finally { loading.value=false }
}

async function doCreate() {
  if(!form.value.title||!form.value.startDate||!form.value.endDate) return ElMessage.warning('请填写完整')
  creating.value=true
  try { await studyPlanApi.create(form.value); ElMessage.success('创建成功'); showCreate.value=false; load() }
  catch(e){ ElMessage.error(e?.response?.data?.msg||'创建失败') } finally { creating.value=false }
}

async function doComplete(id) { completing.value=true
  try { await studyPlanApi.complete(id); ElMessage.success('计划已完成'); load() }
  catch(e){ ElMessage.error('操作失败') } finally { completing.value=false }
}

onMounted(load)
</script>

<style scoped>
.sp-page{max-width:900px;margin:0 auto}
.page-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:20px}
.page-title{display:flex;align-items:center;gap:8px;font-size:22px;font-weight:700;color:#303133}
.active-card{background:linear-gradient(135deg,#ecf5ff,#fff);border-radius:16px;padding:20px 24px;margin-bottom:24px;border:1px solid #d9ecff;box-shadow:0 2px 12px rgba(0,0,0,0.04)}
.ac-header{display:flex;align-items:center;justify-content:space-between;margin-bottom:8px}
.ac-title{font-weight:700;font-size:18px;color:#303133}
.ac-meta{font-size:13px;color:#909399;margin-bottom:8px}
.ac-goal{font-size:14px;color:#606266;margin-bottom:12px}
.ac-progress{margin-top:8px}
.acp-label{font-size:12px;color:#909399;margin-bottom:4px}
.history-card{background:#fff;border-radius:14px;padding:16px 20px;box-shadow:0 2px 8px rgba(0,0,0,0.04)}
.hc-title{font-weight:600;font-size:15px;color:#303133;margin-bottom:12px}
.hc-item{display:flex;align-items:center;justify-content:space-between;padding:10px 0;border-bottom:1px solid #f5f5f5}
.hc-item:last-child{border-bottom:none}
.hci-left{display:flex;flex-direction:column;gap:2px}
.hci-title{font-weight:500;font-size:14px;color:#303133}
.hci-date{font-size:11px;color:#909399}
</style>

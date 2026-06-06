<template>
  <div class="hw-page" v-loading="loading">
    <div class="page-head"><h2 class="page-title"><el-icon :size="24"><Document /></el-icon> 作业</h2></div>

    <div v-if="list.length" class="hw-list">
      <div v-for="h in list" :key="h.id" class="hw-card" @click="viewHomework(h)">
        <div class="hw-left">
          <div class="hw-title">{{ h.title }}</div>
          <div class="hw-meta">满分{{ h.totalScore }} · 及格{{ h.passScore }}分
            <span v-if="h.deadline"> · 截止 {{ h.deadline }}</span>
          </div>
        </div>
        <el-tag :type="h.publishStatus==='PUBLISHED'?'success':'info'" size="small">
          {{ h.publishStatus==='PUBLISHED'?'已发布':'草稿' }}
        </el-tag>
      </div>
      <div class="pager" v-if="total>pageSize">
        <el-pagination layout="prev,pager,next" :total="total" :page-size="pageSize" v-model:current-page="pageNo" @change="load" small />
      </div>
    </div>
    <el-empty v-else description="暂无作业" :image-size="80" />

    <el-dialog v-model="detailVisible" :title="detail?.title" width="620px" :close-on-click-modal="false">
      <div v-if="detail" class="detail-body">
        <div class="d-desc">{{ detail.description || '暂无说明' }}</div>
        <div class="d-info">满分 {{ detail.totalScore }} · 及格 {{ detail.passScore }}分
          <span v-if="detail.deadline"> · 截止 {{ detail.deadline }}</span>
        </div>
        <div v-if="submission" class="d-submitted">
          <el-alert title="已提交" type="success" :closable="false" show-icon />
          <div class="sub-meta">提交时间: {{ submission.submitTime }}</div>
          <div class="sub-answers">答案: {{ submission.answers }}</div>
        </div>
        <div v-else class="d-answer">
          <el-input v-model="answerText" placeholder="请输入你的答案(JSON格式)..." type="textarea" :rows="6" />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible=false">关闭</el-button>
        <el-button v-if="!submission" type="primary" @click="doSubmit" :loading="submitting">提交作业</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { homeworkApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading=ref(false), list=ref([]), pageNo=ref(1), pageSize=ref(10), total=ref(0)
const detailVisible=ref(false), detail=ref(null), submission=ref(null), answerText=ref(''), submitting=ref(false)

async function load() { loading.value=true
  try { const r=await homeworkApi.myPage({pageNo:pageNo.value,pageSize:pageSize.value}); const d=r.data?.data; list.value=d?.list||[]; total.value=d?.total||0 }
  catch(e){ ElMessage.error('加载失败') } finally { loading.value=false }
}

async function viewHomework(h) {
  detail.value=h; answerText.value=''; submission.value=null; detailVisible.value=true
  try { const r=await homeworkApi.mySubmission(h.id); submission.value=r.data?.data }
  catch(e){ submission.value=null }
}

async function doSubmit() {
  if(!answerText.value.trim()) return ElMessage.warning('请输入答案')
  submitting.value=true
  try { await homeworkApi.submit({homeworkId:detail.value.id,answers:answerText.value,durationSeconds:0}); ElMessage.success('提交成功'); detailVisible.value=false; load() }
  catch(e){ ElMessage.error(e?.response?.data?.msg||'提交失败') }
  finally { submitting.value=false }
}

onMounted(load)
</script>

<style scoped>
.hw-page{max-width:900px;margin:0 auto}
.page-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:20px}
.page-title{display:flex;align-items:center;gap:8px;font-size:22px;font-weight:700;color:#303133}
.hw-card{display:flex;align-items:center;justify-content:space-between;background:#fff;border-radius:12px;padding:16px 20px;margin-bottom:10px;cursor:pointer;box-shadow:0 2px 8px rgba(0,0,0,0.04);transition:transform .2s}
.hw-card:hover{transform:translateY(-2px);box-shadow:0 4px 16px rgba(0,0,0,0.08)}
.hw-left{flex:1;min-width:0}
.hw-title{font-weight:600;font-size:15px;color:#303133}
.hw-meta{font-size:12px;color:#909399;margin-top:4px}
.pager{display:flex;justify-content:center;padding-top:16px}
.detail-body{padding:8px 0}
.d-desc{color:#606266;margin-bottom:12px;line-height:1.6}
.d-info{font-size:13px;color:#909399;margin-bottom:16px}
.d-answer{margin:16px 0}
.d-submitted{margin:16px 0}
.sub-meta{font-size:12px;color:#909399;margin-top:8px}
.sub-answers{font-size:13px;color:#303133;margin-top:4px;word-break:break-all}
</style>

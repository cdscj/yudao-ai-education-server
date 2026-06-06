<template>
  <div class="goals-page" v-loading="loading">
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><Flag /></el-icon> AI 学习目标</h2>
      <el-button type="primary" round @click="showCreate = true"><el-icon><Plus /></el-icon> 创建目标</el-button>
    </div>

    <!-- 概览卡片 -->
    <el-row :gutter="14" class="overview-row">
      <el-col :xs="12" :sm="6" v-for="s in overviewCards" :key="s.key">
        <div class="ov-card" :style="{borderTopColor: s.color}">
          <div class="ov-num" :style="{color: s.color}">{{ s.val }}</div>
          <div class="ov-label">{{ s.label }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 目标时间线 -->
    <div class="goals-list" v-if="goals.length">
      <div v-for="g in goals" :key="g.id" class="goal-card" :class="{ completed: g.status === 'COMPLETED' }">
        <div class="gc-left">
          <div class="gc-icon" :style="{background: goalIconBg(g.goalType)}">{{ goalIcon(g.goalType) }}</div>
          <div class="gc-line" v-if="g !== goals[goals.length-1]"></div>
        </div>
        <div class="gc-body">
          <div class="gc-header">
            <span class="gc-title">{{ g.title }}</span>
            <el-tag :type="statusTagType(g.status)" size="small" effect="plain" round>{{ statusLabel(g.status) }}</el-tag>
          </div>
          <div class="gc-desc" v-if="g.description">{{ g.description }}</div>
          <div class="gc-progress">
            <el-progress :percentage="Math.round((g.currentValue/g.targetValue)*100)" :status="g.status==='COMPLETED'?'success':undefined" :stroke-width="10" />
            <div class="gc-progress-text">
              <span>{{ g.currentValue }} / {{ g.targetValue }} {{ unitLabel(g.goalType) }}</span>
              <span v-if="g.deadline" class="gc-deadline">截止 {{ g.deadline }}</span>
            </div>
          </div>
          <div v-if="g.aiFeedback" class="gc-ai">
            <el-icon color="#409eff"><MagicStick /></el-icon>
            <span>{{ g.aiFeedback }}</span>
          </div>
          <div class="gc-actions" v-if="g.status === 'ACTIVE'">
            <el-button size="small" round @click="openProgress(g)"><el-icon><Edit /></el-icon> 更新进度</el-button>
            <el-button size="small" type="success" round @click="doComplete(g.id)"><el-icon><CircleCheck /></el-icon> 完成目标</el-button>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="empty-card">
      <el-empty description="还没有学习目标，设定一个目标开始学习之旅吧!" :image-size="100">
        <el-button type="primary" round @click="showCreate = true">🎯 创建第一个目标</el-button>
      </el-empty>
    </div>

    <!-- 创建对话框 -->
    <el-dialog v-model="showCreate" title="🎯 创建学习目标" width="500px" :close-on-click-modal="false" class="goal-dialog">
      <el-form :model="form" label-position="top">
        <el-form-item label="目标类型">
          <el-select v-model="form.goalType" style="width:100%">
            <el-option v-for="t in goalTypes" :key="t.value" :label="t.label" :value="t.value">
              <span>{{ t.icon }} {{ t.label }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="目标标题">
          <el-input v-model="form.title" placeholder="清晰的标题能帮助你坚持下去" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="描述（可选）">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="描述一下你的目标" />
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item :label="'目标值 (' + unitLabel(form.goalType) + ')'">
              <el-input-number v-model="form.targetValue" :min="1" :max="9999" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期（可选）">
              <el-date-picker v-model="form.deadline" type="date" placeholder="无截止日期" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showCreate = false">取消</el-button>
          <el-button type="primary" :loading="creating" @click="doCreate" round>创建目标</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 进度对话框 -->
    <el-dialog v-model="showProgress" title="更新进度" width="380px" class="goal-dialog">
      <div style="text-align:center;padding:10px">
        <div style="font-weight:600;font-size:16px;margin-bottom:4px">{{ progressGoal?.title }}</div>
        <div style="color:#909399;font-size:13px;margin-bottom:16px">当前 {{ progressGoal?.currentValue }} / {{ progressGoal?.targetValue }}</div>
        <el-slider v-model="newProgress" :min="0" :max="progressGoal?.targetValue" :step="1" show-input />
      </div>
      <template #footer>
        <el-button @click="showProgress = false">取消</el-button>
        <el-button type="primary" @click="doUpdateProgress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { goalApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const goals = ref([]); const stats = ref({}); const showCreate = ref(false)
const showProgress = ref(false); const creating = ref(false)
const progressGoal = ref(null); const newProgress = ref(0)

const form = reactive({ goalType:'RESOURCE_COUNT', title:'', description:'', targetValue:10, deadline:null })

const goalTypes = [
  { value:'STUDY_MINUTES', label:'学习时长', icon:'⏱️', unit:'分钟' },
  { value:'RESOURCE_COUNT', label:'完成资源', icon:'📚', unit:'个' },
  { value:'CHECK_IN_STREAK', label:'连续签到', icon:'🔥', unit:'天' },
  { value:'POINTS_TARGET', label:'积分目标', icon:'⭐', unit:'分' },
]

const overviewCards = computed(() => [
  { key:'total', val:stats.value?.total||0, label:'总目标', color:'#409eff' },
  { key:'completed', val:stats.value?.completed||0, label:'已完成', color:'#67c23a' },
  { key:'active', val:stats.value?.active||0, label:'进行中', color:'#e6a23c' },
  { key:'rate', val:(stats.value?.total?(Math.round(stats.value.completed/stats.value.total*100)):0)+'%', label:'完成率', color:'#9c27b0' },
])

function goalIcon(t) { return goalTypes.find(gt=>gt.value===t)?.icon || '🎯' }
function goalIconBg(t) {
  const map = { STUDY_MINUTES:'#e6f7ff', RESOURCE_COUNT:'#f0f9eb', CHECK_IN_STREAK:'#fff7e6', POINTS_TARGET:'#fef0f0' }
  return map[t] || '#f5f5f5'
}
function unitLabel(t) { return goalTypes.find(gt=>gt.value===t)?.unit || '' }
function statusTagType(s) { return { ACTIVE:'warning', COMPLETED:'success', FAILED:'danger' }[s] || 'info' }
function statusLabel(s) { return { ACTIVE:'进行中', COMPLETED:'已完成', FAILED:'未完成' }[s] || s }

async function load() {
  loading.value = true
  try {
    const [gl, st] = await Promise.all([goalApi.list(), goalApi.stats()])
    goals.value = (gl.data?.data || []).sort((a,b) => a.status==='COMPLETED'?1:a.status==='ACTIVE'?-1:0)
    stats.value = st.data?.data || {}
  } catch (e) {
    console.error('Failed to load goals:', e)
    ElMessage.error('加载目标列表失败，请重试')
  } finally {
    loading.value = false
  }
}
async function doCreate() {
  if (!form.title) { ElMessage.warning('请输入目标标题'); return }
  creating.value = true
  try {
    await goalApi.create({
      goalType: form.goalType, title: form.title, description: form.description,
      targetValue: form.targetValue,
      deadline: form.deadline ? form.deadline.toISOString().split('T')[0] : null
    })
    ElMessage.success('目标创建成功! 🎯'); showCreate.value = false
    Object.assign(form, { goalType:'RESOURCE_COUNT', title:'', description:'', targetValue:10, deadline:null })
    await load()  // 立即重新加载以验证数据已保存
  } catch (e) {
    console.error('Failed to create goal:', e)
    ElMessage.error(e?.response?.data?.msg || '创建目标失败，请重试')
  } finally { creating.value = false }
}
function openProgress(g) { progressGoal.value = g; newProgress.value = g.currentValue; showProgress.value = true }
async function doUpdateProgress() {
  try {
    await goalApi.progress(progressGoal.value.id, newProgress.value)
    ElMessage.success('进度已更新')
    showProgress.value = false
    await load()
  } catch (e) {
    console.error('Failed to update progress:', e)
    ElMessage.error(e?.response?.data?.msg || '更新进度失败，请重试')
  }
}
async function doComplete(id) {
  try {
    await goalApi.complete(id)
    ElMessage.success('目标完成! 🎉 AI 正在生成点评...')
    await load()
  } catch (e) {
    console.error('Failed to complete goal:', e)
    ElMessage.error(e?.response?.data?.msg || '操作失败，请重试')
  }
}
onMounted(load)
</script>

<style scoped>
.goals-page { max-width:900px; margin:0 auto }
.page-head { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }

.overview-row { margin-bottom:20px }
.ov-card { background:#fff; border-radius:14px; padding:18px; text-align:center; border-top:3px solid #409eff; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
.ov-num { font-size:26px; font-weight:800; line-height:1 }
.ov-label { font-size:12px; color:#909399; margin-top:4px }

.goals-list { position:relative }
.goal-card { display:flex; gap:16px; padding-bottom:20px }
.goal-card.completed { opacity:0.7 }
.gc-left { display:flex; flex-direction:column; align-items:center; width:48px; flex-shrink:0 }
.gc-icon { width:44px; height:44px; border-radius:14px; display:flex; align-items:center; justify-content:center; font-size:20px }
.gc-line { width:2px; flex:1; background:#e8e8e8; margin-top:8px; min-height:20px }
.gc-body { flex:1; background:#fff; border-radius:14px; padding:18px; box-shadow:0 2px 12px rgba(0,0,0,0.04); min-width:0 }
.gc-header { display:flex; align-items:center; justify-content:space-between; gap:8px; flex-wrap:wrap }
.gc-title { font-weight:700; font-size:15px; color:#303133 }
.gc-desc { font-size:13px; color:#909399; margin-top:6px }
.gc-progress { margin-top:12px }
.gc-progress-text { display:flex; justify-content:space-between; font-size:11px; color:#c0c4cc; margin-top:4px }
.gc-deadline { color:#e6a23c }
.gc-ai { display:flex; align-items:flex-start; gap:6px; margin-top:10px; padding:8px 12px; background:#f0f9ff; border-radius:8px; font-size:13px; color:#606266; border:1px solid #d6e4ff }
.gc-actions { display:flex; gap:8px; margin-top:14px }

.empty-card { background:#fff; border-radius:16px; padding:40px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
.goal-dialog :deep(.el-dialog__header) { font-weight:600 }
</style>

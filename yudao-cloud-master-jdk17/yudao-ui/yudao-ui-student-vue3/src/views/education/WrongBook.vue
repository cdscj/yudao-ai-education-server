<template>
  <div class="wb-page" v-loading="loading">
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><EditPen /></el-icon> 错题本</h2>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row" v-if="stats">
      <div class="stat-card"><span class="sc-num">{{ stats.totalCount || 0 }}</span><span class="sc-label">错题总数</span></div>
    </div>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="filterSubjectId" placeholder="选择学科" clearable style="width:160px" @change="load">
        <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
      </el-select>
      <el-select v-model="filterMastery" placeholder="掌握程度" clearable style="width:140px" @change="load">
        <el-option label="未掌握" :value="0" />
        <el-option label="较弱" :value="1" />
        <el-option label="一般" :value="2" />
        <el-option label="较好" :value="3" />
        <el-option label="熟练" :value="4" />
        <el-option label="精通" :value="5" />
      </el-select>
    </div>

    <!-- 错题列表 -->
    <div class="list-card">
      <div v-if="list.length" class="wrong-list">
        <div v-for="item in list" :key="item.id" class="wrong-item" @click="showDetail(item)">
          <div class="wi-left">
            <div class="wi-status" :class="{ correct: item.isCorrect, wrong: !item.isCorrect }">
              {{ item.isCorrect ? '✓' : '✗' }}
            </div>
            <div class="wi-info">
              <div class="wi-question">{{ item.userAnswer || '(空)' }}</div>
              <div class="wi-answer" v-if="!item.isCorrect">正确答案: {{ item.correctAnswer }}</div>
            </div>
          </div>
          <div class="wi-right">
            <el-tag size="small" :type="masteryType(item.masteryLevel)">Lv.{{ item.masteryLevel }}</el-tag>
            <span class="wi-review">复习 {{ item.reviewCount }} 次</span>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无错题记录" :image-size="80" />
      <div class="pager" v-if="total > pageSize">
        <el-pagination layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="pageNo" @change="load" small />
      </div>
    </div>

    <!-- 错题详情弹窗 -->
    <el-dialog v-model="detailVisible" title="错题详情" width="560px" :close-on-click-modal="false">
      <div class="detail-content" v-if="detail">
        <div class="d-section">
          <div class="d-label">你的答案</div>
          <div class="d-value" :class="{ wrong: !detail.isCorrect }">{{ detail.userAnswer || '(空)' }}</div>
        </div>
        <div class="d-section" v-if="!detail.isCorrect">
          <div class="d-label">正确答案</div>
          <div class="d-value correct">{{ detail.correctAnswer }}</div>
        </div>
        <div class="d-section" v-if="detail.errorAnalysis">
          <div class="d-label">AI 分析</div>
          <div class="d-value analysis">{{ detail.errorAnalysis }}</div>
        </div>
        <div class="d-meta">
          <el-tag size="small">掌握度 Lv.{{ detail.masteryLevel }}</el-tag>
          <el-tag size="small" type="info">复习 {{ detail.reviewCount }} 次</el-tag>
          <span v-if="detail.lastReviewTime">上次复习: {{ detail.lastReviewTime }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="doReview" type="primary" :loading="reviewing">标记复习</el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { wrongBookApi, subjectApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const list = ref([])
const stats = ref(null)
const subjects = ref([])
const filterSubjectId = ref(null)
const filterMastery = ref(null)
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const detailVisible = ref(false)
const detail = ref(null)
const reviewing = ref(false)

function masteryType(level) {
  if (level >= 4) return 'success'
  if (level >= 2) return 'warning'
  return 'danger'
}

async function loadSubjects() {
  try {
    const r = await subjectApi.list()
    subjects.value = r.data?.data || []
  } catch (e) { console.error('Failed to load subjects:', e) }
}

async function load() {
  loading.value = true
  try {
    const [r, s] = await Promise.all([
      wrongBookApi.page({ subjectId: filterSubjectId.value, masteryLevel: filterMastery.value, pageNo: pageNo.value, pageSize: pageSize.value }),
      wrongBookApi.stats().catch(() => ({ data: null }))
    ])
    const data = r.data?.data
    list.value = data?.list || []
    total.value = data?.total || 0
    stats.value = s.data?.data || { totalCount: 0 }
  } catch (e) {
    console.error('Failed to load wrong book:', e)
    ElMessage.error('加载错题本失败')
  } finally { loading.value = false }
}

function showDetail(item) {
  detail.value = item
  detailVisible.value = true
}

async function doReview() {
  if (!detail.value) return
  reviewing.value = true
  try {
    await wrongBookApi.review(detail.value.id)
    ElMessage.success('已标记复习')
    detail.value.reviewCount++
    detail.value.masteryLevel = Math.min((detail.value.masteryLevel || 0) + 1, 5)
    detailVisible.value = false
    load()
  } catch (e) {
    console.error('Failed to review:', e)
  } finally { reviewing.value = false }
}

onMounted(async () => {
  await loadSubjects()
  await load()
})
</script>

<style scoped>
.wb-page { max-width:900px; margin:0 auto }
.page-head { display:flex; align-items:center; justify-content:space-between; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }

.stats-row { display:flex; gap:12px; margin-bottom:16px }
.stat-card { display:flex; flex-direction:column; align-items:center; background:#fff; border-radius:12px; padding:16px 24px; box-shadow:0 2px 8px rgba(0,0,0,0.04); min-width:100px }
.sc-num { font-size:28px; font-weight:800; color:#409eff }
.sc-label { font-size:12px; color:#909399; margin-top:2px }

.filter-bar { display:flex; gap:10px; margin-bottom:16px }

.list-card { background:#fff; border-radius:16px; padding:16px 20px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
.wrong-item { display:flex; align-items:center; justify-content:space-between; padding:12px; border-bottom:1px solid #f5f5f5; cursor:pointer; transition:background .2s }
.wrong-item:last-child { border-bottom:none }
.wrong-item:hover { background:#f8f9fb }
.wi-left { display:flex; align-items:center; gap:12px; flex:1; min-width:0 }
.wi-status { width:32px;height:32px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-weight:bold;font-size:16px;flex-shrink:0 }
.wi-status.correct { background:#e6f7e6; color:#67c23a }
.wi-status.wrong { background:#fde2e2; color:#f56c6c }
.wi-info { min-width:0 }
.wi-question { font-weight:500;color:#303133;font-size:14px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap }
.wi-answer { color:#67c23a;font-size:12px;margin-top:2px }
.wi-right { display:flex;align-items:center;gap:8px;flex-shrink:0 }
.wi-review { font-size:12px;color:#909399 }

.pager { display:flex;justify-content:center;padding-top:16px }

.d-section { margin-bottom:14px }
.d-label { font-size:13px;color:#909399;margin-bottom:4px }
.d-value { font-size:15px;color:#303133;padding:10px 14px;background:#f8f9fb;border-radius:8px }
.d-value.wrong { color:#f56c6c; background:#fef0f0 }
.d-value.correct { color:#67c23a; background:#f0f9eb }
.d-value.analysis { color:#409eff; background:#ecf5ff; line-height:1.6 }
.d-meta { display:flex;align-items:center;gap:10px;flex-wrap:wrap;padding-top:8px;border-top:1px solid #f0f0f0;font-size:12px;color:#909399 }
</style>

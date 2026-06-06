<template>
  <div class="school-page" v-loading="loading">
    <!-- 页头 -->
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><School /></el-icon> 学校信息</h2>
    </div>

    <!-- 未绑定学校 - 搜索/绑定表单 -->
    <div v-if="!boundSchool" class="bind-card">
      <div class="bind-icon"><el-icon :size="48"><School /></el-icon></div>
      <div class="bind-title">绑定你的学校</div>
      <div class="bind-desc">绑定学校后可以查看班级同学和学习课程表</div>
      <el-form :model="bindForm" label-width="100px" class="bind-form">
        <el-form-item label="学校" required>
          <el-select
            v-model="bindForm.schoolId"
            filterable
            remote
            :remote-method="searchSchool"
            placeholder="搜索并选择学校"
            style="width:100%"
            :loading="searchLoading"
          >
            <el-option v-for="s in schoolOptions" :key="s.id" :label="s.name" :value="s.id">
              <div style="display:flex;justify-content:space-between">
                <span>{{ s.name }}</span>
                <span style="color:#909399;font-size:12px">{{ s.province }}-{{ s.city }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-row :gutter="12">
          <el-col :span="12">
            <el-form-item label="专业" required>
              <el-input v-model="bindForm.major" placeholder="如：计算机科学与技术" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" required>
              <el-select v-model="bindForm.grade" placeholder="选择年级" style="width:100%">
                <el-option label="大一" value="大一" />
                <el-option label="大二" value="大二" />
                <el-option label="大三" value="大三" />
                <el-option label="大四" value="大四" />
                <el-option label="研一" value="研一" />
                <el-option label="研二" value="研二" />
                <el-option label="研三" value="研三" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="12">
          <el-col :span="8">
            <el-form-item label="班级">
              <el-input v-model="bindForm.className" placeholder="如：计科2101班" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学号">
              <el-input v-model="bindForm.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入学年份">
              <el-date-picker
                v-model="bindForm.enrollmentYear"
                type="year"
                placeholder="选择年份"
                style="width:100%"
                value-format="YYYY"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" size="large" @click="doBind" :loading="binding" style="width:200px">
            <el-icon><Link /></el-icon> 绑定学校
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 已绑定学校 - 学校信息卡片 -->
    <div v-else>
      <div class="school-info-card">
        <div class="sic-badge">
          <el-icon :size="64" color="#fff"><School /></el-icon>
        </div>
        <div class="sic-name">{{ boundSchool.schoolName || boundSchool.school?.name }}</div>
        <div class="sic-location">
          <el-icon><Location /></el-icon>
          {{ boundSchool.schoolProvince || boundSchool.school?.province }} - {{ boundSchool.schoolCity || boundSchool.school?.city }}
        </div>
        <div class="sic-details">
          <div class="sicd-item">
            <span class="sicd-label">专业</span>
            <span class="sicd-value">{{ boundSchool.major }}</span>
          </div>
          <div class="sicd-divider" />
          <div class="sicd-item">
            <span class="sicd-label">年级</span>
            <span class="sicd-value">{{ boundSchool.grade }}</span>
          </div>
          <div class="sicd-divider" />
          <div class="sicd-item">
            <span class="sicd-label">班级</span>
            <span class="sicd-value">{{ boundSchool.className || '-' }}</span>
          </div>
        </div>
        <div class="sic-meta">
          <div class="sicm-row">
            <span class="sicm-label">学号</span>
            <span class="sicm-value">{{ boundSchool.studentNo || '-' }}</span>
          </div>
          <div class="sicm-row">
            <span class="sicm-label">入学年份</span>
            <span class="sicm-value">{{ boundSchool.enrollmentYear || '-' }}</span>
          </div>
          <div class="sicm-row">
            <span class="sicm-label">状态</span>
            <el-tag type="success" effect="dark" round>在读</el-tag>
          </div>
        </div>
      </div>

      <!-- 同学列表 -->
      <div class="classmates-section">
        <div class="section-title"><el-icon :size="20"><UserFilled /></el-icon> 班级同学 ({{ classmateList.length }})</div>
        <div class="classmates-grid" v-if="classmateList.length">
          <div v-for="c in classmateList" :key="c.id" class="classmate-card">
            <div class="cc-avatar">
              <el-avatar :size="56" style="background:linear-gradient(135deg,#36cfc9,#5cdbd3);font-size:22px">
                {{ c.nickname?.charAt(0) || c.studentName?.charAt(0) || '?' }}
              </el-avatar>
            </div>
            <div class="cc-name">{{ c.nickname || c.studentName || '未知' }}</div>
            <div class="cc-major">{{ c.major || c.grade || '' }}</div>
            <div class="cc-grade" v-if="c.grade">
              <el-tag size="small" effect="plain">{{ c.grade }}</el-tag>
            </div>
          </div>
        </div>
        <div v-else class="empty-card">
          <el-empty description="暂无同学信息" :image-size="80" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { schoolApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const boundSchool = ref(null)
const searchLoading = ref(false)
const schoolOptions = ref([])
const binding = ref(false)

const bindForm = ref({
  schoolId: null,
  major: '',
  grade: '',
  className: '',
  studentNo: '',
  enrollmentYear: '',
})

const classmateList = ref([])

async function searchSchool(query) {
  if (!query) return
  searchLoading.value = true
  try {
    const r = await schoolApi.list({ name: query })
    schoolOptions.value = r.data?.data || r.data?.list || []
  } catch (e) {
    console.error('Failed to search school:', e)
    schoolOptions.value = []
  } finally {
    searchLoading.value = false
  }
}

async function doBind() {
  if (!bindForm.value.schoolId) { ElMessage.warning('请选择学校'); return }
  if (!bindForm.value.major) { ElMessage.warning('请输入专业'); return }
  if (!bindForm.value.grade) { ElMessage.warning('请选择年级'); return }
  binding.value = true
  try {
    await schoolApi.bind(bindForm.value)
    ElMessage.success('学校绑定成功！')
    await loadMySchool()
  } catch (e) {
    console.error('Failed to bind school:', e)
    ElMessage.error(e?.response?.data?.msg || '学校绑定失败，请重试')
  } finally {
    binding.value = false
  }
}

async function loadMySchool() {
  loading.value = true
  try {
    const r = await schoolApi.mySchool()
    const data = r.data?.data
    if (data) {
      boundSchool.value = data
      await loadClassmates()
    } else {
      boundSchool.value = null
    }
  } catch (e) {
    console.error('Failed to load my school:', e)
    boundSchool.value = null
  } finally {
    loading.value = false
  }
}

async function loadClassmates() {
  try {
    const r = await schoolApi.classmates()
    classmateList.value = r.data?.data || r.data?.list || []
  } catch (e) {
    console.error('Failed to load classmates:', e)
    classmateList.value = []
  }
}

onMounted(loadMySchool)
</script>

<style scoped>
.school-page { max-width:900px; margin:0 auto }
.page-head { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }

/* 绑定卡片 */
.bind-card {
  background:#fff;
  border-radius:20px;
  padding:40px 36px;
  box-shadow:0 2px 12px rgba(0,0,0,0.04);
  text-align:center;
  border:1px solid #e8f0fe;
}
.bind-icon {
  width:80px; height:80px;
  background:linear-gradient(135deg,#409eff,#66b1ff);
  border-radius:50%;
  display:flex; align-items:center; justify-content:center;
  margin:0 auto 16px;
  box-shadow:0 4px 14px rgba(64,158,255,0.3);
}
.bind-title { font-size:22px; font-weight:700; color:#303133; margin-bottom:6px }
.bind-desc { font-size:14px; color:#909399; margin-bottom:30px }
.bind-form { max-width:620px; margin:0 auto; text-align:left }

/* 学校信息卡片 */
.school-info-card {
  background:linear-gradient(135deg, #1a73e8, #4a90d9);
  border-radius:20px;
  padding:36px 32px 28px;
  color:#fff;
  text-align:center;
  box-shadow:0 6px 20px rgba(26,115,232,0.25);
  position:relative;
  overflow:hidden;
}
.school-info-card::before {
  content:'';
  position:absolute;
  top:-60px; right:-60px;
  width:200px; height:200px;
  background:rgba(255,255,255,0.08);
  border-radius:50%;
}
.school-info-card::after {
  content:'';
  position:absolute;
  bottom:-40px; left:-40px;
  width:160px; height:160px;
  background:rgba(255,255,255,0.06);
  border-radius:50%;
}
.sic-badge {
  width:96px; height:96px;
  background:rgba(255,255,255,0.15);
  backdrop-filter:blur(4px);
  border-radius:50%;
  display:flex; align-items:center; justify-content:center;
  margin:0 auto 18px;
  border:2px solid rgba(255,255,255,0.25);
}
.sic-name { font-size:26px; font-weight:700; margin-bottom:6px }
.sic-location { display:flex; align-items:center; justify-content:center; gap:4px; font-size:14px; opacity:0.85; margin-bottom:24px }
.sic-details {
  display:flex; align-items:center; justify-content:center; gap:20px;
  padding:16px 0;
  border-top:1px solid rgba(255,255,255,0.15);
  border-bottom:1px solid rgba(255,255,255,0.15);
  margin-bottom:16px;
}
.sicd-item { text-align:center }
.sicd-label { display:block; font-size:11px; opacity:0.7; margin-bottom:4px }
.sicd-value { font-size:16px; font-weight:600 }
.sicd-divider { width:1px; height:30px; background:rgba(255,255,255,0.15) }
.sic-meta { max-width:400px; margin:0 auto }
.sicm-row { display:flex; justify-content:space-between; padding:6px 0; font-size:14px }
.sicm-label { opacity:0.8 }
.sicm-value { font-weight:500 }

/* 同学区域 */
.classmates-section {
  margin-top:24px;
  background:#fff;
  border-radius:20px;
  padding:24px;
  box-shadow:0 2px 12px rgba(0,0,0,0.04);
}
.section-title {
  display:flex;
  align-items:center;
  gap:8px;
  font-size:17px;
  font-weight:700;
  color:#303133;
  margin-bottom:18px;
  padding-bottom:12px;
  border-bottom:1px solid #f0f0f0;
}
.classmates-grid {
  display:grid;
  grid-template-columns:repeat(auto-fill,minmax(160px,1fr));
  gap:14px;
}
.classmate-card {
  background:#f8faff;
  border-radius:14px;
  padding:20px 14px 16px;
  text-align:center;
  transition:all .3s;
  border:1px solid #eef4ff;
}
.classmate-card:hover {
  transform:translateY(-3px);
  box-shadow:0 6px 20px rgba(64,158,255,0.1);
  border-color:#409eff;
}
.cc-avatar { margin-bottom:10px }
.cc-name { font-weight:600; font-size:14px; color:#303133 }
.cc-major { font-size:12px; color:#909399; margin-top:3px }
.cc-grade { margin-top:6px }

.empty-card { background:#fff; border-radius:16px; padding:40px; box-shadow:0 2px 12px rgba(0,0,0,0.04) }
</style>

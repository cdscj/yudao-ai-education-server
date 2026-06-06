<template>
  <div class="schedule-page" v-loading="loading">
    <!-- 页头 -->
    <div class="page-head">
      <h2 class="page-title"><el-icon :size="24"><Timer /></el-icon> 课程表</h2>
      <div style="display:flex;gap:10px;align-items:center">
        <el-select v-model="currentSemester" placeholder="选择学期" size="default" style="width:160px" @change="loadWeekly">
          <el-option v-for="s in semesterOptions" :key="s" :label="s" :value="s" />
        </el-select>
      </div>
    </div>

    <!-- 今日课程概览 -->
    <div class="today-card" v-if="todayCourses.length">
      <div class="today-header"><el-icon :size="18"><Sunny /></el-icon> 今日课程 ({{ todayCourses.length }})</div>
      <div class="today-list">
        <div v-for="c in todayCourses" :key="c.id" class="today-item" :style="{ borderLeftColor: courseColor(c) }">
          <div class="ti-time">{{ c.startTime }} - {{ c.endTime }}</div>
          <div class="ti-info">
            <span class="ti-name">{{ c.courseName }}</span>
            <span class="ti-teacher">{{ c.teacher }}</span>
            <span class="ti-room">{{ c.classroom }}</span>
          </div>
          <el-tag size="small" round :color="courseColor(c)" style="color:#fff;border:none">{{ c.courseType || '必修' }}</el-tag>
        </div>
      </div>
    </div>

    <!-- 每周课程网格 -->
    <div class="schedule-card">
      <div class="schedule-grid">
        <!-- 时间轴列 -->
        <div class="sg-corner">时间</div>
        <div v-for="day in dayLabels" :key="day" class="sg-day-header">{{ day }}</div>

        <!-- 每个时间段行 -->
        <template v-for="hour in timeSlots" :key="hour">
          <div class="sg-time">{{ hour }}:00</div>
          <div v-for="dayIdx in 7" :key="dayIdx" class="sg-cell">
            <div
              v-for="course in getCoursesAtSlot(dayIdx, hour)"
              :key="course.id"
              class="course-card"
              :style="{ background: courseColorBg(course), borderLeft: '4px solid ' + courseColor(course) }"
            >
              <div class="cc-name">{{ course.courseName }}</div>
              <div class="cc-meta">{{ course.teacher }}</div>
              <div class="cc-meta">{{ course.classroom }} {{ course.startTime }}-{{ course.endTime }}</div>
            </div>
          </div>
        </template>
      </div>
      <div v-if="!loading && !hasSchedule" class="schedule-empty">
        <el-empty description="管理员尚未导入课表，敬请期待" :image-size="80" />
      </div>
      <div v-else-if="!loading && weeklyCourses.length === 0 && currentSemester" class="schedule-empty">
        <el-empty :description="'暂无 ' + currentSemester + ' 学期的课程安排'" :image-size="80" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { scheduleApi, schoolApi } from '@/api/index.js'

const dayLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const timeSlots = Array.from({ length: 14 }, (_, i) => i + 8) // 8:00 - 21:00

const loading = ref(false)
const currentSemester = ref('')
const semesterOptions = ref(['2024-2025-1', '2024-2025-2', '2025-2026-1', '2025-2026-2'])
const weeklyCourses = ref([])
const todayCourses = ref([])
const hasSchedule = ref(false) // 是否有任何学期数据

function courseColor(course) {
  return course.color || '#409eff'
}

function courseColorBg(course) {
  const c = courseColor(course)
  return c + '15'
}

function getCoursesAtSlot(dayIdx, hour) {
  return weeklyCourses.value.filter(c => {
    if (c.dayOfWeek !== dayIdx) return false
    const sh = parseInt(c.startTime)
    const eh = parseInt(c.endTime)
    return hour >= sh && hour < eh
  })
}

async function loadWeekly() {
  if (!currentSemester.value) return
  try {
    const r = await scheduleApi.weekly(currentSemester.value)
    const raw = r.data?.data
    // 后端返回 Map<Integer, List> 格式 {1: [...], 2: [...]}，需要 flatten 为数组
    if (raw && !Array.isArray(raw)) {
      weeklyCourses.value = []
      for (const courses of Object.values(raw)) {
        if (Array.isArray(courses)) {
          weeklyCourses.value.push(...courses)
        }
      }
    } else {
      weeklyCourses.value = raw || []
    }
    if (weeklyCourses.value.length > 0) {
      hasSchedule.value = true
    }
  } catch (e) {
    console.error('Failed to load weekly schedule:', e)
    weeklyCourses.value = []
  }
}

async function loadToday() {
  try {
    const r = await scheduleApi.today()
    todayCourses.value = r.data?.data || r.data?.list || []
  } catch (e) {
    console.error('Failed to load today schedule:', e)
    todayCourses.value = []
  }
}

onMounted(async () => {
  loading.value = true
  try {
    // 获取学生所在学校信息来推断当前学期
    try {
      const r = await schoolApi.mySchool()
      // mySchool 暂不返回 semester，使用默认学期
      const data = r.data?.data
      if (!currentSemester.value) {
        currentSemester.value = '2024-2025-2'
      }
    } catch (e) {
      if (!currentSemester.value) currentSemester.value = '2024-2025-2'
    }
    await loadWeekly()
    await loadToday()
  } catch (e) {
    console.error('Failed to load schedule page:', e)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.schedule-page { max-width:1200px; margin:0 auto }
.page-head { display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:12px; margin-bottom:20px }
.page-title { display:flex; align-items:center; gap:8px; font-size:22px; font-weight:700; color:#303133 }

/* 今日课程卡片 */
.today-card {
  background:linear-gradient(135deg,#fdf6ec,#fff);
  border-radius:16px;
  padding:18px 22px;
  margin-bottom:18px;
  box-shadow:0 2px 12px rgba(0,0,0,0.04);
  border:1px solid #f5e6d3;
}
.today-header {
  display:flex;
  align-items:center;
  gap:6px;
  font-weight:700;
  font-size:15px;
  color:#e6a23c;
  margin-bottom:12px;
}
.today-list { display:flex; flex-wrap:wrap; gap:10px }
.today-item {
  display:flex;
  align-items:center;
  gap:14px;
  background:#fff;
  border-radius:10px;
  padding:10px 16px;
  border-left:4px solid #409eff;
  box-shadow:0 1px 4px rgba(0,0,0,0.05);
  flex:1;
  min-width:200px;
}
.ti-time { font-weight:600; font-size:13px; color:#303133; white-space:nowrap }
.ti-info { display:flex; flex-direction:column; gap:1px; flex:1 }
.ti-name { font-weight:600; font-size:14px; color:#303133 }
.ti-teacher, .ti-room { font-size:11px; color:#909399 }

/* 课程网格卡片 */
.schedule-card {
  background:#fff;
  border-radius:16px;
  padding:20px;
  box-shadow:0 2px 12px rgba(0,0,0,0.04);
  overflow-x:auto;
}
.schedule-grid {
  display:grid;
  grid-template-columns:60px repeat(7, 1fr);
  gap:2px;
  min-width:700px;
}
.sg-corner, .sg-day-header, .sg-time, .sg-cell {
  min-height:40px;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:12px;
  font-weight:600;
  border-radius:4px;
}
.sg-corner {
  background:#f5f7fa;
  color:#909399;
  position:sticky;
  left:0;
  z-index:2;
}
.sg-day-header {
  background:linear-gradient(135deg,#409eff,#66b1ff);
  color:#fff;
  padding:8px 0;
  font-size:13px;
  position:sticky;
  top:0;
  z-index:1;
}
.sg-time {
  background:#f5f7fa;
  color:#909399;
  font-weight:500;
  font-size:11px;
  position:sticky;
  left:0;
  z-index:2;
}
.sg-cell {
  background:#fafbfc;
  min-height:56px;
  flex-direction:column;
  gap:2px;
  padding:2px;
  align-items:stretch;
}
.sg-cell:hover {
  background:#eef4ff;
}

/* 课程卡片 */
.course-card {
  background:#f0f5ff;
  border-radius:6px;
  padding:4px 6px;
  font-size:11px;
  line-height:1.4;
}
.cc-name { font-weight:700; color:#303133; font-size:12px }
.cc-meta { color:#909399; font-size:10px }

.schedule-empty { padding:30px 0 }
</style>

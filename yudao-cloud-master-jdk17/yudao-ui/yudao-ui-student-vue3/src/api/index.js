import axios from 'axios'
import { getAccessToken, removeTokens } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

const TENANT_ID = import.meta.env.VITE_TENANT_ID || '1'

function authHeaders() {
  const h = { 'tenant-id': TENANT_ID, 'Content-Type': 'application/json' }
  const t = getAccessToken()
  if (t) h['Authorization'] = 'Bearer ' + t
  return h
}

axios.interceptors.request.use(config => {
  config.headers['tenant-id'] = TENANT_ID
  const token = getAccessToken()
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})

function handleUnauthorized() {
  ElMessage.warning('登录已过期，请重新登录')
  removeTokens()
  router.push('/login')
}

axios.interceptors.response.use(
  response => {
    // Debug: log API responses in development
    if (import.meta.env.DEV) {
      console.log('[API]', response.config.url, response.data?.code)
    }
    if (response.data && response.data.code === 401) {
      handleUnauthorized()
      return Promise.reject(new Error('未登录'))
    }
    return response
  },
  error => {
    // Debug: log API errors in development
    if (import.meta.env.DEV) {
      console.error('[API Error]', error.config?.url, error.response?.status, error.response?.data)
    }
    if (error.response && error.response.status === 401) {
      handleUnauthorized()
    }
    return Promise.reject(error)
  }
)

const BASE = '/app-api/ai/education'
const SOCIAL = '/app-api/ai/social'

export const profileApi = {
  get: () => axios.get(BASE + '/profile/get'),
  chatBuild: (message) => fetch(BASE + '/profile/chat-build', { method: 'POST', headers: authHeaders(), body: JSON.stringify({ message }) })
}
export const resourceApi = {
  page: (p) => axios.get(BASE + '/resource/page', { params: p }),
  get: (id) => axios.get(BASE + '/resource/get', { params: { id } }),
  generate: (d) => fetch(BASE + '/resource/generate-stream', { method: 'POST', headers: authHeaders(), body: JSON.stringify(d) })
}
export const pathApi = {
  page: (p) => axios.get(BASE + '/path/page', { params: p }),
  get: (id) => axios.get(BASE + '/path/get', { params: { id } }),
  generate: (d) => fetch(BASE + '/path/generate-stream', { method: 'POST', headers: authHeaders(), body: JSON.stringify(d) })
}
export const tutorApi = {
  sessions: () => axios.get(BASE + '/tutoring/sessions'),
  messages: (id) => axios.get(BASE + '/tutoring/messages', { params: { sessionId: id } }),
  chat: (d) => fetch(BASE + '/tutoring/chat-stream', { method: 'POST', headers: authHeaders(), body: JSON.stringify(d) })
}
export const evalApi = {
  page: (p) => axios.get(BASE + '/evaluation/page', { params: p }),
  generate: () => fetch(BASE + '/evaluation/generate-stream', { method: 'POST', headers: authHeaders() })
}

// ========== AI 社交与学习激励 ==========
export const checkInApi = {
  checkIn: () => axios.post(SOCIAL + '/check-in/check-in'),
  summary: () => axios.get(SOCIAL + '/check-in/summary'),
  records: (p) => axios.get(SOCIAL + '/check-in/records', { params: p }),
  calendar: (yearMonth) => axios.get(SOCIAL + '/check-in/calendar', { params: { yearMonth } }),
}
export const pointsApi = {
  my: () => axios.get(SOCIAL + '/points/my'),
  level: () => axios.get(SOCIAL + '/points/level'),
}
export const leaderboardApi = {
  list: (periodType, topN) => axios.get(SOCIAL + '/leaderboard/list', { params: { periodType, topN } }),
  myRank: (periodType) => axios.get(SOCIAL + '/leaderboard/my-rank', { params: { periodType } }),
}
export const friendApi = {
  list: (p) => axios.get(SOCIAL + '/friend/list', { params: p }),
  pending: () => axios.get(SOCIAL + '/friend/pending'),
  sendRequest: (friendUserId, remark) => axios.post(SOCIAL + '/friend/send-request', { friendUserId, remark }),
  accept: (requestId) => axios.post(SOCIAL + '/friend/accept', null, { params: { requestId } }),
  reject: (requestId) => axios.post(SOCIAL + '/friend/reject', null, { params: { requestId } }),
  remove: (friendUserId) => axios.delete(SOCIAL + '/friend/remove', { params: { friendUserId } }),
}
export const followApi = {
  follow: (followUserId) => axios.post(SOCIAL + '/follow/follow', null, { params: { followUserId } }),
  unfollow: (followUserId) => axios.post(SOCIAL + '/follow/unfollow', null, { params: { followUserId } }),
  following: (p) => axios.get(SOCIAL + '/follow/following', { params: p }),
  followers: (p) => axios.get(SOCIAL + '/follow/followers', { params: p }),
  isFollowing: (targetUserId) => axios.get(SOCIAL + '/follow/is-following', { params: { targetUserId } }),
}
export const goalApi = {
  list: () => axios.get(SOCIAL + '/goal/list'),
  create: (d) => axios.post(SOCIAL + '/goal/create', d),
  progress: (goalId, currentValue) => axios.put(SOCIAL + '/goal/progress', { currentValue }, { params: { goalId } }),
  complete: (goalId) => axios.post(SOCIAL + '/goal/complete', null, { params: { goalId } }),
  stats: () => axios.get(SOCIAL + '/goal/stats'),
}
export const activityApi = {
  feed: (p) => axios.get(SOCIAL + '/activity/feed', { params: p }),
}

// ========== AI 教育模块（学科、题库、错题本） ==========
const EDU = '/app-api/ai/education'
export const subjectApi = {
  list: () => axios.get(EDU + '/subject-category/list'),
  tree: () => axios.get(EDU + '/subject-category/tree'),
}
export const tagApi = {
  listBySubject: (subjectId) => axios.get(EDU + '/knowledge-tag/list-by-subject', { params: { subjectId } }),
}
export const questionApi = {
  page: (p) => axios.get(EDU + '/question-bank/page', { params: p }),
  get: (id) => axios.get(EDU + '/question-bank/get', { params: { id } }),
}
export const wrongBookApi = {
  page: (p) => axios.get(EDU + '/wrong-answer/page', { params: p }),
  get: (id) => axios.get(EDU + '/wrong-answer/get', { params: { id } }),
  stats: () => axios.get(EDU + '/wrong-answer/stats'),
  review: (id) => axios.post(EDU + '/wrong-answer/review', null, { params: { id } }),
  record: (d) => axios.post(EDU + '/wrong-answer/record', d),
}

export const homeworkApi = {
  myPage: (p) => axios.get(EDU + '/homework/my-page', { params: p }),
  myGet: (id) => axios.get(EDU + '/homework/my-get', { params: { id } }),
  mySubmission: (homeworkId) => axios.get(EDU + '/homework/my-submission', { params: { homeworkId } }),
  submit: (d) => axios.post(EDU + '/homework/submit', d),
}
export const examApi = {
  list: (p) => axios.get(EDU + '/exam/list', { params: p }),
  get: (id) => axios.get(EDU + '/exam/get', { params: { id } }),
  start: (examId) => axios.post(EDU + '/exam/start', { examId }),
  submit: (d) => axios.post(EDU + '/exam/submit', d),
  recordGet: (examId) => axios.get(EDU + '/exam/record-get', { params: { examId } }),
  history: (p) => axios.get(EDU + '/exam/history', { params: p }),
}
export const dashboardApi = {
  data: () => axios.get(EDU + '/study-data/dashboard'),
}

export const studyPlanApi = {
  active: () => axios.get(EDU + '/study-plan/active'),
  get: (id) => axios.get(EDU + '/study-plan/get', { params: { id } }),
  create: (d) => axios.post(EDU + '/study-plan/create', d),
  complete: (id) => axios.put(EDU + '/study-plan/complete', null, { params: { id } }),
  history: (p) => axios.get(EDU + '/study-plan/history', { params: p }),
}
const NOTIFY = '/app-api/ai/notification'
export const notificationApi = {
  page: (p) => axios.get(NOTIFY + '/page', { params: p }),
  unreadCount: () => axios.get(NOTIFY + '/unread-count'),
  markRead: (id) => axios.put(NOTIFY + '/mark-read', null, { params: { id } }),
  markAllRead: () => axios.put(NOTIFY + '/mark-all-read'),
}

const SCHOOL = '/app-api/ai/school'
const SCHEDULE = '/app-api/ai/schedule'

export const schoolApi = {
  list: (p) => axios.get(SCHOOL + '/list', { params: p }),
  get: (id) => axios.get(SCHOOL + '/get', { params: { id } }),
  mySchool: () => axios.get(SCHOOL + '/my-school'),
  bind: (d) => axios.post(SCHOOL + '/bind', d),
  classmates: () => axios.get(SCHOOL + '/classmates'),
}
export const scheduleApi = {
  weekly: (semester) => axios.get(SCHEDULE + '/weekly', { params: { semester } }),
  today: () => axios.get(SCHEDULE + '/today'),
  get: (id) => axios.get(SCHEDULE + '/get', { params: { id } }),
}

export const authApi = {
  login: (data) => axios.post('/app-api/member/auth/login', data),
  smsLogin: (data) => axios.post('/app-api/member/auth/sms-login', data),
  sendSmsCode: (data) => axios.post('/app-api/member/auth/send-sms-code', data),
  logout: () => axios.post('/app-api/member/auth/logout'),
  socialAuthRedirect: (type, redirectUri) => axios.get('/app-api/member/auth/social-auth-redirect', { params: { type, redirectUri } }),
  socialLogin: (data) => axios.post('/app-api/member/auth/social-login', data),
  updatePassword: (data) => axios.put('/app-api/member/user/update-password', data),
  resetPassword: (data) => axios.put('/app-api/member/user/reset-password', data),
}

export const memberApi = {
  get: () => axios.get('/app-api/member/user/get'),
  update: (data) => axios.put('/app-api/member/user/update', data),
}

export const uploadApi = {
  file: (file, dir) => { const fd = new FormData(); fd.append('file', file); if (dir) fd.append('directory', dir); return axios.post('/app-api/infra/file/upload', fd) },
}

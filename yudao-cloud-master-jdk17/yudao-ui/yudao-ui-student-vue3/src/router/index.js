import { createRouter, createWebHistory } from 'vue-router'
import { getAccessToken } from '@/utils/auth'

const routes = [
  { path: '/', redirect: '/profile' },
  { path: '/login', name: 'Login', component: () => import('@/views/login/Login.vue') },
  { path: '/profile', name: 'Profile', component: () => import('@/views/Profile.vue') },
  { path: '/resources', name: 'Resources', component: () => import('@/views/Resources.vue') },
  { path: '/path', name: 'LearningPath', component: () => import('@/views/LearningPath.vue') },
  { path: '/tutoring', name: 'Tutoring', component: () => import('@/views/Tutoring.vue') },
  { path: '/evaluation', name: 'Evaluation', component: () => import('@/views/Evaluation.vue') },
  { path: '/checkin', name: 'CheckIn', component: () => import('@/views/CheckIn.vue') },
  { path: '/leaderboard', name: 'Leaderboard', component: () => import('@/views/Leaderboard.vue') },
  { path: '/friends', name: 'Friends', component: () => import('@/views/Friends.vue') },
  { path: '/goals', name: 'Goals', component: () => import('@/views/Goals.vue') },
  { path: '/activity', name: 'Activity', component: () => import('@/views/Activity.vue') },
  { path: '/school', name: 'School', component: () => import('@/views/School.vue') },
  { path: '/schedule', name: 'Schedule', component: () => import('@/views/Schedule.vue') },
  { path: '/wrongbook', name: 'WrongBook', component: () => import('@/views/education/WrongBook.vue') },
  { path: '/homework', name: 'Homework', component: () => import('@/views/education/Homework.vue') },
  { path: '/exam', name: 'Exam', component: () => import('@/views/education/Exam.vue') },
  { path: '/dashboard', name: 'Dashboard', component: () => import('@/views/education/Dashboard.vue') },
  { path: '/study-plan', name: 'StudyPlan', component: () => import('@/views/education/StudyPlan.vue') },
  { path: '/questionbank', name: 'QuestionBank', component: () => import('@/views/education/QuestionBank.vue') },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  const token = getAccessToken()
  if (to.path === '/login' && token) return '/profile'
  if (to.path !== '/login' && !token) return '/login'
})

export default router

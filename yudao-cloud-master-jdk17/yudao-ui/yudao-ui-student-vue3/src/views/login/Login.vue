<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-header">
        <div class="logo">🎓</div>
        <h2>AI 个性化学习平台</h2>
        <p>学生端</p>
      </div>

      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="密码登录" name="pwd">
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="0" size="large" @submit.prevent="handlePwdLogin" @keyup.enter="handlePwdLogin">
            <el-form-item prop="mobile">
              <el-input v-model="pwdForm.mobile" placeholder="手机号" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="pwdForm.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" style="width:100%" size="large" native-type="submit">登 录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="验证码登录" name="sms">
          <el-form ref="smsFormRef" :model="smsForm" :rules="smsRules" label-width="0" size="large" @submit.prevent="handleSmsLogin" @keyup.enter="handleSmsLogin">
            <el-form-item prop="mobile">
              <el-input v-model="smsForm.mobile" placeholder="手机号" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item prop="code">
              <div style="display:flex;width:100%;gap:8px">
                <el-input v-model="smsForm.code" placeholder="短信验证码" :prefix-icon="Key" style="flex:1" />
                <el-button :disabled="smsCountdown > 0" style="width:120px;flex-shrink:0" @click="handleSmsSendCode">
                  {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" style="width:100%" size="large" native-type="submit">登 录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="reg">
          <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-width="0" size="large" @submit.prevent="handleRegister" @keyup.enter="handleRegister">
            <el-form-item prop="mobile">
              <el-input v-model="regForm.mobile" placeholder="手机号" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item prop="code">
              <div style="display:flex;width:100%;gap:8px">
                <el-input v-model="regForm.code" placeholder="短信验证码" :prefix-icon="Key" style="flex:1" />
                <el-button :disabled="regCountdown > 0" style="width:120px;flex-shrink:0" @click="handleRegSendCode">
                  {{ regCountdown > 0 ? `${regCountdown}s` : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="regForm.password" type="password" placeholder="设置密码（4-16位）" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="regForm.confirmPassword" type="password" placeholder="确认密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" style="width:100%" size="large" native-type="submit">注 册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <div class="social-divider"><span>第三方账号登录</span></div>
      <div class="social-btns">
        <div class="social-btn" @click="handleSocialLogin(31)">
          <span class="social-icon wechat">微</span>
          <span>微信</span>
        </div>
        <div class="social-btn" @click="handleSocialLogin(10)">
          <span class="social-icon gitee">G</span>
          <span>Gitee</span>
        </div>
        <div class="social-btn" @click="handleSocialLogin(20)">
          <span class="social-icon dingtalk">钉</span>
          <span>钉钉</span>
        </div>
      </div>

      <div v-if="error" class="error-msg">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Phone, Lock, Key } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'
import { authApi } from '@/api/index'

const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('pwd')
const loading = ref(false)
const error = ref('')

// 密码登录
const pwdForm = reactive({ mobile: '', password: '' })
const pwdRules = {
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const pwdFormRef = ref(null)

// 验证码登录
const smsForm = reactive({ mobile: '', code: '' })
const smsCountdown = ref(0)
let smsTimer = null
const smsRules = {
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}
const smsFormRef = ref(null)

// 注册
const regForm = reactive({ mobile: '', code: '', password: '', confirmPassword: '' })
const regCountdown = ref(0)
let regTimer = null
const regRules = {
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 4, max: 16, message: '密码长度为 4-16 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, cb) => value === regForm.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
  ]
}
const regFormRef = ref(null)

onMounted(() => {
  const params = new URLSearchParams(window.location.search)
  const code = params.get('code')
  const state = params.get('state')
  if (code && state) {
    const type = localStorage.getItem('social_login_type')
    if (type) {
      handleSocialCallback(parseInt(type), code, state)
    }
  }
})

function startCountdown(name) {
  if (name === 'sms') {
    smsCountdown.value = 60
    clearInterval(smsTimer)
    smsTimer = setInterval(() => { smsCountdown.value--; if (smsCountdown.value <= 0) clearInterval(smsTimer) }, 1000)
  } else {
    regCountdown.value = 60
    clearInterval(regTimer)
    regTimer = setInterval(() => { regCountdown.value--; if (regCountdown.value <= 0) clearInterval(regTimer) }, 1000)
  }
}

// ===== 密码登录 =====
async function handlePwdLogin() {
  error.value = ''
  loading.value = true
  try {
    await authStore.login(pwdForm)
    router.push('/profile')
  } catch (e) {
    console.error('[登录失败]', e)
    const status = e.response?.status
    if (status === 503) {
      error.value = '服务不可用，请确认 member-server 已启动'
    } else if (status === 401) {
      error.value = '手机号或密码错误'
    } else {
      error.value = e.response?.data?.msg || e.message || '登录失败'
    }
  } finally {
    loading.value = false
  }
}

// ===== 验证码登录 =====
async function handleSmsSendCode() {
  if (!smsForm.mobile) { error.value = '请输入手机号'; return }
  error.value = ''
  try {
    await authApi.sendSmsCode({ mobile: smsForm.mobile, scene: 1 })
    startCountdown('sms')
  } catch (e) {
    console.error('[发送验证码失败]', e)
    error.value = e.response?.data?.msg || e.message || '验证码发送失败'
  }
}

async function handleSmsLogin() {
  if (!smsForm.mobile || !smsForm.code) { error.value = '请填写手机号和验证码'; return }
  error.value = ''
  loading.value = true
  try {
    await authStore.smsLogin(smsForm)
    router.push('/profile')
  } catch (e) {
    console.error('[验证码登录失败]', e)
    const status = e.response?.status
    if (status === 503) {
      error.value = '服务不可用，请确认 member-server 已启动'
    } else {
      error.value = e.response?.data?.msg || e.message || '登录失败'
    }
  } finally {
    loading.value = false
  }
}

// ===== 注册 =====
async function handleRegSendCode() {
  if (!regForm.mobile) { error.value = '请输入手机号'; return }
  error.value = ''
  try {
    await authApi.sendSmsCode({ mobile: regForm.mobile, scene: 1 })
    startCountdown('reg')
  } catch (e) {
    console.error('[发送验证码失败]', e)
    error.value = e.response?.data?.msg || e.message || '验证码发送失败'
  }
}

async function handleRegister() {
  if (!regForm.mobile || !regForm.code) { error.value = '请填写手机号和验证码'; return }
  if (regForm.password !== regForm.confirmPassword) { error.value = '两次密码不一致'; return }
  error.value = ''
  loading.value = true
  try {
    await authStore.smsLogin({ mobile: regForm.mobile, code: regForm.code })
    router.push('/profile')
  } catch (e) {
    console.error('[注册失败]', e)
    const status = e.response?.status
    if (status === 503) {
      error.value = '服务不可用，请确认 member-server 已启动'
    } else {
      error.value = e.response?.data?.msg || e.message || '注册失败'
    }
  } finally {
    loading.value = false
  }
}

// ===== 第三方登录 =====
function handleSocialLogin(type) {
  const redirectUri = window.location.origin + '/login'
  localStorage.setItem('social_login_type', type)
  loading.value = true
  error.value = ''
  authApi.socialAuthRedirect(type, redirectUri).then(res => {
    if (res.data.code === 0 && res.data.data) {
      window.location.href = res.data.data
    } else {
      error.value = res.data.msg || '获取授权地址失败'
    }
  }).catch(() => {
    error.value = '获取授权地址失败，请检查后端配置'
  }).finally(() => {
    loading.value = false
  })
}

async function handleSocialCallback(type, code, state) {
  loading.value = true
  error.value = ''
  try {
    await authStore.socialLogin({ type, code, state })
    window.history.replaceState({}, '', '/login')
    router.push('/profile')
  } catch (e) {
    error.value = e.message || '第三方登录失败'
    window.history.replaceState({}, '', '/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header .logo {
  font-size: 48px;
  margin-bottom: 8px;
}
.login-header h2 {
  margin: 0 0 4px;
  font-size: 22px;
  color: #303133;
}
.login-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}
.social-divider {
  display: flex;
  align-items: center;
  margin: 24px 0 16px;
  color: #dcdfe6;
  font-size: 13px;
}
.social-divider::before,
.social-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #dcdfe6;
}
.social-divider span {
  padding: 0 16px;
  color: #909399;
}
.social-btns {
  display: flex;
  gap: 12px;
  justify-content: center;
}
.social-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: background 0.2s;
  font-size: 13px;
  color: #606266;
}
.social-btn:hover {
  background: #f5f7fa;
}
.social-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}
.social-icon.wechat { background: #07C160; }
.social-icon.gitee { background: #C71D23; }
.social-icon.dingtalk { background: #0089FF; }
.error-msg {
  color: #f56c6c;
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
}
</style>

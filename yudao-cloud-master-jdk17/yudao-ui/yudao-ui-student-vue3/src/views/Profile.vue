<template>
  <el-row :gutter="20">
    <el-col :span="14">
      <el-card shadow="never" style="height:calc(100vh - 140px);display:flex;flex-direction:column">
        <template #header><span style="font-weight:600"><el-icon><ChatDotSquare /></el-icon> 对话式画像构建</span></template>
        <div class="chat-msgs" ref="chatRef" style="flex:1;overflow-y:auto;padding:10px 0">
          <div v-for="(m,i) in messages" :key="i" :class="['msg', m.role]">
            <div class="av">🤖</div>
            <div class="bubble" :class="m.role" v-html="m.role==='assistant'?render(m.content):m.content"></div>
          </div>
          <div v-if="loading" class="msg assistant"><div class="av">🤖</div><div class="bubble assistant"><el-icon class="is-loading"><Loading /></el-icon></div></div>
        </div>
        <div style="display:flex;padding-top:12px;border-top:1px solid #eee">
          <el-input v-model="input" placeholder="告诉我你的专业、学习目标..." @keyup.enter="send" :disabled="loading" />
          <el-button type="primary" @click="send" :loading="loading" style="margin-left:10px">发送</el-button>
        </div>
      </el-card>
    </el-col>
    <el-col :span="10">
      <el-card shadow="never"><template #header><span style="font-weight:600"><el-icon><DataBoard /></el-icon> 画像概览</span></template>
        <div v-if="profile">
          <div v-for="(v,k) in parsed" :key="k" style="padding:6px 0;border-bottom:1px solid #f0f0f0">
            <div style="font-size:12px;color:#909399">{{ labels[k]||k }}</div>
            <div style="font-size:14px">{{ v||'未设置' }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无画像" />
      </el-card>
    </el-col>
  </el-row>
</template>
<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { profileApi } from '@/api/index.js'
import { marked } from 'marked'
const messages = ref([])
const input = ref(''); const loading = ref(false); const profile = ref(null); const chatRef = ref(null)
const labels = {major:'专业',grade:'年级',learningGoals:'学习目标',knowledgeLevel:'知识水平',learningPreferences:'学习偏好',weakPoints:'薄弱环节',strongPoints:'优势能力'}
const parsed = computed(() => { try { return profile.value ? JSON.parse(profile.value.profileJson||'{}') : {} } catch { return {} } })
const render = (t) => marked.parse(t||'',{breaks:true})

async function loadMessages() {
  try {
    const r = await profileApi.get()
    if (r.data?.data) {
      profile.value = r.data.data
      const history = profile.value.conversationHistory
      if (history) {
        const arr = JSON.parse(history)
        if (arr.length > 0) { messages.value = arr; return }
      }
    }
  } catch (e) { console.error('Failed to load profile:', e) }
  messages.value = [{role:'assistant',content:'你好！我是你的专属学习助手，请告诉我你的专业和目标吧~'}]
}

async function send() {
  if (!input.value.trim()||loading.value) return
  messages.value.push({role:'user',content:input.value})
  const q = input.value; input.value = ''; loading.value = true
  try {
    const r = await profileApi.chatBuild(q)
    const reader = r.body.getReader(); const dec = new TextDecoder(); let buf = ''
    while (true) { const {done,value} = await reader.read(); if (done) break; buf += dec.decode(value,{stream:true}) }
    const lines = buf.split('\n')
    let content = ''
    for (const line of lines) {
      const m = line.match(/^data:\s?(.*)/)
      if (m) try { const d=JSON.parse(m[1]); content += (d.data||'') } catch (e) { console.error('Parse stream error:', e) }
    }
    messages.value.push({role:'assistant',content})
    try { const r = await profileApi.get(); if (r.data?.data) profile.value = r.data.data } catch (e) { console.error('Failed to refresh profile:', e) }
  } catch (e) { console.error('Profile chat send failed:', e); messages.value.push({role:'assistant',content:'AI 服务暂不可用，请确认 ai-server 已启动'}) }
  loading.value = false; nextTick(()=>chatRef.value?.scrollTo(0,chatRef.value.scrollHeight))
}

onMounted(() => loadMessages())
</script>
<style scoped>
.msg { display:flex; margin-bottom:16px; }
.msg .av { width:36px;height:36px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:20px;margin:0 10px;flex-shrink:0; }
.bubble { max-width:75%;padding:10px 14px;border-radius:12px;line-height:1.6;font-size:14px;background:#f0f2f5; }
.bubble.user { background:#409eff;color:#fff;margin-left:auto; }
</style>

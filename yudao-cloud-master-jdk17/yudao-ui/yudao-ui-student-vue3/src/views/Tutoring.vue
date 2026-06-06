<template>
  <el-row :gutter="16" style="height:calc(100vh - 140px)">
    <el-col :span="5" style="height:100%">
      <el-card shadow="never" style="height:100%;display:flex;flex-direction:column">
        <template #header><span style="font-weight:600"><el-icon><ChatLineSquare /></el-icon> 会话</span></template>
        <div style="flex:1;overflow-y:auto">
          <div v-for="s in sessions" :key="s.id" :class="['s-item',{active:cur===s.id}]" @click="switchS(s.id)">{{ s.title||'新会话' }}</div>
          <el-empty v-if="!sessions.length" description="暂无" />
        </div>
        <el-button type="primary" @click="newS" style="width:100%;margin-top:8px">新会话</el-button>
      </el-card>
    </el-col>
    <el-col :span="19" style="height:100%">
      <el-card shadow="never" style="height:100%;display:flex;flex-direction:column">
        <template #header>
          <div style="display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:8px">
            <span style="font-weight:600"><el-icon><ChatDotSquare /></el-icon> AI 智能辅导</span>
            <div style="display:flex;gap:8px">
              <el-select v-model="selectedSubject" placeholder="选择学科" size="small" style="width:130px" clearable @change="onSubjectChange">
                <el-option v-for="s in subjects" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
              <el-select v-model="selectedTag" placeholder="知识点" size="small" style="width:130px" clearable :disabled="!selectedSubject">
                <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </div>
          </div>
        </template>
        <div class="msgs" ref="chatRef" style="flex:1;overflow-y:auto;padding:10px 0">
          <div v-for="(m,i) in msgs" :key="i" :class="['msg', m.role]">
            <div class="av">{{ m.role==='assistant'?'🤖':'👤' }}</div>
            <div class="b" :class="m.role" v-html="m.role==='assistant'?render(m.content):m.content"></div>
          </div>
          <div v-if="loading" class="msg assistant"><div class="av">🤖</div><div class="b assistant"><el-icon class="is-loading"><Loading /></el-icon></div></div>
        </div>
        <div style="display:flex;padding-top:12px;border-top:1px solid #eee">
          <el-input v-model="q" placeholder="输入你的问题..." type="textarea" :rows="2" :disabled="loading" />
          <el-button type="primary" @click="send" :loading="loading" style="margin-left:10px">发送</el-button>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>
<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { tutorApi, subjectApi, tagApi } from '@/api/index.js'
import { marked } from 'marked'
const sessions = ref([]); const cur = ref(0); const msgs = ref([]); const q = ref(''); const loading = ref(false); const chatRef = ref(null)
const subjects = ref([]); const tags = ref([]); const selectedSubject = ref(null); const selectedTag = ref(null)
const render = (t) => marked.parse(t||'',{breaks:true})
async function loadS() { try { const r=await tutorApi.sessions(); sessions.value=r.data?.data||[] } catch (e) { console.error('Failed to load sessions:', e) } }
async function switchS(id) { cur.value=id; if(!id) { msgs.value=[]; return } try { const r=await tutorApi.messages(id); msgs.value=(r.data?.data||[]).map(m=>({role:m.role,content:m.content})); scroll() } catch (e) { console.error('Failed to switch session:', e) } }
function newS() { cur.value=0; msgs.value=[]; q.value='' }
async function send() { if (!q.value.trim()||loading.value) return; const question=q.value; msgs.value.push({role:'user',content:question}); q.value=''; loading.value=true
  try {
    const r = await tutorApi.chat({sessionId:cur.value,question})
    const reader=r.body.getReader(); const dec=new TextDecoder(); let buf='',content=''
    while (true) { const {done,value}=await reader.read(); if(done) break; buf+=dec.decode(value,{stream:true}) }
    buf.split('\n').forEach(l=>{const m=l.match(/^data:\s?(.*)/); if(m) try {const d=JSON.parse(m[1]); content+=d.data||''} catch (e) { console.error('Parse stream error:', e) }})
    msgs.value.push({role:'assistant',content}); await loadS(); if(sessions.value.length) cur.value=sessions.value[0].id
  } catch (e) { console.error('Tutoring send failed:', e); msgs.value.push({role:'assistant',content:'AI 服务暂不可用，请稍后重试'}) }
  loading.value=false; scroll()
}
function scroll() { nextTick(()=>chatRef.value?.scrollTo(0,chatRef.value.scrollHeight)) }
async function loadSubjects() { try { const r=await subjectApi.list(); subjects.value=r.data?.data||[] } catch(e){ console.error('Failed to load subjects:', e) } }
async function onSubjectChange(id) { selectedTag.value=null; if(!id) { tags.value=[]; return } try { const r=await tagApi.listBySubject(id); tags.value=r.data?.data||[] } catch(e){ tags.value=[] } }
onMounted(async ()=>{ await loadSubjects(); loadS() })
</script>
<style scoped>
.s-item { padding:10px;cursor:pointer;border-radius:8px;margin-bottom:4px; }
.s-item:hover { background:#f0f2f5; }
.s-item.active { background:#ecf5ff; color:#409eff; }
.msg { display:flex; margin-bottom:16px; }
.msg .av { width:36px;height:36px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:20px;margin:0 10px;flex-shrink:0; }
.b { max-width:80%;padding:10px 14px;border-radius:12px;line-height:1.6;font-size:14px;background:#f0f2f5; }
.b.user { background:#409eff; color:#fff;margin-left:auto; }
</style>

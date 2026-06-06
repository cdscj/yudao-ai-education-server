<template>
  <el-card shadow="never">
    <template #header><span style="font-weight:600"><el-icon><MapLocation /></el-icon> 个性化学习路径</span></template>
    <el-form :model="form" inline>
      <el-form-item label="学习目标"><el-input v-model="form.goal" placeholder="如：掌握Python" style="width:300px" /></el-form-item>
      <el-form-item label="周期"><el-input-number v-model="form.durationDays" :min="7" :max="365" :step="7" /> 天</el-form-item>
      <el-form-item><el-button type="primary" @click="genPath" :loading="gen"><el-icon><Refresh /></el-icon> 规划路径</el-button></el-form-item>
    </el-form>
    <div v-if="stream" v-html="render(stream)" style="padding:10px 0;line-height:1.8" class="md"></div>
  </el-card>
  <el-card shadow="never" style="margin-top:16px">
    <template #header><span style="font-weight:600"><el-icon><List /></el-icon> 已有路径</span></template>
    <el-table :data="list" stripe>
      <el-table-column prop="goal" label="学习目标" min-width="300" show-overflow-tooltip />
      <el-table-column prop="totalNodes" label="阶段数" width="80" />
      <el-table-column prop="status" label="状态" width="100"><template #default="{row}"><el-tag :type="row.status==='COMPLETED'?'success':'info'" size="small">{{row.status}}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="80"><template #default="{row}"><el-button type="primary" link @click="view(row)">查看</el-button></template></el-table-column>
    </el-table>
  </el-card>
  <el-dialog v-model="dlg" title="路径详情" width="70%" top="5vh"><div v-if="viewing" v-html="render(viewing)" class="md"></div></el-dialog>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { pathApi } from '@/api/index.js'
import { marked } from 'marked'
const form = reactive({goal:'',courseName:'',durationDays:30})
const gen = ref(false); const stream = ref(''); const list = ref([])
const dlg = ref(false); const viewing = ref('')
const render = (t) => marked.parse(t||'',{breaks:true})
async function genPath() {
  gen.value = true; stream.value = ''
  try {
    const r = await pathApi.generate(form)
    const reader = r.body.getReader(); const dec = new TextDecoder(); let buf=''
    while (true) { const {done,value}=await reader.read(); if(done) break; buf+=dec.decode(value,{stream:true}) }
    buf.split('\n').forEach(l=>{const m=l.match(/^data:\s?(.*)/); if(m) try {const d=JSON.parse(m[1]); const v=d.data||''; stream.value+=v==='null'?'':v} catch (e) { console.error('Parse stream error:', e) }})
  } catch (e) { console.error('Generate path failed:', e); ElMessage.error('生成学习路径失败，请重试') }
  gen.value = false; load()
}
async function load() { try { const r=await pathApi.page({pageNo:1,pageSize:20}); list.value=r.data?.data?.list||[] } catch (e) { console.error('Failed to load paths:', e); ElMessage.error('加载学习路径失败') } }
function formatPathContent(text) {
  if (!text) return ''
  // Try parse as JSON first
  try {
    const obj = JSON.parse(text)
    // If it has a title field, format as structured content
    if (obj.title) {
      let html = `<h2>${obj.title}</h2>`
      if (obj.description) html += `<p>${obj.description}</p>`
      if (obj.nodes || obj.phases) {
        const nodes = obj.nodes || obj.phases || []
        html += '<div style="margin-top:12px">'
        nodes.forEach((n, i) => {
          html += `<div style="background:#f5f7fa;padding:12px;border-radius:8px;margin-bottom:8px;border-left:3px solid #409eff">`
          html += `<strong>阶段 ${i+1}: ${n.title||'阶段'+(i+1)}</strong>`
          if (n.description) html += `<p style="color:#606266;margin:4px 0">${n.description}</p>`
          if (n.duration) html += `<span style="font-size:12px;color:#909399">⏱ ${n.duration}</span>`
          if (n.topics && n.topics.length) html += `<div style="margin-top:4px;font-size:13px;color:#606266">📚 ${n.topics.join(' · ')}</div>`
          html += '</div>'
        })
        html += '</div>'
      }
      return html
    }
    return `<pre style="background:#f5f7fa;padding:16px;border-radius:8px;font-size:13px">${JSON.stringify(obj, null, 2)}</pre>`
  } catch {
    // Not JSON, render as markdown
    return marked.parse(text, {breaks:true})
  }
}
async function view(row) { try { const r=await pathApi.get(row.id); const text = r.data?.data?.content || r.data?.data?.description || ''; viewing.value=formatPathContent(text); dlg.value=true } catch (e) { console.error('Failed to view path:', e); ElMessage.error('查看路径详情失败') } }
onMounted(load)
</script>
<style scoped>.md :deep(pre){background:#1e1e1e;color:#d4d4d4;padding:16px;border-radius:8px;overflow-x:auto}</style>

<template>
  <el-card shadow="never">
    <template #header><span style="font-weight:600"><el-icon><MagicStick /></el-icon> AI 多模态资源生成</span></template>
    <el-form :model="form" inline>
      <el-form-item label="类型"><el-select v-model="form.resourceType" style="width:140px"><el-option label="课程文档" value="DOCUMENT" /><el-option label="思维导图" value="MIND_MAP" /><el-option label="练习题" value="EXERCISE" /><el-option label="拓展阅读" value="READING" /><el-option label="代码案例" value="CODE_EXAMPLE" /></el-select></el-form-item>
      <el-form-item label="主题"><el-input v-model="form.topic" placeholder="如：机器学习" style="width:220px" /></el-form-item>
      <el-form-item><el-button type="primary" @click="generate" :loading="gen"><el-icon><Refresh /></el-icon> 生成</el-button></el-form-item>
    </el-form>
    <div v-if="stream" v-html="render(stream)" style="padding:10px 0;line-height:1.8" class="md"></div>
  </el-card>
  <el-card shadow="never" style="margin-top:16px">
    <template #header><span style="font-weight:600"><el-icon><List /></el-icon> 我的资源</span></template>
    <el-table :data="list" stripe v-loading="loading">
      <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
      <el-table-column prop="resourceType" label="类型" width="100"><template #default="{row}"><el-tag size="small">{{row.resourceType}}</el-tag></template></el-table-column>
      <el-table-column prop="createTime" label="时间" width="160" />
      <el-table-column label="操作" width="80"><template #default="{row}"><el-button type="primary" link @click="view(row)">查看</el-button></template></el-table-column>
    </el-table>
  </el-card>
  <el-dialog v-model="dlg" title="资源详情" width="70%" top="5vh"><div v-if="viewing" v-html="render(viewing)" class="md"></div></el-dialog>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { resourceApi } from '@/api/index.js'
import { marked } from 'marked'
const form = reactive({resourceType:'DOCUMENT',topic:'',difficulty:'',requirements:''})
const gen = ref(false); const stream = ref(''); const list = ref([]); const loading = ref(false)
const dlg = ref(false); const viewing = ref('')
const render = (t) => marked.parse(t||'',{breaks:true})
async function generate() {
  if (!form.topic) return; gen.value = true; stream.value = ''
  try {
    const r = await resourceApi.generate(form)
    const reader = r.body.getReader(); const dec = new TextDecoder(); let buf=''
    while (true) { const {done,value}=await reader.read(); if(done) break; buf+=dec.decode(value,{stream:true}) }
    buf.split('\n').forEach(l=>{const m=l.match(/^data:\s?(.*)/); if(m) try {const d=JSON.parse(m[1]); const v=d.data||''; stream.value+=v==='null'?'':v} catch (e) { console.error('Parse stream error:', e) }})
  } catch (e) { console.error('Generate resource failed:', e); ElMessage.error('生成资源失败，请重试') }
  gen.value = false; load()
}
async function load() { loading.value = true; try { const r=await resourceApi.page({pageNo:1,pageSize:20}); list.value=r.data?.data?.list||[] } catch (e) { console.error('Failed to load resources:', e); ElMessage.error('加载资源列表失败') }; loading.value=false }
async function view(row) { try { const r=await resourceApi.get(row.id); viewing.value=r.data?.data?.content; dlg.value=true } catch (e) { console.error('Failed to view resource:', e); ElMessage.error('查看资源详情失败') } }
onMounted(load)
</script>
<style scoped>
.md :deep(code) { background:#f0f2f5; padding:2px 6px; border-radius:4px; }
.md :deep(pre) { background:#1e1e1e; color:#d4d4d4; padding:16px; border-radius:8px; overflow-x:auto; }
</style>

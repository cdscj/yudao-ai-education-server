<template>
  <el-card shadow="never">
    <template #header><span style="font-weight:600"><el-icon><DataAnalysis /></el-icon> 学习效果评估</span></template>
    <el-button type="primary" @click="gen" :loading="genning" size="large"><el-icon><Refresh /></el-icon> 生成评估报告</el-button>
    <div v-if="stream" style="margin-top:16px">
      <el-alert title="评估完成" type="success" show-icon :closable="false" style="margin-bottom:12px" />
      <div v-html="render(stream)" class="md"></div>
    </div>
  </el-card>
  <el-card shadow="never" style="margin-top:16px">
    <template #header><span style="font-weight:600"><el-icon><List /></el-icon> 历史评估</span></template>
    <el-table :data="list" stripe>
      <el-table-column prop="evaluationType" label="类型" width="120" />
      <el-table-column prop="evaluation" label="摘要" min-width="400" show-overflow-tooltip />
      <el-table-column prop="createTime" label="时间" width="160" />
    </el-table>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { evalApi } from '@/api/index.js'
import { marked } from 'marked'
const genning = ref(false); const stream = ref(''); const list = ref([])
const render = (t) => marked.parse(t||'',{breaks:true})
async function gen() {
  genning.value=true; stream.value=''
  try {
    const r=await evalApi.generate(); const reader=r.body.getReader(); const dec=new TextDecoder(); let buf=''
    while (true) { const {done,value}=await reader.read(); if(done) break; buf+=dec.decode(value,{stream:true}) }
    buf.split('\n').forEach(l=>{const m=l.match(/^data:\s?(.*)/); if(m) try {const d=JSON.parse(m[1]); stream.value+=d.data||''} catch (e) { console.error('Parse stream error:', e) }})
  } catch (e) { console.error('Generate evaluation failed:', e); ElMessage.error('生成评估报告失败，请重试') }
  genning.value=false; load()
}
async function load() { try { const r=await evalApi.page({pageNo:1,pageSize:20}); list.value=r.data?.list||[] } catch (e) { console.error('Failed to load evaluations:', e); ElMessage.error('加载评估记录失败') } }
onMounted(load)
</script>
<style scoped>.md :deep(pre){background:#1e1e1e;color:#d4d4d4;padding:16px;border-radius:8px;overflow-x:auto}</style>

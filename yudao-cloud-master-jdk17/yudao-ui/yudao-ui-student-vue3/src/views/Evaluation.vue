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
      <el-table-column prop="dimension" label="维度" width="110" />
      <el-table-column label="得分" width="80">
        <template #default="{row}">
          <span v-if="row.score>0" :style="{color:row.score>=80?'#67c23a':row.score>=60?'#e6a23c':'#f56c6c'}">{{row.score}}<span v-if="row.maxScore">/{{row.maxScore}}</span></span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="evaluation" label="评估内容" min-width="300" show-overflow-tooltip />
      <el-table-column label="时间" width="170">
        <template #default="{row}">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
    </el-table>
    <el-empty v-if="list.length===0" description="暂无评估记录，请点击上方按钮生成" />
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { evalApi } from '@/api/index.js'
import { marked } from 'marked'
const genning = ref(false); const stream = ref(''); const list = ref([])
const render = (t) => marked.parse(t||'',{breaks:true})
function formatTime(t){ if(!t)return''; const d=new Date(t); return d.getFullYear()+'年'+(d.getMonth()+1)+'月'+d.getDate()+'日' }
async function gen() {
  genning.value=true; stream.value=''
  try {
    const r=await evalApi.generate(); const reader=r.body.getReader(); const dec=new TextDecoder(); let buf=''
    while (true) { const {done,value}=await reader.read(); if(done) break; buf+=dec.decode(value,{stream:true}) }
    buf.split('\n').forEach(l=>{const m=l.match(/^data:\s?(.*)/); if(m) try {const d=JSON.parse(m[1]); stream.value+=d.data||''} catch (e) { console.error('Parse stream error:', e) }})
  } catch (e) { console.error('Generate evaluation failed:', e); ElMessage.error('生成评估报告失败，请重试') }
  genning.value=false; load()
}
async function load() { try { const r=await evalApi.page({pageNo:1,pageSize:20}); list.value=r.data?.data?.list||[] } catch (e) { console.error('Failed to load evaluations:', e); ElMessage.error('加载评估记录失败') } }
onMounted(load)
</script>
<style scoped>.md :deep(pre){background:#1e1e1e;color:#d4d4d4;padding:16px;border-radius:8px;overflow-x:auto}</style>

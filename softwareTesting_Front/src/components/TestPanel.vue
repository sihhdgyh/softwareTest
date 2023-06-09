<template>
  <div class="flex-wrapper">
    <div class="left-flex-item">
      <n-tabs v-model:value="currTab">
        <n-tab-pane name="Question" tab="问题描述">
          <n-card class="description" size="small" header-style="padding: .8em;">
            <template #header>
              <slot name="header">Question 00. 问题名</slot>
            </template>
            <p class="subtitle">
              <slot name="sub-title">问题描述 / 算法思想</slot>
            </p>
            <p class="text-body">
              <slot name="detail">
                先这样，再那样
              </slot>
            </p>
            <p class="subtitle">代码实现</p>
            <n-card class="code-block" embedded :bordered="false" content-style="padding: .5em;">
              <n-scrollbar style="max-height: 42vh;">
                <n-code :hljs="hljs" :code="code" language="js"></n-code>
              </n-scrollbar>
            </n-card>
          </n-card>
        </n-tab-pane>
        <n-tab-pane v-if="result.length" name="Result" tab="测试结果">
          <n-card class="output" size="small" header-style="padding: .8em;">
            <template #header>
              Testing Result
            </template>
            <template #header-extra>
              <n-button v-if="result.length" quaternary size="small" round type="success" @click="exportCsv">
                导出
              </n-button>
            </template>
            <!-- 打印测试结果 -->
            <n-data-table size="small" :max-height="420" :bordered="false" :columns="resultColumns" :data="result"
              :pagination="pagination">
            </n-data-table>
          </n-card>
        </n-tab-pane>
        <n-tab-pane v-if="result.length" name="Visualization" tab="可视化分析">
          <n-card class="visualization" size="small" header-style="padding: .8em;">
            <n-scrollbar style="max-height: 78vh;">
              <n-h2>
                测试情况
              </n-h2>
              <div id="chart" class="chart" />
              <n-h2>
                版本迭代
              </n-h2>
              <n-card class="description" embedded :bordered="false" content-style="padding: .5em;">
                <div id="iterationChart" class="chart" />
              </n-card>
              <n-data-table size="small" :bordered="false" :columns="iterationColumns" :data="iterationTable">
              </n-data-table>
            </n-scrollbar>
          </n-card>
        </n-tab-pane>
      </n-tabs>
    </div>
    <div class="right-flex-item">
      <n-card class="upload-box" size="small" header-style="padding: .8em; margin-bottom: .8em;">
        <template #header>
          Program Test
        </template>
        <p class="subtitle">Step 01. 选择程序版本</p>
        <n-space vertical>
          <n-select class="cascader-input" v-model:value="version" :options="props.versions"
            placeholder="Click to select" @update:value="handleVersionSelect" />
        </n-space>
        <p class="subtitle">Step 02. 选择用例集</p>
        <n-space vertical>
          <n-cascader class="cascader-input" v-model:value="usecaseType" :options="options" :show-path="false"
            :check-strategy="'child'" placeholder="Click to select" />
        </n-space>
        <p class="subtitle">Step 03. 上传用例集 (Optional)</p>
        <n-space justify="center">
          <n-upload ref="uploadRef" action="#" :default-upload="false" accept=".csv" @change="handleUpload">
            <n-upload-dragger class="upload-content">
              <div style="margin-bottom: 12px">
                <n-icon size="2.5em" :depth="3">
                  <cloud-download-outline />
                </n-icon>
              </div>
              <n-text>
                点击或者拖动文件到该区域来上传
              </n-text>
              <n-p depth="3" style="margin-top: .5em;">
                请上传 .csv 格式的文件<br />(若上面已经选择了测试集，请直接跳转到第三步)
              </n-p>
            </n-upload-dragger>
          </n-upload>
        </n-space>
        <p class="subtitle">Step 04. 运行测试集</p>
        <n-space justify="center">
          <n-button class="upload-btn" :disabled="!((fileListLength || usecaseType) && version)" @click="handleTesting"
            strong type="primary">
            开始测试
          </n-button>
        </n-space>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { NH2, NTabs, NTabPane, NCard, NCode, NScrollbar, NSpace, NCascader, NUpload, NUploadDragger, NIcon, NText, NP, NButton, NDataTable, useMessage, NSelect } from 'naive-ui'
import type { CascaderOption } from 'naive-ui'
import { type Component, onUpdated, ref ,defineEmits} from 'vue'
import { CloudDownloadOutline } from '@vicons/ionicons5'
import hljs from 'highlight.js/lib/core'
import javascript from 'highlight.js/lib/languages/javascript'
import Papa from 'papaparse'
import type { Row, Column, ECOption, Iteration } from '../interface'
import * as echarts from 'echarts/core'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LegendComponent,
  ToolboxComponent
} from 'echarts/components'
import { LabelLayout, UniversalTransition } from 'echarts/features'
import { CanvasRenderer } from 'echarts/renderers'

const emit = defineEmits(['triangleJudge','calendarProblem'])
const props = defineProps<{
  context: string,  // 测试上下文
  options: any[],  // 支持的测试用例类型
  code: string, // 代码字符串
  iteration: Iteration,
  versions: any[], // 可选版本集
  ecOption: ECOption // ECharts 选项
}>()

echarts.use([
  LineChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LabelLayout,
  UniversalTransition,
  CanvasRenderer,
  LegendComponent,
  PieChart,
  ToolboxComponent,
  BarChart
])

//测试结果饼状图
const ecOption: ECOption = {
  tooltip: {
    trigger: 'item'
  },
  legend: {
    top: '5%',
    left: 'center'
  },
  series: [
    {
      name: 'Access From',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '40',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: [
        { value: 60, name: 'No Output' },
        { value: 80, name: 'Passed' },
        { value: 32, name: 'Not Passed' }
      ]
    }
  ]
}

onUpdated(() => {
  if (currTab.value === 'Visualization') {
    // 加载测试结果饼状图
    const eChart = echarts.init(document.getElementById('chart') as HTMLDivElement)
    eChart.setOption(ecOption)

    // 加载版本迭代柱状图
    const iterationChart = echarts.init(document.getElementById('iterationChart') as HTMLDivElement)
    iterationChart.setOption(props.ecOption)

    // 绘制代码版本迭代表
    //console.log('iteration', props.iteration)
    iterationColumns.value.length = 0
    iterationTable.value.length = 0
    iterationColumns.value.push(...props.iteration.columns)
    iterationTable.value.push(...props.iteration.data)
    console.log('iteration table', iterationTable)
  }
})

let composable: Function, getArgs: (row: Row) => any[]
const composables = import.meta.glob('../composables/*.ts')

//const code = props.code,
const code = ref(``),
  options = ref<CascaderOption[]>(props.options)

const version = ref(null)

const usecaseType = ref(null)

function handleVersionSelect(value: string) {
  //alert(value)
  
  if(props.context=='triangleJudge'){
    emit('triangleJudge',{triangleJudge:value})
    if(value=='0.0.0'){
      code.value=`function triangleJudge(a: number, b: number, c: number): string {
    if (a <= 0 || b <= 0 || c <= 0 || a > 200 || b > 200 || c > 200) {
        return '边长数值越界'
    }
    if (
        a + b > c &&
        a + c > b &&
        b + c > a
    ) {
        if (a === b || a === c || b === c) {
            return '该三角形是等腰三角形'
        } else if (a === b && a === c) {
            return '该三角形是等边三角形'
        } else {
            return '该三角形是普通三角形'
        }
    } else {
        return '所给三边数据不能构成三角形'
    }
}`
    }else if(value=='0.1.0'){
      code.value=`function triangleJudge(a: number, b: number, c: number): string {
    if (a <= 0 || b <= 0 || c <= 0 ||  a > 200 || b > 200 || c > 200){
        return '边长数值越界'
    }
    if (
        a + b > c &&
        a + c > b &&
        b + c > a
    ) {
        if (a === b && a === c){
            return '该三角形是等边三角形'
        }else if (a === b || a === c || b === c){
            return '该三角形是等腰三角形'
        }else {
            return '该三角形是普通三角形'
        }
    } else {
        return '所给三边数据不能构成三角形'
    }
}`
    }else{
      code.value=``
    }
  }else if(props.context=='calendarProblem'){
    emit('calendarProblem',{calendarProblem:value})
    if(value=='0.0.0'){
      code.value=`function calendarProblem(year: number, month: number, day: number): string {
    if (year < 1900 || year > 2100) {
        return "年份数值越界"
    }
    if (month <= 0 || month > 12) {
        return "月份数值越界"
    }

    let monthDays: number[] = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    let isLeap: number = 0
    if (year % 4 == 0) {
        isLeap = 1
    }
    if (year % 100 == 0 && year % 400 == 0) {
        isLeap = 1;
    }

    monthDays[1] += isLeap
    let maxDays: number = monthDays[month - 1]
    if (day <= 0 || day > maxDays) {
        return "日期数值越界"
    }

    let result: number[] = [year, month, day + 1]

    if (day == maxDays)
        result[2] = 1
    result[1]++
    if (result[1] > 12)
        result[1] = 1
    result[0]++
    return result[0] + "/" + result[1] + "/" + result[2]
}`
    }else if(value=='0.1.0'){
      code.value=`function calendarProblem(year: number, month: number, day: number): string {
    if (year < 1900 || year > 2100) {
        return "年份数值越界"
    }
    if (month <= 0 || month > 12) {
        return "月份数值越界"
    }

    let monthDays: number[] = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    let isLeap: number = 0
    if (year % 4 == 0) {
        isLeap = 1
    }
    if (year % 100 == 0 && year % 400 == 0) {
        isLeap = 1;
    }

    monthDays[1] += isLeap
    let maxDays: number = monthDays[month - 1]
    if (day <= 0 || day > maxDays) {
        return "日期数值越界"
    }

    let result: number[] = [year, month, day + 1]

    if (day == maxDays) {
        result[2] = 1
        result[1]++
    }
    if (result[1] > 12) {
        result[1] = 1
        result[0]++
    }
    return result[0] + "/" + result[1] + "/" + result[2]
}`
    }else if(value=='0.2.0'){
      code.value=`function calendarProblem(year: number, month: number, day: number): string {
    if (year < 1900 || year > 2100) {
        return "年份数值越界"
    }
    if (month <= 0 || month > 12) {
        return "月份数值越界"
    }

    let monthDays: number[] = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]
    let isLeap: number = 0
    if (year % 400 == 0) {
        isLeap = 1
    } else if (year % 100 != 0 && year % 4 == 0) {
        isLeap = 1;
    }

    monthDays[1] += isLeap
    let maxDays: number = monthDays[month - 1]
    if (day <= 0 || day > maxDays) {
        return "日期数值越界"
    }

    let result: number[] = [year, month, day + 1]

    if (day == maxDays) {
        result[2] = 1
        result[1]++
    }
    if (result[1] > 12) {
        result[1] = 1
        result[0]++
    }
    return result[0] + "/" + result[1] + "/" + result[2]
}`
    }else{
      code.value=``
    }
  }else if(props.context=='telecomSystem'){
    if(value=='0.0.0'){
      code.value=`function telecomSystem(callingTime: number, count: number): string {
    if (callingTime < 0 || callingTime > 31 * 24 * 60) {
        return "通话时长数值越界"
    }
    if (count < 0 || count > 11) {
        return "未按时缴费次数越界"
    }

    let maxNum: number[] = [1, 2, 3, 3, 6]
    let level: number = getLevel(callingTime)
    if (count <= maxNum[level]) {
        return String((25 + 0.15 * callingTime * (1 - (level + 1) * 0.005)))
    } else {
        return String((25 + 0.15 * callingTime))
    }
}

//获取折扣档位
function getLevel(time: number): number {
    if (time > 0 && time <= 60)
        return 1
    else if (time > 60 && time <= 120)
        return 2
    else if (time > 120 && time <= 180)
        return 3
    else if (time > 180 && time <= 300)
        return 4
    else
        return 5
}`
    }else if(value=='0.1.0'){
      code.value=`function telecomSystem(callingTime: number, count: number): string {
    if (callingTime < 0 || callingTime > 31 * 24 * 60) {
        return "通话时长数值越界"
    }
    if (count < 0 || count > 11) {
        return "未按时缴费次数越界"
    }

    let maxNum: number[] = [1, 2, 3, 3, 6]
    let level: number = getLevel(callingTime)
    if (count <= maxNum[level - 1]) {
        return String((25 + 0.15 * callingTime * (1 - (level + 1) * 0.005)))
    } else {
        return String((25 + 0.15 * callingTime))
    }
}

//获取折扣档位
function getLevel(time: number): number {
    if (time > 0 && time <= 60)
        return 1
    else if (time > 60 && time <= 120)
        return 2
    else if (time > 120 && time <= 180)
        return 3
    else if (time > 180 && time <= 300)
        return 4
    else
        return 5
}
`
    }else if(value=='0.2.0'){
      code.value=`function telecomSystem(callingTime: number, count: number): string {
    if (callingTime < 0 || callingTime > 31 * 24 * 60) {
        return "通话时长数值越界"
    }
    if (count < 0 || count > 11) {
        return "未按时缴费次数越界"
    }

    let maxNum: number[] = [1, 2, 3, 3, 6]
    let level: number = getLevel(callingTime)
    if (count <= maxNum[level - 1]) {
        return String(Math.round((25 + 0.15 * callingTime * (1 - (level + 1) * 0.005)) * 100) / 100)
    } else {
        return String(Math.round((25 + 0.15 * callingTime) * 100) / 100)
    }
}

//获取折扣档位
function getLevel(time: number): number {
    if (time > 0 && time <= 60)
        return 1
    else if (time > 60 && time <= 120)
        return 2
    else if (time > 120 && time <= 180)
        return 3
    else if (time > 180 && time <= 300)
        return 4
    else
        return 5
}`
    }else{
      code.value=``
    }
  }else if(props.context=='computerSelling'){
    if(value=='0.0.0'){
      code.value=`function computerSelling(host: number, monitor: number, peripheral: number): string {
    if (host <= 0 || monitor <= 0 || peripheral <= 0) {
        return "数据非法，各部件销售数量不能小于1"
    }
    if (host > 70) {
        return "数据非法，主机销售数量不能超过70"
    }
    if (monitor > 80) {
        return "数据非法，显示器销售数量不能超过80"
    }
    if (peripheral > 90) {
        return "数据非法，外设销售数量不能超过90"
    }

    let totalSales: number = host * 25 + monitor * 30 + peripheral * 45;
    if (totalSales <= 1000) {
        return String(totalSales * 0.1)
    } else if (totalSales <= 1800) {
        return String(totalSales * 0.15)
    } else {
        return String(totalSales * 0.2)
    }
}`
    }else if(value=='0.1.0'){
      code.value=`function computerSelling(host: number, monitor: number, peripheral: number): string {
    if (host == -1) {
        return "系统开始统计月度销售额"
    }
    if (host <= 0 || monitor <= 0 || peripheral <= 0) {
        return "数据非法，各部件销售数量不能小于1"
    }
    if (host > 70) {
        return "数据非法，主机销售数量不能超过70"
    }
    if (monitor > 80) {
        return "数据非法，显示器销售数量不能超过80"
    }
    if (peripheral > 90) {
        return "数据非法，外设销售数量不能超过90"
    }

    let totalSales: number = host * 25 + monitor * 30 + peripheral * 45;
    if (totalSales <= 1000) {
        return String(totalSales * 0.1)
    } else if (totalSales <= 1800) {
        return String(totalSales * 0.15)
    } else {
        return String(totalSales * 0.2)
    }
}
`
    }else{
      code.value=``
    }
  }else if(props.context=='commissionCaculate'){
    if(value=='0.0.0'){
      code.value=`function commissionCaculate(annualSales: number, leaveDays: number, cashArrival: number): number {
   if(annualSales>200&&leaveDays<=10&&leaveDays>=0&&cashArrival<=1&&cashArrival>=0){
        if(cashArrival>=0.6)
            return annualSales/7;
        else {
            return 0;
        }
   }else if(annualSales>=0&&leaveDays>=0&&cashArrival<=1&&cashArrival>=0){
    if(cashArrival<=0.85){
        return annualSales/6;
    }else{
        return annualSales/5;
    }
   }
   return 0;
}`
    }else{
      code.value=``
    }
  }
  //code.value=codes.triangleJudge[value]
  for (let index in composables) {
    if (index === `../composables/${props.context}v${value}.ts`) {
      composables[index]().then(({ useSingleTest, useGetArgs }) => {
        [composable, getArgs] = [useSingleTest, useGetArgs]
        //console.log(composable, getArgs)
      })
      break
    }
  }
}

// 传入.csv对应的数组，创建表头
const createColumns = (rawData: any[]) => {
  let cols = []
  for (let item of rawData[0]) {
    cols.push({
      title: item,
      key: item,
    })
  }
  return cols
}

const createRows = (rawData: any[]) => {
  let data = []
  let counter = 0
  const rowNum = rawData.length
  for (let i = 1; i < rowNum; ++i) {
    let row: Row = { key: (counter++).toString() }
    let j = 0
    for (let item of resultColumns.value) {
      row[item.key] = rawData[i][j++]
    }
    //console.log(row)
    data.push(row)
  }
  return data
}

// 测试函数
const executeTesting = (dataContent: Row[]) => {
  let falseNum = 0
  let nullAnsNum = 0
  for (let row of dataContent) {
    //console.log(getArgs)
    //console.log(composable)
    let args = getArgs(row)
    row.ActualOutput = composable.apply(this, args)
    if (row.ActualOutput == null) {
      nullAnsNum++
    }
    row.TesterName = `e`
    let myTime = new Date()
    row.Time = myTime.toLocaleString()
    if (row.ActualOutput === row.ExpectedOutput) {
      row.Correctness = `TRUE`
    } else {
      row.Correctness = `FALSE`
      falseNum++
    }
    //console.log(row)
  }
  return {
    falseNum,
    nullAnsNum
  }
}

const uploadRef = ref<Component | null>(null)
const fileData = ref<any[] | null>(null)
const fileListLength = ref(0)
function handleUpload(options: { fileList: string | any[] }) {
  fileListLength.value = options.fileList.length;
  if (fileListLength.value !== 0) {
    // 获取手动上传的.csv文件对象,转化为数组
    //console.log(options.fileList[0].file)
    Papa.parse(options.fileList[0].file, {
      complete: (res) => {
        fileData.value = res.data
        //console.log(fileData.value)
      }
    })
  } else {
    fileData.value = null
  }
}
function getLocalFile(filePath: string) {
  // 使用XMLHttpRequest读取本地文件
  let xhr = null
  xhr = new XMLHttpRequest()
  const okStatus = document.location.protocol === 'file' ? 0 : 200
  xhr.open('GET', filePath, false)
  xhr.overrideMimeType('text/html;charset=utf-8')
  xhr.send()
  return xhr.status === okStatus ? xhr.responseText : null
}

const iterationColumns = ref<Column[]>([])
const iterationTable = ref<any[]>([])

const resultColumns = ref<Column[]>([])
const result = ref<Row[]>([])
const pagination = {
  pageSize: 7
}
const currTab = ref('Question')
const message = useMessage()
function handleTesting() {
  if (fileListLength.value) {
    // 使用手动上传的.csv文件
    message.info(`使用用户手动上传的测试用例。`)
  } else {
    // 使用项目本地的.csv文件
    let rawFile = getLocalFile(`/testUsecases/${props.context}/${usecaseType.value}.csv`)
    if (!rawFile) {
      message.warning(`暂时未准备该类型测试用例，请选择其他用例集或手动上传用例集。`)
      return false
    }
    //console.log(rawFile)
    Papa.parse(rawFile, {
      complete: (res) => {
        fileData.value = res.data
      }
    })
    console.log(fileData.value)
  }
  // 绘制表头
  resultColumns.value = createColumns(fileData.value as any[])
  // 绘制表格
  result.value = createRows(fileData.value as any[])
  console.log(result.value, resultColumns.value)
  // 进行测试并回填结果
  const { falseNum, nullAnsNum } = executeTesting(result.value)
  message.success(`测试完毕，共执行 ${result.value.length} 个用例，通过 ${result.value.length - falseNum} 个用例。`)

  // @ts-ignore
  ecOption.series[0].data[0].value = nullAnsNum
  // @ts-ignore
  ecOption.series[0].data[1].value = result.value.length - falseNum
  // @ts-ignore
  ecOption.series[0].data[2].value = falseNum

  currTab.value = 'Result'
}
// 导出.csv文件
function exportCsv() {
  const tableData = []
  const cols = []
  for (let col of resultColumns.value) {
    cols.push(col.title)
  }
  tableData.push(cols)
  for (let item of result.value) {
    let row: any[] = []
    for (let property in item) {
      row.push(item[property])
    }
    row.shift()
    tableData.push(row)
  }
  console.log(tableData)
  const csv = Papa.unparse(tableData)
  // 定义文件内容，类型为Blob
  let content = new Blob([csv])
  // 生产url对象
  let urlObject = window.URL || window.webkitURL || window
  let url = urlObject.createObjectURL(content)
  // 生产<a></a>DOM元素
  let el = document.createElement('a')
  // 链接赋值
  el.href = url
  el.download = `${props.context}_${fileListLength.value ? 'manual' : usecaseType.value}.csv`
  // 模拟点击事件，开始下载
  el.click()
  // 移除链接，释放资源
  urlObject.revokeObjectURL(url)
}

hljs.registerLanguage('javascript', javascript)
</script>

<style scoped>
.flex-wrapper {
  display: flex;
  flex-direction: row;
}

.left-flex-item {
  max-width: 63%;
  flex: 1 0 auto;
  overflow-y: auto;
}

.visualization {
  width: 100%;
  box-sizing: border-box;
  min-height: 83vh;
  max-height: 85vh;
}

.chart {
  font-size: 1rem;
  width: 42em;
  height: 20em;
}

.right-flex-item {
  padding-top: 3.2em;
  margin-left: .6em;
  flex: 1 1 auto;
}

.description {
  box-sizing: border-box;
  width: 100%;
}

.output {
  width: 100%;
  min-height: 83vh;
  max-height: 85vh;
}

.subtitle {
  margin: 0;
  font-size: 1.1em;
  font-weight: 600;
}

.text-body {
  margin: 0 0 .8em 0;
  font-size: 1em;
}

.code-block {
  overflow-y: auto;
}

::-webkit-scrollbar {
  width: 0 !important;
  height: 0;
}

.upload-box {
  box-sizing: border-box;
  height: 100%;
  width: 100%;
}

.cascader-input {
  width: 50%;
  margin: 1.2em auto;
  overflow-x: hidden;
}

.upload-content {
  margin: 1.2em 0;
}

.upload-btn {
  margin: 1.2em 0;
}
</style>
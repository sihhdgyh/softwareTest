<template>
  <test-panel :context="context" :options="options" :versions="versions" :ec-option="ecOption"
    :iteration="iteration" @commissionCaculate="change">
    <template #header>
      Question 17. 佣金计算。一销售系统，如果销售员的年销售额大于200万RMB且请假天数不超过10天的情况下，现金到帐大于等于60%，则佣金（提成）系数为7，即佣金值为销售额除以佣金系数；现金到帐小于60%，佣金不予计算。所有其他情况且现金到帐小于等于85%，则按佣金系数均为6计算佣金，现金到账大于85%，佣金系数按5处理。
    </template>
    <template #sub-title>
      算法思想
    </template>
    <template #detail>
      本题输入变量有三个，首先判断销售额和请假天数，再判断现金到账
      <br />① 输入年销售额、请假天数、现金到账3个变量；
      <br />② 先判断销售额是否大于200（万）和请假天数是否不超过10（天）
      <br />③ 再判断现金到账条件；
    </template>
  
    
  </test-panel>
</template>

<script setup lang="ts">
import type { ECOption } from '@/interface'
import * as echarts from 'echarts/core'
import TestPanel from '../../components/TestPanel.vue'
import {ref,computed,onUpdated} from 'vue'

interface ICommissionCaculate {
  commissionCaculate : string
}
const change = (val:ICommissionCaculate) => {
    //alert(val.triangleJudge); // 获取子组件的值  打印 user2
    if(val.commissionCaculate=='0.0.0'){
      version.value=true
    }
    else{
      version.value=false
    }
    // version.value="111"
    // alert(version.value)
}



const version=ref(true)

// 上下文
const context = 'commissionCaculate'

// 测试用例集选项
const options = [
  {
    label: '白盒测试',
    value: 'white-box',
    children: [
      {
        label: '语句覆盖',
        value: 'statement-coverage',
      },
      {
        label: '判断覆盖',
        value: 'judgment-coverage',
      },
      {
        label: '条件覆盖',
        value: 'condition-coverage',
      },
      {
        label: '判断-条件覆盖',
        value: 'judgmentCondition-coverage',
      },
      {
        label: '条件组合覆盖',
        value: 'conditionalCombination-coverage',
      },

    ]
  },
]

// 实现代码


// 程序版本集
const versions = [
  {
    label: '0.0.0',
    value: '0.0.0'
  },
]

// ECharts 绘图选项
const ecOption: ECOption = {
  xAxis: {
    type: 'category',
    data: ['0.0.0版本']
  },
  yAxis: [
    {
      type: 'value',
      name: '测试用例通过率',
      alignTicks: true,
      position: 'left',
      axisLabel: {
        formatter: '{value} %'
      }
    },
    {
      type: 'value',
      name: '测试用例通过数',
      position: 'right',
    }
  ],
  tooltip: {
    trigger: 'axis'
  },
  toolbox: {
    show: true,
    feature: {
      dataView: { show: true, readOnly: false },
      magicType: { show: true, type: ['line', 'bar'] },
      restore: { show: true },
      saveAsImage: { show: true }
    }
  },
  series: [
    {
      data: [
        {
          value: 97.2,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "#83bff6" },
              { offset: 0.8, color: "#188df0" },
              { offset: 1, color: "#188df0" },
            ]),
          }
        },
        {
          value: 100,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "#83bff6" },
              { offset: 0.8, color: "#188df0" },
              { offset: 1, color: "#188df0" },
            ]),
          }
        }
      ],
      type: 'bar',
      yAxisIndex: 0,
      name: '测试用例通过率',
      tooltip: {
        valueFormatter: (value) => value + ' %'
      }
    },
    {
      data: [
        {
          value: 29,
          itemStyle: {
            color: 'green'
          }
        },
        {
          value: 30,
          itemStyle: {
            color: 'green'
          }
        }
      ],
      type: 'line',
      yAxisIndex: 1,
      markPoint: {
        data: [
          { type: 'max', name: 'Max' }
        ]
      },
      name: '测试用例通过数',
      tooltip: {
        valueFormatter: (value) => value + ' 个'
      }
    }
  ]
}

// 代码版本迭代信息
const iteration = {
  columns: [{
    title: '版本号',
    key: 'version'
  }, {
    title: '测试数据集',
    key: 'dataset'
  }, {
    title: '测试情况',
    key: 'result'
  }, {
    title: '缺陷描述',
    key: 'bug'
  }],
  data: [{
    key: '0',
    version: '0.0.0',
    dataset: '强健壮等价类',
    result: '通过29/30',
    bug: '等腰与等边条件判断逻辑顺序有误'
  }, {
    key: '1',
    version: '0.0.0',
    dataset: '强健壮等价类',
    result: '通过30/30',
    bug: '测试全部通过'
  }]
}

</script>

<style scoped>
</style>
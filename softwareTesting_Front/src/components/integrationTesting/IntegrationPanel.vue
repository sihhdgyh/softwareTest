<script lang="ts" setup>
import { ref } from "vue";
import {
  NCard,
  NText,
  NH2,
  NDataTable,
  NSelect,
  type DataTableColumns,
} from "naive-ui";
import Papa from "papaparse";
// 引入 Axios 库
import axios from "axios";

// 选项数据
const options = [
  { label: "选择被测接口", value: "0" },
  { label: "GUN_INT_001", value: "1" },
  { label: "GUN_INT_002", value: "2" },
  { label: "GUN_INT_003", value: "3" },
  { label: "GUN_INT_004", value: "4" },
  { label: "GUN_INT_005", value: "5" },
  { label: "GUN_INT_006", value: "6" },
  { label: "GUN_INT_007", value: "7" },
];

// 选择的值
const value = ref("0");

// 列数据
const columns = ref<DataTableColumns>([]);
// 行数据
const data = ref<{ [index: string]: any }[]>([]);
// 分页
const pagination = {
  pageSize: 6,
};

function getLocalFile(context: string) {
  // 使用XMLHttpRequest读取本地文件
  let xhr = new XMLHttpRequest();
  const okStatus = document.location.protocol === "file" ? 0 : 200;
  xhr.open("GET", `/testUsecases/testFile/integrationTest/${context}.csv`, false);
  console.log(context);
  xhr.overrideMimeType("text/html;charset=utf-8");
  xhr.send();
  return xhr.status === okStatus ? xhr.responseText : null;
}

// 生成列
const createColumns = (cols: string[]) => {
  cols.forEach((item) => {
    columns.value.push({
      key: item,
      title: item,
    });
  });
  //console.log(columns.value)
};

// 生成行
const createRows = (rows: any[]) => {
  rows.forEach((row) => {
    const rowTmp: { [index: string]: any } = {};
    let counter: number = 0;
    for (let prop of columns.value) {
      rowTmp[(prop as { key: string }).key] = row[counter++];
    }
    data.value.push(rowTmp);
  });
  //console.log(data.value)
};

function handleChange(value: string) {
  columns.value = [];
  data.value = [];
  const selectedOption = options.find((option) => option.value === value);
  if (selectedOption) {
    let fileData = getLocalFile(selectedOption.label);
    Papa.parse(fileData as string, {
      complete: (res) => {
        const parsedResult = res.data as string[][];
        createColumns(parsedResult[0]);
        parsedResult.splice(0, 1);
        createRows(parsedResult);
      },
    });
  }
}

function sendRequest(i: number) {
  // 读取 JSON 文件
  const xhr = new XMLHttpRequest();
  xhr.open("GET", "GUN_INT_cfg.json", true);
  xhr.onreadystatechange = function () {
    if (xhr.readyState === 4 && xhr.status === 200) {
      const apiData = JSON.parse(xhr.responseText);
      if (i >= 0 && i < apiData.length) {
        const api = apiData[i];
        const url = api.url;
        const method = api.method;
        //读取data
      }
    }
  }
} 
</script>

<template>
  <n-card class="unit-panel-wrapper">
    <n-h2>
      <slot name="title" />
    </n-h2>
    <n-text tag="div">
      <slot name="test-case" />
    </n-text>
    <n-space vertical>
      <n-select v-model:value="value" :options="options" @change="handleChange" />
    </n-space>
    <br />
    <n-data-table
      size="small"
      :max-height="420"
      :bordered="false"
      :columns="columns"
      :data="data"
      :pagination="pagination"
    />
  </n-card>
</template>


<style scoped>
.unit-panel-wrapper {
  box-sizing: border-box;
  height: 41.5em;
}
</style>
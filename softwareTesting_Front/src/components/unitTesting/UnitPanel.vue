<script lang="ts" setup>
import { ref } from "vue";
import {
  NCard,
  NText,
  NH2,
  NDataTable,
  type DataTableColumns,
  NSelect,
} from "naive-ui";
import Papa from "papaparse";

const props = defineProps<{
  context: string;
}>();

let options = [{ label: "选择被测方法", value: "0" }];
// 选项数据
if (props.context == 'SMSCode') {
  options = [
    { label: "选择被测方法", value: "0" },
    { label: "getCodeToSMS", value: "1" },
    { label: "send", value: "2" },
  ];
}
else if (props.context == 'Book') {
  options = [
    { label: "选择被测方法", value: "0" },
    { label: "search", value: "1" },
    { label: "getBookid", value: "2" },
  ];
}
else if (props.context == 'User') {
  options = [
    { label: "选择被测方法", value: "0" },
    { label: "register", value: "1" },
    { label: "findpassword", value: "2" },
  ];
}

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
  xhr.open(
    "GET",
    `/testUsecases/testFile/unitTest/${context}.csv`,
    false
  );
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
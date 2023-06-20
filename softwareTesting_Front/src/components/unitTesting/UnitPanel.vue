<script lang="ts" setup>
import { ref } from "vue";
import {
  NCard,
  NText,
  NH2,
  NDataTable,
  NSelect,
  type DataTableColumns,
  NButton,
} from "naive-ui";
import Papa from "papaparse";
import axios from "axios";

const props = defineProps<{
  context: string;
}>();

let options = [{ label: "选择被测方法", value: "0" }];
// 选项数据
if (props.context == "SMSCode") {
  options = [
    { label: "选择被测方法", value: "0" },
    { label: "getCodeToSMS", value: "1" },
    { label: "send", value: "2" },
  ];
} else if (props.context == "Book") {
  options = [
    { label: "选择被测方法", value: "0" },
    { label: "search", value: "1" },
    { label: "getBookid", value: "2" },
  ];
} else if (props.context == "User") {
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
  xhr.open("GET", `/testUsecases/testFile/unitTest/${context}.csv`, false);
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

// 读取 JSON 文件
function readJSONFile() {
  let path = "";
  if (props.context == "SMSCode")
    path = "/testUsecases/testFile/unitTest/SMSCode_info.json";
  else if (props.context == "Book")
    path = "/testUsecases/testFile/unitTest/Book_info.json";
  else if (props.context == "User")
    path = "/testUsecases/testFile/unitTest/Book_info.json";
  return axios
    .get(path)
    .then((response) => response.data)
    .catch((error) => {
      console.log("Failed to read JSON file:", error);
      throw error;
    });
}

function sendRequest() {
  readJSONFile().then((content) => {
    const item = content[parseInt(value.value) - 1];
    const url = item.url;
    const method = item.method;
    axios({
      url: url,
      method: method,
    })
      .then()
      .catch((error) => {
        console.error("Error:", error);
      });
    //读取result并显示
    const selectedOption = options.find(
      (option) => option.value == value.value
    );
    columns.value = [];
    data.value = [];
    if (selectedOption) {
      let fileData = getLocalFile(selectedOption.label + "Result");
      Papa.parse(fileData as string, {
        complete: (res) => {
          const parsedResult = res.data as string[][];
          createColumns(parsedResult[0]);
          parsedResult.splice(0, 1);
          createRows(parsedResult);
        },
      });
    }
  });
}

function handleClick() {
  sendRequest();
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
    <n-button class="button" type="primary" @click="handleClick">Test</n-button>
  </n-card>
</template>

<style scoped>
.unit-panel-wrapper {
  box-sizing: border-box;
  height: 41.5em;
}
.button {
  margin-top: 2em;
  margin-left: 82em;
  margin-right: auto;
}
</style>
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
// 引入 Axios 库
import axios from "axios";

//页面名称
const props = defineProps<{
  context: string;
}>();
// 选项数据
let options = [{ label: "选择被测接口", value: "0" }];
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

function change_part() {
  if (props.context == "GUN_INT") {
    options = [
      { label: "选择被测接口", value: "0" },
      { label: "GUN_INT_001", value: "1" },
      { label: "GUN_INT_002", value: "2" },
      { label: "GUN_INT_003", value: "3" },
      { label: "GUN_INT_004", value: "4" },
    ];
  } else if (props.context == "HLD_INT") {
    options = [
      { label: "选择被测接口", value: "0" },
      { label: "HLD_001_INT_001", value: "1" },
      { label: "HLD_003_INT_001", value: "2" },
    ];
  }
}

function getLocalFile(context: string) {
  // 使用XMLHttpRequest读取本地文件
  let xhr = new XMLHttpRequest();
  const okStatus = document.location.protocol === "file" ? 0 : 200;
  xhr.open(
    "GET",
    `/testUsecases/testFile/integrationTest/${context}.csv`,
    false
  );
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
  readData(value);
  columns.value = [];
  data.value = [];
  const selectedOption = options.find((option) => option.value == value);
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

let all_testcase: { [key: string]: any } = {};
let all_expected: { [key: string]: any } = {};
function readData(value: string) {
  all_testcase = {};
  const selectedOption = options.find((option) => option.value === value);
  if (selectedOption) {
    //console.log(selectedOption.label);
    let fileData = getLocalFile(selectedOption.label);

    // 解析 CSV 文件数据
    Papa.parse(fileData as string, {
      complete: (res) => {
        const parsedResult = res.data as string[][];

        // 获取字段索引
        const headerRow = parsedResult[0];
        const noIndex = headerRow.indexOf("no");
        const expectedIndex = headerRow.indexOf("expected");
        // 遍历数据行
        for (let i = 1; i < parsedResult.length; i++) {
          const dataRow = parsedResult[i];
          const testCase: { [key: string]: any } = {};
          const expected: { [key: string]: any } = {};
          // 提取 no 之后、expected 之前的数据
          for (let j = noIndex + 1; j < expectedIndex; j++) {
            const key = headerRow[j];
            const value = dataRow[j];
            testCase[key] = value;
          }
          expected[headerRow[expectedIndex]] = dataRow[expectedIndex];
          // 将 testCase 存入 all_testcase 对象
          const testCaseKey = dataRow[noIndex];
          all_testcase[testCaseKey] = testCase;
          all_expected[testCaseKey] = expected;
        }
        //console.log("All test cases:", all_testcase[1]);
      },
    });
  }
}

// 读取 JSON 文件
function readJSONFile() {
  if (props.context == "GUN_INT") {
    return axios
      .get("/testUsecases/testFile/integrationTest/GUN_INT_info.json")
      .then((response) => response.data)
      .catch((error) => {
        console.log("Failed to read JSON file:", error);
        throw error;
      });
  } else if (props.context == "HLD_INT") {
    return axios
      .get("/testUsecases/testFile/integrationTest/HLD_INT_info.json")
      .then((response) => response.data)
      .catch((error) => {
        console.log("Failed to read JSON file:", error);
        throw error;
      });
  }
}

// 发送 Axios 请求

// // 发送请求函数
function sendRequest(i: number) {
  if (props.context == "GUN_INT") {
    readJSONFile().then((content) => {
      const item = content[parseInt(value.value) - 1];
      const url = item.url;
      const method = item.method;
      const exp = item.expected;
      const keys = Object.keys(all_testcase);
      const key = keys[i];
      let request_data = all_testcase[key];
      console.log(request_data);
      axios({
        url: url,
        method: method,
        data: request_data,
      })
        .then((response) => {
          const keys = Object.keys(all_testcase);
          const key = keys[i];
          //console.log("Response:", i, response.data);
          //console.log(all_expected[key]);
          data.value[i][
            Object.keys(data.value[i])[Object.keys(data.value[i]).length - 2]
          ] = response.data[exp];
          if (response.data[exp] == all_expected[key].expected)
            data.value[i][
              Object.keys(data.value[i])[Object.keys(data.value[i]).length - 1]
            ] = "yes";
          else
            data.value[i][
              Object.keys(data.value[i])[Object.keys(data.value[i]).length - 1]
            ] = "no";
        })
        .catch((error) => {
          console.error("Error:", error);
        });
    });
  } else if (props.context == "HLD_INT") {
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
}

function handleClick() {
  const keys = Object.keys(all_testcase);
  const count = keys.length;
  for (let i = 0; i < count; i++) sendRequest(i);
}

change_part();
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
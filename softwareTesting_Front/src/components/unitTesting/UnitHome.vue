<script lang="ts" setup>
import { NCard, NText, NA, NBlockquote, NH2, NUl, NLi } from 'naive-ui'
import IconVitestVue from '../icons/IconVitest.vue'

</script>

<template>
  <n-card class="unit-home-wrapper">
    <n-h2>单元测试</n-h2>
    <n-blockquote>
      单元测试（Unit Testing），是指对软件中的最小可测试单元进行检查和验证。
    </n-blockquote>
    <n-text tag="div">
      选了3个类——SMSCode、Book、User类，一共是6个函数
    </n-text>
    <br />
    <n-text tag="div">
      这些通常是与 Vue 无关的纯 JavaScript/TypeScript 模块。一般来说，在 Vue 应用中为业务逻辑编写单元测试与使用其他框架的应用没有明显区别。
    </n-text>
    <n-h2>测试工具</n-h2>
    <n-text tag="div">
      本项目使用单元测试工具
      <n-a href="https://vitest.dev/" target="_blank">
        Junit
      </n-a>
      将测试用例写在文件中，通过读写csv文件的方式实现
    </n-text>
    <br />
  </n-card>
</template>

<style scoped>
.unit-home-wrapper {
  height: 41.5em;
  box-sizing: border-box;
}

.vitest-logo {
  width: 1.4em;
  height: 1.4em;
}
</style>
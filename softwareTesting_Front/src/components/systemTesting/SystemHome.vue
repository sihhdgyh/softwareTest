<script lang="ts" setup>
import { NCard, NText, NA, NBlockquote, NH2, NImage, NUl, NLi } from 'naive-ui'

</script>

<template>
  <n-card class="unit-home-wrapper">
    <n-h2>系统测试</n-h2>
    <n-h2>测试工具</n-h2>
    <n-text tag="div">
      本项目使用端到端测试框架
      <n-a href="https://www.cypress.io/" target="_blank">
        <n-image src="/cypress-logo.png" class="cypress-logo" />
      </n-a>
    </n-text>
    <br />
    <n-blockquote>
      做了功能性测试与非功能性测试
    </n-blockquote>
    <n-text tag="div">
     功能性测试如下
    </n-text>
    <br />
    <n-text tag="div">
      非功能性测试
    </n-text>
    <img src="non-function.png "/>
  </n-card>
</template>

<style scoped>
.unit-home-wrapper {
  height: 41.5em;
  box-sizing: border-box;
}

.cypress-logo {
  height: 1.2em;
}
</style>
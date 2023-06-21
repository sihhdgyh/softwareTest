<script lang="ts" setup>
import { NCard, NText, NA, NBlockquote, NH2 } from 'naive-ui'
import IconVitestVue from '../icons/IconVitest.vue'

</script>

<template>
  <n-card class="unit-home-wrapper">
    <n-h2>集成测试</n-h2>
    <n-blockquote>
      选取了两个模块——登录注册模块与验证码模块
    </n-blockquote>
    <n-text tag="div">
      首先测试了模块接口与模块间集成接口，然后测了前后端接口
    </n-text>
    <br />
    <n-text tag="div">
    </n-text>
    
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
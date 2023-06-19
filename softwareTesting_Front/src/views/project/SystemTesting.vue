<script setup lang="ts">
import { NTabs, NTabPane } from 'naive-ui'
import SystemHomeVue from '../../components/systemTesting/SystemHome.vue'
import sdk from '@stackblitz/sdk'
import { onMounted } from 'vue'

const tabs = [
  {
    name: 'home',
    tab: '系统测试'
  }
]

</script>

<template>
  <div class="root-wrapper">
    <div class="left-part">
      <n-tabs type="line" animated default-value="home">
        <n-tab-pane v-for="item in tabs" :name="item.name" :tab="item.tab">
          <system-home-vue v-if="item.name === 'home'" />
        </n-tab-pane>
      </n-tabs>
    </div>
    <div class="right-part">
      <div id="stackblitz" />
    </div>
  </div>
</template>

<style scoped>
.root-wrapper {
  display: flex;
  gap: .5em;
  font-size: 1rem;
}

.left-part {
  width: 36em;
}

.right-part {
  box-sizing: border-box;
  width: 45.6em;
}
</style>
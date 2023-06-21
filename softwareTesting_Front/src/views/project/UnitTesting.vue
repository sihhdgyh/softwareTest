<script setup lang="ts">
import { NTabs, NTabPane } from 'naive-ui'
import UnitPanelVue from '../../components/unitTesting/UnitPanel.vue'
import UnitHomeVue from '../../components/unitTesting/UnitHome.vue'
import sdk from '@stackblitz/sdk'
import { onMounted } from 'vue'

const tabs = [
  {
    name: 'home',
    tab: '单元测试'
  },
  {
    name: 'SMSCode',
    tab: 'SMSCode类',
  },
  {
    name: 'Book',
    tab: 'Book类',
  },
  {
    name: 'User',
    tab: 'User类',
  }
]
</script>

<template>
  <div class="root-wrapper">
    <div class="left-part">
      <n-tabs type="line" animated default-value="home">
        <n-tab-pane v-for="item in tabs" :name="item.name" :tab="item.tab">
          <unit-home-vue v-if="item.name === 'home'" />
          <unit-panel-vue :context="item.name" v-else>
            <template #title>
              {{ item.tab }}
            </template>
            <template #test-case>
              {{ item.testCase }}
            </template>
          </unit-panel-vue>
        </n-tab-pane>
      </n-tabs>
    </div>
  </div>
</template>

<style scoped>
.root-wrapper {
  display: flex;
  gap: .5em;
  font-size: 1rem;
}

</style>
<script setup lang="ts">
import { NTabs, NTabPane } from "naive-ui";
import IntegrationHomeVue from "../../components/integrationTesting/IntegrationHome.vue";
import IntegrationPanelVue from "../../components/integrationTesting/IntegrationPanel.vue";
import sdk from "@stackblitz/sdk";
import { onMounted } from "vue";

const tabs = [
  {
    name: "home",
    tab: "集成测试",
  },
  {
    name: "HLD_INT",
    tab: "模块内、模块间接口",
  },
  {
    name: "GUN_INT",
    tab: "模块与界面接口",
  },
];
</script>

<template>
  <div class="root-wrapper">
    <div class="left-part">
      <n-tabs type="line" animated default-value="home">
        <n-tab-pane v-for="item in tabs" :name="item.name" :tab="item.tab">
          <integration-home-vue v-if="item.name === 'home'" />
          <integration-panel-vue :context="item.name" v-else>
            <template #title>{{ item.tab }}</template>
          </integration-panel-vue>
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
  gap: 0.5em;
  font-size: 1rem;
}

.left-part {
  width: 50em;
}

.right-part {
  box-sizing: border-box;
  width: 45.6em;
}
</style>
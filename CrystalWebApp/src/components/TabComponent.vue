<script setup lang="ts">

import {useI18n} from "vue-i18n";
import router from "@/router";

const { t } = useI18n()

export interface TabItem {
  name: string,
  translate: boolean,
  path: string
}

const props = defineProps<{
  currentPath: string,
  items: TabItem[],
  align: number,
  size: string
}>()

</script>

<template>
  <main
      class="tab-container flex flex-horizontal gap-8 mt-8"
      :class="{
        'flex-center': align == 1,
        'flex-end': align == 2
      }"
  >
    <div
        v-for="(item, index) in props.items"
        :key="index"
        :class="{
          'tab__item': true,
          'tab__item--active': currentPath == item.path,
        }"
        :style="
          'font-size: ' + (props.size == 'small' ? 'var(--font-size-small)' : (props.size == 'default' ? 'var(--font-size-default)' : 'var(--font-size-large)'))
        "
        @click="router.push(item.path)"
    >
      {{ item.translate ? t(item.name) : item.name }}
    </div>
  </main>
</template>

<style scoped>
.tab-container {
  width: 100%;

  .tab__item {
    color: #aaa;
    cursor: pointer;
    user-select: none;
  }

  .tab__item--active {
    color: #020303;
  }
}
</style>
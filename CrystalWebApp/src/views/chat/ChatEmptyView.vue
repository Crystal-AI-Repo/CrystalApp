<script setup lang="ts">

import {useI18n} from "vue-i18n";

const { t } = useI18n()

</script>

<template>
  <main class="flex-grow-1 flex flex-vertical flex-center">
    <p class="text-3xl">{{ t('chat.text.emptySession') }}</p>
    <el-button class="mt-4" size="large" type="primary">{{ t('chat.button.exploreWorkshop') }}</el-button>
  </main>
</template>

<style scoped>

</style>
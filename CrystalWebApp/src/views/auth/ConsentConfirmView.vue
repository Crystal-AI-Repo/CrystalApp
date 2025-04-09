<script setup lang="ts">

import {getQueryString} from "@/utils/url-utils.ts";

const code = getQueryString("code")

if (!code) {
  ElMessage.error("Error parameter")
}
</script>

<template>
  <main>

  </main>
</template>

<style scoped>

</style>
<script setup lang="ts">

import {getQueryString} from "@/utils/url-utils.ts";

const code = getQueryString("code")

if (!code) {
  location.href = "http://localhost:5173/"
}



</script>

<template>
  <main>

  </main>
</template>

<style scoped>

</style>
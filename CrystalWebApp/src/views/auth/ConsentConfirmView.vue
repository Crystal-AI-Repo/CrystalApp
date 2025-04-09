<script setup lang="ts">

import {getQueryString} from "@/utils/url-utils.ts";
import {applicationFormUrlEncoded, doPost} from "@/net/axios-request.ts";

interface TokenRequestResponse {
  access_token: string,
  refresh_token: string,
}

const code = getQueryString("code")

if (!code) {
  ElMessage.error("Error parameter")
}

doPost<TokenRequestResponse>(
    "/api/auth/token",
    {
      "Content-Type": applicationFormUrlEncoded
    },
    { code: code }
).then((res) => {
  localStorage.setItem("ticket", res.data.access_token)
  localStorage.setItem("r_ticket", res.data.refresh_token)
}).catch((err) => {
  ElMessage.warning(err.code + ": " + err.message)
})
</script>

<template>
  <main>

  </main>
</template>

<style scoped>

</style>
<script setup lang="ts">

import {getQueryString} from "@/utils/url-utils.ts";
import {applicationFormUrlEncoded, doPost} from "@/net/axios-request.ts";
import {clearUserAuthToken} from "@/utils/auth-utils.ts";
import router from "@/router";

interface TokenRequestResponse {
  access_token: string,
  refresh_token: string,
  expires_in: number
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
  localStorage.setItem("ticket_expires", res.data.expires_in.toString())

  const afterAuth = localStorage.getItem('after_auth')
  if (afterAuth) {
    location.href = afterAuth
  }
}).catch((err) => {
  ElMessage.warning(err.code + ": " + err.message)
  setTimeout(() => {
    clearUserAuthToken()
    router.push("/")
  }, 1000)
})
</script>

<template>
  <main>

  </main>
</template>

<style scoped>

</style>
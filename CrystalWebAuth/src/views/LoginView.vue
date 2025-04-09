<script setup lang="ts">
import {useI18n} from "vue-i18n";
import {ref} from "vue";
import {applicationFormUrlEncoded, doPost, internalPost} from "@/net/axios-request.ts";
import {getQueryString} from "@/utils/url-utils.ts";

const { t } = useI18n()

const nonceId = getQueryString("nonceId")
const targetUrl= getQueryString("target")
if (!nonceId || !targetUrl) {
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  // Jump to the correct login page
  location.href = "http://127.0.0.1:8080/oauth2/authorize?client_id=crystal-app&response_type=code&scope=profile&redirect_uri=http%3A%2F%2Flocalhost%3A5173%2Fconsent"

  loading.close()
}

interface LoginForm {
  username: string,
  password: string
}

const formData = ref<LoginForm>({
  username: "admin",
  password: "123456"
})

function onSubmit() {
  doPost(
      "/api/login",
      { "Content-Type": applicationFormUrlEncoded, nonceId: nonceId },
      { username: formData.value.username, password: formData.value.password }
  ).then((res) => {
    location.href = targetUrl
  }).catch((err) => {
    ElMessage.warning(err.message)
  })
}
</script>

<template>
  <main>
    <p class="view-title">{{ t('titleLogin') }}</p>
    <span style="height: 1rem"/>
    <el-form style="margin-top: 1rem" :model="formData" label-position="top" size="default">
      <el-form-item :label="t('inputTitleUsername')">
        <el-input v-model="formData.username" :placeholder="t('inputPlaceholderUsername')" />
      </el-form-item>

      <el-form-item :label="t('inputTitlePassword')">
        <el-input v-model="formData.password" type="password" show-password />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="onSubmit">{{ t('buttonLogin') }}</el-button>
        <el-button>{{ t('buttonRegister') }}</el-button>
      </el-form-item>
    </el-form>
  </main>
</template>

<style scoped>
@import "common-styles.css";
</style>
<script setup lang="ts">

import {useI18n} from "vue-i18n";
import {applicationFormUrlEncoded, doGet, doPost} from "@/net/axios-request.ts";
import {ref} from "vue";
import {getQueryString} from "@/utils/url-utils.ts";

const { t } = useI18n()

interface ConsentInfoResponse {
  clientId: string,
  clientName: string,
  principalName: string,
  scopes: ScopeInfo[],
  previouslyApprovedScopes: ScopeInfo[],
  state: string,
  requestURI: string
}

interface ScopeInfo {
  scopeName: string,
  description: string
}

const scopeInfo = ref<ConsentInfoResponse>({
  clientId: "", clientName: "", previouslyApprovedScopes: [], principalName: "", requestURI: "", scopes: [], state: ""
})

const prevCheckedScopes = ref<string[]>([])
const checkedScopes = ref<string[]>([])

doGet<ConsentInfoResponse>(`/api/oauth2/consent/parameters${window.location.search}`, {}, {}).then((res) => {
  scopeInfo.value = res.data
}).catch((err) => {
  ElMessage.warning(err.code + ": " + err.message)
})

function submit() {
  const data = new FormData()
  if (checkedScopes.value.length > 0) {
    checkedScopes.value.forEach((e: any) => data.append('scope', e))
  } else {
    ElMessage.warning("At least one permission should be permitted")
    return
  }
  data.append('state', scopeInfo.value.state)
  data.append('client_id', scopeInfo.value.clientId)
  console.log(data.get("scope"))
  doPost<string>(
      `/api${scopeInfo.value.requestURI}`,
      {
        'Content-Type': applicationFormUrlEncoded,
        nonceId: getQueryString('nonceId')
      },
      new URLSearchParams(data)
  ).then((res) => {
    window.location.href = res.data
  }).catch((err) => {
    ElMessage.warning(err.code + ": " + err.message)
  })
}


</script>

<template>
  <main>
    <p class="view-title">{{ t('titleConsent') }}</p>
    <p class="view-subtitle">{{ t('subtitleConsent', { applicationName: scopeInfo.clientId }) }}</p>

    <div class="permissions-box">
      <el-checkbox-group v-model="prevCheckedScopes" size="medium" class="flex-vertical">
        <el-checkbox
            v-for="(item, index) in scopeInfo.previouslyApprovedScopes"
            :key="index"
            :label="item.description"
            :checked="true"
            disabled
        />
      </el-checkbox-group>
      <el-checkbox-group v-model="checkedScopes" size="medium" class="flex-vertical">
        <el-checkbox
            v-for="(item, index) in scopeInfo.scopes"
            :key="index"
            :label="item.description"
            :value="item.scopeName.replace('#', '.')"
        />
      </el-checkbox-group>
    </div>


    <div>
      <el-button size="default" type="primary" @click="submit()">{{ t('buttonPermit') }}</el-button>
      <el-button size="default">{{ t('buttonCancel') }}</el-button>
    </div>
  </main>
</template>

<style scoped>
@import "common-styles.css";

.permissions-box {
  margin: 1rem 0;
}
</style>
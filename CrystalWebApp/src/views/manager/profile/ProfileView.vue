<script setup lang="ts">

import {ref, watch} from "vue";
import type {User} from "@/net/api/user-controller.ts";
import {getMyProfile, updateMyProfile} from "@/net/api/user-controller.ts";
import TabComponent, {TabItem} from "@/components/TabComponent.vue";
import router from "@/router";
import {Check, Close, Edit} from "@element-plus/icons-vue";
import {getUserAvatarUrl} from "@/utils/url-utils.ts";

const serverBaseUrl = import.meta.env.VITE_SERVER_BASE_URL

const currentPath = ref(location.pathname)
watch(router.currentRoute, () => {
  currentPath.value = location.pathname
})

const myProfile = ref<User>({
  activated: false,
  avatar: "",
  email: "",
  id: 0,
  modifiedTime: 0,
  nickname: "",
  registeredTime: 0,
  username: ""
})

async function refreshMyProfile() {
  myProfile.value = await getMyProfile()
}

refreshMyProfile()

const tabItems = ref<TabItem[]>([
  {
    name: 'manager.profile.tab.myChats',
    translate: true,
    path: '/manager/profile'
  },
  {
    name: 'manager.profile.tab.myCharacters',
    translate: true,
    path: '/manager/profile/myCharacters'
  }
])

const nicknameEditInput = ref()
const editingProfile = ref(false)
const isSubmitProfileProcessing = ref(false)

function switchProfileEditMode() {
  editingProfile.value = !editingProfile.value

  nicknameEditInput.value = myProfile.value.nickname
}

async function submitEditProfile() {
  isSubmitProfileProcessing.value = true

  await updateMyProfile({
    nickname: nicknameEditInput.value
  })

  isSubmitProfileProcessing.value = false

  editingProfile.value = false

  await refreshMyProfile()
}

</script>

<template>
  <main class="page-container">
    <div class="flex flex-horizontal flex-center-vertically">
      <el-avatar size="large" :src="getUserAvatarUrl(myProfile.id)" />

      <div class="flex flex-vertical ml-4 mr-4">
        <el-input v-if="editingProfile" v-model="nicknameEditInput" size="default" />
        <p v-else class="p-nickname">{{ myProfile.nickname }}</p>
        <p class="p-username">@{{ myProfile.username }}</p>
      </div>

      <el-button v-if="!editingProfile" :icon="Edit" circle type="info" plain @click="switchProfileEditMode()" />
      <div v-else>
        <el-button :icon="Close" circle type="danger" plain @click="switchProfileEditMode()" :disabled="isSubmitProfileProcessing" />
        <el-button :icon="Check" circle type="primary" plain @click="submitEditProfile()" :loading="isSubmitProfileProcessing" />
      </div>
    </div>

    <TabComponent :current-path="currentPath" :items="tabItems" :align="0" size="default" />

    <RouterView class="mt-4" />
  </main>
</template>

<style scoped>
.page-container {
  flex-grow: 1;
}

.p-nickname {
  font-size: 1.05rem;
  color: var(--color-primary);
}

.p-username {
  font-size: .95rem;
  color: var(--color-secondary);
}
</style>
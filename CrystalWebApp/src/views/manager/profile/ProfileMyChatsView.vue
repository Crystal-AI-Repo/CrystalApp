<script setup lang="ts">

import {ref} from "vue";
import type {UserContactVO} from "@/net/api/user-contact-controller.ts";
import {getUserContactList} from "@/net/api/user-contact-controller.ts";
import ContactCardComponent from "@/components/ContactCardComponent.vue";
import router from "@/router";

const currentPage = ref(1)
const totalPages = ref(1)
const totalCount = ref(0)
const currentPageData = ref<UserContactVO[]>()

async function refreshData() {
  const result = (await getUserContactList(currentPage.value)).data
  totalPages.value = result.pages
  totalCount.value = result.total
  currentPageData.value = result.records
}

refreshData()

</script>

<template>
  <main>
    <ContactCardComponent
        v-for="(item, index) in currentPageData"
        :contact="item"
        :key="index"
        :show-operation-buttons="true"
        component-style="card"
        :click-event="() => router.push(`/chat/${item.contact.id}`)"
    />
  </main>
</template>

<style scoped>

</style>
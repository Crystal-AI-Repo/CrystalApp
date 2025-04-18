<script setup lang="ts">

import HeaderComponent from "@/components/HeaderComponent.vue";

import {ref, watch} from "vue";
import type {UserContactVO} from "@/net/api/user-contact-controller.ts";
import {getUserContactList} from "@/net/api/user-contact-controller.ts";
import ContactCardComponent from "@/components/ContactCardComponent.vue";
import router from "@/router";
import {useRoute} from "vue-router";

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

/**
 * Route
 */
const route = useRoute()
const currentContactId = ref(route.params.contactId)
watch(router.currentRoute, () => {
  currentContactId.value = route.params.contactId
})

</script>

<template>
  <main class="flex flex-vertical">
    <HeaderComponent />

    <div class="main-container flex-grow-1 flex flex-horizontal ">
      <!-- Contact List -->
      <div class="contact-list">
        <ContactCardComponent
            v-for="(item, index) in currentPageData"
            :contact="item"
            :key="index"
            componentStyle="plain"
            :click-event="() => {
              router.push(`/chat/${item.contact.id}`)
            }"
            :is-active="currentContactId == item.contact.id"
        />
      </div>

      <!-- Chat View -->
      <RouterView />
    </div>
  </main>
</template>

<style scoped>
.main-container {
  width: 100%;
  height: 100vh;
  padding-top: 72px;
}

.contact-list {
  min-width: 240px;
  max-width: 15vw;
  box-shadow: 0 0 .25rem 0 rgba(0, 0, 0, .15);
}
</style>
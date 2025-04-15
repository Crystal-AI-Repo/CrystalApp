<script setup lang="ts">

import {ref, watch} from "vue";
import type {UserContact, UserContactVO} from "@/net/api/user-contact-controller.ts";
import {getUserContactList} from "@/net/api/user-contact-controller.ts";
import {ChatCharacter, getChatCharacterDetails} from "@/net/api/chat-character-controller.ts";
import {Delete} from "@element-plus/icons-vue";

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
    <div class="contact-card-wrapper flex-horizontal flex-center-vertically" v-for="(item, index) in currentPageData" :key="item.contact.id">
      <div class="contact-card flex flex-horizontal flex-center-vertically" v-if="item.contact.contactType == 0">
        <el-avatar class="flex-shrink-0" size="large" />
        <div class="flex flex-vertical ml-4">
          <p class="contact-name">{{ item.reifiedContact.name }}</p>
          <p class="contact-msg">{{ item.reifiedContact.greetingMessage }}</p>
        </div>
      </div>

      <span class="flex-grow-1" />

      <!-- Operation Buttons -->
      <div class="operations">
        <el-button type="danger" :icon="Delete" circle size="default" plain />
      </div>
    </div>
  </main>
</template>

<style scoped>
.contact-card-wrapper {
  padding: 1rem;
  box-shadow: 0 0 .1rem 0 var(--color-secondary);
  border-radius: .5rem;
  transition-duration: .25s;
  cursor: pointer;
  margin-bottom: 1rem;

  .operations {
    transition-duration: .25s;
    opacity: 0;
  }

  &:hover {
    transition-duration: .25s;
    box-shadow: 0 0 .25rem 0 var(--color-primary);
  }

  &:hover .operations {
    opacity: 1;
  }
}

.contact-card {
  .contact-name {
    font-size: var(--font-size-default);
    color: var(--color-primary);
  }

  .contact-msg {
    font-size: var(--font-size-small-p);
    color: var(--color-secondary);
  }
}
</style>
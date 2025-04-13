<script setup lang="ts">

import {ref, watch} from "vue";
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";
import {getMyCreatedChatCharacters} from "@/net/api/chat-character-controller.ts";
import {formatTimestamp} from "@/utils/datetime-utils.ts";
import {getUserProfile, User} from "../../../net/api/user-controller.ts";
import router from "@/router";

const currentPage = ref(1)
const totalPages = ref(1)
const totalCount = ref(0)
const currentPageData = ref<ChatCharacter[]>()

async function refreshData() {
  const result = await getMyCreatedChatCharacters(currentPage.value)
  totalPages.value = result.pages
  totalCount.value = result.total
  currentPageData.value = result.records
}

refreshData()

const userProfileMap = ref<Map<number, User>>(new Map())

watch(currentPageData, () => {
  currentPageData.value?.forEach(async (e: ChatCharacter) => {
    if (!userProfileMap.value.has(e.authorUid)) {
      // Prevent multi-request for the same user
      userProfileMap.value.set(e.authorUid, {})
      userProfileMap.value.set(e.authorUid, await getUserProfile(e.authorUid))
    }
  })
})

function onCardClick(item: ChatCharacter) {
  router.push(`/manager/newCharacter?mode=edit&id=${item.id}`)
}
</script>

<template>
  <main>
    <div class="chat-character-grid" id="chat-character-grid">
      <div class="chat-character-card" v-for="(item, index) in currentPageData" @click="onCardClick(item)">
        <div class="avatar-container">
          <div class="character-info">
            <p class="character-name">{{ item.name }}</p>
            <p class="character-description">{{ item.description.length > 18 ? item.description.slice(0, 18) + '...' : item.description }}</p>
          </div>

          <img class="avatar" :alt="item.name" src="@/assets/main-background.png" />
        </div>

        <div class="flex flex-horizontal flex-center-vertically p-2">
          <el-avatar size="small" />

          <div class="flex flex-vertical ml-2">
            <p class="author-name">{{ userProfileMap.get(item.authorUid).nickname }}</p>
            <p class="created-time">{{ formatTimestamp(item.createdTime) }}</p>
          </div>
        </div>


      </div>
    </div>

  </main>
</template>

<style scoped lang="scss">
$card-size: 10vw;
$grid-gap: 1rem;
$grid-row-count: 5;

.chat-character-grid {
  width: calc($card-size * $grid-row-count + ($grid-row-count - 1) * $grid-gap);
  display: grid;
  grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  gap: $grid-gap;
}

.chat-character-card {
  width: $card-size;
  min-height: $card-size;
  border-radius: .5rem;
  box-shadow: 0 0 .25rem 1px rgba(0, 0, 0, .15);
  overflow: hidden;
  transition-duration: .25s;
  cursor: pointer;

  .avatar-container {
    position: relative;
    width: $card-size;
    height: $card-size;
    overflow: hidden;

    .avatar {
      width: $card-size;
      height: $card-size;
      object-fit: cover;
      transition-duration: .25s;
    }

    &:hover .avatar {
      transition-duration: .25s;
      transform: scale(1.05);
    }
  }

  .character-info {
    width: 100%;
    padding: .75rem;
    position: absolute;
    z-index: 999;
    bottom: 0;

    .character-name {
      color: #FFFFFF;
      text-shadow: 0 0 2rem #000000;
      font-size: var(--font-size-default-p);
    }

    .character-description {
      color: #FFFFFF;
      text-shadow: 0 0 2rem #000000;
      font-size: var(--font-size-small);
      max-lines: 2;
    }
  }

  .author-name {
    color: var(--color-primary);
    font-size: var(--font-size-small);
  }

  .created-time {
    color: var(--color-secondary);
    font-size: var(--font-size-small-s);
  }

  &:hover {
    transition-duration: .25s;
    box-shadow: 0 0 .5rem 1px rgba(0, 0, 0, .2);
  }

  &:hover .character-info {
    transition-duration: .25s;
  }
}
</style>
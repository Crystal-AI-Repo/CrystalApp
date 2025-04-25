<script setup lang="ts">

import {ref, watch} from "vue";
import type {ChatCharacter} from "@/net/api/chat-character-controller.ts";
import {
  deleteChatCharacter,
  getEmptyChatCharacter,
  getMyCreatedChatCharacters
} from "@/net/api/chat-character-controller.ts";
import {formatTimestamp} from "@/utils/datetime-utils.ts";
import router from "@/router";
import {Delete, Edit} from "@element-plus/icons-vue";
import {useI18n} from "vue-i18n";
import {addChatCharacterContact} from "@/net/api/user-contact-controller.ts";
import {showSimpleDialog} from "@/utils/dialog-utils.ts";
import {DelayedAction} from "@/utils/delay-utils.ts";
import {getUserAvatarUrl} from "@/utils/url-utils.ts";
import {userProfileStore} from "@/stores/generic-kv-store.ts";

/**
 * Cache Store
 */
const userProfileCacheIds = ref<string[]>([])
const { getUserState } = userProfileStore.useCache('getUserState', userProfileCacheIds)


const { t } = useI18n()

const gridDataRefreshing = ref(false)
const currentPage = ref(1)
const totalPages = ref(1)
const totalCount = ref(0)
const currentPageData = ref<ChatCharacter[]>([])

async function refreshData() {
  gridDataRefreshing.value = true

  new DelayedAction(
      200,
      async () => {
        const result = await getMyCreatedChatCharacters(currentPage.value)
        totalPages.value = result.pages
        totalCount.value = result.total
        currentPageData.value = result.records
      },
      () => {
        gridDataRefreshing.value = false
      }
  ).start()
}

refreshData()


watch(currentPageData, () => {
  userProfileCacheIds.value = currentPageData.value.map((e: ChatCharacter) => e.authorUid)
})

function onCardClick(item: ChatCharacter) {
  router.push(`/manager/newCharacter?mode=edit&id=${item.id}`)
}

function onCardDeleteButtonClick(item: ChatCharacter) {
  showSimpleDialog(
      t,
      item.name,
      t('manager.profile.myCharacters.text.deleteMyChatCharacter',
          { name: item.name }),
      async () => {
        deleteChatCharacter(item.id).then(() => {
          refreshData()
        })
      }
  )
}

/**
 * Dialog AddChatCharacterContactVisible
 */
const dialogAddChatCharacterContactVisible = ref(false)
const dialogAddChatCharacterContactData = ref<ChatCharacter>(getEmptyChatCharacter())
const dialogAddChatCharacterContactProcessing = ref(false)

function showAddChatCharacterDialog(character: ChatCharacter) {
  dialogAddChatCharacterContactVisible.value = true
  dialogAddChatCharacterContactData.value = character
}

function confirmAddChatCharacter(character: ChatCharacter) {
  dialogAddChatCharacterContactProcessing.value = true

  addChatCharacterContact(character.id).then(() => {
    ElMessage.success(t('manager.profile.myCharacters.text.chatCharacterContactAdded', { name: character.name }))
  }).finally(() => {
    dialogAddChatCharacterContactData.value = getEmptyChatCharacter()
    dialogAddChatCharacterContactVisible.value = false
    dialogAddChatCharacterContactProcessing.value = false
  })
}
</script>

<template>
  <main>
    <el-skeleton :loading="gridDataRefreshing" animated>
      <!-- Skeleton Content -->
      <template #template>
        <div class="chat-character-grid" id="chat-character-grid">
          <div class="chat-character-card" v-for="(item, i) in [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]">
            <div class="avatar-container">
              <div class="character-info flex flex-vertical">
                <el-skeleton-item variant="text" style="width: 40%" />
                <el-skeleton-item class="mt-2" variant="text" style="width: 80%" />
              </div>

              <el-skeleton-item class="avatar" variant="image" />
            </div>

            <div class="flex flex-horizontal flex-center-vertically p-2">
              <el-skeleton-item style="--el-skeleton-circle-size: 24px; flex-shrink: 0" variant="circle" />

              <div class="flex flex-vertical ml-2" style="width: 100%">
                <el-skeleton-item variant="text" style="width: 40%" />
                <el-skeleton-item class="mt-2" variant="text" style="width: 70%" />
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Actual Context -->
      <template #default>
        <div class="chat-character-grid" id="chat-character-grid">
          <div class="chat-character-card" v-for="(item, index) in currentPageData" @click.stop="showAddChatCharacterDialog(item)">
            <div class="avatar-container">
              <div class="character-operations flex flex-horizontal">
                <el-button type="danger" :icon="Delete" circle plain @click.stop="onCardDeleteButtonClick(item)" size="default" />
                <span class="flex-grow-1" />
                <el-button type="primary" :icon="Edit" circle plain @click.stop="onCardClick(item)" size="default" />
              </div>

              <div class="character-info">
                <p class="character-name">{{ item.name }}</p>
                <p class="character-description">{{ item.description.length > 18 ? item.description.slice(0, 18) + '...' : item.description }}</p>
              </div>

              <img class="avatar" :alt="item.name" src="@/assets/main-background.png" />
            </div>

            <div class="flex flex-horizontal flex-center-vertically p-2">
              <el-avatar class="flex-shrink-0" size="small" :src="getUserAvatarUrl(item.authorUid)" />

              <div class="flex flex-vertical ml-2">
                <p class="author-name">{{ getUserState(item.authorUid).data?.nickname }}</p>
                <p class="created-time">{{ formatTimestamp(item.createdTime) }}</p>
              </div>
            </div>

          </div>
        </div>
      </template>
    </el-skeleton>


    <el-dialog v-model="dialogAddChatCharacterContactVisible" :title="dialogAddChatCharacterContactData.name" width="380px">
      <p>{{ dialogAddChatCharacterContactData.description }}</p>
      <template #footer>
        <div class="dialog-footer">
          <el-button size="default" @click="dialogAddChatCharacterContactVisible = false" :disabled="dialogAddChatCharacterContactProcessing">{{ t('dialog.cancel') }}</el-button>
          <el-button size="default" type="primary" @click="confirmAddChatCharacter(dialogAddChatCharacterContactData)" :loading="dialogAddChatCharacterContactProcessing">{{ t('dialog.confirm') }}</el-button>
        </div>
      </template>
    </el-dialog>
  </main>
</template>

<style lang="scss">
$card-size: 10vw;
$grid-gap: 1rem;
$grid-row-count: 5;

.chat-character-grid {
  width: calc($card-size * $grid-row-count + ($grid-row-count - 1) * $grid-gap);
  display: grid;
  grid-template-columns: repeat($grid-row-count, 1fr);
  gap: $grid-gap;
}

.chat-character-card {
  width: $card-size;
  min-height: $card-size;
  border-radius: .5rem;
  box-shadow: 0 0 .1rem 0 var(--color-secondary);
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

  .character-operations {
    width: 100%;
    position: absolute;
    padding: .75rem;
    z-index: 999;
    top: 0;
    opacity: 0;
    transition-duration: .25s;
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
    box-shadow: 0 0 .25rem 0 var(--color-primary);
  }

  &:hover .character-info {
    transition-duration: .25s;
  }

  &:hover .character-operations {
    opacity: 1;
  }
}
</style>
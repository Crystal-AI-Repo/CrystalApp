<script setup lang="ts">
import {Top} from "@element-plus/icons-vue";
import {ref, watch} from "vue";
import type {ChatHistoryMessage} from "@/net/api/history-message-controller.ts";
import {getChatHistoryLeaves, getChatHistoryUpwards} from "@/net/api/history-message-controller.ts";
import {useRoute} from "vue-router";
import router from "@/router";
import {sendMessageToContact} from "@/net/api/user-contact-controller.ts";
import type {OpenAIStreamChunk} from "@/data/open-api.ts";
import {getCharacterAvatarUrl, getUserAvatarUrl} from "@/utils/url-utils.ts";
import {getUserAuthToken} from "@/utils/auth-utils.ts";
import {chatCharacterStore, userProfileStore} from "@/stores/generic-kv-store.ts";

const route = useRoute()
const contactId = ref(route.params.contactId as string)
watch(router.currentRoute, () => {
  contactId.value = route.params.contactId as string
})

const messageInputRef = ref(null)
const inputMessage = ref('')

const sendMessageButtonBlocking = ref(true)

/**
 * Events
 */
const adjustMessageInputHeight = () => {
  const el = messageInputRef.value;
  el.style.height = 'auto';
  el.style.height = `${el.scrollHeight}px`;
}

const messageInputEvent = () => {
  adjustMessageInputHeight()

  sendMessageButtonBlocking.value = inputMessage.value.length <= 0
}

/**
 * Chat History
 */
const renderingChatHistory = ref<ChatHistoryMessage[]>([])

function getCurrentReplyingMessage(): ChatHistoryMessage | null {
  if (renderingChatHistory.value.length == 0) {
    return null
  }

  return renderingChatHistory.value[renderingChatHistory.value.length - 1]
}

function addRenderingUserMessage(msg: string) {
  const uid = getUserAuthToken()?.payloads.uid ?? '0'
  renderingChatHistory.value.push({
    id: "0",
    childrenSize: 0,
    createdTime: new Date().getTime(),
    message: msg,
    messageType: 0,
    revoked: false,
    sender: uid,
    senderType: 1
  })
}

function onNewMessageStreamReceived(pack: OpenAIStreamChunk) {
  const existing: ChatHistoryMessage | undefined = renderingChatHistory.value.find((e: ChatHistoryMessage) => e.id == pack.id)
  if (existing == undefined) {
    renderingChatHistory.value.push({
      id: pack.id,
      childrenSize: 0,
      createdTime: pack.created,
      message: pack.choices[0].delta.content,
      messageType: 0,
      revoked: false,
      sender: contactId.value,
      senderType: 0
    })
  } else {
    existing.messageType = 0
    existing.message = existing.message + pack.choices[0].delta.content
  }

}

const chatHistoryTree = ref<ChatHistoryMessage>({
  childrenSize: 0,
  createdTime: 0,
  id: "",
  message: "",
  messageType: 0,
  revoked: false,
  sender: "",
  senderType: 0
})

const chatHistoryLeaves = ref<ChatHistoryMessage[]>([])
const selectedLeafNode = ref<ChatHistoryMessage>()

const contactIdChangeEvent = () => {
  getChatHistoryLeaves(contactId.value).then((res) => {
    chatHistoryLeaves.value = res.data.sort((a, b) => a.createdTime - b.createdTime)
    // Select the latest leaf
    const selected: ChatHistoryMessage = chatHistoryLeaves.value[chatHistoryLeaves.value.length - 1]
    renderingChatHistory.value = [selected]
    selectedLeafNode.value = selected
  })
}

watch(contactId, () => {
  contactIdChangeEvent()
})

function loadChatHistoryUpwards() {
  if (renderingChatHistory.value.length == 0) {
    return
  }

  getChatHistoryUpwards(contactId.value, renderingChatHistory.value[0].id).then((res) => {
    const data = res.data.filter(e => e.messageType != 0)
    renderingChatHistory.value = [...data.reverse(), ...renderingChatHistory.value]

    data.filter(e => e.senderType == 1).map((e: ChatHistoryMessage) => e.sender).forEach(e => {
      cacheStore.getUserProfileByUid(e)
    })
  })
}

/**
 * Send Message
 */
async function sendMessage() {
  if (sendMessageButtonBlocking.value) {
    return
  }

  const replyingMessage = getCurrentReplyingMessage()
  if (replyingMessage == null) {
    ElMessage.error("Could not find header of history messages")
    return
  }

  sendMessageButtonBlocking.value = true

  addRenderingUserMessage(inputMessage.value)

  let firstPackReceived = false
  for await (const pack of sendMessageToContact(contactId.value, replyingMessage.id, inputMessage.value)) {
    if (!firstPackReceived) {
      firstPackReceived = true
      inputMessage.value = ''
      sendMessageButtonBlocking.value = false
    }

    onNewMessageStreamReceived(pack)
  }

}

contactIdChangeEvent()

/**
 * Cache Store
 */
const chatCharacterCacheIds = ref<string[]>([])
const { getChatCharacterState } = chatCharacterStore.useCache('getChatCharacterState', chatCharacterCacheIds)

const userProfileCacheIds = ref<string[]>([])
const { getUserState } = userProfileStore.useCache('getUserState', userProfileCacheIds)

watch(renderingChatHistory, () => {
  chatCharacterCacheIds.value = renderingChatHistory.value
      .filter((e: ChatHistoryMessage) => e.senderType == 0 && e.messageType != 0)
      .map((e: ChatHistoryMessage) => e.sender)

  userProfileCacheIds.value = renderingChatHistory.value
      .filter((e: ChatHistoryMessage) => e.senderType == 1 && e.messageType != 0)
      .map((e: ChatHistoryMessage) => e.sender)
})
</script>

<template>
  <main class="width-100 height-100 flex flex-vertical overflow-y-scroll">
    <div class="input-area-container flex flex-vertical">
      <textarea ref="messageInputRef" class="input" v-model="inputMessage" rows="1" @input="messageInputEvent" />
      <div class="flex flex-horizontal mt-4">
        <span class="flex-grow-1" />
        <el-button
            size="default"
            type="primary"
            :icon="Top"
            circle
            :disabled="sendMessageButtonBlocking"
            @click="sendMessage"
        />
      </div>
    </div>

    <div><el-button @click="loadChatHistoryUpwards()">Load More</el-button></div>
    <div class="chat-container flex-grow-1 flex flex-vertical">
      <div
          class="chat-message-container flex flex-horizontal gap-2"
          :class="{
            'flex-horizontal-reversed': message.senderType == 1,
            'flex-align-self-end': message.senderType == 1
          }"
          v-for="(message, index) in renderingChatHistory as ChatHistoryMessage[]"
      >
        <el-avatar
            class="flex-shrink-0"
            size="default"
            :src="message.senderType == 0 ? getCharacterAvatarUrl(message.sender) : message.senderType == 1 ? getUserAvatarUrl(getUserState(message.sender).data?.id ?? '0') : ''"
        />

        <div class="flex flex-vertical">
          <p :class="{'text-align-right': message.senderType == 1}">
            {{ message.senderType == 0 ? getChatCharacterState(message.sender).data?.name ?? 'LOADING' : message.senderType == 1 ? getUserState(message.sender).data?.nickname ?? '' : '' }}
          </p>
          <div class="message-container">
            <p class="message">{{ message.message }}</p>
          </div>
        </div>
      </div>

      <div style="height: 16rem" />
    </div>
  </main>
</template>

<style scoped>
.chat-container {
  padding: 2rem;
  min-width: 40vw;
  margin: auto;
}

.chat-message-container {
  max-width: 30vw;
  margin-bottom: 1rem;

  .message-container {
    margin-top: .5rem;
    background: #fff;
    padding: .5rem;
    box-shadow: 0 0 .25rem 0 rgba(0, 0, 0, .15);
    border-radius: .5rem;

    .message {
      font-size: var(--font-size-default);
    }
  }
}

.input-area-container {
  position: absolute;
  bottom: 0;
  align-self: center;
  width: 35vw;
  min-width: 300px;
  margin: 2rem;
  background: #f5f5f5;
  padding: 1rem;
  border-radius: .5rem;
  box-shadow: 0 0 1rem 0 rgba(0, 0, 0, .05);
  z-index: 999;

  .input {
    width: 100%;
    height: auto;
    max-height: 12rem;
    line-height: 1.5rem;
    resize: none;
    overflow-y: auto;
    box-sizing: border-box;
  }
}
</style>
<script setup lang="ts">
import {Top} from "@element-plus/icons-vue";
import {ref, watch} from "vue";
import type {ChatHistoryMessage, LocalChatHistoryMessage} from "@/net/api/history-message-controller.ts";
import {getChatHistoryLeaves, getChatHistoryUpwards} from "@/net/api/history-message-controller.ts";
import {useRoute} from "vue-router";
import router from "@/router";

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

watch(contactId, () => {
  getChatHistoryLeaves(contactId.value).then((res) => {
    chatHistoryLeaves.value = res.data.sort((a, b) => a.createdTime - b.createdTime)
    // Select the latest leaf
    const selected: ChatHistoryMessage = chatHistoryLeaves.value[chatHistoryLeaves.value.length - 1]
    renderingChatHistory.value = [selected]
    selectedLeafNode.value = selected
  })
})

function loadChatHistoryUpwards() {
  if (renderingChatHistory.value.length == 0) {
    return
  }

  getChatHistoryUpwards(contactId.value, renderingChatHistory.value[0].id).then((res) => {
    renderingChatHistory.value = [...res.data.reverse(), ...renderingChatHistory.value]
  })
}

/**
 * Send Message
 */
function sendMessage() {
  if (sendMessageButtonBlocking.value) {
    return
  }

  inputMessage.value = ''
}
</script>

<template>
  <main class="width-100 height-100 flex flex-vertical">
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
        <el-avatar class="flex-shrink-0" size="default" />

        <div class="flex flex-vertical">
          <p :class="{'text-align-right': message.senderType == 1}">{{ message.sender }}</p>
          <div class="message-container">
            <p class="message">{{ message.message }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area-container flex flex-vertical">
      <textarea ref="messageInputRef" class="input" v-model="inputMessage" rows="1" @input="messageInputEvent" />
      <div class="flex flex-horizontal mt-4">
        <span class="flex-grow-1" />
        <el-button size="default" type="primary" :icon="Top" circle :disabled="sendMessageButtonBlocking" @click="sendMessage" />
      </div>
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
  max-width: 20vw;
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
  align-self: center;
  width: 35vw;
  min-width: 300px;
  margin: 2rem;
  background: rgba(0, 0, 0, .05);
  padding: 1rem;
  border-radius: .5rem;
  box-shadow: 0 0 1rem 0 rgba(0, 0, 0, .05);

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
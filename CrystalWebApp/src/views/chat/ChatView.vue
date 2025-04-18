<script setup lang="ts">
import {Top} from "@element-plus/icons-vue";
import {ref} from "vue";
import type {ChatHistoryMessage} from "@/net/api/contact-message-controller.ts";


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
const chatHistory = ref<ChatHistoryMessage[]>([
  {
    id: 1,
    senderType: 0,
    sender: 1,
    messageType: 0,
    message: 'Hello! How can I help you today? : )',
    createdTime: 1800,
    revoked: false
  },
  {
    id: 2,
    senderType: 1,
    sender: 1,
    messageType: 0,
    message: 'Introduce yourself plz',
    createdTime: 2000,
    revoked: false
  }
])

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
    <div class="chat-container flex-grow-1 flex flex-vertical">
      <div
          class="chat-message-container flex flex-horizontal gap-2"
          :class="{
            'flex-horizontal-reversed': message.senderType == 1,
            'flex-align-self-end': message.senderType == 1
          }"
          v-for="(message, index) in chatHistory as ChatHistoryMessage[]"
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
<script setup lang="ts">

import type {UserContactVO} from "@/net/api/user-contact-controller.ts";
import {Delete} from "@element-plus/icons-vue";

const props = defineProps<{
  contact: UserContactVO,
  customOperationButtons?: boolean,
  showOperationButtons?: boolean,
  componentStyle?: 'card' | 'plain',
  isActive?: boolean,
  clickEvent?: () => void
}>()

</script>

<template>
  <div
      class="contact-card-wrapper flex flex-horizontal flex-center-vertically"
      v-if="props.componentStyle == 'card'"
      @click="() => props.clickEvent()"
  >
    <div class="contact-card flex flex-horizontal flex-center-vertically" v-if="props.contact.contact.contactType == 0">
      <el-avatar class="flex-shrink-0" size="large" />
      <div class="width-100 flex flex-vertical ml-4">
        <p class="contact-name">{{ props.contact.reifiedContact.name }}</p>
        <p class="contact-msg">{{ props.contact.reifiedContact.greetingMessage }}</p>
      </div>
    </div>

    <span class="flex-grow-1" />

    <!-- Operation Buttons -->
    <div class="operations" v-if="props.showOperationButtons">
      <slot name="operations" v-if="props.customOperationButtons" />
      <el-button type="danger" :icon="Delete" circle size="default" plain />
    </div>
  </div>

  <div
      :class="{'contact-plain-card-wrapper--active': props.isActive}"
      class="contact-plain-card-wrapper flex flex-horizontal flex-center-vertically"
      v-if="props.componentStyle == 'plain'"
      @click="() => props.clickEvent()"
  >
    <div class="contact-plain-card flex flex-horizontal flex-center-vertically" v-if="props.contact.contact.contactType == 0">
      <el-avatar class="flex-shrink-0" size="default" />
      <div class="width-100 flex flex-vertical ml-4">
        <!-- Row 1 -->
        <div class="flex flex-horizontal flex-center-vertically">
          <p class="contact-name">{{ props.contact.reifiedContact.name }}</p>
          <span class="flex-grow-1" />
          <p class="last-message-time">2023/11/08</p>
        </div>
        <!-- Row 2 -->
        <div class="flex flex-horizontal flex-center-vertically">
          <p class="contact-msg">{{ props.contact.reifiedContact.greetingMessage }}</p>
          <span class="flex-grow-1" />
          <span class="unread-message">99+</span>
        </div>
      </div>
    </div>

    <span class="flex-grow-1" />

    <!-- Operation Buttons -->
    <div class="operations" v-if="props.showOperationButtons">
      <slot name="operations" v-if="props.customOperationButtons" />
      <el-button type="danger" :icon="Delete" circle size="default" plain />
    </div>
  </div>
</template>

<style scoped>
.contact-plain-card-wrapper {
  width: 100%;
  padding: 1rem;
  transition-duration: .25s;
  cursor: pointer;

  .operations {
    transition-duration: .25s;
    opacity: 0;
  }

  &:hover {
    transition-duration: .25s;
    background: rgba(0, 0, 0, .05);
  }

  &:hover .operations {
    opacity: 1;
  }
}

.contact-plain-card-wrapper--active {
  background: var(--color-secondary-container);
  transition-duration: .25s;

  &:hover {
    transition-duration: .25s;
    background: var(--color-primary-container);
  }

  .contact-plain-card {
    .contact-name {
      color: var(--color-on-primary-container);
    }

    .contact-msg {
      color: var(--color-on-primary-container);
    }

    .last-message-time {
      color: var(--color-on-primary-container);
    }
  }

  &:hover .contact-plain-card {
    .contact-name {
      color: var(--color-on-secondary-container);
    }

    .contact-msg {
      color: var(--color-on-secondary-container);
    }

    .last-message-time {
      color: var(--color-on-secondary-container);
    }
  }
}

.contact-plain-card {
  width: 100%;

  .contact-name  {
    font-size: var(--font-size-small);
    color: var(--color-primary);
  }

  .contact-msg {
    height: var(--font-size-small-s);
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: var(--font-size-small-s);
    font-size: var(--font-size-small-s);
    color: var(--color-secondary);
  }

  .last-message-time {
    font-size: var(--font-size-small-s);
    color: var(--color-secondary);
  }

  .unread-message {
    font-size: var(--font-size-tiny-p);
    background: var(--color-error-container);
    color: var(--color-on-error-container);
    padding: .1rem .15rem;
    border-radius: .5rem;
  }
}
</style>

<style scoped>
.contact-card-wrapper {
  width: 100%;
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
  width: 100%;

  .contact-name {
    font-size: var(--font-size-default);
    color: var(--color-primary);
  }

  .contact-msg {
    overflow: hidden;
    text-overflow: ellipsis;
    height: calc(var(--font-size-small-s) * 2);
    line-height: calc(var(--font-size-small-s) * 2);
    font-size: var(--font-size-small-s);
    color: var(--color-secondary);
  }
}
</style>
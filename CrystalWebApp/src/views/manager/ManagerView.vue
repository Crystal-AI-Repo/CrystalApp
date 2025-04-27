<script setup lang="ts">

import HeaderComponent from "@/components/HeaderComponent.vue";
import {ref, watch} from "vue";
import router from "@/router";
import {useI18n} from "vue-i18n";
import {startWith} from "@/utils/string-utils.ts";

const { t } = useI18n()

const currentPath = ref(location.pathname)
watch(router.currentRoute, () => {
  currentPath.value = location.pathname
})

interface SideMenuItem {
  displayName: string,
  translate: boolean,
  path: string
}

const items = ref<SideMenuItem[]>([
  {
    displayName: 'manager.sideNav.profile',
    translate: true,
    path: '/manager/profile'
  },
  {
    displayName: 'manager.sideNav.newCharacter',
    translate: true,
    path: '/manager/newCharacter'
  },
  {
    displayName: 'manager.sideNav.models',
    translate: true,
    path: '/manager/models'
  },
])

function jumpTo(path: string) {
  currentPath.value = path
  router.push(path)
}
</script>

<template>
  <main class="flex">
    <HeaderComponent />

    <div class="main-container flex-horizontal gap-8">
      <nav class="nav">
        <div
            v-for="(item, index) in items"
            :class="{
              'nav__item': true,
              'nav__item--active': startWith(currentPath, item.path)
            }"
            @click="jumpTo(item.path)">
          {{ item.translate ? t(item.displayName) : item.displayName }}<span class="flex-grow-1" />>
        </div>
      </nav>

      <RouterView />
    </div>

  </main>
</template>

<style scoped>
.main-container {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
  padding-left: 15%;
  padding-right: 15%;
  padding-top: 128px;
  padding-bottom: 128px;

  .nav {
    min-width: 256px;
    .nav__item {
      border-radius: 1rem;
      padding: .5rem 1rem;
      transition-duration: .25s;
      display: flex;
      flex-direction: row;
      align-items: center;
      margin-bottom: .25rem;
      cursor: pointer;
      user-select: none;

      .icon {
        width: 24px;
        height: 24px;
        margin-right: .5rem;
      }

      &:hover {
        transition-duration: .25s;
        background: rgba(0, 0, 0, .1);
      }
    }

    .nav__item--active {
      color: rgba(77, 123, 215, 1);
      background: rgba(227, 237, 253, 1);

      &:hover {
        color: rgba(77, 123, 215, 1)!important;
        background: rgba(227, 237, 253, 1)!important;
      }
    }
  }
}
</style>
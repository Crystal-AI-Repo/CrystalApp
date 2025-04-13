<script setup lang="ts">
import {ref, watch} from "vue";
import {getCenterColors, toHexColor} from "@/utils/color-utils.ts";
import {useI18n} from "vue-i18n";
import router from "@/router";
import HeaderComponent from "@/components/HeaderComponent.vue";
import TabComponent, {TabItem} from "@/components/TabComponent.vue";

const { t } = useI18n()

const currentPath = ref(location.pathname)
watch(router.currentRoute, () => {
  currentPath.value = location.pathname
})

const subtitle = ref<string>("Your next choice for your characters!")
const subtitleCharColors = ref<number[][]>([])

const fxUpdateSubtitleCharColors = () => {
  subtitleCharColors.value = getCenterColors([243, 202, 202], [161, 202, 251], subtitle.value.length)
}

watch(subtitle, () => {
  fxUpdateSubtitleCharColors()
})

fxUpdateSubtitleCharColors()

// setInterval(() => {
//   subtitleCharColors.value = moveRight(subtitleCharColors.value, 1)
// }, 50)

const tabItems = ref<TabItem[]>([
  {
    name: 'home.tab.commendations',
    translate: true,
    path: '/explore'
  },
  {
    name: 'home.tab.latest',
    translate: true,
    path: '/latest'
  }
])

</script>

<template>
  <main>
    <HeaderComponent />

    <div class="main-background flex flex-vertical flex-center">
      <p class="title">Crystal AI Chat</p>
      <p class="subtitle flex-center-horizontally">
        <span
            v-for="(char, i) in Array.from(subtitle)"
            class="subtitle__character"
            :style="'color: #' + toHexColor(subtitleCharColors[i])"
        >
          {{ char }}
        </span>
      </p>
    </div>

    <TabComponent :items="tabItems" :current-path="currentPath" :align="1" size="large" />

    <RouterView />
  </main>
</template>

<style scoped>
.main-background {
  width: 100%;
  height: 85vh;
  background: url("@/assets/main-background.png") no-repeat center;
  background-size: cover;

  .title {
    font-size: 3rem;
    font-weight: bold;
    color: #fff;
    text-shadow: 0 0 1rem #aaa;
    text-align: center;
  }

  .subtitle {
    font-size: 1.75rem;
    font-weight: bold;
    text-align: center;

    .subtitle__character {
      text-shadow: 0 0 .5rem #AAAAAA50;
    }
  }
}
</style>

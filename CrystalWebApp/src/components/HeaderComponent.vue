<script setup lang="ts">

import {ArrowDown, Search} from "@element-plus/icons-vue";
import {useI18n} from "vue-i18n";
import {ref} from "vue";
import router from "@/router";
import {clearUserAuthToken, getUserAuthToken, getUserToken} from "@/utils/auth-utils.ts";
import {getUserAvatarUrl} from "@/utils/url-utils.ts";

const serverBaseUrl = import.meta.env.VITE_SERVER_BASE_URL

const { t } = useI18n()

const currentPath = location.pathname

interface HeaderItem {
  displayName: string,
  translate: boolean,
  path: string
}

const items = ref<HeaderItem[]>([
  {
    displayName: 'home.header.nav.category',
    translate: true,
    path: '/category'
  },
  {
    displayName: 'home.header.nav.author',
    translate: true,
    path: '/author'
  },
  {
    displayName: 'home.header.nav.rank',
    translate: true,
    path: '/rank'
  }
])

function logout() {
  clearUserAuthToken()
  location.reload()
}
</script>

<template>
  <main class="header-wrapper">
    <div class="header flex flex-horizontal flex-center-vertically flex-center-horizontally">
      <div class="logo" @click="router.push('/')">CRYSTAL</div>
      <div class="menu flex flex-horizontal">
        <div
            v-for="(item, index) in items"
            :class="{
              'menu__item': true,
              'menu__item--active': currentPath == item.path
            }"
            :key="index"
            @click="router.push(item.path)"
        >
          {{ item.translate ? t(item.displayName) : item.displayName }}
        </div>
      </div>

      <div class="search-box flex flex-horizontal flex-center-vertically ml-4">
        <Search class="prefix-icon" />
        <input class="ml-2" />
      </div>

      <el-popover
          placement="bottom"
          style="padding: 0"
      >
        <template #reference>
          <div class="profile-box flex flex-horizontal flex-center-vertically ml-4">
            <el-avatar :size="24" :src="getUserAvatarUrl(getUserAuthToken()?.payloads.uid ?? '0')" class="flex-shrink-0" />
            <ArrowDown class="icon ml-2" />
          </div>
        </template>
        <template #default>
          <div class="avatar-float-container">
            <div class="nav">
              <div class="nav__item" @click="router.push('/manager/profile')">
                {{ t('home.header.avatar.nav.profile') }}
              </div>
              <div class="nav__item" @click="logout()">
                {{ t('home.header.avatar.nav.logout') }}
              </div>
            </div>
          </div>
        </template>
      </el-popover>

    </div>
  </main>
</template>

<style scoped lang="scss">
.avatar-float-container {
  padding: .25rem 0;
  background: #fff;

  .nav {
    .nav__item {
      padding: .5rem;
      transition-duration: .25s;
      cursor: pointer;
      user-select: none;

      &:hover {
        background: rgba(0, 0, 0, .1);
        transition-duration: .25s;
      }
    }
  }
}
</style>

<style>
.el-popover {
  padding: 0!important;
}
</style>

<style scoped lang="scss">
.header-wrapper {
  width: 100%;
  height: 72px;
  position: absolute;
  z-index: 999;
  background: rgba(255, 255, 255, .75);
  backdrop-filter: blur(1rem);
  box-shadow: 0 0 .25rem 0 rgba(0, 0, 0, .15);
}

.header {
  height: inherit;

  .logo {
    width: 100px;
    height: 30px;
    line-height: 30px;
    text-align: center;
    cursor: pointer;
    user-select: none;
  }

  .search-box {
    width: 40%;
    height: 40px;
    background: #fff;
    border-radius: 20px;
    box-sizing: border-box;
    padding: .1rem 1rem;
    border: 1px solid #80808050;

    .prefix-icon {
      width: 18px;
      height: 18px;
    }

    input {
      width: 100%;
    }
  }

  .profile-box {
    width: 64px;
    height: 32px;
    background: #fff;
    border-radius: 16px;
    padding: 0 .5rem;

    .icon {
      width: 16px;
      height: 16px;
    }
  }
}

.menu {
  .menu__item {
    padding: 10px 6px;
    color: #222;
    cursor: pointer;
    user-select: none;
    font-size: 1rem;
  }

  .menu__item--active {
    color: #3a9fff;
  }
}


</style>
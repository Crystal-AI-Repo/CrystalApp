import {createRouter, createWebHistory} from 'vue-router'
import HomeView from "@/views/home/HomeView.vue";
import ConsentConfirmView from "@/views/auth/ConsentConfirmView.vue";
import HomeExploreView from "@/views/home/HomeExploreView.vue";
import HomeLatestView from "@/views/home/HomeLatestView.vue";
import MainRankView from "@/views/main/MainRankView.vue";
import MainCategoryView from "@/views/main/MainCategoryView.vue";
import MainAuthorView from "@/views/main/MainAuthorView.vue";
import {getUserAuthToken} from "@/utils/auth-utils.ts";
import ErrorView from "@/views/ErrorView.vue";
import ManagerView from "@/views/manager/ManagerView.vue";
import ProfileView from "@/views/manager/profile/ProfileView.vue";
import NewRoleView from "@/views/manager/CharacterEditView.vue";
import ModelManagerView from "@/views/manager/ModelManagerView.vue";
import ProfileMyChatsView from "@/views/manager/profile/ProfileMyChatsView.vue";
import ProfileMyCharactersView from "@/views/manager/profile/ProfileMyCharactersView.vue";
import ChatContainerView from "@/views/chat/ChatContainerView.vue";
import ChatView from "@/views/chat/ChatView.vue";
import ChatEmptyView from "@/views/chat/ChatEmptyView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: HomeView,
      redirect: '/explore',
      children: [
        {
          path: "explore",
          name: "main-explore",
          component: HomeExploreView
        },
        {
          path: "latest",
          name: "main-latest",
          component: HomeLatestView
        }
      ]
    },
    {
      path: "/category",
      name: "category",
      component: MainCategoryView
    },
    {
      path: "/author",
      name: "author",
      component: MainAuthorView
    },
    {
      path: "/rank",
      name: "rank",
      component: MainRankView
    },
    {
      path: '/chat',
      name: 'chat',
      component: ChatContainerView,
      children: [
        {
          path: '',
          name: 'chat-empty',
          component: ChatEmptyView
        },
        {
          path: ':contactId',
          name: 'chat-contact',
          component: ChatView
        }
      ]
    },
    {
      path: "/manager",
      name: "manager",
      component: ManagerView,
      children: [
        {
          path: 'profile',
          name: 'manager-profile',
          component: ProfileView,
          children: [
            {
              path: '',
              name: 'manager-profile-my-chats',
              component: ProfileMyChatsView
            },
            {
              path: 'myCharacters',
              name: 'manager-profile-my-characters',
              component: ProfileMyCharactersView
            }
          ]
        },
        {
          path: 'newCharacter',
          name: 'manager-newCharacter',
          component: NewRoleView
        },
        {
          path: 'models',
          name: 'manager-models',
          component: ModelManagerView
        },
      ]
    },
    {
      path: '/auth/consent',
      name: 'consent-confirm',
      component: ConsentConfirmView
    },
    {
      path: '/error',
      name: 'error',
      component: ErrorView
    }
  ],
})

router.beforeEach((to, from) => {
  if (to.name == 'consent-confirm') {
    return true
  }

  if (getUserAuthToken() == null) {
    localStorage.setItem("after_auth", location.href)
    location.href = 'http://localhost:6173/login'
    return { name: 'error' }
  }

  return true
})

export default router

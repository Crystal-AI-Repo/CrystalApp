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

import {createRouter, createWebHistory} from 'vue-router'
import HomeView from "@/views/HomeView.vue";
import ConsentConfirmView from "@/views/auth/ConsentConfirmView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: HomeView,
      children: []
    },
    {
      path: '/auth/consent',
      name: 'consent-confirm',
      component: ConsentConfirmView
    }
  ],
})

export default router

import {createRouter, createWebHistory} from 'vue-router'
import HomeView from "@/views/AuthorizationContainer.vue";
import LoginView from "@/views/LoginView.vue";
import RegisterView from "@/views/RegisterView.vue";
import ConsentView from "@/views/ConsentView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'main',
      component: HomeView,
      children: [
        {
          path: 'login',
          name: 'main-login',
          component: LoginView
        },
        {
          path: 'register',
          name: 'main-register',
          component: RegisterView
        },
        {
          path: 'consent',
          name: 'main-consent',
          component: ConsentView
        }
      ]
    },
  ],
})

export default router

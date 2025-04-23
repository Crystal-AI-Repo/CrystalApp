import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  build: {
    outDir: '../src/main/resources/static'
  },
  plugins: [
    vue(),
    vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "@/assets/mixin.scss";'
      }
    }
  },
  server: {
    proxy: {
      '/api': {
        target: "http://127.0.0.1:8080",
        changeOrigin: true,
        ws: true,
        rewrite: (path) => path.replace(/^\/api\//, '')
      },'/d-api': {
        target: "http://127.0.0.1:5210",
        changeOrigin: true,
        ws: true,
        rewrite: (path) => path.replace(/^\/d-api\//, '')
      }
    }
  }
})

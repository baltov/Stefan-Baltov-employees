import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [vue(), tailwindcss()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:9090", // Your Spring Boot backend URL
        changeOrigin: true,
      },
      "/sse": {
        target: "http://localhost:9090", // Your Spring Boot backend URL
        changeOrigin: true,
      },
    },
  },

})

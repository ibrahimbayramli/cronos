import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  base: '/cronos/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/cronos/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})

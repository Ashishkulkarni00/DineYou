import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import basicSsl from '@vitejs/plugin-basic-ssl';

export default defineConfig({
  build: { target: 'es2019' },
  server: {
    hmr: false,
    host: true,
    port: 5173,
    allowedHosts: [
      'norm-comfort-backed-solid.trycloudflare.com',
      '192.168.1.7'
    ]
  },
  plugins: [
    react(),
    tailwindcss(),
    basicSsl()
  ],
})

import {defineConfig} from 'vite';
import tailwindcss from '@tailwindcss/vite';

const backendServer = {
        "target": "http://localhost:8080",
        "secure": false,
        "logLevel": "debug",
        "changeOrigin": true
      }

export default defineConfig({
  plugins: [
    tailwindcss()
  ],
  build: {
    outDir: './dist/public',
  },
  server: {
    proxy: {
      "/api": backendServer
    }
  }
});

import {defineConfig} from 'vite';
import tailwindcss from '@tailwindcss/vite';

const backendServer = {
        "target": "http://localhost:9050",
        "secure": false,
        "logLevel": "debug",
        "changeOrigin": true
      }

export default defineConfig({
  plugins: [
    tailwindcss()
  ],
  build: {},
  server: {
    proxy: {
      "/grpc": backendServer,
      "/dev": backendServer
    }
  }
});

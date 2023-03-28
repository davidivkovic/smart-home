import { sveltekit } from '@sveltejs/kit/vite'
import path from 'path';
import { defineConfig } from 'vite'
import Icons from 'unplugin-icons/vite'

export default defineConfig({
  plugins: [sveltekit(), Icons({ compiler: 'svelte' })],
  resolve: {
    alias: {
      '@': path.resolve('./src')
    }
  }
})

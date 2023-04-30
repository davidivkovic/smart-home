import { writable, get } from 'svelte/store'

export const currentPage = writable(1)
export const nextPage = () => currentPage.update((n) => n + 1)
export const firstPage = () => currentPage.set(1)

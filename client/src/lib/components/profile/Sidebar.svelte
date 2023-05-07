<script>
  import { page } from '$app/stores'

  const tabs = [
    { name: 'Settings', route: '/profile' },
    { name: 'Your properties', route: '/profile/properties' }
  ]
</script>

<nav class="flex h-full w-40 flex-col">
  {#each tabs as tab}
    <a
      href={tab.route}
      class={`px-4 py-3 ${
        $page.url.pathname == tab.route ? 'border-l-2 border-l-black font-medium bg-[#eaeaea]' : ''
      }
        text-sm transition hover:bg-[#eaeaea]`}>{tab.name}</a
    >
  {/each}
</nav>

<script>
  import Container from './Container.svelte'
  import userStore, { user, isAdmin } from '$lib/stores/userStore'
  import { headerHeight } from '$lib/stores/appStore'
  import { page } from '$app/stores'
</script>

<div class="w-full flex flex-col items-center justify-center" bind:clientHeight={$headerHeight}>
  <Container>
    <div class="flex pt-8">
      <a
        href="/certificates"
        class="text-sm font-medium underline-offset-2"
        class:underline={$page.url.pathname === '/certificates'}
      >
        Certificates
      </a>
      <a
        href="/csrs"
        class="ml-4 text-sm font-medium underline-offset-2"
        class:underline={$page.url.pathname === '/csrs'}
      >
        Signing Requests
      </a>
      {#if $isAdmin}
        <a
          href="/users"
          class="ml-4 text-sm font-medium underline-offset-2"
          class:underline={$page.url.pathname === '/users'}
        >
          Users
        </a>
      {/if}
      <a
        href="/buildings"
        class="ml-4 text-sm font-medium underline-offset-2"
        class:underline={$page.url.pathname === '/buildings'}
      >
        Buildings
      </a>
      {#if $isAdmin}
      <a
        href="/logs"
        class="ml-4 text-sm font-medium underline-offset-2"
        class:underline={$page.url.pathname === '/logs'}
      >
        Logs
      </a>
      {/if}
      <a
        href="/profile"
        class="ml-auto mr-1.5 text-sm"
        class:underline={$page.url.pathname === '/profile'}
      >
        ({$user.firstName}
        {$user.lastName})
      </a>
      <button type="button" class="p-0 text-sm font-medium" on:click={userStore.logout}>
        Log out
      </button>
    </div>
  </Container>
</div>

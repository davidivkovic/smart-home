<script>
  import Container from './Container.svelte'
  import userStore, { user, isAdmin } from '$lib/stores/userStore'
  import { page } from '$app/stores'
</script>

<div class="sticky h-16 w-full justify-center">
  <Container>
    <div class="flex">
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

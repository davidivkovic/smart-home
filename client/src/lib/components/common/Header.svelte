<script>
  import Container from './Container.svelte'
  import userStore from '$lib/stores/userStore'
</script>

<div class="sticky h-20 w-full justify-center">
  <Container>
    <button class="secondary" on:click={userStore.logout}>Log out</button>
  </Container>
</div>

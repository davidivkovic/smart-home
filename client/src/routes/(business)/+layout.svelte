<script>
  import Container from '$lib/components/common/Container.svelte'
  import Header from '$lib/components/common/Header.svelte'
  import DialogOutlet from '$lib/components/UI/dialog/DialogOutlet.svelte'
  import NotificationOutlet from '$lib/components/UI/notification/NotificationOutlet.svelte'
</script>

<main class="flex min-h-screen flex-col">
  <!-- <Header /> -->
  <Container>
    <NotificationOutlet />
    <DialogOutlet />
    <slot />
  </Container>
</main>

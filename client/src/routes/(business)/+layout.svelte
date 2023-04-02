<script>
  import Container from '$lib/components/common/Container.svelte'
  import Header from '$lib/components/common/Header.svelte'
  import DialogOutlet from '$lib/components/UI/dialog/DialogOutlet.svelte'
  import NotificationOutlet from '$lib/components/UI/notification/NotificationOutlet.svelte'
</script>

<div class="flex min-h-screen w-full flex-col">
  <Header />
  <Container>
    <slot />
  </Container>
</div>

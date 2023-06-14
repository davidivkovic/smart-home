<script>
  import Container from '$lib/components/common/Container.svelte'
  import Header from '$lib/components/common/Header.svelte'
</script>

<div class="flex  w-full flex-col">
  <Header />
  <Container>
    <slot />
  </Container>
</div>

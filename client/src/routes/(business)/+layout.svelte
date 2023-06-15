<script>
  import Container from '$lib/components/common/Container.svelte'
  import Header from '$lib/components/common/Header.svelte'
</script>

<div class="flex flex-1 w-full flex-col">
  <Header />
  <Container>
    <slot />
  </Container>
</div>

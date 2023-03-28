<script>
  import '../app.css'
  import '../fonts.css'
  
  import Container from '../components/common/Container.svelte'
  import Header from '../components/common/Header.svelte'
  import DialogOutlet from '../components/UI/dialog/DialogOutlet.svelte'
  import NotificationOutlet from '../components/UI/notification/NotificationOutlet.svelte'
</script>

<main class="flex min-h-screen flex-col">
  <Header />
  <Container>
    <NotificationOutlet />
    <DialogOutlet />
    <slot />
  </Container>
</main>

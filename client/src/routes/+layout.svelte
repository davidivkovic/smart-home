<script>
  import '@/app.css'
  import '@/fonts.css'
  import DialogOutlet from '$lib/components/UI/dialog/DialogOutlet.svelte'
  import NotificationOutlet from '$lib/components/UI/notification/NotificationOutlet.svelte'
</script>

<main class="flex min-h-screen items-center justify-center">
  <slot />
</main>

<NotificationOutlet/>
<DialogOutlet/>

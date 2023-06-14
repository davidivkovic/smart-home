<script>
  import '@/app.css'
  import '@/fonts.css'
  import DialogOutlet from '$lib/components/UI/dialog/DialogOutlet.svelte'
  import NotificationOutlet from '$lib/components/UI/notification/NotificationOutlet.svelte'
</script>

<NotificationOutlet/>
<main class="flex flex-col items-center justify-center">
  <slot />
</main>
<DialogOutlet/>

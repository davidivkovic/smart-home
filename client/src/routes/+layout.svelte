<script>
  import '@/app.css'
  import '@/fonts.css'
  import DialogOutlet from '$lib/components/UI/dialog/DialogOutlet.svelte'
  import NotificationOutlet from '$lib/components/UI/notification/NotificationOutlet.svelte'
</script>

<div class="min-h-screen flex flex-col">
  <NotificationOutlet/>
  <main class="flex flex-1 flex-col items-center justify-center">
    <slot />
  </main>
  <DialogOutlet/>
</div>

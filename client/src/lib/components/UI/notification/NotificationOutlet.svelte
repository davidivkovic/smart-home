<script>
  import { notifications } from '$lib/stores/appStore'
  import Notification from './Notification.svelte'
</script>

<div class="w-full flex flex-col gap-2">
  {#each $notifications as notification}
    <Notification title={notification.title} text={notification.text} close={notification.close} />
  {/each}
</div>

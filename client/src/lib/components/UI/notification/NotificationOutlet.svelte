<script>
  import { notifications } from '$lib/stores/appStore'
  import Notification from './Notification.svelte'
  import { notificationsHeight } from '$lib/stores/appStore'
</script>

<div class="w-full flex flex-col gap-2" bind:clientHeight={$notificationsHeight}>
  {#each $notifications as notification}
    <Notification title={notification.title} text={notification.text} close={notification.close} />
  {/each}
</div>

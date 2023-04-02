<script>
  import { dialogs } from '$lib/stores/appStore'
  import Dialog from './Dialog.svelte'
</script>

{#each $dialogs as dialog}
  <Dialog close={dialog.close} hasCloseButton={dialog.hasCloseButton}>
    <svelte:component this={dialog.content} {...dialog.props} close={dialog.close} />
  </Dialog>
{/each}

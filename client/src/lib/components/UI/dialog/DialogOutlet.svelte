<script>
  import { dialogs } from '@/lib/stores/appStore'
  import Dialog from './Dialog.svelte'
</script>

{#each $dialogs as dialog}
  <Dialog close={dialog.close}>
    <svelte:component this={dialog.content} {...dialog.props} />
  </Dialog>
{/each}

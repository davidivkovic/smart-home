<script>
  import { dialogs } from '@/appStore'
  import Dialog from './Dialog.svelte'
</script>

{#each $dialogs as dialog}
  <Dialog close={dialog.close}>
    <svelte:component this={dialog.content} {...dialog.props} />
  </Dialog>
{/each}

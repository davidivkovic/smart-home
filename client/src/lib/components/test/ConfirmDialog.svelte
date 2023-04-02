<script>
  import { openDialog } from '@/lib/stores/appStore'
  import AnotherDialog from './AnotherDialog.svelte'

  export let someProp
  const openAnotherDialog = () => {
    openDialog(AnotherDialog)
  }
</script>

<div>
  <h2 class="mb-6">Confirm</h2>
  <p>
    {someProp}
  </p>
  <button class="primary" on:click={openAnotherDialog}>Open another one</button>
</div>

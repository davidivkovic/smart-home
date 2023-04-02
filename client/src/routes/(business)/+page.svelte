<script>
  import ConfirmDialog from '$lib/components/test/ConfirmDialog.svelte'
  import { openNotification, openDialog } from '$lib/stores/appStore'

  const open = () => {
    openNotification('Uh oh! Something went wrong!', 'Sorry, there was a problem with your request')
  }

  const openNoTitle = () => {
    openNotification('Sorry, there was a problem with your request')
  }

  const openTestDialog = () => {
    openDialog(ConfirmDialog, {someProp: 'Hello World!'}, () => console.log("Closing ConfirmDialog"))
  }
</script>

<div>
  <button on:click={open}>Open notification</button>
  <button on:click={openNoTitle}>Open notification no title</button>
  <button on:click={openTestDialog}>Open dialog</button>
</div>

<button class="primary">Primary</button>
<button class="secondary">Secondary</button>

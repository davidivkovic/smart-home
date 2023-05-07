<script>
  import TwoFactorDialog from "$lib/components/profile/TwoFactorDialog.svelte"
  import { openDialog } from "$lib/stores/appStore"

  const open2FADialog = () => openDialog(TwoFactorDialog)
  
</script>

<button on:click={open2FADialog} class="primary">Open 2FA dialog</button>
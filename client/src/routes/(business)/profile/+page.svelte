<script>
  import { add2fa } from '$lib/api/auth'
  import TwoFactorDialog from '$lib/components/profile/TwoFactorDialog.svelte'
  import { openDialog } from '$lib/stores/appStore'

  const open2FADialog = async () => {
    try {
      const data = await add2fa()
      const qr = 'data:image/png;base64,' + data.qrCode
      openDialog(TwoFactorDialog, { qr, mfaSecret: data.secret })
    } catch (e) {
      console.log(e)
    }
  }
</script>

<div class="h-full w-full">
  <h1 class="text-xl">Profile Settings</h1>
  <div class="mt-5 flex flex-col">
    <div class="flex gap-2 items-center">

      <h2 class="text-lg">Two-Factor Authentication</h2>
      <span class="text-xs text-green-500 font-semibold">(Enabled)</span>
    </div>
    <div class="flex flex-col gap-2 mt-4">
      <p>
        Two-factor authentication (2FA) is a security mechanism that adds an extra layer of protection
        to your account beyond just a username and password.
      </p>
      <p>
        When you're logging in to your account with two-factor authentication enabled, you'll need to
        provide a code from the authenticator app before accessing your account.
      </p>
      <p>You can enable or disable this feature any time.</p>
    </div>
    <!-- <button on:click={open2FADialog} class="primary w-fit mt-4">Enable 2FA</button> -->
    <button class="secondary w-fit mt-4">Disable 2FA</button>
  </div>
</div>

<script>
  import { invalidateAll } from '$app/navigation'
  import { openDialog } from '$lib/stores/appStore'
  import { add2fa, disable2fa, revokeRefreshToken } from '$lib/api/auth'
  import TwoFactorDialog from '$lib/components/profile/TwoFactorDialog.svelte'
  import CodeDialog from '$lib/components/common/CodeDialog.svelte'
  import userStore from '$lib/stores/userStore.js'

  export let data

  const open2FADialog = async () => {
    try {
      const data = await add2fa()
      const qr = 'data:image/png;base64,' + data.qrCode
      openDialog(TwoFactorDialog, { qr, mfaSecret: data.secret }, async (result) => {
        if (result === 'ok') await invalidateAll()
      })
    } catch (e) {
      console.log(e)
    }
  }

  const disable2FA = () => {
    openDialog(
      CodeDialog,
      {
        title: 'Verify your code',
        description: 'Enter the six digit code displayed in your authenticator application',
        callback: (code) => disable2fa(code)
      },
      async (result) => result === 'ok' && (await invalidateAll())
    )
  }

  const removeDevice = async (token) => {
    try {
      await revokeRefreshToken(token.id)
      if (token.isThisDevice) await userStore.logout()
      else data.refreshTokens = data.refreshTokens.filter((t) => t.id !== token.id)
    }
    catch (e) {
      console.log(e)
    }
  }
</script>

<div class="h-full w-full">
  <h1 class="text-xl">Profile Settings</h1>
  <div class="mt-5 flex flex-col">
    <div class="flex items-center gap-2">
      <h2 class="text-lg">Two-Factor Authentication</h2>
      {#if data.mfaEnabled}
        <span class="text-xs font-semibold text-green-500">(Enabled)</span>
      {/if}
    </div>
    <div class="mt-3 flex flex-col gap-2 text-sm">
      <p>
        Two-factor authentication (2FA) is a security mechanism that adds an extra layer of
        protection to your account beyond just a username and password.
      </p>
      <p>
        When you're logging in to your account with two-factor authentication enabled, you'll need
        to provide a code from the authenticator app before accessing your account.
      </p>
      <p>You can enable or disable this feature any time.</p>
    </div>
    {#if data.mfaEnabled}
      <button on:click={disable2FA} class="secondary mt-4 w-fit">Disable 2FA</button>
    {:else}
      <button on:click={open2FADialog} class="primary mt-4 w-fit">Enable 2FA</button>
    {/if}
  </div>

  <h2 class="mt-5 text-lg">Devices</h2>
  <div class="mt-2">
    {#each data.refreshTokens as token}
      <div class="flex">
        <p>{token.id}</p>
        {#if token.isThisDevice}
          <div>Current device</div>
        {/if}
        <button on:click={() => removeDevice(token)}>Log Out</button>
      </div>
    {/each}
  </div>
</div>

<script>
  import { openDialog } from '$lib/stores/appStore'
  import { confirm2fa } from '$lib/api/auth'
  import SuccessDialog from './SuccessDialog.svelte'
  import Tooltip from './Tooltip.svelte'
  import Code from '../common/Code.svelte'
  import CopyIcon from '~icons/tabler/copy'

  export let qr
  export let mfaSecret
  export let close

  let error = ''

  let tooltipOpen = false

  const copyCode = async () => {
    await navigator.clipboard.writeText(mfaSecret)
    if (tooltipOpen) return
    tooltipOpen = true
    setTimeout(() => (tooltipOpen = false), 2000)
  }

  const confirmCode = async (e) => {
    e.preventDefault()
    const formData = new FormData(e.target)
    const code = Array.from(formData.values()).join('')

    try {
      await confirm2fa(code)
      openDialog(SuccessDialog, {}, () => close('ok'))
    } catch (e) {
      error = e.message
    }
  }
</script>

<div class="w-[500px] px-2">
  <h2 class="text-2xl">Two-Factor Authentication</h2>

  <div class="mt-7">
    <h3 class="text-[15px]">Step 1: Download an authenticator app</h3>
    <p class="text-sm">
      Download and install any <a
        href="https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2&hl=en&gl=US&pli=1"
        target="_blank"
        class="font-medium text-[#0000EE] visited:font-medium visited:text-[#551A8B] hover:underline"
        >authenticator app</a
      >
      (Eg. Google Authenticator) on your phone.
    </p>
  </div>
  <div class="mt-7">
    <h3 class="text-[15px]">Step 2: Scan the QR code</h3>
    <p class="text-sm">
      Open the authenticator app and scan the image below using your phone's camera.
    </p>
    <div class="mt-7 flex w-full justify-between gap-4">
      <div class="relative -ml-1 h-48 w-48 overflow-clip">
        <img src={qr} alt="QR code" class="absolute inset-auto h-full w-full scale-[115%]" />
      </div>
      <div class="flex-1">
        <p class="text-[13px]">
          Can't scan the QR code? Enter the code below manually in your authenticator app.
        </p>
        <div
          class="relative mr-1 mt-3 flex items-center justify-between border border-neutral-300 bg-neutral-100 px-4 py-3 text-base font-medium"
        >
          <div class="flex gap-1.5">
            {#each mfaSecret.match(/.{1,4}/g) as group}
              <span>{group}</span>
            {/each}
          </div>
          <button
            on:click={copyCode}
            class="absolute right-0 flex aspect-square h-full w-auto items-center justify-center bg-transparent px-0 py-0"
          >
            <CopyIcon />
          </button>
          <Tooltip open={tooltipOpen} text="Copied to clipboard" />
        </div>
        <p class="mt-3 text-[13px]">
          Note: Write the 16 character code on a piece of paper and keep it in a safe place. If
          lost, you may lose access to your account.
        </p>
      </div>
    </div>

    <div class="mt-7">
      <h3 class="text-[15px]">Step 3: Verify your code</h3>
      <p class="text-sm">Enter the 6-digit verification code generated.</p>
      <form
        on:submit|preventDefault={confirmCode}
        class="mt-3 flex w-full flex-col items-center justify-center"
      >
        <Code />
        {#if error}
          <p class="mt-3 text-[13px] text-red-500">{error}</p>
        {/if}
        <div class="mt-12 flex w-full justify-between gap-3">
          <div class="flex items-center gap-3">
            <input required type="checkbox" id="confirm" />
            <label
              class="cursor-pointer select-none text-[13px] font-normal text-neutral-800"
              for="confirm"
            >
              I've written down the authenticator recovery code above
            </label>
          </div>
          <div class="flex gap-2">
            <button type="button" on:click={close} class="secondary !border-transparent">
              Cancel
            </button>
            <button class="primary px-7">Submit</button>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
  import CopyIcon from '~icons/tabler/copy'
  import Tooltip from './Tooltip.svelte'
  import { openDialog } from '$lib/stores/appStore'
  import SuccessDialog from './SuccessDialog.svelte'

  export let close

  const inputCount = 6
  const codeInputs = Array.from(Array(inputCount).keys())

  let error = ''

  let inputContainer
  let tooltipOpen = false

  const qr =
    'https://www.investopedia.com/thmb/hJrIBjjMBGfx0oa_bHAgZ9AWyn0=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/qr-code-bc94057f452f4806af70fd34540f72ad.png'
  const mfaSecret = 'SEDRFRGT3DRE5674'

  const copyCode = async () => {
    await navigator.clipboard.writeText(mfaSecret)
    if (tooltipOpen) return
    tooltipOpen = true
    setTimeout(() => (tooltipOpen = false), 2000)
  }

  const handlePaste = (e) => {
    const paste = e.clipboardData.getData('text')
    const inputs = inputContainer.childNodes
    inputs.forEach((input, index) => (input.value = paste[index]))
  }

  const focusSibling = (sibling) => {
    setTimeout(() => sibling.focus(), 0)
  }

  const handleInput = (e) => {
    const input = e.target
    const keyCode = e.key
    const previousSibling = input.previousElementSibling
    const nextSibling = input.nextElementSibling

    if (keyCode === 'ArrowLeft' && previousSibling) {
      focusSibling(previousSibling)
    } else if (['ArrowRight', 'Tab'].includes(keyCode) && input.nextElementSibling) {
      focusSibling(nextSibling)
    } else if (keyCode === 'Backspace') {
      input.value = ''
      if (previousSibling) {
        focusSibling(previousSibling)
      }
    } else if (Number(keyCode) || keyCode === '0') {
      input.value = Number(keyCode)
      if (nextSibling) {
        focusSibling(nextSibling)
      }
    }
  }

  const confirmCode = () => {
    const code = Array.from(inputContainer.children)
      .map((input) => input.value)
      .join('')

    console.log(code)
    openDialog(SuccessDialog, {}, close)
  }
</script>

<div class="w-[500px]">
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
    <div class="mt-7 flex w-full justify-between gap-3">
      <img src={qr} alt="QR code" class="h-48 w-48" />
      <div class="flex-1">
        <p class="text-[13px]">
          Can't scan the QR code? Enter the code below manually in your authenticator app.
        </p>
        <div
          class="relative mt-3 flex items-center justify-between border border-neutral-300 bg-neutral-100 px-4 py-3 text-base font-medium"
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
        <div bind:this={inputContainer} class="mt-4 flex w-1/2 min-w-fit justify-between gap-3">
          {#each codeInputs as codeInput (codeInput)}
            <input
              required
              type="text"
              maxlength="1"
              class="h-12 w-12 px-0 text-center text-base font-medium caret-transparent"
              on:paste|preventDefault={handlePaste}
              on:keydown={handleInput}
              oninput="this.value =this.value.replace(/[^0-9.]/g, '').replace(/(..*?)..*/g,'$1');"
            />
          {/each}
        </div>
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

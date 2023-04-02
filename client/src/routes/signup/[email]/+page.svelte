<script>
  import { goto } from '$app/navigation'
  import { page } from '$app/stores'
  import { resendConfirmation, confirmEmail } from '$lib/api/auth'
  const email = $page.params.email

  const inputCount = 6
  const codeInputs = Array.from(Array(inputCount).keys())

  let resendStatus = ''
  let error = ''

  let inputContainer

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

  const resendCode = () => {
    try {
      resendConfirmation(email)
      resendStatus = 'New confirmation code send'
      error = ''
      // openNotification('Confirmation code resent')
    } catch (err) {
      resendStatus = err
    }
  }

  const confirm = async () => {
    const token = Array.from(inputContainer.children)
      .map((input) => input.value)
      .join('')
    try {
      await confirmEmail({ email, token })
      goto('/login')
    } catch (err) {
      resendStatus = ''
      error = err
    }
  }
</script>

<form
  on:submit|preventDefault={confirm}
  class="flex max-w-[460px] flex-col justify-between border bg-white py-10 px-9"
>
  <h1>Confirm your email</h1>
  <p class="mt-1">Enter the 6-digit code sent to you at {email}.</p>
  <div bind:this={inputContainer} class="mt-5 flex w-full justify-between gap-2">
    {#each codeInputs as codeInput (codeInput)}
      <input
        required
        type="text"
        maxlength="1"
        class="h-14 w-14 px-0 text-center caret-transparent"
        on:paste|preventDefault={handlePaste}
        on:keydown={handleInput}
        oninput="this.value =
        this.value.replace(/[^0-9.]/g, '').replace(/(..*?)..*/g,'$1');"
      />
    {/each}
  </div>
  <button class="primary mt-5">Confirm email</button>
  <p class="mt-6 text-center text-sm">
    Did't get a code? <button
      type="button"
      class="bg-transparent p-0 font-normal text-black underline"
      on:click={resendCode}>Resend code</button
    >
  </p>
  <p class="mt-3 text-center text-sm text-neutral-600">{resendStatus}</p>
  <p class="text-center text-sm text-red-600">{error}</p>
</form>

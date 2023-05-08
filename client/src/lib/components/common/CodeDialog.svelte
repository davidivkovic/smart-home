<script>
  import Code from './Code.svelte'

  export let close
  export let title
  export let callback

  let mode = 'code'
  let switchModeText = "Don't have a code? Enter your secret"
  let secret = ''

  let error = ''

  const toggleMode = () => {
    error = ''
    mode === 'code' ? (mode = 'secret') : (mode = 'code')
    mode === 'code'
      ? (switchModeText = "Don't have a code? Enter your secret")
      : (switchModeText = 'Have a code? Enter it here')
  }

  const submitForm = async (e) => {
    e.preventDefault()
    error = ''
    try {
      const formData = new FormData(e.target)
      let code
      if (mode === 'code') {
        code = Array.from(formData.values()).join('')
      } else {
        code = secret
      }

      await callback(code)
      close('ok')
    } catch (e) {
      error = e.message
    }
  }
</script>

<form on:submit|preventDefault={submitForm} class="flex w-[400px] flex-col justify-between p-2">
  <h1>{title}</h1>

  {#if mode === 'code'}
    <p class="mt-4">Enter the 6-digit code from your authenticator app</p>
    <div class="flex w-full justify-center">
      <Code required={mode === 'code'} />
    </div>
  {/if}

  {#if mode === 'secret'}
    <p class="mt-4">Enter your 16 character secret</p>
    <input
      required={mode === 'secret'}
      pattern="^[0-9A-Za-z]*$"
      bind:value={secret}
      type="text"
      class="mt-4"
      minlength="16"
      maxlength="16"
    />
  {/if}

  <p class="mt-2 text-center text-sm text-red-600">{error}</p>
  <div class="mt-4 flex w-full items-center justify-between">
    <button
      type="button"
      on:click={toggleMode}
      class="cursor-pointer bg-transparent p-0 text-[13px] font-medium hover:underline"
      >{switchModeText}</button
    >
    <button class="primary w-fit">Submit</button>
  </div>
</form>

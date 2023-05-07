<script>
  import { page } from '$app/stores'
  import { resendConfirmation, confirmEmail } from '$lib/api/auth'
  import { openDialog } from '$lib/stores/appStore'
  import ContinueDialog from '$lib/components/signup/ContinueDialog.svelte'
  import Code from '$lib/components/common/Code.svelte'

  const email = $page.params.email

  let resendStatus = ''
  let error = ''

  const resendCode = async () => {
    try {
      await resendConfirmation(email)
      resendStatus = 'New confirmation code sent'
      error = ''
    } catch (err) {
      error = err.message
    }
  }

  const confirm = async (e) => {
    e.preventDefault()
    const formData = new FormData(e.target)
    const token = Array.from(formData.values()).join('')

    try {
      await confirmEmail({ email, token })
      openDialog(ContinueDialog)
    } catch (err) {
      resendStatus = ''
      error = err.message
    }
  }
</script>

<form
  on:submit|preventDefault={confirm}
  class="flex max-w-[460px] flex-col justify-between border bg-white py-10 px-9"
>
  <h1>Confirm your email</h1>
  <p class="mt-1">Enter the 6-digit code sent to you at {email}.</p>
  <Code />
  <p class="mt-2 text-center text-sm text-red-600">{error}</p>
  <button class="primary mt-5">Confirm email</button>
  <p class="mt-4 text-center text-sm">
    Didn't get a code? <button
      type="button"
      class="bg-transparent p-0 font-normal text-black underline"
      on:click={resendCode}>Resend code</button
    >
  </p>
  <p class="mt-3 text-center text-sm text-neutral-600">{resendStatus}</p>
</form>

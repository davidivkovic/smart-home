<script>
  import { goto } from '$app/navigation'
  import { signup } from '$lib/api/auth'
  import CheckIcon from '~icons/tabler/circle-check-filled'

  let signupForm
  let error = ''

  let password = ''

  const passwordRequirements = [
    { text: 'At least 8 characters long' },
    { text: 'Contain at least 1 number' },
    { text: 'Contain at least 1 lowercase letter' },
    { text: 'Contain at least 1 uppercase letter' },
    { text: 'Contain at least 1 special character' }
  ]

  $: passwordRequirements[0].validator = password.length >= 8
  $: passwordRequirements[1].validator = /\d/.test(password)
  $: passwordRequirements[2].validator = /[a-z]/.test(password)
  $: passwordRequirements[3].validator = /[A-Z]/.test(password)
  $: passwordRequirements[4].validator = /[^a-zA-Z0-9]+/.test(password)

  const signupUser = async () => {
    const formData = new FormData(signupForm)
    const data = Object.fromEntries(formData)
    try {
      await signup(data)
      goto(`/signup/${data.email}`)
    } catch (err) {
      error = err
    }
  }
</script>

<form
  bind:this={signupForm}
  on:submit|preventDefault={signupUser}
  class="flex max-w-[444px] flex-col justify-between border bg-white py-7 px-10"
>
  <div>
    <h1>Create an account</h1>
    <p class="mt-1">Enter your information to get started</p>
    <div class="mt-3 flex flex-col gap-2">
      <label for="email">First name</label>
      <input
        required
        type="text"
        name="firstName"
        placeholder="Enter your first name"
        autocomplete="given-name"
      />
    </div>
    <div class="mt-3 flex flex-col gap-2">
      <label for="email">Last name</label>
      <input
        required
        type="text"
        name="lastName"
        placeholder="Enter your last name"
        autocomplete="family-name"
      />
    </div>
    <div class="mt-3 flex flex-col gap-2">
      <label for="email">Email</label>
      <input
        required
        type="text"
        name="email"
        placeholder="Enter your email"
        autocomplete="email"
      />
    </div>
    <div class="mt-3 flex flex-col gap-2">
      <label for="password">Password</label>
      <input
        bind:value={password}
        required
        type="password"
        name="password"
        placeholder="Enter your password"
        autocomplete="current-password"
        minlength="8"
        pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9])[A-Za-z\d@$!%*?&].*$"
        title="Must fullfill all password requirements (see below)"
      />
    </div>
    <div class="mt-3 text-[13px]">
      <span class="font-medium">Password minimum requirements:</span>
      <ul>
        {#each passwordRequirements as requirement}
          <li class="flex items-center gap-2">
            <CheckIcon
              class={`mt-0.5 h-4 w-4 transition-colors ${
                requirement.validator ? 'text-[#40c82b]' : 'text-neutral-200'
              }`}
            />
            <span class="mt-1">{requirement.text}</span>
          </li>
        {/each}
      </ul>
    </div>
  </div>
  <p class="mt-4 text-center text-sm text-red-600">{error}</p>
  <div class="mt-4">
    <button class="primary w-full"> Sign up</button>
    <p class="mt-5 text-center text-sm">
      Already have an account? <a href="/login" class="text-black underline">Log in</a>
    </p>
    <small class="mt-5">
      By proceeding, you consent to get calls, WhatsApp or SMS messages, including by automated
      means, from Smart Home and its affiliates to the number provided.</small
    >
    <small class="mt-5">
      This site is protected by reCAPTCHA and the Google
      <span class="text-black underline"> Privacy Policy</span> and
      <span class="text-black underline">Terms of Service</span> apply
    </small>
  </div>
</form>

<script>
  import { goto } from '$app/navigation'

  let signupForm
  const error = ''

  const signup = () => {
    const formData = new FormData(signupForm)
    const data = Object.fromEntries(formData)
    // send to backend
    goto(`/signup/${data.email}`)
  }
</script>

<form
  bind:this={signupForm}
  on:submit|preventDefault={signup}
  class="flex max-w-[444px] flex-col justify-between border bg-white py-10 px-10"
>
  <div>
    <h1>Create an account</h1>
    <p class="mt-1">Enter your information to get started</p>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">First name</label>
      <input
        required
        type="text"
        name="firstName"
        placeholder="Enter your first name"
        autocomplete="given-name"
      />
    </div>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">Last name</label>
      <input
        required
        type="text"
        name="lastName"
        placeholder="Enter your last name"
        autocomplete="family-name"
      />
    </div>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">Email</label>
      <input
        required
        type="text"
        name="email"
        placeholder="Enter your email"
        autocomplete="email"
      />
    </div>
    <div class="mt-4 flex flex-col gap-2">
      <label for="password">Password</label>
      <input
        required
        type="password"
        name="password"
        placeholder="Enter your password"
        autocomplete="current-password"
      />
    </div>
  </div>
  <p class="mt-4 text-center text-sm text-red-600">{error}</p>
  <div class="mt-4">
    <button class="primary w-full"> Sign up</button>
    <p class="mt-6 text-center text-sm">
      Already have an account? <a href="/signin" class="text-black underline">Sign in</a>
    </p>
    <small class="mt-6">
      By proceeding, you consent to get calls, WhatsApp or SMS messages, including by automated
      means, from Smart Home and its affiliates to the number provided.</small
    >
    <small class="mt-6">
      This site is protected by reCAPTCHA and the Google
      <span class="text-black underline"> Privacy Policy</span> and
      <span class="text-black underline">Terms of Service</span> apply
    </small>
  </div>
</form>

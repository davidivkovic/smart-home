<script>
  let error = ''

  let email = ''
  let password = ''

  const signin = () => {
    console.log(email, password)
  }
</script>

<form on:submit|preventDefault={signin} class="flex max-w-[430px] flex-col justify-between border bg-white py-10 px-9">
  <div>
    <h1>Welcome Back</h1>
    <p class="mt-1 text-[15px]">Enter your credentials and sign in to your account</p>
    <div class="mt-5 flex flex-col gap-1">
      <label for="email">Email</label>
      <input bind:value={email} required type="text" placeholder="Your email" autocomplete="email" />
    </div>
    <div class="mt-4 flex flex-col gap-1">
      <div class="flex justify-between">
        <label for="password">Password</label>
        <a
          href="/"
          class="mt-0.5 block cursor-pointer text-center text-[13px] text-neutral-600 underline"
        >
          Forgot password?
        </a>
      </div>
      <input bind:value={password} required type="password" placeholder="Your password" autocomplete="current-password" />
    </div>
  </div>
  <p class="text-center mt-4 text-red-600 text-sm">{error}</p>
  <div class="mt-5">
    <button class="primary w-full"> Sign in </button>
    <p class="mt-6 text-center text-sm">
      Don't have an account? <a href="/signup" class="text-black underline">Sign up</a>
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

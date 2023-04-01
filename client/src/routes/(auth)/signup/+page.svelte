<script>
  const error = ''
  const signup = () => {
    console.log('signup')
  }
</script>

<form
  on:submit|preventDefault={signup}
  class="flex max-w-[444px] flex-col justify-between border bg-white py-10 px-10"
>
  <div>
    <h1>Create an account</h1>
    <p class="mt-1 text-[15px]">Enter your credentials and sign in to your account</p>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">First name</label>
      <input required type="text" placeholder="Your first name" autocomplete="given-name" />
    </div>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">Last name</label>
      <input required type="text" placeholder="Your last name" autocomplete="family-name" />
    </div>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">Email</label>
      <input required type="text" placeholder="Your email" autocomplete="email" />
    </div>
    <div class="mt-4 flex flex-col gap-2">
      <label for="password">Password</label>
      <input required type="password" placeholder="Your password" autocomplete="current-password" />
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

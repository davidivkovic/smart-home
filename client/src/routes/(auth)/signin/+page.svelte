<div class="flex max-w-[444px] flex-col justify-between border bg-white py-10 px-10">
  <div>
    <h1>Welcome Back</h1>
    <p class="mt-1 text-[15px]">Enter your credentials and sign in to your account</p>
    <div class="mt-5 flex flex-col gap-2">
      <label for="email">Email</label>
      <input type="text" placeholder="Your email" />
    </div>
    <div class="mt-4 flex flex-col gap-2">
      <div class="flex justify-between">
        <label for="password">Password</label>
        <a
          href="/auth/password/forgotten"
          class="block cursor-pointer text-center text-sm text-neutral-600 underline"
        >
          Forgot password?
        </a>
      </div>
      <input type="password" placeholder="Your password" />
    </div>
  </div>
  <div class="mt-4">
    <button class="primary w-full"> Sign in </button>
    <p class="mt-6 text-center text-sm">
      Don't have an account? <a href="/signup" class="text-black underline">Sign up</a>
    </p>
    <small class="mt-6">
      By proceeding, you consent to get calls, WhatsApp or SMS messages, including by automated
      means, from Uber and its affiliates to the number provided.</small
    >
    <small class="mt-6">
      This site is protected by reCAPTCHA and the Google
      <span class="text-black underline"> Privacy Policy</span> and
      <span class="text-black underline">Terms of Service</span> apply
    </small>
  </div>
</div>

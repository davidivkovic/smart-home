<script>
  import ArrowRight from '~icons/tabler/arrow-right'

  export let close

  const navigateToLogin = () => {
    close()
  }
</script>

<div class="w-[450px] p-2">
  <h2>Two-Factor Authentication Enabled</h2>
  <p class="mt-2">
    You have successfully enabled two-factor authentication. From now on, you will need to enter a
    code when logging in.
  </p>
  <button on:click={navigateToLogin} class="primary mt-6 flex items-center gap-2">
    <span>Continue</span>
    <ArrowRight />
  </button>
</div>

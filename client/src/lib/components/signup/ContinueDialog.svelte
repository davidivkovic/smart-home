<script>
  import { goto } from '$app/navigation'

  import ArrowRight from '~icons/tabler/arrow-right'

  export let close

  const navigateToLogin = () => {
    close()
    goto('/login')
  }

</script>

<div class="w-[400px] p-2">
  <h2>Signup complete</h2>
  <p class="mt-1">
    Your account has been successfully verified. Click the button below to continue to the login
    page.
  </p>
  <button on:click={navigateToLogin} class="primary mt-6 flex items-center gap-2">
    <span>Continue to login</span>
    <ArrowRight />
  </button>
</div>

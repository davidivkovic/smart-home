<script>
  import { goto } from '$app/navigation'

  import { isAuthenticated } from '$lib/stores/userStore'

  $: !$isAuthenticated && goto('/login')
</script>

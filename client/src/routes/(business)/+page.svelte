<script>
  import { goto } from "$app/navigation"

  import { isAuthenticated } from "$lib/stores/userStore"

  $: !$isAuthenticated && goto('/login')
</script>

<div>Hi from home page</div>
<div>{$isAuthenticated}</div>
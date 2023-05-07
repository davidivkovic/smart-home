<script>
  import Sidebar from '$lib/components/profile/Sidebar.svelte'
</script>

<div class="flex w-full gap-10">
  <Sidebar />
  <div class="flex-1">
    <slot />
  </div>
</div>
